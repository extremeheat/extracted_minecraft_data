package net.minecraft.resources;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public enum PackCompatibility {
   TOO_OLD("old"),
   TOO_NEW("new"),
   COMPATIBLE("compatible");

   private final ITextComponent field_198975_d;
   private final ITextComponent field_198976_e;

   private PackCompatibility(String var3) {
      this.field_198975_d = new TextComponentTranslation("resourcePack.incompatible." + var3, new Object[0]);
      this.field_198976_e = new TextComponentTranslation("resourcePack.incompatible.confirm." + var3, new Object[0]);
   }

   public boolean func_198968_a() {
      return this == COMPATIBLE;
   }

   public static PackCompatibility func_198969_a(int var0) {
      if (var0 < 4) {
         return TOO_OLD;
      } else {
         return var0 > 4 ? TOO_NEW : COMPATIBLE;
      }
   }

   public ITextComponent func_198967_b() {
      return this.field_198975_d;
   }

   public ITextComponent func_198971_c() {
      return this.field_198976_e;
   }
}
