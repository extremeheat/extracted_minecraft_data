package net.minecraft.block;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockFarmland extends Block {
   public static final IntegerProperty field_176531_a;
   protected static final VoxelShape field_196432_b;

   protected BlockFarmland(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176531_a, 0));
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      if (var2 == EnumFacing.UP && !var1.func_196955_c(var4, var5)) {
         var4.func_205220_G_().func_205360_a(var5, this, 1);
      }

      return super.func_196271_a(var1, var2, var3, var4, var5, var6);
   }

   public boolean func_196260_a(IBlockState var1, IWorldReaderBase var2, BlockPos var3) {
      IBlockState var4 = var2.func_180495_p(var3.func_177984_a());
      return !var4.func_185904_a().func_76220_a() || var4.func_177230_c() instanceof BlockFenceGate;
   }

   public IBlockState func_196258_a(BlockItemUseContext var1) {
      return !this.func_176223_P().func_196955_c(var1.func_195991_k(), var1.func_195995_a()) ? Blocks.field_150346_d.func_176223_P() : super.func_196258_a(var1);
   }

   public int func_200011_d(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return var2.func_201572_C();
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return field_196432_b;
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public void func_196267_b(IBlockState var1, World var2, BlockPos var3, Random var4) {
      if (!var1.func_196955_c(var2, var3)) {
         func_199610_d(var1, var2, var3);
      } else {
         int var5 = (Integer)var1.func_177229_b(field_176531_a);
         if (!func_176530_e(var2, var3) && !var2.func_175727_C(var3.func_177984_a())) {
            if (var5 > 0) {
               var2.func_180501_a(var3, (IBlockState)var1.func_206870_a(field_176531_a, var5 - 1), 2);
            } else if (!func_176529_d(var2, var3)) {
               func_199610_d(var1, var2, var3);
            }
         } else if (var5 < 7) {
            var2.func_180501_a(var3, (IBlockState)var1.func_206870_a(field_176531_a, 7), 2);
         }

      }
   }

   public void func_180658_a(World var1, BlockPos var2, Entity var3, float var4) {
      if (!var1.field_72995_K && var1.field_73012_v.nextFloat() < var4 - 0.5F && var3 instanceof EntityLivingBase && (var3 instanceof EntityPlayer || var1.func_82736_K().func_82766_b("mobGriefing")) && var3.field_70130_N * var3.field_70130_N * var3.field_70131_O > 0.512F) {
         func_199610_d(var1.func_180495_p(var2), var1, var2);
      }

      super.func_180658_a(var1, var2, var3, var4);
   }

   public static void func_199610_d(IBlockState var0, World var1, BlockPos var2) {
      var1.func_175656_a(var2, func_199601_a(var0, Blocks.field_150346_d.func_176223_P(), var1, var2));
   }

   private static boolean func_176529_d(IBlockReader var0, BlockPos var1) {
      Block var2 = var0.func_180495_p(var1.func_177984_a()).func_177230_c();
      return var2 instanceof BlockCrops || var2 instanceof BlockStem || var2 instanceof BlockAttachedStem;
   }

   private static boolean func_176530_e(IWorldReaderBase var0, BlockPos var1) {
      Iterator var2 = BlockPos.func_177975_b(var1.func_177982_a(-4, 0, -4), var1.func_177982_a(4, 1, 4)).iterator();

      BlockPos.MutableBlockPos var3;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         var3 = (BlockPos.MutableBlockPos)var2.next();
      } while(!var0.func_204610_c(var3).func_206884_a(FluidTags.field_206959_a));

      return true;
   }

   public IItemProvider func_199769_a(IBlockState var1, World var2, BlockPos var3, int var4) {
      return Blocks.field_150346_d;
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_176531_a);
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return var4 == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
   }

   public boolean func_196266_a(IBlockState var1, IBlockReader var2, BlockPos var3, PathType var4) {
      return false;
   }

   static {
      field_176531_a = BlockStateProperties.field_208133_ah;
      field_196432_b = Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 15.0D, 16.0D);
   }
}
