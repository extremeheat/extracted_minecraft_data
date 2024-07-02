package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.SquidModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.GlowSquid;

public class GlowSquidRenderer extends SquidRenderer<GlowSquid> {
   private static final ResourceLocation GLOW_SQUID_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/squid/glow_squid.png");

   public GlowSquidRenderer(EntityRendererProvider.Context var1, SquidModel<GlowSquid> var2) {
      super(var1, var2);
   }

   public ResourceLocation getTextureLocation(GlowSquid var1) {
      return GLOW_SQUID_LOCATION;
   }

   protected int getBlockLightLevel(GlowSquid var1, BlockPos var2) {
      int var3 = (int)Mth.clampedLerp(0.0F, 15.0F, 1.0F - (float)var1.getDarkTicksRemaining() / 10.0F);
      return var3 == 15 ? 15 : Math.max(var3, super.getBlockLightLevel(var1, var2));
   }
}
