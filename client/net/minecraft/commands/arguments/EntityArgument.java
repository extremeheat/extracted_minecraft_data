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
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class EntityArgument implements ArgumentType<EntitySelector> {
   private static final Collection<String> EXAMPLES = Arrays.asList("Player", "0123", "@e", "@e[type=foo]", "dd12be42-52a9-4a91-a8a1-11c01849e498");
   public static final SimpleCommandExceptionType ERROR_NOT_SINGLE_ENTITY = new SimpleCommandExceptionType(Component.translatable("argument.entity.toomany"));
   public static final SimpleCommandExceptionType ERROR_NOT_SINGLE_PLAYER = new SimpleCommandExceptionType(Component.translatable("argument.player.toomany"));
   public static final SimpleCommandExceptionType ERROR_ONLY_PLAYERS_ALLOWED = new SimpleCommandExceptionType(Component.translatable("argument.player.entities"));
   public static final SimpleCommandExceptionType NO_ENTITIES_FOUND = new SimpleCommandExceptionType(Component.translatable("argument.entity.notfound.entity"));
   public static final SimpleCommandExceptionType NO_PLAYERS_FOUND = new SimpleCommandExceptionType(Component.translatable("argument.entity.notfound.player"));
   public static final SimpleCommandExceptionType ERROR_SELECTORS_NOT_ALLOWED = new SimpleCommandExceptionType(Component.translatable("argument.entity.selector.not_allowed"));
   final boolean single;
   final boolean playersOnly;

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
      return this.parse(var1, true);
   }

   public <S> EntitySelector parse(StringReader var1, S var2) throws CommandSyntaxException {
      return this.parse(var1, EntitySelectorParser.allowSelectors(var2));
   }

   private EntitySelector parse(StringReader var1, boolean var2) throws CommandSyntaxException {
      boolean var3 = false;
      EntitySelectorParser var4 = new EntitySelectorParser(var1, var2);
      EntitySelector var5 = var4.parse();
      if (var5.getMaxResults() > 1 && this.single) {
         if (this.playersOnly) {
            var1.setCursor(0);
            throw ERROR_NOT_SINGLE_PLAYER.createWithContext(var1);
         } else {
            var1.setCursor(0);
            throw ERROR_NOT_SINGLE_ENTITY.createWithContext(var1);
         }
      } else if (var5.includesEntities() && this.playersOnly && !var5.isSelfSelector()) {
         var1.setCursor(0);
         throw ERROR_ONLY_PLAYERS_ALLOWED.createWithContext(var1);
      } else {
         return var5;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      Object var4 = var1.getSource();
      if (var4 instanceof SharedSuggestionProvider var3) {
         StringReader var8 = new StringReader(var2.getInput());
         var8.setCursor(var2.getStart());
         EntitySelectorParser var5 = new EntitySelectorParser(var8, EntitySelectorParser.allowSelectors(var3));

         try {
            var5.parse();
         } catch (CommandSyntaxException var7) {
         }

         return var5.fillSuggestions(var2, (var2x) -> {
            Collection var3x = var3.getOnlinePlayerNames();
            Object var4 = this.playersOnly ? var3x : Iterables.concat(var3x, var3.getSelectedEntities());
            SharedSuggestionProvider.suggest((Iterable)var4, var2x);
         });
      } else {
         return Suggestions.empty();
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(final StringReader var1, final Object var2) throws CommandSyntaxException {
      return this.parse(var1, var2);
   }

   // $FF: synthetic method
   public Object parse(final StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }

   public static class Info implements ArgumentTypeInfo<EntityArgument, Template> {
      private static final byte FLAG_SINGLE = 1;
      private static final byte FLAG_PLAYERS_ONLY = 2;

      public Info() {
         super();
      }

      public void serializeToNetwork(Template var1, FriendlyByteBuf var2) {
         int var3 = 0;
         if (var1.single) {
            var3 |= 1;
         }

         if (var1.playersOnly) {
            var3 |= 2;
         }

         var2.writeByte(var3);
      }

      public Template deserializeFromNetwork(FriendlyByteBuf var1) {
         byte var2 = var1.readByte();
         return new Template((var2 & 1) != 0, (var2 & 2) != 0);
      }

      public void serializeToJson(Template var1, JsonObject var2) {
         var2.addProperty("amount", var1.single ? "single" : "multiple");
         var2.addProperty("type", var1.playersOnly ? "players" : "entities");
      }

      public Template unpack(EntityArgument var1) {
         return new Template(var1.single, var1.playersOnly);
      }

      // $FF: synthetic method
      public ArgumentTypeInfo.Template deserializeFromNetwork(final FriendlyByteBuf var1) {
         return this.deserializeFromNetwork(var1);
      }

      public final class Template implements ArgumentTypeInfo.Template<EntityArgument> {
         final boolean single;
         final boolean playersOnly;

         Template(final boolean var2, final boolean var3) {
            super();
            this.single = var2;
            this.playersOnly = var3;
         }

         public EntityArgument instantiate(CommandBuildContext var1) {
            return new EntityArgument(this.single, this.playersOnly);
         }

         public ArgumentTypeInfo<EntityArgument, ?> type() {
            return Info.this;
         }

         // $FF: synthetic method
         public ArgumentType instantiate(final CommandBuildContext var1) {
            return this.instantiate(var1);
         }
      }
   }
}
