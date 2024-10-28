package net.minecraft.world.item;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class InstrumentItem extends Item {
   private final TagKey<Instrument> instruments;

   public InstrumentItem(Item.Properties var1, TagKey<Instrument> var2) {
      super(var1);
      this.instruments = var2;
   }

   public void appendHoverText(ItemStack var1, Item.TooltipContext var2, List<Component> var3, TooltipFlag var4) {
      super.appendHoverText(var1, var2, var3, var4);
      Optional var5 = this.getInstrument(var1).flatMap(Holder::unwrapKey);
      if (var5.isPresent()) {
         MutableComponent var6 = Component.translatable(Util.makeDescriptionId("instrument", ((ResourceKey)var5.get()).location()));
         var3.add(var6.withStyle(ChatFormatting.GRAY));
      }

   }

   public static ItemStack create(Item var0, Holder<Instrument> var1) {
      ItemStack var2 = new ItemStack(var0);
      var2.set(DataComponents.INSTRUMENT, var1);
      return var2;
   }

   public static void setRandom(ItemStack var0, TagKey<Instrument> var1, RandomSource var2) {
      Optional var3 = BuiltInRegistries.INSTRUMENT.getRandomElementOf(var1, var2);
      var3.ifPresent((var1x) -> {
         var0.set(DataComponents.INSTRUMENT, var1x);
      });
   }

   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      Optional var5 = this.getInstrument(var4);
      if (var5.isPresent()) {
         Instrument var6 = (Instrument)((Holder)var5.get()).value();
         var2.startUsingItem(var3);
         play(var1, var2, var6);
         var2.getCooldowns().addCooldown(this, var6.useDuration());
         var2.awardStat(Stats.ITEM_USED.get(this));
         return InteractionResultHolder.consume(var4);
      } else {
         return InteractionResultHolder.fail(var4);
      }
   }

   public int getUseDuration(ItemStack var1, LivingEntity var2) {
      Optional var3 = this.getInstrument(var1);
      return (Integer)var3.map((var0) -> {
         return ((Instrument)var0.value()).useDuration();
      }).orElse(0);
   }

   private Optional<Holder<Instrument>> getInstrument(ItemStack var1) {
      Holder var2 = (Holder)var1.get(DataComponents.INSTRUMENT);
      if (var2 != null) {
         return Optional.of(var2);
      } else {
         Iterator var3 = BuiltInRegistries.INSTRUMENT.getTagOrEmpty(this.instruments).iterator();
         return var3.hasNext() ? Optional.of((Holder)var3.next()) : Optional.empty();
      }
   }

   public UseAnim getUseAnimation(ItemStack var1) {
      return UseAnim.TOOT_HORN;
   }

   private static void play(Level var0, Player var1, Instrument var2) {
      SoundEvent var3 = (SoundEvent)var2.soundEvent().value();
      float var4 = var2.range() / 16.0F;
      var0.playSound((Player)var1, (Entity)var1, var3, SoundSource.RECORDS, var4, 1.0F);
      var0.gameEvent(GameEvent.INSTRUMENT_PLAY, var1.position(), GameEvent.Context.of((Entity)var1));
   }
}
