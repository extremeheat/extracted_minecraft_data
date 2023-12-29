package net.minecraft.world.level.levelgen.structure.structures;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;

public final class JigsawStructure extends Structure {
   public static final int MAX_TOTAL_STRUCTURE_RANGE = 128;
   public static final int MAX_DEPTH = 20;
   public static final Codec<JigsawStructure> CODEC = ExtraCodecs.validate(
         RecordCodecBuilder.mapCodec(
            var0 -> var0.group(
                     settingsCodec(var0),
                     StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(var0x -> var0x.startPool),
                     ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(var0x -> var0x.startJigsawName),
                     Codec.intRange(0, 20).fieldOf("size").forGetter(var0x -> var0x.maxDepth),
                     HeightProvider.CODEC.fieldOf("start_height").forGetter(var0x -> var0x.startHeight),
                     Codec.BOOL.fieldOf("use_expansion_hack").forGetter(var0x -> var0x.useExpansionHack),
                     Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(var0x -> var0x.projectStartToHeightmap),
                     Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(var0x -> var0x.maxDistanceFromCenter),
                     Codec.list(PoolAliasBinding.CODEC).optionalFieldOf("pool_aliases", List.of()).forGetter(var0x -> var0x.poolAliases)
                  )
                  .apply(var0, JigsawStructure::new)
         ),
         JigsawStructure::verifyRange
      )
      .codec();
   private final Holder<StructureTemplatePool> startPool;
   private final Optional<ResourceLocation> startJigsawName;
   private final int maxDepth;
   private final HeightProvider startHeight;
   private final boolean useExpansionHack;
   private final Optional<Heightmap.Types> projectStartToHeightmap;
   private final int maxDistanceFromCenter;
   private final List<PoolAliasBinding> poolAliases;

   private static DataResult<JigsawStructure> verifyRange(JigsawStructure var0) {
      byte var1 = switch(var0.terrainAdaptation()) {
         case NONE -> 0;
         case BURY, BEARD_THIN, BEARD_BOX -> 12;
      };
      return var0.maxDistanceFromCenter + var1 > 128
         ? DataResult.error(() -> "Structure size including terrain adaptation must not exceed 128")
         : DataResult.success(var0);
   }

   public JigsawStructure(
      Structure.StructureSettings var1,
      Holder<StructureTemplatePool> var2,
      Optional<ResourceLocation> var3,
      int var4,
      HeightProvider var5,
      boolean var6,
      Optional<Heightmap.Types> var7,
      int var8,
      List<PoolAliasBinding> var9
   ) {
      super(var1);
      this.startPool = var2;
      this.startJigsawName = var3;
      this.maxDepth = var4;
      this.startHeight = var5;
      this.useExpansionHack = var6;
      this.projectStartToHeightmap = var7;
      this.maxDistanceFromCenter = var8;
      this.poolAliases = var9;
   }

   public JigsawStructure(
      Structure.StructureSettings var1, Holder<StructureTemplatePool> var2, int var3, HeightProvider var4, boolean var5, Heightmap.Types var6
   ) {
      this(var1, var2, Optional.empty(), var3, var4, var5, Optional.of(var6), 80, List.of());
   }

   public JigsawStructure(Structure.StructureSettings var1, Holder<StructureTemplatePool> var2, int var3, HeightProvider var4, boolean var5) {
      this(var1, var2, Optional.empty(), var3, var4, var5, Optional.empty(), 80, List.of());
   }

   @Override
   public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext var1) {
      ChunkPos var2 = var1.chunkPos();
      int var3 = this.startHeight.sample(var1.random(), new WorldGenerationContext(var1.chunkGenerator(), var1.heightAccessor()));
      BlockPos var4 = new BlockPos(var2.getMinBlockX(), var3, var2.getMinBlockZ());
      return JigsawPlacement.addPieces(
         var1,
         this.startPool,
         this.startJigsawName,
         this.maxDepth,
         var4,
         this.useExpansionHack,
         this.projectStartToHeightmap,
         this.maxDistanceFromCenter,
         PoolAliasLookup.create(this.poolAliases, var4, var1.seed())
      );
   }

   @Override
   public StructureType<?> type() {
      return StructureType.JIGSAW;
   }

   public List<PoolAliasBinding> getPoolAliases() {
      return this.poolAliases;
   }
}
