package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition$MovingObjectType;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

public class ItemEnderEye extends Item {
   public ItemEnderEye() {
      super();
      this.func_77637_a(CreativeTabs.field_78026_f);
   }

   @Override
   public boolean func_77648_a(ItemStack var1, EntityPlayer var2, World var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10) {
      Block var11 = var3.func_147439_a(var4, var5, var6);
      int var12 = var3.func_72805_g(var4, var5, var6);
      if (!var2.func_82247_a(var4, var5, var6, var7, var1) || var11 != Blocks.field_150378_br || BlockEndPortalFrame.func_150020_b(var12)) {
         return false;
      } else if (var3.field_72995_K) {
         return true;
      } else {
         var3.func_72921_c(var4, var5, var6, var12 + 4, 2);
         var3.func_147453_f(var4, var5, var6, Blocks.field_150378_br);
         --var1.field_77994_a;

         for(int var13 = 0; var13 < 16; ++var13) {
            double var14 = (double)((float)var4 + (5.0F + field_77697_d.nextFloat() * 6.0F) / 16.0F);
            double var16 = (double)((float)var5 + 0.8125F);
            double var18 = (double)((float)var6 + (5.0F + field_77697_d.nextFloat() * 6.0F) / 16.0F);
            double var20 = 0.0;
            double var22 = 0.0;
            double var24 = 0.0;
            var3.func_72869_a("smoke", var14, var16, var18, var20, var22, var24);
         }

         int var26 = var12 & 3;
         int var27 = 0;
         int var15 = 0;
         boolean var28 = false;
         boolean var17 = true;
         int var29 = Direction.field_71577_f[var26];

         for(int var19 = -2; var19 <= 2; ++var19) {
            int var33 = var4 + Direction.field_71583_a[var29] * var19;
            int var21 = var6 + Direction.field_71581_b[var29] * var19;
            if (var3.func_147439_a(var33, var5, var21) == Blocks.field_150378_br) {
               if (!BlockEndPortalFrame.func_150020_b(var3.func_72805_g(var33, var5, var21))) {
                  var17 = false;
                  break;
               }

               var15 = var19;
               if (!var28) {
                  var27 = var19;
                  var28 = true;
               }
            }
         }

         if (var17 && var15 == var27 + 2) {
            for(int var30 = var27; var30 <= var15; ++var30) {
               int var34 = var4 + Direction.field_71583_a[var29] * var30;
               int var38 = var6 + Direction.field_71581_b[var29] * var30;
               var34 += Direction.field_71583_a[var26] * 4;
               var38 += Direction.field_71581_b[var26] * 4;
               if (var3.func_147439_a(var34, var5, var38) != Blocks.field_150378_br
                  || !BlockEndPortalFrame.func_150020_b(var3.func_72805_g(var34, var5, var38))) {
                  var17 = false;
                  break;
               }
            }

            for(int var31 = var27 - 1; var31 <= var15 + 1; var31 += 4) {
               for(int var36 = 1; var36 <= 3; ++var36) {
                  int var40 = var4 + Direction.field_71583_a[var29] * var31;
                  int var44 = var6 + Direction.field_71581_b[var29] * var31;
                  var40 += Direction.field_71583_a[var26] * var36;
                  var44 += Direction.field_71581_b[var26] * var36;
                  if (var3.func_147439_a(var40, var5, var44) != Blocks.field_150378_br
                     || !BlockEndPortalFrame.func_150020_b(var3.func_72805_g(var40, var5, var44))) {
                     var17 = false;
                     break;
                  }
               }
            }

            if (var17) {
               for(int var32 = var27; var32 <= var15; ++var32) {
                  for(int var37 = 1; var37 <= 3; ++var37) {
                     int var42 = var4 + Direction.field_71583_a[var29] * var32;
                     int var46 = var6 + Direction.field_71581_b[var29] * var32;
                     var42 += Direction.field_71583_a[var26] * var37;
                     var46 += Direction.field_71581_b[var26] * var37;
                     var3.func_147465_d(var42, var5, var46, Blocks.field_150384_bq, 0, 2);
                  }
               }
            }
         }

         return true;
      }
   }

   @Override
   public ItemStack func_77659_a(ItemStack var1, World var2, EntityPlayer var3) {
      MovingObjectPosition var4 = this.func_77621_a(var2, var3, false);
      if (var4 != null
         && var4.field_72313_a == MovingObjectPosition$MovingObjectType.BLOCK
         && var2.func_147439_a(var4.field_72311_b, var4.field_72312_c, var4.field_72309_d) == Blocks.field_150378_br) {
         return var1;
      } else {
         if (!var2.field_72995_K) {
            ChunkPosition var5 = var2.func_147440_b("Stronghold", (int)var3.field_70165_t, (int)var3.field_70163_u, (int)var3.field_70161_v);
            if (var5 != null) {
               EntityEnderEye var6 = new EntityEnderEye(var2, var3.field_70165_t, var3.field_70163_u + 1.62 - (double)var3.field_70129_M, var3.field_70161_v);
               var6.func_70220_a((double)var5.field_151329_a, var5.field_151327_b, (double)var5.field_151328_c);
               var2.func_72838_d(var6);
               var2.func_72956_a(var3, "random.bow", 0.5F, 0.4F / (field_77697_d.nextFloat() * 0.4F + 0.8F));
               var2.func_72889_a(null, 1002, (int)var3.field_70165_t, (int)var3.field_70163_u, (int)var3.field_70161_v, 0);
               if (!var3.field_71075_bZ.field_75098_d) {
                  --var1.field_77994_a;
               }
            }
         }

         return var1;
      }
   }
}
