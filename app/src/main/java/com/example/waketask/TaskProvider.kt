package com.example.waketask

/**
 * VerificationTask
 * -----------------
 * A simple data holder describing one "prove you're awake" task.
 *  - title:      short label shown above the passage, e.g. "Psalm 23:1"
 *  - promptText: the full text the user needs to read out loud
 *  - keywords:   the important words we listen for in what they actually say
 *
 * `data class` is a special Kotlin class that automatically gets useful behaviour
 * (like printing itself nicely) just from listing its fields - no extra code needed.
 */
data class VerificationTask(
    val title: String,
    val promptText: String,
    val keywords: List<String>
) {
    /**
     * Very simple "fuzzy" matching: what FRACTION of our keywords actually showed up
     * in what the user said? Speech-to-text is never perfect (it mishears words,
     * misses punctuation, etc.), so instead of demanding an exact word-for-word match,
     * we just check that most of the important words were spoken.
     *
     * Returns a number from 0.0 (nothing matched) to 1.0 (everything matched).
     */
    fun matchScore(spokenText: String): Double {
        if (keywords.isEmpty()) return 0.0

        val spokenWords = spokenText
            .lowercase()
            .replace(Regex("[^a-z0-9\\s]"), "") // strip punctuation like . , ! ?
            .split(Regex("\\s+"))
            .toSet()

        val hits = keywords.count { keyword -> spokenWords.contains(keyword.lowercase()) }
        return hits.toDouble() / keywords.size
    }
}

/**
 * TaskProvider
 * ------------
 * Hands out a random verification task. Right now it has two small built-in banks:
 *   - Bible verses (public-domain King James text)
 *   - Cloud computing term definitions (written for this project)
 *
 * To add more tasks later, just add more VerificationTask(...) entries to either list -
 * no other code needs to change.
 */
object TaskProvider {

    private val bibleVerses = listOf(
        VerificationTask(
            title = "Psalm 23:1",
            promptText = "The Lord is my shepherd; I shall not want.",
            keywords = listOf("lord", "shepherd", "shall", "not", "want")
        ),
        VerificationTask(
            title = "John 3:16",
            promptText = "For God so loved the world, that he gave his only begotten Son.",
            keywords = listOf("god", "loved", "world", "gave", "only", "begotten", "son")
        ),
        VerificationTask(
            title = "Proverbs 3:5",
            promptText = "Trust in the Lord with all thine heart, and lean not unto thine own understanding.",
            keywords = listOf("trust", "lord", "heart", "lean", "not", "understanding")
        ),
        VerificationTask(
            title = "Philippians 4:13",
            promptText = "I can do all things through Christ which strengtheneth me.",
            keywords = listOf("all", "things", "christ", "strengtheneth")
        )
    )

    private val cloudTerms = listOf(
        VerificationTask(
            title = "Cloud Term: IaaS",
            promptText = "Infrastructure as a Service provides virtualized computing resources over the internet.",
            keywords = listOf("infrastructure", "service", "virtualized", "computing", "resources", "internet")
        ),
        VerificationTask(
            title = "Cloud Term: Load Balancer",
            promptText = "A load balancer distributes incoming network traffic across multiple servers.",
            keywords = listOf("load", "balancer", "distributes", "network", "traffic", "servers")
        ),
        VerificationTask(
            title = "Cloud Term: Auto Scaling",
            promptText = "Auto scaling automatically adjusts computing capacity based on current demand.",
            keywords = listOf("auto", "scaling", "adjusts", "computing", "capacity", "demand")
        ),
        VerificationTask(
            title = "Cloud Term: Containerization",
            promptText = "Containerization packages an application with everything it needs to run consistently anywhere.",
            keywords = listOf("containerization", "packages", "application", "run", "consistently", "anywhere")
        )
    )

    /** Picks one random task from the bank matching the requested type. */
    fun randomTask(taskType: String): VerificationTask {
        return when (taskType) {
            "CLOUD" -> cloudTerms.random()
            else -> bibleVerses.random()
        }
    }
}
