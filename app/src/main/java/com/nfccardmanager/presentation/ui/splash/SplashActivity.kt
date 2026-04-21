package com.nfccardmanager.presentation.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nfccardmanager.R
import com.nfccardmanager.presentation.ui.main.MainActivity
import com.nfccardmanager.presentation.ui.theme.Cyan400
import com.nfccardmanager.presentation.ui.theme.NavyDark
import com.nfccardmanager.presentation.ui.theme.NFCCardManagerTheme
import kotlinx.coroutines.delay

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NFCCardManagerTheme {
                SplashScreen(
                    onFinished = {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
        delay(1200L)   // icon visible for ~1 second after fade-in
        visible = false
        delay(450L)    // wait for fade-out to finish
        onFinished()
    }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 450, easing = SplashEaseInOut),
        label = "alpha"
    )
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.92f,
        animationSpec = tween(durationMillis = 500, easing = SplashEaseOut),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyDark),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .alpha(alpha)
                .scale(scale)
                .padding(horizontal = 40.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.splash_icon),
                contentDescription = "NFC Card Manager",
                modifier = Modifier
                    .size(220.dp)
                    .clip(RoundedCornerShape(44.dp))
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "NFC Card Manager",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "SCAN  ·  STORE  ·  EMULATE",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Cyan400,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

private val SplashEaseInOut = CubicBezierEasing(0.65f, 0f, 0.35f, 1f)
private val SplashEaseOut   = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1f)
