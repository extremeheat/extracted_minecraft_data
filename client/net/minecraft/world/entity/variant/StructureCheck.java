package net.minecraft.world.entity.variant;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.world.level.levelgen.structure.Structure;

public record StructureCheck(HolderSet<Structure> requiredStructures) implements SpawnCondition {
   public static final MapCodec<StructureCheck> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(RegistryCodecs.holderSet(Registries.STRUCTURE).fieldOf("structures").forGetter(StructureCheck::requiredStructures)).apply(i, StructureCheck::new));

   public StructureCheck {
      super();
   }

   public boolean test(final SpawnContext context) {
      return context.level().getLevel().structureManager().getStructureWithPieceAt(context.pos(), this.requiredStructures).isValid();
   }

   public MapCodec<StructureCheck> codec() {
      return MAP_CODEC;
   }
}
