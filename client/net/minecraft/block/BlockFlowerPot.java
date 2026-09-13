package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.world.World;

public class BlockFlowerPot extends BlockContainer {
   public BlockFlowerPot() {
      super(Material.field_151594_q);
      this.func_149683_g();
   }

   @Override
   public void func_149683_g() {
      float var1 = 0.375F;
      float var2 = var1 / 2.0F;
      this.func_149676_a(0.5F - var2, 0.0F, 0.5F - var2, 0.5F + var2, var1, 0.5F + var2);
   }

   @Override
   public boolean func_149662_c() {
      return false;
   }

   @Override
   public int func_149645_b() {
      return 33;
   }

   @Override
   public boolean func_149686_d() {
      return false;
   }

   @Override
   public boolean func_149727_a(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
      ItemStack var10 = var5.field_71071_by.func_70448_g();
      if (var10 != null && var10.func_77973_b() instanceof ItemBlock) {
         TileEntityFlowerPot var11 = this.func_149929_e(var1, var2, var3, var4);
         if (var11 == null) {
            return false;
         } else if (var11.func_145965_a() != null) {
            return false;
         } else {
            Block var12 = Block.func_149634_a(var10.func_77973_b());
            if (!this.func_149928_a(var12, var10.func_77960_j())) {
               return false;
            } else {
               var11.func_145964_a(var10.func_77973_b(), var10.func_77960_j());
               var11.func_70296_d();
               if (!var1.func_72921_c(var2, var3, var4, var10.func_77960_j(), 2)) {
                  var1.func_147471_g(var2, var3, var4);
               }

               if (!var5.field_71075_bZ.field_75098_d && --var10.field_77994_a <= 0) {
                  var5.field_71071_by.func_70299_a(var5.field_71071_by.field_70461_c, null);
               }

               return true;
            }
         }
      } else {
         return false;
      }
   }

   private boolean func_149928_a(Block var1, int var2) {
      if (var1 == Blocks.field_150327_N
         || var1 == Blocks.field_150328_O
         || var1 == Blocks.field_150434_aF
         || var1 == Blocks.field_150338_P
         || var1 == Blocks.field_150337_Q
         || var1 == Blocks.field_150345_g
         || var1 == Blocks.field_150330_I) {
         return true;
      } else {
         return var1 == Blocks.field_150329_H && var2 == 2;
      }
   }

   @Override
   public Item func_149694_d(World var1, int var2, int var3, int var4) {
      TileEntityFlowerPot var5 = this.func_149929_e(var1, var2, var3, var4);
      return var5 != null && var5.func_145965_a() != null ? var5.func_145965_a() : Items.field_151162_bE;
   }

   @Override
   public int func_149643_k(World var1, int var2, int var3, int var4) {
      TileEntityFlowerPot var5 = this.func_149929_e(var1, var2, var3, var4);
      return var5 != null && var5.func_145965_a() != null ? var5.func_145966_b() : 0;
   }

   @Override
   public boolean func_149648_K() {
      return true;
   }

   @Override
   public boolean func_149742_c(World var1, int var2, int var3, int var4) {
      return super.func_149742_c(var1, var2, var3, var4) && World.func_147466_a(var1, var2, var3 - 1, var4);
   }

   @Override
   public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
      if (!World.func_147466_a(var1, var2, var3 - 1, var4)) {
         this.func_149697_b(var1, var2, var3, var4, var1.func_72805_g(var2, var3, var4), 0);
         var1.func_147468_f(var2, var3, var4);
      }
   }

   @Override
   public void func_149749_a(World var1, int var2, int var3, int var4, Block var5, int var6) {
      TileEntityFlowerPot var7 = this.func_149929_e(var1, var2, var3, var4);
      if (var7 != null && var7.func_145965_a() != null) {
         this.func_149642_a(var1, var2, var3, var4, new ItemStack(var7.func_145965_a(), 1, var7.func_145966_b()));
      }

      super.func_149749_a(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public void func_149681_a(World var1, int var2, int var3, int var4, int var5, EntityPlayer var6) {
      super.func_149681_a(var1, var2, var3, var4, var5, var6);
      if (var6.field_71075_bZ.field_75098_d) {
         TileEntityFlowerPot var7 = this.func_149929_e(var1, var2, var3, var4);
         if (var7 != null) {
            var7.func_145964_a(Item.func_150899_d(0), 0);
         }
      }
   }

   @Override
   public Item func_149650_a(int var1, Random var2, int var3) {
      return Items.field_151162_bE;
   }

   private TileEntityFlowerPot func_149929_e(World var1, int var2, int var3, int var4) {
      TileEntity var5 = var1.func_147438_o(var2, var3, var4);
      return var5 != null && var5 instanceof TileEntityFlowerPot ? (TileEntityFlowerPot)var5 : null;
   }

   @Override
   public TileEntity func_149915_a(World var1, int var2) {
      Object var3 = null;
      byte var4 = 0;
      switch(var2) {
         case 1:
            var3 = Blocks.field_150328_O;
            var4 = 0;
            break;
         case 2:
            var3 = Blocks.field_150327_N;
            break;
         case 3:
            var3 = Blocks.field_150345_g;
            var4 = 0;
            break;
         case 4:
            var3 = Blocks.field_150345_g;
            var4 = 1;
            break;
         case 5:
            var3 = Blocks.field_150345_g;
            var4 = 2;
            break;
         case 6:
            var3 = Blocks.field_150345_g;
            var4 = 3;
            break;
         case 7:
            var3 = Blocks.field_150337_Q;
            break;
         case 8:
            var3 = Blocks.field_150338_P;
            break;
         case 9:
            var3 = Blocks.field_150434_aF;
            break;
         case 10:
            var3 = Blocks.field_150330_I;
            break;
         case 11:
            var3 = Blocks.field_150329_H;
            var4 = 2;
            break;
         case 12:
            var3 = Blocks.field_150345_g;
            var4 = 4;
            break;
         case 13:
            var3 = Blocks.field_150345_g;
            var4 = 5;
      }

      return new TileEntityFlowerPot(Item.func_150898_a((Block)var3), var4);
   }
}
