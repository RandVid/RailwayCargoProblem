fun main() {
    val (s, t) = readln().split(" ").map { it.toInt() }
    val unload = hashMapOf<Int, Int>()
    val load = hashMapOf<Int, Int>()
    val cargoMap = hashMapOf<Int, MutableList<Int>>()
    val stations = hashSetOf<Int>()

    repeat(s) {
        val (station, cUnload, cLoad) = readln().split(" ").map { it.toInt() }
        unload[station] = cUnload
        load[station] = cLoad
        cargoMap.getOrPut(cLoad) { mutableListOf() }.add(station)
        stations.add(station)
    }

    val graph = hashMapOf<Int, MutableList<Int>>()
    repeat(t) {
        val (from, to) = readln().split(" ").map { it.toInt() }
        graph.getOrPut(from) { mutableListOf() }.add(to)
    }

    val s0 = readln().toInt()
    val reachable = hashSetOf<Int>()
    val queue = ArrayDeque<Int>()
    queue.add(s0)
    reachable.add(s0)

    while (queue.isNotEmpty()) {
        val v = queue.removeFirst()
        for (n in graph[v] ?: emptyList()) {
            if (n !in reachable) {
                reachable.add(n)
                queue.add(n)
            }
        }
    }

    val possibleCargo = hashMapOf<Int, MutableSet<Int>>()
    for (cargo in cargoMap.keys) {
        val q = ArrayDeque<Int>()
        for (start in cargoMap[cargo]!!) {
            if (start !in reachable) continue
            q.add(start)
        }

        while (q.isNotEmpty()) {
            val station = q.removeFirst()
            if (unload[station] == cargo) continue

            for (next in graph[station] ?: emptyList()) {
                if (cargo !in possibleCargo.getOrPut(next) { hashSetOf() }) {
                    q.add(next)
                    possibleCargo[next]!!.add(cargo)
                }
            }
        }
    }
    for (station in stations.sorted()) {
        val cargos = possibleCargo[station]?.sorted() ?: emptyList()
        println("Station $station, ${cargos.size} cargo types: ${cargos.joinToString(" ")}")
    }
}