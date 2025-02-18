package br.com.imepac.appteste.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.imepac.appteste.R
import br.com.imepac.appteste.databinding.FragmentFormTaskBinding
import br.com.imepac.appteste.databinding.FragmentTodoBinding
import br.com.imepac.appteste.helper.BaseFragment
import br.com.imepac.appteste.helper.FirebaseHelper
import br.com.imepac.appteste.helper.initToolbar
import br.com.imepac.appteste.helper.showBottomSheet
import br.com.imepac.appteste.model.Task

class FormTaskFragment : BaseFragment() {

    private val args: FormTaskFragmentArgs by navArgs()
    
    private var _binding : FragmentFormTaskBinding? = null
    private val binding get() = _binding!!

    private lateinit var task : Task
    private var newTask: Boolean = true
    private var statusTask: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormTaskBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
       
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)
        initListeners()
        getArgs()
        
    }

    private fun getArgs(){

        args.task.let{
            if (it != null){
                task =it
            configTask()
            }
        }

    }

    private fun configTask(){

        newTask = false
        statusTask = task.status
        binding.textToolbar.text = getText(R.string.text_title_edit_form_task_fragment)
        binding.edtDescription.setText(task.description)
        setStatus()

    }

    private fun setStatus(){

        binding.radioGroup.check(
            when(task.status){
                0 -> {
                    R.id.rdTodo
                }
                1 -> {
                    R.id.rdDoing
                }
                else ->{
                    R.id.rdDone
                }
            }
        )

    }

    private fun initListeners(){
        
        binding.btnSave.setOnClickListener { validateData() }
        binding.radioGroup.setOnCheckedChangeListener { _,id  ->
            statusTask = when(id){
                R.id.rdTodo -> 0
                R.id.rdDoing -> 1
                else -> 2
            }
        }
        
    }

    private fun validateData(){
        
        val description = binding.edtDescription.text.toString().trim()
        if (description.isNotEmpty()){
            hideKeyboard()
            binding.progressBar.isVisible  = true
            if(newTask) task = Task()
            task.description = description
            task.status = statusTask
            saveTask()
        }else{
            showBottomSheet(message = R.string.text_description_empty_form_task_fragment)
        }

    }

    private fun saveTask(){

        FirebaseHelper
            .getDatabase()
            .child("task")
            .child(FirebaseHelper.getIdUser()?: "")
            .child(task.id)
            .setValue(task)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    if(newTask) { // Nova tarefa
                        findNavController().popBackStack()
                        showBottomSheet(message = R.string.text_form_sucess_save_task_form_task_fragment)
                    }else{ // Editando tarefa
                        binding.progressBar.isVisible  = false
                        findNavController().popBackStack()
                        showBottomSheet(message = R.string.text_form_sucess_changed_task_form_task_fragment)
                    }
                }else{
                    showBottomSheet(message = R.string.text_erro_task_form_task_fragment)
                }
            }.addOnFailureListener {
                binding.progressBar.isVisible  = false
                showBottomSheet(message = R.string.text_erro_task_form_task_fragment)
            }

    }

    override fun onDestroyView() {

        super.onDestroyView()
        _binding = null

    }
}