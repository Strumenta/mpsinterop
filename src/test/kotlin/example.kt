class Dummy

fun main(args: Array<String>) {
    println("A")
    val inputStream = Dummy::class.java.getResourceAsStream("/formats-structure.mps")
    println(inputStream)
}