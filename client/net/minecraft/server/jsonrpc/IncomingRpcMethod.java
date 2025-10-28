package net.minecraft.server.jsonrpc;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import java.util.Locale;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.jsonrpc.api.MethodInfo;
import net.minecraft.server.jsonrpc.api.ParamInfo;
import net.minecraft.server.jsonrpc.api.ResultInfo;
import net.minecraft.server.jsonrpc.api.Schema;
import net.minecraft.server.jsonrpc.internalapi.MinecraftApi;
import net.minecraft.server.jsonrpc.methods.ClientInfo;
import net.minecraft.server.jsonrpc.methods.EncodeJsonRpcException;
import net.minecraft.server.jsonrpc.methods.InvalidParameterJsonRpcException;
import org.jspecify.annotations.Nullable;

public interface IncomingRpcMethod<Params, Result> {
   MethodInfo<Params, Result> info();

   Attributes attributes();

   JsonElement apply(MinecraftApi var1, @Nullable JsonElement var2, ClientInfo var3);

   static <Result> IncomingRpcMethodBuilder<Void, Result> method(ParameterlessRpcMethodFunction<Result> var0) {
      return new IncomingRpcMethodBuilder<Void, Result>(var0);
   }

   static <Params, Result> IncomingRpcMethodBuilder<Params, Result> method(RpcMethodFunction<Params, Result> var0) {
      return new IncomingRpcMethodBuilder<Params, Result>(var0);
   }

   static <Result> IncomingRpcMethodBuilder<Void, Result> method(Function<MinecraftApi, Result> var0) {
      return new IncomingRpcMethodBuilder<Void, Result>(var0);
   }

   public static record Attributes(boolean runOnMainThread, boolean discoverable) {
      public Attributes(boolean var1, boolean var2) {
         super();
         this.runOnMainThread = var1;
         this.discoverable = var2;
      }
   }

   public static record ParameterlessMethod<Params, Result>(MethodInfo<Params, Result> info, Attributes attributes, ParameterlessRpcMethodFunction<Result> supplier) implements IncomingRpcMethod<Params, Result> {
      public ParameterlessMethod(MethodInfo<Params, Result> var1, Attributes var2, ParameterlessRpcMethodFunction<Result> var3) {
         super();
         this.info = var1;
         this.attributes = var2;
         this.supplier = var3;
      }

      public JsonElement apply(MinecraftApi var1, @Nullable JsonElement var2, ClientInfo var3) {
         if (var2 == null || var2.isJsonArray() && var2.getAsJsonArray().isEmpty()) {
            if (this.info.params().isPresent()) {
               throw new IllegalArgumentException("Parameterless method unexpectedly has parameter description");
            } else {
               Object var4 = this.supplier.apply(var1, var3);
               if (this.info.result().isEmpty()) {
                  throw new IllegalStateException("No result codec defined");
               } else {
                  return (JsonElement)((ResultInfo)this.info.result().get()).schema().codec().encodeStart(JsonOps.INSTANCE, var4).getOrThrow(InvalidParameterJsonRpcException::new);
               }
            }
         } else {
            throw new InvalidParameterJsonRpcException("Expected no params, or an empty array");
         }
      }
   }

   public static record Method<Params, Result>(MethodInfo<Params, Result> info, Attributes attributes, RpcMethodFunction<Params, Result> function) implements IncomingRpcMethod<Params, Result> {
      public Method(MethodInfo<Params, Result> var1, Attributes var2, RpcMethodFunction<Params, Result> var3) {
         super();
         this.info = var1;
         this.attributes = var2;
         this.function = var3;
      }

      public JsonElement apply(MinecraftApi var1, @Nullable JsonElement var2, ClientInfo var3) {
         if (var2 != null && (var2.isJsonArray() || var2.isJsonObject())) {
            if (this.info.params().isEmpty()) {
               throw new IllegalArgumentException("Method defined as having parameters without describing them");
            } else {
               JsonElement var4;
               if (var2.isJsonObject()) {
                  String var5 = ((ParamInfo)this.info.params().get()).name();
                  JsonElement var6 = var2.getAsJsonObject().get(var5);
                  if (var6 == null) {
                     throw new InvalidParameterJsonRpcException(String.format(Locale.ROOT, "Params passed by-name, but expected param [%s] does not exist", var5));
                  }

                  var4 = var6;
               } else {
                  JsonArray var7 = var2.getAsJsonArray();
                  if (var7.isEmpty() || var7.size() > 1) {
                     throw new InvalidParameterJsonRpcException("Expected exactly one element in the params array");
                  }

                  var4 = var7.get(0);
               }

               Object var8 = ((ParamInfo)this.info.params().get()).schema().codec().parse(JsonOps.INSTANCE, var4).getOrThrow(InvalidParameterJsonRpcException::new);
               Object var9 = this.function.apply(var1, var8, var3);
               if (this.info.result().isEmpty()) {
                  throw new IllegalStateException("No result codec defined");
               } else {
                  return (JsonElement)((ResultInfo)this.info.result().get()).schema().codec().encodeStart(JsonOps.INSTANCE, var9).getOrThrow(EncodeJsonRpcException::new);
               }
            }
         } else {
            throw new InvalidParameterJsonRpcException("Expected params as array or named");
         }
      }
   }

   public static class IncomingRpcMethodBuilder<Params, Result> {
      private String description = "";
      private @Nullable ParamInfo<Params> paramInfo;
      private @Nullable ResultInfo<Result> resultInfo;
      private boolean discoverable = true;
      private boolean runOnMainThread = true;
      private @Nullable ParameterlessRpcMethodFunction<Result> parameterlessFunction;
      private @Nullable RpcMethodFunction<Params, Result> parameterFunction;

      public IncomingRpcMethodBuilder(ParameterlessRpcMethodFunction<Result> var1) {
         super();
         this.parameterlessFunction = var1;
      }

      public IncomingRpcMethodBuilder(RpcMethodFunction<Params, Result> var1) {
         super();
         this.parameterFunction = var1;
      }

      public IncomingRpcMethodBuilder(Function<MinecraftApi, Result> var1) {
         super();
         this.parameterlessFunction = (var1x, var2) -> var1.apply(var1x);
      }

      public IncomingRpcMethodBuilder<Params, Result> description(String var1) {
         this.description = var1;
         return this;
      }

      public IncomingRpcMethodBuilder<Params, Result> response(String var1, Schema<Result> var2) {
         this.resultInfo = new ResultInfo<Result>(var1, var2.info());
         return this;
      }

      public IncomingRpcMethodBuilder<Params, Result> param(String var1, Schema<Params> var2) {
         this.paramInfo = new ParamInfo<Params>(var1, var2.info());
         return this;
      }

      public IncomingRpcMethodBuilder<Params, Result> undiscoverable() {
         this.discoverable = false;
         return this;
      }

      public IncomingRpcMethodBuilder<Params, Result> notOnMainThread() {
         this.runOnMainThread = false;
         return this;
      }

      public IncomingRpcMethod<Params, Result> build() {
         if (this.resultInfo == null) {
            throw new IllegalStateException("No response defined");
         } else {
            Attributes var1 = new Attributes(this.runOnMainThread, this.discoverable);
            MethodInfo var2 = new MethodInfo(this.description, this.paramInfo, this.resultInfo);
            if (this.parameterlessFunction != null) {
               return new ParameterlessMethod<Params, Result>(var2, var1, this.parameterlessFunction);
            } else if (this.parameterFunction != null) {
               if (this.paramInfo == null) {
                  throw new IllegalStateException("No param schema defined");
               } else {
                  return new Method<Params, Result>(var2, var1, this.parameterFunction);
               }
            } else {
               throw new IllegalStateException("No method defined");
            }
         }
      }

      public IncomingRpcMethod<?, ?> register(Registry<IncomingRpcMethod<?, ?>> var1, String var2) {
         return this.register(var1, ResourceLocation.withDefaultNamespace(var2));
      }

      private IncomingRpcMethod<?, ?> register(Registry<IncomingRpcMethod<?, ?>> var1, ResourceLocation var2) {
         return (IncomingRpcMethod)Registry.register(var1, (ResourceLocation)var2, this.build());
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
