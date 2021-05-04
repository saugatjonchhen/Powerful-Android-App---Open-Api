package com.saugat.openapiapp.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.saugat.openapiapp.R
import com.saugat.openapiapp.ui.auth.state.AuthStateEvent.RegisterAttemptEvent
import com.saugat.openapiapp.ui.auth.state.RegistrationFields
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : BaseAuthFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i(TAG, "RegisterFragment: ${viewModel.hashCode()}")

//        viewModel.testRegister().observe(viewLifecycleOwner, Observer { response ->
//
//            when (response) {
//
//                is GenericApiResponse.ApiSuccessResponse -> {
//                    Log.i(TAG, "REGISTER RESPONSE: ${response.body}")
//                }
//
//                is GenericApiResponse.ApiErrorResponse -> {
//                    Log.i(TAG, "REGISTER RESPONSE: ${response.errorMessage}")
//                }
//
//                is GenericApiResponse.ApiEmptyResponse -> {
//                    Log.i(TAG, "REGISTER RESPONSE: Empty Response")
//                }
//
//            }
//
//        })

        register_button.setOnClickListener {
            register()
        }

        subscribeObservers()

    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            it.registrationFields?.let { registrationFields ->
                registrationFields.registration_email?.let { input_email.setText(it) }
                registrationFields.registration_username?.let { input_username.setText(it) }
                registrationFields.registration_password?.let { input_password.setText(it) }
                registrationFields.registration_confirm_password?.let {
                    input_password_confirm.setText(it)
                }
            }
        })

    }

    fun register() {
        viewModel.setStateEvent(
            RegisterAttemptEvent(
                input_email.text.toString(),
                input_username.text.toString(),
                input_password.text.toString(),
                input_password_confirm.text.toString()
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()

        viewModel.setRegistrationFields(
            RegistrationFields(
                input_email.text.toString(),
                input_username.text.toString(),
                input_password.text.toString(),
                input_password_confirm.text.toString()
            )
        )
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param bundle Parameter 1.
         * @return A new instance of fragment RegisterFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(bundle: Bundle) =
            RegisterFragment().apply {
                arguments = bundle
            }
    }
}