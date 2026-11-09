package it.memowidget.app

import android.app.*
import android.os.Bundle
import android.content.Intent
import android.graphics.Color
import android.view.ViewGroup
import android.widget.*
import java.time.LocalDate
import java.util.Calendar

class MainActivity: Activity() {
 private lateinit var root: LinearLayout
 override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); showHome(); handle(intent) }
 override fun onNewIntent(intent: Intent) { super.onNewIntent(intent); setIntent(intent); handle(intent) }
 private fun handle(i: Intent) { when(i.getStringExtra("mode")) { "add" -> edit(null); "settings" -> settings(); "edit" -> Store.all(this).find { it.id==i.getLongExtra("memo_id",-1) }?.let { actions(it) } }; i.removeExtra("mode") }
 private fun dp(v: Int)=(v*resources.displayMetrics.density).toInt()
 private fun column(): LinearLayout = LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(dp(18),dp(20),dp(18),dp(18)) }
 private fun showHome() {
  root=column(); root.setBackgroundColor(Color.parseColor(Store.color(this,"widgetBg","#202630"))); setContentView(ScrollView(this).apply { addView(root) })
  val header=TextView(this).apply { text="MemoWidget     +"; textSize=24f; setTextColor(Color.WHITE); setPadding(0,0,0,dp(15)); setOnClickListener { edit(null) } }; root.addView(header)
  val setting=Button(this).apply { text="Impostazioni"; setOnClickListener { settings() } }; root.addView(setting)
  val hint=TextView(this).apply { text="Tocca un appuntamento per modificarlo o eliminarlo. Gli appuntamenti senza data sono in cima."; setTextColor(Color.WHITE); setPadding(0,dp(12),0,dp(12)) }; root.addView(hint)
  Store.all(this).forEach { m ->
   val t=TextView(this@MainActivity).apply { text=(m.date?.let { LocalDate.parse(it).toString()+"  " } ?: "SENZA DATA  ")+m.text; textSize=Store.size(this).toFloat(); setTextColor(Color.parseColor(m.fg ?: Store.color(this@MainActivity,"fg","#FFFFFF"))); setBackgroundColor(Color.parseColor(m.bg ?: Store.color(this@MainActivity,"rowBg","#344E69"))); setPadding(dp(12),dp(15),dp(12),dp(15)); setOnClickListener { actions(m) }; setOnLongClickListener { actions(m); true } }
   root.addView(t,LinearLayout.LayoutParams(-1,-2).apply { bottomMargin=dp(6) })
  }
 }
 private fun actions(m: Memo) { AlertDialog.Builder(this).setTitle("Appuntamento").setItems(arrayOf("Modifica","Elimina")) { _,which -> if(which==0) edit(m) else AlertDialog.Builder(this).setTitle("Conferma eliminazione").setMessage("Eliminare questo appuntamento?").setNegativeButton("Annulla",null).setPositiveButton("Elimina") { _,_ -> Store.save(this,Store.all(this).filter { it.id!=m.id }); showHome() }.show() }.setNegativeButton("Annulla",null).show() }
 private fun edit(old: Memo?) {
  val layout=column(); val hasDate=CheckBox(this).apply { text="Inserisci una data"; isChecked=old?.date!=null }; layout.addView(hasDate)
  var date=old?.date
  val pick=Button(this).apply { text=date ?: "Scegli data" }; layout.addView(pick)
  pick.setOnClickListener { val cal=Calendar.getInstance(); date?.let { val d=LocalDate.parse(it); cal.set(d.year,d.monthValue-1,d.dayOfMonth) }; DatePickerDialog(this,{ _,y,m,d -> date="%04d-%02d-%02d".format(y,m+1,d); pick.text=date },cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show() }
  fun visibility() { pick.visibility=if(hasDate.isChecked) android.view.View.VISIBLE else android.view.View.GONE }; hasDate.setOnCheckedChangeListener { _,_ -> visibility() }; visibility()
  val memoInput=EditText(this).apply { hint="Testo dell'appuntamento"; setText(old?.text ?: ""); minLines=2; maxLines=5 }; layout.addView(memoInput)
  val colors=arrayOf("Predefinito","Blu","Verde","Viola","Giallo","Bianco","Nero")
  val codes=arrayOf("","#344E69","#65734D","#66516E","#FFE49B","#FFFFFF","#171717")
  val bg=Spinner(this).apply { adapter=ArrayAdapter(this@MainActivity,android.R.layout.simple_spinner_dropdown_item,colors); setSelection(codes.indexOf(old?.bg ?: "").coerceAtLeast(0)) }; layout.addView(TextView(this).apply { text="Sfondo di questo appuntamento" }); layout.addView(bg)
  val fg=Spinner(this).apply { adapter=ArrayAdapter(this@MainActivity,android.R.layout.simple_spinner_dropdown_item,colors); setSelection(codes.indexOf(old?.fg ?: "").coerceAtLeast(0)) }; layout.addView(TextView(this).apply { text="Testo di questo appuntamento" }); layout.addView(fg)
  AlertDialog.Builder(this).setTitle(if(old==null) "Nuovo appuntamento" else "Modifica appuntamento").setView(ScrollView(this).apply { addView(layout) }).setNegativeButton("Annulla",null).setPositiveButton("Salva") { _,_ ->
   val value=memoInput.text.toString().trim(); if(value.isNotEmpty()) { val items=Store.all(this).filter { it.id!=old?.id }.toMutableList(); items.add(Memo(old?.id ?: System.currentTimeMillis(),value,if(hasDate.isChecked) date ?: LocalDate.now().toString() else null,codes[bg.selectedItemPosition].ifBlank { null },codes[fg.selectedItemPosition].ifBlank { null })); Store.save(this,items); showHome() } else Toast.makeText(this,"Inserisci il testo",Toast.LENGTH_SHORT).show()
  }.show()
 }
 private fun settings() {
  val layout=column()
  val keys=listOf("widgetBg" to "Sfondo widget", "rowBg" to "Sfondo appuntamenti", "fg" to "Colore testo")
  val fields=keys.map { (key,label) -> val e=EditText(this).apply { setSingleLine(); setText(Store.color(this@MainActivity,key,when(key) { "widgetBg"->"#202630";"rowBg"->"#344E69";else->"#FFFFFF" })) }; layout.addView(TextView(this).apply { text=label+" (es. #202630)" }); layout.addView(e); key to e }
  val size=EditText(this).apply { inputType=android.text.InputType.TYPE_CLASS_NUMBER; setText(Store.size(this@MainActivity).toString()) }; layout.addView(TextView(this).apply { text="Dimensione caratteri (sp)" }); layout.addView(size)
  AlertDialog.Builder(this).setTitle("Impostazioni").setView(layout).setNegativeButton("Annulla",null).setPositiveButton("Salva") { _,_ ->
   try { val editor=Store.pref(this).edit(); fields.forEach { (key,e) -> val value=e.text.toString(); Color.parseColor(value); editor.putString(key,value) }; editor.putInt("size",(size.text.toString().toIntOrNull() ?: 14).coerceIn(10,30)); editor.apply(); MemoWidgetProvider.refresh(this); showHome() } catch(e: Exception) { Toast.makeText(this,"Colore non valido: usa #RRGGBB",Toast.LENGTH_LONG).show() }
  }.show()
 }
}
