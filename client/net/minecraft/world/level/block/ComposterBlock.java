package net.minecraft.world.level.block;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
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
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ComposterBlock extends Block implements WorldlyContainerHolder {
   public static final IntegerProperty LEVEL;
   public static final Object2FloatMap<ItemLike> COMPOSTABLES;
   private static final VoxelShape OUTER_SHAPE;
   private static final VoxelShape[] SHAPES;

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
      add(0.3F, Items.BIRCH_LEAVES);
      add(0.3F, Items.OAK_SAPLING);
      add(0.3F, Items.SPRUCE_SAPLING);
      add(0.3F, Items.BIRCH_SAPLING);
      add(0.3F, Items.JUNGLE_SAPLING);
      add(0.3F, Items.ACACIA_SAPLING);
      add(0.3F, Items.DARK_OAK_SAPLING);
      add(0.3F, Items.BEETROOT_SEEDS);
      add(0.3F, Items.DRIED_KELP);
      add(0.3F, Items.GRASS);
      add(0.3F, Items.KELP);
      add(0.3F, Items.MELON_SEEDS);
      add(0.3F, Items.PUMPKIN_SEEDS);
      add(0.3F, Items.SEAGRASS);
      add(0.3F, Items.SWEET_BERRIES);
      add(0.3F, Items.WHEAT_SEEDS);
      add(0.5F, Items.DRIED_KELP_BLOCK);
      add(0.5F, Items.TALL_GRASS);
      add(0.5F, Items.CACTUS);
      add(0.5F, Items.SUGAR_CANE);
      add(0.5F, Items.VINE);
      add(0.5F, Items.NETHER_SPROUTS);
      add(0.5F, Items.WEEPING_VINES);
      add(0.5F, Items.TWISTING_VINES);
      add(0.5F, Items.MELON_SLICE);
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
      add(0.85F, Items.HAY_BLOCK);
      add(0.85F, Items.BROWN_MUSHROOM_BLOCK);
      add(0.85F, Items.RED_MUSHROOM_BLOCK);
      add(0.85F, Items.NETHER_WART_BLOCK);
      add(0.85F, Items.WARPED_WART_BLOCK);
      add(0.85F, Items.BREAD);
      add(0.85F, Items.BAKED_POTATO);
      add(0.85F, Items.COOKIE);
      add(1.0F, Items.CAKE);
      add(1.0F, Items.PUMPKIN_PIE);
   }

   private static void add(float var0, ItemLike var1) {
      COMPOSTABLES.put(var1.asItem(), var0);
   }

   public ComposterBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(LEVEL, 0));
   }

   public static void handleFill(Level var0, BlockPos var1, boolean var2) {
      BlockState var3 = var0.getBlockState(var1);
      var0.playLocalSound((double)var1.getX(), (double)var1.getY(), (double)var1.getZ(), var2 ? SoundEvents.COMPOSTER_FILL_SUCCESS : SoundEvents.COMPOSTER_FILL, SoundSource.BLOCKS, 1.0F, 1.0F, false);
      double var4 = var3.getShape(var0, var1).max(Direction.Axis.Y, 0.5D, 0.5D) + 0.03125D;
      double var6 = 0.13124999403953552D;
      double var8 = 0.737500011920929D;
      Random var10 = var0.getRandom();

      for(int var11 = 0; var11 < 10; ++var11) {
         double var12 = var10.nextGaussian() * 0.02D;
         double var14 = var10.nextGaussian() * 0.02D;
         double var16 = var10.nextGaussian() * 0.02D;
         var0.addParticle(ParticleTypes.COMPOSTER, (double)var1.getX() + 0.13124999403953552D + 0.737500011920929D * (double)var10.nextFloat(), (double)var1.getY() + var4 + (double)var10.nextFloat() * (1.0D - var4), (double)var1.getZ() + 0.13124999403953552D + 0.737500011920929D * (double)var10.nextFloat(), var12, var14, var16);
      }

   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPES[(Integer)var1.getValue(LEVEL)];
   }

   public VoxelShape getInteractionShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      return OUTER_SHAPE;
   }

   public VoxelShape getCollisionShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPES[0];
   }

   public void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if ((Integer)var1.getValue(LEVEL) == 7) {
         var2.getBlockTicks().scheduleTick(var3, var1.getBlock(), 20);
      }

   }

   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      int var7 = (Integer)var1.getValue(LEVEL);
      ItemStack var8 = var4.getItemInHand(var5);
      if (var7 < 8 && COMPOSTABLES.containsKey(var8.getItem())) {
         if (var7 < 7 && !var2.isClientSide) {
            BlockState var9 = addItem(var1, var2, var3, var8);
            var2.levelEvent(1500, var3, var1 != var9 ? 1 : 0);
            if (!var4.getAbilities().instabuild) {
               var8.shrink(1);
            }
         }

         return InteractionResult.sidedSuccess(var2.isClientSide);
      } else if (var7 == 8) {
         extractProduce(var1, var2, var3);
         return InteractionResult.sidedSuccess(var2.isClientSide);
      } else {
         return InteractionResult.PASS;
      }
   }

   public static BlockState insertItem(BlockState var0, ServerLevel var1, ItemStack var2, BlockPos var3) {
      int var4 = (Integer)var0.getValue(LEVEL);
      if (var4 < 7 && COMPOSTABLES.containsKey(var2.getItem())) {
         BlockState var5 = addItem(var0, var1, var3, var2);
         var2.shrink(1);
         return var5;
      } else {
         return var0;
      }
   }

   public static BlockState extractProduce(BlockState var0, Level var1, BlockPos var2) {
      if (!var1.isClientSide) {
         float var3 = 0.7F;
         double var4 = (double)(var1.random.nextFloat() * 0.7F) + 0.15000000596046448D;
         double var6 = (double)(var1.random.nextFloat() * 0.7F) + 0.06000000238418579D + 0.6D;
         double var8 = (double)(var1.random.nextFloat() * 0.7F) + 0.15000000596046448D;
         ItemEntity var10 = new ItemEntity(var1, (double)var2.getX() + var4, (double)var2.getY() + var6, (double)var2.getZ() + var8, new ItemStack(Items.BONE_MEAL));
         var10.setDefaultPickUpDelay();
         var1.addFreshEntity(var10);
      }

      BlockState var11 = empty(var0, var1, var2);
      var1.playSound((Player)null, (BlockPos)var2, SoundEvents.COMPOSTER_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
      return var11;
   }

   private static BlockState empty(BlockState var0, LevelAccessor var1, BlockPos var2) {
      BlockState var3 = (BlockState)var0.setValue(LEVEL, 0);
      var1.setBlock(var2, var3, 3);
      return var3;
   }

   private static BlockState addItem(BlockState var0, LevelAccessor var1, BlockPos var2, ItemStack var3) {
      int var4 = (Integer)var0.getValue(LEVEL);
      float var5 = COMPOSTABLES.getFloat(var3.getItem());
      if ((var4 != 0 || var5 <= 0.0F) && var1.getRandom().nextDouble() >= (double)var5) {
         return var0;
      } else {
         int var6 = var4 + 1;
         BlockState var7 = (BlockState)var0.setValue(LEVEL, var6);
         var1.setBlock(var2, var7, 3);
         if (var6 == 7) {
            var1.getBlockTicks().scheduleTick(var2, var0.getBlock(), 20);
         }

         return var7;
      }
   }

   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, Random var4) {
      if ((Integer)var1.getValue(LEVEL) == 7) {
         var2.setBlock(var3, (BlockState)var1.cycle(LEVEL), 3);
         var2.playSound((Player)null, var3, SoundEvents.COMPOSTER_READY, SoundSource.BLOCKS, 1.0F, 1.0F);
      }

   }

   public boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      return (Integer)var1.getValue(LEVEL);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(LEVEL);
   }

   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      return false;
   }

   public WorldlyContainer getContainer(BlockState var1, LevelAccessor var2, BlockPos var3) {
      int var4 = (Integer)var1.getValue(LEVEL);
      if (var4 == 8) {
         return new ComposterBlock.OutputContainer(var1, var2, var3, new ItemStack(Items.BONE_MEAL));
      } else {
         return (WorldlyContainer)(var4 < 7 ? new ComposterBlock.InputContainer(var1, var2, var3) : new ComposterBlock.EmptyContainer());
      }
   }

   static {
      LEVEL = BlockStateProperties.LEVEL_COMPOSTER;
      COMPOSTABLES = new Object2FloatOpenHashMap();
      OUTER_SHAPE = Shapes.block();
      SHAPES = (VoxelShape[])Util.make(new VoxelShape[9], (var0) -> {
         for(int var1 = 0; var1 < 8; ++var1) {
            var0[var1] = Shapes.join(OUTER_SHAPE, Block.box(2.0D, (double)Math.max(2, 1 + var1 * 2), 2.0D, 14.0D, 16.0D, 14.0D), BooleanOp.ONLY_FIRST);
         }

         var0[8] = var0[7];
      });
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

      public int getMaxStackSize() {
         return 1;
      }

      public int[] getSlotsForFace(Direction var1) {
         return var1 == Direction.UP ? new int[]{0} : new int[0];
      }

      public boolean canPlaceItemThroughFace(int var1, ItemStack var2, @Nullable Direction var3) {
         return !this.changed && var3 == Direction.UP && ComposterBlock.COMPOSTABLES.containsKey(var2.getItem());
      }

      public boolean canTakeItemThroughFace(int var1, ItemStack var2, Direction var3) {
         return false;
      }

      public void setChanged() {
         ItemStack var1 = this.getItem(0);
         if (!var1.isEmpty()) {
            this.changed = true;
            BlockState var2 = ComposterBlock.addItem(this.state, this.level, this.pos, var1);
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

      public int getMaxStackSize() {
         return 1;
      }

      public int[] getSlotsForFace(Direction var1) {
         return var1 == Direction.DOWN ? new int[]{0} : new int[0];
      }

      public boolean canPlaceItemThroughFace(int var1, ItemStack var2, @Nullable Direction var3) {
         return false;
      }

      public boolean canTakeItemThroughFace(int var1, ItemStack var2, Direction var3) {
         return !this.changed && var3 == Direction.DOWN && var2.is(Items.BONE_MEAL);
      }

      public void setChanged() {
         ComposterBlock.empty(this.state, this.level, this.pos);
         this.changed = true;
      }
   }

   static class EmptyContainer extends SimpleContainer implements WorldlyContainer {
      public EmptyContainer() {
         super(0);
      }

      public int[] getSlotsForFace(Direction var1) {
         return new int[0];
      }

      public boolean canPlaceItemThroughFace(int var1, ItemStack var2, @Nullable Direction var3) {
         return false;
      }

      public boolean canTakeItemThroughFace(int var1, ItemStack var2, Direction var3) {
         return false;
      }
   }
}
