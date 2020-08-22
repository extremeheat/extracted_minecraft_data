package net.minecraft.world.item;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.material.MaterialColor;

public enum DyeColor implements StringRepresentable {
   WHITE(0, "white", 16383998, MaterialColor.SNOW, 15790320, 16777215),
   ORANGE(1, "orange", 16351261, MaterialColor.COLOR_ORANGE, 15435844, 16738335),
   MAGENTA(2, "magenta", 13061821, MaterialColor.COLOR_MAGENTA, 12801229, 16711935),
   LIGHT_BLUE(3, "light_blue", 3847130, MaterialColor.COLOR_LIGHT_BLUE, 6719955, 10141901),
   YELLOW(4, "yellow", 16701501, MaterialColor.COLOR_YELLOW, 14602026, 16776960),
   LIME(5, "lime", 8439583, MaterialColor.COLOR_LIGHT_GREEN, 4312372, 12582656),
   PINK(6, "pink", 15961002, MaterialColor.COLOR_PINK, 14188952, 16738740),
   GRAY(7, "gray", 4673362, MaterialColor.COLOR_GRAY, 4408131, 8421504),
   LIGHT_GRAY(8, "light_gray", 10329495, MaterialColor.COLOR_LIGHT_GRAY, 11250603, 13882323),
   CYAN(9, "cyan", 1481884, MaterialColor.COLOR_CYAN, 2651799, 65535),
   PURPLE(10, "purple", 8991416, MaterialColor.COLOR_PURPLE, 8073150, 10494192),
   BLUE(11, "blue", 3949738, MaterialColor.COLOR_BLUE, 2437522, 255),
   BROWN(12, "brown", 8606770, MaterialColor.COLOR_BROWN, 5320730, 9127187),
   GREEN(13, "green", 6192150, MaterialColor.COLOR_GREEN, 3887386, 65280),
   RED(14, "red", 11546150, MaterialColor.COLOR_RED, 11743532, 16711680),
   BLACK(15, "black", 1908001, MaterialColor.COLOR_BLACK, 1973019, 0);

   private static final DyeColor[] BY_ID = (DyeColor[])Arrays.stream(values()).sorted(Comparator.comparingInt(DyeColor::getId)).toArray((var0) -> {
      return new DyeColor[var0];
   });
   private static final Int2ObjectOpenHashMap BY_FIREWORK_COLOR = new Int2ObjectOpenHashMap((Map)Arrays.stream(values()).collect(Collectors.toMap((var0) -> {
      return var0.fireworkColor;
   }, (var0) -> {
      return var0;
   })));
   private final int id;
   private final String name;
   private final MaterialColor color;
   private final int textureDiffuseColor;
   private final int textureDiffuseColorBGR;
   private final float[] textureDiffuseColors;
   private final int fireworkColor;
   private final int textColor;

   private DyeColor(int var3, String var4, int var5, MaterialColor var6, int var7, int var8) {
      this.id = var3;
      this.name = var4;
      this.textureDiffuseColor = var5;
      this.color = var6;
      this.textColor = var8;
      int var9 = (var5 & 16711680) >> 16;
      int var10 = (var5 & '\uff00') >> 8;
      int var11 = (var5 & 255) >> 0;
      this.textureDiffuseColorBGR = var11 << 16 | var10 << 8 | var9 << 0;
      this.textureDiffuseColors = new float[]{(float)var9 / 255.0F, (float)var10 / 255.0F, (float)var11 / 255.0F};
      this.fireworkColor = var7;
   }

   public int getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public float[] getTextureDiffuseColors() {
      return this.textureDiffuseColors;
   }

   public MaterialColor getMaterialColor() {
      return this.color;
   }

   public int getFireworkColor() {
      return this.fireworkColor;
   }

   public int getTextColor() {
      return this.textColor;
   }

   public static DyeColor byId(int var0) {
      if (var0 < 0 || var0 >= BY_ID.length) {
         var0 = 0;
      }

      return BY_ID[var0];
   }

   public static DyeColor byName(String var0, DyeColor var1) {
      DyeColor[] var2 = values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         DyeColor var5 = var2[var4];
         if (var5.name.equals(var0)) {
            return var5;
         }
      }

      return var1;
   }

   @Nullable
   public static DyeColor byFireworkColor(int var0) {
      return (DyeColor)BY_FIREWORK_COLOR.get(var0);
   }

   public String toString() {
      return this.name;
   }

   public String getSerializedName() {
      return this.name;
   }
}
