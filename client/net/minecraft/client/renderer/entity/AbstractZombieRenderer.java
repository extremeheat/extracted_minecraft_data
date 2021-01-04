package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;

public abstract class AbstractZombieRenderer<T extends Zombie, M extends ZombieModel<T>> extends HumanoidMobRenderer<T, M> {
   private static final ResourceLocation ZOMBIE_LOCATION = new ResourceLocation("textures/entity/zombie/zombie.png");

   protected AbstractZombieRenderer(EntityRenderDispatcher var1, M var2, M var3, M var4) {
      super(var1, var2, 0.5F);
      this.addLayer(new HumanoidArmorLayer(this, var3, var4));
   }

   protected ResourceLocation getTextureLocation(Zombie var1) {
      return ZOMBIE_LOCATION;
   }

   protected void setupRotations(T var1, float var2, float var3, float var4) {
      if (var1.isUnderWaterConverting()) {
         var3 += (float)(Math.cos((double)var1.tickCount * 3.25D) * 3.141592653589793D * 0.25D);
      }

      super.setupRotations(var1, var2, var3, var4);
   }
}
