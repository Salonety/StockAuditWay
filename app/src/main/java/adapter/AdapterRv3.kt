package adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.StockTaking.model.ModelJobListSp
import com.example.stockauditwayinfotech.R
import com.example.stockauditwayinfotech.model.*


class AdapterRv3(
    val context: Context,
    val remainList3: ArrayList<ModelMatInStore>
):
    RecyclerView.Adapter<AdapterRv3.MyViewHolder>()

{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.rv_item_three,parent,false))

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.cthree.text= "${position+1}"
        holder.barthree.text=remainList3[position].Barcode3





    }
    override fun getItemCount(): Int {
        return  remainList3.size
    }


    open class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val cthree: TextView = itemView.findViewById(R.id.c_3)
        val barthree:TextView=itemView.findViewById(R.id.bar_3)



    }

}