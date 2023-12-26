package org.polyfrost.blockoverlay.render

import cc.polyfrost.oneconfig.utils.dsl.mc
import net.minecraft.block.Block
import net.minecraft.block.BlockBush
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.inventory.IInventory
import net.minecraft.util.MovingObjectPosition
import net.minecraftforge.client.event.DrawBlockHighlightEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11
import org.polyfrost.blockoverlay.config.ModConfig
import net.minecraft.client.renderer.GlStateManager as GL

object OverlayEventHandler {
    private const val PADDING = 0.002

    @SubscribeEvent
    fun onRenderBlockOverlay(event: DrawBlockHighlightEvent) {
        if (!ModConfig.enabled) return
        when (ModConfig.mode) {
            0 -> { // Hidden
                event.isCanceled = true
                renderBlockBreakOverlay(event.player, event.partialTicks)
            }

            1 -> { // Vanilla
                renderBlockBreakOverlay(event.player, event.partialTicks)
            }

            2, 3 -> { // Side | Full
                event.isCanceled = true
            }
        }
    }

    @SubscribeEvent
    fun onRenderBlockOverlay(event: RenderWorldLastEvent) {
        if (!ModConfig.enabled) return
        if (ModConfig.mode != 2 && ModConfig.mode != 3) return
        val entity = mc.renderViewEntity ?: return
        if (mc.gameSettings.hideGUI) return
        val block = getFocusedBlock() ?: return
        val isAdventure = mc.playerController.currentGameType.isAdventure
        if (isAdventure && !ModConfig.persistent && !canRenderBlockOverlay()) return
        renderBlockOverlay(block, entity, event.partialTicks)
    }

    private fun lerp(a: Double, b: Double, weight: Float) = a + (b - a) * weight

    private fun renderBlockOverlay(block: Block, entity: Entity, partialTicks: Float) {
        val entityX = lerp(entity.lastTickPosX, entity.posX, partialTicks)
        val entityY = lerp(entity.lastTickPosY, entity.posY, partialTicks)
        val entityZ = lerp(entity.lastTickPosZ, entity.posZ, partialTicks)
        val thickness = ModConfig.lineWidth
        val outline = ModConfig.outlineColor
        val mouseOver = mc.objectMouseOver
        val blockPos = mouseOver.blockPos
        val boundingBox = block.getSelectedBoundingBox(mc.theWorld, blockPos).expand(PADDING, PADDING, PADDING)
        val side = mouseOver.sideHit
        GL11.glPushMatrix()
        GL.disableAlpha()
        GL.enableBlend()
        GL.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO)
        GL.disableTexture2D()
        GL.depthMask(false)
        if (ModConfig.ignoreDepth) GL.disableDepth()
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST)
        if (outline) GL11.glLineWidth(thickness)
        GL11.glShadeModel(GL11.GL_SMOOTH)
        OverlayRenderer.drawBlock(boundingBox.offset(-entityX, -entityY, -entityZ), side)
        GL11.glLineWidth(2.0f)
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
        GL.enableDepth()
        GL.depthMask(true)
        GL.enableTexture2D()
        GL.enableAlpha()
        GL11.glPopMatrix()
        renderBlockBreakOverlay(entity, partialTicks)
        if (ModConfig.shapedOverlay) OverlayRenderer.drawBlockModelMode(block, blockPos, entityX, entityY, entityZ)
    }

    private fun getFocusedBlock(): Block? {
        val mouseOver = mc.objectMouseOver ?: return null
        if (mouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return null
        val blockPos = mouseOver.blockPos
        if (!mc.theWorld.worldBorder.contains(blockPos)) return null
        val block = mc.theWorld.getBlockState(blockPos).block ?: return null
        if (block.material === Material.air) return null
        if (ModConfig.hidePlants && block is BlockBush) return null
        if (!ModConfig.barriers && block === Blocks.barrier) return null
        block.setBlockBoundsBasedOnState(mc.theWorld, blockPos)
        return block
    }

    private fun canRenderBlockOverlay(): Boolean {
        val entity = mc.renderViewEntity
        if (entity !is EntityPlayer) return false
        if (entity.capabilities.allowEdit) return true
        val heldItem = entity.currentEquippedItem ?: return false
        val hovering = mc.objectMouseOver ?: return false
        if (hovering.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return false
        val blockPos = hovering.blockPos
        val blockState = mc.theWorld.getBlockState(blockPos)
        val block = blockState.block
        return if (mc.playerController.isSpectator) {
            block.hasTileEntity(blockState) && mc.theWorld.getTileEntity(blockPos) is IInventory
        } else {
            heldItem.canDestroy(block) || heldItem.canPlaceOn(block)
        }
    }

    private fun renderBlockBreakOverlay(entity: Entity, partialTicks: Float) {
        GL.enableBlend()
        GL.tryBlendFuncSeparate(770, 1, 1, 0)
        mc.textureManager.getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, false)
        mc.renderGlobal.drawBlockDamageTexture(Tessellator.getInstance(), Tessellator.getInstance().worldRenderer, entity, partialTicks)
        mc.textureManager.getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap()
        GL.disableBlend()
    }
}