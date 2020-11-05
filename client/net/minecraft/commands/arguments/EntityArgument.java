package net.minecraft.commands.arguments;

import com.google.common.collect.Iterables;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class EntityArgument implements ArgumentType<EntitySelector> {
   private static final Collection<String> EXAMPLES = Arrays.asList("Player", "0123", "@e", "@e[type=foo]", "dd12be42-52a9-4a91-a8a1-11c01849e498");
   public static final SimpleCommandExceptionType ERROR_NOT_SINGLE_ENTITY = new SimpleCommandExceptionType(new TranslatableComponent("argument.entity.toomany"));
   public static final SimpleCommandExceptionType ERROR_NOT_SINGLE_PLAYER = new SimpleCommandExceptionType(new TranslatableComponent("argument.player.toomany"));
   public static final SimpleCommandExceptionType ERROR_ONLY_PLAYERS_ALLOWED = new SimpleCommandExceptionType(new TranslatableComponent("argument.player.entities"));
   public static final SimpleCommandExceptionType NO_ENTITIES_FOUND = new SimpleCommandExceptionType(new TranslatableComponent("argument.entity.notfound.entity"));
   public static final SimpleCommandExceptionType NO_PLAYERS_FOUND = new SimpleCommandExceptionType(new TranslatableComponent("argument.entity.notfound.player"));
   public static final SimpleCommandExceptionType ERROR_SELECTORS_NOT_ALLOWED = new SimpleCommandExceptionType(new TranslatableComponent("argument.entity.selector.not_allowed"));
   private final boolean single;
   private final boolean playersOnly;

   protected EntityArgument(boolean var1, boolean var2) {
      super();
      this.single = var1;
      this.playersOnly = var2;
   }

   public static EntityArgument entity() {
      return new EntityArgument(true, false);
   }

   public static Entity getEntity(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      return ((EntitySelector)var0.getArgument(var1, EntitySelector.class)).findSingleEntity((CommandSourceStack)var0.getSource());
   }

   public static EntityArgument entities() {
      return new EntityArgument(false, false);
   }

   public static Collection<? extends Entity> getEntities(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      Collection var2 = getOptionalEntities(var0, var1);
      if (var2.isEmpty()) {
         throw NO_ENTITIES_FOUND.create();
      } else {
         return var2;
      }
   }

   public static Collection<? extends Entity> getOptionalEntities(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      return ((EntitySelector)var0.getArgument(var1, EntitySelector.class)).findEntities((CommandSourceStack)var0.getSource());
   }

   public static Collection<ServerPlayer> getOptionalPlayers(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      return ((EntitySelector)var0.getArgument(var1, EntitySelector.class)).findPlayers((CommandSourceStack)var0.getSource());
   }

   public static EntityArgument player() {
      return new EntityArgument(true, true);
   }

   public static ServerPlayer getPlayer(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      return ((EntitySelector)var0.getArgument(var1, EntitySelector.class)).findSinglePlayer((CommandSourceStack)var0.getSource());
   }

   public static EntityArgument players() {
      return new EntityArgument(false, true);
   }

   public static Collection<ServerPlayer> getPlayers(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      List var2 = ((EntitySelector)var0.getArgument(var1, EntitySelector.class)).findPlayers((CommandSourceStack)var0.getSource());
      if (var2.isEmpty()) {
         throw NO_PLAYERS_FOUND.create();
      } else {
         return var2;
      }
   }

   public EntitySelector parse(StringReader var1) throws CommandSyntaxException {
      boolean var2 = false;
      EntitySelectorParser var3 = new EntitySelectorParser(var1);
      EntitySelector var4 = var3.parse();
      if (var4.getMaxResults() > 1 && this.single) {
         if (this.playersOnly) {
            var1.setCursor(0);
            throw ERROR_NOT_SINGLE_PLAYER.createWithContext(var1);
         } else {
            var1.setCursor(0);
            throw ERROR_NOT_SINGLE_ENTITY.createWithContext(var1);
         }
      } else if (var4.includesEntities() && this.playersOnly && !var4.isSelfSelector()) {
         var1.setCursor(0);
         throw ERROR_ONLY_PLAYERS_ALLOWED.createWithContext(var1);
      } else {
         return var4;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      if (var1.getSource() instanceof SharedSuggestionProvider) {
         StringReader var3 = new StringReader(var2.getInput());
         var3.setCursor(var2.getStart());
         SharedSuggestionProvider var4 = (SharedSuggestionProvider)var1.getSource();
         EntitySelectorParser var5 = new EntitySelectorParser(var3, var4.hasPermission(2));

         try {
            var5.parse();
         } catch (CommandSyntaxException var7) {
         }

         return var5.fillSuggestions(var2, (var2x) -> {
            Collection var3 = var4.getOnlinePlayerNames();
            Object var4x = this.playersOnly ? var3 : Iterables.concat(var3, var4.getSelectedEntities());
            SharedSuggestionProvider.suggest((Iterable)var4x, var2x);
         });
      } else {
         return Suggestions.empty();
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }

   public static class Serializer implements ArgumentSerializer<EntityArgument> {
      public Serializer() {
         super();
      }

      public void serializeToNetwork(EntityArgument var1, FriendlyByteBuf var2) {
         byte var3 = 0;
         if (var1.single) {
            var3 = (byte)(var3 | 1);
         }

         if (var1.playersOnly) {
            var3 = (byte)(var3 | 2);
         }

         var2.writeByte(var3);
      }

      public EntityArgument deserializeFromNetwork(FriendlyByteBuf var1) {
         byte var2 = var1.readByte();
         return new EntityArgument((var2 & 1) != 0, (var2 & 2) != 0);
      }

      public void serializeToJson(EntityArgument var1, JsonObject var2) {
         var2.addProperty("amount", var1.single ? "single" : "multiple");
         var2.addProperty("type", var1.playersOnly ? "players" : "entities");
      }

      // $FF: synthetic method
      public ArgumentType deserializeFromNetwork(FriendlyByteBuf var1) {
         return this.deserializeFromNetwork(var1);
      }
   }
}
