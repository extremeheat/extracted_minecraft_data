package net.minecraft.world;

public enum EnumDifficulty {
   PEACEFUL(0, "options.difficulty.peaceful"),
   EASY(1, "options.difficulty.easy"),
   NORMAL(2, "options.difficulty.normal"),
   HARD(3, "options.difficulty.hard");

   private static final EnumDifficulty[] field_151530_e = new EnumDifficulty[values().length];
   private final int field_151527_f;
   private final String field_151528_g;

   private EnumDifficulty(int var3, String var4) {
      this.field_151527_f = var3;
      this.field_151528_g = var4;
   }

   public int func_151525_a() {
      return this.field_151527_f;
   }

   public static EnumDifficulty func_151523_a(int var0) {
      return field_151530_e[var0 % field_151530_e.length];
   }

   public String func_151526_b() {
      return this.field_151528_g;
   }

   static {
      EnumDifficulty[] var0 = values();
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         EnumDifficulty var3 = var0[var2];
         field_151530_e[var3.field_151527_f] = var3;
      }

   }
}
