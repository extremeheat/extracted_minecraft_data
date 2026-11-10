package net.minecraft.world.level.levelgen.structure.placement;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;

public interface StructurePlacements {
   static void bootstrap(final WritableRegistry<MapCodec<? extends StructurePlacement>> registry) {
      Registry.register(registry, (String)"concentric_rings", ConcentricRingsStructurePlacement.CODEC);
      Registry.register(registry, (String)"dimension_origin", DimensionOriginStructurePlacement.CODEC);
      Registry.register(registry, (String)"random_spread", RandomSpreadStructurePlacement.CODEC);
   }
}
