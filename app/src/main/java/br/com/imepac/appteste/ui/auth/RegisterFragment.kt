package br.com.imepac.appteste.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import br.com.imepac.appteste.R
import br.com.imepac.appteste.databinding.FragmentRegisterBinding
import br.com.imepac.appteste.helper.BaseFragment
import br.com.imepac.appteste.helper.FirebaseHelper
import br.com.imepac.appteste.helper.initToolbar
import br.com.imepac.appteste.helper.showBottomSheet
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class RegisterFragment : BaseFragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)
        auth = Firebase.auth
        initClicks()
    }

    private fun initClicks(){ binding.btnRegister.setOnClickListener{ validateData() } }

    private fun validateData(){

        val email = binding.editEmail.text.toString().trim()
        val password = binding.editPassword.text.toString().trim()
        if (email.isNotEmpty()){
            if (password.isNotEmpty()){
                hideKeyboard()
                binding.progressBar.isVisible = true
                registerUser(email,password)
            }else{
                showBottomSheet(message = R.string.text_password_empty_register_fragment)
            }
        }else{
            showBottomSheet(message = R.string.text_email_empty_register_fragment)
        }

    }

    private fun registerUser(email: String, password: String){

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    findNavController().navigate(R.id.action_global_homeFragment)
                } else {
                    ""
                    binding.progressBar.isVisible = false
                }
            }

    }

    override fun onDestroyView() {

        super.onDestroyView()
        _binding = null

    }

}