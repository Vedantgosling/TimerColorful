package com.example.timer1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.test.isEnabled
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timer1.ui.CircularIndicator
import com.example.timer1.ui.TimerViewModel
import com.example.timer1.ui.theme.Timer1Theme
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Timer1Theme(darkTheme = true, dynamicColor = true) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val timerViewModel: TimerViewModel by viewModels()
                    TimerScreenContent(timerViewModel)


                }
            }
        }
    }
}


@Composable
fun TimerScreenContent(timerViewModel: TimerViewModel) {
    val timerValue by timerViewModel.timer.collectAsState()
    val load by timerViewModel.isLoading.collectAsState()

    TimerScreen(
        loading = load,
        timerValue = timerValue,
        onStartClick = { timerViewModel.startTimer() },
        onPauseClick = { timerViewModel.pauseTimer() },
        onStopClick = { timerViewModel.stopTimer() }
    )

}


@Composable
fun TimerScreen(
    loading:Boolean,
    timerValue: Long,
    onStartClick: () -> Unit,
    onPauseClick: () -> Unit,
    onStopClick: () -> Unit
) {


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(modifier = Modifier
            .fillMaxHeight(0.5f)
            .fillMaxWidth()
            .padding(7.dp)) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .background(Color.Gray)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(color = Color.Cyan),
                    onClick = if (loading) {
                        onPauseClick

                    } else {
                        onStartClick
                    }
                )
                .animatedBorder(
                    borderColors = listOf(Color.Red, Color.Green, Color.Blue),
                    backgroundColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    borderWidth = 6.dp
                ),
                contentAlignment = Alignment.Center,
                ){
                Text(text =  timerValue.formatTime(), fontSize = 25.sp,
                    fontStyle = FontStyle.Normal, color = Color.Blue)
                if(loading){
                    CircularIndicator()
                }
            }
        }

        Spacer(modifier = Modifier.height(15.dp))
        StopButton(onStopClick)
    }
}


fun Long.formatTime(): String {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val remainingSeconds = this % 60
    return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
}

@Composable
fun StopButton(stop:()-> Unit) {
    OutlinedButton(
        onClick = stop,
        shape = RoundedCornerShape(30.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Red,
            contentColor = Color.Gray,
            disabledContainerColor = Color.White)
    ){
        Text( text = "Stop", fontSize = 20.sp, color = Color.Black )
    }
}





@Composable
fun Modifier.animatedBorder(
    borderColors: List<Color>,
    backgroundColor: Color,
    shape: Shape = RectangleShape,
    borderWidth: Dp = 1.dp,
    animationDurationInMillis: Int = 1000,
    easing: Easing = LinearEasing
): Modifier {
    val brush = Brush.sweepGradient(borderColors)
    val infiniteTransition = rememberInfiniteTransition(label = "animatedBorder")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = animationDurationInMillis, easing = easing),
            repeatMode = RepeatMode.Restart
        ), label = "angleAnimation"
    )

    return this
        .clip(shape)
        .padding(borderWidth)
        .drawWithContent {
            rotate(angle) {
                drawCircle(
                    brush = brush,
                    radius = size.width,
                    blendMode = BlendMode.SrcIn,
                )
            }
            drawContent()
        }
        .background(color = backgroundColor, shape = shape)
}
