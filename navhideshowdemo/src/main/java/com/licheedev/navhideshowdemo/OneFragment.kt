package com.licheedev.navhideshowdemo

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.licheedev.fragmentnavigatorhs.ReplaceFragment
import kotlinx.android.synthetic.main.fragment_one.*

class OneFragment : BaseFragment() {

    override val layoutId: Int
        get() = R.layout.fragment_one

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnGoFragment2.setOnClickListener {
            findNavController().navigate(R.id.twoFragment)
        }
    }

}