package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import org.apache.commons.lang3.tuple.Pair;

public enum BannerPattern {
   BASE("base", "b"),
   SQUARE_BOTTOM_LEFT("square_bottom_left", "bl", "   ", "   ", "#  "),
   SQUARE_BOTTOM_RIGHT("square_bottom_right", "br", "   ", "   ", "  #"),
   SQUARE_TOP_LEFT("square_top_left", "tl", "#  ", "   ", "   "),
   SQUARE_TOP_RIGHT("square_top_right", "tr", "  #", "   ", "   "),
   STRIPE_BOTTOM("stripe_bottom", "bs", "   ", "   ", "###"),
   STRIPE_TOP("stripe_top", "ts", "###", "   ", "   "),
   STRIPE_LEFT("stripe_left", "ls", "#  ", "#  ", "#  "),
   STRIPE_RIGHT("stripe_right", "rs", "  #", "  #", "  #"),
   STRIPE_CENTER("stripe_center", "cs", " # ", " # ", " # "),
   STRIPE_MIDDLE("stripe_middle", "ms", "   ", "###", "   "),
   STRIPE_DOWNRIGHT("stripe_downright", "drs", "#  ", " # ", "  #"),
   STRIPE_DOWNLEFT("stripe_downleft", "dls", "  #", " # ", "#  "),
   STRIPE_SMALL("small_stripes", "ss", "# #", "# #", "   "),
   CROSS("cross", "cr", "# #", " # ", "# #"),
   STRAIGHT_CROSS("straight_cross", "sc", " # ", "###", " # "),
   TRIANGLE_BOTTOM("triangle_bottom", "bt", "   ", " # ", "# #"),
   TRIANGLE_TOP("triangle_top", "tt", "# #", " # ", "   "),
   TRIANGLES_BOTTOM("triangles_bottom", "bts", "   ", "# #", " # "),
   TRIANGLES_TOP("triangles_top", "tts", " # ", "# #", "   "),
   DIAGONAL_LEFT("diagonal_left", "ld", "## ", "#  ", "   "),
   DIAGONAL_RIGHT("diagonal_up_right", "rd", "   ", "  #", " ##"),
   DIAGONAL_LEFT_MIRROR("diagonal_up_left", "lud", "   ", "#  ", "## "),
   DIAGONAL_RIGHT_MIRROR("diagonal_right", "rud", " ##", "  #", "   "),
   CIRCLE_MIDDLE("circle", "mc", "   ", " # ", "   "),
   RHOMBUS_MIDDLE("rhombus", "mr", " # ", "# #", " # "),
   HALF_VERTICAL("half_vertical", "vh", "## ", "## ", "## "),
   HALF_HORIZONTAL("half_horizontal", "hh", "###", "###", "   "),
   HALF_VERTICAL_MIRROR("half_vertical_right", "vhr", " ##", " ##", " ##"),
   HALF_HORIZONTAL_MIRROR("half_horizontal_bottom", "hhb", "   ", "###", "###"),
   BORDER("border", "bo", "###", "# #", "###"),
   CURLY_BORDER("curly_border", "cbo", new ItemStack(Blocks.VINE)),
   GRADIENT("gradient", "gra", "# #", " # ", " # "),
   GRADIENT_UP("gradient_up", "gru", " # ", " # ", "# #"),
   BRICKS("bricks", "bri", new ItemStack(Blocks.BRICKS)),
   GLOBE("globe", "glb"),
   CREEPER("creeper", "cre", new ItemStack(Items.CREEPER_HEAD)),
   SKULL("skull", "sku", new ItemStack(Items.WITHER_SKELETON_SKULL)),
   FLOWER("flower", "flo", new ItemStack(Blocks.OXEYE_DAISY)),
   MOJANG("mojang", "moj", new ItemStack(Items.ENCHANTED_GOLDEN_APPLE));

   public static final int COUNT = values().length;
   public static final int AVAILABLE_PATTERNS = COUNT - 5 - 1;
   private final String filename;
   private final String hashname;
   private final String[] patterns;
   private ItemStack patternItem;

   private BannerPattern(String var3, String var4) {
      this.patterns = new String[3];
      this.patternItem = ItemStack.EMPTY;
      this.filename = var3;
      this.hashname = var4;
   }

   private BannerPattern(String var3, String var4, ItemStack var5) {
      this(var3, var4);
      this.patternItem = var5;
   }

   private BannerPattern(String var3, String var4, String var5, String var6, String var7) {
      this(var3, var4);
      this.patterns[0] = var5;
      this.patterns[1] = var6;
      this.patterns[2] = var7;
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

   public static class Builder {
      private final List patterns = Lists.newArrayList();

      public BannerPattern.Builder addPattern(BannerPattern var1, DyeColor var2) {
         this.patterns.add(Pair.of(var1, var2));
         return this;
      }

      public ListTag toListTag() {
         ListTag var1 = new ListTag();
         Iterator var2 = this.patterns.iterator();

         while(var2.hasNext()) {
            Pair var3 = (Pair)var2.next();
            CompoundTag var4 = new CompoundTag();
            var4.putString("Pattern", ((BannerPattern)var3.getLeft()).hashname);
            var4.putInt("Color", ((DyeColor)var3.getRight()).getId());
            var1.add(var4);
         }

         return var1;
      }
   }
}
