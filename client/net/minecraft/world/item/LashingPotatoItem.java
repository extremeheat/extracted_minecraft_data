package net.minecraft.world.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LashingPotatoHook;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class LashingPotatoItem extends Item {
   public LashingPotatoItem(Item.Properties var1) {
      super(var1);
   }

   @Override
   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      LashingPotatoHook var5 = var2.grappling;
      if (var5 != null) {
         retrieve(var1, var2, var5);
      } else {
         if (!var1.isClientSide) {
            var4.hurtAndBreak(1, var2, LivingEntity.getSlotForHand(var3));
         }

         this.shoot(var1, var2);
      }

      return InteractionResultHolder.sidedSuccess(var4, var1.isClientSide);
   }

   private void shoot(Level var1, Player var2) {
      if (!var1.isClientSide) {
         var1.addFreshEntity(new LashingPotatoHook(var1, var2));
      }

      var2.awardStat(Stats.ITEM_USED.get(this));
      var1.playSound(
         null,
         var2.getX(),
         var2.getY(),
         var2.getZ(),
         SoundEvents.FISHING_BOBBER_THROW,
         SoundSource.NEUTRAL,
         0.5F,
         0.4F / (var1.getRandom().nextFloat() * 0.4F + 0.8F)
      );
      var2.gameEvent(GameEvent.ITEM_INTERACT_START);
   }

   private static void retrieve(Level var0, Player var1, LashingPotatoHook var2) {
      if (!var0.isClientSide()) {
         var2.discard();
         var1.grappling = null;
      }

      var0.playSound(
         null,
         var1.getX(),
         var1.getY(),
         var1.getZ(),
         SoundEvents.FISHING_BOBBER_RETRIEVE,
         SoundSource.NEUTRAL,
         1.0F,
         0.4F / (var0.getRandom().nextFloat() * 0.4F + 0.8F)
      );
      var1.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
   }
}
