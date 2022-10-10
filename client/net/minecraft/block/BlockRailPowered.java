package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockRailPowered extends BlockRailBase {
   public static final EnumProperty<RailShape> field_176568_b;
   public static final BooleanProperty field_176569_M;

   protected BlockRailPowered(Block.Properties var1) {
      super(true, var1);
      this.func_180632_j((IBlockState)((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176568_b, RailShape.NORTH_SOUTH)).func_206870_a(field_176569_M, false));
   }

   protected boolean func_176566_a(World var1, BlockPos var2, IBlockState var3, boolean var4, int var5) {
      if (var5 >= 8) {
         return false;
      } else {
         int var6 = var2.func_177958_n();
         int var7 = var2.func_177956_o();
         int var8 = var2.func_177952_p();
         boolean var9 = true;
         RailShape var10 = (RailShape)var3.func_177229_b(field_176568_b);
         switch(var10) {
         case NORTH_SOUTH:
            if (var4) {
               ++var8;
            } else {
               --var8;
            }
            break;
         case EAST_WEST:
            if (var4) {
               --var6;
            } else {
               ++var6;
            }
            break;
         case ASCENDING_EAST:
            if (var4) {
               --var6;
            } else {
               ++var6;
               ++var7;
               var9 = false;
            }

            var10 = RailShape.EAST_WEST;
            break;
         case ASCENDING_WEST:
            if (var4) {
               --var6;
               ++var7;
               var9 = false;
            } else {
               ++var6;
            }

            var10 = RailShape.EAST_WEST;
            break;
         case ASCENDING_NORTH:
            if (var4) {
               ++var8;
            } else {
               --var8;
               ++var7;
               var9 = false;
            }

            var10 = RailShape.NORTH_SOUTH;
            break;
         case ASCENDING_SOUTH:
            if (var4) {
               ++var8;
               ++var7;
               var9 = false;
            } else {
               --var8;
            }

            var10 = RailShape.NORTH_SOUTH;
         }

         if (this.func_208071_a(var1, new BlockPos(var6, var7, var8), var4, var5, var10)) {
            return true;
         } else {
            return var9 && this.func_208071_a(var1, new BlockPos(var6, var7 - 1, var8), var4, var5, var10);
         }
      }
   }

   protected boolean func_208071_a(World var1, BlockPos var2, boolean var3, int var4, RailShape var5) {
      IBlockState var6 = var1.func_180495_p(var2);
      if (var6.func_177230_c() != this) {
         return false;
      } else {
         RailShape var7 = (RailShape)var6.func_177229_b(field_176568_b);
         if (var5 == RailShape.EAST_WEST && (var7 == RailShape.NORTH_SOUTH || var7 == RailShape.ASCENDING_NORTH || var7 == RailShape.ASCENDING_SOUTH)) {
            return false;
         } else if (var5 == RailShape.NORTH_SOUTH && (var7 == RailShape.EAST_WEST || var7 == RailShape.ASCENDING_EAST || var7 == RailShape.ASCENDING_WEST)) {
            return false;
         } else if ((Boolean)var6.func_177229_b(field_176569_M)) {
            return var1.func_175640_z(var2) ? true : this.func_176566_a(var1, var2, var6, var3, var4 + 1);
         } else {
            return false;
         }
      }
   }

   protected void func_189541_b(IBlockState var1, World var2, BlockPos var3, Block var4) {
      boolean var5 = (Boolean)var1.func_177229_b(field_176569_M);
      boolean var6 = var2.func_175640_z(var3) || this.func_176566_a(var2, var3, var1, true, 0) || this.func_176566_a(var2, var3, var1, false, 0);
      if (var6 != var5) {
         var2.func_180501_a(var3, (IBlockState)var1.func_206870_a(field_176569_M, var6), 3);
         var2.func_195593_d(var3.func_177977_b(), this);
         if (((RailShape)var1.func_177229_b(field_176568_b)).func_208092_c()) {
            var2.func_195593_d(var3.func_177984_a(), this);
         }
      }

   }

   public IProperty<RailShape> func_176560_l() {
      return field_176568_b;
   }

   public IBlockState func_185499_a(IBlockState var1, Rotation var2) {
      switch(var2) {
      case CLOCKWISE_180:
         switch((RailShape)var1.func_177229_b(field_176568_b)) {
         case ASCENDING_EAST:
            return (IBlockState)var1.func_206870_a(field_176568_b, RailShape.ASCENDING_WEST);
         case ASCENDING_WEST:
            return (IBlockState)var1.func_206870_a(field_176568_b, RailShape.ASCENDING_EAST);
         case ASCENDING_NORTH:
            return (IBlockState)var1.func_206870_a(field_176568_b, RailShape.ASCENDING_SOUTH);
         case ASCENDING_SOUTH:
            return (IBlockState)var1.func_206870_a(field_176568_b, RailShape.ASCENDING_NORTH);
         case SOUTH_EAST:
            return (IBlockState)var1.func_206870_a(field_176568_b, RailShape.NORTH_WEST);
         case SOUTH_WEST:
            return (IBlockState)var1.func_206870_a(field_176568_b, RailShape.NORTH_EAST);
         case NORTH_WEST:
            return (IBlockState)var1.func_206870_a(field_176568_b, RailShape.SOUTH_EAST);
         case NORTH_EAST:
            return (IBlockState)var1.func_206870_a(field_176568_b, RailShape.SOUTH_WEST);
         }
      case COUNTERCLOCKWISE_90:
         switch((RailShape)var1.func_177229_b(field_176568_b)) {
         case NORTH_SOUTH:
            return (IBlockState)var1.func_206870_a(field_176568_b, RailShape.EAST_WEST);
         case EAST_WEST:
            return (IBlockState)var1.func_206870_a(field_176568_b, RailShape.NORTH_SOUTH);
         case ASCENDING_EAST:
            return (IBlockState)var1.func_206870_a(field_176568_b, RailShape.ASCENDING_NORTH);
         case ASCENDING_WEST:
            return (IBlockState)var1.func_206870_a(field_176568_b, RailShape.ASCENDING_SOUTH);
         case ASCENDING_NORTH:
            return (IBlockState)var1.func_206870_a(field_176568_b, RailShape.ASCENDING_WEST);
         case ASCENDING_SOUTH:
            return (IBlockState)var1.func_206870_a(field_176568_b, RailShape.ASCENDING_EAST);
         case SOUTH_EAST:
            return (IBlockState)var1.func_206870_a(field_176568_b, RailShape.NORTH_EAST);
         case SOUTH_WEST:
            return (IBlockState)var1.func_206870_a(field_176568_b, RailShape.SOUTH_EAST);
         case NORTH_WEST:
            return (IBlockState)var1.func_206870_a(field_176568_b, RailShape.SOUTH_WEST);
         case NORTH_EAST:
            return (IBlockState)var1.func_206870_a(field_176568_b, RailShape.NORTH_WEST);
         }
      case CLOCKWISE_90:
         switch((RailShape)var1.func_177229_b(field_176568_b)) {
         case NORTH_SOUTH:
            return (IBlockState)var1.func_206870_a(field_176568_b, RailShape.EAST_WEST);
         case EAST_WEST:
            return (IBlockState)var1.func_206870_a(field_176568_b, RailShape.NORTH_SOUTH);
         case ASCENDING_EAST:
            return (IBlockState)var1.func_206870_a(field_176568_b, RailShape.ASCENDING_SOUTH);
         case ASCENDING_WEST:
            return (IBlockState)var1.func_206870_a(field_176568_b, RailShape.ASCENDING_NORTH);
         case ASCENDING_NORTH:
            return (IBlockState)var1.func_206870_a(field_176568_b, RailShape.ASCENDING_EAST);
         case ASCENDING_SOUTH:
            return (IBlockState)var1.func_206870_a(field_176568_b, RailShape.ASCENDING_WEST);
         case SOUTH_EAST:
            return (IBlockState)var1.func_206870_a(field_176568_b, RailShape.SOUTH_WEST);
         case SOUTH_WEST:
            return (IBlockState)var1.func_206870_a(field_176568_b, RailShape.NORTH_WEST);
         case NORTH_WEST:
            return (IBlockState)var1.func_206870_a(field_176568_b, RailShape.NORTH_EAST);
         case NORTH_EAST:
            return (IBlockState)var1.func_206870_a(field_176568_b, RailShape.SOUTH_EAST);
         }
      default:
         return var1;
      }
   }

   public IBlockState func_185471_a(IBlockState var1, Mirror var2) {
      RailShape var3 = (RailShape)var1.func_177229_b(field_176568_b);
      switch(var2) {
      case LEFT_RIGHT:
         switch(var3) {
         case ASCENDING_NORTH:
            return (IBlockState)var1.func_206870_a(field_176568_b, RailShape.ASCENDING_SOUTH);
         case ASCENDING_SOUTH:
            return (IBlockState)var1.func_206870_a(field_176568_b, RailShape.ASCENDING_NORTH);
         case SOUTH_EAST:
            return (IBlockState)var1.func_206870_a(field_176568_b, RailShape.NORTH_EAST);
         case SOUTH_WEST:
            return (IBlockState)var1.func_206870_a(field_176568_b, RailShape.NORTH_WEST);
         case NORTH_WEST:
            return (IBlockState)var1.func_206870_a(field_176568_b, RailShape.SOUTH_WEST);
         case NORTH_EAST:
            return (IBlockState)var1.func_206870_a(field_176568_b, RailShape.SOUTH_EAST);
         default:
            return super.func_185471_a(var1, var2);
         }
      case FRONT_BACK:
         switch(var3) {
         case ASCENDING_EAST:
            return (IBlockState)var1.func_206870_a(field_176568_b, RailShape.ASCENDING_WEST);
         case ASCENDING_WEST:
            return (IBlockState)var1.func_206870_a(field_176568_b, RailShape.ASCENDING_EAST);
         case ASCENDING_NORTH:
         case ASCENDING_SOUTH:
         default:
            break;
         case SOUTH_EAST:
            return (IBlockState)var1.func_206870_a(field_176568_b, RailShape.SOUTH_WEST);
         case SOUTH_WEST:
            return (IBlockState)var1.func_206870_a(field_176568_b, RailShape.SOUTH_EAST);
         case NORTH_WEST:
            return (IBlockState)var1.func_206870_a(field_176568_b, RailShape.NORTH_EAST);
         case NORTH_EAST:
            return (IBlockState)var1.func_206870_a(field_176568_b, RailShape.NORTH_WEST);
         }
      }

      return super.func_185471_a(var1, var2);
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_176568_b, field_176569_M);
   }

   static {
      field_176568_b = BlockStateProperties.field_208166_S;
      field_176569_M = BlockStateProperties.field_208194_u;
   }
}
