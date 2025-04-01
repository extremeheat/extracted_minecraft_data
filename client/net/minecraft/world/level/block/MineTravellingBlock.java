package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.MineTravellingBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MineTravellingBlock extends BaseEntityBlock {
   public static final MapCodec<MineTravellingBlock> CODEC = simpleCodec(MineTravellingBlock::new);
   private static final VoxelShape SHAPE = Block.cube(12.0);

   public MapCodec<MineTravellingBlock> codec() {
      return CODEC;
   }

   protected MineTravellingBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new MineTravellingBlockEntity(var1, var2);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      return createTickerHelper(var3, BlockEntityType.MINE_TRAVELLING_BLOCK_ENTITY, var1.isClientSide ? MineTravellingBlockEntity::gatewayAnimationTick : MineTravellingBlockEntity::portalTick);
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      BlockEntity var5 = var2.getBlockEntity(var3);
      if (var5 instanceof MineTravellingBlockEntity var6) {
         int var7 = var6.getParticleAmount() * 2;

         for(int var8 = 0; var8 < var7; ++var8) {
            double var9 = (double)var3.getX() + var4.nextDouble();
            double var11 = (double)var3.getY() + var4.nextDouble();
            double var13 = (double)var3.getZ() + var4.nextDouble();
            double var15 = (var4.nextDouble() - 0.5) * 0.5;
            double var17 = (var4.nextDouble() - 0.5) * 0.5;
            double var19 = (var4.nextDouble() - 0.5) * 0.5;
            int var21 = var4.nextInt(2) * 2 - 1;
            if (var4.nextBoolean()) {
               var13 = (double)var3.getZ() + 0.5 + 0.25 * (double)var21;
               var19 = (double)(var4.nextFloat() * 2.0F * (float)var21);
            } else {
               var9 = (double)var3.getX() + 0.5 + 0.25 * (double)var21;
               var15 = (double)(var4.nextFloat() * 2.0F * (float)var21);
            }

            var2.addParticle(ParticleTypes.MINE_TRAVEL, var9, var11, var13, var15, var17, var19);
         }

      }
   }

   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      BlockEntity var6 = var2.getBlockEntity(var3);
      if (var4 instanceof ServerPlayer var7) {
         if (var2 instanceof ServerLevel var8) {
            if (var6 instanceof MineTravellingBlockEntity var9) {
               if (var9.isRevisitBlock() && !var8.isMine()) {
                  var8.leaveForMine(var3, true, Optional.of(var7.getUUID()));
               } else if (var8.isMine() && var7.isRevisiting()) {
                  var8.respawnPlayerIntoHub(var7, (var0) -> var0.getInventory().clearContent());
               } else {
                  var8.toggledMineTravellingBlock(var3);
               }
            }
         }
      }

      return InteractionResult.SUCCESS;
   }

   protected ItemStack getCloneItemStack(LevelReader var1, BlockPos var2, BlockState var3, boolean var4) {
      return ItemStack.EMPTY;
   }

   protected boolean canBeReplaced(BlockState var1, Fluid var2) {
      return false;
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   protected RenderShape getRenderShape(BlockState var1) {
      return RenderShape.INVISIBLE;
   }

   public static void createBlock(Level var0, BlockPos var1, ResourceKey<LevelStem> var2, boolean var3) {
      var0.destroyBlock(var1, true, (Entity)null);
      if (var0.setBlock(var1, Blocks.MINE_TRAVELLING_BLOCK.defaultBlockState(), 2)) {
         BlockEntity var4 = var0.getBlockEntity(var1);
         if (var4 instanceof MineTravellingBlockEntity) {
            MineTravellingBlockEntity var5 = (MineTravellingBlockEntity)var4;
            var5.setDimension(var2);
            var5.setRevisitBlock(var3);
         }
      }

      var0.globalLevelEvent(1038, var1, 0);
   }
}
