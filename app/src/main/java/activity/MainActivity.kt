package activity

import adapter.AdapterRVList
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import com.StockTaking.connection.SessionManager
import com.StockTaking.model.DataModel
import com.StockTaking.model.ModelJobList
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
    private var scanBy = ""
    private val scanByList = ArrayList<String>()
    private var itemsettt = ""
    private var formattedDate = ""
    private var itemNameNew = ""
    private var qty="1"
    private lateinit var sessionManager: SessionManager
    var progressDialog: ProgressDialog? = null
    private val BarcodeList = ArrayList<DataModel>()
    private val jobList = ArrayList<ModelJobList>()


//C012223S01M000001
    //saloni

    private var barCode = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(this)
        connectionClass = ConnectionClass(context)

        val c = Calendar.getInstance().time
        val df = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        formattedDate = df.format(c)
        tvDate.text = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(c)

        imgback.setOnClickListener {
            onBackPressed()

        }
        tvReset.setOnClickListener {
            overridePendingTransition(0, 0)
            finish()
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
//        edtscanbarcode
        edtscanbarcode.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                edtscanbarcode.setBackgroundResource(android.R.drawable.spinner_background)

            }
            if (!hasFocus) {
                edtscanbarcode.setBackgroundColor((android.R.drawable.spinner_dropdown_background))

            }

        }


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


        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Loading..")
        progressDialog!!.setTitle("Please Wait")
        progressDialog!!.isIndeterminate = false
        progressDialog!!.setCancelable(true)
        progressDialog!!.show()

        Handler(mainLooper).postDelayed({
            con = connectionClass.CONN()!!
            statement = con.createStatement() }, 200)
    }//on create ends here

    private fun checkISerialNumber() {
        try {
            val query =
                // " Select SerialNumber from inv_purchaseitemserialnumber where Code=

                "select SerialNumber  from inv_purchaseitemserialnumber where SerialNumber=('${barCode}')"

            val resultSet = statement.executeQuery(query)
            if (resultSet.next()) {
                //validation part
                val code = resultSet.getString("SerialNumber")

                if (code==barCode){
                    myToast(this,"valid Item Code")
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


    }

//    private fun checkBarcode(barCode: String) {
//        try {
//            val query =
//                "Select SerialNumber from INV_PurchaseItemSerialNumber where SerialNumber = ('${barCode}')"
//
//            //OR CartonNumber = ('${edtCartonNumber.text}')
//            try {
//                val statement = conn!!.createStatement()
//                val resultSet = statement.executeQuery(query)
//                if (resultSet.next()) {
//                    itemNameNew = resultSet.getString("barCode")
//                    myToast(this, "Item Already Dispatch")
//                    binding.edtscanbarcode.text?.clear()
//                    if (itemNameNew == barCode) {
//                        getItemDetails(edtQty, rackNumber)
//                    }
//                } else {
//
//
//                }
//
//            } catch (se: Exception) {
//                Log.e("ERROR", se.message!!)
//            }
//
//
//        }




//    private fun setRecyclerData( qty: String, barCode: String) {
//        rvrecyclerView.add(0, ModelRVList(qty,barCode))
//        binding.rvrecyclerView.adapter = AdapterRVList(this, rvrecyclerView)
//
//
//    }

}









