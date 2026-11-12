package pl.shishasklep.zebrapackscanner

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : Activity() {
    companion object {
        const val ACTION_SCAN = "pl.shishasklep.zebrapackscanner.SCAN"
        const val ACTION_INTERNAL_SCAN = "pl.shishasklep.zebrapackscanner.INTERNAL_SCAN"
        const val EXTRA_CODE = "scan_code"
    }

    private lateinit var lastCode: TextView
    private lateinit var counter: TextView
    private lateinit var history: LinearLayout
    private lateinit var hint: TextView
    private val seen = LinkedHashSet<String>()
    private var total = 0
    private var lastValue = ""
    private var lastAt = 0L

    private val scanReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val value = intent?.getStringExtra(EXTRA_CODE)?.trim().orEmpty()
            if (value.isNotEmpty()) acceptScan(value)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildScreen()
        val filter = IntentFilter(ACTION_INTERNAL_SCAN)
        if (Build.VERSION.SDK_INT >= 33) {
            registerReceiver(scanReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            @Suppress("DEPRECATION")
            registerReceiver(scanReceiver, filter)
        }
    }

    override fun onDestroy() {
        try { unregisterReceiver(scanReceiver) } catch (_: Exception) {}
        super.onDestroy()
    }

    private fun buildScreen() {
        val outer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(18), dp(18), dp(14))
            setBackgroundColor(0xFFF4F6F8.toInt())
        }

        val title = TextView(this).apply {
            text = "ZEBRA PACK SCANNER"
            textSize = 21f
            setTextColor(0xFF17212B.toInt())
            typeface = Typeface.DEFAULT_BOLD
        }
        outer.addView(title)

        val subtitle = TextView(this).apply {
            text = "Сканирование номера пачки"
            textSize = 14f
            setTextColor(0xFF65717D.toInt())
            setPadding(0, dp(4), 0, dp(16))
        }
        outer.addView(subtitle)

        val scanCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(14), dp(20), dp(14), dp(20))
            setBackgroundColor(0xFFFFFFFF.toInt())
            elevation = dp(2).toFloat()
        }
        val scanLabel = TextView(this).apply {
            text = "ПОСЛЕДНЕЕ СКАНИРОВАНИЕ"
            textSize = 12f
            setTextColor(0xFF65717D.toInt())
            typeface = Typeface.DEFAULT_BOLD
        }
        scanCard.addView(scanLabel)
        lastCode = TextView(this).apply {
            text = "Готов к сканированию"
            textSize = 24f
            setTextColor(0xFF17212B.toInt())
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setPadding(0, dp(12), 0, dp(8))
        }
        scanCard.addView(lastCode)
        hint = TextView(this).apply {
            text = "Нажми боковую кнопку сканера Zebra"
            textSize = 13f
            setTextColor(0xFF65717D.toInt())
            gravity = Gravity.CENTER
        }
        scanCard.addView(hint)
        outer.addView(scanCard, LinearLayout.LayoutParams(-1, dp(160)))

        counter = TextView(this).apply {
            text = "Сканирований: 0"
            textSize = 16f
            setTextColor(0xFF17212B.toInt())
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, dp(18), 0, dp(10))
        }
        outer.addView(counter)

        val historyTitle = TextView(this).apply {
            text = "ИСТОРИЯ СКАНИРОВАНИЙ"
            textSize = 12f
            setTextColor(0xFF65717D.toInt())
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, dp(4), 0, dp(8))
        }
        outer.addView(historyTitle)

        val scroll = ScrollView(this)
        history = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        scroll.addView(history)
        outer.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))

        val apiInfo = TextView(this).apply {
            text = "IdoSell API: пока не подключён"
            textSize = 12f
            setTextColor(0xFF9A6500.toInt())
            setPadding(0, dp(10), 0, dp(8))
        }
        outer.addView(apiInfo)

        val clear = Button(this).apply {
            text = "Очистить историю"
            setOnClickListener {
                history.removeAllViews()
                seen.clear()
                total = 0
                counter.text = "Сканирований: 0"
                lastCode.text = "Готов к сканированию"
                hint.text = "История очищена"
            }
        }
        outer.addView(clear, LinearLayout.LayoutParams(-1, dp(48)))
        setContentView(outer)
    }

    private fun acceptScan(code: String) {
        val now = System.currentTimeMillis()
        if (code == lastValue && now - lastAt < 1800L) return
        lastValue = code
        lastAt = now
        total++
        seen.add(code)
        lastCode.text = code
        hint.text = if (seen.size < total) "Повторный номер - записан в истории" else "Номер получен. Поиск в IdoSell будет подключён позже."
        counter.text = "Сканирований: $total  |  Уникальных пачек: ${seen.size}"

        val stamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(now))
        val row = TextView(this).apply {
            text = "$stamp    $code"
            textSize = 16f
            setTextColor(0xFF24313D.toInt())
            setPadding(dp(12), dp(12), dp(8), dp(12))
            setBackgroundColor(0xFFFFFFFF.toInt())
        }
        history.addView(row, 0)
        val divider = View(this).apply { setBackgroundColor(0xFFE4E9EE.toInt()) }
        history.addView(divider, 1, LinearLayout.LayoutParams(-1, dp(1)))
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
