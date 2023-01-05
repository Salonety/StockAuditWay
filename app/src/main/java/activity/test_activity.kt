package activity

import adapter.AdapterDemo
import adapter.AdapterRVList
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.StockTaking.connection.SessionManager
import com.StockTaking.model.ModelJobListSp
import com.StockTaking.other.MethodFactory.Companion.myToast
import com.crm_copy.connection.ConnectionClass
import com.example.stockauditwayinfotech.R
import com.example.stockauditwayinfotech.databinding.ActivityTestBinding
import com.example.stockauditwayinfotech.model.Demo
import com.example.stockauditwayinfotech.model.ModelRVList
import kotlinx.android.synthetic.main.activity_main.add_qty
import kotlinx.android.synthetic.main.activity_main.edtscanbarcode
import kotlinx.android.synthetic.main.activity_main.imgback
import kotlinx.android.synthetic.main.activity_main.sp_jobnum
import kotlinx.android.synthetic.main.activity_main.spinner
import kotlinx.android.synthetic.main.activity_main.tvReset
import kotlinx.android.synthetic.main.activity_test.*
import kotlinx.android.synthetic.main.stockauditrv_item.*
import java.sql.Connection
import java.sql.Statement
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class test_activity : AppCompatActivity() {
    private val context: Context = this@test_activity
    private lateinit var connectionClass: ConnectionClass
    private lateinit var binding: ActivityTestBinding
    private lateinit var con: Connection
    private lateinit var statement: Statement
    private lateinit var sessionManager: SessionManager
    var progressDialog: ProgressDialog? = null
    private val rvList = ArrayList<ModelRVList>()
   private val rvtwoList= ArrayList<Demo>()
    private var barCode = ""
    private var scanBy = ""
    private var code=0
    private var qty = ""
    private var dateorg = ""
    private var gg = ""
    private var  addd = 0
    private val jobList = ArrayList<ModelJobListSp>()
    private var jobNumber=""
    var list_of_items = arrayOf("Automatic", "Manual")




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //connection class
        sessionManager = SessionManager(this)
        connectionClass = ConnectionClass(context)

        dele(code)
        //auto manual spinner
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                if (list_of_items.size > 0)
                {
                    scanBy=list_of_items[i]

                    if(scanBy=="Automatic")
                    {
                        binding.edtQty.visibility=View.VISIBLE
                        binding.edtQtymanual.visibility=View.GONE
                        binding.addQty.visibility=View.GONE
                        binding.edtQty.text.clear()
                        binding.edtQtymanual.text.clear()
                        binding.edtscanbarcode.visibility=View.VISIBLE
                        binding.edtscanbarcodeman.visibility=View.GONE
                    }
                    if(scanBy=="Manual")
                    {
                        binding.edtscanbarcode.text.clear()
                        binding.edtQtymanual.text.clear()
                        binding.edtQty.visibility=View.GONE
                        binding.edtQtymanual.visibility=View.VISIBLE
                        binding.addQty.visibility=View.VISIBLE
                        binding.edtscanbarcode.visibility=View.GONE
                        binding.edtscanbarcodeman.visibility=View.VISIBLE

                    }

                } }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {
            }
        }
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
            getJobNumber() }, 200)
        //for date and Time
        dateorg = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(Date())
        binding.tvDate.text = dateorg

        //for back Button
        imgback.setOnClickListener {
            onBackPressed()
        }
        //reset button
        tvReset.setOnClickListener {
            overridePendingTransition(0, 0)
            finish()
            startActivity(intent)
            overridePendingTransition(0, 0)
        }


        //add qty button
        add_qty.setOnClickListener {
            barCode = binding.edtscanbarcodeman.text.toString()
            checkISerialNumberty(barCode)

        }


        //bar code
        binding.edtscanbarcode.setOnKeyListener { view, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                return@setOnKeyListener false
            }
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                barCode = binding.edtscanbarcode.text.toString()
                checkISerialNumber(barCode,jobNumber)
                return@setOnKeyListener true
            }
            false
        }
        binding.edtscanbarcodeman.setOnKeyListener { view, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                return@setOnKeyListener false
            }

            false
        }



      //functions
        fetchjobdatasp()
        focus()
        mode()
        rid()




    }//onCreate ends here

    fun rid() {
        if (::statement.isInitialized) {
            try {
                val quary = //"select Id from StockTaking where id=('${ide}')"
                    " Select Id from StockTaking Where JobNumber = ('${jobNumber}') and BarCode =('${barCode}')"
                val resultSet = statement.executeQuery(quary)
                if (resultSet.next()) {
                    val code = resultSet.getInt("Id")

                    dele(code)


                }
            } catch (e: java.lang.Exception) {
                Log.e(ContentValues.TAG, "error: " + e.message)
                e.printStackTrace()
            }
        }

    }

    fun dele(code: Int) {
         if (::statement.isInitialized) {
             try {
                 val quary = "delete from StockTaking where id=('${code}')"
                 val resultSet = statement.executeQuery(quary)
                 if (resultSet.next()) {
                     //val code = resultSet.getInt("Id")

                     Toast.makeText(context, "deleted successfully", Toast.LENGTH_SHORT).show()
                 }
             }
             catch (e: java.lang.Exception) {
                 Log.e(ContentValues.TAG, "error: " + e.message)
                 e.printStackTrace()
             }
         }

    }


    //for manual check
    private fun checkISerialNumberty(barCode: String) {
        try {
            val query =
                "select COL1 from StockAudit   where JobNumber=('${jobNumber}') and COL1=('${barCode}')"
            val resultSet = statement.executeQuery(query)
            if (resultSet.next()) {
                //validation part
                val code = resultSet.getString("COL1")
                if (code == barCode) {
                    //myToast(this, "valid Item Code")
                    checkDataCodeMan(code)
                    binding.edtQtymanual.requestFocus()
                    binding.edtscanbarcodeman.text.clear()
                }
                Log.e(ContentValues.TAG, "code$code")
            }
                else {
                val toneGen1 = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150)
                    myToast(this, "Invalid Item Code")
                setRecyx(barCode)
                getallsum()
                insertItemDetails()
                binding.edtscanbarcodeman.text.clear()

            }
        } catch (se: Exception) {
            Log.e("ERROR", se.message!!)
        }
    }
    private fun setRecyx(barCode: String) {
        qty = binding.edtQtymanual.text.toString().trim()
        if (qty == "") {
            rvList.add(0, ModelRVList( "1", jobNumber, barCode, dateorg,"N"))
            rv_trecyclerView.adapter = AdapterRVList(this, rvList)
            binding.tvcoutn.text = rvList.size.toString()

            rvtwoList.add(0, Demo("1",jobNumber,barCode,dateorg))
            binding.rvTrecyclerView.adapter=AdapterDemo(this,rvtwoList)
            binding.tvcoutn.text = rvList.size.toString()
            binding.edtQtymanual.text.clear()


        }
        else{
            rvList.add(0, ModelRVList(qty, jobNumber, barCode, dateorg,"N"))
            binding.rvrecyclerView.adapter = AdapterRVList(this, rvList)
            binding.tvcoutn.text = rvList.size.toString()

            rvtwoList.add(0, Demo(qty,jobNumber,barCode,dateorg))
            binding.rvTrecyclerView.adapter=AdapterDemo(this,rvtwoList)
            binding.tvcoutn.text = rvtwoList.size.toString()
            binding.edtQtymanual.text.clear()
        }

    }
    //for dupli in manual
    private fun checkDataCodeMan(code: String) {
        try {
            val query =
                //" select BarCode from [StockTaking] where BarCode=('${code}')"
            "select BarCode from [StockTaking] where BarCode =('${code}') and JobNumber =('${jobNumber}')"
            val resultSet = statement.executeQuery(query)

            if (resultSet.next()) {
                //validation part
                val BarCode = resultSet.getString("BarCode")
                if (BarCode == code) {
                    val toneGen1 = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
                    toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150)
                    myToast(this, "barcode already exists")
                    if (resultSet.next()){
                        val jobs = resultSet.getString("JobNumber")
                        if (jobs==code) {
                            val toneGen1 = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
                            toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150)
                            myToast(this,"barcode does not exists on this job")
                        }
                    }

                }
            }
            else {
                qty = binding.edtQtymanual.text.toString().trim()
                if (qty == "") {
                    rvList.add(0, ModelRVList("1", jobNumber, barCode, dateorg,"y"))
                    rv_trecyclerView.adapter = AdapterRVList(this, rvList)
                    binding.tvcoutn.text = rvList.size.toString()

                    rvtwoList.add(0, Demo("1",jobNumber,barCode,dateorg))
                    binding.rvTrecyclerView.adapter=AdapterDemo(this,rvtwoList)
                    binding.tvcoutn.text = rvList.size.toString()
                    binding.edtQtymanual.text.clear()


                }
                else{
                    rvList.add(0, ModelRVList(qty, jobNumber, barCode, dateorg,"Y"))
                    binding.rvrecyclerView.adapter = AdapterRVList(this, rvList)
                    binding.tvcoutn.text = rvList.size.toString()

                    rvtwoList.add(0, Demo(qty,jobNumber,barCode,dateorg))
                    binding.rvTrecyclerView.adapter=AdapterDemo(this,rvtwoList)
                    binding.tvcoutn.text = rvList.size.toString()
                    binding.edtQtymanual.text.clear()


                }
                getallsum()
                insertItemDetails()
                binding.edtscanbarcodeman.text.clear()
            }

        } catch (se: Exception)
        {
            Log.e("ERROR", se.message!!)
        }

    }
    //inserting data in database
    private fun insertItemDetails() {
        for (itemDetail in rvList)
        {
            try {
                var n = 0
                val sql =
                   // "INSERT INTO StockTaking (JobNumber,BarCode,Qty,CreatedOn) VALUES (?,?,?,?)"
                    "INSERT INTO StockTaking (JobNumber,BarCode,Qty,CreatedOn,ExistBarcode) VALUES (?,?,?,?,?)"
                val statement = con.prepareStatement(sql)
                statement.setString(1, itemDetail.joblist)//JobNumber
                statement.setString(2, itemDetail.barCode)//BarCode
                statement.setString(3, itemDetail.qty.toString())//Qty
                statement.setString(4, itemDetail.datetime)//CreatedOn
                statement.setString(5, itemDetail.yes)//for yes and no

                n = statement.executeUpdate()
                if (n > 0) {
                    rvList.clear()
                    //myToast(this, "successfully Inserted")
                }

                else Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            } catch (e: java.lang.Exception) {
                Log.e(ContentValues.TAG, "error: " + e.message)
                e.printStackTrace()
            }
        }

    }
    //switching mode
    private fun mode() {
        spinner!!.onItemSelectedListener
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, list_of_items)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner!!.setAdapter(aa)
    }
    //for focus on edittext
    private fun focus() {

        //for focus
        edtscanbarcode.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
            {
                edtscanbarcode.setBackgroundResource(android.R.drawable.spinner_background)
            }
            if (!hasFocus)
            {
                edtscanbarcode.setBackgroundColor(android.R.drawable.spinner_dropdown_background)
            }

        }

        edtscanbarcodeman.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
            {
                edtscanbarcodeman.setBackgroundResource(android.R.drawable.spinner_background)
            }
            if (!hasFocus)
            {
                edtscanbarcodeman.setBackgroundColor(android.R.drawable.spinner_dropdown_background)
            }

        }


    }
    //for auto check
    private fun checkISerialNumber(barCode: String,jobNumber:String) {
        try {
            val query = "select COL1 from StockAudit where JobNumber=('${jobNumber}') and COL1=('${barCode}')"
            val resultSet = statement.executeQuery(query)
            if (resultSet.next())
            {
                //validation part
                val code = resultSet.getString("COL1")

                if (code==barCode){
                    //myToast(this, "valid Item Code")
                    checkDataCode(code,jobNumber)
                    binding.edtQty.requestFocus()
                    binding.edtscanbarcode.text.clear()
                }

                Log.e(ContentValues.TAG, "code$code")
            }
            else {
                val toneGen1 = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150)
                myToast(this, "Invalid Item Code")
                setRecyclery(barCode)
                getallsum()
                insertItemDetails()
                binding.edtscanbarcode.text.clear()

            }
        } catch (se: Exception) {
            Log.e("ERROR", se.message!!)
        }

    }
    //for dupli in auto
    private fun checkDataCode(code: String,jobNumber:String) {
        try {
            val query = //" select BarCode from [StockTaking] where BarCode=('${code}')"
           "select BarCode from [StockTaking] where BarCode =('${code}') and JobNumber =('${jobNumber}')"
            val resultSet = statement.executeQuery(query)

            if (resultSet.next()) {
                //validation part
                val BarCode = resultSet.getString("BarCode")
                if (BarCode == code) {
                    val toneGen1 = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
                    toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150)
                    myToast(this, "barcode already exists")
                    if (resultSet.next()){
                        val jobs = resultSet.getString("JobNumber")
                        if (jobs==code) {
                            val toneGen1 = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
                            toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150)
                            myToast(this,"barcode does not exists on this job")
                        }
                    }

                }
            }
            else {
                setRecyclerData(barCode)
                getallsum()
                insertItemDetails()
                binding.edtscanbarcode.text.clear()
                //binding.totalscannedQty.text.clear()
            }

        } catch (se: Exception)
        {
            Log.e("ERROR", se.message!!)
        }
    }
    //for auto rv
    private fun setRecyclerData(barCode: String) {
        if (sp_jobnum.getSelectedItem() != null) {
            sp_jobnum.getSelectedItem()
        } else {
            myToast(this, "Please enter Job Number")
        }
        rvList.add(0, ModelRVList("1", jobNumber, barCode, dateorg,"Y"))
        binding.rvrecyclerView.adapter = AdapterRVList(this, rvList)
        binding.tvcoutn.text = rvList.size.toString()
        rvtwoList.add(0, Demo("1",jobNumber,barCode,dateorg))
        binding.rvTrecyclerView.adapter=AdapterDemo(this,rvtwoList)
        binding.tvcoutn.text=rvtwoList.size.toString()

    }
    private fun setRecyclery(barCode: String) {
        if (sp_jobnum.getSelectedItem() != null) {
            sp_jobnum.getSelectedItem()
        } else {
            myToast(this, "Please enter Job Number")
        }
        rvList.add(0, ModelRVList("1", jobNumber, barCode, dateorg,"N"))
        binding.rvrecyclerView.adapter = AdapterRVList(this, rvList)
        binding.tvcoutn.text = rvList.size.toString()
        rvtwoList.add(0, Demo("1",jobNumber,barCode,dateorg))
        binding.rvTrecyclerView.adapter=AdapterDemo(this,rvtwoList)
        binding.tvcoutn.text=rvtwoList.size.toString()

    }
    //for fetching jobs in spinner
    private fun getJobNumber() {
        val quary = "select * from JobCreation"
        val resultSet = statement.executeQuery(quary)
        if (!resultSet.next()) {
            Log.e(ContentValues.TAG, "EmptyListData:")
            jobList.clear()
        } else {
            jobList.add(0, ModelJobListSp("","",""))
            do {
                if (resultSet.getString("JobNumber") != "")
                    jobList.add(ModelJobListSp(resultSet.getString("Id"),resultSet.getString("JobNumber"),resultSet.getString("CreatedOn")))
                //.text=jobList.size.toString()
            } while (resultSet.next())
            sp_jobnum.adapter = ArrayAdapter<ModelJobListSp>(context, android.R.layout.simple_list_item_1, jobList)
            progressDialog!!.dismiss()
        }
    }
    //job spinner
    private fun fetchjobdatasp()  {
        sp_jobnum.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @SuppressLint("SuspiciousIndentation")
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                jobNumber = jobList[i].JobNumber
                val total="select sum(sa.COL2) as tot from JobCreation jc inner join StockAudit sa on  sa.JobNumber= jc.JobNumber where sa.JobNumber=('${jobNumber}')"
                val resultSet = statement.executeQuery(total)
                if (resultSet.next())
                {
                    var resk = resultSet.getInt("tot")
                    var gh=resk.toString()
                    binding.TotalQty.setText(gh)
                }

                 if (jobList.size > 0) {
                    jobNumber = jobList[i].JobNumber
                    Log.e(ContentValues.TAG, "type: $jobNumber")
                    binding.edtscanbarcodeman.isEnabled=true
                    binding.edtscanbarcodeman.requestFocus()
                    binding.edtscanbarcode.isEnabled=true
                    binding.edtscanbarcode.requestFocus()

                }
                rvtwoList.clear()
                binding.totalscannedQty.text.clear()
            }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {
            }
        }
    }
    //for total qty sum
    private fun getallsum() {
         addd = rvtwoList.sumBy { it.qty.toInt() }
         gg = addd.toString()
        totalscannedQty.setText(gg) }


} //program ends here