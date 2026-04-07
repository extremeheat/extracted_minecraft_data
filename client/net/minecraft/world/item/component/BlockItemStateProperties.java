package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Util;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import org.jspecify.annotations.Nullable;

public record BlockItemStateProperties(Map<String, String> properties) implements TooltipProvider {
   public static final BlockItemStateProperties EMPTY = new BlockItemStateProperties(Map.of());
   public static final Codec<BlockItemStateProperties> CODEC;
   private static final StreamCodec<ByteBuf, Map<String, String>> PROPERTIES_STREAM_CODEC;
   public static final StreamCodec<ByteBuf, BlockItemStateProperties> STREAM_CODEC;

   public BlockItemStateProperties {
      super();
   }

   public <T extends Comparable<T>> BlockItemStateProperties with(final Property<T> property, final T value) {
      return new BlockItemStateProperties(Util.copyAndPut(this.properties, property.getName(), property.getName(value)));
   }

   public <T extends Comparable<T>> BlockItemStateProperties with(final Property<T> property, final BlockState state) {
      return this.with(property, state.getValue(property));
   }

   public <T extends Comparable<T>> @Nullable T get(final Property<T> property) {
      String value = (String)this.properties.get(property.getName());
      return (T)(value == null ? null : (Comparable)property.getValue(value).orElse((Object)null));
   }

   public BlockState apply(BlockState state) {
      StateDefinition<Block, BlockState> stateDefinition = state.getBlock().getStateDefinition();

      for(Map.Entry<String, String> entry : this.properties.entrySet()) {
         Property<?> property = stateDefinition.getProperty((String)entry.getKey());
         if (property != null) {
            state = updateState(state, property, (String)entry.getValue());
         }
      }

      return state;
   }

   private static <T extends Comparable<T>> BlockState updateState(final BlockState state, final Property<T> property, final String value) {
      return (BlockState)property.getValue(value).map((v) -> (BlockState)state.setValue(property, v)).orElse(state);
   }

   public boolean isEmpty() {
      return this.properties.isEmpty();
   }

   public void addToTooltip(final Item.TooltipContext context, final Consumer<Component> consumer, final TooltipFlag flag, final DataComponentGetter components) {
      Integer honeyLevel = (Integer)this.get(BeehiveBlock.HONEY_LEVEL);
      if (honeyLevel != null) {
         consumer.accept(Component.translatable("container.beehive.honey", honeyLevel, 5).withStyle(ChatFormatting.GRAY));
      }

   }

   static {
      CODEC = Codec.unboundedMap(Codec.STRING, Codec.STRING).xmap(BlockItemStateProperties::new, BlockItemStateProperties::properties);
      PROPERTIES_STREAM_CODEC = ByteBufCodecs.map(Object2ObjectOpenHashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.STRING_UTF8);
      STREAM_CODEC = PROPERTIES_STREAM_CODEC.map(BlockItemStateProperties::new, BlockItemStateProperties::properties);
   }
}
