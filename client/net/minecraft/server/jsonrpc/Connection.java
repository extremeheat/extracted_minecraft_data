package net.minecraft.server.jsonrpc;

import com.google.gson.JsonElement;
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
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.jsonrpc.internalapi.MinecraftApi;
import net.minecraft.server.jsonrpc.methods.ClientInfo;
import net.minecraft.server.jsonrpc.methods.EncodeJsonRpcException;
import net.minecraft.server.jsonrpc.methods.InvalidParameterJsonRpcException;
import net.minecraft.server.jsonrpc.methods.RemoteRpcErrorException;
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
            ((PendingRpcRequest)var2.getValue()).resultFuture().completeExceptionally(new ReadTimeoutException("RPC method " + String.valueOf(((PendingRpcRequest)var2.getValue()).method().methodInfo().name()) + " timed out waiting for response"));
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
         this.channel.writeAndFlush(JsonRPCErrors.PARSE_ERROR.create((Integer)null, var2.getMessage()));
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
      } else {
         this.channel.writeAndFlush(JsonRPCErrors.INVALID_REQUEST.create((Integer)null));
      }

   }

   public void sendNotification(OutgoingRpcMethod<Void, ?> var1) {
      this.sendRequest(var1, (Object)null, false);
   }

   public <Params> void sendNotification(OutgoingRpcMethod<Params, ?> var1, Params var2) {
      this.sendRequest(var1, var2, false);
   }

   public <Result> CompletableFuture<Result> sendRequest(OutgoingRpcMethod<Void, Result> var1) {
      return this.sendRequest(var1, (Object)null, true);
   }

   public <Params, Result> CompletableFuture<Result> sendRequest(OutgoingRpcMethod<Params, Result> var1, Params var2) {
      return this.sendRequest(var1, var2, true);
   }

   @Nullable
   @Contract("_,_,false->null;_,_,true->!null")
   private <Params, Result> CompletableFuture<Result> sendRequest(OutgoingRpcMethod<Params, Result> var1, @Nullable Params var2, boolean var3) {
      List var4 = var2 != null ? List.of((JsonElement)Objects.requireNonNull(var1.encodeParams(var2))) : List.of();
      String var5 = var1.methodInfo().name().toString();
      if (var3) {
         CompletableFuture var6 = new CompletableFuture();
         int var7 = this.transactionId.incrementAndGet();
         long var8 = Util.timeSource.get(TimeUnit.MILLISECONDS);
         this.pendingRequests.put(var7, new PendingRpcRequest(var1, var6, var8 + 5000L));
         this.channel.writeAndFlush(JsonRPCUtils.createRequest(var7, var5, var4));
         return var6;
      } else {
         this.channel.writeAndFlush(JsonRPCUtils.createRequest((Integer)null, var5, var4));
         return null;
      }
   }

   @Nullable
   private JsonObject handleJsonObject(JsonObject var1) {
      Integer var2 = JsonRPCUtils.getRequestId(var1);
      String var3 = JsonRPCUtils.getMethodName(var1);
      JsonElement var4 = JsonRPCUtils.getResult(var1);
      JsonElement var5 = JsonRPCUtils.getParams(var1);
      JsonObject var6 = JsonRPCUtils.getError(var1);
      if (var3 != null && var4 == null && var6 == null) {
         return this.handleIncomingRequest(var2, var3, var5);
      } else if (var3 == null && var4 != null && var6 == null && var2 != null) {
         this.handleRequestResponse(var2, var4);
         return null;
      } else {
         return var3 == null && var4 == null && var6 != null ? this.handleError(var2, var6) : JsonRPCErrors.INVALID_REQUEST.create(var2);
      }
   }

   @Nullable
   private JsonObject handleIncomingRequest(@Nullable Integer var1, String var2, @Nullable JsonElement var3) {
      try {
         JsonElement var4 = this.dispatchIncomingRequest(var1, var2, var3);
         return var4 != null && var1 != null ? JsonRPCUtils.createResult(var1, var4) : null;
      } catch (InvalidParameterJsonRpcException var5) {
         LOGGER.debug("Invalid parameter invocation {}: {}", var2, var5.getMessage());
         return JsonRPCErrors.INVALID_PARAMS.create(var1, var5.getMessage());
      } catch (EncodeJsonRpcException var6) {
         LOGGER.error("Failed to encode json rpc response {}: {}", var2, var6.getMessage());
         return JsonRPCErrors.INTERNAL_ERROR.create(var1, var6.getMessage());
      } catch (Exception var7) {
         LOGGER.error("Error while dispatching rpc method {}", var2, var7);
         return JsonRPCErrors.INTERNAL_ERROR.create(var1);
      }
   }

   @Nullable
   public JsonElement dispatchIncomingRequest(@Nullable Integer var1, String var2, @Nullable JsonElement var3) {
      ResourceLocation var4 = ResourceLocation.tryParse(var2);
      if (var4 == null) {
         return JsonRPCErrors.INVALID_REQUEST.create(var1, "Failed to parse method value: " + var2);
      } else {
         Optional var5 = BuiltInRegistries.INCOMING_RPC_METHOD.getOptional(var4);
         if (var5.isEmpty()) {
            return JsonRPCErrors.METHOD_NOT_FOUND.create(var1, "Method not found: " + var2);
         } else if (((IncomingRpcMethod)var5.get()).methodInfo().runOnMainThread()) {
            try {
               return (JsonElement)this.minecraftApi.submit((Supplier)(() -> ((IncomingRpcMethod)var5.get()).apply(this.minecraftApi, var3, this.clientInfo))).join();
            } catch (CompletionException var9) {
               Throwable var8 = var9.getCause();
               if (var8 instanceof RuntimeException) {
                  RuntimeException var7 = (RuntimeException)var8;
                  throw var7;
               } else {
                  throw var9;
               }
            }
         } else {
            return ((IncomingRpcMethod)var5.get()).apply(this.minecraftApi, var3, this.clientInfo);
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
   private JsonObject handleError(@Nullable Integer var1, JsonObject var2) {
      if (var1 != null) {
         PendingRpcRequest var3 = (PendingRpcRequest)this.pendingRequests.remove(var1);
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
