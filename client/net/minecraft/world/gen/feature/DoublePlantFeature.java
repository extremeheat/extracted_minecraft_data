package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class DoublePlantFeature extends Feature<DoublePlantConfig> {
   public DoublePlantFeature() {
      super();
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, DoublePlantConfig var5) {
      boolean var6 = false;

      for(int var7 = 0; var7 < 64; ++var7) {
         BlockPos var8 = var4.func_177982_a(var3.nextInt(8) - var3.nextInt(8), var3.nextInt(4) - var3.nextInt(4), var3.nextInt(8) - var3.nextInt(8));
         if (var1.func_175623_d(var8) && var8.func_177956_o() < 254 && var5.field_202435_a.func_196955_c(var1, var8)) {
            ((BlockDoublePlant)var5.field_202435_a.func_177230_c()).func_196390_a(var1, var8, 2);
            var6 = true;
         }
      }

      return var6;
   }
}
