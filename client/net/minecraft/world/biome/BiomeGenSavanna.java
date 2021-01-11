package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenSavannaTree;

public class BiomeGenSavanna extends BiomeGenBase {
   private static final WorldGenSavannaTree field_150627_aC = new WorldGenSavannaTree(false);

   protected BiomeGenSavanna(int var1) {
      super(var1);
      this.field_76762_K.add(new BiomeGenBase.SpawnListEntry(EntityHorse.class, 1, 2, 6));
      this.field_76760_I.field_76832_z = 1;
      this.field_76760_I.field_76802_A = 4;
      this.field_76760_I.field_76803_B = 20;
   }

   public WorldGenAbstractTree func_150567_a(Random var1) {
      return (WorldGenAbstractTree)(var1.nextInt(5) > 0 ? field_150627_aC : this.field_76757_N);
   }

   protected BiomeGenBase func_180277_d(int var1) {
      BiomeGenSavanna.Mutated var2 = new BiomeGenSavanna.Mutated(var1, this);
      var2.field_76750_F = (this.field_76750_F + 1.0F) * 0.5F;
      var2.field_76748_D = this.field_76748_D * 0.5F + 0.3F;
      var2.field_76749_E = this.field_76749_E * 0.5F + 1.2F;
      return var2;
   }

   public void func_180624_a(World var1, Random var2, BlockPos var3) {
      field_180280_ag.func_180710_a(BlockDoublePlant.EnumPlantType.GRASS);

      for(int var4 = 0; var4 < 7; ++var4) {
         int var5 = var2.nextInt(16) + 8;
         int var6 = var2.nextInt(16) + 8;
         int var7 = var2.nextInt(var1.func_175645_m(var3.func_177982_a(var5, 0, var6)).func_177956_o() + 32);
         field_180280_ag.func_180709_b(var1, var2, var3.func_177982_a(var5, var7, var6));
      }

      super.func_180624_a(var1, var2, var3);
   }

   public static class Mutated extends BiomeGenMutated {
      public Mutated(int var1, BiomeGenBase var2) {
         super(var1, var2);
         this.field_76760_I.field_76832_z = 2;
         this.field_76760_I.field_76802_A = 2;
         this.field_76760_I.field_76803_B = 5;
      }

      public void func_180622_a(World var1, Random var2, ChunkPrimer var3, int var4, int var5, double var6) {
         this.field_76752_A = Blocks.field_150349_c.func_176223_P();
         this.field_76753_B = Blocks.field_150346_d.func_176223_P();
         if (var6 > 1.75D) {
            this.field_76752_A = Blocks.field_150348_b.func_176223_P();
            this.field_76753_B = Blocks.field_150348_b.func_176223_P();
         } else if (var6 > -0.5D) {
            this.field_76752_A = Blocks.field_150346_d.func_176223_P().func_177226_a(BlockDirt.field_176386_a, BlockDirt.DirtType.COARSE_DIRT);
         }

         this.func_180628_b(var1, var2, var3, var4, var5, var6);
      }

      public void func_180624_a(World var1, Random var2, BlockPos var3) {
         this.field_76760_I.func_180292_a(var1, var2, this, var3);
      }
   }
}
