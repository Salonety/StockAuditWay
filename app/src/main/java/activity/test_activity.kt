package activity

import adapter.AdapterRVList
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.StockTaking.connection.SessionManager
import com.StockTaking.model.ModelJobListSp
import com.StockTaking.other.MethodFactory.Companion.myToast
import com.crm_copy.connection.ConnectionClass
import com.example.stockauditwayinfotech.R
import com.example.stockauditwayinfotech.databinding.ActivityTestBinding
import com.example.stockauditwayinfotech.model.ModelRVList
import kotlinx.android.synthetic.main.activity_main.add_qty
import kotlinx.android.synthetic.main.activity_main.edtscanbarcode
import kotlinx.android.synthetic.main.activity_main.imgback
import kotlinx.android.synthetic.main.activity_main.sp_jobnum
import kotlinx.android.synthetic.main.activity_main.spinner
import kotlinx.android.synthetic.main.activity_main.tvReset
import kotlinx.android.synthetic.main.activity_test.*
import java.sql.Connection
import java.sql.Statement
import java.text.SimpleDateFormat
import java.util.*

class test_activity : AppCompatActivity() {
    private val context: Context = this@test_activity
    private lateinit var connectionClass: ConnectionClass
    private lateinit var binding: ActivityTestBinding
    private var conn: Connection? = null
    private lateinit var con: Connection
    private lateinit var statement: Statement
    private val scanByList = ArrayList<String>()
    private lateinit var sessionManager: SessionManager
    var progressDialog: ProgressDialog? = null
    private val rvList = ArrayList<ModelRVList>()
    private var barCode = ""
    private var scanBy = ""
    private var qty = ""
    private var dateorg = ""
    private val jobList = ArrayList<ModelJobListSp>()
    private var jobNumber=""
    private var jobtotalqty=""
    var list_of_items = arrayOf("Automatic", "Manual")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //connection class
        sessionManager = SessionManager(this)
        connectionClass = ConnectionClass(context)

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
//     //to insert data in table
//        binding.btnSaveData.setOnClickListener {
//            insertItemDetails()
//        }



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
            insertItemDetails()

        }


        //bar code
        binding.edtscanbarcode.setOnKeyListener { view, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                return@setOnKeyListener false
            }
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                barCode = binding.edtscanbarcode.text.toString()
                checkISerialNumber(barCode)
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
        checkISerialNumberty(barCode)
        fetchjobdatasp()
        focus()
        mode()
        //totalQtyty()

    }//onCreate ends here

//    private fun totalQtyty() {
//        val qry= "select sum(cast(col2 as int)) as Qty from StockAudit where JobNumber=('${jobNumber}')"
//        val resultSet= statement.executeQuery(qry)
//        val code = resultSet.getString("JobNumber")
//
//
//
//
//    }


    /*private fun totalQtyty() {
          val str= "select sum(cast(col2 as int)) as Qty from StockAudit where JobNumber=('${jobNumber}')"
          val resultSet= statement.executeQuery(str)
            val code = resultSet.getString("JobNumber")
        val myStr = sp_jobnum.selectedItem.toString()
        if (myStr==code){
            binding.TotalQty.setText(myStr)

        }
    }*/


    //for manual check
    private fun checkISerialNumberty(barCode: String) {
        try {
            val query =
                " select COL1 from stockaudit where  COL1=('${barCode}')"
            val resultSet = statement.executeQuery(query)
            if (resultSet.next()) {
                //validation part
                val code = resultSet.getString("COL1")
                if (rvList.size > 0) {
                    var isTrue = false
                    val iterator = rvList.iterator()
                    while (iterator.hasNext()) {
                        var itemIterator = iterator.next()
                        if (itemIterator.Scanby == code) {
                            isTrue = true
                            //iterator.remove()
                            myToast(this, "Barcode Already Scan")
                        }
                    }
                    if (!isTrue) {
                        if (sp_jobnum.getSelectedItem() != null) {
                            sp_jobnum.getSelectedItem()
                        } else {
                            myToast(this, "Please enter Job Number")
                        }

                        qty = binding.edtQtymanual.text.toString().trim()
                        if (qty == "") {
                            rvList.add(0, ModelRVList("1", jobNumber, barCode, dateorg))
                            binding.rvrecyclerView.adapter = AdapterRVList(this, rvList)
                            binding.tvcoutn.text = rvList.size.toString()
                            binding.edtQtymanual.text.clear()
                        }
                        else{
                            rvList.add(0, ModelRVList(qty, jobNumber, barCode, dateorg))
                            binding.rvrecyclerView.adapter = AdapterRVList(this, rvList)
                            binding.tvcoutn.text = rvList.size.toString()
                        }
                        binding.edtscanbarcodeman.text.clear()

                    }
                }
                else{
                    if (sp_jobnum.getSelectedItem() != null) {
                        sp_jobnum.getSelectedItem()
                    } else {
                        myToast(this, "Please enter Job Number")
                    }

                    qty = binding.edtQtymanual.text.toString().trim()
                    if (qty == "") {
                        rvList.add(0, ModelRVList("1", jobNumber, barCode, dateorg))
                        binding.rvrecyclerView.adapter = AdapterRVList(this, rvList)
                        binding.tvcoutn.text = rvList.size.toString()
                        binding.edtQtymanual.text.clear()
                    }
                    else{
                        rvList.add(0, ModelRVList(qty, jobNumber, barCode, dateorg))
                        binding.rvrecyclerView.adapter = AdapterRVList(this, rvList)
                        binding.tvcoutn.text = rvList.size.toString()
                    }
                    binding.edtscanbarcodeman.text.clear()
                }
                binding.edtscanbarcodeman.text?.clear()
                Log.e(ContentValues.TAG,"code$code")
            }
            else {
                myToast(this, "Invalid Item Code")
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
                    "INSERT INTO StockTaking (JobNumber,BarCode,Qty,CreatedOn) VALUES (?,?,?,?)"
                val statement = con.prepareStatement(sql)
                statement.setString(1, itemDetail.joblist)//JobNumber
                statement.setString(2, itemDetail.Scanby)//BarCode
                statement.setString(3, itemDetail.qty.toString())//Qty
                statement.setString(4, itemDetail.datetime)//CreatedOn

                n = statement.executeUpdate()
                if (n > 0) {
                    SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Save successfully !")
                        .setConfirmText("Ok")
                        .setConfirmClickListener { obj: SweetAlertDialog ->
                            obj.dismiss()
                            finish()
                        }
                        .show()
                    Toast.makeText(this, "successfully Inserted", Toast.LENGTH_SHORT).show()
                } else Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
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
    private fun checkISerialNumber(barCode: String) {
        try {
            val query =
                " select COL1 from stockaudit where  COL1=('${barCode}')"
            val resultSet = statement.executeQuery(query)
            if (resultSet.next()) {
                //validation part
                val code = resultSet.getString("COL1")
                if (rvList.size > 0) {
                        var isTrue = false
                        val iterator = rvList.iterator()
                        while (iterator.hasNext()) {
                            var itemIterator = iterator.next()
                            if (itemIterator.Scanby == code) {
                                isTrue = true
                                //iterator.remove()
                                myToast(this, "Barcode Already Scan")

                            }
                        }
                    if (!isTrue) {
                        setRecyclerData(barCode)
                        insertItemDetails()
                    }
                    }
                    else{
                        setRecyclerData(barCode)
                    insertItemDetails()
                    }
                binding.edtscanbarcode.text?.clear()
                Log.e(ContentValues.TAG,"code$code")
            }
            else {
                myToast(this, "Invalid Item Code")
                binding.edtscanbarcode.text.clear()
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
        rvList.add(0, ModelRVList("1", jobNumber, barCode, dateorg))
        binding.rvrecyclerView.adapter = AdapterRVList(this, rvList)
        binding.tvcoutn.text = rvList.size.toString()

    }
    //for fetching jobs in spinner
    private fun getJobNumber() {
        val quary = "select * from JobCreation"
        val resultSet = statement.executeQuery(quary)
        if (!resultSet.next()) {
            Log.e(ContentValues.TAG, "EmptyListData:")

            jobList.clear()
        } else {
            jobList.add(0, ModelJobListSp("","Select Job Number",""))
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
    private fun fetchjobdatasp() {
        //for spinner
        sp_jobnum.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                val myStr = sp_jobnum.selectedItem.toString()
                   binding.TotalQty.setText(myStr)

                if (jobList.size > 0) {
                    jobNumber = jobList[i].JobNumber
                    Log.e(ContentValues.TAG, "type: $jobNumber")

                    binding.edtscanbarcode.requestFocus()
                    binding.edtscanbarcodeman.requestFocus()
                }
            }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {

                if(sp_jobnum.getSelectedItem() !=null ) {
                    Toast.makeText(getApplicationContext(),"please select Job number",Toast. LENGTH_SHORT);

                }


            }
        }
    }



}