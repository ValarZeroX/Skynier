package app.skynier.skynier.ui.record

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.skynier.skynier.R
import app.skynier.skynier.database.entities.RecordEntity
import app.skynier.skynier.database.entities.UserSettingsEntity
import app.skynier.skynier.library.textColor
import app.skynier.skynier.ui.theme.Gray
import app.skynier.skynier.viewmodels.UserSettingsViewModel
import java.text.DecimalFormat

@Composable
fun RecordDashboard(records: List<RecordEntity>, userSettings: UserSettingsEntity?) {
    // 定义数据类
    data class CurrencyTotals(
        val currency: String,
        val totalIncome: Double,
        val totalExpenses: Double,
        val totalBalance: Double
    )

    // 数据计算
    val currencyTotalsList = remember(records) {
        records.groupBy { it.currency }.map { (currency, recordsInCurrency) ->
            val totalIncome = recordsInCurrency.filter { it.type == 1 }.sumOf { it.amount }
            val totalExpenses = recordsInCurrency.filter { it.type == 2 }.sumOf { it.amount }
            val totalBalance = totalIncome - totalExpenses
            CurrencyTotals(currency, totalIncome, totalExpenses, totalBalance)
        }
    }

    val primaryCurrency = userSettings?.currency ?: currencyTotalsList.firstOrNull()?.currency ?: ""
    val primaryCurrencyTotals =
        currencyTotalsList.find { it.currency == primaryCurrency } ?: CurrencyTotals(
            currency = primaryCurrency,
            totalIncome = 0.0,
            totalExpenses = 0.0,
            totalBalance = 0.0
        )

    val decimalFormat = DecimalFormat("#,###.##")

    var totalExpensesColor = MaterialTheme.colorScheme.onBackground
    userSettings?.let { totalExpensesColor = textColor(it.textColor, 2) }
    var totalIncomeColor = MaterialTheme.colorScheme.onBackground
    userSettings?.let { totalIncomeColor = textColor(it.textColor, 1) }

    var expanded by rememberSaveable { mutableStateOf(false) }

    // UI 显示
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        // 展开状态
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // 标题行
            Row {
                Text(
                    text = stringResource(id = R.string.currency),
                    modifier = Modifier.weight(1f).wrapContentWidth(Alignment.CenterHorizontally),
                    fontSize = 12.sp,
                )
                Text(
                    text = stringResource(id = R.string.expense),
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    fontSize = 12.sp,
                )
                Text(
                    text = stringResource(id = R.string.income),
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    fontSize = 12.sp,
                )
                Text(
                    text = stringResource(id = R.string.total),
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    fontSize = 12.sp,
                )
            }
            if (!expanded) {
                // 显示第一个币别的信息
                Row {
                    Text(
                        text = primaryCurrencyTotals.currency,
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.CenterHorizontally),
                        fontSize = 10.sp,
                        color = Gray,
                    )
                    Text(
                        text = decimalFormat.format(primaryCurrencyTotals.totalExpenses),
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.CenterHorizontally),
                        fontSize = 12.sp,
                        color = totalExpensesColor,
                    )
                    Text(
                        text = decimalFormat.format(primaryCurrencyTotals.totalIncome),
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.CenterHorizontally),
                        fontSize = 12.sp,
                        color = totalIncomeColor,
                    )
                    Text(
                        text = decimalFormat.format(primaryCurrencyTotals.totalBalance),
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.CenterHorizontally),
                        fontSize = 12.sp,
                    )
                }
            } else {
                // 显示每个币别的信息
                currencyTotalsList.forEach { totals ->
                    Row {
                        Text(
                            text = totals.currency,
                            modifier = Modifier
                                .weight(1f)
                                .wrapContentWidth(Alignment.CenterHorizontally),
                            fontSize = 10.sp,
                            color = Gray,
                        )
                        Text(
                            text = decimalFormat.format(totals.totalExpenses),
                            modifier = Modifier
                                .weight(1f)
                                .wrapContentWidth(Alignment.CenterHorizontally),
                            fontSize = 12.sp,
                            color = totalExpensesColor,
                        )
                        Text(
                            text = decimalFormat.format(totals.totalIncome),
                            modifier = Modifier
                                .weight(1f)
                                .wrapContentWidth(Alignment.CenterHorizontally),
                            fontSize = 12.sp,
                            color = totalIncomeColor,
                        )
                        Text(
                            text = decimalFormat.format(totals.totalBalance),
                            modifier = Modifier
                                .weight(1f)
                                .wrapContentWidth(Alignment.CenterHorizontally),
                            fontSize = 12.sp,
                        )
                    }
                }
            }
        }
    }
}

