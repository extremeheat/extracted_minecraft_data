package net.minecraft.block;

import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockHugeMushroom extends Block {
   public static final BooleanProperty field_196459_a;
   public static final BooleanProperty field_196461_b;
   public static final BooleanProperty field_196463_c;
   public static final BooleanProperty field_196464_y;
   public static final BooleanProperty field_196465_z;
   public static final BooleanProperty field_196460_A;
   private static final Map<EnumFacing, BooleanProperty> field_196462_B;
   @Nullable
   private final Block field_176379_b;

   public BlockHugeMushroom(@Nullable Block var1, Block.Properties var2) {
      super(var2);
      this.field_176379_b = var1;
      this.func_180632_j((IBlockState)((IBlockState)((IBlockState)((IBlockState)((IBlockState)((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_196459_a, true)).func_206870_a(field_196461_b, true)).func_206870_a(field_196463_c, true)).func_206870_a(field_196464_y, true)).func_206870_a(field_196465_z, true)).func_206870_a(field_196460_A, true));
   }

   public int func_196264_a(IBlockState var1, Random var2) {
      return Math.max(0, var2.nextInt(9) - 6);
   }

   public IItemProvider func_199769_a(IBlockState var1, World var2, BlockPos var3, int var4) {
      return (IItemProvider)(this.field_176379_b == null ? Items.field_190931_a : this.field_176379_b);
   }

   public IBlockState func_196258_a(BlockItemUseContext var1) {
      World var2 = var1.func_195991_k();
      BlockPos var3 = var1.func_195995_a();
      return (IBlockState)((IBlockState)((IBlockState)((IBlockState)((IBlockState)((IBlockState)this.func_176223_P().func_206870_a(field_196460_A, this != var2.func_180495_p(var3.func_177977_b()).func_177230_c())).func_206870_a(field_196465_z, this != var2.func_180495_p(var3.func_177984_a()).func_177230_c())).func_206870_a(field_196459_a, this != var2.func_180495_p(var3.func_177978_c()).func_177230_c())).func_206870_a(field_196461_b, this != var2.func_180495_p(var3.func_177974_f()).func_177230_c())).func_206870_a(field_196463_c, this != var2.func_180495_p(var3.func_177968_d()).func_177230_c())).func_206870_a(field_196464_y, this != var2.func_180495_p(var3.func_177976_e()).func_177230_c());
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      return var3.func_177230_c() == this ? (IBlockState)var1.func_206870_a((IProperty)field_196462_B.get(var2), false) : super.func_196271_a(var1, var2, var3, var4, var5, var6);
   }

   public IBlockState func_185499_a(IBlockState var1, Rotation var2) {
      return (IBlockState)((IBlockState)((IBlockState)((IBlockState)((IBlockState)((IBlockState)var1.func_206870_a((IProperty)field_196462_B.get(var2.func_185831_a(EnumFacing.NORTH)), var1.func_177229_b(field_196459_a))).func_206870_a((IProperty)field_196462_B.get(var2.func_185831_a(EnumFacing.SOUTH)), var1.func_177229_b(field_196463_c))).func_206870_a((IProperty)field_196462_B.get(var2.func_185831_a(EnumFacing.EAST)), var1.func_177229_b(field_196461_b))).func_206870_a((IProperty)field_196462_B.get(var2.func_185831_a(EnumFacing.WEST)), var1.func_177229_b(field_196464_y))).func_206870_a((IProperty)field_196462_B.get(var2.func_185831_a(EnumFacing.UP)), var1.func_177229_b(field_196465_z))).func_206870_a((IProperty)field_196462_B.get(var2.func_185831_a(EnumFacing.DOWN)), var1.func_177229_b(field_196460_A));
   }

   public IBlockState func_185471_a(IBlockState var1, Mirror var2) {
      return (IBlockState)((IBlockState)((IBlockState)((IBlockState)((IBlockState)((IBlockState)var1.func_206870_a((IProperty)field_196462_B.get(var2.func_185803_b(EnumFacing.NORTH)), var1.func_177229_b(field_196459_a))).func_206870_a((IProperty)field_196462_B.get(var2.func_185803_b(EnumFacing.SOUTH)), var1.func_177229_b(field_196463_c))).func_206870_a((IProperty)field_196462_B.get(var2.func_185803_b(EnumFacing.EAST)), var1.func_177229_b(field_196461_b))).func_206870_a((IProperty)field_196462_B.get(var2.func_185803_b(EnumFacing.WEST)), var1.func_177229_b(field_196464_y))).func_206870_a((IProperty)field_196462_B.get(var2.func_185803_b(EnumFacing.UP)), var1.func_177229_b(field_196465_z))).func_206870_a((IProperty)field_196462_B.get(var2.func_185803_b(EnumFacing.DOWN)), var1.func_177229_b(field_196460_A));
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_196465_z, field_196460_A, field_196459_a, field_196461_b, field_196463_c, field_196464_y);
   }

   static {
      field_196459_a = BlockSixWay.field_196488_a;
      field_196461_b = BlockSixWay.field_196490_b;
      field_196463_c = BlockSixWay.field_196492_c;
      field_196464_y = BlockSixWay.field_196495_y;
      field_196465_z = BlockSixWay.field_196496_z;
      field_196460_A = BlockSixWay.field_196489_A;
      field_196462_B = BlockSixWay.field_196491_B;
   }
}
