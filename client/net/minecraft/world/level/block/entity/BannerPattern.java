package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

public enum BannerPattern {
   BASE("base", "b", false),
   SQUARE_BOTTOM_LEFT("square_bottom_left", "bl"),
   SQUARE_BOTTOM_RIGHT("square_bottom_right", "br"),
   SQUARE_TOP_LEFT("square_top_left", "tl"),
   SQUARE_TOP_RIGHT("square_top_right", "tr"),
   STRIPE_BOTTOM("stripe_bottom", "bs"),
   STRIPE_TOP("stripe_top", "ts"),
   STRIPE_LEFT("stripe_left", "ls"),
   STRIPE_RIGHT("stripe_right", "rs"),
   STRIPE_CENTER("stripe_center", "cs"),
   STRIPE_MIDDLE("stripe_middle", "ms"),
   STRIPE_DOWNRIGHT("stripe_downright", "drs"),
   STRIPE_DOWNLEFT("stripe_downleft", "dls"),
   STRIPE_SMALL("small_stripes", "ss"),
   CROSS("cross", "cr"),
   STRAIGHT_CROSS("straight_cross", "sc"),
   TRIANGLE_BOTTOM("triangle_bottom", "bt"),
   TRIANGLE_TOP("triangle_top", "tt"),
   TRIANGLES_BOTTOM("triangles_bottom", "bts"),
   TRIANGLES_TOP("triangles_top", "tts"),
   DIAGONAL_LEFT("diagonal_left", "ld"),
   DIAGONAL_RIGHT("diagonal_up_right", "rd"),
   DIAGONAL_LEFT_MIRROR("diagonal_up_left", "lud"),
   DIAGONAL_RIGHT_MIRROR("diagonal_right", "rud"),
   CIRCLE_MIDDLE("circle", "mc"),
   RHOMBUS_MIDDLE("rhombus", "mr"),
   HALF_VERTICAL("half_vertical", "vh"),
   HALF_HORIZONTAL("half_horizontal", "hh"),
   HALF_VERTICAL_MIRROR("half_vertical_right", "vhr"),
   HALF_HORIZONTAL_MIRROR("half_horizontal_bottom", "hhb"),
   BORDER("border", "bo"),
   CURLY_BORDER("curly_border", "cbo"),
   GRADIENT("gradient", "gra"),
   GRADIENT_UP("gradient_up", "gru"),
   BRICKS("bricks", "bri"),
   GLOBE("globe", "glb", true),
   CREEPER("creeper", "cre", true),
   SKULL("skull", "sku", true),
   FLOWER("flower", "flo", true),
   MOJANG("mojang", "moj", true),
   PIGLIN("piglin", "pig", true);

   private static final BannerPattern[] VALUES = values();
   public static final int COUNT = VALUES.length;
   public static final int PATTERN_ITEM_COUNT = (int)Arrays.stream(VALUES).filter((var0) -> {
      return var0.hasPatternItem;
   }).count();
   public static final int AVAILABLE_PATTERNS = COUNT - PATTERN_ITEM_COUNT - 1;
   private final boolean hasPatternItem;
   private final String filename;
   private final String hashname;

   private BannerPattern(String var3, String var4) {
      this(var3, var4, false);
   }

   private BannerPattern(String var3, String var4, boolean var5) {
      this.filename = var3;
      this.hashname = var4;
      this.hasPatternItem = var5;
   }

   public ResourceLocation location(boolean var1) {
      String var2 = var1 ? "banner" : "shield";
      return new ResourceLocation("entity/" + var2 + "/" + this.getFilename());
   }

   public String getFilename() {
      return this.filename;
   }

   public String getHashname() {
      return this.hashname;
   }

   @Nullable
   public static BannerPattern byHash(String var0) {
      BannerPattern[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         BannerPattern var4 = var1[var3];
         if (var4.hashname.equals(var0)) {
            return var4;
         }
      }

      return null;
   }

   @Nullable
   public static BannerPattern byFilename(String var0) {
      BannerPattern[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         BannerPattern var4 = var1[var3];
         if (var4.filename.equals(var0)) {
            return var4;
         }
      }

      return null;
   }

   public static class Builder {
      private final List<Pair<BannerPattern, DyeColor>> patterns = Lists.newArrayList();

      public Builder() {
         super();
      }

      public BannerPattern.Builder addPattern(BannerPattern var1, DyeColor var2) {
         return this.addPattern(Pair.of(var1, var2));
      }

      public BannerPattern.Builder addPattern(Pair<BannerPattern, DyeColor> var1) {
         this.patterns.add(var1);
         return this;
      }

      public ListTag toListTag() {
         ListTag var1 = new ListTag();
         Iterator var2 = this.patterns.iterator();

         while(var2.hasNext()) {
            Pair var3 = (Pair)var2.next();
            CompoundTag var4 = new CompoundTag();
            var4.putString("Pattern", ((BannerPattern)var3.getFirst()).hashname);
            var4.putInt("Color", ((DyeColor)var3.getSecond()).getId());
            var1.add(var4);
         }

         return var1;
      }
   }
}
