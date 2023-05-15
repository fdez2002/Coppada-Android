package com.fdez.projecttfg.ui.account

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.fdez.projecttfg.databinding.FragmentAccountBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class AccountFragment : Fragment() {
    private var _binding: FragmentAccountBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private var email: String? = null
    private var nombre: String? = null
    private var passwo: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        val root: View = binding.root
        auth = Firebase.auth

        detectarLoginUser()


        binding.buttonLogin.setOnClickListener{
            if (binding.tiResg.isVisible){
                binding.tiResg.isVisible = false
            }else{
                login()

            }

        }
        binding.buttonResgistrar.setOnClickListener{
            if (!binding.tiResg.isVisible){
                binding.tiResg.isVisible = true
            }else{
                registerUser()
            }


        }

        binding.buttonCerrarSesi.setOnClickListener{
            cerrarSesion()
        }
        binding.buttonCamContra.setOnClickListener{
            resetPass()
        }

        return root
    }


    private fun resetPass() {
        //val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val emailAddress = user!!.email

        if (emailAddress != null) {
            auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        showAlert(requireContext(), "",
                            "Se ha enviado un correo para restablecer la contraseña a $emailAddress. Si no lo ves, revisa el spam", )
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Error al enviar el correo de restablecimiento de contraseña",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

    }

    private fun activarIniciosesion(){
       //binding.buttonCamContra.isVisible = false
        binding.buttonCerrarSesi.isVisible = false
        binding.buttonCamContra.isVisible = false
        binding.tiResg.isVisible = false

        binding.buttonResgistrar.isVisible = true
        binding.buttonLogin.isVisible = true
        binding.tiContrase.isVisible = true
    }
    private fun desacticarInicioSesion(){
       //binding.buttonCamContra.isVisible = true
        binding.buttonCerrarSesi.isVisible = true
        binding.buttonCamContra.isVisible = true
        binding.tiResg.isVisible = true

        binding.buttonResgistrar.isVisible = false
        binding.buttonLogin.isVisible = false
        binding.tiContrase.isVisible = false
    }

    private fun cerrarSesion() {
        FirebaseAuth.getInstance().signOut()
        Toast.makeText(requireContext(), "Sesión cerrada", Toast.LENGTH_LONG).show()
        activarIniciosesion()

    }


    private fun detectarLoginUser() {
        //Obtener datos del usuario y asignar a los TextField
        val currentUser = auth.currentUser

        if (currentUser != null) {

            if (currentUser.isEmailVerified) {
                //El correo electrónico del usuario ha sido verificado
                desacticarInicioSesion()
                email = currentUser.email
                nombre = currentUser.displayName
                if (email != null) {
                    binding.textFieldCorreo.setText(email)
                }
                if (nombre != null) {
                    binding.textFieldNombre.setText(nombre)
                }
            } else {
                //El correo electrónico del usuario no ha sido verificado
                Toast.makeText(requireContext(), "Por favor, verifica tu correo electrónico", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun login() {
        // Verificar si hay un usuario actualmente autenticado
        val currentUser = auth.currentUser

        if (currentUser != null) {
            //El usuario está autenticado y registrado en Firebase
            val uid = currentUser.uid
            //val email = currentUser.email
            val email = binding.textFieldCorreo.text.toString()
            val password = binding.textFieldContrasena.text.toString()
            //Verificar si el usuario ha verificado su correo electrónico
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //El inicio de sesión ha sido exitoso
                        detectarLoginUser()
                    } else {
                        Toast.makeText(requireContext(), "Usuario no registrado o datos incorrectos", Toast.LENGTH_SHORT).show()
                        //El inicio de sesión ha fallado
                    }
                }

        } else {
            val email = binding.textFieldCorreo.text.toString()
            val password = binding.textFieldContrasena.text.toString()
            // El usuario no está autenticado o no está registrado en Firebase
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Los campos estan vacios", Toast.LENGTH_SHORT).show()
            } else {
                //Ejecutar el inicio de sesión con Firebase Auth
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            //El inicio de sesión ha sido exitoso
                            detectarLoginUser()
                        } else {
                            Toast.makeText(requireContext(), "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                            //El inicio de sesión ha fallado
                        }
                    }
            }
        }
    }
    private fun registerUser() {
        val email = binding.textFieldCorreo.text.toString()
        val password = binding.textFieldContrasena.text.toString()
        val nombre = binding.textFieldNombre.text.toString()

        if (email.isEmpty() || password.isEmpty() || nombre.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor, introduce todos los datos", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //El registro ha sido exitoso
                    val user = auth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(nombre)
                        .build()
                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                //El nombre del usuario ha sido actualizado correctamente
                                Toast.makeText(requireContext(), "Registro exitoso", Toast.LENGTH_SHORT).show()
                                correoVerificacion()

                                if (user != null) {
                                    if (user.isEmailVerified) {
                                        //El correo electrónico del usuario ha sido verificado
                                        //Puede permitir que el usuario acceda a las funciones de la aplicación que requieren autenticación
                                        desacticarInicioSesion()
                                    } else {
                                        //El correo electrónico del usuario no ha sido verificado
                                        //Muestra un mensaje al usuario para que verifique su correo electrónico y proporciona un botón para enviar un correo electrónico de verificación.
                                        Toast.makeText(requireContext(), "Verifica tu correo electrónico.", Toast.LENGTH_LONG).show()
                                    }
                                } else {
                                    //No hay ningún usuario autenticado actualmente
                                }
                            } else {
                                //Ha ocurrido un error al actualizar el nombre del usuario
                                Toast.makeText(requireContext(), "Error al actualizar el perfil", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    showAlert(requireContext(), "", "Error, puede que el correo ya exista o que los campos sean incorrectos.")

                }
            }
    }
    //Función para mostrar el AlertDialog
    fun showAlert(context: Context, title: String, message: String) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("ok") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun correoVerificacion(){
        val user = auth.currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //El correo de verificación ha sido enviado exitosamente
                    Toast.makeText(requireContext(), "Se ha enviado un correo de verificación", Toast.LENGTH_SHORT).show()
                } else {
                    //Ha ocurrido un error al enviar el correo de verificación
                    Toast.makeText(requireContext(), "Error al enviar el correo de verificación", Toast.LENGTH_SHORT).show()
                }
            }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}