package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class BlastFurnaceBlock extends AbstractFurnaceBlock {
   protected BlastFurnaceBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new BlastFurnaceBlockEntity(var1, var2);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      return createFurnaceTicker(var1, var3, BlockEntityType.BLAST_FURNACE);
   }

   protected void openContainer(Level var1, BlockPos var2, Player var3) {
      BlockEntity var4 = var1.getBlockEntity(var2);
      if (var4 instanceof BlastFurnaceBlockEntity) {
         var3.openMenu((MenuProvider)var4);
         var3.awardStat(Stats.INTERACT_WITH_BLAST_FURNACE);
      }

   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, Random var4) {
      if ((Boolean)var1.getValue(LIT)) {
         double var5 = (double)var3.getX() + 0.5D;
         double var7 = (double)var3.getY();
         double var9 = (double)var3.getZ() + 0.5D;
         if (var4.nextDouble() < 0.1D) {
            var2.playLocalSound(var5, var7, var9, SoundEvents.BLASTFURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
         }

         Direction var11 = (Direction)var1.getValue(FACING);
         Direction.Axis var12 = var11.getAxis();
         double var13 = 0.52D;
         double var15 = var4.nextDouble() * 0.6D - 0.3D;
         double var17 = var12 == Direction.Axis.X ? (double)var11.getStepX() * 0.52D : var15;
         double var19 = var4.nextDouble() * 9.0D / 16.0D;
         double var21 = var12 == Direction.Axis.Z ? (double)var11.getStepZ() * 0.52D : var15;
         var2.addParticle(ParticleTypes.SMOKE, var5 + var17, var7 + var19, var9 + var21, 0.0D, 0.0D, 0.0D);
      }
   }
}
