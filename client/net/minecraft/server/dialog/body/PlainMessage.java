package net.minecraft.server.dialog.body;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.util.ExtraCodecs;

public record PlainMessage(Component contents, int width) implements DialogBody {
   public static final int DEFAULT_WIDTH = 200;
   public static final MapCodec<PlainMessage> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ComponentSerialization.CODEC.fieldOf("contents").forGetter(PlainMessage::contents), ExtraCodecs.POSITIVE_INT.optionalFieldOf("width", 200).forGetter(PlainMessage::width)).apply(var0, PlainMessage::new));
   public static final Codec<PlainMessage> CODEC;

   public PlainMessage(Component var1, int var2) {
      super();
      this.contents = var1;
      this.width = var2;
   }

   public MapCodec<PlainMessage> mapCodec() {
      return MAP_CODEC;
   }

   static {
      CODEC = Codec.withAlternative(MAP_CODEC.codec(), ComponentSerialization.CODEC, (var0) -> new PlainMessage(var0, 200));
   }
}
