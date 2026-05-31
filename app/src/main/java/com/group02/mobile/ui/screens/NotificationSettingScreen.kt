package com.group02.mobile.ui.screens

import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.group02.mobile.ui.theme.* // Đảm bảo có InkBlack, InkDark, NihonRedLight, CardBorder, TextPrimary...
import com.group02.mobile.viewmodel.NotificationViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingScreen(
    onNavigateBack: () -> Unit,
    viewModel: NotificationViewModel = viewModel()
) {
    val context = LocalContext.current
    val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    val savedSchedule by viewModel.scheduleState.collectAsState()
    var currentSchedule by remember { mutableStateOf<Map<String, List<String>>>(emptyMap()) }
    var selectedDay by remember { mutableStateOf("Monday") }


    LaunchedEffect(Unit) {
        viewModel.loadSchedules()
    }

    LaunchedEffect(savedSchedule) {
        currentSchedule = savedSchedule
        datLichBaoThucChuan(context, savedSchedule)
    }


    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    val timePickerDialog = TimePickerDialog(context, { _, selectedHour, selectedMinute ->
        val formattedTime = String.format(Locale.US, "%02d:%02d", selectedHour, selectedMinute)
        val existingTimes = currentSchedule[selectedDay] ?: emptyList()
        if (!existingTimes.contains(formattedTime)) {
            val updatedTimes = (existingTimes + formattedTime).sorted()
            currentSchedule = currentSchedule + (selectedDay to updatedTimes)
        }
    }, hour, minute, true)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thiết Lập Lịch Nhắc Học", fontWeight = FontWeight.Bold, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = NihonRedLight
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = InkDark)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(InkBlack)
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("1. Chọn ngày học trong tuần:", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(daysOfWeek) { day ->
                    val isSelected = day == selectedDay
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isSelected) NihonRedLight else CardBorder,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable { selectedDay = day }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = day.substring(0, 3),
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("2. Khung giờ học của [$selectedDay]:", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)

            val timesForSelectedDay = currentSchedule[selectedDay] ?: emptyList()
            if (timesForSelectedDay.isEmpty()) {
                Text("Chưa đặt lịch hẹn giờ nào cho ngày này (Hệ thống sẽ nhắc nhở ngầm lúc 06:00).", color = TextSecondary, fontSize = 14.sp)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    timesForSelectedDay.forEach { time ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(InkDark, RoundedCornerShape(8.dp))
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(time, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(
                                text = "Xóa",
                                color = NihonRedLight,
                                modifier = Modifier.clickable {
                                    val updatedTimes = timesForSelectedDay - time
                                    currentSchedule = if (updatedTimes.isEmpty()) {
                                        currentSchedule - selectedDay
                                    } else {
                                        currentSchedule + (selectedDay to updatedTimes)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            Button(
                onClick = { timePickerDialog.show() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = CardBorder)
            ) {
                Text("Thêm Khung Giờ Học", color = TextPrimary)
            }

            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    viewModel.saveSchedules(currentSchedule) { success ->
                        if (success) {
                            datLichBaoThucChuan(context, currentSchedule)
                            Toast.makeText(context, "Lưu lịch nhắc thành công!", Toast.LENGTH_SHORT).show()
                            onNavigateBack()
                        } else {
                            Toast.makeText(context, "Có lỗi xảy ra rồi!", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = NihonRedLight),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("LƯU LỊCH VÀO HỆ THỐNG", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

fun datLichBaoThucChuan(context: android.content.Context, schedule: Map<String, List<String>>) {
    val alarmManager = context.getSystemService(android.content.Context.ALARM_SERVICE) as android.app.AlarmManager
    val now = Calendar.getInstance()
    var targetTimeInMillis: Long = Long.MAX_VALUE
    var selectedAlarmTime = ""

    val fixedDaysOfWeek = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    val daysMapping = mapOf(
        "Sunday" to Calendar.SUNDAY,
        "Monday" to Calendar.MONDAY,
        "Tuesday" to Calendar.TUESDAY,
        "Wednesday" to Calendar.WEDNESDAY,
        "Thursday" to Calendar.THURSDAY,
        "Friday" to Calendar.FRIDAY,
        "Saturday" to Calendar.SATURDAY
    )

    for (dayName in fixedDaysOfWeek) {
        val targetDayOfWeek = daysMapping[dayName] ?: continue
        val timeList = schedule[dayName]


        if (timeList.isNullOrEmpty()) {
            val targetCalendar = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_WEEK, targetDayOfWeek)
                set(Calendar.HOUR_OF_DAY, 8)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            if (targetCalendar.before(now)) {
                targetCalendar.add(Calendar.DAY_OF_YEAR, 7)
            }

            if (targetCalendar.timeInMillis < targetTimeInMillis) {
                targetTimeInMillis = targetCalendar.timeInMillis
                selectedAlarmTime = "$dayName lúc 06:00 (Mặc định)"
            }
        }
        else {
            for (timeStr in timeList) {
                val parts = timeStr.split(":")
                if (parts.size != 2) continue
                val hour = parts[0].toInt()
                val minute = parts[1].toInt()

                val targetCalendar = Calendar.getInstance().apply {
                    set(Calendar.DAY_OF_WEEK, targetDayOfWeek)
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                if (targetCalendar.before(now)) {
                    targetCalendar.add(Calendar.DAY_OF_YEAR, 7)
                }

                if (targetCalendar.timeInMillis < targetTimeInMillis) {
                    targetTimeInMillis = targetCalendar.timeInMillis
                    selectedAlarmTime = "$dayName lúc $timeStr"
                }
            }
        }
    }

    if (targetTimeInMillis != Long.MAX_VALUE) {
        val intent = android.content.Intent(context, com.group02.mobile.utils.AlarmReceiver::class.java).apply {
            putExtra("title", "Đến giờ học tiếng Nhật! 📚")
            putExtra("body", "Lịch học [$selectedAlarmTime] đến thời gian rồi mau vào app học ngay!")
        }

        val pendingIntent = android.app.PendingIntent.getBroadcast(
            context,
            3005,
            intent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, targetTimeInMillis, pendingIntent)
            } else {
                val settingsIntent = android.content.Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                context.startActivity(settingsIntent)
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, targetTimeInMillis, pendingIntent)
        }
    }
}