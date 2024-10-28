package net.minecraft.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class EntityRenderDispatcher implements ResourceManagerReloadListener {
   private static final RenderType SHADOW_RENDER_TYPE = RenderType.entityShadow(ResourceLocation.withDefaultNamespace("textures/misc/shadow.png"));
   private static final float MAX_SHADOW_RADIUS = 32.0F;
   private static final float SHADOW_POWER_FALLOFF_Y = 0.5F;
   private Map<EntityType<?>, EntityRenderer<?>> renderers = ImmutableMap.of();
   private Map<PlayerSkin.Model, EntityRenderer<? extends Player>> playerRenderers = Map.of();
   public final TextureManager textureManager;
   private Level level;
   public Camera camera;
   private Quaternionf cameraOrientation;
   public Entity crosshairPickEntity;
   private final ItemRenderer itemRenderer;
   private final BlockRenderDispatcher blockRenderDispatcher;
   private final ItemInHandRenderer itemInHandRenderer;
   private final Font font;
   public final Options options;
   private final EntityModelSet entityModels;
   private boolean shouldRenderShadow = true;
   private boolean renderHitBoxes;

   public <E extends Entity> int getPackedLightCoords(E var1, float var2) {
      return this.getRenderer(var1).getPackedLightCoords(var1, var2);
   }

   public EntityRenderDispatcher(Minecraft var1, TextureManager var2, ItemRenderer var3, BlockRenderDispatcher var4, Font var5, Options var6, EntityModelSet var7) {
      super();
      this.textureManager = var2;
      this.itemRenderer = var3;
      this.itemInHandRenderer = new ItemInHandRenderer(var1, this, var3);
      this.blockRenderDispatcher = var4;
      this.font = var5;
      this.options = var6;
      this.entityModels = var7;
   }

   public <T extends Entity> EntityRenderer<? super T> getRenderer(T var1) {
      if (var1 instanceof AbstractClientPlayer var2) {
         PlayerSkin.Model var3 = var2.getSkin().model();
         EntityRenderer var4 = (EntityRenderer)this.playerRenderers.get(var3);
         return var4 != null ? var4 : (EntityRenderer)this.playerRenderers.get(PlayerSkin.Model.WIDE);
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

   public void overrideCameraOrientation(Quaternionf var1) {
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
         double var26 = var2 + var14.x();
         double var27 = var4 + var14.y();
         double var19 = var6 + var14.z();
         var10.pushPose();
         var10.translate(var26, var27, var19);
         var13.render(var1, var8, var9, var10, var11, var12);
         if (var1.displayFireAnimation()) {
            this.renderFlame(var10, var11, var1, Mth.rotationAroundAxis(Mth.Y_AXIS, this.cameraOrientation, new Quaternionf()));
         }

         var10.translate(-var14.x(), -var14.y(), -var14.z());
         if ((Boolean)this.options.entityShadows().get() && this.shouldRenderShadow && !var1.isInvisible()) {
            float var21 = var13.getShadowRadius(var1);
            if (var21 > 0.0F) {
               double var22 = this.distanceToSqr(var1.getX(), var1.getY(), var1.getZ());
               float var24 = (float)((1.0 - var22 / 256.0) * (double)var13.shadowStrength);
               if (var24 > 0.0F) {
                  renderShadow(var10, var11, var1, var24, var9, this.level, Math.min(var21, 32.0F));
               }
            }
         }

         if (this.renderHitBoxes && !var1.isInvisible() && !Minecraft.getInstance().showOnlyReducedInfo()) {
            renderHitbox(var10, var11.getBuffer(RenderType.lines()), var1, var9, 1.0F, 1.0F, 1.0F);
         }

         var10.popPose();
      } catch (Throwable var25) {
         CrashReport var15 = CrashReport.forThrowable(var25, "Rendering entity in world");
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

   private static void renderServerSideHitbox(PoseStack var0, Entity var1, MultiBufferSource var2) {
      Entity var3 = getServerSideEntity(var1);
      if (var3 == null) {
         DebugRenderer.renderFloatingText(var0, var2, "Missing", var1.getX(), var1.getBoundingBox().maxY + 1.5, var1.getZ(), -65536);
      } else {
         var0.pushPose();
         var0.translate(var3.getX() - var1.getX(), var3.getY() - var1.getY(), var3.getZ() - var1.getZ());
         renderHitbox(var0, var2.getBuffer(RenderType.lines()), var3, 1.0F, 0.0F, 1.0F, 0.0F);
         renderVector(var0, var2.getBuffer(RenderType.lines()), new Vector3f(), var3.getDeltaMovement(), -256);
         var0.popPose();
      }
   }

   @Nullable
   private static Entity getServerSideEntity(Entity var0) {
      IntegratedServer var1 = Minecraft.getInstance().getSingleplayerServer();
      if (var1 != null) {
         ServerLevel var2 = var1.getLevel(var0.level().dimension());
         if (var2 != null) {
            return var2.getEntity(var0.getId());
         }
      }

      return null;
   }

   private static void renderHitbox(PoseStack var0, VertexConsumer var1, Entity var2, float var3, float var4, float var5, float var6) {
      AABB var7 = var2.getBoundingBox().move(-var2.getX(), -var2.getY(), -var2.getZ());
      LevelRenderer.renderLineBox(var0, var1, var7, var4, var5, var6, 1.0F);
      if (var2 instanceof EnderDragon) {
         double var8 = -Mth.lerp((double)var3, var2.xOld, var2.getX());
         double var10 = -Mth.lerp((double)var3, var2.yOld, var2.getY());
         double var12 = -Mth.lerp((double)var3, var2.zOld, var2.getZ());
         EnderDragonPart[] var14 = ((EnderDragon)var2).getSubEntities();
         int var15 = var14.length;

         for(int var16 = 0; var16 < var15; ++var16) {
            EnderDragonPart var17 = var14[var16];
            var0.pushPose();
            double var18 = var8 + Mth.lerp((double)var3, var17.xOld, var17.getX());
            double var20 = var10 + Mth.lerp((double)var3, var17.yOld, var17.getY());
            double var22 = var12 + Mth.lerp((double)var3, var17.zOld, var17.getZ());
            var0.translate(var18, var20, var22);
            LevelRenderer.renderLineBox(var0, var1, var17.getBoundingBox().move(-var17.getX(), -var17.getY(), -var17.getZ()), 0.25F, 1.0F, 0.0F, 1.0F);
            var0.popPose();
         }
      }

      if (var2 instanceof LivingEntity) {
         float var24 = 0.01F;
         LevelRenderer.renderLineBox(var0, var1, var7.minX, (double)(var2.getEyeHeight() - 0.01F), var7.minZ, var7.maxX, (double)(var2.getEyeHeight() + 0.01F), var7.maxZ, 1.0F, 0.0F, 0.0F, 1.0F);
      }

      Entity var25 = var2.getVehicle();
      if (var25 != null) {
         float var9 = Math.min(var25.getBbWidth(), var2.getBbWidth()) / 2.0F;
         float var26 = 0.0625F;
         Vec3 var11 = var25.getPassengerRidingPosition(var2).subtract(var2.position());
         LevelRenderer.renderLineBox(var0, var1, var11.x - (double)var9, var11.y, var11.z - (double)var9, var11.x + (double)var9, var11.y + 0.0625, var11.z + (double)var9, 1.0F, 1.0F, 0.0F, 1.0F);
      }

      renderVector(var0, var1, new Vector3f(0.0F, var2.getEyeHeight(), 0.0F), var2.getViewVector(var3).scale(2.0), -16776961);
   }

   private static void renderVector(PoseStack var0, VertexConsumer var1, Vector3f var2, Vec3 var3, int var4) {
      PoseStack.Pose var5 = var0.last();
      var1.addVertex(var5, var2).setColor(var4).setNormal(var5, (float)var3.x, (float)var3.y, (float)var3.z);
      var1.addVertex(var5, (float)((double)var2.x() + var3.x), (float)((double)var2.y() + var3.y), (float)((double)var2.z() + var3.z)).setColor(var4).setNormal(var5, (float)var3.x, (float)var3.y, (float)var3.z);
   }

   private void renderFlame(PoseStack var1, MultiBufferSource var2, Entity var3, Quaternionf var4) {
      TextureAtlasSprite var5 = ModelBakery.FIRE_0.sprite();
      TextureAtlasSprite var6 = ModelBakery.FIRE_1.sprite();
      var1.pushPose();
      float var7 = var3.getBbWidth() * 1.4F;
      var1.scale(var7, var7, var7);
      float var8 = 0.5F;
      float var9 = 0.0F;
      float var10 = var3.getBbHeight() / var7;
      float var11 = 0.0F;
      var1.mulPose(var4);
      var1.translate(0.0F, 0.0F, 0.3F - (float)((int)var10) * 0.02F);
      float var12 = 0.0F;
      int var13 = 0;
      VertexConsumer var14 = var2.getBuffer(Sheets.cutoutBlockSheet());

      for(PoseStack.Pose var15 = var1.last(); var10 > 0.0F; ++var13) {
         TextureAtlasSprite var16 = var13 % 2 == 0 ? var5 : var6;
         float var17 = var16.getU0();
         float var18 = var16.getV0();
         float var19 = var16.getU1();
         float var20 = var16.getV1();
         if (var13 / 2 % 2 == 0) {
            float var21 = var19;
            var19 = var17;
            var17 = var21;
         }

         fireVertex(var15, var14, -var8 - 0.0F, 0.0F - var11, var12, var19, var20);
         fireVertex(var15, var14, var8 - 0.0F, 0.0F - var11, var12, var17, var20);
         fireVertex(var15, var14, var8 - 0.0F, 1.4F - var11, var12, var17, var18);
         fireVertex(var15, var14, -var8 - 0.0F, 1.4F - var11, var12, var19, var18);
         var10 -= 0.45F;
         var11 -= 0.45F;
         var8 *= 0.9F;
         var12 -= 0.03F;
      }

      var1.popPose();
   }

   private static void fireVertex(PoseStack.Pose var0, VertexConsumer var1, float var2, float var3, float var4, float var5, float var6) {
      var1.addVertex(var0, var2, var3, var4).setColor(-1).setUv(var5, var6).setUv1(0, 10).setLight(240).setNormal(var0, 0.0F, 1.0F, 0.0F);
   }

   private static void renderShadow(PoseStack var0, MultiBufferSource var1, Entity var2, float var3, float var4, LevelReader var5, float var6) {
      double var7 = Mth.lerp((double)var4, var2.xOld, var2.getX());
      double var9 = Mth.lerp((double)var4, var2.yOld, var2.getY());
      double var11 = Mth.lerp((double)var4, var2.zOld, var2.getZ());
      float var13 = Math.min(var3 / 0.5F, var6);
      int var14 = Mth.floor(var7 - (double)var6);
      int var15 = Mth.floor(var7 + (double)var6);
      int var16 = Mth.floor(var9 - (double)var13);
      int var17 = Mth.floor(var9);
      int var18 = Mth.floor(var11 - (double)var6);
      int var19 = Mth.floor(var11 + (double)var6);
      PoseStack.Pose var20 = var0.last();
      VertexConsumer var21 = var1.getBuffer(SHADOW_RENDER_TYPE);
      BlockPos.MutableBlockPos var22 = new BlockPos.MutableBlockPos();

      for(int var23 = var18; var23 <= var19; ++var23) {
         for(int var24 = var14; var24 <= var15; ++var24) {
            var22.set(var24, 0, var23);
            ChunkAccess var25 = var5.getChunk(var22);

            for(int var26 = var16; var26 <= var17; ++var26) {
               var22.setY(var26);
               float var27 = var3 - (float)(var9 - (double)var22.getY()) * 0.5F;
               renderBlockShadow(var20, var21, var25, var5, var22, var7, var9, var11, var6, var27);
            }
         }
      }

   }

   private static void renderBlockShadow(PoseStack.Pose var0, VertexConsumer var1, ChunkAccess var2, LevelReader var3, BlockPos var4, double var5, double var7, double var9, float var11, float var12) {
      BlockPos var13 = var4.below();
      BlockState var14 = var2.getBlockState(var13);
      if (var14.getRenderShape() != RenderShape.INVISIBLE && var3.getMaxLocalRawBrightness(var4) > 3) {
         if (var14.isCollisionShapeFullBlock(var2, var13)) {
            VoxelShape var15 = var14.getShape(var2, var13);
            if (!var15.isEmpty()) {
               float var16 = LightTexture.getBrightness(var3.dimensionType(), var3.getMaxLocalRawBrightness(var4));
               float var17 = var12 * 0.5F * var16;
               if (var17 >= 0.0F) {
                  if (var17 > 1.0F) {
                     var17 = 1.0F;
                  }

                  int var18 = FastColor.ARGB32.color(Mth.floor(var17 * 255.0F), 255, 255, 255);
                  AABB var19 = var15.bounds();
                  double var20 = (double)var4.getX() + var19.minX;
                  double var22 = (double)var4.getX() + var19.maxX;
                  double var24 = (double)var4.getY() + var19.minY;
                  double var26 = (double)var4.getZ() + var19.minZ;
                  double var28 = (double)var4.getZ() + var19.maxZ;
                  float var30 = (float)(var20 - var5);
                  float var31 = (float)(var22 - var5);
                  float var32 = (float)(var24 - var7);
                  float var33 = (float)(var26 - var9);
                  float var34 = (float)(var28 - var9);
                  float var35 = -var30 / 2.0F / var11 + 0.5F;
                  float var36 = -var31 / 2.0F / var11 + 0.5F;
                  float var37 = -var33 / 2.0F / var11 + 0.5F;
                  float var38 = -var34 / 2.0F / var11 + 0.5F;
                  shadowVertex(var0, var1, var18, var30, var32, var33, var35, var37);
                  shadowVertex(var0, var1, var18, var30, var32, var34, var35, var38);
                  shadowVertex(var0, var1, var18, var31, var32, var34, var36, var38);
                  shadowVertex(var0, var1, var18, var31, var32, var33, var36, var37);
               }

            }
         }
      }
   }

   private static void shadowVertex(PoseStack.Pose var0, VertexConsumer var1, int var2, float var3, float var4, float var5, float var6, float var7) {
      Vector3f var8 = var0.pose().transformPosition(var3, var4, var5, new Vector3f());
      var1.addVertex(var8.x(), var8.y(), var8.z(), var2, var6, var7, OverlayTexture.NO_OVERLAY, 15728880, 0.0F, 1.0F, 0.0F);
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

   public Quaternionf cameraOrientation() {
      return this.cameraOrientation;
   }

   public ItemInHandRenderer getItemInHandRenderer() {
      return this.itemInHandRenderer;
   }

   public void onResourceManagerReload(ResourceManager var1) {
      EntityRendererProvider.Context var2 = new EntityRendererProvider.Context(this, this.itemRenderer, this.blockRenderDispatcher, this.itemInHandRenderer, var1, this.entityModels, this.font);
      this.renderers = EntityRenderers.createEntityRenderers(var2);
      this.playerRenderers = EntityRenderers.createPlayerRenderers(var2);
   }
}
