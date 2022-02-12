package com.c1ctech.twitloginexp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.c1ctech.twitloginexp.databinding.ActivityMainBinding
import android.widget.Toast
import com.twitter.sdk.android.core.*
import android.content.Intent
import com.squareup.picasso.Picasso
import com.twitter.sdk.android.core.models.User

class MainActivity : AppCompatActivity() {

    lateinit var activityMainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(activityMainBinding.root)

        loginWithTwitter()
    }

    private fun loginWithTwitter() {

        //if user is not authenticated
        if (getTwitterSession() == null) {

            //Sets the Callback to invoke when login completes.
            activityMainBinding
                .loginButton.setCallback(object : Callback<TwitterSession?>() {

                //Unsuccessful call due to network failure, non-2XX status code, or unexpected exception.
                override fun failure(exception: TwitterException?) {
                    Toast.makeText(
                        applicationContext,
                        "Login failed: " + exception,
                        Toast.LENGTH_LONG
                    ).show()
                }

                //Called when call completes successfully.
                override fun success(result: Result<TwitterSession?>?) {
                    Toast.makeText(applicationContext, "Login successful", Toast.LENGTH_LONG).show()
                    val twitterSession = result?.data

                    //get secret and token from twitterSession
                    /*val token = twitterSession?.authToken?.token
                    val secret = twitterSession?.authToken?.secret*/

                    if (twitterSession != null) {
                        //fetch user twitter info and display it on screen
                        fetchUserTwitterInfo()
                    }
                }
            })
        } else {
            Toast.makeText(this, "User already authenticated", Toast.LENGTH_SHORT).show()
            fetchUserTwitterInfo()
        }
    }

    //return the active session if available
    private fun getTwitterSession(): TwitterSession? {
        val twitterSession = TwitterCore.getInstance().sessionManager.activeSession
        return twitterSession
    }


    // this method will provide you User model which contain all user information
    fun fetchUserTwitterInfo() {

        //fetch twitter image with other information if user is already authenticated
        if (getTwitterSession() != null) {

            //initialize twitter api client
            val twitterApiClient = TwitterCore.getInstance().apiClient

            //pass includeEmail : true if you want to fetch Email as well
            val call =
                twitterApiClient.accountService.verifyCredentials(true, false, true)
            call.enqueue(object : Callback<User?>() {

                // Called when call completes successfully.
                override fun success(result: Result<User?>?) {

                    val user = result?.data

                    activityMainBinding.userDetail.text =
                        """
                        User Id : ${user?.id}
                        User Name : ${user?.name}
                        Email Id : ${user?.email}
                        Screen Name : ${user?.screenName}
                        """.trimIndent()

                    var imageProfileUrl = user?.profileImageUrl

                    //NOTE : User profile provided by twitter is very small in size
                    //Link : https://developer.twitter.com/en/docs/accounts-and-users/user-profile-images-and-banners
                    //so if you want to get bigger size image then do the following:
                    imageProfileUrl = imageProfileUrl?.replace("_normal", "")

                    //load image using Picasso
                    Picasso.with(this@MainActivity)
                        .load(imageProfileUrl)
                        .placeholder(R.drawable.placeholder)
                        .into(activityMainBinding.userImage)
                }

                // Unsuccessful call due to unexpected exception.
                override fun failure(exception: TwitterException?) {
                    Toast.makeText(
                        this@MainActivity,
                        exception?.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })
        } else {
            //if user is not authenticated first ask user to do authentication
            Toast.makeText(this, "User is not authenticated.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        // Pass the activity result to the login button.
        activityMainBinding.loginButton.onActivityResult(requestCode, resultCode, data)
    }
}