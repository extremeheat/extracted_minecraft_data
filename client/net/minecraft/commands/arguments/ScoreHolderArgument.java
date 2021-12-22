package net.minecraft.commands.arguments;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.entity.Entity;

public class ScoreHolderArgument implements ArgumentType<ScoreHolderArgument.Result> {
   public static final SuggestionProvider<CommandSourceStack> SUGGEST_SCORE_HOLDERS = (var0, var1) -> {
      StringReader var2 = new StringReader(var1.getInput());
      var2.setCursor(var1.getStart());
      EntitySelectorParser var3 = new EntitySelectorParser(var2);

      try {
         var3.parse();
      } catch (CommandSyntaxException var5) {
      }

      return var3.fillSuggestions(var1, (var1x) -> {
         SharedSuggestionProvider.suggest((Iterable)((CommandSourceStack)var0.getSource()).getOnlinePlayerNames(), var1x);
      });
   };
   private static final Collection<String> EXAMPLES = Arrays.asList("Player", "0123", "*", "@e");
   private static final SimpleCommandExceptionType ERROR_NO_RESULTS = new SimpleCommandExceptionType(new TranslatableComponent("argument.scoreHolder.empty"));
   private static final byte FLAG_MULTIPLE = 1;
   final boolean multiple;

   public ScoreHolderArgument(boolean var1) {
      super();
      this.multiple = var1;
   }

   public static String getName(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      return (String)getNames(var0, var1).iterator().next();
   }

   public static Collection<String> getNames(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      return getNames(var0, var1, Collections::emptyList);
   }

   public static Collection<String> getNamesWithDefaultWildcard(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      ServerScoreboard var10002 = ((CommandSourceStack)var0.getSource()).getServer().getScoreboard();
      Objects.requireNonNull(var10002);
      return getNames(var0, var1, var10002::getTrackedPlayers);
   }

   public static Collection<String> getNames(CommandContext<CommandSourceStack> var0, String var1, Supplier<Collection<String>> var2) throws CommandSyntaxException {
      Collection var3 = ((ScoreHolderArgument.Result)var0.getArgument(var1, ScoreHolderArgument.Result.class)).getNames((CommandSourceStack)var0.getSource(), var2);
      if (var3.isEmpty()) {
         throw EntityArgument.NO_ENTITIES_FOUND.create();
      } else {
         return var3;
      }
   }

   public static ScoreHolderArgument scoreHolder() {
      return new ScoreHolderArgument(false);
   }

   public static ScoreHolderArgument scoreHolders() {
      return new ScoreHolderArgument(true);
   }

   public ScoreHolderArgument.Result parse(StringReader var1) throws CommandSyntaxException {
      if (var1.canRead() && var1.peek() == '@') {
         EntitySelectorParser var5 = new EntitySelectorParser(var1);
         EntitySelector var6 = var5.parse();
         if (!this.multiple && var6.getMaxResults() > 1) {
            throw EntityArgument.ERROR_NOT_SINGLE_ENTITY.create();
         } else {
            return new ScoreHolderArgument.SelectorResult(var6);
         }
      } else {
         int var2 = var1.getCursor();

         while(var1.canRead() && var1.peek() != ' ') {
            var1.skip();
         }

         String var3 = var1.getString().substring(var2, var1.getCursor());
         if (var3.equals("*")) {
            return (var0, var1x) -> {
               Collection var2 = (Collection)var1x.get();
               if (var2.isEmpty()) {
                  throw ERROR_NO_RESULTS.create();
               } else {
                  return var2;
               }
            };
         } else {
            Set var4 = Collections.singleton(var3);
            return (var1x, var2x) -> {
               return var4;
            };
         }
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }

   @FunctionalInterface
   public interface Result {
      Collection<String> getNames(CommandSourceStack var1, Supplier<Collection<String>> var2) throws CommandSyntaxException;
   }

   public static class SelectorResult implements ScoreHolderArgument.Result {
      private final EntitySelector selector;

      public SelectorResult(EntitySelector var1) {
         super();
         this.selector = var1;
      }

      public Collection<String> getNames(CommandSourceStack var1, Supplier<Collection<String>> var2) throws CommandSyntaxException {
         List var3 = this.selector.findEntities(var1);
         if (var3.isEmpty()) {
            throw EntityArgument.NO_ENTITIES_FOUND.create();
         } else {
            ArrayList var4 = Lists.newArrayList();
            Iterator var5 = var3.iterator();

            while(var5.hasNext()) {
               Entity var6 = (Entity)var5.next();
               var4.add(var6.getScoreboardName());
            }

            return var4;
         }
      }
   }

   public static class Serializer implements ArgumentSerializer<ScoreHolderArgument> {
      public Serializer() {
         super();
      }

      public void serializeToNetwork(ScoreHolderArgument var1, FriendlyByteBuf var2) {
         byte var3 = 0;
         if (var1.multiple) {
            var3 = (byte)(var3 | 1);
         }

         var2.writeByte(var3);
      }

      public ScoreHolderArgument deserializeFromNetwork(FriendlyByteBuf var1) {
         byte var2 = var1.readByte();
         boolean var3 = (var2 & 1) != 0;
         return new ScoreHolderArgument(var3);
      }

      public void serializeToJson(ScoreHolderArgument var1, JsonObject var2) {
         var2.addProperty("amount", var1.multiple ? "multiple" : "single");
      }

      // $FF: synthetic method
      public ArgumentType deserializeFromNetwork(FriendlyByteBuf var1) {
         return this.deserializeFromNetwork(var1);
      }
   }
}
