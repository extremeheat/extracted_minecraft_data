package net.minecraft.client.gui.screens.advancements;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

enum AdvancementTabType {
   ABOVE(new Sprites(Identifier.withDefaultNamespace("advancements/tab_above_left_selected"), Identifier.withDefaultNamespace("advancements/tab_above_middle_selected"), Identifier.withDefaultNamespace("advancements/tab_above_right_selected")), new Sprites(Identifier.withDefaultNamespace("advancements/tab_above_left"), Identifier.withDefaultNamespace("advancements/tab_above_middle"), Identifier.withDefaultNamespace("advancements/tab_above_right")), 28, 32, 8),
   BELOW(new Sprites(Identifier.withDefaultNamespace("advancements/tab_below_left_selected"), Identifier.withDefaultNamespace("advancements/tab_below_middle_selected"), Identifier.withDefaultNamespace("advancements/tab_below_right_selected")), new Sprites(Identifier.withDefaultNamespace("advancements/tab_below_left"), Identifier.withDefaultNamespace("advancements/tab_below_middle"), Identifier.withDefaultNamespace("advancements/tab_below_right")), 28, 32, 8),
   LEFT(new Sprites(Identifier.withDefaultNamespace("advancements/tab_left_top_selected"), Identifier.withDefaultNamespace("advancements/tab_left_middle_selected"), Identifier.withDefaultNamespace("advancements/tab_left_bottom_selected")), new Sprites(Identifier.withDefaultNamespace("advancements/tab_left_top"), Identifier.withDefaultNamespace("advancements/tab_left_middle"), Identifier.withDefaultNamespace("advancements/tab_left_bottom")), 32, 28, 5),
   RIGHT(new Sprites(Identifier.withDefaultNamespace("advancements/tab_right_top_selected"), Identifier.withDefaultNamespace("advancements/tab_right_middle_selected"), Identifier.withDefaultNamespace("advancements/tab_right_bottom_selected")), new Sprites(Identifier.withDefaultNamespace("advancements/tab_right_top"), Identifier.withDefaultNamespace("advancements/tab_right_middle"), Identifier.withDefaultNamespace("advancements/tab_right_bottom")), 32, 28, 5);

   private final Sprites selectedSprites;
   private final Sprites unselectedSprites;
   private final int width;
   private final int height;
   private final int max;

   private AdvancementTabType(final Sprites selectedSprites, final Sprites unselectedSprites, final int width, final int height, final int max) {
      this.selectedSprites = selectedSprites;
      this.unselectedSprites = unselectedSprites;
      this.width = width;
      this.height = height;
      this.max = max;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public int getMax() {
      return this.max;
   }

   public void draw(final GuiGraphics graphics, final int tabX, final int tabY, final boolean selected, final int index) {
      Sprites sprites = selected ? this.selectedSprites : this.unselectedSprites;
      Identifier sprite;
      if (index == 0) {
         sprite = sprites.first();
      } else if (index == this.max - 1) {
         sprite = sprites.last();
      } else {
         sprite = sprites.middle();
      }

      graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, tabX, tabY, this.width, this.height);
   }

   public void drawIcon(final GuiGraphics graphics, final int xo, final int yo, final int index, final ItemStack icon) {
      int x = xo + this.getX(index);
      int y = yo + this.getY(index);
      switch (this.ordinal()) {
         case 0:
            x += 6;
            y += 9;
            break;
         case 1:
            x += 6;
            y += 6;
            break;
         case 2:
            x += 10;
            y += 5;
            break;
         case 3:
            x += 6;
            y += 5;
      }

      graphics.renderFakeItem(icon, x, y);
   }

   public int getX(final int index) {
      switch (this.ordinal()) {
         case 0 -> {
            return (this.width + 4) * index;
         }
         case 1 -> {
            return (this.width + 4) * index;
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

   public int getY(final int index) {
      switch (this.ordinal()) {
         case 0 -> {
            return -this.height + 4;
         }
         case 1 -> {
            return 136;
         }
         case 2 -> {
            return this.height * index;
         }
         case 3 -> {
            return this.height * index;
         }
         default -> throw new UnsupportedOperationException("Don't know what this tab type is!" + String.valueOf(this));
      }
   }

   public boolean isMouseOver(final int xo, final int yo, final int index, final double mx, final double my) {
      int x = xo + this.getX(index);
      int y = yo + this.getY(index);
      return mx > (double)x && mx < (double)(x + this.width) && my > (double)y && my < (double)(y + this.height);
   }

   // $FF: synthetic method
   private static AdvancementTabType[] $values() {
      return new AdvancementTabType[]{ABOVE, BELOW, LEFT, RIGHT};
   }

   private static record Sprites(Identifier first, Identifier middle, Identifier last) {
      private Sprites {
         super();
      }
   }
}
