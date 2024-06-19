package br.com.imepac.appteste.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.imepac.appteste.R
import br.com.imepac.appteste.databinding.FragmentDoingBinding
import br.com.imepac.appteste.databinding.FragmentDoneBinding
import br.com.imepac.appteste.helper.FirebaseHelper
import br.com.imepac.appteste.helper.showBottomSheet
import br.com.imepac.appteste.model.Task
import br.com.imepac.appteste.ui.adapter.TaskAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class DoneFragment : Fragment() {

    private var _binding : FragmentDoneBinding? = null
    private val binding get() = _binding!!

    private lateinit var taskAdapter: TaskAdapter

    private val taskList = mutableListOf<Task>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        getTasks()

    }

    private fun getTasks() {
        FirebaseHelper
            .getDatabase()
            .child("task")
            .child(FirebaseHelper.getIdUser() ?: "")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {

                        taskList.clear()
                        for (snap in snapshot.children) {
                            val task = snap.getValue(Task::class.java) as Task

                            if (task.status == 2) taskList.add(task)
                        }

                        taskList.reverse()
                        initAdapter()
                    }

                    tasksEmpty()
                    binding.progressBar.isVisible = false
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Erro", Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun tasksEmpty(){

        binding.textInfo.text = if (taskList.isEmpty()){
            getText(R.string.tasks_list_empty)
        }else{
            ""
        }

    }

    private fun initAdapter(){

        binding.rvTask.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTask.setHasFixedSize(true)
        taskAdapter = TaskAdapter(requireContext(),taskList){ task, select ->
            optionSelect(task, select)
        }
        binding.rvTask.adapter = taskAdapter

    }

    private fun optionSelect(task: Task, select: Int){

        when(select){
            TaskAdapter.SELECT_REMOVE ->{
                deleteTask(task)
            }
            TaskAdapter.SELECT_EDIT ->{
                val action = HomeFragmentDirections
                    .actionHomeFragmentToFormTaskFragment(task)
                findNavController().navigate(action)
            }
            TaskAdapter.SELECT_BACK -> {
                task.status = 1
                updateTask(task)
            }
        }

    }

    private fun updateTask(task: Task){

        FirebaseHelper
            .getDatabase()
            .child("task")
            .child(FirebaseHelper.getIdUser()?:"")
            .child(task.id)
            .setValue(task)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    showBottomSheet(message = R.string.sucess_change_status)
                }else{
                    showBottomSheet(message = R.string.error_change_status)
                }
            }.addOnFailureListener {
                binding.progressBar.isVisible  = false
                showBottomSheet(message = R.string.error_change_status)
            }

    }

    private fun deleteTask(task: Task){

        FirebaseHelper
            .getDatabase()
            .child("task")
            .child(FirebaseHelper.getIdUser() ?: "")
            .child(task.id)
            .removeValue()
        taskList.remove(task)
        taskAdapter.notifyDataSetChanged()

    }

    override fun onDestroyView() {

        super.onDestroyView()
        _binding = null

    }

}