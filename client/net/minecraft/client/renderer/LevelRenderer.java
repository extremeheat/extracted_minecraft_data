package net.minecraft.client.renderer;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.resource.RenderTargetDescriptor;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;
import java.util.OptionalDouble;
import net.minecraft.SharedConstants;
import net.minecraft.TracingExecutor;
import net.minecraft.client.Camera;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Options;
import net.minecraft.client.PrioritizeChunkUpdates;
import net.minecraft.client.TextureFilteringMethod;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.chunk.ChunkSectionLayerGroup;
import net.minecraft.client.renderer.chunk.ChunkSectionsToRender;
import net.minecraft.client.renderer.chunk.CompiledSectionMesh;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.client.renderer.chunk.SectionMesh;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.chunk.TranslucencyPointOfView;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.gizmos.DrawableGizmoPrimitives;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.OptionsRenderState;
import net.minecraft.client.renderer.state.level.BlockBreakingRenderState;
import net.minecraft.client.renderer.state.level.BlockOutlineRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.client.renderer.state.level.SectionUpdateRenderState;
import net.minecraft.client.renderer.state.level.SkyRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.SectionPos;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.SimpleGizmoCollector;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.jspecify.annotations.Nullable;

public class LevelRenderer implements AutoCloseable {
   private static final Identifier TRANSPARENCY_POST_CHAIN_ID = Identifier.withDefaultNamespace("transparency");
   private static final Identifier ENTITY_OUTLINE_POST_CHAIN_ID = Identifier.withDefaultNamespace("entity_outline");
   private static final int MINIMUM_TRANSPARENT_SORT_COUNT = 15;
   private static final float CHUNK_VISIBILITY_THRESHOLD = 0.3F;
   private final GameRenderer gameRenderer;
   private final EntityRenderDispatcher entityRenderDispatcher;
   private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
   private final RenderBuffers renderBuffers;
   private final FeatureRenderDispatcher featureRenderDispatcher;
   private final SubmitNodeStorage submitNodeStorage;
   private final ModelManager modelManager;
   private final TextureManager textureManager;
   private final AtlasManager atlasManager;
   private final ShaderManager shaderManager;
   private final LevelRenderState levelRenderState;
   private final OptionsRenderState optionsRenderState;
   private @Nullable SkyRenderer skyRenderer;
   private final CloudRenderer cloudRenderer = new CloudRenderer();
   private final WorldBorderRenderer worldBorderRenderer = new WorldBorderRenderer();
   private final WeatherEffectRenderer weatherEffectRenderer = new WeatherEffectRenderer();
   private final SectionOcclusionGraph sectionOcclusionGraph = new SectionOcclusionGraph();
   private final ObjectArrayList<SectionRenderDispatcher.RenderSection> visibleSections = new ObjectArrayList(10000);
   private final ObjectArrayList<SectionRenderDispatcher.RenderSection> nearbyVisibleSections = new ObjectArrayList(50);
   private @Nullable ViewArea viewArea;
   private final RenderTarget entityOutlineTarget;
   private final LevelTargetBundle targets = new LevelTargetBundle();
   private @Nullable SectionRenderDispatcher sectionRenderDispatcher;
   private @Nullable BlockPos lastTranslucentSortBlockPos;
   private int translucencyResortIterationIndex;
   private @Nullable GpuSampler chunkLayerSampler;
   private final SimpleGizmoCollector renderThreadGizmos = new SimpleGizmoCollector();
   private FinalizedGizmos finalizedGizmos = new FinalizedGizmos(new DrawableGizmoPrimitives(), new DrawableGizmoPrimitives());

   public LevelRenderer(final EntityRenderDispatcher entityRenderDispatcher, final BlockEntityRenderDispatcher blockEntityRenderDispatcher, final ModelManager modelManager, final TextureManager textureManager, final AtlasManager atlasManager, final ShaderManager shaderManager, final GameRenderer gameRenderer, final int width, final int height) {
      super();
      this.gameRenderer = gameRenderer;
      this.entityRenderDispatcher = entityRenderDispatcher;
      this.blockEntityRenderDispatcher = blockEntityRenderDispatcher;
      this.renderBuffers = gameRenderer.renderBuffers();
      this.featureRenderDispatcher = gameRenderer.featureRenderDispatcher();
      this.submitNodeStorage = this.featureRenderDispatcher.getSubmitNodeStorage();
      this.modelManager = modelManager;
      this.textureManager = textureManager;
      this.atlasManager = atlasManager;
      this.shaderManager = shaderManager;
      this.levelRenderState = gameRenderer.gameRenderState().levelRenderState;
      this.optionsRenderState = gameRenderer.gameRenderState().optionsRenderState;
      this.entityOutlineTarget = new TextureTarget("Entity Outline", width, height, true);
   }

   public void render(final GraphicsResourceAllocator resourceAllocator, final DeltaTracker deltaTracker, final boolean renderOutline, final CameraRenderState cameraState, final Matrix4fc modelViewMatrix, final GpuBufferSlice terrainFog, final Vector4f fogColor, final boolean shouldRenderSky) {
      float deltaPartialTick = deltaTracker.getGameTimeDeltaPartialTick(false);
      final ProfilerFiller profiler = Profiler.get();
      profiler.push("repositionCamera");
      this.repositionCamera(cameraState);
      profiler.popPush("setupFrameGraph");
      Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
      modelViewStack.pushMatrix();
      modelViewStack.mul(modelViewMatrix);
      FrameGraphBuilder frame = new FrameGraphBuilder();
      this.targets.main = frame.<RenderTarget>importExternal("main", this.gameRenderer.mainRenderTarget());
      int screenWidth = this.gameRenderer.mainRenderTarget().width;
      int screenHeight = this.gameRenderer.mainRenderTarget().height;
      RenderTargetDescriptor screenSizeTargetDescriptor = new RenderTargetDescriptor(screenWidth, screenHeight, true, 0);
      PostChain transparencyChain = this.getTransparencyChain();
      if (transparencyChain != null) {
         this.targets.translucent = frame.<RenderTarget>createInternal("translucent", screenSizeTargetDescriptor);
         this.targets.itemEntity = frame.<RenderTarget>createInternal("item_entity", screenSizeTargetDescriptor);
         this.targets.particles = frame.<RenderTarget>createInternal("particles", screenSizeTargetDescriptor);
         this.targets.weather = frame.<RenderTarget>createInternal("weather", screenSizeTargetDescriptor);
         this.targets.clouds = frame.<RenderTarget>createInternal("clouds", screenSizeTargetDescriptor);
      }

      this.targets.entityOutline = frame.<RenderTarget>importExternal("entity_outline", this.entityOutlineTarget);
      FramePass clearPass = frame.addPass("clear");
      this.targets.main = clearPass.<RenderTarget>readsAndWrites(this.targets.main);
      clearPass.executes(() -> {
         RenderTarget mainRenderTarget = this.gameRenderer.mainRenderTarget();
         RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(mainRenderTarget.getColorTexture(), ARGB.colorFromFloat(0.0F, fogColor.x, fogColor.y, fogColor.z), mainRenderTarget.getDepthTexture(), 0.0);
      });
      if (shouldRenderSky) {
         this.addSkyPass(frame, cameraState, terrainFog);
      }

      ChunkSectionsToRender chunkSectionsToRender = this.prepareChunkRenders(this.levelRenderState.cameraRenderState.viewRotationMatrix);
      this.addMainPass(frame, modelViewMatrix, terrainFog, renderOutline, this.levelRenderState, profiler, chunkSectionsToRender);
      PostChain entityOutlineChain = this.shaderManager.getPostChain(ENTITY_OUTLINE_POST_CHAIN_ID, LevelTargetBundle.OUTLINE_TARGETS);
      if (this.levelRenderState.haveGlowingEntities && entityOutlineChain != null) {
         entityOutlineChain.addToFrame(frame, screenWidth, screenHeight, this.targets);
      }

      CloudStatus cloudStatus = this.optionsRenderState.cloudStatus;
      if (cloudStatus != CloudStatus.OFF && ARGB.alpha(this.levelRenderState.cloudColor) > 0) {
         this.addCloudsPass(frame, cloudStatus, this.levelRenderState.cameraRenderState.pos, this.levelRenderState.gameTime, deltaPartialTick, this.levelRenderState.cloudColor, this.levelRenderState.cloudHeight, this.optionsRenderState.cloudRange);
      }

      this.addWeatherPass(frame, terrainFog);
      if (transparencyChain != null) {
         transparencyChain.addToFrame(frame, screenWidth, screenHeight, this.targets);
      }

      this.addLateDebugPass(frame, this.levelRenderState.cameraRenderState, terrainFog, modelViewMatrix);
      profiler.popPush("executeFrameGraph");
      frame.execute(resourceAllocator, new FrameGraphBuilder.Inspector() {
         {
            Objects.requireNonNull(LevelRenderer.this);
         }

         public void beforeExecutePass(final String name) {
            profiler.push(name);
         }

         public void afterExecutePass(final String name) {
            profiler.pop();
         }
      });
      profiler.pop();
      this.targets.clear();
      modelViewStack.popMatrix();
      profiler.push("compileSections");
      this.compileSections(cameraState);
      profiler.pop();
      if (this.sectionRenderDispatcher != null) {
         this.sectionRenderDispatcher.lock();
         profiler.push("uploadTerrainBuffers");

         try {
            this.sectionRenderDispatcher.uploadTerrainBuffersToGpu();
         } finally {
            this.sectionRenderDispatcher.unlock();
         }

         profiler.pop();
      }

      profiler.push("updateSectionOcclusion");
      this.sectionOcclusionGraph.update(cameraState, this.optionsRenderState.fov, this.levelRenderState.addedEmptySections, this.levelRenderState.removedEmptySections);
      profiler.pop();
      Runnable playerCompiledSectionCallback = this.levelRenderState.playerCompiledSectionCallback;
      if (playerCompiledSectionCallback != null && this.isSectionCompiledAndVisible(this.levelRenderState.cameraRenderState.blockPos)) {
         playerCompiledSectionCallback.run();
      }

   }

   private void repositionCamera(final CameraRenderState camera) {
      Vec3 cameraPos = camera.pos;
      SectionPos cameraSectionPos = SectionPos.of((Position)cameraPos);
      if (this.viewArea.repositionCamera(cameraSectionPos)) {
         this.worldBorderRenderer.invalidate();
      }

      this.sectionRenderDispatcher.setCameraPosition(cameraPos);
   }

   private void addSkyPass(final FrameGraphBuilder frame, final CameraRenderState cameraState, final GpuBufferSlice skyFog) {
      FogType fogType = cameraState.fogType;
      if (fogType != FogType.POWDER_SNOW && fogType != FogType.LAVA && !cameraState.entityRenderState.doesMobEffectBlockSky) {
         if (this.levelRenderState.shouldResetSkyRenderer || this.skyRenderer == null) {
            if (this.skyRenderer != null) {
               this.skyRenderer.close();
            }

            this.skyRenderer = new SkyRenderer(this.textureManager, this.atlasManager, this.gameRenderer.mainRenderTarget());
         }

         SkyRenderState state = this.levelRenderState.skyRenderState;
         if (state.skybox != DimensionType.Skybox.NONE) {
            FramePass pass = frame.addPass("sky");
            this.targets.main = pass.<RenderTarget>readsAndWrites(this.targets.main);
            pass.executes(() -> {
               RenderSystem.setShaderFog(skyFog);
               if (state.skybox == DimensionType.Skybox.END) {
                  this.skyRenderer.renderEndSky();
                  if (state.endFlashIntensity > 1.0E-5F) {
                     PoseStack poseStack = new PoseStack();
                     this.skyRenderer.renderEndFlash(poseStack, state.endFlashIntensity, state.endFlashXAngle, state.endFlashYAngle);
                  }

               } else {
                  PoseStack poseStack = new PoseStack();
                  this.skyRenderer.renderSkyDisc(state.skyColor);
                  this.skyRenderer.renderSunriseAndSunset(poseStack, state.sunAngle, state.sunriseAndSunsetColor);
                  this.skyRenderer.renderSunMoonAndStars(poseStack, state.sunAngle, state.moonAngle, state.starAngle, state.moonPhase, state.rainBrightness, state.starBrightness);
                  if (state.shouldRenderDarkDisc) {
                     this.skyRenderer.renderDarkDisc();
                  }

               }
            });
         }
      }
   }

   private void addMainPass(final FrameGraphBuilder frame, final Matrix4fc modelViewMatrix, final GpuBufferSlice terrainFog, final boolean renderOutline, final LevelRenderState levelRenderState, final ProfilerFiller profiler, final ChunkSectionsToRender chunkSectionsToRender) {
      FramePass pass = frame.addPass("main");
      this.targets.main = pass.<RenderTarget>readsAndWrites(this.targets.main);
      if (this.targets.translucent != null) {
         this.targets.translucent = pass.<RenderTarget>readsAndWrites(this.targets.translucent);
      }

      if (this.targets.itemEntity != null) {
         this.targets.itemEntity = pass.<RenderTarget>readsAndWrites(this.targets.itemEntity);
      }

      if (this.targets.weather != null) {
         this.targets.weather = pass.<RenderTarget>readsAndWrites(this.targets.weather);
      }

      if (this.targets.particles != null) {
         this.targets.particles = pass.<RenderTarget>readsAndWrites(this.targets.particles);
      }

      if (levelRenderState.haveGlowingEntities && this.targets.entityOutline != null) {
         this.targets.entityOutline = pass.<RenderTarget>readsAndWrites(this.targets.entityOutline);
      }

      ResourceHandle<RenderTarget> mainTarget = this.targets.main;
      ResourceHandle<RenderTarget> translucentTarget = this.targets.translucent;
      ResourceHandle<RenderTarget> itemEntityTarget = this.targets.itemEntity;
      ResourceHandle<RenderTarget> entityOutlineTarget = this.targets.entityOutline;
      ResourceHandle<RenderTarget> particleTarget = this.targets.particles;
      pass.executes(() -> {
         RenderSystem.setShaderFog(terrainFog);
         if (levelRenderState.shouldResetChunkLayerSampler || this.chunkLayerSampler == null) {
            if (this.chunkLayerSampler != null) {
               this.chunkLayerSampler.close();
            }

            int maxAnisotropy = this.optionsRenderState.textureFiltering == TextureFilteringMethod.ANISOTROPIC ? this.optionsRenderState.maxAnisotropyValue : 1;
            this.chunkLayerSampler = RenderSystem.getDevice().createSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.LINEAR, FilterMode.LINEAR, maxAnisotropy, OptionalDouble.empty());
         }

         profiler.push("solidTerrain");
         chunkSectionsToRender.renderGroup(ChunkSectionLayerGroup.OPAQUE, this.chunkLayerSampler);
         this.gameRenderer.lighting().setupFor(Lighting.Entry.LEVEL);
         if (levelRenderState.shouldShowEntityOutlines && entityOutlineTarget != null) {
            RenderTarget outlineTarget = entityOutlineTarget.get();
            RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(outlineTarget.getColorTexture(), 0, outlineTarget.getDepthTexture(), 0.0);
         }

         PoseStack poseStack = new PoseStack();
         MultiBufferSource.BufferSource bufferSource = this.renderBuffers.bufferSource();
         MultiBufferSource.BufferSource crumblingBufferSource = this.renderBuffers.crumblingBufferSource();
         profiler.popPush("submitFeatures");
         this.submitEntities(poseStack, levelRenderState, this.submitNodeStorage);
         this.submitBlockEntities(poseStack, levelRenderState, this.submitNodeStorage);
         levelRenderState.particlesRenderState.submit(this.submitNodeStorage, levelRenderState.cameraRenderState);
         this.submitBlockDestroyAnimation(poseStack, this.submitNodeStorage, levelRenderState);
         if (renderOutline) {
            this.submitBlockOutline(poseStack, this.submitNodeStorage, levelRenderState);
         }

         profiler.popPush("renderSolidFeatures");
         this.featureRenderDispatcher.renderSolidFeatures();
         bufferSource.uploadAndDraw();
         profiler.pop();
         this.checkPoseStack(poseStack);
         if (translucentTarget != null) {
            ((RenderTarget)translucentTarget.get()).copyDepthFrom(mainTarget.get());
         }

         if (itemEntityTarget != null) {
            ((RenderTarget)itemEntityTarget.get()).copyDepthFrom(mainTarget.get());
         }

         if (particleTarget != null) {
            ((RenderTarget)particleTarget.get()).copyDepthFrom(mainTarget.get());
         }

         profiler.push("renderTranslucentFeatures");
         this.featureRenderDispatcher.renderTranslucentFeatures();
         bufferSource.uploadAndDraw();
         crumblingBufferSource.uploadAndDraw();
         profiler.pop();
         this.renderBuffers.outlineBufferSource().endOutlineBatch();
         this.finalizeGizmoCollection();
         this.finalizedGizmos.standardPrimitives().render(poseStack, bufferSource, levelRenderState.cameraRenderState, modelViewMatrix);
         bufferSource.uploadAndDraw();
         this.checkPoseStack(poseStack);
         profiler.push("translucentTerrain");
         chunkSectionsToRender.renderGroup(ChunkSectionLayerGroup.TRANSLUCENT, this.chunkLayerSampler);
         profiler.pop();
         this.featureRenderDispatcher.renderTranslucentAfterTerrain();
         bufferSource.uploadAndDraw();
         this.featureRenderDispatcher.clearSubmitNodes();
         levelRenderState.particlesRenderState.reset();
      });
   }

   private void addCloudsPass(final FrameGraphBuilder frame, final CloudStatus cloudStatus, final Vec3 cameraPosition, final long gameTime, final float partialTicks, final int cloudColor, final float cloudHeight, final int cloudRange) {
      FramePass pass = frame.addPass("clouds");
      if (this.targets.clouds != null) {
         this.targets.clouds = pass.<RenderTarget>readsAndWrites(this.targets.clouds);
      } else {
         this.targets.main = pass.<RenderTarget>readsAndWrites(this.targets.main);
      }

      pass.executes(() -> this.cloudRenderer.render(cloudColor, cloudStatus, cloudHeight, cloudRange, cameraPosition, gameTime, partialTicks));
   }

   private void addWeatherPass(final FrameGraphBuilder frame, final GpuBufferSlice fog) {
      int renderDistance = this.optionsRenderState.renderDistance * 16;
      FramePass pass = frame.addPass("weather");
      if (this.targets.weather != null) {
         this.targets.weather = pass.<RenderTarget>readsAndWrites(this.targets.weather);
      } else {
         this.targets.main = pass.<RenderTarget>readsAndWrites(this.targets.main);
      }

      pass.executes(() -> {
         RenderSystem.setShaderFog(fog);
         CameraRenderState cameraState = this.levelRenderState.cameraRenderState;
         this.weatherEffectRenderer.render(cameraState.pos, this.levelRenderState.weatherRenderState);
         this.worldBorderRenderer.render(this.levelRenderState.worldBorderRenderState, cameraState.pos, (double)renderDistance, (double)this.levelRenderState.cameraRenderState.depthFar);
      });
   }

   private void addLateDebugPass(final FrameGraphBuilder frame, final CameraRenderState camera, final GpuBufferSlice fog, final Matrix4fc modelViewMatrix) {
      FramePass pass = frame.addPass("late_debug");
      this.targets.main = pass.<RenderTarget>readsAndWrites(this.targets.main);
      if (this.targets.itemEntity != null) {
         this.targets.itemEntity = pass.<RenderTarget>readsAndWrites(this.targets.itemEntity);
      }

      ResourceHandle<RenderTarget> mainTarget = this.targets.main;
      pass.executes(() -> {
         RenderSystem.setShaderFog(fog);
         PoseStack poseStack = new PoseStack();
         MultiBufferSource.BufferSource bufferSource = this.renderBuffers.bufferSource();
         RenderTarget mainRenderTarget = mainTarget.get();
         RenderSystem.outputColorTextureOverride = mainRenderTarget.getColorTextureView();
         RenderSystem.outputDepthTextureOverride = mainRenderTarget.getDepthTextureView();
         if (!this.finalizedGizmos.alwaysOnTopPrimitives().isEmpty()) {
            RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(mainRenderTarget.getDepthTexture(), 0.0);
            this.finalizedGizmos.alwaysOnTopPrimitives().render(poseStack, bufferSource, camera, modelViewMatrix);
            bufferSource.uploadAndDraw();
         }

         RenderSystem.outputColorTextureOverride = null;
         RenderSystem.outputDepthTextureOverride = null;
         this.checkPoseStack(poseStack);
      });
   }

   public ChunkSectionsToRender prepareChunkRenders(final Matrix4fc modelViewMatrix) {
      ObjectListIterator<SectionRenderDispatcher.RenderSection> iterator = this.visibleSections.listIterator(0);
      EnumMap<ChunkSectionLayer, Int2ObjectOpenHashMap<List<RenderPass.Draw<GpuBufferSlice[]>>>> drawGroups = new EnumMap(ChunkSectionLayer.class);
      int largestIndexCount = 0;

      for(ChunkSectionLayer layer : ChunkSectionLayer.values()) {
         drawGroups.put(layer, new Int2ObjectOpenHashMap());
      }

      List<DynamicUniforms.ChunkSectionInfo> sectionInfos = new ArrayList();
      GpuTextureView blockAtlas = this.textureManager.getTexture(TextureAtlas.LOCATION_BLOCKS).getTextureView();
      int textureAtlasWidth = blockAtlas.getWidth(0);
      int textureAtlasHeight = blockAtlas.getHeight(0);
      if (this.sectionRenderDispatcher != null) {
         this.sectionRenderDispatcher.lock();

         try {
            while(iterator.hasNext()) {
               SectionRenderDispatcher.RenderSection section = (SectionRenderDispatcher.RenderSection)iterator.next();
               SectionMesh sectionMesh = section.getSectionMesh();
               BlockPos renderOffset = section.getRenderOrigin();
               long now = Util.getMillis();
               int uboIndex = -1;

               for(ChunkSectionLayer layer : ChunkSectionLayer.values()) {
                  SectionMesh.SectionDraw draw = sectionMesh.getSectionDraw(layer);
                  SectionRenderDispatcher.RenderSectionBufferSlice slice = this.sectionRenderDispatcher.getRenderSectionSlice(sectionMesh, layer);
                  if (slice != null && draw != null && (!draw.hasCustomIndexBuffer() || slice.indexBuffer() != null)) {
                     if (uboIndex == -1) {
                        uboIndex = sectionInfos.size();
                        sectionInfos.add(new DynamicUniforms.ChunkSectionInfo(new Matrix4f(modelViewMatrix), renderOffset.getX(), renderOffset.getY(), renderOffset.getZ(), section.getVisibility(now), textureAtlasWidth, textureAtlasHeight));
                     }

                     int combinedHash = 173;
                     VertexFormat vertexFormat = layer.pipeline().getVertexFormat();
                     GpuBuffer vertexBuffer = slice.vertexBuffer();
                     if (layer != ChunkSectionLayer.TRANSLUCENT) {
                        combinedHash = 31 * combinedHash + vertexBuffer.hashCode();
                     }

                     int firstIndex = 0;
                     GpuBuffer indexBuffer;
                     VertexFormat.IndexType indexType;
                     if (!draw.hasCustomIndexBuffer()) {
                        if (draw.indexCount() > largestIndexCount) {
                           largestIndexCount = draw.indexCount();
                        }

                        indexBuffer = null;
                        indexType = null;
                     } else {
                        indexBuffer = slice.indexBuffer();
                        indexType = draw.indexType();
                        if (layer != ChunkSectionLayer.TRANSLUCENT) {
                           combinedHash = 31 * combinedHash + indexBuffer.hashCode();
                           combinedHash = 31 * combinedHash + indexType.hashCode();
                        }

                        firstIndex = (int)(slice.indexBufferOffset() / (long)indexType.bytes);
                     }

                     int baseVertex = (int)(slice.vertexBufferOffset() / (long)vertexFormat.getVertexSize());
                     List<RenderPass.Draw<GpuBufferSlice[]>> draws = (List)((Int2ObjectOpenHashMap)drawGroups.get(layer)).computeIfAbsent(combinedHash, (var0) -> new ArrayList());
                     draws.add(new RenderPass.Draw(0, vertexBuffer, indexBuffer, indexType, firstIndex, draw.indexCount(), baseVertex, (sectionUbos, uploader) -> uploader.upload("ChunkSection", sectionUbos[uboIndex])));
                  }
               }
            }
         } finally {
            this.sectionRenderDispatcher.unlock();
         }
      }

      GpuBufferSlice[] chunkSectionInfos = RenderSystem.getDynamicUniforms().writeChunkSections((DynamicUniforms.ChunkSectionInfo[])sectionInfos.toArray(new DynamicUniforms.ChunkSectionInfo[0]));
      return new ChunkSectionsToRender(blockAtlas, drawGroups, largestIndexCount, chunkSectionInfos);
   }

   private void compileSections(final CameraRenderState camera) {
      ProfilerFiller profiler = Profiler.get();
      profiler.push("populateSectionsToCompile");
      BlockPos cameraPosition = camera.blockPos;
      long fadeDuration = (long)Mth.floor(this.optionsRenderState.chunkSectionFadeInTime * 1000.0);

      for(SectionUpdateRenderState state : this.levelRenderState.sectionUpdateRenderStates) {
         BlockPos center = SectionPos.of(state.sectionNode()).center();
         double distSqr = center.distSqr(cameraPosition);
         boolean isNearby = distSqr < 768.0;
         boolean rebuildSync = false;
         if (this.optionsRenderState.prioritizeChunkUpdates == PrioritizeChunkUpdates.NEARBY) {
            rebuildSync = isNearby || state.playerChanged();
         } else if (this.optionsRenderState.prioritizeChunkUpdates == PrioritizeChunkUpdates.PLAYER_AFFECTED) {
            rebuildSync = state.playerChanged();
         }

         SectionRenderDispatcher.RenderSection section = this.viewArea.getRenderSection(state.sectionNode());
         if (!isNearby && !section.wasPreviouslyEmpty()) {
            section.setFadeDuration(fadeDuration);
         } else {
            section.setFadeDuration(0L);
         }

         section.setWasPreviouslyEmpty(false);
         if (rebuildSync) {
            profiler.push("compileSectionSynchronously");
            section.compileSync(state.region());
            profiler.pop();
         } else {
            section.compileAsync(state.region());
         }
      }

      profiler.popPush("scheduleTranslucentResort");
      this.scheduleTranslucentSectionResort(camera.pos);
      profiler.pop();
   }

   private void checkPoseStack(final PoseStack poseStack) {
      if (!poseStack.isEmpty()) {
         throw new IllegalStateException("Pose stack not empty");
      }
   }

   private void submitEntities(final PoseStack poseStack, final LevelRenderState levelRenderState, final SubmitNodeCollector output) {
      Vec3 cameraPos = levelRenderState.cameraRenderState.pos;
      double camX = cameraPos.x();
      double camY = cameraPos.y();
      double camZ = cameraPos.z();

      for(EntityRenderState state : levelRenderState.entityRenderStates) {
         if (!levelRenderState.haveGlowingEntities) {
            state.outlineColor = 0;
         }

         this.entityRenderDispatcher.submit(state, levelRenderState.cameraRenderState, state.x - camX, state.y - camY, state.z - camZ, poseStack, output);
      }

   }

   private void submitBlockEntities(final PoseStack poseStack, final LevelRenderState levelRenderState, final SubmitNodeStorage submitNodeStorage) {
      Vec3 cameraPos = levelRenderState.cameraRenderState.pos;
      double camX = cameraPos.x();
      double camY = cameraPos.y();
      double camZ = cameraPos.z();

      for(BlockEntityRenderState renderState : levelRenderState.blockEntityRenderStates) {
         BlockPos blockPos = renderState.blockPos;
         poseStack.pushPose();
         poseStack.translate((double)blockPos.getX() - camX, (double)blockPos.getY() - camY, (double)blockPos.getZ() - camZ);
         this.blockEntityRenderDispatcher.submit(renderState, poseStack, submitNodeStorage, levelRenderState.cameraRenderState);
         poseStack.popPose();
      }

   }

   private void submitBlockDestroyAnimation(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final LevelRenderState levelRenderState) {
      Vec3 cameraPos = levelRenderState.cameraRenderState.pos;
      double camX = cameraPos.x();
      double camY = cameraPos.y();
      double camZ = cameraPos.z();

      for(BlockBreakingRenderState state : levelRenderState.blockBreakingRenderStates) {
         if (state.blockState().getRenderShape() == RenderShape.MODEL) {
            BlockPos pos = state.blockPos();
            poseStack.pushPose();
            poseStack.translate((double)pos.getX() - camX, (double)pos.getY() - camY, (double)pos.getZ() - camZ);
            poseStack.translate(state.blockState().getOffset(pos));
            BlockStateModel model = this.modelManager.getBlockStateModelSet().get(state.blockState());
            submitNodeCollector.submitBreakingBlockModel(poseStack, model, state.blockState().getSeed(pos), state.progress());
            poseStack.popPose();
         }
      }

   }

   private void submitBlockOutline(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final LevelRenderState levelRenderState) {
      BlockOutlineRenderState state = levelRenderState.blockOutlineRenderState;
      if (state != null) {
         Vec3 cameraPos = levelRenderState.cameraRenderState.pos;
         BlockPos pos = state.pos();
         poseStack.pushPose();
         poseStack.translate((double)pos.getX() - cameraPos.x, (double)pos.getY() - cameraPos.y, (double)pos.getZ() - cameraPos.z);
         if (state.highContrast()) {
            this.submitHitOutline(poseStack, submitNodeCollector, RenderTypes.secondaryBlockOutline(), state, -16777216, 7.0F, state.isTranslucent());
         }

         int outlineColor = state.highContrast() ? -11010079 : ARGB.black(102);
         this.submitHitOutline(poseStack, submitNodeCollector, RenderTypes.lines(), state, outlineColor, this.gameRenderer.gameRenderState().windowRenderState.appropriateLineWidth, state.isTranslucent());
         poseStack.popPose();
      }
   }

   private void submitHitOutline(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final RenderType renderType, final BlockOutlineRenderState state, final int color, final float width, final boolean afterTerrain) {
      if (SharedConstants.DEBUG_SHAPES) {
         submitNodeCollector.submitShapeOutline(poseStack, state.shape(), renderType, -1, width, afterTerrain);
         if (state.collisionShape() != null) {
            submitNodeCollector.submitShapeOutline(poseStack, state.collisionShape(), renderType, ARGB.colorFromFloat(0.4F, 0.0F, 0.0F, 0.0F), width, afterTerrain);
         }

         if (state.occlusionShape() != null) {
            submitNodeCollector.submitShapeOutline(poseStack, state.occlusionShape(), renderType, ARGB.colorFromFloat(0.4F, 0.0F, 1.0F, 0.0F), width, afterTerrain);
         }

         if (state.interactionShape() != null) {
            submitNodeCollector.submitShapeOutline(poseStack, state.interactionShape(), renderType, ARGB.colorFromFloat(0.4F, 0.0F, 0.0F, 1.0F), width, afterTerrain);
         }
      } else {
         submitNodeCollector.submitShapeOutline(poseStack, state.shape(), renderType, color, width, afterTerrain);
      }

   }

   public void resize(final int width, final int height) {
      this.sectionOcclusionGraph.invalidate();
      this.entityOutlineTarget.resize(width, height);
   }

   public void endFrame() {
      this.cloudRenderer.endFrame();
   }

   public void close() {
      this.entityOutlineTarget.destroyBuffers();
      if (this.skyRenderer != null) {
         this.skyRenderer.close();
      }

      if (this.chunkLayerSampler != null) {
         this.chunkLayerSampler.close();
      }

      this.worldBorderRenderer.close();
      this.cloudRenderer.close();
      this.weatherEffectRenderer.close();
   }

   public void doEntityOutline() {
      if (this.levelRenderState.shouldShowEntityOutlines) {
         this.entityOutlineTarget.blitAndBlendToTexture(this.gameRenderer.mainRenderTarget().getColorTextureView());
      }

   }

   public void invalidateCompiledGeometry(final ClientLevel level, final Options options, final Camera camera, final BlockColors blockColors) {
      SectionCompiler sectionCompiler = new SectionCompiler((Boolean)options.ambientOcclusion().get(), (Boolean)options.cutoutLeaves().get(), this.modelManager.getBlockStateModelSet(), this.modelManager.getFluidStateModelSet(), blockColors);
      if (this.sectionRenderDispatcher == null) {
         TracingExecutor var10003 = Util.backgroundExecutor();
         RenderBuffers var10004 = this.renderBuffers;
         SectionOcclusionGraph var10006 = this.sectionOcclusionGraph;
         Objects.requireNonNull(var10006);
         this.sectionRenderDispatcher = new SectionRenderDispatcher(var10003, var10004, sectionCompiler, var10006::schedulePropagationFrom);
      } else {
         this.sectionRenderDispatcher.setCompiler(sectionCompiler);
      }

      this.cloudRenderer().markForRebuild();
      LeavesBlock.setCutoutLeaves((Boolean)options.cutoutLeaves().get());
      if (this.viewArea != null) {
         this.viewArea.releaseAllBuffers();
      }

      this.sectionRenderDispatcher.clearCompileQueue();
      this.viewArea = new ViewArea(this.sectionRenderDispatcher, level.getMinY(), level.getMaxY(), level.getMinSectionY(), level.getMaxSectionY(), options.getEffectiveRenderDistance(), this.sectionOcclusionGraph);
      this.sectionOcclusionGraph().waitAndReset(this.viewArea);
      this.clearVisibleSections();
      SectionPos cameraSectionPos = SectionPos.of((Position)camera.position());
      this.viewArea.repositionCamera(cameraSectionPos);
   }

   private @Nullable PostChain getTransparencyChain() {
      return !this.gameRenderer.gameRenderState().useShaderTransparency() ? null : this.shaderManager.getPostChain(TRANSPARENCY_POST_CHAIN_ID, LevelTargetBundle.SORTING_TARGETS);
   }

   private void scheduleTranslucentSectionResort(final Vec3 cameraPos) {
      if (!this.visibleSections.isEmpty()) {
         BlockPos cameraBlockPos = BlockPos.containing(cameraPos);
         boolean blockPosChanged = !cameraBlockPos.equals(this.lastTranslucentSortBlockPos);
         TranslucencyPointOfView pointOfView = new TranslucencyPointOfView();
         ObjectListIterator var5 = this.nearbyVisibleSections.iterator();

         while(var5.hasNext()) {
            SectionRenderDispatcher.RenderSection section = (SectionRenderDispatcher.RenderSection)var5.next();
            this.scheduleResort(section, pointOfView, cameraPos, blockPosChanged, true);
         }

         this.translucencyResortIterationIndex %= this.visibleSections.size();
         int resortsLeft = Math.max(this.visibleSections.size() / 8, 15);

         while(resortsLeft-- > 0) {
            int index = this.translucencyResortIterationIndex++ % this.visibleSections.size();
            this.scheduleResort((SectionRenderDispatcher.RenderSection)this.visibleSections.get(index), pointOfView, cameraPos, blockPosChanged, false);
         }

         this.lastTranslucentSortBlockPos = cameraBlockPos;
      }
   }

   private void scheduleResort(final SectionRenderDispatcher.RenderSection section, final TranslucencyPointOfView pointOfView, final Vec3 cameraPos, final boolean blockPosChanged, final boolean isNearby) {
      pointOfView.set(cameraPos, section.getSectionNode());
      boolean pointOfViewChanged = section.getSectionMesh().isDifferentPointOfView(pointOfView);
      boolean resortBecauseBlockPosChanged = blockPosChanged && (pointOfView.isAxisAligned() || isNearby);
      if ((resortBecauseBlockPosChanged || pointOfViewChanged) && !section.transparencyResortingScheduled() && section.hasTranslucentGeometry()) {
         section.resortTransparency();
      }

   }

   public void clearVisibleSections() {
      this.visibleSections.clear();
      this.nearbyVisibleSections.clear();
   }

   public void resetLevelRenderData() {
      if (this.viewArea != null) {
         this.viewArea.releaseAllBuffers();
         this.viewArea = null;
      }

      if (this.sectionRenderDispatcher != null) {
         this.sectionRenderDispatcher.dispose();
      }

      this.sectionRenderDispatcher = null;
      this.sectionOcclusionGraph.waitAndReset((ViewArea)null);
      this.clearVisibleSections();
   }

   public boolean hasRenderedAllSections() {
      return this.sectionRenderDispatcher == null || this.sectionRenderDispatcher.isQueueEmpty();
   }

   public boolean isSectionCompiledAndVisible(final BlockPos blockPos) {
      if (this.viewArea == null) {
         return false;
      } else {
         SectionRenderDispatcher.RenderSection renderSection = this.viewArea.getRenderSectionAt(blockPos);
         if (renderSection != null && renderSection.sectionMesh.get() != CompiledSectionMesh.UNCOMPILED) {
            return renderSection.getVisibility(Util.getMillis()) >= 0.3F;
         } else {
            return false;
         }
      }
   }

   public @Nullable SectionRenderDispatcher sectionRenderDispatcher() {
      return this.sectionRenderDispatcher;
   }

   public EntityRenderDispatcher entityRenderDispatcher() {
      return this.entityRenderDispatcher;
   }

   public BlockEntityRenderDispatcher blockEntityRenderDispatcher() {
      return this.blockEntityRenderDispatcher;
   }

   public @Nullable RenderTarget entityOutlineTarget() {
      return this.targets.entityOutline != null ? (RenderTarget)this.targets.entityOutline.get() : null;
   }

   public @Nullable RenderTarget translucentTarget() {
      return this.targets.translucent != null ? (RenderTarget)this.targets.translucent.get() : null;
   }

   public @Nullable RenderTarget itemEntityTarget() {
      return this.targets.itemEntity != null ? (RenderTarget)this.targets.itemEntity.get() : null;
   }

   public @Nullable RenderTarget particlesTarget() {
      return this.targets.particles != null ? (RenderTarget)this.targets.particles.get() : null;
   }

   public @Nullable RenderTarget weatherTarget() {
      return this.targets.weather != null ? (RenderTarget)this.targets.weather.get() : null;
   }

   public @Nullable RenderTarget cloudsTarget() {
      return this.targets.clouds != null ? (RenderTarget)this.targets.clouds.get() : null;
   }

   public CloudRenderer cloudRenderer() {
      return this.cloudRenderer;
   }

   public @Nullable SkyRenderer skyRenderer() {
      return this.skyRenderer;
   }

   public WeatherEffectRenderer weatherEffectRenderer() {
      return this.weatherEffectRenderer;
   }

   public WorldBorderRenderer worldBorderRenderer() {
      return this.worldBorderRenderer;
   }

   public @Nullable ViewArea viewArea() {
      return this.viewArea;
   }

   public ObjectArrayList<SectionRenderDispatcher.RenderSection> visibleSections() {
      return this.visibleSections;
   }

   public ObjectArrayList<SectionRenderDispatcher.RenderSection> nearbyVisibleSections() {
      return this.nearbyVisibleSections;
   }

   public SectionOcclusionGraph sectionOcclusionGraph() {
      return this.sectionOcclusionGraph;
   }

   public Gizmos.TemporaryCollection collectPerFrameRenderThreadGizmos() {
      return Gizmos.withCollector(this.renderThreadGizmos);
   }

   private void finalizeGizmoCollection() {
      DrawableGizmoPrimitives standardPrimitives = new DrawableGizmoPrimitives();
      DrawableGizmoPrimitives alwaysOnTopPrimitives = new DrawableGizmoPrimitives();
      long currentMillis = Util.getMillis();

      for(SimpleGizmoCollector.GizmoInstance instance : this.renderThreadGizmos.drainGizmos()) {
         instance.gizmo().emit(instance.isAlwaysOnTop() ? alwaysOnTopPrimitives : standardPrimitives, instance.getAlphaMultiplier(currentMillis));
      }

      this.finalizedGizmos = new FinalizedGizmos(standardPrimitives, alwaysOnTopPrimitives);
   }

   public void addMainThreadGizmos(final List<SimpleGizmoCollector.GizmoInstance> mainThreadGizmos) {
      this.renderThreadGizmos.addTemporaryGizmos(mainThreadGizmos);
   }

   private static record FinalizedGizmos(DrawableGizmoPrimitives standardPrimitives, DrawableGizmoPrimitives alwaysOnTopPrimitives) {
      private FinalizedGizmos {
         super();
      }
   }
}
