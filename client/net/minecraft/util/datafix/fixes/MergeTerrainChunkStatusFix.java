package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.util.datafix.schemas.NamespacedSchema;
import org.jspecify.annotations.Nullable;

public class MergeTerrainChunkStatusFix extends DataFix {
   private static final String BEFORE_TERRAIN_STATUS = "minecraft:biomes";
   private static final String OLD_FINAL_TERRAIN_STATUS = "minecraft:carvers";
   private static final Set<String> OLD_INTERMEDIATE_TERRAIN_STATUSES = Set.of("minecraft:noise", "minecraft:surface");

   public MergeTerrainChunkStatusFix(final Schema schema) {
      super(schema, false);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> chunkType = this.getInputSchema().getType(References.CHUNK);
      OpticFinder<?> sectionsF = chunkType.findField("sections");
      Type<?> sectionType = ((List.ListType)sectionsF.type()).getElement();
      Type<?> biomesType = sectionType.findFieldType("biomes");
      Type<?> blockStatesType = sectionType.findFieldType("block_states");
      return this.makeRule(chunkType, sectionsF, sectionType, biomesType, blockStatesType);
   }

   private <Biomes, BlockStates> TypeRewriteRule makeRule(final Type<?> chunkType, final OpticFinder<?> sectionsF, final Type<?> sectionType, final Type<Biomes> biomesType, final Type<BlockStates> blockStatesType) {
      Type<Pair<Either<Biomes, Unit>, Pair<Either<BlockStates, Unit>, Dynamic<?>>>> expectedSectionType = DSL.and(DSL.optional(DSL.field("biomes", biomesType)), DSL.optional(DSL.field("block_states", blockStatesType)), DSL.remainderType());
      if (!Objects.equals(sectionType, expectedSectionType)) {
         String var10002 = String.valueOf(sectionType);
         throw new IllegalStateException(var10002 + " did not match " + String.valueOf(expectedSectionType));
      } else {
         OpticFinder<Pair<Either<Biomes, Unit>, Pair<Either<BlockStates, Unit>, Dynamic<?>>>> sectionF = DSL.typeFinder(expectedSectionType);
         return this.fixTypeEverywhereTyped("MergeTerrainChunkStatusFix", chunkType, (chunk) -> {
            FixedAndStatuses statuses = this.fixStatuses(chunk);
            chunk = statuses.fixed;
            boolean statusIntermediate = OLD_INTERMEDIATE_TERRAIN_STATUSES.contains(statuses.oldStatus);
            boolean targetStatusIntermediate = statuses.oldTargetStatus != null && OLD_INTERMEDIATE_TERRAIN_STATUSES.contains(statuses.oldTargetStatus);
            if (statusIntermediate || targetStatusIntermediate) {
               boolean onlyRemoveBlocksBelowZero = statuses.oldTargetStatus != null && !targetStatusIntermediate;
               chunk = removeBlockStates(chunk, sectionsF, sectionF, onlyRemoveBlocksBelowZero);
            }

            return chunk;
         });
      }
   }

   private static <Biomes, BlockStates> Typed<?> removeBlockStates(final Typed<?> chunk, final OpticFinder<?> sectionsF, final OpticFinder<Pair<Either<Biomes, Unit>, Pair<Either<BlockStates, Unit>, Dynamic<?>>>> sectionF, final boolean onlyBelowZero) {
      return chunk.update(DSL.remainderFinder(), (remainder) -> remainder.remove("Heightmaps")).updateTyped(sectionsF, (sections) -> sections.update(sectionF, (section) -> {
            byte y = ((Dynamic)((Pair)section.getSecond()).getSecond()).get("Y").asByte((byte)0);
            return y >= 0 && onlyBelowZero ? section : section.mapSecond((blockStatesAndRemainder) -> blockStatesAndRemainder.mapFirst((var0) -> Either.right(Unit.INSTANCE)).mapSecond((sectionRemainder) -> sectionRemainder.remove("block_states")));
         }));
   }

   private FixedAndStatuses fixStatuses(final Typed<?> chunk) {
      Dynamic<?> remainder = (Dynamic)chunk.getOrCreate(DSL.remainderFinder());
      String oldStatus = unwrapStatus(remainder.get("Status"));
      remainder = remainder.set("Status", remainder.createString(fixStatus(oldStatus)));
      Optional<? extends Dynamic<?>> maybeBelowZeroRetrogen = remainder.get("below_zero_retrogen").result();
      String oldTargetStatus;
      if (maybeBelowZeroRetrogen.isPresent()) {
         Dynamic<?> belowZeroRetrogen = (Dynamic)maybeBelowZeroRetrogen.get();
         oldTargetStatus = unwrapStatus(belowZeroRetrogen.get("target_status"));
         belowZeroRetrogen = belowZeroRetrogen.set("target_status", remainder.createString(fixStatus(oldTargetStatus)));
         remainder = remainder.set("below_zero_retrogen", belowZeroRetrogen);
      } else {
         oldTargetStatus = null;
      }

      return new FixedAndStatuses(chunk.set(DSL.remainderFinder(), remainder), oldStatus, oldTargetStatus);
   }

   private static String unwrapStatus(final OptionalDynamic<?> status) {
      return NamespacedSchema.ensureNamespaced(status.asString("minecraft:empty"));
   }

   private static String fixStatus(final String status) {
      if ("minecraft:carvers".equals(status)) {
         return "minecraft:terrain";
      } else {
         return OLD_INTERMEDIATE_TERRAIN_STATUSES.contains(status) ? "minecraft:biomes" : status;
      }
   }

   private static record FixedAndStatuses(Typed<?> fixed, String oldStatus, @Nullable String oldTargetStatus) {
      private FixedAndStatuses {
         super();
      }
   }
}
