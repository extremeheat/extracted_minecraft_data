package net.minecraft.world.level;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.chunk.StructureAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureCheck;
import net.minecraft.world.level.levelgen.structure.StructureCheckResult;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import org.jspecify.annotations.Nullable;

public class StructureManager {
   private final LevelAccessor level;
   private final WorldOptions worldOptions;
   private final StructureCheck structureCheck;

   public StructureManager(final LevelAccessor level, final WorldOptions worldOptions, final StructureCheck structureCheck) {
      super();
      this.level = level;
      this.worldOptions = worldOptions;
      this.structureCheck = structureCheck;
   }

   public StructureManager forWorldGenRegion(final WorldGenRegion region) {
      if (region.getLevel() != this.level) {
         String var10002 = String.valueOf(region.getLevel());
         throw new IllegalStateException("Using invalid structure manager (source level: " + var10002 + ", region: " + String.valueOf(region));
      } else {
         return new StructureManager(region, this.worldOptions, this.structureCheck);
      }
   }

   public List<StructureStart> startsForStructure(final ChunkPos pos, final Predicate<Structure> matcher) {
      Map<Structure, LongSet> allReferences = this.level.getChunk(pos.x(), pos.z(), ChunkStatus.STRUCTURE_REFERENCES).getAllReferences();
      ImmutableList.Builder<StructureStart> result = ImmutableList.builder();

      for(Map.Entry<Structure, LongSet> entry : allReferences.entrySet()) {
         Structure structure = (Structure)entry.getKey();
         if (matcher.test(structure)) {
            LongSet var10002 = (LongSet)entry.getValue();
            Objects.requireNonNull(result);
            this.fillStartsForStructure(structure, var10002, result::add);
         }
      }

      return result.build();
   }

   public List<StructureStart> startsForStructure(final SectionPos pos, final Structure structure) {
      LongSet referencesForStructure = this.level.getChunk(pos.x(), pos.z(), ChunkStatus.STRUCTURE_REFERENCES).getReferencesForStructure(structure);
      ImmutableList.Builder<StructureStart> result = ImmutableList.builder();
      Objects.requireNonNull(result);
      this.fillStartsForStructure(structure, referencesForStructure, result::add);
      return result.build();
   }

   public void fillStartsForStructure(final Structure structure, final LongSet referencesForStructure, final Consumer<StructureStart> consumer) {
      LongIterator var4 = referencesForStructure.iterator();

      while(var4.hasNext()) {
         long key = (Long)var4.next();
         SectionPos sectionPos = SectionPos.of(ChunkPos.unpack(key), this.level.getMinSectionY());
         StructureStart start = this.getStartForStructure(sectionPos, structure, this.level.getChunk(sectionPos.x(), sectionPos.z(), ChunkStatus.STRUCTURE_STARTS));
         if (start != null && start.isValid()) {
            consumer.accept(start);
         }
      }

   }

   public @Nullable StructureStart getStartForStructure(final SectionPos pos, final Structure structure, final StructureAccess chunk) {
      return chunk.getStartForStructure(structure);
   }

   public void setStartForStructure(final SectionPos pos, final Structure structure, final StructureStart start, final StructureAccess chunk) {
      chunk.setStartForStructure(structure, start);
   }

   public void addReferenceForStructure(final SectionPos pos, final Structure structure, final long reference, final StructureAccess chunk) {
      chunk.addReferenceForStructure(structure, reference);
   }

   public boolean shouldGenerateStructures() {
      return this.worldOptions.generateStructures();
   }

   public StructureStart getStructureAt(final BlockPos blockPos, final Structure structure) {
      for(StructureStart structureStart : this.startsForStructure(SectionPos.of(blockPos), structure)) {
         if (structureStart.getBoundingBox().isInside(blockPos)) {
            return structureStart;
         }
      }

      return StructureStart.INVALID_START;
   }

   public StructureStart getStructureWithPieceAt(final BlockPos blockPos, final TagKey<Structure> structureTag) {
      return this.getStructureWithPieceAt(blockPos, (Predicate)((structure) -> structure.is(structureTag)));
   }

   public StructureStart getStructureWithPieceAt(final BlockPos blockPos, final HolderSet<Structure> structures) {
      Objects.requireNonNull(structures);
      return this.getStructureWithPieceAt(blockPos, structures::contains);
   }

   public StructureStart getStructureWithPieceAt(final BlockPos blockPos, final Predicate<Holder<Structure>> predicate) {
      Registry<Structure> structures = this.registryAccess().lookupOrThrow(Registries.STRUCTURE);

      for(StructureStart structureStart : this.startsForStructure((ChunkPos)ChunkPos.containing(blockPos), (Predicate)((s) -> {
         Optional var10000 = structures.get(structures.getId(s));
         Objects.requireNonNull(predicate);
         return (Boolean)var10000.map(predicate::test).orElse(false);
      }))) {
         if (this.structureHasPieceAt(blockPos, structureStart)) {
            return structureStart;
         }
      }

      return StructureStart.INVALID_START;
   }

   public StructureStart getStructureWithPieceAt(final BlockPos blockPos, final Structure structure) {
      for(StructureStart structureStart : this.startsForStructure(SectionPos.of(blockPos), structure)) {
         if (this.structureHasPieceAt(blockPos, structureStart)) {
            return structureStart;
         }
      }

      return StructureStart.INVALID_START;
   }

   public boolean structureHasPieceAt(final BlockPos blockPos, final StructureStart structureStart) {
      for(StructurePiece piece : structureStart.getPieces()) {
         if (piece.getBoundingBox().isInside(blockPos)) {
            return true;
         }
      }

      return false;
   }

   public boolean hasAnyStructureAt(final BlockPos pos) {
      SectionPos sectionPos = SectionPos.of(pos);
      return this.level.getChunk(sectionPos.x(), sectionPos.z(), ChunkStatus.STRUCTURE_REFERENCES).hasAnyStructureReferences();
   }

   public Map<Structure, LongSet> getAllStructuresAt(final BlockPos pos) {
      SectionPos sectionPos = SectionPos.of(pos);
      return this.level.getChunk(sectionPos.x(), sectionPos.z(), ChunkStatus.STRUCTURE_REFERENCES).getAllReferences();
   }

   public StructureCheckResult checkStructurePresence(final ChunkPos pos, final Structure structure, final StructurePlacement placement, final boolean createReference) {
      return this.structureCheck.checkStart(pos, structure, placement, createReference);
   }

   public void addReference(final StructureStart start) {
      start.addReference();
      this.structureCheck.incrementReference(start.getChunkPos(), start.getStructure());
   }

   public RegistryAccess registryAccess() {
      return this.level.registryAccess();
   }
}
