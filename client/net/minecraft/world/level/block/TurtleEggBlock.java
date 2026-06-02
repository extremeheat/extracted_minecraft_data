package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.turtle.Turtle;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class TurtleEggBlock extends Block {
   public static final MapCodec<TurtleEggBlock> CODEC = simpleCodec(TurtleEggBlock::new);
   public static final IntegerProperty HATCH;
   public static final IntegerProperty EGGS;
   public static final int MAX_HATCH_LEVEL = 2;
   public static final int MIN_EGGS = 1;
   public static final int MAX_EGGS = 4;
   private static final VoxelShape SHAPE_SINGLE;
   private static final VoxelShape SHAPE_MULTIPLE;

   public MapCodec<TurtleEggBlock> codec() {
      return CODEC;
   }

   public TurtleEggBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(HATCH, 0)).setValue(EGGS, 1));
   }

   public void stepOn(final Level level, final BlockPos pos, final BlockState onState, final Entity entity) {
      if (!entity.isSteppingCarefully()) {
         this.destroyEgg(level, onState, pos, entity, 100);
      }

      super.stepOn(level, pos, onState, entity);
   }

   public void fallOn(final Level level, final BlockState state, final BlockPos pos, final Entity entity, final double fallDistance) {
      if (!(entity instanceof Zombie)) {
         this.destroyEgg(level, state, pos, entity, 3);
      }

      super.fallOn(level, state, pos, entity, fallDistance);
   }

   private void destroyEgg(final Level level, final BlockState state, final BlockPos pos, final Entity entity, final int randomness) {
      if (state.is(Blocks.TURTLE_EGG) && level instanceof ServerLevel serverLevel) {
         if (this.canDestroyEgg(serverLevel, entity) && level.getRandom().nextInt(randomness) == 0) {
            this.decreaseEggs(serverLevel, pos, state);
         }
      }

   }

   private void decreaseEggs(final Level level, final BlockPos pos, final BlockState state) {
      level.playSound((Entity)null, (BlockPos)pos, SoundEvents.TURTLE_EGG_BREAK, SoundSource.BLOCKS, 0.7F, 0.9F + level.getRandom().nextFloat() * 0.2F);
      int numberOfEggs = (Integer)state.getValue(EGGS);
      if (numberOfEggs <= 1) {
         level.destroyBlock(pos, false);
      } else {
         level.setBlock(pos, (BlockState)state.setValue(EGGS, numberOfEggs - 1), 2);
         level.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(state));
         level.levelEvent(2001, pos, Block.getId(state));
      }

   }

   protected void randomTick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
      if (this.shouldUpdateHatchLevel(level, pos) && onSand(level, pos)) {
         int hatch = (Integer)state.getValue(HATCH);
         if (hatch < 2) {
            level.playSound((Entity)null, pos, SoundEvents.TURTLE_EGG_CRACK, SoundSource.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);
            level.setBlock(pos, (BlockState)state.setValue(HATCH, hatch + 1), 2);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(state));
         } else {
            level.playSound((Entity)null, pos, SoundEvents.TURTLE_EGG_HATCH, SoundSource.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);
            level.removeBlock(pos, false);
            level.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(state));

            for(int i = 0; i < (Integer)state.getValue(EGGS); ++i) {
               level.levelEvent(2001, pos, Block.getId(state));
               Turtle turtle = (Turtle)EntityTypes.TURTLE.create(level, (EntitySpawnReason)EntitySpawnReason.BREEDING);
               if (turtle != null) {
                  turtle.setAge(-24000);
                  turtle.setHomePos(pos);
                  turtle.snapTo((double)pos.getX() + 0.3 + (double)i * 0.2, (double)pos.getY(), (double)pos.getZ() + 0.3, 0.0F, 0.0F);
                  level.addFreshEntity(turtle);
               }
            }
         }
      }

   }

   public static boolean onSand(final BlockGetter level, final BlockPos pos) {
      return isSand(level, pos.below());
   }

   public static boolean isSand(final BlockGetter level, final BlockPos pos) {
      return level.getBlockState(pos).is(BlockTags.SAND);
   }

   protected void onPlace(final BlockState state, final Level level, final BlockPos pos, final BlockState oldState, final boolean movedByPiston) {
      if (onSand(level, pos) && !level.isClientSide()) {
         level.levelEvent(2012, pos, 15);
      }

   }

   private boolean shouldUpdateHatchLevel(final Level level, final BlockPos pos) {
      float chance = (Float)level.environmentAttributes().getValue(EnvironmentAttributes.TURTLE_EGG_HATCH_CHANCE, pos);
      return chance > 0.0F && level.getRandom().nextFloat() < chance;
   }

   public void playerDestroy(final Level level, final Player player, final BlockPos pos, final BlockState state, final @Nullable BlockEntity blockEntity, final ItemStack destroyedWith) {
      super.playerDestroy(level, player, pos, state, blockEntity, destroyedWith);
      this.decreaseEggs(level, pos, state);
   }

   protected boolean canBeReplaced(final BlockState state, final BlockPlaceContext context) {
      return !context.isSecondaryUseActive() && context.getItemInHand().is(this.asItem()) && (Integer)state.getValue(EGGS) < 4 ? true : super.canBeReplaced(state, context);
   }

   public @Nullable BlockState getStateForPlacement(final BlockPlaceContext context) {
      BlockState state = context.getLevel().getBlockState(context.getClickedPos());
      return state.is(this) ? (BlockState)state.setValue(EGGS, Math.min(4, (Integer)state.getValue(EGGS) + 1)) : super.getStateForPlacement(context);
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return (Integer)state.getValue(EGGS) == 1 ? SHAPE_SINGLE : SHAPE_MULTIPLE;
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(HATCH, EGGS);
   }

   private boolean canDestroyEgg(final ServerLevel level, final Entity entity) {
      if (!(entity instanceof Turtle) && !(entity instanceof Bat)) {
         if (!(entity instanceof LivingEntity)) {
            return false;
         } else {
            return entity instanceof Player || (Boolean)level.getGameRules().get(GameRules.MOB_GRIEFING);
         }
      } else {
         return false;
      }
   }

   static {
      HATCH = BlockStateProperties.HATCH;
      EGGS = BlockStateProperties.EGGS;
      SHAPE_SINGLE = Block.box(3.0, 0.0, 3.0, 12.0, 7.0, 12.0);
      SHAPE_MULTIPLE = Block.column(14.0, 0.0, 7.0);
   }
}
