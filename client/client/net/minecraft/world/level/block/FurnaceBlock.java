package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class FurnaceBlock extends AbstractFurnaceBlock {
   public static final MapCodec<FurnaceBlock> CODEC = simpleCodec(FurnaceBlock::new);

   @Override
   public MapCodec<FurnaceBlock> codec() {
      return CODEC;
   }

   protected FurnaceBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   @Override
   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new FurnaceBlockEntity(var1, var2);
   }

   @Nullable
   @Override
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      return createFurnaceTicker(var1, var3, BlockEntityType.FURNACE);
   }

   @Override
   protected void openContainer(Level var1, BlockPos var2, Player var3) {
      BlockEntity var4 = var1.getBlockEntity(var2);
      if (var4 instanceof FurnaceBlockEntity) {
         var3.openMenu((MenuProvider)var4);
         var3.awardStat(Stats.INTERACT_WITH_FURNACE);
      }
   }

   @Override
   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      if (var1.getValue(LIT)) {
         double var5 = (double)var3.getX() + 0.5;
         double var7 = (double)var3.getY();
         double var9 = (double)var3.getZ() + 0.5;
         if (var4.nextDouble() < 0.1) {
            var2.playLocalSound(var5, var7, var9, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
         }

         Direction var11 = var1.getValue(FACING);
         Direction.Axis var12 = var11.getAxis();
         double var13 = 0.52;
         double var15 = var4.nextDouble() * 0.6 - 0.3;
         double var17 = var12 == Direction.Axis.X ? (double)var11.getStepX() * 0.52 : var15;
         double var19 = var4.nextDouble() * 6.0 / 16.0;
         double var21 = var12 == Direction.Axis.Z ? (double)var11.getStepZ() * 0.52 : var15;
         var2.addParticle(ParticleTypes.SMOKE, var5 + var17, var7 + var19, var9 + var21, 0.0, 0.0, 0.0);
         var2.addParticle(ParticleTypes.FLAME, var5 + var17, var7 + var19, var9 + var21, 0.0, 0.0, 0.0);
      }
   }
}
