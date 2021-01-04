package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.WitherBossModel;
import net.minecraft.client.renderer.entity.layers.WitherArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.boss.wither.WitherBoss;

public class WitherBossRenderer extends MobRenderer<WitherBoss, WitherBossModel<WitherBoss>> {
   private static final ResourceLocation WITHER_INVULNERABLE_LOCATION = new ResourceLocation("textures/entity/wither/wither_invulnerable.png");
   private static final ResourceLocation WITHER_LOCATION = new ResourceLocation("textures/entity/wither/wither.png");

   public WitherBossRenderer(EntityRenderDispatcher var1) {
      super(var1, new WitherBossModel(0.0F), 1.0F);
      this.addLayer(new WitherArmorLayer(this));
   }

   protected ResourceLocation getTextureLocation(WitherBoss var1) {
      int var2 = var1.getInvulnerableTicks();
      return var2 > 0 && (var2 > 80 || var2 / 5 % 2 != 1) ? WITHER_INVULNERABLE_LOCATION : WITHER_LOCATION;
   }

   protected void scale(WitherBoss var1, float var2) {
      float var3 = 2.0F;
      int var4 = var1.getInvulnerableTicks();
      if (var4 > 0) {
         var3 -= ((float)var4 - var2) / 220.0F * 0.5F;
      }

      GlStateManager.scalef(var3, var3, var3);
   }
}
