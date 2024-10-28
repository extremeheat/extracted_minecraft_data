package net.minecraft.world.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

public class NameTagItem extends Item {
   public NameTagItem(Item.Properties var1) {
      super(var1);
   }

   public InteractionResult interactLivingEntity(ItemStack var1, Player var2, LivingEntity var3, InteractionHand var4) {
      Component var5 = (Component)var1.get(DataComponents.CUSTOM_NAME);
      if (var5 != null && !(var3 instanceof Player)) {
         if (!var2.level().isClientSide && var3.isAlive()) {
            var3.setCustomName(var5);
            if (var3 instanceof Mob) {
               Mob var6 = (Mob)var3;
               var6.setPersistenceRequired();
            }

            var1.shrink(1);
         }

         return InteractionResult.sidedSuccess(var2.level().isClientSide);
      } else {
         return InteractionResult.PASS;
      }
   }
}
