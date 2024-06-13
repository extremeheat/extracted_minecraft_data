package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ComposterBlock extends Block implements WorldlyContainerHolder {
   public static final MapCodec<ComposterBlock> CODEC = simpleCodec(ComposterBlock::new);
   public static final int READY = 8;
   public static final int MIN_LEVEL = 0;
   public static final int MAX_LEVEL = 7;
   public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_COMPOSTER;
   public static final Object2FloatMap<ItemLike> COMPOSTABLES = new Object2FloatOpenHashMap();
   private static final int AABB_SIDE_THICKNESS = 2;
   private static final VoxelShape OUTER_SHAPE = Shapes.block();
   private static final VoxelShape[] SHAPES = Util.make(new VoxelShape[9], var0 -> {
      for (int var1 = 0; var1 < 8; var1++) {
         var0[var1] = Shapes.join(OUTER_SHAPE, Block.box(2.0, (double)Math.max(2, 1 + var1 * 2), 2.0, 14.0, 16.0, 14.0), BooleanOp.ONLY_FIRST);
      }

      var0[8] = var0[7];
   });

   @Override
   public MapCodec<ComposterBlock> codec() {
      return CODEC;
   }

   public static void bootStrap() {
      COMPOSTABLES.defaultReturnValue(-1.0F);
      float var0 = 0.3F;
      float var1 = 0.5F;
      float var2 = 0.65F;
      float var3 = 0.85F;
      float var4 = 1.0F;
      add(0.3F, Items.JUNGLE_LEAVES);
      add(0.3F, Items.OAK_LEAVES);
      add(0.3F, Items.SPRUCE_LEAVES);
      add(0.3F, Items.DARK_OAK_LEAVES);
      add(0.3F, Items.ACACIA_LEAVES);
      add(0.3F, Items.CHERRY_LEAVES);
      add(0.3F, Items.BIRCH_LEAVES);
      add(0.3F, Items.AZALEA_LEAVES);
      add(0.3F, Items.MANGROVE_LEAVES);
      add(0.3F, Items.OAK_SAPLING);
      add(0.3F, Items.SPRUCE_SAPLING);
      add(0.3F, Items.BIRCH_SAPLING);
      add(0.3F, Items.JUNGLE_SAPLING);
      add(0.3F, Items.ACACIA_SAPLING);
      add(0.3F, Items.CHERRY_SAPLING);
      add(0.3F, Items.DARK_OAK_SAPLING);
      add(0.3F, Items.MANGROVE_PROPAGULE);
      add(0.3F, Items.BEETROOT_SEEDS);
      add(0.3F, Items.DRIED_KELP);
      add(0.3F, Items.SHORT_GRASS);
      add(0.3F, Items.KELP);
      add(0.3F, Items.MELON_SEEDS);
      add(0.3F, Items.PUMPKIN_SEEDS);
      add(0.3F, Items.SEAGRASS);
      add(0.3F, Items.SWEET_BERRIES);
      add(0.3F, Items.GLOW_BERRIES);
      add(0.3F, Items.WHEAT_SEEDS);
      add(0.3F, Items.MOSS_CARPET);
      add(0.3F, Items.PINK_PETALS);
      add(0.3F, Items.SMALL_DRIPLEAF);
      add(0.3F, Items.HANGING_ROOTS);
      add(0.3F, Items.MANGROVE_ROOTS);
      add(0.3F, Items.TORCHFLOWER_SEEDS);
      add(0.3F, Items.PITCHER_POD);
      add(0.5F, Items.DRIED_KELP_BLOCK);
      add(0.5F, Items.TALL_GRASS);
      add(0.5F, Items.FLOWERING_AZALEA_LEAVES);
      add(0.5F, Items.CACTUS);
      add(0.5F, Items.SUGAR_CANE);
      add(0.5F, Items.VINE);
      add(0.5F, Items.NETHER_SPROUTS);
      add(0.5F, Items.WEEPING_VINES);
      add(0.5F, Items.TWISTING_VINES);
      add(0.5F, Items.MELON_SLICE);
      add(0.5F, Items.GLOW_LICHEN);
      add(0.65F, Items.SEA_PICKLE);
      add(0.65F, Items.LILY_PAD);
      add(0.65F, Items.PUMPKIN);
      add(0.65F, Items.CARVED_PUMPKIN);
      add(0.65F, Items.MELON);
      add(0.65F, Items.APPLE);
      add(0.65F, Items.BEETROOT);
      add(0.65F, Items.CARROT);
      add(0.65F, Items.COCOA_BEANS);
      add(0.65F, Items.POTATO);
      add(0.65F, Items.WHEAT);
      add(0.65F, Items.BROWN_MUSHROOM);
      add(0.65F, Items.RED_MUSHROOM);
      add(0.65F, Items.MUSHROOM_STEM);
      add(0.65F, Items.CRIMSON_FUNGUS);
      add(0.65F, Items.WARPED_FUNGUS);
      add(0.65F, Items.NETHER_WART);
      add(0.65F, Items.CRIMSON_ROOTS);
      add(0.65F, Items.WARPED_ROOTS);
      add(0.65F, Items.SHROOMLIGHT);
      add(0.65F, Items.DANDELION);
      add(0.65F, Items.POPPY);
      add(0.65F, Items.BLUE_ORCHID);
      add(0.65F, Items.ALLIUM);
      add(0.65F, Items.AZURE_BLUET);
      add(0.65F, Items.RED_TULIP);
      add(0.65F, Items.ORANGE_TULIP);
      add(0.65F, Items.WHITE_TULIP);
      add(0.65F, Items.PINK_TULIP);
      add(0.65F, Items.OXEYE_DAISY);
      add(0.65F, Items.CORNFLOWER);
      add(0.65F, Items.LILY_OF_THE_VALLEY);
      add(0.65F, Items.WITHER_ROSE);
      add(0.65F, Items.FERN);
      add(0.65F, Items.SUNFLOWER);
      add(0.65F, Items.LILAC);
      add(0.65F, Items.ROSE_BUSH);
      add(0.65F, Items.PEONY);
      add(0.65F, Items.LARGE_FERN);
      add(0.65F, Items.SPORE_BLOSSOM);
      add(0.65F, Items.AZALEA);
      add(0.65F, Items.MOSS_BLOCK);
      add(0.65F, Items.BIG_DRIPLEAF);
      add(0.85F, Items.HAY_BLOCK);
      add(0.85F, Items.BROWN_MUSHROOM_BLOCK);
      add(0.85F, Items.RED_MUSHROOM_BLOCK);
      add(0.85F, Items.NETHER_WART_BLOCK);
      add(0.85F, Items.WARPED_WART_BLOCK);
      add(0.85F, Items.FLOWERING_AZALEA);
      add(0.85F, Items.BREAD);
      add(0.85F, Items.BAKED_POTATO);
      add(0.85F, Items.COOKIE);
      add(0.85F, Items.TORCHFLOWER);
      add(0.85F, Items.PITCHER_PLANT);
      add(1.0F, Items.CAKE);
      add(1.0F, Items.PUMPKIN_PIE);
   }

   private static void add(float var0, ItemLike var1) {
      COMPOSTABLES.put(var1.asItem(), var0);
   }

   public ComposterBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, Integer.valueOf(0)));
   }

   public static void handleFill(Level var0, BlockPos var1, boolean var2) {
      BlockState var3 = var0.getBlockState(var1);
      var0.playLocalSound(var1, var2 ? SoundEvents.COMPOSTER_FILL_SUCCESS : SoundEvents.COMPOSTER_FILL, SoundSource.BLOCKS, 1.0F, 1.0F, false);
      double var4 = var3.getShape(var0, var1).max(Direction.Axis.Y, 0.5, 0.5) + 0.03125;
      double var6 = 0.13124999403953552;
      double var8 = 0.737500011920929;
      RandomSource var10 = var0.getRandom();

      for (int var11 = 0; var11 < 10; var11++) {
         double var12 = var10.nextGaussian() * 0.02;
         double var14 = var10.nextGaussian() * 0.02;
         double var16 = var10.nextGaussian() * 0.02;
         var0.addParticle(
            ParticleTypes.COMPOSTER,
            (double)var1.getX() + 0.13124999403953552 + 0.737500011920929 * (double)var10.nextFloat(),
            (double)var1.getY() + var4 + (double)var10.nextFloat() * (1.0 - var4),
            (double)var1.getZ() + 0.13124999403953552 + 0.737500011920929 * (double)var10.nextFloat(),
            var12,
            var14,
            var16
         );
      }
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPES[var1.getValue(LEVEL)];
   }

   @Override
   protected VoxelShape getInteractionShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      return OUTER_SHAPE;
   }

   @Override
   protected VoxelShape getCollisionShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPES[0];
   }

   @Override
   protected void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (var1.getValue(LEVEL) == 7) {
         var2.scheduleTick(var3, var1.getBlock(), 20);
      }
   }

   @Override
   protected ItemInteractionResult useItemOn(ItemStack var1, BlockState var2, Level var3, BlockPos var4, Player var5, InteractionHand var6, BlockHitResult var7) {
      int var8 = var2.getValue(LEVEL);
      if (var8 < 8 && COMPOSTABLES.containsKey(var1.getItem())) {
         if (var8 < 7 && !var3.isClientSide) {
            BlockState var9 = addItem(var5, var2, var3, var4, var1);
            var3.levelEvent(1500, var4, var2 != var9 ? 1 : 0);
            var5.awardStat(Stats.ITEM_USED.get(var1.getItem()));
            var1.consume(1, var5);
         }

         return ItemInteractionResult.sidedSuccess(var3.isClientSide);
      } else {
         return super.useItemOn(var1, var2, var3, var4, var5, var6, var7);
      }
   }

   @Override
   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      int var6 = var1.getValue(LEVEL);
      if (var6 == 8) {
         extractProduce(var4, var1, var2, var3);
         return InteractionResult.sidedSuccess(var2.isClientSide);
      } else {
         return InteractionResult.PASS;
      }
   }

   public static BlockState insertItem(Entity var0, BlockState var1, ServerLevel var2, ItemStack var3, BlockPos var4) {
      int var5 = var1.getValue(LEVEL);
      if (var5 < 7 && COMPOSTABLES.containsKey(var3.getItem())) {
         BlockState var6 = addItem(var0, var1, var2, var4, var3);
         var3.shrink(1);
         return var6;
      } else {
         return var1;
      }
   }

   public static BlockState extractProduce(Entity var0, BlockState var1, Level var2, BlockPos var3) {
      if (!var2.isClientSide) {
         Vec3 var4 = Vec3.atLowerCornerWithOffset(var3, 0.5, 1.01, 0.5).offsetRandom(var2.random, 0.7F);
         ItemEntity var5 = new ItemEntity(var2, var4.x(), var4.y(), var4.z(), new ItemStack(Items.BONE_MEAL));
         var5.setDefaultPickUpDelay();
         var2.addFreshEntity(var5);
      }

      BlockState var6 = empty(var0, var1, var2, var3);
      var2.playSound(null, var3, SoundEvents.COMPOSTER_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
      return var6;
   }

   static BlockState empty(@Nullable Entity var0, BlockState var1, LevelAccessor var2, BlockPos var3) {
      BlockState var4 = var1.setValue(LEVEL, Integer.valueOf(0));
      var2.setBlock(var3, var4, 3);
      var2.gameEvent(GameEvent.BLOCK_CHANGE, var3, GameEvent.Context.of(var0, var4));
      return var4;
   }

   static BlockState addItem(@Nullable Entity var0, BlockState var1, LevelAccessor var2, BlockPos var3, ItemStack var4) {
      int var5 = var1.getValue(LEVEL);
      float var6 = COMPOSTABLES.getFloat(var4.getItem());
      if ((var5 != 0 || !(var6 > 0.0F)) && !(var2.getRandom().nextDouble() < (double)var6)) {
         return var1;
      } else {
         int var7 = var5 + 1;
         BlockState var8 = var1.setValue(LEVEL, Integer.valueOf(var7));
         var2.setBlock(var3, var8, 3);
         var2.gameEvent(GameEvent.BLOCK_CHANGE, var3, GameEvent.Context.of(var0, var8));
         if (var7 == 7) {
            var2.scheduleTick(var3, var1.getBlock(), 20);
         }

         return var8;
      }
   }

   @Override
   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (var1.getValue(LEVEL) == 7) {
         var2.setBlock(var3, var1.cycle(LEVEL), 3);
         var2.playSound(null, var3, SoundEvents.COMPOSTER_READY, SoundSource.BLOCKS, 1.0F, 1.0F);
      }
   }

   @Override
   protected boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   @Override
   protected int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      return var1.getValue(LEVEL);
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(LEVEL);
   }

   @Override
   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return false;
   }

   @Override
   public WorldlyContainer getContainer(BlockState var1, LevelAccessor var2, BlockPos var3) {
      int var4 = var1.getValue(LEVEL);
      if (var4 == 8) {
         return new ComposterBlock.OutputContainer(var1, var2, var3, new ItemStack(Items.BONE_MEAL));
      } else {
         return (WorldlyContainer)(var4 < 7 ? new ComposterBlock.InputContainer(var1, var2, var3) : new ComposterBlock.EmptyContainer());
      }
   }

   static class EmptyContainer extends SimpleContainer implements WorldlyContainer {
      public EmptyContainer() {
         super(0);
      }

      @Override
      public int[] getSlotsForFace(Direction var1) {
         return new int[0];
      }

      @Override
      public boolean canPlaceItemThroughFace(int var1, ItemStack var2, @Nullable Direction var3) {
         return false;
      }

      @Override
      public boolean canTakeItemThroughFace(int var1, ItemStack var2, Direction var3) {
         return false;
      }
   }

   static class InputContainer extends SimpleContainer implements WorldlyContainer {
      private final BlockState state;
      private final LevelAccessor level;
      private final BlockPos pos;
      private boolean changed;

      public InputContainer(BlockState var1, LevelAccessor var2, BlockPos var3) {
         super(1);
         this.state = var1;
         this.level = var2;
         this.pos = var3;
      }

      @Override
      public int getMaxStackSize() {
         return 1;
      }

      @Override
      public int[] getSlotsForFace(Direction var1) {
         return var1 == Direction.UP ? new int[]{0} : new int[0];
      }

      @Override
      public boolean canPlaceItemThroughFace(int var1, ItemStack var2, @Nullable Direction var3) {
         return !this.changed && var3 == Direction.UP && ComposterBlock.COMPOSTABLES.containsKey(var2.getItem());
      }

      @Override
      public boolean canTakeItemThroughFace(int var1, ItemStack var2, Direction var3) {
         return false;
      }

      @Override
      public void setChanged() {
         ItemStack var1 = this.getItem(0);
         if (!var1.isEmpty()) {
            this.changed = true;
            BlockState var2 = ComposterBlock.addItem(null, this.state, this.level, this.pos, var1);
            this.level.levelEvent(1500, this.pos, var2 != this.state ? 1 : 0);
            this.removeItemNoUpdate(0);
         }
      }
   }

   static class OutputContainer extends SimpleContainer implements WorldlyContainer {
      private final BlockState state;
      private final LevelAccessor level;
      private final BlockPos pos;
      private boolean changed;

      public OutputContainer(BlockState var1, LevelAccessor var2, BlockPos var3, ItemStack var4) {
         super(var4);
         this.state = var1;
         this.level = var2;
         this.pos = var3;
      }

      @Override
      public int getMaxStackSize() {
         return 1;
      }

      @Override
      public int[] getSlotsForFace(Direction var1) {
         return var1 == Direction.DOWN ? new int[]{0} : new int[0];
      }

      @Override
      public boolean canPlaceItemThroughFace(int var1, ItemStack var2, @Nullable Direction var3) {
         return false;
      }

      @Override
      public boolean canTakeItemThroughFace(int var1, ItemStack var2, Direction var3) {
         return !this.changed && var3 == Direction.DOWN && var2.is(Items.BONE_MEAL);
      }

      @Override
      public void setChanged() {
         ComposterBlock.empty(null, this.state, this.level, this.pos);
         this.changed = true;
      }
   }
}
