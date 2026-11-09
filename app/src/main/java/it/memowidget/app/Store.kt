package it.memowidget.app

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class Memo(val id: Long, val text: String, val date: String?, val bg: String? = null, val fg: String? = null)
object Store {
 private const val PREF = "memo_prefs"
 fun pref(c: Context) = c.getSharedPreferences(PREF, Context.MODE_PRIVATE)
 fun color(c: Context, key: String, fallback: String) = pref(c).getString(key, fallback) ?: fallback
 fun size(c: Context) = pref(c).getInt("size",14)
 fun all(c: Context): List<Memo> {
  val arr = JSONArray(pref(c).getString("memos","[]"))
  return (0 until arr.length()).map { val x=arr.getJSONObject(it); Memo(x.getLong("id"),x.getString("text"),x.optString("date").ifBlank { null },x.optString("bg").ifBlank { null },x.optString("fg").ifBlank { null }) }
   .sortedWith(compareBy<Memo> { it.date != null }.thenByDescending { it.date ?: "" }.thenByDescending { it.id })
 }
 fun save(c: Context, items: List<Memo>) {
  val arr=JSONArray(); items.forEach { m -> arr.put(JSONObject().put("id",m.id).put("text",m.text).put("date",m.date ?: "").put("bg",m.bg ?: "").put("fg",m.fg ?: "")) }
  pref(c).edit().putString("memos",arr.toString()).apply(); MemoWidgetProvider.refresh(c)
 }
}
