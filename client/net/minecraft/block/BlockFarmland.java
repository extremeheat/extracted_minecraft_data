package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockFarmland extends Block {
   private IIcon field_149824_a;
   private IIcon field_149823_b;

   protected BlockFarmland() {
      super(Material.field_151578_c);
      this.func_149675_a(true);
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.9375F, 1.0F);
      this.func_149713_g(255);
   }

   @Override
   public AxisAlignedBB func_149668_a(World var1, int var2, int var3, int var4) {
      return AxisAlignedBB.func_72330_a((double)(var2 + 0), (double)(var3 + 0), (double)(var4 + 0), (double)(var2 + 1), (double)(var3 + 1), (double)(var4 + 1));
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
   public IIcon func_149691_a(int var1, int var2) {
      if (var1 == 1) {
         return var2 > 0 ? this.field_149824_a : this.field_149823_b;
      } else {
         return Blocks.field_150346_d.func_149733_h(var1);
      }
   }

   @Override
   public void func_149674_a(World var1, int var2, int var3, int var4, Random var5) {
      if (!this.func_149821_m(var1, var2, var3, var4) && !var1.func_72951_B(var2, var3 + 1, var4)) {
         int var6 = var1.func_72805_g(var2, var3, var4);
         if (var6 > 0) {
            var1.func_72921_c(var2, var3, var4, var6 - 1, 2);
         } else if (!this.func_149822_e(var1, var2, var3, var4)) {
            var1.func_147449_b(var2, var3, var4, Blocks.field_150346_d);
         }
      } else {
         var1.func_72921_c(var2, var3, var4, 7, 2);
      }
   }

   @Override
   public void func_149746_a(World var1, int var2, int var3, int var4, Entity var5, float var6) {
      if (!var1.field_72995_K && var1.field_73012_v.nextFloat() < var6 - 0.5F) {
         if (!(var5 instanceof EntityPlayer) && !var1.func_82736_K().func_82766_b("mobGriefing")) {
            return;
         }

         var1.func_147449_b(var2, var3, var4, Blocks.field_150346_d);
      }
   }

   private boolean func_149822_e(World var1, int var2, int var3, int var4) {
      byte var5 = 0;

      for(int var6 = var2 - var5; var6 <= var2 + var5; ++var6) {
         for(int var7 = var4 - var5; var7 <= var4 + var5; ++var7) {
            Block var8 = var1.func_147439_a(var6, var3 + 1, var7);
            if (var8 == Blocks.field_150464_aj
               || var8 == Blocks.field_150394_bc
               || var8 == Blocks.field_150393_bb
               || var8 == Blocks.field_150469_bN
               || var8 == Blocks.field_150459_bM) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean func_149821_m(World var1, int var2, int var3, int var4) {
      for(int var5 = var2 - 4; var5 <= var2 + 4; ++var5) {
         for(int var6 = var3; var6 <= var3 + 1; ++var6) {
            for(int var7 = var4 - 4; var7 <= var4 + 4; ++var7) {
               if (var1.func_147439_a(var5, var6, var7).func_149688_o() == Material.field_151586_h) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   @Override
   public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
      super.func_149695_a(var1, var2, var3, var4, var5);
      Material var6 = var1.func_147439_a(var2, var3 + 1, var4).func_149688_o();
      if (var6.func_76220_a()) {
         var1.func_147449_b(var2, var3, var4, Blocks.field_150346_d);
      }
   }

   @Override
   public Item func_149650_a(int var1, Random var2, int var3) {
      return Blocks.field_150346_d.func_149650_a(0, var2, var3);
   }

   @Override
   public Item func_149694_d(World var1, int var2, int var3, int var4) {
      return Item.func_150898_a(Blocks.field_150346_d);
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_149824_a = var1.func_94245_a(this.func_149641_N() + "_wet");
      this.field_149823_b = var1.func_94245_a(this.func_149641_N() + "_dry");
   }
}
