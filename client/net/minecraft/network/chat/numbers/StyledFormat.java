package net.minecraft.network.chat.numbers;

import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.StreamCodec;

public class StyledFormat implements NumberFormat {
   public static final NumberFormatType<StyledFormat> TYPE = new NumberFormatType<StyledFormat>() {
      private static final MapCodec<StyledFormat> CODEC;
      private static final StreamCodec<RegistryFriendlyByteBuf, StyledFormat> STREAM_CODEC;

      public MapCodec<StyledFormat> mapCodec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, StyledFormat> streamCodec() {
         return STREAM_CODEC;
      }

      static {
         CODEC = Style.Serializer.MAP_CODEC.xmap(StyledFormat::new, (var0) -> {
            return var0.style;
         });
         STREAM_CODEC = StreamCodec.composite(Style.Serializer.TRUSTED_STREAM_CODEC, (var0) -> {
            return var0.style;
         }, StyledFormat::new);
      }
   };
   public static final StyledFormat NO_STYLE;
   public static final StyledFormat SIDEBAR_DEFAULT;
   public static final StyledFormat PLAYER_LIST_DEFAULT;
   final Style style;

   public StyledFormat(Style var1) {
      super();
      this.style = var1;
   }

   public MutableComponent format(int var1) {
      return Component.literal(Integer.toString(var1)).withStyle(this.style);
   }

   public NumberFormatType<StyledFormat> type() {
      return TYPE;
   }

   static {
      NO_STYLE = new StyledFormat(Style.EMPTY);
      SIDEBAR_DEFAULT = new StyledFormat(Style.EMPTY.withColor(ChatFormatting.RED));
      PLAYER_LIST_DEFAULT = new StyledFormat(Style.EMPTY.withColor(ChatFormatting.YELLOW));
   }
}
