package net.minecraft.world.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class CorruptedPotatoPeelsItem extends Item {
   public CorruptedPotatoPeelsItem(Item.Properties var1) {
      super(var1);
   }

   @Override
   public InteractionResult useOn(UseOnContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      BlockState var4 = var2.getBlockState(var3);
      if (var4.is(Blocks.TERREDEPOMME) && var1.getClickedFace() == Direction.UP) {
         Player var5 = var1.getPlayer();
         var1.getItemInHand().shrink(1);
         var2.playSound(var5, var3, SoundEvents.CORRUPTED_PEELGRASS_BLOCK_PLACE, SoundSource.BLOCKS, 1.0F, var2.random.nextFloat(0.9F, 1.1F));
         var2.setBlockAndUpdate(var3, Blocks.CORRUPTED_PEELGRASS_BLOCK.defaultBlockState());
         if (var5 instanceof ServerPlayer var6 && var5.level().dimension().equals(Level.OVERWORLD)) {
            CriteriaTriggers.BRING_HOME_CORRUPTION.trigger((ServerPlayer)var6);
         }

         return InteractionResult.sidedSuccess(var2.isClientSide);
      } else {
         return super.useOn(var1);
      }
   }
}
