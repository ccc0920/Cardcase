package com.team4.cardcase2.foreground

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.team4.cardcase2.AppSession
import com.team4.cardcase2.R
import com.team4.cardcase2.entity.UserCardsResponse
import com.team4.cardcase2.interfaces.HttpRequest
import java.io.ByteArrayOutputStream

private const val PICK_AVATAR_REQUEST = 42

class InfoFragment : Fragment() {

    private var avatarBase64: String = ""

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_info, container, false)
        val ctx = requireContext()

        val inputName: EditText = root.findViewById(R.id.inputName)
        val inputPhone: EditText = root.findViewById(R.id.inputPhone)
        val inputBio: EditText = root.findViewById(R.id.inputBio)
        val profileEmail: TextView = root.findViewById(R.id.profileEmail)
        val profileUserId: TextView = root.findViewById(R.id.profileUserId)
        val profileAvatar: ImageView = root.findViewById(R.id.profileAvatar)
        val genderGroup: RadioGroup = root.findViewById(R.id.genderGroup)

        inputName.setText(AppSession.getUserName(ctx))
        inputPhone.setText(AppSession.getUserPhone(ctx))
        inputBio.setText(AppSession.getUserBio(ctx))
        profileEmail.text = AppSession.getEmail(ctx).ifEmpty { "—" }
        profileUserId.text = "#${AppSession.getUserId(ctx)}"

        when (AppSession.getUserGender(ctx)) {
            "Male"              -> root.findViewById<RadioButton>(R.id.genderMale).isChecked = true
            "Female"            -> root.findViewById<RadioButton>(R.id.genderFemale).isChecked = true
            "Non-binary"        -> root.findViewById<RadioButton>(R.id.genderNonbinary).isChecked = true
            "Prefer not to say" -> root.findViewById<RadioButton>(R.id.genderOther).isChecked = true
        }

        // Load saved local avatar first
        val localAvatar = AppSession.getLocalAvatar(ctx)
        if (localAvatar.isNotEmpty()) {
            val bytes = Base64.decode(localAvatar, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.let { profileAvatar.setImageBitmap(it) }
        } else {
            // Fall back to card avatar from server
            val userId = AppSession.getUserId(ctx)
            val token = AppSession.getToken(ctx)
            if (userId > 0 && token.isNotEmpty()) {
                HttpRequest().sendGetRequest(
                    "http://10.0.2.2:8080/api/cards/user/$userId", token
                ) { response, _ ->
                    activity?.runOnUiThread {
                        try {
                            val result = UserCardsResponse.fromJson(response ?: return@runOnUiThread)
                            val avatar = result.cards.firstOrNull { it.avatar.isNotEmpty() }?.avatar
                            if (avatar != null) {
                                val bytes = Base64.decode(avatar, Base64.DEFAULT)
                                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                if (bmp != null) profileAvatar.setImageBitmap(bmp)
                            }
                        } catch (_: Exception) {}
                    }
                }
            }
        }

        // Click avatar to pick from gallery
        profileAvatar.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_AVATAR_REQUEST)
        }

        root.findViewById<TextView>(R.id.backLast).setOnClickListener {
            findNavController().navigate(R.id.action_infoFragment_to_settingsFragment)
        }

        root.findViewById<TextView>(R.id.saveButton).setOnClickListener {
            AppSession.setUserName(ctx, inputName.text.toString().trim())
            AppSession.setUserPhone(ctx, inputPhone.text.toString().trim())
            AppSession.setUserBio(ctx, inputBio.text.toString().trim())

            val selectedId = genderGroup.checkedRadioButtonId
            if (selectedId != -1) {
                AppSession.setUserGender(ctx, root.findViewById<RadioButton>(selectedId).text.toString())
            }

            if (avatarBase64.isNotEmpty()) {
                AppSession.saveLocalAvatar(ctx, avatarBase64)
            }

            Toast.makeText(ctx, "Profile saved", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_infoFragment_to_settingsFragment)
        }

        return root
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_AVATAR_REQUEST && resultCode == Activity.RESULT_OK && data?.data != null) {
            val uri: Uri = data.data!!
            val stream = context?.contentResolver?.openInputStream(uri) ?: return
            val bitmap = BitmapFactory.decodeStream(stream) ?: return
            val scaled = Bitmap.createScaledBitmap(bitmap, 200, 200, true)
            val out = ByteArrayOutputStream()
            scaled.compress(Bitmap.CompressFormat.JPEG, 80, out)
            avatarBase64 = Base64.encodeToString(out.toByteArray(), Base64.DEFAULT)
            view?.findViewById<ImageView>(R.id.profileAvatar)?.setImageBitmap(scaled)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) = InfoFragment()
    }
}
