package com.fdez.projecttfg

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import com.fdez.projecttfg.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.buttonLogin.setOnClickListener{
            if (binding.tiResg.isVisible){
                binding.tiResg.isVisible = false
            }else{
                setup()

            }

        }
        binding.buttonResgistrar.setOnClickListener{
            binding.tiResg.isVisible = true
        }
        return root
    }

    private fun setup() {
        // Inicializar Firebase Auth
        auth = Firebase.auth

        // Verificar si hay un usuario actualmente autenticado
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // El usuario está autenticado y registrado en Firebase
            val uid = currentUser.uid
            val email = currentUser.email
            Toast.makeText(requireContext(), uid, Toast.LENGTH_LONG).show()


        } else {
            val email = binding.textFieldCorreo.text.toString()
            val password = binding.textFieldContrasena.text.toString()
            //El usuario no está autenticado o no está registrado en Firebase
            if (email.isEmpty() && password.isEmpty()) {
                Toast.makeText(requireContext(), "Los campos estan vacios", Toast.LENGTH_LONG).show()
            }else{
                // Ejecutar el inicio de sesión con Firebase Auth
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            //El inicio de sesión ha sido exitoso
                            Toast.makeText(requireContext(), "Hola", Toast.LENGTH_LONG).show()
                            val user = auth.currentUser
                        } else {
                            Toast.makeText(requireContext(), "Adios", Toast.LENGTH_LONG).show()
                            //El inicio de sesión ha fallado
                        }
                    }

            }



        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

