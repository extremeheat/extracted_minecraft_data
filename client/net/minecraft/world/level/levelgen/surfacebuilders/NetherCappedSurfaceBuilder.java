package net.minecraft.world.level.levelgen.surfacebuilders;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.serialization.Codec;
import java.util.Comparator;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;

public abstract class NetherCappedSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderBaseConfiguration> {
   private long seed;
   private ImmutableMap<BlockState, PerlinNoise> floorNoises = ImmutableMap.of();
   private ImmutableMap<BlockState, PerlinNoise> ceilingNoises = ImmutableMap.of();
   private PerlinNoise patchNoise;

   public NetherCappedSurfaceBuilder(Codec<SurfaceBuilderBaseConfiguration> var1) {
      super(var1);
   }

   public void apply(Random var1, ChunkAccess var2, Biome var3, int var4, int var5, int var6, double var7, BlockState var9, BlockState var10, int var11, long var12, SurfaceBuilderBaseConfiguration var14) {
      int var15 = var11 + 1;
      int var16 = var4 & 15;
      int var17 = var5 & 15;
      int var18 = (int)(var7 / 3.0D + 3.0D + var1.nextDouble() * 0.25D);
      int var19 = (int)(var7 / 3.0D + 3.0D + var1.nextDouble() * 0.25D);
      double var20 = 0.03125D;
      boolean var22 = this.patchNoise.getValue((double)var4 * 0.03125D, 109.0D, (double)var5 * 0.03125D) * 75.0D + var1.nextDouble() > 0.0D;
      BlockState var23 = (BlockState)((Entry)this.ceilingNoises.entrySet().stream().max(Comparator.comparing((var3x) -> {
         return ((PerlinNoise)var3x.getValue()).getValue((double)var4, (double)var11, (double)var5);
      })).get()).getKey();
      BlockState var24 = (BlockState)((Entry)this.floorNoises.entrySet().stream().max(Comparator.comparing((var3x) -> {
         return ((PerlinNoise)var3x.getValue()).getValue((double)var4, (double)var11, (double)var5);
      })).get()).getKey();
      BlockPos.MutableBlockPos var25 = new BlockPos.MutableBlockPos();
      BlockState var26 = var2.getBlockState(var25.set(var16, 128, var17));

      for(int var27 = 127; var27 >= 0; --var27) {
         var25.set(var16, var27, var17);
         BlockState var28 = var2.getBlockState(var25);
         int var29;
         if (var26.is(var9.getBlock()) && (var28.isAir() || var28 == var10)) {
            for(var29 = 0; var29 < var18; ++var29) {
               var25.move(Direction.UP);
               if (!var2.getBlockState(var25).is(var9.getBlock())) {
                  break;
               }

               var2.setBlockState(var25, var23, false);
            }

            var25.set(var16, var27, var17);
         }

         if ((var26.isAir() || var26 == var10) && var28.is(var9.getBlock())) {
            for(var29 = 0; var29 < var19 && var2.getBlockState(var25).is(var9.getBlock()); ++var29) {
               if (var22 && var27 >= var15 - 4 && var27 <= var15 + 1) {
                  var2.setBlockState(var25, this.getPatchBlockState(), false);
               } else {
                  var2.setBlockState(var25, var24, false);
               }

               var25.move(Direction.DOWN);
            }
         }

         var26 = var28;
      }

   }

   public void initNoise(long var1) {
      if (this.seed != var1 || this.patchNoise == null || this.floorNoises.isEmpty() || this.ceilingNoises.isEmpty()) {
         this.floorNoises = initPerlinNoises(this.getFloorBlockStates(), var1);
         this.ceilingNoises = initPerlinNoises(this.getCeilingBlockStates(), var1 + (long)this.floorNoises.size());
         this.patchNoise = new PerlinNoise(new WorldgenRandom(var1 + (long)this.floorNoises.size() + (long)this.ceilingNoises.size()), ImmutableList.of(0));
      }

      this.seed = var1;
   }

   private static ImmutableMap<BlockState, PerlinNoise> initPerlinNoises(ImmutableList<BlockState> var0, long var1) {
      Builder var3 = new Builder();

      for(UnmodifiableIterator var4 = var0.iterator(); var4.hasNext(); ++var1) {
         BlockState var5 = (BlockState)var4.next();
         var3.put(var5, new PerlinNoise(new WorldgenRandom(var1), ImmutableList.of(-4)));
      }

      return var3.build();
   }

   protected abstract ImmutableList<BlockState> getFloorBlockStates();

   protected abstract ImmutableList<BlockState> getCeilingBlockStates();

   protected abstract BlockState getPatchBlockState();
}
