package net.minecraft.world.level.block;

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

public class PumpkinBlock extends StemGrownBlock {
   protected PumpkinBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      ItemStack var7 = var4.getItemInHand(var5);
      if (var7.method_87(Items.SHEARS)) {
         if (!var2.isClientSide) {
            Direction var8 = var6.getDirection();
            Direction var9 = var8.getAxis() == Direction.Axis.field_501 ? var4.getDirection().getOpposite() : var8;
            var2.playSound((Player)null, (BlockPos)var3, SoundEvents.PUMPKIN_CARVE, SoundSource.BLOCKS, 1.0F, 1.0F);
            var2.setBlock(var3, (BlockState)Blocks.CARVED_PUMPKIN.defaultBlockState().setValue(CarvedPumpkinBlock.FACING, var9), 11);
            ItemEntity var10 = new ItemEntity(var2, (double)var3.getX() + 0.5D + (double)var9.getStepX() * 0.65D, (double)var3.getY() + 0.1D, (double)var3.getZ() + 0.5D + (double)var9.getStepZ() * 0.65D, new ItemStack(Items.PUMPKIN_SEEDS, 4));
            var10.setDeltaMovement(0.05D * (double)var9.getStepX() + var2.random.nextDouble() * 0.02D, 0.05D, 0.05D * (double)var9.getStepZ() + var2.random.nextDouble() * 0.02D);
            var2.addFreshEntity(var10);
            var7.hurtAndBreak(1, var4, (var1x) -> {
               var1x.broadcastBreakEvent(var5);
            });
            var2.gameEvent(var4, GameEvent.SHEAR, var3);
            var4.awardStat(Stats.ITEM_USED.get(Items.SHEARS));
         }

         return InteractionResult.sidedSuccess(var2.isClientSide);
      } else {
         return super.use(var1, var2, var3, var4, var5, var6);
      }
   }

   public StemBlock getStem() {
      return (StemBlock)Blocks.PUMPKIN_STEM;
   }

   public AttachedStemBlock getAttachedStem() {
      return (AttachedStemBlock)Blocks.ATTACHED_PUMPKIN_STEM;
   }
}
