package pl.shishasklep.zebrapackscanner

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ScanReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != MainActivity.ACTION_SCAN) return
        val code = intent.getStringExtra("com.symbol.datawedge.data_string")
            ?: intent.getStringExtra("data")
            ?: return
        val clean = code.trim()
        if (clean.isNotEmpty()) {
            context.sendBroadcast(
                Intent(MainActivity.ACTION_INTERNAL_SCAN)
                    .setPackage(context.packageName)
                    .putExtra(MainActivity.EXTRA_CODE, clean)
            )
        }
    }
}
