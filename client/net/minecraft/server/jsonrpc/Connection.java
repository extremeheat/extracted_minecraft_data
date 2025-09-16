package net.minecraft.server.jsonrpc;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.ReadTimeoutException;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.jsonrpc.internalapi.MinecraftApi;
import net.minecraft.server.jsonrpc.methods.ClientInfo;
import net.minecraft.server.jsonrpc.methods.EncodeJsonRpcException;
import net.minecraft.server.jsonrpc.methods.InvalidParameterJsonRpcException;
import net.minecraft.server.jsonrpc.methods.InvalidRequestJsonRpcException;
import net.minecraft.server.jsonrpc.methods.MethodNotFoundJsonRpcException;
import net.minecraft.server.jsonrpc.methods.RemoteRpcErrorException;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;

public class Connection extends SimpleChannelInboundHandler<JsonElement> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final AtomicInteger CONNECTION_ID_COUNTER = new AtomicInteger(0);
   private final JsonRpcLogger jsonRpcLogger;
   private final ClientInfo clientInfo;
   private final ManagementServer managementServer;
   private final Channel channel;
   private final MinecraftApi minecraftApi;
   private final AtomicInteger transactionId = new AtomicInteger();
   private final Int2ObjectMap<PendingRpcRequest<?>> pendingRequests = Int2ObjectMaps.synchronize(new Int2ObjectOpenHashMap());

   public Connection(Channel var1, ManagementServer var2, MinecraftApi var3, JsonRpcLogger var4) {
      super();
      this.clientInfo = ClientInfo.of(CONNECTION_ID_COUNTER.incrementAndGet());
      this.managementServer = var2;
      this.minecraftApi = var3;
      this.channel = var1;
      this.jsonRpcLogger = var4;
   }

   public void tick() {
      long var1 = Util.getMillis();
      this.pendingRequests.int2ObjectEntrySet().removeIf((var2) -> {
         boolean var3 = ((PendingRpcRequest)var2.getValue()).timedOut(var1);
         if (var3) {
            ((PendingRpcRequest)var2.getValue()).resultFuture().completeExceptionally(new ReadTimeoutException("RPC method " + String.valueOf(((PendingRpcRequest)var2.getValue()).method().key().location()) + " timed out waiting for response"));
         }

         return var3;
      });
   }

   public void channelActive(ChannelHandlerContext var1) throws Exception {
      this.jsonRpcLogger.log(this.clientInfo, "Management connection opened for {}", this.channel.remoteAddress());
      super.channelActive(var1);
      this.managementServer.onConnected(this);
   }

   public void channelInactive(ChannelHandlerContext var1) throws Exception {
      this.jsonRpcLogger.log(this.clientInfo, "Management connection closed for {}", this.channel.remoteAddress());
      super.channelInactive(var1);
      this.managementServer.onDisconnected(this);
   }

   public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) throws Exception {
      if (var2.getCause() instanceof JsonParseException) {
         this.channel.writeAndFlush(JsonRPCErrors.PARSE_ERROR.createWithUnknownId(var2.getMessage()));
      } else {
         super.exceptionCaught(var1, var2);
         this.channel.close().awaitUninterruptibly();
      }
   }

   protected void channelRead0(ChannelHandlerContext var1, JsonElement var2) {
      if (var2.isJsonObject()) {
         JsonObject var3 = this.handleJsonObject(var2.getAsJsonObject());
         if (var3 != null) {
            this.channel.writeAndFlush(var3);
         }
      } else if (var2.isJsonArray()) {
         this.channel.writeAndFlush(this.handleBatchRequest(var2.getAsJsonArray().asList()));
      } else {
         this.channel.writeAndFlush(JsonRPCErrors.INVALID_REQUEST.createWithUnknownId((String)null));
      }

   }

   private JsonArray handleBatchRequest(List<JsonElement> var1) {
      JsonArray var2 = new JsonArray();
      Stream var10000 = var1.stream().map((var1x) -> this.handleJsonObject(var1x.getAsJsonObject())).filter(Objects::nonNull);
      Objects.requireNonNull(var2);
      var10000.forEach(var2::add);
      return var2;
   }

   public void sendNotification(Holder.Reference<? extends OutgoingRpcMethod<Void, ?>> var1) {
      this.sendRequest(var1, (Object)null, false);
   }

   public <Params> void sendNotification(Holder.Reference<? extends OutgoingRpcMethod<Params, ?>> var1, Params var2) {
      this.sendRequest(var1, var2, false);
   }

   public <Result> CompletableFuture<Result> sendRequest(Holder.Reference<? extends OutgoingRpcMethod<Void, Result>> var1) {
      return this.sendRequest(var1, (Object)null, true);
   }

   public <Params, Result> CompletableFuture<Result> sendRequest(Holder.Reference<? extends OutgoingRpcMethod<Params, Result>> var1, Params var2) {
      return this.sendRequest(var1, var2, true);
   }

   @Nullable
   @Contract("_,_,false->null;_,_,true->!null")
   private <Params, Result> CompletableFuture<Result> sendRequest(Holder.Reference<? extends OutgoingRpcMethod<Params, ? extends Result>> var1, @Nullable Params var2, boolean var3) {
      List var4 = var2 != null ? List.of((JsonElement)Objects.requireNonNull(((OutgoingRpcMethod)var1.value()).encodeParams(var2))) : List.of();
      if (var3) {
         CompletableFuture var5 = new CompletableFuture();
         int var6 = this.transactionId.incrementAndGet();
         long var7 = Util.timeSource.get(TimeUnit.MILLISECONDS);
         this.pendingRequests.put(var6, new PendingRpcRequest(var1, var5, var7 + 5000L));
         this.channel.writeAndFlush(JsonRPCUtils.createRequest(var6, var1.key().location(), var4));
         return var5;
      } else {
         this.channel.writeAndFlush(JsonRPCUtils.createRequest((Integer)null, var1.key().location(), var4));
         return null;
      }
   }

   @Nullable
   @VisibleForTesting
   JsonObject handleJsonObject(JsonObject var1) {
      try {
         JsonElement var2 = JsonRPCUtils.getRequestId(var1);
         String var3 = JsonRPCUtils.getMethodName(var1);
         JsonElement var4 = JsonRPCUtils.getResult(var1);
         JsonElement var5 = JsonRPCUtils.getParams(var1);
         JsonObject var6 = JsonRPCUtils.getError(var1);
         if (var3 != null && var4 == null && var6 == null) {
            return var2 != null && !isValidRequestId(var2) ? JsonRPCErrors.INVALID_REQUEST.createWithUnknownId("Invalid request id - only String, Number and NULL supported") : this.handleIncomingRequest(var2, var3, var5);
         } else if (var3 == null && var4 != null && var6 == null && var2 != null) {
            if (isValidResponseId(var2)) {
               this.handleRequestResponse(var2.getAsInt(), var4);
            } else {
               LOGGER.warn("Received respose {} with id {} we did not request", var4, var2);
            }

            return null;
         } else {
            return var3 == null && var4 == null && var6 != null ? this.handleError(var2, var6) : JsonRPCErrors.INVALID_REQUEST.createWithoutData((JsonElement)Objects.requireNonNullElse(var2, JsonNull.INSTANCE));
         }
      } catch (Exception var7) {
         LOGGER.error("Error while handling rpc request", var7);
         return JsonRPCErrors.INTERNAL_ERROR.createWithUnknownId("Unknown error handling request - check server logs for stack trace");
      }
   }

   private static boolean isValidRequestId(JsonElement var0) {
      return var0.isJsonNull() || GsonHelper.isNumberValue(var0) || GsonHelper.isStringValue(var0);
   }

   private static boolean isValidResponseId(JsonElement var0) {
      return GsonHelper.isNumberValue(var0);
   }

   @Nullable
   private JsonObject handleIncomingRequest(@Nullable JsonElement var1, String var2, @Nullable JsonElement var3) {
      boolean var4 = var1 != null;

      try {
         JsonElement var5 = this.dispatchIncomingRequest(var2, var3);
         return var5 != null && var4 ? JsonRPCUtils.createSuccessResult(var1, var5) : null;
      } catch (InvalidParameterJsonRpcException var6) {
         LOGGER.debug("Invalid parameter invocation {}: {}, {}", new Object[]{var2, var3, var6.getMessage()});
         return var4 ? JsonRPCErrors.INVALID_PARAMS.create(var1, var6.getMessage()) : null;
      } catch (EncodeJsonRpcException var7) {
         LOGGER.error("Failed to encode json rpc response {}: {}", var2, var7.getMessage());
         return var4 ? JsonRPCErrors.INTERNAL_ERROR.create(var1, var7.getMessage()) : null;
      } catch (InvalidRequestJsonRpcException var8) {
         return var4 ? JsonRPCErrors.INVALID_REQUEST.create(var1, var8.getMessage()) : null;
      } catch (MethodNotFoundJsonRpcException var9) {
         return var4 ? JsonRPCErrors.METHOD_NOT_FOUND.create(var1, var9.getMessage()) : null;
      } catch (Exception var10) {
         LOGGER.error("Error while dispatching rpc method {}", var2, var10);
         return var4 ? JsonRPCErrors.INTERNAL_ERROR.createWithoutData(var1) : null;
      }
   }

   @Nullable
   public JsonElement dispatchIncomingRequest(String var1, @Nullable JsonElement var2) {
      ResourceLocation var3 = ResourceLocation.tryParse(var1);
      if (var3 == null) {
         throw new InvalidRequestJsonRpcException("Failed to parse method value: " + var1);
      } else {
         Optional var4 = BuiltInRegistries.INCOMING_RPC_METHOD.getOptional(var3);
         if (var4.isEmpty()) {
            throw new MethodNotFoundJsonRpcException("Method not found: " + var1);
         } else if (((IncomingRpcMethod)var4.get()).attributes().runOnMainThread()) {
            try {
               return (JsonElement)this.minecraftApi.submit((Supplier)(() -> ((IncomingRpcMethod)var4.get()).apply(this.minecraftApi, var2, this.clientInfo))).join();
            } catch (CompletionException var8) {
               Throwable var7 = var8.getCause();
               if (var7 instanceof RuntimeException) {
                  RuntimeException var6 = (RuntimeException)var7;
                  throw var6;
               } else {
                  throw var8;
               }
            }
         } else {
            return ((IncomingRpcMethod)var4.get()).apply(this.minecraftApi, var2, this.clientInfo);
         }
      }
   }

   private void handleRequestResponse(int var1, JsonElement var2) {
      PendingRpcRequest var3 = (PendingRpcRequest)this.pendingRequests.remove(var1);
      if (var3 == null) {
         LOGGER.warn("Received unknown response (id: {}): {}", var1, var2);
      } else {
         var3.accept(var2);
      }

   }

   @Nullable
   private JsonObject handleError(@Nullable JsonElement var1, JsonObject var2) {
      if (var1 != null && isValidResponseId(var1)) {
         PendingRpcRequest var3 = (PendingRpcRequest)this.pendingRequests.remove(var1.getAsInt());
         if (var3 != null) {
            var3.resultFuture().completeExceptionally(new RemoteRpcErrorException(var1, var2));
         }
      }

      LOGGER.error("Received error (id: {}): {}", var1, var2);
      return null;
   }

   // $FF: synthetic method
   protected void channelRead0(final ChannelHandlerContext var1, final Object var2) throws Exception {
      this.channelRead0(var1, (JsonElement)var2);
   }
}
