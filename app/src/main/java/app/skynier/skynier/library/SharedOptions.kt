package app.skynier.skynier.library

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Approval
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BreakfastDining
import androidx.compose.material.icons.filled.BrunchDining
import androidx.compose.material.icons.filled.Commute
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Subway
import androidx.compose.material.icons.filled.Toys
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import app.skynier.skynier.R

data class CategoryIcon(
    val icon: ImageVector,
    val category: String
)
object SharedOptions {
    val iconMap = mapOf(
        "Restaurant" to CategoryIcon(Icons.Filled.Restaurant, "category_food"), //餐廳
        "FastFood" to CategoryIcon(Icons.Filled.Fastfood, "category_food"), //快餐
        "BreakfastDining" to CategoryIcon(Icons.Filled.BreakfastDining, "category_food"), //早餐
        "BrunchDining" to CategoryIcon(Icons.Filled.BrunchDining, "category_food"), //早午餐
        "LunchDining" to CategoryIcon(Icons.Filled.LunchDining, "category_food"), //午餐

        "Commute" to CategoryIcon(Icons.Filled.Commute, "category_transportation"), //通勤
        "LocalTaxi" to CategoryIcon(Icons.Filled.LocalTaxi, "category_transportation"), //計程車
        "Flight" to CategoryIcon(Icons.Filled.Flight, "category_transportation"), //航班
        "Train" to CategoryIcon(Icons.Filled.Train, "category_transportation"), //火車
        "Subway" to CategoryIcon(Icons.Filled.Subway, "category_transportation"), //捷運
        "DirectionsBus" to CategoryIcon(Icons.Filled.DirectionsBus, "category_transportation"), //巴士

        "FlightTakeoff" to CategoryIcon(Icons.Filled.FlightTakeoff, "category_travel"), //出境 or 旅行
        "FitnessCenter" to CategoryIcon(Icons.Filled.FitnessCenter, "category_travel"), //健身中心
        "Hotel" to CategoryIcon(Icons.Filled.Hotel, "category_travel"), //飯店

        "Forum" to CategoryIcon(Icons.Filled.Forum, "category_social"), //社交
        "Group" to CategoryIcon(Icons.Filled.Group, "category_social"), //朋友
        "Groups" to CategoryIcon(Icons.Filled.Groups, "category_social"), //團體

        "ShoppingCart" to CategoryIcon(Icons.Filled.ShoppingCart, "category_shopping"), //購物
        "Toys" to CategoryIcon(Icons.Filled.Toys, "category_shopping"), //玩具
        "Computer" to CategoryIcon(Icons.Filled.Computer, "category_shopping"), //電子產品
        "Apparel" to CategoryIcon(Icons.Filled.Approval, "category_shopping"), //服飾


        "AccountBalance" to CategoryIcon(Icons.Filled.AccountBalance, "category_finance"), //金融
        "Paid" to CategoryIcon(Icons.Filled.Paid, "category_finance"), //薪資
        "CreditCard" to CategoryIcon(Icons.Filled.CreditCard, "category_finance"), //信用卡
        "Payments" to CategoryIcon(Icons.Filled.Payments, "category_finance"), //付款方式
        "Savings" to CategoryIcon(Icons.Filled.Savings, "category_finance"), //儲蓄
        "AccountBalanceWallet" to CategoryIcon(Icons.Filled.AccountBalanceWallet, "category_finance"), //銀行餘額錢包
        "Wallet" to CategoryIcon(Icons.Filled.Wallet, "category_finance"), //錢包
        "Money" to CategoryIcon(Icons.Filled.Money, "category_finance"), //現金
        "AttachMoney" to CategoryIcon(Icons.Filled.AttachMoney, "category_finance"), //現金符號
    )
}