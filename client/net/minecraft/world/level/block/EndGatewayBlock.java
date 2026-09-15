package net.minecraft.world.level.block;

import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class EndGatewayBlock extends BaseEntityBlock implements Portal {
   protected EndGatewayBlock(final BlockBehaviour.Properties properties) {
      super(properties);
   }

   public BlockEntity newBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
      return new TheEndGatewayBlockEntity(worldPosition, blockState);
   }

   public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(final Level level, final BlockState blockState, final BlockEntityType<T> type) {
      return createTickerHelper(type, BlockEntityTypes.END_GATEWAY, level.isClientSide() ? TheEndGatewayBlockEntity::beamAnimationTick : TheEndGatewayBlockEntity::portalTick);
   }

   public void animateTick(final BlockState state, final Level level, final BlockPos pos, final RandomSource random) {
      BlockEntity blockEntity = level.getBlockEntity(pos);
      if (blockEntity instanceof TheEndGatewayBlockEntity theEndGatewayBlockEntity) {
         int particleCount = theEndGatewayBlockEntity.getParticleAmount();

         for(int i = 0; i < particleCount; ++i) {
            double x = (double)pos.getX() + random.nextDouble();
            double y = (double)pos.getY() + random.nextDouble();
            double z = (double)pos.getZ() + random.nextDouble();
            double xa = (random.nextDouble() - 0.5) * 0.5;
            double ya = (random.nextDouble() - 0.5) * 0.5;
            double za = (random.nextDouble() - 0.5) * 0.5;
            int flip = random.nextInt(2) * 2 - 1;
            if (random.nextBoolean()) {
               z = (double)pos.getZ() + 0.5 + 0.25 * (double)flip;
               za = (double)(random.nextFloat() * 2.0F * (float)flip);
            } else {
               x = (double)pos.getX() + 0.5 + 0.25 * (double)flip;
               xa = (double)(random.nextFloat() * 2.0F * (float)flip);
            }

            level.addParticle(ParticleTypes.PORTAL, x, y, z, xa, ya, za);
         }

      }
   }

   protected ItemStack getCloneItemStack(final LevelReader level, final BlockPos pos, final BlockState state, final boolean includeData) {
      return ItemStack.EMPTY;
   }

   protected boolean canBeReplaced(final BlockState state, final Fluid fluid) {
      return false;
   }

   protected void entityInside(final BlockState state, final Level level, final BlockPos pos, final Entity entity, final InsideBlockEffectApplier effectApplier, final boolean isPrecise) {
      if (entity.canUsePortal(false)) {
         BlockEntity blockEntity = level.getBlockEntity(pos);
         if (!level.isClientSide() && blockEntity instanceof TheEndGatewayBlockEntity) {
            TheEndGatewayBlockEntity endGatewayBlockEntity = (TheEndGatewayBlockEntity)blockEntity;
            if (!endGatewayBlockEntity.isCoolingDown()) {
               entity.setAsInsidePortal(this, pos);
               TheEndGatewayBlockEntity.triggerCooldown(level, pos, state, endGatewayBlockEntity);
            }
         }
      }

   }

   public @Nullable TeleportTransition getPortalDestination(final ServerLevel currentLevel, final Entity entity, final BlockPos portalEntryPos) {
      BlockEntity blockEntity = currentLevel.getBlockEntity(portalEntryPos);
      if (blockEntity instanceof TheEndGatewayBlockEntity endGatewayBlockEntity) {
         Vec3 teleportPosition = endGatewayBlockEntity.getPortalPosition(currentLevel, portalEntryPos);
         if (teleportPosition == null) {
            return null;
         } else {
            return entity instanceof ThrownEnderpearl ? new TeleportTransition(currentLevel, teleportPosition, Vec3.ZERO, 0.0F, 0.0F, Set.of(), TeleportTransition.PLACE_PORTAL_TICKET) : new TeleportTransition(currentLevel, teleportPosition, Vec3.ZERO, 0.0F, 0.0F, Relative.union(Relative.DELTA, Relative.ROTATION), TeleportTransition.PLACE_PORTAL_TICKET);
         }
      } else {
         return null;
      }
   }

   protected RenderShape getRenderShape(final BlockState state) {
      return RenderShape.INVISIBLE;
   }
}
