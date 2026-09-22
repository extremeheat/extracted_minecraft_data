package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Dynamic;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class MoveNoiseBiomesFix extends DataFix {
   private static final Set<String> STATUSES_WITHOUT_BIOMES = Set.of("minecraft:empty", "minecraft:structure_starts", "minecraft:structure_references", "minecraft:noise_biomes");
   private static final List<String> ALL_STATUSES = List.of("minecraft:empty", "minecraft:structure_starts", "minecraft:structure_references", "minecraft:noise_biomes", "minecraft:biomes", "minecraft:terrain", "minecraft:features", "minecraft:initialize_light", "minecraft:light", "minecraft:spawn", "minecraft:full");

   public MoveNoiseBiomesFix(final Schema schema) {
      super(schema, true);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> chunkType = this.getInputSchema().getType(References.CHUNK);
      OpticFinder<?> sectionsF = chunkType.findField("sections");
      Type<?> sectionType = ((com.mojang.datafixers.types.templates.List.ListType)sectionsF.type()).getElement();
      Type<?> biomesType = sectionType.findFieldType("biomes");
      Type<?> blockStatesType = sectionType.findFieldType("block_states");
      Type<?> chunkTypeOut = this.getOutputSchema().getType(References.CHUNK);
      Type<?> sectionsTypeOut = chunkTypeOut.findFieldType("sections");
      Type<?> sectionTypeOut = ((com.mojang.datafixers.types.templates.List.ListType)sectionsTypeOut).getElement();
      return this.makeRule(chunkType, sectionsF, sectionType, biomesType, blockStatesType, chunkTypeOut, sectionsTypeOut, sectionTypeOut);
   }

   private <Biomes, BlockStates> TypeRewriteRule makeRule(final Type<?> chunkType, final OpticFinder<?> sectionsF, final Type<?> sectionType, final Type<Biomes> biomesType, final Type<BlockStates> blockStatesType, final Type<?> chunkTypeOut, final Type<?> sectionsTypeOut, final Type<?> sectionTypeOut) {
      Type<Pair<Either<Biomes, Unit>, Pair<Either<BlockStates, Unit>, Dynamic<?>>>> expectedSectionType = DSL.and(DSL.optional(DSL.field("biomes", biomesType)), DSL.optional(DSL.field("block_states", blockStatesType)), DSL.remainderType());
      if (!Objects.equals(sectionType, expectedSectionType)) {
         String var12 = String.valueOf(sectionType);
         throw new IllegalStateException(var12 + " did not match " + String.valueOf(expectedSectionType));
      } else {
         OpticFinder<Pair<Either<Biomes, Unit>, Pair<Either<BlockStates, Unit>, Dynamic<?>>>> sectionF = DSL.typeFinder(expectedSectionType);
         Type<Pair<Either<Biomes, Unit>, Pair<Either<Biomes, Unit>, Pair<Either<BlockStates, Unit>, Dynamic<?>>>>> expectedSectionTypeOut = DSL.and(DSL.optional(DSL.field("biomes", biomesType)), DSL.optional(DSL.field("noise_biomes", biomesType)), DSL.optional(DSL.field("block_states", blockStatesType)), DSL.remainderType());
         if (!Objects.equals(sectionTypeOut, expectedSectionTypeOut)) {
            String var10002 = String.valueOf(sectionType);
            throw new IllegalStateException(var10002 + " did not match " + String.valueOf(expectedSectionType));
         } else {
            return this.fixTypeEverywhereTyped("MoveNoiseBiomeFix", chunkType, chunkTypeOut, (chunk) -> {
               chunk = moveBiomes(chunk, sectionsF, sectionF, sectionsTypeOut, expectedSectionTypeOut);
               chunk = this.updateChunkStatusAndRetrogen(chunk);
               return chunk;
            });
         }
      }
   }

   private static <Biomes, BlockStates> Typed<?> moveBiomes(final Typed<?> chunk, final OpticFinder<?> sectionsF, final OpticFinder<Pair<Either<Biomes, Unit>, Pair<Either<BlockStates, Unit>, Dynamic<?>>>> sectionF, final Type<?> sectionsTypeOut, final Type<Pair<Either<Biomes, Unit>, Pair<Either<Biomes, Unit>, Pair<Either<BlockStates, Unit>, Dynamic<?>>>>> sectionTypeOut) {
      return chunk.updateTyped(sectionsF, sectionsTypeOut, (sections) -> sections.update(sectionF, sectionTypeOut, (section) -> {
            Either<Biomes, Unit> emptyBiomes = Either.right(Unit.INSTANCE);
            Either<Biomes, Unit> noiseBiomes = (Either)section.getFirst();
            Pair<Either<BlockStates, Unit>, Dynamic<?>> blockStatesAndRemainder = ((Pair)section.getSecond()).mapSecond((sectionRemainder) -> sectionRemainder.remove("biomes"));
            return new Pair(emptyBiomes, new Pair(noiseBiomes, blockStatesAndRemainder));
         }));
   }

   private Typed<?> updateChunkStatusAndRetrogen(final Typed<?> chunk) {
      return chunk.update(DSL.remainderFinder(), (tag) -> {
         tag = tag.renameAndFixField("Status", "status", MoveNoiseBiomesFix::renameBiomesToNoiseBiomes);
         tag = tag.renameField("below_zero_retrogen", "retrogen");
         String status = NamespacedSchema.ensureNamespaced(tag.get("status").asString("minecraft:empty"));
         Optional<? extends Dynamic<?>> previousRetroGen = tag.get("retrogen").result();
         boolean needsBiomesFixing = !STATUSES_WITHOUT_BIOMES.contains(status);
         if (previousRetroGen.isPresent()) {
            Dynamic<?> retroGen = (Dynamic)previousRetroGen.get();
            String targetStatus = NamespacedSchema.ensureNamespaced(retroGen.get("target_status").asString("minecraft:empty"));
            List<String> statusesToRerun = collectStatusesToRerun(status, targetStatus, needsBiomesFixing);
            if (needsBiomesFixing) {
               tag = tag.set("status", tag.createString("minecraft:noise_biomes"));
            }

            Stream var10005 = statusesToRerun.stream();
            Objects.requireNonNull(tag);
            tag = tag.set("retrogen", retroGen.set("statuses_to_rerun", tag.createList(var10005.map(tag::createString))).set("has_below_zero_retrogen", tag.createBoolean(true)));
         } else if (needsBiomesFixing) {
            tag = tag.set("status", tag.createString("minecraft:noise_biomes")).set("retrogen", tag.createMap(Map.of(tag.createString("statuses_to_rerun"), tag.createList(Stream.of(tag.createString("minecraft:biomes"))), tag.createString("target_status"), tag.createString(status))));
         }

         return tag;
      });
   }

   private static Dynamic<?> renameBiomesToNoiseBiomes(final Dynamic<?> status) {
      return NamespacedSchema.ensureNamespaced(status.asString("")).equals("minecraft:biomes") ? status.createString("minecraft:noise_biomes") : status;
   }

   private static List<String> collectStatusesToRerun(final String status, final String targetStatus, final boolean alwaysIncludeBiomes) {
      List<String> statusesToRerun = new ArrayList();
      boolean collect = false;

      for(String rerunStatus : ALL_STATUSES) {
         if (collect || alwaysIncludeBiomes && rerunStatus.equals("minecraft:biomes")) {
            statusesToRerun.add(rerunStatus);
         }

         if (rerunStatus.equals(targetStatus)) {
            break;
         }

         if (rerunStatus.equals(status)) {
            collect = true;
         }
      }

      return statusesToRerun;
   }
}
