package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;

public record Bees(List<BeehiveBlockEntity.Occupant> bees) implements TooltipProvider {
   public static final Codec<Bees> CODEC;
   public static final StreamCodec<ByteBuf, Bees> STREAM_CODEC;
   public static final Bees EMPTY;

   public Bees(List<BeehiveBlockEntity.Occupant> var1) {
      super();
      this.bees = var1;
   }

   public void addToTooltip(Item.TooltipContext var1, Consumer<Component> var2, TooltipFlag var3, DataComponentGetter var4) {
      var2.accept(Component.translatable("container.beehive.bees", this.bees.size(), 3).withStyle(ChatFormatting.GRAY));
   }

   static {
      CODEC = BeehiveBlockEntity.Occupant.LIST_CODEC.xmap(Bees::new, Bees::bees);
      STREAM_CODEC = BeehiveBlockEntity.Occupant.STREAM_CODEC.apply(ByteBufCodecs.list()).map(Bees::new, Bees::bees);
      EMPTY = new Bees(List.of());
   }
}
