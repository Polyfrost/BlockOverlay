package org.polyfrost.blockoverlay

import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.*
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import org.polyfrost.blockoverlay.config.ModConfig
import org.polyfrost.blockoverlay.render.Overlay

@Mod(
    modid = PolyBlockOverlay.MODID,
    name = PolyBlockOverlay.NAME,
    version = PolyBlockOverlay.VERSION,
    modLanguageAdapter = "cc.polyfrost.oneconfig.utils.KotlinLanguageAdapter"
)
object PolyBlockOverlay {
    const val MODID = "@ID@"
    const val NAME = "@NAME@"
    const val VERSION = "@VER@"

    @EventHandler
    fun onInit(event: FMLInitializationEvent) {
        ModConfig
        MinecraftForge.EVENT_BUS.register(Overlay)
    }
}