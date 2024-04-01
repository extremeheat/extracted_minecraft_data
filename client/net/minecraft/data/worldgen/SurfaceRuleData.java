package net.minecraft.data.worldgen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;

public class SurfaceRuleData {
   private static final SurfaceRules.RuleSource AIR = makeStateRule(Blocks.AIR);
   private static final SurfaceRules.RuleSource BEDROCK = makeStateRule(Blocks.BEDROCK);
   private static final SurfaceRules.RuleSource WHITE_TERRACOTTA = makeStateRule(Blocks.WHITE_TERRACOTTA);
   private static final SurfaceRules.RuleSource ORANGE_TERRACOTTA = makeStateRule(Blocks.ORANGE_TERRACOTTA);
   private static final SurfaceRules.RuleSource TERRACOTTA = makeStateRule(Blocks.TERRACOTTA);
   private static final SurfaceRules.RuleSource RED_SAND = makeStateRule(Blocks.RED_SAND);
   private static final SurfaceRules.RuleSource RED_SANDSTONE = makeStateRule(Blocks.RED_SANDSTONE);
   private static final SurfaceRules.RuleSource STONE = makeStateRule(Blocks.STONE);
   private static final SurfaceRules.RuleSource DEEPSLATE = makeStateRule(Blocks.DEEPSLATE);
   private static final SurfaceRules.RuleSource DIRT = makeStateRule(Blocks.DIRT);
   private static final SurfaceRules.RuleSource TERREDEPOMME = makeStateRule(Blocks.TERREDEPOMME);
   private static final SurfaceRules.RuleSource PODZOL = makeStateRule(Blocks.PODZOL);
   private static final SurfaceRules.RuleSource COARSE_DIRT = makeStateRule(Blocks.COARSE_DIRT);
   private static final SurfaceRules.RuleSource MYCELIUM = makeStateRule(Blocks.MYCELIUM);
   private static final SurfaceRules.RuleSource GRASS_BLOCK = makeStateRule(Blocks.GRASS_BLOCK);
   private static final SurfaceRules.RuleSource PEELGRASS_BLOCK = makeStateRule(Blocks.PEELGRASS_BLOCK);
   private static final SurfaceRules.RuleSource CORRUPTED_PEELGRASS_BLOCK = makeStateRule(Blocks.CORRUPTED_PEELGRASS_BLOCK);
   private static final SurfaceRules.RuleSource GREEN_PEELS = makeStateRule(Blocks.POTATO_PEELS_BLOCK_MAP.get(DyeColor.GREEN));
   private static final SurfaceRules.RuleSource GRAY_PEELS = makeStateRule(Blocks.POTATO_PEELS_BLOCK_MAP.get(DyeColor.GRAY));
   private static final SurfaceRules.RuleSource LIGH_GRAY_PEELS = makeStateRule(Blocks.POTATO_PEELS_BLOCK_MAP.get(DyeColor.LIGHT_GRAY));
   private static final SurfaceRules.RuleSource PEELS = makeStateRule(Blocks.POTATO_PEELS_BLOCK_MAP.get(DyeColor.WHITE));
   private static final SurfaceRules.RuleSource CALCITE = makeStateRule(Blocks.CALCITE);
   private static final SurfaceRules.RuleSource GRAVEL = makeStateRule(Blocks.GRAVEL);
   private static final SurfaceRules.RuleSource SAND = makeStateRule(Blocks.SAND);
   private static final SurfaceRules.RuleSource GRAVTATER = makeStateRule(Blocks.GRAVTATER);
   private static final SurfaceRules.RuleSource SANDSTONE = makeStateRule(Blocks.SANDSTONE);
   private static final SurfaceRules.RuleSource PACKED_ICE = makeStateRule(Blocks.PACKED_ICE);
   private static final SurfaceRules.RuleSource SNOW_BLOCK = makeStateRule(Blocks.SNOW_BLOCK);
   private static final SurfaceRules.RuleSource MUD = makeStateRule(Blocks.MUD);
   private static final SurfaceRules.RuleSource POWDER_SNOW = makeStateRule(Blocks.POWDER_SNOW);
   private static final SurfaceRules.RuleSource ICE = makeStateRule(Blocks.ICE);
   private static final SurfaceRules.RuleSource WATER = makeStateRule(Blocks.WATER);
   private static final SurfaceRules.RuleSource LAVA = makeStateRule(Blocks.LAVA);
   private static final SurfaceRules.RuleSource NETHERRACK = makeStateRule(Blocks.NETHERRACK);
   private static final SurfaceRules.RuleSource SOUL_SAND = makeStateRule(Blocks.SOUL_SAND);
   private static final SurfaceRules.RuleSource SOUL_SOIL = makeStateRule(Blocks.SOUL_SOIL);
   private static final SurfaceRules.RuleSource BASALT = makeStateRule(Blocks.BASALT);
   private static final SurfaceRules.RuleSource BLACKSTONE = makeStateRule(Blocks.BLACKSTONE);
   private static final SurfaceRules.RuleSource WARPED_WART_BLOCK = makeStateRule(Blocks.WARPED_WART_BLOCK);
   private static final SurfaceRules.RuleSource WARPED_NYLIUM = makeStateRule(Blocks.WARPED_NYLIUM);
   private static final SurfaceRules.RuleSource NETHER_WART_BLOCK = makeStateRule(Blocks.NETHER_WART_BLOCK);
   private static final SurfaceRules.RuleSource CRIMSON_NYLIUM = makeStateRule(Blocks.CRIMSON_NYLIUM);
   private static final SurfaceRules.RuleSource ENDSTONE = makeStateRule(Blocks.END_STONE);

   public SurfaceRuleData() {
      super();
   }

   private static SurfaceRules.RuleSource makeStateRule(Block var0) {
      return SurfaceRules.state(var0.defaultBlockState());
   }

   public static SurfaceRules.RuleSource overworld() {
      return overworldLike(true, false, true);
   }

   public static SurfaceRules.RuleSource overworldLike(boolean var0, boolean var1, boolean var2) {
      SurfaceRules.ConditionSource var3 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(97), 2);
      SurfaceRules.ConditionSource var4 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(256), 0);
      SurfaceRules.ConditionSource var5 = SurfaceRules.yStartCheck(VerticalAnchor.absolute(63), -1);
      SurfaceRules.ConditionSource var6 = SurfaceRules.yStartCheck(VerticalAnchor.absolute(74), 1);
      SurfaceRules.ConditionSource var7 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(60), 0);
      SurfaceRules.ConditionSource var8 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(62), 0);
      SurfaceRules.ConditionSource var9 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(63), 0);
      SurfaceRules.ConditionSource var10 = SurfaceRules.waterBlockCheck(-1, 0);
      SurfaceRules.ConditionSource var11 = SurfaceRules.waterBlockCheck(0, 0);
      SurfaceRules.ConditionSource var12 = SurfaceRules.waterStartCheck(-6, -1);
      SurfaceRules.ConditionSource var13 = SurfaceRules.hole();
      SurfaceRules.ConditionSource var14 = SurfaceRules.isBiome(Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN);
      SurfaceRules.ConditionSource var15 = SurfaceRules.steep();
      SurfaceRules.RuleSource var16 = SurfaceRules.sequence(SurfaceRules.ifTrue(var11, GRASS_BLOCK), DIRT);
      SurfaceRules.RuleSource var17 = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, SANDSTONE), SAND);
      SurfaceRules.RuleSource var18 = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, STONE), GRAVEL);
      SurfaceRules.ConditionSource var19 = SurfaceRules.isBiome(Biomes.WARM_OCEAN, Biomes.BEACH, Biomes.SNOWY_BEACH);
      SurfaceRules.ConditionSource var20 = SurfaceRules.isBiome(Biomes.DESERT);
      SurfaceRules.RuleSource var21 = SurfaceRules.sequence(
         SurfaceRules.ifTrue(
            SurfaceRules.isBiome(Biomes.STONY_PEAKS),
            SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.CALCITE, -0.0125, 0.0125), CALCITE), STONE)
         ),
         SurfaceRules.ifTrue(
            SurfaceRules.isBiome(Biomes.STONY_SHORE),
            SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.GRAVEL, -0.05, 0.05), var18), STONE)
         ),
         SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WINDSWEPT_HILLS), SurfaceRules.ifTrue(surfaceNoiseAbove(1.0), STONE)),
         SurfaceRules.ifTrue(var19, var17),
         SurfaceRules.ifTrue(var20, var17),
         SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.DRIPSTONE_CAVES), STONE)
      );
      SurfaceRules.RuleSource var22 = SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.POWDER_SNOW, 0.45, 0.58), SurfaceRules.ifTrue(var11, POWDER_SNOW));
      SurfaceRules.RuleSource var23 = SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.POWDER_SNOW, 0.35, 0.6), SurfaceRules.ifTrue(var11, POWDER_SNOW));
      SurfaceRules.RuleSource var24 = SurfaceRules.sequence(
         SurfaceRules.ifTrue(
            SurfaceRules.isBiome(Biomes.FROZEN_PEAKS),
            SurfaceRules.sequence(
               SurfaceRules.ifTrue(var15, PACKED_ICE),
               SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PACKED_ICE, -0.5, 0.2), PACKED_ICE),
               SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.ICE, -0.0625, 0.025), ICE),
               SurfaceRules.ifTrue(var11, SNOW_BLOCK)
            )
         ),
         SurfaceRules.ifTrue(
            SurfaceRules.isBiome(Biomes.SNOWY_SLOPES), SurfaceRules.sequence(SurfaceRules.ifTrue(var15, STONE), var22, SurfaceRules.ifTrue(var11, SNOW_BLOCK))
         ),
         SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.JAGGED_PEAKS), STONE),
         SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.GROVE), SurfaceRules.sequence(var22, DIRT)),
         var21,
         SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WINDSWEPT_SAVANNA), SurfaceRules.ifTrue(surfaceNoiseAbove(1.75), STONE)),
         SurfaceRules.ifTrue(
            SurfaceRules.isBiome(Biomes.WINDSWEPT_GRAVELLY_HILLS),
            SurfaceRules.sequence(
               SurfaceRules.ifTrue(surfaceNoiseAbove(2.0), var18),
               SurfaceRules.ifTrue(surfaceNoiseAbove(1.0), STONE),
               SurfaceRules.ifTrue(surfaceNoiseAbove(-1.0), DIRT),
               var18
            )
         ),
         SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.MANGROVE_SWAMP), MUD),
         DIRT
      );
      SurfaceRules.RuleSource var25 = SurfaceRules.sequence(
         SurfaceRules.ifTrue(
            SurfaceRules.isBiome(Biomes.FROZEN_PEAKS),
            SurfaceRules.sequence(
               SurfaceRules.ifTrue(var15, PACKED_ICE),
               SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PACKED_ICE, 0.0, 0.2), PACKED_ICE),
               SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.ICE, 0.0, 0.025), ICE),
               SurfaceRules.ifTrue(var11, SNOW_BLOCK)
            )
         ),
         SurfaceRules.ifTrue(
            SurfaceRules.isBiome(Biomes.SNOWY_SLOPES), SurfaceRules.sequence(SurfaceRules.ifTrue(var15, STONE), var23, SurfaceRules.ifTrue(var11, SNOW_BLOCK))
         ),
         SurfaceRules.ifTrue(
            SurfaceRules.isBiome(Biomes.JAGGED_PEAKS), SurfaceRules.sequence(SurfaceRules.ifTrue(var15, STONE), SurfaceRules.ifTrue(var11, SNOW_BLOCK))
         ),
         SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.GROVE), SurfaceRules.sequence(var23, SurfaceRules.ifTrue(var11, SNOW_BLOCK))),
         var21,
         SurfaceRules.ifTrue(
            SurfaceRules.isBiome(Biomes.WINDSWEPT_SAVANNA),
            SurfaceRules.sequence(SurfaceRules.ifTrue(surfaceNoiseAbove(1.75), STONE), SurfaceRules.ifTrue(surfaceNoiseAbove(-0.5), COARSE_DIRT))
         ),
         SurfaceRules.ifTrue(
            SurfaceRules.isBiome(Biomes.WINDSWEPT_GRAVELLY_HILLS),
            SurfaceRules.sequence(
               SurfaceRules.ifTrue(surfaceNoiseAbove(2.0), var18),
               SurfaceRules.ifTrue(surfaceNoiseAbove(1.0), STONE),
               SurfaceRules.ifTrue(surfaceNoiseAbove(-1.0), var16),
               var18
            )
         ),
         SurfaceRules.ifTrue(
            SurfaceRules.isBiome(Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA),
            SurfaceRules.sequence(SurfaceRules.ifTrue(surfaceNoiseAbove(1.75), COARSE_DIRT), SurfaceRules.ifTrue(surfaceNoiseAbove(-0.95), PODZOL))
         ),
         SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.ICE_SPIKES), SurfaceRules.ifTrue(var11, SNOW_BLOCK)),
         SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.MANGROVE_SWAMP), MUD),
         SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.MUSHROOM_FIELDS), MYCELIUM),
         var16
      );
      SurfaceRules.ConditionSource var26 = SurfaceRules.noiseCondition(Noises.SURFACE, -0.909, -0.5454);
      SurfaceRules.ConditionSource var27 = SurfaceRules.noiseCondition(Noises.SURFACE, -0.1818, 0.1818);
      SurfaceRules.ConditionSource var28 = SurfaceRules.noiseCondition(Noises.SURFACE, 0.5454, 0.909);
      SurfaceRules.RuleSource var29 = SurfaceRules.sequence(
         SurfaceRules.ifTrue(
            SurfaceRules.ON_FLOOR,
            SurfaceRules.sequence(
               SurfaceRules.ifTrue(
                  SurfaceRules.isBiome(Biomes.WOODED_BADLANDS),
                  SurfaceRules.ifTrue(
                     var3,
                     SurfaceRules.sequence(
                        SurfaceRules.ifTrue(var26, COARSE_DIRT), SurfaceRules.ifTrue(var27, COARSE_DIRT), SurfaceRules.ifTrue(var28, COARSE_DIRT), var16
                     )
                  )
               ),
               SurfaceRules.ifTrue(
                  SurfaceRules.isBiome(Biomes.SWAMP),
                  SurfaceRules.ifTrue(
                     var8, SurfaceRules.ifTrue(SurfaceRules.not(var9), SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SWAMP, 0.0), WATER))
                  )
               ),
               SurfaceRules.ifTrue(
                  SurfaceRules.isBiome(Biomes.MANGROVE_SWAMP),
                  SurfaceRules.ifTrue(
                     var7, SurfaceRules.ifTrue(SurfaceRules.not(var9), SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SWAMP, 0.0), WATER))
                  )
               )
            )
         ),
         SurfaceRules.ifTrue(
            SurfaceRules.isBiome(Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.WOODED_BADLANDS),
            SurfaceRules.sequence(
               SurfaceRules.ifTrue(
                  SurfaceRules.ON_FLOOR,
                  SurfaceRules.sequence(
                     SurfaceRules.ifTrue(var4, ORANGE_TERRACOTTA),
                     SurfaceRules.ifTrue(
                        var6,
                        SurfaceRules.sequence(
                           SurfaceRules.ifTrue(var26, TERRACOTTA),
                           SurfaceRules.ifTrue(var27, TERRACOTTA),
                           SurfaceRules.ifTrue(var28, TERRACOTTA),
                           SurfaceRules.bandlands()
                        )
                     ),
                     SurfaceRules.ifTrue(var10, SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, RED_SANDSTONE), RED_SAND)),
                     SurfaceRules.ifTrue(SurfaceRules.not(var13), ORANGE_TERRACOTTA),
                     SurfaceRules.ifTrue(var12, WHITE_TERRACOTTA),
                     var18
                  )
               ),
               SurfaceRules.ifTrue(
                  var5,
                  SurfaceRules.sequence(SurfaceRules.ifTrue(var9, SurfaceRules.ifTrue(SurfaceRules.not(var6), ORANGE_TERRACOTTA)), SurfaceRules.bandlands())
               ),
               SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, SurfaceRules.ifTrue(var12, WHITE_TERRACOTTA))
            )
         ),
         SurfaceRules.ifTrue(
            SurfaceRules.ON_FLOOR,
            SurfaceRules.ifTrue(
               var10,
               SurfaceRules.sequence(
                  SurfaceRules.ifTrue(
                     var14,
                     SurfaceRules.ifTrue(
                        var13, SurfaceRules.sequence(SurfaceRules.ifTrue(var11, AIR), SurfaceRules.ifTrue(SurfaceRules.temperature(), ICE), WATER)
                     )
                  ),
                  var25
               )
            )
         ),
         SurfaceRules.ifTrue(
            var12,
            SurfaceRules.sequence(
               SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.ifTrue(var14, SurfaceRules.ifTrue(var13, WATER))),
               SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, var24),
               SurfaceRules.ifTrue(var19, SurfaceRules.ifTrue(SurfaceRules.DEEP_UNDER_FLOOR, SANDSTONE)),
               SurfaceRules.ifTrue(var20, SurfaceRules.ifTrue(SurfaceRules.VERY_DEEP_UNDER_FLOOR, SANDSTONE))
            )
         ),
         SurfaceRules.ifTrue(
            SurfaceRules.ON_FLOOR,
            SurfaceRules.sequence(
               SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS), STONE),
               SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN), var17),
               var18
            )
         )
      );
      Builder var30 = ImmutableList.builder();
      if (var1) {
         var30.add(
            SurfaceRules.ifTrue(SurfaceRules.not(SurfaceRules.verticalGradient("bedrock_roof", VerticalAnchor.belowTop(5), VerticalAnchor.top())), BEDROCK)
         );
      }

      if (var2) {
         var30.add(SurfaceRules.ifTrue(SurfaceRules.verticalGradient("bedrock_floor", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5)), BEDROCK));
      }

      SurfaceRules.RuleSource var31 = SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(), var29);
      var30.add(var0 ? var31 : var29);
      var30.add(SurfaceRules.ifTrue(SurfaceRules.verticalGradient("deepslate", VerticalAnchor.absolute(0), VerticalAnchor.absolute(8)), DEEPSLATE));
      return SurfaceRules.sequence((SurfaceRules.RuleSource[])var30.build().toArray(var0x -> new SurfaceRules.RuleSource[var0x]));
   }

   public static SurfaceRules.RuleSource potato() {
      SurfaceRules.RuleSource var0 = SurfaceRules.sequence(
         SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.HASH), SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, TERREDEPOMME), GRAVTATER))
      );
      SurfaceRules.RuleSource var1 = SurfaceRules.sequence(
         SurfaceRules.ifTrue(
            SurfaceRules.ON_FLOOR,
            SurfaceRules.sequence(
               var0,
               SurfaceRules.ifTrue(
                  SurfaceRules.isBiome(Biomes.WASTELAND),
                  SurfaceRules.sequence(
                     SurfaceRules.ifTrue(surfaceNoiseAbove(2.0), CORRUPTED_PEELGRASS_BLOCK),
                     SurfaceRules.ifTrue(surfaceNoiseAbove(1.0), GREEN_PEELS),
                     SurfaceRules.ifTrue(surfaceNoiseAbove(0.0), GRAY_PEELS),
                     SurfaceRules.ifTrue(surfaceNoiseAbove(-1.0), LIGH_GRAY_PEELS)
                  )
               ),
               SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.CORRUPTION), CORRUPTED_PEELGRASS_BLOCK),
               PEELGRASS_BLOCK
            )
         ),
         SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, SurfaceRules.sequence(var0, TERREDEPOMME))
      );
      Builder var2 = ImmutableList.builder();
      var2.add(var1);
      return SurfaceRules.sequence((SurfaceRules.RuleSource[])var2.build().toArray(var0x -> new SurfaceRules.RuleSource[var0x]));
   }

   public static SurfaceRules.RuleSource nether() {
      SurfaceRules.ConditionSource var0 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(31), 0);
      SurfaceRules.ConditionSource var1 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(32), 0);
      SurfaceRules.ConditionSource var2 = SurfaceRules.yStartCheck(VerticalAnchor.absolute(30), 0);
      SurfaceRules.ConditionSource var3 = SurfaceRules.not(SurfaceRules.yStartCheck(VerticalAnchor.absolute(35), 0));
      SurfaceRules.ConditionSource var4 = SurfaceRules.yBlockCheck(VerticalAnchor.belowTop(5), 0);
      SurfaceRules.ConditionSource var5 = SurfaceRules.hole();
      SurfaceRules.ConditionSource var6 = SurfaceRules.noiseCondition(Noises.SOUL_SAND_LAYER, -0.012);
      SurfaceRules.ConditionSource var7 = SurfaceRules.noiseCondition(Noises.GRAVEL_LAYER, -0.012);
      SurfaceRules.ConditionSource var8 = SurfaceRules.noiseCondition(Noises.PATCH, -0.012);
      SurfaceRules.ConditionSource var9 = SurfaceRules.noiseCondition(Noises.NETHERRACK, 0.54);
      SurfaceRules.ConditionSource var10 = SurfaceRules.noiseCondition(Noises.NETHER_WART, 1.17);
      SurfaceRules.ConditionSource var11 = SurfaceRules.noiseCondition(Noises.NETHER_STATE_SELECTOR, 0.0);
      SurfaceRules.RuleSource var12 = SurfaceRules.ifTrue(var8, SurfaceRules.ifTrue(var2, SurfaceRules.ifTrue(var3, GRAVEL)));
      return SurfaceRules.sequence(
         SurfaceRules.ifTrue(SurfaceRules.verticalGradient("bedrock_floor", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5)), BEDROCK),
         SurfaceRules.ifTrue(SurfaceRules.not(SurfaceRules.verticalGradient("bedrock_roof", VerticalAnchor.belowTop(5), VerticalAnchor.top())), BEDROCK),
         SurfaceRules.ifTrue(var4, NETHERRACK),
         SurfaceRules.ifTrue(
            SurfaceRules.isBiome(Biomes.BASALT_DELTAS),
            SurfaceRules.sequence(
               SurfaceRules.ifTrue(SurfaceRules.UNDER_CEILING, BASALT),
               SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, SurfaceRules.sequence(var12, SurfaceRules.ifTrue(var11, BASALT), BLACKSTONE))
            )
         ),
         SurfaceRules.ifTrue(
            SurfaceRules.isBiome(Biomes.SOUL_SAND_VALLEY),
            SurfaceRules.sequence(
               SurfaceRules.ifTrue(SurfaceRules.UNDER_CEILING, SurfaceRules.sequence(SurfaceRules.ifTrue(var11, SOUL_SAND), SOUL_SOIL)),
               SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, SurfaceRules.sequence(var12, SurfaceRules.ifTrue(var11, SOUL_SAND), SOUL_SOIL))
            )
         ),
         SurfaceRules.ifTrue(
            SurfaceRules.ON_FLOOR,
            SurfaceRules.sequence(
               SurfaceRules.ifTrue(SurfaceRules.not(var1), SurfaceRules.ifTrue(var5, LAVA)),
               SurfaceRules.ifTrue(
                  SurfaceRules.isBiome(Biomes.WARPED_FOREST),
                  SurfaceRules.ifTrue(
                     SurfaceRules.not(var9), SurfaceRules.ifTrue(var0, SurfaceRules.sequence(SurfaceRules.ifTrue(var10, WARPED_WART_BLOCK), WARPED_NYLIUM))
                  )
               ),
               SurfaceRules.ifTrue(
                  SurfaceRules.isBiome(Biomes.CRIMSON_FOREST),
                  SurfaceRules.ifTrue(
                     SurfaceRules.not(var9), SurfaceRules.ifTrue(var0, SurfaceRules.sequence(SurfaceRules.ifTrue(var10, NETHER_WART_BLOCK), CRIMSON_NYLIUM))
                  )
               )
            )
         ),
         SurfaceRules.ifTrue(
            SurfaceRules.isBiome(Biomes.NETHER_WASTES),
            SurfaceRules.sequence(
               SurfaceRules.ifTrue(
                  SurfaceRules.UNDER_FLOOR,
                  SurfaceRules.ifTrue(
                     var6,
                     SurfaceRules.sequence(
                        SurfaceRules.ifTrue(SurfaceRules.not(var5), SurfaceRules.ifTrue(var2, SurfaceRules.ifTrue(var3, SOUL_SAND))), NETHERRACK
                     )
                  )
               ),
               SurfaceRules.ifTrue(
                  SurfaceRules.ON_FLOOR,
                  SurfaceRules.ifTrue(
                     var0,
                     SurfaceRules.ifTrue(
                        var3,
                        SurfaceRules.ifTrue(var7, SurfaceRules.sequence(SurfaceRules.ifTrue(var1, GRAVEL), SurfaceRules.ifTrue(SurfaceRules.not(var5), GRAVEL)))
                     )
                  )
               )
            )
         ),
         NETHERRACK
      );
   }

   public static SurfaceRules.RuleSource end() {
      return ENDSTONE;
   }

   public static SurfaceRules.RuleSource air() {
      return AIR;
   }

   private static SurfaceRules.ConditionSource surfaceNoiseAbove(double var0) {
      return SurfaceRules.noiseCondition(Noises.SURFACE, var0 / 8.25, 1.7976931348623157E308);
   }
}
