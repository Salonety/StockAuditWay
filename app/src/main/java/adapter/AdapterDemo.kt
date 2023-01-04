package adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.stockauditwayinfotech.R
import com.example.stockauditwayinfotech.model.Demo
import com.example.stockauditwayinfotech.model.ModelRVList


class AdapterDemo(
    val context: Context,
    val rvtwoList: ArrayList<Demo>
):
    RecyclerView.Adapter<AdapterDemo.MyViewHolder>()

{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.stockauditrv_item,parent,false))

    }


    override fun getItemCount(): Int {
        return rvtwoList.size
    }


    open class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val serialNo: TextView = itemView.findViewById(R.id.tvCountStockve)
        //val itemName: TextView = itemView.findViewById(R.id.tvItemNameRv)
        val qty: TextView = itemView.findViewById(R.id.adp_qty)
        val barCode: TextView = itemView.findViewById(R.id.adt_barcode)
        val date: TextView = itemView.findViewById(R.id.adt_date)
        val JobNum:TextView=itemView.findViewById(R.id.JobNum)



    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.serialNo.text= "${position+1}"
        // holder.itemName.text= list[position].itemName
        holder.JobNum.text= rvtwoList[position].joblist
        holder.qty.text= rvtwoList[position].qty.toString()
        holder.barCode.text= rvtwoList[position].barCode
        holder.date.text= rvtwoList[position].datetime


    }

}