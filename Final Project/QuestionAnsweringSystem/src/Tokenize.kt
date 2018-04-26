import java.io.File
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList
import opennlp.*
import opennlp.tools.doccat.BagOfWordsFeatureGenerator

fun main(args: Array<String>) {
    val bufferedReader = File("FAQs.txt").bufferedReader()

    //val inputString  = inputStream.bufferedReader().use { it.readText() }
    val listOfLines = ArrayList<String>()

    bufferedReader.useLines { lines -> lines.forEach { listOfLines.add(it) } }
    //listOfLines.forEach{ println(" > " + it) }
    tokenize(listOfLines)


}

fun tokenize(allLines : ArrayList<String>) {
    val listOfQues = ArrayList<String>()
    val listofAns = ArrayList<String>()

    //println(allLines.get(1)[0])

    allLines.forEach {
        if(it[0].isDigit()){
            //println(it)
            listOfQues.add(it)
        }
        else {
            if (it[0].equals('A')) {
                listofAns.add(it)
            }
        }
    }

//    listOfQues.forEach{println(" > " + it)}
//
//    println()
//    println()
//
//    listofAns.forEach{ println(" > " + it)}

    var bagOfWordsFeatureGenerator = BagOfWordsFeatureGenerator()

    var arrOfStrings = arrayOf(listOfQues[0], listofAns[0])

    var bag = bagOfWordsFeatureGenerator.extractFeatures(arrOfStrings, null )

    bag.forEach{println(it)}

}