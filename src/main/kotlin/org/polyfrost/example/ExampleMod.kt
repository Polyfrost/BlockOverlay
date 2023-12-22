package org.polyfrost.example

import cc.polyfrost.oneconfig.utils.commands.CommandManager
import net.minecraftforge.fml.common.Mod.*
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import org.polyfrost.example.command.ExampleCommand
import org.polyfrost.example.config.TestConfig


@net.minecraftforge.fml.common.Mod(modid = ExampleMod.MODID, name = ExampleMod.NAME, version = ExampleMod.VERSION)
object ExampleMod {
    const val MODID = "@ID@"
    const val NAME = "@NAME@"
    const val VERSION = "@VER@"

    @Instance(MODID)
    var INSTANCE = ExampleMod
    var config = TestConfig()

    @EventHandler
    fun onInit(event: FMLInitializationEvent) {
        config = TestConfig()
        CommandManager.INSTANCE.registerCommand(ExampleCommand())
    }
}