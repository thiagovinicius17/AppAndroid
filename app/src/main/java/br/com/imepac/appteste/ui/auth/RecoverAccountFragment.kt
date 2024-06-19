package br.com.imepac.appteste.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import br.com.imepac.appteste.R
import br.com.imepac.appteste.databinding.FragmentLoginBinding
import br.com.imepac.appteste.databinding.FragmentRecoverAccountBinding
import br.com.imepac.appteste.helper.BaseFragment
import br.com.imepac.appteste.helper.FirebaseHelper
import br.com.imepac.appteste.helper.initToolbar
import br.com.imepac.appteste.helper.showBottomSheet
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class RecoverAccountFragment : BaseFragment() {

    private var _binding: FragmentRecoverAccountBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecoverAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)
        auth = Firebase.auth
        initClicks()

    }

    private fun initClicks() { binding.btnRecover.setOnClickListener { validateData() } }

    private fun validateData() {

        val email = binding.recoverEmail.text.toString().trim()
        if (email.isNotEmpty()) {
            hideKeyboard()
            binding.progressBar.isVisible = true
            recoverAccountUser(email)
        } else {
            showBottomSheet(message = R.string.text_email_empty_recover_account_fragment)
        }

    }

    private fun recoverAccountUser(email: String){

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    showBottomSheet(message = R.string.text_email_send_sucess_recover_account_fragment)
                } else {
                    ""
                }
                binding.progressBar.isVisible = false
            }

    }

    override fun onDestroyView() {

        super.onDestroyView()
        _binding = null

    }

}