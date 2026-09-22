package net.minecraft.world.level.levelgen.structure.structures;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.NoiseColumn;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

public class NetherFossilStructure extends Structure {
   public static final MapCodec<NetherFossilStructure> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(settingsCodec(i), HeightProvider.CODEC.fieldOf("height").forGetter((c) -> c.height)).apply(i, NetherFossilStructure::new));
   public final HeightProvider height;

   public NetherFossilStructure(final Structure.StructureSettings settings, final HeightProvider height) {
      super(settings);
      this.height = height;
   }

   public Optional<Structure.GenerationStub> findGenerationPoint(final Structure.GenerationContext context) {
      WorldgenRandom random = context.random();
      int blockX = context.chunkPos().getMinBlockX() + random.nextInt(16);
      int blockZ = context.chunkPos().getMinBlockZ() + random.nextInt(16);
      int seaLevel = context.chunkGenerator().getSeaLevel();
      int y = this.height.sample(random, VerticalAnchor.Context.from(context.chunkGenerator(), context.heightAccessor()));
      NoiseColumn column = context.chunkGenerator().getBaseColumn(blockX, blockZ, context.heightAccessor(), context.randomState());

      while(y > seaLevel) {
         --y;
         if (column.isEmpty(y + 1) && column.isSolid(y)) {
            break;
         }
      }

      if (y <= seaLevel) {
         return Optional.empty();
      } else {
         BlockPos position = new BlockPos(blockX, y, blockZ);
         return Optional.of(new Structure.GenerationStub(position, (builder) -> NetherFossilPieces.addPieces(context.structureTemplateManager(), builder, random, position)));
      }
   }

   public StructureType<?> type() {
      return StructureType.NETHER_FOSSIL;
   }
}
