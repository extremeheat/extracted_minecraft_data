package net.minecraft.world.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerUnlocks;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.StructureTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class ExitEyeItem extends Item {
   public ExitEyeItem(Item.Properties var1) {
      super(var1);
   }

   public int getUseDuration(ItemStack var1, LivingEntity var2) {
      return 0;
   }

   public InteractionResult use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      var2.startUsingItem(var3);
      if (var1 instanceof ServerLevel var5) {
         if (var2 instanceof ServerPlayer var6) {
            if (var6.isRevisiting() && var5.isMine()) {
               var5.respawnPlayerIntoHub(var6, (var0) -> var0.getInventory().clearContent());
               return InteractionResult.SUCCESS_SERVER;
            }
         }

         if (!var2.isUnlocked(PlayerUnlocks.PATHFINDER)) {
            var2.hurtServer(var5, var2.damageSources().enderPearl(), 2.0F);
         }

         BlockPos var9 = var5.findNearestMapStructure(StructureTags.EYE_OF_ENDER_LOCATED, var2.blockPosition(), 100, false);
         if (var9 == null) {
            return InteractionResult.CONSUME;
         }

         EyeOfEnder var7 = new EyeOfEnder(var1, var2.getX(), var2.getY(0.5), var2.getZ());
         var7.setItem(var4);
         var7.signalTo(var9);
         var1.gameEvent(GameEvent.PROJECTILE_SHOOT, var7.position(), GameEvent.Context.of((Entity)var2));
         var1.addFreshEntity(var7);
         if (var2 instanceof ServerPlayer var8) {
            CriteriaTriggers.USED_EXIT_EYE.trigger(var8, var9);
         }

         float var10 = Mth.lerp(var1.random.nextFloat(), 0.33F, 0.5F);
         var1.playSound((Entity)null, var2.getX(), var2.getY(), var2.getZ(), SoundEvents.ENDER_EYE_LAUNCH, SoundSource.NEUTRAL, 1.0F, var10);
         var4.consume(1, var2);
         var2.awardStat(Stats.ITEM_USED.get(this));
      }

      return InteractionResult.SUCCESS_SERVER;
   }
}
