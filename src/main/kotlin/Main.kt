import java.io.File
import kotlin.system.exitProcess

// End of sentence is '.' or '?' or '!' (possibly few of them)
val sentenceSeparatorRegex = Regex("""[.?!]+""")

// End of word is one of '.', '?', '!', ',', ':', ';', '-', '–' or whitespace (possibly few of them)
val wordSeparatorRegex = Regex("""[.?!,:;–\s-]+""")

/** Splits string to sentences */
val String.sentences: List<String>
    get() = this.split(sentenceSeparatorRegex).map { it.trim() }.filter { it != "" }


/** Splits string to words */
val String.words: List<String>
    get() = this.split(wordSeparatorRegex).map { it.trim() }.filter { it != "" }

/** counts occurrences of item in list.
 * @return List of pair item and number of occurrences. List sorted by occurrences in descending order.
 */
fun <T> List<T>.countEqualElements(): List<Pair<T, Int>> {

    val stats = mutableMapOf<T, Int>()

    for (element in this)
         stats[element] = (stats[element] ?: 0) + 1

    return stats.toList().sortedByDescending { it.second }
}

/** Creates csv statistics report */
fun csvReport(totalSentences: Int, distribution: List<Pair<Int, Int>>, out: Appendable) {
    out.append("Всего предложений:, $totalSentences\n")
    out.append("\n")
    out.append("Слов в предложении, Количество предложений\n")
    for ((wordsInSentence, nSentences) in distribution)
        out.append("$wordsInSentence, $nSentences\n")
}

/** Creates human-friendly statistics report. Not intended to be parsed by computer */
fun humanFriendlyReport(totalSentences: Int, distribution: List<Pair<Int, Int>>, out: Appendable) {
    out.append("Всего предложений: $totalSentences\n")
    for ((wordsInSentence, nSentences) in distribution)
        out.append("Слов в предложении: $wordsInSentence. Количество таких предложений: $nSentences\n")
}


fun main(args: Array<String>) {
    if(args.size != 1 && args.size != 2) {
        print("usage: counter file [out]")
        exitProcess(0)
    }
    val text = File(args[0]).readText()

    val totalSentences = text.sentences.size
    val sentenceSizes = text.sentences.map { it.words.size }

    val distribution = sentenceSizes.countEqualElements()

    when (val filename = args.getOrNull(1)) {
        null -> humanFriendlyReport (totalSentences, distribution, System.out)
        else -> File(filename).writer().use {
            csvReport(totalSentences, distribution, it)
        }
    }


}
