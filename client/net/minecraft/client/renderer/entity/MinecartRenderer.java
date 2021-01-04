package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.MinecartModel;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class MinecartRenderer<T extends AbstractMinecart> extends EntityRenderer<T> {
   private static final ResourceLocation MINECART_LOCATION = new ResourceLocation("textures/entity/minecart.png");
   protected final EntityModel<T> model = new MinecartModel();

   public MinecartRenderer(EntityRenderDispatcher var1) {
      super(var1);
      this.shadowRadius = 0.7F;
   }

   public void render(T var1, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.pushMatrix();
      this.bindTexture(var1);
      long var10 = (long)var1.getId() * 493286711L;
      var10 = var10 * var10 * 4392167121L + var10 * 98761L;
      float var12 = (((float)(var10 >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
      float var13 = (((float)(var10 >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
      float var14 = (((float)(var10 >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
      GlStateManager.translatef(var12, var13, var14);
      double var15 = Mth.lerp((double)var9, var1.xOld, var1.x);
      double var17 = Mth.lerp((double)var9, var1.yOld, var1.y);
      double var19 = Mth.lerp((double)var9, var1.zOld, var1.z);
      double var21 = 0.30000001192092896D;
      Vec3 var23 = var1.getPos(var15, var17, var19);
      float var24 = Mth.lerp(var9, var1.xRotO, var1.xRot);
      if (var23 != null) {
         Vec3 var25 = var1.getPosOffs(var15, var17, var19, 0.30000001192092896D);
         Vec3 var26 = var1.getPosOffs(var15, var17, var19, -0.30000001192092896D);
         if (var25 == null) {
            var25 = var23;
         }

         if (var26 == null) {
            var26 = var23;
         }

         var2 += var23.x - var15;
         var4 += (var25.y + var26.y) / 2.0D - var17;
         var6 += var23.z - var19;
         Vec3 var27 = var26.add(-var25.x, -var25.y, -var25.z);
         if (var27.length() != 0.0D) {
            var27 = var27.normalize();
            var8 = (float)(Math.atan2(var27.z, var27.x) * 180.0D / 3.141592653589793D);
            var24 = (float)(Math.atan(var27.y) * 73.0D);
         }
      }

      GlStateManager.translatef((float)var2, (float)var4 + 0.375F, (float)var6);
      GlStateManager.rotatef(180.0F - var8, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(-var24, 0.0F, 0.0F, 1.0F);
      float var30 = (float)var1.getHurtTime() - var9;
      float var31 = var1.getDamage() - var9;
      if (var31 < 0.0F) {
         var31 = 0.0F;
      }

      if (var30 > 0.0F) {
         GlStateManager.rotatef(Mth.sin(var30) * var30 * var31 / 10.0F * (float)var1.getHurtDir(), 1.0F, 0.0F, 0.0F);
      }

      int var32 = var1.getDisplayOffset();
      if (this.solidRender) {
         GlStateManager.enableColorMaterial();
         GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(var1));
      }

      BlockState var28 = var1.getDisplayBlockState();
      if (var28.getRenderShape() != RenderShape.INVISIBLE) {
         GlStateManager.pushMatrix();
         this.bindTexture(TextureAtlas.LOCATION_BLOCKS);
         float var29 = 0.75F;
         GlStateManager.scalef(0.75F, 0.75F, 0.75F);
         GlStateManager.translatef(-0.5F, (float)(var32 - 8) / 16.0F, 0.5F);
         this.renderMinecartContents(var1, var9, var28);
         GlStateManager.popMatrix();
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.bindTexture(var1);
      }

      GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
      this.model.render(var1, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
      GlStateManager.popMatrix();
      if (this.solidRender) {
         GlStateManager.tearDownSolidRenderingTextureCombine();
         GlStateManager.disableColorMaterial();
      }

      super.render(var1, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation getTextureLocation(T var1) {
      return MINECART_LOCATION;
   }

   protected void renderMinecartContents(T var1, float var2, BlockState var3) {
      GlStateManager.pushMatrix();
      Minecraft.getInstance().getBlockRenderer().renderSingleBlock(var3, var1.getBrightness());
      GlStateManager.popMatrix();
   }
}
