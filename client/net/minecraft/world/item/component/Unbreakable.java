package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import io.netty.buffer.ByteBuf;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.TooltipFlag;

public record Unbreakable(boolean c) implements TooltipProvider {
   private final boolean showInTooltip;
   public static final Codec<Unbreakable> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(ExtraCodecs.strictOptionalField(Codec.BOOL, "show_in_tooltip", true).forGetter(Unbreakable::showInTooltip))
            .apply(var0, Unbreakable::new)
   );
   public static final StreamCodec<ByteBuf, Unbreakable> STREAM_CODEC = ByteBufCodecs.BOOL.map(Unbreakable::new, Unbreakable::showInTooltip);
   private static final Component TOOLTIP = Component.translatable("item.unbreakable").withStyle(ChatFormatting.BLUE);

   public Unbreakable(boolean var1) {
      super();
      this.showInTooltip = var1;
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
