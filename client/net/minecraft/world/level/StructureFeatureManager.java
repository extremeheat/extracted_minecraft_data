package net.minecraft.world.level;

import com.mojang.datafixers.DataFixUtils;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.FeatureAccess;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.StructureStart;

public class StructureFeatureManager {
   private final LevelAccessor level;
   private final WorldGenSettings worldGenSettings;

   public StructureFeatureManager(LevelAccessor var1, WorldGenSettings var2) {
      super();
      this.level = var1;
      this.worldGenSettings = var2;
   }

   public StructureFeatureManager forWorldGenRegion(WorldGenRegion var1) {
      if (var1.getLevel() != this.level) {
         throw new IllegalStateException("Using invalid feature manager (source level: " + var1.getLevel() + ", region: " + var1);
      } else {
         return new StructureFeatureManager(var1, this.worldGenSettings);
      }
   }

   public Stream<? extends StructureStart<?>> startsForFeature(SectionPos var1, StructureFeature<?> var2) {
      return this.level.getChunk(var1.x(), var1.z(), ChunkStatus.STRUCTURE_REFERENCES).getReferencesForFeature(var2).stream().map((var0) -> {
         return SectionPos.of(new ChunkPos(var0), 0);
      }).map((var2x) -> {
         return this.getStartForFeature(var2x, var2, this.level.getChunk(var2x.x(), var2x.z(), ChunkStatus.STRUCTURE_STARTS));
      }).filter((var0) -> {
         return var0 != null && var0.isValid();
      });
   }

   @Nullable
   public StructureStart<?> getStartForFeature(SectionPos var1, StructureFeature<?> var2, FeatureAccess var3) {
      return var3.getStartForFeature(var2);
   }

   public void setStartForFeature(SectionPos var1, StructureFeature<?> var2, StructureStart<?> var3, FeatureAccess var4) {
      var4.setStartForFeature(var2, var3);
   }

   public void addReferenceForFeature(SectionPos var1, StructureFeature<?> var2, long var3, FeatureAccess var5) {
      var5.addReferenceForFeature(var2, var3);
   }

   public boolean shouldGenerateFeatures() {
      return this.worldGenSettings.generateFeatures();
   }

   public StructureStart<?> getStructureAt(BlockPos var1, boolean var2, StructureFeature<?> var3) {
      return (StructureStart)DataFixUtils.orElse(this.startsForFeature(SectionPos.of(var1), var3).filter((var1x) -> {
         return var1x.getBoundingBox().isInside(var1);
      }).filter((var2x) -> {
         return !var2 || var2x.getPieces().stream().anyMatch((var1x) -> {
            return var1x.getBoundingBox().isInside(var1);
         });
      }).findFirst(), StructureStart.INVALID_START);
   }
}
