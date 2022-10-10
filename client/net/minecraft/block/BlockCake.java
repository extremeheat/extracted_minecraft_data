package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockCake extends Block {
   public static final IntegerProperty field_176589_a;
   protected static final VoxelShape[] field_196402_b;

   protected BlockCake(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176589_a, 0));
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return field_196402_b[(Integer)var1.func_177229_b(field_176589_a)];
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public boolean func_196250_a(IBlockState var1, World var2, BlockPos var3, EntityPlayer var4, EnumHand var5, EnumFacing var6, float var7, float var8, float var9) {
      if (!var2.field_72995_K) {
         return this.func_180682_b(var2, var3, var1, var4);
      } else {
         ItemStack var10 = var4.func_184586_b(var5);
         return this.func_180682_b(var2, var3, var1, var4) || var10.func_190926_b();
      }
   }

   private boolean func_180682_b(IWorld var1, BlockPos var2, IBlockState var3, EntityPlayer var4) {
      if (!var4.func_71043_e(false)) {
         return false;
      } else {
         var4.func_195066_a(StatList.field_188076_J);
         var4.func_71024_bL().func_75122_a(2, 0.1F);
         int var5 = (Integer)var3.func_177229_b(field_176589_a);
         if (var5 < 6) {
            var1.func_180501_a(var2, (IBlockState)var3.func_206870_a(field_176589_a, var5 + 1), 3);
         } else {
            var1.func_175698_g(var2);
         }

         return true;
      }
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      return var2 == EnumFacing.DOWN && !var1.func_196955_c(var4, var5) ? Blocks.field_150350_a.func_176223_P() : super.func_196271_a(var1, var2, var3, var4, var5, var6);
   }

   public boolean func_196260_a(IBlockState var1, IWorldReaderBase var2, BlockPos var3) {
      return var2.func_180495_p(var3.func_177977_b()).func_185904_a().func_76220_a();
   }

   public IItemProvider func_199769_a(IBlockState var1, World var2, BlockPos var3, int var4) {
      return Items.field_190931_a;
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_176589_a);
   }

   public int func_180641_l(IBlockState var1, World var2, BlockPos var3) {
      return (7 - (Integer)var1.func_177229_b(field_176589_a)) * 2;
   }

   public boolean func_149740_M(IBlockState var1) {
      return true;
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return BlockFaceShape.UNDEFINED;
   }

   public boolean func_196266_a(IBlockState var1, IBlockReader var2, BlockPos var3, PathType var4) {
      return false;
   }

   static {
      field_176589_a = BlockStateProperties.field_208173_Z;
      field_196402_b = new VoxelShape[]{Block.func_208617_a(1.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.func_208617_a(3.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.func_208617_a(5.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.func_208617_a(7.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.func_208617_a(9.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.func_208617_a(11.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.func_208617_a(13.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D)};
   }
}
