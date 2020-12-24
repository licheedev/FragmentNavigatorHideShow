package com.licheedev.navhideshowdemo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.licheedev.myutils.LogPlus


abstract class BaseFragment : Fragment() {

    override fun onAttach(context: Context) {
        LogPlus.w("${this::class.java.simpleName}(${hashCode()})")
        super.onAttach(context)
    }

    override fun onDetach() {
        LogPlus.w("${this::class.java.simpleName}(${hashCode()})")
        super.onDetach()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        LogPlus.w("${this::class.java.simpleName}(${hashCode()})")
        super.onCreate(savedInstanceState)
    }

    protected abstract val layoutId: Int
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        LogPlus.e("${this::class.java.simpleName}(${hashCode()})")
        return inflater.inflate(layoutId, container, false)
    }


    private val className = this::class.java.simpleName

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        LogPlus.w("$className(${hashCode()})")
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        LogPlus.e("${className}(${hashCode()})")
        super.onResume()
    }

    override fun onPause() {
        LogPlus.e("${className}(${hashCode()})")
        super.onPause()
    }

    override fun onStart() {
        LogPlus.w("${className}(${hashCode()})")
        super.onStart()
    }

    override fun onStop() {
        LogPlus.w("${className}(${hashCode()})")
        super.onStop()
    }

    override fun onDestroyView() {
        LogPlus.e("${className}(${hashCode()})")
        super.onDestroyView()
    }

    override fun onDestroy() {
        LogPlus.w("${className}(${hashCode()})")
        super.onDestroy()
    }

}