package net.minecraft.server.jsonrpc;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.jsonrpc.api.MethodInfo;
import net.minecraft.server.jsonrpc.api.ParamInfo;
import net.minecraft.server.jsonrpc.api.ResultInfo;
import net.minecraft.server.jsonrpc.api.Schema;
import org.jspecify.annotations.Nullable;

public interface OutgoingRpcMethod<Params, Result> {
   String NOTIFICATION_PREFIX = "notification/";

   MethodInfo<Params, Result> info();

   Attributes attributes();

   default @Nullable JsonElement encodeParams(Params var1) {
      return null;
   }

   default @Nullable Result decodeResult(JsonElement var1) {
      return null;
   }

   static OutgoingRpcMethodBuilder<Void, Void> notification() {
      return new OutgoingRpcMethodBuilder<Void, Void>(ParmeterlessNotification::new);
   }

   static <Params> OutgoingRpcMethodBuilder<Params, Void> notificationWithParams() {
      return new OutgoingRpcMethodBuilder<Params, Void>(Notification::new);
   }

   static <Result> OutgoingRpcMethodBuilder<Void, Result> request() {
      return new OutgoingRpcMethodBuilder<Void, Result>(ParameterlessMethod::new);
   }

   static <Params, Result> OutgoingRpcMethodBuilder<Params, Result> requestWithParams() {
      return new OutgoingRpcMethodBuilder<Params, Result>(Method::new);
   }

   public static record Attributes(boolean discoverable) {
      public Attributes(boolean var1) {
         super();
         this.discoverable = var1;
      }
   }

   public static record ParmeterlessNotification(MethodInfo<Void, Void> info, Attributes attributes) implements OutgoingRpcMethod<Void, Void> {
      public ParmeterlessNotification(MethodInfo<Void, Void> var1, Attributes var2) {
         super();
         this.info = var1;
         this.attributes = var2;
      }
   }

   public static record Notification<Params>(MethodInfo<Params, Void> info, Attributes attributes) implements OutgoingRpcMethod<Params, Void> {
      public Notification(MethodInfo<Params, Void> var1, Attributes var2) {
         super();
         this.info = var1;
         this.attributes = var2;
      }

      public @Nullable JsonElement encodeParams(Params var1) {
         if (this.info.params().isEmpty()) {
            throw new IllegalStateException("Method defined as having no parameters");
         } else {
            return (JsonElement)((ParamInfo)this.info.params().get()).schema().codec().encodeStart(JsonOps.INSTANCE, var1).getOrThrow();
         }
      }
   }

   public static record ParameterlessMethod<Result>(MethodInfo<Void, Result> info, Attributes attributes) implements OutgoingRpcMethod<Void, Result> {
      public ParameterlessMethod(MethodInfo<Void, Result> var1, Attributes var2) {
         super();
         this.info = var1;
         this.attributes = var2;
      }

      public Result decodeResult(JsonElement var1) {
         if (this.info.result().isEmpty()) {
            throw new IllegalStateException("Method defined as having no result");
         } else {
            return (Result)((ResultInfo)this.info.result().get()).schema().codec().parse(JsonOps.INSTANCE, var1).getOrThrow();
         }
      }
   }

   public static record Method<Params, Result>(MethodInfo<Params, Result> info, Attributes attributes) implements OutgoingRpcMethod<Params, Result> {
      public Method(MethodInfo<Params, Result> var1, Attributes var2) {
         super();
         this.info = var1;
         this.attributes = var2;
      }

      public @Nullable JsonElement encodeParams(Params var1) {
         if (this.info.params().isEmpty()) {
            throw new IllegalStateException("Method defined as having no parameters");
         } else {
            return (JsonElement)((ParamInfo)this.info.params().get()).schema().codec().encodeStart(JsonOps.INSTANCE, var1).getOrThrow();
         }
      }

      public Result decodeResult(JsonElement var1) {
         if (this.info.result().isEmpty()) {
            throw new IllegalStateException("Method defined as having no result");
         } else {
            return (Result)((ResultInfo)this.info.result().get()).schema().codec().parse(JsonOps.INSTANCE, var1).getOrThrow();
         }
      }
   }

   public static class OutgoingRpcMethodBuilder<Params, Result> {
      public static final Attributes DEFAULT_ATTRIBUTES = new Attributes(true);
      private final Factory<Params, Result> method;
      private String description = "";
      private @Nullable ParamInfo<Params> paramInfo;
      private @Nullable ResultInfo<Result> resultInfo;

      public OutgoingRpcMethodBuilder(Factory<Params, Result> var1) {
         super();
         this.method = var1;
      }

      public OutgoingRpcMethodBuilder<Params, Result> description(String var1) {
         this.description = var1;
         return this;
      }

      public OutgoingRpcMethodBuilder<Params, Result> response(String var1, Schema<Result> var2) {
         this.resultInfo = new ResultInfo<Result>(var1, var2);
         return this;
      }

      public OutgoingRpcMethodBuilder<Params, Result> param(String var1, Schema<Params> var2) {
         this.paramInfo = new ParamInfo<Params>(var1, var2);
         return this;
      }

      private OutgoingRpcMethod<Params, Result> build() {
         MethodInfo var1 = new MethodInfo(this.description, this.paramInfo, this.resultInfo);
         return this.method.create(var1, DEFAULT_ATTRIBUTES);
      }

      public Holder.Reference<OutgoingRpcMethod<Params, Result>> register(String var1) {
         return this.register(Identifier.withDefaultNamespace("notification/" + var1));
      }

      private Holder.Reference<OutgoingRpcMethod<Params, Result>> register(Identifier var1) {
         return Registry.registerForHolder(BuiltInRegistries.OUTGOING_RPC_METHOD, var1, this.build());
      }
   }

   @FunctionalInterface
   public interface Factory<Params, Result> {
      OutgoingRpcMethod<Params, Result> create(MethodInfo<Params, Result> var1, Attributes var2);
   }
}
