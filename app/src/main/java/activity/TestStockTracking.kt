package activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.crm_copy.connection.ConnectionClass
import com.example.stockauditwayinfotech.R
import com.example.stockauditwayinfotech.databinding.ActivityMainBinding
import com.example.stockauditwayinfotech.databinding.ActivityTestStockTrackingBinding
import kotlinx.android.synthetic.main.activity_main.*
import java.sql.Statement

class TestStockTracking : AppCompatActivity() {
    private val context: Context = this@TestStockTracking
    private lateinit var connectionClass: ConnectionClass
    private lateinit var binding: ActivityTestStockTrackingBinding
    private lateinit var statement: Statement
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestStockTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSave.setOnClickListener {
            var barCode = binding.adtxt.text.toString().trim()
            Log.e("hh","done$barCode")
            checkSerialNum(barCode)
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