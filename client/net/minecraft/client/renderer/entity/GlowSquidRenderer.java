package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.SquidModel;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.SquidRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.GlowSquid;

public class GlowSquidRenderer extends SquidRenderer<GlowSquid> {
   private static final Identifier GLOW_SQUID_LOCATION = Identifier.withDefaultNamespace("textures/entity/squid/glow_squid.png");

   public GlowSquidRenderer(EntityRendererProvider.Context var1, SquidModel var2, SquidModel var3) {
      super(var1, var2, var3);
   }

   public Identifier getTextureLocation(SquidRenderState var1) {
      return GLOW_SQUID_LOCATION;
   }

   protected int getBlockLightLevel(GlowSquid var1, BlockPos var2) {
      int var3 = (int)Mth.clampedLerp(1.0F - (float)var1.getDarkTicksRemaining() / 10.0F, 0.0F, 15.0F);
      return var3 == 15 ? 15 : Math.max(var3, super.getBlockLightLevel(var1, var2));
   }

   // $FF: synthetic method
   public Identifier getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((SquidRenderState)var1);
   }
}
