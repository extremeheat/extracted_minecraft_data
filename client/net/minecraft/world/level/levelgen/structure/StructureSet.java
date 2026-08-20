package net.minecraft.world.level.levelgen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;

public record StructureSet(List<StructureSelectionEntry> structures, StructurePlacement placement) {
   public static final Codec<StructureSet> DIRECT_CODEC = RecordCodecBuilder.create((i) -> i.group(StructureSet.StructureSelectionEntry.CODEC.listOf().fieldOf("structures").forGetter(StructureSet::structures), StructurePlacement.CODEC.fieldOf("placement").forGetter(StructureSet::placement)).apply(i, StructureSet::new));
   public static final Codec<Holder<StructureSet>> CODEC;

   public StructureSet(final Holder<Structure> singleEntry, final StructurePlacement placement) {
      this(List.of(new StructureSelectionEntry(singleEntry, 1)), placement);
   }

   public StructureSet {
      super();
   }

   public static StructureSelectionEntry entry(final Holder<Structure> structure, final int weight) {
      return new StructureSelectionEntry(structure, weight);
   }

   public static StructureSelectionEntry entry(final Holder<Structure> structure) {
      return new StructureSelectionEntry(structure, 1);
   }

   static {
      CODEC = RegistryCodecs.holder(Registries.STRUCTURE_SET, DIRECT_CODEC);
   }

   public static record StructureSelectionEntry(Holder<Structure> structure, int weight) {
      public static final Codec<StructureSelectionEntry> CODEC = RecordCodecBuilder.create((i) -> i.group(Structure.CODEC.fieldOf("structure").forGetter(StructureSelectionEntry::structure), ExtraCodecs.POSITIVE_INT.fieldOf("weight").forGetter(StructureSelectionEntry::weight)).apply(i, StructureSelectionEntry::new));

      public StructureSelectionEntry {
         super();
      }
   }
}
