package day16

import Puzzle
import java.io.File

fun main() {
    PacketDecoder().run()
}

// https://adventofcode.com/2021/day/16

class PacketDecoder : Puzzle(16, "Packet Decoder") {
    override val puzzleInput = "day16_input.txt"
    override val testInput = arrayOf(
        "day16_test1.txt",
        "day16_test2.txt",
        "day16_test3.txt",
        "day16_test4.txt",
        "day16_test5.txt",
        "day16_test6.txt",
        "day16_test7.txt")
    override val label1 = "version sum"
    override val label2 = "packet value"

    private lateinit var packet: Packet

    override fun part1(input: String): Long {
        packet = decode(File(input).readText())
        return versionSum
    }
    override fun part2(input: String): Long {
        return packet.value
    }
}

private var versionSum = 0L

private fun decode(hex: String): Packet {
    //println(hex)
    versionSum = 0L
    val reader = BitReader(hex)
    return decodePacket(reader, 0)
}

private fun decodePacket(reader: BitReader,
                         depth: Int): Packet {

    // Depth and indent is only used for debugging print statements
    var indent = ""
    for (i in 1..depth) indent += "  "

    val packetVersion = reader.readBits(3)
    val packetType = reader.readBits(3).toInt()
    val packet = Packet()
    packet.size = 6
    packet.value = 0

    versionSum += packetVersion

    if (packetType == 4) {
        // literal packets
        var number = 0L
        var more = 1L
        while (more != 0L) {
            more = reader.readBits(1)
            val n = reader.readBits(4)
            number = (number shl 4) + n
            packet.value = number
            packet.size += 5
        }
        //println("${indent}v $packetVersion literal $number")
    } else {
        // operator packets
        val lenType = reader.readBits(1).toInt()
        packet.size++

        //val names = arrayOf("sum", "product", "minimum", "maximum", "literal", "greater-than", "less-than", "equals")
        //val op = names[packetType]
        when (lenType) {
            0 -> {
                var packetLength = reader.readBits(15)
                packet.size += 15
                //println("${indent}v $packetVersion $op sub-packet bits $packetLength")
                while (packetLength > 0) {
                    val subPacket = decodePacket(reader, depth+1)
                    packet.subPackets.add(subPacket)
                    packet.size += subPacket.size
                    packetLength -= subPacket.size
                }
            }
            1 -> {
                val count = reader.readBits(11)
                packet.size += 11
                //println("${indent}v$packetVersion $op sub-packet count $count")
                for (i in 1 .. count) {
                    val subPacket = decodePacket(reader, depth+1)
                    packet.subPackets.add(subPacket)
                    packet.size += subPacket.size
                }
            }
        }

        // Probably could condense this with functions
        when (packetType) {
            0 -> { // sum
                var sum = 0L
                packet.subPackets.forEach { sum += it.value }
                packet.value = sum
            }
            1 -> { // product
                if (packet.subPackets.size > 0) {
                    var product = 1L
                    packet.subPackets.forEach { product *= it.value }
                    packet.value = product
                }
            }
            2 -> { // minimum
                var min = Long.MAX_VALUE
                packet.subPackets.forEach {
                    if (it.value < min)
                        min = it.value
                }
                packet.value = min
            }
            3 -> { // maximum
                var max = Long.MIN_VALUE
                packet.subPackets.forEach {
                    if (it.value > max)
                        max = it.value
                }
                packet.value = max
            }
            5 -> { // greater-than
                if (packet.subPackets[0].value > packet.subPackets[1].value)
                    packet.value = 1
                else
                    packet.value = 0
            }
            6 -> { // less-than
                if (packet.subPackets[0].value < packet.subPackets[1].value)
                    packet.value = 1
                else
                    packet.value = 0
            }
            7 -> { // equals
                if (packet.subPackets[0].value == packet.subPackets[1].value)
                    packet.value = 1
                else
                    packet.value = 0
            }
        }
        //println("${indent}value: ${packet.value}")
    }
    return packet
}

// Didn't really want to make a Packet class but here we are
private class Packet() {
    val subPackets = mutableListOf<Packet>()
    var size: Int = 0
    var value: Long = 0
}

// Reads arbitrary bit length numbers from a hexidecimal string input
private class BitReader(private val hexStream: String) {
    private var hexIndex = 0
    private var bitIndex = 0
    // Seems like there should be a language feature for this:
    private val hexMap = mapOf(
        '0' to 0,
        '1' to 1,
        '2' to 2,
        '3' to 3,
        '4' to 4,
        '5' to 5,
        '6' to 6,
        '7' to 7,
        '8' to 8,
        '9' to 9,
        'A' to 10,
        'B' to 11,
        'C' to 12,
        'D' to 13,
        'E' to 14,
        'F' to 15)
    private val bitMap = mapOf(
        0 to 0b1000,
        1 to 0b0100,
        2 to 0b0010,
        3 to 0b0001)

    fun readBits(bits: Int): Long {
        var output = 0L
        var input = current()
        for (i in 0 until bits) {
            output = output shl 1
            val mask = bitMap[bitIndex]!!
            if ((input and mask) != 0)
                output += 1
            bitIndex++
            if (bitIndex > 3) {
                bitIndex = 0
                hexIndex++
                if (hexIndex >= hexStream.length) {
                    return output
                }
                input = current()
            }
        }
        return output
    }

    private fun current(): Int {
        return hexMap[hexStream[hexIndex]]!!
    }
}