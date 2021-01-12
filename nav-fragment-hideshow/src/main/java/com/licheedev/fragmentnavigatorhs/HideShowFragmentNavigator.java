/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.licheedev.fragmentnavigatorhs;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigator;
import androidx.navigation.fragment.FragmentNavigator;
import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.Map;

/**
 * 默认使用hide-show，而不是replace来切换Fragment的FragmentNavigator；
 * 如果需要使用replace来切换Fragment，则可以使用{@link ReplaceFragment}注解来标记
 */
@Navigator.Name("fragment")
public class HideShowFragmentNavigator extends FragmentNavigator {

    private static final String TAG = "FragmentNavigator";

    private final Context mContext;
    private final FragmentManager mFragmentManager;
    private final int mContainerId;

    public HideShowFragmentNavigator(@NonNull Context context, @NonNull FragmentManager manager,
        int containerId) {
        super(context, manager, containerId);
        mContext = context;
        mFragmentManager = manager;
        mContainerId = containerId;

 
    }

    @Nullable
    @Override
    public NavDestination navigate(@NonNull FragmentNavigator.Destination destination,
        @Nullable Bundle args, @Nullable NavOptions navOptions,
        @Nullable Navigator.Extras navigatorExtras) {
        if (mFragmentManager.isStateSaved()) {
            Log.i(TAG,
                "Ignoring navigate() call: FragmentManager has already" + " saved its state");
            return null;
        }
        String className = destination.getClassName();
        if (className.charAt(0) == '.') {
            className = mContext.getPackageName() + className;
        }

        final FragmentTransaction ft = mFragmentManager.beginTransaction();

        int enterAnim = navOptions != null ? navOptions.getEnterAnim() : -1;
        int exitAnim = navOptions != null ? navOptions.getExitAnim() : -1;
        int popEnterAnim = navOptions != null ? navOptions.getPopEnterAnim() : -1;
        int popExitAnim = navOptions != null ? navOptions.getPopExitAnim() : -1;
        if (enterAnim != -1 || exitAnim != -1 || popEnterAnim != -1 || popExitAnim != -1) {
            enterAnim = enterAnim != -1 ? enterAnim : 0;
            exitAnim = exitAnim != -1 ? exitAnim : 0;
            popEnterAnim = popEnterAnim != -1 ? popEnterAnim : 0;
            popExitAnim = popExitAnim != -1 ? popExitAnim : 0;
            ft.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim);
        }

        /////////////////////////////////////////////
        Fragment fragment = mFragmentManager.getPrimaryNavigationFragment();
        boolean toReplace = false;
        if (fragment != null) {
            toReplace = toReplaceFragment(fragment);
            if (toReplace) {
                //ft.remove(fragment);
            } else {
                ft.setMaxLifecycle(fragment, Lifecycle.State.STARTED);
                ft.hide(fragment);
            }
        }
        Fragment frag;
        if (toReplace) {
            frag = instantiateFragment(mContext, mFragmentManager, className, args);
            frag.setArguments(args);
            ft.replace(mContainerId, frag);
        } else {
            frag = mFragmentManager.findFragmentByTag(className);
            if (frag != null) {
                ft.setMaxLifecycle(frag, Lifecycle.State.RESUMED);
                ft.show(frag);
            } else {
                frag = instantiateFragment(mContext, mFragmentManager, className, args);
                frag.setArguments(args);
                ft.add(mContainerId, frag, className);
            }
        }

        //ft.replace(mContainerId, frag);
        ft.setPrimaryNavigationFragment(frag);
        /////////////////////////////////////////////

        final @IdRes int destId = destination.getId();

        ArrayDeque<Integer> mBackStack=null;
        try {
            Field backStack = FragmentNavigator.class.getDeclaredField("mBackStack");
            backStack.setAccessible(true);
            mBackStack = (ArrayDeque<Integer>) backStack.get(this);
        } catch (Exception e) {
            e.printStackTrace(); // 不可达
        }
        
        final boolean initialNavigation = mBackStack.isEmpty();
        // TODO Build first class singleTop behavior for fragments
        final boolean isSingleTopReplacement = navOptions != null
            && !initialNavigation
            && navOptions.shouldLaunchSingleTop()
            && mBackStack.peekLast() == destId;

        boolean isAdded;
        if (initialNavigation) {
            isAdded = true;
        } else if (isSingleTopReplacement) {
            // Single Top means we only want one instance on the back stack
            if (mBackStack.size() > 1) {
                // If the Fragment to be replaced is on the FragmentManager's
                // back stack, a simple replace() isn't enough so we
                // remove it from the back stack and put our replacement
                // on the back stack in its place
                mFragmentManager.popBackStack(
                    generateBackStackName(mBackStack.size(), mBackStack.peekLast()),
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
                ft.addToBackStack(generateBackStackName(mBackStack.size(), destId));
            }
            isAdded = false;
        } else {
            ft.addToBackStack(generateBackStackName(mBackStack.size() + 1, destId));
            isAdded = true;
        }
        if (navigatorExtras instanceof Extras) {
            Extras extras = (Extras) navigatorExtras;
            for (Map.Entry<View, String> sharedElement : extras.getSharedElements().entrySet()) {
                ft.addSharedElement(sharedElement.getKey(), sharedElement.getValue());
            }
        }
        ft.setReorderingAllowed(true);
        ft.commit();
        // The commit succeeded, update our view of the world
        if (isAdded) {
            mBackStack.add(destId);
            return destination;
        } else {
            return null;
        }
    }

    @NonNull
    private String generateBackStackName(int backStackIndex, int destId) {
        return backStackIndex + "-" + destId;
    }

    /**
     * 前一个Fragment是否需要被replace掉
     *
     * @return
     */
    private static boolean toReplaceFragment(Fragment fragment) {
        return fragment.getClass().isAnnotationPresent(ReplaceFragment.class);
    }
}
