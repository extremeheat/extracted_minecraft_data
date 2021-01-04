package net.minecraft.world.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.player.Player;

public class SaddleItem extends Item {
   public SaddleItem(Item.Properties var1) {
      super(var1);
   }

   public boolean interactEnemy(ItemStack var1, Player var2, LivingEntity var3, InteractionHand var4) {
      if (var3 instanceof Pig) {
         Pig var5 = (Pig)var3;
         if (var5.isAlive() && !var5.hasSaddle() && !var5.isBaby()) {
            var5.setSaddle(true);
            var5.level.playSound(var2, var5.x, var5.y, var5.z, SoundEvents.PIG_SADDLE, SoundSource.NEUTRAL, 0.5F, 1.0F);
            var1.shrink(1);
         }

         return true;
      } else {
         return false;
      }
   }
}
