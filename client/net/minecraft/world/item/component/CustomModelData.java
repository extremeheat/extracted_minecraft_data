package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record CustomModelData(int value) {
   public static final CustomModelData DEFAULT = new CustomModelData(0);
   public static final Codec<CustomModelData> CODEC;
   public static final StreamCodec<ByteBuf, CustomModelData> STREAM_CODEC;

   public CustomModelData(int value) {
      super();
      this.value = value;
   }

   public int value() {
      return this.value;
   }

   static {
      CODEC = Codec.INT.xmap(CustomModelData::new, CustomModelData::value);
      STREAM_CODEC = ByteBufCodecs.VAR_INT.map(CustomModelData::new, CustomModelData::value);
   }
}
