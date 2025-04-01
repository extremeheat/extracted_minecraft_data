package net.minecraft.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Map;
import java.util.function.Supplier;
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
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MapRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HitboxRenderState;
import net.minecraft.client.renderer.entity.state.HitboxesRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.entity.state.ServerHitboxesRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
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
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class EntityRenderDispatcher implements ResourceManagerReloadListener {
   private static final RenderType SHADOW_RENDER_TYPE = RenderType.entityShadow(ResourceLocation.withDefaultNamespace("textures/misc/shadow.png"));
   private static final float MAX_SHADOW_RADIUS = 32.0F;
   private static final float SHADOW_POWER_FALLOFF_Y = 0.5F;
   private Map<EntityType<?>, EntityRenderer<?, ?>> renderers = ImmutableMap.of();
   private Map<PlayerSkin.Model, EntityRenderer<? extends Player, ?>> playerRenderers = Map.of();
   public final TextureManager textureManager;
   private Level level;
   public Camera camera;
   private Quaternionf cameraOrientation;
   public Entity crosshairPickEntity;
   private final ItemModelResolver itemModelResolver;
   private final MapRenderer mapRenderer;
   private final BlockRenderDispatcher blockRenderDispatcher;
   private final ItemInHandRenderer itemInHandRenderer;
   private final Font font;
   public final Options options;
   private final Supplier<EntityModelSet> entityModels;
   private final EquipmentAssetManager equipmentAssets;
   private boolean shouldRenderShadow = true;
   private boolean renderHitBoxes;

   public <E extends Entity> int getPackedLightCoords(E var1, float var2) {
      return this.getRenderer(var1).getPackedLightCoords(var1, var2);
   }

   public EntityRenderDispatcher(Minecraft var1, TextureManager var2, ItemModelResolver var3, ItemRenderer var4, MapRenderer var5, BlockRenderDispatcher var6, Font var7, Options var8, Supplier<EntityModelSet> var9, EquipmentAssetManager var10) {
      super();
      this.textureManager = var2;
      this.itemModelResolver = var3;
      this.mapRenderer = var5;
      this.itemInHandRenderer = new ItemInHandRenderer(var1, this, var4, var3);
      this.blockRenderDispatcher = var6;
      this.font = var7;
      this.options = var8;
      this.entityModels = var9;
      this.equipmentAssets = var10;
   }

   public <T extends Entity> EntityRenderer<? super T, ?> getRenderer(T var1) {
      if (var1 instanceof AbstractClientPlayer var5) {
         PlayerSkin.Model var3 = var5.getSkin().model();
         EntityRenderer var4 = (EntityRenderer)this.playerRenderers.get(var3);
         return var4 != null ? var4 : (EntityRenderer)this.playerRenderers.get(PlayerSkin.Model.WIDE);
      } else {
         EntityType var2 = var1.getType();
         return this.getRenderer(var2);
      }
   }

   @Nullable
   public <T extends Entity> EntityRenderer<? super T, ?> getRenderer(EntityType<?> var1) {
      return (EntityRenderer)this.renderers.get(var1);
   }

   public <S extends EntityRenderState> EntityRenderer<?, ? super S> getRenderer(S var1) {
      if (var1 instanceof PlayerRenderState var2) {
         PlayerSkin.Model var3 = var2.skin.model();
         EntityRenderer var4 = (EntityRenderer)this.playerRenderers.get(var3);
         return var4 != null ? var4 : (EntityRenderer)this.playerRenderers.get(PlayerSkin.Model.WIDE);
      } else {
         return (EntityRenderer)this.renderers.get(var1.entityType);
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

   public <E extends Entity> void render(E var1, double var2, double var4, double var6, float var8, PoseStack var9, MultiBufferSource var10, int var11) {
      EntityRenderer var12 = this.getRenderer(var1);
      this.render(var1, var2, var4, var6, var8, var9, var10, var11, var12);
   }

   private <E extends Entity, S extends EntityRenderState> void render(E var1, double var2, double var4, double var6, float var8, PoseStack var9, MultiBufferSource var10, int var11, EntityRenderer<? super E, S> var12) {
      EntityRenderState var13;
      try {
         var13 = var12.createRenderState(var1, var8);
      } catch (Throwable var19) {
         CrashReport var15 = CrashReport.forThrowable(var19, "Extracting render state for an entity in world");
         CrashReportCategory var16 = var15.addCategory("Entity being extracted");
         var1.fillCrashReportCategory(var16);
         CrashReportCategory var17 = this.fillRendererDetails(var2, var4, var6, var12, var15);
         var17.setDetail("Delta", var8);
         throw new ReportedException(var15);
      }

      try {
         this.render(var13, var2, var4, var6, var9, var10, var11, var12);
      } catch (Throwable var18) {
         CrashReport var20 = CrashReport.forThrowable(var18, "Rendering entity in world");
         CrashReportCategory var21 = var20.addCategory("Entity being rendered");
         var1.fillCrashReportCategory(var21);
         throw new ReportedException(var20);
      }
   }

   public <S extends EntityRenderState> void render(S var1, double var2, double var4, double var6, PoseStack var8, MultiBufferSource var9, int var10) {
      EntityRenderer var11 = this.getRenderer(var1);
      this.render(var1, var2, var4, var6, var8, var9, var10, var11);
   }

   private <S extends EntityRenderState> void render(S var1, double var2, double var4, double var6, PoseStack var8, MultiBufferSource var9, int var10, EntityRenderer<?, S> var11) {
      try {
         Vec3 var12 = var11.getRenderOffset(var1);
         double var24 = var2 + var12.x();
         double var15 = var4 + var12.y();
         double var17 = var6 + var12.z();
         var8.pushPose();
         var8.translate(var24, var15, var17);
         var11.render(var1, var8, var9, var10);
         if (var1.displayFireAnimation) {
            this.renderFlame(var8, var9, var1, Mth.rotationAroundAxis(Mth.Y_AXIS, this.cameraOrientation, new Quaternionf()));
         }

         if (var1 instanceof PlayerRenderState) {
            var8.translate(-var12.x(), -var12.y(), -var12.z());
         }

         if ((Boolean)this.options.entityShadows().get() && this.shouldRenderShadow && !var1.isInvisible) {
            float var19 = var11.getShadowRadius(var1);
            if (var19 > 0.0F) {
               double var20 = var1.distanceToCameraSq;
               float var22 = (float)((1.0 - var20 / 256.0) * (double)var11.getShadowStrength(var1));
               if (var22 > 0.0F) {
                  renderShadow(var8, var9, var1, var22, this.level, Math.min(var19, 32.0F));
               }
            }
         }

         if (!(var1 instanceof PlayerRenderState)) {
            var8.translate(-var12.x(), -var12.y(), -var12.z());
         }

         if (var1.hitboxesRenderState != null) {
            this.renderHitboxes(var8, var1, var1.hitboxesRenderState, var9);
         }

         var8.popPose();
      } catch (Throwable var23) {
         CrashReport var13 = CrashReport.forThrowable(var23, "Rendering entity in world");
         CrashReportCategory var14 = var13.addCategory("EntityRenderState being rendered");
         var1.fillCrashReportCategory(var14);
         this.fillRendererDetails(var2, var4, var6, var11, var13);
         throw new ReportedException(var13);
      }
   }

   private <S extends EntityRenderState> CrashReportCategory fillRendererDetails(double var1, double var3, double var5, EntityRenderer<?, S> var7, CrashReport var8) {
      CrashReportCategory var9 = var8.addCategory("Renderer details");
      var9.setDetail("Assigned renderer", var7);
      var9.setDetail("Location", CrashReportCategory.formatLocation(this.level, var1, var3, var5));
      return var9;
   }

   private void renderHitboxes(PoseStack var1, EntityRenderState var2, HitboxesRenderState var3, MultiBufferSource var4) {
      VertexConsumer var5 = var4.getBuffer(RenderType.lines());
      renderHitboxesAndViewVector(var1, var3, var5, var2.eyeHeight);
      ServerHitboxesRenderState var6 = var2.serverHitboxesRenderState;
      if (var6 != null) {
         if (var6.missing()) {
            HitboxRenderState var7 = (HitboxRenderState)var3.hitboxes().getFirst();
            DebugRenderer.renderFloatingText(var1, var4, "Missing", var2.x, var7.y1() + 1.5, var2.z, -65536);
         } else if (var6.hitboxes() != null) {
            var1.pushPose();
            var1.translate(var6.serverEntityX() - var2.x, var6.serverEntityY() - var2.y, var6.serverEntityZ() - var2.z);
            renderHitboxesAndViewVector(var1, var6.hitboxes(), var5, var6.eyeHeight());
            Vec3 var8 = new Vec3(var6.deltaMovementX(), var6.deltaMovementY(), var6.deltaMovementZ());
            ShapeRenderer.renderVector(var1, var5, new Vector3f(), var8, -256);
            var1.popPose();
         }
      }

   }

   private static void renderHitboxesAndViewVector(PoseStack var0, HitboxesRenderState var1, VertexConsumer var2, float var3) {
      UnmodifiableIterator var4 = var1.hitboxes().iterator();

      while(var4.hasNext()) {
         HitboxRenderState var5 = (HitboxRenderState)var4.next();
         renderHitbox(var0, var2, var5);
      }

      Vec3 var6 = new Vec3(var1.viewX(), var1.viewY(), var1.viewZ());
      ShapeRenderer.renderVector(var0, var2, new Vector3f(0.0F, var3, 0.0F), var6.scale(2.0), -16776961);
   }

   private static void renderHitbox(PoseStack var0, VertexConsumer var1, HitboxRenderState var2) {
      var0.pushPose();
      var0.translate(var2.offsetX(), var2.offsetY(), var2.offsetZ());
      ShapeRenderer.renderLineBox(var0, var1, var2.x0(), var2.y0(), var2.z0(), var2.x1(), var2.y1(), var2.z1(), var2.red(), var2.green(), var2.blue(), 1.0F);
      var0.popPose();
   }

   private void renderFlame(PoseStack var1, MultiBufferSource var2, EntityRenderState var3, Quaternionf var4) {
      TextureAtlasSprite var5 = ModelBakery.FIRE_0.sprite();
      TextureAtlasSprite var6 = ModelBakery.FIRE_1.sprite();
      var1.pushPose();
      float var7 = var3.boundingBoxWidth * 1.4F;
      var1.scale(var7, var7, var7);
      float var8 = 0.5F;
      float var9 = 0.0F;
      float var10 = var3.boundingBoxHeight / var7;
      float var11 = 0.0F;
      var1.mulPose((Quaternionfc)var4);
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

   private static void renderShadow(PoseStack var0, MultiBufferSource var1, EntityRenderState var2, float var3, LevelReader var4, float var5) {
      float var6 = Math.min(var3 / 0.5F, var5);
      int var7 = Mth.floor(var2.x - (double)var5);
      int var8 = Mth.floor(var2.x + (double)var5);
      int var9 = Mth.floor(var2.y - (double)var6);
      int var10 = Mth.floor(var2.y);
      int var11 = Mth.floor(var2.z - (double)var5);
      int var12 = Mth.floor(var2.z + (double)var5);
      PoseStack.Pose var13 = var0.last();
      VertexConsumer var14 = var1.getBuffer(SHADOW_RENDER_TYPE);
      BlockPos.MutableBlockPos var15 = new BlockPos.MutableBlockPos();

      for(int var16 = var11; var16 <= var12; ++var16) {
         for(int var17 = var7; var17 <= var8; ++var17) {
            var15.set(var17, 0, var16);
            ChunkAccess var18 = var4.getChunk(var15);

            for(int var19 = var9; var19 <= var10; ++var19) {
               var15.setY(var19);
               float var20 = var3 - (float)(var2.y - (double)var15.getY()) * 0.5F;
               renderBlockShadow(var13, var14, var18, var4, var15, var2.x, var2.y, var2.z, var5, var20);
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

                  int var18 = ARGB.color(Mth.floor(var17 * 255.0F), 255, 255, 255);
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
      EntityRendererProvider.Context var2 = new EntityRendererProvider.Context(this, this.itemModelResolver, this.mapRenderer, this.blockRenderDispatcher, var1, (EntityModelSet)this.entityModels.get(), this.equipmentAssets, this.font);
      this.renderers = EntityRenderers.createEntityRenderers(var2);
      this.playerRenderers = EntityRenderers.createPlayerRenderers(var2);
   }
}
