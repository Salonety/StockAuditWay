package com.StockTaking.model

data class ModelJobListSp(
    val Id: String,
    val JobNumber:String,
    val datetime:String,
    ) {


    override fun toString(): String {
        return JobNumber
    }
}