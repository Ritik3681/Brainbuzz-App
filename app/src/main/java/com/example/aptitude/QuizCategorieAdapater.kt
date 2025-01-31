package com.example.aptitude

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView

class QuizCategorieAdapater(private val dataitem: MutableList<DataClass>

,  private val fragmentManager: FragmentManager

):RecyclerView.Adapter<QuizCategorieAdapater.QuizViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
       val view=LayoutInflater.from(parent.context).inflate(R.layout.quiz_categorie_layout,parent,
           false
       )
        return QuizViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataitem.size
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
       holder.bind(dataitem[position],fragmentManager)



    }


    class QuizViewHolder(itemview: View):RecyclerView.ViewHolder(itemview){
        val name : TextView = itemview.findViewById(R.id.categorieName)

        fun bind(model:DataClass,fragmentManager: FragmentManager){
            if (name!=null){
                name.text=model.CategorieName
                itemView.setOnClickListener{
                      QuestionListFragmnet.questionModelist=model.questionList
                    val fragment = QuestionListFragmnet() // Replace with your Fragment class
                    fragmentManager.beginTransaction()
                        .replace(R.id.fragment, fragment)
                        .addToBackStack(null)
                        // Replace with the container's ID
                        .commit()
                }

            }


        }





    }


}