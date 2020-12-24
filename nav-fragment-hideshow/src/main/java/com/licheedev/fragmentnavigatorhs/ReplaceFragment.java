package com.licheedev.fragmentnavigatorhs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记从当前Fragment切换到另外一个Fragment时，当前Fragment需要被replace掉，而不是hide掉
 * 配合{@link HideShowNavHostFragment}、{@link HideShowFragmentNavigator} 来使用
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ReplaceFragment {

}
