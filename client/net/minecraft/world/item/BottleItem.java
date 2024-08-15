package net.minecraft.world.item;

import java.util.List;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class BottleItem extends Item {
   public BottleItem(Item.Properties var1) {
      super(var1);
   }

   @Override
   public InteractionResult use(Level var1, Player var2, InteractionHand var3) {
      List var4 = var1.getEntitiesOfClass(
         AreaEffectCloud.class, var2.getBoundingBox().inflate(2.0), var0 -> var0 != null && var0.isAlive() && var0.getOwner() instanceof EnderDragon
      );
      ItemStack var5 = var2.getItemInHand(var3);
      if (!var4.isEmpty()) {
         AreaEffectCloud var8 = (AreaEffectCloud)var4.get(0);
         var8.setRadius(var8.getRadius() - 0.5F);
         var1.playSound(null, var2.getX(), var2.getY(), var2.getZ(), SoundEvents.BOTTLE_FILL_DRAGONBREATH, SoundSource.NEUTRAL, 1.0F, 1.0F);
         var1.gameEvent(var2, GameEvent.FLUID_PICKUP, var2.position());
         if (var2 instanceof ServerPlayer var9) {
            CriteriaTriggers.PLAYER_INTERACTED_WITH_ENTITY.trigger(var9, var5, var8);
         }

         return InteractionResult.SUCCESS.heldItemTransformedTo(this.turnBottleIntoItem(var5, var2, new ItemStack(Items.DRAGON_BREATH)));
      } else {
         BlockHitResult var6 = getPlayerPOVHitResult(var1, var2, ClipContext.Fluid.SOURCE_ONLY);
         if (var6.getType() == HitResult.Type.MISS) {
            return InteractionResult.PASS;
         } else {
            if (var6.getType() == HitResult.Type.BLOCK) {
               BlockPos var7 = var6.getBlockPos();
               if (!var1.mayInteract(var2, var7)) {
                  return InteractionResult.PASS;
               }

               if (var1.getFluidState(var7).is(FluidTags.WATER)) {
                  var1.playSound(var2, var2.getX(), var2.getY(), var2.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
                  var1.gameEvent(var2, GameEvent.FLUID_PICKUP, var7);
                  return InteractionResult.SUCCESS
                     .heldItemTransformedTo(this.turnBottleIntoItem(var5, var2, PotionContents.createItemStack(Items.POTION, Potions.WATER)));
               }
            }

            return InteractionResult.PASS;
         }
      }
   }

   protected ItemStack turnBottleIntoItem(ItemStack var1, Player var2, ItemStack var3) {
      var2.awardStat(Stats.ITEM_USED.get(this));
      return ItemUtils.createFilledResult(var1, var2, var3);
   }
}
