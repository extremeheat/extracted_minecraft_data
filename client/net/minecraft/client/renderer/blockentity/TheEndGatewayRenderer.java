package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;

public class TheEndGatewayRenderer extends TheEndPortalRenderer<TheEndGatewayBlockEntity> {
   private static final ResourceLocation BEAM_LOCATION = new ResourceLocation("textures/entity/end_gateway_beam.png");

   public TheEndGatewayRenderer(BlockEntityRendererProvider.Context var1) {
      super(var1);
   }

   public void render(TheEndGatewayBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      if (var1.isSpawning() || var1.isCoolingDown()) {
         float var7 = var1.isSpawning() ? var1.getSpawnPercent(var2) : var1.getCooldownPercent(var2);
         double var8 = var1.isSpawning() ? (double)var1.getLevel().getMaxBuildHeight() : 50.0D;
         var7 = Mth.sin(var7 * 3.1415927F);
         int var10 = Mth.floor((double)var7 * var8);
         float[] var11 = var1.isSpawning() ? DyeColor.MAGENTA.getTextureDiffuseColors() : DyeColor.PURPLE.getTextureDiffuseColors();
         long var12 = var1.getLevel().getGameTime();
         BeaconRenderer.renderBeaconBeam(var3, var4, BEAM_LOCATION, var2, var7, var12, -var10, var10 * 2, var11, 0.15F, 0.175F);
      }

      super.render((TheEndPortalBlockEntity)var1, var2, var3, var4, var5, var6);
   }

   protected float getOffsetUp() {
      return 1.0F;
   }

   protected float getOffsetDown() {
      return 0.0F;
   }

   protected RenderType renderType() {
      return RenderType.endGateway();
   }

   public int getViewDistance() {
      return 256;
   }
}
