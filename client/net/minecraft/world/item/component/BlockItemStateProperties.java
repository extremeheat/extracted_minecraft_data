package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public record BlockItemStateProperties(Map<String, String> properties) {
   public static final BlockItemStateProperties EMPTY = new BlockItemStateProperties(Map.of());
   public static final Codec<BlockItemStateProperties> CODEC = Codec.unboundedMap(Codec.STRING, Codec.STRING)
      .xmap(BlockItemStateProperties::new, BlockItemStateProperties::properties);
   private static final StreamCodec<ByteBuf, Map<String, String>> PROPERTIES_STREAM_CODEC = ByteBufCodecs.map(
      Object2ObjectOpenHashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.STRING_UTF8
   );
   public static final StreamCodec<ByteBuf, BlockItemStateProperties> STREAM_CODEC = PROPERTIES_STREAM_CODEC.map(
      BlockItemStateProperties::new, BlockItemStateProperties::properties
   );

   public BlockItemStateProperties(Map<String, String> properties) {
      super();
      this.properties = (Map<String, String>)properties;
   }

   public <T extends Comparable<T>> BlockItemStateProperties with(Property<T> var1, T var2) {
      return new BlockItemStateProperties(Util.copyAndPut(this.properties, var1.getName(), var1.getName((T)var2)));
   }

   public <T extends Comparable<T>> BlockItemStateProperties with(Property<T> var1, BlockState var2) {
      return this.with(var1, var2.getValue(var1));
   }

   @Nullable
   public <T extends Comparable<T>> T get(Property<T> var1) {
      String var2 = this.properties.get(var1.getName());
      return (T)(var2 == null ? null : var1.getValue(var2).orElse(null));
   }

   public BlockState apply(BlockState var1) {
      StateDefinition var2 = var1.getBlock().getStateDefinition();

      for (Entry var4 : this.properties.entrySet()) {
         Property var5 = var2.getProperty((String)var4.getKey());
         if (var5 != null) {
            var1 = updateState(var1, var5, (String)var4.getValue());
         }
      }

      return var1;
   }

   private static <T extends Comparable<T>> BlockState updateState(BlockState var0, Property<T> var1, String var2) {
      return var1.getValue(var2).map(var2x -> var0.setValue(var1, var2x)).orElse(var0);
   }

   public boolean isEmpty() {
      return this.properties.isEmpty();
   }
}
