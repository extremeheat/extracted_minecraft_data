package net.minecraft.world.biome.provider;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.structure.Structure;

public class CheckerboardBiomeProvider extends BiomeProvider {
   private final Biome[] field_205320_b;
   private final int field_205321_c;

   public CheckerboardBiomeProvider(CheckerboardBiomeProviderSettings var1) {
      super();
      this.field_205320_b = var1.func_205432_a();
      this.field_205321_c = var1.func_205433_b() + 4;
   }

   public Biome func_180300_a(BlockPos var1, @Nullable Biome var2) {
      return this.field_205320_b[Math.abs(((var1.func_177958_n() >> this.field_205321_c) + (var1.func_177952_p() >> this.field_205321_c)) % this.field_205320_b.length)];
   }

   public Biome[] func_201535_a(int var1, int var2, int var3, int var4) {
      return this.func_201539_b(var1, var2, var3, var4);
   }

   public Biome[] func_201537_a(int var1, int var2, int var3, int var4, boolean var5) {
      Biome[] var6 = new Biome[var3 * var4];

      for(int var7 = 0; var7 < var4; ++var7) {
         for(int var8 = 0; var8 < var3; ++var8) {
            int var9 = Math.abs(((var1 + var7 >> this.field_205321_c) + (var2 + var8 >> this.field_205321_c)) % this.field_205320_b.length);
            Biome var10 = this.field_205320_b[var9];
            var6[var7 * var3 + var8] = var10;
         }
      }

      return var6;
   }

   @Nullable
   public BlockPos func_180630_a(int var1, int var2, int var3, List<Biome> var4, Random var5) {
      return null;
   }

   public boolean func_205004_a(Structure<?> var1) {
      return (Boolean)this.field_205005_a.computeIfAbsent(var1, (var1x) -> {
         Biome[] var2 = this.field_205320_b;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Biome var5 = var2[var4];
            if (var5.func_201858_a(var1x)) {
               return true;
            }
         }

         return false;
      });
   }

   public Set<IBlockState> func_205706_b() {
      if (this.field_205707_b.isEmpty()) {
         Biome[] var1 = this.field_205320_b;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            Biome var4 = var1[var3];
            this.field_205707_b.add(var4.func_203944_q().func_204108_a());
         }
      }

      return this.field_205707_b;
   }

   public Set<Biome> func_201538_a(int var1, int var2, int var3) {
      return Sets.newHashSet(this.field_205320_b);
   }
}
