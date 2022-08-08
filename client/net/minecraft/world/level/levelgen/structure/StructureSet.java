package net.minecraft.world.level.levelgen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;

public record StructureSet(List<StructureSelectionEntry> c, StructurePlacement d) {
   private final List<StructureSelectionEntry> structures;
   private final StructurePlacement placement;
   public static final Codec<StructureSet> DIRECT_CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(StructureSet.StructureSelectionEntry.CODEC.listOf().fieldOf("structures").forGetter(StructureSet::structures), StructurePlacement.CODEC.fieldOf("placement").forGetter(StructureSet::placement)).apply(var0, StructureSet::new);
   });
   public static final Codec<Holder<StructureSet>> CODEC;

   public StructureSet(Holder<Structure> var1, StructurePlacement var2) {
      this(List.of(new StructureSelectionEntry(var1, 1)), var2);
   }

   public StructureSet(List<StructureSelectionEntry> var1, StructurePlacement var2) {
      super();
      this.structures = var1;
      this.placement = var2;
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
      CODEC = RegistryFileCodec.create(Registry.STRUCTURE_SET_REGISTRY, DIRECT_CODEC);
   }

   public static record StructureSelectionEntry(Holder<Structure> b, int c) {
      private final Holder<Structure> structure;
      private final int weight;
      public static final Codec<StructureSelectionEntry> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(Structure.CODEC.fieldOf("structure").forGetter(StructureSelectionEntry::structure), ExtraCodecs.POSITIVE_INT.fieldOf("weight").forGetter(StructureSelectionEntry::weight)).apply(var0, StructureSelectionEntry::new);
      });

      public StructureSelectionEntry(Holder<Structure> var1, int var2) {
         super();
         this.structure = var1;
         this.weight = var2;
      }

      public Holder<Structure> structure() {
         return this.structure;
      }

      public int weight() {
         return this.weight;
      }
   }
}
