package br.com.imepac.appteste.ui.auth

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import br.com.imepac.appteste.R
import br.com.imepac.appteste.databinding.FragmentLoginBinding
import br.com.imepac.appteste.helper.BaseFragment
import br.com.imepac.appteste.helper.FirebaseHelper
import br.com.imepac.appteste.helper.showBottomSheet
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class LoginFragment : BaseFragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        initClicks()

    }

    private fun initClicks(){

        binding.btnLogin.setOnClickListener { validateData()}
        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        binding.btnRecover.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_recoverAccountFragment)
        }

    }

    private fun validateData() {

        val email = binding.logEmail.text.toString().trim()
        val password = binding.logPassword.text.toString().trim()
        if (email.isNotEmpty()) {
            if (password.isNotEmpty()) {
                hideKeyboard()
                binding.progressBar.isVisible = true
                loginUser(email, password)
            } else {
                showBottomSheet(message = R.string.text_password_empty_login_fragment)
            }
        } else {
            showBottomSheet(message = R.string.text_email_empty_login_fragment)
        }

    }

    private fun loginUser(email: String, password: String){

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    findNavController().navigate(R.id.action_global_homeFragment)
                } else {

                    Log.i("INFOTESTE", "loginUser: ${task.exception?.message}")
                    binding.progressBar.isVisible = false

                }
            }

    }

    override fun onDestroyView() {

        super.onDestroyView()
        _binding = null

    }

}