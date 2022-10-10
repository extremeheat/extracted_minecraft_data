package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockRail extends BlockRailBase {
   public static final EnumProperty<RailShape> field_176565_b;

   protected BlockRail(Block.Properties var1) {
      super(false, var1);
      this.func_180632_j((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176565_b, RailShape.NORTH_SOUTH));
   }

   protected void func_189541_b(IBlockState var1, World var2, BlockPos var3, Block var4) {
      if (var4.func_176223_P().func_185897_m() && (new BlockRailState(var2, var3, var1)).func_196910_b() == 3) {
         this.func_208489_a(var2, var3, var1, false);
      }

   }

   public IProperty<RailShape> func_176560_l() {
      return field_176565_b;
   }

   public IBlockState func_185499_a(IBlockState var1, Rotation var2) {
      switch(var2) {
      case CLOCKWISE_180:
         switch((RailShape)var1.func_177229_b(field_176565_b)) {
         case ASCENDING_EAST:
            return (IBlockState)var1.func_206870_a(field_176565_b, RailShape.ASCENDING_WEST);
         case ASCENDING_WEST:
            return (IBlockState)var1.func_206870_a(field_176565_b, RailShape.ASCENDING_EAST);
         case ASCENDING_NORTH:
            return (IBlockState)var1.func_206870_a(field_176565_b, RailShape.ASCENDING_SOUTH);
         case ASCENDING_SOUTH:
            return (IBlockState)var1.func_206870_a(field_176565_b, RailShape.ASCENDING_NORTH);
         case SOUTH_EAST:
            return (IBlockState)var1.func_206870_a(field_176565_b, RailShape.NORTH_WEST);
         case SOUTH_WEST:
            return (IBlockState)var1.func_206870_a(field_176565_b, RailShape.NORTH_EAST);
         case NORTH_WEST:
            return (IBlockState)var1.func_206870_a(field_176565_b, RailShape.SOUTH_EAST);
         case NORTH_EAST:
            return (IBlockState)var1.func_206870_a(field_176565_b, RailShape.SOUTH_WEST);
         }
      case COUNTERCLOCKWISE_90:
         switch((RailShape)var1.func_177229_b(field_176565_b)) {
         case ASCENDING_EAST:
            return (IBlockState)var1.func_206870_a(field_176565_b, RailShape.ASCENDING_NORTH);
         case ASCENDING_WEST:
            return (IBlockState)var1.func_206870_a(field_176565_b, RailShape.ASCENDING_SOUTH);
         case ASCENDING_NORTH:
            return (IBlockState)var1.func_206870_a(field_176565_b, RailShape.ASCENDING_WEST);
         case ASCENDING_SOUTH:
            return (IBlockState)var1.func_206870_a(field_176565_b, RailShape.ASCENDING_EAST);
         case SOUTH_EAST:
            return (IBlockState)var1.func_206870_a(field_176565_b, RailShape.NORTH_EAST);
         case SOUTH_WEST:
            return (IBlockState)var1.func_206870_a(field_176565_b, RailShape.SOUTH_EAST);
         case NORTH_WEST:
            return (IBlockState)var1.func_206870_a(field_176565_b, RailShape.SOUTH_WEST);
         case NORTH_EAST:
            return (IBlockState)var1.func_206870_a(field_176565_b, RailShape.NORTH_WEST);
         case NORTH_SOUTH:
            return (IBlockState)var1.func_206870_a(field_176565_b, RailShape.EAST_WEST);
         case EAST_WEST:
            return (IBlockState)var1.func_206870_a(field_176565_b, RailShape.NORTH_SOUTH);
         }
      case CLOCKWISE_90:
         switch((RailShape)var1.func_177229_b(field_176565_b)) {
         case ASCENDING_EAST:
            return (IBlockState)var1.func_206870_a(field_176565_b, RailShape.ASCENDING_SOUTH);
         case ASCENDING_WEST:
            return (IBlockState)var1.func_206870_a(field_176565_b, RailShape.ASCENDING_NORTH);
         case ASCENDING_NORTH:
            return (IBlockState)var1.func_206870_a(field_176565_b, RailShape.ASCENDING_EAST);
         case ASCENDING_SOUTH:
            return (IBlockState)var1.func_206870_a(field_176565_b, RailShape.ASCENDING_WEST);
         case SOUTH_EAST:
            return (IBlockState)var1.func_206870_a(field_176565_b, RailShape.SOUTH_WEST);
         case SOUTH_WEST:
            return (IBlockState)var1.func_206870_a(field_176565_b, RailShape.NORTH_WEST);
         case NORTH_WEST:
            return (IBlockState)var1.func_206870_a(field_176565_b, RailShape.NORTH_EAST);
         case NORTH_EAST:
            return (IBlockState)var1.func_206870_a(field_176565_b, RailShape.SOUTH_EAST);
         case NORTH_SOUTH:
            return (IBlockState)var1.func_206870_a(field_176565_b, RailShape.EAST_WEST);
         case EAST_WEST:
            return (IBlockState)var1.func_206870_a(field_176565_b, RailShape.NORTH_SOUTH);
         }
      default:
         return var1;
      }
   }

   public IBlockState func_185471_a(IBlockState var1, Mirror var2) {
      RailShape var3 = (RailShape)var1.func_177229_b(field_176565_b);
      switch(var2) {
      case LEFT_RIGHT:
         switch(var3) {
         case ASCENDING_NORTH:
            return (IBlockState)var1.func_206870_a(field_176565_b, RailShape.ASCENDING_SOUTH);
         case ASCENDING_SOUTH:
            return (IBlockState)var1.func_206870_a(field_176565_b, RailShape.ASCENDING_NORTH);
         case SOUTH_EAST:
            return (IBlockState)var1.func_206870_a(field_176565_b, RailShape.NORTH_EAST);
         case SOUTH_WEST:
            return (IBlockState)var1.func_206870_a(field_176565_b, RailShape.NORTH_WEST);
         case NORTH_WEST:
            return (IBlockState)var1.func_206870_a(field_176565_b, RailShape.SOUTH_WEST);
         case NORTH_EAST:
            return (IBlockState)var1.func_206870_a(field_176565_b, RailShape.SOUTH_EAST);
         default:
            return super.func_185471_a(var1, var2);
         }
      case FRONT_BACK:
         switch(var3) {
         case ASCENDING_EAST:
            return (IBlockState)var1.func_206870_a(field_176565_b, RailShape.ASCENDING_WEST);
         case ASCENDING_WEST:
            return (IBlockState)var1.func_206870_a(field_176565_b, RailShape.ASCENDING_EAST);
         case ASCENDING_NORTH:
         case ASCENDING_SOUTH:
         default:
            break;
         case SOUTH_EAST:
            return (IBlockState)var1.func_206870_a(field_176565_b, RailShape.SOUTH_WEST);
         case SOUTH_WEST:
            return (IBlockState)var1.func_206870_a(field_176565_b, RailShape.SOUTH_EAST);
         case NORTH_WEST:
            return (IBlockState)var1.func_206870_a(field_176565_b, RailShape.NORTH_EAST);
         case NORTH_EAST:
            return (IBlockState)var1.func_206870_a(field_176565_b, RailShape.NORTH_WEST);
         }
      }

      return super.func_185471_a(var1, var2);
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_176565_b);
   }

   static {
      field_176565_b = BlockStateProperties.field_208165_R;
   }
}
