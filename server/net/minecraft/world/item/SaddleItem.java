package net.minecraft.world.item;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.player.Player;

public class SaddleItem extends Item {
   public SaddleItem(Item.Properties var1) {
      super(var1);
   }

   public InteractionResult interactLivingEntity(ItemStack var1, Player var2, LivingEntity var3, InteractionHand var4) {
      if (var3 instanceof Saddleable && var3.isAlive()) {
         Saddleable var5 = (Saddleable)var3;
         if (!var5.isSaddled() && var5.isSaddleable()) {
            if (!var2.level.isClientSide) {
               var5.equipSaddle(SoundSource.NEUTRAL);
               var1.shrink(1);
            }

            return InteractionResult.sidedSuccess(var2.level.isClientSide);
         }
      }

      return InteractionResult.PASS;
   }
}
