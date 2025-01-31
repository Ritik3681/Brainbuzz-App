package com.example.aptitude

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class QuizCategoriesFragment : Fragment() {

    private var arrayList: MutableList<DataClass> = arrayListOf()
    private var quizCategoryAdapter: QuizCategorieAdapater? = null
    private lateinit var recyclerView:RecyclerView
    private lateinit var progressbar:ProgressBar
    var listener:OnAdvanceClickedListerner?=null
    lateinit var advancebtn:TextView

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // Handle any arguments if needed
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_quiz_categories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("back", "onViewCreated: backpress")
        progressbar = view.findViewById(R.id.progressBar)
         recyclerView = view.findViewById(R.id.quizCategorieRecyclerview)
        getDataFromFirebase(view)

        mAuth = FirebaseAuth.getInstance()
        progressbar.visibility = View.VISIBLE

        val logoutTextView: ImageView = view.findViewById(R.id.logout)
        logoutTextView.setOnClickListener {
            logoutUser()
        }

        advancebtn=view.findViewById(R.id.advanced)
        advancebtn.setOnClickListener{
            listener?.OnAvancedCleicked()
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener=context as OnAdvanceClickedListerner
    }

    private fun setUpRecyclerview() {
      //  val recyclerView: RecyclerView = view?.findViewById(R.id.quizCategorieRecyclerview)
        recyclerView.layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        recyclerView.setHasFixedSize(true)
        quizCategoryAdapter = QuizCategorieAdapater(arrayList, requireFragmentManager())
        recyclerView.adapter = quizCategoryAdapter
    }

//    private fun getDataFromFirebase(view: View) {
//
//        if (!isNetworkAvailable(requireContext())) {
//            Toast.makeText(activity, "No internet connection. Please check your connection and try again.", Toast.LENGTH_LONG).show()
//            progressbar.visibility = View.GONE
//            return
//        }
//
//        FirebaseDatabase.getInstance().reference
//            .get()
//            .addOnSuccessListener { dataSnapshot ->
//                if (dataSnapshot.exists()) {
//                    arrayList.clear() // Clear the list to avoid duplication
//
//                    for (data1 in dataSnapshot.children) {
//
//                        val quizModel = data1.children.iterator()
//                        while (quizModel.hasNext()){
//                           var data =  quizModel.next()
//
//                            Log.d("data111", "getDataFromFirebase: ${data.value}")
////                            forEach{ mydata->
//                          var finalData =   data.getValue(DataClass::class.java)
//                            if (finalData != null) {
//                                arrayList.add(finalData)
//                            }
//
////                            }
//                        }
////                        if (quizModel != null) {
////                            arrayList.add(quizModel)
////                        }
//                    }
//
//                    setUpRecyclerview()// Call setupRecyclerView after data is added
//                    progressbar.visibility = View.GONE
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.e("FirebaseError", "Error getting data", exception)
//            }
//
//
//    }
private fun getDataFromFirebase(view: View) {
    if (!isNetworkAvailable(requireContext())) {
        Toast.makeText(activity, "No internet connection. Please check your connection and try again.", Toast.LENGTH_LONG).show()
        progressbar.visibility = View.GONE
        return
    }

    FirebaseDatabase.getInstance().reference
        .get()
        .addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                arrayList.clear() // Clear the list to avoid duplication

                for (data1 in dataSnapshot.children) {
                    val quizModel = data1.getValue(DataClass::class.java)
                    if (quizModel != null) {
                        arrayList.add(quizModel)
                    }
                }

                setUpRecyclerview() // Call setupRecyclerView after data is added
                progressbar.visibility = View.GONE
            }
        }
        .addOnFailureListener { exception ->
            Log.e("FirebaseError", "Error getting data", exception)
        }
}




    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            return networkInfo.isConnected
        }
    }

    private fun logoutUser() {
        // Sign out the user from Firebase
        mAuth .signOut()

        // Navigate back to the LoginFragment
        val fragmentManager = parentFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.fragment, Login()) // Use the correct container ID
            .commit()
    }

    interface OnAdvanceClickedListerner{
        fun OnAvancedCleicked()
    }
}
