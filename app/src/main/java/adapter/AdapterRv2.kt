package adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.StockTaking.model.ModelJobListSp
import com.example.stockauditwayinfotech.R
import com.example.stockauditwayinfotech.model.ModelMatOutStore
import com.example.stockauditwayinfotech.model.ModelMatSup
import com.example.stockauditwayinfotech.model.ModelRVList
import com.example.stockauditwayinfotech.model.ModelRemain


class AdapterRv2(
    val context: Context,
    val remainList2: ArrayList<ModelMatOutStore>
):
    RecyclerView.Adapter<AdapterRv2.MyViewHolder>()

{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.rv_item_two,parent,false))

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.ctwo.text= "${position+1}"
        holder.bartwo.text=remainList2[position].Barcode2





    }
    override fun getItemCount(): Int {
        return  remainList2.size
    }


    open class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val ctwo: TextView = itemView.findViewById(R.id.c_2)
        val bartwo:TextView=itemView.findViewById(R.id.bar_2)



    }

}