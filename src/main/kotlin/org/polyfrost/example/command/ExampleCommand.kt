package org.polyfrost.example.command

import cc.polyfrost.oneconfig.utils.commands.annotations.Command
import cc.polyfrost.oneconfig.utils.commands.annotations.Main
import org.polyfrost.example.ExampleMod

@Command(value = ExampleMod.MODID, description = "Access the ${ExampleMod.NAME} GUI.")
class ExampleCommand {
    @Main
    private fun handle() {
        ExampleMod.INSTANCE.config.openGui()
    }
}