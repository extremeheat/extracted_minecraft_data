package net.minecraft.world.item;

import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.InstrumentComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.mines.WorldEffects;

public class InstrumentItem extends Item {
   public InstrumentItem(Item.Properties var1) {
      super(var1);
   }

   public static ItemStack create(Item var0, Holder<Instrument> var1) {
      ItemStack var2 = new ItemStack(var0);
      var2.set(DataComponents.INSTRUMENT, new InstrumentComponent(var1));
      return var2;
   }

   public InteractionResult use(Level var1, Player var2, InteractionHand var3) {
      if (var1 instanceof ServerLevel var4) {
         if (var4.isActive(WorldEffects.WARDEN_BOSS_FIGHT)) {
            TargetingConditions var5 = TargetingConditions.forNonCombat();
            Warden var6 = (Warden)var4.getNearestEntity(Warden.class, var5, var2, var2.getX(), var2.getY(), var2.getZ(), var2.getBoundingBox().inflate(16.0));
            if (var6 != null && var6.getBrain().isMemoryValue(MemoryModuleType.ACTING_STAGE, -1)) {
               if (var6.crowdWaitingSoundTimeLeft >= 820) {
                  var6.getBrain().setMemory(MemoryModuleType.ACTING_STAGE, 0);
               } else if (var2 instanceof ServerPlayer) {
                  ServerPlayer var7 = (ServerPlayer)var2;
                  ((ServerPlayer)var2).sendSystemMessage(Component.literal("Why are they trying to start the show now? We will get quiet when it's time!"), true);
               }
            }
         }
      }

      ItemStack var8 = var2.getItemInHand(var3);
      Optional var9 = this.getInstrument(var8, var2.registryAccess());
      if (var9.isPresent()) {
         Instrument var10 = (Instrument)((Holder)var9.get()).value();
         var2.startUsingItem(var3);
         play(var1, var2, var10);
         var2.getCooldowns().addCooldown(var8, Mth.floor(var10.useDuration() * 20.0F));
         var2.awardStat(Stats.ITEM_USED.get(this));
         return InteractionResult.CONSUME;
      } else {
         return InteractionResult.FAIL;
      }
   }

   public int getUseDuration(ItemStack var1, LivingEntity var2) {
      Optional var3 = this.getInstrument(var1, var2.registryAccess());
      return (Integer)var3.map((var0) -> Mth.floor(((Instrument)var0.value()).useDuration() * 20.0F)).orElse(0);
   }

   private Optional<Holder<Instrument>> getInstrument(ItemStack var1, HolderLookup.Provider var2) {
      InstrumentComponent var3 = (InstrumentComponent)var1.get(DataComponents.INSTRUMENT);
      return var3 != null ? var3.unwrap(var2) : Optional.empty();
   }

   public ItemUseAnimation getUseAnimation(ItemStack var1) {
      return ItemUseAnimation.TOOT_HORN;
   }

   private static void play(Level var0, Player var1, Instrument var2) {
      SoundEvent var3 = (SoundEvent)var2.soundEvent().value();
      float var4 = var2.range() / 16.0F;
      var0.playSound(var1, (Entity)var1, var3, SoundSource.RECORDS, var4, 1.0F);
      var0.gameEvent(GameEvent.INSTRUMENT_PLAY, var1.position(), GameEvent.Context.of((Entity)var1));
   }
}
