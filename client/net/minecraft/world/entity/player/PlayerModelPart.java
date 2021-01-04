package net.minecraft.world.entity.player;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public enum PlayerModelPart {
   CAPE(0, "cape"),
   JACKET(1, "jacket"),
   LEFT_SLEEVE(2, "left_sleeve"),
   RIGHT_SLEEVE(3, "right_sleeve"),
   LEFT_PANTS_LEG(4, "left_pants_leg"),
   RIGHT_PANTS_LEG(5, "right_pants_leg"),
   HAT(6, "hat");

   private final int bit;
   private final int mask;
   private final String id;
   private final Component name;

   private PlayerModelPart(int var3, String var4) {
      this.bit = var3;
      this.mask = 1 << var3;
      this.id = var4;
      this.name = new TranslatableComponent("options.modelPart." + var4, new Object[0]);
   }

   public int getMask() {
      return this.mask;
   }

   public String getId() {
      return this.id;
   }

   public Component getName() {
      return this.name;
   }
}
