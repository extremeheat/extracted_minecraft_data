package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.init.Blocks;
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
         this.field_76752_A = Blocks.field_150433_aE;
      }

      this.field_76762_K.clear();
   }

   @Override
   public void func_76728_a(World var1, Random var2, int var3, int var4) {
      if (this.field_150615_aC) {
         for(int var5 = 0; var5 < 3; ++var5) {
            int var6 = var3 + var2.nextInt(16) + 8;
            int var7 = var4 + var2.nextInt(16) + 8;
            this.field_150616_aD.func_76484_a(var1, var2, var6, var1.func_72976_f(var6, var7), var7);
         }

         for(int var8 = 0; var8 < 2; ++var8) {
            int var9 = var3 + var2.nextInt(16) + 8;
            int var10 = var4 + var2.nextInt(16) + 8;
            this.field_150617_aE.func_76484_a(var1, var2, var9, var1.func_72976_f(var9, var10), var10);
         }
      }

      super.func_76728_a(var1, var2, var3, var4);
   }

   @Override
   public WorldGenAbstractTree func_150567_a(Random var1) {
      return new WorldGenTaiga2(false);
   }

   @Override
   protected BiomeGenBase func_150566_k() {
      BiomeGenBase var1 = new BiomeGenSnow(this.field_76756_M + 128, true)
         .func_150557_a(13828095, true)
         .func_76735_a(this.field_76791_y + " Spikes")
         .func_76742_b()
         .func_76732_a(0.0F, 0.5F)
         .func_150570_a(new BiomeGenBase$Height(this.field_76748_D + 0.1F, this.field_76749_E + 0.1F));
      var1.field_76748_D = this.field_76748_D + 0.3F;
      var1.field_76749_E = this.field_76749_E + 0.4F;
      return var1;
   }
}
