package net.minecraft.world.level.levelgen.structure;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OceanRuinConfiguration;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class OceanRuinFeature extends StructureFeature<OceanRuinConfiguration> {
   public OceanRuinFeature(Codec<OceanRuinConfiguration> var1) {
      super(var1, PieceGeneratorSupplier.simple(PieceGeneratorSupplier.checkForBiomeOnTop(Heightmap.Types.OCEAN_FLOOR_WG), OceanRuinFeature::generatePieces));
   }

   private static void generatePieces(StructurePiecesBuilder var0, PieceGenerator.Context<OceanRuinConfiguration> var1) {
      BlockPos var2 = new BlockPos(var1.chunkPos().getMinBlockX(), 90, var1.chunkPos().getMinBlockZ());
      Rotation var3 = Rotation.getRandom(var1.random());
      OceanRuinPieces.addPieces(var1.structureManager(), var2, var3, var0, var1.random(), (OceanRuinConfiguration)var1.config());
   }

   public static enum Type implements StringRepresentable {
      WARM("warm"),
      COLD("cold");

      public static final Codec<OceanRuinFeature.Type> CODEC = StringRepresentable.fromEnum(OceanRuinFeature.Type::values, OceanRuinFeature.Type::byName);
      private static final Map<String, OceanRuinFeature.Type> BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toMap(OceanRuinFeature.Type::getName, (var0) -> {
         return var0;
      }));
      private final String name;

      private Type(String var3) {
         this.name = var3;
      }

      public String getName() {
         return this.name;
      }

      @Nullable
      public static OceanRuinFeature.Type byName(String var0) {
         return (OceanRuinFeature.Type)BY_NAME.get(var0);
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static OceanRuinFeature.Type[] $values() {
         return new OceanRuinFeature.Type[]{WARM, COLD};
      }
   }
}
