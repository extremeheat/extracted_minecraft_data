package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.SlabType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public class BlockSlab extends Block implements IBucketPickupHandler, ILiquidContainer {
   public static final EnumProperty<SlabType> field_196505_a;
   public static final BooleanProperty field_204512_b;
   protected static final VoxelShape field_196506_b;
   protected static final VoxelShape field_196507_c;

   public BlockSlab(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)this.func_176223_P().func_206870_a(field_196505_a, SlabType.BOTTOM)).func_206870_a(field_204512_b, false));
   }

   public int func_200011_d(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return var2.func_201572_C();
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_196505_a, field_204512_b);
   }

   protected boolean func_149700_E() {
      return false;
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      SlabType var4 = (SlabType)var1.func_177229_b(field_196505_a);
      switch(var4) {
      case DOUBLE:
         return VoxelShapes.func_197868_b();
      case TOP:
         return field_196507_c;
      default:
         return field_196506_b;
      }
   }

   public boolean func_185481_k(IBlockState var1) {
      return var1.func_177229_b(field_196505_a) == SlabType.DOUBLE || var1.func_177229_b(field_196505_a) == SlabType.TOP;
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      SlabType var5 = (SlabType)var2.func_177229_b(field_196505_a);
      if (var5 == SlabType.DOUBLE) {
         return BlockFaceShape.SOLID;
      } else if (var4 == EnumFacing.UP && var5 == SlabType.TOP) {
         return BlockFaceShape.SOLID;
      } else {
         return var4 == EnumFacing.DOWN && var5 == SlabType.BOTTOM ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
      }
   }

   @Nullable
   public IBlockState func_196258_a(BlockItemUseContext var1) {
      IBlockState var2 = var1.func_195991_k().func_180495_p(var1.func_195995_a());
      if (var2.func_177230_c() == this) {
         return (IBlockState)((IBlockState)var2.func_206870_a(field_196505_a, SlabType.DOUBLE)).func_206870_a(field_204512_b, false);
      } else {
         IFluidState var3 = var1.func_195991_k().func_204610_c(var1.func_195995_a());
         IBlockState var4 = (IBlockState)((IBlockState)this.func_176223_P().func_206870_a(field_196505_a, SlabType.BOTTOM)).func_206870_a(field_204512_b, var3.func_206886_c() == Fluids.field_204546_a);
         EnumFacing var5 = var1.func_196000_l();
         return var5 != EnumFacing.DOWN && (var5 == EnumFacing.UP || (double)var1.func_195993_n() <= 0.5D) ? var4 : (IBlockState)var4.func_206870_a(field_196505_a, SlabType.TOP);
      }
   }

   public int func_196264_a(IBlockState var1, Random var2) {
      return var1.func_177229_b(field_196505_a) == SlabType.DOUBLE ? 2 : 1;
   }

   public boolean func_149686_d(IBlockState var1) {
      return var1.func_177229_b(field_196505_a) == SlabType.DOUBLE;
   }

   public boolean func_196253_a(IBlockState var1, BlockItemUseContext var2) {
      ItemStack var3 = var2.func_195996_i();
      SlabType var4 = (SlabType)var1.func_177229_b(field_196505_a);
      if (var4 != SlabType.DOUBLE && var3.func_77973_b() == this.func_199767_j()) {
         if (var2.func_196012_c()) {
            boolean var5 = (double)var2.func_195993_n() > 0.5D;
            EnumFacing var6 = var2.func_196000_l();
            if (var4 == SlabType.BOTTOM) {
               return var6 == EnumFacing.UP || var5 && var6.func_176740_k().func_176722_c();
            } else {
               return var6 == EnumFacing.DOWN || !var5 && var6.func_176740_k().func_176722_c();
            }
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   public Fluid func_204508_a(IWorld var1, BlockPos var2, IBlockState var3) {
      if ((Boolean)var3.func_177229_b(field_204512_b)) {
         var1.func_180501_a(var2, (IBlockState)var3.func_206870_a(field_204512_b, false), 3);
         return Fluids.field_204546_a;
      } else {
         return Fluids.field_204541_a;
      }
   }

   public IFluidState func_204507_t(IBlockState var1) {
      return (Boolean)var1.func_177229_b(field_204512_b) ? Fluids.field_204546_a.func_207204_a(false) : super.func_204507_t(var1);
   }

   public boolean func_204510_a(IBlockReader var1, BlockPos var2, IBlockState var3, Fluid var4) {
      return var3.func_177229_b(field_196505_a) != SlabType.DOUBLE && !(Boolean)var3.func_177229_b(field_204512_b) && var4 == Fluids.field_204546_a;
   }

   public boolean func_204509_a(IWorld var1, BlockPos var2, IBlockState var3, IFluidState var4) {
      if (var3.func_177229_b(field_196505_a) != SlabType.DOUBLE && !(Boolean)var3.func_177229_b(field_204512_b) && var4.func_206886_c() == Fluids.field_204546_a) {
         if (!var1.func_201670_d()) {
            var1.func_180501_a(var2, (IBlockState)var3.func_206870_a(field_204512_b, true), 3);
            var1.func_205219_F_().func_205360_a(var2, var4.func_206886_c(), var4.func_206886_c().func_205569_a(var1));
         }

         return true;
      } else {
         return false;
      }
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      if ((Boolean)var1.func_177229_b(field_204512_b)) {
         var4.func_205219_F_().func_205360_a(var5, Fluids.field_204546_a, Fluids.field_204546_a.func_205569_a(var4));
      }

      return super.func_196271_a(var1, var2, var3, var4, var5, var6);
   }

   public boolean func_196266_a(IBlockState var1, IBlockReader var2, BlockPos var3, PathType var4) {
      switch(var4) {
      case LAND:
         return var1.func_177229_b(field_196505_a) == SlabType.BOTTOM;
      case WATER:
         return var2.func_204610_c(var3).func_206884_a(FluidTags.field_206959_a);
      case AIR:
         return false;
      default:
         return false;
      }
   }

   static {
      field_196505_a = BlockStateProperties.field_208145_at;
      field_204512_b = BlockStateProperties.field_208198_y;
      field_196506_b = Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
      field_196507_c = Block.func_208617_a(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   }
}
