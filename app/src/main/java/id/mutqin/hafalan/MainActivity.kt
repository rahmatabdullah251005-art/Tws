package id.mutqin.hafalan

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import id.mutqin.hafalan.data.HafalanRepository
import id.mutqin.hafalan.data.RoomHafalanRepository
import id.mutqin.hafalan.data.local.MutqinDatabase
import id.mutqin.hafalan.ui.MutqinScreen
import id.mutqin.hafalan.ui.MutqinViewModel
import id.mutqin.hafalan.ui.theme.MutqinTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )

        val database = MutqinDatabase.getInstance(applicationContext)
        val repository: HafalanRepository = RoomHafalanRepository(database.mutqinDao())

        setContent {
            MutqinTheme {
                val viewModel: MutqinViewModel = viewModel(
                    factory = MutqinViewModel.Factory(repository)
                )
                MutqinScreen(viewModel = viewModel)
            }
        }
    }
}
