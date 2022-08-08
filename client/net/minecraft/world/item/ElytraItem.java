package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public class ElytraItem extends Item implements Wearable {
   public ElytraItem(Item.Properties var1) {
      super(var1);
      DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
   }

   public static boolean isFlyEnabled(ItemStack var0) {
      return var0.getDamageValue() < var0.getMaxDamage() - 1;
   }

   public boolean isValidRepairItem(ItemStack var1, ItemStack var2) {
      return var2.is(Items.PHANTOM_MEMBRANE);
   }

   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      EquipmentSlot var5 = Mob.getEquipmentSlotForItem(var4);
      ItemStack var6 = var2.getItemBySlot(var5);
      if (var6.isEmpty()) {
         var2.setItemSlot(var5, var4.copy());
         if (!var1.isClientSide()) {
            var2.awardStat(Stats.ITEM_USED.get(this));
         }

         var4.setCount(0);
         return InteractionResultHolder.sidedSuccess(var4, var1.isClientSide());
      } else {
         return InteractionResultHolder.fail(var4);
      }
   }

   @Nullable
   public SoundEvent getEquipSound() {
      return SoundEvents.ARMOR_EQUIP_ELYTRA;
   }
}
