package app.skynier.skynier.library

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AirplaneTicket
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AirplaneTicket
import androidx.compose.material.icons.filled.Approval
import androidx.compose.material.icons.filled.Atm
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Attractions
import androidx.compose.material.icons.filled.BakeryDining
import androidx.compose.material.icons.filled.Bento
import androidx.compose.material.icons.filled.BikeScooter
import androidx.compose.material.icons.filled.BookOnline
import androidx.compose.material.icons.filled.BreakfastDining
import androidx.compose.material.icons.filled.BrunchDining
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CarRepair
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.CardTravel
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Church
import androidx.compose.material.icons.filled.Commute
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Cottage
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.material.icons.filled.DirectionsBoat
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsRailway
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsSubway
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.ElectricBike
import androidx.compose.material.icons.filled.ElectricCar
import androidx.compose.material.icons.filled.ElectricMoped
import androidx.compose.material.icons.filled.ElectricScooter
import androidx.compose.material.icons.filled.EvStation
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Festival
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.FlightLand
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.GolfCourse
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Hail
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.HeadphonesBattery
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.HolidayVillage
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Houseboat
import androidx.compose.material.icons.filled.Icecream
import androidx.compose.material.icons.filled.KebabDining
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Liquor
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.LocalActivity
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.LocalPizza
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Motorcycle
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.Museum
import androidx.compose.material.icons.filled.Nightlife
import androidx.compose.material.icons.filled.NoMeals
import androidx.compose.material.icons.filled.OtherHouses
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PedalBike
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.RamenDining
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.RiceBowl
import androidx.compose.material.icons.filled.RoomService
import androidx.compose.material.icons.filled.Sailing
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.SetMeal
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ShutterSpeed
import androidx.compose.material.icons.filled.SmokeFree
import androidx.compose.material.icons.filled.SmokingRooms
import androidx.compose.material.icons.filled.Snowmobile
import androidx.compose.material.icons.filled.SoupKitchen
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.SportsBar
import androidx.compose.material.icons.filled.Subway
import androidx.compose.material.icons.filled.TakeoutDining
import androidx.compose.material.icons.filled.Tapas
import androidx.compose.material.icons.filled.Toys
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.Tram
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.Villa
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.filled.WineBar
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import app.skynier.skynier.R

data class CategoryIcon(
    val icon: ImageVector,
    val category: String
)
object SharedOptions {
    val iconMap = mapOf(
        "BreakfastDining" to CategoryIcon(Icons.Filled.BreakfastDining, "category_food"), //早餐
        "BrunchDining" to CategoryIcon(Icons.Filled.BrunchDining, "category_food"), //早午餐
        "LunchDining" to CategoryIcon(Icons.Filled.LunchDining, "category_food"), //午餐
        "DinnerDining" to CategoryIcon(Icons.Filled.DinnerDining, "category_food"), //晚餐餐飲
        "LocalDining" to CategoryIcon(Icons.Filled.LocalDining, "category_food"), //餐飲
        "SetMeal" to CategoryIcon(Icons.Filled.SetMeal, "category_food"), //套餐
        "Nightlife" to CategoryIcon(Icons.Filled.Nightlife, "category_food"), //夜生活
        "FastFood" to CategoryIcon(Icons.Filled.Fastfood, "category_food"), //快餐
        "Restaurant" to CategoryIcon(Icons.Filled.Restaurant, "category_food"), //餐廳
        "IceCream" to CategoryIcon(Icons.Filled.Icecream, "category_food"), //冰淇淋
        "LocalPizza" to CategoryIcon(Icons.Filled.LocalPizza, "category_food"), //披薩
        "LocalDrink" to CategoryIcon(Icons.Filled.LocalDrink, "category_food"), //飲品
        "Tapas" to CategoryIcon(Icons.Filled.Tapas, "category_food"), //小吃
        "KebabDining" to CategoryIcon(Icons.Filled.KebabDining, "category_food"), //烤肉店
        "RiceBowl" to CategoryIcon(Icons.Filled.RiceBowl, "category_food"), //飯
        "Bento" to CategoryIcon(Icons.Filled.Bento, "category_food"), //便當
        "SoupKitchen" to CategoryIcon(Icons.Filled.SoupKitchen, "category_food"), //湯
        "TakeoutDining" to CategoryIcon(Icons.Filled.TakeoutDining, "category_food"), //外帶
        "BakeryDining" to CategoryIcon(Icons.Filled.BakeryDining, "category_food"), //麵包店
        "RamenDining" to CategoryIcon(Icons.Filled.RamenDining, "category_food"), //拉麵餐廳
        "LocalCafe" to CategoryIcon(Icons.Filled.LocalCafe, "category_food"), //咖啡店
        "Liquor" to CategoryIcon(Icons.Filled.Liquor, "category_food"), //酒
        "LocalBar" to CategoryIcon(Icons.Filled.LocalBar, "category_food"), //酒吧
        "WineBar" to CategoryIcon(Icons.Filled.WineBar, "category_food"), //酒吧
        "SportsBar" to CategoryIcon(Icons.Filled.SportsBar, "category_food"), //運動酒吧
        "NoMeals" to CategoryIcon(Icons.Filled.NoMeals, "category_food"), //禁止飲食

        "Commute" to CategoryIcon(Icons.Filled.Commute, "category_transportation"), //通勤
        "Flight" to CategoryIcon(Icons.Filled.Flight, "category_transportation"), //航班
        "FlightTakeoff" to CategoryIcon(Icons.Filled.FlightTakeoff, "category_transportation"), //出境
        "FlightLand" to CategoryIcon(Icons.Filled.FlightLand, "category_transportation"), //入境
        "DirectionsRun" to CategoryIcon(Icons.AutoMirrored.Filled.DirectionsRun, "category_transportation"), //跑步
        "DirectionsWalk" to CategoryIcon(Icons.AutoMirrored.Filled.DirectionsWalk, "category_transportation"), //步行
        "DirectionsBike" to CategoryIcon(Icons.AutoMirrored.Filled.DirectionsBike, "category_transportation"), //腳踏車
        "Hail" to CategoryIcon(Icons.Filled.Hail, "category_transportation"), //招呼
        "Train" to CategoryIcon(Icons.Filled.Train, "category_transportation"), //火車
        "Subway" to CategoryIcon(Icons.Filled.Subway, "category_transportation"), //捷運
        "DirectionsSubway" to CategoryIcon(Icons.Filled.DirectionsSubway, "category_transportation"), //地鐵
        "DirectionsRailway" to CategoryIcon(Icons.Filled.DirectionsRailway, "category_transportation"), //火車
        "Tram" to CategoryIcon(Icons.Filled.Tram, "category_transportation"), //路面電車
        "DirectionsBus" to CategoryIcon(Icons.Filled.DirectionsBus, "category_transportation"), //巴士
        "DirectionsBoat" to CategoryIcon(Icons.Filled.DirectionsBoat, "category_transportation"), //船
        "Sailing" to CategoryIcon(Icons.Filled.Sailing, "category_transportation"), //帆船
        "LocalTaxi" to CategoryIcon(Icons.Filled.LocalTaxi, "category_transportation"), //計程車
        "DirectionsCar" to CategoryIcon(Icons.Filled.DirectionsCar, "category_transportation"), //汽車
        "ElectricCar" to CategoryIcon(Icons.Filled.ElectricCar, "category_transportation"), //電動車
        "Motorcycle" to CategoryIcon(Icons.Filled.Motorcycle, "category_transportation"), //機車
        "ElectricMoped" to CategoryIcon(Icons.Filled.ElectricMoped, "category_transportation"), //電動機車
        "PedalBike" to CategoryIcon(Icons.Filled.PedalBike, "category_transportation"), //自行車
        "ElectricBike" to CategoryIcon(Icons.Filled.ElectricBike, "category_transportation"), //電動自行車
        "TwoWheeler" to CategoryIcon(Icons.Filled.TwoWheeler, "category_transportation"), //雙人車
        "ElectricScooter" to CategoryIcon(Icons.Filled.ElectricScooter, "category_transportation"), //電動滑板車
        "CarRepair" to CategoryIcon(Icons.Filled.CarRepair, "category_transportation"), //汽車維護
        "Snowmobile" to CategoryIcon(Icons.Filled.Snowmobile, "category_transportation"), //雪地摩托車
        "LocalParking" to CategoryIcon(Icons.Filled.LocalParking, "category_transportation"), //停車場
        "LocalGasStation" to CategoryIcon(Icons.Filled.LocalGasStation, "category_transportation"), //加油站
        "EvStation" to CategoryIcon(Icons.Filled.EvStation, "category_transportation"), //充電站



        "AirplaneTicket" to CategoryIcon(Icons.AutoMirrored.Filled.AirplaneTicket, "category_travel"), //機票
        "LocalActivity" to CategoryIcon(Icons.Filled.LocalActivity, "category_travel"), //活動 門票
        "Park" to CategoryIcon(Icons.Filled.Park, "category_travel"), //公園
        "LocationCity" to CategoryIcon(Icons.Filled.LocationCity, "category_travel"), //城市
        "FitnessCenter" to CategoryIcon(Icons.Filled.FitnessCenter, "category_travel"), //健身中心
        "Hotel" to CategoryIcon(Icons.Filled.Hotel, "category_travel"), //飯店
        "Spa" to CategoryIcon(Icons.Filled.Spa, "category_travel"), //温泉
        "Cottage" to CategoryIcon(Icons.Filled.Cottage, "category_travel"), //小屋
        "FamilyRestroom" to CategoryIcon(Icons.Filled.FamilyRestroom, "category_travel"), //家庭房
        "Pool" to CategoryIcon(Icons.Filled.Pool, "category_travel"), //水池
        "OtherHouses" to CategoryIcon(Icons.Filled.OtherHouses, "category_travel"), //其他房屋
        "Luggage" to CategoryIcon(Icons.Filled.Luggage, "category_travel"), //行李
        "Casino" to CategoryIcon(Icons.Filled.Casino, "category_travel"), //賭場
        "RoomService" to CategoryIcon(Icons.Filled.RoomService, "category_travel"), //客房服務
        "HolidayVillage" to CategoryIcon(Icons.Filled.HolidayVillage, "category_travel"), //渡假村
        "Museum" to CategoryIcon(Icons.Filled.Museum, "category_travel"), //博物館
        "Festival" to CategoryIcon(Icons.Filled.Festival, "category_travel"), //節慶 馬戲團
        "Attractions" to CategoryIcon(Icons.Filled.Attractions, "category_travel"), //景點 摩天輪
        "GolfCourse" to CategoryIcon(Icons.Filled.GolfCourse, "category_travel"), //高爾夫球場
        "Villa" to CategoryIcon(Icons.Filled.Villa, "category_travel"), //別墅
        "SmokeFree" to CategoryIcon(Icons.Filled.SmokeFree, "category_travel"), //無菸
        "SmokingRooms" to CategoryIcon(Icons.Filled.SmokingRooms, "category_travel"), //吸煙室
        "Houseboat" to CategoryIcon(Icons.Filled.Houseboat, "category_travel"), //船屋
        "Church" to CategoryIcon(Icons.Filled.Church, "category_travel"), //教會
        "LocalLaundryService" to CategoryIcon(Icons.Filled.LocalLaundryService, "category_travel"), //洗衣店

        "Forum" to CategoryIcon(Icons.Filled.Forum, "category_social"), //社交
        "Group" to CategoryIcon(Icons.Filled.Group, "category_social"), //朋友
        "Groups" to CategoryIcon(Icons.Filled.Groups, "category_social"), //團體

        "ShoppingCart" to CategoryIcon(Icons.Filled.ShoppingCart, "category_shopping"), //購物
        "Toys" to CategoryIcon(Icons.Filled.Toys, "category_shopping"), //玩具
        "Apparel" to CategoryIcon(Icons.Filled.Approval, "category_shopping"), //服飾
        "MenuBook" to CategoryIcon(Icons.AutoMirrored.Filled.MenuBook, "category_shopping"), //書籍
        "BookOnline" to CategoryIcon(Icons.Filled.BookOnline, "category_shopping"), //電子書
        "Diamond" to CategoryIcon(Icons.Filled.Diamond, "category_shopping"), //鑽石 or 珠寶
        "LocalMall" to CategoryIcon(Icons.Filled.LocalMall, "category_shopping"), //商業活動
        "Computer" to CategoryIcon(Icons.Filled.Computer, "category_shopping"), //電腦
        "Mouse" to CategoryIcon(Icons.Filled.Mouse, "category_shopping"), //滑鼠
        "Keyboard" to CategoryIcon(Icons.Filled.Keyboard, "category_shopping"), //鍵盤
        "Tv" to CategoryIcon(Icons.Filled.Tv, "category_shopping"), //電視
        "LiveTv" to CategoryIcon(Icons.Filled.LiveTv, "category_shopping"), //線上電視
        "Phone" to CategoryIcon(Icons.Filled.Phone, "category_shopping"), //電話
        "PhoneIphone" to CategoryIcon(Icons.Filled.PhoneIphone, "category_shopping"), //IPhone
        "PhoneAndroid" to CategoryIcon(Icons.Filled.PhoneAndroid, "category_shopping"), //Android
        "Headphones" to CategoryIcon(Icons.Filled.Headphones, "category_shopping"), //耳機
        "HeadsetMic" to CategoryIcon(Icons.Filled.HeadsetMic, "category_shopping"), //耳機麥克風
        "HeadphonesBattery" to CategoryIcon(Icons.Filled.HeadphonesBattery, "category_shopping"), //耳機電池


        "AccountBalance" to CategoryIcon(Icons.Filled.AccountBalance, "category_finance"), //金融
        "Paid" to CategoryIcon(Icons.Filled.Paid, "category_finance"), //薪資
        "CreditCard" to CategoryIcon(Icons.Filled.CreditCard, "category_finance"), //信用卡
        "Payments" to CategoryIcon(Icons.Filled.Payments, "category_finance"), //付款方式
        "Savings" to CategoryIcon(Icons.Filled.Savings, "category_finance"), //儲蓄
        "AccountBalanceWallet" to CategoryIcon(Icons.Filled.AccountBalanceWallet, "category_finance"), //銀行餘額錢包
        "Wallet" to CategoryIcon(Icons.Filled.Wallet, "category_finance"), //錢包
        "Money" to CategoryIcon(Icons.Filled.Money, "category_finance"), //現金
        "AttachMoney" to CategoryIcon(Icons.Filled.AttachMoney, "category_finance"), //現金符號
        "Atm" to CategoryIcon(Icons.Filled.Atm, "category_finance"), //Atm
        "LocalAtm" to CategoryIcon(Icons.Filled.LocalAtm, "category_finance"), //Atm
        "Calculate" to CategoryIcon(Icons.Filled.Calculate, "category_finance"), //計算機
        "CardMembership" to CategoryIcon(Icons.Filled.CardMembership, "category_finance"), //會員卡
        "CardTravel" to CategoryIcon(Icons.Filled.CardTravel, "category_finance"), //旅遊卡
        "CurrencyExchange" to CategoryIcon(Icons.Filled.CurrencyExchange, "category_finance"), //幣別切換
    )
}