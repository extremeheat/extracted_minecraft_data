package net.minecraft.server.jsonrpc.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.Nullable;

public record MethodInfo<Params, Result>(String description, Optional<ParamInfo<Params>> params, Optional<ResultInfo<Result>> result) {
   public MethodInfo(String var1, @Nullable ParamInfo<Params> var2, @Nullable ResultInfo<Result> var3) {
      this(var1, Optional.ofNullable(var2), Optional.ofNullable(var3));
   }

   public MethodInfo(String var1, Optional<ParamInfo<Params>> var2, Optional<ResultInfo<Result>> var3) {
      super();
      this.description = var1;
      this.params = var2;
      this.result = var3;
   }

   private static <Params> Optional<ParamInfo<Params>> toOptional(List<ParamInfo<Params>> var0) {
      return var0.isEmpty() ? Optional.empty() : Optional.of((ParamInfo)var0.getFirst());
   }

   private static <Params> List<ParamInfo<Params>> toList(Optional<ParamInfo<Params>> var0) {
      return var0.isPresent() ? List.of((ParamInfo)var0.get()) : List.of();
   }

   private static <Params> Codec<Optional<ParamInfo<Params>>> paramsTypedCodec() {
      return ParamInfo.typedCodec().codec().listOf().xmap(MethodInfo::toOptional, MethodInfo::toList);
   }

   static <Params, Result> MapCodec<MethodInfo<Params, Result>> typedCodec() {
      return RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.STRING.fieldOf("description").forGetter(MethodInfo::description), paramsTypedCodec().fieldOf("params").forGetter(MethodInfo::params), ResultInfo.typedCodec().optionalFieldOf("result").forGetter(MethodInfo::result)).apply(var0, MethodInfo::new));
   }

   public Named<Params, Result> named(ResourceLocation var1) {
      return new Named<Params, Result>(var1, this);
   }

   public static record Named<Params, Result>(ResourceLocation name, MethodInfo<Params, Result> contents) {
      public static final Codec<Named<?, ?>> CODEC = typedCodec();

      public Named(ResourceLocation var1, MethodInfo<Params, Result> var2) {
         super();
         this.name = var1;
         this.contents = var2;
      }

      public static <Params, Result> Codec<Named<Params, Result>> typedCodec() {
         return RecordCodecBuilder.create((var0) -> var0.group(ResourceLocation.CODEC.fieldOf("name").forGetter(Named::name), MethodInfo.typedCodec().forGetter(Named::contents)).apply(var0, Named::new));
      }
   }
}
