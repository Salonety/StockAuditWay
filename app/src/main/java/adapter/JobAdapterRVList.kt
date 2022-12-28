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
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.jobcreationrv_item,parent,false))

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.serialNo.text= "${position+1}"
        holder.JobNumber.text= jobListSp[position].JobNumber
        holder.jobCreateOn.text=jobListSp[position].datetime




    }
    override fun getItemCount(): Int {
        return  jobListSp.size
    }


    open class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val serialNo: TextView = itemView.findViewById(R.id.tvCountStockve)
        val JobNumber:TextView=itemView.findViewById(R.id.tvSerialNumberStockve)
        val jobCreateOn:TextView=itemView.findViewById(R.id.jobCreateOn)



    }

}