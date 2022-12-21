package com.StockTaking.model

import java.io.Serializable

data class ModelJobList(
    val Id:Int,
    val JobNumber:String,

    ) {
    override fun toString(): String {
        return JobNumber
    }
}