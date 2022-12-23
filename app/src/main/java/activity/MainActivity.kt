package activity

import adapter.AdapterRVList
import adapter.JobAdapterRVList
import android.R
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
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.StockTaking.connection.SessionManager
import com.StockTaking.model.DataModel
import com.StockTaking.model.ModelJobListSp
import com.StockTaking.other.MethodFactory.Companion.myToast
import com.crm_copy.connection.ConnectionClass
import com.example.stockauditwayinfotech.databinding.ActivityMainBinding
import com.example.stockauditwayinfotech.model.ModelRVList
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.stockauditrv_item.*
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private var itemCodeNew=""
    private val context: Context = this@MainActivity
    private lateinit var connectionClass: ConnectionClass
    private lateinit var binding: ActivityMainBinding
    private var conn: Connection? = null
    private lateinit var con: Connection
    private lateinit var statement: Statement
    private val scanByList = ArrayList<String>()
    private var itemsettt = ""
    private var formattedDate = ""
    private var itemNameNew = ""
    private lateinit var sessionManager: SessionManager
    var progressDialog: ProgressDialog? = null
    private val BarcodeList = ArrayList<DataModel>()
    private val rvList = ArrayList<ModelRVList>()



    //saloni
    private val JobNum=""
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
                   }
                   if(scanBy=="Manual")
                   {
                       binding.edtQty.visibility=View.GONE
                       binding.edtQtymanual.visibility=View.VISIBLE
                   }




               }



            //                if (putAwayList.size > 0) {
//                    purchaseNumber = putAwayList[i].purchaseNumber
//                    totalQty = putAwayList[i].totalQty
//                    accountname = putAwayList[i].accountName
//                    eamil = putAwayList[i].eamil
//                    purchaseid = putAwayList[i].id
//
//                }
            }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {
            }
        }

       //for date
//        val c = Calendar.getInstance().time
//        val df = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
//        formattedDate = df.format(c)
//        date= SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(c)

        dateorg = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(Date())
        binding.tvDate.text = dateorg
      //for spinner
        sp_jobnum.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
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

     // edtscanbarcode
        edtscanbarcode.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                edtscanbarcode.setBackgroundResource(R.drawable.spinner_background)

            }
            if (!hasFocus) {
                edtscanbarcode.setBackgroundColor((R.drawable.spinner_dropdown_background))

            }

        }

       //bar code
        binding.edtscanbarcode.setOnKeyListener { view, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                return@setOnKeyListener false
            }
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                barCode = binding.edtscanbarcode.text.toString()
                checkISerialNumber()
                return@setOnKeyListener true
            }
            false
        }
        //edtQty.setEnabled(false)



    //add qty button
      add_qty.setOnClickListener {
          barCode = binding.edtscanbarcode.text.toString()
          try {
              val query =
                  " select COL1 from stockaudit where  COL1=('${barCode}')"

              val resultSet = statement.executeQuery(query)
              if (resultSet.next()) {
                  //validation part
                  val code = resultSet.getString("COL1")

                  if (code==barCode){
                      myToast(this,"valid Item Code")
                      setRecyclerSam(barCode)
                      binding.edtQty.requestFocus()
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


          qty =binding.edtQtymanual.text.toString().trim()
          rvList.add(0, ModelRVList(qty, jobNumber,barCode,dateorg))
          binding.rvrecyclerView.adapter=AdapterRVList(this,rvList)
          binding.tvcoutn.text=rvList.size.toString()


      }

  // edt qty focus
        edtQty.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                edtQty.setBackgroundResource(R.drawable.spinner_background)

            }
            if (!hasFocus) {
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

    }//on create ends here

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

        try {
            val qry =
                "select * from stockaudit"
            val statement = con.createStatement()
            val rsgr: ResultSet = statement.executeQuery(qry)
            while (rsgr.next()) {
                val id = rsgr.getInt("Id")
                jobNumber = rsgr.getString("JobNumber")

                jobList.add(0, ModelJobListSp("",jobNumber))

            }

            sp_jobnum.adapter = ArrayAdapter<ModelJobListSp>(context, R.layout.simple_list_item_1,jobList)
            progressDialog!!.dismiss()
            Log.e(ContentValues.TAG, "Dataaaaa$jobList: ")
        }
        catch (e: Exception) {
            Log.e(ContentValues.TAG, "error: " + e.message)
            e.printStackTrace()
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
                    setRecyclerSam(barCode)
                    //binding.edtQty.requestFocus()
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


  //setting data in recylerview
    private fun setRecyclerSam(barCode: String) {

        rvList.add(0, ModelRVList("1", jobNumber,barCode,dateorg))
        binding.rvrecyclerView.adapter=AdapterRVList(this,rvList)
        binding.tvcoutn.text=rvList.size.toString()

    }
} //end of class










