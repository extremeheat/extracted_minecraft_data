package net.minecraft.item;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.IStringSerializable;

public enum EnumDyeColor implements IStringSerializable {
   WHITE(0, "white", 16383998, MaterialColor.field_151666_j, 15790320),
   ORANGE(1, "orange", 16351261, MaterialColor.field_151676_q, 15435844),
   MAGENTA(2, "magenta", 13061821, MaterialColor.field_151675_r, 12801229),
   LIGHT_BLUE(3, "light_blue", 3847130, MaterialColor.field_151674_s, 6719955),
   YELLOW(4, "yellow", 16701501, MaterialColor.field_151673_t, 14602026),
   LIME(5, "lime", 8439583, MaterialColor.field_151672_u, 4312372),
   PINK(6, "pink", 15961002, MaterialColor.field_151671_v, 14188952),
   GRAY(7, "gray", 4673362, MaterialColor.field_151670_w, 4408131),
   LIGHT_GRAY(8, "light_gray", 10329495, MaterialColor.field_197656_x, 11250603),
   CYAN(9, "cyan", 1481884, MaterialColor.field_151679_y, 2651799),
   PURPLE(10, "purple", 8991416, MaterialColor.field_151678_z, 8073150),
   BLUE(11, "blue", 3949738, MaterialColor.field_151649_A, 2437522),
   BROWN(12, "brown", 8606770, MaterialColor.field_151650_B, 5320730),
   GREEN(13, "green", 6192150, MaterialColor.field_151651_C, 3887386),
   RED(14, "red", 11546150, MaterialColor.field_151645_D, 11743532),
   BLACK(15, "black", 1908001, MaterialColor.field_151646_E, 1973019);

   private static final EnumDyeColor[] field_196062_q = (EnumDyeColor[])Arrays.stream(values()).sorted(Comparator.comparingInt(EnumDyeColor::func_196059_a)).toArray((var0) -> {
      return new EnumDyeColor[var0];
   });
   private static final Int2ObjectOpenHashMap<EnumDyeColor> field_196063_r = new Int2ObjectOpenHashMap((Map)Arrays.stream(values()).collect(Collectors.toMap((var0) -> {
      return var0.field_196067_y;
   }, (var0) -> {
      return var0;
   })));
   private final int field_196064_s;
   private final String field_176785_v;
   private final MaterialColor field_196065_u;
   private final int field_193351_w;
   private final int field_196066_w;
   private final float[] field_193352_x;
   private final int field_196067_y;

   private EnumDyeColor(int var3, String var4, int var5, MaterialColor var6, int var7) {
      this.field_196064_s = var3;
      this.field_176785_v = var4;
      this.field_193351_w = var5;
      this.field_196065_u = var6;
      int var8 = (var5 & 16711680) >> 16;
      int var9 = (var5 & '\uff00') >> 8;
      int var10 = (var5 & 255) >> 0;
      this.field_196066_w = var10 << 16 | var9 << 8 | var8 << 0;
      this.field_193352_x = new float[]{(float)var8 / 255.0F, (float)var9 / 255.0F, (float)var10 / 255.0F};
      this.field_196067_y = var7;
   }

   public int func_196059_a() {
      return this.field_196064_s;
   }

   public String func_176762_d() {
      return this.field_176785_v;
   }

   public int func_196057_c() {
      return this.field_196066_w;
   }

   public float[] func_193349_f() {
      return this.field_193352_x;
   }

   public MaterialColor func_196055_e() {
      return this.field_196065_u;
   }

   public int func_196060_f() {
      return this.field_196067_y;
   }

   public static EnumDyeColor func_196056_a(int var0) {
      if (var0 < 0 || var0 >= field_196062_q.length) {
         var0 = 0;
      }

      return field_196062_q[var0];
   }

   public static EnumDyeColor func_204271_a(String var0) {
      EnumDyeColor[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         EnumDyeColor var4 = var1[var3];
         if (var4.field_176785_v.equals(var0)) {
            return var4;
         }
      }

      return WHITE;
   }

   @Nullable
   public static EnumDyeColor func_196058_b(int var0) {
      return (EnumDyeColor)field_196063_r.get(var0);
   }

   public String toString() {
      return this.field_176785_v;
   }

   public String func_176610_l() {
      return this.field_176785_v;
   }
}
