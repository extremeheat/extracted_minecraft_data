package net.minecraft.world;

import net.minecraft.world.entity.EquipmentSlot;

public enum InteractionHand {
   MAIN_HAND,
   OFF_HAND;

   private InteractionHand() {
   }

   public EquipmentSlot asEquipmentSlot() {
      return this == MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
   }

   // $FF: synthetic method
   private static InteractionHand[] $values() {
      return new InteractionHand[]{MAIN_HAND, OFF_HAND};
   }
}
