package net.minecraft.world.item.component;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.TooltipFlag;

public record ItemLore(List<Component> lines, List<Component> styledLines) implements TooltipProvider {
   public static final ItemLore EMPTY = new ItemLore(List.of());
   public static final int MAX_LINES = 256;
   private static final Style LORE_STYLE;
   public static final Codec<ItemLore> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, ItemLore> STREAM_CODEC;

   public ItemLore(List<Component> var1) {
      this(var1, Lists.transform(var1, (var0) -> {
         return ComponentUtils.mergeStyles(var0.copy(), LORE_STYLE);
      }));
   }

   public ItemLore(List<Component> var1, List<Component> var2) {
      super();
      this.lines = var1;
      this.styledLines = var2;
   }

   public ItemLore withLineAdded(Component var1) {
      return new ItemLore(Util.copyAndAdd((List)this.lines, (Object)var1));
   }

   public void addToTooltip(Consumer<Component> var1, TooltipFlag var2) {
      this.styledLines.forEach(var1);
   }

   public List<Component> lines() {
      return this.lines;
   }

   public List<Component> styledLines() {
      return this.styledLines;
   }

   static {
      LORE_STYLE = Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE).withItalic(true);
      CODEC = ComponentSerialization.FLAT_CODEC.sizeLimitedListOf(256).xmap(ItemLore::new, ItemLore::lines);
      STREAM_CODEC = ComponentSerialization.STREAM_CODEC.apply(ByteBufCodecs.list(256)).map(ItemLore::new, ItemLore::lines);
   }
}