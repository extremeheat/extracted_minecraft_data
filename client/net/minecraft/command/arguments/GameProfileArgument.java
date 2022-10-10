package net.minecraft.command.arguments;

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
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentTranslation;

public class GameProfileArgument implements ArgumentType<GameProfileArgument.IProfileProvider> {
   private static final Collection<String> field_201311_b = Arrays.asList("Player", "0123", "dd12be42-52a9-4a91-a8a1-11c01849e498", "@e");
   public static final SimpleCommandExceptionType field_197111_a = new SimpleCommandExceptionType(new TextComponentTranslation("argument.player.unknown", new Object[0]));

   public GameProfileArgument() {
      super();
   }

   public static Collection<GameProfile> func_197109_a(CommandContext<CommandSource> var0, String var1) throws CommandSyntaxException {
      return ((GameProfileArgument.IProfileProvider)var0.getArgument(var1, GameProfileArgument.IProfileProvider.class)).getNames((CommandSource)var0.getSource());
   }

   public static GameProfileArgument func_197108_a() {
      return new GameProfileArgument();
   }

   public GameProfileArgument.IProfileProvider parse(StringReader var1) throws CommandSyntaxException {
      if (var1.canRead() && var1.peek() == '@') {
         EntitySelectorParser var4 = new EntitySelectorParser(var1);
         EntitySelector var5 = var4.func_201345_m();
         if (var5.func_197351_b()) {
            throw EntityArgument.field_197100_c.create();
         } else {
            return new GameProfileArgument.ProfileProvider(var5);
         }
      } else {
         int var2 = var1.getCursor();

         while(var1.canRead() && var1.peek() != ' ') {
            var1.skip();
         }

         String var3 = var1.getString().substring(var2, var1.getCursor());
         return (var1x) -> {
            GameProfile var2 = var1x.func_197028_i().func_152358_ax().func_152655_a(var3);
            if (var2 == null) {
               throw field_197111_a.create();
            } else {
               return Collections.singleton(var2);
            }
         };
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      if (var1.getSource() instanceof ISuggestionProvider) {
         StringReader var3 = new StringReader(var2.getInput());
         var3.setCursor(var2.getStart());
         EntitySelectorParser var4 = new EntitySelectorParser(var3);

         try {
            var4.func_201345_m();
         } catch (CommandSyntaxException var6) {
         }

         return var4.func_201993_a(var2, (var1x) -> {
            ISuggestionProvider.func_197005_b(((ISuggestionProvider)var1.getSource()).func_197011_j(), var1x);
         });
      } else {
         return Suggestions.empty();
      }
   }

   public Collection<String> getExamples() {
      return field_201311_b;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }

   public static class ProfileProvider implements GameProfileArgument.IProfileProvider {
      private final EntitySelector field_197106_a;

      public ProfileProvider(EntitySelector var1) {
         super();
         this.field_197106_a = var1;
      }

      public Collection<GameProfile> getNames(CommandSource var1) throws CommandSyntaxException {
         List var2 = this.field_197106_a.func_197342_d(var1);
         if (var2.isEmpty()) {
            throw EntityArgument.field_197102_e.create();
         } else {
            ArrayList var3 = Lists.newArrayList();
            Iterator var4 = var2.iterator();

            while(var4.hasNext()) {
               EntityPlayerMP var5 = (EntityPlayerMP)var4.next();
               var3.add(var5.func_146103_bH());
            }

            return var3;
         }
      }
   }

   @FunctionalInterface
   public interface IProfileProvider {
      Collection<GameProfile> getNames(CommandSource var1) throws CommandSyntaxException;
   }
}
