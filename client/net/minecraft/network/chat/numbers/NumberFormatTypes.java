package net.minecraft.network.chat.numbers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class NumberFormatTypes {
   public static final MapCodec<NumberFormat> MAP_CODEC;
   public static final Codec<NumberFormat> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, NumberFormat> STREAM_CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Optional<NumberFormat>> OPTIONAL_STREAM_CODEC;

   public NumberFormatTypes() {
      super();
   }

   public static NumberFormatType<?> bootstrap(Registry<NumberFormatType<?>> var0) {
      Registry.register(var0, (String)"blank", BlankFormat.TYPE);
      Registry.register(var0, (String)"styled", StyledFormat.TYPE);
      return (NumberFormatType)Registry.register(var0, (String)"fixed", FixedFormat.TYPE);
   }

   static {
      MAP_CODEC = BuiltInRegistries.NUMBER_FORMAT_TYPE.byNameCodec().dispatchMap(NumberFormat::type, NumberFormatType::mapCodec);
      CODEC = MAP_CODEC.codec();
      STREAM_CODEC = ByteBufCodecs.registry(Registries.NUMBER_FORMAT_TYPE).dispatch(NumberFormat::type, NumberFormatType::streamCodec);
      OPTIONAL_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs::optional);
   }
}
