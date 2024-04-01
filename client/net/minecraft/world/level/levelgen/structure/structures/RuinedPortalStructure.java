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
import net.minecraft.tags.BiomeTags;
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
   private static final String[] STRUCTURE_LOCATION_PORTATOLS = new String[]{
      "ruined_portatol/portal_1",
      "ruined_portatol/portal_2",
      "ruined_portatol/portal_3",
      "ruined_portatol/portal_4",
      "ruined_portatol/portal_5",
      "ruined_portatol/portal_6",
      "ruined_portatol/portal_7",
      "ruined_portatol/portal_8",
      "ruined_portatol/portal_9",
      "ruined_portatol/portal_10"
   };
   private static final String[] STRUCTURE_LOCATION_GIANT_PORTATOLS = new String[]{
      "ruined_portatol/giant_portal_1", "ruined_portatol/giant_portal_2", "ruined_portatol/giant_portal_3"
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

         float var23 = var3.nextFloat();

         for(RuinedPortalStructure.Setup var8 : this.setups) {
            var23 -= var8.weight() / var5;
            if (var23 < 0.0F) {
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
         RuinedPortalStructure.Setup var22 = var4;
         var2.airPocket = sample(var3, var22.airPocketProbability());
         var2.mossiness = var22.mossiness();
         var2.overgrown = var22.overgrown();
         var2.vines = var22.vines();
         var2.replaceWithBlackstone = var22.replaceWithBlackstone();
         var2.potato = var22.potato;
         String[] var24 = var22.potato ? STRUCTURE_LOCATION_GIANT_PORTATOLS : STRUCTURE_LOCATION_GIANT_PORTALS;
         String[] var26 = var22.potato ? STRUCTURE_LOCATION_PORTATOLS : STRUCTURE_LOCATION_PORTALS;
         ResourceLocation var27;
         if (var3.nextFloat() < 0.05F) {
            var27 = new ResourceLocation(var24[var3.nextInt(var24.length)]);
         } else {
            var27 = new ResourceLocation(var26[var3.nextInt(var26.length)]);
         }

         StructureTemplate var9 = var1.structureTemplateManager().getOrCreate(var27);
         Rotation var10 = Util.getRandom(Rotation.values(), var3);
         Mirror var11 = var3.nextFloat() < 0.5F ? Mirror.NONE : Mirror.FRONT_BACK;
         BlockPos var12 = new BlockPos(var9.getSize().getX() / 2, 0, var9.getSize().getZ() / 2);
         ChunkGenerator var13 = var1.chunkGenerator();
         LevelHeightAccessor var14 = var1.heightAccessor();
         RandomState var15 = var1.randomState();
         BlockPos var16 = var1.chunkPos().getWorldPosition();
         BoundingBox var17 = var9.getBoundingBox(var16, var10, var12, var11);
         BlockPos var18 = var17.getCenter();
         int var19 = var13.getBaseHeight(var18.getX(), var18.getZ(), RuinedPortalPiece.getHeightMapType(var22.placement()), var14, var15) - 1;
         int var20 = findSuitableY(
            var3,
            var13,
            var22.placement(),
            var2.airPocket,
            var19,
            var17.getYSpan(),
            var17,
            var14,
            var15,
            var1.chunkGenerator()
               .getBiomeSource()
               .getNoiseBiome(QuartPos.fromBlock(var16.getX()), QuartPos.fromBlock(var16.getY()), QuartPos.fromBlock(var16.getZ()), var15.sampler())
               .is(BiomeTags.IS_POTATO)
         );
         BlockPos var21 = new BlockPos(var16.getX(), var20, var16.getZ());
         return Optional.of(
            new Structure.GenerationStub(
               var21,
               (Consumer<StructurePiecesBuilder>)(var10x -> {
                  if (var22.canBeCold()) {
                     var2.cold = isCold(
                        var21,
                        var1.chunkGenerator()
                           .getBiomeSource()
                           .getNoiseBiome(QuartPos.fromBlock(var21.getX()), QuartPos.fromBlock(var21.getY()), QuartPos.fromBlock(var21.getZ()), var15.sampler())
                     );
                  }
      
                  var10x.addPiece(new RuinedPortalPiece(var1.structureTemplateManager(), var21, var22.placement(), var2, var27, var9, var10, var11, var12));
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
      RandomState var8,
      boolean var9
   ) {
      int var11 = var7.getMinBuildHeight() + (var9 ? 40 : 15);
      if (var9 && var4 < var11) {
         var4 = var11;
      }

      int var10;
      if (var2 == RuinedPortalPiece.VerticalPlacement.IN_NETHER) {
         if (var3) {
            var10 = Mth.randomBetweenInclusive(var0, 32, 100);
         } else if (var0.nextFloat() < 0.5F) {
            var10 = Mth.randomBetweenInclusive(var0, 27, 29);
         } else {
            var10 = Mth.randomBetweenInclusive(var0, 29, 100);
         }
      } else if (var2 == RuinedPortalPiece.VerticalPlacement.IN_MOUNTAIN) {
         int var12 = var4 - var5;
         var10 = getRandomWithinInterval(var0, 70, var12);
      } else if (var2 == RuinedPortalPiece.VerticalPlacement.UNDERGROUND) {
         int var20 = var4 - var5;
         var10 = getRandomWithinInterval(var0, var11, var20);
      } else if (var2 == RuinedPortalPiece.VerticalPlacement.PARTLY_BURIED) {
         var10 = var4 - var5 + Mth.randomBetweenInclusive(var0, 2, 8);
      } else {
         var10 = var4;
      }

      ImmutableList var21 = ImmutableList.of(
         new BlockPos(var6.minX(), 0, var6.minZ()),
         new BlockPos(var6.maxX(), 0, var6.minZ()),
         new BlockPos(var6.minX(), 0, var6.maxZ()),
         new BlockPos(var6.maxX(), 0, var6.maxZ())
      );
      List var13 = var21.stream().map(var3x -> var1.getBaseColumn(var3x.getX(), var3x.getZ(), var7, var8)).collect(Collectors.toList());
      Heightmap.Types var14 = var2 == RuinedPortalPiece.VerticalPlacement.ON_OCEAN_FLOOR ? Heightmap.Types.OCEAN_FLOOR_WG : Heightmap.Types.WORLD_SURFACE_WG;

      int var15;
      for(var15 = var10; var15 > var11; --var15) {
         int var16 = 0;

         for(NoiseColumn var18 : var13) {
            BlockState var19 = var18.getBlock(var15);
            if (var14.isOpaque().test(var19)) {
               if (++var16 == 3) {
                  return var15;
               }
            }
         }
      }

      return var15;
   }

   private static int getRandomWithinInterval(RandomSource var0, int var1, int var2) {
      return var1 < var2 ? Mth.randomBetweenInclusive(var0, var1, var2) : var2;
   }

   @Override
   public StructureType<?> type() {
      return StructureType.RUINED_PORTAL;
   }

   public static record Setup(RuinedPortalPiece.VerticalPlacement b, float c, float d, boolean e, boolean f, boolean g, boolean h, float i, boolean j) {
      private final RuinedPortalPiece.VerticalPlacement placement;
      private final float airPocketProbability;
      private final float mossiness;
      private final boolean overgrown;
      private final boolean vines;
      private final boolean canBeCold;
      private final boolean replaceWithBlackstone;
      private final float weight;
      final boolean potato;
      public static final Codec<RuinedPortalStructure.Setup> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  RuinedPortalPiece.VerticalPlacement.CODEC.fieldOf("placement").forGetter(RuinedPortalStructure.Setup::placement),
                  Codec.floatRange(0.0F, 1.0F).fieldOf("air_pocket_probability").forGetter(RuinedPortalStructure.Setup::airPocketProbability),
                  Codec.floatRange(0.0F, 1.0F).fieldOf("mossiness").forGetter(RuinedPortalStructure.Setup::mossiness),
                  Codec.BOOL.fieldOf("overgrown").forGetter(RuinedPortalStructure.Setup::overgrown),
                  Codec.BOOL.fieldOf("vines").forGetter(RuinedPortalStructure.Setup::vines),
                  Codec.BOOL.fieldOf("can_be_cold").forGetter(RuinedPortalStructure.Setup::canBeCold),
                  Codec.BOOL.fieldOf("replace_with_blackstone").forGetter(RuinedPortalStructure.Setup::replaceWithBlackstone),
                  ExtraCodecs.POSITIVE_FLOAT.fieldOf("weight").forGetter(RuinedPortalStructure.Setup::weight),
                  Codec.BOOL.fieldOf("potaot").forGetter(RuinedPortalStructure.Setup::potato)
               )
               .apply(var0, RuinedPortalStructure.Setup::new)
      );

      public Setup(
         RuinedPortalPiece.VerticalPlacement var1, float var2, float var3, boolean var4, boolean var5, boolean var6, boolean var7, float var8, boolean var9
      ) {
         super();
         this.placement = var1;
         this.airPocketProbability = var2;
         this.mossiness = var3;
         this.overgrown = var4;
         this.vines = var5;
         this.canBeCold = var6;
         this.replaceWithBlackstone = var7;
         this.weight = var8;
         this.potato = var9;
      }
   }
}
