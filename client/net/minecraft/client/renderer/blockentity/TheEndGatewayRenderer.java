package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;

public class TheEndGatewayRenderer extends TheEndPortalRenderer {
   private static final ResourceLocation BEAM_LOCATION = new ResourceLocation("textures/entity/end_gateway_beam.png");

   public TheEndGatewayRenderer() {
      super();
   }

   public void render(TheEndPortalBlockEntity var1, double var2, double var4, double var6, float var8, int var9) {
      GlStateManager.disableFog();
      TheEndGatewayBlockEntity var10 = (TheEndGatewayBlockEntity)var1;
      if (var10.isSpawning() || var10.isCoolingDown()) {
         GlStateManager.alphaFunc(516, 0.1F);
         this.bindTexture(BEAM_LOCATION);
         float var11 = var10.isSpawning() ? var10.getSpawnPercent(var8) : var10.getCooldownPercent(var8);
         double var12 = var10.isSpawning() ? 256.0D - var4 : 50.0D;
         var11 = Mth.sin(var11 * 3.1415927F);
         int var14 = Mth.floor((double)var11 * var12);
         float[] var15 = var10.isSpawning() ? DyeColor.MAGENTA.getTextureDiffuseColors() : DyeColor.PURPLE.getTextureDiffuseColors();
         BeaconRenderer.renderBeaconBeam(var2, var4, var6, (double)var8, (double)var11, var10.getLevel().getGameTime(), 0, var14, var15, 0.15D, 0.175D);
         BeaconRenderer.renderBeaconBeam(var2, var4, var6, (double)var8, (double)var11, var10.getLevel().getGameTime(), 0, -var14, var15, 0.15D, 0.175D);
      }

      super.render(var1, var2, var4, var6, var8, var9);
      GlStateManager.enableFog();
   }

   protected int getPasses(double var1) {
      return super.getPasses(var1) + 1;
   }

   protected float getOffset() {
      return 1.0F;
   }
}
