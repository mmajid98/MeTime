package com.socialtime

import android.app.AppOpsManager
import android.app.AppOpsManager.MODE_ALLOWED
import android.app.AppOpsManager.OPSTR_GET_USAGE_STATS
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Process.myUid
import com.github.mikephil.charting.data.PieEntry
import com.metime.Constants
import com.metime.ProfilePage.UsageContract
import java.util.*

class UsagePresenter(val context: Context, val view: UsageContract.View, val activity : Int) : UsageContract.Presenter {

    private var usageStatsManager: UsageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    private val packageManager: PackageManager = context.packageManager
    private var duration : Int = -1

    private val startTime: Long
        get() {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, duration)
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

    override fun retrieveUsageStats(durations : Int) {
        if (!checkForPermission(context)) {
            view.onUserHasNoPermission()
            return
        }
            duration = durations
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
        if (activity == 1) Constants.dataList = list
        else {
            Constants.pieData = mutableListOf()
            for (i in 0..3) {
                Constants.pieData.add(PieEntry(list[i].foreground.toFloat(), list[i].appName))
            }
        }
        return mutableListOf()
    }

    @Throws(IllegalArgumentException::class)
    private fun fromUsageStat(usageStats: UsageStats): UsageStatsWrapper {
        try {
            val ai = packageManager.getApplicationInfo(usageStats.packageName, 0)
            return UsageStatsWrapper(usageStats.packageName, usageStats.totalTimeInForeground, packageManager.getApplicationIcon(ai), packageManager.getApplicationLabel(ai).toString())

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