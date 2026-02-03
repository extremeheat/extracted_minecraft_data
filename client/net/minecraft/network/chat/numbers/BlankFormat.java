package net.minecraft.network.chat.numbers;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;

public class BlankFormat implements NumberFormat {
   public static final BlankFormat INSTANCE = new BlankFormat();
   public static final NumberFormatType<BlankFormat> TYPE = new NumberFormatType<BlankFormat>() {
      private static final MapCodec<BlankFormat> CODEC;
      private static final StreamCodec<RegistryFriendlyByteBuf, BlankFormat> STREAM_CODEC;

      public MapCodec<BlankFormat> mapCodec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, BlankFormat> streamCodec() {
         return STREAM_CODEC;
      }

      static {
         CODEC = MapCodec.unit(BlankFormat.INSTANCE);
         STREAM_CODEC = StreamCodec.<RegistryFriendlyByteBuf, BlankFormat>unit(BlankFormat.INSTANCE);
      }
   };

   private BlankFormat() {
      super();
   }

   public MutableComponent format(final int value) {
      return Component.empty();
   }

   public NumberFormatType<BlankFormat> type() {
      return TYPE;
   }
}
