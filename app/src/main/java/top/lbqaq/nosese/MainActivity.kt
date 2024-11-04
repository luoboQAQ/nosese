package top.lbqaq.nosese

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.SharedPreferences
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val statusView : TextView = findViewById(R.id.statusView)
        val timeNowView : TextView = findViewById(R.id.timeNowView)
        val timeSetButton : Button = findViewById(R.id.timeSetButton)
        val dateSetView : TextView = findViewById(R.id.dateSetView)
        val timeSetView : TextView = findViewById(R.id.timeSetView)
        val refreshButton : Button = findViewById(R.id.refreshButton)

        val formatter : DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        //添加异常捕获，在非框架环境下不闪退
        var normalEnv = false
        val sharedPreferences : SharedPreferences = try {
            getSharedPreferences("config", Context.MODE_WORLD_READABLE)
        } catch (e : SecurityException){
            normalEnv = true
            getSharedPreferences("normal", Context.MODE_PRIVATE)
        }
        if (normalEnv) {
            Toast.makeText(this,"非模块环境，仅供调试，设置不会保存",Toast.LENGTH_SHORT).show()
        }

        dateSetView.setOnClickListener{
            // 日期选择器
            val ca = Calendar.getInstance()
            var mYear = ca[Calendar.YEAR]
            var mMonth = ca[Calendar.MONTH]
            var mDay = ca[Calendar.DAY_OF_MONTH]

            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    mYear = year
                    mMonth = month
                    mDay = dayOfMonth
                    val mDate = "${year}-%02d-%02d".format(month + 1, dayOfMonth)
                    // 将选择的日期赋值给TextView
                    dateSetView.text = mDate
                },
                mYear, mMonth, mDay
            )
            datePickerDialog.show()
        }

        timeSetView.setOnClickListener{
            // 时间选择器
            val ca = Calendar.getInstance()
            val mHour = ca[Calendar.HOUR_OF_DAY]
            val mMinute = ca[Calendar.MINUTE]

            val timePickerDialog = TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    val mTime = "%02d:%02d".format(hourOfDay,minute)
                    timeSetView.text = mTime
                },
                mHour, mMinute, true
            )
            timePickerDialog.show()
        }

        timeSetButton.setOnClickListener{
            //拼接时间字符串
            val timeString = buildString {
                append(dateSetView.text)
                append(" ")
                append(timeSetView.text)
                append(":00")
            }
            try {
                val time = LocalDateTime.parse(timeString,formatter)
            } catch (e : DateTimeParseException){
                Toast.makeText(this,"日期不合法，请从新设置",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            with(sharedPreferences.edit()) {
                putString("settingDate", timeString)
                apply() // 使用apply()提交数据
            }
            refreshUI(sharedPreferences, timeNowView, formatter, statusView)
        }

        refreshButton.setOnClickListener{
            refreshUI(sharedPreferences, timeNowView, formatter, statusView)
        }

        //初始化时间
        val nowDateTime : LocalDateTime = LocalDateTime.now()
        dateSetView.text = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(nowDateTime)
//        timeSetView.text = DateTimeFormatter.ofPattern("HH:mm").format(nowDateTime)

        refreshUI(sharedPreferences, timeNowView, formatter, statusView)
    }

    private fun refreshUI(
        sharedPreferences: SharedPreferences,
        timeNowView: TextView,
        formatter: DateTimeFormatter,
        statusView: TextView
    ) {
        val settingDateText = sharedPreferences.getString("settingDate", "-1")
        var settingDate = LocalDateTime.now()
        if (settingDateText == "-1") {
            timeNowView.text = "未设置"
        } else {
            settingDate = LocalDateTime.parse(settingDateText, formatter)
            timeNowView.text = settingDate.format(formatter)
        }

        val localDateTime = LocalDateTime.now()
        var isTimeOk = false
        if (localDateTime.isAfter(settingDate)) {
            isTimeOk = true
        }

        if (isTimeOk) {
            statusView.text = "已解锁"
        } else {
            statusView.text = "暂未解锁"
        }
    }
}
