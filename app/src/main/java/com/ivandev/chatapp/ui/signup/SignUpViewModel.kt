package com.ivandev.chatapp.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.ivandev.chatapp.data.firebase.FirestoreService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val firestoreService: FirestoreService
) : ViewModel() {
    private val _isVerified = MutableStateFlow(false)
    val isVerified: Flow<Boolean> get() = _isVerified
    private var flag = true

    fun checkUserIsVerified(firebaseUser: FirebaseUser) {
        viewModelScope.launch {
            while (flag) {
                val user = firebaseUser.reload()
                user.addOnSuccessListener {
                    val condition = firebaseUser.isEmailVerified
                    _isVerified.value = condition
                    if (condition) {
                        flag = false
                    }
                }
                delay(2000)
            }
        }
    }

    fun createAccount(userName: String, uid: String) {
        firestoreService.createAccount(userName, uid)
    }
}