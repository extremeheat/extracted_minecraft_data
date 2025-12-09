package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.state.EquineRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.equine.AbstractHorse;

public abstract class AbstractHorseRenderer<T extends AbstractHorse, S extends EquineRenderState, M extends EntityModel<? super S>> extends AgeableMobRenderer<T, S, M> {
   public AbstractHorseRenderer(EntityRendererProvider.Context var1, M var2, M var3) {
      super(var1, var2, var3, 0.75F);
   }

   public void extractRenderState(T var1, S var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.saddle = var1.getItemBySlot(EquipmentSlot.SADDLE).copy();
      var2.bodyArmorItem = var1.getBodyArmorItem().copy();
      var2.isRidden = var1.isVehicle();
      var2.eatAnimation = var1.getEatAnim(var3);
      var2.standAnimation = var1.getStandAnim(var3);
      var2.feedingAnimation = var1.getMouthAnim(var3);
      var2.animateTail = var1.tailCounter > 0;
   }
}
