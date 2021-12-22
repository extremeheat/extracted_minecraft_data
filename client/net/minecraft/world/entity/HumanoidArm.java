package net.minecraft.world.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public enum HumanoidArm {
   LEFT(new TranslatableComponent("options.mainHand.left")),
   RIGHT(new TranslatableComponent("options.mainHand.right"));

   private final Component name;

   private HumanoidArm(Component var3) {
      this.name = var3;
   }

   public HumanoidArm getOpposite() {
      return this == LEFT ? RIGHT : LEFT;
   }

   public String toString() {
      return this.name.getString();
   }

   public Component getName() {
      return this.name;
   }

   // $FF: synthetic method
   private static HumanoidArm[] $values() {
      return new HumanoidArm[]{LEFT, RIGHT};
   }
}
