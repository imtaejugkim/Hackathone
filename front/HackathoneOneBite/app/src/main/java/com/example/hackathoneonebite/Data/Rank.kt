package com.example.hackathoneonebite.Data

data class Rank(var userId: Long, var userIdStr: String, var username: String, var score: Int, var profileUrl: String) {
    /*override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Rank
        if (rankText != other.rankText) return false
        if (followText != other.followText) return false
        if (rankName != other.rankName) return false
        return true
    }
    override fun hashCode(): Int {
        var result = rankText
        result = 31 * result + followText
        result = 31 * result + rankName.hashCode()
        return result
    }*/
}