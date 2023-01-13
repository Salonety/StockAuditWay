package adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.stockauditwayinfotech.R
import com.example.stockauditwayinfotech.model.ModelRemain


class RemainRVList(
    val context: Context,
    val remainList: ArrayList<ModelRemain>
):
    RecyclerView.Adapter<RemainRVList.MyViewHolder>()

{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.remain_item,parent,false))

    }



    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.rema.text= "${position+1}"
        holder.reml.text=remainList[position].remain






    }
    override fun getItemCount(): Int {
        return  remainList.size

    }


    open class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val rema: TextView = itemView.findViewById(R.id.remain_c)
        val reml:TextView=itemView.findViewById(R.id.remain_l)



    }



}




