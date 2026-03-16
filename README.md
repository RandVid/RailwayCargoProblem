# Railway Cargo Problem

## Solution Approach

The solution implements a **BFS-based algorithm** that simulates cargo propagation through the railway network.

### Algorithm Overview

1. **Reachability Analysis**
   - First, perform BFS from the starting station `s_0` to determine which stations are reachable
   - Only reachable stations need to be considered for cargo propagation

2. **Cargo Propagation**
   - For each distinct cargo type `c`:
     - Identify all stations that load cargo type `c`
     - Perform BFS starting from these loading stations
     - Propagate cargo `c` through the graph following these rules:
       - If a station unloads cargo `c` AND doesn't load `c`, stop propagation (cargo is removed)
       - If a station doesn't unload `c`, continue propagation (cargo passes through)
     - Mark all visited stations as having access to cargo type `c`

3. **Output Results**
   - For each station, output the sorted list of possible cargo types

### Key Implementation Details

From [Main.kt](fleet-file://utdu5g2ng8hqlmm30vu8/Users/ilya.plisko/IdeaProjects/RailwayCargoProblem/src/Main.kt?type=file&root=%252F):

1. **Data Structures**:
   - `unload[station]` — cargo type unloaded at each station
   - `load[station]` — cargo type loaded at each station
   - `cargoMap[cargo]` — list of stations that load each cargo type
   - `graph[station]` — adjacency list of outgoing tracks
   - `possibleCargo[station]` — set of cargo types that can reach each station

2. **Reachability BFS** (lines 21-34):
   - Standard BFS to mark all stations reachable from `s_0`

3. **Cargo Propagation BFS** (lines 36-55):
   - For each cargo type, start BFS from all loading stations
   - Stop propagation at stations where cargo is unloaded (line 46)
   - Track visited stations per cargo type to avoid redundant processing

## Complexity Analysis

**Time Complexity**: `O(C × (S + T))`
- `C` — number of distinct cargo types that are loaded
- `S` — number of stations
- `T` — number of tracks
- For each cargo type, we perform a BFS which visits each station and track at most once

**Space Complexity**: `O(S × C)`
- Storing possible cargo types for each station

## Implementation Features

### Arbitrary Integer Station IDs
The solution uses HashMap-based data structures to support arbitrary integer station IDs (not just consecutive 1 to S):
```kotlin
val unload = hashMapOf<Int, Int>()
val load = hashMapOf<Int, Int>()
val graph = hashMapOf<Int, MutableList<Int>>()
```
Same works for cargo types too.

This means station IDs like `101`, `205`, `999` work without any modifications, and memory usage is `O(actual_stations)` rather than `O(max_station_id)`.

**Further Extension**: If you need to use non-integer IDs (e.g., string station names like `"NYC"`, `"LAX"`) or non-integer cargo types, simply change the type parameters:
```kotlin
val unload = hashMapOf<String, String>()  // String station IDs, String cargo types
val load = hashMapOf<String, String>()
val graph = hashMapOf<String, MutableList<String>>()
val cargoMap = hashMapOf<String, MutableList<String>>()
```

This flexibility makes the solution adaptable to various real-world scenarios where IDs might be UUIDs, codes, or other identifiers.

## Possible Improvements

### Bitset Optimization
Instead of running separate BFS for each cargo type, we can use **bitsets** to propagate multiple cargo types simultaneously:

1. Represent the set of cargo types at each station as an array of bitsets of wordsize `W`
2. Run a `C/W` BFS where each station tracks which cargo types have reached it
3. When visiting a neighbor:
   - Start with current station's cargo bitset
   - Remove cargo that gets unloaded at the neighbor
   - Add cargo that gets loaded at the neighbor
   - If the bitset changes, add the neighbor to the queue

**Improved Complexity**: `O(C(S + T)/W)` with bitset operations
- Much smaller amount of BFS to be run
- Bitset operations are typically very fast in practice

### Implementation Sketch
```kotlin
val possibleCargo = Array(s + 1) { BitSet() }
val queue = ArrayDeque<Int>()
queue.add(s0)
possibleCargo[s0][load[s0]] = true

while (queue.isNotEmpty()) {
    val station = queue.removeFirst()
    var cargos = possibleCargo[station].copy()
    cargos.clear(unload[station])
    cargos.set(load[station])

    for (next in graph[station]) {
        if (cargos isNotSubsetOf possibleCargo[next]) {
            possibleCargo[next].or(cargos)
            queue.add(next)
        }
    }
}
```

This approach would be particularly effective when:
- The number of cargo types `C` is large
- Many cargo types share similar propagation paths
- Memory for bitsets is acceptable

## Running the Solution

```bash
kotlinc src/Main.kt -include-runtime -d RailwayCargo.jar &&
java -jar RailwayCargo.jar < input.txt
```

Or compile and run directly:
```bash
kotlinc src/Main.kt -include-runtime -d RailwayCargo.jar && 
java -jar RailwayCargo.jar
```

## Example

### Input
```
3 3
1 0 1
2 1 2
3 2 0
1 2
1 3
3 1
1
```

### Output
```
Station 1, 2 cargo types: 0 1
Station 2, 1 cargo types: 1
Station 3, 1 cargo types: 1
```
