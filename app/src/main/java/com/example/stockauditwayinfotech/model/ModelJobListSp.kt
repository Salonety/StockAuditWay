package com.StockTaking.model

data class ModelJobListSp(
    val Id: String,
    val JobNumber:String,
    ) {


    override fun toString(): String {
        return JobNumber
    }
}