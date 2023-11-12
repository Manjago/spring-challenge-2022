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
                    heroesCoord[id] = Coord(x, y)
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Coord

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }

    companion object {
        val ZERO = Coord(0,0)
    }
}

fun power2(a: Int) = a * a
fun dist(a: Coord, b:Coord) : Int = power2(a.x - b.x) + power2(a.y - b.y)

sealed interface State {
    enum class Wait : State {
        INSTANCE;
    }
    class Focused(val victimId: Int) : State
    class Go(val dest: Coord) : State
}

class Player {

    var initialCoord: Array<Coord>? = null
    lateinit var coord : Array<Coord>
    val states = Array<State>(3) {State.Wait.INSTANCE}

    fun nextMove(spiders : Map<Int, Spider>, heroesCoord: Array<Coord>) : List<String> {

        if (initialCoord == null) {
            initialCoord = heroesCoord
        }

        coord = heroesCoord

        // clean old focused
        for(i in 0 until 3) {
            val state = states[i]
            if (state is State.Focused) {
                if (!spiders.containsKey(state.victimId)) {
                    val oldVicitmId = state.victimId
                    states[i] = State.Go(initialCoord!![i])
                    System.err.println("$i release from $oldVicitmId")
                }
            }
        }

        //clean old Go
        for(i in 0 until 3) {
            val state = states[i]
            if (state is State.Go && coord[i] == state.dest) {
                states[i] = State.Wait.INSTANCE
                System.err.println("$i trip end")
            }
        }

        // get new
        spiders.values.asSequence().filter { spider -> spider.nearBase && spider.threatForMe }.forEach {spider ->
            if (states.asSequence().any { it is State.Focused && it.victimId == spider.id}.not()) {

                // nearest
                val ordered = sequenceOf(0, 1, 2).filter { states[it] !is State.Focused }.sortedBy {
                    dist(spider, coord[it])
                }.toList()
                if (ordered.isNotEmpty()) {
                    val index = ordered[0]
                    states[index] = State.Focused(spider.id)
                    System.err.println("$index focus on ${spider.id}")
                }
            }
        }

        val answer = mutableListOf<String>()

        for(i in 0 until 3) {
            when(val state = states[i]) {
                is State.Wait -> {
                    answer.add("WAIT")
                    System.err.println("$i WAIT")
                }
                is State.Focused -> {
                    val target = spiders[state.victimId]!!
                    answer.add("MOVE ${target.x} ${target.y}")
                    System.err.println("$i focused on spider ${state.victimId} dist ${dist(coord[i], target)}")
                }
                is State.Go -> {
                    val target = state.dest
                    answer.add("MOVE ${target.x} ${target.y}")
                    System.err.println("$i got to $target dist ${dist(coord[i], target)}")
                }
            }
        }

        return answer.toList()
    }
}