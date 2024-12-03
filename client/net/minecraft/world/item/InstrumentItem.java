package net.minecraft.world.item;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class InstrumentItem extends Item {
   private final TagKey<Instrument> instruments;

   public InstrumentItem(TagKey<Instrument> var1, Item.Properties var2) {
      super(var2);
      this.instruments = var1;
   }

   public void appendHoverText(ItemStack var1, Item.TooltipContext var2, List<Component> var3, TooltipFlag var4) {
      super.appendHoverText(var1, var2, var3, var4);
      HolderLookup.Provider var5 = var2.registries();
      if (var5 != null) {
         Optional var6 = this.getInstrument(var1, var5);
         if (var6.isPresent()) {
            MutableComponent var7 = ((Instrument)((Holder)var6.get()).value()).description().copy();
            ComponentUtils.mergeStyles(var7, Style.EMPTY.withColor(ChatFormatting.GRAY));
            var3.add(var7);
         }

      }
   }

   public static ItemStack create(Item var0, Holder<Instrument> var1) {
      ItemStack var2 = new ItemStack(var0);
      var2.set(DataComponents.INSTRUMENT, var1);
      return var2;
   }

   public InteractionResult use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      Optional var5 = this.getInstrument(var4, var2.registryAccess());
      if (var5.isPresent()) {
         Instrument var6 = (Instrument)((Holder)var5.get()).value();
         var2.startUsingItem(var3);
         play(var1, var2, var6);
         var2.getCooldowns().addCooldown(var4, Mth.floor(var6.useDuration() * 20.0F));
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
      Holder var3 = (Holder)var1.get(DataComponents.INSTRUMENT);
      if (var3 != null) {
         return Optional.of(var3);
      } else {
         Optional var4 = var2.lookupOrThrow(Registries.INSTRUMENT).get(this.instruments);
         if (var4.isPresent()) {
            Iterator var5 = ((HolderSet.Named)var4.get()).iterator();
            if (var5.hasNext()) {
               return Optional.of((Holder)var5.next());
            }
         }

         return Optional.empty();
      }
   }

   public ItemUseAnimation getUseAnimation(ItemStack var1) {
      return ItemUseAnimation.TOOT_HORN;
   }

   private static void play(Level var0, Player var1, Instrument var2) {
      SoundEvent var3 = (SoundEvent)var2.soundEvent().value();
      float var4 = var2.range() / 16.0F;
      var0.playSound((Player)var1, (Entity)var1, var3, SoundSource.RECORDS, var4, 1.0F);
      var0.gameEvent(GameEvent.INSTRUMENT_PLAY, var1.position(), GameEvent.Context.of((Entity)var1));
   }
}
