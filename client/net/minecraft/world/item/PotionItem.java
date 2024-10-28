package net.minecraft.world.item;

import java.util.List;
import java.util.Objects;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class PotionItem extends Item {
   private static final int DRINK_DURATION = 32;

   public PotionItem(Item.Properties var1) {
      super(var1);
   }

   public ItemStack getDefaultInstance() {
      ItemStack var1 = super.getDefaultInstance();
      var1.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.WATER));
      return var1;
   }

   public ItemStack finishUsingItem(ItemStack var1, Level var2, LivingEntity var3) {
      Player var4 = var3 instanceof Player ? (Player)var3 : null;
      if (var4 instanceof ServerPlayer) {
         CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)var4, var1);
      }

      if (!var2.isClientSide) {
         PotionContents var5 = (PotionContents)var1.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
         var5.forEachEffect((var2x) -> {
            if (((MobEffect)var2x.getEffect().value()).isInstantenous()) {
               ((MobEffect)var2x.getEffect().value()).applyInstantenousEffect(var4, var4, var3, var2x.getAmplifier(), 1.0);
            } else {
               var3.addEffect(var2x);
            }

         });
      }

      if (var4 != null) {
         var4.awardStat(Stats.ITEM_USED.get(this));
         var1.consume(1, var4);
      }

      if (var4 == null || !var4.hasInfiniteMaterials()) {
         if (var1.isEmpty()) {
            return new ItemStack(Items.GLASS_BOTTLE);
         }

         if (var4 != null) {
            var4.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));
         }
      }

      var3.gameEvent(GameEvent.DRINK);
      return var1;
   }

   public InteractionResult useOn(UseOnContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      Player var4 = var1.getPlayer();
      ItemStack var5 = var1.getItemInHand();
      PotionContents var6 = (PotionContents)var5.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
      BlockState var7 = var2.getBlockState(var3);
      if (var1.getClickedFace() != Direction.DOWN && var7.is(BlockTags.CONVERTABLE_TO_MUD) && var6.is(Potions.WATER)) {
         var2.playSound((Player)null, (BlockPos)var3, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 1.0F, 1.0F);
         var4.setItemInHand(var1.getHand(), ItemUtils.createFilledResult(var5, var4, new ItemStack(Items.GLASS_BOTTLE)));
         var4.awardStat(Stats.ITEM_USED.get(var5.getItem()));
         if (!var2.isClientSide) {
            ServerLevel var8 = (ServerLevel)var2;

            for(int var9 = 0; var9 < 5; ++var9) {
               var8.sendParticles(ParticleTypes.SPLASH, (double)var3.getX() + var2.random.nextDouble(), (double)(var3.getY() + 1), (double)var3.getZ() + var2.random.nextDouble(), 1, 0.0, 0.0, 0.0, 1.0);
            }
         }

         var2.playSound((Player)null, (BlockPos)var3, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
         var2.gameEvent((Entity)null, GameEvent.FLUID_PLACE, var3);
         var2.setBlockAndUpdate(var3, Blocks.MUD.defaultBlockState());
         return InteractionResult.sidedSuccess(var2.isClientSide);
      } else {
         return InteractionResult.PASS;
      }
   }

   public int getUseDuration(ItemStack var1, LivingEntity var2) {
      return 32;
   }

   public UseAnim getUseAnimation(ItemStack var1) {
      return UseAnim.DRINK;
   }

   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      return ItemUtils.startUsingInstantly(var1, var2, var3);
   }

   public String getDescriptionId(ItemStack var1) {
      return Potion.getName(((PotionContents)var1.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)).potion(), this.getDescriptionId() + ".effect.");
   }

   public void appendHoverText(ItemStack var1, Item.TooltipContext var2, List<Component> var3, TooltipFlag var4) {
      PotionContents var5 = (PotionContents)var1.get(DataComponents.POTION_CONTENTS);
      if (var5 != null) {
         Objects.requireNonNull(var3);
         var5.addPotionTooltip(var3::add, 1.0F, var2.tickRate());
      }
   }
}
