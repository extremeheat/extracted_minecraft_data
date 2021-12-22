package net.minecraft.client.gui.screens.advancements;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;

enum AdvancementTabType {
   ABOVE(0, 0, 28, 32, 8),
   BELOW(84, 0, 28, 32, 8),
   LEFT(0, 64, 32, 28, 5),
   RIGHT(96, 64, 32, 28, 5);

   private final int textureX;
   private final int textureY;
   private final int width;
   private final int height;
   private final int max;

   private AdvancementTabType(int var3, int var4, int var5, int var6, int var7) {
      this.textureX = var3;
      this.textureY = var4;
      this.width = var5;
      this.height = var6;
      this.max = var7;
   }

   public int getMax() {
      return this.max;
   }

   public void draw(PoseStack var1, GuiComponent var2, int var3, int var4, boolean var5, int var6) {
      int var7 = this.textureX;
      if (var6 > 0) {
         var7 += this.width;
      }

      if (var6 == this.max - 1) {
         var7 += this.width;
      }

      int var8 = var5 ? this.textureY + this.height : this.textureY;
      var2.blit(var1, var3 + this.getX(var6), var4 + this.getY(var6), var7, var8, this.width, this.height);
   }

   public void drawIcon(int var1, int var2, int var3, ItemRenderer var4, ItemStack var5) {
      int var6 = var1 + this.getX(var3);
      int var7 = var2 + this.getY(var3);
      switch(this) {
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

      var4.renderAndDecorateFakeItem(var5, var6, var7);
   }

   public int getX(int var1) {
      switch(this) {
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
      switch(this) {
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

   // $FF: synthetic method
   private static AdvancementTabType[] $values() {
      return new AdvancementTabType[]{ABOVE, BELOW, LEFT, RIGHT};
   }
}
