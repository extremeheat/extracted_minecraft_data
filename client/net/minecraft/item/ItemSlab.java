package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemSlab extends ItemBlock {
   private final boolean field_150948_b;
   private final BlockSlab field_150949_c;
   private final BlockSlab field_150947_d;

   public ItemSlab(Block var1, BlockSlab var2, BlockSlab var3, boolean var4) {
      super(var1);
      this.field_150949_c = var2;
      this.field_150947_d = var3;
      this.field_150948_b = var4;
      this.func_77656_e(0);
      this.func_77627_a(true);
   }

   @Override
   public IIcon func_77617_a(int var1) {
      return Block.func_149634_a(this).func_149691_a(2, var1);
   }

   @Override
   public int func_77647_b(int var1) {
      return var1;
   }

   @Override
   public String func_77667_c(ItemStack var1) {
      return this.field_150949_c.func_150002_b(var1.func_77960_j());
   }

   @Override
   public boolean func_77648_a(ItemStack var1, EntityPlayer var2, World var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10) {
      if (this.field_150948_b) {
         return super.func_77648_a(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
      } else if (var1.field_77994_a == 0) {
         return false;
      } else if (!var2.func_82247_a(var4, var5, var6, var7, var1)) {
         return false;
      } else {
         Block var11 = var3.func_147439_a(var4, var5, var6);
         int var12 = var3.func_72805_g(var4, var5, var6);
         int var13 = var12 & 7;
         boolean var14 = (var12 & 8) != 0;
         if ((var7 == 1 && !var14 || var7 == 0 && var14) && var11 == this.field_150949_c && var13 == var1.func_77960_j()) {
            if (var3.func_72855_b(this.field_150947_d.func_149668_a(var3, var4, var5, var6))
               && var3.func_147465_d(var4, var5, var6, this.field_150947_d, var13, 3)) {
               var3.func_72908_a(
                  (double)((float)var4 + 0.5F),
                  (double)((float)var5 + 0.5F),
                  (double)((float)var6 + 0.5F),
                  this.field_150947_d.field_149762_H.func_150496_b(),
                  (this.field_150947_d.field_149762_H.func_150497_c() + 1.0F) / 2.0F,
                  this.field_150947_d.field_149762_H.func_150494_d() * 0.8F
               );
               --var1.field_77994_a;
            }

            return true;
         } else {
            return this.func_150946_a(var1, var2, var3, var4, var5, var6, var7)
               ? true
               : super.func_77648_a(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
         }
      }
   }

   @Override
   public boolean func_150936_a(World var1, int var2, int var3, int var4, int var5, EntityPlayer var6, ItemStack var7) {
      int var8 = var2;
      int var9 = var3;
      int var10 = var4;
      Block var11 = var1.func_147439_a(var2, var3, var4);
      int var12 = var1.func_72805_g(var2, var3, var4);
      int var13 = var12 & 7;
      boolean var14 = (var12 & 8) != 0;
      if ((var5 == 1 && !var14 || var5 == 0 && var14) && var11 == this.field_150949_c && var13 == var7.func_77960_j()) {
         return true;
      } else {
         if (var5 == 0) {
            --var3;
         }

         if (var5 == 1) {
            ++var3;
         }

         if (var5 == 2) {
            --var4;
         }

         if (var5 == 3) {
            ++var4;
         }

         if (var5 == 4) {
            --var2;
         }

         if (var5 == 5) {
            ++var2;
         }

         Block var15 = var1.func_147439_a(var2, var3, var4);
         int var16 = var1.func_72805_g(var2, var3, var4);
         var13 = var16 & 7;
         return var15 == this.field_150949_c && var13 == var7.func_77960_j() ? true : super.func_150936_a(var1, var8, var9, var10, var5, var6, var7);
      }
   }

   private boolean func_150946_a(ItemStack var1, EntityPlayer var2, World var3, int var4, int var5, int var6, int var7) {
      if (var7 == 0) {
         --var5;
      }

      if (var7 == 1) {
         ++var5;
      }

      if (var7 == 2) {
         --var6;
      }

      if (var7 == 3) {
         ++var6;
      }

      if (var7 == 4) {
         --var4;
      }

      if (var7 == 5) {
         ++var4;
      }

      Block var8 = var3.func_147439_a(var4, var5, var6);
      int var9 = var3.func_72805_g(var4, var5, var6);
      int var10 = var9 & 7;
      if (var8 == this.field_150949_c && var10 == var1.func_77960_j()) {
         if (var3.func_72855_b(this.field_150947_d.func_149668_a(var3, var4, var5, var6))
            && var3.func_147465_d(var4, var5, var6, this.field_150947_d, var10, 3)) {
            var3.func_72908_a(
               (double)((float)var4 + 0.5F),
               (double)((float)var5 + 0.5F),
               (double)((float)var6 + 0.5F),
               this.field_150947_d.field_149762_H.func_150496_b(),
               (this.field_150947_d.field_149762_H.func_150497_c() + 1.0F) / 2.0F,
               this.field_150947_d.field_149762_H.func_150494_d() * 0.8F
            );
            --var1.field_77994_a;
         }

         return true;
      } else {
         return false;
      }
   }
}
