package machine

const val OP_BUY = "buy"
const val OP_FILL = "fill"
const val OP_TAKE = "take"
const val OP_REMAINING = "remaining"
const val OP_EXIT = "exit"
const val TEXT_ACTION = "\nWrite action (buy, fill, take, remaining, exit): "

data class MachineStatus(
    val amountMoney: Int,
    val waterMl: Int,
    val milkMl: Int,
    val coffeeGr: Int,
    val amountCups: Int
) {
    override fun toString(): String = """
    
    The coffee machine has:
    $waterMl of water
    $milkMl of milk
    $coffeeGr of coffee beans
    $amountCups of disposable cups
    $amountMoney of money
    """.trimIndent()
}

data class CoffeeTypeRequirementAndCost(
    val type: Int,
    val waterRequired: Int,
    val milkRequired: Int,
    val coffeeRequired: Int,
    val cost: Int
)

val coffeTypesMap = mapOf(
    1 to CoffeeTypeRequirementAndCost(1, 250, 0, 16, 4),
    2 to CoffeeTypeRequirementAndCost(2, 350, 75, 20, 7),
    3 to CoffeeTypeRequirementAndCost(3, 200, 100, 12, 6)
)

fun take(current: MachineStatus): MachineStatus {
    println("I gave you \$${current.amountMoney}")
    return current.copy(amountMoney = 0)
}

fun fill(current: MachineStatus): MachineStatus {
    print("\nWrite how many ml of water do you want to add:")
    val waterToAdd = readLine()!!.toInt()
    print("\nWrite how many ml of milk do you want to add:")
    val milkToAdd = readLine()!!.toInt()
    print("\nWrite how many grams of coffee beans do you want to add:")
    val coffeeToAdd = readLine()!!.toInt()
    print("\nWrite how many disposable cups of coffee do you want to add:")
    val cupsToAdd = readLine()!!.toInt()
    return current.let {
        MachineStatus(
            waterMl = it.waterMl + waterToAdd,
            milkMl = it.milkMl + milkToAdd,
            coffeeGr = it.coffeeGr + coffeeToAdd,
            amountCups = it.amountCups + cupsToAdd,
            amountMoney = it.amountMoney
        )
    }
}

fun buy(current: MachineStatus): MachineStatus {
    println("What do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu:")
    val buyType = readLine()!!
    if (buyType != "back") {
        val selectedType = coffeTypesMap[buyType.toInt()]!!
        return current.let {
            val (available, what) = enoughExistingForCoffeeType(selectedType, current)
            if (available) {
                println("I have enough resources, making you a coffee!")
                MachineStatus(
                    amountMoney = it.amountMoney + selectedType.cost,
                    amountCups = it.amountCups - 1,
                    waterMl = it.waterMl - selectedType.waterRequired,
                    milkMl = it.milkMl - selectedType.milkRequired,
                    coffeeGr = it.coffeeGr - selectedType.coffeeRequired
                )
            } else {
                println("Sorry not enough $what!")
                current

            }
        }
    } else return current
}

fun enoughExistingForCoffeeType(
    selectedCoffee: CoffeeTypeRequirementAndCost,
    current: MachineStatus
): Pair<Boolean, String> =
    if (current.coffeeGr >= selectedCoffee.coffeeRequired && current.milkMl >= selectedCoffee.milkRequired && current.waterMl >= selectedCoffee.waterRequired && current.amountCups > 0) true to "" else {
        false to if (current.coffeeGr < selectedCoffee.coffeeRequired) "coffee" else if (current.milkMl < selectedCoffee.milkRequired) "milk" else if (current.waterMl < selectedCoffee.waterRequired) "water" else "cups"
    }


fun remaining(machineStatus: MachineStatus): MachineStatus {
    println(machineStatus)
    return machineStatus
}

fun main() {
    var machineStatus = MachineStatus(550, 400, 540, 120, 9)
    print(TEXT_ACTION)
    var operation = readLine()!!
    while (operation != OP_EXIT) {

        machineStatus = when (operation) {
            OP_TAKE -> take(machineStatus)
            OP_FILL -> fill(machineStatus)
            OP_BUY -> buy(machineStatus)
            OP_REMAINING -> remaining(machineStatus)
            else -> machineStatus
        }
        print(TEXT_ACTION)
        operation = readLine()!!
    }

}


