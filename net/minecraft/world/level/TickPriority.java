package net.minecraft.world.level;

public enum TickPriority {
   EXTREMELY_HIGH(-3),
   VERY_HIGH(-2),
   HIGH(-1),
   NORMAL(0),
   LOW(1),
   VERY_LOW(2),
   EXTREMELY_LOW(3);

   private final int value;

   private TickPriority(int var3) {
      this.value = var3;
   }

   public static TickPriority byValue(int var0) {
      TickPriority[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         TickPriority var4 = var1[var3];
         if (var4.value == var0) {
            return var4;
         }
      }

      if (var0 < EXTREMELY_HIGH.value) {
         return EXTREMELY_HIGH;
      } else {
         return EXTREMELY_LOW;
      }
   }

   public int getValue() {
      return this.value;
   }
}
