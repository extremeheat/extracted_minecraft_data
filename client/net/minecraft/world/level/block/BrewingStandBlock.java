package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
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
   public static final BooleanProperty[] HAS_BOTTLE;
   protected static final VoxelShape SHAPE;

   public BrewingStandBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(HAS_BOTTLE[0], false)).setValue(HAS_BOTTLE[1], false)).setValue(HAS_BOTTLE[2], false));
   }

   public RenderShape getRenderShape(BlockState var1) {
      return RenderShape.MODEL;
   }

   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new BrewingStandBlockEntity(var1, var2);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      return var1.isClientSide ? null : createTickerHelper(var3, BlockEntityType.BREWING_STAND, BrewingStandBlockEntity::serverTick);
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      if (var2.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         BlockEntity var7 = var2.getBlockEntity(var3);
         if (var7 instanceof BrewingStandBlockEntity) {
            var4.openMenu((BrewingStandBlockEntity)var7);
            var4.awardStat(Stats.INTERACT_WITH_BREWINGSTAND);
         }

         return InteractionResult.CONSUME;
      }
   }

   public void setPlacedBy(Level var1, BlockPos var2, BlockState var3, LivingEntity var4, ItemStack var5) {
      if (var5.hasCustomHoverName()) {
         BlockEntity var6 = var1.getBlockEntity(var2);
         if (var6 instanceof BrewingStandBlockEntity) {
            ((BrewingStandBlockEntity)var6).setCustomName(var5.getHoverName());
         }
      }

   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, Random var4) {
      double var5 = (double)var3.getX() + 0.4D + (double)var4.nextFloat() * 0.2D;
      double var7 = (double)var3.getY() + 0.7D + (double)var4.nextFloat() * 0.3D;
      double var9 = (double)var3.getZ() + 0.4D + (double)var4.nextFloat() * 0.2D;
      var2.addParticle(ParticleTypes.SMOKE, var5, var7, var9, 0.0D, 0.0D, 0.0D);
   }

   public void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var1.is(var4.getBlock())) {
         BlockEntity var6 = var2.getBlockEntity(var3);
         if (var6 instanceof BrewingStandBlockEntity) {
            Containers.dropContents(var2, (BlockPos)var3, (Container)((BrewingStandBlockEntity)var6));
         }

         super.onRemove(var1, var2, var3, var4, var5);
      }
   }

   public boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(var2.getBlockEntity(var3));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(HAS_BOTTLE[0], HAS_BOTTLE[1], HAS_BOTTLE[2]);
   }

   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      return false;
   }

   static {
      HAS_BOTTLE = new BooleanProperty[]{BlockStateProperties.HAS_BOTTLE_0, BlockStateProperties.HAS_BOTTLE_1, BlockStateProperties.HAS_BOTTLE_2};
      SHAPE = Shapes.or(Block.box(1.0D, 0.0D, 1.0D, 15.0D, 2.0D, 15.0D), Block.box(7.0D, 0.0D, 7.0D, 9.0D, 14.0D, 9.0D));
   }
}
