package adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.stockauditwayinfotech.R
import com.example.stockauditwayinfotech.model.ModelRVList


class AdapterRVList(
    val context: Context,
    val list: ArrayList<ModelRVList>
):
    RecyclerView.Adapter<AdapterRVList.MyViewHolder>()

{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.stockauditrv_item,parent,false))

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //holder.serialNo.text= "${position+1}"
       // holder.itemName.text= list[position].itemName
        holder.qty.text= list[position].qty
        holder.barCode.text= list[position].barCode


    }
    override fun getItemCount(): Int {
        return list.size
    }


    open class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        //val serialNo: TextView = itemView.findViewById(R.id.tvSrNoRv)
        //val itemName: TextView = itemView.findViewById(R.id.tvItemNameRv)
        val qty: TextView = itemView.findViewById(R.id.edtQty)
        val barCode: TextView = itemView.findViewById(R.id.txt_barcode)



    }

}