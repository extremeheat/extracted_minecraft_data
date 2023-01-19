package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Transformation;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class DebugRenderer {
   public final PathfindingRenderer pathfindingRenderer = new PathfindingRenderer();
   public final DebugRenderer.SimpleDebugRenderer waterDebugRenderer;
   public final DebugRenderer.SimpleDebugRenderer chunkBorderRenderer;
   public final DebugRenderer.SimpleDebugRenderer heightMapRenderer;
   public final DebugRenderer.SimpleDebugRenderer collisionBoxRenderer;
   public final DebugRenderer.SimpleDebugRenderer neighborsUpdateRenderer;
   public final StructureRenderer structureRenderer;
   public final DebugRenderer.SimpleDebugRenderer lightDebugRenderer;
   public final DebugRenderer.SimpleDebugRenderer worldGenAttemptRenderer;
   public final DebugRenderer.SimpleDebugRenderer solidFaceRenderer;
   public final DebugRenderer.SimpleDebugRenderer chunkRenderer;
   public final BrainDebugRenderer brainDebugRenderer;
   public final VillageSectionsDebugRenderer villageSectionsDebugRenderer;
   public final BeeDebugRenderer beeDebugRenderer;
   public final RaidDebugRenderer raidDebugRenderer;
   public final GoalSelectorDebugRenderer goalSelectorRenderer;
   public final GameTestDebugRenderer gameTestDebugRenderer;
   public final GameEventListenerRenderer gameEventListenerRenderer;
   private boolean renderChunkborder;

   public DebugRenderer(Minecraft var1) {
      super();
      this.waterDebugRenderer = new WaterDebugRenderer(var1);
      this.chunkBorderRenderer = new ChunkBorderRenderer(var1);
      this.heightMapRenderer = new HeightMapRenderer(var1);
      this.collisionBoxRenderer = new CollisionBoxRenderer(var1);
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
   }

   public void clear() {
      this.pathfindingRenderer.clear();
      this.waterDebugRenderer.clear();
      this.chunkBorderRenderer.clear();
      this.heightMapRenderer.clear();
      this.collisionBoxRenderer.clear();
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
         Predicate var7 = var0x -> !var0x.isSpectator() && var0x.isPickable();
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
         AABB var8 = new AABB(var0, var1).move(var7);
         renderFilledBox(var8, var2, var3, var4, var5);
      }
   }

   public static void renderFilledBox(BlockPos var0, float var1, float var2, float var3, float var4, float var5) {
      Camera var6 = Minecraft.getInstance().gameRenderer.getMainCamera();
      if (var6.isInitialized()) {
         Vec3 var7 = var6.getPosition().reverse();
         AABB var8 = new AABB(var0).move(var7).inflate((double)var1);
         renderFilledBox(var8, var2, var3, var4, var5);
      }
   }

   public static void renderFilledBox(AABB var0, float var1, float var2, float var3, float var4) {
      renderFilledBox(var0.minX, var0.minY, var0.minZ, var0.maxX, var0.maxY, var0.maxZ, var1, var2, var3, var4);
   }

   public static void renderFilledBox(
      double var0, double var2, double var4, double var6, double var8, double var10, float var12, float var13, float var14, float var15
   ) {
      Tesselator var16 = Tesselator.getInstance();
      BufferBuilder var17 = var16.getBuilder();
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      var17.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
      LevelRenderer.addChainedFilledBoxVertices(var17, var0, var2, var4, var6, var8, var10, var12, var13, var14, var15);
      var16.end();
   }

   public static void renderFloatingText(String var0, int var1, int var2, int var3, int var4) {
      renderFloatingText(var0, (double)var1 + 0.5, (double)var2 + 0.5, (double)var3 + 0.5, var4);
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
         PoseStack var21 = RenderSystem.getModelViewStack();
         var21.pushPose();
         var21.translate((float)(var1 - var15), (float)(var3 - var17) + 0.07F, (float)(var5 - var19));
         var21.mulPoseMatrix(new Matrix4f().rotation(var13.rotation()));
         var21.scale(var8, -var8, var8);
         if (var11) {
            RenderSystem.disableDepthTest();
         } else {
            RenderSystem.enableDepthTest();
         }

         RenderSystem.depthMask(true);
         var21.scale(-1.0F, 1.0F, 1.0F);
         RenderSystem.applyModelViewMatrix();
         float var22 = var9 ? (float)(-var14.width(var0)) / 2.0F : 0.0F;
         var22 -= var10 / var8;
         MultiBufferSource.BufferSource var23 = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
         var14.drawInBatch(var0, var22, 0.0F, var7, false, Transformation.identity().getMatrix(), var23, var11, 0, 15728880);
         var23.endBatch();
         RenderSystem.enableDepthTest();
         var21.popPose();
         RenderSystem.applyModelViewMatrix();
      }
   }

   public interface SimpleDebugRenderer {
      void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7);

      default void clear() {
      }
   }
}
