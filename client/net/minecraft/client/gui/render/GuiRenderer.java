package net.minecraft.client.gui.render;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
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
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.MappableRingBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Projection;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.client.renderer.state.WindowRenderState;
import net.minecraft.client.renderer.state.gui.BlitRenderState;
import net.minecraft.client.renderer.state.gui.GlyphRenderState;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.client.renderer.state.gui.GuiItemRenderState;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.client.renderer.state.gui.pip.OversizedItemRenderState;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.joml.Matrix3x2fc;
import org.joml.Matrix4f;
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
   public static final int CLEAR_COLOR = 0;
   private static final Comparator<ScreenRectangle> SCISSOR_COMPARATOR = Comparator.nullsFirst(Comparator.comparing(ScreenRectangle::top).thenComparing(ScreenRectangle::bottom).thenComparing(ScreenRectangle::left).thenComparing(ScreenRectangle::right));
   private static final Comparator<TextureSetup> TEXTURE_COMPARATOR = Comparator.nullsFirst(Comparator.comparing(TextureSetup::getSortKey));
   private static final Comparator<GuiElementRenderState> ELEMENT_SORT_COMPARATOR;
   private final Map<Object, OversizedItemRenderer> oversizedItemRenderers = new Object2ObjectOpenHashMap();
   private final GuiRenderState renderState;
   private final List<Draw> draws = new ArrayList();
   private final List<MeshToDraw> meshesToDraw = new ArrayList();
   private final ByteBufferBuilder byteBufferBuilder = new ByteBufferBuilder(786432);
   private final Map<VertexFormat, MappableRingBuffer> vertexBuffers = new Object2ObjectOpenHashMap();
   private int firstDrawIndexAfterBlur = 2147483647;
   private final Projection guiProjection = new Projection();
   private final ProjectionMatrixBuffer guiProjectionMatrixBuffer = new ProjectionMatrixBuffer("gui");
   private final MultiBufferSource.BufferSource bufferSource;
   private final SubmitNodeCollector submitNodeCollector;
   private final FeatureRenderDispatcher featureRenderDispatcher;
   private final Map<Class<? extends PictureInPictureRenderState>, PictureInPictureRenderer<?>> pictureInPictureRenderers;
   private @Nullable GuiItemAtlas itemAtlas;
   private int cachedGuiScale;
   private final CubeMap cubeMap = new CubeMap(Identifier.withDefaultNamespace("textures/gui/title/background/panorama"));
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

   public void endFrame() {
      if (this.itemAtlas != null) {
         this.itemAtlas.endFrame();
      }

   }

   public void render() {
      ProfilerFiller profiler = Profiler.get();
      if (this.renderState.panoramaRenderState != null) {
         this.cubeMap.render(10.0F, this.renderState.panoramaRenderState.spin());
      }

      profiler.push("prepare");
      this.prepare();
      profiler.popPush("draw");
      this.draw();
      profiler.popPush("vertexBufferRotate");

      for(MappableRingBuffer buffer : this.vertexBuffers.values()) {
         buffer.rotate();
      }

      profiler.pop();
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

   private void draw() {
      if (!this.draws.isEmpty()) {
         Minecraft minecraft = Minecraft.getInstance();
         WindowRenderState windowState = minecraft.gameRenderer.getGameRenderState().windowRenderState;
         this.guiProjection.setupOrtho(1000.0F, 11000.0F, (float)windowState.width / (float)windowState.guiScale, (float)windowState.height / (float)windowState.guiScale, true);
         RenderSystem.setProjectionMatrix(this.guiProjectionMatrixBuffer.getBuffer(this.guiProjection), ProjectionType.ORTHOGRAPHIC);
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
         GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms().writeTransform((new Matrix4f()).setTranslation(0.0F, 0.0F, -11000.0F));
         if (this.firstDrawIndexAfterBlur > 0) {
            this.executeDrawRange(() -> "GUI before blur", mainRenderTarget, dynamicTransforms, indexBuffer, indexType, 0, Math.min(this.firstDrawIndexAfterBlur, this.draws.size()));
         }

         if (this.draws.size() > this.firstDrawIndexAfterBlur) {
            RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(mainRenderTarget.getDepthTexture(), 0.0);
            minecraft.gameRenderer.processBlurEffect();
            this.executeDrawRange(() -> "GUI after blur", mainRenderTarget, dynamicTransforms, indexBuffer, indexType, this.firstDrawIndexAfterBlur, this.draws.size());
         }
      }
   }

   private void executeDrawRange(final Supplier<String> label, final RenderTarget mainRenderTarget, final GpuBufferSlice dynamicTransforms, final GpuBuffer indexBuffer, final VertexFormat.IndexType indexType, final int startIndex, final int endIndex) {
      try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(label, mainRenderTarget.getColorTextureView(), OptionalInt.empty(), mainRenderTarget.useDepth ? mainRenderTarget.getDepthTextureView() : null, OptionalDouble.empty())) {
         RenderSystem.bindDefaultUniforms(renderPass);
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
               GuiRenderer.this.renderState.addGlyphToCurrentLayer(new GlyphRenderState(pose, glyph, scissor));
            }
         });
      });
   }

   private void prepareItemElements() {
      Set<Object> itemsInFrame = this.renderState.getItemModelIdentities();
      if (!itemsInFrame.isEmpty()) {
         int guiScale = this.getGuiScaleInvalidatingItemAtlasIfChanged();
         GuiItemAtlas itemAtlas = this.prepareItemAtlas(itemsInFrame, 16 * guiScale);
         MutableBoolean hasOversizedItems = new MutableBoolean(false);
         this.renderState.forEachItem((itemState) -> {
            if (itemState.oversizedItemBounds() != null) {
               hasOversizedItems.setTrue();
            } else {
               GuiItemAtlas.SlotView slotView = itemAtlas.getOrUpdate(itemState.itemStackRenderState());
               if (slotView != null) {
                  this.submitBlitFromItemAtlas(itemState, slotView);
               }

            }
         });
         if (hasOversizedItems.booleanValue()) {
            this.renderState.forEachItem((itemState) -> {
               if (itemState.oversizedItemBounds() != null) {
                  TrackingItemStackRenderState itemStackRenderState = itemState.itemStackRenderState();
                  OversizedItemRenderer oversizedItemRenderer = (OversizedItemRenderer)this.oversizedItemRenderers.computeIfAbsent(itemStackRenderState.getModelIdentity(), (var0) -> new OversizedItemRenderer());
                  ScreenRectangle actualItemBounds = itemState.oversizedItemBounds();
                  OversizedItemRenderState oversizedItemRenderState = new OversizedItemRenderState(itemState, actualItemBounds.left(), actualItemBounds.top(), actualItemBounds.right(), actualItemBounds.bottom());
                  oversizedItemRenderer.prepare(oversizedItemRenderState, this.renderState, this.featureRenderDispatcher, guiScale);
               }

            });
         }

      }
   }

   private void preparePictureInPicture() {
      int guiScale = Minecraft.getInstance().gameRenderer.getGameRenderState().windowRenderState.guiScale;
      this.renderState.forEachPictureInPicture((pictureInPictureState) -> this.preparePictureInPictureState(pictureInPictureState, guiScale));
   }

   private <T extends PictureInPictureRenderState> void preparePictureInPictureState(final T picturesInPictureState, final int guiScale) {
      PictureInPictureRenderer<T> renderer = (PictureInPictureRenderer)this.pictureInPictureRenderers.get(picturesInPictureState.getClass());
      if (renderer != null) {
         renderer.prepare(picturesInPictureState, this.renderState, this.featureRenderDispatcher, guiScale);
      }

   }

   private void submitBlitFromItemAtlas(final GuiItemRenderState itemState, final GuiItemAtlas.SlotView slotView) {
      this.renderState.addBlitToCurrentLayer(new BlitRenderState(RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA, TextureSetup.singleTexture(slotView.textureView(), RenderSystem.getSamplerCache().getRepeat(FilterMode.NEAREST)), itemState.pose(), itemState.x(), itemState.y(), itemState.x() + 16, itemState.y() + 16, slotView.u0(), slotView.u1(), slotView.v0(), slotView.v1(), -1, itemState.scissorArea(), (ScreenRectangle)null));
   }

   private GuiItemAtlas prepareItemAtlas(final Set<Object> itemsInFrame, final int slotTextureSize) {
      if (this.itemAtlas != null && this.itemAtlas.tryPrepareFor(itemsInFrame)) {
         return this.itemAtlas;
      } else {
         int newTextureSize = GuiItemAtlas.computeTextureSizeFor(slotTextureSize, itemsInFrame.size());
         if (this.itemAtlas != null && this.itemAtlas.textureSize() == newTextureSize) {
            LOGGER.warn("Too many items ({}) in UI, some will be skipped! (Reached maximum texture size {}x{})", new Object[]{itemsInFrame.size(), newTextureSize, newTextureSize});
            return this.itemAtlas;
         } else {
            if (this.itemAtlas != null) {
               this.itemAtlas.close();
            }

            this.itemAtlas = new GuiItemAtlas(this.submitNodeCollector, this.featureRenderDispatcher, newTextureSize, slotTextureSize);
            return this.itemAtlas;
         }
      }
   }

   private int getGuiScaleInvalidatingItemAtlasIfChanged() {
      int guiScale = Minecraft.getInstance().gameRenderer.getGameRenderState().windowRenderState.guiScale;
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
      if (this.itemAtlas != null) {
         this.itemAtlas.close();
         this.itemAtlas = null;
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
      WindowRenderState windowState = Minecraft.getInstance().gameRenderer.getGameRenderState().windowRenderState;
      int windowHeight = windowState.height;
      int guiScale = windowState.guiScale;
      double left = (double)(rectangle.left() * guiScale);
      double bottom = (double)(windowHeight - rectangle.bottom() * guiScale);
      double width = (double)(rectangle.width() * guiScale);
      double height = (double)(rectangle.height() * guiScale);
      renderPass.enableScissor((int)left, (int)bottom, Math.max(0, (int)width), Math.max(0, (int)height));
   }

   public void registerPanoramaTextures(final TextureManager textureManager) {
      this.cubeMap.registerTextures(textureManager);
   }

   public void close() {
      this.byteBufferBuilder.close();
      if (this.itemAtlas != null) {
         this.itemAtlas.close();
         this.itemAtlas = null;
      }

      this.pictureInPictureRenderers.values().forEach(PictureInPictureRenderer::close);
      this.guiProjectionMatrixBuffer.close();

      for(MappableRingBuffer buffer : this.vertexBuffers.values()) {
         buffer.close();
      }

      this.oversizedItemRenderers.values().forEach(PictureInPictureRenderer::close);
      this.cubeMap.close();
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
}
