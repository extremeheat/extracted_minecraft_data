package net.minecraft.world.item;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.level.Level;

public class EggItem extends Item implements ProjectileItem {
   public EggItem(Item.Properties var1) {
      super(var1);
   }

   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      var1.playSound((Player)null, var2.getX(), var2.getY(), var2.getZ(), (SoundEvent)SoundEvents.EGG_THROW, SoundSource.PLAYERS, 0.5F, 0.4F / (var1.getRandom().nextFloat() * 0.4F + 0.8F));
      if (!var1.isClientSide) {
         ThrownEgg var5 = new ThrownEgg(var1, var2);
         var5.setItem(var4);
         var5.shootFromRotation(var2, var2.getXRot(), var2.getYRot(), 0.0F, 1.5F, 1.0F);
         var1.addFreshEntity(var5);
      }

      var2.awardStat(Stats.ITEM_USED.get(this));
      var4.consume(1, var2);
      return InteractionResultHolder.sidedSuccess(var4, var1.isClientSide());
   }

   public Projectile asProjectile(Level var1, Position var2, ItemStack var3, Direction var4) {
      ThrownEgg var5 = new ThrownEgg(var1, var2.x(), var2.y(), var2.z());
      var5.setItem(var3);
      return var5;
   }
}
