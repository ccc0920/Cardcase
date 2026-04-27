package com.team4.cardcase2.foreground

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.team4.cardcase2.AppSession
import com.team4.cardcase2.R
import com.team4.cardcase2.entity.ServerCard
import com.team4.cardcase2.entity.UserCardsResponse
import com.team4.cardcase2.entity.WholeServerCard
import com.team4.cardcase2.interfaces.HttpRequest
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class GroupFragment : Fragment(), ButtonClickListener {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var groupAdapter: GroupAdapter
    private lateinit var cardAdapter: CardAdapter
    private var cardLists: MutableList<ServerCard> = mutableListOf()
    private lateinit var cardView: RecyclerView
    private var currentGroup: String = "All Contacts"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_group, container, false)

        val userId = AppSession.getUserId(requireContext())

        val recyclerView: RecyclerView = root.findViewById(R.id.groupView)
        val groupLists = getGids(userId)
        groupAdapter = GroupAdapter(groupLists, this)
        recyclerView.adapter = groupAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val addGroupButton: Button = root.findViewById(R.id.addGroupButton)
        addGroupButton.setOnClickListener { showInputDialog(userId) }

        val scanButton: Button = root.findViewById(R.id.scanGroupButton)
        scanButton.setOnClickListener {
            childFragmentManager.beginTransaction()
                .replace(R.id.cardFragment, ScanFragment())
                .addToBackStack(null)
                .commit()
        }

        cardView = root.findViewById(R.id.cardView)
        cardAdapter = CardAdapter(mutableListOf())
        cardView.adapter = cardAdapter
        cardView.layoutManager = LinearLayoutManager(requireContext())

        loadAllContacts(userId)

        return root
    }

    private fun loadAllContacts(userId: Int) {
        val ctx = context ?: return
        val token = AppSession.getToken(ctx)
        if (token.isEmpty()) return

        HttpRequest().sendGetRequest("http://10.0.2.2:8080/api/cards/user/$userId", token) { response, exception ->
            activity?.runOnUiThread {
                if (exception == null && response != null) {
                    try {
                        val result = UserCardsResponse.fromJson(response)
                        if (result.success) {
                            cardLists = result.cards.toMutableList()
                            cardAdapter = CardAdapter(cardLists)
                            cardView.adapter = cardAdapter
                        }
                    } catch (e: Exception) {
                        // ignore parse errors
                    }
                }
            }
        }
    }

    private fun showInputDialog(userId: Int) {
        val inputEditText = EditText(requireContext())
        AlertDialog.Builder(requireContext())
            .setTitle("New Group")
            .setView(inputEditText)
            .setPositiveButton("Create") { _, _ ->
                val name = inputEditText.text.toString().trim()
                if (name.isNotEmpty()) {
                    addGid(userId, name)
                    groupAdapter.addItem(name)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun addGid(uid: Int, gid: String) {
        val db = requireContext().openOrCreateDatabase("sqlite.db", Context.MODE_PRIVATE, null)
        db.execSQL("CREATE TABLE IF NOT EXISTS SQLTable(uid INTEGER, cid INTEGER, gid TEXT)")
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM SQLTable WHERE uid = ? AND gid = ?",
            arrayOf(uid.toString(), gid)
        )
        cursor.moveToFirst()
        if (cursor.getInt(0) == 0) {
            val cv = ContentValues().apply {
                put("uid", uid); put("cid", -1); put("gid", gid)
            }
            db.insert("SQLTable", null, cv)
        }
        cursor.close()
        db.close()
    }

    private fun getGids(uid: Int): MutableList<String> {
        val db = requireContext().openOrCreateDatabase("sqlite.db", Context.MODE_PRIVATE, null)
        db.execSQL("CREATE TABLE IF NOT EXISTS SQLTable(uid INTEGER, cid INTEGER, gid TEXT)")
        val gidList = mutableListOf<String>()
        val cursor = db.rawQuery(
            "SELECT DISTINCT gid FROM SQLTable WHERE uid = ?",
            arrayOf(uid.toString())
        )
        if (cursor.moveToFirst()) {
            do { gidList.add(cursor.getString(cursor.getColumnIndexOrThrow("gid"))) }
            while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return gidList
    }

    private fun getCidList(uid: Int, gid: String): MutableList<Int> {
        val db = requireContext().openOrCreateDatabase("sqlite.db", Context.MODE_PRIVATE, null)
        val cidList = mutableListOf<Int>()
        val cursor = db.rawQuery(
            "SELECT cid FROM SQLTable WHERE uid = ? AND gid = ? AND cid != -1",
            arrayOf(uid.toString(), gid)
        )
        if (cursor.moveToFirst()) {
            do { cidList.add(cursor.getInt(cursor.getColumnIndexOrThrow("cid"))) }
            while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return cidList
    }

    override suspend fun rowClick(id: Int, text: String) {
        val ctx = context ?: return
        val userId = AppSession.getUserId(ctx)
        val token = AppSession.getToken(ctx)

        if (text == "All Contacts" || text == "ACCEED") {
            loadAllContacts(userId)
            return
        }

        val cidList = getCidList(userId, text)
        val fetchedCards = mutableListOf<ServerCard>()
        for (cid in cidList) {
            val card = fetchCard(cid, token)
            if (card != null) fetchedCards.add(card)
        }
        activity?.runOnUiThread {
            cardLists = fetchedCards
            cardAdapter = CardAdapter(fetchedCards)
            cardView.adapter = cardAdapter
        }
    }

    private suspend fun fetchCard(cid: Int, token: String): ServerCard? =
        suspendCancellableCoroutine { cont ->
            val url = "http://10.0.2.2:8080/api/cards/$cid"
            HttpRequest().sendGetRequest(url, token) { response, exception ->
                if (exception != null || response == null) {
                    cont.resume(null)
                } else {
                    try {
                        val result = WholeServerCard.fromJson(response)
                        cont.resume(result.card)
                    } catch (e: Exception) {
                        cont.resume(null)
                    }
                }
            }
        }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GroupFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
