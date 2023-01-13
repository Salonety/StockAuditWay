package activity

import adapter.PaginationCallBack
import adapter.PaginationScrollListener
import adapter.RemainRVList
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isEmpty
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.StockTaking.connection.SessionManager
import com.StockTaking.model.ModelJobListSp
import com.crm_copy.connection.ConnectionClass
import com.example.stockauditwayinfotech.databinding.ActivityRemainbarcodeBinding
import com.example.stockauditwayinfotech.model.ModelRemain
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_remainbarcode.*
import java.sql.Connection
import java.sql.Statement


class Remainbarcode : AppCompatActivity(){
    private val context: Context = this@Remainbarcode
    private lateinit var connectionClass: ConnectionClass
    private lateinit var binding: ActivityRemainbarcodeBinding
    private lateinit var con: Connection
    private lateinit var statement: Statement
    private lateinit var sessionManager: SessionManager
    private var rem = ""
    private lateinit var conn: Connection
    private val remainList = ArrayList<ModelRemain>()
    private val jobList = ArrayList<ModelJobListSp>()
     private var jobNumber=""
    var progressDialog: ProgressDialog? = null
    var isLastPage: Boolean = false
    var isLoading: Boolean = false
    private var anext=1
    private var bprev=100


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRemainbarcodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        connectionClass = ConnectionClass(context)
        if (connectionClass.CONN() != null) {
            conn = connectionClass.CONN()!!
        }
        //connection class
        sessionManager = SessionManager(this)
        connectionClass = ConnectionClass(context)
        //progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Loading..")
        progressDialog!!.setTitle("Please Wait")
        progressDialog!!.isIndeterminate = false
        progressDialog!!.setCancelable(true)
        progressDialog!!.show()

        //handler
        Handler(mainLooper).postDelayed({
            con = connectionClass.CONN()!!
            statement = con.createStatement()
            getjob() }, 200)


     //for back Button
        imgback_r.setOnClickListener {
            onBackPressed()
        }


        //for prev button
//        prev.setOnClickListener {
//            remainList.clear()
//            bprev+100
//            process()
//
//        }

        //for next button
        nxt.setOnClickListener {
          var xy=  anext+100
           var ab= bprev+100
            getremainlist(jobNumber,xy,ab)
        }




        //function
        getjob()
        process()

    }





    private fun process() {
        spinner_rem.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @SuppressLint("SuspiciousIndentation")
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                if (jobList.size > 0) {
                    jobNumber = jobList[i].JobNumber
                    remainList.clear()
                    getremainlist(jobNumber,anext,bprev)
                }


            }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {
            }
        }
    }

    private fun getjob() {
        if (::statement.isInitialized) {
            val quary = "select * from JobCreation"
            val resultSet = statement.executeQuery(quary)
            if (!resultSet.next()) {
                Log.e(ContentValues.TAG, "EmptyListData:")
                jobList.clear()
            } else {
                jobList.add(0, ModelJobListSp("", "", ""))
                do {
                    if (resultSet.getString("JobNumber") != "")
                        jobList.add(
                            ModelJobListSp(
                                resultSet.getString("Id"),
                                resultSet.getString("JobNumber"),
                                resultSet.getString("CreatedOn")))
                    //.text=jobList.size.toString()
                } while (resultSet.next())
                spinner_rem.adapter = ArrayAdapter<ModelJobListSp>(
                    context,
                    android.R.layout.simple_list_item_1,
                    jobList
                )

            }
        }
    }


    @SuppressLint("SuspiciousIndentation")
    private fun getremainlist(jobNumber:String,anext:Int,bprev:Int) {

        if (::conn.isInitialized) {
        val stmt = conn.prepareCall("{call GetRemainingBarCode(?,?,?)}")
            stmt?.setString(1,jobNumber)
            stmt?.setInt(2,anext)
            stmt?.setInt(3,bprev)


        val rs = stmt.executeQuery()
        if (rs.next()) {
            do {
                val remain = rs.getString("COL1")
                   progressDialog!!.show()
                   remainList.add(ModelRemain(remain))
                    progressDialog!!.dismiss()
            } while (rs.next())
            progressDialog!!.show()
            remain_recyclerView.adapter = RemainRVList(this, remainList)
            remain_count.text = remainList.size.toString()

        }
            progressDialog!!.dismiss()

        }



    }




}


