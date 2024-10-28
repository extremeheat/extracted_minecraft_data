package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class DebugRenderer {
   public final PathfindingRenderer pathfindingRenderer = new PathfindingRenderer();
   public final SimpleDebugRenderer waterDebugRenderer;
   public final SimpleDebugRenderer chunkBorderRenderer;
   public final SimpleDebugRenderer heightMapRenderer;
   public final SimpleDebugRenderer collisionBoxRenderer;
   public final SimpleDebugRenderer supportBlockRenderer;
   public final SimpleDebugRenderer neighborsUpdateRenderer;
   public final StructureRenderer structureRenderer;
   public final SimpleDebugRenderer lightDebugRenderer;
   public final SimpleDebugRenderer worldGenAttemptRenderer;
   public final SimpleDebugRenderer solidFaceRenderer;
   public final SimpleDebugRenderer chunkRenderer;
   public final BrainDebugRenderer brainDebugRenderer;
   public final VillageSectionsDebugRenderer villageSectionsDebugRenderer;
   public final BeeDebugRenderer beeDebugRenderer;
   public final RaidDebugRenderer raidDebugRenderer;
   public final GoalSelectorDebugRenderer goalSelectorRenderer;
   public final GameTestDebugRenderer gameTestDebugRenderer;
   public final GameEventListenerRenderer gameEventListenerRenderer;
   public final LightSectionDebugRenderer skyLightSectionDebugRenderer;
   public final BreezeDebugRenderer breezeDebugRenderer;
   private boolean renderChunkborder;

   public DebugRenderer(Minecraft var1) {
      super();
      this.waterDebugRenderer = new WaterDebugRenderer(var1);
      this.chunkBorderRenderer = new ChunkBorderRenderer(var1);
      this.heightMapRenderer = new HeightMapRenderer(var1);
      this.collisionBoxRenderer = new CollisionBoxRenderer(var1);
      this.supportBlockRenderer = new SupportBlockRenderer(var1);
      this.neighborsUpdateRenderer = new NeighborsUpdateRenderer(var1);
      this.structureRenderer = new StructureRenderer(var1);
      this.lightDebugRenderer = new LightDebugRenderer(var1);
      this.worldGenAttemptRenderer = new WorldGenAttemptRenderer();
      this.solidFaceRenderer = new SolidFaceRenderer(var1);
      this.chunkRenderer = new ChunkDebugRenderer(var1);
      this.brainDebugRenderer = new BrainDebugRenderer(var1);
      this.villageSectionsDebugRenderer = new VillageSectionsDebugRenderer();
      this.beeDebugRenderer = new BeeDebugRenderer(var1);
      this.raidDebugRenderer = new RaidDebugRenderer(var1);
      this.goalSelectorRenderer = new GoalSelectorDebugRenderer(var1);
      this.gameTestDebugRenderer = new GameTestDebugRenderer();
      this.gameEventListenerRenderer = new GameEventListenerRenderer(var1);
      this.skyLightSectionDebugRenderer = new LightSectionDebugRenderer(var1, LightLayer.SKY);
      this.breezeDebugRenderer = new BreezeDebugRenderer(var1);
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
      this.worldGenAttemptRenderer.clear();
      this.solidFaceRenderer.clear();
      this.chunkRenderer.clear();
      this.brainDebugRenderer.clear();
      this.villageSectionsDebugRenderer.clear();
      this.beeDebugRenderer.clear();
      this.raidDebugRenderer.clear();
      this.goalSelectorRenderer.clear();
      this.gameTestDebugRenderer.clear();
      this.gameEventListenerRenderer.clear();
      this.skyLightSectionDebugRenderer.clear();
      this.breezeDebugRenderer.clear();
   }

   public boolean switchRenderChunkborder() {
      this.renderChunkborder = !this.renderChunkborder;
      return this.renderChunkborder;
   }

   public void render(PoseStack var1, MultiBufferSource.BufferSource var2, double var3, double var5, double var7) {
      if (this.renderChunkborder && !Minecraft.getInstance().showOnlyReducedInfo()) {
         this.chunkBorderRenderer.render(var1, var2, var3, var5, var7);
      }

      this.gameTestDebugRenderer.render(var1, var2, var3, var5, var7);
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
         Predicate var7 = (var0x) -> {
            return !var0x.isSpectator() && var0x.isPickable();
         };
         EntityHitResult var8 = ProjectileUtil.getEntityHitResult(var0, var2, var4, var5, var7, (double)var6);
         if (var8 == null) {
            return Optional.empty();
         } else {
            return var2.distanceToSqr(var8.getLocation()) > (double)var6 ? Optional.empty() : Optional.of(var8.getEntity());
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
      LevelRenderer.addChainedFilledBoxVertices(var0, var18, var2, var4, var6, var8, var10, var12, var14, var15, var16, var17);
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
         var0.mulPose(var15.rotation());
         var0.scale(var10, -var10, var10);
         float var23 = var11 ? (float)(-var16.width(var2)) / 2.0F : 0.0F;
         var23 -= var12 / var10;
         var16.drawInBatch((String)var2, var23, 0.0F, var9, false, var0.last().pose(), var1, var13 ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL, 0, 15728880);
         var0.popPose();
      }
   }

   public interface SimpleDebugRenderer {
      void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7);

      default void clear() {
      }
   }
}
