package org.polyfrost.blockoverlay.render

import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.utils.dsl.mc
import net.minecraft.block.*
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.WorldRenderer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumFacing.*
import net.minecraft.util.Vec3i
import org.lwjgl.opengl.GL11
import org.polyfrost.blockoverlay.config.ModConfig
import java.util.*
import net.minecraft.client.renderer.GlStateManager as GL

object OverlayRenderer {
    private val TESSELLATOR: Tessellator = Tessellator.getInstance()
    private val WORLD_RENDERER: WorldRenderer = TESSELLATOR.worldRenderer

    fun drawBlock(box: AxisAlignedBB, side: EnumFacing) {
        GL.translate(box.minX, box.minY, box.minZ)
        GL.scale(box.maxX - box.minX, box.maxY - box.minY, box.maxZ - box.minZ)
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

    private val a = Vec3i(0, 1, 0)  //     a-----------d
    private val b = Vec3i(0, 1, 1)  //    /|          /|      y
    private val c = Vec3i(1, 1, 1)  //   / |         / |      | N
    private val d = Vec3i(1, 1, 0)  //  b-----------c  |      |/
    private val e = Vec3i(0, 0, 0)  //  |  e--------|--h      o-----x E
    private val f = Vec3i(0, 0, 1)  //  | /         | /      /
    private val g = Vec3i(1, 0, 1)  //  |/          |/      z
    private val h = Vec3i(1, 0, 0)  //  f-----------g

    private val pointsMap = EnumMap(
        mapOf(
            UP to listOf(a, b, c, d),
            DOWN to listOf(e, h, g, f),
            NORTH to listOf(a, d, h, e),
            EAST to listOf(d, c, g, h),
            SOUTH to listOf(b, f, g, c),
            WEST to listOf(a, e, f, b),
        )
    )

    private fun drawBlockSide(side: EnumFacing) {
        if (ModConfig.overlayColor && !ModConfig.shapedOverlay) drawRect(
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
                pos(point.x.toDouble(), point.y.toDouble(), point.z.toDouble()).color(color).endVertex()
            }
            TESSELLATOR.draw()
        }
    }

    private fun Int.isEven() = this and 1 == 0
    private fun Int.isOdd() = !isEven()
    private fun WorldRenderer.color(color: OneColor) =
        color(color.red, color.green, color.blue, color.alpha)

    fun drawBlockModelMode(block: Block, blockPos: BlockPos, entityX: Double, entityY: Double, entityZ: Double) {
        when (block) {
            is BlockChest,
            is BlockEnderChest,
            is BlockSign,
            is BlockSkull,
            is BlockAir,
            -> return
        }
        val blockState = mc.theWorld.getBlockState(blockPos)
        val mask = AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
        val list = mutableListOf<AxisAlignedBB>()
        block.addCollisionBoxesToList(mc.theWorld, blockPos, blockState, mask, list, mc.thePlayer)

        GL.pushMatrix()
        GL.tryBlendFuncSeparate(770, 771, 1, 0)
        GL.enableBlend()
        GL.color(1.0f, 1.0f, 1.0f, 0.5f)
        GL.doPolygonOffset(-3.0f, -3.0f)
        GL.enablePolygonOffset()
        GL.alphaFunc(516, 0.1f)
        GL.depthMask(false)
        GL.enableAlpha()
        GL.translate(-entityX, -entityY, -entityZ)

        WORLD_RENDERER.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK)
        WORLD_RENDERER.noColor()
        mc.blockRendererDispatcher.renderBlockDamage(blockState, blockPos, TextureManager.updatedSprite(), mc.theWorld)
        TESSELLATOR.draw()

        GL.disableAlpha()
        GL.doPolygonOffset(0.0f, 0.0f)
        GL.disablePolygonOffset()
        GL.enableAlpha()
        GL.depthMask(true)
        GL.popMatrix()
    }
}