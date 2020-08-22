package net.minecraft.world.item;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;

public class PotionItem extends Item {
   public PotionItem(Item.Properties var1) {
      super(var1);
   }

   public ItemStack getDefaultInstance() {
      return PotionUtils.setPotion(super.getDefaultInstance(), Potions.WATER);
   }

   public ItemStack finishUsingItem(ItemStack var1, Level var2, LivingEntity var3) {
      Player var4 = var3 instanceof Player ? (Player)var3 : null;
      if (var4 instanceof ServerPlayer) {
         CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)var4, var1);
      }

      if (!var2.isClientSide) {
         List var5 = PotionUtils.getMobEffects(var1);
         Iterator var6 = var5.iterator();

         while(var6.hasNext()) {
            MobEffectInstance var7 = (MobEffectInstance)var6.next();
            if (var7.getEffect().isInstantenous()) {
               var7.getEffect().applyInstantenousEffect(var4, var4, var3, var7.getAmplifier(), 1.0D);
            } else {
               var3.addEffect(new MobEffectInstance(var7));
            }
         }
      }

      if (var4 != null) {
         var4.awardStat(Stats.ITEM_USED.get(this));
         if (!var4.abilities.instabuild) {
            var1.shrink(1);
         }
      }

      if (var4 == null || !var4.abilities.instabuild) {
         if (var1.isEmpty()) {
            return new ItemStack(Items.GLASS_BOTTLE);
         }

         if (var4 != null) {
            var4.inventory.add(new ItemStack(Items.GLASS_BOTTLE));
         }
      }

      return var1;
   }

   public int getUseDuration(ItemStack var1) {
      return 32;
   }

   public UseAnim getUseAnimation(ItemStack var1) {
      return UseAnim.DRINK;
   }

   public InteractionResultHolder use(Level var1, Player var2, InteractionHand var3) {
      var2.startUsingItem(var3);
      return InteractionResultHolder.success(var2.getItemInHand(var3));
   }

   public String getDescriptionId(ItemStack var1) {
      return PotionUtils.getPotion(var1).getName(this.getDescriptionId() + ".effect.");
   }

   public void appendHoverText(ItemStack var1, @Nullable Level var2, List var3, TooltipFlag var4) {
      PotionUtils.addPotionTooltip(var1, var3, 1.0F);
   }

   public boolean isFoil(ItemStack var1) {
      return super.isFoil(var1) || !PotionUtils.getMobEffects(var1).isEmpty();
   }

   public void fillItemCategory(CreativeModeTab var1, NonNullList var2) {
      if (this.allowdedIn(var1)) {
         Iterator var3 = Registry.POTION.iterator();

         while(var3.hasNext()) {
            Potion var4 = (Potion)var3.next();
            if (var4 != Potions.EMPTY) {
               var2.add(PotionUtils.setPotion(new ItemStack(this), var4));
            }
         }
      }

   }
}
