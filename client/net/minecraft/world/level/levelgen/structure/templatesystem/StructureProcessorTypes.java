package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;

public class StructureProcessorTypes {
   public StructureProcessorTypes() {
      super();
   }

   public static MapCodec<? extends StructureProcessor> bootstrap(final Registry<MapCodec<? extends StructureProcessor>> registry) {
      Registry.register(registry, (String)"blackstone_replace", BlackstoneReplaceProcessor.MAP_CODEC);
      Registry.register(registry, (String)"block_age", BlockAgeProcessor.MAP_CODEC);
      Registry.register(registry, (String)"block_ignore", BlockIgnoreProcessor.MAP_CODEC);
      Registry.register(registry, (String)"block_rot", BlockRotProcessor.MAP_CODEC);
      Registry.register(registry, (String)"capped", CappedProcessor.MAP_CODEC);
      Registry.register(registry, (String)"gravity", GravityProcessor.MAP_CODEC);
      Registry.register(registry, (String)"jigsaw_replacement", JigsawReplacementProcessor.MAP_CODEC);
      Registry.register(registry, (String)"lava_submerged_block", LavaSubmergedBlockProcessor.MAP_CODEC);
      Registry.register(registry, (String)"nop", NopProcessor.MAP_CODEC);
      Registry.register(registry, (String)"protected_blocks", ProtectedBlockProcessor.MAP_CODEC);
      return (MapCodec)Registry.register(registry, (String)"rule", RuleProcessor.MAP_CODEC);
   }
}
