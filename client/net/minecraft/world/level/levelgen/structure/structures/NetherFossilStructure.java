package net.minecraft.world.level.levelgen.structure.structures;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

public class NetherFossilStructure extends Structure {
   public static final MapCodec<NetherFossilStructure> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(settingsCodec(var0), HeightProvider.CODEC.fieldOf("height").forGetter((var0x) -> {
         return var0x.height;
      })).apply(var0, NetherFossilStructure::new);
   });
   public final HeightProvider height;

   public NetherFossilStructure(Structure.StructureSettings var1, HeightProvider var2) {
      super(var1);
      this.height = var2;
   }

   public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext var1) {
      WorldgenRandom var2 = var1.random();
      int var3 = var1.chunkPos().getMinBlockX() + var2.nextInt(16);
      int var4 = var1.chunkPos().getMinBlockZ() + var2.nextInt(16);
      int var5 = var1.chunkGenerator().getSeaLevel();
      WorldGenerationContext var6 = new WorldGenerationContext(var1.chunkGenerator(), var1.heightAccessor());
      int var7 = this.height.sample(var2, var6);
      NoiseColumn var8 = var1.chunkGenerator().getBaseColumn(var3, var4, var1.heightAccessor(), var1.randomState());
      BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos(var3, var7, var4);

      while(var7 > var5) {
         BlockState var10 = var8.getBlock(var7);
         --var7;
         BlockState var11 = var8.getBlock(var7);
         if (var10.isAir() && (var11.is(Blocks.SOUL_SAND) || var11.isFaceSturdy(EmptyBlockGetter.INSTANCE, var9.setY(var7), Direction.UP))) {
            break;
         }
      }

      if (var7 <= var5) {
         return Optional.empty();
      } else {
         BlockPos var12 = new BlockPos(var3, var7, var4);
         return Optional.of(new Structure.GenerationStub(var12, (var3x) -> {
            NetherFossilPieces.addPieces(var1.structureTemplateManager(), var3x, var2, var12);
         }));
      }
   }

   public StructureType<?> type() {
      return StructureType.NETHER_FOSSIL;
   }
}
