package net.minecraft.server.jsonrpc.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ResultInfo(String name, Schema schema) {
   public static final MapCodec<ResultInfo> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.STRING.fieldOf("name").forGetter(ResultInfo::name), Schema.CODEC.fieldOf("schema").forGetter(ResultInfo::schema)).apply(var0, ResultInfo::new));

   public ResultInfo(String var1, Schema var2) {
      super();
      this.name = var1;
      this.schema = var2;
   }
}
