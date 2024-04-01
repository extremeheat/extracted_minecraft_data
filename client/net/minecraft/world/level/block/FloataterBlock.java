package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.grid.FlyingTickable;
import net.minecraft.world.grid.GridCarrier;
import net.minecraft.world.grid.SubGridBlocks;
import net.minecraft.world.grid.SubGridCapture;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.Vec3;

public class FloataterBlock extends Block implements FlyingTickable {
   public static final MapCodec<FloataterBlock> CODEC = simpleCodec(FloataterBlock::new);
   public static final DirectionProperty FACING = DirectionalBlock.FACING;
   public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;

   @Override
   protected MapCodec<FloataterBlock> codec() {
      return CODEC;
   }

   protected FloataterBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(TRIGGERED, Boolean.valueOf(false)));
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return this.defaultBlockState().setValue(FACING, var1.getNearestLookingDirection());
   }

   @Override
   protected BlockState rotate(BlockState var1, Rotation var2) {
      return var1.setValue(FACING, var2.rotate(var1.getValue(FACING)));
   }

   @Override
   protected BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation(var1.getValue(FACING)));
   }

   @Override
   protected void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      boolean var7 = shouldTriggerNoQC(DoorBlock.shouldTrigger(var2, var3), var2, var3);
      boolean var8 = var1.getValue(TRIGGERED);
      if (var7 != var8) {
         if (var7) {
            var2.scheduleTick(var3, this, 1);
            var2.setBlock(var3, var1.setValue(TRIGGERED, Boolean.valueOf(true)), 2);
         } else {
            var2.setBlock(var3, var1.setValue(TRIGGERED, Boolean.valueOf(false)), 2);
         }
      }
   }

   private static boolean shouldTriggerNoQC(boolean var0, Level var1, BlockPos var2) {
      return var0 && var1.hasNeighborSignal(var2);
   }

   @Override
   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      Direction var5 = var1.getValue(FACING);
      SubGridCapture var6 = SubGridCapture.scan(var2, var3, var5);
      if (var6 != null) {
         GridCarrier var7 = new GridCarrier(EntityType.GRID_CARRIER, var2);
         BlockPos var8 = var6.minPos();
         var7.moveTo((double)var8.getX(), (double)var8.getY(), (double)var8.getZ());
         var7.grid().setBlocks(var6.blocks());
         var7.grid().setBiome(var2.getBiome(var3));
         var7.setMovement(var5, (float)var6.engines() * 0.1F);
         var6.remove(var2);
         var2.addFreshEntity(var7);
      }
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, TRIGGERED);
   }

   @Override
   public void flyingTick(Level var1, SubGridBlocks var2, BlockState var3, BlockPos var4, Vec3 var5, Direction var6) {
      if (var1.isClientSide) {
         Direction var7 = var3.getValue(FACING);
         if (var6 == var7 && var3.getValue(TRIGGERED) && var1.getRandom().nextBoolean()) {
            Direction var8 = var7.getOpposite();
            if (var2.getBlockState(var4.relative(var8)).isAir()) {
               double var9 = 0.5;
               var5 = var5.add(0.5, 0.5, 0.5).add((double)var8.getStepX() * 0.5, (double)var8.getStepY() * 0.5, (double)var8.getStepZ() * 0.5);
               var1.addParticle(ParticleTypes.CLOUD, var5.x, var5.y, var5.z, 0.0, 0.0, 0.0);
            }
         }
      }
   }
}
