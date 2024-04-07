package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;

public abstract class AbstractZombieRenderer<T extends Zombie, M extends ZombieModel<T>> extends HumanoidMobRenderer<T, M> {
   private static final ResourceLocation ZOMBIE_LOCATION = new ResourceLocation("textures/entity/zombie/zombie.png");

   protected AbstractZombieRenderer(EntityRendererProvider.Context var1, M var2, M var3, M var4) {
      super(var1, (M)var2, 0.5F);
      this.addLayer(new HumanoidArmorLayer<>(this, var3, var4, var1.getModelManager()));
   }

   public ResourceLocation getTextureLocation(Zombie var1) {
      return ZOMBIE_LOCATION;
   }

   protected boolean isShaking(T var1) {
      return super.isShaking((T)var1) || var1.isUnderWaterConverting();
   }
}
