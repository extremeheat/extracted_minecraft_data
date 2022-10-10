package net.minecraft.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockRedstoneTorch extends BlockTorch {
   public static final BooleanProperty field_196528_a;
   private static final Map<IBlockReader, List<BlockRedstoneTorch.Toggle>> field_196529_b;

   protected BlockRedstoneTorch(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_196528_a, true));
   }

   public int func_149738_a(IWorldReaderBase var1) {
      return 2;
   }

   public void func_196259_b(IBlockState var1, World var2, BlockPos var3, IBlockState var4) {
      EnumFacing[] var5 = EnumFacing.values();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         EnumFacing var8 = var5[var7];
         var2.func_195593_d(var3.func_177972_a(var8), this);
      }

   }

   public void func_196243_a(IBlockState var1, World var2, BlockPos var3, IBlockState var4, boolean var5) {
      if (!var5) {
         EnumFacing[] var6 = EnumFacing.values();
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            EnumFacing var9 = var6[var8];
            var2.func_195593_d(var3.func_177972_a(var9), this);
         }

      }
   }

   public int func_180656_a(IBlockState var1, IBlockReader var2, BlockPos var3, EnumFacing var4) {
      return (Boolean)var1.func_177229_b(field_196528_a) && EnumFacing.UP != var4 ? 15 : 0;
   }

   protected boolean func_176597_g(World var1, BlockPos var2, IBlockState var3) {
      return var1.func_175709_b(var2.func_177977_b(), EnumFacing.DOWN);
   }

   public void func_196267_b(IBlockState var1, World var2, BlockPos var3, Random var4) {
      func_196527_a(var1, var2, var3, var4, this.func_176597_g(var2, var3, var1));
   }

   public static void func_196527_a(IBlockState var0, World var1, BlockPos var2, Random var3, boolean var4) {
      List var5 = (List)field_196529_b.get(var1);

      while(var5 != null && !var5.isEmpty() && var1.func_82737_E() - ((BlockRedstoneTorch.Toggle)var5.get(0)).field_150844_d > 60L) {
         var5.remove(0);
      }

      if ((Boolean)var0.func_177229_b(field_196528_a)) {
         if (var4) {
            var1.func_180501_a(var2, (IBlockState)var0.func_206870_a(field_196528_a, false), 3);
            if (func_176598_a(var1, var2, true)) {
               var1.func_184133_a((EntityPlayer)null, var2, SoundEvents.field_187745_eA, SoundCategory.BLOCKS, 0.5F, 2.6F + (var1.field_73012_v.nextFloat() - var1.field_73012_v.nextFloat()) * 0.8F);

               for(int var6 = 0; var6 < 5; ++var6) {
                  double var7 = (double)var2.func_177958_n() + var3.nextDouble() * 0.6D + 0.2D;
                  double var9 = (double)var2.func_177956_o() + var3.nextDouble() * 0.6D + 0.2D;
                  double var11 = (double)var2.func_177952_p() + var3.nextDouble() * 0.6D + 0.2D;
                  var1.func_195594_a(Particles.field_197601_L, var7, var9, var11, 0.0D, 0.0D, 0.0D);
               }

               var1.func_205220_G_().func_205360_a(var2, var1.func_180495_p(var2).func_177230_c(), 160);
            }
         }
      } else if (!var4 && !func_176598_a(var1, var2, false)) {
         var1.func_180501_a(var2, (IBlockState)var0.func_206870_a(field_196528_a, true), 3);
      }

   }

   public void func_189540_a(IBlockState var1, World var2, BlockPos var3, Block var4, BlockPos var5) {
      if ((Boolean)var1.func_177229_b(field_196528_a) == this.func_176597_g(var2, var3, var1) && !var2.func_205220_G_().func_205361_b(var3, this)) {
         var2.func_205220_G_().func_205360_a(var3, this, this.func_149738_a(var2));
      }

   }

   public int func_176211_b(IBlockState var1, IBlockReader var2, BlockPos var3, EnumFacing var4) {
      return var4 == EnumFacing.DOWN ? var1.func_185911_a(var2, var3, var4) : 0;
   }

   public boolean func_149744_f(IBlockState var1) {
      return true;
   }

   public void func_180655_c(IBlockState var1, World var2, BlockPos var3, Random var4) {
      if ((Boolean)var1.func_177229_b(field_196528_a)) {
         double var5 = (double)var3.func_177958_n() + 0.5D + (var4.nextDouble() - 0.5D) * 0.2D;
         double var7 = (double)var3.func_177956_o() + 0.7D + (var4.nextDouble() - 0.5D) * 0.2D;
         double var9 = (double)var3.func_177952_p() + 0.5D + (var4.nextDouble() - 0.5D) * 0.2D;
         var2.func_195594_a(RedstoneParticleData.field_197564_a, var5, var7, var9, 0.0D, 0.0D, 0.0D);
      }
   }

   public int func_149750_m(IBlockState var1) {
      return (Boolean)var1.func_177229_b(field_196528_a) ? super.func_149750_m(var1) : 0;
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_196528_a);
   }

   private static boolean func_176598_a(World var0, BlockPos var1, boolean var2) {
      Object var3 = (List)field_196529_b.get(var0);
      if (var3 == null) {
         var3 = Lists.newArrayList();
         field_196529_b.put(var0, var3);
      }

      if (var2) {
         ((List)var3).add(new BlockRedstoneTorch.Toggle(var1.func_185334_h(), var0.func_82737_E()));
      }

      int var4 = 0;

      for(int var5 = 0; var5 < ((List)var3).size(); ++var5) {
         BlockRedstoneTorch.Toggle var6 = (BlockRedstoneTorch.Toggle)((List)var3).get(var5);
         if (var6.field_180111_a.equals(var1)) {
            ++var4;
            if (var4 >= 8) {
               return true;
            }
         }
      }

      return false;
   }

   static {
      field_196528_a = BlockStateProperties.field_208190_q;
      field_196529_b = Maps.newHashMap();
   }

   public static class Toggle {
      private final BlockPos field_180111_a;
      private final long field_150844_d;

      public Toggle(BlockPos var1, long var2) {
         super();
         this.field_180111_a = var1;
         this.field_150844_d = var2;
      }
   }
}
