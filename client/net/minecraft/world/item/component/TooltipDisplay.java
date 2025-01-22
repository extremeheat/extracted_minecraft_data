package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ReferenceLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSortedSets;
import java.util.List;
import java.util.SequencedSet;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record TooltipDisplay(boolean hideTooltip, SequencedSet<DataComponentType<?>> hiddenComponents) {
   private static final Codec<SequencedSet<DataComponentType<?>>> COMPONENT_SET_CODEC;
   public static final Codec<TooltipDisplay> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, TooltipDisplay> STREAM_CODEC;
   public static final TooltipDisplay DEFAULT;

   public TooltipDisplay(boolean var1, SequencedSet<DataComponentType<?>> var2) {
      super();
      this.hideTooltip = var1;
      this.hiddenComponents = var2;
   }

   public TooltipDisplay withHidden(DataComponentType<?> var1, boolean var2) {
      if (this.hiddenComponents.contains(var1) == var2) {
         return this;
      } else {
         ReferenceLinkedOpenHashSet var3 = new ReferenceLinkedOpenHashSet(this.hiddenComponents);
         if (var2) {
            var3.add(var1);
         } else {
            var3.remove(var1);
         }

         return new TooltipDisplay(this.hideTooltip, var3);
      }
   }

   public boolean shows(DataComponentType<?> var1) {
      return !this.hideTooltip && !this.hiddenComponents.contains(var1);
   }

   static {
      COMPONENT_SET_CODEC = DataComponentType.CODEC.listOf().xmap(ReferenceLinkedOpenHashSet::new, List::copyOf);
      CODEC = RecordCodecBuilder.create((var0) -> var0.group(Codec.BOOL.optionalFieldOf("hide_tooltip", false).forGetter(TooltipDisplay::hideTooltip), COMPONENT_SET_CODEC.optionalFieldOf("hidden_components", ReferenceSortedSets.emptySet()).forGetter(TooltipDisplay::hiddenComponents)).apply(var0, TooltipDisplay::new));
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, TooltipDisplay::hideTooltip, DataComponentType.STREAM_CODEC.apply(ByteBufCodecs.collection(ReferenceLinkedOpenHashSet::new)), TooltipDisplay::hiddenComponents, TooltipDisplay::new);
      DEFAULT = new TooltipDisplay(false, ReferenceSortedSets.emptySet());
   }
}
