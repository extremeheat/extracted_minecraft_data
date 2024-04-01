package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;

public record DebugStickState(Map<Holder<Block>, Property<?>> c) {
   private final Map<Holder<Block>, Property<?>> properties;
   public static final DebugStickState EMPTY = new DebugStickState(Map.of());
   public static final Codec<DebugStickState> CODEC = ExtraCodecs.unboundedDispatchMap(
         BuiltInRegistries.BLOCK.holderByNameCodec(), var0 -> Codec.STRING.comapFlatMap(var1 -> {
               Property var2 = ((Block)var0.value()).getStateDefinition().getProperty(var1);
               return var2 != null ? DataResult.success(var2) : DataResult.error(() -> "No property on " + var0.getRegisteredName() + " with name: " + var1);
            }, Property::getName)
      )
      .xmap(DebugStickState::new, DebugStickState::properties);

   public DebugStickState(Map<Holder<Block>, Property<?>> var1) {
      super();
      this.properties = var1;
   }

   public DebugStickState withProperty(Holder<Block> var1, Property<?> var2) {
      return new DebugStickState(Util.copyAndPut(this.properties, var1, var2));
   }
}
