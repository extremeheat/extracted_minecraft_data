package net.minecraft.server.jsonrpc.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public record MethodInfo<Params, Result>(String description, Optional<ParamInfo<Params>> params, Optional<ResultInfo<Result>> result) {
   public MethodInfo(final String description, final @Nullable ParamInfo<Params> paramInfo, final @Nullable ResultInfo<Result> resultInfo) {
      this(description, Optional.ofNullable(paramInfo), Optional.ofNullable(resultInfo));
   }

   public MethodInfo {
      super();
   }

   private static <Params> Optional<ParamInfo<Params>> toOptional(final List<ParamInfo<Params>> list) {
      return list.isEmpty() ? Optional.empty() : Optional.of((ParamInfo)list.getFirst());
   }

   private static <Params> List<ParamInfo<Params>> toList(final Optional<ParamInfo<Params>> opt) {
      return opt.isPresent() ? List.of((ParamInfo)opt.get()) : List.of();
   }

   private static <Params> Codec<Optional<ParamInfo<Params>>> paramsTypedCodec() {
      return ParamInfo.typedCodec().codec().listOf().xmap(MethodInfo::toOptional, MethodInfo::toList);
   }

   private static <Params, Result> MapCodec<MethodInfo<Params, Result>> typedCodec() {
      return RecordCodecBuilder.mapCodec((i) -> i.group(Codec.STRING.fieldOf("description").forGetter(MethodInfo::description), paramsTypedCodec().fieldOf("params").forGetter(MethodInfo::params), ResultInfo.typedCodec().optionalFieldOf("result").forGetter(MethodInfo::result)).apply(i, MethodInfo::new));
   }

   public Named<Params, Result> named(final Identifier name) {
      return new Named<Params, Result>(name, this);
   }

   public static record Named<Params, Result>(Identifier name, MethodInfo<Params, Result> contents) {
      public static final Codec<Named<?, ?>> CODEC = typedCodec();

      public Named {
         super();
      }

      public static <Params, Result> Codec<Named<Params, Result>> typedCodec() {
         return RecordCodecBuilder.create((i) -> i.group(Identifier.CODEC.fieldOf("name").forGetter(Named::name), MethodInfo.typedCodec().forGetter(Named::contents)).apply(i, Named::new));
      }
   }
}
