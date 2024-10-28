package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
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

   public MapCodec<PumpkinBlock> codec() {
      return CODEC;
   }

   protected PumpkinBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   protected ItemInteractionResult useItemOn(ItemStack var1, BlockState var2, Level var3, BlockPos var4, Player var5, InteractionHand var6, BlockHitResult var7) {
      if (!var1.is(Items.SHEARS)) {
         return super.useItemOn(var1, var2, var3, var4, var5, var6, var7);
      } else if (var3.isClientSide) {
         return ItemInteractionResult.sidedSuccess(var3.isClientSide);
      } else {
         Direction var8 = var7.getDirection();
         Direction var9 = var8.getAxis() == Direction.Axis.Y ? var5.getDirection().getOpposite() : var8;
         var3.playSound((Player)null, (BlockPos)var4, SoundEvents.PUMPKIN_CARVE, SoundSource.BLOCKS, 1.0F, 1.0F);
         var3.setBlock(var4, (BlockState)Blocks.CARVED_PUMPKIN.defaultBlockState().setValue(CarvedPumpkinBlock.FACING, var9), 11);
         ItemEntity var10 = new ItemEntity(var3, (double)var4.getX() + 0.5 + (double)var9.getStepX() * 0.65, (double)var4.getY() + 0.1, (double)var4.getZ() + 0.5 + (double)var9.getStepZ() * 0.65, new ItemStack(Items.PUMPKIN_SEEDS, 4));
         var10.setDeltaMovement(0.05 * (double)var9.getStepX() + var3.random.nextDouble() * 0.02, 0.05, 0.05 * (double)var9.getStepZ() + var3.random.nextDouble() * 0.02);
         var3.addFreshEntity(var10);
         var1.hurtAndBreak(1, var5, LivingEntity.getSlotForHand(var6));
         var3.gameEvent(var5, GameEvent.SHEAR, var4);
         var5.awardStat(Stats.ITEM_USED.get(Items.SHEARS));
         return ItemInteractionResult.sidedSuccess(var3.isClientSide);
      }
   }
}
