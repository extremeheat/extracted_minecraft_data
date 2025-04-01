package net.minecraft.world.item;

import net.minecraft.core.component.DataComponents;
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

public class ShieldItem extends Item {
   public ShieldItem(Item.Properties var1) {
      super(var1);
   }

   public Component getName(ItemStack var1) {
      DyeColor var2 = (DyeColor)var1.get(DataComponents.BASE_COLOR);
      if (var2 != null) {
         String var10000 = this.descriptionId;
         return Component.translatable(var10000 + "." + var2.getName());
      } else {
         return super.getName(var1);
      }
   }

   public InteractionResult use(Level var1, Player var2, InteractionHand var3) {
      if (var1 instanceof ServerLevel && var2 instanceof ServerPlayer) {
         ServerPlayer var4 = (ServerPlayer)var2;
         if (var2.isActive(PlayerUnlocks.FIRE_SHIELD)) {
            var4.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 100));
         }
      }

      return InteractionResult.SUCCESS;
   }
}
