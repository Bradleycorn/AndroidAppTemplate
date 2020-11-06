package net.bradball.android.androidapptemplate.ui


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import dagger.hilt.android.AndroidEntryPoint
import net.bradball.android.androidapptemplate.ui.theme.MyApplicationTheme

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(color = MaterialTheme.colors.background) {
                    AppScreen()
                }
            }
        }
    }

    @Preview
    @Composable
    fun AppScreen() {
        Row(horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
                .fillMaxWidth()) {

            Text(text = "AppScreen",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h6)
        }
    }



}
