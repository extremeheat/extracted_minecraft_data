package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDragonEgg extends Block {
   public BlockDragonEgg() {
      super(Material.field_151566_D);
      this.func_149676_a(0.0625F, 0.0F, 0.0625F, 0.9375F, 1.0F, 0.9375F);
   }

   @Override
   public void func_149726_b(World var1, int var2, int var3, int var4) {
      var1.func_147464_a(var2, var3, var4, this, this.func_149738_a(var1));
   }

   @Override
   public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
      var1.func_147464_a(var2, var3, var4, this, this.func_149738_a(var1));
   }

   @Override
   public void func_149674_a(World var1, int var2, int var3, int var4, Random var5) {
      this.func_150018_e(var1, var2, var3, var4);
   }

   private void func_150018_e(World var1, int var2, int var3, int var4) {
      if (BlockFalling.func_149831_e(var1, var2, var3 - 1, var4) && var3 >= 0) {
         byte var5 = 32;
         if (!BlockFalling.field_149832_M && var1.func_72904_c(var2 - var5, var3 - var5, var4 - var5, var2 + var5, var3 + var5, var4 + var5)) {
            EntityFallingBlock var6 = new EntityFallingBlock(
               var1, (double)((float)var2 + 0.5F), (double)((float)var3 + 0.5F), (double)((float)var4 + 0.5F), this
            );
            var1.func_72838_d(var6);
         } else {
            var1.func_147468_f(var2, var3, var4);

            while(BlockFalling.func_149831_e(var1, var2, var3 - 1, var4) && var3 > 0) {
               --var3;
            }

            if (var3 > 0) {
               var1.func_147465_d(var2, var3, var4, this, 0, 2);
            }
         }
      }
   }

   @Override
   public boolean func_149727_a(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
      this.func_150019_m(var1, var2, var3, var4);
      return true;
   }

   @Override
   public void func_149699_a(World var1, int var2, int var3, int var4, EntityPlayer var5) {
      this.func_150019_m(var1, var2, var3, var4);
   }

   private void func_150019_m(World var1, int var2, int var3, int var4) {
      if (var1.func_147439_a(var2, var3, var4) == this) {
         for(int var5 = 0; var5 < 1000; ++var5) {
            int var6 = var2 + var1.field_73012_v.nextInt(16) - var1.field_73012_v.nextInt(16);
            int var7 = var3 + var1.field_73012_v.nextInt(8) - var1.field_73012_v.nextInt(8);
            int var8 = var4 + var1.field_73012_v.nextInt(16) - var1.field_73012_v.nextInt(16);
            if (var1.func_147439_a(var6, var7, var8).field_149764_J == Material.field_151579_a) {
               if (!var1.field_72995_K) {
                  var1.func_147465_d(var6, var7, var8, this, var1.func_72805_g(var2, var3, var4), 2);
                  var1.func_147468_f(var2, var3, var4);
               } else {
                  short var9 = 128;

                  for(int var10 = 0; var10 < var9; ++var10) {
                     double var11 = var1.field_73012_v.nextDouble();
                     float var13 = (var1.field_73012_v.nextFloat() - 0.5F) * 0.2F;
                     float var14 = (var1.field_73012_v.nextFloat() - 0.5F) * 0.2F;
                     float var15 = (var1.field_73012_v.nextFloat() - 0.5F) * 0.2F;
                     double var16 = (double)var6 + (double)(var2 - var6) * var11 + (var1.field_73012_v.nextDouble() - 0.5) * 1.0 + 0.5;
                     double var18 = (double)var7 + (double)(var3 - var7) * var11 + var1.field_73012_v.nextDouble() * 1.0 - 0.5;
                     double var20 = (double)var8 + (double)(var4 - var8) * var11 + (var1.field_73012_v.nextDouble() - 0.5) * 1.0 + 0.5;
                     var1.func_72869_a("portal", var16, var18, var20, (double)var13, (double)var14, (double)var15);
                  }
               }

               return;
            }
         }
      }
   }

   @Override
   public int func_149738_a(World var1) {
      return 5;
   }

   @Override
   public boolean func_149662_c() {
      return false;
   }

   @Override
   public boolean func_149686_d() {
      return false;
   }

   @Override
   public boolean func_149646_a(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      return true;
   }

   @Override
   public int func_149645_b() {
      return 27;
   }

   @Override
   public Item func_149694_d(World var1, int var2, int var3, int var4) {
      return Item.func_150899_d(0);
   }
}
