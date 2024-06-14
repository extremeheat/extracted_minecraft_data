package net.minecraft.world.item;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.gameevent.GameEvent;

public class SaddleItem extends Item {
   public SaddleItem(Item.Properties var1) {
      super(var1);
   }

   @Override
   public InteractionResult interactLivingEntity(ItemStack var1, Player var2, LivingEntity var3, InteractionHand var4) {
      if (var3 instanceof Saddleable var5 && var3.isAlive() && !var5.isSaddled() && var5.isSaddleable()) {
         if (!var2.level().isClientSide) {
            var5.equipSaddle(var1.split(1), SoundSource.NEUTRAL);
            var3.level().gameEvent(var3, GameEvent.EQUIP, var3.position());
         }

         return InteractionResult.sidedSuccess(var2.level().isClientSide);
      }

      return InteractionResult.PASS;
   }
}
