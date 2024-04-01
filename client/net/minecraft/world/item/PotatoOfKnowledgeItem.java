package net.minecraft.world.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.XpComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class PotatoOfKnowledgeItem extends Item {
   public PotatoOfKnowledgeItem(Item.Properties var1) {
      super(var1);
   }

   @Override
   public int getUseDuration(ItemStack var1) {
      return 20;
   }

   @Override
   public UseAnim getUseAnimation(ItemStack var1) {
      return UseAnim.EAT;
   }

   @Override
   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      var2.startUsingItem(var3);
      ItemStack var4 = var2.getItemInHand(var3);
      return InteractionResultHolder.sidedSuccess(var4, var1.isClientSide);
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public ItemStack finishUsingItem(ItemStack var1, Level var2, LivingEntity var3) {
      if (var3 instanceof ServerPlayer var4) {
         int var5 = var1.getOrDefault(DataComponents.XP, XpComponent.DEFAULT).value();
         var4.giveExperiencePoints(var5);
      }

      var2.playSound(
         null,
         var3.getX(),
         var3.getY(),
         var3.getZ(),
         SoundEvents.PLAYER_BURP,
         SoundSource.PLAYERS,
         1.0F,
         1.0F + (var2.random.nextFloat() - var2.random.nextFloat()) * 0.4F
      );
      var1.consume(1, var3);
      var3.gameEvent(GameEvent.EAT);
      return var1;
   }
}
