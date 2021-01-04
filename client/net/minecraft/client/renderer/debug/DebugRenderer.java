package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class DebugRenderer {
   public final PathfindingRenderer pathfindingRenderer;
   public final DebugRenderer.SimpleDebugRenderer waterDebugRenderer;
   public final DebugRenderer.SimpleDebugRenderer chunkBorderRenderer;
   public final DebugRenderer.SimpleDebugRenderer heightMapRenderer;
   public final DebugRenderer.SimpleDebugRenderer collisionBoxRenderer;
   public final DebugRenderer.SimpleDebugRenderer neighborsUpdateRenderer;
   public final CaveDebugRenderer caveRenderer;
   public final StructureRenderer structureRenderer;
   public final DebugRenderer.SimpleDebugRenderer lightDebugRenderer;
   public final DebugRenderer.SimpleDebugRenderer worldGenAttemptRenderer;
   public final DebugRenderer.SimpleDebugRenderer solidFaceRenderer;
   public final DebugRenderer.SimpleDebugRenderer chunkRenderer;
   public final VillageDebugRenderer villageDebugRenderer;
   public final RaidDebugRenderer raidDebugRenderer;
   public final GoalSelectorDebugRenderer goalSelectorRenderer;
   private boolean renderChunkborder;

   public DebugRenderer(Minecraft var1) {
      super();
      this.pathfindingRenderer = new PathfindingRenderer(var1);
      this.waterDebugRenderer = new WaterDebugRenderer(var1);
      this.chunkBorderRenderer = new ChunkBorderRenderer(var1);
      this.heightMapRenderer = new HeightMapRenderer(var1);
      this.collisionBoxRenderer = new CollisionBoxRenderer(var1);
      this.neighborsUpdateRenderer = new NeighborsUpdateRenderer(var1);
      this.caveRenderer = new CaveDebugRenderer(var1);
      this.structureRenderer = new StructureRenderer(var1);
      this.lightDebugRenderer = new LightDebugRenderer(var1);
      this.worldGenAttemptRenderer = new WorldGenAttemptRenderer(var1);
      this.solidFaceRenderer = new SolidFaceRenderer(var1);
      this.chunkRenderer = new ChunkDebugRenderer(var1);
      this.villageDebugRenderer = new VillageDebugRenderer(var1);
      this.raidDebugRenderer = new RaidDebugRenderer(var1);
      this.goalSelectorRenderer = new GoalSelectorDebugRenderer(var1);
   }

   public void clear() {
      this.pathfindingRenderer.clear();
      this.waterDebugRenderer.clear();
      this.chunkBorderRenderer.clear();
      this.heightMapRenderer.clear();
      this.collisionBoxRenderer.clear();
      this.neighborsUpdateRenderer.clear();
      this.caveRenderer.clear();
      this.structureRenderer.clear();
      this.lightDebugRenderer.clear();
      this.worldGenAttemptRenderer.clear();
      this.solidFaceRenderer.clear();
      this.chunkRenderer.clear();
      this.villageDebugRenderer.clear();
      this.raidDebugRenderer.clear();
      this.goalSelectorRenderer.clear();
   }

   public boolean shouldRender() {
      return this.renderChunkborder;
   }

   public boolean switchRenderChunkborder() {
      this.renderChunkborder = !this.renderChunkborder;
      return this.renderChunkborder;
   }

   public void render(long var1) {
      if (this.renderChunkborder && !Minecraft.getInstance().showOnlyReducedInfo()) {
         this.chunkBorderRenderer.render(var1);
      }

   }

   public static Optional<Entity> getTargetedEntity(@Nullable Entity var0, int var1) {
      if (var0 == null) {
         return Optional.empty();
      } else {
         Vec3 var2 = var0.getEyePosition(1.0F);
         Vec3 var3 = var0.getViewVector(1.0F).scale((double)var1);
         Vec3 var4 = var2.add(var3);
         AABB var5 = var0.getBoundingBox().expandTowards(var3).inflate(1.0D);
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

   public static void renderFilledBox(BlockPos var0, BlockPos var1, float var2, float var3, float var4, float var5) {
      Camera var6 = Minecraft.getInstance().gameRenderer.getMainCamera();
      if (var6.isInitialized()) {
         Vec3 var7 = var6.getPosition().reverse();
         AABB var8 = (new AABB(var0, var1)).move(var7);
         renderFilledBox(var8, var2, var3, var4, var5);
      }
   }

   public static void renderFilledBox(BlockPos var0, float var1, float var2, float var3, float var4, float var5) {
      Camera var6 = Minecraft.getInstance().gameRenderer.getMainCamera();
      if (var6.isInitialized()) {
         Vec3 var7 = var6.getPosition().reverse();
         AABB var8 = (new AABB(var0)).move(var7).inflate((double)var1);
         renderFilledBox(var8, var2, var3, var4, var5);
      }
   }

   public static void renderFilledBox(AABB var0, float var1, float var2, float var3, float var4) {
      renderFilledBox(var0.minX, var0.minY, var0.minZ, var0.maxX, var0.maxY, var0.maxZ, var1, var2, var3, var4);
   }

   public static void renderFilledBox(double var0, double var2, double var4, double var6, double var8, double var10, float var12, float var13, float var14, float var15) {
      Tesselator var16 = Tesselator.getInstance();
      BufferBuilder var17 = var16.getBuilder();
      var17.begin(5, DefaultVertexFormat.POSITION_COLOR);
      LevelRenderer.addChainedFilledBoxVertices(var17, var0, var2, var4, var6, var8, var10, var12, var13, var14, var15);
      var16.end();
   }

   public static void renderFloatingText(String var0, int var1, int var2, int var3, int var4) {
      renderFloatingText(var0, (double)var1 + 0.5D, (double)var2 + 0.5D, (double)var3 + 0.5D, var4);
   }

   public static void renderFloatingText(String var0, double var1, double var3, double var5, int var7) {
      renderFloatingText(var0, var1, var3, var5, var7, 0.02F);
   }

   public static void renderFloatingText(String var0, double var1, double var3, double var5, int var7, float var8) {
      renderFloatingText(var0, var1, var3, var5, var7, var8, true, 0.0F, false);
   }

   public static void renderFloatingText(String var0, double var1, double var3, double var5, int var7, float var8, boolean var9, float var10, boolean var11) {
      Minecraft var12 = Minecraft.getInstance();
      Camera var13 = var12.gameRenderer.getMainCamera();
      if (var13.isInitialized() && var12.getEntityRenderDispatcher().options != null) {
         Font var14 = var12.font;
         double var15 = var13.getPosition().x;
         double var17 = var13.getPosition().y;
         double var19 = var13.getPosition().z;
         GlStateManager.pushMatrix();
         GlStateManager.translatef((float)(var1 - var15), (float)(var3 - var17) + 0.07F, (float)(var5 - var19));
         GlStateManager.normal3f(0.0F, 1.0F, 0.0F);
         GlStateManager.scalef(var8, -var8, var8);
         EntityRenderDispatcher var21 = var12.getEntityRenderDispatcher();
         GlStateManager.rotatef(-var21.playerRotY, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(-var21.playerRotX, 1.0F, 0.0F, 0.0F);
         GlStateManager.enableTexture();
         if (var11) {
            GlStateManager.disableDepthTest();
         } else {
            GlStateManager.enableDepthTest();
         }

         GlStateManager.depthMask(true);
         GlStateManager.scalef(-1.0F, 1.0F, 1.0F);
         float var22 = var9 ? (float)(-var14.width(var0)) / 2.0F : 0.0F;
         var22 -= var10 / var8;
         var14.draw(var0, var22, 0.0F, var7);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.enableDepthTest();
         GlStateManager.popMatrix();
      }
   }

   public interface SimpleDebugRenderer {
      void render(long var1);

      default void clear() {
      }
   }
}
