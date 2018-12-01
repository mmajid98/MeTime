package com.metime.setChallenge

data class NewsFeed (var key : String = "", var sort : Long = 0, var money: Int = 0, var time : Long = 0, var name: String = "", var image: String = "", var message: String = "", var likes: Int = 0, var likesList : MutableMap<String, String> = mutableMapOf())