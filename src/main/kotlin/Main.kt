import java.util.*
import java.io.*
import java.math.*

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
fun main(args : Array<String>) {
    val input = Scanner(System.`in`)
    val baseX = input.nextInt() // The corner of the map representing your base
    val baseY = input.nextInt()
    val heroesPerPlayer = input.nextInt() // Always 3

    // game loop
    while (true) {
        for (i in 0 until 2) {
            val health = input.nextInt() // Each player's base health
            val mana = input.nextInt() // Ignore in the first league; Spend ten mana to cast a spell
        }
        val entityCount = input.nextInt() // Amount of heros and monsters you can see
        val spiders = mutableListOf<Spider>()
        val heroes = mutableMapOf<Int, Hero>()
        for (i in 0 until entityCount) {
            val id = input.nextInt() // Unique identifier
            val type = input.nextInt() // 0=monster, 1=your hero, 2=opponent hero
            val x = input.nextInt() // Position of this entity
            val y = input.nextInt()
            val shieldLife = input.nextInt() // Ignore for this league; Count down until shield spell fades
            val isControlled = input.nextInt() // Ignore for this league; Equals 1 when this entity is under a control spell
            val health = input.nextInt() // Remaining health of this monster
            val vx = input.nextInt() // Trajectory of this monster
            val vy = input.nextInt()
            val nearBase = input.nextInt() // 0=monster with no target yet, 1=monster targeting a base
            val threatFor = input.nextInt() // Given this monster's trajectory, is it a threat to 1=your base, 2=your opponent's base, 0=neither

            when (type) {
                0 -> {
                    spiders.add(Spider(
                        id,
                        x,
                        y,
                        health,
                        vx,
                        vy,
                        nearBase != 0,
                        threatForMe = threatFor == 1
                    ))
                }
                1 -> {
                    heroes.put(id, Hero(id, x, y))
                }
            }

        }

        val nextMove = nextMove(spiders, heroes)
        System.err.println("nextMove size ${nextMove.size}")
        nextMove.forEach {
            println(it)
        }
    }
}

val baseX = 1131
val baseY = 1131

data class Spider(
    val id: Int,
    val x: Int,
    val y: Int,
    val health: Int,
    val vx: Int,
    val vy: Int,
    val nearBase: Boolean,
    val threatForMe: Boolean
) {
    var cost: Int = calcCost()

    private fun calcCost() : Int = when {
        nearBase -> dist(baseX, baseY, x, y) - 10000
        threatForMe -> {
            dist(baseX, baseY, x, y)
        }
        else -> 0
    }

    fun dist(xMe: Int, yMe: Int, xHis: Int, yHis: Int) : Int = (xMe - xHis)*(xMe - xHis) + (yMe - yHis) * (yMe - yHis)
}

data class Hero(
    val id: Int,
    val x: Int,
    val y: Int
)

fun nextMove(spiders : List<Spider>, heroes: Map<Int, Hero>) : List<String> {

    val sorted = spiders.sortedBy { it.cost }
    val answer = mutableListOf<String>()
    for(i in sorted.indices) {
       val spider = spiders[i]
       answer.add("MOVE ${spider.x} ${spider.y}")
       if (answer.size == 3) {
           break
       }
    }

    while (answer.size < 3) {
        answer.add("WAIT")
    }

    return answer.toList()
}