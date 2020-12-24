package com.licheedev.navhideshowdemo

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_two.*


class TwoFragment : BaseFragment() {


    override val layoutId: Int
        get() = R.layout.fragment_two

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnGoFragment3.setOnClickListener {
            findNavController().navigate(R.id.threeFragment)
        }

        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        btnPopupTo1.setOnClickListener {
            findNavController().navigate(R.id.popUpToOneFragment)
        }
    }
}