package net.minecraft.world.level.block;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CampfireBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
   protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 7.0, 16.0);
   public static final BooleanProperty LIT = BlockStateProperties.LIT;
   public static final BooleanProperty SIGNAL_FIRE = BlockStateProperties.SIGNAL_FIRE;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   private static final VoxelShape VIRTUAL_FENCE_POST = Block.box(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);
   private static final int SMOKE_DISTANCE = 5;
   private final boolean spawnParticles;
   private final int fireDamage;

   public CampfireBlock(boolean var1, int var2, BlockBehaviour.Properties var3) {
      super(var3);
      this.spawnParticles = var1;
      this.fireDamage = var2;
      this.registerDefaultState(
         this.stateDefinition
            .any()
            .setValue(LIT, Boolean.valueOf(true))
            .setValue(SIGNAL_FIRE, Boolean.valueOf(false))
            .setValue(WATERLOGGED, Boolean.valueOf(false))
            .setValue(FACING, Direction.NORTH)
      );
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      BlockEntity var7 = var2.getBlockEntity(var3);
      if (var7 instanceof CampfireBlockEntity var8) {
         ItemStack var9 = var4.getItemInHand(var5);
         Optional var10 = var8.getCookableRecipe(var9);
         if (var10.isPresent()) {
            if (!var2.isClientSide
               && var8.placeFood(var4, var4.getAbilities().instabuild ? var9.copy() : var9, ((CampfireCookingRecipe)var10.get()).getCookingTime())) {
               var4.awardStat(Stats.INTERACT_WITH_CAMPFIRE);
               return InteractionResult.SUCCESS;
            }

            return InteractionResult.CONSUME;
         }
      }

      return InteractionResult.PASS;
   }

   @Override
   public void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      if (var1.getValue(LIT) && var4 instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)var4)) {
         var4.hurt(var2.damageSources().inFire(), (float)this.fireDamage);
      }

      super.entityInside(var1, var2, var3, var4);
   }

   @Override
   public void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var1.is(var4.getBlock())) {
         BlockEntity var6 = var2.getBlockEntity(var3);
         if (var6 instanceof CampfireBlockEntity) {
            Containers.dropContents(var2, var3, ((CampfireBlockEntity)var6).getItems());
         }

         super.onRemove(var1, var2, var3, var4, var5);
      }
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      boolean var4 = var2.getFluidState(var3).getType() == Fluids.WATER;
      return this.defaultBlockState()
         .setValue(WATERLOGGED, Boolean.valueOf(var4))
         .setValue(SIGNAL_FIRE, Boolean.valueOf(this.isSmokeSource(var2.getBlockState(var3.below()))))
         .setValue(LIT, Boolean.valueOf(!var4))
         .setValue(FACING, var1.getHorizontalDirection());
   }

   @Override
   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var1.getValue(WATERLOGGED)) {
         var4.scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(var4));
      }

      return var2 == Direction.DOWN
         ? var1.setValue(SIGNAL_FIRE, Boolean.valueOf(this.isSmokeSource(var3)))
         : super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   private boolean isSmokeSource(BlockState var1) {
      return var1.is(Blocks.HAY_BLOCK);
   }

   @Override
   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   @Override
   public RenderShape getRenderShape(BlockState var1) {
      return RenderShape.MODEL;
   }

   @Override
   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      if (var1.getValue(LIT)) {
         if (var4.nextInt(10) == 0) {
            var2.playLocalSound(
               (double)var3.getX() + 0.5,
               (double)var3.getY() + 0.5,
               (double)var3.getZ() + 0.5,
               SoundEvents.CAMPFIRE_CRACKLE,
               SoundSource.BLOCKS,
               0.5F + var4.nextFloat(),
               var4.nextFloat() * 0.7F + 0.6F,
               false
            );
         }

         if (this.spawnParticles && var4.nextInt(5) == 0) {
            for(int var5 = 0; var5 < var4.nextInt(1) + 1; ++var5) {
               var2.addParticle(
                  ParticleTypes.LAVA,
                  (double)var3.getX() + 0.5,
                  (double)var3.getY() + 0.5,
                  (double)var3.getZ() + 0.5,
                  (double)(var4.nextFloat() / 2.0F),
                  5.0E-5,
                  (double)(var4.nextFloat() / 2.0F)
               );
            }
         }
      }
   }

   public static void dowse(@Nullable Entity var0, LevelAccessor var1, BlockPos var2, BlockState var3) {
      if (var1.isClientSide()) {
         for(int var4 = 0; var4 < 20; ++var4) {
            makeParticles((Level)var1, var2, var3.getValue(SIGNAL_FIRE), true);
         }
      }

      BlockEntity var5 = var1.getBlockEntity(var2);
      if (var5 instanceof CampfireBlockEntity) {
         ((CampfireBlockEntity)var5).dowse();
      }

      var1.gameEvent(var0, GameEvent.BLOCK_CHANGE, var2);
   }

   @Override
   public boolean placeLiquid(LevelAccessor var1, BlockPos var2, BlockState var3, FluidState var4) {
      if (!var3.getValue(BlockStateProperties.WATERLOGGED) && var4.getType() == Fluids.WATER) {
         boolean var5 = var3.getValue(LIT);
         if (var5) {
            if (!var1.isClientSide()) {
               var1.playSound(null, var2, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0F, 1.0F);
            }

            dowse(null, var1, var2, var3);
         }

         var1.setBlock(var2, var3.setValue(WATERLOGGED, Boolean.valueOf(true)).setValue(LIT, Boolean.valueOf(false)), 3);
         var1.scheduleTick(var2, var4.getType(), var4.getType().getTickDelay(var1));
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void onProjectileHit(Level var1, BlockState var2, BlockHitResult var3, Projectile var4) {
      BlockPos var5 = var3.getBlockPos();
      if (!var1.isClientSide && var4.isOnFire() && var4.mayInteract(var1, var5) && !var2.getValue(LIT) && !var2.getValue(WATERLOGGED)) {
         var1.setBlock(var5, var2.setValue(BlockStateProperties.LIT, Boolean.valueOf(true)), 11);
      }
   }

   public static void makeParticles(Level var0, BlockPos var1, boolean var2, boolean var3) {
      RandomSource var4 = var0.getRandom();
      SimpleParticleType var5 = var2 ? ParticleTypes.CAMPFIRE_SIGNAL_SMOKE : ParticleTypes.CAMPFIRE_COSY_SMOKE;
      var0.addAlwaysVisibleParticle(
         var5,
         true,
         (double)var1.getX() + 0.5 + var4.nextDouble() / 3.0 * (double)(var4.nextBoolean() ? 1 : -1),
         (double)var1.getY() + var4.nextDouble() + var4.nextDouble(),
         (double)var1.getZ() + 0.5 + var4.nextDouble() / 3.0 * (double)(var4.nextBoolean() ? 1 : -1),
         0.0,
         0.07,
         0.0
      );
      if (var3) {
         var0.addParticle(
            ParticleTypes.SMOKE,
            (double)var1.getX() + 0.5 + var4.nextDouble() / 4.0 * (double)(var4.nextBoolean() ? 1 : -1),
            (double)var1.getY() + 0.4,
            (double)var1.getZ() + 0.5 + var4.nextDouble() / 4.0 * (double)(var4.nextBoolean() ? 1 : -1),
            0.0,
            0.005,
            0.0
         );
      }
   }

   public static boolean isSmokeyPos(Level var0, BlockPos var1) {
      for(int var2 = 1; var2 <= 5; ++var2) {
         BlockPos var3 = var1.below(var2);
         BlockState var4 = var0.getBlockState(var3);
         if (isLitCampfire(var4)) {
            return true;
         }

         boolean var5 = Shapes.joinIsNotEmpty(VIRTUAL_FENCE_POST, var4.getCollisionShape(var0, var1, CollisionContext.empty()), BooleanOp.AND);
         if (var5) {
            BlockState var6 = var0.getBlockState(var3.below());
            return isLitCampfire(var6);
         }
      }

      return false;
   }

   public static boolean isLitCampfire(BlockState var0) {
      return var0.hasProperty(LIT) && var0.is(BlockTags.CAMPFIRES) && var0.getValue(LIT);
   }

   @Override
   public FluidState getFluidState(BlockState var1) {
      return var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   @Override
   public BlockState rotate(BlockState var1, Rotation var2) {
      return var1.setValue(FACING, var2.rotate(var1.getValue(FACING)));
   }

   @Override
   public BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation(var1.getValue(FACING)));
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(LIT, SIGNAL_FIRE, WATERLOGGED, FACING);
   }

   @Override
   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new CampfireBlockEntity(var1, var2);
   }

   @Nullable
   @Override
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      if (var1.isClientSide) {
         return var2.getValue(LIT) ? createTickerHelper(var3, BlockEntityType.CAMPFIRE, CampfireBlockEntity::particleTick) : null;
      } else {
         return var2.getValue(LIT)
            ? createTickerHelper(var3, BlockEntityType.CAMPFIRE, CampfireBlockEntity::cookTick)
            : createTickerHelper(var3, BlockEntityType.CAMPFIRE, CampfireBlockEntity::cooldownTick);
      }
   }

   @Override
   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      return false;
   }

   public static boolean canLight(BlockState var0) {
      return var0.is(BlockTags.CAMPFIRES, var0x -> var0x.hasProperty(WATERLOGGED) && var0x.hasProperty(LIT))
         && !var0.getValue(WATERLOGGED)
         && !var0.getValue(LIT);
   }
}
