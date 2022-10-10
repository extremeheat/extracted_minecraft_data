package net.minecraft.client.gui.advancements;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

enum AdvancementTabType {
   ABOVE(0, 0, 28, 32, 8),
   BELOW(84, 0, 28, 32, 8),
   LEFT(0, 64, 32, 28, 5),
   RIGHT(96, 64, 32, 28, 5);

   private final int field_192660_f;
   private final int field_192661_g;
   private final int field_192662_h;
   private final int field_192663_i;
   private final int field_192664_j;

   private AdvancementTabType(int var3, int var4, int var5, int var6, int var7) {
      this.field_192660_f = var3;
      this.field_192661_g = var4;
      this.field_192662_h = var5;
      this.field_192663_i = var6;
      this.field_192664_j = var7;
   }

   public int func_192650_a() {
      return this.field_192664_j;
   }

   public void func_192651_a(Gui var1, int var2, int var3, boolean var4, int var5) {
      int var6 = this.field_192660_f;
      if (var5 > 0) {
         var6 += this.field_192662_h;
      }

      if (var5 == this.field_192664_j - 1) {
         var6 += this.field_192662_h;
      }

      int var7 = var4 ? this.field_192661_g + this.field_192663_i : this.field_192661_g;
      var1.func_73729_b(var2 + this.func_192648_a(var5), var3 + this.func_192653_b(var5), var6, var7, this.field_192662_h, this.field_192663_i);
   }

   public void func_192652_a(int var1, int var2, int var3, ItemRenderer var4, ItemStack var5) {
      int var6 = var1 + this.func_192648_a(var3);
      int var7 = var2 + this.func_192653_b(var3);
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

      var4.func_184391_a((EntityLivingBase)null, var5, var6, var7);
   }

   public int func_192648_a(int var1) {
      switch(this) {
      case ABOVE:
         return (this.field_192662_h + 4) * var1;
      case BELOW:
         return (this.field_192662_h + 4) * var1;
      case LEFT:
         return -this.field_192662_h + 4;
      case RIGHT:
         return 248;
      default:
         throw new UnsupportedOperationException("Don't know what this tab type is!" + this);
      }
   }

   public int func_192653_b(int var1) {
      switch(this) {
      case ABOVE:
         return -this.field_192663_i + 4;
      case BELOW:
         return 136;
      case LEFT:
         return this.field_192663_i * var1;
      case RIGHT:
         return this.field_192663_i * var1;
      default:
         throw new UnsupportedOperationException("Don't know what this tab type is!" + this);
      }
   }

   public boolean func_198891_a(int var1, int var2, int var3, double var4, double var6) {
      int var8 = var1 + this.func_192648_a(var3);
      int var9 = var2 + this.func_192653_b(var3);
      return var4 > (double)var8 && var4 < (double)(var8 + this.field_192662_h) && var6 > (double)var9 && var6 < (double)(var9 + this.field_192663_i);
   }
}
