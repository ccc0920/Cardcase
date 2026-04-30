package com.team4.cardcase2.foreground

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.team4.cardcase2.AppSession
import com.team4.cardcase2.CardLocalStore
import com.team4.cardcase2.R
import com.team4.cardcase2.entity.Encoder
import java.io.InputStream

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val PICK_IMAGE_REQUEST = 1

class ScanFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_scan, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showScanOptionsDialog()
    }

    private fun showScanOptionsDialog() {
        val options = arrayOf("使用相机扫描", "从相册选择二维码图片")
        AlertDialog.Builder(requireContext())
            .setTitle("选择扫描方式")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> startCameraScan()
                    1 -> selectImageFromGallery()
                }
            }
            .setNegativeButton("取消") { _, _ ->
                parentFragmentManager.popBackStack()
            }
            .show()
    }

    private fun startCameraScan() {
        val options = com.journeyapps.barcodescanner.ScanOptions().apply {
            setDesiredBarcodeFormats(com.journeyapps.barcodescanner.ScanOptions.QR_CODE)
            setPrompt("扫描二维码")
            setCameraId(0)
            setBeepEnabled(false)
            setBarcodeImageEnabled(true)
        }
        scanLauncher.launch(options)
    }

    private val scanLauncher = registerForActivityResult(
        com.journeyapps.barcodescanner.ScanContract()
    ) { result ->
        if (result.contents != null) {
            processQRResult(result.contents)
        } else {
            Toast.makeText(context, "扫描取消", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }
    }

    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val uri: Uri? = data.data
            if (uri != null) {
                val stream: InputStream? = context?.contentResolver?.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(stream)
                if (bitmap != null) scanQRCodeFromBitmap(bitmap)
            } else {
                parentFragmentManager.popBackStack()
            }
        } else {
            parentFragmentManager.popBackStack()
        }
    }

    private fun scanQRCodeFromBitmap(bitmap: Bitmap) {
        val intArray = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(intArray, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        val source = RGBLuminanceSource(bitmap.width, bitmap.height, intArray)
        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
        try {
            val result = MultiFormatReader().decode(binaryBitmap)
            processQRResult(result.text)
        } catch (e: Exception) {
            Toast.makeText(context, "No QR code found in image", Toast.LENGTH_LONG).show()
            parentFragmentManager.popBackStack()
        }
    }

    private fun processQRResult(encoded: String) {
        val ctx = context ?: return
        val userId = AppSession.getUserId(ctx)

        val cardId = try { Encoder().decode(encoded) } catch (e: Exception) { -1 }
        if (cardId <= 0) {
            Toast.makeText(ctx, "Invalid QR code", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        // Look up card in local DB (searches all users, both own cards and contacts)
        val card = CardLocalStore.findByDisplayId(ctx, cardId)
        if (card == null) {
            Toast.makeText(ctx, "Card not found (ID: $cardId)", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        CardLocalStore.upsertContact(ctx, userId, card)
        showGroupDialog(userId, cardId)
    }

    private fun showGroupDialog(userId: Int, cardId: Int) {
        val ctx = context ?: return
        val groups = getGids(userId).toTypedArray()
        if (groups.isEmpty()) {
            Toast.makeText(ctx, "Create a group first", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        var selectedIdx = 0
        AlertDialog.Builder(ctx)
            .setTitle("Add to group")
            .setSingleChoiceItems(groups, selectedIdx) { _, which -> selectedIdx = which }
            .setPositiveButton("Add") { _, _ ->
                addToGroup(userId, cardId, groups[selectedIdx])
                Toast.makeText(ctx, "Added to ${groups[selectedIdx]}", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
            .setNegativeButton("Cancel") { _, _ -> parentFragmentManager.popBackStack() }
            .show()
    }

    private fun getGids(uid: Int): List<String> {
        val db = requireContext().openOrCreateDatabase("sqlite.db", Context.MODE_PRIVATE, null)
        db.execSQL("CREATE TABLE IF NOT EXISTS SQLTable(uid INTEGER, cid INTEGER, gid TEXT)")
        val list = mutableListOf<String>()
        val cursor = db.rawQuery(
            "SELECT DISTINCT gid FROM SQLTable WHERE uid = ?",
            arrayOf(uid.toString())
        )
        if (cursor.moveToFirst()) {
            do { list.add(cursor.getString(cursor.getColumnIndexOrThrow("gid"))) }
            while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }

    private fun addToGroup(uid: Int, cid: Int, gid: String) {
        val db = requireContext().openOrCreateDatabase("sqlite.db", Context.MODE_PRIVATE, null)
        db.execSQL("CREATE TABLE IF NOT EXISTS SQLTable(uid INTEGER, cid INTEGER, gid TEXT)")
        val cursor = db.rawQuery(
            "SELECT * FROM SQLTable WHERE uid = ? AND cid = ?",
            arrayOf(uid.toString(), cid.toString())
        )
        if (cursor.moveToFirst()) {
            db.execSQL("UPDATE SQLTable SET gid = ? WHERE uid = ? AND cid = ?", arrayOf(gid, uid, cid))
        } else {
            val cv = ContentValues().apply {
                put("uid", uid); put("cid", cid); put("gid", gid)
            }
            db.insert("SQLTable", null, cv)
        }
        cursor.close()
        db.close()
    }

    interface QRCodeScanResultListener {
        fun onQRCodeScanned(result: String)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ScanFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
