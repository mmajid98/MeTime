package com.socialtime

import android.app.AppOpsManager
import android.app.AppOpsManager.MODE_ALLOWED
import android.app.AppOpsManager.OPSTR_GET_USAGE_STATS
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Process.myUid
import com.github.mikephil.charting.data.PieEntry
import com.metime.ProfilePage.Constants
import com.metime.ProfilePage.UsageContract
import com.metime.R
import org.jetbrains.anko.doAsync
import java.util.*

class UsagePresenter(val context: Context, val view: UsageContract.View) : UsageContract.Presenter {

    private var usageStatsManager: UsageStatsManager
    private val packageManager: PackageManager

    init {
        usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        packageManager = context.packageManager
    }

    private val startTime: Long
        get() {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_WEEK, -7)
            return calendar.timeInMillis
        }

    private val installedAppList: List<String>
        get() {
            val infos = packageManager.getInstalledApplications(flags)
            val installedApps = ArrayList<String>()
            for (info in infos) {
                installedApps.add(info.packageName)
            }
            return installedApps
        }

    override fun retrieveUsageStats() {
        if (!checkForPermission(context)) {
            view.onUserHasNoPermission()
            return
        }
            val installedApps = installedAppList
            val usageStats = usageStatsManager.queryAndAggregateUsageStats(startTime, System.currentTimeMillis())
            val stats = ArrayList<UsageStats>()
            stats.addAll(usageStats.values)
            val finalList = buildUsageStatsWrapper(installedApps, stats)
            view.onUsageStatsRetrieved(finalList)


    }

    private fun checkForPermission(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, myUid(), context.packageName)
        return mode == MODE_ALLOWED
    }

    private fun buildUsageStatsWrapper(packageNames: List<String>, usageStatses: List<UsageStats>): MutableList<UsageStatsWrapper> {
        val list = mutableListOf<UsageStatsWrapper>()
        for (name in packageNames) {
            for (stat in usageStatses) {
                if (name == stat.packageName) {
                    list.add(fromUsageStat(stat))
                }
            }
        }
        list.sortByDescending { it.foreground }
        Constants.pieData = mutableListOf()
        for ( i in 0..3) {
            Constants.pieData.add(PieEntry(list[i].foreground.toFloat() , list[i].appName))
        }
        return mutableListOf()
    }

    @Throws(IllegalArgumentException::class)
    private fun fromUsageStat(usageStats: UsageStats): UsageStatsWrapper {
        try {
            val ai = packageManager.getApplicationInfo(usageStats.packageName, 0)
            return UsageStatsWrapper(usageStats.totalTimeInForeground, packageManager.getApplicationIcon(ai), packageManager.getApplicationLabel(ai).toString())

        } catch (e: PackageManager.NameNotFoundException) {
            throw IllegalArgumentException(e)
        }

    }

    companion object {

        private val flags = PackageManager.GET_META_DATA or
                PackageManager.GET_SHARED_LIBRARY_FILES or
                PackageManager.GET_UNINSTALLED_PACKAGES
    }
}