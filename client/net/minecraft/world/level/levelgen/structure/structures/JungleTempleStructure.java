package net.minecraft.world.level.levelgen.structure.structures;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.levelgen.structure.SinglePieceStructure;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

public class JungleTempleStructure extends SinglePieceStructure {
   public static final MapCodec<JungleTempleStructure> CODEC = simpleCodec(JungleTempleStructure::new);

   public JungleTempleStructure(Structure.StructureSettings var1) {
      super(JungleTemplePiece::new, 12, 15, var1);
   }

   @Override
   public StructureType<?> type() {
      return StructureType.JUNGLE_TEMPLE;
   }
}
