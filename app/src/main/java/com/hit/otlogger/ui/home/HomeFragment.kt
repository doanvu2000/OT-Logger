package com.hit.otlogger.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hit.otlogger.base.BaseFragment
import com.hit.otlogger.databinding.FragmentHomeBinding

class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    override fun inflateLayout(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun initView() {

    }

    override fun initData() {

    }

    override fun initListener() {

    }
}