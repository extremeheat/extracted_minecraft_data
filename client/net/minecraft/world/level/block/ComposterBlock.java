package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.livingblock.LivingBlock;
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
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class ComposterBlock extends Block implements WorldlyContainerHolder {
   public static final MapCodec<ComposterBlock> CODEC = simpleCodec(ComposterBlock::new);
   public static final int READY = 8;
   public static final int MIN_LEVEL = 0;
   public static final int MAX_LEVEL = 7;
   public static final IntegerProperty LEVEL;
   public static final Object2FloatMap<ItemLike> COMPOSTABLES;
   private static final int HOLE_WIDTH = 12;
   private static final VoxelShape[] SHAPES;

   public MapCodec<ComposterBlock> codec() {
      return CODEC;
   }

   public static void bootStrap() {
      COMPOSTABLES.defaultReturnValue(-1.0F);
      float low = 0.3F;
      float lowMid = 0.5F;
      float mid = 0.65F;
      float midHigh = 0.85F;
      float high = 1.0F;
      add(0.3F, Items.JUNGLE_LEAVES);
      add(0.3F, Items.OAK_LEAVES);
      add(0.3F, Items.SPRUCE_LEAVES);
      add(0.3F, Items.DARK_OAK_LEAVES);
      add(0.3F, Items.PALE_OAK_LEAVES);
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
      add(0.3F, Items.PALE_OAK_SAPLING);
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
      add(0.3F, Items.PALE_MOSS_CARPET);
      add(0.3F, Items.PALE_HANGING_MOSS);
      add(0.3F, Items.PINK_PETALS);
      add(0.3F, Items.WILDFLOWERS);
      add(0.3F, Items.LEAF_LITTER);
      add(0.3F, Items.SMALL_DRIPLEAF);
      add(0.3F, Items.HANGING_ROOTS);
      add(0.3F, Items.MANGROVE_ROOTS);
      add(0.3F, Items.TORCHFLOWER_SEEDS);
      add(0.3F, Items.PITCHER_POD);
      add(0.3F, Items.FIREFLY_BUSH);
      add(0.3F, Items.BUSH);
      add(0.3F, Items.CACTUS_FLOWER);
      add(0.3F, Items.DRY_SHORT_GRASS);
      add(0.3F, Items.DRY_TALL_GRASS);
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
      add(0.65F, Items.OPEN_EYEBLOSSOM);
      add(0.65F, Items.CLOSED_EYEBLOSSOM);
      add(0.65F, Items.FERN);
      add(0.65F, Items.SUNFLOWER);
      add(0.65F, Items.LILAC);
      add(0.65F, Items.ROSE_BUSH);
      add(0.65F, Items.PEONY);
      add(0.65F, Items.LARGE_FERN);
      add(0.65F, Items.SPORE_BLOSSOM);
      add(0.65F, Items.AZALEA);
      add(0.65F, Items.MOSS_BLOCK);
      add(0.65F, Items.PALE_MOSS_BLOCK);
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

   private static void add(final float value, final ItemLike item) {
      COMPOSTABLES.put(item.asItem(), value);
   }

   public ComposterBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(LEVEL, 0));
   }

   public static void handleFill(final Level level, final BlockPos pos, final boolean success) {
      BlockState state = level.getBlockState(pos);
      level.playLocalSound(pos, success ? SoundEvents.COMPOSTER_FILL_SUCCESS : SoundEvents.COMPOSTER_FILL, SoundSource.BLOCKS, 1.0F, 1.0F, false);
      double centerHeight = state.getShape(level, pos).max(Direction.Axis.Y, 0.5, 0.5) + 0.03125;
      double sideOffsetPixels = 2.0;
      double sideOffset = 0.1875;
      double width = 0.625;
      RandomSource random = level.getRandom();

      for(int i = 0; i < 10; ++i) {
         double xa = random.nextGaussian() * 0.02;
         double ya = random.nextGaussian() * 0.02;
         double za = random.nextGaussian() * 0.02;
         level.addParticle(ParticleTypes.COMPOSTER, (double)pos.getX() + 0.1875 + 0.625 * (double)random.nextFloat(), (double)pos.getY() + centerHeight + (double)random.nextFloat() * (1.0 - centerHeight), (double)pos.getZ() + 0.1875 + 0.625 * (double)random.nextFloat(), xa, ya, za);
      }

   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return SHAPES[(Integer)state.getValue(LEVEL)];
   }

   protected VoxelShape getInteractionShape(final BlockState state, final BlockGetter level, final BlockPos pos) {
      return Shapes.block();
   }

   protected VoxelShape getCollisionShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return SHAPES[0];
   }

   protected void onPlace(final BlockState state, final Level level, final BlockPos pos, final BlockState oldState, final boolean movedByPiston) {
      if ((Integer)state.getValue(LEVEL) == 7) {
         level.scheduleTick(pos, state.getBlock(), 20);
      }

   }

   protected InteractionResult useItemOn(final ItemStack itemStack, final BlockState state, final Level level, final BlockPos pos, final Player player, final InteractionHand hand, final BlockHitResult hitResult) {
      int fillLevel = (Integer)state.getValue(LEVEL);
      if (fillLevel < 8 && COMPOSTABLES.containsKey(itemStack.getItem())) {
         if (fillLevel < 7 && !level.isClientSide()) {
            BlockState newState = addItem(player, state, level, pos, itemStack);
            level.levelEvent(1500, pos, state != newState ? 1 : 0);
            player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
            itemStack.consume(1, player);
         }

         return InteractionResult.SUCCESS;
      } else {
         return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult);
      }
   }

   protected InteractionResult useWithoutItem(final BlockState state, final Level level, final BlockPos pos, final Player player, final BlockHitResult hitResult) {
      int fillLevel = (Integer)state.getValue(LEVEL);
      if (fillLevel == 8) {
         extractProduce(player, state, level, pos);
         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.PASS;
      }
   }

   public static BlockState insertItem(final Entity sourceEntity, final BlockState state, final ServerLevel level, final ItemStack itemStack, final BlockPos pos) {
      int fillLevel = (Integer)state.getValue(LEVEL);
      if (fillLevel < 7 && COMPOSTABLES.containsKey(itemStack.getItem())) {
         BlockState newState = addItem(sourceEntity, state, level, pos, itemStack);
         itemStack.shrink(1);
         return newState;
      } else {
         return state;
      }
   }

   public static BlockState extractProduce(final Entity sourceEntity, final BlockState state, final Level level, final BlockPos pos) {
      if (!level.isClientSide()) {
         LivingBlock block = LivingBlock.createAt(level, pos.above(), new ItemStack(Items.BONE_MEAL));
         RandomSource random = level.getRandom();
         if (block != null) {
            block.setDeltaMovement((double)random.nextFloat() - 0.5, (double)random.nextFloat(), (double)random.nextFloat() - 0.5);
            if (sourceEntity instanceof ServerPlayer) {
               ServerPlayer serverPlayer = (ServerPlayer)sourceEntity;
               CriteriaTriggers.SUMMONED_ENTITY.trigger(serverPlayer, block);
            }
         }
      }

      BlockState emptyState = empty(sourceEntity, state, level, pos);
      level.playSound((Entity)null, (BlockPos)pos, SoundEvents.COMPOSTER_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
      return emptyState;
   }

   private static BlockState empty(final @Nullable Entity sourceEntity, final BlockState state, final LevelAccessor level, final BlockPos pos) {
      BlockState newState = (BlockState)state.setValue(LEVEL, 0);
      level.setBlock(pos, newState, 3);
      level.gameEvent(GameEvent.BLOCK_CHANGE, (BlockPos)pos, (GameEvent.Context)GameEvent.Context.of(sourceEntity, newState));
      return newState;
   }

   private static BlockState addItem(final @Nullable Entity sourceEntity, final BlockState state, final LevelAccessor level, final BlockPos pos, final ItemStack itemStack) {
      int fillLevel = (Integer)state.getValue(LEVEL);
      float chance = COMPOSTABLES.getFloat(itemStack.getItem());
      if ((fillLevel != 0 || !(chance > 0.0F)) && !(level.getRandom().nextDouble() < (double)chance)) {
         return state;
      } else {
         int newLevel = fillLevel + 1;
         BlockState newState = (BlockState)state.setValue(LEVEL, newLevel);
         level.setBlock(pos, newState, 3);
         level.gameEvent(GameEvent.BLOCK_CHANGE, (BlockPos)pos, (GameEvent.Context)GameEvent.Context.of(sourceEntity, newState));
         if (newLevel == 7) {
            level.scheduleTick(pos, state.getBlock(), 20);
         }

         return newState;
      }
   }

   protected void tick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
      if ((Integer)state.getValue(LEVEL) == 7) {
         level.setBlock(pos, (BlockState)state.cycle(LEVEL), 3);
         level.playSound((Entity)null, pos, SoundEvents.COMPOSTER_READY, SoundSource.BLOCKS, 1.0F, 1.0F);
      }

   }

   protected boolean hasAnalogOutputSignal(final BlockState state) {
      return true;
   }

   protected int getAnalogOutputSignal(final BlockState state, final Level level, final BlockPos pos, final Direction direction) {
      return (Integer)state.getValue(LEVEL);
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(LEVEL);
   }

   protected boolean isPathfindable(final BlockState state, final PathComputationType type) {
      return false;
   }

   public WorldlyContainer getContainer(final BlockState state, final LevelAccessor level, final BlockPos pos) {
      int contentLevel = (Integer)state.getValue(LEVEL);
      if (contentLevel == 8) {
         return new OutputContainer(state, level, pos, new ItemStack(Items.BONE_MEAL));
      } else {
         return (WorldlyContainer)(contentLevel < 7 ? new InputContainer(state, level, pos) : new EmptyContainer());
      }
   }

   static {
      LEVEL = BlockStateProperties.LEVEL_COMPOSTER;
      COMPOSTABLES = new Object2FloatOpenHashMap();
      SHAPES = (VoxelShape[])Util.make(() -> {
         VoxelShape[] shapes = Block.boxes(8, (level) -> Shapes.join(Shapes.block(), Block.column(12.0, (double)Math.clamp((long)(1 + level * 2), 2, 16), 16.0), BooleanOp.ONLY_FIRST));
         shapes[8] = shapes[7];
         return shapes;
      });
   }

   private static class EmptyContainer extends SimpleContainer implements WorldlyContainer {
      public EmptyContainer() {
         super(0);
      }

      public int[] getSlotsForFace(final Direction direction) {
         return new int[0];
      }

      public boolean canPlaceItemThroughFace(final int slot, final ItemStack itemStack, final @Nullable Direction direction) {
         return false;
      }

      public boolean canTakeItemThroughFace(final int slot, final ItemStack itemStack, final Direction direction) {
         return false;
      }
   }

   private static class OutputContainer extends SimpleContainer implements WorldlyContainer {
      private final BlockState state;
      private final LevelAccessor level;
      private final BlockPos pos;
      private boolean changed;

      public OutputContainer(final BlockState state, final LevelAccessor level, final BlockPos pos, final ItemStack contents) {
         super(contents);
         this.state = state;
         this.level = level;
         this.pos = pos;
      }

      public int getMaxStackSize() {
         return 1;
      }

      public int[] getSlotsForFace(final Direction direction) {
         return direction == Direction.DOWN ? new int[]{0} : new int[0];
      }

      public boolean canPlaceItemThroughFace(final int slot, final ItemStack itemStack, final @Nullable Direction direction) {
         return false;
      }

      public boolean canTakeItemThroughFace(final int slot, final ItemStack itemStack, final Direction direction) {
         return !this.changed && direction == Direction.DOWN && itemStack.is(Items.BONE_MEAL);
      }

      public void setChanged() {
         ComposterBlock.empty((Entity)null, this.state, this.level, this.pos);
         this.changed = true;
      }
   }

   private static class InputContainer extends SimpleContainer implements WorldlyContainer {
      private final BlockState state;
      private final LevelAccessor level;
      private final BlockPos pos;
      private boolean changed;

      public InputContainer(final BlockState state, final LevelAccessor level, final BlockPos pos) {
         super(1);
         this.state = state;
         this.level = level;
         this.pos = pos;
      }

      public int getMaxStackSize() {
         return 1;
      }

      public int[] getSlotsForFace(final Direction direction) {
         return direction == Direction.UP ? new int[]{0} : new int[0];
      }

      public boolean canPlaceItemThroughFace(final int slot, final ItemStack itemStack, final @Nullable Direction direction) {
         return !this.changed && direction == Direction.UP && ComposterBlock.COMPOSTABLES.containsKey(itemStack.getItem());
      }

      public boolean canTakeItemThroughFace(final int slot, final ItemStack itemStack, final Direction direction) {
         return false;
      }

      public void setChanged() {
         ItemStack contents = this.getItem(0);
         if (!contents.isEmpty()) {
            this.changed = true;
            BlockState newState = ComposterBlock.addItem((Entity)null, this.state, this.level, this.pos, contents);
            this.level.levelEvent(1500, this.pos, newState != this.state ? 1 : 0);
            this.removeItemNoUpdate(0);
         }

      }
   }
}
