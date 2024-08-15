package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;

public abstract class AbstractZombieRenderer<T extends Zombie, S extends ZombieRenderState, M extends ZombieModel<S>> extends HumanoidMobRenderer<T, S, M> {
   private static final ResourceLocation ZOMBIE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/zombie/zombie.png");

   protected AbstractZombieRenderer(EntityRendererProvider.Context var1, M var2, M var3, M var4, M var5, M var6, M var7) {
      super(var1, (M)var2, (M)var3, 0.5F);
      this.addLayer(new HumanoidArmorLayer<>(this, var4, var5, var6, var7, var1.getModelManager()));
   }

   public ResourceLocation getTextureLocation(S var1) {
      return ZOMBIE_LOCATION;
   }

   public void extractRenderState(T var1, S var2, float var3) {
      super.extractRenderState((T)var1, (S)var2, var3);
      var2.isConverting = var1.isUnderWaterConverting();
   }

   protected boolean isShaking(S var1) {
      return super.isShaking((S)var1) || var1.isConverting;
   }
}
