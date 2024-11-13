package net.minecraft.client.gui.screens.advancements;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

enum AdvancementTabType {
   ABOVE(new Sprites(ResourceLocation.withDefaultNamespace("advancements/tab_above_left_selected"), ResourceLocation.withDefaultNamespace("advancements/tab_above_middle_selected"), ResourceLocation.withDefaultNamespace("advancements/tab_above_right_selected")), new Sprites(ResourceLocation.withDefaultNamespace("advancements/tab_above_left"), ResourceLocation.withDefaultNamespace("advancements/tab_above_middle"), ResourceLocation.withDefaultNamespace("advancements/tab_above_right")), 28, 32, 8),
   BELOW(new Sprites(ResourceLocation.withDefaultNamespace("advancements/tab_below_left_selected"), ResourceLocation.withDefaultNamespace("advancements/tab_below_middle_selected"), ResourceLocation.withDefaultNamespace("advancements/tab_below_right_selected")), new Sprites(ResourceLocation.withDefaultNamespace("advancements/tab_below_left"), ResourceLocation.withDefaultNamespace("advancements/tab_below_middle"), ResourceLocation.withDefaultNamespace("advancements/tab_below_right")), 28, 32, 8),
   LEFT(new Sprites(ResourceLocation.withDefaultNamespace("advancements/tab_left_top_selected"), ResourceLocation.withDefaultNamespace("advancements/tab_left_middle_selected"), ResourceLocation.withDefaultNamespace("advancements/tab_left_bottom_selected")), new Sprites(ResourceLocation.withDefaultNamespace("advancements/tab_left_top"), ResourceLocation.withDefaultNamespace("advancements/tab_left_middle"), ResourceLocation.withDefaultNamespace("advancements/tab_left_bottom")), 32, 28, 5),
   RIGHT(new Sprites(ResourceLocation.withDefaultNamespace("advancements/tab_right_top_selected"), ResourceLocation.withDefaultNamespace("advancements/tab_right_middle_selected"), ResourceLocation.withDefaultNamespace("advancements/tab_right_bottom_selected")), new Sprites(ResourceLocation.withDefaultNamespace("advancements/tab_right_top"), ResourceLocation.withDefaultNamespace("advancements/tab_right_middle"), ResourceLocation.withDefaultNamespace("advancements/tab_right_bottom")), 32, 28, 5);

   private final Sprites selectedSprites;
   private final Sprites unselectedSprites;
   private final int width;
   private final int height;
   private final int max;

   private AdvancementTabType(final Sprites var3, final Sprites var4, final int var5, final int var6, final int var7) {
      this.selectedSprites = var3;
      this.unselectedSprites = var4;
      this.width = var5;
      this.height = var6;
      this.max = var7;
   }

   public int getMax() {
      return this.max;
   }

   public void draw(GuiGraphics var1, int var2, int var3, boolean var4, int var5) {
      Sprites var6 = var4 ? this.selectedSprites : this.unselectedSprites;
      ResourceLocation var7;
      if (var5 == 0) {
         var7 = var6.first();
      } else if (var5 == this.max - 1) {
         var7 = var6.last();
      } else {
         var7 = var6.middle();
      }

      var1.blitSprite(RenderType::guiTextured, var7, var2 + this.getX(var5), var3 + this.getY(var5), this.width, this.height);
   }

   public void drawIcon(GuiGraphics var1, int var2, int var3, int var4, ItemStack var5) {
      int var6 = var2 + this.getX(var4);
      int var7 = var3 + this.getY(var4);
      switch (this.ordinal()) {
         case 0:
            var6 += 6;
            var7 += 9;
            break;
         case 1:
            var6 += 6;
            var7 += 6;
            break;
         case 2:
            var6 += 10;
            var7 += 5;
            break;
         case 3:
            var6 += 6;
            var7 += 5;
      }

      var1.renderFakeItem(var5, var6, var7);
   }

   public int getX(int var1) {
      switch (this.ordinal()) {
         case 0 -> {
            return (this.width + 4) * var1;
         }
         case 1 -> {
            return (this.width + 4) * var1;
         }
         case 2 -> {
            return -this.width + 4;
         }
         case 3 -> {
            return 248;
         }
         default -> throw new UnsupportedOperationException("Don't know what this tab type is!" + String.valueOf(this));
      }
   }

   public int getY(int var1) {
      switch (this.ordinal()) {
         case 0 -> {
            return -this.height + 4;
         }
         case 1 -> {
            return 136;
         }
         case 2 -> {
            return this.height * var1;
         }
         case 3 -> {
            return this.height * var1;
         }
         default -> throw new UnsupportedOperationException("Don't know what this tab type is!" + String.valueOf(this));
      }
   }

   public boolean isMouseOver(int var1, int var2, int var3, double var4, double var6) {
      int var8 = var1 + this.getX(var3);
      int var9 = var2 + this.getY(var3);
      return var4 > (double)var8 && var4 < (double)(var8 + this.width) && var6 > (double)var9 && var6 < (double)(var9 + this.height);
   }

   // $FF: synthetic method
   private static AdvancementTabType[] $values() {
      return new AdvancementTabType[]{ABOVE, BELOW, LEFT, RIGHT};
   }

   static record Sprites(ResourceLocation first, ResourceLocation middle, ResourceLocation last) {
      Sprites(ResourceLocation var1, ResourceLocation var2, ResourceLocation var3) {
         super();
         this.first = var1;
         this.middle = var2;
         this.last = var3;
      }
   }
}
