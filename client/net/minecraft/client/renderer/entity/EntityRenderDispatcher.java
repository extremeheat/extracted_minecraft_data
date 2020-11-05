package net.minecraft.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EntityRenderDispatcher implements ResourceManagerReloadListener {
   private static final RenderType SHADOW_RENDER_TYPE = RenderType.entityShadow(new ResourceLocation("textures/misc/shadow.png"));
   private Map<EntityType<?>, EntityRenderer<?>> renderers = ImmutableMap.of();
   private Map<String, EntityRenderer<? extends Player>> playerRenderers = ImmutableMap.of();
   public final TextureManager textureManager;
   private Level level;
   public Camera camera;
   private Quaternion cameraOrientation;
   public Entity crosshairPickEntity;
   private final ItemRenderer itemRenderer;
   private final Font font;
   public final Options options;
   private final EntityModelSet entityModels;
   private boolean shouldRenderShadow = true;
   private boolean renderHitBoxes;

   public <E extends Entity> int getPackedLightCoords(E var1, float var2) {
      return this.getRenderer(var1).getPackedLightCoords(var1, var2);
   }

   public EntityRenderDispatcher(TextureManager var1, ItemRenderer var2, Font var3, Options var4, EntityModelSet var5) {
      super();
      this.textureManager = var1;
      this.itemRenderer = var2;
      this.font = var3;
      this.options = var4;
      this.entityModels = var5;
   }

   public <T extends Entity> EntityRenderer<? super T> getRenderer(T var1) {
      if (var1 instanceof AbstractClientPlayer) {
         String var2 = ((AbstractClientPlayer)var1).getModelName();
         EntityRenderer var3 = (EntityRenderer)this.playerRenderers.get(var2);
         return var3 != null ? var3 : (EntityRenderer)this.playerRenderers.get("default");
      } else {
         return (EntityRenderer)this.renderers.get(var1.getType());
      }
   }

   public void prepare(Level var1, Camera var2, Entity var3) {
      this.level = var1;
      this.camera = var2;
      this.cameraOrientation = var2.rotation();
      this.crosshairPickEntity = var3;
   }

   public void overrideCameraOrientation(Quaternion var1) {
      this.cameraOrientation = var1;
   }

   public void setRenderShadow(boolean var1) {
      this.shouldRenderShadow = var1;
   }

   public void setRenderHitBoxes(boolean var1) {
      this.renderHitBoxes = var1;
   }

   public boolean shouldRenderHitBoxes() {
      return this.renderHitBoxes;
   }

   public <E extends Entity> boolean shouldRender(E var1, Frustum var2, double var3, double var5, double var7) {
      EntityRenderer var9 = this.getRenderer(var1);
      return var9.shouldRender(var1, var2, var3, var5, var7);
   }

   public <E extends Entity> void render(E var1, double var2, double var4, double var6, float var8, float var9, PoseStack var10, MultiBufferSource var11, int var12) {
      EntityRenderer var13 = this.getRenderer(var1);

      try {
         Vec3 var14 = var13.getRenderOffset(var1, var9);
         double var25 = var2 + var14.x();
         double var26 = var4 + var14.y();
         double var19 = var6 + var14.z();
         var10.pushPose();
         var10.translate(var25, var26, var19);
         var13.render(var1, var8, var9, var10, var11, var12);
         if (var1.displayFireAnimation()) {
            this.renderFlame(var10, var11, var1);
         }

         var10.translate(-var14.x(), -var14.y(), -var14.z());
         if (this.options.entityShadows && this.shouldRenderShadow && var13.shadowRadius > 0.0F && !var1.isInvisible()) {
            double var21 = this.distanceToSqr(var1.getX(), var1.getY(), var1.getZ());
            float var23 = (float)((1.0D - var21 / 256.0D) * (double)var13.shadowStrength);
            if (var23 > 0.0F) {
               renderShadow(var10, var11, var1, var23, var9, this.level, var13.shadowRadius);
            }
         }

         if (this.renderHitBoxes && !var1.isInvisible() && !Minecraft.getInstance().showOnlyReducedInfo()) {
            this.renderHitbox(var10, var11.getBuffer(RenderType.lines()), var1, var9);
         }

         var10.popPose();
      } catch (Throwable var24) {
         CrashReport var15 = CrashReport.forThrowable(var24, "Rendering entity in world");
         CrashReportCategory var16 = var15.addCategory("Entity being rendered");
         var1.fillCrashReportCategory(var16);
         CrashReportCategory var17 = var15.addCategory("Renderer details");
         var17.setDetail("Assigned renderer", (Object)var13);
         var17.setDetail("Location", (Object)CrashReportCategory.formatLocation(this.level, var2, var4, var6));
         var17.setDetail("Rotation", (Object)var8);
         var17.setDetail("Delta", (Object)var9);
         throw new ReportedException(var15);
      }
   }

   private void renderHitbox(PoseStack var1, VertexConsumer var2, Entity var3, float var4) {
      float var5 = var3.getBbWidth() / 2.0F;
      this.renderBox(var1, var2, var3, 1.0F, 1.0F, 1.0F);
      if (var3 instanceof EnderDragon) {
         double var6 = -Mth.lerp((double)var4, var3.xOld, var3.getX());
         double var8 = -Mth.lerp((double)var4, var3.yOld, var3.getY());
         double var10 = -Mth.lerp((double)var4, var3.zOld, var3.getZ());
         EnderDragonPart[] var12 = ((EnderDragon)var3).getSubEntities();
         int var13 = var12.length;

         for(int var14 = 0; var14 < var13; ++var14) {
            EnderDragonPart var15 = var12[var14];
            var1.pushPose();
            double var16 = var6 + Mth.lerp((double)var4, var15.xOld, var15.getX());
            double var18 = var8 + Mth.lerp((double)var4, var15.yOld, var15.getY());
            double var20 = var10 + Mth.lerp((double)var4, var15.zOld, var15.getZ());
            var1.translate(var16, var18, var20);
            this.renderBox(var1, var2, var15, 0.25F, 1.0F, 0.0F);
            var1.popPose();
         }
      }

      if (var3 instanceof LivingEntity) {
         float var22 = 0.01F;
         LevelRenderer.renderLineBox(var1, var2, (double)(-var5), (double)(var3.getEyeHeight() - 0.01F), (double)(-var5), (double)var5, (double)(var3.getEyeHeight() + 0.01F), (double)var5, 1.0F, 0.0F, 0.0F, 1.0F);
      }

      Vec3 var23 = var3.getViewVector(var4);
      Matrix4f var7 = var1.last().pose();
      var2.vertex(var7, 0.0F, var3.getEyeHeight(), 0.0F).color(0, 0, 255, 255).endVertex();
      var2.vertex(var7, (float)(var23.x * 2.0D), (float)((double)var3.getEyeHeight() + var23.y * 2.0D), (float)(var23.z * 2.0D)).color(0, 0, 255, 255).endVertex();
   }

   private void renderBox(PoseStack var1, VertexConsumer var2, Entity var3, float var4, float var5, float var6) {
      AABB var7 = var3.getBoundingBox().move(-var3.getX(), -var3.getY(), -var3.getZ());
      LevelRenderer.renderLineBox(var1, var2, var7, var4, var5, var6, 1.0F);
   }

   private void renderFlame(PoseStack var1, MultiBufferSource var2, Entity var3) {
      TextureAtlasSprite var4 = ModelBakery.FIRE_0.sprite();
      TextureAtlasSprite var5 = ModelBakery.FIRE_1.sprite();
      var1.pushPose();
      float var6 = var3.getBbWidth() * 1.4F;
      var1.scale(var6, var6, var6);
      float var7 = 0.5F;
      float var8 = 0.0F;
      float var9 = var3.getBbHeight() / var6;
      float var10 = 0.0F;
      var1.mulPose(Vector3f.YP.rotationDegrees(-this.camera.getYRot()));
      var1.translate(0.0D, 0.0D, (double)(-0.3F + (float)((int)var9) * 0.02F));
      float var11 = 0.0F;
      int var12 = 0;
      VertexConsumer var13 = var2.getBuffer(Sheets.cutoutBlockSheet());

      for(PoseStack.Pose var14 = var1.last(); var9 > 0.0F; ++var12) {
         TextureAtlasSprite var15 = var12 % 2 == 0 ? var4 : var5;
         float var16 = var15.getU0();
         float var17 = var15.getV0();
         float var18 = var15.getU1();
         float var19 = var15.getV1();
         if (var12 / 2 % 2 == 0) {
            float var20 = var18;
            var18 = var16;
            var16 = var20;
         }

         fireVertex(var14, var13, var7 - 0.0F, 0.0F - var10, var11, var18, var19);
         fireVertex(var14, var13, -var7 - 0.0F, 0.0F - var10, var11, var16, var19);
         fireVertex(var14, var13, -var7 - 0.0F, 1.4F - var10, var11, var16, var17);
         fireVertex(var14, var13, var7 - 0.0F, 1.4F - var10, var11, var18, var17);
         var9 -= 0.45F;
         var10 -= 0.45F;
         var7 *= 0.9F;
         var11 += 0.03F;
      }

      var1.popPose();
   }

   private static void fireVertex(PoseStack.Pose var0, VertexConsumer var1, float var2, float var3, float var4, float var5, float var6) {
      var1.vertex(var0.pose(), var2, var3, var4).color(255, 255, 255, 255).uv(var5, var6).overlayCoords(0, 10).uv2(240).normal(var0.normal(), 0.0F, 1.0F, 0.0F).endVertex();
   }

   private static void renderShadow(PoseStack var0, MultiBufferSource var1, Entity var2, float var3, float var4, LevelReader var5, float var6) {
      float var7 = var6;
      if (var2 instanceof Mob) {
         Mob var8 = (Mob)var2;
         if (var8.isBaby()) {
            var7 = var6 * 0.5F;
         }
      }

      double var24 = Mth.lerp((double)var4, var2.xOld, var2.getX());
      double var10 = Mth.lerp((double)var4, var2.yOld, var2.getY());
      double var12 = Mth.lerp((double)var4, var2.zOld, var2.getZ());
      int var14 = Mth.floor(var24 - (double)var7);
      int var15 = Mth.floor(var24 + (double)var7);
      int var16 = Mth.floor(var10 - (double)var7);
      int var17 = Mth.floor(var10);
      int var18 = Mth.floor(var12 - (double)var7);
      int var19 = Mth.floor(var12 + (double)var7);
      PoseStack.Pose var20 = var0.last();
      VertexConsumer var21 = var1.getBuffer(SHADOW_RENDER_TYPE);
      Iterator var22 = BlockPos.betweenClosed(new BlockPos(var14, var16, var18), new BlockPos(var15, var17, var19)).iterator();

      while(var22.hasNext()) {
         BlockPos var23 = (BlockPos)var22.next();
         renderBlockShadow(var20, var21, var5, var23, var24, var10, var12, var7, var3);
      }

   }

   private static void renderBlockShadow(PoseStack.Pose var0, VertexConsumer var1, LevelReader var2, BlockPos var3, double var4, double var6, double var8, float var10, float var11) {
      BlockPos var12 = var3.below();
      BlockState var13 = var2.getBlockState(var12);
      if (var13.getRenderShape() != RenderShape.INVISIBLE && var2.getMaxLocalRawBrightness(var3) > 3) {
         if (var13.isCollisionShapeFullBlock(var2, var12)) {
            VoxelShape var14 = var13.getShape(var2, var3.below());
            if (!var14.isEmpty()) {
               float var15 = (float)(((double)var11 - (var6 - (double)var3.getY()) / 2.0D) * 0.5D * (double)var2.getBrightness(var3));
               if (var15 >= 0.0F) {
                  if (var15 > 1.0F) {
                     var15 = 1.0F;
                  }

                  AABB var16 = var14.bounds();
                  double var17 = (double)var3.getX() + var16.minX;
                  double var19 = (double)var3.getX() + var16.maxX;
                  double var21 = (double)var3.getY() + var16.minY;
                  double var23 = (double)var3.getZ() + var16.minZ;
                  double var25 = (double)var3.getZ() + var16.maxZ;
                  float var27 = (float)(var17 - var4);
                  float var28 = (float)(var19 - var4);
                  float var29 = (float)(var21 - var6);
                  float var30 = (float)(var23 - var8);
                  float var31 = (float)(var25 - var8);
                  float var32 = -var27 / 2.0F / var10 + 0.5F;
                  float var33 = -var28 / 2.0F / var10 + 0.5F;
                  float var34 = -var30 / 2.0F / var10 + 0.5F;
                  float var35 = -var31 / 2.0F / var10 + 0.5F;
                  shadowVertex(var0, var1, var15, var27, var29, var30, var32, var34);
                  shadowVertex(var0, var1, var15, var27, var29, var31, var32, var35);
                  shadowVertex(var0, var1, var15, var28, var29, var31, var33, var35);
                  shadowVertex(var0, var1, var15, var28, var29, var30, var33, var34);
               }

            }
         }
      }
   }

   private static void shadowVertex(PoseStack.Pose var0, VertexConsumer var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      var1.vertex(var0.pose(), var3, var4, var5).color(1.0F, 1.0F, 1.0F, var2).uv(var6, var7).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(var0.normal(), 0.0F, 1.0F, 0.0F).endVertex();
   }

   public void setLevel(@Nullable Level var1) {
      this.level = var1;
      if (var1 == null) {
         this.camera = null;
      }

   }

   public double distanceToSqr(Entity var1) {
      return this.camera.getPosition().distanceToSqr(var1.position());
   }

   public double distanceToSqr(double var1, double var3, double var5) {
      return this.camera.getPosition().distanceToSqr(var1, var3, var5);
   }

   public Quaternion cameraOrientation() {
      return this.cameraOrientation;
   }

   public void onResourceManagerReload(ResourceManager var1) {
      EntityRendererProvider.Context var2 = new EntityRendererProvider.Context(this, this.itemRenderer, var1, this.entityModels, this.font);
      this.renderers = EntityRenderers.createEntityRenderers(var2);
      this.playerRenderers = EntityRenderers.createPlayerRenderers(var2);
   }
}
