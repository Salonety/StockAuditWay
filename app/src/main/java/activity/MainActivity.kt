package activity

import adapter.AdapterRVList
import android.R
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
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
import com.example.stockauditwayinfotech.databinding.ActivityMainBinding
import com.example.stockauditwayinfotech.model.ModelRVList
import kotlinx.android.synthetic.main.activity_main.*
import java.sql.Connection
import java.sql.Statement
import java.text.SimpleDateFormat
import java.util.*



class MainActivity : AppCompatActivity() {
    private val context: Context = this@MainActivity
    private lateinit var connectionClass: ConnectionClass
    private lateinit var binding: ActivityMainBinding
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
    var list_of_items = arrayOf("Automatic", "Manual")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
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
                   }
                   if(scanBy=="Manual")
                   {
                       binding.edtQty.visibility=View.GONE
                       binding.edtQtymanual.visibility=View.VISIBLE
                       binding.addQty.visibility=View.VISIBLE
                       binding.edtscanbarcode.text.clear()

                   }




               }
            }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {
            }
        }
        //for date and Time
        dateorg = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(Date())
        binding.tvDate.text = dateorg

      //for spinner
        sp_jobnum.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
/*
                if(sp_jobnum.getSelectedItem() !=null ) {
                    Toast.makeText(getApplicationContext()," please select Job number",Toast. LENGTH_SHORT);

                }
*/
                if (jobList.size > 0) {
                    jobNumber = jobList[i].JobNumber
                    Log.e(ContentValues.TAG, "type: $jobNumber")
                }
            }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {

            }
        }
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

        //for qty
            binding.edtQty.setText("1")


       //bar code
        binding.edtscanbarcode.setOnKeyListener { view, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                return@setOnKeyListener false
            }
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                barCode = binding.edtscanbarcode.text.toString()
                checkISerialNumber()
                //checkDupli()
                return@setOnKeyListener true
            }
            false
        }


        //add qty button
          add_qty.setOnClickListener {
          qty = binding.edtQtymanual.text.toString().trim()
          rvList.add(0, ModelRVList(qty, jobNumber, barCode, dateorg))
          binding.rvrecyclerView.adapter = AdapterRVList(this, rvList)
          binding.tvcoutn.text = rvList.size.toString()



      }

  // edt qty focus
        edtQtymanual.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
            {
                edtQty.setBackgroundResource(R.drawable.spinner_background)
            }
            if (!hasFocus)
            {
                edtQty.setBackgroundColor(R.drawable.spinner_dropdown_background)
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


        binding.btnSaveData.setOnClickListener {
            insertItemDetails()
        }
//mode spinner
         mode()
        focus()


    }//on create ends here

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
        edtQty.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
            {
                edtQty.setBackgroundResource(android.R.drawable.spinner_background)
            }
            if (!hasFocus)
            {
                edtQty.setBackgroundColor(android.R.drawable.spinner_dropdown_background)
            }

        }
    }




    //for switching automatic to manual
    private fun mode() {
        spinner!!.onItemSelectedListener
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, list_of_items)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner!!.setAdapter(aa)
    }
    // for inserting into stocktaking table in databse
    private fun insertItemDetails() {
        for (itemDetail in rvList)
        {
            try {
                var n = 0
                val sql =
                    "INSERT INTO StockTaking (JobNumber,BarCode,Qty,CreatedOn) VALUES (?,?,?,?)"
                val statement = con.prepareStatement(sql)
                statement.setString(1, itemDetail.joblist)//JobNumber
                statement.setString(2, itemDetail.barCode)//BarCode
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
    //for getting job num in spinner
    private fun getJobNumber() {
       val quary = "select * from JobCreation"
    val resultSet = statement.executeQuery(quary)
        if (!resultSet.next()) {
           Log.e(TAG, "EmptyListData:")

         jobList.clear()
     } else {
           jobList.add(0, ModelJobListSp("","Select Job Number",""))
          do {
              if (resultSet.getString("JobNumber") != "")
                jobList.add(ModelJobListSp(resultSet.getString("Id"),resultSet.getString("JobNumber"),resultSet.getString("CreatedOn")))
              total_qty.text=jobList.size.toString()
            } while (resultSet.next())
           sp_jobnum.adapter = ArrayAdapter<ModelJobListSp>(context, R.layout.simple_list_item_1, jobList)
             progressDialog!!.dismiss()
       }

        }

    //validation from Serialnumber
    private fun checkISerialNumber() {
        try {
            val query =
                " select COL1 from stockaudit where  COL1=('${barCode}')"

            val resultSet = statement.executeQuery(query)
            if (resultSet.next()) {
                //validation part
                val code = resultSet.getString("COL1")
                if (code==barCode){
                    myToast(this,"valid Item Code")
                    enterClick()
                    setRecyclerSam(barCode)
                    //binding.edtQty.requestFocus()
                    binding.edtQtymanual.requestFocus()
                    //binding.edtscanbarcode.requestFocus()
                }
                Log.e(ContentValues.TAG,"code$code")
            }
            else {
                myToast(this,"Invalid Item Code")
                binding.edtscanbarcode.text.clear()
            }

        } catch (se: Exception)
        {
            Log.e("ERROR", se.message!!)
        }


    }

    private fun enterClick() {
        binding.edtscanbarcode.requestFocus()


        if (rvList.size > 0) {
            var isTrue = false
            val iterator = rvList.iterator()
            while (iterator.hasNext()) {
                var itemIterator = iterator.next()

                if (itemIterator.barCode== barCode) {
                    iterator.remove()
                    isTrue = true
                    myToast(this, "Barcode Already Scan")
                }
            }
            if (!isTrue){

                setRecyclerSam(barCode)
            }
        }else{
            setRecyclerSam(barCode)

        }
        binding.edtscanbarcode.text?.clear()
    }

    //setting data in recylerview
    private fun setRecyclerSam(barCode: String) {
        if (sp_jobnum.getSelectedItem() != null) {
            sp_jobnum.getSelectedItem()
        } else {
            myToast(this,"Please enter Job Number")
        }
        rvList.add(0, ModelRVList(qty, jobNumber, barCode, dateorg))
        binding.rvrecyclerView.adapter = AdapterRVList(this, rvList)
        binding.tvcoutn.text = rvList.size.toString()



    }
}   //end of class










