package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TurtleEggBlock extends Block {
   public static final MapCodec<TurtleEggBlock> CODEC = simpleCodec(TurtleEggBlock::new);
   public static final int MAX_HATCH_LEVEL = 2;
   public static final int MIN_EGGS = 1;
   public static final int MAX_EGGS = 4;
   private static final VoxelShape ONE_EGG_AABB = Block.box(3.0, 0.0, 3.0, 12.0, 7.0, 12.0);
   private static final VoxelShape MULTIPLE_EGGS_AABB = Block.box(1.0, 0.0, 1.0, 15.0, 7.0, 15.0);
   public static final IntegerProperty HATCH = BlockStateProperties.HATCH;
   public static final IntegerProperty EGGS = BlockStateProperties.EGGS;

   @Override
   public MapCodec<TurtleEggBlock> codec() {
      return CODEC;
   }

   public TurtleEggBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(this.stateDefinition.any().setValue(HATCH, Integer.valueOf(0)).setValue(EGGS, Integer.valueOf(1)));
   }

   @Override
   public void stepOn(Level var1, BlockPos var2, BlockState var3, Entity var4) {
      if (!var4.isSteppingCarefully()) {
         this.destroyEgg(var1, var3, var2, var4, 100);
      }

      super.stepOn(var1, var2, var3, var4);
   }

   @Override
   public void fallOn(Level var1, BlockState var2, BlockPos var3, Entity var4, float var5) {
      if (!(var4 instanceof Zombie)) {
         this.destroyEgg(var1, var2, var3, var4, 3);
      }

      super.fallOn(var1, var2, var3, var4, var5);
   }

   private void destroyEgg(Level var1, BlockState var2, BlockPos var3, Entity var4, int var5) {
      if (this.canDestroyEgg(var1, var4)) {
         if (!var1.isClientSide && var1.random.nextInt(var5) == 0 && var2.is(Blocks.TURTLE_EGG)) {
            this.decreaseEggs(var1, var3, var2);
         }
      }
   }

   private void decreaseEggs(Level var1, BlockPos var2, BlockState var3) {
      var1.playSound(null, var2, SoundEvents.TURTLE_EGG_BREAK, SoundSource.BLOCKS, 0.7F, 0.9F + var1.random.nextFloat() * 0.2F);
      int var4 = var3.getValue(EGGS);
      if (var4 <= 1) {
         var1.destroyBlock(var2, false);
      } else {
         var1.setBlock(var2, var3.setValue(EGGS, Integer.valueOf(var4 - 1)), 2);
         var1.gameEvent(GameEvent.BLOCK_DESTROY, var2, GameEvent.Context.of(var3));
         var1.levelEvent(2001, var2, Block.getId(var3));
      }
   }

   @Override
   protected void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (this.shouldUpdateHatchLevel(var2) && onSand(var2, var3)) {
         int var5 = var1.getValue(HATCH);
         if (var5 < 2) {
            var2.playSound(null, var3, SoundEvents.TURTLE_EGG_CRACK, SoundSource.BLOCKS, 0.7F, 0.9F + var4.nextFloat() * 0.2F);
            var2.setBlock(var3, var1.setValue(HATCH, Integer.valueOf(var5 + 1)), 2);
            var2.gameEvent(GameEvent.BLOCK_CHANGE, var3, GameEvent.Context.of(var1));
         } else {
            var2.playSound(null, var3, SoundEvents.TURTLE_EGG_HATCH, SoundSource.BLOCKS, 0.7F, 0.9F + var4.nextFloat() * 0.2F);
            var2.removeBlock(var3, false);
            var2.gameEvent(GameEvent.BLOCK_DESTROY, var3, GameEvent.Context.of(var1));

            for (int var6 = 0; var6 < var1.getValue(EGGS); var6++) {
               var2.levelEvent(2001, var3, Block.getId(var1));
               Turtle var7 = EntityType.TURTLE.create(var2, EntitySpawnReason.BREEDING);
               if (var7 != null) {
                  var7.setAge(-24000);
                  var7.setHomePos(var3);
                  var7.moveTo((double)var3.getX() + 0.3 + (double)var6 * 0.2, (double)var3.getY(), (double)var3.getZ() + 0.3, 0.0F, 0.0F);
                  var2.addFreshEntity(var7);
               }
            }
         }
      }
   }

   public static boolean onSand(BlockGetter var0, BlockPos var1) {
      return isSand(var0, var1.below());
   }

   public static boolean isSand(BlockGetter var0, BlockPos var1) {
      return var0.getBlockState(var1).is(BlockTags.SAND);
   }

   @Override
   protected void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (onSand(var2, var3) && !var2.isClientSide) {
         var2.levelEvent(2012, var3, 15);
      }
   }

   private boolean shouldUpdateHatchLevel(Level var1) {
      float var2 = var1.getTimeOfDay(1.0F);
      return (double)var2 < 0.69 && (double)var2 > 0.65 ? true : var1.random.nextInt(500) == 0;
   }

   @Override
   public void playerDestroy(Level var1, Player var2, BlockPos var3, BlockState var4, @Nullable BlockEntity var5, ItemStack var6) {
      super.playerDestroy(var1, var2, var3, var4, var5, var6);
      this.decreaseEggs(var1, var3, var4);
   }

   @Override
   protected boolean canBeReplaced(BlockState var1, BlockPlaceContext var2) {
      return !var2.isSecondaryUseActive() && var2.getItemInHand().is(this.asItem()) && var1.getValue(EGGS) < 4 ? true : super.canBeReplaced(var1, var2);
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockState var2 = var1.getLevel().getBlockState(var1.getClickedPos());
      return var2.is(this) ? var2.setValue(EGGS, Integer.valueOf(Math.min(4, var2.getValue(EGGS) + 1))) : super.getStateForPlacement(var1);
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return var1.getValue(EGGS) > 1 ? MULTIPLE_EGGS_AABB : ONE_EGG_AABB;
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(HATCH, EGGS);
   }

   private boolean canDestroyEgg(Level var1, Entity var2) {
      if (var2 instanceof Turtle || var2 instanceof Bat) {
         return false;
      } else {
         return !(var2 instanceof LivingEntity) ? false : var2 instanceof Player || var1.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
      }
   }
}
