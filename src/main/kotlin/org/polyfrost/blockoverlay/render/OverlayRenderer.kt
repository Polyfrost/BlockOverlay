package org.polyfrost.blockoverlay.render

import cc.polyfrost.oneconfig.config.core.OneColor
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.WorldRenderer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumFacing.*
import org.lwjgl.opengl.GL11
import org.polyfrost.blockoverlay.config.ModConfig

object OverlayRenderer {
    private val TESSELLATOR: Tessellator = Tessellator.getInstance()
    private val WORLD_RENDERER: WorldRenderer = TESSELLATOR.worldRenderer

    fun drawBlock(box: AxisAlignedBB, side: EnumFacing?) {
        GlStateManager.translate(box.minX, box.minY, box.minZ)
        GlStateManager.scale(box.maxX - box.minX, box.maxY - box.minY, box.maxZ - box.minZ)
        if (side == null) {
            drawBlockFull()
        } else {
            drawBlockSide(side)
        }
    }

    private fun drawBlockFull() {
        for (side in EnumFacing.entries) {
            drawBlockSide(side)
        }
    }

    private fun vec3(vararg elements: Double) = doubleArrayOf(*elements)
    private val a = vec3(0.0, 1.0, 0.0)
    private val b = vec3(0.0, 1.0, 1.0)
    private val c = vec3(1.0, 1.0, 1.0)
    private val d = vec3(1.0, 1.0, 0.0)
    private val e = vec3(0.0, 0.0, 0.0)
    private val f = vec3(0.0, 0.0, 1.0)
    private val g = vec3(1.0, 0.0, 1.0)
    private val h = vec3(1.0, 0.0, 0.0)

    /*/ 🤓🤓🤓
        b----------c
       /|         /|
      / |        / |
     a----------d  |
     |  f-------|--g
     | /        | /
     |/         |/
     e----------h
     */

    private val pointsMap = mapOf(
        UP to listOf(a, b, c, d),
        DOWN to listOf(e, h, g, f),
        NORTH to listOf(a, d, h, e),
        EAST to listOf(d, c, g, h),
        SOUTH to listOf(b, f, g, c),
        WEST to listOf(a, e, f, b),
    )

    private fun drawBlockSide(side: EnumFacing) {
        val points = pointsMap[side] ?: return
        if (ModConfig.overlayColor) drawRect(
            GL11.GL_QUADS,
            points,
            ModConfig.colorFO, if (ModConfig.gradient) ModConfig.colorSO else ModConfig.colorFO
        )
        if (ModConfig.outlineColor) drawRect(
            GL11.GL_LINE_LOOP,
            points,
            ModConfig.colorFU, if (ModConfig.gradient) ModConfig.colorSU else ModConfig.colorFU
        )
    }

    private fun drawRect(glMode: Int, points: List<DoubleArray>, startColor: OneColor, endColor: OneColor) {
        with(WORLD_RENDERER) {
            begin(glMode, DefaultVertexFormats.POSITION_COLOR)
            for (i in 0..3) {
                val color = if (i and 1 == 0) startColor else endColor
                pos(points[i][0], points[i][1], points[i][2]).color(color).endVertex()
            }
            TESSELLATOR.draw()
        }
    }

    private fun WorldRenderer.color(color: OneColor) =
        color(color.red, color.green, color.blue, color.alpha)

}