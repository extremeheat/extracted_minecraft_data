package net.minecraft.world.entity.ai.goal;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

public class UseItemGoal extends Goal {
   private final Mob mob;
   private final ItemStack item;
   private final Predicate canUseSelector;
   private final SoundEvent finishUsingSound;

   public UseItemGoal(Mob var1, ItemStack var2, @Nullable SoundEvent var3, Predicate var4) {
      this.mob = var1;
      this.item = var2;
      this.finishUsingSound = var3;
      this.canUseSelector = var4;
   }

   public boolean canUse() {
      return this.canUseSelector.test(this.mob);
   }

   public boolean canContinueToUse() {
      return this.mob.isUsingItem();
   }

   public void start() {
      this.mob.setItemSlot(EquipmentSlot.MAINHAND, this.item.copy());
      this.mob.startUsingItem(InteractionHand.MAIN_HAND);
   }

   public void stop() {
      this.mob.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
      if (this.finishUsingSound != null) {
         this.mob.playSound(this.finishUsingSound, 1.0F, this.mob.getRandom().nextFloat() * 0.2F + 0.9F);
      }

   }
}
