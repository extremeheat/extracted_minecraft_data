package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.Half;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockTrapDoor extends BlockHorizontal implements IBucketPickupHandler, ILiquidContainer {
   public static final BooleanProperty field_176283_b;
   public static final EnumProperty<Half> field_176285_M;
   public static final BooleanProperty field_196381_c;
   public static final BooleanProperty field_204614_t;
   protected static final VoxelShape field_185734_d;
   protected static final VoxelShape field_185735_e;
   protected static final VoxelShape field_185736_f;
   protected static final VoxelShape field_185737_g;
   protected static final VoxelShape field_185732_B;
   protected static final VoxelShape field_185733_C;

   protected BlockTrapDoor(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)((IBlockState)((IBlockState)((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_185512_D, EnumFacing.NORTH)).func_206870_a(field_176283_b, false)).func_206870_a(field_176285_M, Half.BOTTOM)).func_206870_a(field_196381_c, false)).func_206870_a(field_204614_t, false));
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      if (!(Boolean)var1.func_177229_b(field_176283_b)) {
         return var1.func_177229_b(field_176285_M) == Half.TOP ? field_185733_C : field_185732_B;
      } else {
         switch((EnumFacing)var1.func_177229_b(field_185512_D)) {
         case NORTH:
         default:
            return field_185737_g;
         case SOUTH:
            return field_185736_f;
         case WEST:
            return field_185735_e;
         case EAST:
            return field_185734_d;
         }
      }
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public boolean func_196266_a(IBlockState var1, IBlockReader var2, BlockPos var3, PathType var4) {
      switch(var4) {
      case LAND:
         return (Boolean)var1.func_177229_b(field_176283_b);
      case WATER:
         return (Boolean)var1.func_177229_b(field_204614_t);
      case AIR:
         return (Boolean)var1.func_177229_b(field_176283_b);
      default:
         return false;
      }
   }

   public boolean func_196250_a(IBlockState var1, World var2, BlockPos var3, EntityPlayer var4, EnumHand var5, EnumFacing var6, float var7, float var8, float var9) {
      if (this.field_149764_J == Material.field_151573_f) {
         return false;
      } else {
         var1 = (IBlockState)var1.func_177231_a(field_176283_b);
         var2.func_180501_a(var3, var1, 2);
         if ((Boolean)var1.func_177229_b(field_204614_t)) {
            var2.func_205219_F_().func_205360_a(var3, Fluids.field_204546_a, Fluids.field_204546_a.func_205569_a(var2));
         }

         this.func_185731_a(var4, var2, var3, (Boolean)var1.func_177229_b(field_176283_b));
         return true;
      }
   }

   protected void func_185731_a(@Nullable EntityPlayer var1, World var2, BlockPos var3, boolean var4) {
      int var5;
      if (var4) {
         var5 = this.field_149764_J == Material.field_151573_f ? 1037 : 1007;
         var2.func_180498_a(var1, var5, var3, 0);
      } else {
         var5 = this.field_149764_J == Material.field_151573_f ? 1036 : 1013;
         var2.func_180498_a(var1, var5, var3, 0);
      }

   }

   public void func_189540_a(IBlockState var1, World var2, BlockPos var3, Block var4, BlockPos var5) {
      if (!var2.field_72995_K) {
         boolean var6 = var2.func_175640_z(var3);
         if (var6 != (Boolean)var1.func_177229_b(field_196381_c)) {
            if ((Boolean)var1.func_177229_b(field_176283_b) != var6) {
               var1 = (IBlockState)var1.func_206870_a(field_176283_b, var6);
               this.func_185731_a((EntityPlayer)null, var2, var3, var6);
            }

            var2.func_180501_a(var3, (IBlockState)var1.func_206870_a(field_196381_c, var6), 2);
            if ((Boolean)var1.func_177229_b(field_204614_t)) {
               var2.func_205219_F_().func_205360_a(var3, Fluids.field_204546_a, Fluids.field_204546_a.func_205569_a(var2));
            }
         }

      }
   }

   public IBlockState func_196258_a(BlockItemUseContext var1) {
      IBlockState var2 = this.func_176223_P();
      IFluidState var3 = var1.func_195991_k().func_204610_c(var1.func_195995_a());
      EnumFacing var4 = var1.func_196000_l();
      if (!var1.func_196012_c() && var4.func_176740_k().func_176722_c()) {
         var2 = (IBlockState)((IBlockState)var2.func_206870_a(field_185512_D, var4)).func_206870_a(field_176285_M, var1.func_195993_n() > 0.5F ? Half.TOP : Half.BOTTOM);
      } else {
         var2 = (IBlockState)((IBlockState)var2.func_206870_a(field_185512_D, var1.func_195992_f().func_176734_d())).func_206870_a(field_176285_M, var4 == EnumFacing.UP ? Half.BOTTOM : Half.TOP);
      }

      if (var1.func_195991_k().func_175640_z(var1.func_195995_a())) {
         var2 = (IBlockState)((IBlockState)var2.func_206870_a(field_176283_b, true)).func_206870_a(field_196381_c, true);
      }

      return (IBlockState)var2.func_206870_a(field_204614_t, var3.func_206886_c() == Fluids.field_204546_a);
   }

   public BlockRenderLayer func_180664_k() {
      return BlockRenderLayer.CUTOUT;
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_185512_D, field_176283_b, field_176285_M, field_196381_c, field_204614_t);
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return (var4 == EnumFacing.UP && var2.func_177229_b(field_176285_M) == Half.TOP || var4 == EnumFacing.DOWN && var2.func_177229_b(field_176285_M) == Half.BOTTOM) && !(Boolean)var2.func_177229_b(field_176283_b) ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
   }

   public Fluid func_204508_a(IWorld var1, BlockPos var2, IBlockState var3) {
      if ((Boolean)var3.func_177229_b(field_204614_t)) {
         var1.func_180501_a(var2, (IBlockState)var3.func_206870_a(field_204614_t, false), 3);
         return Fluids.field_204546_a;
      } else {
         return Fluids.field_204541_a;
      }
   }

   public IFluidState func_204507_t(IBlockState var1) {
      return (Boolean)var1.func_177229_b(field_204614_t) ? Fluids.field_204546_a.func_207204_a(false) : super.func_204507_t(var1);
   }

   public boolean func_204510_a(IBlockReader var1, BlockPos var2, IBlockState var3, Fluid var4) {
      return !(Boolean)var3.func_177229_b(field_204614_t) && var4 == Fluids.field_204546_a;
   }

   public boolean func_204509_a(IWorld var1, BlockPos var2, IBlockState var3, IFluidState var4) {
      if (!(Boolean)var3.func_177229_b(field_204614_t) && var4.func_206886_c() == Fluids.field_204546_a) {
         if (!var1.func_201670_d()) {
            var1.func_180501_a(var2, (IBlockState)var3.func_206870_a(field_204614_t, true), 3);
            var1.func_205219_F_().func_205360_a(var2, var4.func_206886_c(), var4.func_206886_c().func_205569_a(var1));
         }

         return true;
      } else {
         return false;
      }
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      if ((Boolean)var1.func_177229_b(field_204614_t)) {
         var4.func_205219_F_().func_205360_a(var5, Fluids.field_204546_a, Fluids.field_204546_a.func_205569_a(var4));
      }

      return super.func_196271_a(var1, var2, var3, var4, var5, var6);
   }

   static {
      field_176283_b = BlockStateProperties.field_208193_t;
      field_176285_M = BlockStateProperties.field_208164_Q;
      field_196381_c = BlockStateProperties.field_208194_u;
      field_204614_t = BlockStateProperties.field_208198_y;
      field_185734_d = Block.func_208617_a(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);
      field_185735_e = Block.func_208617_a(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
      field_185736_f = Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
      field_185737_g = Block.func_208617_a(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);
      field_185732_B = Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D);
      field_185733_C = Block.func_208617_a(0.0D, 13.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   }
}
