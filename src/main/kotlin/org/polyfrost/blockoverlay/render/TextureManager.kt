package org.polyfrost.blockoverlay.render

import cc.polyfrost.oneconfig.utils.dsl.mc
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.ResourceLocation
import org.polyfrost.blockoverlay.config.ModConfig

object TextureManager {
    private val overlayLocation = ResourceLocation("block_overlay:overlay")
    private val overlayTexture = DynamicTexture(1, 1)
    private val overlaySprite: TextureAtlasSprite

    init {
        updateColor()
        mc.textureManager.loadTexture(overlayLocation, overlayTexture)
        overlaySprite = mc.textureMapBlocks.registerSprite(overlayLocation)
    }

    fun updateColor() {
        overlayTexture.textureData.fill(ModConfig.colorFO.rgb)
        overlayTexture.updateDynamicTexture()
    }

    fun updatedSprite(): TextureAtlasSprite {
        mc.textureManager.bindTexture(overlayLocation)
        overlayTexture.textureData.fill(ModConfig.colorFO.rgb)
        overlayTexture.updateDynamicTexture()
        return overlaySprite
    }
}