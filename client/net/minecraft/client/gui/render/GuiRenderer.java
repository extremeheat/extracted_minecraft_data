package net.minecraft.client.gui.render;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.buffers.BufferType;
import com.mojang.blaze3d.buffers.BufferUsage;
import com.mojang.blaze3d.buffers.GpuBuffer;
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
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.BlitRenderState;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.gui.render.state.GuiItemRenderState;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.GuiTextRenderState;
import net.minecraft.client.gui.render.state.TextRenderState;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
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
   private static final Comparator<ScreenRectangle> scissorComparator = Comparator.nullsFirst(Comparator.comparing(ScreenRectangle::top).thenComparing(ScreenRectangle::bottom).thenComparing(ScreenRectangle::left).thenComparing(ScreenRectangle::right));
   private static final Comparator<GuiElementRenderState> ELEMENT_SORT_COMPARATOR;
   private final Map<Object, AtlasPosition> atlasPositions = new Object2ObjectOpenHashMap();
   private final GuiRenderState renderState;
   private final List<Draw> draws = new ArrayList();
   private final ByteBufferBuilder byteBufferBuilder = new ByteBufferBuilder(786432);
   private final MultiBufferSource.BufferSource bufferSource;
   private final Map<Class<? extends PictureInPictureRenderState>, PictureInPictureRenderer<?>> pictureInPictureRenderers;
   @Nullable
   private GpuTexture itemsAtlas;
   @Nullable
   private GpuTexture itemsAtlasDepth;
   private final Matrix4f itemsAtlasProjectionMatrix = new Matrix4f();
   private int itemAtlasX;
   private int itemAtlasY;
   private int cachedGuiScale;
   private int frameNumber;

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

   public void render() {
      this.bufferSource.endBatch();
      this.preparePictureInPicture();
      this.prepareItemElements();
      this.prepareText();
      this.renderState.getElementStates().sort(ELEMENT_SORT_COMPARATOR);
      ScreenRectangle var1 = null;
      RenderPipeline var2 = null;
      TextureSetup var3 = null;
      BufferBuilder var4 = null;

      for(GuiElementRenderState var6 : this.renderState.getElementStates()) {
         RenderPipeline var7 = var6.pipeline();
         TextureSetup var8 = var6.textureSetup();
         ScreenRectangle var9 = var6.scissorArea();
         if (var7 != var2 || this.scissorChanged(var9, var1) || !var8.equals(var3)) {
            if (var4 != null) {
               this.recordDraw(var4, var2, var3, var1);
            }

            var4 = this.getBufferBuilder(var7);
            var2 = var7;
            var3 = var8;
            var1 = var9;
         }

         var6.buildVertices(var4);
      }

      if (var4 != null) {
         this.recordDraw(var4, var2, var3, var1);
      }

      Window var18 = Minecraft.getInstance().getWindow();
      Matrix4f var19 = (new Matrix4f()).setOrtho(0.0F, (float)var18.getWidth() / (float)var18.getGuiScale(), (float)var18.getHeight() / (float)var18.getGuiScale(), 0.0F, 1000.0F, 11000.0F);
      RenderSystem.setProjectionMatrix(var19, ProjectionType.ORTHOGRAPHIC);
      Matrix4fStack var20 = RenderSystem.getModelViewStack();
      var20.pushMatrix();
      var20.translation(0.0F, 0.0F, -11000.0F);
      RenderTarget var21 = Minecraft.getInstance().getMainRenderTarget();
      int var22 = 0;

      for(Draw var11 : this.draws) {
         if (var11.indexBuffer == null && var11.indexCount > var22) {
            var22 = var11.indexCount;
         }
      }

      RenderSystem.AutoStorageIndexBuffer var23 = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
      GpuBuffer var24 = var23.getBuffer(var22);
      VertexFormat.IndexType var12 = var23.type();

      try (RenderPass var13 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(var21.getColorTexture(), OptionalInt.empty(), var21.useDepth ? var21.getDepthTexture() : null, OptionalDouble.empty())) {
         for(Draw var15 : this.draws) {
            this.executeDraw(var15, var13, var24, var12);
         }
      }

      var20.popMatrix();
      this.draws.clear();
      this.renderState.reset();
   }

   private void prepareText() {
      for(GuiTextRenderState var2 : this.renderState.getTextStates()) {
         TextRenderState var3 = var2.textRenderState();
         BakedGlyph var4 = var3.whiteGlyph();
         boolean var5 = var3.effects() != null;
         if (var3.backgroundColor() != 0) {
            BakedGlyph.Effect var6 = new BakedGlyph.Effect((float)(var2.x() - 1), (float)(var2.y() - 1), var3.endX(), (float)(var2.y() + var3.lineHeight()), 0.0F, var3.backgroundColor());
            RenderType var7 = var4.renderType(var3.mode());
            var4.extractBackground(this.renderState, var7, var6, var2);
         }

         for(BakedGlyph.GlyphInstance var14 : var3.glyphInstances()) {
            BakedGlyph var8 = var14.glyph();
            RenderType var9 = var8.renderType(var3.mode());
            var8.extractChar(this.renderState, var9, var14, true, var2);
         }

         if (var5) {
            RenderType var11 = var4.renderType(var3.mode());

            for(BakedGlyph.Effect var18 : var3.effects()) {
               var4.extractEffect(this.renderState, var11, var18, true, var2);
            }
         }

         for(BakedGlyph.GlyphInstance var16 : var3.glyphInstances()) {
            BakedGlyph var19 = var16.glyph();
            RenderType var21 = var19.renderType(var3.mode());
            var19.extractChar(this.renderState, var21, var16, false, var2);
         }

         if (var5) {
            RenderType var13 = var4.renderType(var3.mode());

            for(BakedGlyph.Effect var20 : var3.effects()) {
               var4.extractEffect(this.renderState, var13, var20, false, var2);
            }
         }
      }

   }

   private void prepareItemElements() {
      List var1 = this.renderState.getItemStates();
      if (!var1.isEmpty()) {
         int var2 = this.getGuiScaleInvalidatingItemAtlasIfChanged();
         int var3 = this.getItemCount();
         int var4 = 16 * var2;
         int var5 = this.calculateAtlasSizeInPixels(var3, var4);
         if (this.itemsAtlas == null) {
            this.createAtlasTextures(var5);
            this.itemsAtlasProjectionMatrix.setOrtho(0.0F, (float)var5, (float)var5, 0.0F, -1000.0F, 1000.0F);
         }

         RenderSystem.outputColorTextureOverride = this.itemsAtlas;
         RenderSystem.outputDepthTextureOverride = this.itemsAtlasDepth;
         RenderSystem.setProjectionMatrix(this.itemsAtlasProjectionMatrix, ProjectionType.ORTHOGRAPHIC);
         Lighting.setupFor3DItems();
         PoseStack var6 = new PoseStack();

         for(GuiItemRenderState var8 : var1) {
            ItemStackRenderState var9 = var8.itemStackRenderState();
            AtlasPosition var10 = (AtlasPosition)this.atlasPositions.get(var9.getModelIdentity());
            if (var10 == null || var9.isAnimated() && var10.lastAnimatedOnFrame != this.frameNumber) {
               if (this.itemAtlasX + var4 > var5) {
                  this.itemAtlasX = 0;
                  this.itemAtlasY += var4;
               }

               boolean var11 = var9.isAnimated() && var10 != null;
               if (!var11 && this.itemAtlasY + var4 > var5) {
                  LOGGER.warn("Trying to render too many items in GUI at the same time. Skipping some of them.");
                  break;
               }

               int var12 = var11 ? var10.x : this.itemAtlasX;
               int var13 = var11 ? var10.y : this.itemAtlasY;
               if (var11) {
                  RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(this.itemsAtlas, 0, this.itemsAtlasDepth, 1.0, var12, var5 - var13 - var4, var4, var4);
               }

               this.renderItemToAtlas(var9, var6, var12, var13, var4);
               float var14 = (float)var12 / (float)var5;
               float var15 = (float)(var5 - var13) / (float)var5;
               this.submitBlitFromItemAtlas(var8, var14, var15, var4, var5);
               if (var11) {
                  var10.lastAnimatedOnFrame = this.frameNumber;
               } else {
                  this.atlasPositions.put(var8.itemStackRenderState().getModelIdentity(), new AtlasPosition(this.itemAtlasX, this.itemAtlasY, var14, var15, this.frameNumber));
                  this.itemAtlasX += var4;
               }
            } else {
               this.submitBlitFromItemAtlas(var8, var10.u, var10.v, var4, var5);
            }
         }

         RenderSystem.outputColorTextureOverride = null;
         RenderSystem.outputDepthTextureOverride = null;
      }
   }

   private void preparePictureInPicture() {
      int var1 = Minecraft.getInstance().getWindow().getGuiScale();

      for(PictureInPictureRenderState var3 : this.renderState.getPicturesInPictureStates()) {
         this.preparePictureInPictureState(var3, var1);
      }

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
         Lighting.setupForFlatItems();
      } else {
         Lighting.setupFor3DItems();
      }

      var1.render(var2, this.bufferSource, 15728880, OverlayTexture.NO_OVERLAY);
      this.bufferSource.endBatch();
      var2.popPose();
   }

   private void submitBlitFromItemAtlas(GuiItemRenderState var1, float var2, float var3, int var4, int var5) {
      float var6 = var2 + (float)var4 / (float)var5;
      float var7 = var3 + (float)(-var4) / (float)var5;
      this.renderState.submitGuiElement(new BlitRenderState(RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA, TextureSetup.singleTexture(this.itemsAtlas), var1.pose(), var1.x(), var1.y(), var1.x() + 16, var1.y() + 16, var1.z(), var2, var6, var3, var7, -1, var1.layer(), var1.scissorArea()));
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
      List var1 = this.renderState.getItemStates();
      int var2;
      if (this.atlasPositions.isEmpty()) {
         var2 = var1.size();
      } else {
         var2 = this.atlasPositions.size();

         for(GuiItemRenderState var4 : var1) {
            if (!this.atlasPositions.containsKey(var4.itemStackRenderState().getModelIdentity())) {
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
         GpuBuffer var6 = RenderSystem.getDevice().createBuffer(() -> "TODO GUI vertex buffer", BufferType.VERTICES, BufferUsage.STATIC_WRITE, var5.vertexBuffer());
         GpuBuffer var7;
         VertexFormat.IndexType var8;
         if (var5.indexBuffer() != null) {
            var7 = RenderSystem.getDevice().createBuffer(() -> "TODO GUI index buffer", BufferType.VERTICES, BufferUsage.STATIC_WRITE, var5.indexBuffer());
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
   }

   static {
      ELEMENT_SORT_COMPARATOR = Comparator.comparing(GuiElementRenderState::layer).thenComparing(GuiElementRenderState::scissorArea, scissorComparator).thenComparing(GuiElementRenderState::pipeline, Comparator.comparing(RenderPipeline::getSortKey)).thenComparing(GuiElementRenderState::z);
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
