package net.minecraft.world.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.SnekComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class VenomousPotatoItem extends Item {
   private static final Component VISIBLE_NAME = Component.translatable("item.minecraft.snektato.revealed");

   public VenomousPotatoItem(Item.Properties var1) {
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
   public Component getName(ItemStack var1) {
      SnekComponent var2 = var1.get(DataComponents.SNEK);
      return var2 != null && var2.revealed() ? VISIBLE_NAME : super.getName(var1);
   }

   @Override
   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      var2.startUsingItem(var3);
      ItemStack var4 = var2.getItemInHand(var3);
      return InteractionResultHolder.sidedSuccess(var4, var1.isClientSide);
   }

   @Override
   public ItemStack finishUsingItem(ItemStack var1, Level var2, LivingEntity var3) {
      var2.playSound(
         null,
         var3.getX(),
         var3.getY(),
         var3.getZ(),
         SoundEvents.SPIDER_AMBIENT,
         SoundSource.PLAYERS,
         1.0F,
         1.0F + (var2.random.nextFloat() - var2.random.nextFloat()) * 0.4F
      );
      CaveSpider.poisonMethodThatSpidersUse(var3, null);
      var3.gameEvent(GameEvent.EAT);
      var3.hurt(var2.damageSources().magic(), 2.0F);
      var1.set(DataComponents.SNEK, new SnekComponent(true));
      return var1;
   }
}
