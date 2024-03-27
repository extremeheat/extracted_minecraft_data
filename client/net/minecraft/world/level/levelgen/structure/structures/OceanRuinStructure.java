package net.minecraft.world.level.levelgen.structure.structures;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class OceanRuinStructure extends Structure {
   public static final MapCodec<OceanRuinStructure> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(
               settingsCodec(var0),
               OceanRuinStructure.Type.CODEC.fieldOf("biome_temp").forGetter(var0x -> var0x.biomeTemp),
               Codec.floatRange(0.0F, 1.0F).fieldOf("large_probability").forGetter(var0x -> var0x.largeProbability),
               Codec.floatRange(0.0F, 1.0F).fieldOf("cluster_probability").forGetter(var0x -> var0x.clusterProbability)
            )
            .apply(var0, OceanRuinStructure::new)
   );
   public final OceanRuinStructure.Type biomeTemp;
   public final float largeProbability;
   public final float clusterProbability;

   public OceanRuinStructure(Structure.StructureSettings var1, OceanRuinStructure.Type var2, float var3, float var4) {
      super(var1);
      this.biomeTemp = var2;
      this.largeProbability = var3;
      this.clusterProbability = var4;
   }

   @Override
   public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext var1) {
      return onTopOfChunkCenter(var1, Heightmap.Types.OCEAN_FLOOR_WG, var2 -> this.generatePieces(var2, var1));
   }

   private void generatePieces(StructurePiecesBuilder var1, Structure.GenerationContext var2) {
      BlockPos var3 = new BlockPos(var2.chunkPos().getMinBlockX(), 90, var2.chunkPos().getMinBlockZ());
      Rotation var4 = Rotation.getRandom(var2.random());
      OceanRuinPieces.addPieces(var2.structureTemplateManager(), var3, var4, var1, var2.random(), this);
   }

   @Override
   public StructureType<?> type() {
      return StructureType.OCEAN_RUIN;
   }

   public static enum Type implements StringRepresentable {
      WARM("warm"),
      COLD("cold");

      public static final Codec<OceanRuinStructure.Type> CODEC = StringRepresentable.fromEnum(OceanRuinStructure.Type::values);
      private final String name;

      private Type(String var3) {
         this.name = var3;
      }

      public String getName() {
         return this.name;
      }

      @Override
      public String getSerializedName() {
         return this.name;
      }
   }
}
