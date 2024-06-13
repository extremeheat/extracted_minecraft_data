package net.minecraft.world.ticks;

public enum TickPriority {
   EXTREMELY_HIGH(-3),
   VERY_HIGH(-2),
   HIGH(-1),
   NORMAL(0),
   LOW(1),
   VERY_LOW(2),
   EXTREMELY_LOW(3);

   private final int value;

   private TickPriority(final int nullxx) {
      this.value = nullxx;
   }

   public static TickPriority byValue(int var0) {
      for (TickPriority var4 : values()) {
         if (var4.value == var0) {
            return var4;
         }
      }

      return var0 < EXTREMELY_HIGH.value ? EXTREMELY_HIGH : EXTREMELY_LOW;
   }

   public int getValue() {
      return this.value;
   }
}
