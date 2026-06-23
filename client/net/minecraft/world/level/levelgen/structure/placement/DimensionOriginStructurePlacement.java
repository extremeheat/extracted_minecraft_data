package net.minecraft.world.level.levelgen.structure.placement;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;

public class DimensionOriginStructurePlacement implements StructurePlacement {
   public static final DimensionOriginStructurePlacement INSTANCE = new DimensionOriginStructurePlacement();
   public static final MapCodec<DimensionOriginStructurePlacement> CODEC;

   private DimensionOriginStructurePlacement() {
      super();
   }

   public boolean isStructureChunk(final ChunkGeneratorStructureState state, final int sourceX, final int sourceZ) {
      ChunkPos origin = state.getDimensionOrigin();
      return origin.x() == sourceX && origin.z() == sourceZ;
   }

   public MapCodec<DimensionOriginStructurePlacement> codec() {
      return CODEC;
   }

   static {
      CODEC = MapCodec.unit(INSTANCE);
   }
}
