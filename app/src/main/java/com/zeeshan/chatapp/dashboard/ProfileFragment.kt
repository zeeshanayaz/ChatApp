package com.zeeshan.chatapp.dashboard


import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.CircularProgressDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.zeeshan.chatapp.R
import com.zeeshan.chatapp.model.User
import com.zeeshan.chatapp.utilities.AppPref
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.profile_edit_dialog.view.*

/**
 * A simple [Fragment] subclass.
 *
 */
class ProfileFragment : Fragment() {

    //    lateinit var currUser : User
    var selectedPhotoUri: Uri? = null
//    private lateinit var dbReference: FirebaseFirestore


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currUser = AppPref(activity!!).getUser()

        if (currUser != null) {
            setUserDataOnCreate(currUser)
        }



        profileSeletPhotoBtn.setOnClickListener {
            Log.d("ProfileFragment", "Profile Select Photo Button Pressed")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
//            Snackbar.make(view, "Select Photo", Snackbar.LENGTH_SHORT).setAction("Action", null).show()
        }

        profileUserBio.setOnClickListener {
            val editInputDialog = LayoutInflater.from(activity).inflate(R.layout.profile_edit_dialog, null)
            val dialogBuilder = AlertDialog.Builder(activity!!)
                .setView(editInputDialog)
                .setTitle("Update User Detail.")
                .show()

//            if (!currUser?.userBio.isNullOrEmpty()){
//                editInputDialog.inputProfileUserBio.setText(currUser?.userBio)
//            }
//            else{
//                editInputDialog.inputProfileUserBio.setText("")
//            }

            editInputDialog.changeProfileDataBtn.setOnClickListener {
                profileUserBio.setText(editInputDialog.inputProfileUserBio.text.toString())
                val data = User(
                    "${currUser?.userId}",
                    currUser?.userName!!,
                    currUser.userEmail,
                    profileUserBio.text.toString(),
                    currUser.profileImageUrl,
                    currUser.registrationToken
                )
                saveData(data)
                dialogBuilder.dismiss()
            }

        }
    }

    private fun setUserDataOnCreate(currUser: User) {
        if (!currUser.userName.isNullOrEmpty()) {
            profileUserNameText!!.text = currUser.userName
        } else {
            profileUserNameText.setText(getString(R.string.userName))
            profileUserNameText.setTextColor(resources.getColor(R.color.colorSecondaryText))
        }
        profileUserEmail.text = currUser.userEmail

        if (!currUser.userBio.isNullOrEmpty()) {
            profileUserBio.text = currUser.userBio
        } else {
            profileUserBio.setText("No Bio Added")
//                    profileBioTxt.text = "  "
            profileUserBio.setTextColor(resources.getColor(R.color.colorSecondaryText))
        }
        if (!currUser.profileImageUrl.isNullOrEmpty()) {

//                    Loading User Image from Download URi using Glide Library
            profileSeletPhotoBtn.alpha = 0f

            Glide.with(activity!!).applyDefaultRequestOptions(RequestOptions().apply() {
                placeholder(CircularProgressDrawable(activity!!).apply {
                    strokeWidth = 2f
                    centerRadius = 50f
                    start()
                })
            }).load(currUser.profileImageUrl).into(profileImageImageView)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("ProfileFragment", "Photo was selected")

            selectedPhotoUri = data.data
//            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            val inputStream = activity!!.contentResolver.openInputStream(selectedPhotoUri!!)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            profileImageImageView.setImageBitmap(bitmap)
            profileSeletPhotoBtn.alpha = 0f
//            val bitmapDrawable = BitmapDrawable(bitmap)
//            profileSeletPhotoBtn.setBackgroundDrawable(bitmapDrawable)
            uploadImageToFirebase()
        }

    }

    private fun uploadImageToFirebase() {
        val currUser = AppPref(activity!!).getUser()

        val fileName = currUser?.userId
        val ref = FirebaseStorage.getInstance().getReference("/images/profileImages/$fileName")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener { uri ->
                Log.d("ProfileFragment", "Successfully Uploaded Image : ${uri.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    val data = User(
                        "${currUser?.userId}",
                        currUser?.userName!!, currUser.userEmail, currUser.userBio, "$it", currUser.registrationToken
                    )

                    saveData(data)
                }
            }
            .addOnFailureListener {
                Log.v("ProfileFragment", it.toString())
            }
    }

    private fun saveData(data: User) {
        val currUser = AppPref(activity!!).getUser()
        val userRef = FirebaseFirestore.getInstance().collection("Users").document(currUser?.userId!!)
        userRef.set(data)
        AppPref(activity!!).setUser(data)
    }

}
