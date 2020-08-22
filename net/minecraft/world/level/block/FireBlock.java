package net.minecraft.world.level.block;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.end.TheEndDimension;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FireBlock extends Block {
   public static final IntegerProperty AGE;
   public static final BooleanProperty NORTH;
   public static final BooleanProperty EAST;
   public static final BooleanProperty SOUTH;
   public static final BooleanProperty WEST;
   public static final BooleanProperty UP;
   private static final Map PROPERTY_BY_DIRECTION;
   private final Object2IntMap flameOdds = new Object2IntOpenHashMap();
   private final Object2IntMap burnOdds = new Object2IntOpenHashMap();

   protected FireBlock(Block.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0)).setValue(NORTH, false)).setValue(EAST, false)).setValue(SOUTH, false)).setValue(WEST, false)).setValue(UP, false));
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return Shapes.empty();
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      return this.canSurvive(var1, var4, var5) ? (BlockState)this.getStateForPlacement(var4, var5).setValue(AGE, var1.getValue(AGE)) : Blocks.AIR.defaultBlockState();
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return this.getStateForPlacement(var1.getLevel(), var1.getClickedPos());
   }

   public BlockState getStateForPlacement(BlockGetter var1, BlockPos var2) {
      BlockPos var3 = var2.below();
      BlockState var4 = var1.getBlockState(var3);
      if (!this.canBurn(var4) && !var4.isFaceSturdy(var1, var3, Direction.UP)) {
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
      return var2.getBlockState(var4).isFaceSturdy(var2, var4, Direction.UP) || this.isValidFireLocation(var2, var3);
   }

   public int getTickDelay(LevelReader var1) {
      return 30;
   }

   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, Random var4) {
      if (var2.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
         if (!var1.canSurvive(var2, var3)) {
            var2.removeBlock(var3, false);
         }

         Block var5 = var2.getBlockState(var3.below()).getBlock();
         boolean var6 = var2.dimension instanceof TheEndDimension && var5 == Blocks.BEDROCK || var5 == Blocks.NETHERRACK || var5 == Blocks.MAGMA_BLOCK;
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
               var2.getBlockTicks().scheduleTick(var3, this, this.getTickDelay(var2) + var4.nextInt(10));
               if (!this.isValidFireLocation(var2, var3)) {
                  BlockPos var19 = var3.below();
                  if (!var2.getBlockState(var19).isFaceSturdy(var2, var19, Direction.UP) || var7 > 3) {
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

                        var11.set((Vec3i)var3).move(var12, var14, var13);
                        int var16 = this.getFireOdds(var2, var11);
                        if (var16 > 0) {
                           int var17 = (var16 + 40 + var2.getDifficulty().getId() * 7) / (var7 + 30);
                           if (var9) {
                              var17 /= 2;
                           }

                           if (var17 > 0 && var4.nextInt(var15) <= var17 && (!var2.isRaining() || !this.isNearRain(var2, var11))) {
                              int var18 = Math.min(15, var7 + var4.nextInt(5) / 4);
                              var2.setBlock(var11, (BlockState)this.getStateForPlacement(var2, var11).setValue(AGE, var18), 3);
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
            var1.setBlock(var2, (BlockState)this.getStateForPlacement(var1, var2).setValue(AGE, var8), 3);
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

   public boolean canBurn(BlockState var1) {
      return this.getFlameOdds(var1) > 0;
   }

   public void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (var4.getBlock() != var1.getBlock()) {
         if (var2.dimension.getType() != DimensionType.OVERWORLD && var2.dimension.getType() != DimensionType.NETHER || !((NetherPortalBlock)Blocks.NETHER_PORTAL).trySpawnPortal(var2, var3)) {
            if (!var1.canSurvive(var2, var3)) {
               var2.removeBlock(var3, false);
            } else {
               var2.getBlockTicks().scheduleTick(var3, this, this.getTickDelay(var2) + var2.random.nextInt(10));
            }
         }
      }
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, Random var4) {
      if (var4.nextInt(24) == 0) {
         var2.playLocalSound((double)((float)var3.getX() + 0.5F), (double)((float)var3.getY() + 0.5F), (double)((float)var3.getZ() + 0.5F), SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 1.0F + var4.nextFloat(), var4.nextFloat() * 0.7F + 0.3F, false);
      }

      BlockPos var5 = var3.below();
      BlockState var6 = var2.getBlockState(var5);
      int var7;
      double var8;
      double var10;
      double var12;
      if (!this.canBurn(var6) && !var6.isFaceSturdy(var2, var5, Direction.UP)) {
         if (this.canBurn(var2.getBlockState(var3.west()))) {
            for(var7 = 0; var7 < 2; ++var7) {
               var8 = (double)var3.getX() + var4.nextDouble() * 0.10000000149011612D;
               var10 = (double)var3.getY() + var4.nextDouble();
               var12 = (double)var3.getZ() + var4.nextDouble();
               var2.addParticle(ParticleTypes.LARGE_SMOKE, var8, var10, var12, 0.0D, 0.0D, 0.0D);
            }
         }

         if (this.canBurn(var2.getBlockState(var3.east()))) {
            for(var7 = 0; var7 < 2; ++var7) {
               var8 = (double)(var3.getX() + 1) - var4.nextDouble() * 0.10000000149011612D;
               var10 = (double)var3.getY() + var4.nextDouble();
               var12 = (double)var3.getZ() + var4.nextDouble();
               var2.addParticle(ParticleTypes.LARGE_SMOKE, var8, var10, var12, 0.0D, 0.0D, 0.0D);
            }
         }

         if (this.canBurn(var2.getBlockState(var3.north()))) {
            for(var7 = 0; var7 < 2; ++var7) {
               var8 = (double)var3.getX() + var4.nextDouble();
               var10 = (double)var3.getY() + var4.nextDouble();
               var12 = (double)var3.getZ() + var4.nextDouble() * 0.10000000149011612D;
               var2.addParticle(ParticleTypes.LARGE_SMOKE, var8, var10, var12, 0.0D, 0.0D, 0.0D);
            }
         }

         if (this.canBurn(var2.getBlockState(var3.south()))) {
            for(var7 = 0; var7 < 2; ++var7) {
               var8 = (double)var3.getX() + var4.nextDouble();
               var10 = (double)var3.getY() + var4.nextDouble();
               var12 = (double)(var3.getZ() + 1) - var4.nextDouble() * 0.10000000149011612D;
               var2.addParticle(ParticleTypes.LARGE_SMOKE, var8, var10, var12, 0.0D, 0.0D, 0.0D);
            }
         }

         if (this.canBurn(var2.getBlockState(var3.above()))) {
            for(var7 = 0; var7 < 2; ++var7) {
               var8 = (double)var3.getX() + var4.nextDouble();
               var10 = (double)(var3.getY() + 1) - var4.nextDouble() * 0.10000000149011612D;
               var12 = (double)var3.getZ() + var4.nextDouble();
               var2.addParticle(ParticleTypes.LARGE_SMOKE, var8, var10, var12, 0.0D, 0.0D, 0.0D);
            }
         }
      } else {
         for(var7 = 0; var7 < 3; ++var7) {
            var8 = (double)var3.getX() + var4.nextDouble();
            var10 = (double)var3.getY() + var4.nextDouble() * 0.5D + 0.5D;
            var12 = (double)var3.getZ() + var4.nextDouble();
            var2.addParticle(ParticleTypes.LARGE_SMOKE, var8, var10, var12, 0.0D, 0.0D, 0.0D);
         }
      }

   }

   protected void createBlockStateDefinition(StateDefinition.Builder var1) {
      var1.add(AGE, NORTH, EAST, SOUTH, WEST, UP);
   }

   public void setFlammable(Block var1, int var2, int var3) {
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
   }

   static {
      AGE = BlockStateProperties.AGE_15;
      NORTH = PipeBlock.NORTH;
      EAST = PipeBlock.EAST;
      SOUTH = PipeBlock.SOUTH;
      WEST = PipeBlock.WEST;
      UP = PipeBlock.UP;
      PROPERTY_BY_DIRECTION = (Map)PipeBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter((var0) -> {
         return var0.getKey() != Direction.DOWN;
      }).collect(Util.toMap());
   }
}
