package net.minecraft.network.chat.numbers;

import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public class StyledFormat implements NumberFormat {
   public static final NumberFormatType<StyledFormat> TYPE = new NumberFormatType<StyledFormat>() {
      private static final MapCodec<StyledFormat> CODEC = Style.Serializer.MAP_CODEC.xmap(StyledFormat::new, var0 -> var0.style);

      @Override
      public MapCodec<StyledFormat> mapCodec() {
         return CODEC;
      }

      public void writeToStream(FriendlyByteBuf var1, StyledFormat var2) {
         var1.writeWithCodec(NbtOps.INSTANCE, Style.Serializer.CODEC, var2.style);
      }

      public StyledFormat readFromStream(FriendlyByteBuf var1) {
         Style var2 = var1.readWithCodecTrusted(NbtOps.INSTANCE, Style.Serializer.CODEC);
         return new StyledFormat(var2);
      }
   };
   public static final StyledFormat NO_STYLE = new StyledFormat(Style.EMPTY);
   public static final StyledFormat SIDEBAR_DEFAULT = new StyledFormat(Style.EMPTY.withColor(ChatFormatting.RED));
   public static final StyledFormat PLAYER_LIST_DEFAULT = new StyledFormat(Style.EMPTY.withColor(ChatFormatting.YELLOW));
   final Style style;

   public StyledFormat(Style var1) {
      super();
      this.style = var1;
   }

   @Override
   public MutableComponent format(int var1) {
      return Component.literal(Integer.toString(var1)).withStyle(this.style);
   }

   @Override
   public NumberFormatType<StyledFormat> type() {
      return TYPE;
   }
}
