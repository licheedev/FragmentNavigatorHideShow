package com.licheedev.fragmentnavigatorhs;

import androidx.annotation.NonNull;
import androidx.navigation.Navigator;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.fragment.NavHostFragment;
import java.lang.reflect.Method;

/**
 * 默认使用hide-show，而不是replace来切换Fragment的NavHostFragment;
 * 内部返回 {@link HideShowFragmentNavigator};
 * 如果需要使用replace来切换Fragment，则可以使用{@link ReplaceFragment}注解来标记
 */
public class HideShowNavHostFragment extends NavHostFragment {

    @NonNull
    @Override
    protected Navigator<? extends FragmentNavigator.Destination> createFragmentNavigator() {
        return new HideShowFragmentNavigator(requireContext(), getChildFragmentManager(),
            reflectGetContainerId());
    }

    private int reflectGetContainerId() {
        try {
            Method method = NavHostFragment.class.getDeclaredMethod("getContainerId");
            method.setAccessible(true);
            return (int) method.invoke(this);
        } catch (Exception e) {
            e.printStackTrace();
            return R.id.nav_host_fragment_container;
        }
    }
}
