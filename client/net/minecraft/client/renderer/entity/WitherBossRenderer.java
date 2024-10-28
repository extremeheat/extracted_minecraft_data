package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.WitherBossModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.WitherArmorLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.boss.wither.WitherBoss;

public class WitherBossRenderer extends MobRenderer<WitherBoss, WitherBossModel<WitherBoss>> {
   private static final ResourceLocation WITHER_INVULNERABLE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/wither/wither_invulnerable.png");
   private static final ResourceLocation WITHER_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/wither/wither.png");

   public WitherBossRenderer(EntityRendererProvider.Context var1) {
      super(var1, new WitherBossModel(var1.bakeLayer(ModelLayers.WITHER)), 1.0F);
      this.addLayer(new WitherArmorLayer(this, var1.getModelSet()));
   }

   protected int getBlockLightLevel(WitherBoss var1, BlockPos var2) {
      return 15;
   }

   public ResourceLocation getTextureLocation(WitherBoss var1) {
      int var2 = var1.getInvulnerableTicks();
      return var2 > 0 && (var2 > 80 || var2 / 5 % 2 != 1) ? WITHER_INVULNERABLE_LOCATION : WITHER_LOCATION;
   }

   protected void scale(WitherBoss var1, PoseStack var2, float var3) {
      float var4 = 2.0F;
      int var5 = var1.getInvulnerableTicks();
      if (var5 > 0) {
         var4 -= ((float)var5 - var3) / 220.0F * 0.5F;
      }

      var2.scale(var4, var4, var4);
   }
}
