package net.minecraft.world.level.levelgen.structure.structures;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public final class JigsawStructure extends Structure {
   public static final int MAX_TOTAL_STRUCTURE_RANGE = 128;
   public static final Codec<JigsawStructure> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(settingsCodec(var0), StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter((var0x) -> {
         return var0x.startPool;
      }), ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter((var0x) -> {
         return var0x.startJigsawName;
      }), Codec.intRange(0, 7).fieldOf("size").forGetter((var0x) -> {
         return var0x.maxDepth;
      }), HeightProvider.CODEC.fieldOf("start_height").forGetter((var0x) -> {
         return var0x.startHeight;
      }), Codec.BOOL.fieldOf("use_expansion_hack").forGetter((var0x) -> {
         return var0x.useExpansionHack;
      }), Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter((var0x) -> {
         return var0x.projectStartToHeightmap;
      }), Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter((var0x) -> {
         return var0x.maxDistanceFromCenter;
      })).apply(var0, JigsawStructure::new);
   }).flatXmap(verifyRange(), verifyRange()).codec();
   private final Holder<StructureTemplatePool> startPool;
   private final Optional<ResourceLocation> startJigsawName;
   private final int maxDepth;
   private final HeightProvider startHeight;
   private final boolean useExpansionHack;
   private final Optional<Heightmap.Types> projectStartToHeightmap;
   private final int maxDistanceFromCenter;

   private static Function<JigsawStructure, DataResult<JigsawStructure>> verifyRange() {
      return (var0) -> {
         byte var10000;
         switch (var0.terrainAdaptation()) {
            case NONE:
               var10000 = 0;
               break;
            case BURY:
            case BEARD_THIN:
            case BEARD_BOX:
               var10000 = 12;
               break;
            default:
               throw new IncompatibleClassChangeError();
         }

         byte var1 = var10000;
         return var0.maxDistanceFromCenter + var1 > 128 ? DataResult.error("Structure size including terrain adaptation must not exceed 128") : DataResult.success(var0);
      };
   }

   public JigsawStructure(Structure.StructureSettings var1, Holder<StructureTemplatePool> var2, Optional<ResourceLocation> var3, int var4, HeightProvider var5, boolean var6, Optional<Heightmap.Types> var7, int var8) {
      super(var1);
      this.startPool = var2;
      this.startJigsawName = var3;
      this.maxDepth = var4;
      this.startHeight = var5;
      this.useExpansionHack = var6;
      this.projectStartToHeightmap = var7;
      this.maxDistanceFromCenter = var8;
   }

   public JigsawStructure(Structure.StructureSettings var1, Holder<StructureTemplatePool> var2, int var3, HeightProvider var4, boolean var5, Heightmap.Types var6) {
      this(var1, var2, Optional.empty(), var3, var4, var5, Optional.of(var6), 80);
   }

   public JigsawStructure(Structure.StructureSettings var1, Holder<StructureTemplatePool> var2, int var3, HeightProvider var4, boolean var5) {
      this(var1, var2, Optional.empty(), var3, var4, var5, Optional.empty(), 80);
   }

   public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext var1) {
      ChunkPos var2 = var1.chunkPos();
      int var3 = this.startHeight.sample(var1.random(), new WorldGenerationContext(var1.chunkGenerator(), var1.heightAccessor()));
      BlockPos var4 = new BlockPos(var2.getMinBlockX(), var3, var2.getMinBlockZ());
      Pools.forceBootstrap();
      return JigsawPlacement.addPieces(var1, this.startPool, this.startJigsawName, this.maxDepth, var4, this.useExpansionHack, this.projectStartToHeightmap, this.maxDistanceFromCenter);
   }

   public StructureType<?> type() {
      return StructureType.JIGSAW;
   }
}
