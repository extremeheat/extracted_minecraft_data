package net.minecraft.world.item;

import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownLingeringPotion;
import net.minecraft.world.level.Level;

public class LingeringPotionItem extends ThrowablePotionItem {
   public LingeringPotionItem(Item.Properties var1) {
      super(var1);
   }

   public InteractionResult use(Level var1, Player var2, InteractionHand var3) {
      var1.playSound((Entity)null, var2.getX(), var2.getY(), var2.getZ(), SoundEvents.LINGERING_POTION_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (var1.getRandom().nextFloat() * 0.4F + 0.8F));
      return super.use(var1, var2, var3);
   }

   protected AbstractThrownPotion createPotion(ServerLevel var1, LivingEntity var2, ItemStack var3) {
      return new ThrownLingeringPotion(var1, var2, var3);
   }

   protected AbstractThrownPotion createPotion(Level var1, Position var2, ItemStack var3) {
      return new ThrownLingeringPotion(var1, var2.x(), var2.y(), var2.z(), var3);
   }
}
