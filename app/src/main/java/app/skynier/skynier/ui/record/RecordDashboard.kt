package app.skynier.skynier.ui.record

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import app.skynier.skynier.database.entities.RecordEntity
import app.skynier.skynier.database.entities.UserSettingsEntity
import app.skynier.skynier.library.textColor
import app.skynier.skynier.viewmodels.UserSettingsViewModel
import java.text.DecimalFormat

@Composable
fun RecordDashboard(records: List<RecordEntity>, userSettings: UserSettingsEntity?,) {
    // 計算總收入和總支出
    val (totalIncome, totalExpenses) = remember(records) {
        val income = records.filter { it.type == 1 }.sumOf { it.amount }
        val expenses = records.filter { it.type == 2 }.sumOf { it.amount }
        Pair(income, expenses)
    }


    // 計算總額
    val totalBalance = totalIncome - totalExpenses

    val decimalFormat = DecimalFormat("#,###.##")
    val formattedValueTotalIncome = decimalFormat.format(totalIncome)
    val formattedValueTotalExpenses = decimalFormat.format(totalExpenses)
    val formattedValueTotalBalance = decimalFormat.format(totalBalance)

    var totalExpensesColor = MaterialTheme.colorScheme.onBackground
    userSettings?.let { totalExpensesColor = textColor(it.textColor, 2) }
    var totalIncomeColor = MaterialTheme.colorScheme.onBackground
    userSettings?.let { totalIncomeColor = textColor(it.textColor, 1) }
    // UI 顯示
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row {
            Text(
                text = "收入",
                modifier = Modifier.weight(1f).wrapContentWidth(Alignment.CenterHorizontally),
                fontSize = 12.sp,
            )
            Text(
                text = "支出",
                modifier = Modifier.weight(1f).wrapContentWidth(Alignment.CenterHorizontally),
                fontSize = 12.sp,

                )
            Text(
                text = "總額",
                modifier = Modifier.weight(1f).wrapContentWidth(Alignment.CenterHorizontally),
                fontSize = 12.sp,
            )
        }
        Row {
            Text(
                text = formattedValueTotalExpenses,
                modifier = Modifier.weight(1f).wrapContentWidth(Alignment.CenterHorizontally),
                fontSize = 12.sp,
                color = totalExpensesColor,
            )
            Text(
                text = formattedValueTotalIncome,
                modifier = Modifier.weight(1f).wrapContentWidth(Alignment.CenterHorizontally),
                fontSize = 12.sp,
                color = totalIncomeColor
            )
            Text(
                text = formattedValueTotalBalance,
                modifier = Modifier.weight(1f).wrapContentWidth(Alignment.CenterHorizontally),
                fontSize = 12.sp,
            )
        }
    }
}