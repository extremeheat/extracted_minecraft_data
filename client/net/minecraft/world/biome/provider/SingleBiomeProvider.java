package net.minecraft.world.biome.provider;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.structure.Structure;

public class SingleBiomeProvider extends BiomeProvider {
   private final Biome field_76947_d;

   public SingleBiomeProvider(SingleBiomeProviderSettings var1) {
      super();
      this.field_76947_d = var1.func_205437_a();
   }

   public Biome func_180300_a(BlockPos var1, @Nullable Biome var2) {
      return this.field_76947_d;
   }

   public Biome[] func_201535_a(int var1, int var2, int var3, int var4) {
      return this.func_201539_b(var1, var2, var3, var4);
   }

   public Biome[] func_201537_a(int var1, int var2, int var3, int var4, boolean var5) {
      Biome[] var6 = new Biome[var3 * var4];
      Arrays.fill(var6, 0, var3 * var4, this.field_76947_d);
      return var6;
   }

   @Nullable
   public BlockPos func_180630_a(int var1, int var2, int var3, List<Biome> var4, Random var5) {
      return var4.contains(this.field_76947_d) ? new BlockPos(var1 - var3 + var5.nextInt(var3 * 2 + 1), 0, var2 - var3 + var5.nextInt(var3 * 2 + 1)) : null;
   }

   public boolean func_205004_a(Structure<?> var1) {
      Map var10000 = this.field_205005_a;
      Biome var10002 = this.field_76947_d;
      var10002.getClass();
      return (Boolean)var10000.computeIfAbsent(var1, var10002::func_201858_a);
   }

   public Set<IBlockState> func_205706_b() {
      if (this.field_205707_b.isEmpty()) {
         this.field_205707_b.add(this.field_76947_d.func_203944_q().func_204108_a());
      }

      return this.field_205707_b;
   }

   public Set<Biome> func_201538_a(int var1, int var2, int var3) {
      return Sets.newHashSet(new Biome[]{this.field_76947_d});
   }
}
