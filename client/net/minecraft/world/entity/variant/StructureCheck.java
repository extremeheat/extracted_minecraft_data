package net.minecraft.world.entity.variant;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;

public record StructureCheck(HolderSet<Structure> requiredStructures) implements SpawnCondition {
   public static final MapCodec<StructureCheck> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(RegistryCodecs.homogeneousList(Registries.STRUCTURE).fieldOf("structures").forGetter(StructureCheck::requiredStructures)).apply(var0, StructureCheck::new));

   public StructureCheck(HolderSet<Structure> var1) {
      super();
      this.requiredStructures = var1;
   }

   public boolean test(SpawnContext var1) {
      return var1.level().getLevel().structureManager().getStructureWithPieceAt(var1.pos(), this.requiredStructures).isValid();
   }

   public MapCodec<StructureCheck> codec() {
      return MAP_CODEC;
   }

   // $FF: synthetic method
   public boolean test(final Object var1) {
      return this.test((SpawnContext)var1);
   }
}
