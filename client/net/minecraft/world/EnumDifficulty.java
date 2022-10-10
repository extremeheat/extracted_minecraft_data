package net.minecraft.world;

import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public enum EnumDifficulty {
   PEACEFUL(0, "peaceful"),
   EASY(1, "easy"),
   NORMAL(2, "normal"),
   HARD(3, "hard");

   private static final EnumDifficulty[] field_151530_e = (EnumDifficulty[])Arrays.stream(values()).sorted(Comparator.comparingInt(EnumDifficulty::func_151525_a)).toArray((var0) -> {
      return new EnumDifficulty[var0];
   });
   private final int field_151527_f;
   private final String field_151528_g;

   private EnumDifficulty(int var3, String var4) {
      this.field_151527_f = var3;
      this.field_151528_g = var4;
   }

   public int func_151525_a() {
      return this.field_151527_f;
   }

   public ITextComponent func_199285_b() {
      return new TextComponentTranslation("options.difficulty." + this.field_151528_g, new Object[0]);
   }

   public static EnumDifficulty func_151523_a(int var0) {
      return field_151530_e[var0 % field_151530_e.length];
   }

   public String func_151526_b() {
      return this.field_151528_g;
   }
}
