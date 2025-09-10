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
import com.mojang.logging.LogUtils;
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
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.GraphicsStatus;
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
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
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
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.slf4j.Logger;

public class LevelRenderer implements ResourceManagerReloadListener, AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
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
   private final SkyRenderer skyRenderer = new SkyRenderer();
   private final CloudRenderer cloudRenderer = new CloudRenderer();
   private final WorldBorderRenderer worldBorderRenderer = new WorldBorderRenderer();
   private final WeatherEffectRenderer weatherEffectRenderer = new WeatherEffectRenderer();
   private final ParticlesRenderState particlesRenderState = new ParticlesRenderState();
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

   public void tickParticles(Camera var1) {
      this.weatherEffectRenderer.tickRainParticles(this.minecraft.level, var1, this.ticks, (ParticleStatus)this.minecraft.options.particles().get());
   }

   public void close() {
      if (this.entityOutlineTarget != null) {
         this.entityOutlineTarget.destroyBuffers();
      }

      this.skyRenderer.close();
      this.cloudRenderer.close();
   }

   public void onResourceManagerReload(ResourceManager var1) {
      this.initOutline();
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
            this.minecraft.options.graphicsMode().set(GraphicsStatus.FANCY);
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
         ItemBlockRenderTypes.setFancy(Minecraft.useFancyGraphics());
         this.lastViewDistance = this.minecraft.options.getEffectiveRenderDistance();
         if (this.viewArea != null) {
            this.viewArea.releaseAllBuffers();
         }

         this.sectionRenderDispatcher.clearCompileQueue();
         this.viewArea = new ViewArea(this.sectionRenderDispatcher, this.level, this.minecraft.options.getEffectiveRenderDistance(), this);
         this.sectionOcclusionGraph.waitAndReset(this.viewArea);
         this.clearVisibleSections();
         Camera var1 = this.minecraft.gameRenderer.getMainCamera();
         this.viewArea.repositionCamera(SectionPos.of((Position)var1.getPosition()));
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
      Vec3 var4 = var1.getPosition();
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
         if (var3 && this.level.getBlockState(var1.getBlockPosition()).isSolidRender()) {
            var15 = false;
         }

         var5.push("updateSOG");
         this.sectionOcclusionGraph.update(var15, var1, var2, this.visibleSections, this.level.getChunkSource().getLoadedEmptySections());
         var5.pop();
         double var16 = Math.floor((double)(var1.getXRot() / 2.0F));
         double var18 = Math.floor((double)(var1.getYRot() / 2.0F));
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
      this.blockEntityRenderDispatcher.prepare(this.level, var4, this.minecraft.hitResult);
      this.entityRenderDispatcher.prepare(var4, this.minecraft.crosshairPickEntity);
      final ProfilerFiller var12 = Profiler.get();
      var12.push("populateLightUpdates");
      this.level.pollLightUpdates();
      var12.popPush("runLightUpdates");
      this.level.getChunkSource().getLightEngine().runLightUpdates();
      var12.popPush("prepareCullFrustum");
      Vec3 var13 = var4.getPosition();
      Frustum var14 = this.prepareCullFrustum(var5, var7, var13);
      var12.popPush("cullTerrain");
      this.cullTerrain(var4, var14, this.minecraft.player.isSpectator());
      var12.popPush("compileSections");
      this.compileSections(var4);
      var12.popPush("extractEntities");
      this.extractVisibleEntities(var4, var14, var2, this.levelRenderState);
      var12.popPush("extractBlockEntities");
      this.extractVisibleBlockEntities(var4, var11, this.levelRenderState);
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
         this.addSkyPass(var16, var4, var11, var8);
      }

      this.addMainPass(var16, var14, var4, var5, var8, var3, this.levelRenderState, var2, var12);
      PostChain var22 = this.minecraft.getShaderManager().getPostChain(ENTITY_OUTLINE_POST_CHAIN_ID, LevelTargetBundle.OUTLINE_TARGETS);
      if (this.levelRenderState.haveGlowingEntities && var22 != null) {
         var22.addToFrame(var16, var17, var18, this.targets);
      }

      this.minecraft.particleEngine.extract(this.particlesRenderState, (new Frustum(var14)).offset(-3.0F), var4, var11);
      this.addParticlesPass(var16, var8);
      CloudStatus var23 = this.minecraft.options.getCloudsType();
      if (var23 != CloudStatus.OFF) {
         Optional var24 = this.level.dimensionType().cloudHeight();
         if (var24.isPresent()) {
            float var25 = (float)this.ticks + var11;
            int var26 = this.level.getCloudColor(var11);
            this.addCloudsPass(var16, var23, var4.getPosition(), var25, var26, (float)(Integer)var24.get() + 0.33F);
         }
      }

      this.addWeatherPass(var16, var4.getPosition(), var11, var8);
      if (var20 != null) {
         var20.addToFrame(var16, var17, var18, this.targets);
      }

      this.addLateDebugPass(var16, var13, var8);
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

   private void addMainPass(FrameGraphBuilder var1, Frustum var2, Camera var3, Matrix4f var4, GpuBufferSlice var5, boolean var6, LevelRenderState var7, DeltaTracker var8, ProfilerFiller var9) {
      FramePass var10 = var1.addPass("main");
      this.targets.main = var10.<RenderTarget>readsAndWrites(this.targets.main);
      if (this.targets.translucent != null) {
         this.targets.translucent = var10.<RenderTarget>readsAndWrites(this.targets.translucent);
      }

      if (this.targets.itemEntity != null) {
         this.targets.itemEntity = var10.<RenderTarget>readsAndWrites(this.targets.itemEntity);
      }

      if (this.targets.weather != null) {
         this.targets.weather = var10.<RenderTarget>readsAndWrites(this.targets.weather);
      }

      if (var7.haveGlowingEntities && this.targets.entityOutline != null) {
         this.targets.entityOutline = var10.<RenderTarget>readsAndWrites(this.targets.entityOutline);
      }

      ResourceHandle var11 = this.targets.main;
      ResourceHandle var12 = this.targets.translucent;
      ResourceHandle var13 = this.targets.itemEntity;
      ResourceHandle var14 = this.targets.entityOutline;
      var10.executes(() -> {
         RenderSystem.setShaderFog(var5);
         float var13x = var8.getGameTimeDeltaPartialTick(false);
         Vec3 var14x = var3.getPosition();
         double var15 = var14x.x();
         double var17 = var14x.y();
         double var19 = var14x.z();
         var9.push("terrain");
         ChunkSectionsToRender var21 = this.prepareChunkRenders(var4, var15, var17, var19);
         var21.renderGroup(ChunkSectionLayerGroup.OPAQUE);
         this.minecraft.gameRenderer.getLighting().setupFor(Lighting.Entry.LEVEL);
         if (var13 != null) {
            ((RenderTarget)var13.get()).copyDepthFrom(this.minecraft.getMainRenderTarget());
         }

         if (this.shouldShowEntityOutlines() && var14 != null) {
            RenderTarget var22 = (RenderTarget)var14.get();
            RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(var22.getColorTexture(), 0, var22.getDepthTexture(), 1.0);
         }

         PoseStack var25 = new PoseStack();
         MultiBufferSource.BufferSource var23 = this.renderBuffers.bufferSource();
         MultiBufferSource.BufferSource var24 = this.renderBuffers.crumblingBufferSource();
         var9.popPush("submitEntities");
         this.submitEntities(var25, var3, var7, this.submitNodeStorage);
         var9.popPush("submitBlockEntities");
         this.submitBlockEntities(var25, var3, var7, this.submitNodeStorage);
         var9.popPush("renderFeatures");
         this.featureRenderDispatcher.renderAllFeatures();
         var23.endLastBatch();
         this.checkPoseStack(var25);
         var23.endBatch(RenderType.solid());
         var23.endBatch(RenderType.endPortal());
         var23.endBatch(RenderType.endGateway());
         var23.endBatch(Sheets.solidBlockSheet());
         var23.endBatch(Sheets.cutoutBlockSheet());
         var23.endBatch(Sheets.bedSheet());
         var23.endBatch(Sheets.shulkerBoxSheet());
         var23.endBatch(Sheets.signSheet());
         var23.endBatch(Sheets.hangingSignSheet());
         var23.endBatch(Sheets.chestSheet());
         this.renderBuffers.outlineBufferSource().endOutlineBatch();
         if (var6) {
            this.renderBlockOutline(var3, var23, var25, false);
         }

         var9.popPush("debug");
         this.minecraft.debugRenderer.render(var25, var2, var23, var15, var17, var19);
         var23.endLastBatch();
         var9.pop();
         this.checkPoseStack(var25);
         var23.endBatch(Sheets.translucentItemSheet());
         var23.endBatch(Sheets.bannerSheet());
         var23.endBatch(Sheets.shieldSheet());
         var23.endBatch(RenderType.armorEntityGlint());
         var23.endBatch(RenderType.glint());
         var23.endBatch(RenderType.glintTranslucent());
         var23.endBatch(RenderType.entityGlint());
         var9.push("destroyProgress");
         this.renderBlockDestroyAnimation(var25, var3, var24);
         var24.endBatch();
         var9.pop();
         this.checkPoseStack(var25);
         var23.endBatch(RenderType.waterMask());
         var23.endBatch();
         if (var12 != null) {
            ((RenderTarget)var12.get()).copyDepthFrom((RenderTarget)var11.get());
         }

         var9.push("translucent");
         var21.renderGroup(ChunkSectionLayerGroup.TRANSLUCENT);
         var9.popPush("string");
         var21.renderGroup(ChunkSectionLayerGroup.TRIPWIRE);
         if (var6) {
            this.renderBlockOutline(var3, var23, var25, true);
         }

         var23.endBatch();
         var9.pop();
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

         this.particlesRenderState.submit(this.submitNodeStorage);
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

   private void addWeatherPass(FrameGraphBuilder var1, Vec3 var2, float var3, GpuBufferSlice var4) {
      int var5 = this.minecraft.options.getEffectiveRenderDistance() * 16;
      float var6 = this.minecraft.gameRenderer.getDepthFar();
      FramePass var7 = var1.addPass("weather");
      if (this.targets.weather != null) {
         this.targets.weather = var7.<RenderTarget>readsAndWrites(this.targets.weather);
      } else {
         this.targets.main = var7.<RenderTarget>readsAndWrites(this.targets.main);
      }

      var7.executes(() -> {
         RenderSystem.setShaderFog(var4);
         MultiBufferSource.BufferSource var6x = this.renderBuffers.bufferSource();
         this.weatherEffectRenderer.render(this.minecraft.level, var6x, this.ticks, var3, var2);
         this.worldBorderRenderer.render(this.level.getWorldBorder(), var2, (double)var5, (double)var6);
         var6x.endBatch();
      });
   }

   private void addLateDebugPass(FrameGraphBuilder var1, Vec3 var2, GpuBufferSlice var3) {
      FramePass var4 = var1.addPass("late_debug");
      this.targets.main = var4.<RenderTarget>readsAndWrites(this.targets.main);
      if (this.targets.itemEntity != null) {
         this.targets.itemEntity = var4.<RenderTarget>readsAndWrites(this.targets.itemEntity);
      }

      ResourceHandle var5 = this.targets.main;
      var4.executes(() -> {
         RenderSystem.setShaderFog(var3);
         PoseStack var4 = new PoseStack();
         MultiBufferSource.BufferSource var5x = this.renderBuffers.bufferSource();
         RenderSystem.outputColorTextureOverride = ((RenderTarget)var5.get()).getColorTextureView();
         RenderSystem.outputDepthTextureOverride = ((RenderTarget)var5.get()).getDepthTextureView();
         this.minecraft.debugRenderer.renderAfterTranslucents(var4, var5x, var2.x, var2.y, var2.z);
         var5x.endLastBatch();
         RenderSystem.outputColorTextureOverride = null;
         RenderSystem.outputDepthTextureOverride = null;
         this.checkPoseStack(var4);
      });
   }

   private void extractVisibleEntities(Camera var1, Frustum var2, DeltaTracker var3, LevelRenderState var4) {
      Vec3 var5 = var1.getPosition();
      double var6 = var5.x();
      double var8 = var5.y();
      double var10 = var5.z();
      TickRateManager var12 = this.minecraft.level.tickRateManager();
      boolean var13 = this.shouldShowEntityOutlines();
      Entity.setViewScale(Mth.clamp((double)this.minecraft.options.getEffectiveRenderDistance() / 8.0, 1.0, 2.5) * (Double)this.minecraft.options.entityDistanceScaling().get());

      for(Entity var15 : this.level.entitiesForRendering()) {
         if (this.entityRenderDispatcher.shouldRender(var15, var2, var6, var8, var10) || var15.hasIndirectPassenger(this.minecraft.player)) {
            BlockPos var16 = var15.blockPosition();
            if ((this.level.isOutsideBuildHeight(var16.getY()) || this.isSectionCompiled(var16)) && (var15 != var1.getEntity() || var1.isDetached() || var1.getEntity() instanceof LivingEntity && ((LivingEntity)var1.getEntity()).isSleeping()) && (!(var15 instanceof LocalPlayer) || var1.getEntity() == var15)) {
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

   private void submitEntities(PoseStack var1, Camera var2, LevelRenderState var3, SubmitNodeCollector var4) {
      Vec3 var5 = var2.getPosition();
      double var6 = var5.x();
      double var8 = var5.y();
      double var10 = var5.z();

      for(EntityRenderState var13 : var3.entityRenderStates) {
         if (!var3.haveGlowingEntities) {
            var13.outlineColor = 0;
         }

         this.entityRenderDispatcher.submit(var13, var13.x - var6, var13.y - var8, var13.z - var10, var1, var4);
      }

   }

   private void extractVisibleBlockEntities(Camera var1, float var2, LevelRenderState var3) {
      Vec3 var4 = var1.getPosition();
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

   private void submitBlockEntities(PoseStack var1, Camera var2, LevelRenderState var3, SubmitNodeStorage var4) {
      Vec3 var5 = var2.getPosition();
      double var6 = var5.x();
      double var8 = var5.y();
      double var10 = var5.z();

      for(BlockEntityRenderState var13 : var3.blockEntityRenderStates) {
         BlockPos var14 = var13.blockPos;
         var1.pushPose();
         var1.translate((double)var14.getX() - var6, (double)var14.getY() - var8, (double)var14.getZ() - var10);
         this.blockEntityRenderDispatcher.submit(var13, var1, var4);
         var1.popPose();
      }

   }

   private void renderBlockDestroyAnimation(PoseStack var1, Camera var2, MultiBufferSource.BufferSource var3) {
      Vec3 var4 = var2.getPosition();
      double var5 = var4.x();
      double var7 = var4.y();
      double var9 = var4.z();
      ObjectIterator var11 = this.destructionProgress.long2ObjectEntrySet().iterator();

      while(var11.hasNext()) {
         Long2ObjectMap.Entry var12 = (Long2ObjectMap.Entry)var11.next();
         BlockPos var13 = BlockPos.of(var12.getLongKey());
         if (!(var13.distToCenterSqr(var5, var7, var9) > 1024.0)) {
            SortedSet var14 = (SortedSet)var12.getValue();
            if (var14 != null && !var14.isEmpty()) {
               int var15 = ((BlockDestructionProgress)var14.last()).getProgress();
               var1.pushPose();
               var1.translate((double)var13.getX() - var5, (double)var13.getY() - var7, (double)var13.getZ() - var9);
               PoseStack.Pose var16 = var1.last();
               SheetedDecalTextureGenerator var17 = new SheetedDecalTextureGenerator(var3.getBuffer((RenderType)ModelBakery.DESTROY_TYPES.get(var15)), var16, 1.0F);
               this.minecraft.getBlockRenderer().renderBreakingTexture(this.level.getBlockState(var13), var13, this.level, var1, var17);
               var1.popPose();
            }
         }
      }

   }

   private void renderBlockOutline(Camera var1, MultiBufferSource.BufferSource var2, PoseStack var3, boolean var4) {
      HitResult var6 = this.minecraft.hitResult;
      if (var6 instanceof BlockHitResult var5) {
         if (var5.getType() != HitResult.Type.MISS) {
            BlockPos var13 = var5.getBlockPos();
            BlockState var7 = this.level.getBlockState(var13);
            if (!var7.isAir() && this.level.getWorldBorder().isWithinBounds(var13)) {
               boolean var8 = ItemBlockRenderTypes.getChunkRenderType(var7).sortOnUpload();
               if (var8 != var4) {
                  return;
               }

               Vec3 var9 = var1.getPosition();
               Boolean var10 = (Boolean)this.minecraft.options.highContrastBlockOutline().get();
               if (var10) {
                  VertexConsumer var11 = var2.getBuffer(RenderType.secondaryBlockOutline());
                  this.renderHitOutline(var3, var11, var1.getEntity(), var9.x, var9.y, var9.z, var13, var7, -16777216);
               }

               VertexConsumer var14 = var2.getBuffer(RenderType.lines());
               int var12 = var10 ? -11010079 : ARGB.color(102, -16777216);
               this.renderHitOutline(var3, var14, var1.getEntity(), var9.x, var9.y, var9.z, var13, var7, var12);
               var2.endLastBatch();
            }

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
               var25.add(new DynamicUniforms.Transform(var1, var26, new Vector3f((float)((double)var23.getX() - var2), (float)((double)var23.getY() - var4), (float)((double)var23.getZ() - var6)), var27, 1.0F));
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

   public void tick() {
      if (this.level.tickRateManager().runsNormally()) {
         ++this.ticks;
      }

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

   private void addSkyPass(FrameGraphBuilder var1, Camera var2, float var3, GpuBufferSlice var4) {
      FogType var5 = var2.getFluidInCamera();
      if (var5 != FogType.POWDER_SNOW && var5 != FogType.LAVA && !this.doesMobEffectBlockSky(var2)) {
         DimensionSpecialEffects var6 = this.level.effects();
         DimensionSpecialEffects.SkyType var7 = var6.skyType();
         if (var7 != DimensionSpecialEffects.SkyType.NONE) {
            FramePass var8 = var1.addPass("sky");
            this.targets.main = var8.<RenderTarget>readsAndWrites(this.targets.main);
            var8.executes(() -> {
               RenderSystem.setShaderFog(var4);
               if (var7 == DimensionSpecialEffects.SkyType.END) {
                  this.skyRenderer.renderEndSky();
                  EndFlashState var16 = this.level.endFlashState();
                  if (var16 != null) {
                     float var17 = var16.getIntensity(var3);
                     if (var17 > 1.0E-5F) {
                        PoseStack var18 = new PoseStack();
                        this.skyRenderer.renderEndFlash(var18, var17, var16.getXAngle(), var16.getYAngle());
                     }

                  }
               } else {
                  PoseStack var5 = new PoseStack();
                  float var6x = this.level.getSunAngle(var3);
                  float var7x = this.level.getTimeOfDay(var3);
                  float var8 = 1.0F - this.level.getRainLevel(var3);
                  float var9 = this.level.getStarBrightness(var3) * var8;
                  int var10 = var6.getSunriseOrSunsetColor(var7x);
                  int var11 = this.level.getMoonPhase();
                  int var12 = this.level.getSkyColor(this.minecraft.gameRenderer.getMainCamera().getPosition(), var3);
                  float var13 = ARGB.redFloat(var12);
                  float var14 = ARGB.greenFloat(var12);
                  float var15 = ARGB.blueFloat(var12);
                  this.skyRenderer.renderSkyDisc(var13, var14, var15);
                  if (var6.isSunriseOrSunset(var7x)) {
                     this.skyRenderer.renderSunriseAndSunset(var5, var6x, var10);
                  }

                  this.skyRenderer.renderSunMoonAndStars(var5, var7x, var11, var8, var9);
                  if (this.shouldRenderDarkDisc(var3)) {
                     this.skyRenderer.renderDarkDisc();
                  }

               }
            });
         }
      }
   }

   private boolean shouldRenderDarkDisc(float var1) {
      return this.minecraft.player.getEyePosition(var1).y - this.level.getLevelData().getHorizonHeight(this.level) < 0.0;
   }

   private boolean doesMobEffectBlockSky(Camera var1) {
      Entity var3 = var1.getEntity();
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
      BlockPos var4 = var1.getBlockPosition();
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
      this.scheduleTranslucentSectionResort(var1.getPosition());
      var2.pop();
   }

   private void renderHitOutline(PoseStack var1, VertexConsumer var2, Entity var3, double var4, double var6, double var8, BlockPos var10, BlockState var11, int var12) {
      if (SharedConstants.DEBUG_SHAPES) {
         ShapeRenderer.renderShape(var1, var2, var11.getShape(this.level, var10), (double)var10.getX() - var4, (double)var10.getY() - var6, (double)var10.getZ() - var8, ARGB.colorFromFloat(1.0F, 1.0F, 1.0F, 1.0F));
         ShapeRenderer.renderShape(var1, var2, var11.getCollisionShape(this.level, var10), (double)var10.getX() - var4, (double)var10.getY() - var6, (double)var10.getZ() - var8, ARGB.colorFromFloat(0.4F, 0.0F, 0.0F, 0.0F));
         ShapeRenderer.renderShape(var1, var2, var11.getOcclusionShape(), (double)var10.getX() - var4, (double)var10.getY() - var6, (double)var10.getZ() - var8, ARGB.colorFromFloat(0.4F, 0.0F, 1.0F, 0.0F));
         ShapeRenderer.renderShape(var1, var2, var11.getInteractionShape(this.level, var10), (double)var10.getX() - var4, (double)var10.getY() - var6, (double)var10.getZ() - var8, ARGB.colorFromFloat(0.4F, 0.0F, 0.0F, 1.0F));
      } else {
         ShapeRenderer.renderShape(var1, var2, var11.getShape(this.level, var10, CollisionContext.of(var3)), (double)var10.getX() - var4, (double)var10.getY() - var6, (double)var10.getZ() - var8, var12);
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

   public void addParticle(ParticleOptions var1, boolean var2, double var3, double var5, double var7, double var9, double var11, double var13) {
      this.addParticle(var1, var2, false, var3, var5, var7, var9, var11, var13);
   }

   public void addParticle(ParticleOptions var1, boolean var2, boolean var3, double var4, double var6, double var8, double var10, double var12, double var14) {
      try {
         Camera var16 = this.minecraft.gameRenderer.getMainCamera();
         ParticleStatus var20 = this.calculateParticleLevel(var3);
         if (var2) {
            this.minecraft.particleEngine.createParticle(var1, var4, var6, var8, var10, var12, var14);
         } else if (!(var16.getPosition().distanceToSqr(var4, var6, var8) > 1024.0)) {
            if (var20 != ParticleStatus.MINIMAL) {
               this.minecraft.particleEngine.createParticle(var1, var4, var6, var8, var10, var12, var14);
            }
         }
      } catch (Throwable var19) {
         CrashReport var17 = CrashReport.forThrowable(var19, "Exception while adding particle");
         CrashReportCategory var18 = var17.addCategory("Particle being added");
         var18.setDetail("ID", BuiltInRegistries.PARTICLE_TYPE.getKey(var1.getType()));
         var18.setDetail("Parameters", (CrashReportDetail)(() -> ParticleTypes.CODEC.encodeStart(this.level.registryAccess().createSerializationContext(NbtOps.INSTANCE), var1).toString()));
         var18.setDetail("Position", (CrashReportDetail)(() -> CrashReportCategory.formatLocation(this.level, var4, var6, var8)));
         throw new ReportedException(var17);
      }
   }

   public <T extends ParticleOptions> void addParticle(T var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      this.addParticle(var1, var1.getType().getOverrideLimiter(), var2, var4, var6, var8, var10, var12);
   }

   private ParticleStatus calculateParticleLevel(boolean var1) {
      ParticleStatus var2 = (ParticleStatus)this.minecraft.options.particles().get();
      if (var1 && var2 == ParticleStatus.MINIMAL && this.level.random.nextInt(10) == 0) {
         var2 = ParticleStatus.DECREASED;
      }

      if (var2 == ParticleStatus.DECREASED && this.level.random.nextInt(3) == 0) {
         var2 = ParticleStatus.MINIMAL;
      }

      return var2;
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
