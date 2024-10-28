package net.minecraft.commands.arguments;

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
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.ScoreHolder;

public class ScoreHolderArgument implements ArgumentType<Result> {
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
   private static final SimpleCommandExceptionType ERROR_NO_RESULTS = new SimpleCommandExceptionType(Component.translatable("argument.scoreHolder.empty"));
   final boolean multiple;

   public ScoreHolderArgument(boolean var1) {
      super();
      this.multiple = var1;
   }

   public static ScoreHolder getName(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      return (ScoreHolder)getNames(var0, var1).iterator().next();
   }

   public static Collection<ScoreHolder> getNames(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      return getNames(var0, var1, Collections::emptyList);
   }

   public static Collection<ScoreHolder> getNamesWithDefaultWildcard(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      ServerScoreboard var10002 = ((CommandSourceStack)var0.getSource()).getServer().getScoreboard();
      Objects.requireNonNull(var10002);
      return getNames(var0, var1, var10002::getTrackedPlayers);
   }

   public static Collection<ScoreHolder> getNames(CommandContext<CommandSourceStack> var0, String var1, Supplier<Collection<ScoreHolder>> var2) throws CommandSyntaxException {
      Collection var3 = ((Result)var0.getArgument(var1, Result.class)).getNames((CommandSourceStack)var0.getSource(), var2);
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

   public Result parse(StringReader var1) throws CommandSyntaxException {
      if (var1.canRead() && var1.peek() == '@') {
         EntitySelectorParser var7 = new EntitySelectorParser(var1);
         EntitySelector var8 = var7.parse();
         if (!this.multiple && var8.getMaxResults() > 1) {
            throw EntityArgument.ERROR_NOT_SINGLE_ENTITY.createWithContext(var1);
         } else {
            return new SelectorResult(var8);
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
            List var4 = List.of(ScoreHolder.forNameOnly(var3));
            if (var3.startsWith("#")) {
               return (var1x, var2x) -> {
                  return var4;
               };
            } else {
               try {
                  UUID var5 = UUID.fromString(var3);
                  return (var2x, var3x) -> {
                     MinecraftServer var4x = var2x.getServer();
                     Entity var5x = null;
                     ArrayList var6 = null;
                     Iterator var7 = var4x.getAllLevels().iterator();

                     while(var7.hasNext()) {
                        ServerLevel var8 = (ServerLevel)var7.next();
                        Entity var9 = var8.getEntity(var5);
                        if (var9 != null) {
                           if (var5x == null) {
                              var5x = var9;
                           } else {
                              if (var6 == null) {
                                 var6 = new ArrayList();
                                 var6.add(var5x);
                              }

                              var6.add(var9);
                           }
                        }
                     }

                     if (var6 != null) {
                        return var6;
                     } else if (var5x != null) {
                        return List.of(var5x);
                     } else {
                        return var4;
                     }
                  };
               } catch (IllegalArgumentException var6) {
                  return (var2x, var3x) -> {
                     MinecraftServer var4x = var2x.getServer();
                     ServerPlayer var5 = var4x.getPlayerList().getPlayerByName(var3);
                     return var5 != null ? List.of(var5) : var4;
                  };
               }
            }
         }
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(final StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }

   @FunctionalInterface
   public interface Result {
      Collection<ScoreHolder> getNames(CommandSourceStack var1, Supplier<Collection<ScoreHolder>> var2) throws CommandSyntaxException;
   }

   public static class SelectorResult implements Result {
      private final EntitySelector selector;

      public SelectorResult(EntitySelector var1) {
         super();
         this.selector = var1;
      }

      public Collection<ScoreHolder> getNames(CommandSourceStack var1, Supplier<Collection<ScoreHolder>> var2) throws CommandSyntaxException {
         List var3 = this.selector.findEntities(var1);
         if (var3.isEmpty()) {
            throw EntityArgument.NO_ENTITIES_FOUND.create();
         } else {
            return List.copyOf(var3);
         }
      }
   }

   public static class Info implements ArgumentTypeInfo<ScoreHolderArgument, Template> {
      private static final byte FLAG_MULTIPLE = 1;

      public Info() {
         super();
      }

      public void serializeToNetwork(Template var1, FriendlyByteBuf var2) {
         int var3 = 0;
         if (var1.multiple) {
            var3 |= 1;
         }

         var2.writeByte(var3);
      }

      public Template deserializeFromNetwork(FriendlyByteBuf var1) {
         byte var2 = var1.readByte();
         boolean var3 = (var2 & 1) != 0;
         return new Template(var3);
      }

      public void serializeToJson(Template var1, JsonObject var2) {
         var2.addProperty("amount", var1.multiple ? "multiple" : "single");
      }

      public Template unpack(ScoreHolderArgument var1) {
         return new Template(var1.multiple);
      }

      // $FF: synthetic method
      public ArgumentTypeInfo.Template deserializeFromNetwork(final FriendlyByteBuf var1) {
         return this.deserializeFromNetwork(var1);
      }

      public final class Template implements ArgumentTypeInfo.Template<ScoreHolderArgument> {
         final boolean multiple;

         Template(final boolean var2) {
            super();
            this.multiple = var2;
         }

         public ScoreHolderArgument instantiate(CommandBuildContext var1) {
            return new ScoreHolderArgument(this.multiple);
         }

         public ArgumentTypeInfo<ScoreHolderArgument, ?> type() {
            return Info.this;
         }

         // $FF: synthetic method
         public ArgumentType instantiate(final CommandBuildContext var1) {
            return this.instantiate(var1);
         }
      }
   }
}
