package com.example.diplomaapplication.recycler_views

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.example.diplomaapplication.R
import com.example.diplomaapplication.databinding.FragmentWelcomeAuthBinding
import com.example.diplomaapplication.databinding.ViewPagerCardBinding
import com.example.diplomaapplication.model.PagerCard


class WelcomeViewPagerAdapter(private val context: Context,private val listOfCards:ArrayList<PagerCard>):PagerAdapter() {

    private var _binding: ViewPagerCardBinding? = null
    private val binding get() = _binding!!

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout
    }

    override fun getCount(): Int {
        return listOfCards.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding = ViewPagerCardBinding.inflate(LayoutInflater.from(context), container, false)
        binding.pagerTitle.text = listOfCards[position].title
        binding.pagerMessage.text = listOfCards[position].message
        container.addView(binding.root)
        return binding.root
    }



    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }



}