package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.TooltipFlag;

public record XpComponent(int d) implements TooltipProvider {
   private final int value;
   public static final XpComponent DEFAULT = new XpComponent(10);
   public static final Codec<XpComponent> CODEC = Codec.INT.xmap(XpComponent::new, XpComponent::value);
   public static final StreamCodec<ByteBuf, XpComponent> STREAM_CODEC = ByteBufCodecs.INT.map(XpComponent::new, XpComponent::value);

   public XpComponent(int var1) {
      super();
      this.value = var1;
   }

   @Override
   public void addToTooltip(Consumer<Component> var1, TooltipFlag var2) {
      var1.accept(Component.translatable("item.minecraft.potato_of_knowledge.amount", this.value));
   }
}
