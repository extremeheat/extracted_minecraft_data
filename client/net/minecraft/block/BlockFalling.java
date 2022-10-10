package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.init.Particles;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockFalling extends Block {
   public static boolean field_149832_M;

   public BlockFalling(Block.Properties var1) {
      super(var1);
   }

   public void func_196259_b(IBlockState var1, World var2, BlockPos var3, IBlockState var4) {
      var2.func_205220_G_().func_205360_a(var3, this, this.func_149738_a(var2));
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      var4.func_205220_G_().func_205360_a(var5, this, this.func_149738_a(var4));
      return super.func_196271_a(var1, var2, var3, var4, var5, var6);
   }

   public void func_196267_b(IBlockState var1, World var2, BlockPos var3, Random var4) {
      if (!var2.field_72995_K) {
         this.func_176503_e(var2, var3);
      }

   }

   private void func_176503_e(World var1, BlockPos var2) {
      if (func_185759_i(var1.func_180495_p(var2.func_177977_b())) && var2.func_177956_o() >= 0) {
         boolean var3 = true;
         if (!field_149832_M && var1.func_175707_a(var2.func_177982_a(-32, -32, -32), var2.func_177982_a(32, 32, 32))) {
            if (!var1.field_72995_K) {
               EntityFallingBlock var5 = new EntityFallingBlock(var1, (double)var2.func_177958_n() + 0.5D, (double)var2.func_177956_o(), (double)var2.func_177952_p() + 0.5D, var1.func_180495_p(var2));
               this.func_149829_a(var5);
               var1.func_72838_d(var5);
            }
         } else {
            if (var1.func_180495_p(var2).func_177230_c() == this) {
               var1.func_175698_g(var2);
            }

            BlockPos var4;
            for(var4 = var2.func_177977_b(); func_185759_i(var1.func_180495_p(var4)) && var4.func_177956_o() > 0; var4 = var4.func_177977_b()) {
            }

            if (var4.func_177956_o() > 0) {
               var1.func_175656_a(var4.func_177984_a(), this.func_176223_P());
            }
         }

      }
   }

   protected void func_149829_a(EntityFallingBlock var1) {
   }

   public int func_149738_a(IWorldReaderBase var1) {
      return 2;
   }

   public static boolean func_185759_i(IBlockState var0) {
      Block var1 = var0.func_177230_c();
      Material var2 = var0.func_185904_a();
      return var0.func_196958_f() || var1 == Blocks.field_150480_ab || var2.func_76224_d() || var2.func_76222_j();
   }

   public void func_176502_a_(World var1, BlockPos var2, IBlockState var3, IBlockState var4) {
   }

   public void func_190974_b(World var1, BlockPos var2) {
   }

   public void func_180655_c(IBlockState var1, World var2, BlockPos var3, Random var4) {
      if (var4.nextInt(16) == 0) {
         BlockPos var5 = var3.func_177977_b();
         if (func_185759_i(var2.func_180495_p(var5))) {
            double var6 = (double)((float)var3.func_177958_n() + var4.nextFloat());
            double var8 = (double)var3.func_177956_o() - 0.05D;
            double var10 = (double)((float)var3.func_177952_p() + var4.nextFloat());
            var2.func_195594_a(new BlockParticleData(Particles.field_197628_u, var1), var6, var8, var10, 0.0D, 0.0D, 0.0D);
         }
      }

   }

   public int func_189876_x(IBlockState var1) {
      return -16777216;
   }
}
