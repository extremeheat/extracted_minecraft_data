package net.minecraft.world.ticks;

import com.mojang.serialization.Codec;

public enum TickPriority {
   EXTREMELY_HIGH(-3),
   VERY_HIGH(-2),
   HIGH(-1),
   NORMAL(0),
   LOW(1),
   VERY_LOW(2),
   EXTREMELY_LOW(3);

   public static final Codec<TickPriority> CODEC = Codec.INT.xmap(TickPriority::byValue, TickPriority::getValue);
   private final int value;

   private TickPriority(final int value) {
      this.value = value;
   }

   public static TickPriority byValue(final int value) {
      for(TickPriority priority : values()) {
         if (priority.value == value) {
            return priority;
         }
      }

      if (value < EXTREMELY_HIGH.value) {
         return EXTREMELY_HIGH;
      } else {
         return EXTREMELY_LOW;
      }
   }

   public int getValue() {
      return this.value;
   }

   // $FF: synthetic method
   private static TickPriority[] $values() {
      return new TickPriority[]{EXTREMELY_HIGH, VERY_HIGH, HIGH, NORMAL, LOW, VERY_LOW, EXTREMELY_LOW};
   }
}
