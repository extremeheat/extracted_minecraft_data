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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.pip.OversizedItemRenderer;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.BlitRenderState;
import net.minecraft.client.gui.render.state.GlyphRenderState;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.gui.render.state.GuiItemRenderState;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.pip.OversizedItemRenderState;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.CachedOrthoProjectionMatrixBuffer;
import net.minecraft.client.renderer.MappableRingBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.joml.Matrix3x2fc;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.jspecify.annotations.Nullable;
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
   private static final int MAXIMUM_ITEM_ATLAS_SIZE = RenderSystem.getDevice().getMaxTextureSize();
   public static final int CLEAR_COLOR = 0;
   private static final Comparator<ScreenRectangle> SCISSOR_COMPARATOR = Comparator.nullsFirst(Comparator.comparing(ScreenRectangle::top).thenComparing(ScreenRectangle::bottom).thenComparing(ScreenRectangle::left).thenComparing(ScreenRectangle::right));
   private static final Comparator<TextureSetup> TEXTURE_COMPARATOR = Comparator.nullsFirst(Comparator.comparing(TextureSetup::getSortKey));
   private static final Comparator<GuiElementRenderState> ELEMENT_SORT_COMPARATOR;
   private final Map<Object, AtlasPosition> atlasPositions = new Object2ObjectOpenHashMap();
   private final Map<Object, OversizedItemRenderer> oversizedItemRenderers = new Object2ObjectOpenHashMap();
   private final GuiRenderState renderState;
   private final List<Draw> draws = new ArrayList();
   private final List<MeshToDraw> meshesToDraw = new ArrayList();
   private final ByteBufferBuilder byteBufferBuilder = new ByteBufferBuilder(786432);
   private final Map<VertexFormat, MappableRingBuffer> vertexBuffers = new Object2ObjectOpenHashMap();
   private int firstDrawIndexAfterBlur = 2147483647;
   private final CachedOrthoProjectionMatrixBuffer guiProjectionMatrixBuffer = new CachedOrthoProjectionMatrixBuffer("gui", 1000.0F, 11000.0F, true);
   private final CachedOrthoProjectionMatrixBuffer itemsProjectionMatrixBuffer = new CachedOrthoProjectionMatrixBuffer("items", -1000.0F, 1000.0F, true);
   private final MultiBufferSource.BufferSource bufferSource;
   private final SubmitNodeCollector submitNodeCollector;
   private final FeatureRenderDispatcher featureRenderDispatcher;
   private final Map<Class<? extends PictureInPictureRenderState>, PictureInPictureRenderer<?>> pictureInPictureRenderers;
   private @Nullable GpuTexture itemsAtlas;
   private @Nullable GpuTextureView itemsAtlasView;
   private @Nullable GpuTexture itemsAtlasDepth;
   private @Nullable GpuTextureView itemsAtlasDepthView;
   private int itemAtlasX;
   private int itemAtlasY;
   private int cachedGuiScale;
   private int frameNumber;
   private @Nullable ScreenRectangle previousScissorArea = null;
   private @Nullable RenderPipeline previousPipeline = null;
   private @Nullable TextureSetup previousTextureSetup = null;
   private @Nullable BufferBuilder bufferBuilder = null;

   public GuiRenderer(final GuiRenderState renderState, final MultiBufferSource.BufferSource bufferSource, final SubmitNodeCollector submitNodeCollector, final FeatureRenderDispatcher featureRenderDispatcher, final List<PictureInPictureRenderer<?>> pictureInPictureRenderers) {
      super();
      this.renderState = renderState;
      this.bufferSource = bufferSource;
      this.submitNodeCollector = submitNodeCollector;
      this.featureRenderDispatcher = featureRenderDispatcher;
      ImmutableMap.Builder<Class<? extends PictureInPictureRenderState>, PictureInPictureRenderer<?>> builder = ImmutableMap.builder();

      for(PictureInPictureRenderer<?> pictureInPictureRenderer : pictureInPictureRenderers) {
         builder.put(pictureInPictureRenderer.getRenderStateClass(), pictureInPictureRenderer);
      }

      this.pictureInPictureRenderers = builder.buildOrThrow();
   }

   public void incrementFrameNumber() {
      ++this.frameNumber;
   }

   public void render(final GpuBufferSlice fogBuffer) {
      this.prepare();
      this.draw(fogBuffer);

      for(MappableRingBuffer buffer : this.vertexBuffers.values()) {
         buffer.rotate();
      }

      this.draws.clear();
      this.meshesToDraw.clear();
      this.renderState.reset();
      this.firstDrawIndexAfterBlur = 2147483647;
      this.clearUnusedOversizedItemRenderers();
      if (SharedConstants.DEBUG_SHUFFLE_UI_RENDERING_ORDER) {
         RenderPipeline.updateSortKeySeed();
         TextureSetup.updateSortKeySeed();
      }

   }

   private void clearUnusedOversizedItemRenderers() {
      Iterator<Map.Entry<Object, OversizedItemRenderer>> oversizedItemRendererIterator = this.oversizedItemRenderers.entrySet().iterator();

      while(oversizedItemRendererIterator.hasNext()) {
         Map.Entry<Object, OversizedItemRenderer> next = (Map.Entry)oversizedItemRendererIterator.next();
         OversizedItemRenderer renderer = (OversizedItemRenderer)next.getValue();
         if (!renderer.usedOnThisFrame()) {
            renderer.close();
            oversizedItemRendererIterator.remove();
         } else {
            renderer.resetUsedOnThisFrame();
         }
      }

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

   private void addElementsToMeshes(final GuiRenderState.TraverseRange range) {
      this.previousScissorArea = null;
      this.previousPipeline = null;
      this.previousTextureSetup = null;
      this.bufferBuilder = null;
      this.renderState.forEachElement(this::addElementToMesh, range);
      if (this.bufferBuilder != null) {
         this.recordMesh(this.bufferBuilder, this.previousPipeline, this.previousTextureSetup, this.previousScissorArea);
      }

   }

   private void draw(final GpuBufferSlice fogBuffer) {
      if (!this.draws.isEmpty()) {
         Minecraft minecraft = Minecraft.getInstance();
         Window window = minecraft.getWindow();
         RenderSystem.setProjectionMatrix(this.guiProjectionMatrixBuffer.getBuffer((float)window.getWidth() / (float)window.getGuiScale(), (float)window.getHeight() / (float)window.getGuiScale()), ProjectionType.ORTHOGRAPHIC);
         RenderTarget mainRenderTarget = minecraft.getMainRenderTarget();
         int maxIndexCount = 0;

         for(Draw draw : this.draws) {
            if (draw.indexCount > maxIndexCount) {
               maxIndexCount = draw.indexCount;
            }
         }

         RenderSystem.AutoStorageIndexBuffer autoIndices = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
         GpuBuffer indexBuffer = autoIndices.getBuffer(maxIndexCount);
         VertexFormat.IndexType indexType = autoIndices.type();
         GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms().writeTransform((new Matrix4f()).setTranslation(0.0F, 0.0F, -11000.0F), new Vector4f(1.0F, 1.0F, 1.0F, 1.0F), new Vector3f(), new Matrix4f());
         if (this.firstDrawIndexAfterBlur > 0) {
            this.executeDrawRange(() -> "GUI before blur", mainRenderTarget, fogBuffer, dynamicTransforms, indexBuffer, indexType, 0, Math.min(this.firstDrawIndexAfterBlur, this.draws.size()));
         }

         if (this.draws.size() > this.firstDrawIndexAfterBlur) {
            RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(mainRenderTarget.getDepthTexture(), 1.0);
            minecraft.gameRenderer.processBlurEffect();
            this.executeDrawRange(() -> "GUI after blur", mainRenderTarget, fogBuffer, dynamicTransforms, indexBuffer, indexType, this.firstDrawIndexAfterBlur, this.draws.size());
         }
      }
   }

   private void executeDrawRange(final Supplier<String> label, final RenderTarget mainRenderTarget, final GpuBufferSlice fogBuffer, final GpuBufferSlice dynamicTransforms, final GpuBuffer indexBuffer, final VertexFormat.IndexType indexType, final int startIndex, final int endIndex) {
      try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(label, mainRenderTarget.getColorTextureView(), OptionalInt.empty(), mainRenderTarget.useDepth ? mainRenderTarget.getDepthTextureView() : null, OptionalDouble.empty())) {
         RenderSystem.bindDefaultUniforms(renderPass);
         renderPass.setUniform("Fog", fogBuffer);
         renderPass.setUniform("DynamicTransforms", dynamicTransforms);

         for(int i = startIndex; i < endIndex; ++i) {
            Draw draw = (Draw)this.draws.get(i);
            this.executeDraw(draw, renderPass, indexBuffer, indexType);
         }
      }

   }

   private void addElementToMesh(final GuiElementRenderState elementState) {
      RenderPipeline pipeline = elementState.pipeline();
      TextureSetup textureSetup = elementState.textureSetup();
      ScreenRectangle scissorArea = elementState.scissorArea();
      if (pipeline != this.previousPipeline || this.scissorChanged(scissorArea, this.previousScissorArea) || !textureSetup.equals(this.previousTextureSetup)) {
         if (this.bufferBuilder != null) {
            this.recordMesh(this.bufferBuilder, this.previousPipeline, this.previousTextureSetup, this.previousScissorArea);
         }

         this.bufferBuilder = this.getBufferBuilder(pipeline);
         this.previousPipeline = pipeline;
         this.previousTextureSetup = textureSetup;
         this.previousScissorArea = scissorArea;
      }

      elementState.buildVertices(this.bufferBuilder);
   }

   private void prepareText() {
      this.renderState.forEachText((text) -> {
         final Matrix3x2fc pose = text.pose;
         final ScreenRectangle scissor = text.scissor;
         text.ensurePrepared().visit(new Font.GlyphVisitor() {
            {
               Objects.requireNonNull(GuiRenderer.this);
            }

            public void acceptGlyph(final TextRenderable.Styled glyph) {
               this.accept(glyph);
            }

            public void acceptEffect(final TextRenderable effect) {
               this.accept(effect);
            }

            private void accept(final TextRenderable glyph) {
               GuiRenderer.this.renderState.submitGlyphToCurrentLayer(new GlyphRenderState(pose, glyph, scissor));
            }
         });
      });
   }

   private void prepareItemElements() {
      if (!this.renderState.getItemModelIdentities().isEmpty()) {
         int guiScale = this.getGuiScaleInvalidatingItemAtlasIfChanged();
         int singleItemTextureSize = 16 * guiScale;
         int atlasSizeInPixels = this.calculateAtlasSizeInPixels(singleItemTextureSize);
         if (this.itemsAtlas == null) {
            this.createAtlasTextures(atlasSizeInPixels);
         }

         RenderSystem.outputColorTextureOverride = this.itemsAtlasView;
         RenderSystem.outputDepthTextureOverride = this.itemsAtlasDepthView;
         RenderSystem.setProjectionMatrix(this.itemsProjectionMatrixBuffer.getBuffer((float)atlasSizeInPixels, (float)atlasSizeInPixels), ProjectionType.ORTHOGRAPHIC);
         Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_3D);
         PoseStack poseStack = new PoseStack();
         MutableBoolean alreadyWarned = new MutableBoolean(false);
         MutableBoolean hasOversizedItems = new MutableBoolean(false);
         this.renderState.forEachItem((itemState) -> {
            if (itemState.oversizedItemBounds() != null) {
               hasOversizedItems.setTrue();
            } else {
               TrackingItemStackRenderState itemStackRenderState = itemState.itemStackRenderState();
               AtlasPosition atlasPosition = (AtlasPosition)this.atlasPositions.get(itemStackRenderState.getModelIdentity());
               if (atlasPosition == null || itemStackRenderState.isAnimated() && atlasPosition.lastAnimatedOnFrame != this.frameNumber) {
                  if (this.itemAtlasX + singleItemTextureSize > atlasSizeInPixels) {
                     this.itemAtlasX = 0;
                     this.itemAtlasY += singleItemTextureSize;
                  }

                  boolean reDrawingAnimated = itemStackRenderState.isAnimated() && atlasPosition != null;
                  if (!reDrawingAnimated && this.itemAtlasY + singleItemTextureSize > atlasSizeInPixels) {
                     if (alreadyWarned.isFalse()) {
                        LOGGER.warn("Trying to render too many items in GUI at the same time. Skipping some of them.");
                        alreadyWarned.setTrue();
                     }

                  } else {
                     int renderX = reDrawingAnimated ? atlasPosition.x : this.itemAtlasX;
                     int renderY = reDrawingAnimated ? atlasPosition.y : this.itemAtlasY;
                     if (reDrawingAnimated) {
                        RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(this.itemsAtlas, 0, this.itemsAtlasDepth, 1.0, renderX, atlasSizeInPixels - renderY - singleItemTextureSize, singleItemTextureSize, singleItemTextureSize);
                     }

                     this.renderItemToAtlas(itemStackRenderState, poseStack, renderX, renderY, singleItemTextureSize);
                     float u0 = (float)renderX / (float)atlasSizeInPixels;
                     float v0 = (float)(atlasSizeInPixels - renderY) / (float)atlasSizeInPixels;
                     this.submitBlitFromItemAtlas(itemState, u0, v0, singleItemTextureSize, atlasSizeInPixels);
                     if (reDrawingAnimated) {
                        atlasPosition.lastAnimatedOnFrame = this.frameNumber;
                     } else {
                        this.atlasPositions.put(itemState.itemStackRenderState().getModelIdentity(), new AtlasPosition(this.itemAtlasX, this.itemAtlasY, u0, v0, this.frameNumber));
                        this.itemAtlasX += singleItemTextureSize;
                     }

                  }
               } else {
                  this.submitBlitFromItemAtlas(itemState, atlasPosition.u, atlasPosition.v, singleItemTextureSize, atlasSizeInPixels);
               }
            }
         });
         RenderSystem.outputColorTextureOverride = null;
         RenderSystem.outputDepthTextureOverride = null;
         if (hasOversizedItems.booleanValue()) {
            this.renderState.forEachItem((itemState) -> {
               if (itemState.oversizedItemBounds() != null) {
                  TrackingItemStackRenderState itemStackRenderState = itemState.itemStackRenderState();
                  OversizedItemRenderer oversizedItemRenderer = (OversizedItemRenderer)this.oversizedItemRenderers.computeIfAbsent(itemStackRenderState.getModelIdentity(), (key) -> new OversizedItemRenderer(this.bufferSource));
                  ScreenRectangle actualItemBounds = itemState.oversizedItemBounds();
                  OversizedItemRenderState oversizedItemRenderState = new OversizedItemRenderState(itemState, actualItemBounds.left(), actualItemBounds.top(), actualItemBounds.right(), actualItemBounds.bottom());
                  oversizedItemRenderer.prepare(oversizedItemRenderState, this.renderState, guiScale);
               }

            });
         }

      }
   }

   private void preparePictureInPicture() {
      int guiScale = Minecraft.getInstance().getWindow().getGuiScale();
      this.renderState.forEachPictureInPicture((pictureInPictureState) -> this.preparePictureInPictureState(pictureInPictureState, guiScale));
   }

   private <T extends PictureInPictureRenderState> void preparePictureInPictureState(final T picturesInPictureState, final int guiScale) {
      PictureInPictureRenderer<T> renderer = (PictureInPictureRenderer)this.pictureInPictureRenderers.get(picturesInPictureState.getClass());
      if (renderer != null) {
         renderer.prepare(picturesInPictureState, this.renderState, guiScale);
      }

   }

   private void renderItemToAtlas(final TrackingItemStackRenderState itemStackRenderState, final PoseStack poseStack, final int renderX, final int renderY, final int singleItemTextureSize) {
      poseStack.pushPose();
      poseStack.translate((float)renderX + (float)singleItemTextureSize / 2.0F, (float)renderY + (float)singleItemTextureSize / 2.0F, 0.0F);
      poseStack.scale((float)singleItemTextureSize, (float)(-singleItemTextureSize), (float)singleItemTextureSize);
      boolean flat = !itemStackRenderState.usesBlockLight();
      if (flat) {
         Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_FLAT);
      } else {
         Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_3D);
      }

      RenderSystem.enableScissorForRenderTypeDraws(renderX, this.itemsAtlas.getHeight(0) - renderY - singleItemTextureSize, singleItemTextureSize, singleItemTextureSize);
      itemStackRenderState.submit(poseStack, this.submitNodeCollector, 15728880, OverlayTexture.NO_OVERLAY, 0);
      this.featureRenderDispatcher.renderAllFeatures();
      this.bufferSource.endBatch();
      RenderSystem.disableScissorForRenderTypeDraws();
      poseStack.popPose();
   }

   private void submitBlitFromItemAtlas(final GuiItemRenderState itemState, final float u0, final float v0, final int singleItemTextureSize, final int atlasSizeInPixels) {
      float u1 = u0 + (float)singleItemTextureSize / (float)atlasSizeInPixels;
      float v1 = v0 + (float)(-singleItemTextureSize) / (float)atlasSizeInPixels;
      this.renderState.submitBlitToCurrentLayer(new BlitRenderState(RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA, TextureSetup.singleTexture(this.itemsAtlasView, RenderSystem.getSamplerCache().getRepeat(FilterMode.NEAREST)), itemState.pose(), itemState.x(), itemState.y(), itemState.x() + 16, itemState.y() + 16, u0, u1, v0, v1, -1, itemState.scissorArea(), (ScreenRectangle)null));
   }

   private void createAtlasTextures(final int atlasSizeInPixels) {
      GpuDevice device = RenderSystem.getDevice();
      this.itemsAtlas = device.createTexture("UI items atlas", 13, TextureFormat.RGBA8, atlasSizeInPixels, atlasSizeInPixels, 1, 1);
      this.itemsAtlasView = device.createTextureView(this.itemsAtlas);
      this.itemsAtlasDepth = device.createTexture("UI items atlas depth", 9, TextureFormat.DEPTH32, atlasSizeInPixels, atlasSizeInPixels, 1, 1);
      this.itemsAtlasDepthView = device.createTextureView(this.itemsAtlasDepth);
      device.createCommandEncoder().clearColorAndDepthTextures(this.itemsAtlas, 0, this.itemsAtlasDepth, 1.0);
   }

   private int calculateAtlasSizeInPixels(final int singleItemTextureSize) {
      Set<Object> itemStates = this.renderState.getItemModelIdentities();
      int itemCount;
      if (this.atlasPositions.isEmpty()) {
         itemCount = itemStates.size();
      } else {
         itemCount = this.atlasPositions.size();

         for(Object itemState : itemStates) {
            if (!this.atlasPositions.containsKey(itemState)) {
               ++itemCount;
            }
         }
      }

      if (this.itemsAtlas != null) {
         int currentAtlasItemsPerRow = this.itemsAtlas.getWidth(0) / singleItemTextureSize;
         int currentAtlasCapacity = currentAtlasItemsPerRow * currentAtlasItemsPerRow;
         if (itemCount < currentAtlasCapacity) {
            return this.itemsAtlas.getWidth(0);
         }

         this.invalidateItemAtlas();
      }

      int itemCountOnThisFrame = itemStates.size();
      int atlasSizeInItems = Mth.smallestSquareSide(itemCountOnThisFrame + itemCountOnThisFrame / 2);
      return Math.clamp((long)Mth.smallestEncompassingPowerOfTwo(atlasSizeInItems * singleItemTextureSize), 512, MAXIMUM_ITEM_ATLAS_SIZE);
   }

   private int getGuiScaleInvalidatingItemAtlasIfChanged() {
      int guiScale = Minecraft.getInstance().getWindow().getGuiScale();
      if (guiScale != this.cachedGuiScale) {
         this.invalidateItemAtlas();

         for(OversizedItemRenderer renderer : this.oversizedItemRenderers.values()) {
            renderer.invalidateTexture();
         }

         this.cachedGuiScale = guiScale;
      }

      return guiScale;
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

   private void recordMesh(final BufferBuilder bufferBuilder, final RenderPipeline pipeline, final TextureSetup textureSetup, final @Nullable ScreenRectangle scissorArea) {
      MeshData mesh = bufferBuilder.build();
      if (mesh != null) {
         this.meshesToDraw.add(new MeshToDraw(mesh, pipeline, textureSetup, scissorArea));
      }

   }

   private void recordDraws() {
      this.ensureVertexBufferSizes();
      CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
      Object2IntMap<VertexFormat> offsets = new Object2IntOpenHashMap();

      for(MeshToDraw meshToDraw : this.meshesToDraw) {
         MeshData mesh = meshToDraw.mesh;
         MeshData.DrawState drawState = mesh.drawState();
         VertexFormat format = drawState.format();
         MappableRingBuffer vertexBuffer = (MappableRingBuffer)this.vertexBuffers.get(format);
         if (!offsets.containsKey(format)) {
            offsets.put(format, 0);
         }

         ByteBuffer meshVertexBuffer = mesh.vertexBuffer();
         int meshBufferSize = meshVertexBuffer.remaining();
         int offset = offsets.getInt(format);

         try (GpuBuffer.MappedView mappedView = commandEncoder.mapBuffer(vertexBuffer.currentBuffer().slice((long)offset, (long)meshBufferSize), false, true)) {
            MemoryUtil.memCopy(meshVertexBuffer, mappedView.data());
         }

         offsets.put(format, offset + meshBufferSize);
         this.draws.add(new Draw(vertexBuffer.currentBuffer(), offset / format.getVertexSize(), drawState.mode(), drawState.indexCount(), meshToDraw.pipeline, meshToDraw.textureSetup, meshToDraw.scissorArea));
         meshToDraw.close();
      }

   }

   private void ensureVertexBufferSizes() {
      Object2IntMap<VertexFormat> requiredSizes = this.calculatedRequiredVertexBufferSizes();
      ObjectIterator var2 = requiredSizes.object2IntEntrySet().iterator();

      while(var2.hasNext()) {
         Object2IntMap.Entry<VertexFormat> entry = (Object2IntMap.Entry)var2.next();
         VertexFormat vertexFormat = (VertexFormat)entry.getKey();
         int requiredSize = entry.getIntValue();
         MappableRingBuffer vertexBuffer = (MappableRingBuffer)this.vertexBuffers.get(vertexFormat);
         if (vertexBuffer == null || vertexBuffer.size() < requiredSize) {
            if (vertexBuffer != null) {
               vertexBuffer.close();
            }

            this.vertexBuffers.put(vertexFormat, new MappableRingBuffer(() -> "GUI vertex buffer for " + String.valueOf(vertexFormat), 34, requiredSize));
         }
      }

   }

   private Object2IntMap<VertexFormat> calculatedRequiredVertexBufferSizes() {
      Object2IntMap<VertexFormat> requiredVertexBufferSizes = new Object2IntOpenHashMap();

      for(MeshToDraw meshToDraw : this.meshesToDraw) {
         MeshData.DrawState drawState = meshToDraw.mesh.drawState();
         VertexFormat format = drawState.format();
         if (!requiredVertexBufferSizes.containsKey(format)) {
            requiredVertexBufferSizes.put(format, 0);
         }

         requiredVertexBufferSizes.put(format, requiredVertexBufferSizes.getInt(format) + drawState.vertexCount() * format.getVertexSize());
      }

      return requiredVertexBufferSizes;
   }

   private void executeDraw(final Draw draw, final RenderPass renderPass, final GpuBuffer indexBuffer, final VertexFormat.IndexType indexType) {
      RenderPipeline pipeline = draw.pipeline();
      renderPass.setPipeline(pipeline);
      renderPass.setVertexBuffer(0, draw.vertexBuffer);
      ScreenRectangle scissorArea = draw.scissorArea();
      if (scissorArea != null) {
         this.enableScissor(scissorArea, renderPass);
      } else {
         renderPass.disableScissor();
      }

      if (draw.textureSetup.texure0() != null) {
         renderPass.bindTexture("Sampler0", draw.textureSetup.texure0(), draw.textureSetup.sampler0());
      }

      if (draw.textureSetup.texure1() != null) {
         renderPass.bindTexture("Sampler1", draw.textureSetup.texure1(), draw.textureSetup.sampler1());
      }

      if (draw.textureSetup.texure2() != null) {
         renderPass.bindTexture("Sampler2", draw.textureSetup.texure2(), draw.textureSetup.sampler2());
      }

      renderPass.setIndexBuffer(indexBuffer, indexType);
      renderPass.drawIndexed(draw.baseVertex, 0, draw.indexCount, 1);
   }

   private BufferBuilder getBufferBuilder(final RenderPipeline pipeline) {
      return new BufferBuilder(this.byteBufferBuilder, pipeline.getVertexFormatMode(), pipeline.getVertexFormat());
   }

   private boolean scissorChanged(final @Nullable ScreenRectangle newScissor, final @Nullable ScreenRectangle oldScissor) {
      if (newScissor == oldScissor) {
         return false;
      } else if (newScissor != null) {
         return !newScissor.equals(oldScissor);
      } else {
         return true;
      }
   }

   private void enableScissor(final ScreenRectangle rectangle, final RenderPass renderPass) {
      Window window = Minecraft.getInstance().getWindow();
      int windowHeight = window.getHeight();
      int guiScale = window.getGuiScale();
      double left = (double)(rectangle.left() * guiScale);
      double bottom = (double)(windowHeight - rectangle.bottom() * guiScale);
      double width = (double)(rectangle.width() * guiScale);
      double height = (double)(rectangle.height() * guiScale);
      renderPass.enableScissor((int)left, (int)bottom, Math.max(0, (int)width), Math.max(0, (int)height));
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

      for(MappableRingBuffer buffer : this.vertexBuffers.values()) {
         buffer.close();
      }

      this.oversizedItemRenderers.values().forEach(PictureInPictureRenderer::close);
   }

   static {
      ELEMENT_SORT_COMPARATOR = Comparator.comparing(GuiElementRenderState::scissorArea, SCISSOR_COMPARATOR).thenComparing(GuiElementRenderState::pipeline, Comparator.comparing(RenderPipeline::getSortKey)).thenComparing(GuiElementRenderState::textureSetup, TEXTURE_COMPARATOR);
   }

   private static record Draw(GpuBuffer vertexBuffer, int baseVertex, VertexFormat.Mode mode, int indexCount, RenderPipeline pipeline, TextureSetup textureSetup, @Nullable ScreenRectangle scissorArea) {
      private Draw {
         super();
      }
   }

   private static record MeshToDraw(MeshData mesh, RenderPipeline pipeline, TextureSetup textureSetup, @Nullable ScreenRectangle scissorArea) implements AutoCloseable {
      private MeshToDraw {
         super();
      }

      public void close() {
         this.mesh.close();
      }
   }

   private static final class AtlasPosition {
      final int x;
      final int y;
      final float u;
      final float v;
      int lastAnimatedOnFrame;

      private AtlasPosition(final int x, final int y, final float u, final float v, final int lastAnimatedOnFrame) {
         super();
         this.x = x;
         this.y = y;
         this.u = u;
         this.v = v;
         this.lastAnimatedOnFrame = lastAnimatedOnFrame;
      }
   }
}
