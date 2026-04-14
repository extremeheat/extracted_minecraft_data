package net.minecraft.client.renderer.extract;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import net.minecraft.SharedConstants;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.SectionUpdateTracker;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.ViewArea;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.chunk.CompiledSectionMesh;
import net.minecraft.client.renderer.chunk.RenderRegionCache;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.debug.GameTestBlockHighlightRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.BlockBreakingRenderState;
import net.minecraft.client.renderer.state.level.BlockOutlineRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.client.renderer.state.level.SectionUpdateRenderState;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.SectionPos;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.SimpleGizmoCollector;
import net.minecraft.server.level.BlockDestructionProgress;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class LevelExtractor implements ResourceManagerReloadListener {
   private static final float CHUNK_VISIBILITY_THRESHOLD = 0.3F;
   private final Minecraft minecraft;
   private final LevelRenderer levelRenderer;
   private @Nullable ClientLevel level;
   private @Nullable SectionUpdateTracker sectionUpdateTracker;
   private final LevelRenderState levelRenderState;
   public final DebugRenderer debugRenderer = new DebugRenderer();
   public final GameTestBlockHighlightRenderer gameTestBlockHighlightRenderer = new GameTestBlockHighlightRenderer();
   private final SimpleGizmoCollector mainThreadGizmos = new SimpleGizmoCollector();
   private double prevCamRotX = 4.9E-324;
   private double prevCamRotY = 4.9E-324;
   private int lastViewDistance = -1;
   private boolean shouldInvalidateCompiledGeometry;
   private boolean shouldResetLevelRenderData;
   private boolean shouldResetChunkLayerSampler;
   private boolean shouldResetSkyRenderer;

   public LevelExtractor(final Minecraft minecraft, final LevelRenderState levelRenderState, final LevelRenderer levelRenderer) {
      super();
      this.minecraft = minecraft;
      this.levelRenderer = levelRenderer;
      this.levelRenderState = levelRenderState;
   }

   public void extract(final DeltaTracker deltaTracker, final Camera camera, final float deltaPartialTick) {
      if (this.minecraft.options.getEffectiveRenderDistance() != this.lastViewDistance) {
         this.allChanged();
      }

      Vec3 cameraPos = camera.position();
      if (this.sectionUpdateTracker != null) {
         this.sectionUpdateTracker.repositionCamera(SectionPos.of((Position)cameraPos));
      }

      if (this.shouldResetLevelRenderData) {
         this.levelRenderer.resetLevelRenderData();
         this.shouldResetLevelRenderData = false;
      }

      this.levelRenderState.reset();
      ProfilerFiller profiler = Profiler.get();
      profiler.push("level");
      this.levelRenderState.shouldResetChunkLayerSampler = this.shouldResetChunkLayerSampler;
      this.shouldResetChunkLayerSampler = false;
      this.levelRenderState.shouldResetSkyRenderer = this.shouldResetSkyRenderer;
      this.shouldResetSkyRenderer = false;
      this.levelRenderState.gameTime = this.level.getGameTime();
      Frustum cullFrustum = camera.getCullFrustum();
      profiler.push("prepareDispatchers");
      this.levelRenderer.blockEntityRenderDispatcher().prepare(cameraPos);
      this.levelRenderer.entityRenderDispatcher().prepare(camera, this.minecraft.crosshairPickEntity);
      if (this.shouldInvalidateCompiledGeometry) {
         this.levelRenderer.invalidateCompiledGeometry(this.level, this.minecraft.options, camera, this.minecraft.getBlockColors());
         this.shouldInvalidateCompiledGeometry = false;
      } else if (camera.getCapturedFrustum() == null) {
         double camRotX = Math.floor((double)(camera.xRot() / 2.0F));
         double camRotY = Math.floor((double)(camera.yRot() / 2.0F));
         if (this.levelRenderer.sectionOcclusionGraph().consumeFrustumUpdate() || camRotX != this.prevCamRotX || camRotY != this.prevCamRotY) {
            profiler.popPush("applyFrustum");
            this.applyFrustum(cullFrustum);
            this.prevCamRotX = camRotX;
            this.prevCamRotY = camRotY;
         }
      }

      if (this.sectionUpdateTracker != null && this.level != null) {
         ClientChunkCache chunkCache = this.level.getChunkSource();
         this.levelRenderState.addedEmptySections = chunkCache.addedEmptySections();
         this.levelRenderState.removedEmptySections = chunkCache.removedEmptySections();
         chunkCache.flipEmptySectionUpdates();
         profiler.popPush("sectionUpdates");
         RenderRegionCache cache = new RenderRegionCache();
         ObjectListIterator var15 = this.levelRenderer.visibleSections().iterator();

         while(var15.hasNext()) {
            SectionRenderDispatcher.RenderSection section = (SectionRenderDispatcher.RenderSection)var15.next();
            SectionUpdateTracker.SectionDirtyState dirtyState = this.sectionUpdateTracker.getDirtyState(section.getSectionNode());
            if (dirtyState != null && dirtyState.isDirty() && (section.sectionMesh.get() != CompiledSectionMesh.UNCOMPILED || this.sectionUpdateTracker.hasAllNeighbors(this.level, section.getSectionNode()))) {
               this.levelRenderState.sectionUpdateRenderStates.add(new SectionUpdateRenderState(section.getSectionNode(), dirtyState.isDirtyFromPlayer(), cache.createRegion(this.level, section.getSectionNode())));
               dirtyState.setNotDirty();
            }
         }
      }

      profiler.popPush("entities");
      this.extractVisibleEntities(camera, cullFrustum, deltaTracker, this.levelRenderState);
      profiler.popPush("blockEntities");
      this.extractVisibleBlockEntities(camera, deltaPartialTick, this.levelRenderState);
      profiler.popPush("blockOutline");
      this.extractBlockOutline(camera, this.levelRenderState);
      profiler.popPush("blockBreaking");
      this.extractBlockDestroyAnimation(camera, this.levelRenderState);
      profiler.popPush("weather");
      this.levelRenderer.weatherEffectRenderer().extractRenderState(this.level, deltaPartialTick, cameraPos, this.levelRenderState.weatherRenderState);
      SkyRenderer skyRenderer = this.levelRenderer.skyRenderer();
      if (skyRenderer != null) {
         profiler.popPush("sky");
         skyRenderer.extractRenderState(this.level, deltaPartialTick, camera, this.levelRenderState.skyRenderState);
      }

      profiler.popPush("border");
      this.levelRenderer.worldBorderRenderer().extract(this.level.getWorldBorder(), deltaPartialTick, cameraPos, (double)(this.minecraft.options.getEffectiveRenderDistance() * 16), this.levelRenderState.worldBorderRenderState);
      profiler.popPush("particles");
      this.minecraft.particleEngine.extract(this.levelRenderState.particlesRenderState, (new Frustum(cullFrustum)).offset(-3.0F), camera, deltaPartialTick);
      profiler.popPush("cloud");
      this.levelRenderState.cloudColor = (Integer)camera.attributeProbe().getValue(EnvironmentAttributes.CLOUD_COLOR, deltaPartialTick);
      if (ARGB.alpha(this.levelRenderState.cloudColor) > 0) {
         this.levelRenderState.cloudHeight = (Float)camera.attributeProbe().getValue(EnvironmentAttributes.CLOUD_HEIGHT, deltaPartialTick);
      }

      profiler.popPush("debug");
      this.debugRenderer.emitGizmos(cullFrustum, cameraPos.x, cameraPos.y, cameraPos.z, deltaTracker.getGameTimeDeltaPartialTick(false));
      this.gameTestBlockHighlightRenderer.emitGizmos();
      this.levelRenderState.render3dCrosshair = this.minecraft.debugEntries.isCurrentlyEnabled(DebugScreenEntries.THREE_DIMENSIONAL_CROSSHAIR);
      ClientPacketListener connection = this.minecraft.getConnection();
      if (connection != null) {
         this.levelRenderState.playerCompiledSectionCallback = connection.getPlayerCompiledSectionCallback();
      }

      this.levelRenderState.shouldShowEntityOutlines = this.shouldShowEntityOutlines(camera);
      this.extractGizmos();
      profiler.pop();
      profiler.pop();
   }

   private void extractVisibleEntities(final Camera camera, final Frustum frustum, final DeltaTracker deltaTracker, final LevelRenderState output) {
      Vec3 cameraPos = camera.position();
      double camX = cameraPos.x();
      double camY = cameraPos.y();
      double camZ = cameraPos.z();
      TickRateManager tickRateManager = this.minecraft.level.tickRateManager();
      boolean shouldShowEntityOutlines = this.shouldShowEntityOutlines(camera);
      Entity.setViewScale(Mth.clamp((double)this.minecraft.options.getEffectiveRenderDistance() / 8.0, 1.0, 2.5) * (Double)this.minecraft.options.entityDistanceScaling().get());
      EntityRenderDispatcher entityRenderDispatcher = this.levelRenderer.entityRenderDispatcher();

      for(Entity entity : this.level.entitiesForRendering()) {
         if (entityRenderDispatcher.shouldRender(entity, frustum, camX, camY, camZ) || entity.hasIndirectPassenger(this.minecraft.player)) {
            BlockPos blockPos = entity.blockPosition();
            if ((this.level.isOutsideBuildHeight(blockPos.getY()) || this.levelRenderer.isSectionCompiledAndVisible(blockPos)) && (entity != camera.entity() || camera.isDetached() || camera.entity() instanceof LivingEntity && ((LivingEntity)camera.entity()).isSleeping()) && (!(entity instanceof LocalPlayer) || camera.entity() == entity)) {
               if (entity.tickCount == 0) {
                  entity.xOld = entity.getX();
                  entity.yOld = entity.getY();
                  entity.zOld = entity.getZ();
               }

               float partialEntity = deltaTracker.getGameTimeDeltaPartialTick(!tickRateManager.isEntityFrozen(entity));
               EntityRenderState state = this.extractEntity(entity, partialEntity);
               output.entityRenderStates.add(state);
               if (state.appearsGlowing() && shouldShowEntityOutlines) {
                  output.haveGlowingEntities = true;
               }
            }
         }
      }

      output.lastEntityRenderStateCount = output.entityRenderStates.size();
   }

   private EntityRenderState extractEntity(final Entity entity, final float partialTickTime) {
      return this.levelRenderer.entityRenderDispatcher().extractEntity(entity, partialTickTime);
   }

   private void extractVisibleBlockEntities(final Camera camera, final float deltaPartialTick, final LevelRenderState levelRenderState) {
      Vec3 cameraPos = camera.position();
      double camX = cameraPos.x();
      double camY = cameraPos.y();
      double camZ = cameraPos.z();
      PoseStack poseStack = new PoseStack();
      Iterator<BlockEntity> iterator = this.levelRenderer.visibleSections().iterator();

      while(iterator.hasNext()) {
         SectionRenderDispatcher.RenderSection section = (SectionRenderDispatcher.RenderSection)iterator.next();
         List<BlockEntity> renderableBlockEntities = section.getSectionMesh().getRenderableBlockEntities();
         if (!renderableBlockEntities.isEmpty() && !(section.getVisibility(Util.getMillis()) < 0.3F)) {
            for(BlockEntity blockEntity : renderableBlockEntities) {
               BlockPos blockPos = blockEntity.getBlockPos();
               SortedSet<BlockDestructionProgress> progresses = (SortedSet)this.level.destructionProgress().get(blockPos.asLong());
               ModelFeatureRenderer.CrumblingOverlay breakProgress;
               if (progresses != null && !progresses.isEmpty()) {
                  poseStack.pushPose();
                  poseStack.translate((double)blockPos.getX() - camX, (double)blockPos.getY() - camY, (double)blockPos.getZ() - camZ);
                  breakProgress = new ModelFeatureRenderer.CrumblingOverlay(((BlockDestructionProgress)progresses.last()).getProgress(), poseStack.last());
                  poseStack.popPose();
               } else {
                  breakProgress = null;
               }

               BlockEntityRenderState state = this.levelRenderer.blockEntityRenderDispatcher().tryExtractRenderState(blockEntity, deltaPartialTick, breakProgress, false);
               if (state != null) {
                  levelRenderState.blockEntityRenderStates.add(state);
               }
            }
         }
      }

      iterator = this.level.getGloballyRenderedBlockEntities().iterator();

      while(iterator.hasNext()) {
         BlockEntity blockEntity = (BlockEntity)iterator.next();
         if (blockEntity.isRemoved()) {
            iterator.remove();
         } else {
            BlockEntityRenderState state = this.levelRenderer.blockEntityRenderDispatcher().tryExtractRenderState(blockEntity, deltaPartialTick, (ModelFeatureRenderer.CrumblingOverlay)null, true);
            if (state != null) {
               levelRenderState.blockEntityRenderStates.add(state);
            }
         }
      }

   }

   private void extractBlockDestroyAnimation(final Camera camera, final LevelRenderState levelRenderState) {
      Vec3 cameraPos = camera.position();
      double camX = cameraPos.x();
      double camY = cameraPos.y();
      double camZ = cameraPos.z();
      levelRenderState.blockBreakingRenderStates.clear();
      ObjectIterator var10 = this.level.destructionProgress().long2ObjectEntrySet().iterator();

      while(var10.hasNext()) {
         Long2ObjectMap.Entry<SortedSet<BlockDestructionProgress>> entry = (Long2ObjectMap.Entry)var10.next();
         BlockPos pos = BlockPos.of(entry.getLongKey());
         if (!(pos.distToCenterSqr(camX, camY, camZ) > 1024.0)) {
            SortedSet<BlockDestructionProgress> progresses = (SortedSet)entry.getValue();
            if (progresses != null && !progresses.isEmpty()) {
               int progress = ((BlockDestructionProgress)progresses.last()).getProgress();
               levelRenderState.blockBreakingRenderStates.add(new BlockBreakingRenderState(pos, this.level.getBlockState(pos), progress));
            }
         }
      }

   }

   private void extractBlockOutline(final Camera camera, final LevelRenderState levelRenderState) {
      levelRenderState.blockOutlineRenderState = null;
      HitResult var4 = this.minecraft.hitResult;
      if (var4 instanceof BlockHitResult blockHitResult) {
         if (blockHitResult.getType() != HitResult.Type.MISS) {
            BlockPos pos = blockHitResult.getBlockPos();
            BlockState state = this.level.getBlockState(pos);
            if (!state.isAir() && this.level.getWorldBorder().isWithinBounds(pos)) {
               BlockStateModel blockStateModel = this.minecraft.getModelManager().getBlockStateModelSet().get(state);
               boolean isBlockTranslucent = blockStateModel.hasMaterialFlag(1);
               boolean highContrast = (Boolean)this.minecraft.options.highContrastBlockOutline().get();
               CollisionContext context = CollisionContext.of(camera.entity());
               VoxelShape shape = state.getShape(this.level, pos, context);
               if (SharedConstants.DEBUG_SHAPES) {
                  VoxelShape collisionShape = state.getCollisionShape(this.level, pos, context);
                  VoxelShape occlusionShape = state.getOcclusionShape();
                  VoxelShape interactionShape = state.getInteractionShape(this.level, pos);
                  levelRenderState.blockOutlineRenderState = new BlockOutlineRenderState(pos, isBlockTranslucent, highContrast, shape, collisionShape, occlusionShape, interactionShape);
               } else {
                  levelRenderState.blockOutlineRenderState = new BlockOutlineRenderState(pos, isBlockTranslucent, highContrast, shape);
               }
            }

         }
      }
   }

   private void extractGizmos() {
      this.mainThreadGizmos.addTemporaryGizmos(Minecraft.getInstance().getPerTickGizmos());
      IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();
      if (server != null) {
         this.mainThreadGizmos.addTemporaryGizmos(server.getPerTickGizmos());
      }

      this.levelRenderer.addMainThreadGizmos(this.mainThreadGizmos.drainGizmos());
   }

   private void applyFrustum(final Frustum frustum) {
      if (!Minecraft.getInstance().isSameThread()) {
         throw new IllegalStateException("applyFrustum called from wrong thread: " + Thread.currentThread().getName());
      } else {
         this.levelRenderer.clearVisibleSections();
         this.levelRenderer.sectionOcclusionGraph().addSectionsInFrustum(frustum, this.levelRenderer.visibleSections(), this.levelRenderer.nearbyVisibleSections());
      }
   }

   private boolean shouldShowEntityOutlines(final Camera camera) {
      return !camera.isPanoramicMode() && this.minecraft.player != null;
   }

   public void onResourceManagerReload(final ResourceManager resourceManager) {
      this.shouldResetSkyRenderer = true;
   }

   public void setLevel(final @Nullable ClientLevel level) {
      this.level = level;
      if (level != null) {
         this.allChanged();
      } else {
         this.levelRenderer.entityRenderDispatcher().resetCamera();
         this.sectionUpdateTracker = null;
      }

      this.shouldResetLevelRenderData = true;
      this.gameTestBlockHighlightRenderer.clear();
   }

   public void allChanged() {
      if (this.level != null) {
         this.level.clearTintCaches();
         Options options = this.minecraft.options;
         this.lastViewDistance = options.getEffectiveRenderDistance();
         this.sectionUpdateTracker = new SectionUpdateTracker(this.level, this.lastViewDistance);
         Camera camera = this.minecraft.gameRenderer.mainCamera();
         SectionPos cameraSectionPos = SectionPos.of((Position)camera.position());
         this.sectionUpdateTracker.repositionCamera(cameraSectionPos);
         this.shouldInvalidateCompiledGeometry = true;
      }
   }

   public void resetSampler() {
      this.shouldResetChunkLayerSampler = true;
   }

   public void blockChanged(final BlockPos pos, final @Block.UpdateFlags int updateFlags) {
      this.setBlockDirty(pos, (updateFlags & 8) != 0);
   }

   private void setBlockDirty(final BlockPos pos, final boolean playerChanged) {
      for(int z = pos.getZ() - 1; z <= pos.getZ() + 1; ++z) {
         for(int x = pos.getX() - 1; x <= pos.getX() + 1; ++x) {
            for(int y = pos.getY() - 1; y <= pos.getY() + 1; ++y) {
               this.setSectionDirty(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(y), SectionPos.blockToSectionCoord(z), playerChanged);
            }
         }
      }

   }

   public void setBlocksDirty(final int x0, final int y0, final int z0, final int x1, final int y1, final int z1) {
      for(int z = z0 - 1; z <= z1 + 1; ++z) {
         for(int x = x0 - 1; x <= x1 + 1; ++x) {
            for(int y = y0 - 1; y <= y1 + 1; ++y) {
               this.setSectionDirty(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(y), SectionPos.blockToSectionCoord(z));
            }
         }
      }

   }

   public void setBlockDirty(final BlockPos pos, final BlockState oldState, final BlockState newState) {
      if (this.minecraft.getModelManager().requiresRender(oldState, newState)) {
         this.setBlocksDirty(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
      }

   }

   public void setSectionDirtyWithNeighbors(final int sectionX, final int sectionY, final int sectionZ) {
      this.setSectionRangeDirty(sectionX - 1, sectionY - 1, sectionZ - 1, sectionX + 1, sectionY + 1, sectionZ + 1);
   }

   public void setSectionRangeDirty(final int minSectionX, final int minSectionY, final int minSectionZ, final int maxSectionX, final int maxSectionY, final int maxSectionZ) {
      for(int z = minSectionZ; z <= maxSectionZ; ++z) {
         for(int x = minSectionX; x <= maxSectionX; ++x) {
            for(int y = minSectionY; y <= maxSectionY; ++y) {
               this.setSectionDirty(x, y, z);
            }
         }
      }

   }

   public void setSectionDirty(final int sectionX, final int sectionY, final int sectionZ) {
      this.setSectionDirty(sectionX, sectionY, sectionZ, false);
   }

   private void setSectionDirty(final int sectionX, final int sectionY, final int sectionZ, final boolean playerChanged) {
      this.sectionUpdateTracker.setDirty(sectionX, sectionY, sectionZ, playerChanged);
   }

   public Gizmos.TemporaryCollection collectPerFrameMainThreadGizmos() {
      return Gizmos.withCollector(this.mainThreadGizmos);
   }

   public int countRenderedSections() {
      int rendered = 0;
      ObjectListIterator var2 = this.levelRenderer.visibleSections().iterator();

      while(var2.hasNext()) {
         SectionRenderDispatcher.RenderSection section = (SectionRenderDispatcher.RenderSection)var2.next();
         if (section.getSectionMesh().hasRenderableLayers()) {
            ++rendered;
         }
      }

      return rendered;
   }

   @VisibleForDebug
   public @Nullable String sectionStatistics() {
      ViewArea viewArea = this.levelRenderer.viewArea();
      if (viewArea == null) {
         return null;
      } else {
         int totalSections = viewArea.size();
         int rendered = this.countRenderedSections();
         SectionRenderDispatcher sectionRenderDispatcher = this.levelRenderer.sectionRenderDispatcher();
         return String.format(Locale.ROOT, "C: %d/%d %sD: %d, %s", rendered, totalSections, this.minecraft.smartCull ? "(s) " : "", this.lastViewDistance, sectionRenderDispatcher == null ? "null" : sectionRenderDispatcher.getStats());
      }
   }

   @VisibleForDebug
   public @Nullable String entityStatistics() {
      if (this.level == null) {
         return null;
      } else {
         int var10000 = this.levelRenderState.lastEntityRenderStateCount;
         return "E: " + var10000 + "/" + this.level.getEntityCount() + ", SD: " + this.level.getServerSimulationDistance();
      }
   }

   @VisibleForDebug
   public double totalSections() {
      return this.sectionUpdateTracker == null ? 0.0 : (double)this.sectionUpdateTracker.size();
   }

   @VisibleForDebug
   public double lastViewDistance() {
      return (double)this.lastViewDistance;
   }
}
