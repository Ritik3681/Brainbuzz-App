package com.example.aptitude

data class DataClass(
    val id:String,
    val CategorieName:String,
    val questionList:List<QuestionModel>

)
{
    constructor(): this("","", emptyList())
}
data class QuestionModel(
    val question:String,
    val option: List<String>,
    val Correct:String,
){
    constructor() : this("", emptyList(),"")
}

