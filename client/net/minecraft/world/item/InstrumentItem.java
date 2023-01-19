package net.minecraft.world.item;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class InstrumentItem extends Item {
   private static final String TAG_INSTRUMENT = "instrument";
   private TagKey<Instrument> instruments;

   public InstrumentItem(Item.Properties var1, TagKey<Instrument> var2) {
      super(var1);
      this.instruments = var2;
   }

   @Override
   public void appendHoverText(ItemStack var1, @Nullable Level var2, List<Component> var3, TooltipFlag var4) {
      super.appendHoverText(var1, var2, var3, var4);
      Optional var5 = this.getInstrument(var1).flatMap(Holder::unwrapKey);
      if (var5.isPresent()) {
         MutableComponent var6 = Component.translatable(Util.makeDescriptionId("instrument", ((ResourceKey)var5.get()).location()));
         var3.add(var6.withStyle(ChatFormatting.GRAY));
      }
   }

   public static ItemStack create(Item var0, Holder<Instrument> var1) {
      ItemStack var2 = new ItemStack(var0);
      setSoundVariantId(var2, var1);
      return var2;
   }

   public static void setRandom(ItemStack var0, TagKey<Instrument> var1, RandomSource var2) {
      Optional var3 = Registry.INSTRUMENT.getTag(var1).flatMap(var1x -> var1x.getRandomElement(var2));
      if (var3.isPresent()) {
         setSoundVariantId(var0, (Holder<Instrument>)var3.get());
      }
   }

   private static void setSoundVariantId(ItemStack var0, Holder<Instrument> var1) {
      CompoundTag var2 = var0.getOrCreateTag();
      var2.putString("instrument", ((ResourceKey)var1.unwrapKey().orElseThrow(() -> new IllegalStateException("Invalid instrument"))).location().toString());
   }

   @Override
   public void fillItemCategory(CreativeModeTab var1, NonNullList<ItemStack> var2) {
      if (this.allowedIn(var1)) {
         for(Holder var4 : Registry.INSTRUMENT.getTagOrEmpty(this.instruments)) {
            var2.add(create(Items.GOAT_HORN, var4));
         }
      }
   }

   @Override
   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      Optional var5 = this.getInstrument(var4);
      if (var5.isPresent()) {
         Instrument var6 = (Instrument)((Holder)var5.get()).value();
         var2.startUsingItem(var3);
         play(var1, var2, var6);
         var2.getCooldowns().addCooldown(this, var6.useDuration());
         return InteractionResultHolder.consume(var4);
      } else {
         return InteractionResultHolder.fail(var4);
      }
   }

   @Override
   public int getUseDuration(ItemStack var1) {
      Optional var2 = this.getInstrument(var1);
      return var2.isPresent() ? ((Instrument)((Holder)var2.get()).value()).useDuration() : 0;
   }

   private Optional<Holder<Instrument>> getInstrument(ItemStack var1) {
      CompoundTag var2 = var1.getTag();
      if (var2 != null) {
         ResourceLocation var3 = ResourceLocation.tryParse(var2.getString("instrument"));
         if (var3 != null) {
            return Registry.INSTRUMENT.getHolder(ResourceKey.create(Registry.INSTRUMENT_REGISTRY, var3));
         }
      }

      Iterator var4 = Registry.INSTRUMENT.getTagOrEmpty(this.instruments).iterator();
      return var4.hasNext() ? Optional.of((Holder<Instrument>)var4.next()) : Optional.empty();
   }

   @Override
   public UseAnim getUseAnimation(ItemStack var1) {
      return UseAnim.TOOT_HORN;
   }

   private static void play(Level var0, Player var1, Instrument var2) {
      SoundEvent var3 = var2.soundEvent();
      float var4 = var2.range() / 16.0F;
      var0.playSound(var1, var1, var3, SoundSource.RECORDS, var4, 1.0F);
      var0.gameEvent(GameEvent.INSTRUMENT_PLAY, var1.position(), GameEvent.Context.of(var1));
   }
}
