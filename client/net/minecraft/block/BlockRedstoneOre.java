package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockRedstoneOre extends Block {
   public static final BooleanProperty field_196501_a;

   public BlockRedstoneOre(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)this.func_176223_P().func_206870_a(field_196501_a, false));
   }

   public int func_149750_m(IBlockState var1) {
      return (Boolean)var1.func_177229_b(field_196501_a) ? super.func_149750_m(var1) : 0;
   }

   public void func_196270_a(IBlockState var1, World var2, BlockPos var3, EntityPlayer var4) {
      func_196500_d(var1, var2, var3);
      super.func_196270_a(var1, var2, var3, var4);
   }

   public void func_176199_a(World var1, BlockPos var2, Entity var3) {
      func_196500_d(var1.func_180495_p(var2), var1, var2);
      super.func_176199_a(var1, var2, var3);
   }

   public boolean func_196250_a(IBlockState var1, World var2, BlockPos var3, EntityPlayer var4, EnumHand var5, EnumFacing var6, float var7, float var8, float var9) {
      func_196500_d(var1, var2, var3);
      return super.func_196250_a(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   private static void func_196500_d(IBlockState var0, World var1, BlockPos var2) {
      func_180691_e(var1, var2);
      if (!(Boolean)var0.func_177229_b(field_196501_a)) {
         var1.func_180501_a(var2, (IBlockState)var0.func_206870_a(field_196501_a, true), 3);
      }

   }

   public void func_196267_b(IBlockState var1, World var2, BlockPos var3, Random var4) {
      if ((Boolean)var1.func_177229_b(field_196501_a)) {
         var2.func_180501_a(var3, (IBlockState)var1.func_206870_a(field_196501_a, false), 3);
      }

   }

   public IItemProvider func_199769_a(IBlockState var1, World var2, BlockPos var3, int var4) {
      return Items.field_151137_ax;
   }

   public int func_196251_a(IBlockState var1, int var2, World var3, BlockPos var4, Random var5) {
      return this.func_196264_a(var1, var5) + var5.nextInt(var2 + 1);
   }

   public int func_196264_a(IBlockState var1, Random var2) {
      return 4 + var2.nextInt(2);
   }

   public void func_196255_a(IBlockState var1, World var2, BlockPos var3, float var4, int var5) {
      super.func_196255_a(var1, var2, var3, var4, var5);
      if (this.func_199769_a(var1, var2, var3, var5) != this) {
         int var6 = 1 + var2.field_73012_v.nextInt(5);
         this.func_180637_b(var2, var3, var6);
      }

   }

   public void func_180655_c(IBlockState var1, World var2, BlockPos var3, Random var4) {
      if ((Boolean)var1.func_177229_b(field_196501_a)) {
         func_180691_e(var2, var3);
      }

   }

   private static void func_180691_e(World var0, BlockPos var1) {
      double var2 = 0.5625D;
      Random var4 = var0.field_73012_v;
      EnumFacing[] var5 = EnumFacing.values();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         EnumFacing var8 = var5[var7];
         BlockPos var9 = var1.func_177972_a(var8);
         if (!var0.func_180495_p(var9).func_200015_d(var0, var9)) {
            EnumFacing.Axis var10 = var8.func_176740_k();
            double var11 = var10 == EnumFacing.Axis.X ? 0.5D + 0.5625D * (double)var8.func_82601_c() : (double)var4.nextFloat();
            double var13 = var10 == EnumFacing.Axis.Y ? 0.5D + 0.5625D * (double)var8.func_96559_d() : (double)var4.nextFloat();
            double var15 = var10 == EnumFacing.Axis.Z ? 0.5D + 0.5625D * (double)var8.func_82599_e() : (double)var4.nextFloat();
            var0.func_195594_a(RedstoneParticleData.field_197564_a, (double)var1.func_177958_n() + var11, (double)var1.func_177956_o() + var13, (double)var1.func_177952_p() + var15, 0.0D, 0.0D, 0.0D);
         }
      }

   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_196501_a);
   }

   static {
      field_196501_a = BlockRedstoneTorch.field_196528_a;
   }
}
