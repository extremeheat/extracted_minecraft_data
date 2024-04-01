package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LashingPotatoHook;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public class LashingPotatoHookRenderer extends EntityRenderer<LashingPotatoHook> {
   private final ItemRenderer itemRenderer;

   public LashingPotatoHookRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.itemRenderer = var1.getItemRenderer();
   }

   public void render(LashingPotatoHook var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      Player var7 = var1.getPlayerOwner();
      if (var7 != null) {
         var4.pushPose();
         this.itemRenderer
            .renderStatic(
               new ItemStack(Items.POISONOUS_POTATO), ItemDisplayContext.GROUND, var6, OverlayTexture.NO_OVERLAY, var4, var5, var1.level(), var1.getId()
            );
         Vec3 var8 = FishingHookRenderer.getPlayerHandPos(var7, var3, Items.LASHING_POTATO, this.entityRenderDispatcher);
         Vec3 var9 = new Vec3(
            Mth.lerp((double)var3, var1.xo, var1.getX()),
            Mth.lerp((double)var3, var1.yo, var1.getY()) + (double)var1.getEyeHeight(),
            Mth.lerp((double)var3, var1.zo, var1.getZ())
         );
         float var10 = (float)var1.tickCount + var3;
         float var11 = var10 * 0.15F % 1.0F;
         Vec3 var12 = var8.subtract(var9);
         float var13 = (float)(var12.length() + 0.1);
         var12 = var12.normalize();
         float var14 = (float)Math.acos(var12.y);
         float var15 = (float)Math.atan2(var12.z, var12.x);
         var4.mulPose(Axis.YP.rotationDegrees((1.5707964F - var15) * 57.295776F));
         var4.mulPose(Axis.XP.rotationDegrees(var14 * 57.295776F));
         float var16 = var10 * 0.05F * -1.5F;
         float var17 = 0.2F;
         float var18 = Mth.cos(var16 + 3.1415927F) * 0.2F;
         float var19 = Mth.sin(var16 + 3.1415927F) * 0.2F;
         float var20 = Mth.cos(var16 + 0.0F) * 0.2F;
         float var21 = Mth.sin(var16 + 0.0F) * 0.2F;
         float var22 = Mth.cos(var16 + 1.5707964F) * 0.2F;
         float var23 = Mth.sin(var16 + 1.5707964F) * 0.2F;
         float var24 = Mth.cos(var16 + 4.712389F) * 0.2F;
         float var25 = Mth.sin(var16 + 4.712389F) * 0.2F;
         float var27 = 0.0F;
         float var28 = 0.4999F;
         float var29 = -1.0F + var11;
         float var30 = var13 * 2.5F + var29;
         VertexConsumer var31 = var5.getBuffer(RenderType.entityCutoutNoCull(GuardianRenderer.TOXIFIN_BEAM_LOCATION));
         PoseStack.Pose var32 = var4.last();
         vertex(var31, var32, var18, var13, var19, 0.4999F, var30);
         vertex(var31, var32, var18, 0.0F, var19, 0.4999F, var29);
         vertex(var31, var32, var20, 0.0F, var21, 0.0F, var29);
         vertex(var31, var32, var20, var13, var21, 0.0F, var30);
         vertex(var31, var32, var22, var13, var23, 0.4999F, var30);
         vertex(var31, var32, var22, 0.0F, var23, 0.4999F, var29);
         vertex(var31, var32, var24, 0.0F, var25, 0.0F, var29);
         vertex(var31, var32, var24, var13, var25, 0.0F, var30);
         var4.popPose();
         super.render(var1, var2, var3, var4, var5, var6);
      }
   }

   private static void vertex(VertexConsumer var0, PoseStack.Pose var1, float var2, float var3, float var4, float var5, float var6) {
      var0.vertex(var1, var2, var3, var4)
         .color(128, 255, 128, 255)
         .uv(var5, var6)
         .overlayCoords(OverlayTexture.NO_OVERLAY)
         .uv2(15728880)
         .normal(0.0F, 1.0F, 0.0F)
         .endVertex();
   }

   public ResourceLocation getTextureLocation(LashingPotatoHook var1) {
      return TextureAtlas.LOCATION_BLOCKS;
   }
}
