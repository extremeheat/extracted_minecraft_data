package net.minecraft.server.jsonrpc;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.jsonrpc.api.MethodInfo;
import net.minecraft.server.jsonrpc.api.ParamInfo;
import net.minecraft.server.jsonrpc.api.ResultInfo;

public interface OutgoingRpcMethod<Params, Result> {
   String NOTIFICATION_PREFIX = "notification/";

   MethodInfo info();

   Attributes attributes();

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
      return new OutgoingRpcMethodBuilder<Notification<Params>>((var1, var2) -> new Notification(var1, var2, var0));
   }

   static <Result> OutgoingRpcMethodBuilder<ParameterlessMethod<Result>> request(Codec<Result> var0) {
      return new OutgoingRpcMethodBuilder<ParameterlessMethod<Result>>((var1, var2) -> new ParameterlessMethod(var1, var2, var0));
   }

   static <Params, Result> OutgoingRpcMethodBuilder<Method<Params, Result>> request(Codec<Params> var0, Codec<Result> var1) {
      return new OutgoingRpcMethodBuilder<Method<Params, Result>>((var2, var3) -> new Method(var2, var3, var0, var1));
   }

   public static record Attributes(boolean discoverable) {
      public Attributes(boolean var1) {
         super();
         this.discoverable = var1;
      }
   }

   public static record ParmeterlessNotification(MethodInfo info, Attributes attributes) implements OutgoingRpcMethod<Void, Void> {
      public ParmeterlessNotification(MethodInfo var1, Attributes var2) {
         super();
         this.info = var1;
         this.attributes = var2;
      }
   }

   public static record Notification<Params>(MethodInfo info, Attributes attributes, Codec<Params> paramsCodec) implements OutgoingRpcMethod<Params, Void> {
      public Notification(MethodInfo var1, Attributes var2, Codec<Params> var3) {
         super();
         this.info = var1;
         this.attributes = var2;
         this.paramsCodec = var3;
      }

      @Nullable
      public JsonElement encodeParams(Params var1) {
         return (JsonElement)this.paramsCodec.encodeStart(JsonOps.INSTANCE, var1).getOrThrow();
      }
   }

   public static record ParameterlessMethod<Result>(MethodInfo info, Attributes attributes, Codec<Result> resultCodec) implements OutgoingRpcMethod<Void, Result> {
      public ParameterlessMethod(MethodInfo var1, Attributes var2, Codec<Result> var3) {
         super();
         this.info = var1;
         this.attributes = var2;
         this.resultCodec = var3;
      }

      public Result decodeResult(JsonElement var1) {
         return (Result)this.resultCodec.parse(JsonOps.INSTANCE, var1).getOrThrow();
      }
   }

   public static record Method<Params, Result>(MethodInfo info, Attributes attributes, Codec<Params> paramsCodec, Codec<Result> resultCodec) implements OutgoingRpcMethod<Params, Result> {
      public Method(MethodInfo var1, Attributes var2, Codec<Params> var3, Codec<Result> var4) {
         super();
         this.info = var1;
         this.attributes = var2;
         this.paramsCodec = var3;
         this.resultCodec = var4;
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
      public static final Attributes DEFAULT_ATTRIBUTES = new Attributes(true);
      private final Factory<T> method;
      private String description = "";
      @Nullable
      private ParamInfo paramInfo;
      @Nullable
      private ResultInfo resultInfo;

      public OutgoingRpcMethodBuilder(Factory<T> var1) {
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

      private T build() {
         MethodInfo var1 = new MethodInfo(this.description, this.paramInfo, this.resultInfo);
         return this.method.create(var1, DEFAULT_ATTRIBUTES);
      }

      public Holder.Reference<T> register(String var1) {
         return this.register(ResourceLocation.withDefaultNamespace("notification/" + var1));
      }

      private Holder.Reference<T> register(ResourceLocation var1) {
         return Registry.registerForHolder(BuiltInRegistries.OUTGOING_RPC_METHOD, var1, this.build());
      }
   }

   @FunctionalInterface
   public interface Factory<T extends OutgoingRpcMethod<?, ?>> {
      T create(MethodInfo var1, Attributes var2);
   }
}
