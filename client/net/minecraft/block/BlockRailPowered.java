package net.minecraft.block;

import com.google.common.base.Predicate;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockRailPowered extends BlockRailBase {
   public static final PropertyEnum<BlockRailBase.EnumRailDirection> field_176568_b = PropertyEnum.func_177708_a("shape", BlockRailBase.EnumRailDirection.class, new Predicate<BlockRailBase.EnumRailDirection>() {
      public boolean apply(BlockRailBase.EnumRailDirection var1) {
         return var1 != BlockRailBase.EnumRailDirection.NORTH_EAST && var1 != BlockRailBase.EnumRailDirection.NORTH_WEST && var1 != BlockRailBase.EnumRailDirection.SOUTH_EAST && var1 != BlockRailBase.EnumRailDirection.SOUTH_WEST;
      }

      // $FF: synthetic method
      public boolean apply(Object var1) {
         return this.apply((BlockRailBase.EnumRailDirection)var1);
      }
   });
   public static final PropertyBool field_176569_M = PropertyBool.func_177716_a("powered");

   protected BlockRailPowered() {
      super(true);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176568_b, BlockRailBase.EnumRailDirection.NORTH_SOUTH).func_177226_a(field_176569_M, false));
   }

   protected boolean func_176566_a(World var1, BlockPos var2, IBlockState var3, boolean var4, int var5) {
      if (var5 >= 8) {
         return false;
      } else {
         int var6 = var2.func_177958_n();
         int var7 = var2.func_177956_o();
         int var8 = var2.func_177952_p();
         boolean var9 = true;
         BlockRailBase.EnumRailDirection var10 = (BlockRailBase.EnumRailDirection)var3.func_177229_b(field_176568_b);
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

            var10 = BlockRailBase.EnumRailDirection.EAST_WEST;
            break;
         case ASCENDING_WEST:
            if (var4) {
               --var6;
               ++var7;
               var9 = false;
            } else {
               ++var6;
            }

            var10 = BlockRailBase.EnumRailDirection.EAST_WEST;
            break;
         case ASCENDING_NORTH:
            if (var4) {
               ++var8;
            } else {
               --var8;
               ++var7;
               var9 = false;
            }

            var10 = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
            break;
         case ASCENDING_SOUTH:
            if (var4) {
               ++var8;
               ++var7;
               var9 = false;
            } else {
               --var8;
            }

            var10 = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
         }

         if (this.func_176567_a(var1, new BlockPos(var6, var7, var8), var4, var5, var10)) {
            return true;
         } else {
            return var9 && this.func_176567_a(var1, new BlockPos(var6, var7 - 1, var8), var4, var5, var10);
         }
      }
   }

   protected boolean func_176567_a(World var1, BlockPos var2, boolean var3, int var4, BlockRailBase.EnumRailDirection var5) {
      IBlockState var6 = var1.func_180495_p(var2);
      if (var6.func_177230_c() != this) {
         return false;
      } else {
         BlockRailBase.EnumRailDirection var7 = (BlockRailBase.EnumRailDirection)var6.func_177229_b(field_176568_b);
         if (var5 == BlockRailBase.EnumRailDirection.EAST_WEST && (var7 == BlockRailBase.EnumRailDirection.NORTH_SOUTH || var7 == BlockRailBase.EnumRailDirection.ASCENDING_NORTH || var7 == BlockRailBase.EnumRailDirection.ASCENDING_SOUTH)) {
            return false;
         } else if (var5 == BlockRailBase.EnumRailDirection.NORTH_SOUTH && (var7 == BlockRailBase.EnumRailDirection.EAST_WEST || var7 == BlockRailBase.EnumRailDirection.ASCENDING_EAST || var7 == BlockRailBase.EnumRailDirection.ASCENDING_WEST)) {
            return false;
         } else if ((Boolean)var6.func_177229_b(field_176569_M)) {
            return var1.func_175640_z(var2) ? true : this.func_176566_a(var1, var2, var6, var3, var4 + 1);
         } else {
            return false;
         }
      }
   }

   protected void func_176561_b(World var1, BlockPos var2, IBlockState var3, Block var4) {
      boolean var5 = (Boolean)var3.func_177229_b(field_176569_M);
      boolean var6 = var1.func_175640_z(var2) || this.func_176566_a(var1, var2, var3, true, 0) || this.func_176566_a(var1, var2, var3, false, 0);
      if (var6 != var5) {
         var1.func_180501_a(var2, var3.func_177226_a(field_176569_M, var6), 3);
         var1.func_175685_c(var2.func_177977_b(), this);
         if (((BlockRailBase.EnumRailDirection)var3.func_177229_b(field_176568_b)).func_177018_c()) {
            var1.func_175685_c(var2.func_177984_a(), this);
         }
      }

   }

   public IProperty<BlockRailBase.EnumRailDirection> func_176560_l() {
      return field_176568_b;
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176568_b, BlockRailBase.EnumRailDirection.func_177016_a(var1 & 7)).func_177226_a(field_176569_M, (var1 & 8) > 0);
   }

   public int func_176201_c(IBlockState var1) {
      byte var2 = 0;
      int var3 = var2 | ((BlockRailBase.EnumRailDirection)var1.func_177229_b(field_176568_b)).func_177015_a();
      if ((Boolean)var1.func_177229_b(field_176569_M)) {
         var3 |= 8;
      }

      return var3;
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176568_b, field_176569_M});
   }
}
