package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public class FallingBlock extends Block {
   public FallingBlock(Block.Properties var1) {
      super(var1);
   }

   public void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      var2.getBlockTicks().scheduleTick(var3, this, this.getTickDelay(var2));
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      var4.getBlockTicks().scheduleTick(var5, this, this.getTickDelay(var4));
      return super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   public void tick(BlockState var1, Level var2, BlockPos var3, Random var4) {
      if (!var2.isClientSide) {
         this.checkSlide(var2, var3);
      }

   }

   private void checkSlide(Level var1, BlockPos var2) {
      if (isFree(var1.getBlockState(var2.below())) && var2.getY() >= 0) {
         if (!var1.isClientSide) {
            FallingBlockEntity var3 = new FallingBlockEntity(var1, (double)var2.getX() + 0.5D, (double)var2.getY(), (double)var2.getZ() + 0.5D, var1.getBlockState(var2));
            this.falling(var3);
            var1.addFreshEntity(var3);
         }

      }
   }

   protected void falling(FallingBlockEntity var1) {
   }

   public int getTickDelay(LevelReader var1) {
      return 2;
   }

   public static boolean isFree(BlockState var0) {
      Block var1 = var0.getBlock();
      Material var2 = var0.getMaterial();
      return var0.isAir() || var1 == Blocks.FIRE || var2.isLiquid() || var2.isReplaceable();
   }

   public void onLand(Level var1, BlockPos var2, BlockState var3, BlockState var4) {
   }

   public void onBroken(Level var1, BlockPos var2) {
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, Random var4) {
      if (var4.nextInt(16) == 0) {
         BlockPos var5 = var3.below();
         if (isFree(var2.getBlockState(var5))) {
            double var6 = (double)((float)var3.getX() + var4.nextFloat());
            double var8 = (double)var3.getY() - 0.05D;
            double var10 = (double)((float)var3.getZ() + var4.nextFloat());
            var2.addParticle(new BlockParticleOption(ParticleTypes.FALLING_DUST, var1), var6, var8, var10, 0.0D, 0.0D, 0.0D);
         }
      }

   }

   public int getDustColor(BlockState var1) {
      return -16777216;
   }
}
