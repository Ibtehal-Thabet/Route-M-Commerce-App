package com.example.m_commerceapp.ui.fragments.profile

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.m_commerceapp.databinding.FragmentProfileBinding
import com.example.m_commerceapp.ui.activities.login.LoginActivity
import com.example.m_commerceapp.ui.authorization
import com.example.m_commerceapp.ui.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _viewBinding: FragmentProfileBinding? = null
    private val viewBinding get() = _viewBinding!!

    private lateinit var viewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel= ViewModelProvider(this)[ProfileViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _viewBinding = FragmentProfileBinding.inflate(inflater,container,false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()

    }

    private fun initViews() {
//        viewBinding.profileContent.user?.token = authorization

        viewModel.events.observe(viewLifecycleOwner,::handelEvents)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.states.collect {
                renderViewState(it)
            }
//            }
        }

//        enableViews(viewBinding.profileContent.cancelBtn, false)
//        enableViews(viewBinding.profileContent.userFullName,false)
//        enableViews(viewBinding.profileContent.userEmail, false)
//        enableViews(viewBinding.profileContent.userPhone, false)
//        enableViews(viewBinding.profileContent.editBtn, true)

        viewModel.invokeAction(ProfileContract.Action.UserProfile())

        viewModel.firstName.observe(viewLifecycleOwner){firstName ->
            viewBinding.profileContent.userName.text = firstName
        }

        Log.i("userrrr", viewModel.user.value?.name + "   " + viewModel.user.value?.email)

//        viewBinding.profileContent.userFullName.listenChanges {
//            viewModel.setUserName(it)
//            viewModel.setUpdatingDone(true)
//            enableViews(viewBinding.profileContent.editBtn, true)
//        }
        viewModel.name.observe(viewLifecycleOwner){userName ->
            viewBinding.profileContent.userFullName.text = Editable.Factory.getInstance().newEditable(userName)
            viewBinding.profileContent.userFullName.listenChanges {
//            viewModel.setUserName(it)
//            viewModel.setUpdatingDone(true)
                enableViews(viewBinding.profileContent.editBtn, true)

                if (it != viewModel.user.value?.name && viewModel.isCanceling.value == true) {
                    viewModel.setUpdatingDone(true)
//                    enableViews(viewBinding.profileContent.editBtn, true)
                    Log.i("enable", viewModel.user.value?.name.toString())

                } else if (userName == viewModel.user.value?.name && viewModel.isCanceling.value == true) {
                    viewModel.setUpdatingDone(false)
                    enableViews(viewBinding.profileContent.editBtn, false)
                }
            }
        }

//        viewBinding.profileContent.userEmail.listenChanges {
//            viewModel.setUserEmail(it)
//            viewModel.setUpdatingDone(true)
//            enableViews(viewBinding.profileContent.editBtn, true)
//        }
        viewModel.email.observe(viewLifecycleOwner){email ->
            viewBinding.profileContent.userTitleEmail.text = email
            viewBinding.profileContent.userEmail.text = Editable.Factory.getInstance().newEditable(email)

            viewBinding.profileContent.userEmail.listenChanges {
//            viewModel.setUserEmail(it)
//            viewModel.setUpdatingDone(true)
                enableViews(viewBinding.profileContent.editBtn, true)

                if (it != viewModel.user.value?.email && viewModel.isCanceling.value == true) {
                    viewModel.setUpdatingDone(true)
//                enableViews(viewBinding.profileContent.editBtn, true)
                } else if (email == viewModel.user.value?.email && viewModel.isCanceling.value == true) {
                    viewModel.setUpdatingDone(false)
                    enableViews(viewBinding.profileContent.editBtn, false)
                }
            }
        }

//        viewBinding.profileContent.userPhone.listenChanges {
//            viewModel.setUserPhone(it)
//            viewModel.setUpdatingDone(true)
//            enableViews(viewBinding.profileContent.editBtn, true)
//        }
        viewModel.phone.observe(viewLifecycleOwner){phone ->
            viewBinding.profileContent.userPhone.text = Editable.Factory.getInstance().newEditable(phone)

            viewBinding.profileContent.userPhone.listenChanges {
                enableViews(viewBinding.profileContent.editBtn, true)


                if (it != viewModel.user.value?.phone && viewModel.isCanceling.value == true) {
                    viewModel.setUpdatingDone(true)
//                    enableViews(viewBinding.profileContent.editBtn, true)
                } else if (phone == viewModel.user.value?.phone && viewModel.isCanceling.value == true) {
                    viewModel.setUpdatingDone(false)
                    enableViews(viewBinding.profileContent.editBtn, false)
                }
            }
        }

        viewModel.isDoneUpdating.observe(viewLifecycleOwner){
            if (it){
//                viewModel.onEdit()
                enableViews(viewBinding.profileContent.cancelBtn, true)
                enableViews(viewBinding.profileContent.userFullName, true)
                enableViews(viewBinding.profileContent.userEmail, true)
                enableViews(viewBinding.profileContent.userPhone, true)
                enableViews(viewBinding.profileContent.editBtn, true)

            }
            Log.i("is done updating", it.toString())
        }

        viewModel.isUpdating.observe(viewLifecycleOwner){
            if (it){
                enableViews(viewBinding.profileContent.cancelBtn, false)
                enableViews(viewBinding.profileContent.editBtn, true)
                enableViews(viewBinding.profileContent.userFullName,false)
                enableViews(viewBinding.profileContent.userEmail, false)
                enableViews(viewBinding.profileContent.userPhone, false)

            }
            Log.i("is updating", it.toString())
        }

        viewModel.isCanceling.observe(viewLifecycleOwner){
            if (it){
                enableViews(viewBinding.profileContent.cancelBtn, true)
                enableViews(viewBinding.profileContent.editBtn, false)
                enableViews(viewBinding.profileContent.userFullName, true)
                enableViews(viewBinding.profileContent.userEmail, true)
                enableViews(viewBinding.profileContent.userPhone, true)
            }
            Log.i("is canceling", it.toString())
        }

        // edit profile click
        viewBinding.profileContent.editBtn.setOnClickListener {
            if (viewModel.isUpdating.value == true) {
                viewModel.editClick()
                return@setOnClickListener
            }
            if (viewModel.isDoneUpdating.value == true){

                viewModel.setUserName(viewBinding.profileContent.userFullName.text.toString())
                viewModel.setUserEmail(viewBinding.profileContent.userEmail.text.toString())
                viewModel.setUserPhone(viewBinding.profileContent.userPhone.text.toString())
                viewModel.invokeAction(ProfileContract.Action.UpdateProfile())

                Log.i("edit", viewBinding.profileContent.userFullName.text.toString())
            }
        }

        // cancel profile click
        viewBinding.profileContent.cancelBtn.setOnClickListener {
            viewModel.cancelClick()
        }

        // logout click
        viewBinding.profileContent.logout.setOnClickListener{
            logout()
        }
    }

    private fun EditText.listenChanges(textChanged: (String) -> Unit) {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                textChanged(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun handelEvents(event: ProfileContract.Event) {
        when(event){
            is ProfileContract.Event.UpdatedSuccessfully -> showSnackBar(event.message)
            is ProfileContract.Event.UpdatedFailed -> showSnackBar(event.message)
        }
    }

    private fun renderViewState(state: ProfileContract.State) {
        when(state){
            is ProfileContract.State.Loading -> showLoading()
            is ProfileContract.State.Error -> showError(state.message)
            is ProfileContract.State.Success -> TODO()
        }
    }

    private fun showLoading() {
//        _viewBinding?.loadingView?.isVisible = true
//        _viewBinding?.successView?.isVisible = false
//        _viewBinding?.errorView?.isVisible = false
//        _viewBinding?.loadingText?.text= message
    }

    private fun showError(message: String) {
//        _viewBinding?.errorView?.isVisible = true
//        _viewBinding?.loadingView?.isVisible = false
//        _viewBinding?.successView?.isVisible = false
//        _viewBinding?.errorText?.text= message
//        _viewBinding?.btnTryAgain?.setOnClickListener{
//            viewModel.invokeAction(CartContract.Action.GetAuthCart(requireContext()))
//        }
    }

    fun getUserInfo(){
//        viewModel.invokeAction(ProfileContract.Action.UserProfile())
    }

    private fun enableViews(view: View, isEnabled: Boolean) {
        if (isEnabled) {
            view.isEnabled = true
            view.alpha = 1f
        } else {
            view.isEnabled = false
            view.alpha = 0.7f
        }
    }

    private fun logout(){
        viewModel.clearUserInfo(requireContext())
        val intent = Intent(activity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity?.finish() // close current activity so that user can't go back with back button
    }

}