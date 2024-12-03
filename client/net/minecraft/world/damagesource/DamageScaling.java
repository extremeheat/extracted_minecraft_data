package net.minecraft.world.damagesource;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum DamageScaling implements StringRepresentable {
   NEVER("never"),
   WHEN_CAUSED_BY_LIVING_NON_PLAYER("when_caused_by_living_non_player"),
   ALWAYS("always");

   public static final Codec<DamageScaling> CODEC = StringRepresentable.<DamageScaling>fromEnum(DamageScaling::values);
   private final String id;

   private DamageScaling(final String var3) {
      this.id = var3;
   }

   public String getSerializedName() {
      return this.id;
   }

   // $FF: synthetic method
   private static DamageScaling[] $values() {
      return new DamageScaling[]{NEVER, WHEN_CAUSED_BY_LIVING_NON_PLAYER, ALWAYS};
   }
}
