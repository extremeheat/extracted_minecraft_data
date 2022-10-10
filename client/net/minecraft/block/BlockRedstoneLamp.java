package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockRedstoneLamp extends Block {
   public static final BooleanProperty field_196502_a;

   public BlockRedstoneLamp(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)this.func_176223_P().func_206870_a(field_196502_a, false));
   }

   public int func_149750_m(IBlockState var1) {
      return (Boolean)var1.func_177229_b(field_196502_a) ? super.func_149750_m(var1) : 0;
   }

   public void func_196259_b(IBlockState var1, World var2, BlockPos var3, IBlockState var4) {
      super.func_196259_b(var1, var2, var3, var4);
   }

   @Nullable
   public IBlockState func_196258_a(BlockItemUseContext var1) {
      return (IBlockState)this.func_176223_P().func_206870_a(field_196502_a, var1.func_195991_k().func_175640_z(var1.func_195995_a()));
   }

   public void func_189540_a(IBlockState var1, World var2, BlockPos var3, Block var4, BlockPos var5) {
      if (!var2.field_72995_K) {
         boolean var6 = (Boolean)var1.func_177229_b(field_196502_a);
         if (var6 != var2.func_175640_z(var3)) {
            if (var6) {
               var2.func_205220_G_().func_205360_a(var3, this, 4);
            } else {
               var2.func_180501_a(var3, (IBlockState)var1.func_177231_a(field_196502_a), 2);
            }
         }

      }
   }

   public void func_196267_b(IBlockState var1, World var2, BlockPos var3, Random var4) {
      if (!var2.field_72995_K) {
         if ((Boolean)var1.func_177229_b(field_196502_a) && !var2.func_175640_z(var3)) {
            var2.func_180501_a(var3, (IBlockState)var1.func_177231_a(field_196502_a), 2);
         }

      }
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_196502_a);
   }

   static {
      field_196502_a = BlockRedstoneTorch.field_196528_a;
   }
}
