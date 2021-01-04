package net.minecraft.world.item;

import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class CarrotOnAStickItem extends Item {
   public CarrotOnAStickItem(Item.Properties var1) {
      super(var1);
   }

   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      if (var1.isClientSide) {
         return new InteractionResultHolder(InteractionResult.PASS, var4);
      } else {
         if (var2.isPassenger() && var2.getVehicle() instanceof Pig) {
            Pig var5 = (Pig)var2.getVehicle();
            if (var4.getMaxDamage() - var4.getDamageValue() >= 7 && var5.boost()) {
               var4.hurtAndBreak(7, var2, (var1x) -> {
                  var1x.broadcastBreakEvent(var3);
               });
               if (var4.isEmpty()) {
                  ItemStack var6 = new ItemStack(Items.FISHING_ROD);
                  var6.setTag(var4.getTag());
                  return new InteractionResultHolder(InteractionResult.SUCCESS, var6);
               }

               return new InteractionResultHolder(InteractionResult.SUCCESS, var4);
            }
         }

         var2.awardStat(Stats.ITEM_USED.get(this));
         return new InteractionResultHolder(InteractionResult.PASS, var4);
      }
   }
}
