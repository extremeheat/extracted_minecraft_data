package net.minecraft.commands.arguments;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public interface ValidatableArgument {
   ContextKeySet contextKeySet();

   static <T, Self extends ValidatableArgument & SerializableArgument<T>> SerializableArgument.Type<Self> codecWithValidation(final Function<ContextKeySet, Self> factory) {
      MapCodec<Self> mapCodec = RecordCodecBuilder.mapCodec((i) -> i.group(LootContextParamSets.CODEC.fieldOf("context").forGetter((rec$) -> rec$.contextKeySet())).apply(i, factory));
      StreamCodec<RegistryFriendlyByteBuf, Self> streamCodec = StreamCodec.composite(LootContextParamSets.STREAM_CODEC, (rec$) -> rec$.contextKeySet(), factory);
      return new SerializableArgument.Type<Self>(mapCodec, streamCodec);
   }
}
