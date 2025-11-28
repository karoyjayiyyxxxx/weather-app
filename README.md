 臺灣即時氣象查詢 App (Taiwan Weather Forecast App)

這是一個 Android 行動應用程式專案，旨在提供使用者直覺且友善的台灣天氣查詢服務。系統整合了台灣地圖視覺化介面，使用者可快速查詢各縣市的即時氣溫、降雨機率與體感舒適度，並具備天氣警示與查詢紀錄功能。

 🌟 功能特色 (Features)
 1. 互動式主選單
 圖示化介面：首頁採用 12 宮格天氣圖示設計，提供「進入查詢」、「歷史紀錄」、「工作團隊」等功能入口。
 視覺化地圖：查詢介面結合台灣地圖插畫，提升使用者體驗 (UI/UX)。
 2. 即時天氣查詢
 縣市選擇：透過下拉式選單 (Spinner) 快速切換基隆、台北、台中、雲林等全台縣市。
 詳細資訊：即時顯示以下氣象數據：
     天氣現象（如：多雲、午後雷陣雨）
     氣溫範圍 (Temperature)
     降雨機率 (Rain Probability)
     體感描述（如：悶熱、舒適）
 3. 智慧警示系統 (Smart Alerts)
 特殊天氣提醒：系統會根據回傳的資料自動判斷天氣狀況。
     降雨提醒：若有降雨機率，跳出「有可能會下雨喔」提示框。
     雷雨特報：若偵測到雷雨，會跳出黃色警示標語，提醒使用者留意強陣風與閃電。
 動態背景：根據天氣狀況（如陰天、雷雨）自動切換對應的情境圖片。

 4. 歷史紀錄管理 (History Management)
 自動儲存：每次查詢成功後，系統會自動儲存紀錄，並以 Toast 訊息提示「查詢紀錄已儲存」。
 紀錄列表：條列式顯示過往的查詢細節（包含查詢時間戳記 Timestamp）。
 一鍵清除：提供「清除所有紀錄」按鈕，方便使用者管理儲存空間。

 🛠️ 技術堆疊 (Tech Stack)
 開發平台：Android Studio
 程式語言：Java / Kotlin
 介面開發：XML Layout (ConstraintLayout, LinearLayout)
 元件運用：
     `Spinner` (下拉選單)
     `RecyclerView` / `ListView` (紀錄列表)
     `AlertDialog` (警示視窗)
     `Toast` (浮動提示訊息)
 資料儲存：SharedPreferences 或 SQLite (用於儲存歷史紀錄)
 API 串接：中央氣象署 (CWA) 開放資料 API (Open Data API)

 🚀 安裝與執行 (Installation)
1.  環境需求：
     Android Studio Ladybug 或更新版本。
     Android SDK Target 30+ (建議)。
2.  執行步驟：
     將專案 Clone 至本地端。
     開啟 Android Studio 並 Sync Gradle。
     連接 Android 手機或開啟模擬器 (Emulator)。
     點擊 Run 按鈕即可部署 App。

 📝 學習心得
本專案讓我深入了解了 Android Activity 生命週期的管理，以及如何透過 API 抓取網路資料並解析 JSON 格式。此外，在實作「歷史紀錄」功能時，我學習到了本地端資料庫的 CRUD（新增、讀取、刪除）操作，並透過 Adapter 將資料動態綁定到列表介面上，是一個整合性極佳的練習專案。

