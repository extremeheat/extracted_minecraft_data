package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.TooltipFlag;

public record Unbreakable(boolean showInTooltip) implements TooltipProvider {
   public static final Codec<Unbreakable> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(Codec.BOOL.optionalFieldOf("show_in_tooltip", true).forGetter(Unbreakable::showInTooltip)).apply(var0, Unbreakable::new)
   );
   public static final StreamCodec<ByteBuf, Unbreakable> STREAM_CODEC = ByteBufCodecs.BOOL.map(Unbreakable::new, Unbreakable::showInTooltip);
   private static final Component TOOLTIP = Component.translatable("item.unbreakable").withStyle(ChatFormatting.BLUE);

   public Unbreakable(boolean showInTooltip) {
      super();
      this.showInTooltip = showInTooltip;
   }

   @Override
   public void addToTooltip(Consumer<Component> var1, TooltipFlag var2) {
      if (this.showInTooltip) {
         var1.accept(TOOLTIP);
      }
   }

   public Unbreakable withTooltip(boolean var1) {
      return new Unbreakable(var1);
   }
}
