package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockStainedGlassPane extends BlockGlassPane {
   private final EnumDyeColor field_196420_C;

   public BlockStainedGlassPane(EnumDyeColor var1, Block.Properties var2) {
      super(var2);
      this.field_196420_C = var1;
      this.func_180632_j((IBlockState)((IBlockState)((IBlockState)((IBlockState)((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_196409_a, false)).func_206870_a(field_196411_b, false)).func_206870_a(field_196413_c, false)).func_206870_a(field_196414_y, false)).func_206870_a(field_204514_u, false));
   }

   public EnumDyeColor func_196419_d() {
      return this.field_196420_C;
   }

   public BlockRenderLayer func_180664_k() {
      return BlockRenderLayer.TRANSLUCENT;
   }

   public void func_196259_b(IBlockState var1, World var2, BlockPos var3, IBlockState var4) {
      if (var4.func_177230_c() != var1.func_177230_c()) {
         if (!var2.field_72995_K) {
            BlockBeacon.func_176450_d(var2, var3);
         }

      }
   }

   public void func_196243_a(IBlockState var1, World var2, BlockPos var3, IBlockState var4, boolean var5) {
      if (var1.func_177230_c() != var4.func_177230_c()) {
         if (!var2.field_72995_K) {
            BlockBeacon.func_176450_d(var2, var3);
         }

      }
   }
}
