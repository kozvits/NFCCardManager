package com.nfccardmanager.presentation.ui.emulation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nfccardmanager.presentation.ui.main.TypeBadge
import com.nfccardmanager.presentation.ui.main.cardAccentColor
import com.nfccardmanager.presentation.ui.theme.*
import com.nfccardmanager.presentation.viewmodel.EmulationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmulationScreen(
    onNavigateBack: () -> Unit,
    viewModel: EmulationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.card) {
        if (uiState.card != null && !uiState.isEmulating) {
            viewModel.startEmulation()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Emulation", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.card == null) {
                NoCardContent(onBack = onNavigateBack)
            } else {
                val card = uiState.card!!
                val accentColor = cardAccentColor(card.type)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (uiState.isEmulating) {
                        ActiveEmulation(
                            cardName = card.name.ifEmpty { "NFC Card" },
                            cardUid = card.uid,
                            cardType = card.type,
                            accentColor = accentColor,
                            onStop = { viewModel.stopEmulation() }
                        )
                    } else {
                        ReadyEmulation(
                            cardName = card.name.ifEmpty { "NFC Card" },
                            cardUid = card.uid,
                            cardType = card.type,
                            accentColor = accentColor,
                            onStart = { viewModel.startEmulation() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActiveEmulation(
    cardName: String,
    cardUid: String,
    cardType: com.nfccardmanager.domain.model.CardType,
    accentColor: Color,
    onStop: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "emulation_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(900, easing = EaseInOutSineEmulation), RepeatMode.Reverse),
        label = "pulse"
    )
    val ringAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(tween(900, easing = EaseInOutSineEmulation), RepeatMode.Reverse),
        label = "ring_alpha"
    )

    // Status badge
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(MaterialTheme.colorScheme.secondary, CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            "EMULATING",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary,
            letterSpacing = 1.5.sp
        )
    }

    Spacer(modifier = Modifier.height(40.dp))

    // Animated NFC icon
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(200.dp)) {
        repeat(3) { i ->
            Box(
                modifier = Modifier
                    .size((80 + i * 44).dp)
                    .scale(if (i == 0) pulseScale else 1f)
                    .border(
                        (2f - i * 0.5f).dp,
                        accentColor.copy(alpha = ringAlpha * (1f - i * 0.2f)),
                        CircleShape
                    )
            )
        }
        Box(
            modifier = Modifier
                .size(76.dp)
                .background(
                    brush = Brush.radialGradient(
                        listOf(accentColor.copy(alpha = 0.4f), accentColor.copy(alpha = 0.1f))
                    ),
                    shape = CircleShape
                )
                .border(2.dp, accentColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Nfc,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(38.dp)
            )
        }
    }

    Spacer(modifier = Modifier.height(32.dp))

    Text(
        cardName,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
        cardUid.uppercase(),
        style = MaterialTheme.typography.bodySmall,
        fontFamily = FontFamily.Monospace,
        color = accentColor
    )
    Spacer(modifier = Modifier.height(8.dp))
    TypeBadge(cardType)

    Spacer(modifier = Modifier.height(16.dp))
    Text(
        "Hold phone near NFC reader",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(40.dp))

    OutlinedButton(
        onClick = onStop,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth().height(52.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(0.5f))
    ) {
        Icon(Icons.Default.Stop, null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Stop Emulation", fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ReadyEmulation(
    cardName: String,
    cardUid: String,
    cardType: com.nfccardmanager.domain.model.CardType,
    accentColor: Color,
    onStart: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(accentColor.copy(0.15f), CircleShape)
                .border(1.dp, accentColor.copy(0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Nfc, null, tint = accentColor, modifier = Modifier.size(36.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(cardName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(4.dp))
        Text(cardUid.uppercase(), style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace, color = accentColor)
        Spacer(modifier = Modifier.height(8.dp))
        TypeBadge(cardType)
    }

    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = onStart,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Icon(Icons.Default.Nfc, null, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Start Emulation", fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun NoCardContent(onBack: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(32.dp)
    ) {
        Icon(Icons.Default.Nfc, null,
            modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(16.dp))
        Text("No card selected", style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Please select a card from your collection first.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onBack, shape = RoundedCornerShape(12.dp)) { Text("Go Back") }
    }
}

private val EaseInOutSineEmulation = CubicBezierEasing(0.37f, 0f, 0.63f, 1f)
