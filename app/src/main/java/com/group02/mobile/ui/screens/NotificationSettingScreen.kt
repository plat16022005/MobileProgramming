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
import com.group02.mobile.ui.theme.*
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
    var showSurvey by remember { mutableStateOf(false) }

    val surveycalendar = Calendar.getInstance()
    val surveyhour = surveycalendar.get(Calendar.HOUR_OF_DAY)
    val surveyminute = surveycalendar.get(Calendar.MINUTE)


    val surveyTimePickerDialog = TimePickerDialog(context, { _, selectedHour, selectedMinute ->
        val formattedTime = String.format(Locale.US, "%02d:%02d", selectedHour, selectedMinute)

        val updatedSchedule = daysOfWeek.associate { day ->
            val existingTimes = currentSchedule[day] ?: emptyList()
            // Nếu ngày đó chưa có giờ này thì gộp vào và sắp xếp lại, có rồi thì giữ nguyên
            val newTimes = if (existingTimes.contains(formattedTime)) existingTimes else (existingTimes + formattedTime).sorted()
            day to newTimes
        }
        showSurvey = false

        viewModel.saveSchedules(updatedSchedule) { success ->
            if (success) {
                currentSchedule = updatedSchedule
                datLichBaoThucChuan(context, updatedSchedule)
            }
        }
    }, surveyhour, surveyminute, true)

    LaunchedEffect(Unit) {
        viewModel.loadSchedules()
    }

    LaunchedEffect(savedSchedule) {
        currentSchedule = savedSchedule
        if (savedSchedule.isNotEmpty()) {
            datLichBaoThucChuan(context, savedSchedule)
        } else {
            showSurvey = true
        }
    }

    if (showSurvey) {
        AlertDialog(
            onDismissRequest = { showSurvey = false },
            containerColor = InkDark,
            title = { Text(text = "Khảo Sát Thời Gian Rảnh 🌸", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 20.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Nihonlish muốn biết bạn thường rảnh vào khung giờ nào nhất mỗi ngày để tiện nhắc nhở học tập?", color = TextSecondary, fontSize = 14.sp)

                    // Nút Buổi sáng (Gộp thêm 06:00 vào lịch cũ)
                    Button(
                        onClick = {
                            val setupSchedule = daysOfWeek.associate { day ->
                                val existing = currentSchedule[day] ?: emptyList()
                                day to (if (existing.contains("06:00")) existing else (existing + "06:00").sorted())
                            }
                            showSurvey = false
                            viewModel.saveSchedules(setupSchedule) { if (it) { currentSchedule = setupSchedule; datLichBaoThucChuan(context, setupSchedule) } }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = CardBorder)
                    ) { Text("Buổi Sáng cố định (06:00)", color = TextPrimary) }

                    // Nút Buổi tối (Gộp thêm 21:00 vào lịch cũ)
                    Button(
                        onClick = {
                            val setupSchedule = daysOfWeek.associate { day ->
                                val existing = currentSchedule[day] ?: emptyList()
                                day to (if (existing.contains("21:00")) existing else (existing + "21:00").sorted())
                            }
                            showSurvey = false
                            viewModel.saveSchedules(setupSchedule) { if (it) { currentSchedule = setupSchedule; datLichBaoThucChuan(context, setupSchedule) } }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = CardBorder)
                    ) { Text("Buổi Tối cố định (21:00)", color = TextPrimary) }

                    // Nút Cả hai (Gộp cả 06:00 và 21:00 vào lịch cũ)
                    Button(
                        onClick = {
                            val setupSchedule = daysOfWeek.associate { day ->
                                val existing = currentSchedule[day] ?: emptyList<String>()
                                val added = mutableListOf<String>()
                                if (!existing.contains("06:00")) added.add("06:00")
                                if (!existing.contains("21:00")) added.add("21:00")
                                day to (existing + added).sorted()
                            }
                            showSurvey = false
                            viewModel.saveSchedules(setupSchedule) { if (it) { currentSchedule = setupSchedule; datLichBaoThucChuan(context, setupSchedule) } }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = NihonRedLight)
                    ) { Text("Cả Sáng & Tối (Học siêu tốc)", color = Color.White, fontWeight = FontWeight.Bold) }


                    Button(
                        onClick = { surveyTimePickerDialog.show() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = NihonRedLight)
                    ) { Text("➕ Tự chọn giờ rảnh cho bản thân", color = Color.White, fontWeight = FontWeight.Bold) }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSurvey = false }) { Text("Tự đặt lịch thủ công", color = NihonRedLight) }
            }
        )
    }



    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)


    val timePickerDialog = TimePickerDialog(context, { _, selectedHour, selectedMinute ->
        val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
        val existingTimes = currentSchedule[selectedDay] ?: emptyList()
        if (!existingTimes.contains(formattedTime)) {
            val updatedTimes = (existingTimes + formattedTime).sorted()
            currentSchedule = currentSchedule + (selectedDay to updatedTimes)
        }
    }, hour, minute, true)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thiết Lập Lịch Nhắc Học", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = NihonRedLight
                        )
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )

        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("1. Chọn ngày học trong tuần:", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(daysOfWeek) { day ->
                    val isSelected = day == selectedDay
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable { selectedDay = day }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = day.substring(0, 3), // Cắt chữ lấy Mon, Tue, Wed...
                            color = if (isSelected) Color.White else Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("2. Khung giờ học của [$selectedDay]:", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)


            val timesForSelectedDay = currentSchedule[selectedDay] ?: emptyList()
            if (timesForSelectedDay.isEmpty()) {
                Text("Chưa đặt lịch hẹn giờ nào cho ngày này.", color = Color.Gray, fontSize = 14.sp)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    timesForSelectedDay.forEach { time ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(time, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text(
                                "Xóa",
                                color = Color.Red,
                                modifier = Modifier.clickable {
                                    // Xóa giờ khỏi danh sách
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

            // Nút bấm mở Dialog chọn giờ
            Button(
                onClick = { timePickerDialog.show() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Thêm Khung Giờ Học")
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
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("LƯU LỊCH VÀO HỆ THỐNG", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}


fun datLichBaoThucChuan(context: android.content.Context, schedule: Map<String, List<String>>) {
    if (schedule.isEmpty()) return

    val alarmManager = context.getSystemService(android.content.Context.ALARM_SERVICE) as android.app.AlarmManager
    val daysMapping = mapOf(
        "Sunday" to Calendar.SUNDAY,
        "Monday" to Calendar.MONDAY,
        "Tuesday" to Calendar.TUESDAY,
        "Wednesday" to Calendar.WEDNESDAY,
        "Thursday" to Calendar.THURSDAY,
        "Friday" to Calendar.FRIDAY,
        "Saturday" to Calendar.SATURDAY
    )

    val now = Calendar.getInstance()
    var targetTimeInMillis: Long = Long.MAX_VALUE
    var selectedAlarmTime = ""

    // Duyệt qua tất cả các Thứ và Giờ phút người dùng đã cài đặt
    for ((dayName, timeList) in schedule) {
        val targetDayOfWeek = daysMapping[dayName] ?: continue

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

            // Nếu thời điểm đặt lịch này đã trôi qua trong tuần này, ta cộng thêm 7 ngày để hẹn vào tuần sau!
            if (targetCalendar.before(now)) {
                targetCalendar.add(Calendar.DAY_OF_YEAR, 7)
            }

            // Tìm ra mốc thời gian gần với hiện tại nhất để đặt báo thức trước
            if (targetCalendar.timeInMillis < targetTimeInMillis) {
                targetTimeInMillis = targetCalendar.timeInMillis
                selectedAlarmTime = "$dayName lúc $timeStr"
            }
        }
    }

    // Nếu tìm thấy một lịch hẹn hợp lệ trong tương lai, tiến hành cắm chốt với Android
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
                // Nếu chưa có quyền, mở cài đặt để người dùng cấp quyền
                val settingsIntent = android.content.Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                context.startActivity(settingsIntent)
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, targetTimeInMillis, pendingIntent)
        }
    }
}