package activity

import adapter.AdapterRVList
import adapter.JobAdapterRVList
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.StockTaking.connection.SessionManager
import com.StockTaking.model.ModelJobListSp
import com.StockTaking.other.MethodFactory
import com.crm_copy.connection.ConnectionClass
import com.example.stockauditwayinfotech.databinding.ActivityJobCreationBinding
import com.example.stockauditwayinfotech.model.ModelRVList
import kotlinx.android.synthetic.main.activity_job_creation.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.imgback
import java.sql.Connection
import java.sql.Statement
import java.text.SimpleDateFormat
import java.util.*

class JobCreation : AppCompatActivity() {
    private var JobNumber= ""
    private val context: Context = this@JobCreation
    private lateinit var connectionClass: ConnectionClass
    private lateinit var binding: ActivityJobCreationBinding
    private lateinit var statement: Statement
    private lateinit var sessionManager: SessionManager
    private var dateorg = ""
    var progressDialog: ProgressDialog? =null
    private val jobList = ArrayList<ModelJobListSp>()
    private var conn: Connection? = null
    private lateinit var con: Connection
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobCreationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //connection class
        sessionManager = SessionManager(this)
        connectionClass = ConnectionClass(context)
        //for date and Time
        dateorg = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        job_date.text = dateorg
        //for back button
        imgback.setOnClickListener {
            onBackPressed()
        }
        //reset button
        tv_JReset.setOnClickListener {
            overridePendingTransition(0, 0)
            finish()
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

     //Progress
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
            progressDialog!!.dismiss()
             }, 200)



        binding.edtJobNumber.setOnKeyListener { view, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                return@setOnKeyListener false
            }
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                JobNumber = binding.edtJobNumber.text.toString()
                checkIJobNumber(JobNumber)
                //setRecyclerJob(JobNumber)
                return@setOnKeyListener true
            }
            false
        }
//        binding.btnSaveDataJob.setOnClickListener {
//            InsertJob()
//        }

    }//On create ends here

    private fun checkIJobNumber(jobNumber: String) {

        try {
            val query =
                "select jobnumber from JobCreation where JobNumber=('${jobNumber}')"
            val resultSet = statement.executeQuery(query)
            if (resultSet.next()) {
                //validation part
                val jobNumber = resultSet.getString("JobNumber")

                if (jobNumber==JobNumber)
                {
                    MethodFactory.myToast(this, "Job Number already exists")

                }
                else
                {

                    MethodFactory.myToast(this, "yes Item Code")

                }
            }
            else {
                setRecyclerJob(JobNumber)

                InsertJob()
                binding.edtJobNumber.text.clear()


            }

        } catch (se: Exception)
        {
            Log.e("ERROR", se.message!!)
        }


    }

    private fun setRecyclerJob(jobNumber: String) {
        jobList.add(0, ModelJobListSp("", jobNumber,dateorg))
        binding.JobrecyclerView.adapter= JobAdapterRVList(this,jobList)
        Job_count.text=jobList.size.toString()

    }

    private fun InsertJob() {
        for (itemDetail in jobList)
        {
            try {
                var n = 0
                val sql =
                    "INSERT INTO JobCreation (JobNumber,CreatedOn) VALUES (?,?)"
                val statement = con.prepareStatement(sql)
                statement.setString(1, itemDetail.JobNumber)//JobNumber
                statement.setString(2, itemDetail.datetime)//CreatedOn

                n = statement.executeUpdate()
                if (n > 0) {
                    Toast.makeText(this, "successfully Inserted", Toast.LENGTH_SHORT).show()
                } else Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            } catch (e: java.lang.Exception) {
                Log.e(ContentValues.TAG, "error: " + e.message)
                e.printStackTrace()
            }
        }

    }


}



