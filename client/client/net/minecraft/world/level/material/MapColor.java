package net.minecraft.world.level.material;

import com.google.common.base.Preconditions;

public class MapColor {
   private static final MapColor[] MATERIAL_COLORS = new MapColor[64];
   public static final MapColor NONE = new MapColor(0, 0);
   public static final MapColor GRASS = new MapColor(1, 8368696);
   public static final MapColor SAND = new MapColor(2, 16247203);
   public static final MapColor WOOL = new MapColor(3, 13092807);
   public static final MapColor FIRE = new MapColor(4, 16711680);
   public static final MapColor ICE = new MapColor(5, 10526975);
   public static final MapColor METAL = new MapColor(6, 10987431);
   public static final MapColor PLANT = new MapColor(7, 31744);
   public static final MapColor SNOW = new MapColor(8, 16777215);
   public static final MapColor CLAY = new MapColor(9, 10791096);
   public static final MapColor DIRT = new MapColor(10, 9923917);
   public static final MapColor STONE = new MapColor(11, 7368816);
   public static final MapColor WATER = new MapColor(12, 4210943);
   public static final MapColor WOOD = new MapColor(13, 9402184);
   public static final MapColor QUARTZ = new MapColor(14, 16776437);
   public static final MapColor COLOR_ORANGE = new MapColor(15, 14188339);
   public static final MapColor COLOR_MAGENTA = new MapColor(16, 11685080);
   public static final MapColor COLOR_LIGHT_BLUE = new MapColor(17, 6724056);
   public static final MapColor COLOR_YELLOW = new MapColor(18, 15066419);
   public static final MapColor COLOR_LIGHT_GREEN = new MapColor(19, 8375321);
   public static final MapColor COLOR_PINK = new MapColor(20, 15892389);
   public static final MapColor COLOR_GRAY = new MapColor(21, 5000268);
   public static final MapColor COLOR_LIGHT_GRAY = new MapColor(22, 10066329);
   public static final MapColor COLOR_CYAN = new MapColor(23, 5013401);
   public static final MapColor COLOR_PURPLE = new MapColor(24, 8339378);
   public static final MapColor COLOR_BLUE = new MapColor(25, 3361970);
   public static final MapColor COLOR_BROWN = new MapColor(26, 6704179);
   public static final MapColor COLOR_GREEN = new MapColor(27, 6717235);
   public static final MapColor COLOR_RED = new MapColor(28, 10040115);
   public static final MapColor COLOR_BLACK = new MapColor(29, 1644825);
   public static final MapColor GOLD = new MapColor(30, 16445005);
   public static final MapColor DIAMOND = new MapColor(31, 6085589);
   public static final MapColor LAPIS = new MapColor(32, 4882687);
   public static final MapColor EMERALD = new MapColor(33, 55610);
   public static final MapColor PODZOL = new MapColor(34, 8476209);
   public static final MapColor NETHER = new MapColor(35, 7340544);
   public static final MapColor TERRACOTTA_WHITE = new MapColor(36, 13742497);
   public static final MapColor TERRACOTTA_ORANGE = new MapColor(37, 10441252);
   public static final MapColor TERRACOTTA_MAGENTA = new MapColor(38, 9787244);
   public static final MapColor TERRACOTTA_LIGHT_BLUE = new MapColor(39, 7367818);
   public static final MapColor TERRACOTTA_YELLOW = new MapColor(40, 12223780);
   public static final MapColor TERRACOTTA_LIGHT_GREEN = new MapColor(41, 6780213);
   public static final MapColor TERRACOTTA_PINK = new MapColor(42, 10505550);
   public static final MapColor TERRACOTTA_GRAY = new MapColor(43, 3746083);
   public static final MapColor TERRACOTTA_LIGHT_GRAY = new MapColor(44, 8874850);
   public static final MapColor TERRACOTTA_CYAN = new MapColor(45, 5725276);
   public static final MapColor TERRACOTTA_PURPLE = new MapColor(46, 8014168);
   public static final MapColor TERRACOTTA_BLUE = new MapColor(47, 4996700);
   public static final MapColor TERRACOTTA_BROWN = new MapColor(48, 4993571);
   public static final MapColor TERRACOTTA_GREEN = new MapColor(49, 5001770);
   public static final MapColor TERRACOTTA_RED = new MapColor(50, 9321518);
   public static final MapColor TERRACOTTA_BLACK = new MapColor(51, 2430480);
   public static final MapColor CRIMSON_NYLIUM = new MapColor(52, 12398641);
   public static final MapColor CRIMSON_STEM = new MapColor(53, 9715553);
   public static final MapColor CRIMSON_HYPHAE = new MapColor(54, 6035741);
   public static final MapColor WARPED_NYLIUM = new MapColor(55, 1474182);
   public static final MapColor WARPED_STEM = new MapColor(56, 3837580);
   public static final MapColor WARPED_HYPHAE = new MapColor(57, 5647422);
   public static final MapColor WARPED_WART_BLOCK = new MapColor(58, 1356933);
   public static final MapColor DEEPSLATE = new MapColor(59, 6579300);
   public static final MapColor RAW_IRON = new MapColor(60, 14200723);
   public static final MapColor GLOW_LICHEN = new MapColor(61, 8365974);
   public final int col;
   public final int id;

   private MapColor(int var1, int var2) {
      super();
      if (var1 >= 0 && var1 <= 63) {
         this.id = var1;
         this.col = var2;
         MATERIAL_COLORS[var1] = this;
      } else {
         throw new IndexOutOfBoundsException("Map colour ID must be between 0 and 63 (inclusive)");
      }
   }

   public int calculateRGBColor(MapColor.Brightness var1) {
      if (this == NONE) {
         return 0;
      } else {
         int var2 = var1.modifier;
         int var3 = (this.col >> 16 & 0xFF) * var2 / 255;
         int var4 = (this.col >> 8 & 0xFF) * var2 / 255;
         int var5 = (this.col & 0xFF) * var2 / 255;
         return 0xFF000000 | var5 << 16 | var4 << 8 | var3;
      }
   }

   public static MapColor byId(int var0) {
      Preconditions.checkPositionIndex(var0, MATERIAL_COLORS.length, "material id");
      return byIdUnsafe(var0);
   }

   private static MapColor byIdUnsafe(int var0) {
      MapColor var1 = MATERIAL_COLORS[var0];
      return var1 != null ? var1 : NONE;
   }

   public static int getColorFromPackedId(int var0) {
      int var1 = var0 & 0xFF;
      return byIdUnsafe(var1 >> 2).calculateRGBColor(MapColor.Brightness.byIdUnsafe(var1 & 3));
   }

   public byte getPackedId(MapColor.Brightness var1) {
      return (byte)(this.id << 2 | var1.id & 3);
   }

   public static enum Brightness {
      LOW(0, 180),
      NORMAL(1, 220),
      HIGH(2, 255),
      LOWEST(3, 135);

      private static final MapColor.Brightness[] VALUES = new MapColor.Brightness[]{LOW, NORMAL, HIGH, LOWEST};
      public final int id;
      public final int modifier;

      private Brightness(final int param3, final int param4) {
         this.id = nullxx;
         this.modifier = nullxxx;
      }

      public static MapColor.Brightness byId(int var0) {
         Preconditions.checkPositionIndex(var0, VALUES.length, "brightness id");
         return byIdUnsafe(var0);
      }

      static MapColor.Brightness byIdUnsafe(int var0) {
         return VALUES[var0];
      }
   }
}
