package net.minecraft.client.renderer.debug;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.util.Mth;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class DebugRenderer {
   private final List<SimpleDebugRenderer> renderers = new ArrayList();
   private long lastDebugEntriesVersion;

   public DebugRenderer() {
      super();
      this.refreshRendererList();
   }

   public void refreshRendererList() {
      Minecraft var1 = Minecraft.getInstance();
      this.renderers.clear();
      if (var1.debugEntries.isCurrentlyEnabled(DebugScreenEntries.CHUNK_BORDERS)) {
         this.renderers.add(new ChunkBorderRenderer(var1));
      }

      if (var1.debugEntries.isCurrentlyEnabled(DebugScreenEntries.CHUNK_SECTION_OCTREE)) {
         this.renderers.add(new OctreeDebugRenderer(var1));
      }

      if (SharedConstants.DEBUG_PATHFINDING) {
         this.renderers.add(new PathfindingRenderer());
      }

      if (var1.debugEntries.isCurrentlyEnabled(DebugScreenEntries.VISUALIZE_WATER_LEVELS)) {
         this.renderers.add(new WaterDebugRenderer(var1));
      }

      if (var1.debugEntries.isCurrentlyEnabled(DebugScreenEntries.VISUALIZE_HEIGHTMAP)) {
         this.renderers.add(new HeightMapRenderer(var1));
      }

      if (var1.debugEntries.isCurrentlyEnabled(DebugScreenEntries.VISUALIZE_COLLISION_BOXES)) {
         this.renderers.add(new CollisionBoxRenderer(var1));
      }

      if (var1.debugEntries.isCurrentlyEnabled(DebugScreenEntries.VISUALIZE_ENTITY_SUPPORTING_BLOCKS)) {
         this.renderers.add(new SupportBlockRenderer(var1));
      }

      if (SharedConstants.DEBUG_NEIGHBORSUPDATE) {
         this.renderers.add(new NeighborsUpdateRenderer());
      }

      if (SharedConstants.DEBUG_EXPERIMENTAL_REDSTONEWIRE_UPDATE_ORDER) {
         this.renderers.add(new RedstoneWireOrientationsRenderer());
      }

      if (SharedConstants.DEBUG_STRUCTURES) {
         this.renderers.add(new StructureRenderer());
      }

      if (var1.debugEntries.isCurrentlyEnabled(DebugScreenEntries.VISUALIZE_BLOCK_LIGHT_LEVELS) || var1.debugEntries.isCurrentlyEnabled(DebugScreenEntries.VISUALIZE_SKY_LIGHT_LEVELS)) {
         this.renderers.add(new LightDebugRenderer(var1, var1.debugEntries.isCurrentlyEnabled(DebugScreenEntries.VISUALIZE_BLOCK_LIGHT_LEVELS), var1.debugEntries.isCurrentlyEnabled(DebugScreenEntries.VISUALIZE_SKY_LIGHT_LEVELS)));
      }

      if (var1.debugEntries.isCurrentlyEnabled(DebugScreenEntries.VISUALIZE_SOLID_FACES)) {
         this.renderers.add(new SolidFaceRenderer(var1));
      }

      if (SharedConstants.DEBUG_VILLAGE_SECTIONS) {
         this.renderers.add(new VillageSectionsDebugRenderer());
      }

      if (SharedConstants.DEBUG_BRAIN) {
         this.renderers.add(new BrainDebugRenderer(var1));
      }

      if (SharedConstants.DEBUG_POI) {
         this.renderers.add(new PoiDebugRenderer(new BrainDebugRenderer(var1)));
      }

      if (SharedConstants.DEBUG_BEES) {
         this.renderers.add(new BeeDebugRenderer(var1));
      }

      if (SharedConstants.DEBUG_RAIDS) {
         this.renderers.add(new RaidDebugRenderer(var1));
      }

      if (SharedConstants.DEBUG_GOAL_SELECTOR) {
         this.renderers.add(new GoalSelectorDebugRenderer(var1));
      }

      if (var1.debugEntries.isCurrentlyEnabled(DebugScreenEntries.VISUALIZE_CHUNKS_ON_SERVER)) {
         this.renderers.add(new ChunkDebugRenderer(var1));
      }

      if (SharedConstants.DEBUG_GAME_EVENT_LISTENERS) {
         this.renderers.add(new GameEventListenerRenderer());
      }

      if (var1.debugEntries.isCurrentlyEnabled(DebugScreenEntries.VISUALIZE_SKY_LIGHT_SECTIONS)) {
         this.renderers.add(new LightSectionDebugRenderer(var1, LightLayer.SKY));
      }

      if (SharedConstants.DEBUG_BREEZE_MOB) {
         this.renderers.add(new BreezeDebugRenderer(var1));
      }

      if (SharedConstants.DEBUG_ENTITY_BLOCK_INTERSECTION) {
         this.renderers.add(new EntityBlockIntersectionDebugRenderer());
      }

      if (var1.debugEntries.isCurrentlyEnabled(DebugScreenEntries.ENTITY_HITBOXES)) {
         this.renderers.add(new EntityHitboxDebugRenderer(var1));
      }

      this.renderers.add(new ChunkCullingDebugRenderer(var1));
   }

   public void emitGizmos(Frustum var1, double var2, double var4, double var6, float var8) {
      Minecraft var9 = Minecraft.getInstance();
      DebugValueAccess var10 = var9.getConnection().createDebugValueAccess();
      if (var9.debugEntries.getCurrentlyEnabledVersion() != this.lastDebugEntriesVersion) {
         this.lastDebugEntriesVersion = var9.debugEntries.getCurrentlyEnabledVersion();
         this.refreshRendererList();
      }

      for(SimpleDebugRenderer var12 : this.renderers) {
         var12.emitGizmos(var2, var4, var6, var10, var1, var8);
      }

   }

   public static Optional<Entity> getTargetedEntity(@Nullable Entity var0, int var1) {
      if (var0 == null) {
         return Optional.empty();
      } else {
         Vec3 var2 = var0.getEyePosition();
         Vec3 var3 = var0.getViewVector(1.0F).scale((double)var1);
         Vec3 var4 = var2.add(var3);
         AABB var5 = var0.getBoundingBox().expandTowards(var3).inflate(1.0);
         int var6 = var1 * var1;
         EntityHitResult var7 = ProjectileUtil.getEntityHitResult(var0, var2, var4, var5, EntitySelector.CAN_BE_PICKED, (double)var6);
         if (var7 == null) {
            return Optional.empty();
         } else {
            return var2.distanceToSqr(var7.getLocation()) > (double)var6 ? Optional.empty() : Optional.of(var7.getEntity());
         }
      }
   }

   private static Vec3 mixColor(float var0) {
      float var1 = 5.99999F;
      int var2 = (int)(Mth.clamp(var0, 0.0F, 1.0F) * 5.99999F);
      float var3 = var0 * 5.99999F - (float)var2;
      Vec3 var10000;
      switch (var2) {
         case 0 -> var10000 = new Vec3(1.0, (double)var3, 0.0);
         case 1 -> var10000 = new Vec3((double)(1.0F - var3), 1.0, 0.0);
         case 2 -> var10000 = new Vec3(0.0, 1.0, (double)var3);
         case 3 -> var10000 = new Vec3(0.0, 1.0 - (double)var3, 1.0);
         case 4 -> var10000 = new Vec3((double)var3, 0.0, 1.0);
         case 5 -> var10000 = new Vec3(1.0, 0.0, 1.0 - (double)var3);
         default -> throw new IllegalStateException("Unexpected value: " + var2);
      }

      return var10000;
   }

   private static Vec3 shiftHue(float var0, float var1, float var2, float var3) {
      Vec3 var4 = mixColor(var3).scale((double)var0);
      Vec3 var5 = mixColor((var3 + 0.33333334F) % 1.0F).scale((double)var1);
      Vec3 var6 = mixColor((var3 + 0.6666667F) % 1.0F).scale((double)var2);
      Vec3 var7 = var4.add(var5).add(var6);
      double var8 = Math.max(Math.max(1.0, var7.x), Math.max(var7.y, var7.z));
      return new Vec3(var7.x / var8, var7.y / var8, var7.z / var8);
   }

   public interface SimpleDebugRenderer {
      void emitGizmos(double var1, double var3, double var5, DebugValueAccess var7, Frustum var8, float var9);
   }
}
