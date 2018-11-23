package com.metime

import android.net.Uri
import com.github.mikephil.charting.data.PieEntry
import com.metime.LoginRegisterReset.MeProfile
import com.socialtime.UsageStatsWrapper

public class Constants {
    companion object {
        var pieData = mutableListOf<PieEntry>()
        var profile = MeProfile()
        var image : Uri? = null
        var dataList = listOf<UsageStatsWrapper>()
    }
}