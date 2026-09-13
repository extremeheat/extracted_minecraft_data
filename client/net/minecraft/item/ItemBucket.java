package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition$MovingObjectType;
import net.minecraft.world.World;

public class ItemBucket extends Item {
   private Block field_77876_a;

   public ItemBucket(Block var1) {
      super();
      this.field_77777_bU = 1;
      this.field_77876_a = var1;
      this.func_77637_a(CreativeTabs.field_78026_f);
   }

   @Override
   public ItemStack func_77659_a(ItemStack var1, World var2, EntityPlayer var3) {
      boolean var4 = this.field_77876_a == Blocks.field_150350_a;
      MovingObjectPosition var5 = this.func_77621_a(var2, var3, var4);
      if (var5 == null) {
         return var1;
      } else {
         if (var5.field_72313_a == MovingObjectPosition$MovingObjectType.BLOCK) {
            int var6 = var5.field_72311_b;
            int var7 = var5.field_72312_c;
            int var8 = var5.field_72309_d;
            if (!var2.func_72962_a(var3, var6, var7, var8)) {
               return var1;
            }

            if (var4) {
               if (!var3.func_82247_a(var6, var7, var8, var5.field_72310_e, var1)) {
                  return var1;
               }

               Material var9 = var2.func_147439_a(var6, var7, var8).func_149688_o();
               int var10 = var2.func_72805_g(var6, var7, var8);
               if (var9 == Material.field_151586_h && var10 == 0) {
                  var2.func_147468_f(var6, var7, var8);
                  return this.func_150910_a(var1, var3, Items.field_151131_as);
               }

               if (var9 == Material.field_151587_i && var10 == 0) {
                  var2.func_147468_f(var6, var7, var8);
                  return this.func_150910_a(var1, var3, Items.field_151129_at);
               }
            } else {
               if (this.field_77876_a == Blocks.field_150350_a) {
                  return new ItemStack(Items.field_151133_ar);
               }

               if (var5.field_72310_e == 0) {
                  --var7;
               }

               if (var5.field_72310_e == 1) {
                  ++var7;
               }

               if (var5.field_72310_e == 2) {
                  --var8;
               }

               if (var5.field_72310_e == 3) {
                  ++var8;
               }

               if (var5.field_72310_e == 4) {
                  --var6;
               }

               if (var5.field_72310_e == 5) {
                  ++var6;
               }

               if (!var3.func_82247_a(var6, var7, var8, var5.field_72310_e, var1)) {
                  return var1;
               }

               if (this.func_77875_a(var2, var6, var7, var8) && !var3.field_71075_bZ.field_75098_d) {
                  return new ItemStack(Items.field_151133_ar);
               }
            }
         }

         return var1;
      }
   }

   private ItemStack func_150910_a(ItemStack var1, EntityPlayer var2, Item var3) {
      if (var2.field_71075_bZ.field_75098_d) {
         return var1;
      } else if (--var1.field_77994_a <= 0) {
         return new ItemStack(var3);
      } else {
         if (!var2.field_71071_by.func_70441_a(new ItemStack(var3))) {
            var2.func_71019_a(new ItemStack(var3, 1, 0), false);
         }

         return var1;
      }
   }

   public boolean func_77875_a(World var1, int var2, int var3, int var4) {
      if (this.field_77876_a == Blocks.field_150350_a) {
         return false;
      } else {
         Material var5 = var1.func_147439_a(var2, var3, var4).func_149688_o();
         boolean var6 = !var5.func_76220_a();
         if (!var1.func_147437_c(var2, var3, var4) && !var6) {
            return false;
         } else {
            if (var1.field_73011_w.field_76575_d && this.field_77876_a == Blocks.field_150358_i) {
               var1.func_72908_a(
                  (double)((float)var2 + 0.5F),
                  (double)((float)var3 + 0.5F),
                  (double)((float)var4 + 0.5F),
                  "random.fizz",
                  0.5F,
                  2.6F + (var1.field_73012_v.nextFloat() - var1.field_73012_v.nextFloat()) * 0.8F
               );

               for(int var7 = 0; var7 < 8; ++var7) {
                  var1.func_72869_a("largesmoke", (double)var2 + Math.random(), (double)var3 + Math.random(), (double)var4 + Math.random(), 0.0, 0.0, 0.0);
               }
            } else {
               if (!var1.field_72995_K && var6 && !var5.func_76224_d()) {
                  var1.func_147480_a(var2, var3, var4, true);
               }

               var1.func_147465_d(var2, var3, var4, this.field_77876_a, 0, 3);
            }

            return true;
         }
      }
   }
}
