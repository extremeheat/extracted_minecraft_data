package net.minecraft.world.level.levelgen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.material.Fluids;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class NetherWorldCarver extends CaveWorldCarver {
   public NetherWorldCarver(Codec<CaveCarverConfiguration> var1) {
      super(var1);
      this.liquids = ImmutableSet.of(Fluids.LAVA, Fluids.WATER);
   }

   @Override
   protected int getCaveBound() {
      return 10;
   }

   @Override
   protected float getThickness(RandomSource var1) {
      return (var1.nextFloat() * 2.0F + var1.nextFloat()) * 2.0F;
   }

   @Override
   protected double getYScale() {
      return 5.0;
   }

   protected boolean carveBlock(
      CarvingContext var1,
      CaveCarverConfiguration var2,
      ChunkAccess var3,
      Function<BlockPos, Holder<Biome>> var4,
      CarvingMask var5,
      BlockPos.MutableBlockPos var6,
      BlockPos.MutableBlockPos var7,
      Aquifer var8,
      MutableBoolean var9
   ) {
      if (this.canReplaceBlock(var2, var3.getBlockState(var6))) {
         BlockState var10;
         if (var6.getY() <= var1.getMinGenY() + 31) {
            var10 = LAVA.createLegacyBlock();
         } else {
            var10 = CAVE_AIR;
         }

         var3.setBlockState(var6, var10, false);
         return true;
      } else {
         return false;
      }
   }
}
