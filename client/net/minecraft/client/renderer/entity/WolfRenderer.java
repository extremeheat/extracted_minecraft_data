package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.renderer.entity.layers.WolfCollarLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Wolf;

public class WolfRenderer extends MobRenderer<Wolf, WolfModel<Wolf>> {
   private static final ResourceLocation WOLF_LOCATION = new ResourceLocation("textures/entity/wolf/wolf.png");
   private static final ResourceLocation WOLF_TAME_LOCATION = new ResourceLocation("textures/entity/wolf/wolf_tame.png");
   private static final ResourceLocation WOLF_ANGRY_LOCATION = new ResourceLocation("textures/entity/wolf/wolf_angry.png");

   public WolfRenderer(EntityRenderDispatcher var1) {
      super(var1, new WolfModel(), 0.5F);
      this.addLayer(new WolfCollarLayer(this));
   }

   protected float getBob(Wolf var1, float var2) {
      return var1.getTailAngle();
   }

   public void render(Wolf var1, double var2, double var4, double var6, float var8, float var9) {
      if (var1.isWet()) {
         float var10 = var1.getBrightness() * var1.getWetShade(var9);
         GlStateManager.color3f(var10, var10, var10);
      }

      super.render((Mob)var1, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation getTextureLocation(Wolf var1) {
      if (var1.isTame()) {
         return WOLF_TAME_LOCATION;
      } else {
         return var1.isAngry() ? WOLF_ANGRY_LOCATION : WOLF_LOCATION;
      }
   }

   // $FF: synthetic method
   protected float getBob(LivingEntity var1, float var2) {
      return this.getBob((Wolf)var1, var2);
   }
}
