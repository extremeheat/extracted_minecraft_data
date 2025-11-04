package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.SwingAnimationType;
import net.minecraft.world.item.component.SwingAnimation;

public abstract class AbstractZombieRenderer<T extends Zombie, S extends ZombieRenderState, M extends ZombieModel<S>> extends HumanoidMobRenderer<T, S, M> {
   private static final Identifier ZOMBIE_LOCATION = Identifier.withDefaultNamespace("textures/entity/zombie/zombie.png");

   protected AbstractZombieRenderer(EntityRendererProvider.Context var1, M var2, M var3, ArmorModelSet<M> var4, ArmorModelSet<M> var5) {
      super(var1, var2, var3, 0.5F);
      this.addLayer(new HumanoidArmorLayer(this, var4, var5, var1.getEquipmentRenderer()));
   }

   public Identifier getTextureLocation(S var1) {
      return ZOMBIE_LOCATION;
   }

   public void extractRenderState(T var1, S var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.isAggressive = var1.isAggressive();
      var2.isConverting = var1.isUnderWaterConverting();
   }

   protected boolean isShaking(S var1) {
      return super.isShaking(var1) || var1.isConverting;
   }

   protected HumanoidModel.ArmPose getArmPose(T var1, HumanoidArm var2) {
      SwingAnimation var3 = (SwingAnimation)var1.getItemHeldByArm(var2.getOpposite()).get(DataComponents.SWING_ANIMATION);
      return var3 != null && var3.type() == SwingAnimationType.STAB ? HumanoidModel.ArmPose.SPEAR : super.getArmPose(var1, var2);
   }

   // $FF: synthetic method
   protected boolean isShaking(final LivingEntityRenderState var1) {
      return this.isShaking((ZombieRenderState)var1);
   }

   // $FF: synthetic method
   public Identifier getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((ZombieRenderState)var1);
   }
}
