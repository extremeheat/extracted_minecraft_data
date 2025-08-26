package net.minecraft.server.jsonrpc;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.jsonrpc.api.MethodInfo;
import net.minecraft.server.jsonrpc.api.ParamInfo;
import net.minecraft.server.jsonrpc.api.ResultInfo;

public interface OutgoingRpcMethod<Params, Result> {
   MethodInfo methodInfo();

   @Nullable
   default JsonElement encodeParams(Params var1) {
      return null;
   }

   @Nullable
   default Result decodeResult(JsonElement var1) {
      return null;
   }

   static OutgoingRpcMethodBuilder<ParmeterlessNotification> notification() {
      return new OutgoingRpcMethodBuilder<ParmeterlessNotification>(ParmeterlessNotification::new);
   }

   static <Params> OutgoingRpcMethodBuilder<Notification<Params>> notification(Codec<Params> var0) {
      return new OutgoingRpcMethodBuilder<Notification<Params>>((var1) -> new Notification(var1, var0));
   }

   static <Result> OutgoingRpcMethodBuilder<ParameterlessMethod<Result>> request(Codec<Result> var0) {
      return new OutgoingRpcMethodBuilder<ParameterlessMethod<Result>>((var1) -> new ParameterlessMethod(var1, var0));
   }

   static <Params, Result> OutgoingRpcMethodBuilder<Method<Params, Result>> request(Codec<Params> var0, Codec<Result> var1) {
      return new OutgoingRpcMethodBuilder<Method<Params, Result>>((var2) -> new Method(var2, var0, var1));
   }

   public static record ParmeterlessNotification(MethodInfo methodInfo) implements OutgoingRpcMethod<Void, Void> {
      public ParmeterlessNotification(MethodInfo var1) {
         super();
         this.methodInfo = var1;
      }
   }

   public static record Notification<Params>(MethodInfo methodInfo, Codec<Params> paramsCodec) implements OutgoingRpcMethod<Params, Void> {
      public Notification(MethodInfo var1, Codec<Params> var2) {
         super();
         this.methodInfo = var1;
         this.paramsCodec = var2;
      }

      @Nullable
      public JsonElement encodeParams(Params var1) {
         return (JsonElement)this.paramsCodec.encodeStart(JsonOps.INSTANCE, var1).getOrThrow();
      }
   }

   public static record ParameterlessMethod<Result>(MethodInfo methodInfo, Codec<Result> resultCodec) implements OutgoingRpcMethod<Void, Result> {
      public ParameterlessMethod(MethodInfo var1, Codec<Result> var2) {
         super();
         this.methodInfo = var1;
         this.resultCodec = var2;
      }

      public Result decodeResult(JsonElement var1) {
         return (Result)this.resultCodec.parse(JsonOps.INSTANCE, var1).getOrThrow();
      }
   }

   public static record Method<Params, Result>(MethodInfo methodInfo, Codec<Params> paramsCodec, Codec<Result> resultCodec) implements OutgoingRpcMethod<Params, Result> {
      public Method(MethodInfo var1, Codec<Params> var2, Codec<Result> var3) {
         super();
         this.methodInfo = var1;
         this.paramsCodec = var2;
         this.resultCodec = var3;
      }

      @Nullable
      public JsonElement encodeParams(Params var1) {
         return (JsonElement)this.paramsCodec.encodeStart(JsonOps.INSTANCE, var1).getOrThrow();
      }

      public Result decodeResult(JsonElement var1) {
         return (Result)this.resultCodec.parse(JsonOps.INSTANCE, var1).getOrThrow();
      }
   }

   public static class OutgoingRpcMethodBuilder<T extends OutgoingRpcMethod<?, ?>> {
      private final Function<MethodInfo, T> method;
      private String description = "";
      @Nullable
      private ParamInfo paramInfo;
      @Nullable
      private ResultInfo resultInfo;

      public OutgoingRpcMethodBuilder(Function<MethodInfo, T> var1) {
         super();
         this.method = var1;
      }

      public OutgoingRpcMethodBuilder<T> description(String var1) {
         this.description = var1;
         return this;
      }

      public OutgoingRpcMethodBuilder<T> response(ResultInfo var1) {
         this.resultInfo = var1;
         return this;
      }

      public OutgoingRpcMethodBuilder<T> param(ParamInfo var1) {
         this.paramInfo = var1;
         return this;
      }

      private T build(String var1, String var2) {
         MethodInfo var3 = new MethodInfo(ResourceLocation.fromNamespaceAndPath(var1, var2), this.description, true, true);
         if (this.paramInfo != null) {
            var3 = var3.withParam(this.paramInfo);
         }

         if (this.resultInfo != null) {
            var3 = var3.withResult(this.resultInfo);
         }

         return (T)(this.method.apply(var3));
      }

      public T register(String var1, String var2) {
         ResourceLocation var3 = ResourceLocation.fromNamespaceAndPath(var1, var2);
         return (T)(Registry.register(BuiltInRegistries.OUTGOING_RPC_METHOD, (ResourceLocation)var3, this.build(var1, var2)));
      }
   }
}
