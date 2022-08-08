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

   public String getSerializedName() {
      return this.id;
   }

   public int evaluate(RandomSource var1, int var2) {
      int var10000;
      switch (this) {
         case LINEAR:
            var10000 = var1.nextInt(var2);
            break;
         case TRIANGULAR:
            var10000 = (var1.nextInt(var2) + var1.nextInt(var2)) / 2;
            break;
         default:
            throw new IncompatibleClassChangeError();
      }

      return var10000;
   }

   // $FF: synthetic method
   private static RandomSpreadType[] $values() {
      return new RandomSpreadType[]{LINEAR, TRIANGULAR};
   }
}
