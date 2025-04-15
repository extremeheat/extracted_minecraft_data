package net.minecraft.client.gui.render;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.BlitRenderState;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.gui.render.state.GuiItemRenderState;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.TextRenderState;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.CachedOrthoProjectionMatrixBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.slf4j.Logger;

public class GuiRenderer implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final float MAX_GUI_Z = 10000.0F;
   public static final float MIN_GUI_Z = 0.0F;
   private static final float GUI_Z_NEAR = 1000.0F;
   public static final int GUI_3D_Z_FAR = 1000;
   public static final int GUI_3D_Z_NEAR = -1000;
   private static final int DEFAULT_ITEM_SIZE = 16;
   private static final int MINIMUM_ITEM_ATLAS_SIZE = 512;
   private static final int MAXIMUM_ITEM_ATLAS_SIZE = 2048;
   public static final int CLEAR_COLOR = 0;
   private static final Comparator<ScreenRectangle> SCISSOR_COMPARATOR = Comparator.nullsFirst(Comparator.comparing(ScreenRectangle::top).thenComparing(ScreenRectangle::bottom).thenComparing(ScreenRectangle::left).thenComparing(ScreenRectangle::right));
   private static final Comparator<TextureSetup> TEXTURE_COMPARATOR = Comparator.nullsFirst(Comparator.comparing(TextureSetup::hashCode));
   private static final Comparator<GuiElementRenderState> ELEMENT_SORT_COMPARATOR;
   private final Map<Object, AtlasPosition> atlasPositions = new Object2ObjectOpenHashMap();
   private final GuiRenderState renderState;
   private final List<Draw> draws = new ArrayList();
   private final ByteBufferBuilder byteBufferBuilder = new ByteBufferBuilder(786432);
   private final CachedOrthoProjectionMatrixBuffer guiProjectionMatrixBuffer = new CachedOrthoProjectionMatrixBuffer("gui", 1000.0F, 11000.0F, true);
   private final CachedOrthoProjectionMatrixBuffer itemsProjectionMatrixBuffer = new CachedOrthoProjectionMatrixBuffer("items", -1000.0F, 1000.0F, true);
   private final MultiBufferSource.BufferSource bufferSource;
   private final Map<Class<? extends PictureInPictureRenderState>, PictureInPictureRenderer<?>> pictureInPictureRenderers;
   @Nullable
   private GpuTexture itemsAtlas;
   @Nullable
   private GpuTexture itemsAtlasDepth;
   private int itemAtlasX;
   private int itemAtlasY;
   private int cachedGuiScale;
   private int frameNumber;
   @Nullable
   private ScreenRectangle previousScissorArea = null;
   @Nullable
   private RenderPipeline previousPipeline = null;
   @Nullable
   private TextureSetup previousTextureSetup = null;
   @Nullable
   private BufferBuilder bufferBuilder = null;

   public GuiRenderer(GuiRenderState var1, MultiBufferSource.BufferSource var2, List<PictureInPictureRenderer<?>> var3) {
      super();
      this.renderState = var1;
      this.bufferSource = var2;
      ImmutableMap.Builder var4 = ImmutableMap.builder();

      for(PictureInPictureRenderer var6 : var3) {
         var4.put(var6.getRenderStateClass(), var6);
      }

      this.pictureInPictureRenderers = var4.buildOrThrow();
   }

   public void incrementFrameNumber() {
      ++this.frameNumber;
   }

   public void render(GpuBufferSlice var1) {
      this.bufferSource.endBatch();
      this.preparePictureInPicture();
      this.prepareItemElements();
      this.prepareText();
      this.renderState.sortElements(ELEMENT_SORT_COMPARATOR);
      this.previousScissorArea = null;
      this.previousPipeline = null;
      this.previousTextureSetup = null;
      this.bufferBuilder = null;
      this.renderState.forEachElement((var1x, var2x) -> {
         RenderPipeline var3 = var1x.pipeline();
         TextureSetup var4 = var1x.textureSetup();
         ScreenRectangle var5 = var1x.scissorArea();
         if (var3 != this.previousPipeline || this.scissorChanged(var5, this.previousScissorArea) || !var4.equals(this.previousTextureSetup)) {
            if (this.bufferBuilder != null) {
               this.recordDraw(this.bufferBuilder, this.previousPipeline, this.previousTextureSetup, this.previousScissorArea);
            }

            this.bufferBuilder = this.getBufferBuilder(var3);
            this.previousPipeline = var3;
            this.previousTextureSetup = var4;
            this.previousScissorArea = var5;
         }

         var1x.buildVertices(this.bufferBuilder, 0.0F + (float)var2x * 0.01F);
      });
      if (this.bufferBuilder != null) {
         this.recordDraw(this.bufferBuilder, this.previousPipeline, this.previousTextureSetup, this.previousScissorArea);
      }

      Window var2 = Minecraft.getInstance().getWindow();
      RenderSystem.setProjectionMatrix(this.guiProjectionMatrixBuffer.getBuffer((float)var2.getWidth() / (float)var2.getGuiScale(), (float)var2.getHeight() / (float)var2.getGuiScale()), ProjectionType.ORTHOGRAPHIC);
      RenderTarget var3 = Minecraft.getInstance().getMainRenderTarget();
      int var4 = 0;

      for(Draw var6 : this.draws) {
         if (var6.indexBuffer == null && var6.indexCount > var4) {
            var4 = var6.indexCount;
         }
      }

      RenderSystem.AutoStorageIndexBuffer var14 = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
      GpuBuffer var15 = var14.getBuffer(var4);
      VertexFormat.IndexType var7 = var14.type();
      GpuBufferSlice var8 = RenderSystem.getDynamicUniforms().writeTransform((new Matrix4f()).setTranslation(0.0F, 0.0F, -11000.0F), new Vector4f(1.0F, 1.0F, 1.0F, 1.0F), new Vector3f(), new Matrix4f(), 0.0F);

      try (RenderPass var9 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(var3.getColorTexture(), OptionalInt.empty(), var3.useDepth ? var3.getDepthTexture() : null, OptionalDouble.empty())) {
         RenderSystem.bindDefaultUniforms(var9);
         var9.setUniform("Fog", var1);
         var9.setUniform("DynamicTransforms", var8);

         for(Draw var11 : this.draws) {
            this.executeDraw(var11, var9, var15, var7);
         }
      }

      this.draws.clear();
      this.renderState.reset();
   }

   private void prepareText() {
      this.renderState.forEachText((var1) -> {
         this.renderState.down();
         TextRenderState var2 = var1.textRenderState();
         BakedGlyph var3 = var2.whiteGlyph();
         boolean var4 = var2.effects() != null;
         if (var2.backgroundColor() != 0) {
            BakedGlyph.Effect var5 = new BakedGlyph.Effect((float)(var1.x() - 1), (float)(var1.y() - 1), var2.endX(), (float)(var1.y() + var2.lineHeight()), 0.0F, var2.backgroundColor());
            RenderType var6 = var3.renderType(var2.mode());
            var3.extractBackground(this.renderState, var6, var5, var1);
         }

         this.renderState.up();

         for(BakedGlyph.GlyphInstance var13 : var2.glyphInstances()) {
            BakedGlyph var7 = var13.glyph();
            RenderType var8 = var7.renderType(var2.mode());
            var7.extractChar(this.renderState, var8, var13, true, var1);
         }

         if (var4) {
            this.renderState.up();
            RenderType var10 = var3.renderType(var2.mode());

            for(BakedGlyph.Effect var17 : var2.effects()) {
               var3.extractEffect(this.renderState, var10, var17, true, var1);
            }
         }

         this.renderState.up();

         for(BakedGlyph.GlyphInstance var15 : var2.glyphInstances()) {
            BakedGlyph var18 = var15.glyph();
            RenderType var20 = var18.renderType(var2.mode());
            var18.extractChar(this.renderState, var20, var15, false, var1);
         }

         if (var4) {
            this.renderState.up();
            RenderType var12 = var3.renderType(var2.mode());

            for(BakedGlyph.Effect var19 : var2.effects()) {
               var3.extractEffect(this.renderState, var12, var19, false, var1);
            }
         }

      });
   }

   private void prepareItemElements() {
      if (!this.renderState.getItemModelIdentities().isEmpty()) {
         int var1 = this.getGuiScaleInvalidatingItemAtlasIfChanged();
         int var2 = this.getItemCount();
         int var3 = 16 * var1;
         int var4 = this.calculateAtlasSizeInPixels(var2, var3);
         if (this.itemsAtlas == null) {
            this.createAtlasTextures(var4);
         }

         RenderSystem.outputColorTextureOverride = this.itemsAtlas;
         RenderSystem.outputDepthTextureOverride = this.itemsAtlasDepth;
         RenderSystem.setProjectionMatrix(this.itemsProjectionMatrixBuffer.getBuffer((float)var4, (float)var4), ProjectionType.ORTHOGRAPHIC);
         Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_3D);
         PoseStack var5 = new PoseStack();
         MutableBoolean var6 = new MutableBoolean(false);
         this.renderState.forEachItem((var5x) -> {
            ItemStackRenderState var6x = var5x.itemStackRenderState();
            AtlasPosition var7 = (AtlasPosition)this.atlasPositions.get(var6x.getModelIdentity());
            if (var7 == null || var6x.isAnimated() && var7.lastAnimatedOnFrame != this.frameNumber) {
               if (this.itemAtlasX + var3 > var4) {
                  this.itemAtlasX = 0;
                  this.itemAtlasY += var3;
               }

               boolean var8 = var6x.isAnimated() && var7 != null;
               if (!var8 && this.itemAtlasY + var3 > var4) {
                  if (var6.isFalse()) {
                     LOGGER.warn("Trying to render too many items in GUI at the same time. Skipping some of them.");
                     var6.setTrue();
                  }

               } else {
                  int var9 = var8 ? var7.x : this.itemAtlasX;
                  int var10 = var8 ? var7.y : this.itemAtlasY;
                  if (var8) {
                     RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(this.itemsAtlas, 0, this.itemsAtlasDepth, 1.0, var9, var4 - var10 - var3, var3, var3);
                  }

                  this.renderItemToAtlas(var6x, var5, var9, var10, var3);
                  float var11 = (float)var9 / (float)var4;
                  float var12 = (float)(var4 - var10) / (float)var4;
                  this.submitBlitFromItemAtlas(var5x, var11, var12, var3, var4);
                  if (var8) {
                     var7.lastAnimatedOnFrame = this.frameNumber;
                  } else {
                     this.atlasPositions.put(var5x.itemStackRenderState().getModelIdentity(), new AtlasPosition(this.itemAtlasX, this.itemAtlasY, var11, var12, this.frameNumber));
                     this.itemAtlasX += var3;
                  }

               }
            } else {
               this.submitBlitFromItemAtlas(var5x, var7.u, var7.v, var3, var4);
            }
         });
         RenderSystem.outputColorTextureOverride = null;
         RenderSystem.outputDepthTextureOverride = null;
      }
   }

   private void preparePictureInPicture() {
      int var1 = Minecraft.getInstance().getWindow().getGuiScale();
      this.renderState.forEachPictureInPicture((var2) -> this.preparePictureInPictureState(var2, var1));
   }

   private <T extends PictureInPictureRenderState> void preparePictureInPictureState(T var1, int var2) {
      PictureInPictureRenderer var3 = (PictureInPictureRenderer)this.pictureInPictureRenderers.get(var1.getClass());
      if (var3 != null) {
         var3.prepare(var1, this.renderState, var2);
      }

   }

   private void renderItemToAtlas(ItemStackRenderState var1, PoseStack var2, int var3, int var4, int var5) {
      var2.pushPose();
      var2.translate((float)var3 + (float)var5 / 2.0F, (float)var4 + (float)var5 / 2.0F, 0.0F);
      var2.scale((float)var5, (float)(-var5), (float)var5);
      boolean var6 = !var1.usesBlockLight();
      if (var6) {
         Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_FLAT);
      } else {
         Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_3D);
      }

      var1.render(var2, this.bufferSource, 15728880, OverlayTexture.NO_OVERLAY);
      this.bufferSource.endBatch();
      var2.popPose();
   }

   private void submitBlitFromItemAtlas(GuiItemRenderState var1, float var2, float var3, int var4, int var5) {
      float var6 = var2 + (float)var4 / (float)var5;
      float var7 = var3 + (float)(-var4) / (float)var5;
      this.renderState.submitGuiElement(new BlitRenderState(RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA, TextureSetup.singleTexture(this.itemsAtlas), var1.pose(), var1.x(), var1.y(), var1.x() + 16, var1.y() + 16, var2, var6, var3, var7, -1, var1.scissorArea()));
   }

   private void createAtlasTextures(int var1) {
      this.itemsAtlas = RenderSystem.getDevice().createTexture("UI items atlas", TextureFormat.RGBA8, var1, var1, 1);
      this.itemsAtlas.setTextureFilter(FilterMode.NEAREST, false);
      this.itemsAtlasDepth = RenderSystem.getDevice().createTexture("UI items atlas depth", TextureFormat.DEPTH32, var1, var1, 1);
      RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(this.itemsAtlas, 0, this.itemsAtlasDepth, 1.0);
   }

   private int calculateAtlasSizeInPixels(int var1, int var2) {
      int var3 = Mth.smallestSquareSide(var1);
      int var4 = Math.max(512, Mth.smallestEncompassingPowerOfTwo(var3 * var2));
      if (var4 > 2048) {
         this.invalidateItemAtlas();
         var4 = 2048;
      } else if (this.itemsAtlas != null && this.itemsAtlas.getWidth(0) < var4) {
         this.invalidateItemAtlas();
      } else if (this.itemsAtlas != null) {
         var4 = this.itemsAtlas.getWidth(0);
      }

      return var4;
   }

   private int getItemCount() {
      Set var1 = this.renderState.getItemModelIdentities();
      int var2;
      if (this.atlasPositions.isEmpty()) {
         var2 = var1.size();
      } else {
         var2 = this.atlasPositions.size();

         for(Object var4 : var1) {
            if (!this.atlasPositions.containsKey(var4)) {
               ++var2;
            }
         }
      }

      return var2;
   }

   private int getGuiScaleInvalidatingItemAtlasIfChanged() {
      int var1 = Minecraft.getInstance().getWindow().getGuiScale();
      if (var1 != this.cachedGuiScale) {
         this.invalidateItemAtlas();
         this.cachedGuiScale = var1;
      }

      return var1;
   }

   private void invalidateItemAtlas() {
      this.itemAtlasX = 0;
      this.itemAtlasY = 0;
      this.atlasPositions.clear();
      if (this.itemsAtlas != null) {
         this.itemsAtlas.close();
         this.itemsAtlas = null;
      }

      if (this.itemsAtlasDepth != null) {
         this.itemsAtlasDepth.close();
         this.itemsAtlasDepth = null;
      }

   }

   private void recordDraw(BufferBuilder var1, RenderPipeline var2, TextureSetup var3, @Nullable ScreenRectangle var4) {
      try (MeshData var5 = var1.buildOrThrow()) {
         GpuBuffer var6 = RenderSystem.getDevice().createBuffer(() -> "TODO GUI vertex buffer", 32, var5.vertexBuffer());
         GpuBuffer var7;
         VertexFormat.IndexType var8;
         if (var5.indexBuffer() != null) {
            var7 = RenderSystem.getDevice().createBuffer(() -> "TODO GUI index buffer", 64, var5.indexBuffer());
            var8 = var5.drawState().indexType();
         } else {
            var7 = null;
            var8 = null;
         }

         this.draws.add(new Draw(var6, var5.drawState().mode(), var7, var8, var5.drawState().indexCount(), var2, var3, var4));
      }

   }

   private void executeDraw(Draw var1, RenderPass var2, GpuBuffer var3, VertexFormat.IndexType var4) {
      Draw var5 = var1;

      try {
         RenderPipeline var6 = var1.pipeline();
         var2.setPipeline(var6);
         var2.setVertexBuffer(0, var1.vertexBuffer);
         ScreenRectangle var7 = var1.scissorArea();
         if (var7 != null) {
            this.enableScissor(var7, var2);
         } else {
            var2.disableScissor();
         }

         if (var1.textureSetup.texure0() != null) {
            var2.bindSampler("Sampler0", var1.textureSetup.texure0());
         }

         if (var1.textureSetup.texure1() != null) {
            var2.bindSampler("Sampler1", var1.textureSetup.texure1());
         }

         if (var1.textureSetup.texure2() != null) {
            var2.bindSampler("Sampler2", var1.textureSetup.texure2());
         }

         if (var1.indexBuffer() != null) {
            var2.setIndexBuffer(var1.indexBuffer(), var1.indexType);
         } else {
            var2.setIndexBuffer(var3, var4);
         }

         var2.drawIndexed(0, var1.indexCount);
      } catch (Throwable var9) {
         if (var1 != null) {
            try {
               var5.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }
         }

         throw var9;
      }

      if (var1 != null) {
         var1.close();
      }

   }

   private BufferBuilder getBufferBuilder(RenderPipeline var1) {
      return new BufferBuilder(this.byteBufferBuilder, var1.getVertexFormatMode(), var1.getVertexFormat());
   }

   private boolean scissorChanged(ScreenRectangle var1, @Nullable ScreenRectangle var2) {
      if (var1 == var2) {
         return false;
      } else if (var1 != null) {
         return !var1.equals(var2);
      } else {
         return true;
      }
   }

   private void enableScissor(ScreenRectangle var1, RenderPass var2) {
      Window var3 = Minecraft.getInstance().getWindow();
      int var4 = var3.getHeight();
      int var5 = var3.getGuiScale();
      double var6 = (double)(var1.left() * var5);
      double var8 = (double)(var4 - var1.bottom() * var5);
      double var10 = (double)(var1.width() * var5);
      double var12 = (double)(var1.height() * var5);
      var2.enableScissor((int)var6, (int)var8, Math.max(0, (int)var10), Math.max(0, (int)var12));
   }

   public void close() {
      this.byteBufferBuilder.close();
      if (this.itemsAtlas != null) {
         this.itemsAtlas.close();
      }

      if (this.itemsAtlasDepth != null) {
         this.itemsAtlasDepth.close();
      }

      this.pictureInPictureRenderers.values().forEach(PictureInPictureRenderer::close);
      this.guiProjectionMatrixBuffer.close();
      this.itemsProjectionMatrixBuffer.close();
   }

   static {
      ELEMENT_SORT_COMPARATOR = Comparator.comparing(GuiElementRenderState::scissorArea, SCISSOR_COMPARATOR).thenComparing(GuiElementRenderState::pipeline, Comparator.comparing(RenderPipeline::getSortKey)).thenComparing(GuiElementRenderState::textureSetup, TEXTURE_COMPARATOR);
   }

   static record Draw(GpuBuffer vertexBuffer, VertexFormat.Mode mode, @Nullable GpuBuffer indexBuffer, @Nullable VertexFormat.IndexType indexType, int indexCount, RenderPipeline pipeline, TextureSetup textureSetup, @Nullable ScreenRectangle scissorArea) implements AutoCloseable {
      final GpuBuffer vertexBuffer;
      @Nullable
      final GpuBuffer indexBuffer;
      @Nullable
      final VertexFormat.IndexType indexType;
      final int indexCount;
      final TextureSetup textureSetup;

      Draw(GpuBuffer var1, VertexFormat.Mode var2, @Nullable GpuBuffer var3, @Nullable VertexFormat.IndexType var4, int var5, RenderPipeline var6, TextureSetup var7, @Nullable ScreenRectangle var8) {
         super();
         this.vertexBuffer = var1;
         this.mode = var2;
         this.indexBuffer = var3;
         this.indexType = var4;
         this.indexCount = var5;
         this.pipeline = var6;
         this.textureSetup = var7;
         this.scissorArea = var8;
      }

      public void close() {
         this.vertexBuffer.close();
         if (this.indexBuffer != null) {
            this.indexBuffer.close();
         }

      }
   }

   static final class AtlasPosition {
      final int x;
      final int y;
      final float u;
      final float v;
      int lastAnimatedOnFrame;

      AtlasPosition(int var1, int var2, float var3, float var4, int var5) {
         super();
         this.x = var1;
         this.y = var2;
         this.u = var3;
         this.v = var4;
         this.lastAnimatedOnFrame = var5;
      }
   }
}
