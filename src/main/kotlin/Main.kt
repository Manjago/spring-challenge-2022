import java.util.*

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
fun main(args : Array<String>) {
    val input = Scanner(System.`in`)
    val baseX = input.nextInt() // The corner of the map representing your base
    val baseY = input.nextInt()
    val heroesPerPlayer = input.nextInt() // Always 3

    val player = Player()

    // game loop
    while (true) {
        for (i in 0 until 2) {
            val health = input.nextInt() // Each player's base health
            val mana = input.nextInt() // Ignore in the first league; Spend ten mana to cast a spell
        }
        val entityCount = input.nextInt() // Amount of heros and monsters you can see
        val spiders = mutableMapOf<Int, Spider>()
        val heroesCoord = Array(3) { Coord.ZERO }
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
                    spiders.put(id, Spider(
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
                    heroesCoord[i] = Coord(x, y)
                }
            }

        }

        val nextMove = player.nextMove(spiders, heroesCoord)
        nextMove.forEach {
            println(it)
        }
    }
}

class Spider(
    val id: Int,
    x: Int,
    y: Int,
    val health: Int,
    val vx: Int,
    val vy: Int,
    val nearBase: Boolean,
    val threatForMe: Boolean
) : Coord(x, y) {
    override fun toString(): String {
        return "Spider(id=$id, health=$health, vx=$vx, vy=$vy, nearBase=$nearBase, threatForMe=$threatForMe) ${super.toString()}"
    }
}

open class Coord(val x: Int, val y: Int) {
    override fun toString(): String {
        return "Coord(x=$x, y=$y)"
    }
    companion object {
        val ZERO = Coord(0,0)
    }
}

fun dist(xMe: Int, yMe: Int, xHis: Int, yHis: Int) : Int = (xMe - xHis)*(xMe - xHis) + (yMe - yHis) * (yMe - yHis)

sealed interface State {
    enum class Wait : State {
        INSTANCE;
    }
    class Focused(val victimId: Int) : State
}

class Player {

    lateinit var coord : Array<Coord>
    val states = Array<State>(3) {State.Wait.INSTANCE}

    fun nextMove(spiders : Map<Int, Spider>, heroesCoord: Array<Coord>) : List<String> {

        coord = heroesCoord

        // clean old
        for(i in 0 until 3) {
            val state = states[i]
            if (state is State.Focused) {
                if (!spiders.containsKey(state.victimId)) {
                    val oldVicitmId = state.victimId
                    states[i] = State.Wait.INSTANCE
                    System.err.println("$i release from $oldVicitmId")
                }
            }
        }

        // get new
        spiders.values.asSequence().filter { spider -> spider.nearBase }.forEach {spider ->
            if (states.asSequence().any { it is State.Focused && it.victimId == spider.id}.not()) {
                for (i in 0 until 3) {
                    val st = states[i]
                    if (st is State.Wait) {
                        states[i] = State.Focused(spider.id)
                        System.err.println("$i focus on ${spider.id}")
                    }
                }
            }
        }

        val answer = mutableListOf<String>()

        for(i in 0 until 3) {
            val state = states[i]
            when(state) {
                is State.Wait -> answer.add("WAIT")
                is State.Focused -> {
                    val target = spiders[state.victimId]!!
                    answer.add("MOVE ${target.x} ${target.y}")
                }
            }
        }

        return answer.toList()
    }
}