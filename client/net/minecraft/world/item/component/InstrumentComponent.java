package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EitherHolder;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public record InstrumentComponent(EitherHolder<Instrument> instrument) implements TooltipProvider {
   public static final Codec<InstrumentComponent> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, InstrumentComponent> STREAM_CODEC;

   public InstrumentComponent(Holder<Instrument> var1) {
      this(new EitherHolder(var1));
   }

   /** @deprecated */
   @Deprecated
   public InstrumentComponent(ResourceKey<Instrument> var1) {
      this(new EitherHolder(var1));
   }

   public InstrumentComponent(EitherHolder<Instrument> var1) {
      super();
      this.instrument = var1;
   }

   public void addToTooltip(Item.TooltipContext var1, Consumer<Component> var2, TooltipFlag var3, @Nullable Player var4, ItemStack var5) {
      HolderLookup.Provider var6 = var1.registries();
      if (var6 != null) {
         Optional var7 = this.unwrap(var6);
         if (var7.isPresent()) {
            MutableComponent var8 = ((Instrument)((Holder)var7.get()).value()).description().copy();
            ComponentUtils.mergeStyles(var8, Style.EMPTY.withColor(ChatFormatting.GRAY));
            var2.accept(var8);
         }

      }
   }

   public Optional<Holder<Instrument>> unwrap(HolderLookup.Provider var1) {
      return this.instrument.unwrap(var1);
   }

   static {
      CODEC = EitherHolder.codec(Registries.INSTRUMENT, Instrument.CODEC).xmap(InstrumentComponent::new, InstrumentComponent::instrument);
      STREAM_CODEC = EitherHolder.streamCodec(Registries.INSTRUMENT, Instrument.STREAM_CODEC).map(InstrumentComponent::new, InstrumentComponent::instrument);
   }
}
