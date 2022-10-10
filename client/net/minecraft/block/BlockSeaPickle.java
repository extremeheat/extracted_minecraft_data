package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockSeaPickle extends BlockBush implements IGrowable, IBucketPickupHandler, ILiquidContainer {
   public static final IntegerProperty field_204902_a;
   public static final BooleanProperty field_204903_b;
   protected static final VoxelShape field_204904_c;
   protected static final VoxelShape field_204905_t;
   protected static final VoxelShape field_204906_u;
   protected static final VoxelShape field_204907_v;

   protected BlockSeaPickle(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_204902_a, 1)).func_206870_a(field_204903_b, true));
   }

   public int func_149750_m(IBlockState var1) {
      return this.func_204901_j(var1) ? 0 : super.func_149750_m(var1) + 3 * (Integer)var1.func_177229_b(field_204902_a);
   }

   @Nullable
   public IBlockState func_196258_a(BlockItemUseContext var1) {
      IBlockState var2 = var1.func_195991_k().func_180495_p(var1.func_195995_a());
      if (var2.func_177230_c() == this) {
         return (IBlockState)var2.func_206870_a(field_204902_a, Math.min(4, (Integer)var2.func_177229_b(field_204902_a) + 1));
      } else {
         IFluidState var3 = var1.func_195991_k().func_204610_c(var1.func_195995_a());
         boolean var4 = var3.func_206884_a(FluidTags.field_206959_a) && var3.func_206882_g() == 8;
         return (IBlockState)super.func_196258_a(var1).func_206870_a(field_204903_b, var4);
      }
   }

   private boolean func_204901_j(IBlockState var1) {
      return !(Boolean)var1.func_177229_b(field_204903_b);
   }

   protected boolean func_200014_a_(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return !var1.func_196952_d(var2, var3).func_212434_a(EnumFacing.UP).func_197766_b();
   }

   public boolean func_196260_a(IBlockState var1, IWorldReaderBase var2, BlockPos var3) {
      BlockPos var4 = var3.func_177977_b();
      return this.func_200014_a_(var2.func_180495_p(var4), var2, var4);
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      if (!var1.func_196955_c(var4, var5)) {
         return Blocks.field_150350_a.func_176223_P();
      } else {
         if ((Boolean)var1.func_177229_b(field_204903_b)) {
            var4.func_205219_F_().func_205360_a(var5, Fluids.field_204546_a, Fluids.field_204546_a.func_205569_a(var4));
         }

         return super.func_196271_a(var1, var2, var3, var4, var5, var6);
      }
   }

   public boolean func_196253_a(IBlockState var1, BlockItemUseContext var2) {
      return var2.func_195996_i().func_77973_b() == this.func_199767_j() && (Integer)var1.func_177229_b(field_204902_a) < 4 ? true : super.func_196253_a(var1, var2);
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      switch((Integer)var1.func_177229_b(field_204902_a)) {
      case 1:
      default:
         return field_204904_c;
      case 2:
         return field_204905_t;
      case 3:
         return field_204906_u;
      case 4:
         return field_204907_v;
      }
   }

   public Fluid func_204508_a(IWorld var1, BlockPos var2, IBlockState var3) {
      if ((Boolean)var3.func_177229_b(field_204903_b)) {
         var1.func_180501_a(var2, (IBlockState)var3.func_206870_a(field_204903_b, false), 3);
         return Fluids.field_204546_a;
      } else {
         return Fluids.field_204541_a;
      }
   }

   public IFluidState func_204507_t(IBlockState var1) {
      return (Boolean)var1.func_177229_b(field_204903_b) ? Fluids.field_204546_a.func_207204_a(false) : super.func_204507_t(var1);
   }

   public boolean func_204510_a(IBlockReader var1, BlockPos var2, IBlockState var3, Fluid var4) {
      return !(Boolean)var3.func_177229_b(field_204903_b) && var4 == Fluids.field_204546_a;
   }

   public boolean func_204509_a(IWorld var1, BlockPos var2, IBlockState var3, IFluidState var4) {
      if (!(Boolean)var3.func_177229_b(field_204903_b) && var4.func_206886_c() == Fluids.field_204546_a) {
         if (!var1.func_201670_d()) {
            var1.func_180501_a(var2, (IBlockState)var3.func_206870_a(field_204903_b, true), 3);
            var1.func_205219_F_().func_205360_a(var2, var4.func_206886_c(), var4.func_206886_c().func_205569_a(var1));
         }

         return true;
      } else {
         return false;
      }
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_204902_a, field_204903_b);
   }

   public int func_196264_a(IBlockState var1, Random var2) {
      return (Integer)var1.func_177229_b(field_204902_a);
   }

   public boolean func_176473_a(IBlockReader var1, BlockPos var2, IBlockState var3, boolean var4) {
      return true;
   }

   public boolean func_180670_a(World var1, Random var2, BlockPos var3, IBlockState var4) {
      return true;
   }

   public void func_176474_b(World var1, Random var2, BlockPos var3, IBlockState var4) {
      if (!this.func_204901_j(var4) && var1.func_180495_p(var3.func_177977_b()).func_203425_a(BlockTags.field_205598_B)) {
         boolean var5 = true;
         int var6 = 1;
         boolean var7 = true;
         int var8 = 0;
         int var9 = var3.func_177958_n() - 2;
         int var10 = 0;

         for(int var11 = 0; var11 < 5; ++var11) {
            for(int var12 = 0; var12 < var6; ++var12) {
               int var13 = 2 + var3.func_177956_o() - 1;

               for(int var14 = var13 - 2; var14 < var13; ++var14) {
                  BlockPos var15 = new BlockPos(var9 + var11, var14, var3.func_177952_p() - var10 + var12);
                  if (var15 != var3 && var2.nextInt(6) == 0 && var1.func_180495_p(var15).func_177230_c() == Blocks.field_150355_j) {
                     IBlockState var16 = var1.func_180495_p(var15.func_177977_b());
                     if (var16.func_203425_a(BlockTags.field_205598_B)) {
                        var1.func_180501_a(var15, (IBlockState)Blocks.field_204913_jW.func_176223_P().func_206870_a(field_204902_a, var2.nextInt(4) + 1), 3);
                     }
                  }
               }
            }

            if (var8 < 2) {
               var6 += 2;
               ++var10;
            } else {
               var6 -= 2;
               --var10;
            }

            ++var8;
         }

         var1.func_180501_a(var3, (IBlockState)var4.func_206870_a(field_204902_a, 4), 2);
      }

   }

   static {
      field_204902_a = BlockStateProperties.field_208135_aj;
      field_204903_b = BlockStateProperties.field_208198_y;
      field_204904_c = Block.func_208617_a(6.0D, 0.0D, 6.0D, 10.0D, 6.0D, 10.0D);
      field_204905_t = Block.func_208617_a(3.0D, 0.0D, 3.0D, 13.0D, 6.0D, 13.0D);
      field_204906_u = Block.func_208617_a(2.0D, 0.0D, 2.0D, 14.0D, 6.0D, 14.0D);
      field_204907_v = Block.func_208617_a(2.0D, 0.0D, 2.0D, 14.0D, 7.0D, 14.0D);
   }
}
