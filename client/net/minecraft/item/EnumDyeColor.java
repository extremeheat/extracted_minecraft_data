package net.minecraft.item;

import net.minecraft.block.material.MapColor;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IStringSerializable;

public enum EnumDyeColor implements IStringSerializable {
   WHITE(0, 15, "white", "white", MapColor.field_151666_j, EnumChatFormatting.WHITE),
   ORANGE(1, 14, "orange", "orange", MapColor.field_151676_q, EnumChatFormatting.GOLD),
   MAGENTA(2, 13, "magenta", "magenta", MapColor.field_151675_r, EnumChatFormatting.AQUA),
   LIGHT_BLUE(3, 12, "light_blue", "lightBlue", MapColor.field_151674_s, EnumChatFormatting.BLUE),
   YELLOW(4, 11, "yellow", "yellow", MapColor.field_151673_t, EnumChatFormatting.YELLOW),
   LIME(5, 10, "lime", "lime", MapColor.field_151672_u, EnumChatFormatting.GREEN),
   PINK(6, 9, "pink", "pink", MapColor.field_151671_v, EnumChatFormatting.LIGHT_PURPLE),
   GRAY(7, 8, "gray", "gray", MapColor.field_151670_w, EnumChatFormatting.DARK_GRAY),
   SILVER(8, 7, "silver", "silver", MapColor.field_151680_x, EnumChatFormatting.GRAY),
   CYAN(9, 6, "cyan", "cyan", MapColor.field_151679_y, EnumChatFormatting.DARK_AQUA),
   PURPLE(10, 5, "purple", "purple", MapColor.field_151678_z, EnumChatFormatting.DARK_PURPLE),
   BLUE(11, 4, "blue", "blue", MapColor.field_151649_A, EnumChatFormatting.DARK_BLUE),
   BROWN(12, 3, "brown", "brown", MapColor.field_151650_B, EnumChatFormatting.GOLD),
   GREEN(13, 2, "green", "green", MapColor.field_151651_C, EnumChatFormatting.DARK_GREEN),
   RED(14, 1, "red", "red", MapColor.field_151645_D, EnumChatFormatting.DARK_RED),
   BLACK(15, 0, "black", "black", MapColor.field_151646_E, EnumChatFormatting.BLACK);

   private static final EnumDyeColor[] field_176790_q = new EnumDyeColor[values().length];
   private static final EnumDyeColor[] field_176789_r = new EnumDyeColor[values().length];
   private final int field_176788_s;
   private final int field_176787_t;
   private final String field_176786_u;
   private final String field_176785_v;
   private final MapColor field_176784_w;
   private final EnumChatFormatting field_176793_x;

   private EnumDyeColor(int var3, int var4, String var5, String var6, MapColor var7, EnumChatFormatting var8) {
      this.field_176788_s = var3;
      this.field_176787_t = var4;
      this.field_176786_u = var5;
      this.field_176785_v = var6;
      this.field_176784_w = var7;
      this.field_176793_x = var8;
   }

   public int func_176765_a() {
      return this.field_176788_s;
   }

   public int func_176767_b() {
      return this.field_176787_t;
   }

   public String func_176762_d() {
      return this.field_176785_v;
   }

   public MapColor func_176768_e() {
      return this.field_176784_w;
   }

   public static EnumDyeColor func_176766_a(int var0) {
      if (var0 < 0 || var0 >= field_176789_r.length) {
         var0 = 0;
      }

      return field_176789_r[var0];
   }

   public static EnumDyeColor func_176764_b(int var0) {
      if (var0 < 0 || var0 >= field_176790_q.length) {
         var0 = 0;
      }

      return field_176790_q[var0];
   }

   public String toString() {
      return this.field_176785_v;
   }

   public String func_176610_l() {
      return this.field_176786_u;
   }

   static {
      EnumDyeColor[] var0 = values();
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         EnumDyeColor var3 = var0[var2];
         field_176790_q[var3.func_176765_a()] = var3;
         field_176789_r[var3.func_176767_b()] = var3;
      }

   }
}
