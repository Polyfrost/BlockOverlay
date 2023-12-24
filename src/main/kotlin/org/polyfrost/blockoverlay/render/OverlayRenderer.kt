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
import java.util.EnumMap

object OverlayRenderer {
    private val TESSELLATOR: Tessellator = Tessellator.getInstance()
    private val WORLD_RENDERER: WorldRenderer = TESSELLATOR.worldRenderer

    fun drawBlock(box: AxisAlignedBB, side: EnumFacing) {
        GlStateManager.translate(box.minX, box.minY, box.minZ)
        GlStateManager.scale(box.maxX - box.minX, box.maxY - box.minY, box.maxZ - box.minZ)
        if (ModConfig.mode == 3) {
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

    private fun vec3(x: Int, y: Int, z: Int) = doubleArrayOf(x.toDouble(), y.toDouble(), z.toDouble())
    private val a = vec3(0, 1, 0)  //     a-----------d
    private val b = vec3(0, 1, 1)  //    /|          /|      y
    private val c = vec3(1, 1, 1)  //   / |         / |      | N
    private val d = vec3(1, 1, 0)  //  b-----------c  |      |/
    private val e = vec3(0, 0, 0)  //  |  e--------|--h      o-----x E
    private val f = vec3(0, 0, 1)  //  | /         | /      /
    private val g = vec3(1, 0, 1)  //  |/          |/      z
    private val h = vec3(1, 0, 0)  //  f-----------g

    private val pointsMap = EnumMap(mapOf(
        UP to listOf(a, b, c, d),
        DOWN to listOf(e, h, g, f),
        NORTH to listOf(a, d, h, e),
        EAST to listOf(d, c, g, h),
        SOUTH to listOf(b, f, g, c),
        WEST to listOf(a, e, f, b),
    ))

    private fun drawBlockSide(side: EnumFacing) {
        if (ModConfig.overlayColor) drawRect(
            glMode = GL11.GL_QUADS,
            side = side,
            startColor = ModConfig.colorFO,
            endColor = ModConfig.colorSO,
        )
        if (ModConfig.outlineColor) drawRect(
            glMode = GL11.GL_LINE_LOOP,
            side = side,
            startColor = ModConfig.colorFU,
            endColor = ModConfig.colorSU,
        )
    }

    private fun drawRect(glMode: Int, side: EnumFacing, startColor: OneColor, endColor: OneColor) {
        val points = pointsMap[side] ?: return
        with(WORLD_RENDERER) {
            begin(glMode, DefaultVertexFormats.POSITION_COLOR)
            for (i in 0..3) {
                val color = if (ModConfig.gradient && i.isOdd()) endColor else startColor
                val point = points[i]
                pos(point[0], point[1], point[2]).color(color).endVertex()
            }
            TESSELLATOR.draw()
        }
    }

    private fun Int.isEven() = this and 1 == 0
    private fun Int.isOdd() = !isEven()
    private fun WorldRenderer.color(color: OneColor) =
        color(color.red, color.green, color.blue, color.alpha)

}