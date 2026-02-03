package net.minecraft.server.jsonrpc;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import java.util.Locale;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
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

   JsonElement apply(MinecraftApi minecraftApi, @Nullable JsonElement paramsJson, ClientInfo clientInfo);

   static <Result> IncomingRpcMethodBuilder<Void, Result> method(final ParameterlessRpcMethodFunction<Result> function) {
      return new IncomingRpcMethodBuilder<Void, Result>(function);
   }

   static <Params, Result> IncomingRpcMethodBuilder<Params, Result> method(final RpcMethodFunction<Params, Result> function) {
      return new IncomingRpcMethodBuilder<Params, Result>(function);
   }

   static <Result> IncomingRpcMethodBuilder<Void, Result> method(final Function<MinecraftApi, Result> supplier) {
      return new IncomingRpcMethodBuilder<Void, Result>(supplier);
   }

   public static record Attributes(boolean runOnMainThread, boolean discoverable) {
      public Attributes {
         super();
      }
   }

   public static record ParameterlessMethod<Params, Result>(MethodInfo<Params, Result> info, Attributes attributes, ParameterlessRpcMethodFunction<Result> supplier) implements IncomingRpcMethod<Params, Result> {
      public ParameterlessMethod {
         super();
      }

      public JsonElement apply(final MinecraftApi minecraftApi, final @Nullable JsonElement paramsJson, final ClientInfo clientInfo) {
         if (paramsJson == null || paramsJson.isJsonArray() && paramsJson.getAsJsonArray().isEmpty()) {
            if (this.info.params().isPresent()) {
               throw new IllegalArgumentException("Parameterless method unexpectedly has parameter description");
            } else {
               Result result = this.supplier.apply(minecraftApi, clientInfo);
               if (this.info.result().isEmpty()) {
                  throw new IllegalStateException("No result codec defined");
               } else {
                  return (JsonElement)((ResultInfo)this.info.result().get()).schema().codec().encodeStart(JsonOps.INSTANCE, result).getOrThrow(InvalidParameterJsonRpcException::new);
               }
            }
         } else {
            throw new InvalidParameterJsonRpcException("Expected no params, or an empty array");
         }
      }
   }

   public static record Method<Params, Result>(MethodInfo<Params, Result> info, Attributes attributes, RpcMethodFunction<Params, Result> function) implements IncomingRpcMethod<Params, Result> {
      public Method {
         super();
      }

      public JsonElement apply(final MinecraftApi minecraftApi, final @Nullable JsonElement paramsJson, final ClientInfo clientInfo) {
         if (paramsJson != null && (paramsJson.isJsonArray() || paramsJson.isJsonObject())) {
            if (this.info.params().isEmpty()) {
               throw new IllegalArgumentException("Method defined as having parameters without describing them");
            } else {
               JsonElement paramsJsonElement;
               if (paramsJson.isJsonObject()) {
                  String parameterName = ((ParamInfo)this.info.params().get()).name();
                  JsonElement jsonElement = paramsJson.getAsJsonObject().get(parameterName);
                  if (jsonElement == null) {
                     throw new InvalidParameterJsonRpcException(String.format(Locale.ROOT, "Params passed by-name, but expected param [%s] does not exist", parameterName));
                  }

                  paramsJsonElement = jsonElement;
               } else {
                  JsonArray jsonArray = paramsJson.getAsJsonArray();
                  if (jsonArray.isEmpty() || jsonArray.size() > 1) {
                     throw new InvalidParameterJsonRpcException("Expected exactly one element in the params array");
                  }

                  paramsJsonElement = jsonArray.get(0);
               }

               Params params = (Params)((ParamInfo)this.info.params().get()).schema().codec().parse(JsonOps.INSTANCE, paramsJsonElement).getOrThrow(InvalidParameterJsonRpcException::new);
               Result result = this.function.apply(minecraftApi, params, clientInfo);
               if (this.info.result().isEmpty()) {
                  throw new IllegalStateException("No result codec defined");
               } else {
                  return (JsonElement)((ResultInfo)this.info.result().get()).schema().codec().encodeStart(JsonOps.INSTANCE, result).getOrThrow(EncodeJsonRpcException::new);
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

      public IncomingRpcMethodBuilder(final ParameterlessRpcMethodFunction<Result> function) {
         super();
         this.parameterlessFunction = function;
      }

      public IncomingRpcMethodBuilder(final RpcMethodFunction<Params, Result> function) {
         super();
         this.parameterFunction = function;
      }

      public IncomingRpcMethodBuilder(final Function<MinecraftApi, Result> supplier) {
         super();
         this.parameterlessFunction = (apiService, clientInfo) -> supplier.apply(apiService);
      }

      public IncomingRpcMethodBuilder<Params, Result> description(final String description) {
         this.description = description;
         return this;
      }

      public IncomingRpcMethodBuilder<Params, Result> response(final String resultName, final Schema<Result> resultSchema) {
         this.resultInfo = new ResultInfo<Result>(resultName, resultSchema.info());
         return this;
      }

      public IncomingRpcMethodBuilder<Params, Result> param(final String paramName, final Schema<Params> paramSchema) {
         this.paramInfo = new ParamInfo<Params>(paramName, paramSchema.info());
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
            Attributes attributes = new Attributes(this.runOnMainThread, this.discoverable);
            MethodInfo<Params, Result> methodInfo = new MethodInfo<Params, Result>(this.description, this.paramInfo, this.resultInfo);
            if (this.parameterlessFunction != null) {
               return new ParameterlessMethod<Params, Result>(methodInfo, attributes, this.parameterlessFunction);
            } else if (this.parameterFunction != null) {
               if (this.paramInfo == null) {
                  throw new IllegalStateException("No param schema defined");
               } else {
                  return new Method<Params, Result>(methodInfo, attributes, this.parameterFunction);
               }
            } else {
               throw new IllegalStateException("No method defined");
            }
         }
      }

      public IncomingRpcMethod<?, ?> register(final Registry<IncomingRpcMethod<?, ?>> methodRegistry, final String key) {
         return this.register(methodRegistry, Identifier.withDefaultNamespace(key));
      }

      private IncomingRpcMethod<?, ?> register(final Registry<IncomingRpcMethod<?, ?>> methodRegistry, final Identifier id) {
         return (IncomingRpcMethod)Registry.register(methodRegistry, (Identifier)id, this.build());
      }
   }

   @FunctionalInterface
   public interface ParameterlessRpcMethodFunction<Result> {
      Result apply(MinecraftApi api, ClientInfo clientInfo);
   }

   @FunctionalInterface
   public interface RpcMethodFunction<Params, Result> {
      Result apply(MinecraftApi api, Params params, ClientInfo clientInfo);
   }
}
