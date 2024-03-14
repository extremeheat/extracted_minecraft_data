package net.minecraft.world.level.levelgen.structure.structures;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class RuinedPortalStructure extends Structure {
   private static final String[] STRUCTURE_LOCATION_PORTALS = new String[]{
      "ruined_portal/portal_1",
      "ruined_portal/portal_2",
      "ruined_portal/portal_3",
      "ruined_portal/portal_4",
      "ruined_portal/portal_5",
      "ruined_portal/portal_6",
      "ruined_portal/portal_7",
      "ruined_portal/portal_8",
      "ruined_portal/portal_9",
      "ruined_portal/portal_10"
   };
   private static final String[] STRUCTURE_LOCATION_GIANT_PORTALS = new String[]{
      "ruined_portal/giant_portal_1", "ruined_portal/giant_portal_2", "ruined_portal/giant_portal_3"
   };
   private static final float PROBABILITY_OF_GIANT_PORTAL = 0.05F;
   private static final int MIN_Y_INDEX = 15;
   private final List<RuinedPortalStructure.Setup> setups;
   public static final Codec<RuinedPortalStructure> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               settingsCodec(var0), ExtraCodecs.nonEmptyList(RuinedPortalStructure.Setup.CODEC.listOf()).fieldOf("setups").forGetter(var0x -> var0x.setups)
            )
            .apply(var0, RuinedPortalStructure::new)
   );

   public RuinedPortalStructure(Structure.StructureSettings var1, List<RuinedPortalStructure.Setup> var2) {
      super(var1);
      this.setups = var2;
   }

   public RuinedPortalStructure(Structure.StructureSettings var1, RuinedPortalStructure.Setup var2) {
      this(var1, List.of(var2));
   }

   @Override
   public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext var1) {
      RuinedPortalPiece.Properties var2 = new RuinedPortalPiece.Properties();
      WorldgenRandom var3 = var1.random();
      RuinedPortalStructure.Setup var4 = null;
      if (this.setups.size() > 1) {
         float var5 = 0.0F;

         for(RuinedPortalStructure.Setup var7 : this.setups) {
            var5 += var7.weight();
         }

         float var21 = var3.nextFloat();

         for(RuinedPortalStructure.Setup var8 : this.setups) {
            var21 -= var8.weight() / var5;
            if (var21 < 0.0F) {
               var4 = var8;
               break;
            }
         }
      } else {
         var4 = (RuinedPortalStructure.Setup)this.setups.get(0);
      }

      if (var4 == null) {
         throw new IllegalStateException();
      } else {
         RuinedPortalStructure.Setup var20 = var4;
         var2.airPocket = sample(var3, var20.airPocketProbability());
         var2.mossiness = var20.mossiness();
         var2.overgrown = var20.overgrown();
         var2.vines = var20.vines();
         var2.replaceWithBlackstone = var20.replaceWithBlackstone();
         ResourceLocation var22;
         if (var3.nextFloat() < 0.05F) {
            var22 = new ResourceLocation(STRUCTURE_LOCATION_GIANT_PORTALS[var3.nextInt(STRUCTURE_LOCATION_GIANT_PORTALS.length)]);
         } else {
            var22 = new ResourceLocation(STRUCTURE_LOCATION_PORTALS[var3.nextInt(STRUCTURE_LOCATION_PORTALS.length)]);
         }

         StructureTemplate var24 = var1.structureTemplateManager().getOrCreate(var22);
         Rotation var25 = Util.getRandom(Rotation.values(), var3);
         Mirror var9 = var3.nextFloat() < 0.5F ? Mirror.NONE : Mirror.FRONT_BACK;
         BlockPos var10 = new BlockPos(var24.getSize().getX() / 2, 0, var24.getSize().getZ() / 2);
         ChunkGenerator var11 = var1.chunkGenerator();
         LevelHeightAccessor var12 = var1.heightAccessor();
         RandomState var13 = var1.randomState();
         BlockPos var14 = var1.chunkPos().getWorldPosition();
         BoundingBox var15 = var24.getBoundingBox(var14, var25, var10, var9);
         BlockPos var16 = var15.getCenter();
         int var17 = var11.getBaseHeight(var16.getX(), var16.getZ(), RuinedPortalPiece.getHeightMapType(var20.placement()), var12, var13) - 1;
         int var18 = findSuitableY(var3, var11, var20.placement(), var2.airPocket, var17, var15.getYSpan(), var15, var12, var13);
         BlockPos var19 = new BlockPos(var14.getX(), var18, var14.getZ());
         return Optional.of(
            new Structure.GenerationStub(
               var19,
               (Consumer<StructurePiecesBuilder>)(var10x -> {
                  if (var20.canBeCold()) {
                     var2.cold = isCold(
                        var19,
                        var1.chunkGenerator()
                           .getBiomeSource()
                           .getNoiseBiome(QuartPos.fromBlock(var19.getX()), QuartPos.fromBlock(var19.getY()), QuartPos.fromBlock(var19.getZ()), var13.sampler())
                     );
                  }
      
                  var10x.addPiece(new RuinedPortalPiece(var1.structureTemplateManager(), var19, var20.placement(), var2, var22, var24, var25, var9, var10));
               })
            )
         );
      }
   }

   private static boolean sample(WorldgenRandom var0, float var1) {
      if (var1 == 0.0F) {
         return false;
      } else if (var1 == 1.0F) {
         return true;
      } else {
         return var0.nextFloat() < var1;
      }
   }

   private static boolean isCold(BlockPos var0, Holder<Biome> var1) {
      return ((Biome)var1.value()).coldEnoughToSnow(var0);
   }

   private static int findSuitableY(
      RandomSource var0,
      ChunkGenerator var1,
      RuinedPortalPiece.VerticalPlacement var2,
      boolean var3,
      int var4,
      int var5,
      BoundingBox var6,
      LevelHeightAccessor var7,
      RandomState var8
   ) {
      int var10 = var7.getMinBuildHeight() + 15;
      int var9;
      if (var2 == RuinedPortalPiece.VerticalPlacement.IN_NETHER) {
         if (var3) {
            var9 = Mth.randomBetweenInclusive(var0, 32, 100);
         } else if (var0.nextFloat() < 0.5F) {
            var9 = Mth.randomBetweenInclusive(var0, 27, 29);
         } else {
            var9 = Mth.randomBetweenInclusive(var0, 29, 100);
         }
      } else if (var2 == RuinedPortalPiece.VerticalPlacement.IN_MOUNTAIN) {
         int var11 = var4 - var5;
         var9 = getRandomWithinInterval(var0, 70, var11);
      } else if (var2 == RuinedPortalPiece.VerticalPlacement.UNDERGROUND) {
         int var19 = var4 - var5;
         var9 = getRandomWithinInterval(var0, var10, var19);
      } else if (var2 == RuinedPortalPiece.VerticalPlacement.PARTLY_BURIED) {
         var9 = var4 - var5 + Mth.randomBetweenInclusive(var0, 2, 8);
      } else {
         var9 = var4;
      }

      ImmutableList var20 = ImmutableList.of(
         new BlockPos(var6.minX(), 0, var6.minZ()),
         new BlockPos(var6.maxX(), 0, var6.minZ()),
         new BlockPos(var6.minX(), 0, var6.maxZ()),
         new BlockPos(var6.maxX(), 0, var6.maxZ())
      );
      List var12 = var20.stream().map(var3x -> var1.getBaseColumn(var3x.getX(), var3x.getZ(), var7, var8)).collect(Collectors.toList());
      Heightmap.Types var13 = var2 == RuinedPortalPiece.VerticalPlacement.ON_OCEAN_FLOOR ? Heightmap.Types.OCEAN_FLOOR_WG : Heightmap.Types.WORLD_SURFACE_WG;

      int var14;
      for(var14 = var9; var14 > var10; --var14) {
         int var15 = 0;

         for(NoiseColumn var17 : var12) {
            BlockState var18 = var17.getBlock(var14);
            if (var13.isOpaque().test(var18)) {
               if (++var15 == 3) {
                  return var14;
               }
            }
         }
      }

      return var14;
   }

   private static int getRandomWithinInterval(RandomSource var0, int var1, int var2) {
      return var1 < var2 ? Mth.randomBetweenInclusive(var0, var1, var2) : var2;
   }

   @Override
   public StructureType<?> type() {
      return StructureType.RUINED_PORTAL;
   }

   public static record Setup(RuinedPortalPiece.VerticalPlacement b, float c, float d, boolean e, boolean f, boolean g, boolean h, float i) {
      private final RuinedPortalPiece.VerticalPlacement placement;
      private final float airPocketProbability;
      private final float mossiness;
      private final boolean overgrown;
      private final boolean vines;
      private final boolean canBeCold;
      private final boolean replaceWithBlackstone;
      private final float weight;
      public static final Codec<RuinedPortalStructure.Setup> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  RuinedPortalPiece.VerticalPlacement.CODEC.fieldOf("placement").forGetter(RuinedPortalStructure.Setup::placement),
                  Codec.floatRange(0.0F, 1.0F).fieldOf("air_pocket_probability").forGetter(RuinedPortalStructure.Setup::airPocketProbability),
                  Codec.floatRange(0.0F, 1.0F).fieldOf("mossiness").forGetter(RuinedPortalStructure.Setup::mossiness),
                  Codec.BOOL.fieldOf("overgrown").forGetter(RuinedPortalStructure.Setup::overgrown),
                  Codec.BOOL.fieldOf("vines").forGetter(RuinedPortalStructure.Setup::vines),
                  Codec.BOOL.fieldOf("can_be_cold").forGetter(RuinedPortalStructure.Setup::canBeCold),
                  Codec.BOOL.fieldOf("replace_with_blackstone").forGetter(RuinedPortalStructure.Setup::replaceWithBlackstone),
                  ExtraCodecs.POSITIVE_FLOAT.fieldOf("weight").forGetter(RuinedPortalStructure.Setup::weight)
               )
               .apply(var0, RuinedPortalStructure.Setup::new)
      );

      public Setup(RuinedPortalPiece.VerticalPlacement var1, float var2, float var3, boolean var4, boolean var5, boolean var6, boolean var7, float var8) {
         super();
         this.placement = var1;
         this.airPocketProbability = var2;
         this.mossiness = var3;
         this.overgrown = var4;
         this.vines = var5;
         this.canBeCold = var6;
         this.replaceWithBlackstone = var7;
         this.weight = var8;
      }
   }
}
