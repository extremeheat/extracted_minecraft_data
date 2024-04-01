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
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.TooltipFlag;

public record ItemLore(List<Component> e, List<Component> f) implements TooltipProvider {
   private final List<Component> lines;
   private final List<Component> styledLines;
   public static final ItemLore EMPTY = new ItemLore(List.of());
   public static final int MAX_LINES = 256;
   private static final Style LORE_STYLE = Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE).withItalic(true);
   public static final Codec<ItemLore> CODEC = ExtraCodecs.sizeLimitedList(ComponentSerialization.FLAT_CODEC.listOf(), 256)
      .xmap(ItemLore::new, ItemLore::lines);
   public static final StreamCodec<RegistryFriendlyByteBuf, ItemLore> STREAM_CODEC = ComponentSerialization.STREAM_CODEC
      .<List<Component>>apply(ByteBufCodecs.list(256))
      .map(ItemLore::new, ItemLore::lines);

   public ItemLore(List<Component> var1) {
      this(var1, Lists.transform(var1, var0 -> ComponentUtils.mergeStyles(var0.copy(), LORE_STYLE)));
   }

   public ItemLore(List<Component> var1, List<Component> var2) {
      super();
      this.lines = var1;
      this.styledLines = var2;
   }

   public ItemLore withLineAdded(Component var1) {
      return new ItemLore(Util.copyAndAdd(this.lines, var1));
   }

   @Override
   public void addToTooltip(Consumer<Component> var1, TooltipFlag var2) {
      this.styledLines.forEach(var1);
   }
}
