package net.minecraft.world.level.levelgen.structure.placement;

import com.mojang.serialization.Codec;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;

public enum RandomSpreadType implements StringRepresentable {
   LINEAR("linear"),
   TRIANGULAR("triangular");

   public static final Codec<RandomSpreadType> CODEC = StringRepresentable.fromEnum(RandomSpreadType::values);
   private final String id;

   private RandomSpreadType(String var3) {
      this.id = var3;
   }

   @Override
   public String getSerializedName() {
      return this.id;
   }

   public int evaluate(RandomSource var1, int var2) {
      return switch (this) {
         case LINEAR -> var1.nextInt(var2);
         case TRIANGULAR -> (var1.nextInt(var2) + var1.nextInt(var2)) / 2;
      };
   }
}
