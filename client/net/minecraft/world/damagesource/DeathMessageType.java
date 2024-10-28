package net.minecraft.world.damagesource;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum DeathMessageType implements StringRepresentable {
   DEFAULT("default"),
   FALL_VARIANTS("fall_variants"),
   INTENTIONAL_GAME_DESIGN("intentional_game_design");

   public static final Codec<DeathMessageType> CODEC = StringRepresentable.fromEnum(DeathMessageType::values);
   private final String id;

   private DeathMessageType(final String var3) {
      this.id = var3;
   }

   public String getSerializedName() {
      return this.id;
   }

   // $FF: synthetic method
   private static DeathMessageType[] $values() {
      return new DeathMessageType[]{DEFAULT, FALL_VARIANTS, INTENTIONAL_GAME_DESIGN};
   }
}
