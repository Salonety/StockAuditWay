package adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.StockTaking.model.ModelJobListSp
import com.example.stockauditwayinfotech.R
import com.example.stockauditwayinfotech.model.ModelMatSup
import com.example.stockauditwayinfotech.model.ModelRVList
import com.example.stockauditwayinfotech.model.ModelRemain


class AdapterRv1(
    val context: Context,
    val rvListone: ArrayList<ModelMatSup>
):
    RecyclerView.Adapter<AdapterRv1.MyViewHolder>()

{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.rv_item_one,parent,false))

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.cone.text= "${position+1}"
        holder.barone.text=rvListone[position].Barcode1





    }
    override fun getItemCount(): Int {
        return  rvListone.size
    }


    open class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val cone: TextView = itemView.findViewById(R.id.c_1)
        val barone:TextView=itemView.findViewById(R.id.bar_1)



    }

}