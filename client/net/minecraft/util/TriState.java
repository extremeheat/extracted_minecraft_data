package net.minecraft.util;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.function.Function;

public enum TriState implements StringRepresentable {
   TRUE("true"),
   FALSE("false"),
   DEFAULT("default");

   public static final Codec<TriState> CODEC = Codec.either(Codec.BOOL, StringRepresentable.fromEnum(TriState::values)).xmap((either) -> (TriState)either.map(TriState::from, Function.identity()), (triState) -> {
      Either var10000;
      switch (triState.ordinal()) {
         case 0 -> var10000 = Either.left(true);
         case 1 -> var10000 = Either.left(false);
         case 2 -> var10000 = Either.right(triState);
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   });
   private final String name;

   private TriState(final String name) {
      this.name = name;
   }

   public static TriState from(final boolean value) {
      return value ? TRUE : FALSE;
   }

   public boolean toBoolean(final boolean defaultValue) {
      boolean var10000;
      switch (this.ordinal()) {
         case 0 -> var10000 = true;
         case 1 -> var10000 = false;
         default -> var10000 = defaultValue;
      }

      return var10000;
   }

   public String getSerializedName() {
      return this.name;
   }

   // $FF: synthetic method
   private static TriState[] $values() {
      return new TriState[]{TRUE, FALSE, DEFAULT};
   }
}
