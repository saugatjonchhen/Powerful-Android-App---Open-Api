package com.saugat.openapiapp.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.saugat.openapiapp.R
import com.saugat.openapiapp.ui.auth.state.AuthStateEvent.LoginAttemptEvent
import com.saugat.openapiapp.ui.auth.state.LoginFields
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : BaseAuthFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i(TAG, "LoginFragment: ${viewModel.hashCode()}")

//        viewModel.testLogin().observe(viewLifecycleOwner, Observer { response ->
//
//            when (response) {
//
//                is GenericApiResponse.ApiSuccessResponse -> {
//                    Log.i(TAG, "LOGIN RESPONSE: ${response.body}")
//                }
//
//                is GenericApiResponse.ApiErrorResponse -> {
//                    Log.i(TAG, "LOGIN RESPONSE: ${response.errorMessage}")
//                }
//
//                is GenericApiResponse.ApiEmptyResponse -> {
//                    Log.i(TAG, "LOGIN RESPONSE: Empty Response")
//                }
//
//            }
//
//        })

        subscribeObservers()

        login_button.setOnClickListener {
            login()
        }

    }

    private fun subscribeObservers() {

        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            it.loginFields?.let { loginFields ->
                loginFields.login_email?.let { input_email.setText(it) }
                loginFields.login_password?.let { input_password.setText(it) }
            }
        })

    }

    fun login() {
        viewModel.setStateEvent(
            LoginAttemptEvent(
                input_email.text.toString(),
                input_password.text.toString()
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.setLoginFields(
            LoginFields(
                input_email.text.toString(),
                input_password.text.toString()
            )
        )
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param bundle Parameter 1.
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(bundle: Bundle) =
            LoginFragment().apply {
                arguments = bundle
            }
    }
}