package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.renderer.culling.Culler;
import net.minecraft.client.renderer.entity.layers.ShulkerHeadLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ShulkerRenderer extends MobRenderer<Shulker, ShulkerModel<Shulker>> {
   public static final ResourceLocation DEFAULT_TEXTURE_LOCATION = new ResourceLocation("textures/entity/shulker/shulker.png");
   public static final ResourceLocation[] TEXTURE_LOCATION = new ResourceLocation[]{new ResourceLocation("textures/entity/shulker/shulker_white.png"), new ResourceLocation("textures/entity/shulker/shulker_orange.png"), new ResourceLocation("textures/entity/shulker/shulker_magenta.png"), new ResourceLocation("textures/entity/shulker/shulker_light_blue.png"), new ResourceLocation("textures/entity/shulker/shulker_yellow.png"), new ResourceLocation("textures/entity/shulker/shulker_lime.png"), new ResourceLocation("textures/entity/shulker/shulker_pink.png"), new ResourceLocation("textures/entity/shulker/shulker_gray.png"), new ResourceLocation("textures/entity/shulker/shulker_light_gray.png"), new ResourceLocation("textures/entity/shulker/shulker_cyan.png"), new ResourceLocation("textures/entity/shulker/shulker_purple.png"), new ResourceLocation("textures/entity/shulker/shulker_blue.png"), new ResourceLocation("textures/entity/shulker/shulker_brown.png"), new ResourceLocation("textures/entity/shulker/shulker_green.png"), new ResourceLocation("textures/entity/shulker/shulker_red.png"), new ResourceLocation("textures/entity/shulker/shulker_black.png")};

   public ShulkerRenderer(EntityRenderDispatcher var1) {
      super(var1, new ShulkerModel(), 0.0F);
      this.addLayer(new ShulkerHeadLayer(this));
   }

   public void render(Shulker var1, double var2, double var4, double var6, float var8, float var9) {
      int var10 = var1.getClientSideTeleportInterpolation();
      if (var10 > 0 && var1.hasValidInterpolationPositions()) {
         BlockPos var11 = var1.getAttachPosition();
         BlockPos var12 = var1.getOldAttachPosition();
         double var13 = (double)((float)var10 - var9) / 6.0D;
         var13 *= var13;
         double var15 = (double)(var11.getX() - var12.getX()) * var13;
         double var17 = (double)(var11.getY() - var12.getY()) * var13;
         double var19 = (double)(var11.getZ() - var12.getZ()) * var13;
         super.render((Mob)var1, var2 - var15, var4 - var17, var6 - var19, var8, var9);
      } else {
         super.render((Mob)var1, var2, var4, var6, var8, var9);
      }

   }

   public boolean shouldRender(Shulker var1, Culler var2, double var3, double var5, double var7) {
      if (super.shouldRender((Mob)var1, var2, var3, var5, var7)) {
         return true;
      } else {
         if (var1.getClientSideTeleportInterpolation() > 0 && var1.hasValidInterpolationPositions()) {
            BlockPos var9 = var1.getOldAttachPosition();
            BlockPos var10 = var1.getAttachPosition();
            Vec3 var11 = new Vec3((double)var10.getX(), (double)var10.getY(), (double)var10.getZ());
            Vec3 var12 = new Vec3((double)var9.getX(), (double)var9.getY(), (double)var9.getZ());
            if (var2.isVisible(new AABB(var12.x, var12.y, var12.z, var11.x, var11.y, var11.z))) {
               return true;
            }
         }

         return false;
      }
   }

   protected ResourceLocation getTextureLocation(Shulker var1) {
      return var1.getColor() == null ? DEFAULT_TEXTURE_LOCATION : TEXTURE_LOCATION[var1.getColor().getId()];
   }

   protected void setupRotations(Shulker var1, float var2, float var3, float var4) {
      super.setupRotations(var1, var2, var3, var4);
      switch(var1.getAttachFace()) {
      case DOWN:
      default:
         break;
      case EAST:
         GlStateManager.translatef(0.5F, 0.5F, 0.0F);
         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
         break;
      case WEST:
         GlStateManager.translatef(-0.5F, 0.5F, 0.0F);
         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(-90.0F, 0.0F, 0.0F, 1.0F);
         break;
      case NORTH:
         GlStateManager.translatef(0.0F, 0.5F, -0.5F);
         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         break;
      case SOUTH:
         GlStateManager.translatef(0.0F, 0.5F, 0.5F);
         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
         break;
      case UP:
         GlStateManager.translatef(0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
      }

   }

   protected void scale(Shulker var1, float var2) {
      float var3 = 0.999F;
      GlStateManager.scalef(0.999F, 0.999F, 0.999F);
   }
}
