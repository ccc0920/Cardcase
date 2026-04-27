package com.team4.cardcase2.foreground

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.team4.cardcase2.AppSession
import com.team4.cardcase2.R
import com.team4.cardcase2.entity.*
import com.team4.cardcase2.interfaces.HttpRequest
import java.io.ByteArrayOutputStream

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class CreateNewFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var headImage: ImageView
    private lateinit var previewCard: LinearLayout
    private var selectedImageBase64: String = ""
    private var selectedColor: String = "blue"

    private val REQUEST_IMAGE_PICK = 1

    // Selection rings for each color swatch
    private lateinit var selRings: Map<String, View>
    private lateinit var swatches: Map<String, View>

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            val uri: Uri = data.data ?: return
            try {
                val stream = requireContext().contentResolver.openInputStream(uri)
                val original = BitmapFactory.decodeStream(stream)
                if (original != null) {
                    // Scale down to max 256×256 before encoding to keep payload small
                    val scaled = scaleBitmap(original, 256)
                    val out = ByteArrayOutputStream()
                    scaled.compress(Bitmap.CompressFormat.JPEG, 80, out)
                    selectedImageBase64 = Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP)
                    headImage.setImageBitmap(scaled)
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Could not load image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun scaleBitmap(src: Bitmap, maxSide: Int): Bitmap {
        val w = src.width; val h = src.height
        if (w <= maxSide && h <= maxSide) return src
        val scale = maxSide.toFloat() / maxOf(w, h)
        return Bitmap.createScaledBitmap(src, (w * scale).toInt(), (h * scale).toInt(), true)
    }

    private fun selectColor(color: String) {
        selectedColor = color
        selRings.values.forEach { it.visibility = View.INVISIBLE }
        selRings[color]?.visibility = View.VISIBLE
        val gradientRes = colorToGradient(color)
        previewCard.background = ContextCompat.getDrawable(requireContext(), gradientRes)
    }

    private fun colorToGradient(color: String) = when (color) {
        "purple" -> R.drawable.card_gradient_purple
        "teal"   -> R.drawable.card_gradient_teal
        "rose"   -> R.drawable.card_gradient_rose
        "slate"  -> R.drawable.card_gradient_slate
        else     -> R.drawable.card_gradient_blue
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_create_new, container, false)

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
        previewCard = root.findViewById(R.id.previewCard)

        // Color swatches setup
        selRings = mapOf(
            "blue"   to root.findViewById(R.id.swatchBlueSel),
            "purple" to root.findViewById(R.id.swatchPurpleSel),
            "teal"   to root.findViewById(R.id.swatchTealSel),
            "rose"   to root.findViewById(R.id.swatchRoseSel),
            "slate"  to root.findViewById(R.id.swatchSlateSel)
        )
        swatches = mapOf(
            "blue"   to root.findViewById(R.id.swatchBlue),
            "purple" to root.findViewById(R.id.swatchPurple),
            "teal"   to root.findViewById(R.id.swatchTeal),
            "rose"   to root.findViewById(R.id.swatchRose),
            "slate"  to root.findViewById(R.id.swatchSlate)
        )
        swatches.forEach { (color, view) ->
            view.setOnClickListener { selectColor(color) }
            // Also tap the ring itself
            selRings[color]?.setOnClickListener { selectColor(color) }
        }

        if (isEditMode) {
            headerTitle.text = "Edit Card"
            saveButton.text = "Update"
        }

        headImage.setOnClickListener { openGallery() }

        // Pre-fill in edit mode
        if (isEditMode) {
            val ctx = requireContext()
            val token = AppSession.getToken(ctx)
            HttpRequest().sendGetRequest("http://10.0.2.2:8080/api/cards/$editCardId", token) { response, _ ->
                activity?.runOnUiThread {
                    if (response == null) return@runOnUiThread
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
                        // Restore color
                        if (card.design.color.isNotEmpty()) selectColor(card.design.color)
                        // Restore avatar
                        if (card.avatar.isNotEmpty()) {
                            selectedImageBase64 = card.avatar
                            try {
                                val bytes = Base64.decode(card.avatar, Base64.DEFAULT)
                                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                if (bmp != null) headImage.setImageBitmap(bmp)
                            } catch (e: Exception) { /* keep default */ }
                        }
                    } catch (e: Exception) { /* ignore */ }
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

            val name    = Elements("name",    showName.text.toString(),    Position(50, 10), Style("Arial", 20, "#FFFFFF", true,  false, false))
            val title   = Elements("title",   inputTitle.text.toString(),  Position(50, 30), Style("Arial", 14, "#C7D2FE", false, false, false))
            val company = Elements("company", inputCompany.text.toString(),Position(50, 50), Style("Arial", 14, "#C7D2FE", false, false, false))
            val email   = Elements("email",   showEmail.text.toString(),   Position(50, 70), Style("Arial", 12, "#FFFFFF", false, false, false))
            val phone   = Elements("phone",   showPhone.text.toString(),   Position(50, 90), Style("Arial", 12, "#FFFFFF", false, false, false))

            val design = Design(selectedColor, selectedColor, "Arial", "default")
            val serverCard = ServerCard(
                if (isEditMode) editCardId else 0, false, userId,
                listOf(name, title, company, email, phone),
                selectedImageBase64, "", design
            )
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

        backButton.setOnClickListener { findNavController().navigate(R.id.blankFragment) }

        // Live preview watchers
        fun updateName() {
            showName.text = "${inputFirstName.text} ${inputSecondName.text}".trim().ifEmpty { "Your Name" }
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
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { showTitle.text = s }
            override fun afterTextChanged(s: Editable?) {}
        })
        inputCompany.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                showCompany.text = s.toString().uppercase()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        inputPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { showPhone.text = s }
            override fun afterTextChanged(s: Editable?) {}
        })
        inputEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { showEmail.text = s }
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
