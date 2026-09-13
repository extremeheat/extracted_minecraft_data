package net.minecraft.world.biome;

import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.feature.WorldGenSpikes;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeEndDecorator extends BiomeDecorator {
   protected WorldGenerator field_76835_L = new WorldGenSpikes(Blocks.field_150377_bs);

   public BiomeEndDecorator() {
      super();
   }

   @Override
   protected void func_150513_a(BiomeGenBase var1) {
      this.func_76797_b();
      if (this.field_76813_b.nextInt(5) == 0) {
         int var2 = this.field_76814_c + this.field_76813_b.nextInt(16) + 8;
         int var3 = this.field_76811_d + this.field_76813_b.nextInt(16) + 8;
         int var4 = this.field_76815_a.func_72825_h(var2, var3);
         this.field_76835_L.func_76484_a(this.field_76815_a, this.field_76813_b, var2, var4, var3);
      }

      if (this.field_76814_c == 0 && this.field_76811_d == 0) {
         EntityDragon var5 = new EntityDragon(this.field_76815_a);
         var5.func_70012_b(0.0, 128.0, 0.0, this.field_76813_b.nextFloat() * 360.0F, 0.0F);
         this.field_76815_a.func_72838_d(var5);
      }
   }
}
