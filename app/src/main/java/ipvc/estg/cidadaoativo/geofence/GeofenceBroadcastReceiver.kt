package ipvc.estg.cidadaoativo.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import ipvc.estg.cidadaoativo.MapActivity
import ipvc.estg.cidadaoativo.R
import ipvc.estg.cidadaoativo.notifications.NotificationHelper

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        val settingsPreferences: SharedPreferences = context.getSharedPreferences(
            context.getString(R.string.preference_settings_key), Context.MODE_PRIVATE
        )

        if(settingsPreferences.getBoolean("notifications", false)){
            val notificationHelper = NotificationHelper(context)

            when(geofencingEvent.geofenceTransition){
                Geofence.GEOFENCE_TRANSITION_ENTER
                        -> notificationHelper.sendHighPriorityNotification(context.getString(R.string.app_name), context.getString(R.string.geofence_entered), MapActivity::class.java)

                Geofence.GEOFENCE_TRANSITION_EXIT
                        -> notificationHelper.sendHighPriorityNotification(context.getString(R.string.app_name), context.getString(R.string.geofence_exited), MapActivity::class.java)
            }

        }



    }
}