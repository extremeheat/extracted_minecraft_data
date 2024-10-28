package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;

public record Unbreakable(boolean showInTooltip) implements TooltipProvider {
   public static final Codec<Unbreakable> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.BOOL.optionalFieldOf("show_in_tooltip", true).forGetter(Unbreakable::showInTooltip)).apply(var0, Unbreakable::new);
   });
   public static final StreamCodec<ByteBuf, Unbreakable> STREAM_CODEC;
   private static final Component TOOLTIP;

   public Unbreakable(boolean showInTooltip) {
      super();
      this.showInTooltip = showInTooltip;
   }

   public void addToTooltip(Item.TooltipContext var1, Consumer<Component> var2, TooltipFlag var3) {
      if (this.showInTooltip) {
         var2.accept(TOOLTIP);
      }

   }

   public Unbreakable withTooltip(boolean var1) {
      return new Unbreakable(var1);
   }

   public boolean showInTooltip() {
      return this.showInTooltip;
   }

   static {
      STREAM_CODEC = ByteBufCodecs.BOOL.map(Unbreakable::new, Unbreakable::showInTooltip);
      TOOLTIP = Component.translatable("item.unbreakable").withStyle(ChatFormatting.BLUE);
   }
}
