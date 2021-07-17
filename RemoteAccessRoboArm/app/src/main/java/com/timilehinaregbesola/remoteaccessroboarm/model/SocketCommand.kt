package com.timilehinaregbesola.remoteaccessroboarm.model

data class SocketCommand(
    val commandRepeat: CommandAndRepeat,
    val commands: List<Command>
)

data class Command(
    val commandId: Int,
    val angle: Int
)

data class CommandAndRepeat(
    val commandNo: Int,
    val repeatNo: Int
)

fun socketCommandToString(socketCommand: SocketCommand): String {
    var commandListSB: StringBuilder = StringBuilder("")
    socketCommand.commands.forEachIndexed { index, command ->
        commandListSB.append("${command.commandId};${command.angle}")
        if (index != socketCommand.commands.lastIndex) commandListSB.append(":")
    }
    return "${socketCommand.commandRepeat.commandNo};${socketCommand.commandRepeat.repeatNo}:$commandListSB"
}
