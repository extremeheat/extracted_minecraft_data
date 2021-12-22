package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.configurations.MineshaftConfiguration;
import net.minecraft.world.level.levelgen.structure.MineShaftPieces;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class MineshaftFeature extends StructureFeature<MineshaftConfiguration> {
   public MineshaftFeature(Codec<MineshaftConfiguration> var1) {
      super(var1, PieceGeneratorSupplier.simple(MineshaftFeature::checkLocation, MineshaftFeature::generatePieces));
   }

   private static boolean checkLocation(PieceGeneratorSupplier.Context<MineshaftConfiguration> var0) {
      WorldgenRandom var1 = new WorldgenRandom(new LegacyRandomSource(0L));
      var1.setLargeFeatureSeed(var0.seed(), var0.chunkPos().field_504, var0.chunkPos().field_505);
      double var2 = (double)((MineshaftConfiguration)var0.config()).probability;
      return var1.nextDouble() >= var2 ? false : var0.validBiome().test(var0.chunkGenerator().getNoiseBiome(QuartPos.fromBlock(var0.chunkPos().getMiddleBlockX()), QuartPos.fromBlock(50), QuartPos.fromBlock(var0.chunkPos().getMiddleBlockZ())));
   }

   private static void generatePieces(StructurePiecesBuilder var0, PieceGenerator.Context<MineshaftConfiguration> var1) {
      MineShaftPieces.MineShaftRoom var2 = new MineShaftPieces.MineShaftRoom(0, var1.random(), var1.chunkPos().getBlockX(2), var1.chunkPos().getBlockZ(2), ((MineshaftConfiguration)var1.config()).type);
      var0.addPiece(var2);
      var2.addChildren(var2, var0, var1.random());
      int var3 = var1.chunkGenerator().getSeaLevel();
      if (((MineshaftConfiguration)var1.config()).type == MineshaftFeature.Type.MESA) {
         BlockPos var4 = var0.getBoundingBox().getCenter();
         int var5 = var1.chunkGenerator().getBaseHeight(var4.getX(), var4.getZ(), Heightmap.Types.WORLD_SURFACE_WG, var1.heightAccessor());
         int var6 = var5 <= var3 ? var3 : Mth.randomBetweenInclusive(var1.random(), var3, var5);
         int var7 = var6 - var4.getY();
         var0.offsetPiecesVertically(var7);
      } else {
         var0.moveBelowSeaLevel(var3, var1.chunkGenerator().getMinY(), var1.random(), 10);
      }

   }

   public static enum Type implements StringRepresentable {
      NORMAL("normal", Blocks.OAK_LOG, Blocks.OAK_PLANKS, Blocks.OAK_FENCE),
      MESA("mesa", Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_FENCE);

      public static final Codec<MineshaftFeature.Type> CODEC = StringRepresentable.fromEnum(MineshaftFeature.Type::values, MineshaftFeature.Type::byName);
      private static final Map<String, MineshaftFeature.Type> BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toMap(MineshaftFeature.Type::getName, (var0) -> {
         return var0;
      }));
      private final String name;
      private final BlockState woodState;
      private final BlockState planksState;
      private final BlockState fenceState;

      private Type(String var3, Block var4, Block var5, Block var6) {
         this.name = var3;
         this.woodState = var4.defaultBlockState();
         this.planksState = var5.defaultBlockState();
         this.fenceState = var6.defaultBlockState();
      }

      public String getName() {
         return this.name;
      }

      private static MineshaftFeature.Type byName(String var0) {
         return (MineshaftFeature.Type)BY_NAME.get(var0);
      }

      public static MineshaftFeature.Type byId(int var0) {
         return var0 >= 0 && var0 < values().length ? values()[var0] : NORMAL;
      }

      public BlockState getWoodState() {
         return this.woodState;
      }

      public BlockState getPlanksState() {
         return this.planksState;
      }

      public BlockState getFenceState() {
         return this.fenceState;
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static MineshaftFeature.Type[] $values() {
         return new MineshaftFeature.Type[]{NORMAL, MESA};
      }
   }
}
