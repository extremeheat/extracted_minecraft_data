package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.EyeOfPotato;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class PotatoEyeItem extends Item {
   public PotatoEyeItem(Item.Properties var1) {
      super(var1);
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      if (!(var1 instanceof ServerLevel)) {
         return InteractionResultHolder.consume(var4);
      } else {
         ServerLevel var5 = (ServerLevel)var1;
         if (var2.isChapterAndProgressPast("crafted_eyes", 2)) {
            var2.setPotatoQuestChapter("thrown_eye");
         }

         if (var2.isChapterAndProgressPast("potato_village", 7)) {
            var2.setPotatoQuestChapter("thrown_eye_part_two");
         }

         BlockPos var6;
         if (!var5.dimension().equals(Level.OVERWORLD) && !var5.isPotato()) {
            var6 = null;
         } else {
            var6 = var5.findNearestMapStructure(var5.isPotato() ? StructureTags.COLOSSEUM : StructureTags.RUINED_PORTATOL, var2.blockPosition(), 100, false);
         }

         if (var6 != null) {
            if (var2 instanceof ServerPlayer var7) {
               if (var1.isPotato()) {
                  var7.setColosseum(var6);
               } else {
                  var7.setRuinedPortatol(var6);
               }
            }

            EyeOfPotato var8 = new EyeOfPotato(var1, var2.getX(), var2.getY(0.5), var2.getZ());
            var8.setItem(var4);
            var8.signalTo(var6);
            var1.gameEvent(GameEvent.PROJECTILE_SHOOT, var8.position(), GameEvent.Context.of(var2));
            var1.addFreshEntity(var8);
            var1.playSound(
               null,
               var2.getX(),
               var2.getY(),
               var2.getZ(),
               SoundEvents.ENDER_EYE_LAUNCH,
               SoundSource.NEUTRAL,
               0.5F,
               0.4F / (var1.getRandom().nextFloat() * 0.4F + 0.8F)
            );
            var1.levelEvent(null, 1003, var2.blockPosition(), 0);
            var4.consume(1, var2);
            var2.awardStat(Stats.ITEM_USED.get(this));
            var2.swing(var3, true);
         } else {
            var1.playSound(
               null,
               var2.getX(),
               var2.getY(),
               var2.getZ(),
               SoundEvents.ENDER_EYE_LAUNCH,
               SoundSource.NEUTRAL,
               0.5F,
               0.4F / (var1.getRandom().nextFloat() * 0.4F + 0.8F)
            );
            var4.consume(1, var2);
            var2.drop(new ItemStack(Items.POTATO_EYE), true);
         }

         return InteractionResultHolder.success(var4);
      }
   }
}
