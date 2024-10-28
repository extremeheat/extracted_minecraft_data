package net.minecraft.world.level.levelgen.structure.structures;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class EndCityStructure extends Structure {
   public static final MapCodec<EndCityStructure> CODEC = simpleCodec(EndCityStructure::new);

   public EndCityStructure(Structure.StructureSettings var1) {
      super(var1);
   }

   public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext var1) {
      Rotation var2 = Rotation.getRandom(var1.random());
      BlockPos var3 = this.getLowestYIn5by5BoxOffset7Blocks(var1, var2);
      return var3.getY() < 60 ? Optional.empty() : Optional.of(new Structure.GenerationStub(var3, (var4) -> {
         this.generatePieces(var4, var3, var2, var1);
      }));
   }

   private void generatePieces(StructurePiecesBuilder var1, BlockPos var2, Rotation var3, Structure.GenerationContext var4) {
      ArrayList var5 = Lists.newArrayList();
      EndCityPieces.startHouseTower(var4.structureTemplateManager(), var2, var3, var5, var4.random());
      Objects.requireNonNull(var1);
      var5.forEach(var1::addPiece);
   }

   public StructureType<?> type() {
      return StructureType.END_CITY;
   }
}
