package net.minecraft.world.level.levelgen.structure.structures;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

public final class JigsawStructure extends Structure {
   public static final DimensionPadding DEFAULT_DIMENSION_PADDING;
   public static final LiquidSettings DEFAULT_LIQUID_SETTINGS;
   public static final int MAX_TOTAL_STRUCTURE_RANGE = 128;
   public static final int MIN_DEPTH = 0;
   public static final int MAX_DEPTH = 20;
   public static final MapCodec<JigsawStructure> CODEC;
   private final Holder<StructureTemplatePool> startPool;
   private final Optional<Identifier> startJigsawName;
   private final int maxDepth;
   private final HeightProvider startHeight;
   private final boolean useExpansionHack;
   private final Optional<Heightmap.Types> projectStartToHeightmap;
   private final MaxDistance maxDistanceFromCenter;
   private final List<PoolAliasBinding> poolAliases;
   private final DimensionPadding dimensionPadding;
   private final LiquidSettings liquidSettings;

   private static DataResult<JigsawStructure> verifyRange(final JigsawStructure structure) {
      byte var10000;
      switch (structure.terrainAdaptation()) {
         case NONE:
            var10000 = 0;
            break;
         case BURY:
         case BEARD_THIN:
         case BEARD_BOX:
         case ENCAPSULATE:
            var10000 = 12;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      int edgeNeeded = var10000;
      return structure.maxDistanceFromCenter.horizontal() + edgeNeeded > 128 ? DataResult.error(() -> "Horizontal structure size including terrain adaptation must not exceed 128") : DataResult.success(structure);
   }

   public JigsawStructure(final Structure.StructureSettings settings, final Holder<StructureTemplatePool> startPool, final Optional<Identifier> startJigsawName, final int maxDepth, final HeightProvider startHeight, final boolean useExpansionHack, final Optional<Heightmap.Types> projectStartToHeightmap, final MaxDistance maxDistanceFromCenter, final List<PoolAliasBinding> poolAliases, final DimensionPadding dimensionPadding, final LiquidSettings liquidSettings) {
      super(settings);
      this.startPool = startPool;
      this.startJigsawName = startJigsawName;
      this.maxDepth = maxDepth;
      this.startHeight = startHeight;
      this.useExpansionHack = useExpansionHack;
      this.projectStartToHeightmap = projectStartToHeightmap;
      this.maxDistanceFromCenter = maxDistanceFromCenter;
      this.poolAliases = poolAliases;
      this.dimensionPadding = dimensionPadding;
      this.liquidSettings = liquidSettings;
   }

   public JigsawStructure(final Structure.StructureSettings settings, final Holder<StructureTemplatePool> startPool, final int maxDepth, final HeightProvider startHeight, final boolean useExpansionHack, final Heightmap.Types projectStartToHeightmap) {
      this(settings, startPool, Optional.empty(), maxDepth, startHeight, useExpansionHack, Optional.of(projectStartToHeightmap), new MaxDistance(80), List.of(), DEFAULT_DIMENSION_PADDING, DEFAULT_LIQUID_SETTINGS);
   }

   public JigsawStructure(final Structure.StructureSettings settings, final Holder<StructureTemplatePool> startPool, final int maxDepth, final HeightProvider startHeight, final boolean useExpansionHack) {
      this(settings, startPool, Optional.empty(), maxDepth, startHeight, useExpansionHack, Optional.empty(), new MaxDistance(80), List.of(), DEFAULT_DIMENSION_PADDING, DEFAULT_LIQUID_SETTINGS);
   }

   public Optional<Structure.GenerationStub> findGenerationPoint(final Structure.GenerationContext context) {
      ChunkPos chunkPos = context.chunkPos();
      int height = this.startHeight.sample(context.random(), new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor()));
      BlockPos startPos = new BlockPos(chunkPos.getMinBlockX(), height, chunkPos.getMinBlockZ());
      return JigsawPlacement.addPieces(context, this.startPool, this.startJigsawName, this.maxDepth, startPos, this.useExpansionHack, this.projectStartToHeightmap, this.maxDistanceFromCenter, PoolAliasLookup.create(this.poolAliases, startPos, context.seed()), this.dimensionPadding, this.liquidSettings);
   }

   public StructureType<?> type() {
      return StructureType.JIGSAW;
   }

   @VisibleForTesting
   public Holder<StructureTemplatePool> getStartPool() {
      return this.startPool;
   }

   @VisibleForTesting
   public List<PoolAliasBinding> getPoolAliases() {
      return this.poolAliases;
   }

   static {
      DEFAULT_DIMENSION_PADDING = DimensionPadding.ZERO;
      DEFAULT_LIQUID_SETTINGS = LiquidSettings.APPLY_WATERLOGGING;
      CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(settingsCodec(i), StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter((j) -> j.startPool), Identifier.CODEC.optionalFieldOf("start_jigsaw_name").forGetter((j) -> j.startJigsawName), Codec.intRange(0, 20).fieldOf("size").forGetter((j) -> j.maxDepth), HeightProvider.CODEC.fieldOf("start_height").forGetter((j) -> j.startHeight), Codec.BOOL.fieldOf("use_expansion_hack").forGetter((j) -> j.useExpansionHack), Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter((j) -> j.projectStartToHeightmap), JigsawStructure.MaxDistance.CODEC.fieldOf("max_distance_from_center").forGetter((j) -> j.maxDistanceFromCenter), Codec.list(PoolAliasBinding.CODEC).optionalFieldOf("pool_aliases", List.of()).forGetter((j) -> j.poolAliases), DimensionPadding.CODEC.optionalFieldOf("dimension_padding", DEFAULT_DIMENSION_PADDING).forGetter((j) -> j.dimensionPadding), LiquidSettings.CODEC.optionalFieldOf("liquid_settings", DEFAULT_LIQUID_SETTINGS).forGetter((j) -> j.liquidSettings)).apply(i, JigsawStructure::new)).validate(JigsawStructure::verifyRange);
   }

   public static record MaxDistance(int horizontal, int vertical) {
      private static final Codec<Integer> HORIZONTAL_VALUE_CODEC = Codec.intRange(1, 128);
      private static final Codec<MaxDistance> FULL_CODEC = RecordCodecBuilder.create((i) -> i.group(HORIZONTAL_VALUE_CODEC.fieldOf("horizontal").forGetter(MaxDistance::horizontal), ExtraCodecs.intRange(1, DimensionType.Y_SIZE).optionalFieldOf("vertical", DimensionType.Y_SIZE).forGetter(MaxDistance::vertical)).apply(i, MaxDistance::new));
      public static final Codec<MaxDistance> CODEC;

      public MaxDistance(final int value) {
         this(value, value);
      }

      public MaxDistance {
         super();
      }

      static {
         CODEC = Codec.either(FULL_CODEC, HORIZONTAL_VALUE_CODEC).xmap((either) -> (MaxDistance)either.map(Function.identity(), MaxDistance::new), (distance) -> distance.horizontal == distance.vertical ? Either.right(distance.horizontal) : Either.left(distance));
      }
   }
}
