package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

public class EndGatewayBlock extends BaseEntityBlock {
   protected EndGatewayBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new TheEndGatewayBlockEntity(var1, var2);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      return createTickerHelper(var3, BlockEntityType.END_GATEWAY, var1.isClientSide ? TheEndGatewayBlockEntity::beamAnimationTick : TheEndGatewayBlockEntity::teleportTick);
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, Random var4) {
      BlockEntity var5 = var2.getBlockEntity(var3);
      if (var5 instanceof TheEndGatewayBlockEntity) {
         int var6 = ((TheEndGatewayBlockEntity)var5).getParticleAmount();

         for(int var7 = 0; var7 < var6; ++var7) {
            double var8 = (double)var3.getX() + var4.nextDouble();
            double var10 = (double)var3.getY() + var4.nextDouble();
            double var12 = (double)var3.getZ() + var4.nextDouble();
            double var14 = (var4.nextDouble() - 0.5D) * 0.5D;
            double var16 = (var4.nextDouble() - 0.5D) * 0.5D;
            double var18 = (var4.nextDouble() - 0.5D) * 0.5D;
            int var20 = var4.nextInt(2) * 2 - 1;
            if (var4.nextBoolean()) {
               var12 = (double)var3.getZ() + 0.5D + 0.25D * (double)var20;
               var18 = (double)(var4.nextFloat() * 2.0F * (float)var20);
            } else {
               var8 = (double)var3.getX() + 0.5D + 0.25D * (double)var20;
               var14 = (double)(var4.nextFloat() * 2.0F * (float)var20);
            }

            var2.addParticle(ParticleTypes.PORTAL, var8, var10, var12, var14, var16, var18);
         }

      }
   }

   public ItemStack getCloneItemStack(BlockGetter var1, BlockPos var2, BlockState var3) {
      return ItemStack.EMPTY;
   }

   public boolean canBeReplaced(BlockState var1, Fluid var2) {
      return false;
   }
}
