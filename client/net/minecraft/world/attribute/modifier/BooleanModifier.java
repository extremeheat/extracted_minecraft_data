package net.minecraft.world.attribute.modifier;

import com.mojang.serialization.Codec;
import net.minecraft.world.attribute.EnvironmentAttribute;

public enum BooleanModifier implements AttributeModifier<Boolean, Boolean> {
   AND,
   NAND,
   OR,
   NOR,
   XOR,
   XNOR;

   private BooleanModifier() {
   }

   public Boolean apply(Boolean var1, Boolean var2) {
      Boolean var10000;
      switch (this.ordinal()) {
         case 0 -> var10000 = var2 && var1;
         case 1 -> var10000 = !var2 || !var1;
         case 2 -> var10000 = var2 || var1;
         case 3 -> var10000 = !var2 && !var1;
         case 4 -> var10000 = var2 ^ var1;
         case 5 -> var10000 = var2 == var1;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public Codec<Boolean> argumentCodec(EnvironmentAttribute<Boolean> var1) {
      return Codec.BOOL;
   }

   // $FF: synthetic method
   public Object apply(final Object var1, final Object var2) {
      return this.apply((Boolean)var1, (Boolean)var2);
   }

   // $FF: synthetic method
   private static BooleanModifier[] $values() {
      return new BooleanModifier[]{AND, NAND, OR, NOR, XOR, XNOR};
   }
}
