package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
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
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.SortedSet;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
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
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.BlockDestructionProgress;
import net.minecraft.server.level.ParticleStatus;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.VisibleForDebug;
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
import net.minecraft.world.level.lighting.LevelLightEngine;
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
   public static final int SECTION_SIZE = 16;
   public static final int HALF_SECTION_SIZE = 8;
   private static final int TRANSPARENT_SORT_COUNT = 15;
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
   private final Set<BlockEntity> globalBlockEntities = Sets.newHashSet();
   @Nullable
   private ViewArea viewArea;
   private int ticks;
   private final Int2ObjectMap<BlockDestructionProgress> destroyingBlocks = new Int2ObjectOpenHashMap();
   private final Long2ObjectMap<SortedSet<BlockDestructionProgress>> destructionProgress = new Long2ObjectOpenHashMap();
   @Nullable
   private PostChain entityOutlineChain;
   @Nullable
   private RenderTarget entityOutlineTarget;
   @Nullable
   private PostChain transparencyChain;
   private final LevelTargetBundle targets = new LevelTargetBundle();
   private int lastCameraSectionX = -2147483648;
   private int lastCameraSectionY = -2147483648;
   private int lastCameraSectionZ = -2147483648;
   private double prevCamX = 5.0E-324;
   private double prevCamY = 5.0E-324;
   private double prevCamZ = 5.0E-324;
   private double prevCamRotX = 5.0E-324;
   private double prevCamRotY = 5.0E-324;
   @Nullable
   private SectionRenderDispatcher sectionRenderDispatcher;
   private int lastViewDistance = -1;
   private final List<Entity> visibleEntities = new ArrayList<>();
   private int visibleEntityCount;
   private Frustum cullingFrustum;
   private boolean captureFrustum;
   @Nullable
   private Frustum capturedFrustum;
   @Nullable
   private Vec3 lastTranslucentSortPos;

   public LevelRenderer(Minecraft var1, EntityRenderDispatcher var2, BlockEntityRenderDispatcher var3, RenderBuffers var4) {
      super();
      this.minecraft = var1;
      this.entityRenderDispatcher = var2;
      this.blockEntityRenderDispatcher = var3;
      this.renderBuffers = var4;
   }

   public void tickParticles(Camera var1) {
      this.weatherEffectRenderer.tickRainParticles(this.minecraft.level, var1, this.ticks, this.minecraft.options.particles().get());
   }

   @Override
   public void close() {
      if (this.entityOutlineChain != null) {
         this.entityOutlineChain.close();
      }

      if (this.entityOutlineTarget != null) {
         this.entityOutlineTarget.destroyBuffers();
      }

      if (this.transparencyChain != null) {
         this.transparencyChain.close();
      }

      this.cloudRenderer.close();
   }

   @Override
   public void onResourceManagerReload(ResourceManager var1) {
      this.initOutline();
      if (Minecraft.useShaderTransparency()) {
         this.initTransparency();
      }
   }

   public void initOutline() {
      if (this.entityOutlineChain != null) {
         this.entityOutlineChain.close();
      }

      if (this.entityOutlineTarget != null) {
         this.entityOutlineTarget.destroyBuffers();
      }

      ResourceLocation var1 = ResourceLocation.withDefaultNamespace("shaders/post/entity_outline.json");

      try {
         this.entityOutlineChain = PostChain.load(
            this.minecraft.getResourceManager(),
            this.minecraft.getTextureManager(),
            var1,
            Set.of(LevelTargetBundle.MAIN_TARGET_ID, LevelTargetBundle.ENTITY_OUTLINE_TARGET_ID)
         );
         this.entityOutlineTarget = new TextureTarget(this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight(), true);
         this.entityOutlineTarget.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
      } catch (IOException var3) {
         LOGGER.warn("Failed to load shader: {}", var1, var3);
         this.entityOutlineChain = null;
         this.entityOutlineTarget = null;
      } catch (JsonSyntaxException var4) {
         LOGGER.warn("Failed to parse shader: {}", var1, var4);
         this.entityOutlineChain = null;
         this.entityOutlineTarget = null;
      }
   }

   private void initTransparency() {
      this.deinitTransparency();
      ResourceLocation var1 = ResourceLocation.withDefaultNamespace("shaders/post/transparency.json");

      try {
         this.transparencyChain = PostChain.load(
            this.minecraft.getResourceManager(), this.minecraft.getTextureManager(), var1, LevelTargetBundle.SORTING_TARGETS
         );
      } catch (Exception var7) {
         String var3 = var7 instanceof JsonSyntaxException ? "parse" : "load";
         String var4 = "Failed to " + var3 + " shader: " + var1;
         LevelRenderer.TransparencyShaderException var5 = new LevelRenderer.TransparencyShaderException(var4, var7);
         if (this.minecraft.getResourcePackRepository().getSelectedIds().size() > 1) {
            Component var6 = this.minecraft.getResourceManager().listPacks().findFirst().map(var0 -> Component.literal(var0.packId())).orElse(null);
            this.minecraft.options.graphicsMode().set(GraphicsStatus.FANCY);
            this.minecraft.clearResourcePacksOnError(var5, var6, null);
         } else {
            this.minecraft.options.graphicsMode().set(GraphicsStatus.FANCY);
            this.minecraft.options.save();
            LOGGER.error(LogUtils.FATAL_MARKER, var4, var5);
            this.minecraft.emergencySaveAndCrash(new CrashReport(var4, var5));
         }
      }
   }

   private void deinitTransparency() {
      if (this.transparencyChain != null) {
         this.transparencyChain.close();
         this.transparencyChain = null;
      }
   }

   public void doEntityOutline() {
      if (this.shouldShowEntityOutlines()) {
         RenderSystem.enableBlend();
         RenderSystem.blendFuncSeparate(
            GlStateManager.SourceFactor.SRC_ALPHA,
            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
            GlStateManager.SourceFactor.ZERO,
            GlStateManager.DestFactor.ONE
         );
         this.entityOutlineTarget.blitToScreen(this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight(), false);
         RenderSystem.disableBlend();
         RenderSystem.defaultBlendFunc();
      }
   }

   protected boolean shouldShowEntityOutlines() {
      return !this.minecraft.gameRenderer.isPanoramicMode()
         && this.entityOutlineTarget != null
         && this.entityOutlineChain != null
         && this.minecraft.player != null;
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
         this.sectionOcclusionGraph.waitAndReset(null);
         this.visibleSections.clear();
      }
   }

   public void graphicsChanged() {
      if (Minecraft.useShaderTransparency()) {
         this.initTransparency();
      } else {
         this.deinitTransparency();
      }
   }

   public void allChanged() {
      if (this.level != null) {
         this.graphicsChanged();
         this.level.clearTintCaches();
         if (this.sectionRenderDispatcher == null) {
            this.sectionRenderDispatcher = new SectionRenderDispatcher(
               this.level,
               this,
               Util.backgroundExecutor(),
               this.renderBuffers,
               this.minecraft.getBlockRenderer(),
               this.minecraft.getBlockEntityRenderDispatcher()
            );
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
         synchronized (this.globalBlockEntities) {
            this.globalBlockEntities.clear();
         }

         this.viewArea = new ViewArea(this.sectionRenderDispatcher, this.level, this.minecraft.options.getEffectiveRenderDistance(), this);
         this.sectionOcclusionGraph.waitAndReset(this.viewArea);
         this.visibleSections.clear();
         Entity var4 = this.minecraft.getCameraEntity();
         if (var4 != null) {
            this.viewArea.repositionCamera(var4.getX(), var4.getZ());
         }
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
      return String.format(
         Locale.ROOT,
         "C: %d/%d %sD: %d, %s",
         var2,
         var1,
         this.minecraft.smartCull ? "(s) " : "",
         this.lastViewDistance,
         this.sectionRenderDispatcher == null ? "null" : this.sectionRenderDispatcher.getStats()
      );
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

      while (var2.hasNext()) {
         SectionRenderDispatcher.RenderSection var3 = (SectionRenderDispatcher.RenderSection)var2.next();
         if (!var3.getCompiled().hasNoRenderableLayers()) {
            var1++;
         }
      }

      return var1;
   }

   public String getEntityStatistics() {
      return "E: " + this.visibleEntityCount + "/" + this.level.getEntityCount() + ", SD: " + this.level.getServerSimulationDistance();
   }

   private void setupRender(Camera var1, Frustum var2, boolean var3, boolean var4) {
      Vec3 var5 = var1.getPosition();
      if (this.minecraft.options.getEffectiveRenderDistance() != this.lastViewDistance) {
         this.allChanged();
      }

      ProfilerFiller var6 = this.level.getProfiler();
      var6.push("camera");
      double var7 = this.minecraft.player.getX();
      double var9 = this.minecraft.player.getY();
      double var11 = this.minecraft.player.getZ();
      int var13 = SectionPos.posToSectionCoord(var7);
      int var14 = SectionPos.posToSectionCoord(var9);
      int var15 = SectionPos.posToSectionCoord(var11);
      if (this.lastCameraSectionX != var13 || this.lastCameraSectionY != var14 || this.lastCameraSectionZ != var15) {
         this.lastCameraSectionX = var13;
         this.lastCameraSectionY = var14;
         this.lastCameraSectionZ = var15;
         this.viewArea.repositionCamera(var7, var11);
      }

      this.sectionRenderDispatcher.setCamera(var5);
      var6.popPush("cull");
      double var16 = Math.floor(var5.x / 8.0);
      double var18 = Math.floor(var5.y / 8.0);
      double var20 = Math.floor(var5.z / 8.0);
      if (var16 != this.prevCamX || var18 != this.prevCamY || var20 != this.prevCamZ) {
         this.sectionOcclusionGraph.invalidate();
      }

      this.prevCamX = var16;
      this.prevCamY = var18;
      this.prevCamZ = var20;
      var6.popPush("update");
      if (!var3) {
         boolean var22 = this.minecraft.smartCull;
         if (var4 && this.level.getBlockState(var1.getBlockPosition()).isSolidRender()) {
            var22 = false;
         }

         var6.push("section_occlusion_graph");
         this.sectionOcclusionGraph.update(var22, var1, var2, this.visibleSections);
         var6.pop();
         double var23 = Math.floor((double)(var1.getXRot() / 2.0F));
         double var25 = Math.floor((double)(var1.getYRot() / 2.0F));
         if (this.sectionOcclusionGraph.consumeFrustumUpdate() || var23 != this.prevCamRotX || var25 != this.prevCamRotY) {
            this.applyFrustum(offsetFrustum(var2));
            this.prevCamRotX = var23;
            this.prevCamRotY = var25;
         }
      }

      var6.pop();
   }

   public static Frustum offsetFrustum(Frustum var0) {
      return new Frustum(var0).offsetToFullyIncludeCameraCube(8);
   }

   private void applyFrustum(Frustum var1) {
      if (!Minecraft.getInstance().isSameThread()) {
         throw new IllegalStateException("applyFrustum called from wrong thread: " + Thread.currentThread().getName());
      } else {
         this.minecraft.getProfiler().push("apply_frustum");
         this.visibleSections.clear();
         this.sectionOcclusionGraph.addSectionsInFrustum(var1, this.visibleSections);
         this.minecraft.getProfiler().pop();
      }
   }

   public void addRecentlyCompiledSection(SectionRenderDispatcher.RenderSection var1) {
      this.sectionOcclusionGraph.onSectionCompiled(var1);
   }

   public void prepareCullFrustum(Vec3 var1, Matrix4f var2, Matrix4f var3) {
      this.cullingFrustum = new Frustum(var2, var3);
      this.cullingFrustum.prepare(var1.x(), var1.y(), var1.z());
   }

   public void renderLevel(
      GraphicsResourceAllocator var1, DeltaTracker var2, boolean var3, Camera var4, GameRenderer var5, LightTexture var6, Matrix4f var7, Matrix4f var8
   ) {
      float var9 = var2.getGameTimeDeltaPartialTick(false);
      RenderSystem.setShaderGameTime(this.level.getGameTime(), var9);
      this.blockEntityRenderDispatcher.prepare(this.level, var4, this.minecraft.hitResult);
      this.entityRenderDispatcher.prepare(this.level, var4, this.minecraft.crosshairPickEntity);
      final ProfilerFiller var10 = this.level.getProfiler();
      var10.popPush("light_update_queue");
      this.level.pollLightUpdates();
      var10.popPush("light_updates");
      this.level.getChunkSource().getLightEngine().runLightUpdates();
      Vec3 var11 = var4.getPosition();
      double var12 = var11.x();
      double var14 = var11.y();
      double var16 = var11.z();
      var10.popPush("culling");
      boolean var18 = this.capturedFrustum != null;
      Frustum var19 = var18 ? this.capturedFrustum : this.cullingFrustum;
      this.minecraft.getProfiler().popPush("captureFrustum");
      if (this.captureFrustum) {
         this.capturedFrustum = var18 ? new Frustum(var7, var8) : var19;
         this.capturedFrustum.prepare(var12, var14, var16);
         this.captureFrustum = false;
      }

      var10.popPush("fog");
      float var20 = var5.getRenderDistance();
      boolean var21 = this.minecraft.level.effects().isFoggyAt(Mth.floor(var12), Mth.floor(var14))
         || this.minecraft.gui.getBossOverlay().shouldCreateWorldFog();
      Vector4f var22 = FogRenderer.computeFogColor(
         var4, var9, this.minecraft.level, this.minecraft.options.getEffectiveRenderDistance(), var5.getDarkenWorldAmount(var9)
      );
      FogParameters var23 = FogRenderer.setupFog(var4, FogRenderer.FogMode.FOG_TERRAIN, var22, var20, var21, var9);
      FogParameters var24 = FogRenderer.setupFog(var4, FogRenderer.FogMode.FOG_SKY, var22, var20, var21, var9);
      var10.popPush("cullEntities");
      boolean var25 = this.collectVisibleEntities(var4, var19, this.visibleEntities);
      this.visibleEntityCount = this.visibleEntities.size();
      var10.popPush("terrain_setup");
      this.setupRender(var4, var19, var18, this.minecraft.player.isSpectator());
      var10.popPush("compile_sections");
      this.compileSections(var4);
      Matrix4fStack var26 = RenderSystem.getModelViewStack();
      var26.pushMatrix();
      var26.mul(var7);
      FrameGraphBuilder var27 = new FrameGraphBuilder();
      this.targets.main = var27.importExternal("main", this.minecraft.getMainRenderTarget());
      int var28 = this.minecraft.getMainRenderTarget().width;
      int var29 = this.minecraft.getMainRenderTarget().height;
      RenderTargetDescriptor var30 = new RenderTargetDescriptor(var28, var29, true);
      if (this.transparencyChain != null) {
         this.targets.translucent = var27.createInternal("translucent", var30);
         this.targets.itemEntity = var27.createInternal("item_entity", var30);
         this.targets.particles = var27.createInternal("particles", var30);
         this.targets.weather = var27.createInternal("weather", var30);
         this.targets.clouds = var27.createInternal("clouds", var30);
      }

      if (this.entityOutlineTarget != null) {
         this.targets.entityOutline = var27.importExternal("entity_outline", this.entityOutlineTarget);
      }

      FramePass var31 = var27.addPass("clear");
      this.targets.main = var31.readsAndWrites(this.targets.main);
      var31.executes(() -> {
         RenderSystem.clearColor(var22.x, var22.y, var22.z, 0.0F);
         RenderSystem.clear(16640);
      });
      if (!var21) {
         this.addSkyPass(var27, var4, var9, var24);
      }

      this.addMainPass(var27, var4, var7, var8, var23, var3, var25, var2, var10);
      if (var25 && this.entityOutlineChain != null) {
         this.entityOutlineChain.addToFrame(var27, var2, var28, var29, this.targets);
      }

      this.addParticlesPass(var27, var4, var6, var9, var23);
      CloudStatus var32 = this.minecraft.options.getCloudsType();
      if (var32 != CloudStatus.OFF) {
         float var33 = this.level.effects().getCloudHeight();
         if (!Float.isNaN(var33)) {
            float var34 = (float)this.ticks + var9;
            int var35 = this.level.getCloudColor(var9);
            this.addCloudsPass(var27, var7, var8, var32, var4.getPosition(), var34, var35, var33 + 0.33F);
         }
      }

      this.addWeatherPass(var27, var6, var4.getPosition(), var9, var23);
      if (this.transparencyChain != null) {
         this.transparencyChain.addToFrame(var27, this.minecraft.getDeltaTracker(), var28, var29, this.targets);
      }

      this.addLateDebugPass(var27, var11, var23);
      var10.popPush("framegraph");
      var27.execute(var1, new FrameGraphBuilder.Inspector() {
         @Override
         public void beforeExecutePass(String var1) {
            var10.push(var1);
         }

         @Override
         public void afterExecutePass(String var1) {
            var10.pop();
         }
      });
      this.minecraft.getMainRenderTarget().bindWrite(false);
      this.visibleEntities.clear();
      this.targets.clear();
      var26.popMatrix();
      RenderSystem.depthMask(true);
      RenderSystem.disableBlend();
      RenderSystem.setShaderFog(FogParameters.NO_FOG);
   }

   private void addMainPass(
      FrameGraphBuilder var1, Camera var2, Matrix4f var3, Matrix4f var4, FogParameters var5, boolean var6, boolean var7, DeltaTracker var8, ProfilerFiller var9
   ) {
      FramePass var10 = var1.addPass("main");
      this.targets.main = var10.readsAndWrites(this.targets.main);
      if (this.targets.translucent != null) {
         this.targets.translucent = var10.readsAndWrites(this.targets.translucent);
      }

      if (this.targets.itemEntity != null) {
         this.targets.itemEntity = var10.readsAndWrites(this.targets.itemEntity);
      }

      if (this.targets.weather != null) {
         this.targets.weather = var10.readsAndWrites(this.targets.weather);
      }

      if (var7 && this.targets.entityOutline != null) {
         this.targets.entityOutline = var10.readsAndWrites(this.targets.entityOutline);
      }

      ResourceHandle var11 = this.targets.main;
      ResourceHandle var12 = this.targets.translucent;
      ResourceHandle var13 = this.targets.itemEntity;
      ResourceHandle var14 = this.targets.weather;
      ResourceHandle var15 = this.targets.entityOutline;
      var10.executes(() -> {
         RenderSystem.setShaderFog(var5);
         float var13x = var8.getGameTimeDeltaPartialTick(false);
         Vec3 var14x = var2.getPosition();
         double var15x = var14x.x();
         double var17 = var14x.y();
         double var19 = var14x.z();
         var9.push("terrain");
         this.renderSectionLayer(RenderType.solid(), var15x, var17, var19, var3, var4);
         this.renderSectionLayer(RenderType.cutoutMipped(), var15x, var17, var19, var3, var4);
         this.renderSectionLayer(RenderType.cutout(), var15x, var17, var19, var3, var4);
         if (this.level.effects().constantAmbientLight()) {
            Lighting.setupNetherLevel();
         } else {
            Lighting.setupLevel();
         }

         if (var13 != null) {
            ((RenderTarget)var13.get()).setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            ((RenderTarget)var13.get()).clear();
            ((RenderTarget)var13.get()).copyDepthFrom(this.minecraft.getMainRenderTarget());
            ((RenderTarget)var11.get()).bindWrite(false);
         }

         if (var14 != null) {
            ((RenderTarget)var14.get()).setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            ((RenderTarget)var14.get()).clear();
         }

         if (this.shouldShowEntityOutlines() && var15 != null) {
            ((RenderTarget)var15.get()).setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            ((RenderTarget)var15.get()).clear();
            ((RenderTarget)var11.get()).bindWrite(false);
         }

         PoseStack var21 = new PoseStack();
         MultiBufferSource.BufferSource var22 = this.renderBuffers.bufferSource();
         MultiBufferSource.BufferSource var23 = this.renderBuffers.crumblingBufferSource();
         var9.popPush("entities");
         this.renderEntities(var21, var22, var2, var8, this.visibleEntities);
         var22.endLastBatch();
         this.checkPoseStack(var21);
         var9.popPush("blockentities");
         this.renderBlockEntities(var21, var22, var23, var2, var13x);
         var22.endLastBatch();
         this.checkPoseStack(var21);
         var22.endBatch(RenderType.solid());
         var22.endBatch(RenderType.endPortal());
         var22.endBatch(RenderType.endGateway());
         var22.endBatch(Sheets.solidBlockSheet());
         var22.endBatch(Sheets.cutoutBlockSheet());
         var22.endBatch(Sheets.bedSheet());
         var22.endBatch(Sheets.shulkerBoxSheet());
         var22.endBatch(Sheets.signSheet());
         var22.endBatch(Sheets.hangingSignSheet());
         var22.endBatch(Sheets.chestSheet());
         this.renderBuffers.outlineBufferSource().endOutlineBatch();
         if (var6) {
            this.renderBlockOutline(var2, var22, var21, false);
         }

         var9.popPush("debug");
         this.minecraft.debugRenderer.render(var21, var22, var15x, var17, var19);
         var22.endLastBatch();
         this.checkPoseStack(var21);
         var22.endBatch(Sheets.bannerSheet());
         var22.endBatch(Sheets.shieldSheet());
         var22.endBatch(RenderType.armorEntityGlint());
         var22.endBatch(RenderType.glint());
         var22.endBatch(RenderType.glintTranslucent());
         var22.endBatch(RenderType.entityGlint());
         var22.endBatch(RenderType.entityGlintDirect());
         var9.popPush("destroyProgress");
         this.renderBlockDestroyAnimation(var21, var2, var23);
         var23.endBatch();
         this.checkPoseStack(var21);
         var22.endBatch(RenderType.waterMask());
         var22.endBatch(Sheets.translucentCullBlockSheet());
         var22.endBatch();
         if (var12 != null) {
            ((RenderTarget)var12.get()).setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            ((RenderTarget)var12.get()).clear();
            ((RenderTarget)var12.get()).copyDepthFrom((RenderTarget)var11.get());
         }

         var9.popPush("translucent");
         this.renderSectionLayer(RenderType.translucent(), var15x, var17, var19, var3, var4);
         var9.popPush("string");
         this.renderSectionLayer(RenderType.tripwire(), var15x, var17, var19, var3, var4);
         if (var6) {
            this.renderBlockOutline(var2, var22, var21, true);
         }

         var22.endBatch();
         var9.pop();
      });
   }

   private void addParticlesPass(FrameGraphBuilder var1, Camera var2, LightTexture var3, float var4, FogParameters var5) {
      FramePass var6 = var1.addPass("particles");
      if (this.targets.particles != null) {
         this.targets.particles = var6.readsAndWrites(this.targets.particles);
         var6.reads(this.targets.main);
      } else {
         this.targets.main = var6.readsAndWrites(this.targets.main);
      }

      ResourceHandle var7 = this.targets.main;
      ResourceHandle var8 = this.targets.particles;
      var6.executes(() -> {
         RenderSystem.setShaderFog(var5);
         if (var8 != null) {
            ((RenderTarget)var8.get()).setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            ((RenderTarget)var8.get()).clear();
            ((RenderTarget)var8.get()).copyDepthFrom((RenderTarget)var7.get());
         }

         RenderStateShard.PARTICLES_TARGET.setupRenderState();
         this.minecraft.particleEngine.render(var3, var2, var4);
         RenderStateShard.PARTICLES_TARGET.clearRenderState();
      });
   }

   private void addCloudsPass(FrameGraphBuilder var1, Matrix4f var2, Matrix4f var3, CloudStatus var4, Vec3 var5, float var6, int var7, float var8) {
      FramePass var9 = var1.addPass("clouds");
      if (this.targets.clouds != null) {
         this.targets.clouds = var9.readsAndWrites(this.targets.clouds);
      } else {
         this.targets.main = var9.readsAndWrites(this.targets.main);
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

   private void addWeatherPass(FrameGraphBuilder var1, LightTexture var2, Vec3 var3, float var4, FogParameters var5) {
      int var6 = this.minecraft.options.getEffectiveRenderDistance() * 16;
      float var7 = this.minecraft.gameRenderer.getDepthFar();
      FramePass var8 = var1.addPass("weather");
      if (this.targets.weather != null) {
         this.targets.weather = var8.readsAndWrites(this.targets.weather);
      } else {
         this.targets.main = var8.readsAndWrites(this.targets.main);
      }

      var8.executes(() -> {
         RenderSystem.setShaderFog(var5);
         RenderStateShard.WEATHER_TARGET.setupRenderState();
         this.weatherEffectRenderer.render(this.minecraft.level, var2, this.ticks, var4, var3);
         this.worldBorderRenderer.render(this.level.getWorldBorder(), var3, (double)var6, (double)var7);
         RenderStateShard.WEATHER_TARGET.clearRenderState();
      });
   }

   private void addLateDebugPass(FrameGraphBuilder var1, Vec3 var2, FogParameters var3) {
      FramePass var4 = var1.addPass("late_debug");
      this.targets.main = var4.readsAndWrites(this.targets.main);
      if (this.targets.itemEntity != null) {
         this.targets.itemEntity = var4.readsAndWrites(this.targets.itemEntity);
      }

      ResourceHandle var5 = this.targets.main;
      var4.executes(() -> {
         RenderSystem.setShaderFog(var3);
         ((RenderTarget)var5.get()).bindWrite(false);
         PoseStack var4x = new PoseStack();
         MultiBufferSource.BufferSource var5x = this.renderBuffers.bufferSource();
         this.minecraft.debugRenderer.renderAfterTranslucents(var4x, var5x, var2.x, var2.y, var2.z);
         var5x.endLastBatch();
         this.checkPoseStack(var4x);
      });
   }

   private boolean collectVisibleEntities(Camera var1, Frustum var2, List<Entity> var3) {
      Vec3 var4 = var1.getPosition();
      double var5 = var4.x();
      double var7 = var4.y();
      double var9 = var4.z();
      boolean var11 = false;
      boolean var12 = this.shouldShowEntityOutlines();
      Entity.setViewScale(
         Mth.clamp((double)this.minecraft.options.getEffectiveRenderDistance() / 8.0, 1.0, 2.5) * this.minecraft.options.entityDistanceScaling().get()
      );

      for (Entity var14 : this.level.entitiesForRendering()) {
         if (this.entityRenderDispatcher.shouldRender(var14, var2, var5, var7, var9) || var14.hasIndirectPassenger(this.minecraft.player)) {
            BlockPos var15 = var14.blockPosition();
            if ((this.level.isOutsideBuildHeight(var15.getY()) || this.isSectionCompiled(var15))
               && (var14 != var1.getEntity() || var1.isDetached() || var1.getEntity() instanceof LivingEntity && ((LivingEntity)var1.getEntity()).isSleeping())
               && (!(var14 instanceof LocalPlayer) || var1.getEntity() == var14)) {
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

      for (Entity var16 : var5) {
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

      while (var13.hasNext()) {
         SectionRenderDispatcher.RenderSection var14 = (SectionRenderDispatcher.RenderSection)var13.next();
         List var15 = var14.getCompiled().getRenderableBlockEntities();
         if (!var15.isEmpty()) {
            for (BlockEntity var17 : var15) {
               BlockPos var18 = var17.getBlockPos();
               Object var19 = var2;
               var1.pushPose();
               var1.translate((double)var18.getX() - var7, (double)var18.getY() - var9, (double)var18.getZ() - var11);
               SortedSet var20 = (SortedSet)this.destructionProgress.get(var18.asLong());
               if (var20 != null && !var20.isEmpty()) {
                  int var21 = ((BlockDestructionProgress)var20.last()).getProgress();
                  if (var21 >= 0) {
                     PoseStack.Pose var22 = var1.last();
                     SheetedDecalTextureGenerator var23 = new SheetedDecalTextureGenerator(var3.getBuffer(ModelBakery.DESTROY_TYPES.get(var21)), var22, 1.0F);
                     var19 = (MultiBufferSource)var2x -> {
                        VertexConsumer var3x = var2.getBuffer(var2x);
                        return var2x.affectsCrumbling() ? VertexMultiConsumer.create(var23, var3x) : var3x;
                     };
                  }
               }

               this.blockEntityRenderDispatcher.render(var17, var5, var1, (MultiBufferSource)var19);
               var1.popPose();
            }
         }
      }

      synchronized (this.globalBlockEntities) {
         for (BlockEntity var28 : this.globalBlockEntities) {
            BlockPos var29 = var28.getBlockPos();
            var1.pushPose();
            var1.translate((double)var29.getX() - var7, (double)var29.getY() - var9, (double)var29.getZ() - var11);
            this.blockEntityRenderDispatcher.render(var28, var5, var1, var2);
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

      while (var11.hasNext()) {
         Entry var12 = (Entry)var11.next();
         BlockPos var13 = BlockPos.of(var12.getLongKey());
         if (!(var13.distToCenterSqr(var5, var7, var9) > 1024.0)) {
            SortedSet var14 = (SortedSet)var12.getValue();
            if (var14 != null && !var14.isEmpty()) {
               int var15 = ((BlockDestructionProgress)var14.last()).getProgress();
               var1.pushPose();
               var1.translate((double)var13.getX() - var5, (double)var13.getY() - var7, (double)var13.getZ() - var9);
               PoseStack.Pose var16 = var1.last();
               SheetedDecalTextureGenerator var17 = new SheetedDecalTextureGenerator(var3.getBuffer(ModelBakery.DESTROY_TYPES.get(var15)), var16, 1.0F);
               this.minecraft.getBlockRenderer().renderBreakingTexture(this.level.getBlockState(var13), var13, this.level, var1, var17);
               var1.popPose();
            }
         }
      }
   }

   private void renderBlockOutline(Camera var1, MultiBufferSource.BufferSource var2, PoseStack var3, boolean var4) {
      if (this.minecraft.hitResult instanceof BlockHitResult var5) {
         if (var5.getType() != HitResult.Type.MISS) {
            BlockPos var11 = var5.getBlockPos();
            BlockState var7 = this.level.getBlockState(var11);
            if (!var7.isAir() && this.level.getWorldBorder().isWithinBounds(var11)) {
               boolean var8 = ItemBlockRenderTypes.getChunkRenderType(var7).sortOnUpload();
               if (var8 != var4) {
                  return;
               }

               VertexConsumer var9 = var2.getBuffer(RenderType.lines());
               Vec3 var10 = var1.getPosition();
               this.renderHitOutline(var3, var9, var1.getEntity(), var10.x, var10.y, var10.z, var11, var7);
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
      this.entityRenderDispatcher
         .render(var1, var11 - var2, var13 - var4, var15 - var6, var8, var9, var10, this.entityRenderDispatcher.getPackedLightCoords(var1, var8));
   }

   private void scheduleTranslucentSectionResort(Vec3 var1, RenderType var2) {
      if (this.lastTranslucentSortPos == null || !(var1.distanceToSqr(this.lastTranslucentSortPos) <= 1.0)) {
         this.minecraft.getProfiler().push("translucent_sort");
         int var3 = SectionPos.posToSectionCoord(var1.x);
         int var4 = SectionPos.posToSectionCoord(var1.y);
         int var5 = SectionPos.posToSectionCoord(var1.z);
         boolean var6 = this.lastTranslucentSortPos == null
            || var3 != SectionPos.posToSectionCoord(this.lastTranslucentSortPos.x)
            || var5 != SectionPos.posToSectionCoord(this.lastTranslucentSortPos.y)
            || var4 != SectionPos.posToSectionCoord(this.lastTranslucentSortPos.z);
         this.lastTranslucentSortPos = var1;
         int var7 = 0;
         ObjectListIterator var8 = this.visibleSections.iterator();

         while (var8.hasNext()) {
            SectionRenderDispatcher.RenderSection var9 = (SectionRenderDispatcher.RenderSection)var8.next();
            if (var7 < 15 && (var6 || var9.isAxisAlignedWith(var3, var4, var5)) && var9.resortTransparency(var2, this.sectionRenderDispatcher)) {
               var7++;
            }
         }

         this.minecraft.getProfiler().pop();
      }
   }

   private void renderSectionLayer(RenderType var1, double var2, double var4, double var6, Matrix4f var8, Matrix4f var9) {
      RenderSystem.assertOnRenderThread();
      this.minecraft.getProfiler().push(() -> "render_" + var1);
      boolean var10 = var1 != RenderType.translucent();
      ObjectListIterator var11 = this.visibleSections.listIterator(var10 ? 0 : this.visibleSections.size());
      var1.setupRenderState();
      ShaderInstance var12 = RenderSystem.getShader();
      var12.setDefaultUniforms(VertexFormat.Mode.QUADS, var8, var9, this.minecraft.getWindow());
      var12.apply();
      Uniform var13 = var12.MODEL_OFFSET;

      while (var10 ? var11.hasNext() : var11.hasPrevious()) {
         SectionRenderDispatcher.RenderSection var14 = var10
            ? (SectionRenderDispatcher.RenderSection)var11.next()
            : (SectionRenderDispatcher.RenderSection)var11.previous();
         if (!var14.getCompiled().isEmpty(var1)) {
            VertexBuffer var15 = var14.getBuffer(var1);
            BlockPos var16 = var14.getOrigin();
            if (var13 != null) {
               var13.set((float)((double)var16.getX() - var2), (float)((double)var16.getY() - var4), (float)((double)var16.getZ() - var6));
               var13.upload();
            }

            var15.bind();
            var15.draw();
         }
      }

      if (var13 != null) {
         var13.set(0.0F, 0.0F, 0.0F);
      }

      var12.clear();
      VertexBuffer.unbind();
      this.minecraft.getProfiler().pop();
      var1.clearRenderState();
   }

   public void captureFrustum() {
      this.captureFrustum = true;
   }

   public void killFrustum() {
      this.capturedFrustum = null;
   }

   public void tick() {
      if (this.level.tickRateManager().runsNormally()) {
         this.ticks++;
      }

      if (this.ticks % 20 == 0) {
         ObjectIterator var1 = this.destroyingBlocks.values().iterator();

         while (var1.hasNext()) {
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
            this.targets.main = var8.readsAndWrites(this.targets.main);
            var8.executes(() -> {
               RenderSystem.setShaderFog(var4);
               RenderStateShard.MAIN_TARGET.setupRenderState();
               PoseStack var5x = new PoseStack();
               if (var7 == DimensionSpecialEffects.SkyType.END) {
                  this.skyRenderer.renderEndSky(var5x);
               } else {
                  Tesselator var6x = Tesselator.getInstance();
                  float var7x = this.level.getSunAngle(var3);
                  float var8x = this.level.getTimeOfDay(var3);
                  float var9 = 1.0F - this.level.getRainLevel(var3);
                  float var10 = this.level.getStarBrightness(var3) * var9;
                  int var11 = var6.getSunriseOrSunsetColor(var8x);
                  int var12 = this.level.getMoonPhase();
                  int var13 = this.level.getSkyColor(this.minecraft.gameRenderer.getMainCamera().getPosition(), var3);
                  float var14 = ARGB.from8BitChannel(ARGB.red(var13));
                  float var15 = ARGB.from8BitChannel(ARGB.green(var13));
                  float var16 = ARGB.from8BitChannel(ARGB.blue(var13));
                  this.skyRenderer.renderSkyDisc(var14, var15, var16);
                  if (var6.isSunriseOrSunset(var8x)) {
                     this.skyRenderer.renderSunriseAndSunset(var5x, var6x, var7x, var11);
                  }

                  this.skyRenderer.renderSunMoonAndStars(var5x, var6x, var8x, var12, var9, var10, var4);
                  if (this.shouldRenderDarkDisc(var3)) {
                     this.skyRenderer.renderDarkDisc(var5x);
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
      return !(var1.getEntity() instanceof LivingEntity var2) ? false : var2.hasEffect(MobEffects.BLINDNESS) || var2.hasEffect(MobEffects.DARKNESS);
   }

   private void compileSections(Camera var1) {
      this.minecraft.getProfiler().push("populate_sections_to_compile");
      LevelLightEngine var2 = this.level.getLightEngine();
      RenderRegionCache var3 = new RenderRegionCache();
      BlockPos var4 = var1.getBlockPosition();
      ArrayList var5 = Lists.newArrayList();
      ObjectListIterator var6 = this.visibleSections.iterator();

      while (var6.hasNext()) {
         SectionRenderDispatcher.RenderSection var7 = (SectionRenderDispatcher.RenderSection)var6.next();
         SectionPos var8 = SectionPos.of(var7.getOrigin());
         if (var7.isDirty() && var2.lightOnInSection(var8)) {
            boolean var9 = false;
            if (this.minecraft.options.prioritizeChunkUpdates().get() == PrioritizeChunkUpdates.NEARBY) {
               BlockPos var10 = var7.getOrigin().offset(8, 8, 8);
               var9 = var10.distSqr(var4) < 768.0 || var7.isDirtyFromPlayer();
            } else if (this.minecraft.options.prioritizeChunkUpdates().get() == PrioritizeChunkUpdates.PLAYER_AFFECTED) {
               var9 = var7.isDirtyFromPlayer();
            }

            if (var9) {
               this.minecraft.getProfiler().push("build_near_sync");
               this.sectionRenderDispatcher.rebuildSectionSync(var7, var3);
               var7.setNotDirty();
               this.minecraft.getProfiler().pop();
            } else {
               var5.add(var7);
            }
         }
      }

      this.minecraft.getProfiler().popPush("upload");
      this.sectionRenderDispatcher.uploadAllPendingUploads();
      this.minecraft.getProfiler().popPush("schedule_async_compile");

      for (SectionRenderDispatcher.RenderSection var12 : var5) {
         var12.rebuildSectionAsync(this.sectionRenderDispatcher, var3);
         var12.setNotDirty();
      }

      this.minecraft.getProfiler().pop();
      this.scheduleTranslucentSectionResort(var1.getPosition(), RenderType.translucent());
   }

   private void renderHitOutline(PoseStack var1, VertexConsumer var2, Entity var3, double var4, double var6, double var8, BlockPos var10, BlockState var11) {
      ShapeRenderer.renderShape(
         var1,
         var2,
         var11.getShape(this.level, var10, CollisionContext.of(var3)),
         (double)var10.getX() - var4,
         (double)var10.getY() - var6,
         (double)var10.getZ() - var8,
         0.0F,
         0.0F,
         0.0F,
         0.4F
      );
   }

   public void blockChanged(BlockGetter var1, BlockPos var2, BlockState var3, BlockState var4, int var5) {
      this.setBlockDirty(var2, (var5 & 8) != 0);
   }

   private void setBlockDirty(BlockPos var1, boolean var2) {
      for (int var3 = var1.getZ() - 1; var3 <= var1.getZ() + 1; var3++) {
         for (int var4 = var1.getX() - 1; var4 <= var1.getX() + 1; var4++) {
            for (int var5 = var1.getY() - 1; var5 <= var1.getY() + 1; var5++) {
               this.setSectionDirty(SectionPos.blockToSectionCoord(var4), SectionPos.blockToSectionCoord(var5), SectionPos.blockToSectionCoord(var3), var2);
            }
         }
      }
   }

   public void setBlocksDirty(int var1, int var2, int var3, int var4, int var5, int var6) {
      for (int var7 = var3 - 1; var7 <= var6 + 1; var7++) {
         for (int var8 = var1 - 1; var8 <= var4 + 1; var8++) {
            for (int var9 = var2 - 1; var9 <= var5 + 1; var9++) {
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
      for (int var7 = var3; var7 <= var6; var7++) {
         for (int var8 = var1; var8 <= var4; var8++) {
            for (int var9 = var2; var9 <= var5; var9++) {
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
         var18.setDetail(
            "Parameters", () -> ParticleTypes.CODEC.encodeStart(this.level.registryAccess().createSerializationContext(NbtOps.INSTANCE), var1).toString()
         );
         var18.setDetail("Position", () -> CrashReportCategory.formatLocation(this.level, var4, var6, var8));
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
   private Particle addParticleInternal(
      ParticleOptions var1, boolean var2, boolean var3, double var4, double var6, double var8, double var10, double var12, double var14
   ) {
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
      ParticleStatus var2 = this.minecraft.options.particles().get();
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
         ((SortedSet)this.destructionProgress.computeIfAbsent(var5.getPos().asLong(), var0 -> Sets.newTreeSet())).add(var5);
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

   public void onChunkLoaded(ChunkPos var1) {
      this.sectionOcclusionGraph.onChunkLoaded(var1);
   }

   public void needsUpdate() {
      this.sectionOcclusionGraph.invalidate();
      this.cloudRenderer.markForRebuild();
   }

   public void updateGlobalBlockEntities(Collection<BlockEntity> var1, Collection<BlockEntity> var2) {
      synchronized (this.globalBlockEntities) {
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
      return this.targets.entityOutline != null ? this.targets.entityOutline.get() : null;
   }

   @Nullable
   public RenderTarget getTranslucentTarget() {
      return this.targets.translucent != null ? this.targets.translucent.get() : null;
   }

   @Nullable
   public RenderTarget getItemEntityTarget() {
      return this.targets.itemEntity != null ? this.targets.itemEntity.get() : null;
   }

   @Nullable
   public RenderTarget getParticlesTarget() {
      return this.targets.particles != null ? this.targets.particles.get() : null;
   }

   @Nullable
   public RenderTarget getWeatherTarget() {
      return this.targets.weather != null ? this.targets.weather.get() : null;
   }

   @Nullable
   public RenderTarget getCloudsTarget() {
      return this.targets.clouds != null ? this.targets.clouds.get() : null;
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

   public static class TransparencyShaderException extends RuntimeException {
      public TransparencyShaderException(String var1, Throwable var2) {
         super(var1, var2);
      }
   }
}
