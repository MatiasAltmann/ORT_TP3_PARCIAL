package ar.edu.ort.tp3.parcialtp3ort.Models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import android.net.Uri

class LoginViewModel:ViewModel() {

    val usuario = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    //val clave = MutableLiveData<String>()
    var photoUrl: Uri? = Uri.EMPTY


    /*fun guardarCredenciales(nombre: String, password: String) {
        usuario.value = nombre
        clave.value = password
    }*/
    fun guardarCredenciales(user: FirebaseUser?) {
        user?.let {
            // Name, email address, and profile photo Url
            usuario.value = it.displayName
            email.value = it.email
            photoUrl = it.photoUrl


            // Check if user's email is verified
            //val emailVerified = it.isEmailVerified

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            //val uid = it.uid
        }
    }
}