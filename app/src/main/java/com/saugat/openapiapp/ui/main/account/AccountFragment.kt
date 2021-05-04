package com.saugat.openapiapp.ui.main.account

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.saugat.openapiapp.R
import com.saugat.openapiapp.models.AccountProperties
import com.saugat.openapiapp.ui.main.account.state.AccountStateEvent.GetAccountPropertiesEvent
import kotlinx.android.synthetic.main.fragment_account.*

class AccountFragment : BaseAccountFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)


        change_password.setOnClickListener {
            findNavController().navigate(R.id.action_accountFragment_to_changePasswordFragment)
        }

        logout_button.setOnClickListener {
            viewModel.logOut()
        }

        subscribeObservers()
    }

    private fun subscribeObservers() {

        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateChangeListener.onDataStateChanged(dataState)
            if (dataState != null) {
                dataState.data?.let { data ->
                    data.data?.let { event ->
                        event.getContentIfNotHandled()?.let { viewState ->
                            viewState.accountProperties?.let { accountProperties ->
                                Log.i(TAG, "AccountFragment: DataState: ${accountProperties}")
                                viewModel.setAccountPropertiesData(accountProperties)
                            }
                        }
                    }
                }
            }
        })

        viewModel.viewState.observe(this, Observer { viewState ->
            viewState.accountProperties?.let {
                Log.d(TAG, "AccountFragment: ViewState ${viewState}")
                setAccountDataFields(it)
            }
        })

    }

    override fun onResume() {
        super.onResume()

        viewModel.setStateEvent(
            GetAccountPropertiesEvent()
        )
    }

    private fun setAccountDataFields(accountProperties: AccountProperties) {
        email?.text = accountProperties.email
        username?.text = accountProperties.username
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.edit_view_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val isAuthorOfBlogPost = true
        if (isAuthorOfBlogPost) {
            when (item.itemId) {
                R.id.edit -> {
                    findNavController().navigate(R.id.action_accountFragment_to_updateAccountFragment)
                    return true
                }
            }
        }
        return super.onOptionsItemSelected(item)

    }


}