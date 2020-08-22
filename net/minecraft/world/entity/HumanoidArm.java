package net.minecraft.world.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public enum HumanoidArm {
   LEFT(new TranslatableComponent("options.mainHand.left", new Object[0])),
   RIGHT(new TranslatableComponent("options.mainHand.right", new Object[0]));

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
}
