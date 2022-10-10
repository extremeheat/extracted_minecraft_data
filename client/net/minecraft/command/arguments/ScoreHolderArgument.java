package net.minecraft.command.arguments;

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
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.util.text.TextComponentTranslation;

public class ScoreHolderArgument implements ArgumentType<ScoreHolderArgument.INameProvider> {
   public static final SuggestionProvider<CommandSource> field_201326_a = (var0, var1) -> {
      StringReader var2 = new StringReader(var1.getInput());
      var2.setCursor(var1.getStart());
      EntitySelectorParser var3 = new EntitySelectorParser(var2);

      try {
         var3.func_201345_m();
      } catch (CommandSyntaxException var5) {
      }

      return var3.func_201993_a(var1, (var1x) -> {
         ISuggestionProvider.func_197005_b(((CommandSource)var0.getSource()).func_197011_j(), var1x);
      });
   };
   private static final Collection<String> field_201327_b = Arrays.asList("Player", "0123", "*", "@e");
   private static final SimpleCommandExceptionType field_197215_a = new SimpleCommandExceptionType(new TextComponentTranslation("argument.scoreHolder.empty", new Object[0]));
   private final boolean field_197216_b;

   public ScoreHolderArgument(boolean var1) {
      super();
      this.field_197216_b = var1;
   }

   public static String func_197211_a(CommandContext<CommandSource> var0, String var1) throws CommandSyntaxException {
      return (String)func_197213_b(var0, var1).iterator().next();
   }

   public static Collection<String> func_197213_b(CommandContext<CommandSource> var0, String var1) throws CommandSyntaxException {
      return func_197210_a(var0, var1, Collections::emptyList);
   }

   public static Collection<String> func_211707_c(CommandContext<CommandSource> var0, String var1) throws CommandSyntaxException {
      ServerScoreboard var10002 = ((CommandSource)var0.getSource()).func_197028_i().func_200251_aP();
      var10002.getClass();
      return func_197210_a(var0, var1, var10002::func_96526_d);
   }

   public static Collection<String> func_197210_a(CommandContext<CommandSource> var0, String var1, Supplier<Collection<String>> var2) throws CommandSyntaxException {
      Collection var3 = ((ScoreHolderArgument.INameProvider)var0.getArgument(var1, ScoreHolderArgument.INameProvider.class)).getNames((CommandSource)var0.getSource(), var2);
      if (var3.isEmpty()) {
         throw EntityArgument.field_197101_d.create();
      } else {
         return var3;
      }
   }

   public static ScoreHolderArgument func_197209_a() {
      return new ScoreHolderArgument(false);
   }

   public static ScoreHolderArgument func_197214_b() {
      return new ScoreHolderArgument(true);
   }

   public ScoreHolderArgument.INameProvider parse(StringReader var1) throws CommandSyntaxException {
      if (var1.canRead() && var1.peek() == '@') {
         EntitySelectorParser var5 = new EntitySelectorParser(var1);
         EntitySelector var6 = var5.func_201345_m();
         if (!this.field_197216_b && var6.func_197346_a() > 1) {
            throw EntityArgument.field_197098_a.create();
         } else {
            return new ScoreHolderArgument.NameProvider(var6);
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
                  throw field_197215_a.create();
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
      return field_201327_b;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }

   public static class Serializer implements IArgumentSerializer<ScoreHolderArgument> {
      public Serializer() {
         super();
      }

      public void func_197072_a(ScoreHolderArgument var1, PacketBuffer var2) {
         byte var3 = 0;
         if (var1.field_197216_b) {
            var3 = (byte)(var3 | 1);
         }

         var2.writeByte(var3);
      }

      public ScoreHolderArgument func_197071_b(PacketBuffer var1) {
         byte var2 = var1.readByte();
         boolean var3 = (var2 & 1) != 0;
         return new ScoreHolderArgument(var3);
      }

      public void func_212244_a(ScoreHolderArgument var1, JsonObject var2) {
         var2.addProperty("amount", var1.field_197216_b ? "multiple" : "single");
      }

      // $FF: synthetic method
      public ArgumentType func_197071_b(PacketBuffer var1) {
         return this.func_197071_b(var1);
      }
   }

   public static class NameProvider implements ScoreHolderArgument.INameProvider {
      private final EntitySelector field_197205_a;

      public NameProvider(EntitySelector var1) {
         super();
         this.field_197205_a = var1;
      }

      public Collection<String> getNames(CommandSource var1, Supplier<Collection<String>> var2) throws CommandSyntaxException {
         List var3 = this.field_197205_a.func_197341_b(var1);
         if (var3.isEmpty()) {
            throw EntityArgument.field_197101_d.create();
         } else {
            ArrayList var4 = Lists.newArrayList();
            Iterator var5 = var3.iterator();

            while(var5.hasNext()) {
               Entity var6 = (Entity)var5.next();
               var4.add(var6.func_195047_I_());
            }

            return var4;
         }
      }
   }

   @FunctionalInterface
   public interface INameProvider {
      Collection<String> getNames(CommandSource var1, Supplier<Collection<String>> var2) throws CommandSyntaxException;
   }
}
