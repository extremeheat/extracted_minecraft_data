package net.minecraft.network.chat.numbers;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;

public record FixedFormat(Component value) implements NumberFormat {
   public static final NumberFormatType<FixedFormat> TYPE = new NumberFormatType<FixedFormat>() {
      private static final MapCodec<FixedFormat> CODEC;
      private static final StreamCodec<RegistryFriendlyByteBuf, FixedFormat> STREAM_CODEC;

      public MapCodec<FixedFormat> mapCodec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, FixedFormat> streamCodec() {
         return STREAM_CODEC;
      }

      static {
         CODEC = ComponentSerialization.CODEC.fieldOf("value").xmap(FixedFormat::new, FixedFormat::value);
         STREAM_CODEC = StreamCodec.composite(ComponentSerialization.TRUSTED_STREAM_CODEC, FixedFormat::value, FixedFormat::new);
      }
   };

   public FixedFormat {
      super();
   }

   public MutableComponent format(final int value) {
      return this.value.copy();
   }

   public NumberFormatType<FixedFormat> type() {
      return TYPE;
   }
}
