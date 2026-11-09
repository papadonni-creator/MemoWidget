package it.memowidget.app

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.graphics.Color
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class MemoListService: RemoteViewsService() {
 override fun onGetViewFactory(intent: Intent): RemoteViewsFactory = Factory()
 inner class Factory: RemoteViewsFactory {
  private var memos=emptyList<Memo>()
  override fun onCreate() { onDataSetChanged() }
  override fun onDataSetChanged() { memos=Store.all(this@MemoListService) }
  override fun onDestroy() {}
  override fun getCount()=memos.size
  override fun getViewAt(position: Int): RemoteViews? {
   val m=memos.getOrNull(position) ?: return null
   val rv=RemoteViews(packageName,R.layout.widget_row)
   val date=m.date?.let { LocalDate.parse(it) }
   val locale=Locale.ITALIAN
   rv.setTextViewText(R.id.row_date,date?.format(DateTimeFormatter.ofPattern("dd MMM",locale))?.uppercase(locale) ?: "•")
   rv.setTextViewText(R.id.row_weekday,date?.format(DateTimeFormatter.ofPattern("EEE",locale))?.uppercase(locale) ?: "")
   rv.setTextViewText(R.id.row_text,m.text)
   val fg=Color.parseColor(m.fg ?: Store.color(this@MemoListService,"fg","#FFFFFF"))
   val bg=Color.parseColor(m.bg ?: Store.color(this@MemoListService,"rowBg","#344E69"))
   listOf(R.id.row_date,R.id.row_weekday,R.id.row_text).forEach { rv.setTextColor(it,fg) }
   rv.setTextViewTextSize(R.id.row_text,android.util.TypedValue.COMPLEX_UNIT_SP,Store.size(this@MemoListService).toFloat())
   rv.setInt(R.id.row_root,"setBackgroundColor",bg)
   rv.setOnClickFillInIntent(R.id.row_root,Intent().putExtra("mode","edit").putExtra("memo_id",m.id))
   return rv
  }
  override fun getLoadingView(): RemoteViews? = null
  override fun getViewTypeCount()=1
  override fun getItemId(position: Int)=memos[position].id
  override fun hasStableIds()=true
 }
}
