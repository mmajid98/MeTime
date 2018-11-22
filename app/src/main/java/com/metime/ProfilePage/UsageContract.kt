package com.metime.ProfilePage

import com.socialtime.UsageStatsWrapper

interface UsageContract {
    interface View {
        fun onUsageStatsRetrieved(list : MutableList<UsageStatsWrapper>)
        fun onUserHasNoPermission()
    }

    interface Presenter {
        fun retrieveUsageStats()
    }
}