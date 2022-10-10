package net.minecraft.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockFlowingFluid extends Block implements IBucketPickupHandler {
   public static final IntegerProperty field_176367_b;
   protected final FlowingFluid field_204517_c;
   private final List<IFluidState> field_212565_c;
   private final Map<IBlockState, VoxelShape> field_196481_b = Maps.newIdentityHashMap();

   protected BlockFlowingFluid(FlowingFluid var1, Block.Properties var2) {
      super(var2);
      this.field_204517_c = var1;
      this.field_212565_c = Lists.newArrayList();
      this.field_212565_c.add(var1.func_207204_a(false));

      for(int var3 = 1; var3 < 8; ++var3) {
         this.field_212565_c.add(var1.func_207207_a(8 - var3, false));
      }

      this.field_212565_c.add(var1.func_207207_a(8, true));
      this.func_180632_j((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176367_b, 0));
   }

   public void func_196265_a(IBlockState var1, World var2, BlockPos var3, Random var4) {
      var2.func_204610_c(var3).func_206891_b(var2, var3, var4);
   }

   public boolean func_200123_i(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return false;
   }

   public boolean func_196266_a(IBlockState var1, IBlockReader var2, BlockPos var3, PathType var4) {
      return !this.field_204517_c.func_207185_a(FluidTags.field_206960_b);
   }

   public IFluidState func_204507_t(IBlockState var1) {
      int var2 = (Integer)var1.func_177229_b(field_176367_b);
      return (IFluidState)this.field_212565_c.get(Math.min(var2, 8));
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public boolean func_200293_a(IBlockState var1) {
      return false;
   }

   public boolean func_200122_a(IBlockState var1, IBlockState var2, EnumFacing var3) {
      return var2.func_204520_s().func_206886_c().func_207187_a(this.field_204517_c) ? true : super.func_200124_e(var1);
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      IFluidState var4 = var2.func_204610_c(var3.func_177984_a());
      return var4.func_206886_c().func_207187_a(this.field_204517_c) ? VoxelShapes.func_197868_b() : (VoxelShape)this.field_196481_b.computeIfAbsent(var1, (var0) -> {
         IFluidState var1 = var0.func_204520_s();
         return VoxelShapes.func_197873_a(0.0D, 0.0D, 0.0D, 1.0D, (double)var1.func_206885_f(), 1.0D);
      });
   }

   public EnumBlockRenderType func_149645_b(IBlockState var1) {
      return EnumBlockRenderType.INVISIBLE;
   }

   public IItemProvider func_199769_a(IBlockState var1, World var2, BlockPos var3, int var4) {
      return Items.field_190931_a;
   }

   public int func_149738_a(IWorldReaderBase var1) {
      return this.field_204517_c.func_205569_a(var1);
   }

   public void func_196259_b(IBlockState var1, World var2, BlockPos var3, IBlockState var4) {
      if (this.func_204515_c(var2, var3, var1)) {
         var2.func_205219_F_().func_205360_a(var3, var1.func_204520_s().func_206886_c(), this.func_149738_a(var2));
      }

   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      if (var1.func_204520_s().func_206889_d() || var3.func_204520_s().func_206889_d()) {
         var4.func_205219_F_().func_205360_a(var5, var1.func_204520_s().func_206886_c(), this.func_149738_a(var4));
      }

      return super.func_196271_a(var1, var2, var3, var4, var5, var6);
   }

   public void func_189540_a(IBlockState var1, World var2, BlockPos var3, Block var4, BlockPos var5) {
      if (this.func_204515_c(var2, var3, var1)) {
         var2.func_205219_F_().func_205360_a(var3, var1.func_204520_s().func_206886_c(), this.func_149738_a(var2));
      }

   }

   public boolean func_204515_c(World var1, BlockPos var2, IBlockState var3) {
      if (this.field_204517_c.func_207185_a(FluidTags.field_206960_b)) {
         boolean var4 = false;
         EnumFacing[] var5 = EnumFacing.values();
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            EnumFacing var8 = var5[var7];
            if (var8 != EnumFacing.DOWN && var1.func_204610_c(var2.func_177972_a(var8)).func_206884_a(FluidTags.field_206959_a)) {
               var4 = true;
               break;
            }
         }

         if (var4) {
            IFluidState var9 = var1.func_204610_c(var2);
            if (var9.func_206889_d()) {
               var1.func_175656_a(var2, Blocks.field_150343_Z.func_176223_P());
               this.func_180688_d(var1, var2);
               return false;
            }

            if (var9.func_206885_f() >= 0.44444445F) {
               var1.func_175656_a(var2, Blocks.field_150347_e.func_176223_P());
               this.func_180688_d(var1, var2);
               return false;
            }
         }
      }

      return true;
   }

   protected void func_180688_d(IWorld var1, BlockPos var2) {
      double var3 = (double)var2.func_177958_n();
      double var5 = (double)var2.func_177956_o();
      double var7 = (double)var2.func_177952_p();
      var1.func_184133_a((EntityPlayer)null, var2, SoundEvents.field_187659_cY, SoundCategory.BLOCKS, 0.5F, 2.6F + (var1.func_201674_k().nextFloat() - var1.func_201674_k().nextFloat()) * 0.8F);

      for(int var9 = 0; var9 < 8; ++var9) {
         var1.func_195594_a(Particles.field_197594_E, var3 + Math.random(), var5 + 1.2D, var7 + Math.random(), 0.0D, 0.0D, 0.0D);
      }

   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_176367_b);
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return BlockFaceShape.UNDEFINED;
   }

   public Fluid func_204508_a(IWorld var1, BlockPos var2, IBlockState var3) {
      if ((Integer)var3.func_177229_b(field_176367_b) == 0) {
         var1.func_180501_a(var2, Blocks.field_150350_a.func_176223_P(), 11);
         return this.field_204517_c;
      } else {
         return Fluids.field_204541_a;
      }
   }

   static {
      field_176367_b = BlockStateProperties.field_208132_ag;
   }
}
