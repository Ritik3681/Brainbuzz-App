package com.example.aptitude

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() ,Login.OnRegisterClickedListener,
        Login.OnQuizeCategoriesClickedlistener,Register.OnRegisterClickedListener,
        QuizCategoriesFragment.OnAdvanceClickedListerner


{
   // val sun= 2/0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.statusBarColor = ContextCompat.getColor(this, R.color.nwdfx1)

        frag()
    }







    fun frag(){
        if(supportFragmentManager.findFragmentById(R.id.fragment) !is Login){
            val mfragment =Login()
            val fragmentManager= supportFragmentManager
            val fragmentTransaction=fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment,mfragment).commit()
        }
    }

     fun registerFragment(){
         val nfrgment=Register()
         val fragmentManager=supportFragmentManager
         val fragmentTransaction=fragmentManager.beginTransaction()
         fragmentTransaction.addToBackStack(null)
         fragmentTransaction.replace(R.id.fragment,nfrgment).commit()

     }
    fun quizCategoriesFragment(){
        val nfragment=QuizCategoriesFragment()
        val fragmentManager=supportFragmentManager
        val fragmentTransaction=fragmentManager.beginTransaction()
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.replace(R.id.fragment,nfragment).commit()
    }
    fun  loginFragment(){
        val nfragment=Login()
        val fragmentManager=supportFragmentManager
        val fragmentTransaction=fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment,nfragment).commit()

    }
    fun  chatFragment(){
        val nfragment=ChatFragment()
        val fragmentManager=supportFragmentManager
        val fragmentTransaction=fragmentManager.beginTransaction()
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.replace(R.id.fragment,nfragment).commit()

    }









   override fun  OnRegisterClicked(){
       registerFragment()


   }
    override fun OnQuizCategoriesClicked(){
        quizCategoriesFragment()
    }
    override fun OnRegisterclicked(){
        loginFragment()

    }
    override fun OnAvancedCleicked(){
        chatFragment()
    }










}
