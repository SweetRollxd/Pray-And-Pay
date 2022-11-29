package com.example.praypay

object Constants {
    private const val HOST = "217.71.129.139"
    private const val PORT = 4753
    const val API_SERVER = "http://$HOST:$PORT"

    const val USER_ID = 5

    const val LOG_TAG = "PrayNPay"

    val PARAMS = mapOf<String, String>(
        "weight" to "Масса",
        "color" to "Цвет",
        "length" to "Длина",
        "width" to "Ширина",
        "material" to "Материал",
        "expiration date" to "Годен до",
        "manufacturer" to "Производитель",
        "articul" to "Артикул",
        "amount" to "Количество в упаковке",
        "production_date" to "Дата производства",
        "production date" to "Дата производства")

    const val PRODUCT_ID = "product_id"
    const val PRODUCT_TITLE = "description"
    const val PRODUCT_PRICE = "price"
    const val PRODUCT_QUANTITY = "quantity"

}