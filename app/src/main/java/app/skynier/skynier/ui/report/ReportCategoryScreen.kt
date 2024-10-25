package app.skynier.skynier.ui.report

//import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import app.skynier.skynier.database.entities.MainCategoryEntity
import app.skynier.skynier.database.entities.RecordEntity
import app.skynier.skynier.database.entities.UserSettingsEntity
import app.skynier.skynier.library.SharedOptions
import app.skynier.skynier.library.textColorBasedOnAmount
import app.skynier.skynier.ui.theme.Gray
import app.skynier.skynier.viewmodels.MainCategoryViewModel
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat
import java.text.NumberFormat

@Composable
fun ReportCategoryScreen(
    recordTotal: List<RecordEntity>,
    mainCategoryViewModel: MainCategoryViewModel,
    userSettings: UserSettingsEntity?,
) {
    if (recordTotal.isEmpty()){
        Text("No data available", fontSize = 16.sp)
        return
    }
    val context = LocalContext.current
    val mainCategories by mainCategoryViewModel.mainCategories.observeAsState(emptyList())
    val selectedCategoryType by rememberSaveable { mutableIntStateOf(0) }
    val filteredRecords = when (selectedCategoryType) {
        0 -> recordTotal.filter { it.type == 1 } // Expenses
        1 -> recordTotal.filter { it.type == 2 } // Income
        2 -> recordTotal.filter { it.type == 3 || it.type == 4 } // Transfers
        else -> emptyList()
    }

    val categoryMap = filteredRecords.groupBy { it.mainCategoryId }
    Log.d("categoryMap", categoryMap.toString())
    // 載入資料
    LaunchedEffect(Unit) {
        mainCategoryViewModel.loadAllMainCategories()
    }
    Column {
//        Row(modifier = Modifier.fillMaxWidth()) {
        ReportCategoryPieChart(categoryMap, selectedCategoryType, mainCategories)
//        }
        LazyColumn {
            items(categoryMap.entries.toList()) { (mainCategoryId, records) ->
                val mainCategory = mainCategories.find { it.mainCategoryId == mainCategoryId }
                //分類圖示
                val recordIcon = SharedOptions.iconMap[mainCategory?.mainCategoryIcon]
                val backgroundColor = mainCategory?.mainCategoryBackgroundColor?.let {
                    Color(android.graphics.Color.parseColor("#$it"))
                } ?: MaterialTheme.colorScheme.surfaceContainer
                val iconColor = mainCategory?.mainCategoryIconColor?.let {
                    Color(android.graphics.Color.parseColor("#$it"))
                } ?: MaterialTheme.colorScheme.onSurface
                //分類名稱
                val categoryName = mainCategory?.let {
                    val resourceId = context.resources.getIdentifier(
                        it.mainCategoryNameKey,
                        "string",
                        context.packageName
                    )
                    if (resourceId != 0) {
                        context.getString(resourceId) // 如果語系字串存在，顯示語系的值
                    } else {
                        it.mainCategoryNameKey // 如果語系字串不存在，顯示原始值
                    }
                } ?: "Unknown"
                // 計算這個分類的總金額
                val totalAmount = records.sumOf { it.amount }


                val decimalFormat = DecimalFormat("#,###.##")
                val formattedValue = decimalFormat.format(totalAmount)
                var textColor =
                    textColorBasedOnAmount(userSettings?.textColor ?: 0, totalAmount)
                if (selectedCategoryType == 0) {
                    textColor =
                        textColorBasedOnAmount(userSettings?.textColor ?: 0, 0 - totalAmount)
                }
                ListItem(
                    modifier = Modifier.clickable {  },
                    headlineContent = { Text(text = categoryName) },
                    trailingContent = {
                        Text(
                            text = "$$formattedValue",
                            color = textColor,
                            fontSize = 14.sp
                        )
                    },
                    leadingContent = {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(backgroundColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            recordIcon?.let { iconData ->
                                if (mainCategory != null) {
                                    Icon(
                                        imageVector = iconData.icon,
                                        contentDescription = mainCategory.mainCategoryNameKey,
                                        modifier = Modifier.size(20.dp),
                                        tint = iconColor
                                    )
                                }
                            } ?: run {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = "Default icon",
                                    modifier = Modifier.size(18.dp),
                                    tint = androidx.compose.ui.graphics.Color.Gray
                                )
                            }
                        }
                    }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun ReportCategoryPieChart(
    categoryMap: Map<Int, List<RecordEntity>>,
    selectedCategoryType: Int,
    mainCategories: List<MainCategoryEntity>
) {
    val context = LocalContext.current

    val m3OnSurface = MaterialTheme.colorScheme.onSurface.toArgb()
    val m3Surface = MaterialTheme.colorScheme.surface.toArgb()


    val totalAmountMap = categoryMap.mapValues { (_, records) ->
        records.sumOf { it.amount }.toFloat()
    }

    if (totalAmountMap.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "No data available", fontSize = 16.sp)
        }
        return
    }

    val totalAmountSum = totalAmountMap.values.sum()
    val entries = mutableListOf<PieEntry>()
    val colors = mutableListOf<Int>()

    totalAmountMap.forEach { (mainCategoryId, totalAmount) ->
        val category = mainCategories.find { it.mainCategoryId == mainCategoryId }
        val categoryName = category?.let {
            val resourceId = context.resources.getIdentifier(
                it.mainCategoryNameKey,
                "string",
                context.packageName
            )
            if (resourceId != 0) {
                context.getString(resourceId) // 如果語系字串存在，顯示語系的值
            } else {
                it.mainCategoryNameKey // 如果語系字串不存在，顯示原始值
            }
        } ?: "Unknown"

        // 獲取分類顏色
        val categoryColor = category?.mainCategoryBackgroundColor?.let { colorHex ->
            try {
                android.graphics.Color.parseColor("#$colorHex")
            } catch (e: Exception) {
                android.graphics.Color.GRAY // 如果顏色無效，使用灰色作為回退顏色
            }
        } ?: android.graphics.Color.GRAY // 如果分類為 null，使用灰色作為回退顏色

        entries.add(PieEntry(totalAmount / totalAmountSum * 100, categoryName))
        colors.add(categoryColor)
    }

    val dataSet = PieDataSet(entries, "Category Distribution").apply {
        this.colors = colors
        setValueTextColors(colors)
        valueLinePart1Length = 0.6f
        valueLinePart2Length = 0.3f
        valueLineWidth = 2f
        valueLinePart1OffsetPercentage = 115f
        isUsingSliceColorAsValueLineColor = true
        yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        valueTextSize = 16f
        valueTypeface = Typeface.DEFAULT_BOLD
        valueFormatter = object : ValueFormatter() {
            private val formatter = DecimalFormat("0.0%")

            override fun getFormattedValue(value: Float) =
                formatter.format(value / 100f)
        }
    }

    val pieData = PieData(dataSet)

    Column {
        AndroidView(
            factory = { context ->
                PieChart(context).apply {
                    this.data = pieData
                    this.description.isEnabled = false
                    this.legend.isEnabled = false
                    this.setUsePercentValues(true)
                    this.isDrawHoleEnabled = true
                    this.holeRadius = 60f
                    this.setHoleColor(m3Surface)
                    this.setDrawCenterText(true)
                    this.setCenterTextSize(14f)
                    this.setCenterTextColor(m3OnSurface)
                    this.setExtraOffsets(0f, 20f, 0f, 20f)
                }
            },
            update = { chart ->
                chart.data = pieData
                chart.invalidate()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
    }
}