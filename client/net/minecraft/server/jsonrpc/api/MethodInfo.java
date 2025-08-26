package net.minecraft.server.jsonrpc.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;

public record MethodInfo(ResourceLocation name, String description, boolean runOnMainThread, boolean discoverable, List<ParamInfo> params, Optional<ResultInfo> result) {
   public static final MapCodec<MethodInfo> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ResourceLocation.CODEC.fieldOf("name").forGetter(MethodInfo::name), Codec.STRING.fieldOf("description").forGetter(MethodInfo::description), Codec.list(ParamInfo.CODEC.codec()).fieldOf("params").forGetter(MethodInfo::params), ResultInfo.CODEC.codec().optionalFieldOf("result").forGetter(MethodInfo::result)).apply(var0, MethodInfo::new));

   public MethodInfo(ResourceLocation var1, String var2, boolean var3, boolean var4) {
      this(var1, var2, var4, var3, List.of(), Optional.empty());
   }

   public MethodInfo(ResourceLocation var1, String var2, List<ParamInfo> var3, Optional<ResultInfo> var4) {
      this(var1, var2, true, true, var3, var4);
   }

   public MethodInfo(ResourceLocation var1, String var2, boolean var3, boolean var4, List<ParamInfo> var5, Optional<ResultInfo> var6) {
      super();
      this.name = var1;
      this.description = var2;
      this.runOnMainThread = var3;
      this.discoverable = var4;
      this.params = var5;
      this.result = var6;
   }

   public MethodInfo withParam(ParamInfo... var1) {
      ArrayList var2 = new ArrayList(this.params());
      var2.addAll(List.of(var1));
      return new MethodInfo(this.name, this.description, this.runOnMainThread, this.discoverable, var2, this.result);
   }

   public MethodInfo withResult(ResultInfo var1) {
      return new MethodInfo(this.name, this.description, this.runOnMainThread, this.discoverable, this.params, Optional.of(var1));
   }
}
