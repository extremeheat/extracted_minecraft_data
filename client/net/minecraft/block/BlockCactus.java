package net.minecraft.block;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockCactus extends Block {
   public static final IntegerProperty field_176587_a;
   protected static final VoxelShape field_196400_b;
   protected static final VoxelShape field_196401_c;

   protected BlockCactus(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176587_a, 0));
   }

   public void func_196267_b(IBlockState var1, World var2, BlockPos var3, Random var4) {
      if (!var1.func_196955_c(var2, var3)) {
         var2.func_175655_b(var3, true);
      } else {
         BlockPos var5 = var3.func_177984_a();
         if (var2.func_175623_d(var5)) {
            int var6;
            for(var6 = 1; var2.func_180495_p(var3.func_177979_c(var6)).func_177230_c() == this; ++var6) {
            }

            if (var6 < 3) {
               int var7 = (Integer)var1.func_177229_b(field_176587_a);
               if (var7 == 15) {
                  var2.func_175656_a(var5, this.func_176223_P());
                  IBlockState var8 = (IBlockState)var1.func_206870_a(field_176587_a, 0);
                  var2.func_180501_a(var3, var8, 4);
                  var8.func_189546_a(var2, var5, this, var3);
               } else {
                  var2.func_180501_a(var3, (IBlockState)var1.func_206870_a(field_176587_a, var7 + 1), 4);
               }

            }
         }
      }
   }

   public VoxelShape func_196268_f(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return field_196400_b;
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return field_196401_c;
   }

   public boolean func_200124_e(IBlockState var1) {
      return true;
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      if (!var1.func_196955_c(var4, var5)) {
         var4.func_205220_G_().func_205360_a(var5, this, 1);
      }

      return super.func_196271_a(var1, var2, var3, var4, var5, var6);
   }

   public boolean func_196260_a(IBlockState var1, IWorldReaderBase var2, BlockPos var3) {
      Iterator var4 = EnumFacing.Plane.HORIZONTAL.iterator();

      EnumFacing var5;
      Material var7;
      do {
         if (!var4.hasNext()) {
            Block var8 = var2.func_180495_p(var3.func_177977_b()).func_177230_c();
            return (var8 == Blocks.field_150434_aF || var8 == Blocks.field_150354_m || var8 == Blocks.field_196611_F) && !var2.func_180495_p(var3.func_177984_a()).func_185904_a().func_76224_d();
         }

         var5 = (EnumFacing)var4.next();
         IBlockState var6 = var2.func_180495_p(var3.func_177972_a(var5));
         var7 = var6.func_185904_a();
      } while(!var7.func_76220_a() && !var2.func_204610_c(var3.func_177972_a(var5)).func_206884_a(FluidTags.field_206960_b));

      return false;
   }

   public void func_196262_a(IBlockState var1, World var2, BlockPos var3, Entity var4) {
      var4.func_70097_a(DamageSource.field_76367_g, 1.0F);
   }

   public BlockRenderLayer func_180664_k() {
      return BlockRenderLayer.CUTOUT;
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_176587_a);
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return BlockFaceShape.UNDEFINED;
   }

   public boolean func_196266_a(IBlockState var1, IBlockReader var2, BlockPos var3, PathType var4) {
      return false;
   }

   static {
      field_176587_a = BlockStateProperties.field_208171_X;
      field_196400_b = Block.func_208617_a(1.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D);
      field_196401_c = Block.func_208617_a(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);
   }
}
