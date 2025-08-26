package net.minecraft.server.jsonrpc;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.jsonrpc.api.MethodInfo;
import net.minecraft.server.jsonrpc.api.ParamInfo;
import net.minecraft.server.jsonrpc.api.ResultInfo;
import net.minecraft.server.jsonrpc.internalapi.MinecraftApi;
import net.minecraft.server.jsonrpc.methods.ClientInfo;
import net.minecraft.server.jsonrpc.methods.EncodeJsonRpcException;
import net.minecraft.server.jsonrpc.methods.InvalidParameterJsonRpcException;

public interface IncomingRpcMethod {
   MethodInfo methodInfo();

   JsonElement apply(MinecraftApi var1, @Nullable JsonElement var2, ClientInfo var3);

   static <Result> IncomingRpcMethodBuilder<ParameterlessMethod<Result>> method(ParameterlessRpcMethodFunction<Result> var0, Codec<Result> var1) {
      return new IncomingRpcMethodBuilder<ParameterlessMethod<Result>>((var2) -> new ParameterlessMethod(var2, var1, var0));
   }

   static <Params, Result> IncomingRpcMethodBuilder<Method<Params, Result>> method(RpcMethodFunction<Params, Result> var0, Codec<Params> var1, Codec<Result> var2) {
      return new IncomingRpcMethodBuilder<Method<Params, Result>>((var3) -> new Method(var3, var1, var2, var0));
   }

   static <Result> IncomingRpcMethodBuilder<ParameterlessMethod<Result>> method(Function<MinecraftApi, Result> var0, Codec<Result> var1) {
      return new IncomingRpcMethodBuilder<ParameterlessMethod<Result>>((var2) -> new ParameterlessMethod(var2, var1, (var1x, var2x) -> var0.apply(var1x)));
   }

   public static record ParameterlessMethod<Result>(MethodInfo methodInfo, Codec<Result> resultCodec, ParameterlessRpcMethodFunction<Result> supplier) implements IncomingRpcMethod {
      public ParameterlessMethod(MethodInfo var1, Codec<Result> var2, ParameterlessRpcMethodFunction<Result> var3) {
         super();
         this.methodInfo = var1;
         this.resultCodec = var2;
         this.supplier = var3;
      }

      public JsonElement apply(MinecraftApi var1, @Nullable JsonElement var2, ClientInfo var3) {
         if (var2 == null || var2.isJsonArray() && var2.getAsJsonArray().isEmpty()) {
            Object var4 = this.supplier.apply(var1, var3);
            return (JsonElement)this.resultCodec.encodeStart(JsonOps.INSTANCE, var4).getOrThrow(InvalidParameterJsonRpcException::new);
         } else {
            throw new InvalidParameterJsonRpcException("Expected no params, or an empty array");
         }
      }
   }

   public static record Method<Params, Result>(MethodInfo methodInfo, Codec<Params> paramsCodec, Codec<Result> resultCodec, RpcMethodFunction<Params, Result> function) implements IncomingRpcMethod {
      public Method(MethodInfo var1, Codec<Params> var2, Codec<Result> var3, RpcMethodFunction<Params, Result> var4) {
         super();
         this.methodInfo = var1;
         this.paramsCodec = var2;
         this.resultCodec = var3;
         this.function = var4;
      }

      public JsonElement apply(MinecraftApi var1, @Nullable JsonElement var2, ClientInfo var3) {
         if (var2 != null && var2.isJsonArray()) {
            JsonArray var4 = var2.getAsJsonArray();
            if (!var4.isEmpty() && var4.size() <= 1) {
               Object var5 = this.paramsCodec.parse(JsonOps.INSTANCE, var4.get(0)).getOrThrow(InvalidParameterJsonRpcException::new);
               Object var6 = this.function.apply(var1, var5, var3);
               return (JsonElement)this.resultCodec.encodeStart(JsonOps.INSTANCE, var6).getOrThrow(EncodeJsonRpcException::new);
            } else {
               throw new InvalidParameterJsonRpcException("Expected exactly one element in the params array");
            }
         } else {
            throw new InvalidParameterJsonRpcException("Expected params array with exactly one element");
         }
      }
   }

   public static class IncomingRpcMethodBuilder<T extends IncomingRpcMethod> {
      private final Function<MethodInfo, T> method;
      private String description = "";
      @Nullable
      private ParamInfo paramInfo;
      @Nullable
      private ResultInfo resultInfo;
      private boolean discoverable = true;
      private boolean runOnMainThread = true;

      public IncomingRpcMethodBuilder(Function<MethodInfo, T> var1) {
         super();
         this.method = var1;
      }

      public IncomingRpcMethodBuilder<T> description(String var1) {
         this.description = var1;
         return this;
      }

      public IncomingRpcMethodBuilder<T> response(ResultInfo var1) {
         this.resultInfo = var1;
         return this;
      }

      public IncomingRpcMethodBuilder<T> param(ParamInfo var1) {
         this.paramInfo = var1;
         return this;
      }

      public IncomingRpcMethodBuilder<T> undiscoverable() {
         this.discoverable = false;
         return this;
      }

      public IncomingRpcMethodBuilder<T> notOnMainThread() {
         this.runOnMainThread = false;
         return this;
      }

      public T build(String var1, String var2) {
         MethodInfo var3 = new MethodInfo(ResourceLocation.fromNamespaceAndPath(var1, var2), this.description, this.runOnMainThread, this.discoverable);
         if (this.paramInfo != null) {
            var3 = var3.withParam(this.paramInfo);
         }

         if (this.resultInfo != null) {
            var3 = var3.withResult(this.resultInfo);
         }

         return (T)(this.method.apply(var3));
      }

      public IncomingRpcMethod register(Registry<IncomingRpcMethod> var1, String var2, String var3) {
         ResourceLocation var4 = ResourceLocation.withDefaultNamespace(var3);
         return (IncomingRpcMethod)Registry.register(var1, (ResourceLocation)var4, this.build(var2, var3));
      }
   }

   @FunctionalInterface
   public interface ParameterlessRpcMethodFunction<Result> {
      Result apply(MinecraftApi var1, ClientInfo var2);
   }

   @FunctionalInterface
   public interface RpcMethodFunction<Params, Result> {
      Result apply(MinecraftApi var1, Params var2, ClientInfo var3);
   }
}
