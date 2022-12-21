package activity

import android.app.ProgressDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.crm_copy.connection.ConnectionClass
import com.example.stockauditwayinfotech.databinding.ActivityJobCreationBinding
import kotlinx.android.synthetic.main.activity_main.*
import java.sql.Statement

class JobCreation : AppCompatActivity() {
    private val context: Context = this@JobCreation
    private lateinit var connectionClass: ConnectionClass
    private lateinit var binding: ActivityJobCreationBinding
    private lateinit var statement: Statement
    var progressDialog: ProgressDialog? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobCreationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        imgback.setOnClickListener {
            onBackPressed()

        }
    }


    private fun checkSerialNum(barCode: String) {
        try {
            val query =
                "Select SerialNumber from INV_PurchaseItemSerialNumber where SerialNumber = ('${barCode}')"
            val resultSet = statement.executeQuery(query)
            if (resultSet.next()) {
                val serialNumber = resultSet.getString("SerialNumber")

                if (barCode==serialNumber){
                    Toast.makeText(this, "valid Code", Toast.LENGTH_SHORT).show()
                }



            }
            else {
                Toast.makeText(this, "Invalid Code", Toast.LENGTH_SHORT).show()
                edtscanbarcode.text.clear()
            }

        } catch (se: Exception)
        {
            Log.e("ERROR", se.message!!)
        }


    }

}