package adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.StockTaking.model.ModelJobListSp
import com.example.stockauditwayinfotech.R
import com.example.stockauditwayinfotech.model.ModelRVList


class JobAdapterRVList(
    val context: Context,
    val jobListSp: ArrayList<ModelJobListSp>
):
    RecyclerView.Adapter<JobAdapterRVList.MyViewHolder>()

{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.stockauditrv_item,parent,false))

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.JobNumber.text= jobListSp[position].JobNumber


    }
    override fun getItemCount(): Int {
        return  jobListSp.size
    }


    open class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val JobNumber:TextView=itemView.findViewById(R.id.JobNum)



    }

}