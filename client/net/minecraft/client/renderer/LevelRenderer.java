package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
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
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.SortedSet;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.PrioritizeChunkUpdates;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.chunk.ChunkSectionLayerGroup;
import net.minecraft.client.renderer.chunk.ChunkSectionsToRender;
import net.minecraft.client.renderer.chunk.CompiledSectionMesh;
import net.minecraft.client.renderer.chunk.RenderRegionCache;
import net.minecraft.client.renderer.chunk.SectionBuffers;
import net.minecraft.client.renderer.chunk.SectionMesh;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.chunk.TranslucencyPointOfView;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.debug.GameTestBlockHighlightRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.gizmos.DrawableGizmoPrimitives;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.BlockBreakingRenderState;
import net.minecraft.client.renderer.state.BlockOutlineRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.client.renderer.state.ParticlesRenderState;
import net.minecraft.client.renderer.state.SkyRenderState;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.SectionPos;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.SimpleGizmoCollector;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.BlockDestructionProgress;
import net.minecraft.server.level.ParticleStatus;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.ARGB;
import net.minecraft.util.Brightness;
import net.minecraft.util.Mth;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class LevelRenderer implements ResourceManagerReloadListener, AutoCloseable {
   private static final ResourceLocation TRANSPARENCY_POST_CHAIN_ID = ResourceLocation.withDefaultNamespace("transparency");
   private static final ResourceLocation ENTITY_OUTLINE_POST_CHAIN_ID = ResourceLocation.withDefaultNamespace("entity_outline");
   public static final int SECTION_SIZE = 16;
   public static final int HALF_SECTION_SIZE = 8;
   public static final int NEARBY_SECTION_DISTANCE_IN_BLOCKS = 32;
   private static final int MINIMUM_TRANSPARENT_SORT_COUNT = 15;
   private final Minecraft minecraft;
   private final EntityRenderDispatcher entityRenderDispatcher;
   private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
   private final RenderBuffers renderBuffers;
   @Nullable
   private SkyRenderer skyRenderer;
   private final CloudRenderer cloudRenderer = new CloudRenderer();
   private final WorldBorderRenderer worldBorderRenderer = new WorldBorderRenderer();
   private final WeatherEffectRenderer weatherEffectRenderer = new WeatherEffectRenderer();
   private final ParticlesRenderState particlesRenderState = new ParticlesRenderState();
   public final DebugRenderer debugRenderer = new DebugRenderer();
   public final GameTestBlockHighlightRenderer gameTestBlockHighlightRenderer = new GameTestBlockHighlightRenderer();
   @Nullable
   private ClientLevel level;
   private final SectionOcclusionGraph sectionOcclusionGraph = new SectionOcclusionGraph();
   private final ObjectArrayList<SectionRenderDispatcher.RenderSection> visibleSections = new ObjectArrayList(10000);
   private final ObjectArrayList<SectionRenderDispatcher.RenderSection> nearbyVisibleSections = new ObjectArrayList(50);
   @Nullable
   private ViewArea viewArea;
   private int ticks;
   private final Int2ObjectMap<BlockDestructionProgress> destroyingBlocks = new Int2ObjectOpenHashMap();
   private final Long2ObjectMap<SortedSet<BlockDestructionProgress>> destructionProgress = new Long2ObjectOpenHashMap();
   @Nullable
   private RenderTarget entityOutlineTarget;
   private final LevelTargetBundle targets = new LevelTargetBundle();
   private int lastCameraSectionX = -2147483648;
   private int lastCameraSectionY = -2147483648;
   private int lastCameraSectionZ = -2147483648;
   private double prevCamX = 4.9E-324;
   private double prevCamY = 4.9E-324;
   private double prevCamZ = 4.9E-324;
   private double prevCamRotX = 4.9E-324;
   private double prevCamRotY = 4.9E-324;
   @Nullable
   private SectionRenderDispatcher sectionRenderDispatcher;
   private int lastViewDistance = -1;
   private boolean captureFrustum;
   @Nullable
   private Frustum capturedFrustum;
   @Nullable
   private BlockPos lastTranslucentSortBlockPos;
   private int translucencyResortIterationIndex;
   private final LevelRenderState levelRenderState;
   private final SubmitNodeStorage submitNodeStorage;
   private final FeatureRenderDispatcher featureRenderDispatcher;
   private final SimpleGizmoCollector collectedGizmos = new SimpleGizmoCollector();

   public LevelRenderer(Minecraft var1, EntityRenderDispatcher var2, BlockEntityRenderDispatcher var3, RenderBuffers var4, LevelRenderState var5, FeatureRenderDispatcher var6) {
      super();
      this.minecraft = var1;
      this.entityRenderDispatcher = var2;
      this.blockEntityRenderDispatcher = var3;
      this.renderBuffers = var4;
      this.submitNodeStorage = var6.getSubmitNodeStorage();
      this.levelRenderState = var5;
      this.featureRenderDispatcher = var6;
   }

   public void close() {
      if (this.entityOutlineTarget != null) {
         this.entityOutlineTarget.destroyBuffers();
      }

      if (this.skyRenderer != null) {
         this.skyRenderer.close();
      }

      this.cloudRenderer.close();
   }

   public void onResourceManagerReload(ResourceManager var1) {
      this.initOutline();
      if (this.skyRenderer != null) {
         this.skyRenderer.close();
      }

      this.skyRenderer = new SkyRenderer(this.minecraft.getTextureManager(), this.minecraft.getAtlasManager());
   }

   public void initOutline() {
      if (this.entityOutlineTarget != null) {
         this.entityOutlineTarget.destroyBuffers();
      }

      this.entityOutlineTarget = new TextureTarget("Entity Outline", this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight(), true);
   }

   @Nullable
   private PostChain getTransparencyChain() {
      if (!Minecraft.useShaderTransparency()) {
         return null;
      } else {
         PostChain var1 = this.minecraft.getShaderManager().getPostChain(TRANSPARENCY_POST_CHAIN_ID, LevelTargetBundle.SORTING_TARGETS);
         if (var1 == null) {
            this.minecraft.options.improvedTransparency().set(false);
            this.minecraft.options.save();
         }

         return var1;
      }
   }

   public void doEntityOutline() {
      if (this.shouldShowEntityOutlines()) {
         this.entityOutlineTarget.blitAndBlendToTexture(this.minecraft.getMainRenderTarget().getColorTextureView());
      }

   }

   protected boolean shouldShowEntityOutlines() {
      return !this.minecraft.gameRenderer.isPanoramicMode() && this.entityOutlineTarget != null && this.minecraft.player != null;
   }

   public void setLevel(@Nullable ClientLevel var1) {
      this.lastCameraSectionX = -2147483648;
      this.lastCameraSectionY = -2147483648;
      this.lastCameraSectionZ = -2147483648;
      this.level = var1;
      if (var1 != null) {
         this.allChanged();
      } else {
         this.entityRenderDispatcher.resetCamera();
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

      this.gameTestBlockHighlightRenderer.clear();
   }

   private void clearVisibleSections() {
      this.visibleSections.clear();
      this.nearbyVisibleSections.clear();
   }

   public void allChanged() {
      if (this.level != null) {
         this.level.clearTintCaches();
         if (this.sectionRenderDispatcher == null) {
            this.sectionRenderDispatcher = new SectionRenderDispatcher(this.level, this, Util.backgroundExecutor(), this.renderBuffers, this.minecraft.getBlockRenderer(), this.minecraft.getBlockEntityRenderDispatcher());
         } else {
            this.sectionRenderDispatcher.setLevel(this.level);
         }

         this.cloudRenderer.markForRebuild();
         ItemBlockRenderTypes.setCutoutLeaves((Boolean)this.minecraft.options.cutoutLeaves().get());
         this.lastViewDistance = this.minecraft.options.getEffectiveRenderDistance();
         if (this.viewArea != null) {
            this.viewArea.releaseAllBuffers();
         }

         this.sectionRenderDispatcher.clearCompileQueue();
         this.viewArea = new ViewArea(this.sectionRenderDispatcher, this.level, this.minecraft.options.getEffectiveRenderDistance(), this);
         this.sectionOcclusionGraph.waitAndReset(this.viewArea);
         this.clearVisibleSections();
         Camera var1 = this.minecraft.gameRenderer.getMainCamera();
         this.viewArea.repositionCamera(SectionPos.of((Position)var1.position()));
      }
   }

   public void resize(int var1, int var2) {
      this.needsUpdate();
      if (this.entityOutlineTarget != null) {
         this.entityOutlineTarget.resize(var1, var2);
      }

   }

   @Nullable
   public String getSectionStatistics() {
      if (this.viewArea == null) {
         return null;
      } else {
         int var1 = this.viewArea.sections.length;
         int var2 = this.countRenderedSections();
         return String.format(Locale.ROOT, "C: %d/%d %sD: %d, %s", var2, var1, this.minecraft.smartCull ? "(s) " : "", this.lastViewDistance, this.sectionRenderDispatcher == null ? "null" : this.sectionRenderDispatcher.getStats());
      }
   }

   @Nullable
   public SectionRenderDispatcher getSectionRenderDispatcher() {
      return this.sectionRenderDispatcher;
   }

   public double getTotalSections() {
      return this.viewArea == null ? 0.0 : (double)this.viewArea.sections.length;
   }

   public double getLastViewDistance() {
      return (double)this.lastViewDistance;
   }

   public int countRenderedSections() {
      int var1 = 0;
      ObjectListIterator var2 = this.visibleSections.iterator();

      while(var2.hasNext()) {
         SectionRenderDispatcher.RenderSection var3 = (SectionRenderDispatcher.RenderSection)var2.next();
         if (var3.getSectionMesh().hasRenderableLayers()) {
            ++var1;
         }
      }

      return var1;
   }

   @Nullable
   public String getEntityStatistics() {
      if (this.level == null) {
         return null;
      } else {
         int var10000 = this.levelRenderState.entityRenderStates.size();
         return "E: " + var10000 + "/" + this.level.getEntityCount() + ", SD: " + this.level.getServerSimulationDistance();
      }
   }

   private void cullTerrain(Camera var1, Frustum var2, boolean var3) {
      Vec3 var4 = var1.position();
      if (this.minecraft.options.getEffectiveRenderDistance() != this.lastViewDistance) {
         this.allChanged();
      }

      ProfilerFiller var5 = Profiler.get();
      var5.push("repositionCamera");
      int var6 = SectionPos.posToSectionCoord(var4.x());
      int var7 = SectionPos.posToSectionCoord(var4.y());
      int var8 = SectionPos.posToSectionCoord(var4.z());
      if (this.lastCameraSectionX != var6 || this.lastCameraSectionY != var7 || this.lastCameraSectionZ != var8) {
         this.lastCameraSectionX = var6;
         this.lastCameraSectionY = var7;
         this.lastCameraSectionZ = var8;
         this.viewArea.repositionCamera(SectionPos.of((Position)var4));
         this.worldBorderRenderer.invalidate();
      }

      this.sectionRenderDispatcher.setCameraPosition(var4);
      double var9 = Math.floor(var4.x / 8.0);
      double var11 = Math.floor(var4.y / 8.0);
      double var13 = Math.floor(var4.z / 8.0);
      if (var9 != this.prevCamX || var11 != this.prevCamY || var13 != this.prevCamZ) {
         this.sectionOcclusionGraph.invalidate();
      }

      this.prevCamX = var9;
      this.prevCamY = var11;
      this.prevCamZ = var13;
      var5.pop();
      if (this.capturedFrustum == null) {
         boolean var15 = this.minecraft.smartCull;
         if (var3 && this.level.getBlockState(var1.blockPosition()).isSolidRender()) {
            var15 = false;
         }

         var5.push("updateSOG");
         this.sectionOcclusionGraph.update(var15, var1, var2, this.visibleSections, this.level.getChunkSource().getLoadedEmptySections());
         var5.pop();
         double var16 = Math.floor((double)(var1.xRot() / 2.0F));
         double var18 = Math.floor((double)(var1.yRot() / 2.0F));
         if (this.sectionOcclusionGraph.consumeFrustumUpdate() || var16 != this.prevCamRotX || var18 != this.prevCamRotY) {
            var5.push("applyFrustum");
            this.applyFrustum(offsetFrustum(var2));
            var5.pop();
            this.prevCamRotX = var16;
            this.prevCamRotY = var18;
         }
      }

   }

   public static Frustum offsetFrustum(Frustum var0) {
      return (new Frustum(var0)).offsetToFullyIncludeCameraCube(8);
   }

   private void applyFrustum(Frustum var1) {
      if (!Minecraft.getInstance().isSameThread()) {
         throw new IllegalStateException("applyFrustum called from wrong thread: " + Thread.currentThread().getName());
      } else {
         this.clearVisibleSections();
         this.sectionOcclusionGraph.addSectionsInFrustum(var1, this.visibleSections, this.nearbyVisibleSections);
      }
   }

   public void addRecentlyCompiledSection(SectionRenderDispatcher.RenderSection var1) {
      this.sectionOcclusionGraph.schedulePropagationFrom(var1);
   }

   private Frustum prepareCullFrustum(Matrix4f var1, Matrix4f var2, Vec3 var3) {
      Frustum var4;
      if (this.capturedFrustum != null && !this.captureFrustum) {
         var4 = this.capturedFrustum;
      } else {
         var4 = new Frustum(var1, var2);
         var4.prepare(var3.x(), var3.y(), var3.z());
      }

      if (this.captureFrustum) {
         this.capturedFrustum = var4;
         this.captureFrustum = false;
      }

      return var4;
   }

   public void renderLevel(GraphicsResourceAllocator var1, DeltaTracker var2, boolean var3, Camera var4, Matrix4f var5, Matrix4f var6, Matrix4f var7, GpuBufferSlice var8, Vector4f var9, boolean var10) {
      float var11 = var2.getGameTimeDeltaPartialTick(false);
      this.levelRenderState.reset();
      this.blockEntityRenderDispatcher.prepare(var4);
      this.entityRenderDispatcher.prepare(var4, this.minecraft.crosshairPickEntity);
      final ProfilerFiller var12 = Profiler.get();
      var12.push("populateLightUpdates");
      this.level.pollLightUpdates();
      var12.popPush("runLightUpdates");
      this.level.getChunkSource().getLightEngine().runLightUpdates();
      var12.popPush("prepareCullFrustum");
      Vec3 var13 = var4.position();
      Frustum var14 = this.prepareCullFrustum(var5, var7, var13);
      var12.popPush("cullTerrain");
      this.cullTerrain(var4, var14, this.minecraft.player.isSpectator());
      var12.popPush("compileSections");
      this.compileSections(var4);
      var12.popPush("extract");
      var12.push("entities");
      this.extractVisibleEntities(var4, var14, var2, this.levelRenderState);
      var12.popPush("blockEntities");
      this.extractVisibleBlockEntities(var4, var11, this.levelRenderState);
      var12.popPush("blockOutline");
      this.extractBlockOutline(var4, this.levelRenderState);
      var12.popPush("blockBreaking");
      this.extractBlockDestroyAnimation(var4, this.levelRenderState);
      var12.popPush("weather");
      this.weatherEffectRenderer.extractRenderState(this.level, this.ticks, var11, var13, this.levelRenderState.weatherRenderState);
      var12.popPush("sky");
      this.skyRenderer.extractRenderState(this.level, var11, var4, this.levelRenderState.skyRenderState);
      var12.popPush("border");
      this.worldBorderRenderer.extract(this.level.getWorldBorder(), var11, var13, (double)(this.minecraft.options.getEffectiveRenderDistance() * 16), this.levelRenderState.worldBorderRenderState);
      var12.pop();
      var12.popPush("debug");
      this.debugRenderer.emitGizmos(var14, var13.x, var13.y, var13.z, var2.getGameTimeDeltaPartialTick(false));
      this.gameTestBlockHighlightRenderer.emitGizmos();
      var12.popPush("setupFrameGraph");
      Matrix4fStack var15 = RenderSystem.getModelViewStack();
      var15.pushMatrix();
      var15.mul(var5);
      FrameGraphBuilder var16 = new FrameGraphBuilder();
      this.targets.main = var16.<RenderTarget>importExternal("main", this.minecraft.getMainRenderTarget());
      int var17 = this.minecraft.getMainRenderTarget().width;
      int var18 = this.minecraft.getMainRenderTarget().height;
      RenderTargetDescriptor var19 = new RenderTargetDescriptor(var17, var18, true, 0);
      PostChain var20 = this.getTransparencyChain();
      if (var20 != null) {
         this.targets.translucent = var16.<RenderTarget>createInternal("translucent", var19);
         this.targets.itemEntity = var16.<RenderTarget>createInternal("item_entity", var19);
         this.targets.particles = var16.<RenderTarget>createInternal("particles", var19);
         this.targets.weather = var16.<RenderTarget>createInternal("weather", var19);
         this.targets.clouds = var16.<RenderTarget>createInternal("clouds", var19);
      }

      if (this.entityOutlineTarget != null) {
         this.targets.entityOutline = var16.<RenderTarget>importExternal("entity_outline", this.entityOutlineTarget);
      }

      FramePass var21 = var16.addPass("clear");
      this.targets.main = var21.<RenderTarget>readsAndWrites(this.targets.main);
      var21.executes(() -> {
         RenderTarget var2 = this.minecraft.getMainRenderTarget();
         RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(var2.getColorTexture(), ARGB.colorFromFloat(0.0F, var9.x, var9.y, var9.z), var2.getDepthTexture(), 1.0);
      });
      if (var10) {
         this.addSkyPass(var16, var4, var8);
      }

      this.addMainPass(var16, var14, var5, var8, var3, this.levelRenderState, var2, var12);
      PostChain var22 = this.minecraft.getShaderManager().getPostChain(ENTITY_OUTLINE_POST_CHAIN_ID, LevelTargetBundle.OUTLINE_TARGETS);
      if (this.levelRenderState.haveGlowingEntities && var22 != null) {
         var22.addToFrame(var16, var17, var18, this.targets);
      }

      this.minecraft.particleEngine.extract(this.particlesRenderState, (new Frustum(var14)).offset(-3.0F), var4, var11);
      this.addParticlesPass(var16, var8);
      CloudStatus var23 = this.minecraft.options.getCloudsType();
      if (var23 != CloudStatus.OFF) {
         float var24 = (Float)var4.attributeProbe().getValue(EnvironmentAttributes.CLOUD_OPACITY, var11);
         if (var24 > 0.0F) {
            float var25 = (float)this.level.getGameTime() + var11;
            int var26 = this.level.getCloudColor(var11);
            float var27 = (Float)var4.attributeProbe().getValue(EnvironmentAttributes.CLOUD_HEIGHT, var11);
            this.addCloudsPass(var16, var23, this.levelRenderState.cameraRenderState.pos, var25, ARGB.color(var24, var26), var27);
         }
      }

      this.addWeatherPass(var16, var8);
      if (var20 != null) {
         var20.addToFrame(var16, var17, var18, this.targets);
      }

      this.addLateDebugPass(var16, this.levelRenderState.cameraRenderState, var8, var5);
      var12.popPush("executeFrameGraph");
      var16.execute(var1, new FrameGraphBuilder.Inspector() {
         public void beforeExecutePass(String var1) {
            var12.push(var1);
         }

         public void afterExecutePass(String var1) {
            var12.pop();
         }
      });
      this.targets.clear();
      var15.popMatrix();
      var12.pop();
   }

   private void addMainPass(FrameGraphBuilder var1, Frustum var2, Matrix4f var3, GpuBufferSlice var4, boolean var5, LevelRenderState var6, DeltaTracker var7, ProfilerFiller var8) {
      FramePass var9 = var1.addPass("main");
      this.targets.main = var9.<RenderTarget>readsAndWrites(this.targets.main);
      if (this.targets.translucent != null) {
         this.targets.translucent = var9.<RenderTarget>readsAndWrites(this.targets.translucent);
      }

      if (this.targets.itemEntity != null) {
         this.targets.itemEntity = var9.<RenderTarget>readsAndWrites(this.targets.itemEntity);
      }

      if (this.targets.weather != null) {
         this.targets.weather = var9.<RenderTarget>readsAndWrites(this.targets.weather);
      }

      if (var6.haveGlowingEntities && this.targets.entityOutline != null) {
         this.targets.entityOutline = var9.<RenderTarget>readsAndWrites(this.targets.entityOutline);
      }

      ResourceHandle var10 = this.targets.main;
      ResourceHandle var11 = this.targets.translucent;
      ResourceHandle var12 = this.targets.itemEntity;
      ResourceHandle var13 = this.targets.entityOutline;
      var9.executes(() -> {
         RenderSystem.setShaderFog(var4);
         Vec3 var10x = var6.cameraRenderState.pos;
         double var11x = var10x.x();
         double var13x = var10x.y();
         double var15 = var10x.z();
         var8.push("terrain");
         ChunkSectionsToRender var17 = this.prepareChunkRenders(var3, var11x, var13x, var15);
         var17.renderGroup(ChunkSectionLayerGroup.OPAQUE);
         this.minecraft.gameRenderer.getLighting().setupFor(Lighting.Entry.LEVEL);
         if (var12 != null) {
            ((RenderTarget)var12.get()).copyDepthFrom(this.minecraft.getMainRenderTarget());
         }

         if (this.shouldShowEntityOutlines() && var13 != null) {
            RenderTarget var18 = (RenderTarget)var13.get();
            RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(var18.getColorTexture(), 0, var18.getDepthTexture(), 1.0);
         }

         PoseStack var21 = new PoseStack();
         MultiBufferSource.BufferSource var19 = this.renderBuffers.bufferSource();
         MultiBufferSource.BufferSource var20 = this.renderBuffers.crumblingBufferSource();
         var8.popPush("submitEntities");
         this.submitEntities(var21, var6, this.submitNodeStorage);
         var8.popPush("submitBlockEntities");
         this.submitBlockEntities(var21, var6, this.submitNodeStorage);
         var8.popPush("renderFeatures");
         this.featureRenderDispatcher.renderAllFeatures();
         var19.endLastBatch();
         this.checkPoseStack(var21);
         var19.endBatch(RenderTypes.solid());
         var19.endBatch(RenderTypes.endPortal());
         var19.endBatch(RenderTypes.endGateway());
         var19.endBatch(Sheets.solidBlockSheet());
         var19.endBatch(Sheets.cutoutBlockSheet());
         var19.endBatch(Sheets.bedSheet());
         var19.endBatch(Sheets.shulkerBoxSheet());
         var19.endBatch(Sheets.signSheet());
         var19.endBatch(Sheets.hangingSignSheet());
         var19.endBatch(Sheets.chestSheet());
         this.renderBuffers.outlineBufferSource().endOutlineBatch();
         if (var5) {
            this.renderBlockOutline(var19, var21, false, var6);
         }

         var8.pop();
         this.checkPoseStack(var21);
         var19.endBatch(Sheets.translucentItemSheet());
         var19.endBatch(Sheets.bannerSheet());
         var19.endBatch(Sheets.shieldSheet());
         var19.endBatch(RenderTypes.armorEntityGlint());
         var19.endBatch(RenderTypes.glint());
         var19.endBatch(RenderTypes.glintTranslucent());
         var19.endBatch(RenderTypes.entityGlint());
         var8.push("destroyProgress");
         this.renderBlockDestroyAnimation(var21, var20, var6);
         var20.endBatch();
         var8.pop();
         this.checkPoseStack(var21);
         var19.endBatch(RenderTypes.waterMask());
         var19.endBatch();
         if (var11 != null) {
            ((RenderTarget)var11.get()).copyDepthFrom((RenderTarget)var10.get());
         }

         var8.push("translucent");
         var17.renderGroup(ChunkSectionLayerGroup.TRANSLUCENT);
         var8.popPush("string");
         var17.renderGroup(ChunkSectionLayerGroup.TRIPWIRE);
         if (var5) {
            this.renderBlockOutline(var19, var21, true, var6);
         }

         var19.endBatch();
         var8.pop();
      });
   }

   private void addParticlesPass(FrameGraphBuilder var1, GpuBufferSlice var2) {
      FramePass var3 = var1.addPass("particles");
      if (this.targets.particles != null) {
         this.targets.particles = var3.<RenderTarget>readsAndWrites(this.targets.particles);
         var3.reads(this.targets.main);
      } else {
         this.targets.main = var3.<RenderTarget>readsAndWrites(this.targets.main);
      }

      ResourceHandle var4 = this.targets.main;
      ResourceHandle var5 = this.targets.particles;
      var3.executes(() -> {
         RenderSystem.setShaderFog(var2);
         if (var5 != null) {
            ((RenderTarget)var5.get()).copyDepthFrom((RenderTarget)var4.get());
         }

         this.particlesRenderState.submit(this.submitNodeStorage, this.levelRenderState.cameraRenderState);
         this.featureRenderDispatcher.renderAllFeatures();
         this.particlesRenderState.reset();
      });
   }

   private void addCloudsPass(FrameGraphBuilder var1, CloudStatus var2, Vec3 var3, float var4, int var5, float var6) {
      FramePass var7 = var1.addPass("clouds");
      if (this.targets.clouds != null) {
         this.targets.clouds = var7.<RenderTarget>readsAndWrites(this.targets.clouds);
      } else {
         this.targets.main = var7.<RenderTarget>readsAndWrites(this.targets.main);
      }

      var7.executes(() -> this.cloudRenderer.render(var5, var2, var6, var3, var4));
   }

   private void addWeatherPass(FrameGraphBuilder var1, GpuBufferSlice var2) {
      int var3 = this.minecraft.options.getEffectiveRenderDistance() * 16;
      float var4 = this.minecraft.gameRenderer.getDepthFar();
      FramePass var5 = var1.addPass("weather");
      if (this.targets.weather != null) {
         this.targets.weather = var5.<RenderTarget>readsAndWrites(this.targets.weather);
      } else {
         this.targets.main = var5.<RenderTarget>readsAndWrites(this.targets.main);
      }

      var5.executes(() -> {
         RenderSystem.setShaderFog(var2);
         MultiBufferSource.BufferSource var4x = this.renderBuffers.bufferSource();
         CameraRenderState var5 = this.levelRenderState.cameraRenderState;
         this.weatherEffectRenderer.render(var4x, var5.pos, this.levelRenderState.weatherRenderState);
         this.worldBorderRenderer.render(this.levelRenderState.worldBorderRenderState, var5.pos, (double)var3, (double)var4);
         var4x.endBatch();
      });
   }

   private void addLateDebugPass(FrameGraphBuilder var1, CameraRenderState var2, GpuBufferSlice var3, Matrix4f var4) {
      FramePass var5 = var1.addPass("late_debug");
      this.targets.main = var5.<RenderTarget>readsAndWrites(this.targets.main);
      if (this.targets.itemEntity != null) {
         this.targets.itemEntity = var5.<RenderTarget>readsAndWrites(this.targets.itemEntity);
      }

      ResourceHandle var6 = this.targets.main;
      var5.executes(() -> {
         RenderSystem.setShaderFog(var3);
         PoseStack var5 = new PoseStack();
         MultiBufferSource.BufferSource var6x = this.renderBuffers.bufferSource();
         RenderSystem.outputColorTextureOverride = ((RenderTarget)var6.get()).getColorTextureView();
         RenderSystem.outputDepthTextureOverride = ((RenderTarget)var6.get()).getDepthTextureView();
         DrawableGizmoPrimitives var7 = new DrawableGizmoPrimitives();
         DrawableGizmoPrimitives var8 = new DrawableGizmoPrimitives();
         boolean var9 = false;
         this.collectedGizmos.addTemporaryGizmos(this.minecraft.getPerTickGizmos());
         IntegratedServer var10 = this.minecraft.getSingleplayerServer();
         if (var10 != null) {
            this.collectedGizmos.addTemporaryGizmos(var10.getPerTickGizmos());
         }

         long var11 = Util.getMillis();

         for(SimpleGizmoCollector.GizmoInstance var14 : this.collectedGizmos.drainGizmos()) {
            float var15 = var14.getAlphaMultiplier(var11);
            if (var14.isAlwaysOnTop()) {
               var9 = true;
               var14.gizmo().emit(var8, var15);
            } else {
               var14.gizmo().emit(var7, var15);
            }
         }

         var7.render(var5, var6x, var2, var4);
         var6x.endLastBatch();
         if (var9) {
            RenderTarget var16 = Minecraft.getInstance().getMainRenderTarget();
            RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(var16.getDepthTexture(), 1.0);
            var8.render(var5, var6x, var2, var4);
            var6x.endLastBatch();
         }

         RenderSystem.outputColorTextureOverride = null;
         RenderSystem.outputDepthTextureOverride = null;
         this.checkPoseStack(var5);
      });
   }

   private void extractVisibleEntities(Camera var1, Frustum var2, DeltaTracker var3, LevelRenderState var4) {
      Vec3 var5 = var1.position();
      double var6 = var5.x();
      double var8 = var5.y();
      double var10 = var5.z();
      TickRateManager var12 = this.minecraft.level.tickRateManager();
      boolean var13 = this.shouldShowEntityOutlines();
      Entity.setViewScale(Mth.clamp((double)this.minecraft.options.getEffectiveRenderDistance() / 8.0, 1.0, 2.5) * (Double)this.minecraft.options.entityDistanceScaling().get());

      for(Entity var15 : this.level.entitiesForRendering()) {
         if (this.entityRenderDispatcher.shouldRender(var15, var2, var6, var8, var10) || var15.hasIndirectPassenger(this.minecraft.player)) {
            BlockPos var16 = var15.blockPosition();
            if ((this.level.isOutsideBuildHeight(var16.getY()) || this.isSectionCompiled(var16)) && (var15 != var1.entity() || var1.isDetached() || var1.entity() instanceof LivingEntity && ((LivingEntity)var1.entity()).isSleeping()) && (!(var15 instanceof LocalPlayer) || var1.entity() == var15)) {
               if (var15.tickCount == 0) {
                  var15.xOld = var15.getX();
                  var15.yOld = var15.getY();
                  var15.zOld = var15.getZ();
               }

               float var17 = var3.getGameTimeDeltaPartialTick(!var12.isEntityFrozen(var15));
               EntityRenderState var18 = this.extractEntity(var15, var17);
               var4.entityRenderStates.add(var18);
               if (var18.appearsGlowing() && var13) {
                  var4.haveGlowingEntities = true;
               }
            }
         }
      }

   }

   private void submitEntities(PoseStack var1, LevelRenderState var2, SubmitNodeCollector var3) {
      Vec3 var4 = var2.cameraRenderState.pos;
      double var5 = var4.x();
      double var7 = var4.y();
      double var9 = var4.z();

      for(EntityRenderState var12 : var2.entityRenderStates) {
         if (!var2.haveGlowingEntities) {
            var12.outlineColor = 0;
         }

         this.entityRenderDispatcher.submit(var12, var2.cameraRenderState, var12.x - var5, var12.y - var7, var12.z - var9, var1, var3);
      }

   }

   private void extractVisibleBlockEntities(Camera var1, float var2, LevelRenderState var3) {
      Vec3 var4 = var1.position();
      double var5 = var4.x();
      double var7 = var4.y();
      double var9 = var4.z();
      PoseStack var11 = new PoseStack();
      Iterator var12 = this.visibleSections.iterator();

      while(var12.hasNext()) {
         SectionRenderDispatcher.RenderSection var13 = (SectionRenderDispatcher.RenderSection)var12.next();
         List var14 = var13.getSectionMesh().getRenderableBlockEntities();
         if (!var14.isEmpty()) {
            for(BlockEntity var16 : var14) {
               BlockPos var17 = var16.getBlockPos();
               SortedSet var18 = (SortedSet)this.destructionProgress.get(var17.asLong());
               ModelFeatureRenderer.CrumblingOverlay var19;
               if (var18 != null && !var18.isEmpty()) {
                  var11.pushPose();
                  var11.translate((double)var17.getX() - var5, (double)var17.getY() - var7, (double)var17.getZ() - var9);
                  var19 = new ModelFeatureRenderer.CrumblingOverlay(((BlockDestructionProgress)var18.last()).getProgress(), var11.last());
                  var11.popPose();
               } else {
                  var19 = null;
               }

               BlockEntityRenderState var20 = this.blockEntityRenderDispatcher.tryExtractRenderState(var16, var2, var19);
               if (var20 != null) {
                  var3.blockEntityRenderStates.add(var20);
               }
            }
         }
      }

      var12 = this.level.getGloballyRenderedBlockEntities().iterator();

      while(var12.hasNext()) {
         BlockEntity var22 = (BlockEntity)var12.next();
         if (var22.isRemoved()) {
            var12.remove();
         } else {
            BlockEntityRenderState var23 = this.blockEntityRenderDispatcher.tryExtractRenderState(var22, var2, (ModelFeatureRenderer.CrumblingOverlay)null);
            if (var23 != null) {
               var3.blockEntityRenderStates.add(var23);
            }
         }
      }

   }

   private void submitBlockEntities(PoseStack var1, LevelRenderState var2, SubmitNodeStorage var3) {
      Vec3 var4 = var2.cameraRenderState.pos;
      double var5 = var4.x();
      double var7 = var4.y();
      double var9 = var4.z();

      for(BlockEntityRenderState var12 : var2.blockEntityRenderStates) {
         BlockPos var13 = var12.blockPos;
         var1.pushPose();
         var1.translate((double)var13.getX() - var5, (double)var13.getY() - var7, (double)var13.getZ() - var9);
         this.blockEntityRenderDispatcher.submit(var12, var1, var3, var2.cameraRenderState);
         var1.popPose();
      }

   }

   private void extractBlockDestroyAnimation(Camera var1, LevelRenderState var2) {
      Vec3 var3 = var1.position();
      double var4 = var3.x();
      double var6 = var3.y();
      double var8 = var3.z();
      var2.blockBreakingRenderStates.clear();
      ObjectIterator var10 = this.destructionProgress.long2ObjectEntrySet().iterator();

      while(var10.hasNext()) {
         Long2ObjectMap.Entry var11 = (Long2ObjectMap.Entry)var10.next();
         BlockPos var12 = BlockPos.of(var11.getLongKey());
         if (!(var12.distToCenterSqr(var4, var6, var8) > 1024.0)) {
            SortedSet var13 = (SortedSet)var11.getValue();
            if (var13 != null && !var13.isEmpty()) {
               int var14 = ((BlockDestructionProgress)var13.last()).getProgress();
               var2.blockBreakingRenderStates.add(new BlockBreakingRenderState(this.level, var12, var14));
            }
         }
      }

   }

   private void renderBlockDestroyAnimation(PoseStack var1, MultiBufferSource.BufferSource var2, LevelRenderState var3) {
      Vec3 var4 = var3.cameraRenderState.pos;
      double var5 = var4.x();
      double var7 = var4.y();
      double var9 = var4.z();

      for(BlockBreakingRenderState var12 : var3.blockBreakingRenderStates) {
         var1.pushPose();
         BlockPos var13 = var12.blockPos;
         var1.translate((double)var13.getX() - var5, (double)var13.getY() - var7, (double)var13.getZ() - var9);
         PoseStack.Pose var14 = var1.last();
         SheetedDecalTextureGenerator var15 = new SheetedDecalTextureGenerator(var2.getBuffer((RenderType)ModelBakery.DESTROY_TYPES.get(var12.progress)), var14, 1.0F);
         this.minecraft.getBlockRenderer().renderBreakingTexture(var12.blockState, var13, var12, var1, var15);
         var1.popPose();
      }

   }

   private void extractBlockOutline(Camera var1, LevelRenderState var2) {
      var2.blockOutlineRenderState = null;
      HitResult var4 = this.minecraft.hitResult;
      if (var4 instanceof BlockHitResult var3) {
         if (var3.getType() != HitResult.Type.MISS) {
            BlockPos var13 = var3.getBlockPos();
            BlockState var5 = this.level.getBlockState(var13);
            if (!var5.isAir() && this.level.getWorldBorder().isWithinBounds(var13)) {
               boolean var6 = ItemBlockRenderTypes.getChunkRenderType(var5).sortOnUpload();
               boolean var7 = (Boolean)this.minecraft.options.highContrastBlockOutline().get();
               CollisionContext var8 = CollisionContext.of(var1.entity());
               VoxelShape var9 = var5.getShape(this.level, var13, var8);
               if (SharedConstants.DEBUG_SHAPES) {
                  VoxelShape var10 = var5.getCollisionShape(this.level, var13, var8);
                  VoxelShape var11 = var5.getOcclusionShape();
                  VoxelShape var12 = var5.getInteractionShape(this.level, var13);
                  var2.blockOutlineRenderState = new BlockOutlineRenderState(var13, var6, var7, var9, var10, var11, var12);
               } else {
                  var2.blockOutlineRenderState = new BlockOutlineRenderState(var13, var6, var7, var9);
               }
            }

         }
      }
   }

   private void renderBlockOutline(MultiBufferSource.BufferSource var1, PoseStack var2, boolean var3, LevelRenderState var4) {
      BlockOutlineRenderState var5 = var4.blockOutlineRenderState;
      if (var5 != null) {
         if (var5.isTranslucent() == var3) {
            Vec3 var6 = var4.cameraRenderState.pos;
            if (var5.highContrast()) {
               VertexConsumer var7 = var1.getBuffer(RenderTypes.secondaryBlockOutline());
               this.renderHitOutline(var2, var7, var6.x, var6.y, var6.z, var5, -16777216, 7.0F);
            }

            VertexConsumer var9 = var1.getBuffer(RenderTypes.lines());
            int var8 = var5.highContrast() ? -11010079 : ARGB.black(102);
            this.renderHitOutline(var2, var9, var6.x, var6.y, var6.z, var5, var8, this.minecraft.getWindow().getAppropriateLineWidth());
            var1.endLastBatch();
         }
      }
   }

   private void checkPoseStack(PoseStack var1) {
      if (!var1.isEmpty()) {
         throw new IllegalStateException("Pose stack not empty");
      }
   }

   private EntityRenderState extractEntity(Entity var1, float var2) {
      return this.entityRenderDispatcher.extractEntity(var1, var2);
   }

   private void scheduleTranslucentSectionResort(Vec3 var1) {
      if (!this.visibleSections.isEmpty()) {
         BlockPos var2 = BlockPos.containing(var1);
         boolean var3 = !var2.equals(this.lastTranslucentSortBlockPos);
         TranslucencyPointOfView var4 = new TranslucencyPointOfView();
         ObjectListIterator var5 = this.nearbyVisibleSections.iterator();

         while(var5.hasNext()) {
            SectionRenderDispatcher.RenderSection var6 = (SectionRenderDispatcher.RenderSection)var5.next();
            this.scheduleResort(var6, var4, var1, var3, true);
         }

         this.translucencyResortIterationIndex %= this.visibleSections.size();
         int var7 = Math.max(this.visibleSections.size() / 8, 15);

         while(var7-- > 0) {
            int var8 = this.translucencyResortIterationIndex++ % this.visibleSections.size();
            this.scheduleResort((SectionRenderDispatcher.RenderSection)this.visibleSections.get(var8), var4, var1, var3, false);
         }

         this.lastTranslucentSortBlockPos = var2;
      }
   }

   private void scheduleResort(SectionRenderDispatcher.RenderSection var1, TranslucencyPointOfView var2, Vec3 var3, boolean var4, boolean var5) {
      var2.set(var3, var1.getSectionNode());
      boolean var6 = var1.getSectionMesh().isDifferentPointOfView(var2);
      boolean var7 = var4 && (var2.isAxisAligned() || var5);
      if ((var7 || var6) && !var1.transparencyResortingScheduled() && var1.hasTranslucentGeometry()) {
         var1.resortTransparency(this.sectionRenderDispatcher);
      }

   }

   private ChunkSectionsToRender prepareChunkRenders(Matrix4fc var1, double var2, double var4, double var6) {
      ObjectListIterator var8 = this.visibleSections.listIterator(0);
      EnumMap var9 = new EnumMap(ChunkSectionLayer.class);
      int var10 = 0;

      for(ChunkSectionLayer var14 : ChunkSectionLayer.values()) {
         var9.put(var14, new ArrayList());
      }

      ArrayList var25 = new ArrayList();
      Vector4f var26 = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
      Matrix4f var27 = new Matrix4f();

      while(var8.hasNext()) {
         SectionRenderDispatcher.RenderSection var28 = (SectionRenderDispatcher.RenderSection)var8.next();
         SectionMesh var15 = var28.getSectionMesh();

         for(ChunkSectionLayer var19 : ChunkSectionLayer.values()) {
            SectionBuffers var20 = var15.getBuffers(var19);
            if (var20 != null) {
               GpuBuffer var21;
               VertexFormat.IndexType var22;
               if (var20.getIndexBuffer() == null) {
                  if (var20.getIndexCount() > var10) {
                     var10 = var20.getIndexCount();
                  }

                  var21 = null;
                  var22 = null;
               } else {
                  var21 = var20.getIndexBuffer();
                  var22 = var20.getIndexType();
               }

               BlockPos var23 = var28.getRenderOrigin();
               int var24 = var25.size();
               var25.add(new DynamicUniforms.Transform(var1, var26, new Vector3f((float)((double)var23.getX() - var2), (float)((double)var23.getY() - var4), (float)((double)var23.getZ() - var6)), var27));
               ((List)var9.get(var19)).add(new RenderPass.Draw(0, var20.getVertexBuffer(), var21, var22, 0, var20.getIndexCount(), (var1x, var2x) -> var2x.upload("DynamicTransforms", var1x[var24])));
            }
         }
      }

      GpuBufferSlice[] var29 = RenderSystem.getDynamicUniforms().writeTransforms((DynamicUniforms.Transform[])var25.toArray(new DynamicUniforms.Transform[0]));
      return new ChunkSectionsToRender(var9, var10, var29);
   }

   public void endFrame() {
      this.cloudRenderer.endFrame();
   }

   public void captureFrustum() {
      this.captureFrustum = true;
   }

   public void killFrustum() {
      this.capturedFrustum = null;
   }

   public void tick(Camera var1) {
      if (this.level.tickRateManager().runsNormally()) {
         ++this.ticks;
      }

      this.weatherEffectRenderer.tickRainParticles(this.level, var1, this.ticks, (ParticleStatus)this.minecraft.options.particles().get(), (Integer)this.minecraft.options.weatherRadius().get());
      this.removeBlockBreakingProgress();
   }

   private void removeBlockBreakingProgress() {
      if (this.ticks % 20 == 0) {
         ObjectIterator var1 = this.destroyingBlocks.values().iterator();

         while(var1.hasNext()) {
            BlockDestructionProgress var2 = (BlockDestructionProgress)var1.next();
            int var3 = var2.getUpdatedRenderTick();
            if (this.ticks - var3 > 400) {
               var1.remove();
               this.removeProgress(var2);
            }
         }

      }
   }

   private void removeProgress(BlockDestructionProgress var1) {
      long var2 = var1.getPos().asLong();
      Set var4 = (Set)this.destructionProgress.get(var2);
      var4.remove(var1);
      if (var4.isEmpty()) {
         this.destructionProgress.remove(var2);
      }

   }

   private void addSkyPass(FrameGraphBuilder var1, Camera var2, GpuBufferSlice var3) {
      FogType var4 = var2.getFluidInCamera();
      if (var4 != FogType.POWDER_SNOW && var4 != FogType.LAVA && !this.doesMobEffectBlockSky(var2)) {
         SkyRenderState var5 = this.levelRenderState.skyRenderState;
         if (var5.skyType != DimensionSpecialEffects.SkyType.NONE) {
            SkyRenderer var6 = this.skyRenderer;
            if (var6 != null) {
               FramePass var7 = var1.addPass("sky");
               this.targets.main = var7.<RenderTarget>readsAndWrites(this.targets.main);
               var7.executes(() -> {
                  RenderSystem.setShaderFog(var3);
                  if (var5.skyType == DimensionSpecialEffects.SkyType.END) {
                     var6.renderEndSky();
                     if (var5.endFlashIntensity > 1.0E-5F) {
                        PoseStack var7 = new PoseStack();
                        var6.renderEndFlash(var7, var5.endFlashIntensity, var5.endFlashXAngle, var5.endFlashYAngle);
                     }

                  } else {
                     PoseStack var3x = new PoseStack();
                     float var4 = ARGB.redFloat(var5.skyColor);
                     float var5x = ARGB.greenFloat(var5.skyColor);
                     float var6x = ARGB.blueFloat(var5.skyColor);
                     var6.renderSkyDisc(var4, var5x, var6x);
                     if (var5.isSunriseOrSunset) {
                        var6.renderSunriseAndSunset(var3x, var5.sunAngle, var5.sunriseAndSunsetColor);
                     }

                     var6.renderSunMoonAndStars(var3x, var5.timeOfDay, var5.moonPhase, var5.rainBrightness, var5.starBrightness);
                     if (var5.shouldRenderDarkDisc) {
                        var6.renderDarkDisc();
                     }

                  }
               });
            }
         }
      }
   }

   private boolean doesMobEffectBlockSky(Camera var1) {
      Entity var3 = var1.entity();
      if (!(var3 instanceof LivingEntity var2)) {
         return false;
      } else {
         return var2.hasEffect(MobEffects.BLINDNESS) || var2.hasEffect(MobEffects.DARKNESS);
      }
   }

   private void compileSections(Camera var1) {
      ProfilerFiller var2 = Profiler.get();
      var2.push("populateSectionsToCompile");
      RenderRegionCache var3 = new RenderRegionCache();
      BlockPos var4 = var1.blockPosition();
      ArrayList var5 = Lists.newArrayList();
      ObjectListIterator var6 = this.visibleSections.iterator();

      while(var6.hasNext()) {
         SectionRenderDispatcher.RenderSection var7 = (SectionRenderDispatcher.RenderSection)var6.next();
         if (var7.isDirty() && (var7.getSectionMesh() != CompiledSectionMesh.UNCOMPILED || var7.hasAllNeighbors())) {
            boolean var8 = false;
            if (this.minecraft.options.prioritizeChunkUpdates().get() == PrioritizeChunkUpdates.NEARBY) {
               BlockPos var9 = SectionPos.of(var7.getSectionNode()).center();
               var8 = var9.distSqr(var4) < 768.0 || var7.isDirtyFromPlayer();
            } else if (this.minecraft.options.prioritizeChunkUpdates().get() == PrioritizeChunkUpdates.PLAYER_AFFECTED) {
               var8 = var7.isDirtyFromPlayer();
            }

            if (var8) {
               var2.push("compileSectionSynchronously");
               this.sectionRenderDispatcher.rebuildSectionSync(var7, var3);
               var7.setNotDirty();
               var2.pop();
            } else {
               var5.add(var7);
            }
         }
      }

      var2.popPush("uploadSectionMeshes");
      this.sectionRenderDispatcher.uploadAllPendingUploads();
      var2.popPush("scheduleAsyncCompile");

      for(SectionRenderDispatcher.RenderSection var11 : var5) {
         var11.rebuildSectionAsync(var3);
         var11.setNotDirty();
      }

      var2.popPush("scheduleTranslucentResort");
      this.scheduleTranslucentSectionResort(var1.position());
      var2.pop();
   }

   private void renderHitOutline(PoseStack var1, VertexConsumer var2, double var3, double var5, double var7, BlockOutlineRenderState var9, int var10, float var11) {
      BlockPos var12 = var9.pos();
      if (SharedConstants.DEBUG_SHAPES) {
         ShapeRenderer.renderShape(var1, var2, var9.shape(), (double)var12.getX() - var3, (double)var12.getY() - var5, (double)var12.getZ() - var7, ARGB.colorFromFloat(1.0F, 1.0F, 1.0F, 1.0F), var11);
         if (var9.collisionShape() != null) {
            ShapeRenderer.renderShape(var1, var2, var9.collisionShape(), (double)var12.getX() - var3, (double)var12.getY() - var5, (double)var12.getZ() - var7, ARGB.colorFromFloat(0.4F, 0.0F, 0.0F, 0.0F), var11);
         }

         if (var9.occlusionShape() != null) {
            ShapeRenderer.renderShape(var1, var2, var9.occlusionShape(), (double)var12.getX() - var3, (double)var12.getY() - var5, (double)var12.getZ() - var7, ARGB.colorFromFloat(0.4F, 0.0F, 1.0F, 0.0F), var11);
         }

         if (var9.interactionShape() != null) {
            ShapeRenderer.renderShape(var1, var2, var9.interactionShape(), (double)var12.getX() - var3, (double)var12.getY() - var5, (double)var12.getZ() - var7, ARGB.colorFromFloat(0.4F, 0.0F, 0.0F, 1.0F), var11);
         }
      } else {
         ShapeRenderer.renderShape(var1, var2, var9.shape(), (double)var12.getX() - var3, (double)var12.getY() - var5, (double)var12.getZ() - var7, var10, var11);
      }

   }

   public void blockChanged(BlockGetter var1, BlockPos var2, BlockState var3, BlockState var4, int var5) {
      this.setBlockDirty(var2, (var5 & 8) != 0);
   }

   private void setBlockDirty(BlockPos var1, boolean var2) {
      for(int var3 = var1.getZ() - 1; var3 <= var1.getZ() + 1; ++var3) {
         for(int var4 = var1.getX() - 1; var4 <= var1.getX() + 1; ++var4) {
            for(int var5 = var1.getY() - 1; var5 <= var1.getY() + 1; ++var5) {
               this.setSectionDirty(SectionPos.blockToSectionCoord(var4), SectionPos.blockToSectionCoord(var5), SectionPos.blockToSectionCoord(var3), var2);
            }
         }
      }

   }

   public void setBlocksDirty(int var1, int var2, int var3, int var4, int var5, int var6) {
      for(int var7 = var3 - 1; var7 <= var6 + 1; ++var7) {
         for(int var8 = var1 - 1; var8 <= var4 + 1; ++var8) {
            for(int var9 = var2 - 1; var9 <= var5 + 1; ++var9) {
               this.setSectionDirty(SectionPos.blockToSectionCoord(var8), SectionPos.blockToSectionCoord(var9), SectionPos.blockToSectionCoord(var7));
            }
         }
      }

   }

   public void setBlockDirty(BlockPos var1, BlockState var2, BlockState var3) {
      if (this.minecraft.getModelManager().requiresRender(var2, var3)) {
         this.setBlocksDirty(var1.getX(), var1.getY(), var1.getZ(), var1.getX(), var1.getY(), var1.getZ());
      }

   }

   public void setSectionDirtyWithNeighbors(int var1, int var2, int var3) {
      this.setSectionRangeDirty(var1 - 1, var2 - 1, var3 - 1, var1 + 1, var2 + 1, var3 + 1);
   }

   public void setSectionRangeDirty(int var1, int var2, int var3, int var4, int var5, int var6) {
      for(int var7 = var3; var7 <= var6; ++var7) {
         for(int var8 = var1; var8 <= var4; ++var8) {
            for(int var9 = var2; var9 <= var5; ++var9) {
               this.setSectionDirty(var8, var9, var7);
            }
         }
      }

   }

   public void setSectionDirty(int var1, int var2, int var3) {
      this.setSectionDirty(var1, var2, var3, false);
   }

   private void setSectionDirty(int var1, int var2, int var3, boolean var4) {
      this.viewArea.setDirty(var1, var2, var3, var4);
   }

   public void onSectionBecomingNonEmpty(long var1) {
      SectionRenderDispatcher.RenderSection var3 = this.viewArea.getRenderSection(var1);
      if (var3 != null) {
         this.sectionOcclusionGraph.schedulePropagationFrom(var3);
      }

   }

   public void destroyBlockProgress(int var1, BlockPos var2, int var3) {
      if (var3 >= 0 && var3 < 10) {
         BlockDestructionProgress var5 = (BlockDestructionProgress)this.destroyingBlocks.get(var1);
         if (var5 != null) {
            this.removeProgress(var5);
         }

         if (var5 == null || var5.getPos().getX() != var2.getX() || var5.getPos().getY() != var2.getY() || var5.getPos().getZ() != var2.getZ()) {
            var5 = new BlockDestructionProgress(var1, var2);
            this.destroyingBlocks.put(var1, var5);
         }

         var5.setProgress(var3);
         var5.updateTick(this.ticks);
         ((SortedSet)this.destructionProgress.computeIfAbsent(var5.getPos().asLong(), (var0) -> Sets.newTreeSet())).add(var5);
      } else {
         BlockDestructionProgress var4 = (BlockDestructionProgress)this.destroyingBlocks.remove(var1);
         if (var4 != null) {
            this.removeProgress(var4);
         }
      }

   }

   public boolean hasRenderedAllSections() {
      return this.sectionRenderDispatcher.isQueueEmpty();
   }

   public void onChunkReadyToRender(ChunkPos var1) {
      this.sectionOcclusionGraph.onChunkReadyToRender(var1);
   }

   public void needsUpdate() {
      this.sectionOcclusionGraph.invalidate();
      this.cloudRenderer.markForRebuild();
   }

   public static int getLightColor(BlockAndTintGetter var0, BlockPos var1) {
      return getLightColor(LevelRenderer.BrightnessGetter.DEFAULT, var0, var0.getBlockState(var1), var1);
   }

   public static int getLightColor(BrightnessGetter var0, BlockAndTintGetter var1, BlockState var2, BlockPos var3) {
      if (var2.emissiveRendering(var1, var3)) {
         return 15728880;
      } else {
         int var4 = var0.packedBrightness(var1, var3);
         int var5 = LightTexture.block(var4);
         int var6 = var2.getLightEmission();
         if (var5 < var6) {
            int var7 = LightTexture.sky(var4);
            return LightTexture.pack(var6, var7);
         } else {
            return var4;
         }
      }
   }

   public boolean isSectionCompiled(BlockPos var1) {
      SectionRenderDispatcher.RenderSection var2 = this.viewArea.getRenderSectionAt(var1);
      return var2 != null && var2.sectionMesh.get() != CompiledSectionMesh.UNCOMPILED;
   }

   @Nullable
   public RenderTarget entityOutlineTarget() {
      return this.targets.entityOutline != null ? (RenderTarget)this.targets.entityOutline.get() : null;
   }

   @Nullable
   public RenderTarget getTranslucentTarget() {
      return this.targets.translucent != null ? (RenderTarget)this.targets.translucent.get() : null;
   }

   @Nullable
   public RenderTarget getItemEntityTarget() {
      return this.targets.itemEntity != null ? (RenderTarget)this.targets.itemEntity.get() : null;
   }

   @Nullable
   public RenderTarget getParticlesTarget() {
      return this.targets.particles != null ? (RenderTarget)this.targets.particles.get() : null;
   }

   @Nullable
   public RenderTarget getWeatherTarget() {
      return this.targets.weather != null ? (RenderTarget)this.targets.weather.get() : null;
   }

   @Nullable
   public RenderTarget getCloudsTarget() {
      return this.targets.clouds != null ? (RenderTarget)this.targets.clouds.get() : null;
   }

   @VisibleForDebug
   public ObjectArrayList<SectionRenderDispatcher.RenderSection> getVisibleSections() {
      return this.visibleSections;
   }

   @VisibleForDebug
   public SectionOcclusionGraph getSectionOcclusionGraph() {
      return this.sectionOcclusionGraph;
   }

   @Nullable
   public Frustum getCapturedFrustum() {
      return this.capturedFrustum;
   }

   public CloudRenderer getCloudRenderer() {
      return this.cloudRenderer;
   }

   public Gizmos.TemporaryCollection collectPerFrameGizmos() {
      return Gizmos.withCollector(this.collectedGizmos);
   }

   @FunctionalInterface
   public interface BrightnessGetter {
      BrightnessGetter DEFAULT = (var0, var1) -> {
         int var2 = var0.getBrightness(LightLayer.SKY, var1);
         int var3 = var0.getBrightness(LightLayer.BLOCK, var1);
         return Brightness.pack(var3, var2);
      };

      int packedBrightness(BlockAndTintGetter var1, BlockPos var2);
   }
}
