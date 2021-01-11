package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenIcePath;
import net.minecraft.world.gen.feature.WorldGenIceSpike;
import net.minecraft.world.gen.feature.WorldGenTaiga2;

public class BiomeGenSnow extends BiomeGenBase {
   private boolean field_150615_aC;
   private WorldGenIceSpike field_150616_aD = new WorldGenIceSpike();
   private WorldGenIcePath field_150617_aE = new WorldGenIcePath(4);

   public BiomeGenSnow(int var1, boolean var2) {
      super(var1);
      this.field_150615_aC = var2;
      if (var2) {
         this.field_76752_A = Blocks.field_150433_aE.func_176223_P();
      }

      this.field_76762_K.clear();
   }

   public void func_180624_a(World var1, Random var2, BlockPos var3) {
      if (this.field_150615_aC) {
         int var4;
         int var5;
         int var6;
         for(var4 = 0; var4 < 3; ++var4) {
            var5 = var2.nextInt(16) + 8;
            var6 = var2.nextInt(16) + 8;
            this.field_150616_aD.func_180709_b(var1, var2, var1.func_175645_m(var3.func_177982_a(var5, 0, var6)));
         }

         for(var4 = 0; var4 < 2; ++var4) {
            var5 = var2.nextInt(16) + 8;
            var6 = var2.nextInt(16) + 8;
            this.field_150617_aE.func_180709_b(var1, var2, var1.func_175645_m(var3.func_177982_a(var5, 0, var6)));
         }
      }

      super.func_180624_a(var1, var2, var3);
   }

   public WorldGenAbstractTree func_150567_a(Random var1) {
      return new WorldGenTaiga2(false);
   }

   protected BiomeGenBase func_180277_d(int var1) {
      BiomeGenBase var2 = (new BiomeGenSnow(var1, true)).func_150557_a(13828095, true).func_76735_a(this.field_76791_y + " Spikes").func_76742_b().func_76732_a(0.0F, 0.5F).func_150570_a(new BiomeGenBase.Height(this.field_76748_D + 0.1F, this.field_76749_E + 0.1F));
      var2.field_76748_D = this.field_76748_D + 0.3F;
      var2.field_76749_E = this.field_76749_E + 0.4F;
      return var2;
   }
}
