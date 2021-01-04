package net.minecraft.world.item;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class BottleItem extends Item {
   public BottleItem(Item.Properties var1) {
      super(var1);
   }

   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      List var4 = var1.getEntitiesOfClass(AreaEffectCloud.class, var2.getBoundingBox().inflate(2.0D), (var0) -> {
         return var0 != null && var0.isAlive() && var0.getOwner() instanceof EnderDragon;
      });
      ItemStack var5 = var2.getItemInHand(var3);
      if (!var4.isEmpty()) {
         AreaEffectCloud var8 = (AreaEffectCloud)var4.get(0);
         var8.setRadius(var8.getRadius() - 0.5F);
         var1.playSound((Player)null, var2.x, var2.y, var2.z, SoundEvents.BOTTLE_FILL_DRAGONBREATH, SoundSource.NEUTRAL, 1.0F, 1.0F);
         return new InteractionResultHolder(InteractionResult.SUCCESS, this.turnBottleIntoItem(var5, var2, new ItemStack(Items.DRAGON_BREATH)));
      } else {
         HitResult var6 = getPlayerPOVHitResult(var1, var2, ClipContext.Fluid.SOURCE_ONLY);
         if (var6.getType() == HitResult.Type.MISS) {
            return new InteractionResultHolder(InteractionResult.PASS, var5);
         } else {
            if (var6.getType() == HitResult.Type.BLOCK) {
               BlockPos var7 = ((BlockHitResult)var6).getBlockPos();
               if (!var1.mayInteract(var2, var7)) {
                  return new InteractionResultHolder(InteractionResult.PASS, var5);
               }

               if (var1.getFluidState(var7).is(FluidTags.WATER)) {
                  var1.playSound(var2, var2.x, var2.y, var2.z, SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
                  return new InteractionResultHolder(InteractionResult.SUCCESS, this.turnBottleIntoItem(var5, var2, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER)));
               }
            }

            return new InteractionResultHolder(InteractionResult.PASS, var5);
         }
      }
   }

   protected ItemStack turnBottleIntoItem(ItemStack var1, Player var2, ItemStack var3) {
      var1.shrink(1);
      var2.awardStat(Stats.ITEM_USED.get(this));
      if (var1.isEmpty()) {
         return var3;
      } else {
         if (!var2.inventory.add(var3)) {
            var2.drop(var3, false);
         }

         return var1;
      }
   }
}
