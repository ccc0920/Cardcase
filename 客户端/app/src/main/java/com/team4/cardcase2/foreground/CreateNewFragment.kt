package com.team4.cardcase2.foreground

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.team4.cardcase2.AppSession
import com.team4.cardcase2.R
import com.team4.cardcase2.entity.*
import com.team4.cardcase2.interfaces.HttpRequest

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class CreateNewFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var headImage: ImageView
    private var selectedImage: Uri? = null
    private val REQUEST_IMAGE_PICK = 1
    private val REQUEST_PERMISSION = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            selectedImage = data.data
            headImage.setImageURI(selectedImage)
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_create_new, container, false)

        // Determine if we're editing an existing card
        val editCardId = arguments?.getInt("cardId", 0) ?: 0
        val isEditMode = editCardId > 0

        val headerTitle: TextView = root.findViewById(R.id.headerTitle)
        val saveButton: Button = root.findViewById(R.id.saveButton)
        val inputFirstName: EditText = root.findViewById(R.id.inputFirstName)
        val inputSecondName: EditText = root.findViewById(R.id.inputSecondName)
        val showName: TextView = root.findViewById(R.id.showName)
        val showTitle: TextView = root.findViewById(R.id.showTitle)
        val inputCompany: EditText = root.findViewById(R.id.inputCompany)
        val showCompany: TextView = root.findViewById(R.id.showCompany)
        val showPhone: TextView = root.findViewById(R.id.showPhone)
        val showEmail: TextView = root.findViewById(R.id.showEmail)
        val inputPhone: EditText = root.findViewById(R.id.inputPhone)
        val inputEmail: EditText = root.findViewById(R.id.inputEmail)
        val backButton: TextView = root.findViewById(R.id.backButton)
        val inputTitle: EditText = root.findViewById(R.id.inputTitle)
        headImage = root.findViewById(R.id.imageView)

        if (isEditMode) {
            headerTitle.text = "Edit Card"
            saveButton.text = "Update"
        }

        headImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_PERMISSION
                )
            } else {
                openGallery()
            }
        }

        // If editing, load existing card data and pre-fill
        if (isEditMode) {
            val ctx = requireContext()
            val token = AppSession.getToken(ctx)
            HttpRequest().sendGetRequest("http://10.0.2.2:8080/api/cards/$editCardId", token) { response, exception ->
                activity?.runOnUiThread {
                    if (exception != null || response == null) return@runOnUiThread
                    try {
                        val card = WholeServerCard.fromJson(response).card
                        val name = card.elements.firstOrNull { it.type == "name" }?.content ?: ""
                        val parts = name.trim().split(" ", limit = 2)
                        inputFirstName.setText(parts.getOrElse(0) { "" })
                        inputSecondName.setText(parts.getOrElse(1) { "" })
                        inputTitle.setText(card.elements.firstOrNull { it.type == "title" }?.content ?: "")
                        inputCompany.setText(card.elements.firstOrNull { it.type == "company" }?.content ?: "")
                        inputPhone.setText(card.elements.firstOrNull { it.type == "phone" }?.content ?: "")
                        inputEmail.setText(card.elements.firstOrNull { it.type == "email" }?.content ?: "")
                    } catch (e: Exception) {
                        // ignore
                    }
                }
            }
        }

        saveButton.setOnClickListener {
            val ctx = requireContext()
            val userId = AppSession.getUserId(ctx)
            val token = AppSession.getToken(ctx)

            if (userId == 0 || token.isEmpty()) {
                Toast.makeText(ctx, "Not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val name = Elements("name", showName.text.toString(), Position(50, 10),
                Style("Arial", 20, "#FFFFFF", true, false, false))
            val title = Elements("title", inputTitle.text.toString(), Position(50, 30),
                Style("Arial", 14, "#C7D2FE", false, false, false))
            val company = Elements("company", inputCompany.text.toString(), Position(50, 50),
                Style("Arial", 14, "#C7D2FE", false, false, false))
            val email = Elements("email", showEmail.text.toString(), Position(50, 70),
                Style("Arial", 12, "#FFFFFF", false, false, false))
            val phone = Elements("phone", showPhone.text.toString(), Position(50, 90),
                Style("Arial", 12, "#FFFFFF", false, false, false))
            val elements = mutableListOf(name, title, company, email, phone)

            val design = Design("", "", "", "")
            val serverCard = ServerCard(if (isEditMode) editCardId else 0, false, userId, elements, "", "", design)
            val jsonBody = serverCard.toJson()

            val url = if (isEditMode) "http://10.0.2.2:8080/api/cards/$editCardId"
                      else "http://10.0.2.2:8080/api/create-card"

            saveButton.isEnabled = false
            val http = HttpRequest()
            val doRequest: (String, String, String, (String?, Exception?) -> Unit) -> Unit =
                if (isEditMode) http::sendPutRequest else http::sendPostRequest

            doRequest(url, token, jsonBody) { response, exception ->
                activity?.runOnUiThread {
                    saveButton.isEnabled = true
                    if (exception != null) {
                        Toast.makeText(ctx, "Save failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(ctx, if (isEditMode) "Card updated!" else "Card saved!", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.blankFragment)
                    }
                }
            }
        }

        backButton.setOnClickListener {
            findNavController().navigate(R.id.blankFragment)
        }

        fun updateName() {
            val firstName = inputFirstName.text.toString()
            val secondName = inputSecondName.text.toString()
            showName.text = "$firstName $secondName".trim().ifEmpty { "Your Name" }
        }

        val nameWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { updateName() }
            override fun afterTextChanged(s: Editable?) {}
        }
        inputFirstName.addTextChangedListener(nameWatcher)
        inputSecondName.addTextChangedListener(nameWatcher)

        inputTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                showTitle.text = inputTitle.text.toString()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        inputCompany.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                showCompany.text = inputCompany.text.toString().uppercase()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        inputPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                showPhone.text = inputPhone.text.toString()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        inputEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                showEmail.text = inputEmail.text.toString()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        return root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreateNewFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
