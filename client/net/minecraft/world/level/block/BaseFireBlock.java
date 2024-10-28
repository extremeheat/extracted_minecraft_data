package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
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
   private static final int SECONDS_ON_FIRE = 8;
   private final float fireDamage;
   protected static final float AABB_OFFSET = 1.0F;
   protected static final VoxelShape DOWN_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);

   public BaseFireBlock(BlockBehaviour.Properties var1, float var2) {
      super(var1);
      this.fireDamage = var2;
   }

   protected abstract MapCodec<? extends BaseFireBlock> codec();

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return getState(var1.getLevel(), var1.getClickedPos());
   }

   public static BlockState getState(BlockGetter var0, BlockPos var1) {
      BlockPos var2 = var1.below();
      BlockState var3 = var0.getBlockState(var2);
      return SoulFireBlock.canSurviveOnBlock(var3) ? Blocks.SOUL_FIRE.defaultBlockState() : ((FireBlock)Blocks.FIRE).getStateForPlacement(var0, var1);
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return DOWN_AABB;
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      if (var4.nextInt(24) == 0) {
         var2.playLocalSound((double)var3.getX() + 0.5, (double)var3.getY() + 0.5, (double)var3.getZ() + 0.5, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 1.0F + var4.nextFloat(), var4.nextFloat() * 0.7F + 0.3F, false);
      }

      BlockPos var5 = var3.below();
      BlockState var6 = var2.getBlockState(var5);
      int var7;
      double var8;
      double var10;
      double var12;
      if (!this.canBurn(var6) && !var6.isFaceSturdy(var2, var5, Direction.UP)) {
         if (this.canBurn(var2.getBlockState(var3.west()))) {
            for(var7 = 0; var7 < 2; ++var7) {
               var8 = (double)var3.getX() + var4.nextDouble() * 0.10000000149011612;
               var10 = (double)var3.getY() + var4.nextDouble();
               var12 = (double)var3.getZ() + var4.nextDouble();
               var2.addParticle(ParticleTypes.LARGE_SMOKE, var8, var10, var12, 0.0, 0.0, 0.0);
            }
         }

         if (this.canBurn(var2.getBlockState(var3.east()))) {
            for(var7 = 0; var7 < 2; ++var7) {
               var8 = (double)(var3.getX() + 1) - var4.nextDouble() * 0.10000000149011612;
               var10 = (double)var3.getY() + var4.nextDouble();
               var12 = (double)var3.getZ() + var4.nextDouble();
               var2.addParticle(ParticleTypes.LARGE_SMOKE, var8, var10, var12, 0.0, 0.0, 0.0);
            }
         }

         if (this.canBurn(var2.getBlockState(var3.north()))) {
            for(var7 = 0; var7 < 2; ++var7) {
               var8 = (double)var3.getX() + var4.nextDouble();
               var10 = (double)var3.getY() + var4.nextDouble();
               var12 = (double)var3.getZ() + var4.nextDouble() * 0.10000000149011612;
               var2.addParticle(ParticleTypes.LARGE_SMOKE, var8, var10, var12, 0.0, 0.0, 0.0);
            }
         }

         if (this.canBurn(var2.getBlockState(var3.south()))) {
            for(var7 = 0; var7 < 2; ++var7) {
               var8 = (double)var3.getX() + var4.nextDouble();
               var10 = (double)var3.getY() + var4.nextDouble();
               var12 = (double)(var3.getZ() + 1) - var4.nextDouble() * 0.10000000149011612;
               var2.addParticle(ParticleTypes.LARGE_SMOKE, var8, var10, var12, 0.0, 0.0, 0.0);
            }
         }

         if (this.canBurn(var2.getBlockState(var3.above()))) {
            for(var7 = 0; var7 < 2; ++var7) {
               var8 = (double)var3.getX() + var4.nextDouble();
               var10 = (double)(var3.getY() + 1) - var4.nextDouble() * 0.10000000149011612;
               var12 = (double)var3.getZ() + var4.nextDouble();
               var2.addParticle(ParticleTypes.LARGE_SMOKE, var8, var10, var12, 0.0, 0.0, 0.0);
            }
         }
      } else {
         for(var7 = 0; var7 < 3; ++var7) {
            var8 = (double)var3.getX() + var4.nextDouble();
            var10 = (double)var3.getY() + var4.nextDouble() * 0.5 + 0.5;
            var12 = (double)var3.getZ() + var4.nextDouble();
            var2.addParticle(ParticleTypes.LARGE_SMOKE, var8, var10, var12, 0.0, 0.0, 0.0);
         }
      }

   }

   protected abstract boolean canBurn(BlockState var1);

   protected void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      if (!var4.fireImmune()) {
         var4.setRemainingFireTicks(var4.getRemainingFireTicks() + 1);
         if (var4.getRemainingFireTicks() == 0) {
            var4.igniteForSeconds(8);
         }
      }

      var4.hurt(var2.damageSources().inFire(), this.fireDamage);
      super.entityInside(var1, var2, var3, var4);
   }

   protected void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
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

   protected void spawnDestroyParticles(Level var1, Player var2, BlockPos var3, BlockState var4) {
   }

   public BlockState playerWillDestroy(Level var1, BlockPos var2, BlockState var3, Player var4) {
      if (!var1.isClientSide()) {
         var1.levelEvent((Player)null, 1009, var2, 0);
      }

      return super.playerWillDestroy(var1, var2, var3, var4);
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

         if (!var4) {
            return false;
         } else {
            Direction.Axis var9 = var2.getAxis().isHorizontal() ? var2.getCounterClockWise().getAxis() : Direction.Plane.HORIZONTAL.getRandomAxis(var0.random);
            return PortalShape.findEmptyPortalShape(var0, var1, var9).isPresent();
         }
      }
   }
}
