package net.minecraft.world.attribute.modifier;

import com.mojang.serialization.Codec;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.LerpFunction;

public enum BooleanModifier implements AttributeModifier<Boolean, Boolean> {
   AND,
   NAND,
   OR,
   NOR,
   XOR,
   XNOR;

   private BooleanModifier() {
   }

   public Boolean apply(final Boolean subject, final Boolean argument) {
      Boolean var10000;
      switch (this.ordinal()) {
         case 0 -> var10000 = argument && subject;
         case 1 -> var10000 = !argument || !subject;
         case 2 -> var10000 = argument || subject;
         case 3 -> var10000 = !argument && !subject;
         case 4 -> var10000 = argument ^ subject;
         case 5 -> var10000 = argument == subject;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public Codec<Boolean> argumentCodec(final EnvironmentAttribute<Boolean> type) {
      return Codec.BOOL;
   }

   public LerpFunction<Boolean> argumentKeyframeLerp(final EnvironmentAttribute<Boolean> type) {
      return LerpFunction.<Boolean>ofConstant();
   }

   // $FF: synthetic method
   private static BooleanModifier[] $values() {
      return new BooleanModifier[]{AND, NAND, OR, NOR, XOR, XNOR};
   }
}
