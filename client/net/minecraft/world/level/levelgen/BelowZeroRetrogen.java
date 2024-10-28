package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.BitSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.LongStream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;

public final class BelowZeroRetrogen {
   private static final BitSet EMPTY = new BitSet(0);
   private static final Codec<BitSet> BITSET_CODEC;
   private static final Codec<ChunkStatus> NON_EMPTY_CHUNK_STATUS;
   public static final Codec<BelowZeroRetrogen> CODEC;
   private static final Set<ResourceKey<Biome>> RETAINED_RETROGEN_BIOMES;
   public static final LevelHeightAccessor UPGRADE_HEIGHT_ACCESSOR;
   private final ChunkStatus targetStatus;
   private final BitSet missingBedrock;

   private BelowZeroRetrogen(ChunkStatus var1, Optional<BitSet> var2) {
      super();
      this.targetStatus = var1;
      this.missingBedrock = (BitSet)var2.orElse(EMPTY);
   }

   @Nullable
   public static BelowZeroRetrogen read(CompoundTag var0) {
      ChunkStatus var1 = ChunkStatus.byName(var0.getString("target_status"));
      return var1 == ChunkStatus.EMPTY ? null : new BelowZeroRetrogen(var1, Optional.of(BitSet.valueOf(var0.getLongArray("missing_bedrock"))));
   }

   public static void replaceOldBedrock(ProtoChunk var0) {
      boolean var1 = true;
      BlockPos.betweenClosed(0, 0, 0, 15, 4, 15).forEach((var1x) -> {
         if (var0.getBlockState(var1x).is(Blocks.BEDROCK)) {
            var0.setBlockState(var1x, Blocks.DEEPSLATE.defaultBlockState(), false);
         }

      });
   }

   public void applyBedrockMask(ProtoChunk var1) {
      LevelHeightAccessor var2 = var1.getHeightAccessorForGeneration();
      int var3 = var2.getMinBuildHeight();
      int var4 = var2.getMaxBuildHeight() - 1;

      for(int var5 = 0; var5 < 16; ++var5) {
         for(int var6 = 0; var6 < 16; ++var6) {
            if (this.hasBedrockHole(var5, var6)) {
               BlockPos.betweenClosed(var5, var3, var6, var5, var4, var6).forEach((var1x) -> {
                  var1.setBlockState(var1x, Blocks.AIR.defaultBlockState(), false);
               });
            }
         }
      }

   }

   public ChunkStatus targetStatus() {
      return this.targetStatus;
   }

   public boolean hasBedrockHoles() {
      return !this.missingBedrock.isEmpty();
   }

   public boolean hasBedrockHole(int var1, int var2) {
      return this.missingBedrock.get((var2 & 15) * 16 + (var1 & 15));
   }

   public static BiomeResolver getBiomeResolver(BiomeResolver var0, ChunkAccess var1) {
      if (!var1.isUpgrading()) {
         return var0;
      } else {
         Set var10000 = RETAINED_RETROGEN_BIOMES;
         Objects.requireNonNull(var10000);
         Predicate var2 = var10000::contains;
         return (var3, var4, var5, var6) -> {
            Holder var7 = var0.getNoiseBiome(var3, var4, var5, var6);
            return var7.is(var2) ? var7 : var1.getNoiseBiome(var3, 0, var5);
         };
      }
   }

   static {
      BITSET_CODEC = Codec.LONG_STREAM.xmap((var0) -> {
         return BitSet.valueOf(var0.toArray());
      }, (var0) -> {
         return LongStream.of(var0.toLongArray());
      });
      NON_EMPTY_CHUNK_STATUS = BuiltInRegistries.CHUNK_STATUS.byNameCodec().comapFlatMap((var0) -> {
         return var0 == ChunkStatus.EMPTY ? DataResult.error(() -> {
            return "target_status cannot be empty";
         }) : DataResult.success(var0);
      }, Function.identity());
      CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(NON_EMPTY_CHUNK_STATUS.fieldOf("target_status").forGetter(BelowZeroRetrogen::targetStatus), BITSET_CODEC.lenientOptionalFieldOf("missing_bedrock").forGetter((var0x) -> {
            return var0x.missingBedrock.isEmpty() ? Optional.empty() : Optional.of(var0x.missingBedrock);
         })).apply(var0, BelowZeroRetrogen::new);
      });
      RETAINED_RETROGEN_BIOMES = Set.of(Biomes.LUSH_CAVES, Biomes.DRIPSTONE_CAVES, Biomes.DEEP_DARK);
      UPGRADE_HEIGHT_ACCESSOR = new LevelHeightAccessor() {
         public int getHeight() {
            return 64;
         }

         public int getMinBuildHeight() {
            return -64;
         }
      };
   }
}
