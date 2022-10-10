package net.minecraft.world.gen.feature;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.BlockVine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class VinesFeature extends Feature<NoFeatureConfig> {
   public VinesFeature() {
      super();
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, NoFeatureConfig var5) {
      BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos(var4);

      for(int var7 = var4.func_177956_o(); var7 < 256; ++var7) {
         var6.func_189533_g(var4);
         var6.func_196234_d(var3.nextInt(4) - var3.nextInt(4), 0, var3.nextInt(4) - var3.nextInt(4));
         var6.func_185336_p(var7);
         if (var1.func_175623_d(var6)) {
            Iterator var8 = EnumFacing.Plane.HORIZONTAL.iterator();

            while(var8.hasNext()) {
               EnumFacing var9 = (EnumFacing)var8.next();
               IBlockState var10 = (IBlockState)Blocks.field_150395_bd.func_176223_P().func_206870_a(BlockVine.func_176267_a(var9), true);
               if (var10.func_196955_c(var1, var6)) {
                  var1.func_180501_a(var6, var10, 2);
                  break;
               }
            }
         }
      }

      return true;
   }
}
