package net.minecraft.world.entity.player;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum PlayerModelPart implements StringRepresentable {
   CAPE(0, "cape"),
   JACKET(1, "jacket"),
   LEFT_SLEEVE(2, "left_sleeve"),
   RIGHT_SLEEVE(3, "right_sleeve"),
   LEFT_PANTS_LEG(4, "left_pants_leg"),
   RIGHT_PANTS_LEG(5, "right_pants_leg"),
   HAT(6, "hat");

   public static final Codec<PlayerModelPart> CODEC = StringRepresentable.<PlayerModelPart>fromEnum(PlayerModelPart::values);
   private final int bit;
   private final int mask;
   private final String id;
   private final Component name;

   private PlayerModelPart(final int bit, final String name) {
      this.bit = bit;
      this.mask = 1 << bit;
      this.id = name;
      this.name = Component.translatable("options.modelPart." + name);
   }

   public int getMask() {
      return this.mask;
   }

   public int getBit() {
      return this.bit;
   }

   public String getId() {
      return this.id;
   }

   public Component getName() {
      return this.name;
   }

   public String getSerializedName() {
      return this.id;
   }

   // $FF: synthetic method
   private static PlayerModelPart[] $values() {
      return new PlayerModelPart[]{CAPE, JACKET, LEFT_SLEEVE, RIGHT_SLEEVE, LEFT_PANTS_LEG, RIGHT_PANTS_LEG, HAT};
   }
}
