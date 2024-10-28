package net.minecraft.world.level.levelgen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;

public record StructureSet(List<StructureSelectionEntry> structures, StructurePlacement placement) {
   public static final Codec<StructureSet> DIRECT_CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(StructureSet.StructureSelectionEntry.CODEC.listOf().fieldOf("structures").forGetter(StructureSet::structures), StructurePlacement.CODEC.fieldOf("placement").forGetter(StructureSet::placement)).apply(var0, StructureSet::new);
   });
   public static final Codec<Holder<StructureSet>> CODEC;

   public StructureSet(Holder<Structure> var1, StructurePlacement var2) {
      this(List.of(new StructureSelectionEntry(var1, 1)), var2);
   }

   public StructureSet(List<StructureSelectionEntry> structures, StructurePlacement placement) {
      super();
      this.structures = structures;
      this.placement = placement;
   }

   public static StructureSelectionEntry entry(Holder<Structure> var0, int var1) {
      return new StructureSelectionEntry(var0, var1);
   }

   public static StructureSelectionEntry entry(Holder<Structure> var0) {
      return new StructureSelectionEntry(var0, 1);
   }

   public List<StructureSelectionEntry> structures() {
      return this.structures;
   }

   public StructurePlacement placement() {
      return this.placement;
   }

   static {
      CODEC = RegistryFileCodec.create(Registries.STRUCTURE_SET, DIRECT_CODEC);
   }

   public static record StructureSelectionEntry(Holder<Structure> structure, int weight) {
      public static final Codec<StructureSelectionEntry> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(Structure.CODEC.fieldOf("structure").forGetter(StructureSelectionEntry::structure), ExtraCodecs.POSITIVE_INT.fieldOf("weight").forGetter(StructureSelectionEntry::weight)).apply(var0, StructureSelectionEntry::new);
      });

      public StructureSelectionEntry(Holder<Structure> structure, int weight) {
         super();
         this.structure = structure;
         this.weight = weight;
      }

      public Holder<Structure> structure() {
         return this.structure;
      }

      public int weight() {
         return this.weight;
      }
   }
}
