package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockRedstoneRepeater extends BlockRedstoneDiode {
   public static final BooleanProperty field_176411_a;
   public static final IntegerProperty field_176410_b;

   protected BlockRedstoneRepeater(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)((IBlockState)((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_185512_D, EnumFacing.NORTH)).func_206870_a(field_176410_b, 1)).func_206870_a(field_176411_a, false)).func_206870_a(field_196348_c, false));
   }

   public boolean func_196250_a(IBlockState var1, World var2, BlockPos var3, EntityPlayer var4, EnumHand var5, EnumFacing var6, float var7, float var8, float var9) {
      if (!var4.field_71075_bZ.field_75099_e) {
         return false;
      } else {
         var2.func_180501_a(var3, (IBlockState)var1.func_177231_a(field_176410_b), 3);
         return true;
      }
   }

   protected int func_196346_i(IBlockState var1) {
      return (Integer)var1.func_177229_b(field_176410_b) * 2;
   }

   public IBlockState func_196258_a(BlockItemUseContext var1) {
      IBlockState var2 = super.func_196258_a(var1);
      return (IBlockState)var2.func_206870_a(field_176411_a, this.func_176405_b(var1.func_195991_k(), var1.func_195995_a(), var2));
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      return !var4.func_201670_d() && var2.func_176740_k() != ((EnumFacing)var1.func_177229_b(field_185512_D)).func_176740_k() ? (IBlockState)var1.func_206870_a(field_176411_a, this.func_176405_b(var4, var5, var1)) : super.func_196271_a(var1, var2, var3, var4, var5, var6);
   }

   public boolean func_176405_b(IWorldReaderBase var1, BlockPos var2, IBlockState var3) {
      return this.func_176407_c(var1, var2, var3) > 0;
   }

   protected boolean func_185545_A(IBlockState var1) {
      return func_185546_B(var1);
   }

   public void func_180655_c(IBlockState var1, World var2, BlockPos var3, Random var4) {
      if ((Boolean)var1.func_177229_b(field_196348_c)) {
         EnumFacing var5 = (EnumFacing)var1.func_177229_b(field_185512_D);
         double var6 = (double)((float)var3.func_177958_n() + 0.5F) + (double)(var4.nextFloat() - 0.5F) * 0.2D;
         double var8 = (double)((float)var3.func_177956_o() + 0.4F) + (double)(var4.nextFloat() - 0.5F) * 0.2D;
         double var10 = (double)((float)var3.func_177952_p() + 0.5F) + (double)(var4.nextFloat() - 0.5F) * 0.2D;
         float var12 = -5.0F;
         if (var4.nextBoolean()) {
            var12 = (float)((Integer)var1.func_177229_b(field_176410_b) * 2 - 1);
         }

         var12 /= 16.0F;
         double var13 = (double)(var12 * (float)var5.func_82601_c());
         double var15 = (double)(var12 * (float)var5.func_82599_e());
         var2.func_195594_a(RedstoneParticleData.field_197564_a, var6 + var13, var8, var10 + var15, 0.0D, 0.0D, 0.0D);
      }
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_185512_D, field_176410_b, field_176411_a, field_196348_c);
   }

   static {
      field_176411_a = BlockStateProperties.field_208191_r;
      field_176410_b = BlockStateProperties.field_208126_aa;
   }
}
