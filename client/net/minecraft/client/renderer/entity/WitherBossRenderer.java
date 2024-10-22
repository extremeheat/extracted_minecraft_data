package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.WitherBossModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.WitherArmorLayer;
import net.minecraft.client.renderer.entity.state.WitherRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.boss.wither.WitherBoss;

public class WitherBossRenderer extends MobRenderer<WitherBoss, WitherRenderState, WitherBossModel> {
   private static final ResourceLocation WITHER_INVULNERABLE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/wither/wither_invulnerable.png");
   private static final ResourceLocation WITHER_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/wither/wither.png");

   public WitherBossRenderer(EntityRendererProvider.Context var1) {
      super(var1, new WitherBossModel(var1.bakeLayer(ModelLayers.WITHER)), 1.0F);
      this.addLayer(new WitherArmorLayer(this, var1.getModelSet()));
   }

   protected int getBlockLightLevel(WitherBoss var1, BlockPos var2) {
      return 15;
   }

   public ResourceLocation getTextureLocation(WitherRenderState var1) {
      int var2 = Mth.floor(var1.invulnerableTicks);
      return var2 > 0 && (var2 > 80 || var2 / 5 % 2 != 1) ? WITHER_INVULNERABLE_LOCATION : WITHER_LOCATION;
   }

   public WitherRenderState createRenderState() {
      return new WitherRenderState();
   }

   protected void scale(WitherRenderState var1, PoseStack var2) {
      float var3 = 2.0F;
      if (var1.invulnerableTicks > 0.0F) {
         var3 -= var1.invulnerableTicks / 220.0F * 0.5F;
      }

      var2.scale(var3, var3, var3);
   }

   public void extractRenderState(WitherBoss var1, WitherRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      int var4 = var1.getInvulnerableTicks();
      var2.invulnerableTicks = var4 > 0 ? (float)var4 - var3 : 0.0F;
      System.arraycopy(var1.getHeadXRots(), 0, var2.xHeadRots, 0, var2.xHeadRots.length);
      System.arraycopy(var1.getHeadYRots(), 0, var2.yHeadRots, 0, var2.yHeadRots.length);
      var2.isPowered = var1.isPowered();
   }
}
