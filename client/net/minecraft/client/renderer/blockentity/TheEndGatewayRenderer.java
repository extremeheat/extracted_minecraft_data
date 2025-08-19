package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.phys.Vec3;

public class TheEndGatewayRenderer extends TheEndPortalRenderer<TheEndGatewayBlockEntity> {
   private static final ResourceLocation BEAM_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/end_gateway_beam.png");

   public TheEndGatewayRenderer(BlockEntityRendererProvider.Context var1) {
      super(var1);
   }

   public void submit(TheEndGatewayBlockEntity var1, float var2, PoseStack var3, int var4, int var5, Vec3 var6, @Nullable ModelFeatureRenderer.CrumblingOverlay var7, SubmitNodeCollector var8) {
      if (var1.isSpawning() || var1.isCoolingDown()) {
         float var9 = var1.isSpawning() ? var1.getSpawnPercent(var2) : var1.getCooldownPercent(var2);
         double var10 = var1.isSpawning() ? (double)var1.getLevel().getMaxY() : 50.0;
         var9 = Mth.sin(var9 * 3.1415927F);
         int var12 = Mth.floor((double)var9 * var10);
         int var13 = var1.isSpawning() ? DyeColor.MAGENTA.getTextureDiffuseColor() : DyeColor.PURPLE.getTextureDiffuseColor();
         long var14 = var1.getLevel().getGameTime();
         BeaconRenderer.submitBeaconBeam(var3, var8, BEAM_LOCATION, var2, var9, var14, -var12, var12 * 2, var13, 0.15F, 0.175F);
      }

      super.submit(var1, var2, var3, var4, var5, var6, var7, var8);
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
