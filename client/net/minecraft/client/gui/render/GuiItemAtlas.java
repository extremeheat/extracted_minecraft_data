package net.minecraft.client.gui.render;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Projection;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;

public class GuiItemAtlas implements AutoCloseable {
   private static final int MINIMUM_TEXTURE_SIZE = 512;
   private static final int MAXIMUM_TEXTURE_SIZE = RenderSystem.getDevice().getMaxTextureSize();
   private final SubmitNodeCollector submitNodeCollector;
   private final FeatureRenderDispatcher featureRenderDispatcher;
   private final MultiBufferSource.BufferSource bufferSource;
   private final int textureSize;
   private final int slotTextureSize;
   private final GpuTexture texture;
   private final GpuTextureView textureView;
   private final GpuTexture depthTexture;
   private final GpuTextureView depthTextureView;
   private final DynamicAtlasAllocator<Object> allocator;
   private final PoseStack poseStack = new PoseStack();
   private final Projection projection = new Projection();
   private final ProjectionMatrixBuffer projectionMatrixBuffer = new ProjectionMatrixBuffer("items");

   public GuiItemAtlas(final SubmitNodeCollector submitNodeCollector, final FeatureRenderDispatcher featureRenderDispatcher, final MultiBufferSource.BufferSource bufferSource, final int textureSize, final int slotTextureSize) {
      super();
      this.submitNodeCollector = submitNodeCollector;
      this.featureRenderDispatcher = featureRenderDispatcher;
      this.bufferSource = bufferSource;
      int storageSize = textureSize / slotTextureSize;
      this.textureSize = textureSize;
      this.slotTextureSize = slotTextureSize;
      GpuDevice device = RenderSystem.getDevice();
      this.texture = device.createTexture("UI items atlas", 13, TextureFormat.RGBA8, textureSize, textureSize, 1, 1);
      this.textureView = device.createTextureView(this.texture);
      this.depthTexture = device.createTexture("UI items atlas depth", 9, TextureFormat.DEPTH32, textureSize, textureSize, 1, 1);
      this.depthTextureView = device.createTextureView(this.depthTexture);
      this.allocator = new DynamicAtlasAllocator<Object>(storageSize, storageSize);
      device.createCommandEncoder().clearColorAndDepthTextures(this.texture, 0, this.depthTexture, 1.0);
   }

   public static int computeTextureSizeFor(final int slotTextureSize, final int requiredSlotCount) {
      int preferredSlotCount = requiredSlotCount + requiredSlotCount / 2;
      int atlasSize = Mth.smallestSquareSide(preferredSlotCount);
      return Math.clamp((long)Mth.smallestEncompassingPowerOfTwo(atlasSize * slotTextureSize), 512, MAXIMUM_TEXTURE_SIZE);
   }

   public void endFrame() {
      this.allocator.endFrame();
   }

   public boolean tryPrepareFor(final Set<Object> items) {
      return this.allocator.hasSpaceForAll(items) ? true : this.allocator.reclaimSpaceFor(items);
   }

   public @Nullable SlotView getOrUpdate(final TrackingItemStackRenderState item) {
      DynamicAtlasAllocator.Slot slot = this.allocator.getOrAllocate(item.getModelIdentity(), item.isAnimated());
      if (slot == null) {
         return null;
      } else {
         switch (slot.state()) {
            case EMPTY:
               this.drawToSlot(slot.x(), slot.y(), false, item);
               break;
            case STALE:
               this.drawToSlot(slot.x(), slot.y(), true, item);
            case READY:
         }

         float slotUvSize = (float)this.slotTextureSize / (float)this.textureSize;
         float u0 = (float)slot.x() * slotUvSize;
         float v0 = 1.0F - (float)slot.y() * slotUvSize;
         return new SlotView(this.textureView, u0, v0, u0 + slotUvSize, v0 - slotUvSize);
      }
   }

   private void drawToSlot(final int slotX, final int slotY, final boolean clear, final ItemStackRenderState item) {
      int left = slotX * this.slotTextureSize;
      int top = slotY * this.slotTextureSize;
      int bottom = top + this.slotTextureSize;
      GpuDevice device = RenderSystem.getDevice();
      if (clear) {
         device.createCommandEncoder().clearColorAndDepthTextures(this.texture, 0, this.depthTexture, 1.0, left, this.textureSize - bottom, this.slotTextureSize, this.slotTextureSize);
      }

      this.poseStack.pushPose();
      this.poseStack.translate((float)left + (float)this.slotTextureSize / 2.0F, (float)top + (float)this.slotTextureSize / 2.0F, 0.0F);
      this.poseStack.scale((float)this.slotTextureSize, (float)(-this.slotTextureSize), (float)this.slotTextureSize);
      RenderSystem.outputColorTextureOverride = this.textureView;
      RenderSystem.outputDepthTextureOverride = this.depthTextureView;
      this.projection.setupOrtho(-1000.0F, 1000.0F, (float)this.textureSize, (float)this.textureSize, true);
      RenderSystem.setProjectionMatrix(this.projectionMatrixBuffer.getBuffer(this.projection), ProjectionType.ORTHOGRAPHIC);
      RenderSystem.enableScissorForRenderTypeDraws(left, this.textureSize - bottom, this.slotTextureSize, this.slotTextureSize);
      Lighting.Entry lighting = item.usesBlockLight() ? Lighting.Entry.ITEMS_3D : Lighting.Entry.ITEMS_FLAT;
      Minecraft.getInstance().gameRenderer.getLighting().setupFor(lighting);
      item.submit(this.poseStack, this.submitNodeCollector, 15728880, OverlayTexture.NO_OVERLAY, 0);
      this.featureRenderDispatcher.renderAllFeatures();
      this.bufferSource.endBatch();
      RenderSystem.disableScissorForRenderTypeDraws();
      RenderSystem.outputColorTextureOverride = null;
      RenderSystem.outputDepthTextureOverride = null;
      this.poseStack.popPose();
   }

   public int textureSize() {
      return this.textureSize;
   }

   public void close() {
      this.texture.close();
      this.textureView.close();
      this.depthTexture.close();
      this.depthTextureView.close();
      this.projectionMatrixBuffer.close();
   }

   public static record SlotView(GpuTextureView textureView, float u0, float v0, float u1, float v1) {
      public SlotView {
         super();
      }
   }
}
