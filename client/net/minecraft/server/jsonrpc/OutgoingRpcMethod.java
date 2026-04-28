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

   default @Nullable JsonElement encodeParams(final Params params) {
      return null;
   }

   default @Nullable Result decodeResult(final JsonElement result) {
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

   public static record Attributes(boolean discoverable, boolean allowPreServerInit) {
      public Attributes {
         super();
      }
   }

   public static record ParmeterlessNotification(MethodInfo<Void, Void> info, Attributes attributes) implements OutgoingRpcMethod<Void, Void> {
      public ParmeterlessNotification {
         super();
      }
   }

   public static record Notification<Params>(MethodInfo<Params, Void> info, Attributes attributes) implements OutgoingRpcMethod<Params, Void> {
      public Notification {
         super();
      }

      public @Nullable JsonElement encodeParams(final Params params) {
         if (this.info.params().isEmpty()) {
            throw new IllegalStateException("Method defined as having no parameters");
         } else {
            return (JsonElement)((ParamInfo)this.info.params().get()).schema().codec().encodeStart(JsonOps.INSTANCE, params).getOrThrow();
         }
      }
   }

   public static record ParameterlessMethod<Result>(MethodInfo<Void, Result> info, Attributes attributes) implements OutgoingRpcMethod<Void, Result> {
      public ParameterlessMethod {
         super();
      }

      public Result decodeResult(final JsonElement result) {
         if (this.info.result().isEmpty()) {
            throw new IllegalStateException("Method defined as having no result");
         } else {
            return (Result)((ResultInfo)this.info.result().get()).schema().codec().parse(JsonOps.INSTANCE, result).getOrThrow();
         }
      }
   }

   public static record Method<Params, Result>(MethodInfo<Params, Result> info, Attributes attributes) implements OutgoingRpcMethod<Params, Result> {
      public Method {
         super();
      }

      public @Nullable JsonElement encodeParams(final Params params) {
         if (this.info.params().isEmpty()) {
            throw new IllegalStateException("Method defined as having no parameters");
         } else {
            return (JsonElement)((ParamInfo)this.info.params().get()).schema().codec().encodeStart(JsonOps.INSTANCE, params).getOrThrow();
         }
      }

      public Result decodeResult(final JsonElement result) {
         if (this.info.result().isEmpty()) {
            throw new IllegalStateException("Method defined as having no result");
         } else {
            return (Result)((ResultInfo)this.info.result().get()).schema().codec().parse(JsonOps.INSTANCE, result).getOrThrow();
         }
      }
   }

   public static class OutgoingRpcMethodBuilder<Params, Result> {
      public static final Attributes DEFAULT_ATTRIBUTES = new Attributes(true, false);
      private final Factory<Params, Result> method;
      private String description = "";
      private @Nullable ParamInfo<Params> paramInfo;
      private @Nullable ResultInfo<Result> resultInfo;
      private boolean allowPreServerInit = false;

      public OutgoingRpcMethodBuilder(final Factory<Params, Result> method) {
         super();
         this.method = method;
      }

      public OutgoingRpcMethodBuilder<Params, Result> description(final String description) {
         this.description = description;
         return this;
      }

      public OutgoingRpcMethodBuilder<Params, Result> response(final String resultName, final Schema<Result> resultSchema) {
         this.resultInfo = new ResultInfo<Result>(resultName, resultSchema);
         return this;
      }

      public OutgoingRpcMethodBuilder<Params, Result> param(final String paramName, final Schema<Params> paramSchema) {
         this.paramInfo = new ParamInfo<Params>(paramName, paramSchema);
         return this;
      }

      public OutgoingRpcMethodBuilder<Params, Result> allowPreServerInit() {
         this.allowPreServerInit = true;
         return this;
      }

      private OutgoingRpcMethod<Params, Result> build() {
         MethodInfo<Params, Result> methodInfo = new MethodInfo<Params, Result>(this.description, this.paramInfo, this.resultInfo);
         Attributes attributes;
         if (this.allowPreServerInit) {
            attributes = new Attributes(DEFAULT_ATTRIBUTES.discoverable(), true);
         } else {
            attributes = DEFAULT_ATTRIBUTES;
         }

         return this.method.create(methodInfo, attributes);
      }

      public Holder.Reference<OutgoingRpcMethod<Params, Result>> register(final String key) {
         return this.register(Identifier.withDefaultNamespace("notification/" + key));
      }

      private Holder.Reference<OutgoingRpcMethod<Params, Result>> register(final Identifier id) {
         return Registry.registerForHolder(BuiltInRegistries.OUTGOING_RPC_METHOD, id, this.build());
      }
   }

   @FunctionalInterface
   public interface Factory<Params, Result> {
      OutgoingRpcMethod<Params, Result> create(MethodInfo<Params, Result> info, Attributes attributes);
   }
}
