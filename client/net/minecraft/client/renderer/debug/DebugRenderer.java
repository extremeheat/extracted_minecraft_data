package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Quaternionfc;

public class DebugRenderer {
   public final PathfindingRenderer pathfindingRenderer = new PathfindingRenderer();
   public final SimpleDebugRenderer waterDebugRenderer;
   public final SimpleDebugRenderer chunkBorderRenderer;
   public final SimpleDebugRenderer heightMapRenderer;
   public final SimpleDebugRenderer collisionBoxRenderer;
   public final SimpleDebugRenderer supportBlockRenderer;
   public final NeighborsUpdateRenderer neighborsUpdateRenderer;
   public final RedstoneWireOrientationsRenderer redstoneWireOrientationsRenderer;
   public final StructureRenderer structureRenderer;
   public final SimpleDebugRenderer lightDebugRenderer;
   public final SimpleDebugRenderer solidFaceRenderer;
   public final SimpleDebugRenderer chunkRenderer;
   public final BrainDebugRenderer brainDebugRenderer;
   public final PoiDebugRenderer poiDebugRenderer;
   public final VillageSectionsDebugRenderer villageSectionsDebugRenderer;
   public final BeeDebugRenderer beeDebugRenderer;
   public final RaidDebugRenderer raidDebugRenderer;
   public final GoalSelectorDebugRenderer goalSelectorRenderer;
   public final GameTestDebugRenderer gameTestDebugRenderer;
   public final EntityBlockIntersectionDebugRenderer entityBlockIntersectionRenderer;
   public final GameEventListenerRenderer gameEventListenerRenderer;
   public final LightSectionDebugRenderer skyLightSectionDebugRenderer;
   public final BreezeDebugRenderer breezeDebugRenderer;
   public final ChunkCullingDebugRenderer chunkCullingDebugRenderer;
   public final OctreeDebugRenderer octreeDebugRenderer;

   public DebugRenderer(Minecraft var1) {
      super();
      this.waterDebugRenderer = new WaterDebugRenderer(var1);
      this.chunkBorderRenderer = new ChunkBorderRenderer(var1);
      this.heightMapRenderer = new HeightMapRenderer(var1);
      this.collisionBoxRenderer = new CollisionBoxRenderer(var1);
      this.supportBlockRenderer = new SupportBlockRenderer(var1);
      this.neighborsUpdateRenderer = new NeighborsUpdateRenderer();
      this.redstoneWireOrientationsRenderer = new RedstoneWireOrientationsRenderer();
      this.structureRenderer = new StructureRenderer();
      this.lightDebugRenderer = new LightDebugRenderer(var1);
      this.solidFaceRenderer = new SolidFaceRenderer(var1);
      this.chunkRenderer = new ChunkDebugRenderer(var1);
      this.brainDebugRenderer = new BrainDebugRenderer(var1);
      this.poiDebugRenderer = new PoiDebugRenderer(this.brainDebugRenderer);
      this.villageSectionsDebugRenderer = new VillageSectionsDebugRenderer();
      this.beeDebugRenderer = new BeeDebugRenderer(var1);
      this.raidDebugRenderer = new RaidDebugRenderer(var1);
      this.goalSelectorRenderer = new GoalSelectorDebugRenderer(var1);
      this.gameTestDebugRenderer = new GameTestDebugRenderer();
      this.entityBlockIntersectionRenderer = new EntityBlockIntersectionDebugRenderer();
      this.gameEventListenerRenderer = new GameEventListenerRenderer();
      this.skyLightSectionDebugRenderer = new LightSectionDebugRenderer(var1, LightLayer.SKY);
      this.breezeDebugRenderer = new BreezeDebugRenderer(var1);
      this.chunkCullingDebugRenderer = new ChunkCullingDebugRenderer(var1);
      this.octreeDebugRenderer = new OctreeDebugRenderer(var1);
   }

   public void clear() {
      this.pathfindingRenderer.clear();
      this.waterDebugRenderer.clear();
      this.chunkBorderRenderer.clear();
      this.heightMapRenderer.clear();
      this.collisionBoxRenderer.clear();
      this.supportBlockRenderer.clear();
      this.neighborsUpdateRenderer.clear();
      this.structureRenderer.clear();
      this.lightDebugRenderer.clear();
      this.solidFaceRenderer.clear();
      this.chunkRenderer.clear();
      this.brainDebugRenderer.clear();
      this.poiDebugRenderer.clear();
      this.villageSectionsDebugRenderer.clear();
      this.beeDebugRenderer.clear();
      this.raidDebugRenderer.clear();
      this.goalSelectorRenderer.clear();
      this.gameTestDebugRenderer.clear();
      this.gameEventListenerRenderer.clear();
      this.skyLightSectionDebugRenderer.clear();
      this.chunkCullingDebugRenderer.clear();
   }

   public void render(PoseStack var1, Frustum var2, MultiBufferSource.BufferSource var3, double var4, double var6, double var8) {
      Minecraft var10 = Minecraft.getInstance();
      DebugValueAccess var11 = var10.getConnection().createDebugValueAccess();
      if (SharedConstants.DEBUG_PATHFINDING) {
         this.pathfindingRenderer.render(var1, var3, var4, var6, var8, var11);
      }

      if (var10.debugEntries.isCurrentlyEnabled(DebugScreenEntries.CHUNK_BORDERS) && !var10.showOnlyReducedInfo()) {
         this.chunkBorderRenderer.render(var1, var3, var4, var6, var8, var11);
      }

      if (var10.debugEntries.isCurrentlyEnabled(DebugScreenEntries.CHUNK_SECTION_OCTREE)) {
         this.octreeDebugRenderer.render(var1, var2, var3, var4, var6, var8);
      }

      if (SharedConstants.DEBUG_WATER) {
         this.waterDebugRenderer.render(var1, var3, var4, var6, var8, var11);
      }

      if (SharedConstants.DEBUG_HEIGHTMAP) {
         this.heightMapRenderer.render(var1, var3, var4, var6, var8, var11);
      }

      if (SharedConstants.DEBUG_COLLISION) {
         this.collisionBoxRenderer.render(var1, var3, var4, var6, var8, var11);
      }

      if (SharedConstants.DEBUG_SUPPORT_BLOCKS) {
         this.supportBlockRenderer.render(var1, var3, var4, var6, var8, var11);
      }

      if (SharedConstants.DEBUG_NEIGHBORSUPDATE) {
         this.neighborsUpdateRenderer.render(var1, var3, var4, var6, var8, var11);
      }

      if (SharedConstants.DEBUG_EXPERIMENTAL_REDSTONEWIRE_UPDATE_ORDER) {
         this.redstoneWireOrientationsRenderer.render(var1, var3, var4, var6, var8, var11);
      }

      if (SharedConstants.DEBUG_STRUCTURES) {
         this.structureRenderer.render(var1, var3, var4, var6, var8, var11);
      }

      if (SharedConstants.DEBUG_LIGHT) {
         this.lightDebugRenderer.render(var1, var3, var4, var6, var8, var11);
      }

      if (SharedConstants.DEBUG_SOLID_FACE) {
         this.solidFaceRenderer.render(var1, var3, var4, var6, var8, var11);
      }

      if (SharedConstants.DEBUG_VILLAGE_SECTIONS) {
         this.villageSectionsDebugRenderer.render(var1, var3, var4, var6, var8, var11);
      }

      if (SharedConstants.DEBUG_BRAIN) {
         this.brainDebugRenderer.render(var1, var3, var4, var6, var8, var11);
      }

      if (SharedConstants.DEBUG_POI) {
         this.poiDebugRenderer.render(var1, var3, var4, var6, var8, var11);
      }

      if (SharedConstants.DEBUG_BEES) {
         this.beeDebugRenderer.render(var1, var3, var4, var6, var8, var11);
      }

      if (SharedConstants.DEBUG_RAIDS) {
         this.raidDebugRenderer.render(var1, var3, var4, var6, var8, var11);
      }

      if (SharedConstants.DEBUG_GOAL_SELECTOR) {
         this.goalSelectorRenderer.render(var1, var3, var4, var6, var8, var11);
      }

      if (SharedConstants.DEBUG_CHUNKS) {
         this.chunkRenderer.render(var1, var3, var4, var6, var8, var11);
      }

      if (SharedConstants.DEBUG_GAME_EVENT_LISTENERS) {
         this.gameEventListenerRenderer.render(var1, var3, var4, var6, var8, var11);
      }

      if (SharedConstants.DEBUG_SKY_LIGHT_SECTIONS) {
         this.skyLightSectionDebugRenderer.render(var1, var3, var4, var6, var8, var11);
      }

      if (SharedConstants.DEBUG_BREEZE_MOB) {
         this.breezeDebugRenderer.render(var1, var3, var4, var6, var8, var11);
      }

      if (SharedConstants.DEBUG_ENTITY_BLOCK_INTERSECTION) {
         this.entityBlockIntersectionRenderer.render(var1, var3, var4, var6, var8, var11);
      }

      this.gameTestDebugRenderer.render(var1, var3, var4, var6, var8, var11);
   }

   public void renderAfterTranslucents(PoseStack var1, MultiBufferSource.BufferSource var2, double var3, double var5, double var7) {
      Minecraft var9 = Minecraft.getInstance();
      DebugValueAccess var10 = var9.getConnection().createDebugValueAccess();
      this.chunkCullingDebugRenderer.render(var1, var2, var3, var5, var7, var10);
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

   public static void renderFilledUnitCube(PoseStack var0, MultiBufferSource var1, BlockPos var2, float var3, float var4, float var5, float var6) {
      renderFilledBox(var0, var1, var2, var2.offset(1, 1, 1), var3, var4, var5, var6);
   }

   public static void renderFilledBox(PoseStack var0, MultiBufferSource var1, BlockPos var2, BlockPos var3, float var4, float var5, float var6, float var7) {
      Camera var8 = Minecraft.getInstance().gameRenderer.getMainCamera();
      if (var8.isInitialized()) {
         Vec3 var9 = var8.getPosition().reverse();
         AABB var10 = AABB.encapsulatingFullBlocks(var2, var3).move(var9);
         renderFilledBox(var0, var1, var10, var4, var5, var6, var7);
      }
   }

   public static void renderFilledBox(PoseStack var0, MultiBufferSource var1, BlockPos var2, float var3, float var4, float var5, float var6, float var7) {
      Camera var8 = Minecraft.getInstance().gameRenderer.getMainCamera();
      if (var8.isInitialized()) {
         Vec3 var9 = var8.getPosition().reverse();
         AABB var10 = (new AABB(var2)).move(var9).inflate((double)var3);
         renderFilledBox(var0, var1, var10, var4, var5, var6, var7);
      }
   }

   public static void renderFilledBox(PoseStack var0, MultiBufferSource var1, AABB var2, float var3, float var4, float var5, float var6) {
      renderFilledBox(var0, var1, var2.minX, var2.minY, var2.minZ, var2.maxX, var2.maxY, var2.maxZ, var3, var4, var5, var6);
   }

   public static void renderFilledBox(PoseStack var0, MultiBufferSource var1, double var2, double var4, double var6, double var8, double var10, double var12, float var14, float var15, float var16, float var17) {
      VertexConsumer var18 = var1.getBuffer(RenderType.debugFilledBox());
      ShapeRenderer.addChainedFilledBoxVertices(var0, var18, var2, var4, var6, var8, var10, var12, var14, var15, var16, var17);
   }

   public static void renderTextOverBlock(PoseStack var0, MultiBufferSource var1, String var2, BlockPos var3, int var4, int var5, float var6) {
      double var7 = 1.3;
      double var9 = 0.2;
      double var11 = (double)var3.getX() + 0.5;
      double var13 = (double)var3.getY() + 1.3 + (double)var4 * 0.2;
      double var15 = (double)var3.getZ() + 0.5;
      renderFloatingText(var0, var1, var2, var11, var13, var15, var5, var6, true, 0.0F, true);
   }

   public static void renderTextOverMob(PoseStack var0, MultiBufferSource var1, Entity var2, int var3, String var4, int var5, float var6) {
      double var7 = 2.4;
      double var9 = 0.25;
      double var11 = (double)var2.getBlockX() + 0.5;
      double var13 = var2.getY() + 2.4 + (double)var3 * 0.25;
      double var15 = (double)var2.getBlockZ() + 0.5;
      float var17 = 0.5F;
      renderFloatingText(var0, var1, var4, var11, var13, var15, var5, var6, false, 0.5F, true);
   }

   public static void renderFloatingText(PoseStack var0, MultiBufferSource var1, String var2, int var3, int var4, int var5, int var6) {
      renderFloatingText(var0, var1, var2, (double)var3 + 0.5, (double)var4 + 0.5, (double)var5 + 0.5, var6);
   }

   public static void renderFloatingText(PoseStack var0, MultiBufferSource var1, String var2, double var3, double var5, double var7, int var9) {
      renderFloatingText(var0, var1, var2, var3, var5, var7, var9, 0.02F);
   }

   public static void renderFloatingText(PoseStack var0, MultiBufferSource var1, String var2, double var3, double var5, double var7, int var9, float var10) {
      renderFloatingText(var0, var1, var2, var3, var5, var7, var9, var10, true, 0.0F, false);
   }

   public static void renderFloatingText(PoseStack var0, MultiBufferSource var1, String var2, double var3, double var5, double var7, int var9, float var10, boolean var11, float var12, boolean var13) {
      Minecraft var14 = Minecraft.getInstance();
      Camera var15 = var14.gameRenderer.getMainCamera();
      if (var15.isInitialized() && var14.getEntityRenderDispatcher().options != null) {
         Font var16 = var14.font;
         double var17 = var15.getPosition().x;
         double var19 = var15.getPosition().y;
         double var21 = var15.getPosition().z;
         var0.pushPose();
         var0.translate((float)(var3 - var17), (float)(var5 - var19) + 0.07F, (float)(var7 - var21));
         var0.mulPose((Quaternionfc)var15.rotation());
         var0.scale(var10, -var10, var10);
         float var23 = var11 ? (float)(-var16.width(var2)) / 2.0F : 0.0F;
         var23 -= var12 / var10;
         var16.drawInBatch((String)var2, var23, 0.0F, var9, false, var0.last().pose(), var1, var13 ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL, 0, 15728880);
         var0.popPose();
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

   public static void renderVoxelShape(PoseStack var0, VertexConsumer var1, VoxelShape var2, double var3, double var5, double var7, float var9, float var10, float var11, float var12, boolean var13) {
      List var14 = var2.toAabbs();
      if (!var14.isEmpty()) {
         int var15 = var13 ? var14.size() : var14.size() * 8;
         ShapeRenderer.renderShape(var0, var1, Shapes.create((AABB)var14.get(0)), var3, var5, var7, ARGB.colorFromFloat(var12, var9, var10, var11));

         for(int var16 = 1; var16 < var14.size(); ++var16) {
            AABB var17 = (AABB)var14.get(var16);
            float var18 = (float)var16 / (float)var15;
            Vec3 var19 = shiftHue(var9, var10, var11, var18);
            ShapeRenderer.renderShape(var0, var1, Shapes.create(var17), var3, var5, var7, ARGB.colorFromFloat(var12, (float)var19.x, (float)var19.y, (float)var19.z));
         }

      }
   }

   public interface SimpleDebugRenderer {
      void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7, DebugValueAccess var9);

      default void clear() {
      }
   }
}
