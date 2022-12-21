package com.StockTaking.model

import java.io.Serializable

data class ModelCoustmerList(
    val customerName:String,

    ) {
    override fun toString(): String {
        return customerName
    }
}