package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenSavannaTree;

public class BiomeGenSavanna extends BiomeGenBase {
   private static final WorldGenSavannaTree field_150627_aC = new WorldGenSavannaTree(false);

   protected BiomeGenSavanna(int var1) {
      super(var1);
      this.field_76762_K.add(new BiomeGenBase$SpawnListEntry(EntityHorse.class, 1, 2, 6));
      this.field_76760_I.field_76832_z = 1;
      this.field_76760_I.field_76802_A = 4;
      this.field_76760_I.field_76803_B = 20;
   }

   @Override
   public WorldGenAbstractTree func_150567_a(Random var1) {
      return (WorldGenAbstractTree)(var1.nextInt(5) > 0 ? field_150627_aC : this.field_76757_N);
   }

   @Override
   protected BiomeGenBase func_150566_k() {
      BiomeGenSavanna$Mutated var1 = new BiomeGenSavanna$Mutated(this.field_76756_M + 128, this);
      var1.field_76750_F = (this.field_76750_F + 1.0F) * 0.5F;
      var1.field_76748_D = this.field_76748_D * 0.5F + 0.3F;
      var1.field_76749_E = this.field_76749_E * 0.5F + 1.2F;
      return var1;
   }

   @Override
   public void func_76728_a(World var1, Random var2, int var3, int var4) {
      field_150610_ae.func_150548_a(2);

      for(int var5 = 0; var5 < 7; ++var5) {
         int var6 = var3 + var2.nextInt(16) + 8;
         int var7 = var4 + var2.nextInt(16) + 8;
         int var8 = var2.nextInt(var1.func_72976_f(var6, var7) + 32);
         field_150610_ae.func_76484_a(var1, var2, var6, var8, var7);
      }

      super.func_76728_a(var1, var2, var3, var4);
   }
}
