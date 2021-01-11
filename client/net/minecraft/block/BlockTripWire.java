package net.minecraft.block;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTripWire extends Block {
   public static final PropertyBool field_176293_a = PropertyBool.func_177716_a("powered");
   public static final PropertyBool field_176290_b = PropertyBool.func_177716_a("suspended");
   public static final PropertyBool field_176294_M = PropertyBool.func_177716_a("attached");
   public static final PropertyBool field_176295_N = PropertyBool.func_177716_a("disarmed");
   public static final PropertyBool field_176296_O = PropertyBool.func_177716_a("north");
   public static final PropertyBool field_176291_P = PropertyBool.func_177716_a("east");
   public static final PropertyBool field_176289_Q = PropertyBool.func_177716_a("south");
   public static final PropertyBool field_176292_R = PropertyBool.func_177716_a("west");

   public BlockTripWire() {
      super(Material.field_151594_q);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176293_a, false).func_177226_a(field_176290_b, false).func_177226_a(field_176294_M, false).func_177226_a(field_176295_N, false).func_177226_a(field_176296_O, false).func_177226_a(field_176291_P, false).func_177226_a(field_176289_Q, false).func_177226_a(field_176292_R, false));
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.15625F, 1.0F);
      this.func_149675_a(true);
   }

   public IBlockState func_176221_a(IBlockState var1, IBlockAccess var2, BlockPos var3) {
      return var1.func_177226_a(field_176296_O, func_176287_c(var2, var3, var1, EnumFacing.NORTH)).func_177226_a(field_176291_P, func_176287_c(var2, var3, var1, EnumFacing.EAST)).func_177226_a(field_176289_Q, func_176287_c(var2, var3, var1, EnumFacing.SOUTH)).func_177226_a(field_176292_R, func_176287_c(var2, var3, var1, EnumFacing.WEST));
   }

   public AxisAlignedBB func_180640_a(World var1, BlockPos var2, IBlockState var3) {
      return null;
   }

   public boolean func_149662_c() {
      return false;
   }

   public boolean func_149686_d() {
      return false;
   }

   public EnumWorldBlockLayer func_180664_k() {
      return EnumWorldBlockLayer.TRANSLUCENT;
   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return Items.field_151007_F;
   }

   public Item func_180665_b(World var1, BlockPos var2) {
      return Items.field_151007_F;
   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      boolean var5 = (Boolean)var3.func_177229_b(field_176290_b);
      boolean var6 = !World.func_175683_a(var1, var2.func_177977_b());
      if (var5 != var6) {
         this.func_176226_b(var1, var2, var3, 0);
         var1.func_175698_g(var2);
      }

   }

   public void func_180654_a(IBlockAccess var1, BlockPos var2) {
      IBlockState var3 = var1.func_180495_p(var2);
      boolean var4 = (Boolean)var3.func_177229_b(field_176294_M);
      boolean var5 = (Boolean)var3.func_177229_b(field_176290_b);
      if (!var5) {
         this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.09375F, 1.0F);
      } else if (!var4) {
         this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
      } else {
         this.func_149676_a(0.0F, 0.0625F, 0.0F, 1.0F, 0.15625F, 1.0F);
      }

   }

   public void func_176213_c(World var1, BlockPos var2, IBlockState var3) {
      var3 = var3.func_177226_a(field_176290_b, !World.func_175683_a(var1, var2.func_177977_b()));
      var1.func_180501_a(var2, var3, 3);
      this.func_176286_e(var1, var2, var3);
   }

   public void func_180663_b(World var1, BlockPos var2, IBlockState var3) {
      this.func_176286_e(var1, var2, var3.func_177226_a(field_176293_a, true));
   }

   public void func_176208_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4) {
      if (!var1.field_72995_K) {
         if (var4.func_71045_bC() != null && var4.func_71045_bC().func_77973_b() == Items.field_151097_aZ) {
            var1.func_180501_a(var2, var3.func_177226_a(field_176295_N, true), 4);
         }

      }
   }

   private void func_176286_e(World var1, BlockPos var2, IBlockState var3) {
      EnumFacing[] var4 = new EnumFacing[]{EnumFacing.SOUTH, EnumFacing.WEST};
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         EnumFacing var7 = var4[var6];

         for(int var8 = 1; var8 < 42; ++var8) {
            BlockPos var9 = var2.func_177967_a(var7, var8);
            IBlockState var10 = var1.func_180495_p(var9);
            if (var10.func_177230_c() == Blocks.field_150479_bC) {
               if (var10.func_177229_b(BlockTripWireHook.field_176264_a) == var7.func_176734_d()) {
                  Blocks.field_150479_bC.func_176260_a(var1, var9, var10, false, true, var8, var3);
               }
               break;
            }

            if (var10.func_177230_c() != Blocks.field_150473_bD) {
               break;
            }
         }
      }

   }

   public void func_180634_a(World var1, BlockPos var2, IBlockState var3, Entity var4) {
      if (!var1.field_72995_K) {
         if (!(Boolean)var3.func_177229_b(field_176293_a)) {
            this.func_176288_d(var1, var2);
         }
      }
   }

   public void func_180645_a(World var1, BlockPos var2, IBlockState var3, Random var4) {
   }

   public void func_180650_b(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (!var1.field_72995_K) {
         if ((Boolean)var1.func_180495_p(var2).func_177229_b(field_176293_a)) {
            this.func_176288_d(var1, var2);
         }
      }
   }

   private void func_176288_d(World var1, BlockPos var2) {
      IBlockState var3 = var1.func_180495_p(var2);
      boolean var4 = (Boolean)var3.func_177229_b(field_176293_a);
      boolean var5 = false;
      List var6 = var1.func_72839_b((Entity)null, new AxisAlignedBB((double)var2.func_177958_n() + this.field_149759_B, (double)var2.func_177956_o() + this.field_149760_C, (double)var2.func_177952_p() + this.field_149754_D, (double)var2.func_177958_n() + this.field_149755_E, (double)var2.func_177956_o() + this.field_149756_F, (double)var2.func_177952_p() + this.field_149757_G));
      if (!var6.isEmpty()) {
         Iterator var7 = var6.iterator();

         while(var7.hasNext()) {
            Entity var8 = (Entity)var7.next();
            if (!var8.func_145773_az()) {
               var5 = true;
               break;
            }
         }
      }

      if (var5 != var4) {
         var3 = var3.func_177226_a(field_176293_a, var5);
         var1.func_180501_a(var2, var3, 3);
         this.func_176286_e(var1, var2, var3);
      }

      if (var5) {
         var1.func_175684_a(var2, this, this.func_149738_a(var1));
      }

   }

   public static boolean func_176287_c(IBlockAccess var0, BlockPos var1, IBlockState var2, EnumFacing var3) {
      BlockPos var4 = var1.func_177972_a(var3);
      IBlockState var5 = var0.func_180495_p(var4);
      Block var6 = var5.func_177230_c();
      if (var6 == Blocks.field_150479_bC) {
         EnumFacing var9 = var3.func_176734_d();
         return var5.func_177229_b(BlockTripWireHook.field_176264_a) == var9;
      } else if (var6 == Blocks.field_150473_bD) {
         boolean var7 = (Boolean)var2.func_177229_b(field_176290_b);
         boolean var8 = (Boolean)var5.func_177229_b(field_176290_b);
         return var7 == var8;
      } else {
         return false;
      }
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176293_a, (var1 & 1) > 0).func_177226_a(field_176290_b, (var1 & 2) > 0).func_177226_a(field_176294_M, (var1 & 4) > 0).func_177226_a(field_176295_N, (var1 & 8) > 0);
   }

   public int func_176201_c(IBlockState var1) {
      int var2 = 0;
      if ((Boolean)var1.func_177229_b(field_176293_a)) {
         var2 |= 1;
      }

      if ((Boolean)var1.func_177229_b(field_176290_b)) {
         var2 |= 2;
      }

      if ((Boolean)var1.func_177229_b(field_176294_M)) {
         var2 |= 4;
      }

      if ((Boolean)var1.func_177229_b(field_176295_N)) {
         var2 |= 8;
      }

      return var2;
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176293_a, field_176290_b, field_176294_M, field_176295_N, field_176296_O, field_176291_P, field_176292_R, field_176289_Q});
   }
}
