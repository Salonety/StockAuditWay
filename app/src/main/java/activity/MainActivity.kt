package activity

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import com.StockTaking.connection.SessionManager
import com.StockTaking.model.ModelCoustmerList
import com.StockTaking.model.ModelJobList
import com.StockTaking.other.MethodFactory.Companion.myToast
import com.crm_copy.connection.ConnectionClass
import com.example.stockauditwayinfotech.R
import com.example.stockauditwayinfotech.databinding.ActivityMainBinding
import com.example.stockauditwayinfotech.databinding.ActivityStockTakingDashboardBinding
import com.example.stockauditwayinfotech.databinding.MyToastBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.newFixedThreadPoolContext
import java.sql.Connection
import java.sql.Statement
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private val context: Context = this@MainActivity
    private lateinit var connectionClass: ConnectionClass
    private lateinit var binding: ActivityMainBinding
    private  var conn: Connection? = null
    private lateinit var con: Connection
    private lateinit var statement: Statement
    private var scanBy=""
    private val scanByList = ArrayList<String>()
    private var itemsettt=""
    private var formattedDate=""
    private lateinit var sessionManager: SessionManager
    var progressDialog: ProgressDialog? =null
    private val customerList = ArrayList<ModelCoustmerList>()
    private val jobList = ArrayList<ModelJobList>()

    //saloni

   private var barCode=""
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
            overridePendingTransition(0,0)
            finish()
            startActivity(intent)
            overridePendingTransition(0,0)
        }
//        edtscanbarcode

//        edtscanbarcode.setOnFocusChangeListener { _, hasFocus ->
//            if (hasFocus) {
//                edtscanbarcode.setBackgroundResource(android.R.drawable.spinner_background)
//
//            }
//            if (!hasFocus) {
//                edtscanbarcode.setBackgroundColor((android.R.drawable.spinner_dropdown_background))
//
//            }
//
//        }
//
//
//        edtscanbarcode.setOnKeyListener { view, keyCode, keyEvent ->
//            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
//                return@setOnKeyListener false
//            }
//            if (keyCode == KeyEvent.KEYCODE_ENTER) {
//
//                barCode = edtscanbarcode.text.toString()
//
//
//
//                return@setOnKeyListener true
//            }
//            false
//        }



        binding.btnSaveData.setOnClickListener {
            barCode=binding.edtscanbarcode.text.toString().trim()
         // myToast(this,barCode)
            checkSerialNum(barCode)
        }


    }

    private fun checkSerialNum(barCode: String) {
        myToast(this,"enter")

            try {
                val query =
                    "Select SerialNumber from INV_PurchaseItemSerialNumber where SerialNumber = ('${barCode}')"
                val resultSet = statement.executeQuery(query)
                if (resultSet.next()) {
                    val serialNumber = resultSet.getString("SerialNumber")

                    if (barCode==serialNumber){
                        myToast(this,"valid")
                    }



                }
                else {

                    myToast(this,"Invalid")
                    edtscanbarcode.text.clear()
                }

            } catch (se: Exception)
            {
                Log.e("ERROR", se.message!!)
            }

        }
////            val query =
////                "  Select SerialNumber from INV_DispatchItemSerialNumber where IsDeleted is null and SerialNumber = ('$barCode')"
//
//        val query =  "Select SerialNumber from INV_PurchaseItemSerialNumber where  IsDeleted is null and SerialNumber = ('$barCode')"
//
//            try {
//                val statement = conn!!.createStatement()
//                val resultSet = statement.executeQuery(query)
//                if (resultSet.next()) {
//
//                    Toast.makeText(this, "Item Already Dispatch", Toast.LENGTH_SHORT).show()
//                    edtscanbarcode.text?.clear()
//                }
//                else{
//                    checkSerialNumber()
//
//
//
//                }
//
//            }catch (se: Exception)
//            {
//                Log.e("ERROR", se.message!!)
//            }
//



    }







