package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.fishing.FishingHook;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public class FishingHookRenderer extends EntityRenderer<FishingHook> {
   private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/fishing_hook.png");

   public FishingHookRenderer(EntityRenderDispatcher var1) {
      super(var1);
   }

   public void render(FishingHook var1, double var2, double var4, double var6, float var8, float var9) {
      Player var10 = var1.getOwner();
      if (var10 != null && !this.solidRender) {
         GlStateManager.pushMatrix();
         GlStateManager.translatef((float)var2, (float)var4, (float)var6);
         GlStateManager.enableRescaleNormal();
         GlStateManager.scalef(0.5F, 0.5F, 0.5F);
         this.bindTexture(var1);
         Tesselator var11 = Tesselator.getInstance();
         BufferBuilder var12 = var11.getBuilder();
         float var13 = 1.0F;
         float var14 = 0.5F;
         float var15 = 0.5F;
         GlStateManager.rotatef(180.0F - this.entityRenderDispatcher.playerRotY, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef((float)(this.entityRenderDispatcher.options.thirdPersonView == 2 ? -1 : 1) * -this.entityRenderDispatcher.playerRotX, 1.0F, 0.0F, 0.0F);
         if (this.solidRender) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(var1));
         }

         var12.begin(7, DefaultVertexFormat.POSITION_TEX_NORMAL);
         var12.vertex(-0.5D, -0.5D, 0.0D).uv(0.0D, 1.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
         var12.vertex(0.5D, -0.5D, 0.0D).uv(1.0D, 1.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
         var12.vertex(0.5D, 0.5D, 0.0D).uv(1.0D, 0.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
         var12.vertex(-0.5D, 0.5D, 0.0D).uv(0.0D, 0.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
         var11.end();
         if (this.solidRender) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
         }

         GlStateManager.disableRescaleNormal();
         GlStateManager.popMatrix();
         int var16 = var10.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
         ItemStack var17 = var10.getMainHandItem();
         if (var17.getItem() != Items.FISHING_ROD) {
            var16 = -var16;
         }

         float var18 = var10.getAttackAnim(var9);
         float var19 = Mth.sin(Mth.sqrt(var18) * 3.1415927F);
         float var20 = Mth.lerp(var9, var10.yBodyRotO, var10.yBodyRot) * 0.017453292F;
         double var21 = (double)Mth.sin(var20);
         double var23 = (double)Mth.cos(var20);
         double var25 = (double)var16 * 0.35D;
         double var27 = 0.8D;
         double var29;
         double var31;
         double var33;
         double var35;
         double var37;
         if ((this.entityRenderDispatcher.options == null || this.entityRenderDispatcher.options.thirdPersonView <= 0) && var10 == Minecraft.getInstance().player) {
            var37 = this.entityRenderDispatcher.options.fov;
            var37 /= 100.0D;
            Vec3 var39 = new Vec3((double)var16 * -0.36D * var37, -0.045D * var37, 0.4D);
            var39 = var39.xRot(-Mth.lerp(var9, var10.xRotO, var10.xRot) * 0.017453292F);
            var39 = var39.yRot(-Mth.lerp(var9, var10.yRotO, var10.yRot) * 0.017453292F);
            var39 = var39.yRot(var19 * 0.5F);
            var39 = var39.xRot(-var19 * 0.7F);
            var29 = Mth.lerp((double)var9, var10.xo, var10.x) + var39.x;
            var31 = Mth.lerp((double)var9, var10.yo, var10.y) + var39.y;
            var33 = Mth.lerp((double)var9, var10.zo, var10.z) + var39.z;
            var35 = (double)var10.getEyeHeight();
         } else {
            var29 = Mth.lerp((double)var9, var10.xo, var10.x) - var23 * var25 - var21 * 0.8D;
            var31 = var10.yo + (double)var10.getEyeHeight() + (var10.y - var10.yo) * (double)var9 - 0.45D;
            var33 = Mth.lerp((double)var9, var10.zo, var10.z) - var21 * var25 + var23 * 0.8D;
            var35 = var10.isVisuallySneaking() ? -0.1875D : 0.0D;
         }

         var37 = Mth.lerp((double)var9, var1.xo, var1.x);
         double var52 = Mth.lerp((double)var9, var1.yo, var1.y) + 0.25D;
         double var41 = Mth.lerp((double)var9, var1.zo, var1.z);
         double var43 = (double)((float)(var29 - var37));
         double var45 = (double)((float)(var31 - var52)) + var35;
         double var47 = (double)((float)(var33 - var41));
         GlStateManager.disableTexture();
         GlStateManager.disableLighting();
         var12.begin(3, DefaultVertexFormat.POSITION_COLOR);
         boolean var49 = true;

         for(int var50 = 0; var50 <= 16; ++var50) {
            float var51 = (float)var50 / 16.0F;
            var12.vertex(var2 + var43 * (double)var51, var4 + var45 * (double)(var51 * var51 + var51) * 0.5D + 0.25D, var6 + var47 * (double)var51).color(0, 0, 0, 255).endVertex();
         }

         var11.end();
         GlStateManager.enableLighting();
         GlStateManager.enableTexture();
         super.render(var1, var2, var4, var6, var8, var9);
      }
   }

   protected ResourceLocation getTextureLocation(FishingHook var1) {
      return TEXTURE_LOCATION;
   }
}
