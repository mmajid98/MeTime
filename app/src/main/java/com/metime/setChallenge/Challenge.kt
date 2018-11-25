package com.metime.setChallenge

data class Challenge (var key: String = "",
                      var money : Int = 0,
                      var startTime : Long = 0,
                      var endTime: Long = 0,
                      var appNames : List<String> = listOf(),
                      var charity : String = "",
                      var status: Int = 0,
                      var lost :Int = 0
)