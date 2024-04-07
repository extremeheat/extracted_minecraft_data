package net.minecraft.world.level.levelgen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;

public record StructureSet(List<StructureSet.StructureSelectionEntry> structures, StructurePlacement placement) {
   public static final Codec<StructureSet> DIRECT_CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               StructureSet.StructureSelectionEntry.CODEC.listOf().fieldOf("structures").forGetter(StructureSet::structures),
               StructurePlacement.CODEC.fieldOf("placement").forGetter(StructureSet::placement)
            )
            .apply(var0, StructureSet::new)
   );
   public static final Codec<Holder<StructureSet>> CODEC = RegistryFileCodec.create(Registries.STRUCTURE_SET, DIRECT_CODEC);

   public StructureSet(Holder<Structure> var1, StructurePlacement var2) {
      this(List.of(new StructureSet.StructureSelectionEntry(var1, 1)), var2);
   }

   public StructureSet(List<StructureSet.StructureSelectionEntry> structures, StructurePlacement placement) {
      super();
      this.structures = structures;
      this.placement = placement;
   }

   public static StructureSet.StructureSelectionEntry entry(Holder<Structure> var0, int var1) {
      return new StructureSet.StructureSelectionEntry(var0, var1);
   }

   public static StructureSet.StructureSelectionEntry entry(Holder<Structure> var0) {
      return new StructureSet.StructureSelectionEntry(var0, 1);
   }

   public static record StructureSelectionEntry(Holder<Structure> structure, int weight) {
      public static final Codec<StructureSet.StructureSelectionEntry> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  Structure.CODEC.fieldOf("structure").forGetter(StructureSet.StructureSelectionEntry::structure),
                  ExtraCodecs.POSITIVE_INT.fieldOf("weight").forGetter(StructureSet.StructureSelectionEntry::weight)
               )
               .apply(var0, StructureSet.StructureSelectionEntry::new)
      );

      public StructureSelectionEntry(Holder<Structure> structure, int weight) {
         super();
         this.structure = structure;
         this.weight = weight;
      }
   }
}
