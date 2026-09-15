package net.minecraft.world.level.levelgen.structure.placement;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;

public interface StructurePlacements {
   static MapCodec<? extends StructurePlacement> bootstrap(final Registry<MapCodec<? extends StructurePlacement>> registry) {
      Registry.register(registry, (String)"concentric_rings", ConcentricRingsStructurePlacement.CODEC);
      Registry.register(registry, (String)"dimension_origin", DimensionOriginStructurePlacement.CODEC);
      return (MapCodec)Registry.register(registry, (String)"random_spread", RandomSpreadStructurePlacement.CODEC);
   }
}
