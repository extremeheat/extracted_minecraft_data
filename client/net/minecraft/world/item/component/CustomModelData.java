package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

public record CustomModelData(List<Float> floats, List<Boolean> flags, List<String> strings, List<Integer> colors) {
   public static final CustomModelData EMPTY = new CustomModelData(List.of(), List.of(), List.of(), List.of());
   public static final Codec<CustomModelData> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Codec.FLOAT.listOf().optionalFieldOf("floats", List.of()).forGetter(CustomModelData::floats), Codec.BOOL.listOf().optionalFieldOf("flags", List.of()).forGetter(CustomModelData::flags), Codec.STRING.listOf().optionalFieldOf("strings", List.of()).forGetter(CustomModelData::strings), ExtraCodecs.RGB_COLOR_CODEC.listOf().optionalFieldOf("colors", List.of()).forGetter(CustomModelData::colors)).apply(var0, CustomModelData::new));
   public static final StreamCodec<ByteBuf, CustomModelData> STREAM_CODEC;

   public CustomModelData(List<Float> var1, List<Boolean> var2, List<String> var3, List<Integer> var4) {
      super();
      this.floats = var1;
      this.flags = var2;
      this.strings = var3;
      this.colors = var4;
   }

   @Nullable
   private static <T> T getSafe(List<T> var0, int var1) {
      return (T)(var1 >= 0 && var1 < var0.size() ? var0.get(var1) : null);
   }

   @Nullable
   public Float getFloat(int var1) {
      return (Float)getSafe(this.floats, var1);
   }

   @Nullable
   public Boolean getBoolean(int var1) {
      return (Boolean)getSafe(this.flags, var1);
   }

   @Nullable
   public String getString(int var1) {
      return (String)getSafe(this.strings, var1);
   }

   @Nullable
   public Integer getColor(int var1) {
      return (Integer)getSafe(this.colors, var1);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.FLOAT.apply(ByteBufCodecs.list()), CustomModelData::floats, ByteBufCodecs.BOOL.apply(ByteBufCodecs.list()), CustomModelData::flags, ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), CustomModelData::strings, ByteBufCodecs.INT.apply(ByteBufCodecs.list()), CustomModelData::colors, CustomModelData::new);
   }
}
