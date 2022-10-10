package net.minecraft.block;

import java.util.Iterator;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockChorusFlower extends Block {
   public static final IntegerProperty field_185607_a;
   private final BlockChorusPlant field_196405_b;

   protected BlockChorusFlower(BlockChorusPlant var1, Block.Properties var2) {
      super(var2);
      this.field_196405_b = var1;
      this.func_180632_j((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_185607_a, 0));
   }

   public IItemProvider func_199769_a(IBlockState var1, World var2, BlockPos var3, int var4) {
      return Items.field_190931_a;
   }

   public void func_196267_b(IBlockState var1, World var2, BlockPos var3, Random var4) {
      if (!var1.func_196955_c(var2, var3)) {
         var2.func_175655_b(var3, true);
      } else {
         BlockPos var5 = var3.func_177984_a();
         if (var2.func_175623_d(var5) && var5.func_177956_o() < 256) {
            int var6 = (Integer)var1.func_177229_b(field_185607_a);
            if (var6 < 5) {
               boolean var7 = false;
               boolean var8 = false;
               IBlockState var9 = var2.func_180495_p(var3.func_177977_b());
               Block var10 = var9.func_177230_c();
               int var11;
               if (var10 == Blocks.field_150377_bs) {
                  var7 = true;
               } else if (var10 == this.field_196405_b) {
                  var11 = 1;

                  for(int var12 = 0; var12 < 4; ++var12) {
                     Block var13 = var2.func_180495_p(var3.func_177979_c(var11 + 1)).func_177230_c();
                     if (var13 != this.field_196405_b) {
                        if (var13 == Blocks.field_150377_bs) {
                           var8 = true;
                        }
                        break;
                     }

                     ++var11;
                  }

                  if (var11 < 2 || var11 <= var4.nextInt(var8 ? 5 : 4)) {
                     var7 = true;
                  }
               } else if (var9.func_196958_f()) {
                  var7 = true;
               }

               if (var7 && func_185604_a(var2, var5, (EnumFacing)null) && var2.func_175623_d(var3.func_177981_b(2))) {
                  var2.func_180501_a(var3, this.field_196405_b.func_196497_a(var2, var3), 2);
                  this.func_185602_a(var2, var5, var6);
               } else if (var6 < 4) {
                  var11 = var4.nextInt(4);
                  if (var8) {
                     ++var11;
                  }

                  boolean var16 = false;

                  for(int var17 = 0; var17 < var11; ++var17) {
                     EnumFacing var14 = EnumFacing.Plane.HORIZONTAL.func_179518_a(var4);
                     BlockPos var15 = var3.func_177972_a(var14);
                     if (var2.func_175623_d(var15) && var2.func_175623_d(var15.func_177977_b()) && func_185604_a(var2, var15, var14.func_176734_d())) {
                        this.func_185602_a(var2, var15, var6 + 1);
                        var16 = true;
                     }
                  }

                  if (var16) {
                     var2.func_180501_a(var3, this.field_196405_b.func_196497_a(var2, var3), 2);
                  } else {
                     this.func_185605_c(var2, var3);
                  }
               } else {
                  this.func_185605_c(var2, var3);
               }

            }
         }
      }
   }

   private void func_185602_a(World var1, BlockPos var2, int var3) {
      var1.func_180501_a(var2, (IBlockState)this.func_176223_P().func_206870_a(field_185607_a, var3), 2);
      var1.func_175718_b(1033, var2, 0);
   }

   private void func_185605_c(World var1, BlockPos var2) {
      var1.func_180501_a(var2, (IBlockState)this.func_176223_P().func_206870_a(field_185607_a, 5), 2);
      var1.func_175718_b(1034, var2, 0);
   }

   private static boolean func_185604_a(IWorldReaderBase var0, BlockPos var1, @Nullable EnumFacing var2) {
      Iterator var3 = EnumFacing.Plane.HORIZONTAL.iterator();

      EnumFacing var4;
      do {
         if (!var3.hasNext()) {
            return true;
         }

         var4 = (EnumFacing)var3.next();
      } while(var4 == var2 || var0.func_175623_d(var1.func_177972_a(var4)));

      return false;
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      if (var2 != EnumFacing.UP && !var1.func_196955_c(var4, var5)) {
         var4.func_205220_G_().func_205360_a(var5, this, 1);
      }

      return super.func_196271_a(var1, var2, var3, var4, var5, var6);
   }

   public boolean func_196260_a(IBlockState var1, IWorldReaderBase var2, BlockPos var3) {
      IBlockState var4 = var2.func_180495_p(var3.func_177977_b());
      Block var5 = var4.func_177230_c();
      if (var5 != this.field_196405_b && var5 != Blocks.field_150377_bs) {
         if (!var4.func_196958_f()) {
            return false;
         } else {
            boolean var6 = false;
            Iterator var7 = EnumFacing.Plane.HORIZONTAL.iterator();

            while(var7.hasNext()) {
               EnumFacing var8 = (EnumFacing)var7.next();
               IBlockState var9 = var2.func_180495_p(var3.func_177972_a(var8));
               if (var9.func_177230_c() == this.field_196405_b) {
                  if (var6) {
                     return false;
                  }

                  var6 = true;
               } else if (!var9.func_196958_f()) {
                  return false;
               }
            }

            return var6;
         }
      } else {
         return true;
      }
   }

   public void func_180657_a(World var1, EntityPlayer var2, BlockPos var3, IBlockState var4, @Nullable TileEntity var5, ItemStack var6) {
      super.func_180657_a(var1, var2, var3, var4, var5, var6);
      func_180635_a(var1, var3, new ItemStack(this));
   }

   protected ItemStack func_180643_i(IBlockState var1) {
      return ItemStack.field_190927_a;
   }

   public BlockRenderLayer func_180664_k() {
      return BlockRenderLayer.CUTOUT;
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_185607_a);
   }

   public static void func_185603_a(IWorld var0, BlockPos var1, Random var2, int var3) {
      var0.func_180501_a(var1, ((BlockChorusPlant)Blocks.field_185765_cR).func_196497_a(var0, var1), 2);
      func_185601_a(var0, var1, var2, var1, var3, 0);
   }

   private static void func_185601_a(IWorld var0, BlockPos var1, Random var2, BlockPos var3, int var4, int var5) {
      BlockChorusPlant var6 = (BlockChorusPlant)Blocks.field_185765_cR;
      int var7 = var2.nextInt(4) + 1;
      if (var5 == 0) {
         ++var7;
      }

      for(int var8 = 0; var8 < var7; ++var8) {
         BlockPos var9 = var1.func_177981_b(var8 + 1);
         if (!func_185604_a(var0, var9, (EnumFacing)null)) {
            return;
         }

         var0.func_180501_a(var9, var6.func_196497_a(var0, var9), 2);
         var0.func_180501_a(var9.func_177977_b(), var6.func_196497_a(var0, var9.func_177977_b()), 2);
      }

      boolean var13 = false;
      if (var5 < 4) {
         int var14 = var2.nextInt(4);
         if (var5 == 0) {
            ++var14;
         }

         for(int var10 = 0; var10 < var14; ++var10) {
            EnumFacing var11 = EnumFacing.Plane.HORIZONTAL.func_179518_a(var2);
            BlockPos var12 = var1.func_177981_b(var7).func_177972_a(var11);
            if (Math.abs(var12.func_177958_n() - var3.func_177958_n()) < var4 && Math.abs(var12.func_177952_p() - var3.func_177952_p()) < var4 && var0.func_175623_d(var12) && var0.func_175623_d(var12.func_177977_b()) && func_185604_a(var0, var12, var11.func_176734_d())) {
               var13 = true;
               var0.func_180501_a(var12, var6.func_196497_a(var0, var12), 2);
               var0.func_180501_a(var12.func_177972_a(var11.func_176734_d()), var6.func_196497_a(var0, var12.func_177972_a(var11.func_176734_d())), 2);
               func_185601_a(var0, var12, var2, var3, var4, var5 + 1);
            }
         }
      }

      if (!var13) {
         var0.func_180501_a(var1.func_177981_b(var7), (IBlockState)Blocks.field_185766_cS.func_176223_P().func_206870_a(field_185607_a, 5), 2);
      }

   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return BlockFaceShape.UNDEFINED;
   }

   static {
      field_185607_a = BlockStateProperties.field_208169_V;
   }
}
