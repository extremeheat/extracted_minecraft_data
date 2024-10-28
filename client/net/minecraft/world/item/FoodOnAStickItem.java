package net.minecraft.world.item;

import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ItemSteerable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class FoodOnAStickItem<T extends Entity & ItemSteerable> extends Item {
   private final EntityType<T> canInteractWith;
   private final int consumeItemDamage;

   public FoodOnAStickItem(Item.Properties var1, EntityType<T> var2, int var3) {
      super(var1);
      this.canInteractWith = var2;
      this.consumeItemDamage = var3;
   }

   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      if (var1.isClientSide) {
         return InteractionResultHolder.pass(var4);
      } else {
         Entity var5 = var2.getControlledVehicle();
         if (var2.isPassenger() && var5 instanceof ItemSteerable) {
            ItemSteerable var6 = (ItemSteerable)var5;
            if (var5.getType() == this.canInteractWith && var6.boost()) {
               var4.hurtAndBreak(this.consumeItemDamage, var2, LivingEntity.getSlotForHand(var3));
               if (var4.isEmpty()) {
                  ItemStack var7 = var4.transmuteCopyIgnoreEmpty(Items.FISHING_ROD, 1);
                  return InteractionResultHolder.success(var7);
               }

               return InteractionResultHolder.success(var4);
            }
         }

         var2.awardStat(Stats.ITEM_USED.get(this));
         return InteractionResultHolder.pass(var4);
      }
   }
}
