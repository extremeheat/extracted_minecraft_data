package net.minecraft.network.chat.numbers;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;

public class FixedFormat implements NumberFormat {
   public static final NumberFormatType<FixedFormat> TYPE = new NumberFormatType<FixedFormat>() {
      private static final MapCodec<FixedFormat> CODEC = ComponentSerialization.CODEC.fieldOf("value").xmap(FixedFormat::new, var0 -> var0.value);

      @Override
      public MapCodec<FixedFormat> mapCodec() {
         return CODEC;
      }

      public void writeToStream(FriendlyByteBuf var1, FixedFormat var2) {
         var1.writeComponent(var2.value);
      }

      public FixedFormat readFromStream(FriendlyByteBuf var1) {
         Component var2 = var1.readComponentTrusted();
         return new FixedFormat(var2);
      }
   };
   final Component value;

   public FixedFormat(Component var1) {
      super();
      this.value = var1;
   }

   @Override
   public MutableComponent format(int var1) {
      return this.value.copy();
   }

   @Override
   public NumberFormatType<FixedFormat> type() {
      return TYPE;
   }
}
