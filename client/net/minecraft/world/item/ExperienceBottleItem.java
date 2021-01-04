package net.minecraft.world.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import net.minecraft.world.level.Level;

public class ExperienceBottleItem extends Item {
   public ExperienceBottleItem(Item.Properties var1) {
      super(var1);
   }

   public boolean isFoil(ItemStack var1) {
      return true;
   }

   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      if (!var2.abilities.instabuild) {
         var4.shrink(1);
      }

      var1.playSound((Player)null, var2.x, var2.y, var2.z, SoundEvents.EXPERIENCE_BOTTLE_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
      if (!var1.isClientSide) {
         ThrownExperienceBottle var5 = new ThrownExperienceBottle(var1, var2);
         var5.setItem(var4);
         var5.shootFromRotation(var2, var2.xRot, var2.yRot, -20.0F, 0.7F, 1.0F);
         var1.addFreshEntity(var5);
      }

      var2.awardStat(Stats.ITEM_USED.get(this));
      return new InteractionResultHolder(InteractionResult.SUCCESS, var4);
   }
}
