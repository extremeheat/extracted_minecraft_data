package net.minecraft.util;

import org.jspecify.annotations.Nullable;

public class TriBool {
   public TriBool() {
      super();
   }

   public static @Nullable Boolean not(final @Nullable Boolean v) {
      return v != null ? !v : null;
   }

   public static @Nullable Boolean and(final @Nullable Boolean a, final @Nullable Boolean b) {
      if (a != null && b != null) {
         return a && b;
      } else {
         return a != Boolean.FALSE && b != Boolean.FALSE ? null : Boolean.FALSE;
      }
   }

   public static @Nullable Boolean or(final @Nullable Boolean a, final @Nullable Boolean b) {
      if (a != null && b != null) {
         return a || b;
      } else {
         return a != Boolean.TRUE && b != Boolean.TRUE ? null : Boolean.TRUE;
      }
   }
}
