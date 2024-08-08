package net.minecraft.commands.arguments;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class GameProfileArgument implements ArgumentType<GameProfileArgument.Result> {
   private static final Collection<String> EXAMPLES = Arrays.asList("Player", "0123", "dd12be42-52a9-4a91-a8a1-11c01849e498", "@e");
   public static final SimpleCommandExceptionType ERROR_UNKNOWN_PLAYER = new SimpleCommandExceptionType(Component.translatable("argument.player.unknown"));

   public GameProfileArgument() {
      super();
   }

   public static Collection<GameProfile> getGameProfiles(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      return ((GameProfileArgument.Result)var0.getArgument(var1, GameProfileArgument.Result.class)).getNames((CommandSourceStack)var0.getSource());
   }

   public static GameProfileArgument gameProfile() {
      return new GameProfileArgument();
   }

   public <S> GameProfileArgument.Result parse(StringReader var1, S var2) throws CommandSyntaxException {
      return parse(var1, EntitySelectorParser.allowSelectors(var2));
   }

   public GameProfileArgument.Result parse(StringReader var1) throws CommandSyntaxException {
      return parse(var1, true);
   }

   private static GameProfileArgument.Result parse(StringReader var0, boolean var1) throws CommandSyntaxException {
      if (var0.canRead() && var0.peek() == '@') {
         EntitySelectorParser var4 = new EntitySelectorParser(var0, var1);
         EntitySelector var5 = var4.parse();
         if (var5.includesEntities()) {
            throw EntityArgument.ERROR_ONLY_PLAYERS_ALLOWED.createWithContext(var0);
         } else {
            return new GameProfileArgument.SelectorResult(var5);
         }
      } else {
         int var2 = var0.getCursor();

         while (var0.canRead() && var0.peek() != ' ') {
            var0.skip();
         }

         String var3 = var0.getString().substring(var2, var0.getCursor());
         return var1x -> {
            Optional var2x = var1x.getServer().getProfileCache().get(var3);
            return Collections.singleton((GameProfile)var2x.orElseThrow(ERROR_UNKNOWN_PLAYER::create));
         };
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      if (var1.getSource() instanceof SharedSuggestionProvider var3) {
         StringReader var8 = new StringReader(var2.getInput());
         var8.setCursor(var2.getStart());
         EntitySelectorParser var5 = new EntitySelectorParser(var8, EntitySelectorParser.allowSelectors(var3));

         try {
            var5.parse();
         } catch (CommandSyntaxException var7) {
         }

         return var5.fillSuggestions(var2, var1x -> SharedSuggestionProvider.suggest(var3.getOnlinePlayerNames(), var1x));
      } else {
         return Suggestions.empty();
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   @FunctionalInterface
   public interface Result {
      Collection<GameProfile> getNames(CommandSourceStack var1) throws CommandSyntaxException;
   }

   public static class SelectorResult implements GameProfileArgument.Result {
      private final EntitySelector selector;

      public SelectorResult(EntitySelector var1) {
         super();
         this.selector = var1;
      }

      @Override
      public Collection<GameProfile> getNames(CommandSourceStack var1) throws CommandSyntaxException {
         List var2 = this.selector.findPlayers(var1);
         if (var2.isEmpty()) {
            throw EntityArgument.NO_PLAYERS_FOUND.create();
         } else {
            ArrayList var3 = Lists.newArrayList();

            for (ServerPlayer var5 : var2) {
               var3.add(var5.getGameProfile());
            }

            return var3;
         }
      }
   }
}
