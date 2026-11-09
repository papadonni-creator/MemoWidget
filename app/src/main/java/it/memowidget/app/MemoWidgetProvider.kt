package it.memowidget.app

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.widget.RemoteViews

class MemoWidgetProvider: AppWidgetProvider() {
 override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) { ids.forEach { id ->
  val rv=RemoteViews(context.packageName,R.layout.widget)
  val svc=Intent(context,MemoListService::class.java).putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,id).apply { data=android.net.Uri.parse("memo://list/$id") }
  rv.setRemoteAdapter(R.id.widget_list,svc)
  rv.setEmptyView(R.id.widget_list,R.id.widget_empty)
  rv.setTextColor(R.id.widget_title,Color.parseColor(Store.color(context,"fg","#FFFFFF")))
  rv.setInt(R.id.widget_root,"setBackgroundColor",Color.parseColor(Store.color(context,"widgetBg","#202630")))
  fun launch(mode: String, request: Int): PendingIntent { val intent=Intent(context,MainActivity::class.java).putExtra("mode",mode).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP); return PendingIntent.getActivity(context,request,intent,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE) }
  rv.setOnClickPendingIntent(R.id.widget_add,launch("add",id*10+1))
  rv.setOnClickPendingIntent(R.id.widget_settings,launch("settings",id*10+2))
  val template=Intent(context,MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
  rv.setPendingIntentTemplate(R.id.widget_list,PendingIntent.getActivity(context,id*10+3,template,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE))
  manager.updateAppWidget(id,rv)
 } }
 companion object { fun refresh(c: Context) { val manager=AppWidgetManager.getInstance(c); val ids=manager.getAppWidgetIds(ComponentName(c,MemoWidgetProvider::class.java)); manager.notifyAppWidgetViewDataChanged(ids,R.id.widget_list); if(ids.isNotEmpty()) MemoWidgetProvider().onUpdate(c,manager,ids) } }
}
