package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

public class PumpkinBlock extends Block {
   public static final MapCodec<PumpkinBlock> CODEC = simpleCodec(PumpkinBlock::new);

   @Override
   public MapCodec<PumpkinBlock> codec() {
      return CODEC;
   }

   protected PumpkinBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   @Override
   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      ItemStack var7 = var4.getItemInHand(var5);
      if (var7.is(Items.SHEARS)) {
         if (!var2.isClientSide) {
            Direction var8 = var6.getDirection();
            Direction var9 = var8.getAxis() == Direction.Axis.Y ? var4.getDirection().getOpposite() : var8;
            var2.playSound(null, var3, SoundEvents.PUMPKIN_CARVE, SoundSource.BLOCKS, 1.0F, 1.0F);
            var2.setBlock(var3, Blocks.CARVED_PUMPKIN.defaultBlockState().setValue(CarvedPumpkinBlock.FACING, var9), 11);
            ItemEntity var10 = new ItemEntity(
               var2,
               (double)var3.getX() + 0.5 + (double)var9.getStepX() * 0.65,
               (double)var3.getY() + 0.1,
               (double)var3.getZ() + 0.5 + (double)var9.getStepZ() * 0.65,
               new ItemStack(Items.PUMPKIN_SEEDS, 4)
            );
            var10.setDeltaMovement(
               0.05 * (double)var9.getStepX() + var2.random.nextDouble() * 0.02, 0.05, 0.05 * (double)var9.getStepZ() + var2.random.nextDouble() * 0.02
            );
            var2.addFreshEntity(var10);
            var7.hurtAndBreak(1, var4, var1x -> var1x.broadcastBreakEvent(var5));
            var2.gameEvent(var4, GameEvent.SHEAR, var3);
            var4.awardStat(Stats.ITEM_USED.get(Items.SHEARS));
         }

         return InteractionResult.sidedSuccess(var2.isClientSide);
      } else {
         return super.use(var1, var2, var3, var4, var5, var6);
      }
   }
}
