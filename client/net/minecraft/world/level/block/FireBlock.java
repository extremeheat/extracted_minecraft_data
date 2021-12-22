package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FireBlock extends BaseFireBlock {
   public static final int MAX_AGE = 15;
   public static final IntegerProperty AGE;
   public static final BooleanProperty NORTH;
   public static final BooleanProperty EAST;
   public static final BooleanProperty SOUTH;
   public static final BooleanProperty WEST;
   // $FF: renamed from: UP net.minecraft.world.level.block.state.properties.BooleanProperty
   public static final BooleanProperty field_63;
   private static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION;
   private static final VoxelShape UP_AABB;
   private static final VoxelShape WEST_AABB;
   private static final VoxelShape EAST_AABB;
   private static final VoxelShape NORTH_AABB;
   private static final VoxelShape SOUTH_AABB;
   private final Map<BlockState, VoxelShape> shapesCache;
   private static final int FLAME_INSTANT = 60;
   private static final int FLAME_EASY = 30;
   private static final int FLAME_MEDIUM = 15;
   private static final int FLAME_HARD = 5;
   private static final int BURN_INSTANT = 100;
   private static final int BURN_EASY = 60;
   private static final int BURN_MEDIUM = 20;
   private static final int BURN_HARD = 5;
   private final Object2IntMap<Block> flameOdds = new Object2IntOpenHashMap();
   private final Object2IntMap<Block> burnOdds = new Object2IntOpenHashMap();

   public FireBlock(BlockBehaviour.Properties var1) {
      super(var1, 1.0F);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0)).setValue(NORTH, false)).setValue(EAST, false)).setValue(SOUTH, false)).setValue(WEST, false)).setValue(field_63, false));
      this.shapesCache = ImmutableMap.copyOf((Map)this.stateDefinition.getPossibleStates().stream().filter((var0) -> {
         return (Integer)var0.getValue(AGE) == 0;
      }).collect(Collectors.toMap(Function.identity(), FireBlock::calculateShape)));
   }

   private static VoxelShape calculateShape(BlockState var0) {
      VoxelShape var1 = Shapes.empty();
      if ((Boolean)var0.getValue(field_63)) {
         var1 = UP_AABB;
      }

      if ((Boolean)var0.getValue(NORTH)) {
         var1 = Shapes.method_31(var1, NORTH_AABB);
      }

      if ((Boolean)var0.getValue(SOUTH)) {
         var1 = Shapes.method_31(var1, SOUTH_AABB);
      }

      if ((Boolean)var0.getValue(EAST)) {
         var1 = Shapes.method_31(var1, EAST_AABB);
      }

      if ((Boolean)var0.getValue(WEST)) {
         var1 = Shapes.method_31(var1, WEST_AABB);
      }

      return var1.isEmpty() ? DOWN_AABB : var1;
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      return this.canSurvive(var1, var4, var5) ? this.getStateWithAge(var4, var5, (Integer)var1.getValue(AGE)) : Blocks.AIR.defaultBlockState();
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return (VoxelShape)this.shapesCache.get(var1.setValue(AGE, 0));
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return this.getStateForPlacement(var1.getLevel(), var1.getClickedPos());
   }

   protected BlockState getStateForPlacement(BlockGetter var1, BlockPos var2) {
      BlockPos var3 = var2.below();
      BlockState var4 = var1.getBlockState(var3);
      if (!this.canBurn(var4) && !var4.isFaceSturdy(var1, var3, Direction.field_526)) {
         BlockState var5 = this.defaultBlockState();
         Direction[] var6 = Direction.values();
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            Direction var9 = var6[var8];
            BooleanProperty var10 = (BooleanProperty)PROPERTY_BY_DIRECTION.get(var9);
            if (var10 != null) {
               var5 = (BlockState)var5.setValue(var10, this.canBurn(var1.getBlockState(var2.relative(var9))));
            }
         }

         return var5;
      } else {
         return this.defaultBlockState();
      }
   }

   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      BlockPos var4 = var3.below();
      return var2.getBlockState(var4).isFaceSturdy(var2, var4, Direction.field_526) || this.isValidFireLocation(var2, var3);
   }

   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, Random var4) {
      var2.scheduleTick(var3, this, getFireTickDelay(var2.random));
      if (var2.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
         if (!var1.canSurvive(var2, var3)) {
            var2.removeBlock(var3, false);
         }

         BlockState var5 = var2.getBlockState(var3.below());
         boolean var6 = var5.is(var2.dimensionType().infiniburn());
         int var7 = (Integer)var1.getValue(AGE);
         if (!var6 && var2.isRaining() && this.isNearRain(var2, var3) && var4.nextFloat() < 0.2F + (float)var7 * 0.03F) {
            var2.removeBlock(var3, false);
         } else {
            int var8 = Math.min(15, var7 + var4.nextInt(3) / 2);
            if (var7 != var8) {
               var1 = (BlockState)var1.setValue(AGE, var8);
               var2.setBlock(var3, var1, 4);
            }

            if (!var6) {
               if (!this.isValidFireLocation(var2, var3)) {
                  BlockPos var19 = var3.below();
                  if (!var2.getBlockState(var19).isFaceSturdy(var2, var19, Direction.field_526) || var7 > 3) {
                     var2.removeBlock(var3, false);
                  }

                  return;
               }

               if (var7 == 15 && var4.nextInt(4) == 0 && !this.canBurn(var2.getBlockState(var3.below()))) {
                  var2.removeBlock(var3, false);
                  return;
               }
            }

            boolean var9 = var2.isHumidAt(var3);
            int var10 = var9 ? -50 : 0;
            this.checkBurnOut(var2, var3.east(), 300 + var10, var4, var7);
            this.checkBurnOut(var2, var3.west(), 300 + var10, var4, var7);
            this.checkBurnOut(var2, var3.below(), 250 + var10, var4, var7);
            this.checkBurnOut(var2, var3.above(), 250 + var10, var4, var7);
            this.checkBurnOut(var2, var3.north(), 300 + var10, var4, var7);
            this.checkBurnOut(var2, var3.south(), 300 + var10, var4, var7);
            BlockPos.MutableBlockPos var11 = new BlockPos.MutableBlockPos();

            for(int var12 = -1; var12 <= 1; ++var12) {
               for(int var13 = -1; var13 <= 1; ++var13) {
                  for(int var14 = -1; var14 <= 4; ++var14) {
                     if (var12 != 0 || var14 != 0 || var13 != 0) {
                        int var15 = 100;
                        if (var14 > 1) {
                           var15 += (var14 - 1) * 100;
                        }

                        var11.setWithOffset(var3, var12, var14, var13);
                        int var16 = this.getFireOdds(var2, var11);
                        if (var16 > 0) {
                           int var17 = (var16 + 40 + var2.getDifficulty().getId() * 7) / (var7 + 30);
                           if (var9) {
                              var17 /= 2;
                           }

                           if (var17 > 0 && var4.nextInt(var15) <= var17 && (!var2.isRaining() || !this.isNearRain(var2, var11))) {
                              int var18 = Math.min(15, var7 + var4.nextInt(5) / 4);
                              var2.setBlock(var11, this.getStateWithAge(var2, var11, var18), 3);
                           }
                        }
                     }
                  }
               }
            }

         }
      }
   }

   protected boolean isNearRain(Level var1, BlockPos var2) {
      return var1.isRainingAt(var2) || var1.isRainingAt(var2.west()) || var1.isRainingAt(var2.east()) || var1.isRainingAt(var2.north()) || var1.isRainingAt(var2.south());
   }

   private int getBurnOdd(BlockState var1) {
      return var1.hasProperty(BlockStateProperties.WATERLOGGED) && (Boolean)var1.getValue(BlockStateProperties.WATERLOGGED) ? 0 : this.burnOdds.getInt(var1.getBlock());
   }

   private int getFlameOdds(BlockState var1) {
      return var1.hasProperty(BlockStateProperties.WATERLOGGED) && (Boolean)var1.getValue(BlockStateProperties.WATERLOGGED) ? 0 : this.flameOdds.getInt(var1.getBlock());
   }

   private void checkBurnOut(Level var1, BlockPos var2, int var3, Random var4, int var5) {
      int var6 = this.getBurnOdd(var1.getBlockState(var2));
      if (var4.nextInt(var3) < var6) {
         BlockState var7 = var1.getBlockState(var2);
         if (var4.nextInt(var5 + 10) < 5 && !var1.isRainingAt(var2)) {
            int var8 = Math.min(var5 + var4.nextInt(5) / 4, 15);
            var1.setBlock(var2, this.getStateWithAge(var1, var2, var8), 3);
         } else {
            var1.removeBlock(var2, false);
         }

         Block var9 = var7.getBlock();
         if (var9 instanceof TntBlock) {
            TntBlock var10000 = (TntBlock)var9;
            TntBlock.explode(var1, var2);
         }
      }

   }

   private BlockState getStateWithAge(LevelAccessor var1, BlockPos var2, int var3) {
      BlockState var4 = getState(var1, var2);
      return var4.is(Blocks.FIRE) ? (BlockState)var4.setValue(AGE, var3) : var4;
   }

   private boolean isValidFireLocation(BlockGetter var1, BlockPos var2) {
      Direction[] var3 = Direction.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Direction var6 = var3[var5];
         if (this.canBurn(var1.getBlockState(var2.relative(var6)))) {
            return true;
         }
      }

      return false;
   }

   private int getFireOdds(LevelReader var1, BlockPos var2) {
      if (!var1.isEmptyBlock(var2)) {
         return 0;
      } else {
         int var3 = 0;
         Direction[] var4 = Direction.values();
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Direction var7 = var4[var6];
            BlockState var8 = var1.getBlockState(var2.relative(var7));
            var3 = Math.max(this.getFlameOdds(var8), var3);
         }

         return var3;
      }
   }

   protected boolean canBurn(BlockState var1) {
      return this.getFlameOdds(var1) > 0;
   }

   public void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      super.onPlace(var1, var2, var3, var4, var5);
      var2.scheduleTick(var3, this, getFireTickDelay(var2.random));
   }

   private static int getFireTickDelay(Random var0) {
      return 30 + var0.nextInt(10);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(AGE, NORTH, EAST, SOUTH, WEST, field_63);
   }

   private void setFlammable(Block var1, int var2, int var3) {
      this.flameOdds.put(var1, var2);
      this.burnOdds.put(var1, var3);
   }

   public static void bootStrap() {
      FireBlock var0 = (FireBlock)Blocks.FIRE;
      var0.setFlammable(Blocks.OAK_PLANKS, 5, 20);
      var0.setFlammable(Blocks.SPRUCE_PLANKS, 5, 20);
      var0.setFlammable(Blocks.BIRCH_PLANKS, 5, 20);
      var0.setFlammable(Blocks.JUNGLE_PLANKS, 5, 20);
      var0.setFlammable(Blocks.ACACIA_PLANKS, 5, 20);
      var0.setFlammable(Blocks.DARK_OAK_PLANKS, 5, 20);
      var0.setFlammable(Blocks.OAK_SLAB, 5, 20);
      var0.setFlammable(Blocks.SPRUCE_SLAB, 5, 20);
      var0.setFlammable(Blocks.BIRCH_SLAB, 5, 20);
      var0.setFlammable(Blocks.JUNGLE_SLAB, 5, 20);
      var0.setFlammable(Blocks.ACACIA_SLAB, 5, 20);
      var0.setFlammable(Blocks.DARK_OAK_SLAB, 5, 20);
      var0.setFlammable(Blocks.OAK_FENCE_GATE, 5, 20);
      var0.setFlammable(Blocks.SPRUCE_FENCE_GATE, 5, 20);
      var0.setFlammable(Blocks.BIRCH_FENCE_GATE, 5, 20);
      var0.setFlammable(Blocks.JUNGLE_FENCE_GATE, 5, 20);
      var0.setFlammable(Blocks.DARK_OAK_FENCE_GATE, 5, 20);
      var0.setFlammable(Blocks.ACACIA_FENCE_GATE, 5, 20);
      var0.setFlammable(Blocks.OAK_FENCE, 5, 20);
      var0.setFlammable(Blocks.SPRUCE_FENCE, 5, 20);
      var0.setFlammable(Blocks.BIRCH_FENCE, 5, 20);
      var0.setFlammable(Blocks.JUNGLE_FENCE, 5, 20);
      var0.setFlammable(Blocks.DARK_OAK_FENCE, 5, 20);
      var0.setFlammable(Blocks.ACACIA_FENCE, 5, 20);
      var0.setFlammable(Blocks.OAK_STAIRS, 5, 20);
      var0.setFlammable(Blocks.BIRCH_STAIRS, 5, 20);
      var0.setFlammable(Blocks.SPRUCE_STAIRS, 5, 20);
      var0.setFlammable(Blocks.JUNGLE_STAIRS, 5, 20);
      var0.setFlammable(Blocks.ACACIA_STAIRS, 5, 20);
      var0.setFlammable(Blocks.DARK_OAK_STAIRS, 5, 20);
      var0.setFlammable(Blocks.OAK_LOG, 5, 5);
      var0.setFlammable(Blocks.SPRUCE_LOG, 5, 5);
      var0.setFlammable(Blocks.BIRCH_LOG, 5, 5);
      var0.setFlammable(Blocks.JUNGLE_LOG, 5, 5);
      var0.setFlammable(Blocks.ACACIA_LOG, 5, 5);
      var0.setFlammable(Blocks.DARK_OAK_LOG, 5, 5);
      var0.setFlammable(Blocks.STRIPPED_OAK_LOG, 5, 5);
      var0.setFlammable(Blocks.STRIPPED_SPRUCE_LOG, 5, 5);
      var0.setFlammable(Blocks.STRIPPED_BIRCH_LOG, 5, 5);
      var0.setFlammable(Blocks.STRIPPED_JUNGLE_LOG, 5, 5);
      var0.setFlammable(Blocks.STRIPPED_ACACIA_LOG, 5, 5);
      var0.setFlammable(Blocks.STRIPPED_DARK_OAK_LOG, 5, 5);
      var0.setFlammable(Blocks.STRIPPED_OAK_WOOD, 5, 5);
      var0.setFlammable(Blocks.STRIPPED_SPRUCE_WOOD, 5, 5);
      var0.setFlammable(Blocks.STRIPPED_BIRCH_WOOD, 5, 5);
      var0.setFlammable(Blocks.STRIPPED_JUNGLE_WOOD, 5, 5);
      var0.setFlammable(Blocks.STRIPPED_ACACIA_WOOD, 5, 5);
      var0.setFlammable(Blocks.STRIPPED_DARK_OAK_WOOD, 5, 5);
      var0.setFlammable(Blocks.OAK_WOOD, 5, 5);
      var0.setFlammable(Blocks.SPRUCE_WOOD, 5, 5);
      var0.setFlammable(Blocks.BIRCH_WOOD, 5, 5);
      var0.setFlammable(Blocks.JUNGLE_WOOD, 5, 5);
      var0.setFlammable(Blocks.ACACIA_WOOD, 5, 5);
      var0.setFlammable(Blocks.DARK_OAK_WOOD, 5, 5);
      var0.setFlammable(Blocks.OAK_LEAVES, 30, 60);
      var0.setFlammable(Blocks.SPRUCE_LEAVES, 30, 60);
      var0.setFlammable(Blocks.BIRCH_LEAVES, 30, 60);
      var0.setFlammable(Blocks.JUNGLE_LEAVES, 30, 60);
      var0.setFlammable(Blocks.ACACIA_LEAVES, 30, 60);
      var0.setFlammable(Blocks.DARK_OAK_LEAVES, 30, 60);
      var0.setFlammable(Blocks.BOOKSHELF, 30, 20);
      var0.setFlammable(Blocks.TNT, 15, 100);
      var0.setFlammable(Blocks.GRASS, 60, 100);
      var0.setFlammable(Blocks.FERN, 60, 100);
      var0.setFlammable(Blocks.DEAD_BUSH, 60, 100);
      var0.setFlammable(Blocks.SUNFLOWER, 60, 100);
      var0.setFlammable(Blocks.LILAC, 60, 100);
      var0.setFlammable(Blocks.ROSE_BUSH, 60, 100);
      var0.setFlammable(Blocks.PEONY, 60, 100);
      var0.setFlammable(Blocks.TALL_GRASS, 60, 100);
      var0.setFlammable(Blocks.LARGE_FERN, 60, 100);
      var0.setFlammable(Blocks.DANDELION, 60, 100);
      var0.setFlammable(Blocks.POPPY, 60, 100);
      var0.setFlammable(Blocks.BLUE_ORCHID, 60, 100);
      var0.setFlammable(Blocks.ALLIUM, 60, 100);
      var0.setFlammable(Blocks.AZURE_BLUET, 60, 100);
      var0.setFlammable(Blocks.RED_TULIP, 60, 100);
      var0.setFlammable(Blocks.ORANGE_TULIP, 60, 100);
      var0.setFlammable(Blocks.WHITE_TULIP, 60, 100);
      var0.setFlammable(Blocks.PINK_TULIP, 60, 100);
      var0.setFlammable(Blocks.OXEYE_DAISY, 60, 100);
      var0.setFlammable(Blocks.CORNFLOWER, 60, 100);
      var0.setFlammable(Blocks.LILY_OF_THE_VALLEY, 60, 100);
      var0.setFlammable(Blocks.WITHER_ROSE, 60, 100);
      var0.setFlammable(Blocks.WHITE_WOOL, 30, 60);
      var0.setFlammable(Blocks.ORANGE_WOOL, 30, 60);
      var0.setFlammable(Blocks.MAGENTA_WOOL, 30, 60);
      var0.setFlammable(Blocks.LIGHT_BLUE_WOOL, 30, 60);
      var0.setFlammable(Blocks.YELLOW_WOOL, 30, 60);
      var0.setFlammable(Blocks.LIME_WOOL, 30, 60);
      var0.setFlammable(Blocks.PINK_WOOL, 30, 60);
      var0.setFlammable(Blocks.GRAY_WOOL, 30, 60);
      var0.setFlammable(Blocks.LIGHT_GRAY_WOOL, 30, 60);
      var0.setFlammable(Blocks.CYAN_WOOL, 30, 60);
      var0.setFlammable(Blocks.PURPLE_WOOL, 30, 60);
      var0.setFlammable(Blocks.BLUE_WOOL, 30, 60);
      var0.setFlammable(Blocks.BROWN_WOOL, 30, 60);
      var0.setFlammable(Blocks.GREEN_WOOL, 30, 60);
      var0.setFlammable(Blocks.RED_WOOL, 30, 60);
      var0.setFlammable(Blocks.BLACK_WOOL, 30, 60);
      var0.setFlammable(Blocks.VINE, 15, 100);
      var0.setFlammable(Blocks.COAL_BLOCK, 5, 5);
      var0.setFlammable(Blocks.HAY_BLOCK, 60, 20);
      var0.setFlammable(Blocks.TARGET, 15, 20);
      var0.setFlammable(Blocks.WHITE_CARPET, 60, 20);
      var0.setFlammable(Blocks.ORANGE_CARPET, 60, 20);
      var0.setFlammable(Blocks.MAGENTA_CARPET, 60, 20);
      var0.setFlammable(Blocks.LIGHT_BLUE_CARPET, 60, 20);
      var0.setFlammable(Blocks.YELLOW_CARPET, 60, 20);
      var0.setFlammable(Blocks.LIME_CARPET, 60, 20);
      var0.setFlammable(Blocks.PINK_CARPET, 60, 20);
      var0.setFlammable(Blocks.GRAY_CARPET, 60, 20);
      var0.setFlammable(Blocks.LIGHT_GRAY_CARPET, 60, 20);
      var0.setFlammable(Blocks.CYAN_CARPET, 60, 20);
      var0.setFlammable(Blocks.PURPLE_CARPET, 60, 20);
      var0.setFlammable(Blocks.BLUE_CARPET, 60, 20);
      var0.setFlammable(Blocks.BROWN_CARPET, 60, 20);
      var0.setFlammable(Blocks.GREEN_CARPET, 60, 20);
      var0.setFlammable(Blocks.RED_CARPET, 60, 20);
      var0.setFlammable(Blocks.BLACK_CARPET, 60, 20);
      var0.setFlammable(Blocks.DRIED_KELP_BLOCK, 30, 60);
      var0.setFlammable(Blocks.BAMBOO, 60, 60);
      var0.setFlammable(Blocks.SCAFFOLDING, 60, 60);
      var0.setFlammable(Blocks.LECTERN, 30, 20);
      var0.setFlammable(Blocks.COMPOSTER, 5, 20);
      var0.setFlammable(Blocks.SWEET_BERRY_BUSH, 60, 100);
      var0.setFlammable(Blocks.BEEHIVE, 5, 20);
      var0.setFlammable(Blocks.BEE_NEST, 30, 20);
      var0.setFlammable(Blocks.AZALEA_LEAVES, 30, 60);
      var0.setFlammable(Blocks.FLOWERING_AZALEA_LEAVES, 30, 60);
      var0.setFlammable(Blocks.CAVE_VINES, 15, 60);
      var0.setFlammable(Blocks.CAVE_VINES_PLANT, 15, 60);
      var0.setFlammable(Blocks.SPORE_BLOSSOM, 60, 100);
      var0.setFlammable(Blocks.AZALEA, 30, 60);
      var0.setFlammable(Blocks.FLOWERING_AZALEA, 30, 60);
      var0.setFlammable(Blocks.BIG_DRIPLEAF, 60, 100);
      var0.setFlammable(Blocks.BIG_DRIPLEAF_STEM, 60, 100);
      var0.setFlammable(Blocks.SMALL_DRIPLEAF, 60, 100);
      var0.setFlammable(Blocks.HANGING_ROOTS, 30, 60);
      var0.setFlammable(Blocks.GLOW_LICHEN, 15, 100);
   }

   static {
      AGE = BlockStateProperties.AGE_15;
      NORTH = PipeBlock.NORTH;
      EAST = PipeBlock.EAST;
      SOUTH = PipeBlock.SOUTH;
      WEST = PipeBlock.WEST;
      field_63 = PipeBlock.field_55;
      PROPERTY_BY_DIRECTION = (Map)PipeBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter((var0) -> {
         return var0.getKey() != Direction.DOWN;
      }).collect(Util.toMap());
      UP_AABB = Block.box(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D);
      WEST_AABB = Block.box(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);
      EAST_AABB = Block.box(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
      NORTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);
      SOUTH_AABB = Block.box(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);
   }
}
