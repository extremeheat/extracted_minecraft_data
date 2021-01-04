package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class EndGatewayBlock extends BaseEntityBlock {
   protected EndGatewayBlock(Block.Properties var1) {
      super(var1);
   }

   public BlockEntity newBlockEntity(BlockGetter var1) {
      return new TheEndGatewayBlockEntity();
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, Random var4) {
      BlockEntity var5 = var2.getBlockEntity(var3);
      if (var5 instanceof TheEndGatewayBlockEntity) {
         int var6 = ((TheEndGatewayBlockEntity)var5).getParticleAmount();

         for(int var7 = 0; var7 < var6; ++var7) {
            double var8 = (double)((float)var3.getX() + var4.nextFloat());
            double var10 = (double)((float)var3.getY() + var4.nextFloat());
            double var12 = (double)((float)var3.getZ() + var4.nextFloat());
            double var14 = ((double)var4.nextFloat() - 0.5D) * 0.5D;
            double var16 = ((double)var4.nextFloat() - 0.5D) * 0.5D;
            double var18 = ((double)var4.nextFloat() - 0.5D) * 0.5D;
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
}
