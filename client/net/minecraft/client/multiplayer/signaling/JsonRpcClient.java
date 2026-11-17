package net.minecraft.client.multiplayer.signaling;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.mojang.logging.LogUtils;
import java.net.http.WebSocket;
import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
import net.minecraft.server.jsonrpc.JsonRPCErrors;
import net.minecraft.server.jsonrpc.JsonRPCUtils;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.StrictJsonParser;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

final class JsonRpcClient implements AutoCloseable, WebSocket.Listener {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Duration TIMEOUT = Duration.ofSeconds(15L);
   private final MethodHandler methodHandler;
   private final Runnable onConnect;
   private final Runnable onDisconnect;
   private final ScheduledExecutorService executor;
   private final StringBuilder messageBuffer = new StringBuilder();
   private final Map<Integer, CompletableFuture<JsonElement>> pendingRequests = new HashMap();
   private final Set<CompletableFuture<Void>> pendingSends = new HashSet();
   private @Nullable WebSocket webSocket;
   private CompletableFuture<Void> sendChain = CompletableFuture.completedFuture((Object)null);
   private int nextRequestId;
   private boolean closed;

   public JsonRpcClient(final ScheduledExecutorService executor, final MethodHandler methodHandler, final Runnable onConnect, final Runnable onDisconnect) {
      super();
      this.executor = executor;
      this.methodHandler = methodHandler;
      this.onConnect = onConnect;
      this.onDisconnect = onDisconnect;
   }

   public void onOpen(final WebSocket socket) {
      this.submit(() -> {
         this.open(socket);
         return null;
      }).exceptionally((var1) -> {
         socket.abort();
         return null;
      });
   }

   private void open(final WebSocket socket) {
      if (this.closed) {
         socket.abort();
      } else {
         this.webSocket = socket;
         socket.request(1L);
         this.onConnect.run();
      }
   }

   public CompletableFuture<JsonElement> sendRequest(final String method, final List<JsonElement> params) {
      if (!this.closed && this.webSocket != null) {
         int id = ++this.nextRequestId;
         CompletableFuture<JsonElement> response = new CompletableFuture();
         this.pendingRequests.put(id, response);
         this.withTimeout(response);
         response.whenComplete((var2, var3) -> this.pendingRequests.remove(id));
         this.send(JsonRPCUtils.createRequest(id, method, params).toString()).whenComplete((var1, error) -> {
            if (error != null) {
               response.completeExceptionally(error);
            }

         });
         return response;
      } else {
         return CompletableFuture.failedFuture(new IllegalStateException("JSON-RPC is not connected"));
      }
   }

   public CompletableFuture<Void> sendNotification(final String method, final List<JsonElement> params) {
      return this.send(JsonRPCUtils.createRequest((Integer)null, (String)method, params).toString());
   }

   public void sendResponse(final JsonElement id, final JsonElement result) {
      this.send(JsonRPCUtils.createSuccessResult(id, result).toString());
   }

   public void sendError(final JsonElement id, final JsonRPCErrors error) {
      this.send(error.createWithoutData(id).toString());
   }

   private CompletableFuture<Void> send(final String payload) {
      if (!this.closed && this.webSocket != null) {
         CompletableFuture<Void> sent = new CompletableFuture();
         this.pendingSends.add(sent);
         this.withTimeout(sent);
         sent.whenComplete((var2, error) -> {
            this.pendingSends.remove(sent);
            if (error != null) {
               this.fail("JSON-RPC send failed", error);
            }

         });
         CompletableFuture<Void> previous = this.sendChain;
         this.sendChain = sent;
         previous.whenComplete((var3, error) -> {
            if (error != null) {
               sent.completeExceptionally(error);
            } else if (!sent.isDone()) {
               this.sendText(payload, sent);
            }

         });
         return sent;
      } else {
         return CompletableFuture.failedFuture(new IllegalStateException("JSON-RPC is not connected"));
      }
   }

   private void sendText(final String payload, final CompletableFuture<@Nullable Void> sent) {
      WebSocket socket = this.webSocket;
      if (!this.closed && socket != null) {
         try {
            socket.sendText(payload, true).whenCompleteAsync((var1, errorx) -> {
               if (errorx != null) {
                  sent.completeExceptionally(errorx);
               } else {
                  sent.complete((Object)null);
               }

            }, this.executor);
         } catch (RuntimeException error) {
            sent.completeExceptionally(error);
         }

      } else {
         sent.completeExceptionally(new IllegalStateException("JSON-RPC is not connected"));
      }
   }

   private void withTimeout(final CompletableFuture<?> future) {
      ScheduledFuture<?> timeout = this.executor.schedule(() -> future.completeExceptionally(new TimeoutException()), TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
      future.whenComplete((var1, var2) -> timeout.cancel(false));
   }

   private <T> CompletableFuture<@Nullable T> submit(final Supplier<@Nullable T> task) {
      try {
         return CompletableFuture.supplyAsync(task, this.executor);
      } catch (RejectedExecutionException error) {
         return CompletableFuture.failedFuture(error);
      }
   }

   public CompletionStage<?> onText(final WebSocket socket, final CharSequence data, final boolean last) {
      String text = data.toString();
      return this.submit(() -> {
         this.receiveText(socket, text, last);
         return null;
      });
   }

   private void receiveText(final WebSocket socket, final String data, final boolean last) {
      if (!this.closed) {
         this.messageBuffer.append(data);
         if (last) {
            try {
               JsonObject message = GsonHelper.convertToJsonObject(StrictJsonParser.parse(this.messageBuffer.toString()), "message");
               if (!"2.0".equals(GsonHelper.getAsString(message, "jsonrpc"))) {
                  throw new IllegalStateException("Unsupported JSON-RPC version");
               }

               this.handleMessage(message);
            } catch (IllegalStateException | JsonParseException error) {
               this.fail("Invalid JSON-RPC message", error);
            } finally {
               this.messageBuffer.setLength(0);
            }
         }

         if (!this.closed) {
            socket.request(1L);
         }

      }
   }

   private void handleMessage(final JsonObject message) {
      JsonElement id = JsonRPCUtils.getRequestId(message);
      String method = JsonRPCUtils.getMethodName(message);
      if (method != null && !message.has("result") && !message.has("error")) {
         this.methodHandler.onMethod(id, method, JsonRPCUtils.getParams(message));
      } else if (method == null && message.has("result") != message.has("error")) {
         this.handleResponse(id, message);
      } else {
         LOGGER.warn("Invalid JSON-RPC envelope: {}", message);
         if (method != null && id != null) {
            this.sendError(id, JsonRPCErrors.INVALID_REQUEST);
         }

      }
   }

   private void handleResponse(final @Nullable JsonElement id, final JsonObject message) {
      JsonObject details = JsonRPCUtils.getError(message);
      JsonRpcException error;
      if (details != null) {
         error = new JsonRpcException(GsonHelper.getAsInt(details, "code"), GsonHelper.getAsString(details, "message"), details.get("data"));
         LOGGER.warn("JSON-RPC request rejected id={}: code={}, message={}, data={}", new Object[]{id, error.code(), error.serverMessage(), error.data()});
      } else {
         error = null;
      }

      if (id instanceof JsonPrimitive primitive) {
         if (primitive.isNumber()) {
            int responseId;
            try {
               responseId = primitive.getAsBigDecimal().intValueExact();
            } catch (ArithmeticException | NumberFormatException var8) {
               LOGGER.warn("Ignoring JSON-RPC response with invalid integer request id={}", id);
               return;
            }

            CompletableFuture<JsonElement> pending = (CompletableFuture)this.pendingRequests.remove(responseId);
            if (pending == null) {
               LOGGER.warn("JSON-RPC response for unknown request id={}", id);
            } else if (error != null) {
               pending.completeExceptionally(error);
            } else {
               pending.complete((JsonElement)Objects.requireNonNull(JsonRPCUtils.getResult(message)));
            }

            return;
         }
      }

      LOGGER.warn("Ignoring JSON-RPC response with non-numeric request id={}", id);
   }

   public CompletionStage<?> onClose(final WebSocket socket, final int statusCode, final String reason) {
      return this.submit(() -> {
         if (!this.closed) {
            LOGGER.info("JSON-RPC WebSocket disconnected ({}): {}", statusCode, reason);
            this.close(new IllegalStateException("JSON-RPC WebSocket closed (" + statusCode + "): " + reason));
         }

         return null;
      });
   }

   public void onError(final WebSocket socket, final Throwable error) {
      this.executor.execute(() -> this.fail("JSON-RPC WebSocket failed", error));
   }

   private void fail(final String message, final Throwable error) {
      if (!this.closed) {
         LOGGER.warn("{}", message, error);
         this.close(error);
      }

   }

   public void close() {
      this.close(new IllegalStateException("JSON-RPC connection closed"));
   }

   private void close(final Throwable cause) {
      if (!this.closed) {
         this.closed = true;
         List<CompletableFuture<JsonElement>> pending = List.copyOf(this.pendingRequests.values());
         this.pendingRequests.clear();
         pending.forEach((request) -> request.completeExceptionally(cause));
         List<CompletableFuture<Void>> sends = List.copyOf(this.pendingSends);
         this.pendingSends.clear();
         sends.forEach((sent) -> sent.completeExceptionally(cause));
         if (this.webSocket != null) {
            this.webSocket.abort();
            this.webSocket = null;
         }

         this.messageBuffer.setLength(0);
         this.onDisconnect.run();
      }
   }

   @FunctionalInterface
   public interface MethodHandler {
      void onMethod(@Nullable JsonElement id, String method, @Nullable JsonElement params);
   }
}
