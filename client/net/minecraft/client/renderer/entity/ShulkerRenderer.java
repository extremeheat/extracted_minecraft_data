package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.layers.ShulkerHeadLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ShulkerRenderer extends MobRenderer<Shulker, ShulkerModel<Shulker>> {
   public static final ResourceLocation DEFAULT_TEXTURE_LOCATION;
   public static final ResourceLocation[] TEXTURE_LOCATION;

   public ShulkerRenderer(EntityRendererProvider.Context var1) {
      super(var1, new ShulkerModel(var1.getLayer(ModelLayers.SHULKER)), 0.0F);
      this.addLayer(new ShulkerHeadLayer(this));
   }

   public Vec3 getRenderOffset(Shulker var1, float var2) {
      int var3 = var1.getClientSideTeleportInterpolation();
      if (var3 > 0 && var1.hasValidInterpolationPositions()) {
         BlockPos var4 = var1.getAttachPosition();
         BlockPos var5 = var1.getOldAttachPosition();
         double var6 = (double)((float)var3 - var2) / 6.0D;
         var6 *= var6;
         double var8 = (double)(var4.getX() - var5.getX()) * var6;
         double var10 = (double)(var4.getY() - var5.getY()) * var6;
         double var12 = (double)(var4.getZ() - var5.getZ()) * var6;
         return new Vec3(-var8, -var10, -var12);
      } else {
         return super.getRenderOffset(var1, var2);
      }
   }

   public boolean shouldRender(Shulker var1, Frustum var2, double var3, double var5, double var7) {
      if (super.shouldRender((Mob)var1, var2, var3, var5, var7)) {
         return true;
      } else {
         if (var1.getClientSideTeleportInterpolation() > 0 && var1.hasValidInterpolationPositions()) {
            Vec3 var9 = Vec3.atLowerCornerOf(var1.getAttachPosition());
            Vec3 var10 = Vec3.atLowerCornerOf(var1.getOldAttachPosition());
            if (var2.isVisible(new AABB(var10.x, var10.y, var10.z, var9.x, var9.y, var9.z))) {
               return true;
            }
         }

         return false;
      }
   }

   public ResourceLocation getTextureLocation(Shulker var1) {
      return var1.getColor() == null ? DEFAULT_TEXTURE_LOCATION : TEXTURE_LOCATION[var1.getColor().getId()];
   }

   protected void setupRotations(Shulker var1, PoseStack var2, float var3, float var4, float var5) {
      super.setupRotations(var1, var2, var3, var4 + 180.0F, var5);
      var2.translate(0.0D, 0.5D, 0.0D);
      var2.mulPose(var1.getAttachFace().getOpposite().getRotation());
      var2.translate(0.0D, -0.5D, 0.0D);
   }

   static {
      DEFAULT_TEXTURE_LOCATION = new ResourceLocation("textures/" + Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION.texture().getPath() + ".png");
      TEXTURE_LOCATION = (ResourceLocation[])Sheets.SHULKER_TEXTURE_LOCATION.stream().map((var0) -> {
         return new ResourceLocation("textures/" + var0.texture().getPath() + ".png");
      }).toArray((var0) -> {
         return new ResourceLocation[var0];
      });
   }
}
