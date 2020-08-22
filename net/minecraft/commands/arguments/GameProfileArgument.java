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
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;

public class GameProfileArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("Player", "0123", "dd12be42-52a9-4a91-a8a1-11c01849e498", "@e");
   public static final SimpleCommandExceptionType ERROR_UNKNOWN_PLAYER = new SimpleCommandExceptionType(new TranslatableComponent("argument.player.unknown", new Object[0]));

   public static Collection getGameProfiles(CommandContext var0, String var1) throws CommandSyntaxException {
      return ((GameProfileArgument.Result)var0.getArgument(var1, GameProfileArgument.Result.class)).getNames((CommandSourceStack)var0.getSource());
   }

   public static GameProfileArgument gameProfile() {
      return new GameProfileArgument();
   }

   public GameProfileArgument.Result parse(StringReader var1) throws CommandSyntaxException {
      if (var1.canRead() && var1.peek() == '@') {
         EntitySelectorParser var4 = new EntitySelectorParser(var1);
         EntitySelector var5 = var4.parse();
         if (var5.includesEntities()) {
            throw EntityArgument.ERROR_ONLY_PLAYERS_ALLOWED.create();
         } else {
            return new GameProfileArgument.SelectorResult(var5);
         }
      } else {
         int var2 = var1.getCursor();

         while(var1.canRead() && var1.peek() != ' ') {
            var1.skip();
         }

         String var3 = var1.getString().substring(var2, var1.getCursor());
         return (var1x) -> {
            GameProfile var2 = var1x.getServer().getProfileCache().get(var3);
            if (var2 == null) {
               throw ERROR_UNKNOWN_PLAYER.create();
            } else {
               return Collections.singleton(var2);
            }
         };
      }
   }

   public CompletableFuture listSuggestions(CommandContext var1, SuggestionsBuilder var2) {
      if (var1.getSource() instanceof SharedSuggestionProvider) {
         StringReader var3 = new StringReader(var2.getInput());
         var3.setCursor(var2.getStart());
         EntitySelectorParser var4 = new EntitySelectorParser(var3);

         try {
            var4.parse();
         } catch (CommandSyntaxException var6) {
         }

         return var4.fillSuggestions(var2, (var1x) -> {
            SharedSuggestionProvider.suggest((Iterable)((SharedSuggestionProvider)var1.getSource()).getOnlinePlayerNames(), var1x);
         });
      } else {
         return Suggestions.empty();
      }
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }

   public static class SelectorResult implements GameProfileArgument.Result {
      private final EntitySelector selector;

      public SelectorResult(EntitySelector var1) {
         this.selector = var1;
      }

      public Collection getNames(CommandSourceStack var1) throws CommandSyntaxException {
         List var2 = this.selector.findPlayers(var1);
         if (var2.isEmpty()) {
            throw EntityArgument.NO_PLAYERS_FOUND.create();
         } else {
            ArrayList var3 = Lists.newArrayList();
            Iterator var4 = var2.iterator();

            while(var4.hasNext()) {
               ServerPlayer var5 = (ServerPlayer)var4.next();
               var3.add(var5.getGameProfile());
            }

            return var3;
         }
      }
   }

   @FunctionalInterface
   public interface Result {
      Collection getNames(CommandSourceStack var1) throws CommandSyntaxException;
   }
}
