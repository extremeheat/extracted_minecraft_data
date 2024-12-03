package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BrewingStandBlock extends BaseEntityBlock {
   public static final MapCodec<BrewingStandBlock> CODEC = simpleCodec(BrewingStandBlock::new);
   public static final BooleanProperty[] HAS_BOTTLE;
   protected static final VoxelShape SHAPE;

   public MapCodec<BrewingStandBlock> codec() {
      return CODEC;
   }

   public BrewingStandBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(HAS_BOTTLE[0], false)).setValue(HAS_BOTTLE[1], false)).setValue(HAS_BOTTLE[2], false));
   }

   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new BrewingStandBlockEntity(var1, var2);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      return var1.isClientSide ? null : createTickerHelper(var3, BlockEntityType.BREWING_STAND, BrewingStandBlockEntity::serverTick);
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      if (!var2.isClientSide) {
         BlockEntity var7 = var2.getBlockEntity(var3);
         if (var7 instanceof BrewingStandBlockEntity) {
            BrewingStandBlockEntity var6 = (BrewingStandBlockEntity)var7;
            var4.openMenu(var6);
            var4.awardStat(Stats.INTERACT_WITH_BREWINGSTAND);
         }
      }

      return InteractionResult.SUCCESS;
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      double var5 = (double)var3.getX() + 0.4 + (double)var4.nextFloat() * 0.2;
      double var7 = (double)var3.getY() + 0.7 + (double)var4.nextFloat() * 0.3;
      double var9 = (double)var3.getZ() + 0.4 + (double)var4.nextFloat() * 0.2;
      var2.addParticle(ParticleTypes.SMOKE, var5, var7, var9, 0.0, 0.0, 0.0);
   }

   protected void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      Containers.dropContentsOnDestroy(var1, var4, var2, var3);
      super.onRemove(var1, var2, var3, var4, var5);
   }

   protected boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   protected int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(var2.getBlockEntity(var3));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(HAS_BOTTLE[0], HAS_BOTTLE[1], HAS_BOTTLE[2]);
   }

   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return false;
   }

   static {
      HAS_BOTTLE = new BooleanProperty[]{BlockStateProperties.HAS_BOTTLE_0, BlockStateProperties.HAS_BOTTLE_1, BlockStateProperties.HAS_BOTTLE_2};
      SHAPE = Shapes.or(Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0), Block.box(7.0, 0.0, 7.0, 9.0, 14.0, 9.0));
   }
}
