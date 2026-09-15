package net.minecraft.core.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import org.jspecify.annotations.Nullable;

final class Removed {
   public static final Removed INSTANCE = new Removed();
   public static final Codec<Removed> CODEC;

   private Removed() {
      super();
   }

   public String toString() {
      return "<removed>";
   }

   public static boolean isNotRemoved(final Object value) {
      return value != INSTANCE;
   }

   public static boolean isRemoved(final Object value) {
      return value == INSTANCE;
   }

   public static Object nullToRemoved(final @Nullable Object value) {
      return value == null ? INSTANCE : value;
   }

   public static @Nullable Object removedToNull(final Object value) {
      return value == INSTANCE ? null : value;
   }

   static {
      CODEC = MapCodec.unitCodec(INSTANCE);
   }
}
