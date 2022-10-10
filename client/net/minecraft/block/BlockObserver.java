package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockObserver extends BlockDirectional {
   public static final BooleanProperty field_190963_a;

   public BlockObserver(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176387_N, EnumFacing.SOUTH)).func_206870_a(field_190963_a, false));
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_176387_N, field_190963_a);
   }

   public IBlockState func_185499_a(IBlockState var1, Rotation var2) {
      return (IBlockState)var1.func_206870_a(field_176387_N, var2.func_185831_a((EnumFacing)var1.func_177229_b(field_176387_N)));
   }

   public IBlockState func_185471_a(IBlockState var1, Mirror var2) {
      return var1.func_185907_a(var2.func_185800_a((EnumFacing)var1.func_177229_b(field_176387_N)));
   }

   public void func_196267_b(IBlockState var1, World var2, BlockPos var3, Random var4) {
      if ((Boolean)var1.func_177229_b(field_190963_a)) {
         var2.func_180501_a(var3, (IBlockState)var1.func_206870_a(field_190963_a, false), 2);
      } else {
         var2.func_180501_a(var3, (IBlockState)var1.func_206870_a(field_190963_a, true), 2);
         var2.func_205220_G_().func_205360_a(var3, this, 2);
      }

      this.func_190961_e(var2, var3, var1);
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      if (var1.func_177229_b(field_176387_N) == var2 && !(Boolean)var1.func_177229_b(field_190963_a)) {
         this.func_203420_a(var4, var5);
      }

      return super.func_196271_a(var1, var2, var3, var4, var5, var6);
   }

   private void func_203420_a(IWorld var1, BlockPos var2) {
      if (!var1.func_201670_d() && !var1.func_205220_G_().func_205359_a(var2, this)) {
         var1.func_205220_G_().func_205360_a(var2, this, 2);
      }

   }

   protected void func_190961_e(World var1, BlockPos var2, IBlockState var3) {
      EnumFacing var4 = (EnumFacing)var3.func_177229_b(field_176387_N);
      BlockPos var5 = var2.func_177972_a(var4.func_176734_d());
      var1.func_190524_a(var5, this, var2);
      var1.func_175695_a(var5, this, var4);
   }

   public boolean func_149744_f(IBlockState var1) {
      return true;
   }

   public int func_176211_b(IBlockState var1, IBlockReader var2, BlockPos var3, EnumFacing var4) {
      return var1.func_185911_a(var2, var3, var4);
   }

   public int func_180656_a(IBlockState var1, IBlockReader var2, BlockPos var3, EnumFacing var4) {
      return (Boolean)var1.func_177229_b(field_190963_a) && var1.func_177229_b(field_176387_N) == var4 ? 15 : 0;
   }

   public void func_196259_b(IBlockState var1, World var2, BlockPos var3, IBlockState var4) {
      if (var1.func_177230_c() != var4.func_177230_c()) {
         if (!var2.func_201670_d() && (Boolean)var1.func_177229_b(field_190963_a) && !var2.func_205220_G_().func_205359_a(var3, this)) {
            IBlockState var5 = (IBlockState)var1.func_206870_a(field_190963_a, false);
            var2.func_180501_a(var3, var5, 18);
            this.func_190961_e(var2, var3, var5);
         }

      }
   }

   public void func_196243_a(IBlockState var1, World var2, BlockPos var3, IBlockState var4, boolean var5) {
      if (var1.func_177230_c() != var4.func_177230_c()) {
         if (!var2.field_72995_K && (Boolean)var1.func_177229_b(field_190963_a) && var2.func_205220_G_().func_205359_a(var3, this)) {
            this.func_190961_e(var2, var3, (IBlockState)var1.func_206870_a(field_190963_a, false));
         }

      }
   }

   public IBlockState func_196258_a(BlockItemUseContext var1) {
      return (IBlockState)this.func_176223_P().func_206870_a(field_176387_N, var1.func_196010_d().func_176734_d().func_176734_d());
   }

   static {
      field_190963_a = BlockStateProperties.field_208194_u;
   }
}
