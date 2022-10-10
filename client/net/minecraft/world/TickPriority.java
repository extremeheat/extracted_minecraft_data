package net.minecraft.world;

public enum TickPriority {
   EXTREMELY_HIGH(-3),
   VERY_HIGH(-2),
   HIGH(-1),
   NORMAL(0),
   LOW(1),
   VERY_LOW(2),
   EXTREMELY_LOW(3);

   private final int field_205399_h;

   private TickPriority(int var3) {
      this.field_205399_h = var3;
   }

   public static TickPriority func_205397_a(int var0) {
      TickPriority[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         TickPriority var4 = var1[var3];
         if (var4.field_205399_h == var0) {
            return var4;
         }
      }

      if (var0 < EXTREMELY_HIGH.field_205399_h) {
         return EXTREMELY_HIGH;
      } else {
         return EXTREMELY_LOW;
      }
   }

   public int func_205398_a() {
      return this.field_205399_h;
   }
}
