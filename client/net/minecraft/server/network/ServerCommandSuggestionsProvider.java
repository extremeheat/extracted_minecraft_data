package net.minecraft.server.network;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.logging.LogUtils;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class ServerCommandSuggestionsProvider {
   private static final int MAX_COMMAND_SUGGESTIONS = 1000;
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int MIN_INTERVAL_TICKS = 1;
   private final ServerPlayer player;
   private final AtomicReference<@Nullable Request> lastRequest = new AtomicReference();
   private volatile boolean hasBudget;

   public ServerCommandSuggestionsProvider(final ServerPlayer player) {
      super();
      this.player = player;
   }

   public void tick() {
      if (this.player.tickCount % 1 == 0) {
         this.hasBudget = true;
         this.tryProcessRequest();
      }

   }

   private void tryProcessRequest() {
      if (this.hasBudget) {
         Request request = (Request)this.lastRequest.getAndSet((Object)null);
         if (request != null) {
            this.hasBudget = false;

            try {
               StringReader reader = new StringReader(request.command);
               if (reader.canRead() && reader.peek() == '/') {
                  reader.skip();
               }

               MinecraftServer server = this.player.level().getServer();
               CommandDispatcher<CommandSourceStack> dispatcher = server.getCommands().getDispatcher();
               ParseResults<CommandSourceStack> parse = dispatcher.parse(reader, this.player.createCommandSourceStack());
               if (this.lastRequest.get() != null) {
                  return;
               }

               dispatcher.getCompletionSuggestions(parse).thenAccept((suggestions) -> request.future.complete(limitSuggestionCount(suggestions)));
            } catch (Exception e) {
               LOGGER.error("Failed to resolve command suggestions for {}", this.player.getGameProfile().name(), e);
            }

         }
      }
   }

   private static Suggestions limitSuggestionCount(final Suggestions suggestions) {
      return suggestions.getList().size() <= 1000 ? suggestions : new Suggestions(suggestions.getRange(), suggestions.getList().subList(0, 1000));
   }

   private void trySchedule() {
      if (this.hasBudget) {
         this.player.level().getServer().execute(this::tryProcessRequest);
      }

   }

   public CompletableFuture<Suggestions> request(final String command) {
      Request request = new Request(command);
      Request oldRequest = (Request)this.lastRequest.getAndSet(request);
      if (oldRequest == null) {
         this.trySchedule();
      }

      return request.future;
   }

   private static class Request {
      private final String command;
      private final CompletableFuture<Suggestions> future = new CompletableFuture();

      private Request(final String command) {
         super();
         this.command = command;
      }
   }
}
