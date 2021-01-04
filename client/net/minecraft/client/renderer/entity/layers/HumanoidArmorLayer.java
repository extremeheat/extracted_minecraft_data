package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

public class HumanoidArmorLayer<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends AbstractArmorLayer<T, M, A> {
   public HumanoidArmorLayer(RenderLayerParent<T, M> var1, A var2, A var3) {
      super(var1, var2, var3);
   }

   protected void setPartVisibility(A var1, EquipmentSlot var2) {
      this.hideAllArmor(var1);
      switch(var2) {
      case HEAD:
         var1.head.visible = true;
         var1.hat.visible = true;
         break;
      case CHEST:
         var1.body.visible = true;
         var1.rightArm.visible = true;
         var1.leftArm.visible = true;
         break;
      case LEGS:
         var1.body.visible = true;
         var1.rightLeg.visible = true;
         var1.leftLeg.visible = true;
         break;
      case FEET:
         var1.rightLeg.visible = true;
         var1.leftLeg.visible = true;
      }

   }

   protected void hideAllArmor(A var1) {
      var1.setAllVisible(false);
   }
}
