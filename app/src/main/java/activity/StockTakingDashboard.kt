package activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.stockauditwayinfotech.databinding.ActivityStockTakingDashboardBinding

class StockTakingDashboard : AppCompatActivity() {
    private val context: Context = this@StockTakingDashboard
    private lateinit var binding: ActivityStockTakingDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStockTakingDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cardStocktacking.setOnClickListener {
            startActivity(Intent(context,MainActivity::class.java))
        }
        binding.cardJobcreation.setOnClickListener {
            startActivity(Intent(context, TestStockTracking::class.java))
        }
    }

}