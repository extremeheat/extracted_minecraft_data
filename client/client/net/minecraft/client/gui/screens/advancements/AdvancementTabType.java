package net.minecraft.client.gui.screens.advancements;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

enum AdvancementTabType {
   ABOVE(
      new AdvancementTabType.Sprites(
         new ResourceLocation("advancements/tab_above_left_selected"),
         new ResourceLocation("advancements/tab_above_middle_selected"),
         new ResourceLocation("advancements/tab_above_right_selected")
      ),
      new AdvancementTabType.Sprites(
         new ResourceLocation("advancements/tab_above_left"),
         new ResourceLocation("advancements/tab_above_middle"),
         new ResourceLocation("advancements/tab_above_right")
      ),
      28,
      32,
      8
   ),
   BELOW(
      new AdvancementTabType.Sprites(
         new ResourceLocation("advancements/tab_below_left_selected"),
         new ResourceLocation("advancements/tab_below_middle_selected"),
         new ResourceLocation("advancements/tab_below_right_selected")
      ),
      new AdvancementTabType.Sprites(
         new ResourceLocation("advancements/tab_below_left"),
         new ResourceLocation("advancements/tab_below_middle"),
         new ResourceLocation("advancements/tab_below_right")
      ),
      28,
      32,
      8
   ),
   LEFT(
      new AdvancementTabType.Sprites(
         new ResourceLocation("advancements/tab_left_top_selected"),
         new ResourceLocation("advancements/tab_left_middle_selected"),
         new ResourceLocation("advancements/tab_left_bottom_selected")
      ),
      new AdvancementTabType.Sprites(
         new ResourceLocation("advancements/tab_left_top"),
         new ResourceLocation("advancements/tab_left_middle"),
         new ResourceLocation("advancements/tab_left_bottom")
      ),
      32,
      28,
      5
   ),
   RIGHT(
      new AdvancementTabType.Sprites(
         new ResourceLocation("advancements/tab_right_top_selected"),
         new ResourceLocation("advancements/tab_right_middle_selected"),
         new ResourceLocation("advancements/tab_right_bottom_selected")
      ),
      new AdvancementTabType.Sprites(
         new ResourceLocation("advancements/tab_right_top"),
         new ResourceLocation("advancements/tab_right_middle"),
         new ResourceLocation("advancements/tab_right_bottom")
      ),
      32,
      28,
      5
   );

   private final AdvancementTabType.Sprites selectedSprites;
   private final AdvancementTabType.Sprites unselectedSprites;
   private final int width;
   private final int height;
   private final int max;

   private AdvancementTabType(
      final AdvancementTabType.Sprites param3, final AdvancementTabType.Sprites param4, final int param5, final int param6, final int param7
   ) {
      this.selectedSprites = nullxx;
      this.unselectedSprites = nullxxx;
      this.width = nullxxxx;
      this.height = nullxxxxx;
      this.max = nullxxxxxx;
   }

   public int getMax() {
      return this.max;
   }

   public void draw(GuiGraphics var1, int var2, int var3, boolean var4, int var5) {
      AdvancementTabType.Sprites var6 = var4 ? this.selectedSprites : this.unselectedSprites;
      ResourceLocation var7;
      if (var5 == 0) {
         var7 = var6.first();
      } else if (var5 == this.max - 1) {
         var7 = var6.last();
      } else {
         var7 = var6.middle();
      }

      var1.blitSprite(var7, var2 + this.getX(var5), var3 + this.getY(var5), this.width, this.height);
   }

   public void drawIcon(GuiGraphics var1, int var2, int var3, int var4, ItemStack var5) {
      int var6 = var2 + this.getX(var4);
      int var7 = var3 + this.getY(var4);
      switch (this) {
         case ABOVE:
            var6 += 6;
            var7 += 9;
            break;
         case BELOW:
            var6 += 6;
            var7 += 6;
            break;
         case LEFT:
            var6 += 10;
            var7 += 5;
            break;
         case RIGHT:
            var6 += 6;
            var7 += 5;
      }

      var1.renderFakeItem(var5, var6, var7);
   }

   public int getX(int var1) {
      switch (this) {
         case ABOVE:
            return (this.width + 4) * var1;
         case BELOW:
            return (this.width + 4) * var1;
         case LEFT:
            return -this.width + 4;
         case RIGHT:
            return 248;
         default:
            throw new UnsupportedOperationException("Don't know what this tab type is!" + this);
      }
   }

   public int getY(int var1) {
      switch (this) {
         case ABOVE:
            return -this.height + 4;
         case BELOW:
            return 136;
         case LEFT:
            return this.height * var1;
         case RIGHT:
            return this.height * var1;
         default:
            throw new UnsupportedOperationException("Don't know what this tab type is!" + this);
      }
   }

   public boolean isMouseOver(int var1, int var2, int var3, double var4, double var6) {
      int var8 = var1 + this.getX(var3);
      int var9 = var2 + this.getY(var3);
      return var4 > (double)var8 && var4 < (double)(var8 + this.width) && var6 > (double)var9 && var6 < (double)(var9 + this.height);
   }

   static record Sprites(ResourceLocation first, ResourceLocation middle, ResourceLocation last) {
      Sprites(ResourceLocation first, ResourceLocation middle, ResourceLocation last) {
         super();
         this.first = first;
         this.middle = middle;
         this.last = last;
      }
   }
}
