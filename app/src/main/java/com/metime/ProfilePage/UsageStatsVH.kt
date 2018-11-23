package com.socialtime

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.metime.R
import java.util.concurrent.TimeUnit

class UsageStatsVH (i : View) : RecyclerView.ViewHolder(i) {
    val appIcon = i.findViewById<ImageView>(R.id.challenge_icon)
    val appName = i.findViewById<TextView>(R.id.title)
    val lastTimeUsed = i.findViewById<TextView>(R.id.last_used)

    fun bindTo(usageStatsWrapper: UsageStatsWrapper) {
        appIcon.setImageDrawable(usageStatsWrapper.appIcon)
        appName.text = (usageStatsWrapper.appName)
        lastTimeUsed.text = "Total time used today: " + setTime(usageStatsWrapper)
    }

    fun setTime(usageStatsWrapper : UsageStatsWrapper) : String {
        var millis = usageStatsWrapper.foreground

        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        millis -= TimeUnit.HOURS.toMillis(hours);
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        var sb = StringBuilder(64);

        if (hours > 0) {
            sb.append(hours);
            sb.append(" Hr ");
            return(sb.toString());
        }
        if (minutes > 0) {
            sb.append(minutes);
            sb.append(" Min ");
            return(sb.toString());
        }
        if (seconds > 0) {
            sb.append(seconds);
            sb.append(" Sec");
            return(sb.toString());
        }
        sb.append("Never");
        return(sb.toString());
    }
}