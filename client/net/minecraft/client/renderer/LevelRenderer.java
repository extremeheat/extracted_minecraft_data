package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.resource.RenderTargetDescriptor;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.PrioritizeChunkUpdates;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderRegionCache;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
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
import net.minecraft.util.Mth;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.Zone;
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
   @Nullable
   private ClientLevel level;
   private final SectionOcclusionGraph sectionOcclusionGraph = new SectionOcclusionGraph();
   private final ObjectArrayList<SectionRenderDispatcher.RenderSection> visibleSections = new ObjectArrayList(10000);
   private final ObjectArrayList<SectionRenderDispatcher.RenderSection> nearbyVisibleSections = new ObjectArrayList(50);
   private final Set<BlockEntity> globalBlockEntities = Sets.newHashSet();
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
   private final List<Entity> visibleEntities = new ArrayList();
   private int visibleEntityCount;
   private Frustum cullingFrustum;
   private boolean captureFrustum;
   @Nullable
   private Frustum capturedFrustum;
   @Nullable
   private BlockPos lastTranslucentSortBlockPos;
   private int translucencyResortIterationIndex;

   public LevelRenderer(Minecraft var1, EntityRenderDispatcher var2, BlockEntityRenderDispatcher var3, RenderBuffers var4) {
      super();
      this.minecraft = var1;
      this.entityRenderDispatcher = var2;
      this.blockEntityRenderDispatcher = var3;
      this.renderBuffers = var4;
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

      this.entityOutlineTarget = new TextureTarget(this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight(), true);
      this.entityOutlineTarget.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
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
         RenderSystem.enableBlend();
         RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
         this.entityOutlineTarget.blitAndBlendToScreen(this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight());
         RenderSystem.disableBlend();
         RenderSystem.defaultBlendFunc();
      }

   }

   protected boolean shouldShowEntityOutlines() {
      return !this.minecraft.gameRenderer.isPanoramicMode() && this.entityOutlineTarget != null && this.minecraft.player != null;
   }

   public void setLevel(@Nullable ClientLevel var1) {
      this.lastCameraSectionX = -2147483648;
      this.lastCameraSectionY = -2147483648;
      this.lastCameraSectionZ = -2147483648;
      this.entityRenderDispatcher.setLevel(var1);
      this.level = var1;
      if (var1 != null) {
         this.allChanged();
      } else {
         if (this.viewArea != null) {
            this.viewArea.releaseAllBuffers();
            this.viewArea = null;
         }

         if (this.sectionRenderDispatcher != null) {
            this.sectionRenderDispatcher.dispose();
         }

         this.sectionRenderDispatcher = null;
         this.globalBlockEntities.clear();
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

         this.sectionRenderDispatcher.blockUntilClear();
         synchronized(this.globalBlockEntities) {
            this.globalBlockEntities.clear();
         }

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

   public String getSectionStatistics() {
      int var1 = this.viewArea.sections.length;
      int var2 = this.countRenderedSections();
      return String.format(Locale.ROOT, "C: %d/%d %sD: %d, %s", var2, var1, this.minecraft.smartCull ? "(s) " : "", this.lastViewDistance, this.sectionRenderDispatcher == null ? "null" : this.sectionRenderDispatcher.getStats());
   }

   public SectionRenderDispatcher getSectionRenderDispatcher() {
      return this.sectionRenderDispatcher;
   }

   public double getTotalSections() {
      return (double)this.viewArea.sections.length;
   }

   public double getLastViewDistance() {
      return (double)this.lastViewDistance;
   }

   public int countRenderedSections() {
      int var1 = 0;
      ObjectListIterator var2 = this.visibleSections.iterator();

      while(var2.hasNext()) {
         SectionRenderDispatcher.RenderSection var3 = (SectionRenderDispatcher.RenderSection)var2.next();
         if (var3.getCompiled().hasRenderableLayers()) {
            ++var1;
         }
      }

      return var1;
   }

   public String getEntityStatistics() {
      int var10000 = this.visibleEntityCount;
      return "E: " + var10000 + "/" + this.level.getEntityCount() + ", SD: " + this.level.getServerSimulationDistance();
   }

   private void setupRender(Camera var1, Frustum var2, boolean var3, boolean var4) {
      Vec3 var5 = var1.getPosition();
      if (this.minecraft.options.getEffectiveRenderDistance() != this.lastViewDistance) {
         this.allChanged();
      }

      ProfilerFiller var6 = Profiler.get();
      var6.push("camera");
      int var7 = SectionPos.posToSectionCoord(var5.x());
      int var8 = SectionPos.posToSectionCoord(var5.y());
      int var9 = SectionPos.posToSectionCoord(var5.z());
      if (this.lastCameraSectionX != var7 || this.lastCameraSectionY != var8 || this.lastCameraSectionZ != var9) {
         this.lastCameraSectionX = var7;
         this.lastCameraSectionY = var8;
         this.lastCameraSectionZ = var9;
         this.viewArea.repositionCamera(SectionPos.of((Position)var5));
      }

      this.sectionRenderDispatcher.setCamera(var5);
      var6.popPush("cull");
      double var10 = Math.floor(var5.x / 8.0);
      double var12 = Math.floor(var5.y / 8.0);
      double var14 = Math.floor(var5.z / 8.0);
      if (var10 != this.prevCamX || var12 != this.prevCamY || var14 != this.prevCamZ) {
         this.sectionOcclusionGraph.invalidate();
      }

      this.prevCamX = var10;
      this.prevCamY = var12;
      this.prevCamZ = var14;
      var6.popPush("update");
      if (!var3) {
         boolean var16 = this.minecraft.smartCull;
         if (var4 && this.level.getBlockState(var1.getBlockPosition()).isSolidRender()) {
            var16 = false;
         }

         var6.push("section_occlusion_graph");
         this.sectionOcclusionGraph.update(var16, var1, var2, this.visibleSections, this.level.getChunkSource().getLoadedEmptySections());
         var6.pop();
         double var17 = Math.floor((double)(var1.getXRot() / 2.0F));
         double var19 = Math.floor((double)(var1.getYRot() / 2.0F));
         if (this.sectionOcclusionGraph.consumeFrustumUpdate() || var17 != this.prevCamRotX || var19 != this.prevCamRotY) {
            this.applyFrustum(offsetFrustum(var2));
            this.prevCamRotX = var17;
            this.prevCamRotY = var19;
         }
      }

      var6.pop();
   }

   public static Frustum offsetFrustum(Frustum var0) {
      return (new Frustum(var0)).offsetToFullyIncludeCameraCube(8);
   }

   private void applyFrustum(Frustum var1) {
      if (!Minecraft.getInstance().isSameThread()) {
         throw new IllegalStateException("applyFrustum called from wrong thread: " + Thread.currentThread().getName());
      } else {
         Profiler.get().push("apply_frustum");
         this.clearVisibleSections();
         this.sectionOcclusionGraph.addSectionsInFrustum(var1, this.visibleSections, this.nearbyVisibleSections);
         Profiler.get().pop();
      }
   }

   public void addRecentlyCompiledSection(SectionRenderDispatcher.RenderSection var1) {
      this.sectionOcclusionGraph.schedulePropagationFrom(var1);
   }

   public void prepareCullFrustum(Vec3 var1, Matrix4f var2, Matrix4f var3) {
      this.cullingFrustum = new Frustum(var2, var3);
      this.cullingFrustum.prepare(var1.x(), var1.y(), var1.z());
   }

   public void renderLevel(GraphicsResourceAllocator var1, DeltaTracker var2, boolean var3, Camera var4, GameRenderer var5, Matrix4f var6, Matrix4f var7) {
      float var8 = var2.getGameTimeDeltaPartialTick(false);
      RenderSystem.setShaderGameTime(this.level.getGameTime(), var8);
      this.blockEntityRenderDispatcher.prepare(this.level, var4, this.minecraft.hitResult);
      this.entityRenderDispatcher.prepare(this.level, var4, this.minecraft.crosshairPickEntity);
      final ProfilerFiller var9 = Profiler.get();
      var9.popPush("light_update_queue");
      this.level.pollLightUpdates();
      var9.popPush("light_updates");
      this.level.getChunkSource().getLightEngine().runLightUpdates();
      Vec3 var10 = var4.getPosition();
      double var11 = var10.x();
      double var13 = var10.y();
      double var15 = var10.z();
      var9.popPush("culling");
      boolean var17 = this.capturedFrustum != null;
      Frustum var18 = var17 ? this.capturedFrustum : this.cullingFrustum;
      Profiler.get().popPush("captureFrustum");
      if (this.captureFrustum) {
         this.capturedFrustum = var17 ? new Frustum(var6, var7) : var18;
         this.capturedFrustum.prepare(var11, var13, var15);
         this.captureFrustum = false;
      }

      var9.popPush("fog");
      float var19 = var5.getRenderDistance();
      boolean var20 = this.minecraft.level.effects().isFoggyAt(Mth.floor(var11), Mth.floor(var13)) || this.minecraft.gui.getBossOverlay().shouldCreateWorldFog();
      Vector4f var21 = FogRenderer.computeFogColor(var4, var8, this.minecraft.level, this.minecraft.options.getEffectiveRenderDistance(), var5.getDarkenWorldAmount(var8));
      FogParameters var22 = FogRenderer.setupFog(var4, FogRenderer.FogMode.FOG_TERRAIN, var21, var19, var20, var8);
      FogParameters var23 = FogRenderer.setupFog(var4, FogRenderer.FogMode.FOG_SKY, var21, var19, var20, var8);
      var9.popPush("cullEntities");
      boolean var24 = this.collectVisibleEntities(var4, var18, this.visibleEntities);
      this.visibleEntityCount = this.visibleEntities.size();
      var9.popPush("terrain_setup");
      this.setupRender(var4, var18, var17, this.minecraft.player.isSpectator());
      var9.popPush("compile_sections");
      this.compileSections(var4);
      Matrix4fStack var25 = RenderSystem.getModelViewStack();
      var25.pushMatrix();
      var25.mul(var6);
      FrameGraphBuilder var26 = new FrameGraphBuilder();
      this.targets.main = var26.<RenderTarget>importExternal("main", this.minecraft.getMainRenderTarget());
      int var27 = this.minecraft.getMainRenderTarget().width;
      int var28 = this.minecraft.getMainRenderTarget().height;
      RenderTargetDescriptor var29 = new RenderTargetDescriptor(var27, var28, true);
      PostChain var30 = this.getTransparencyChain();
      if (var30 != null) {
         this.targets.translucent = var26.<RenderTarget>createInternal("translucent", var29);
         this.targets.itemEntity = var26.<RenderTarget>createInternal("item_entity", var29);
         this.targets.particles = var26.<RenderTarget>createInternal("particles", var29);
         this.targets.weather = var26.<RenderTarget>createInternal("weather", var29);
         this.targets.clouds = var26.<RenderTarget>createInternal("clouds", var29);
      }

      if (this.entityOutlineTarget != null) {
         this.targets.entityOutline = var26.<RenderTarget>importExternal("entity_outline", this.entityOutlineTarget);
      }

      FramePass var31 = var26.addPass("clear");
      this.targets.main = var31.<RenderTarget>readsAndWrites(this.targets.main);
      var31.executes(() -> {
         RenderSystem.clearColor(var21.x, var21.y, var21.z, 0.0F);
         RenderSystem.clear(16640);
      });
      if (!var20) {
         this.addSkyPass(var26, var4, var8, var23);
      }

      this.addMainPass(var26, var18, var4, var6, var7, var22, var3, var24, var2, var9);
      PostChain var32 = this.minecraft.getShaderManager().getPostChain(ENTITY_OUTLINE_POST_CHAIN_ID, LevelTargetBundle.OUTLINE_TARGETS);
      if (var24 && var32 != null) {
         var32.addToFrame(var26, var27, var28, this.targets);
      }

      this.addParticlesPass(var26, var4, var8, var22);
      CloudStatus var33 = this.minecraft.options.getCloudsType();
      if (var33 != CloudStatus.OFF) {
         float var34 = this.level.effects().getCloudHeight();
         if (!Float.isNaN(var34)) {
            float var35 = (float)this.ticks + var8;
            int var36 = this.level.getCloudColor(var8);
            this.addCloudsPass(var26, var6, var7, var33, var4.getPosition(), var35, var36, var34 + 0.33F);
         }
      }

      this.addWeatherPass(var26, var4.getPosition(), var8, var22);
      if (var30 != null) {
         var30.addToFrame(var26, var27, var28, this.targets);
      }

      this.addLateDebugPass(var26, var10, var22);
      var9.popPush("framegraph");
      var26.execute(var1, new FrameGraphBuilder.Inspector() {
         public void beforeExecutePass(String var1) {
            var9.push(var1);
         }

         public void afterExecutePass(String var1) {
            var9.pop();
         }
      });
      this.minecraft.getMainRenderTarget().bindWrite(false);
      this.visibleEntities.clear();
      this.targets.clear();
      var25.popMatrix();
      RenderSystem.depthMask(true);
      RenderSystem.disableBlend();
      RenderSystem.setShaderFog(FogParameters.NO_FOG);
   }

   private void addMainPass(FrameGraphBuilder var1, Frustum var2, Camera var3, Matrix4f var4, Matrix4f var5, FogParameters var6, boolean var7, boolean var8, DeltaTracker var9, ProfilerFiller var10) {
      FramePass var11 = var1.addPass("main");
      this.targets.main = var11.<RenderTarget>readsAndWrites(this.targets.main);
      if (this.targets.translucent != null) {
         this.targets.translucent = var11.<RenderTarget>readsAndWrites(this.targets.translucent);
      }

      if (this.targets.itemEntity != null) {
         this.targets.itemEntity = var11.<RenderTarget>readsAndWrites(this.targets.itemEntity);
      }

      if (this.targets.weather != null) {
         this.targets.weather = var11.<RenderTarget>readsAndWrites(this.targets.weather);
      }

      if (var8 && this.targets.entityOutline != null) {
         this.targets.entityOutline = var11.<RenderTarget>readsAndWrites(this.targets.entityOutline);
      }

      ResourceHandle var12 = this.targets.main;
      ResourceHandle var13 = this.targets.translucent;
      ResourceHandle var14 = this.targets.itemEntity;
      ResourceHandle var15 = this.targets.weather;
      ResourceHandle var16 = this.targets.entityOutline;
      var11.executes(() -> {
         RenderSystem.setShaderFog(var6);
         float var14x = var9.getGameTimeDeltaPartialTick(false);
         Vec3 var15x = var3.getPosition();
         double var16x = var15x.x();
         double var18 = var15x.y();
         double var20 = var15x.z();
         var10.push("terrain");
         this.renderSectionLayer(RenderType.solid(), var16x, var18, var20, var4, var5);
         this.renderSectionLayer(RenderType.cutoutMipped(), var16x, var18, var20, var4, var5);
         this.renderSectionLayer(RenderType.cutout(), var16x, var18, var20, var4, var5);
         if (this.level.effects().constantAmbientLight()) {
            Lighting.setupNetherLevel();
         } else {
            Lighting.setupLevel();
         }

         if (var14 != null) {
            ((RenderTarget)var14.get()).setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            ((RenderTarget)var14.get()).clear();
            ((RenderTarget)var14.get()).copyDepthFrom(this.minecraft.getMainRenderTarget());
            ((RenderTarget)var12.get()).bindWrite(false);
         }

         if (var15 != null) {
            ((RenderTarget)var15.get()).setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            ((RenderTarget)var15.get()).clear();
         }

         if (this.shouldShowEntityOutlines() && var16 != null) {
            ((RenderTarget)var16.get()).setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            ((RenderTarget)var16.get()).clear();
            ((RenderTarget)var12.get()).bindWrite(false);
         }

         PoseStack var22 = new PoseStack();
         MultiBufferSource.BufferSource var23 = this.renderBuffers.bufferSource();
         MultiBufferSource.BufferSource var24 = this.renderBuffers.crumblingBufferSource();
         var10.popPush("entities");
         this.renderEntities(var22, var23, var3, var9, this.visibleEntities);
         var23.endLastBatch();
         this.checkPoseStack(var22);
         var10.popPush("blockentities");
         this.renderBlockEntities(var22, var23, var24, var3, var14x);
         var23.endLastBatch();
         this.checkPoseStack(var22);
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
         if (var7) {
            this.renderBlockOutline(var3, var23, var22, false);
         }

         var10.popPush("debug");
         this.minecraft.debugRenderer.render(var22, var2, var23, var16x, var18, var20);
         var23.endLastBatch();
         this.checkPoseStack(var22);
         var23.endBatch(Sheets.translucentItemSheet());
         var23.endBatch(Sheets.bannerSheet());
         var23.endBatch(Sheets.shieldSheet());
         var23.endBatch(RenderType.armorEntityGlint());
         var23.endBatch(RenderType.glint());
         var23.endBatch(RenderType.glintTranslucent());
         var23.endBatch(RenderType.entityGlint());
         var10.popPush("destroyProgress");
         this.renderBlockDestroyAnimation(var22, var3, var24);
         var24.endBatch();
         this.checkPoseStack(var22);
         var23.endBatch(RenderType.waterMask());
         var23.endBatch();
         if (var13 != null) {
            ((RenderTarget)var13.get()).setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            ((RenderTarget)var13.get()).clear();
            ((RenderTarget)var13.get()).copyDepthFrom((RenderTarget)var12.get());
         }

         var10.popPush("translucent");
         this.renderSectionLayer(RenderType.translucent(), var16x, var18, var20, var4, var5);
         var10.popPush("string");
         this.renderSectionLayer(RenderType.tripwire(), var16x, var18, var20, var4, var5);
         if (var7) {
            this.renderBlockOutline(var3, var23, var22, true);
         }

         var23.endBatch();
         var10.pop();
      });
   }

   private void addParticlesPass(FrameGraphBuilder var1, Camera var2, float var3, FogParameters var4) {
      FramePass var5 = var1.addPass("particles");
      if (this.targets.particles != null) {
         this.targets.particles = var5.<RenderTarget>readsAndWrites(this.targets.particles);
         var5.reads(this.targets.main);
      } else {
         this.targets.main = var5.<RenderTarget>readsAndWrites(this.targets.main);
      }

      ResourceHandle var6 = this.targets.main;
      ResourceHandle var7 = this.targets.particles;
      var5.executes(() -> {
         RenderSystem.setShaderFog(var4);
         if (var7 != null) {
            ((RenderTarget)var7.get()).setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            ((RenderTarget)var7.get()).clear();
            ((RenderTarget)var7.get()).copyDepthFrom((RenderTarget)var6.get());
         }

         this.minecraft.particleEngine.render(var2, var3, this.renderBuffers.bufferSource());
      });
   }

   private void addCloudsPass(FrameGraphBuilder var1, Matrix4f var2, Matrix4f var3, CloudStatus var4, Vec3 var5, float var6, int var7, float var8) {
      FramePass var9 = var1.addPass("clouds");
      if (this.targets.clouds != null) {
         this.targets.clouds = var9.<RenderTarget>readsAndWrites(this.targets.clouds);
      } else {
         this.targets.main = var9.<RenderTarget>readsAndWrites(this.targets.main);
      }

      ResourceHandle var10 = this.targets.clouds;
      var9.executes(() -> {
         if (var10 != null) {
            ((RenderTarget)var10.get()).setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            ((RenderTarget)var10.get()).clear();
         }

         this.cloudRenderer.render(var7, var4, var8, var2, var3, var5, var6);
      });
   }

   private void addWeatherPass(FrameGraphBuilder var1, Vec3 var2, float var3, FogParameters var4) {
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

   private void addLateDebugPass(FrameGraphBuilder var1, Vec3 var2, FogParameters var3) {
      FramePass var4 = var1.addPass("late_debug");
      this.targets.main = var4.<RenderTarget>readsAndWrites(this.targets.main);
      if (this.targets.itemEntity != null) {
         this.targets.itemEntity = var4.<RenderTarget>readsAndWrites(this.targets.itemEntity);
      }

      ResourceHandle var5 = this.targets.main;
      var4.executes(() -> {
         RenderSystem.setShaderFog(var3);
         ((RenderTarget)var5.get()).bindWrite(false);
         PoseStack var4 = new PoseStack();
         MultiBufferSource.BufferSource var5x = this.renderBuffers.bufferSource();
         this.minecraft.debugRenderer.renderAfterTranslucents(var4, var5x, var2.x, var2.y, var2.z);
         var5x.endLastBatch();
         this.checkPoseStack(var4);
      });
   }

   private boolean collectVisibleEntities(Camera var1, Frustum var2, List<Entity> var3) {
      Vec3 var4 = var1.getPosition();
      double var5 = var4.x();
      double var7 = var4.y();
      double var9 = var4.z();
      boolean var11 = false;
      boolean var12 = this.shouldShowEntityOutlines();
      Entity.setViewScale(Mth.clamp((double)this.minecraft.options.getEffectiveRenderDistance() / 8.0, 1.0, 2.5) * (Double)this.minecraft.options.entityDistanceScaling().get());

      for(Entity var14 : this.level.entitiesForRendering()) {
         if (this.entityRenderDispatcher.shouldRender(var14, var2, var5, var7, var9) || var14.hasIndirectPassenger(this.minecraft.player)) {
            BlockPos var15 = var14.blockPosition();
            if ((this.level.isOutsideBuildHeight(var15.getY()) || this.isSectionCompiled(var15)) && (var14 != var1.getEntity() || var1.isDetached() || var1.getEntity() instanceof LivingEntity && ((LivingEntity)var1.getEntity()).isSleeping()) && (!(var14 instanceof LocalPlayer) || var1.getEntity() == var14)) {
               var3.add(var14);
               if (var12 && this.minecraft.shouldEntityAppearGlowing(var14)) {
                  var11 = true;
               }
            }
         }
      }

      return var11;
   }

   private void renderEntities(PoseStack var1, MultiBufferSource.BufferSource var2, Camera var3, DeltaTracker var4, List<Entity> var5) {
      Vec3 var6 = var3.getPosition();
      double var7 = var6.x();
      double var9 = var6.y();
      double var11 = var6.z();
      TickRateManager var13 = this.minecraft.level.tickRateManager();
      boolean var14 = this.shouldShowEntityOutlines();

      for(Entity var16 : var5) {
         if (var16.tickCount == 0) {
            var16.xOld = var16.getX();
            var16.yOld = var16.getY();
            var16.zOld = var16.getZ();
         }

         Object var17;
         if (var14 && this.minecraft.shouldEntityAppearGlowing(var16)) {
            OutlineBufferSource var18 = this.renderBuffers.outlineBufferSource();
            var17 = var18;
            int var19 = var16.getTeamColor();
            var18.setColor(ARGB.red(var19), ARGB.green(var19), ARGB.blue(var19), 255);
         } else {
            var17 = var2;
         }

         float var20 = var4.getGameTimeDeltaPartialTick(!var13.isEntityFrozen(var16));
         this.renderEntity(var16, var7, var9, var11, var20, var1, (MultiBufferSource)var17);
      }

   }

   private void renderBlockEntities(PoseStack var1, MultiBufferSource.BufferSource var2, MultiBufferSource.BufferSource var3, Camera var4, float var5) {
      Vec3 var6 = var4.getPosition();
      double var7 = var6.x();
      double var9 = var6.y();
      double var11 = var6.z();
      ObjectListIterator var13 = this.visibleSections.iterator();

      while(var13.hasNext()) {
         SectionRenderDispatcher.RenderSection var14 = (SectionRenderDispatcher.RenderSection)var13.next();
         List var15 = var14.getCompiled().getRenderableBlockEntities();
         if (!var15.isEmpty()) {
            for(BlockEntity var17 : var15) {
               BlockPos var18 = var17.getBlockPos();
               Object var19 = var2;
               var1.pushPose();
               var1.translate((double)var18.getX() - var7, (double)var18.getY() - var9, (double)var18.getZ() - var11);
               SortedSet var20 = (SortedSet)this.destructionProgress.get(var18.asLong());
               if (var20 != null && !var20.isEmpty()) {
                  int var21 = ((BlockDestructionProgress)var20.last()).getProgress();
                  if (var21 >= 0) {
                     PoseStack.Pose var22 = var1.last();
                     SheetedDecalTextureGenerator var23 = new SheetedDecalTextureGenerator(var3.getBuffer((RenderType)ModelBakery.DESTROY_TYPES.get(var21)), var22, 1.0F);
                     var19 = (var2x) -> {
                        VertexConsumer var3 = var2.getBuffer(var2x);
                        return var2x.affectsCrumbling() ? VertexMultiConsumer.create(var23, var3) : var3;
                     };
                  }
               }

               this.blockEntityRenderDispatcher.render(var17, var5, var1, (MultiBufferSource)var19);
               var1.popPose();
            }
         }
      }

      synchronized(this.globalBlockEntities) {
         for(BlockEntity var27 : this.globalBlockEntities) {
            BlockPos var28 = var27.getBlockPos();
            var1.pushPose();
            var1.translate((double)var28.getX() - var7, (double)var28.getY() - var9, (double)var28.getZ() - var11);
            this.blockEntityRenderDispatcher.render(var27, var5, var1, var2);
            var1.popPose();
         }

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
      if (!var1.clear()) {
         throw new IllegalStateException("Pose stack not empty");
      }
   }

   private void renderEntity(Entity var1, double var2, double var4, double var6, float var8, PoseStack var9, MultiBufferSource var10) {
      double var11 = Mth.lerp((double)var8, var1.xOld, var1.getX());
      double var13 = Mth.lerp((double)var8, var1.yOld, var1.getY());
      double var15 = Mth.lerp((double)var8, var1.zOld, var1.getZ());
      this.entityRenderDispatcher.render(var1, var11 - var2, var13 - var4, var15 - var6, var8, var9, var10, this.entityRenderDispatcher.getPackedLightCoords(var1, var8));
   }

   private void scheduleTranslucentSectionResort(Vec3 var1) {
      if (!this.visibleSections.isEmpty()) {
         BlockPos var2 = BlockPos.containing(var1);
         boolean var3 = !var2.equals(this.lastTranslucentSortBlockPos);
         Profiler.get().push("translucent_sort");
         SectionRenderDispatcher.TranslucencyPointOfView var4 = new SectionRenderDispatcher.TranslucencyPointOfView();
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
         Profiler.get().pop();
      }
   }

   private void scheduleResort(SectionRenderDispatcher.RenderSection var1, SectionRenderDispatcher.TranslucencyPointOfView var2, Vec3 var3, boolean var4, boolean var5) {
      var2.set(var3, var1.getSectionNode());
      boolean var6 = !var2.equals(var1.pointOfView.get());
      boolean var7 = var4 && (var2.isAxisAligned() || var5);
      if ((var7 || var6) && !var1.transparencyResortingScheduled() && var1.hasTranslucentGeometry()) {
         var1.resortTransparency(this.sectionRenderDispatcher);
      }

   }

   private void renderSectionLayer(RenderType var1, double var2, double var4, double var6, Matrix4f var8, Matrix4f var9) {
      RenderSystem.assertOnRenderThread();
      Zone var10 = Profiler.get().zone((Supplier)(() -> "render_" + var1.name));
      Objects.requireNonNull(var1);
      var10.addText(var1::toString);
      boolean var11 = var1 != RenderType.translucent();
      ObjectListIterator var12 = this.visibleSections.listIterator(var11 ? 0 : this.visibleSections.size());
      var1.setupRenderState();
      CompiledShaderProgram var13 = RenderSystem.getShader();
      if (var13 == null) {
         var1.clearRenderState();
         var10.close();
      } else {
         var13.setDefaultUniforms(VertexFormat.Mode.QUADS, var8, var9, this.minecraft.getWindow());
         var13.apply();
         Uniform var14 = var13.MODEL_OFFSET;

         while(true) {
            if (var11) {
               if (!var12.hasNext()) {
                  break;
               }
            } else if (!var12.hasPrevious()) {
               break;
            }

            SectionRenderDispatcher.RenderSection var15 = var11 ? (SectionRenderDispatcher.RenderSection)var12.next() : (SectionRenderDispatcher.RenderSection)var12.previous();
            if (!var15.getCompiled().isEmpty(var1)) {
               VertexBuffer var16 = var15.getBuffer(var1);
               BlockPos var17 = var15.getOrigin();
               if (var14 != null) {
                  var14.set((float)((double)var17.getX() - var2), (float)((double)var17.getY() - var4), (float)((double)var17.getZ() - var6));
                  var14.upload();
               }

               var16.bind();
               var16.draw();
            }
         }

         if (var14 != null) {
            var14.set(0.0F, 0.0F, 0.0F);
         }

         var13.clear();
         VertexBuffer.unbind();
         var10.close();
         var1.clearRenderState();
      }
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

   private void addSkyPass(FrameGraphBuilder var1, Camera var2, float var3, FogParameters var4) {
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
                  MultiBufferSource.BufferSource var16 = this.renderBuffers.bufferSource();
                  if (var6.isSunriseOrSunset(var7x)) {
                     this.skyRenderer.renderSunriseAndSunset(var5, var16, var6x, var10);
                  }

                  this.skyRenderer.renderSunMoonAndStars(var5, var16, var7x, var11, var8, var9, var4);
                  var16.endBatch();
                  if (this.shouldRenderDarkDisc(var3)) {
                     this.skyRenderer.renderDarkDisc(var5);
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
      var2.push("populate_sections_to_compile");
      RenderRegionCache var3 = new RenderRegionCache();
      BlockPos var4 = var1.getBlockPosition();
      ArrayList var5 = Lists.newArrayList();
      ObjectListIterator var6 = this.visibleSections.iterator();

      while(var6.hasNext()) {
         SectionRenderDispatcher.RenderSection var7 = (SectionRenderDispatcher.RenderSection)var6.next();
         if (var7.isDirty() && var7.hasAllNeighbors()) {
            boolean var8 = false;
            if (this.minecraft.options.prioritizeChunkUpdates().get() == PrioritizeChunkUpdates.NEARBY) {
               BlockPos var9 = var7.getOrigin().offset(8, 8, 8);
               var8 = var9.distSqr(var4) < 768.0 || var7.isDirtyFromPlayer();
            } else if (this.minecraft.options.prioritizeChunkUpdates().get() == PrioritizeChunkUpdates.PLAYER_AFFECTED) {
               var8 = var7.isDirtyFromPlayer();
            }

            if (var8) {
               var2.push("build_near_sync");
               this.sectionRenderDispatcher.rebuildSectionSync(var7, var3);
               var7.setNotDirty();
               var2.pop();
            } else {
               var5.add(var7);
            }
         }
      }

      var2.popPush("upload");
      this.sectionRenderDispatcher.uploadAllPendingUploads();
      var2.popPush("schedule_async_compile");

      for(SectionRenderDispatcher.RenderSection var11 : var5) {
         var11.rebuildSectionAsync(this.sectionRenderDispatcher, var3);
         var11.setNotDirty();
      }

      var2.pop();
      this.scheduleTranslucentSectionResort(var1.getPosition());
   }

   private void renderHitOutline(PoseStack var1, VertexConsumer var2, Entity var3, double var4, double var6, double var8, BlockPos var10, BlockState var11, int var12) {
      ShapeRenderer.renderShape(var1, var2, var11.getShape(this.level, var10, CollisionContext.of(var3)), (double)var10.getX() - var4, (double)var10.getY() - var6, (double)var10.getZ() - var8, var12);
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
         this.addParticleInternal(var1, var2, var3, var4, var6, var8, var10, var12, var14);
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

   @Nullable
   Particle addParticleInternal(ParticleOptions var1, boolean var2, double var3, double var5, double var7, double var9, double var11, double var13) {
      return this.addParticleInternal(var1, var2, false, var3, var5, var7, var9, var11, var13);
   }

   @Nullable
   private Particle addParticleInternal(ParticleOptions var1, boolean var2, boolean var3, double var4, double var6, double var8, double var10, double var12, double var14) {
      Camera var16 = this.minecraft.gameRenderer.getMainCamera();
      ParticleStatus var17 = this.calculateParticleLevel(var3);
      if (var2) {
         return this.minecraft.particleEngine.createParticle(var1, var4, var6, var8, var10, var12, var14);
      } else if (var16.getPosition().distanceToSqr(var4, var6, var8) > 1024.0) {
         return null;
      } else {
         return var17 == ParticleStatus.MINIMAL ? null : this.minecraft.particleEngine.createParticle(var1, var4, var6, var8, var10, var12, var14);
      }
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

   public void updateGlobalBlockEntities(Collection<BlockEntity> var1, Collection<BlockEntity> var2) {
      synchronized(this.globalBlockEntities) {
         this.globalBlockEntities.removeAll(var1);
         this.globalBlockEntities.addAll(var2);
      }
   }

   public static int getLightColor(BlockAndTintGetter var0, BlockPos var1) {
      return getLightColor(var0, var0.getBlockState(var1), var1);
   }

   public static int getLightColor(BlockAndTintGetter var0, BlockState var1, BlockPos var2) {
      if (var1.emissiveRendering(var0, var2)) {
         return 15728880;
      } else {
         int var3 = var0.getBrightness(LightLayer.SKY, var2);
         int var4 = var0.getBrightness(LightLayer.BLOCK, var2);
         int var5 = var1.getLightEmission();
         if (var4 < var5) {
            var4 = var5;
         }

         return var3 << 20 | var4 << 4;
      }
   }

   public boolean isSectionCompiled(BlockPos var1) {
      SectionRenderDispatcher.RenderSection var2 = this.viewArea.getRenderSectionAt(var1);
      return var2 != null && var2.compiled.get() != SectionRenderDispatcher.CompiledSection.UNCOMPILED;
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
}
