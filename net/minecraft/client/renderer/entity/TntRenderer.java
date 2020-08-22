package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.block.Blocks;

public class TntRenderer extends EntityRenderer {
   public TntRenderer(EntityRenderDispatcher var1) {
      super(var1);
      this.shadowRadius = 0.5F;
   }

   public void render(PrimedTnt var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      var4.pushPose();
      var4.translate(0.0D, 0.5D, 0.0D);
      if ((float)var1.getLife() - var3 + 1.0F < 10.0F) {
         float var7 = 1.0F - ((float)var1.getLife() - var3 + 1.0F) / 10.0F;
         var7 = Mth.clamp(var7, 0.0F, 1.0F);
         var7 *= var7;
         var7 *= var7;
         float var8 = 1.0F + var7 * 0.3F;
         var4.scale(var8, var8, var8);
      }

      var4.mulPose(Vector3f.YP.rotationDegrees(-90.0F));
      var4.translate(-0.5D, -0.5D, 0.5D);
      var4.mulPose(Vector3f.YP.rotationDegrees(90.0F));
      TntMinecartRenderer.renderWhiteSolidBlock(Blocks.TNT.defaultBlockState(), var4, var5, var6, var1.getLife() / 5 % 2 == 0);
      var4.popPose();
      super.render(var1, var2, var3, var4, var5, var6);
   }

   public ResourceLocation getTextureLocation(PrimedTnt var1) {
      return TextureAtlas.LOCATION_BLOCKS;
   }
}
