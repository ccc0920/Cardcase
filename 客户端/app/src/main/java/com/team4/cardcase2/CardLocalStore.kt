package com.team4.cardcase2

import android.content.ContentValues
import android.content.Context
import com.team4.cardcase2.entity.*

/**
 * Local SQLite store for the current user's own cards.
 * Cards are written here immediately on creation/edit so they appear
 * in the Wallet even when the server is unreachable.
 */
object CardLocalStore {

    private const val TABLE = "my_cards"
    private const val CONTACTS_TABLE = "scanned_contacts"

    private fun db(ctx: Context) =
        ctx.openOrCreateDatabase("sqlite.db", Context.MODE_PRIVATE, null).also { db ->
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS $TABLE (
                    id        INTEGER PRIMARY KEY AUTOINCREMENT,
                    server_id INTEGER DEFAULT 0,
                    uid       INTEGER NOT NULL,
                    name      TEXT DEFAULT '',
                    title     TEXT DEFAULT '',
                    company   TEXT DEFAULT '',
                    phone     TEXT DEFAULT '',
                    email     TEXT DEFAULT '',
                    avatar    TEXT DEFAULT '',
                    color     TEXT DEFAULT 'blue'
                )
            """.trimIndent())
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS $CONTACTS_TABLE (
                    id         INTEGER PRIMARY KEY AUTOINCREMENT,
                    server_id  INTEGER NOT NULL,
                    scanned_by INTEGER NOT NULL,
                    name       TEXT DEFAULT '',
                    title      TEXT DEFAULT '',
                    company    TEXT DEFAULT '',
                    phone      TEXT DEFAULT '',
                    email      TEXT DEFAULT '',
                    avatar     TEXT DEFAULT '',
                    color      TEXT DEFAULT 'blue',
                    UNIQUE(server_id, scanned_by)
                )
            """.trimIndent())
        }

    /** Insert a new card locally. Returns the local row id. */
    fun insert(
        ctx: Context, uid: Int,
        name: String, title: String, company: String,
        phone: String, email: String, avatar: String, color: String,
        serverId: Int = 0
    ): Int {
        val db = db(ctx)
        val id = db.insert(TABLE, null, ContentValues().apply {
            put("server_id", serverId)
            put("uid",       uid)
            put("name",      name)
            put("title",     title)
            put("company",   company)
            put("phone",     phone)
            put("email",     email)
            put("avatar",    avatar)
            put("color",     color)
        }).toInt()
        db.close()
        return id
    }

    /**
     * Update a card using the displayId (which is server_id when synced, or local row id
     * when offline). Tries server_id match first; falls back to local row id.
     */
    fun updateByDisplayId(
        ctx: Context, displayId: Int,
        name: String, title: String, company: String,
        phone: String, email: String, avatar: String, color: String
    ) {
        val db = db(ctx)
        val cv = ContentValues().apply {
            put("name",    name);    put("title",   title)
            put("company", company); put("phone",   phone)
            put("email",   email);   put("avatar",  avatar)
            put("color",   color)
        }
        val updated = db.update(TABLE, cv,
            "server_id = ? AND server_id != 0", arrayOf(displayId.toString()))
        if (updated == 0) {
            db.update(TABLE, cv, "id = ?", arrayOf(displayId.toString()))
        }
        db.close()
    }

    /** Update an existing local card matched by its local row id. */
    fun updateByLocalId(
        ctx: Context, localId: Int,
        name: String, title: String, company: String,
        phone: String, email: String, avatar: String, color: String
    ) {
        val db = db(ctx)
        db.update(TABLE, ContentValues().apply {
            put("name",    name);    put("title",   title)
            put("company", company); put("phone",   phone)
            put("email",   email);   put("avatar",  avatar)
            put("color",   color)
        }, "id = ?", arrayOf(localId.toString()))
        db.close()
    }

    /** Delete a card by displayId (server_id first, then local id). */
    fun delete(ctx: Context, displayId: Int) {
        val db = db(ctx)
        val deleted = db.delete(TABLE, "server_id = ? AND server_id != 0", arrayOf(displayId.toString()))
        if (deleted == 0) db.delete(TABLE, "id = ?", arrayOf(displayId.toString()))
        db.close()
    }

    /** Save (or refresh) a card scanned from another user. */
    fun upsertContact(ctx: Context, scannedBy: Int, card: ServerCard) {
        val db = db(ctx)
        val cv = ContentValues().apply {
            put("server_id",  card.cardId)
            put("scanned_by", scannedBy)
            put("name",    card.elements.byType("name"))
            put("title",   card.elements.byType("title"))
            put("company", card.elements.byType("company"))
            put("phone",   card.elements.byType("phone"))
            put("email",   card.elements.byType("email"))
            put("avatar",  card.avatar)
            put("color",   card.design.color.ifEmpty { "blue" })
        }
        val cur = db.rawQuery(
            "SELECT id FROM $CONTACTS_TABLE WHERE server_id = ? AND scanned_by = ?",
            arrayOf(card.cardId.toString(), scannedBy.toString())
        )
        val exists = cur.moveToFirst(); cur.close()
        if (exists) {
            db.update(CONTACTS_TABLE, cv, "server_id = ? AND scanned_by = ?",
                arrayOf(card.cardId.toString(), scannedBy.toString()))
        } else {
            db.insert(CONTACTS_TABLE, null, cv)
        }
        db.close()
    }

    /** Load all cards scanned from other users. */
    fun loadContacts(ctx: Context, scannedBy: Int): List<ServerCard> {
        val db = db(ctx)
        val cur = db.rawQuery(
            "SELECT * FROM $CONTACTS_TABLE WHERE scanned_by = ? ORDER BY id DESC",
            arrayOf(scannedBy.toString())
        )
        val cards = mutableListOf<ServerCard>()
        val ds = Style("Arial", 14, "#FFFFFF", false, false, false)
        if (cur.moveToFirst()) {
            do {
                val serverId = cur.getInt(cur.getColumnIndexOrThrow("server_id"))
                val name    = cur.getString(cur.getColumnIndexOrThrow("name"))    ?: ""
                val title   = cur.getString(cur.getColumnIndexOrThrow("title"))   ?: ""
                val company = cur.getString(cur.getColumnIndexOrThrow("company")) ?: ""
                val phone   = cur.getString(cur.getColumnIndexOrThrow("phone"))   ?: ""
                val email   = cur.getString(cur.getColumnIndexOrThrow("email"))   ?: ""
                val avatar  = cur.getString(cur.getColumnIndexOrThrow("avatar"))  ?: ""
                val color   = cur.getString(cur.getColumnIndexOrThrow("color"))   ?: "blue"
                cards.add(ServerCard(
                    cardId = serverId, success = true, userId = scannedBy,
                    elements = listOf(
                        Elements("name",    name,    Position(50, 10), Style("Arial", 20, "#FFFFFF", true, false, false)),
                        Elements("title",   title,   Position(50, 30), ds),
                        Elements("company", company, Position(50, 50), ds),
                        Elements("email",   email,   Position(50, 70), ds),
                        Elements("phone",   phone,   Position(50, 90), ds)
                    ),
                    avatar = avatar, background = "",
                    design = Design(color, color, "Arial", "default")
                ))
            } while (cur.moveToNext())
        }
        cur.close()
        db.close()
        return cards
    }

    /** Set the server-assigned id after a successful POST. */
    fun setServerId(ctx: Context, localId: Int, serverId: Int) {
        val db = db(ctx)
        db.update(TABLE, ContentValues().apply { put("server_id", serverId) },
            "id = ?", arrayOf(localId.toString()))
        db.close()
    }

    /** Upsert cards coming from a successful server GET. */
    fun syncFromServer(ctx: Context, uid: Int, serverCards: List<ServerCard>) {
        val db = db(ctx)
        for (sc in serverCards) {
            val cur = db.rawQuery(
                "SELECT id FROM $TABLE WHERE uid = ? AND server_id = ?",
                arrayOf(uid.toString(), sc.cardId.toString())
            )
            val exists = cur.moveToFirst()
            cur.close()

            val cv = ContentValues().apply {
                put("server_id", sc.cardId)
                put("uid",       uid)
                put("name",      sc.elements.byType("name"))
                put("title",     sc.elements.byType("title"))
                put("company",   sc.elements.byType("company"))
                put("phone",     sc.elements.byType("phone"))
                put("email",     sc.elements.byType("email"))
                put("avatar",    sc.avatar)
                put("color",     sc.design.color.ifEmpty { "blue" })
            }
            if (exists) {
                db.update(TABLE, cv, "uid = ? AND server_id = ?",
                    arrayOf(uid.toString(), sc.cardId.toString()))
            } else {
                db.insert(TABLE, null, cv)
            }
        }
        db.close()
    }

    /** Find a single card by its displayId across ALL users (for QR scan lookup). */
    fun findByDisplayId(ctx: Context, displayId: Int): ServerCard? {
        val db = db(ctx)
        // Try server_id match first, then local id
        val cur = db.rawQuery(
            "SELECT * FROM $TABLE WHERE server_id = ? AND server_id != 0 LIMIT 1",
            arrayOf(displayId.toString())
        )
        val fromServer = if (cur.moveToFirst()) cur else null
        val cur2 = if (fromServer == null) {
            cur.close()
            db.rawQuery("SELECT * FROM $TABLE WHERE id = ? LIMIT 1", arrayOf(displayId.toString()))
        } else null

        val active = fromServer ?: cur2
        var result: ServerCard? = null
        if (active != null && active.moveToFirst()) {
            val localId  = active.getInt(active.getColumnIndexOrThrow("id"))
            val serverId = active.getInt(active.getColumnIndexOrThrow("server_id"))
            val uid      = active.getInt(active.getColumnIndexOrThrow("uid"))
            val name    = active.getString(active.getColumnIndexOrThrow("name"))    ?: ""
            val title   = active.getString(active.getColumnIndexOrThrow("title"))   ?: ""
            val company = active.getString(active.getColumnIndexOrThrow("company")) ?: ""
            val phone   = active.getString(active.getColumnIndexOrThrow("phone"))   ?: ""
            val email   = active.getString(active.getColumnIndexOrThrow("email"))   ?: ""
            val avatar  = active.getString(active.getColumnIndexOrThrow("avatar"))  ?: ""
            val color   = active.getString(active.getColumnIndexOrThrow("color"))   ?: "blue"
            val ds = Style("Arial", 14, "#FFFFFF", false, false, false)
            result = ServerCard(
                cardId = if (serverId > 0) serverId else localId, success = true, userId = uid,
                elements = listOf(
                    Elements("name",    name,    Position(50, 10), Style("Arial", 20, "#FFFFFF", true, false, false)),
                    Elements("title",   title,   Position(50, 30), ds),
                    Elements("company", company, Position(50, 50), ds),
                    Elements("email",   email,   Position(50, 70), ds),
                    Elements("phone",   phone,   Position(50, 90), ds)
                ),
                avatar = avatar, background = "",
                design = Design(color, color, "Arial", "default")
            )
        }
        active?.close()
        db.close()
        return result
    }

    /** Load all cards for a user as ServerCard objects ready for the adapter. */
    fun loadAll(ctx: Context, uid: Int): List<ServerCard> {
        val db = db(ctx)
        val cur = db.rawQuery(
            "SELECT * FROM $TABLE WHERE uid = ? ORDER BY id DESC",
            arrayOf(uid.toString())
        )
        val cards = mutableListOf<ServerCard>()
        if (cur.moveToFirst()) {
            do {
                val localId  = cur.getInt(cur.getColumnIndexOrThrow("id"))
                val serverId = cur.getInt(cur.getColumnIndexOrThrow("server_id"))
                val name     = cur.getString(cur.getColumnIndexOrThrow("name"))    ?: ""
                val title    = cur.getString(cur.getColumnIndexOrThrow("title"))   ?: ""
                val company  = cur.getString(cur.getColumnIndexOrThrow("company")) ?: ""
                val phone    = cur.getString(cur.getColumnIndexOrThrow("phone"))   ?: ""
                val email    = cur.getString(cur.getColumnIndexOrThrow("email"))   ?: ""
                val avatar   = cur.getString(cur.getColumnIndexOrThrow("avatar"))  ?: ""
                val color    = cur.getString(cur.getColumnIndexOrThrow("color"))   ?: "blue"

                // Use server id as the card id if synced; fall back to local row id.
                val displayId = if (serverId > 0) serverId else localId

                val ds = Style("Arial", 14, "#FFFFFF", false, false, false)
                cards.add(ServerCard(
                    cardId = displayId, success = true, userId = uid,
                    elements = listOf(
                        Elements("name",    name,    Position(50, 10), Style("Arial", 20, "#FFFFFF", true, false, false)),
                        Elements("title",   title,   Position(50, 30), ds),
                        Elements("company", company, Position(50, 50), ds),
                        Elements("email",   email,   Position(50, 70), ds),
                        Elements("phone",   phone,   Position(50, 90), ds)
                    ),
                    avatar = avatar, background = "",
                    design = Design(color, color, "Arial", "default")
                ))
            } while (cur.moveToNext())
        }
        cur.close()
        db.close()
        return cards
    }

    private fun List<Elements>.byType(t: String) =
        firstOrNull { it.type == t }?.content ?: ""
}
