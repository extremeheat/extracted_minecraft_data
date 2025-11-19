package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.happyghast.HappyGhast;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class DriedGhastBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {
   public static final MapCodec<DriedGhastBlock> CODEC = simpleCodec(DriedGhastBlock::new);
   public static final int MAX_HYDRATION_LEVEL = 3;
   public static final IntegerProperty HYDRATION_LEVEL;
   public static final BooleanProperty WATERLOGGED;
   public static final int HYDRATION_TICK_DELAY = 5000;
   private static final VoxelShape SHAPE;

   public MapCodec<DriedGhastBlock> codec() {
      return CODEC;
   }

   public DriedGhastBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(HYDRATION_LEVEL, 0)).setValue(WATERLOGGED, false));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, HYDRATION_LEVEL, WATERLOGGED);
   }

   protected BlockState updateShape(BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8) {
      if ((Boolean)var1.getValue(WATERLOGGED)) {
         var3.scheduleTick(var4, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(var2));
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   public int getHydrationLevel(BlockState var1) {
      return (Integer)var1.getValue(HYDRATION_LEVEL);
   }

   private boolean isReadyToSpawn(BlockState var1) {
      return this.getHydrationLevel(var1) == 3;
   }

   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if ((Boolean)var1.getValue(WATERLOGGED)) {
         this.tickWaterlogged(var1, var2, var3, var4);
      } else {
         int var5 = this.getHydrationLevel(var1);
         if (var5 > 0) {
            var2.setBlock(var3, (BlockState)var1.setValue(HYDRATION_LEVEL, var5 - 1), 2);
            var2.gameEvent(GameEvent.BLOCK_CHANGE, var3, GameEvent.Context.of(var1));
         }

      }
   }

   private void tickWaterlogged(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (!this.isReadyToSpawn(var1)) {
         var2.playSound((Entity)null, var3, SoundEvents.DRIED_GHAST_TRANSITION, SoundSource.BLOCKS, 1.0F, 1.0F);
         var2.setBlock(var3, (BlockState)var1.setValue(HYDRATION_LEVEL, this.getHydrationLevel(var1) + 1), 2);
         var2.gameEvent(GameEvent.BLOCK_CHANGE, var3, GameEvent.Context.of(var1));
      } else {
         this.spawnGhastling(var2, var3, var1);
      }

   }

   private void spawnGhastling(ServerLevel var1, BlockPos var2, BlockState var3) {
      var1.removeBlock(var2, false);
      HappyGhast var4 = EntityType.HAPPY_GHAST.create(var1, EntitySpawnReason.BREEDING);
      if (var4 != null) {
         Vec3 var5 = var2.getBottomCenter();
         var4.setBaby(true);
         float var6 = Direction.getYRot((Direction)var3.getValue(FACING));
         var4.setYHeadRot(var6);
         var4.snapTo(var5.x(), var5.y(), var5.z(), var6, 0.0F);
         var1.addFreshEntity(var4);
         var1.playSound((Entity)null, var4, SoundEvents.GHASTLING_SPAWN, SoundSource.BLOCKS, 1.0F, 1.0F);
      }

   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      double var5 = (double)var3.getX() + 0.5;
      double var7 = (double)var3.getY() + 0.5;
      double var9 = (double)var3.getZ() + 0.5;
      if (!(Boolean)var1.getValue(WATERLOGGED)) {
         if (var4.nextInt(40) == 0 && var2.getBlockState(var3.below()).is(BlockTags.TRIGGERS_AMBIENT_DRIED_GHAST_BLOCK_SOUNDS)) {
            var2.playLocalSound(var5, var7, var9, SoundEvents.DRIED_GHAST_AMBIENT, SoundSource.BLOCKS, 1.0F, 1.0F, false);
         }

         if (var4.nextInt(6) == 0) {
            var2.addParticle(ParticleTypes.WHITE_SMOKE, var5, var7, var9, 0.0, 0.02, 0.0);
         }
      } else {
         if (var4.nextInt(40) == 0) {
            var2.playLocalSound(var5, var7, var9, SoundEvents.DRIED_GHAST_AMBIENT_WATER, SoundSource.BLOCKS, 1.0F, 1.0F, false);
         }

         if (var4.nextInt(6) == 0) {
            var2.addParticle(ParticleTypes.HAPPY_VILLAGER, var5 + (double)((var4.nextFloat() * 2.0F - 1.0F) / 3.0F), var7 + 0.4, var9 + (double)((var4.nextFloat() * 2.0F - 1.0F) / 3.0F), 0.0, (double)var4.nextFloat(), 0.0);
         }
      }

   }

   protected void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (((Boolean)var1.getValue(WATERLOGGED) || (Integer)var1.getValue(HYDRATION_LEVEL) > 0) && !var2.getBlockTicks().hasScheduledTick(var3, this)) {
         var2.scheduleTick(var3, this, 5000);
      }

   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      FluidState var2 = var1.getLevel().getFluidState(var1.getClickedPos());
      boolean var3 = var2.getType() == Fluids.WATER;
      return (BlockState)((BlockState)super.getStateForPlacement(var1).setValue(WATERLOGGED, var3)).setValue(FACING, var1.getHorizontalDirection().getOpposite());
   }

   protected FluidState getFluidState(BlockState var1) {
      return (Boolean)var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   public boolean placeLiquid(LevelAccessor var1, BlockPos var2, BlockState var3, FluidState var4) {
      if (!(Boolean)var3.getValue(BlockStateProperties.WATERLOGGED) && var4.getType() == Fluids.WATER) {
         if (!var1.isClientSide()) {
            var1.setBlock(var2, (BlockState)var3.setValue(BlockStateProperties.WATERLOGGED, true), 3);
            var1.scheduleTick(var2, var4.getType(), var4.getType().getTickDelay(var1));
            var1.playSound((Entity)null, var2, SoundEvents.DRIED_GHAST_PLACE_IN_WATER, SoundSource.BLOCKS, 1.0F, 1.0F);
         }

         return true;
      } else {
         return false;
      }
   }

   public void setPlacedBy(Level var1, BlockPos var2, BlockState var3, @Nullable LivingEntity var4, ItemStack var5) {
      super.setPlacedBy(var1, var2, var3, var4, var5);
      var1.playSound((Entity)null, (BlockPos)var2, (Boolean)var3.getValue(WATERLOGGED) ? SoundEvents.DRIED_GHAST_PLACE_IN_WATER : SoundEvents.DRIED_GHAST_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
   }

   public boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return false;
   }

   static {
      HYDRATION_LEVEL = BlockStateProperties.DRIED_GHAST_HYDRATION_LEVELS;
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      SHAPE = Block.column(10.0, 10.0, 0.0, 10.0);
   }
}
