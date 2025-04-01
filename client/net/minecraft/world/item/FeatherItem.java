package net.minecraft.world.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerUnlocks;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class FeatherItem extends Item {
   public FeatherItem(Item.Properties var1) {
      super(var1);
   }

   public Component getName(ItemStack var1) {
      return super.getName(var1);
   }

   public InteractionResult use(Level var1, Player var2, InteractionHand var3) {
      if (var1 instanceof ServerLevel && var2 instanceof ServerPlayer) {
         ServerPlayer var4 = (ServerPlayer)var2;
         if (var4.isUnlocked(PlayerUnlocks.WING_GUARDIAN)) {
            boolean var5 = true;
            var4.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 100));
            var4.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 200));
         }
      }

      return InteractionResult.SUCCESS;
   }
}
