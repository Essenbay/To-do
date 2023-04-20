package com.bigneardranch.to_do.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bigneardranch.to_do.R
import com.bigneardranch.to_do.ui.adapters.TaskListAdapter
import com.bigneardranch.to_do.databinding.FragmentTaskListBinding
import com.bigneardranch.to_do.data.database.TaskModel
import com.bigneardranch.to_do.data.notifications.CurrentTaskNotification
import com.bigneardranch.to_do.listeners.TaskGestureListener
import com.bigneardranch.to_do.ui.viewmodels.TaskListViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch

class TaskListFragment : Fragment() {
    private var _binding: FragmentTaskListBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "FragmentTaskListBinding is null"
        }
    private val taskListViewModel: TaskListViewModel by viewModels { TaskListViewModel.Factory }
    private var showCompletedMenuItem: MenuItem? = null
    private var getDailyNotificationsMenuItem: MenuItem? = null
    private var makeSomeNotificationsPersistentMenuItem: MenuItem? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_menu, menu)
                showCompletedMenuItem = menu.findItem(R.id.showCompleted)
                getDailyNotificationsMenuItem = menu.findItem(R.id.getDailyNotifications)
                makeSomeNotificationsPersistentMenuItem =
                    menu.findItem(R.id.makeSomeNotificationsPersistent)

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.search_task_btn -> {
                        true
                    }
                    R.id.clear_all_tasks -> {
                        taskListViewModel.deleteAllTasks()
                        true
                    }
                    R.id.showCompleted -> {
                        taskListViewModel.updateShowCompletedTasks()
                        true
                    }
                    R.id.getDailyNotifications -> {
                        taskListViewModel.updateGetDailyNotifications()
                        true
                    }
                    R.id.makeSomeNotificationsPersistent -> {
                        taskListViewModel.updateMakeSomeNotificationsPersistent()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)


        binding.apply {
            viewModel = taskListViewModel

            addTaskBtn.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    val newTaskText = taskTitleEdit.text.toString()
                    val newTask = TaskModel(
                        Math.random().toInt(),
                        newTaskText,
                        false,
                        isInProgress = false
                    )
                    binding.taskTitleEdit.text.clear()
                    taskListViewModel.addTask(newTask)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                taskListViewModel.tasksUI.collect { tasksUi ->
                    binding.taskRecycleView.adapter = TaskListAdapter(
                        tasksUi.tasks,
                        object : TaskGestureListener {
                            override fun onClick(taskId: Int) {
                                Toast.makeText(
                                    requireContext(),
                                    "Task was selected: $taskId",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            }

                            override fun onLongClick(v: View, taskId: Int) {
                                showTaskPopup(v, taskId)
                            }

                            override fun onSolvedCheckClicked(
                                task: TaskModel,
                                isChecked: Boolean
                            ) {
                                taskListViewModel.updateTaskSolvedIsChecked(task, isChecked)
                            }

                            override fun onInProgressCheckClicked(
                                task: TaskModel,
                                isChecked: Boolean
                            ) {
                                taskListViewModel.updateTaskInProgressIsChecked(task, isChecked)
                                if (isChecked) {
                                    CurrentTaskNotification.notifyCurrentTask(
                                        requireContext(),
                                        task
                                    )
                                } else {
                                    CurrentTaskNotification.cancelCurrentTask(
                                        requireContext(),
                                        task
                                    )
                                }
                            }
                        }
                    )
                    binding.taskRecycleView.layoutManager = LinearLayoutManager(requireContext())
                    Log.d("TaskListFragment", "UserPreferences: ${tasksUi.userPreferences}")
                    updateShowCompletedMenu(tasksUi.userPreferences.showCompleted)
                    updateGetDailyNotificationsMenu(tasksUi.userPreferences.getDailyNotifications)
                    updateMakeSomeNotificationsPersistentMenu(tasksUi.userPreferences.makeSomeNotificationsPersistent)
                }
            }
        }
    }

    private fun showTaskPopup(v: View, taskId: Int) {
        val popup = PopupMenu(requireContext(), v)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.task_menu, popup.menu)
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.delete_task -> {
                    taskListViewModel.deleteTask(taskId)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun updateShowCompletedMenu(showCompleted: Boolean) {
        val title = if (showCompleted) R.string.hideCompleted
        else R.string.showCompleted
        showCompletedMenuItem?.setTitle(title)
    }

    private fun updateGetDailyNotificationsMenu(getDailyNotifications: Boolean) {

        val title = if (getDailyNotifications) R.string.notGetDailyNotifications
        else R.string.getDailyNotifications
        getDailyNotificationsMenuItem?.setTitle(title)

        if (getDailyNotifications) {
            //Work
        }
    }

    private fun updateMakeSomeNotificationsPersistentMenu(makeSomeNotificationsPersistent: Boolean) {
        val title = if (makeSomeNotificationsPersistent) R.string.makeSomeNotificationsNotPersistent
        else R.string.makeSomeNotificationsPersistent
        makeSomeNotificationsPersistentMenuItem?.setTitle(title)

        if (makeSomeNotificationsPersistent) {
            //Work
        }
    }


    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}