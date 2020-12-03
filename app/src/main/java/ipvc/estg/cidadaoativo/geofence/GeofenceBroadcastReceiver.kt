package ipvc.estg.cidadaoativo.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
import ipvc.estg.cidadaoativo.MapActivity
import ipvc.estg.cidadaoativo.R
import ipvc.estg.cidadaoativo.notifications.NotificationHelper

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val settingsPreferences: SharedPreferences = context.getSharedPreferences(
            context.getString(R.string.preference_settings_key), Context.MODE_PRIVATE
        )

        if(settingsPreferences.getBoolean("notifications", false)){
            val notificationHelper = NotificationHelper(context)

            notificationHelper.sendHighPriorityNotification(context.getString(R.string.app_name), context.getString(R.string.geofence_entered), MapActivity::class.java)
        }

    }
}