package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public abstract class BlockBasePressurePlate extends Block {
   protected static final VoxelShape field_185509_a = Block.func_208617_a(1.0D, 0.0D, 1.0D, 15.0D, 0.5D, 15.0D);
   protected static final VoxelShape field_185510_b = Block.func_208617_a(1.0D, 0.0D, 1.0D, 15.0D, 1.0D, 15.0D);
   protected static final AxisAlignedBB field_185511_c = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.25D, 0.875D);

   protected BlockBasePressurePlate(Block.Properties var1) {
      super(var1);
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return this.func_176576_e(var1) > 0 ? field_185509_a : field_185510_b;
   }

   public int func_149738_a(IWorldReaderBase var1) {
      return 20;
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public boolean func_181623_g() {
      return true;
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      return var2 == EnumFacing.DOWN && !var1.func_196955_c(var4, var5) ? Blocks.field_150350_a.func_176223_P() : super.func_196271_a(var1, var2, var3, var4, var5, var6);
   }

   public boolean func_196260_a(IBlockState var1, IWorldReaderBase var2, BlockPos var3) {
      IBlockState var4 = var2.func_180495_p(var3.func_177977_b());
      return var4.func_185896_q() || var4.func_177230_c() instanceof BlockFence;
   }

   public void func_196267_b(IBlockState var1, World var2, BlockPos var3, Random var4) {
      if (!var2.field_72995_K) {
         int var5 = this.func_176576_e(var1);
         if (var5 > 0) {
            this.func_180666_a(var2, var3, var1, var5);
         }

      }
   }

   public void func_196262_a(IBlockState var1, World var2, BlockPos var3, Entity var4) {
      if (!var2.field_72995_K) {
         int var5 = this.func_176576_e(var1);
         if (var5 == 0) {
            this.func_180666_a(var2, var3, var1, var5);
         }

      }
   }

   protected void func_180666_a(World var1, BlockPos var2, IBlockState var3, int var4) {
      int var5 = this.func_180669_e(var1, var2);
      boolean var6 = var4 > 0;
      boolean var7 = var5 > 0;
      if (var4 != var5) {
         var3 = this.func_176575_a(var3, var5);
         var1.func_180501_a(var2, var3, 2);
         this.func_176578_d(var1, var2);
         var1.func_175704_b(var2, var2);
      }

      if (!var7 && var6) {
         this.func_185508_c(var1, var2);
      } else if (var7 && !var6) {
         this.func_185507_b(var1, var2);
      }

      if (var7) {
         var1.func_205220_G_().func_205360_a(new BlockPos(var2), this, this.func_149738_a(var1));
      }

   }

   protected abstract void func_185507_b(IWorld var1, BlockPos var2);

   protected abstract void func_185508_c(IWorld var1, BlockPos var2);

   public void func_196243_a(IBlockState var1, World var2, BlockPos var3, IBlockState var4, boolean var5) {
      if (!var5 && var1.func_177230_c() != var4.func_177230_c()) {
         if (this.func_176576_e(var1) > 0) {
            this.func_176578_d(var2, var3);
         }

         super.func_196243_a(var1, var2, var3, var4, var5);
      }
   }

   protected void func_176578_d(World var1, BlockPos var2) {
      var1.func_195593_d(var2, this);
      var1.func_195593_d(var2.func_177977_b(), this);
   }

   public int func_180656_a(IBlockState var1, IBlockReader var2, BlockPos var3, EnumFacing var4) {
      return this.func_176576_e(var1);
   }

   public int func_176211_b(IBlockState var1, IBlockReader var2, BlockPos var3, EnumFacing var4) {
      return var4 == EnumFacing.UP ? this.func_176576_e(var1) : 0;
   }

   public boolean func_149744_f(IBlockState var1) {
      return true;
   }

   public EnumPushReaction func_149656_h(IBlockState var1) {
      return EnumPushReaction.DESTROY;
   }

   protected abstract int func_180669_e(World var1, BlockPos var2);

   protected abstract int func_176576_e(IBlockState var1);

   protected abstract IBlockState func_176575_a(IBlockState var1, int var2);

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return BlockFaceShape.UNDEFINED;
   }
}
