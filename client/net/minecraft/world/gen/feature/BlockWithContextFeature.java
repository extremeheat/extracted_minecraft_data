package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class BlockWithContextFeature extends Feature<BlockWithContextConfig> {
   public BlockWithContextFeature() {
      super();
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, BlockWithContextConfig var5) {
      if (var5.field_206925_b.contains(var1.func_180495_p(var4.func_177977_b())) && var5.field_206926_c.contains(var1.func_180495_p(var4)) && var5.field_206927_d.contains(var1.func_180495_p(var4.func_177984_a()))) {
         var1.func_180501_a(var4, var5.field_206924_a, 2);
         return true;
      } else {
         return false;
      }
   }
}
