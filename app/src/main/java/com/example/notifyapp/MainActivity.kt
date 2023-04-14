package com.example.notifyapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.widget.RemoteViews
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.notifyapp.ui.theme.NotifyAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotifyAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background) {
                    NotifyApp()
                }
            }
        }
    }
}

@Composable
fun NotifyApp() {
    val context = LocalContext.current
    val channelId = "MyChannel"
    val notifyId = 0
    val myBitMap =
        BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_foreground)
    val bigText = "This is my test notification in one line. Made it longer " +
            "by setting the setStyle property. " +
            "It should not fit in one line anymore, " +
            "rather show as a longer notification content."
    LaunchedEffect(Unit) {
        createNotifyChannel(channelId, context)
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            "Notifications in Jetpack Compose",
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.padding(bottom = 100.dp)
        )
        Button(onClick = {
            showSimpleNotify(
                context,
                channelId,
                notifyId,
                "Simple notification",
                "This is a simple notification with default priority."
            )
        }, modifier = Modifier.padding(top = 16.dp)) {
            Text(text = "Simple Notification")
        }
        Button(onClick = {
            showSimpleNotifyWithTapAction(
                context,
                channelId,
                notifyId + 1,
                "Simple notification + Tap Action",
                "this simple notifications will open an activity on tap"
            )
        },
            modifier = Modifier.padding(top = 16.dp)) {
            Text(text = "Simple notifications + Tap Action")
        }
        Button(onClick = {
            showLargeTextNotify(
                context,
                channelId,
                notifyId + 2,
                "My large Text Notification",
                bigText
            )
        },
            modifier = Modifier.padding(top = 16.dp)) {
            Text("Large Text Notify")
        }
        Button(onClick = {
            showBigPicWithThumbnaiNotify(
                context,
                channelId,
                notifyId + 2,
                "Big Picture + Avatar Notify",
                "This is a notify showing a big picture and an auto-hiding avatar.",
                myBitMap
            )
        },
            modifier = Modifier.padding(top = 16.dp)) {
            Text("Big Pic and Big icon Notify")
        }
    }
}

fun createNotifyChannel(channelId: String, context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "MyTestChannel"
        val descrText = "My Important test channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descrText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

fun showSimpleNotify(
    context: Context,
    channelId: String,
    notifyId: Int,
    textTitle: String,
    textContent: String,
    priority: Int = NotificationCompat.PRIORITY_DEFAULT,
) {

    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    var intentFlagType = PendingIntent.FLAG_ONE_SHOT
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        intentFlagType =
            PendingIntent.FLAG_IMMUTABLE // or only use FLAG_MUTABLE >> if it needs to be used with inline replies or bubbles.
    }
    val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, intentFlagType)
    val remoteViews = RemoteViews(context.packageName, R.layout.notify)
    Log.i(TAG, context.packageName)
    remoteViews.setTextViewText(R.id.textView, "Custom")
    remoteViews.setTextViewText(R.id.textView2, "text")
    //remoteViews.setTextViewText(R.id.btnIncrementByOne, "GO")

    remoteViews.setOnClickPendingIntent(R.id.root, pendingIntent);
    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
//        .setContentTitle(textTitle)
//        .setContentText(textContent)
//        .addAction(R.drawable.ic_launcher_foreground, "Previous", pendingIntent)
//        .setPriority(priority)
        .setContent(remoteViews)
        .setStyle(NotificationCompat.DecoratedCustomViewStyle());

    with(NotificationManagerCompat.from(context)) {
        notify(notifyId, builder.build())
    }
}

fun showSimpleNotifyWithTapAction(
    context: Context,
    channelId: String,
    notifyId: Int,
    textTitle: String,
    textContent: String,
    priority: Int = NotificationCompat.PRIORITY_DEFAULT,
) {
    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    var intentFlagType = PendingIntent.FLAG_ONE_SHOT
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        intentFlagType =
            PendingIntent.FLAG_IMMUTABLE // or only use FLAG_MUTABLE >> if it needs to be used with inline replies or bubbles.
    }
    val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, intentFlagType)

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_launcher_background)
        .setContentTitle(textTitle)
        .setContentText(textContent)
        .setPriority(priority)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    with(NotificationManagerCompat.from(context)) {
        notify(notifyId, builder.build())
    }
}

fun showLargeTextNotify(
    context: Context,
    channelId: String,
    notifyId: Int,
    textTitle: String,
    textContent: String,
    priority: Int = NotificationCompat.PRIORITY_DEFAULT,
) {
    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(textTitle)
        .setContentText(textContent)
        .setStyle(
            NotificationCompat.BigTextStyle()
                .bigText(textContent)
        )
        .setPriority(priority)

    with(NotificationManagerCompat.from(context)) {
        notify(notifyId, builder.build())
    }
}

fun showBigPicWithThumbnaiNotify(
    context: Context,
    channelId: String,
    notifyId: Int,
    textTitle: String,
    textContent: String,
    bigImage: Bitmap,
    priority: Int = NotificationCompat.PRIORITY_DEFAULT,

    ) {
    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(textTitle)
        .setContentText(textContent)
        .setLargeIcon(bigImage)
        .setStyle(NotificationCompat.BigPictureStyle()
            .bigPicture(bigImage)
            .bigLargeIcon(null)
        )
        .setPriority(priority)

    with(NotificationManagerCompat.from(context)) { notify(notifyId, builder.build()) }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    NotifyAppTheme {
        NotifyApp()
    }
}