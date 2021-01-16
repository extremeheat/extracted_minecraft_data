package net.minecraft.world.level.block;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class BaseFireBlock extends Block {
   private final float fireDamage;
   protected static final VoxelShape DOWN_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

   public BaseFireBlock(BlockBehaviour.Properties var1, float var2) {
      super(var1);
      this.fireDamage = var2;
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return getState(var1.getLevel(), var1.getClickedPos());
   }

   public static BlockState getState(BlockGetter var0, BlockPos var1) {
      BlockPos var2 = var1.below();
      BlockState var3 = var0.getBlockState(var2);
      return SoulFireBlock.canSurviveOnBlock(var3.getBlock()) ? Blocks.SOUL_FIRE.defaultBlockState() : ((FireBlock)Blocks.FIRE).getStateForPlacement(var0, var1);
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return DOWN_AABB;
   }

   protected abstract boolean canBurn(BlockState var1);

   public void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      if (!var4.fireImmune()) {
         var4.setRemainingFireTicks(var4.getRemainingFireTicks() + 1);
         if (var4.getRemainingFireTicks() == 0) {
            var4.setSecondsOnFire(8);
         }

         var4.hurt(DamageSource.IN_FIRE, this.fireDamage);
      }

      super.entityInside(var1, var2, var3, var4);
   }

   public void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var4.is(var1.getBlock())) {
         if (inPortalDimension(var2)) {
            Optional var6 = PortalShape.findEmptyPortalShape(var2, var3, Direction.Axis.X);
            if (var6.isPresent()) {
               ((PortalShape)var6.get()).createPortalBlocks();
               return;
            }
         }

         if (!var1.canSurvive(var2, var3)) {
            var2.removeBlock(var3, false);
         }

      }
   }

   private static boolean inPortalDimension(Level var0) {
      return var0.dimension() == Level.OVERWORLD || var0.dimension() == Level.NETHER;
   }

   public void playerWillDestroy(Level var1, BlockPos var2, BlockState var3, Player var4) {
      if (!var1.isClientSide()) {
         var1.levelEvent((Player)null, 1009, var2, 0);
      }

   }

   public static boolean canBePlacedAt(Level var0, BlockPos var1, Direction var2) {
      BlockState var3 = var0.getBlockState(var1);
      if (!var3.isAir()) {
         return false;
      } else {
         return getState(var0, var1).canSurvive(var0, var1) || isPortal(var0, var1, var2);
      }
   }

   private static boolean isPortal(Level var0, BlockPos var1, Direction var2) {
      if (!inPortalDimension(var0)) {
         return false;
      } else {
         BlockPos.MutableBlockPos var3 = var1.mutable();
         boolean var4 = false;
         Direction[] var5 = Direction.values();
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Direction var8 = var5[var7];
            if (var0.getBlockState(var3.set(var1).move(var8)).is(Blocks.OBSIDIAN)) {
               var4 = true;
               break;
            }
         }

         return var4 && PortalShape.findEmptyPortalShape(var0, var1, var2.getCounterClockWise().getAxis()).isPresent();
      }
   }
}
