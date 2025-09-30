package net.minecraft.server.jsonrpc.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ParamInfo(String name, Schema schema, boolean required) {
   public static final MapCodec<ParamInfo> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.STRING.fieldOf("name").forGetter(ParamInfo::name), Schema.CODEC.fieldOf("schema").forGetter(ParamInfo::schema), Codec.BOOL.fieldOf("required").forGetter(ParamInfo::required)).apply(var0, ParamInfo::new));

   public ParamInfo(String var1, Schema var2) {
      this(var1, var2, true);
   }

   public ParamInfo(String var1, Schema var2, boolean var3) {
      super();
      this.name = var1;
      this.schema = var2;
      this.required = var3;
   }
}
