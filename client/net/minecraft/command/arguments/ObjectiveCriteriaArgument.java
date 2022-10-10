package net.minecraft.command.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.TextComponentTranslation;

public class ObjectiveCriteriaArgument implements ArgumentType<ScoreCriteria> {
   private static final Collection<String> field_201318_b = Arrays.asList("foo", "foo.bar.baz", "minecraft:foo");
   public static final DynamicCommandExceptionType field_197164_a = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("argument.criteria.invalid", new Object[]{var0});
   });

   private ObjectiveCriteriaArgument() {
      super();
   }

   public static ObjectiveCriteriaArgument func_197162_a() {
      return new ObjectiveCriteriaArgument();
   }

   public static ScoreCriteria func_197161_a(CommandContext<CommandSource> var0, String var1) {
      return (ScoreCriteria)var0.getArgument(var1, ScoreCriteria.class);
   }

   public ScoreCriteria parse(StringReader var1) throws CommandSyntaxException {
      int var2 = var1.getCursor();

      while(var1.canRead() && var1.peek() != ' ') {
         var1.skip();
      }

      String var3 = var1.getString().substring(var2, var1.getCursor());
      ScoreCriteria var4 = ScoreCriteria.func_197911_a(var3);
      if (var4 == null) {
         var1.setCursor(var2);
         throw field_197164_a.create(var3);
      } else {
         return var4;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      ArrayList var3 = Lists.newArrayList(ScoreCriteria.field_96643_a.keySet());
      Iterator var4 = IRegistry.field_212634_w.iterator();

      while(var4.hasNext()) {
         StatType var5 = (StatType)var4.next();
         Iterator var6 = var5.func_199080_a().iterator();

         while(var6.hasNext()) {
            Object var7 = var6.next();
            String var8 = this.func_199815_a(var5, var7);
            var3.add(var8);
         }
      }

      return ISuggestionProvider.func_197005_b(var3, var2);
   }

   public <T> String func_199815_a(StatType<T> var1, Object var2) {
      return Stat.func_197918_a(var1, var2);
   }

   public Collection<String> getExamples() {
      return field_201318_b;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
