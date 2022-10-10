package net.minecraft.advancements;

import net.minecraft.util.text.TextFormatting;

public enum FrameType {
   TASK("task", 0, TextFormatting.GREEN),
   CHALLENGE("challenge", 26, TextFormatting.DARK_PURPLE),
   GOAL("goal", 52, TextFormatting.GREEN);

   private final String field_192313_d;
   private final int field_192314_e;
   private final TextFormatting field_193230_f;

   private FrameType(String var3, int var4, TextFormatting var5) {
      this.field_192313_d = var3;
      this.field_192314_e = var4;
      this.field_193230_f = var5;
   }

   public String func_192307_a() {
      return this.field_192313_d;
   }

   public int func_192309_b() {
      return this.field_192314_e;
   }

   public static FrameType func_192308_a(String var0) {
      FrameType[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         FrameType var4 = var1[var3];
         if (var4.field_192313_d.equals(var0)) {
            return var4;
         }
      }

      throw new IllegalArgumentException("Unknown frame type '" + var0 + "'");
   }

   public TextFormatting func_193229_c() {
      return this.field_193230_f;
   }
}
