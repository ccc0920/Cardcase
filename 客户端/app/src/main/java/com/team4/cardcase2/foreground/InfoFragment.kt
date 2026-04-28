package com.team4.cardcase2.foreground

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
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

class InfoFragment : Fragment() {

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

        // Populate from session
        inputName.setText(AppSession.getUserName(ctx))
        inputPhone.setText(AppSession.getUserPhone(ctx))
        inputBio.setText(AppSession.getUserBio(ctx))
        profileEmail.text = AppSession.getEmail(ctx).ifEmpty { "—" }
        profileUserId.text = "#${AppSession.getUserId(ctx)}"

        // Pre-select gender
        when (AppSession.getUserGender(ctx)) {
            "Male"             -> root.findViewById<RadioButton>(R.id.genderMale).isChecked = true
            "Female"           -> root.findViewById<RadioButton>(R.id.genderFemale).isChecked = true
            "Non-binary"       -> root.findViewById<RadioButton>(R.id.genderNonbinary).isChecked = true
            "Prefer not to say"-> root.findViewById<RadioButton>(R.id.genderOther).isChecked = true
        }

        // Load avatar from first card
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

        // Back
        root.findViewById<TextView>(R.id.backLast).setOnClickListener {
            findNavController().navigate(R.id.action_infoFragment_to_settingsFragment)
        }

        // Save
        root.findViewById<TextView>(R.id.saveButton).setOnClickListener {
            val name = inputName.text.toString().trim()
            AppSession.setUserName(ctx, name)
            AppSession.setUserPhone(ctx, inputPhone.text.toString().trim())
            AppSession.setUserBio(ctx, inputBio.text.toString().trim())

            val selectedId = genderGroup.checkedRadioButtonId
            val gender = if (selectedId != -1) root.findViewById<RadioButton>(selectedId).text.toString() else ""
            AppSession.setUserGender(ctx, gender)

            Toast.makeText(ctx, "Profile saved", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_infoFragment_to_settingsFragment)
        }

        return root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) = InfoFragment()
    }
}
