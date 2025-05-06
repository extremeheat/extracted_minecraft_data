package net.minecraft.client.gui.render;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Supplier;
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
import net.minecraft.client.renderer.MappableRingBuffer;
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
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

public class GuiRenderer implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final float MAX_GUI_Z = 10000.0F;
   public static final float MIN_GUI_Z = 0.0F;
   private static final float GUI_Z_NEAR = 1000.0F;
   public static final int GUI_3D_Z_FAR = 1000;
   public static final int GUI_3D_Z_NEAR = -1000;
   public static final int DEFAULT_ITEM_SIZE = 16;
   private static final int MINIMUM_ITEM_ATLAS_SIZE = 512;
   private static final int MAXIMUM_ITEM_ATLAS_SIZE = 2048;
   public static final int CLEAR_COLOR = 0;
   private static final Comparator<ScreenRectangle> SCISSOR_COMPARATOR = Comparator.nullsFirst(Comparator.comparing(ScreenRectangle::top).thenComparing(ScreenRectangle::bottom).thenComparing(ScreenRectangle::left).thenComparing(ScreenRectangle::right));
   private static final Comparator<TextureSetup> TEXTURE_COMPARATOR = Comparator.nullsFirst(Comparator.comparing(TextureSetup::getSortKey));
   private static final Comparator<GuiElementRenderState> ELEMENT_SORT_COMPARATOR;
   private final Map<Object, AtlasPosition> atlasPositions = new Object2ObjectOpenHashMap();
   private final GuiRenderState renderState;
   private final List<Draw> draws = new ArrayList();
   private final List<MeshToDraw> meshesToDraw = new ArrayList();
   private final ByteBufferBuilder byteBufferBuilder = new ByteBufferBuilder(786432);
   private final Map<VertexFormat, MappableRingBuffer> vertexBuffers = new Object2ObjectOpenHashMap();
   private int firstDrawIndexAfterBlur = 2147483647;
   private final CachedOrthoProjectionMatrixBuffer guiProjectionMatrixBuffer = new CachedOrthoProjectionMatrixBuffer("gui", 1000.0F, 11000.0F, true);
   private final CachedOrthoProjectionMatrixBuffer itemsProjectionMatrixBuffer = new CachedOrthoProjectionMatrixBuffer("items", -1000.0F, 1000.0F, true);
   private final MultiBufferSource.BufferSource bufferSource;
   private final Map<Class<? extends PictureInPictureRenderState>, PictureInPictureRenderer<?>> pictureInPictureRenderers;
   @Nullable
   private GpuTexture itemsAtlas;
   @Nullable
   private GpuTextureView itemsAtlasView;
   @Nullable
   private GpuTexture itemsAtlasDepth;
   @Nullable
   private GpuTextureView itemsAtlasDepthView;
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
      this.prepare();
      this.draw(var1);

      for(MappableRingBuffer var3 : this.vertexBuffers.values()) {
         var3.rotate();
      }

      this.draws.clear();
      this.meshesToDraw.clear();
      this.renderState.reset();
      this.firstDrawIndexAfterBlur = 2147483647;
   }

   private void prepare() {
      this.bufferSource.endBatch();
      this.preparePictureInPicture();
      this.prepareItemElements();
      this.prepareText();
      this.renderState.sortElements(ELEMENT_SORT_COMPARATOR);
      this.addElementsToMeshes(GuiRenderState.TraverseRange.BEFORE_BLUR);
      this.firstDrawIndexAfterBlur = this.meshesToDraw.size();
      this.addElementsToMeshes(GuiRenderState.TraverseRange.AFTER_BLUR);
      this.recordDraws();
   }

   private void addElementsToMeshes(GuiRenderState.TraverseRange var1) {
      this.previousScissorArea = null;
      this.previousPipeline = null;
      this.previousTextureSetup = null;
      this.bufferBuilder = null;
      this.renderState.forEachElement(this::addElementToMesh, var1);
      if (this.bufferBuilder != null) {
         this.recordMesh(this.bufferBuilder, this.previousPipeline, this.previousTextureSetup, this.previousScissorArea);
      }

   }

   private void draw(GpuBufferSlice var1) {
      if (!this.draws.isEmpty()) {
         Minecraft var2 = Minecraft.getInstance();
         Window var3 = var2.getWindow();
         RenderSystem.setProjectionMatrix(this.guiProjectionMatrixBuffer.getBuffer((float)var3.getWidth() / (float)var3.getGuiScale(), (float)var3.getHeight() / (float)var3.getGuiScale()), ProjectionType.ORTHOGRAPHIC);
         RenderTarget var4 = var2.getMainRenderTarget();
         int var5 = 0;

         for(Draw var7 : this.draws) {
            if (var7.indexCount > var5) {
               var5 = var7.indexCount;
            }
         }

         RenderSystem.AutoStorageIndexBuffer var10 = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
         GpuBuffer var11 = var10.getBuffer(var5);
         VertexFormat.IndexType var8 = var10.type();
         GpuBufferSlice var9 = RenderSystem.getDynamicUniforms().writeTransform((new Matrix4f()).setTranslation(0.0F, 0.0F, -11000.0F), new Vector4f(1.0F, 1.0F, 1.0F, 1.0F), new Vector3f(), new Matrix4f(), 0.0F);
         if (this.firstDrawIndexAfterBlur > 0) {
            this.executeDrawRange(() -> "GUI before blur", var4, var1, var9, var11, var8, 0, Math.min(this.firstDrawIndexAfterBlur, this.draws.size()));
         }

         if (this.draws.size() > this.firstDrawIndexAfterBlur) {
            RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(var4.getDepthTexture(), 1.0);
            var2.gameRenderer.processBlurEffect();
            this.executeDrawRange(() -> "GUI after blur", var4, var1, var9, var11, var8, this.firstDrawIndexAfterBlur, this.draws.size());
         }
      }
   }

   private void executeDrawRange(Supplier<String> var1, RenderTarget var2, GpuBufferSlice var3, GpuBufferSlice var4, GpuBuffer var5, VertexFormat.IndexType var6, int var7, int var8) {
      try (RenderPass var9 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(var1, var2.getColorTextureView(), OptionalInt.empty(), var2.useDepth ? var2.getDepthTextureView() : null, OptionalDouble.empty())) {
         RenderSystem.bindDefaultUniforms(var9);
         var9.setUniform("Fog", var3);
         var9.setUniform("DynamicTransforms", var4);

         for(int var10 = var7; var10 < var8; ++var10) {
            Draw var11 = (Draw)this.draws.get(var10);
            this.executeDraw(var11, var9, var5, var6);
         }
      }

   }

   private void addElementToMesh(GuiElementRenderState var1, int var2) {
      RenderPipeline var3 = var1.pipeline();
      TextureSetup var4 = var1.textureSetup();
      ScreenRectangle var5 = var1.scissorArea();
      if (var3 != this.previousPipeline || this.scissorChanged(var5, this.previousScissorArea) || !var4.equals(this.previousTextureSetup)) {
         if (this.bufferBuilder != null) {
            this.recordMesh(this.bufferBuilder, this.previousPipeline, this.previousTextureSetup, this.previousScissorArea);
         }

         this.bufferBuilder = this.getBufferBuilder(var3);
         this.previousPipeline = var3;
         this.previousTextureSetup = var4;
         this.previousScissorArea = var5;
      }

      var1.buildVertices(this.bufferBuilder, 0.0F + (float)var2 * 0.01F);
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

         RenderSystem.outputColorTextureOverride = this.itemsAtlasView;
         RenderSystem.outputDepthTextureOverride = this.itemsAtlasDepthView;
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
      this.renderState.submitGuiElementToCurrentLayer(new BlitRenderState(RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA, TextureSetup.singleTexture(this.itemsAtlasView), var1.pose(), var1.x(), var1.y(), var1.x() + 16, var1.y() + 16, var2, var6, var3, var7, -1, var1.scissorArea(), (ScreenRectangle)null));
   }

   private void createAtlasTextures(int var1) {
      GpuDevice var2 = RenderSystem.getDevice();
      this.itemsAtlas = var2.createTexture("UI items atlas", 12, TextureFormat.RGBA8, var1, var1, 1, 1);
      this.itemsAtlas.setTextureFilter(FilterMode.NEAREST, false);
      this.itemsAtlasView = var2.createTextureView(this.itemsAtlas);
      this.itemsAtlasDepth = var2.createTexture("UI items atlas depth", 8, TextureFormat.DEPTH32, var1, var1, 1, 1);
      this.itemsAtlasDepthView = var2.createTextureView(this.itemsAtlasDepth);
      var2.createCommandEncoder().clearColorAndDepthTextures(this.itemsAtlas, 0, this.itemsAtlasDepth, 1.0);
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

      if (this.itemsAtlasView != null) {
         this.itemsAtlasView.close();
         this.itemsAtlasView = null;
      }

      if (this.itemsAtlasDepth != null) {
         this.itemsAtlasDepth.close();
         this.itemsAtlasDepth = null;
      }

      if (this.itemsAtlasDepthView != null) {
         this.itemsAtlasDepthView.close();
         this.itemsAtlasDepthView = null;
      }

   }

   private void recordMesh(BufferBuilder var1, RenderPipeline var2, TextureSetup var3, @Nullable ScreenRectangle var4) {
      MeshData var5 = var1.buildOrThrow();
      this.meshesToDraw.add(new MeshToDraw(var5, var2, var3, var4));
   }

   private void recordDraws() {
      this.ensureVertexBufferSizes();
      CommandEncoder var1 = RenderSystem.getDevice().createCommandEncoder();
      Object2IntOpenHashMap var2 = new Object2IntOpenHashMap();

      for(MeshToDraw var4 : this.meshesToDraw) {
         MeshData var5 = var4.mesh;
         MeshData.DrawState var6 = var5.drawState();
         VertexFormat var7 = var6.format();
         MappableRingBuffer var8 = (MappableRingBuffer)this.vertexBuffers.get(var7);
         if (!var2.containsKey(var7)) {
            var2.put(var7, 0);
         }

         ByteBuffer var9 = var5.vertexBuffer();
         int var10 = var9.remaining();
         int var11 = var2.getInt(var7);

         try (GpuBuffer.MappedView var12 = var1.mapBuffer(var8.currentBuffer().slice(var11, var10), false, true)) {
            MemoryUtil.memCopy(var9, var12.data());
         }

         var2.put(var7, var11 + var10);
         this.draws.add(new Draw(var8.currentBuffer(), var11 / var7.getVertexSize(), var6.mode(), var6.indexCount(), var4.pipeline, var4.textureSetup, var4.scissorArea));
         var4.close();
      }

   }

   private void ensureVertexBufferSizes() {
      Object2IntMap var1 = this.calculatedRequiredVertexBufferSizes();
      ObjectIterator var2 = var1.object2IntEntrySet().iterator();

      while(var2.hasNext()) {
         Object2IntMap.Entry var3 = (Object2IntMap.Entry)var2.next();
         VertexFormat var4 = (VertexFormat)var3.getKey();
         int var5 = var3.getIntValue();
         MappableRingBuffer var6 = (MappableRingBuffer)this.vertexBuffers.get(var4);
         if (var6 == null || var6.size() < var5) {
            if (var6 != null) {
               var6.close();
            }

            this.vertexBuffers.put(var4, new MappableRingBuffer(() -> "GUI vertex buffer for " + String.valueOf(var4), 34, var5));
         }
      }

   }

   private Object2IntMap<VertexFormat> calculatedRequiredVertexBufferSizes() {
      Object2IntOpenHashMap var1 = new Object2IntOpenHashMap();

      for(MeshToDraw var3 : this.meshesToDraw) {
         MeshData.DrawState var4 = var3.mesh.drawState();
         VertexFormat var5 = var4.format();
         if (!var1.containsKey(var5)) {
            var1.put(var5, 0);
         }

         var1.put(var5, var1.getInt(var5) + var4.vertexCount() * var5.getVertexSize());
      }

      return var1;
   }

   private void executeDraw(Draw var1, RenderPass var2, GpuBuffer var3, VertexFormat.IndexType var4) {
      RenderPipeline var5 = var1.pipeline();
      var2.setPipeline(var5);
      var2.setVertexBuffer(0, var1.vertexBuffer);
      ScreenRectangle var6 = var1.scissorArea();
      if (var6 != null) {
         this.enableScissor(var6, var2);
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

      var2.setIndexBuffer(var3, var4);
      var2.drawIndexed(var1.baseVertex, 0, var1.indexCount, 1);
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

      if (this.itemsAtlasView != null) {
         this.itemsAtlasView.close();
      }

      if (this.itemsAtlasDepth != null) {
         this.itemsAtlasDepth.close();
      }

      if (this.itemsAtlasDepthView != null) {
         this.itemsAtlasDepthView.close();
      }

      this.pictureInPictureRenderers.values().forEach(PictureInPictureRenderer::close);
      this.guiProjectionMatrixBuffer.close();
      this.itemsProjectionMatrixBuffer.close();

      for(MappableRingBuffer var2 : this.vertexBuffers.values()) {
         var2.close();
      }

   }

   static {
      ELEMENT_SORT_COMPARATOR = Comparator.comparing(GuiElementRenderState::scissorArea, SCISSOR_COMPARATOR).thenComparing(GuiElementRenderState::pipeline, Comparator.comparing(RenderPipeline::getSortKey)).thenComparing(GuiElementRenderState::textureSetup, TEXTURE_COMPARATOR);
   }

   static record Draw(GpuBuffer vertexBuffer, int baseVertex, VertexFormat.Mode mode, int indexCount, RenderPipeline pipeline, TextureSetup textureSetup, @Nullable ScreenRectangle scissorArea) {
      final GpuBuffer vertexBuffer;
      final int baseVertex;
      final int indexCount;
      final TextureSetup textureSetup;

      Draw(GpuBuffer var1, int var2, VertexFormat.Mode var3, int var4, RenderPipeline var5, TextureSetup var6, @Nullable ScreenRectangle var7) {
         super();
         this.vertexBuffer = var1;
         this.baseVertex = var2;
         this.mode = var3;
         this.indexCount = var4;
         this.pipeline = var5;
         this.textureSetup = var6;
         this.scissorArea = var7;
      }
   }

   static record MeshToDraw(MeshData mesh, RenderPipeline pipeline, TextureSetup textureSetup, @Nullable ScreenRectangle scissorArea) implements AutoCloseable {
      final MeshData mesh;
      final RenderPipeline pipeline;
      final TextureSetup textureSetup;
      @Nullable
      final ScreenRectangle scissorArea;

      MeshToDraw(MeshData var1, RenderPipeline var2, TextureSetup var3, @Nullable ScreenRectangle var4) {
         super();
         this.mesh = var1;
         this.pipeline = var2;
         this.textureSetup = var3;
         this.scissorArea = var4;
      }

      public void close() {
         this.mesh.close();
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
