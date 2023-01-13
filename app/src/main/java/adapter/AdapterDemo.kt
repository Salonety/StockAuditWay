package adapter

import activity.test_activity
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.StockTaking.connection.SessionManager
import com.crm_copy.connection.ConnectionClass
import com.example.stockauditwayinfotech.R
import com.example.stockauditwayinfotech.databinding.ActivityTestBinding
import com.example.stockauditwayinfotech.model.Demo
import kotlinx.android.synthetic.main.stockauditrv_item.view.*
import java.sql.Connection
import java.sql.Statement


class AdapterDemo(


    val context: Context, val rvtwoList: ArrayList<Demo>): RecyclerView.Adapter<AdapterDemo.MyViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.stockauditrv_item,parent,false))

    }

    var codey:Int=0




    override fun getItemCount(): Int {
        return rvtwoList.size
    }




    open class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    {
        val serialNo: TextView = itemView.findViewById(R.id.tvCountStockve)
        //val itemName: TextView = itemView.findViewById(R.id.tvItemNameRv)
        val qty: TextView = itemView.findViewById(R.id.adp_qty)
        val barCode: TextView = itemView.findViewById(R.id.adt_barcode)
        val date: TextView = itemView.findViewById(R.id.adt_date)
        val JobNum:TextView=itemView.findViewById(R.id.JobNum)
        val del:ImageView=itemView.findViewById(R.id.delete_txt)





    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.serialNo.text= "${position+1}"
        // holder.itemName.text= list[position].itemName
        holder.JobNum.text= rvtwoList[position].joblist
        holder.qty.text= rvtwoList[position].qty
        holder.barCode.text= rvtwoList[position].barCode
        holder.date.text= rvtwoList[position].datetime
        holder.del.delete_txt.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Are you sure you want to Delete?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->
                    //Toast.makeText(context,"deleted successfully",Toast.LENGTH_SHORT).show()


                    // Delete selected note from list

                       rvtwoList.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, rvtwoList.size)


                    (context as test_activity).rid()
                    context.dele(codey)



                }
                .setNegativeButton("No") { dialog, id ->
                    // Dismiss the dialog
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()

        }


    }





}