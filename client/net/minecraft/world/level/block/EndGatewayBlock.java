package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;

public class EndGatewayBlock extends BaseEntityBlock implements Portal {
   public static final MapCodec<EndGatewayBlock> CODEC = simpleCodec(EndGatewayBlock::new);

   public MapCodec<EndGatewayBlock> codec() {
      return CODEC;
   }

   protected EndGatewayBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new TheEndGatewayBlockEntity(var1, var2);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      return createTickerHelper(var3, BlockEntityType.END_GATEWAY, var1.isClientSide ? TheEndGatewayBlockEntity::beamAnimationTick : TheEndGatewayBlockEntity::portalTick);
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      BlockEntity var5 = var2.getBlockEntity(var3);
      if (var5 instanceof TheEndGatewayBlockEntity) {
         int var6 = ((TheEndGatewayBlockEntity)var5).getParticleAmount();

         for(int var7 = 0; var7 < var6; ++var7) {
            double var8 = (double)var3.getX() + var4.nextDouble();
            double var10 = (double)var3.getY() + var4.nextDouble();
            double var12 = (double)var3.getZ() + var4.nextDouble();
            double var14 = (var4.nextDouble() - 0.5) * 0.5;
            double var16 = (var4.nextDouble() - 0.5) * 0.5;
            double var18 = (var4.nextDouble() - 0.5) * 0.5;
            int var20 = var4.nextInt(2) * 2 - 1;
            if (var4.nextBoolean()) {
               var12 = (double)var3.getZ() + 0.5 + 0.25 * (double)var20;
               var18 = (double)(var4.nextFloat() * 2.0F * (float)var20);
            } else {
               var8 = (double)var3.getX() + 0.5 + 0.25 * (double)var20;
               var14 = (double)(var4.nextFloat() * 2.0F * (float)var20);
            }

            var2.addParticle(ParticleTypes.PORTAL, var8, var10, var12, var14, var16, var18);
         }

      }
   }

   protected ItemStack getCloneItemStack(LevelReader var1, BlockPos var2, BlockState var3) {
      return ItemStack.EMPTY;
   }

   protected boolean canBeReplaced(BlockState var1, Fluid var2) {
      return false;
   }

   protected void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      if (var4.canUsePortal(false)) {
         BlockEntity var5 = var2.getBlockEntity(var3);
         if (!var2.isClientSide && var5 instanceof TheEndGatewayBlockEntity) {
            TheEndGatewayBlockEntity var6 = (TheEndGatewayBlockEntity)var5;
            if (!var6.isCoolingDown()) {
               var4.setAsInsidePortal(this, var3);
               TheEndGatewayBlockEntity.triggerCooldown(var2, var3, var1, var6);
            }
         }
      }

   }

   @Nullable
   public TeleportTransition getPortalDestination(ServerLevel var1, Entity var2, BlockPos var3) {
      BlockEntity var4 = var1.getBlockEntity(var3);
      if (var4 instanceof TheEndGatewayBlockEntity var5) {
         Vec3 var6 = var5.getPortalPosition(var1, var3);
         if (var6 == null) {
            return null;
         } else {
            return var2 instanceof ThrownEnderpearl ? new TeleportTransition(var1, var6, Vec3.ZERO, 0.0F, 0.0F, Set.of(), TeleportTransition.PLACE_PORTAL_TICKET) : new TeleportTransition(var1, var6, Vec3.ZERO, 0.0F, 0.0F, Relative.union(Relative.DELTA, Relative.ROTATION), TeleportTransition.PLACE_PORTAL_TICKET);
         }
      } else {
         return null;
      }
   }
}
