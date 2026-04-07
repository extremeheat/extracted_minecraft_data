package net.minecraft.world.level.block.state.properties;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum BedPart implements StringRepresentable {
   HEAD("head"),
   FOOT("foot");

   public static final Codec<BedPart> CODEC = StringRepresentable.<BedPart>fromEnum(BedPart::values);
   private final String name;

   private BedPart(final String name) {
      this.name = name;
   }

   public String toString() {
      return this.name;
   }

   public String getSerializedName() {
      return this.name;
   }

   // $FF: synthetic method
   private static BedPart[] $values() {
      return new BedPart[]{HEAD, FOOT};
   }
}
