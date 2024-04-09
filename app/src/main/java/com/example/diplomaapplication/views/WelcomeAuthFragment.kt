package com.example.diplomaapplication.views

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.diplomaapplication.R
import com.example.diplomaapplication.databinding.FragmentWelcomeAuthBinding
import com.example.diplomaapplication.model.PagerCard
import com.example.diplomaapplication.recycler_views.adapter.WelcomeViewPagerAdapter


class WelcomeAuthFragment : Fragment() {


    private var viewPagerItemPosition :Int = 0
    private var handler : Handler = Handler(Looper.getMainLooper())
    private var _binding: FragmentWelcomeAuthBinding? = null
    private val binding get() = _binding!!


    private var changeViewPagerItem : Runnable = object : Runnable {
        override fun run() {
            viewPagerItemPosition = if(viewPagerItemPosition>=binding.welcomeAuthViewPager.childCount) 0
            else binding.welcomeAuthViewPager.currentItem + 1
            binding.welcomeAuthViewPager.setCurrentItem(viewPagerItemPosition,true)
            handler.postDelayed(this,2000)
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentWelcomeAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewPager()
        setupNavigation()
        handler.postDelayed(changeViewPagerItem,2000)
    }



    private fun setupViewPager(){
        binding.welcomeAuthViewPager.adapter = WelcomeViewPagerAdapter(requireContext(),getPagerMessagesList())
        binding.welcomePagerDots.setupWithViewPager(binding.welcomeAuthViewPager)
        for (i in 0 until binding.welcomePagerDots.tabCount) {
            val tab = (binding.welcomePagerDots.getChildAt(0) as ViewGroup).getChildAt(i)
            (tab.layoutParams as ViewGroup.MarginLayoutParams).setMargins(0, 0, 31, 0);
            tab.requestLayout()
        }
        binding.welcomePagerDots.invalidate()
    }


    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(changeViewPagerItem)
    }



    private fun getPagerMessagesList():ArrayList<PagerCard>{
        val listOfCards = ArrayList<PagerCard>()
        listOfCards.add(PagerCard("Давайте начнем","Приложение, которое поможет вам запомнить все ваши ежедневные лекарства! "))
        listOfCards.add(PagerCard("Сохраняйте лекарства","Сохраняйте лекарства — приложение напомнит вам, когда вам нужно принять таблетки! Вам не нужно ничего помнить"))
        listOfCards.add(PagerCard("Свяжитесь с доктором","Легко поддерживать связь с любимым врачом. Сообщите ему и примите решение болезни!"))
        return listOfCards
    }




    private fun setupNavigation(){
        binding.welcomeAuthRegisterButton.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeAuthFragment_to_registerFragment)
        }
        binding.welcomeAuthLoginButton.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeAuthFragment_to_loginFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}