package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.BitSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.LongStream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.NoiseBiomeResolver;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;

public final class RetroGen {
   private static final BitSet EMPTY = new BitSet(0);
   private static final Codec<BitSet> BITSET_CODEC;
   private static final Codec<ChunkStatus> NON_EMPTY_CHUNK_STATUS;
   public static final Codec<RetroGen> CODEC;
   public static final LevelHeightAccessor UPGRADE_HEIGHT_ACCESSOR;
   private final ChunkStatus targetStatus;
   private final BitSet missingBedrock;
   private final List<ChunkStatus> statusesToRerun;
   private final boolean hasBelowZeroRetroGen;

   private RetroGen(final ChunkStatus targetStatus, final List<ChunkStatus> statusesToRerun, final boolean hasBelowZeroRetroGen, final Optional<BitSet> missingBedrock) {
      super();
      this.targetStatus = targetStatus;
      this.missingBedrock = (BitSet)missingBedrock.orElse(EMPTY);
      this.statusesToRerun = statusesToRerun;
      this.hasBelowZeroRetroGen = hasBelowZeroRetroGen;
   }

   public static void replaceOldBedrock(final ProtoChunk chunk) {
      int maxGeneratedBedrockY = 4;
      BlockPos.betweenClosed(0, 0, 0, 15, 4, 15).forEach((pos) -> {
         if (chunk.getBlockState(pos).is(Blocks.BEDROCK)) {
            chunk.setBlockState(pos, Blocks.DEEPSLATE.defaultBlockState());
         }

      });
   }

   public void applyBedrockMask(final ProtoChunk chunk) {
      LevelHeightAccessor heightAccessor = chunk.getHeightAccessorForGeneration();
      int minY = heightAccessor.getMinY();
      int maxY = heightAccessor.getMaxY();

      for(int x = 0; x < 16; ++x) {
         for(int z = 0; z < 16; ++z) {
            if (this.hasBedrockHole(x, z)) {
               BlockPos.betweenClosed(x, minY, z, x, maxY, z).forEach((pos) -> chunk.setBlockState(pos, Blocks.AIR.defaultBlockState()));
            }
         }
      }

   }

   public ChunkStatus targetStatus() {
      return this.targetStatus;
   }

   public ChunkStatus getNextStatus(ChunkStatus status) {
      while(!status.isOrAfter(this.targetStatus)) {
         ChunkStatus nextStatus = status.getNext();
         if (nextStatus == null) {
            return status;
         }

         if (this.statusesToRerun.contains(nextStatus)) {
            return status;
         }

         status = nextStatus;
      }

      return this.targetStatus;
   }

   private List<ChunkStatus> getStatusesToRerun() {
      return this.statusesToRerun;
   }

   public boolean hasBedrockHoles() {
      return !this.missingBedrock.isEmpty();
   }

   public boolean hasBedrockHole(final int x, final int z) {
      return this.missingBedrock.get((z & 15) * 16 + (x & 15));
   }

   public static NoiseBiomeResolver getBiomeResolver(final NoiseBiomeResolver noiseBiomeResolver, final ProtoChunk protoChunk) {
      if (!protoChunk.isUpgrading()) {
         return noiseBiomeResolver;
      } else {
         NoiseBiomeResolver oldBiomeResolver = protoChunk.getNoiseBiomeChunk();
         return oldBiomeResolver == null ? noiseBiomeResolver : (quartX, quartY, quartZ) -> {
            Holder<Biome> noiseBiome = noiseBiomeResolver.getNoiseBiome(quartX, quartY, quartZ);
            return noiseBiome.is(BiomeTags.GENERATED_IN_BELOW_ZERO_RETROGEN) ? noiseBiome : oldBiomeResolver.getNoiseBiome(quartX, 0, quartZ);
         };
      }
   }

   public boolean keepsLight() {
      return this.targetStatus.isOrAfter(ChunkStatus.INITIALIZE_LIGHT) && !this.statusesToRerun.contains(ChunkStatus.INITIALIZE_LIGHT);
   }

   private boolean hasBelowZeroRetroGen() {
      return this.hasBelowZeroRetroGen;
   }

   static {
      BITSET_CODEC = Codec.LONG_STREAM.xmap((longStream) -> BitSet.valueOf(longStream.toArray()), (bitSet) -> LongStream.of(bitSet.toLongArray()));
      NON_EMPTY_CHUNK_STATUS = BuiltInRegistries.CHUNK_STATUS.byNameCodec().comapFlatMap((status) -> status == ChunkStatus.EMPTY ? DataResult.error(() -> "target_status cannot be empty") : DataResult.success(status), Function.identity());
      CODEC = RecordCodecBuilder.create((i) -> i.group(NON_EMPTY_CHUNK_STATUS.fieldOf("target_status").forGetter(RetroGen::targetStatus), NON_EMPTY_CHUNK_STATUS.listOf().fieldOf("statuses_to_rerun").forGetter(RetroGen::getStatusesToRerun), Codec.BOOL.optionalFieldOf("has_below_zero_retrogen", false).forGetter(RetroGen::hasBelowZeroRetroGen), BITSET_CODEC.lenientOptionalFieldOf("missing_bedrock").forGetter((b) -> b.missingBedrock.isEmpty() ? Optional.empty() : Optional.of(b.missingBedrock))).apply(i, RetroGen::new));
      UPGRADE_HEIGHT_ACCESSOR = new LevelHeightAccessor() {
         public int getHeight() {
            return 64;
         }

         public int getMinY() {
            return -64;
         }
      };
   }
}
