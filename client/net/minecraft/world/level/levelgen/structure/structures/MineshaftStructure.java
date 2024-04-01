package net.minecraft.world.level.levelgen.structure.structures;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import java.util.function.IntFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class MineshaftStructure extends Structure {
   public static final Codec<MineshaftStructure> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(settingsCodec(var0), MineshaftStructure.Type.CODEC.fieldOf("mineshaft_type").forGetter(var0x -> var0x.type))
            .apply(var0, MineshaftStructure::new)
   );
   private final MineshaftStructure.Type type;

   public MineshaftStructure(Structure.StructureSettings var1, MineshaftStructure.Type var2) {
      super(var1);
      this.type = var2;
   }

   @Override
   public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext var1) {
      var1.random().nextDouble();
      ChunkPos var2 = var1.chunkPos();
      BlockPos var3 = new BlockPos(var2.getMiddleBlockX(), 50, var2.getMinBlockZ());
      StructurePiecesBuilder var4 = new StructurePiecesBuilder();
      int var5 = this.generatePiecesAndAdjust(var4, var1);
      return Optional.of(new Structure.GenerationStub(var3.offset(0, var5, 0), Either.right(var4)));
   }

   private int generatePiecesAndAdjust(StructurePiecesBuilder var1, Structure.GenerationContext var2) {
      ChunkPos var3 = var2.chunkPos();
      WorldgenRandom var4 = var2.random();
      ChunkGenerator var5 = var2.chunkGenerator();
      MineshaftPieces.MineShaftRoom var6 = new MineshaftPieces.MineShaftRoom(0, var4, var3.getBlockX(2), var3.getBlockZ(2), this.type);
      var1.addPiece(var6);
      var6.addChildren(var6, var1, var4);
      int var7 = var5.getSeaLevel();
      if (this.type == MineshaftStructure.Type.MESA) {
         BlockPos var8 = var1.getBoundingBox().getCenter();
         int var9 = var5.getBaseHeight(var8.getX(), var8.getZ(), Heightmap.Types.WORLD_SURFACE_WG, var2.heightAccessor(), var2.randomState());
         int var10 = var9 <= var7 ? var7 : Mth.randomBetweenInclusive(var4, var7, var9);
         int var11 = var10 - var8.getY();
         var1.offsetPiecesVertically(var11);
         return var11;
      } else {
         return var1.moveBelowSeaLevel(var7, var5.getMinY(), var4, 10);
      }
   }

   @Override
   public StructureType<?> type() {
      return StructureType.MINESHAFT;
   }

   public static enum Type implements StringRepresentable {
      NORMAL("normal", Blocks.OAK_LOG, Blocks.OAK_PLANKS, Blocks.OAK_FENCE),
      MESA("mesa", Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_FENCE),
      POTATO("potato", Blocks.POTATO_STEM, Blocks.POTATO_PLANKS, Blocks.POTATO_FENCE);

      public static final Codec<MineshaftStructure.Type> CODEC = StringRepresentable.fromEnum(MineshaftStructure.Type::values);
      private static final IntFunction<MineshaftStructure.Type> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
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

      public static MineshaftStructure.Type byId(int var0) {
         return BY_ID.apply(var0);
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

      @Override
      public String getSerializedName() {
         return this.name;
      }
   }
}
