package activity

import adapter.AdapterRv1
import adapter.AdapterRv2
import adapter.AdapterRv3
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.StockTaking.connection.SessionManager
import com.StockTaking.model.ModelJobListSp
import com.StockTaking.other.MethodFactory
import com.StockTaking.other.MethodFactory.Companion.myToast
import com.crm_copy.connection.ConnectionClass
import com.example.stockauditwayinfotech.R
import com.example.stockauditwayinfotech.databinding.ActivityDemoBinding
import com.example.stockauditwayinfotech.databinding.ActivityMainBinding
import com.example.stockauditwayinfotech.model.ModelMatInStore
import com.example.stockauditwayinfotech.model.ModelMatOutStore
import com.example.stockauditwayinfotech.model.ModelMatSup
import com.example.stockauditwayinfotech.model.ModelRVList
import kotlinx.android.synthetic.main.activity_demo.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.edtscanbarcode
import kotlinx.android.synthetic.main.activity_remainbarcode.*
import kotlinx.android.synthetic.main.activity_test.*
import java.sql.Connection
import java.sql.Statement
import java.util.ArrayList

class DemoActivity : AppCompatActivity() {
    private val context: Context = this@DemoActivity
    private lateinit var connectionClass: ConnectionClass
    private lateinit var binding: ActivityDemoBinding
    private var conn: Connection? = null
    private lateinit var con: Connection
    private lateinit var statement: Statement
    private val jobList = ArrayList<ModelJobListSp>()
    private val rvListone = ArrayList<ModelMatSup>()
    private val rvListtwo = ArrayList<ModelMatOutStore>()
    private val rvListthree = ArrayList<ModelMatInStore>()
    private var mat = ""
    private var barCode = ""
    private var jobNumber=""
    private lateinit var sessionManager: SessionManager
    var progressDialog: ProgressDialog? = null
    private val mat_in_supp = ArrayList<ModelMatSup>()
    var list_of_mat = arrayOf("Material In (Supplier)", "Material Out (Store)","Material In (Store)")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
            getJobNumber() }, 200)

        imgbackp.setOnClickListener {
            onBackPressed()
        }


        //auto manual spinner
        spinner_mat.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {

                if (list_of_mat.size > 0)
                {
                    mat=list_of_mat[i]

                    if(mat=="Material In (Supplier)")
                    {
                        binding.layout1.visibility=View.VISIBLE
                        binding.layout2.visibility=View.GONE
                        binding.layout3.visibility=View.GONE
                        binding.rvMatInStore.visibility=View.GONE
                        binding.matOutStore.visibility=View.GONE
                        binding.linTb.visibility=View.VISIBLE
                        binding.rvMatInSup.visibility=View.VISIBLE

                    }
                    if(mat=="Material Out (Store)")
                    {
                        binding.layout1.visibility=View.GONE
                        binding.layout2.visibility=View.VISIBLE
                        binding.layout3.visibility=View.GONE
                        binding.linTb.visibility=View.GONE
                        binding.rvMatInStore.visibility=View.GONE
                        binding.matOutStore.visibility=View.VISIBLE
                        binding.rvMatInSup.visibility=View.GONE

                    }
                    if (mat=="Material In (Store)"){
                        binding.layout1.visibility=View.GONE
                        binding.layout2.visibility=View.GONE
                        binding.layout3.visibility=View.VISIBLE
                        binding.linTb.visibility=View.GONE
                        binding.rvMatInStore.visibility=View.VISIBLE
                        binding.matOutStore.visibility=View.GONE
                        binding.rvMatInSup.visibility=View.GONE
                    }

                }
            }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {
            }
        }


        //FUNCTION
        modes()
        enter()
        focus()
        fetchjobdata()
    }//oncreate ends here

    private fun focus() {
        //for focus
        mat_in_sup_bar.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
            {
                mat_in_sup_bar.setBackgroundResource(android.R.drawable.spinner_background)
            }
            if (!hasFocus)
            {
                mat_in_sup_bar.setBackgroundColor(android.R.drawable.spinner_dropdown_background)
            }

        }

        mat_out_store_bar.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
            {
                mat_out_store_bar.setBackgroundResource(android.R.drawable.spinner_background)
            }
            if (!hasFocus)
            {
                mat_out_store_bar.setBackgroundColor(android.R.drawable.spinner_dropdown_background)
            }

        }

        edt_mat_in_store_bar.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
            {
                edt_mat_in_store_bar.setBackgroundResource(android.R.drawable.spinner_background)
            }
            if (!hasFocus)
            {
                edt_mat_in_store_bar.setBackgroundColor(android.R.drawable.spinner_dropdown_background)
            }

        }


    }

    private fun fetchjobdata() {
        sp_job.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @SuppressLint("SuspiciousIndentation")
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                if (jobList.size > 0) {
                    jobNumber = jobList[i].JobNumber
                    Log.e(ContentValues.TAG, "type: $jobNumber")
                    binding.matOutStoreBar.isEnabled=true

                }

            }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {
            }
        }

    }

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
            } while (resultSet.next())
            sp_job.adapter = ArrayAdapter<ModelJobListSp>(context, android.R.layout.simple_list_item_1, jobList)
            progressDialog!!.dismiss()
        }
    }

    private fun enter() {
        //for layout 1
        binding.matInSupBar.setOnKeyListener { view, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                return@setOnKeyListener false
            }
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                barCode = binding.matInSupBar.text.toString()
                checkBarcode1()
                return@setOnKeyListener true
            }
            false
        }

        //for layout 2
        binding.matOutStoreBar.setOnKeyListener { view, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                return@setOnKeyListener false
            }
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                barCode = binding.matOutStoreBar.text.toString()
                checkBarcode2()
                return@setOnKeyListener true
            }
            false
        }
        //for layout 3
        binding.edtMatInStoreBar.setOnKeyListener { view, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                return@setOnKeyListener false
            }
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                barCode = binding.edtMatInStoreBar.text.toString()
                checkBarcode3()
                return@setOnKeyListener true
            }
            false
        }


    }

    private fun checkBarcode3() {
        try {
            val query =
                " select COL1 from stockaudit where  COL1=('${barCode}')"

            val resultSet = statement.executeQuery(query)
            if (resultSet.next()) {
                //validation part
                val code = resultSet.getString("COL1")
                if (code==barCode){
                    //myToast(this, "valid Item Code")
                    checkdata3(code)

                }
                Log.e(ContentValues.TAG,"code$code")
            }
            else {
                myToast(this, "Invalid Item Code")
                binding.matOutStoreBar.text.clear()
            }

        } catch (se: Exception)
        {
            Log.e("ERROR", se.message!!)
        }


    }

    private fun checkdata3(code: String) {
        try {
            val query = " select BarCode from [StockTaking] where BarCode=('${code}')"
            val resultSet = statement.executeQuery(query)
            if (resultSet.next()) {
                //validation part
                val BarCode = resultSet.getString("BarCode")
                if (BarCode == code) {
                    val toneGen1 = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
                    toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150)
                    myToast(this, "barcode already exists")
                }
                else{

                }

            }

            else{
                setRecycler3(barCode)
                insert3()
            }

        } catch (se: Exception)
        {
            Log.e("ERROR", se.message!!)
        }


    }

    private fun insert3() {
        for (itemDetail in rvListthree)
        {
            try {
                var n = 0
                val sql =
                    "INSERT INTO StockTaking (BarCode) VALUES (?)"
                val statement = con.prepareStatement(sql)

                statement.setString(1, itemDetail.Barcode3)//BarCode

                n = statement.executeUpdate()
                if (n > 0) {

                } else Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            } catch (e: java.lang.Exception) {
                Log.e(ContentValues.TAG, "error: " + e.message)
                e.printStackTrace()
            }
        }

    }

    private fun setRecycler3(barCode: String) {
        rvListthree.add(0, ModelMatInStore(barCode))
        binding.rvMatInStore.adapter = AdapterRv3(this, rvListthree)
        binding.countThree.text = rvListtwo.size.toString()


    }

    private fun checkBarcode2() {
        try {
            val query =
                " select COL1 from stockaudit where  COL1=('${barCode}')"

            val resultSet = statement.executeQuery(query)
            if (resultSet.next()) {
                //validation part
                val code = resultSet.getString("COL1")
                if (code==barCode){
                    //myToast(this, "valid Item Code")
                    checkJob(code,jobNumber)
                }
                Log.e(ContentValues.TAG,"code$code")
            }
            else {
                myToast(this, "Invalid Item Code")
                binding.matOutStoreBar.text.clear()
            }

        } catch (se: Exception)
        {
            Log.e("ERROR", se.message!!)
        }


    }

    private fun checkJob(code: String, jobNumber: String) {
        try {
            val query =
            "select COL1  from StockAudit where JobNumber=('${jobNumber}') and COL1=('${code}')"
            val resultSet = statement.executeQuery(query)
            if (resultSet.next()) {
                //validation part
                val bar = resultSet.getString("COL1")
                if (code==bar) {
                    checkdata2(code)
                }
            }


        } catch (se: Exception)
        {
            Log.e("ERROR", se.message!!)
        }


    }

    private fun checkdata2(code: String) {
        try {
            val query = "select BarCode from [StockTaking] where BarCode =('${code}') "
            val resultSet = statement.executeQuery(query)
            if (resultSet.next()) {
                //validation part
                val BarCode = resultSet.getString("BarCode")
                if (BarCode == code) {
                        val toneGen1 = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
                        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150)
                        myToast(this, "barcode already exists")


                }




            }



            else{
                setRecycler2(barCode)
                insert2()
            }


        } catch (se: Exception)
        {
            Log.e("ERROR", se.message!!)
        }


    }

    private fun check(code: String,jobNumber: String) {
        try {
            val query = "select BarCode from [StockTaking] where BarCode =('${code}') and JobNumber=('${jobNumber}') "
            val resultSet = statement.executeQuery(query)
            if (resultSet.next()){
                val jobs = resultSet.getString("JobNumber")
                if (jobs!=code) {
                    val jobs = resultSet.getString("JobNumber")
                    val toneGen1 = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
                    toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150)
                    myToast(this,"barcode does not exists on this job")
                }
            }

            else{
                checkJob(code, jobNumber)

            }





        } catch (se: Exception)
        {
            Log.e("ERROR", se.message!!)
        }

    }

    private fun insert2() {
        for (itemDetail in rvListtwo)
        {
            try {
                var n = 0
                val sql =
                    "INSERT INTO StockTaking (BarCode) VALUES (?)"
                val statement = con.prepareStatement(sql)

                statement.setString(1, itemDetail.Barcode2)//BarCode

                n = statement.executeUpdate()
                if (n > 0) {
                    //Toast.makeText(this, "successfully Inserted", Toast.LENGTH_SHORT).show()
                } else Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            } catch (e: java.lang.Exception) {
                Log.e(ContentValues.TAG, "error: " + e.message)
                e.printStackTrace()
            }
        }


    }

    private fun setRecycler2(barCode: String) {
        rvListtwo.add(0, ModelMatOutStore(barCode))
        binding.matOutStore.adapter = AdapterRv2(this, rvListtwo)
        binding.countTwo.text = rvListtwo.size.toString()



    }

    private fun checkBarcode1() {
        try {
            val query =
                " select COL1 from stockaudit where  COL1=('${barCode}')"

            val resultSet = statement.executeQuery(query)
            if (resultSet.next()) {
                //validation part
                val code = resultSet.getString("COL1")
                if (code==barCode){
                   // myToast(this, "valid Item Code")
                    checkdata1(code)
                    insert1()

                }
                Log.e(ContentValues.TAG,"code$code")
            }
            else {
                MethodFactory.myToast(this, "Invalid Item Code")
                binding.matInSupBar.text.clear()
            }

        } catch (se: Exception)
        {
            Log.e("ERROR", se.message!!)
        }

    }

    private fun insert1() {
        // for inserting into stocktaking table in databse

            for (itemDetail in rvListone)
            {
                try {
                    var n = 0
                    val sql =
                        "INSERT INTO StockTaking (BarCode) VALUES (?)"
                    val statement = con.prepareStatement(sql)

                    statement.setString(1, itemDetail.Barcode1)//BarCode

                    n = statement.executeUpdate()
                    if (n > 0) {
                    } else Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                } catch (e: java.lang.Exception) {
                    Log.e(ContentValues.TAG, "error: " + e.message)
                    e.printStackTrace()
                }
            }


    }

    private fun setRecycler1(barCode: String) {
        rvListone.add(0, ModelMatSup(barCode))
       binding.rvMatInSup.adapter = AdapterRv1(this, rvListone)
       binding.countOne.text = rvListone.size.toString()


    }

    private fun checkdata1(code:String) {
        try {
            val query = " select BarCode from [StockTaking] where BarCode=('${code}')"
                val resultSet = statement.executeQuery(query)
            if (resultSet.next()) {
                //validation part
                val BarCode = resultSet.getString("BarCode")
                if (BarCode == code) {
                    val toneGen1 = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
                    toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150)
                    myToast(this,"barcode already exists")
                }
                else{

                }

            }
            else
            {
                setRecycler1(barCode)
            }

        } catch (se: Exception)
        {
            Log.e("ERROR", se.message!!)
        }

    }

    private fun modes() {
        spinner_mat!!.onItemSelectedListener
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item,list_of_mat)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_mat!!.setAdapter(aa)
    }

}//program ends here

