package net.minecraft.commands.arguments;

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
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class ObjectiveCriteriaArgument implements ArgumentType<ObjectiveCriteria> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo.bar.baz", "minecraft:foo");
   public static final DynamicCommandExceptionType ERROR_INVALID_VALUE = new DynamicCommandExceptionType((var0) -> {
      return Component.translatable("argument.criteria.invalid", var0);
   });

   private ObjectiveCriteriaArgument() {
      super();
   }

   public static ObjectiveCriteriaArgument criteria() {
      return new ObjectiveCriteriaArgument();
   }

   public static ObjectiveCriteria getCriteria(CommandContext<CommandSourceStack> var0, String var1) {
      return (ObjectiveCriteria)var0.getArgument(var1, ObjectiveCriteria.class);
   }

   public ObjectiveCriteria parse(StringReader var1) throws CommandSyntaxException {
      int var2 = var1.getCursor();

      while(var1.canRead() && var1.peek() != ' ') {
         var1.skip();
      }

      String var3 = var1.getString().substring(var2, var1.getCursor());
      return (ObjectiveCriteria)ObjectiveCriteria.byName(var3).orElseThrow(() -> {
         var1.setCursor(var2);
         return ERROR_INVALID_VALUE.create(var3);
      });
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      ArrayList var3 = Lists.newArrayList(ObjectiveCriteria.getCustomCriteriaNames());
      Iterator var4 = Registry.STAT_TYPE.iterator();

      while(var4.hasNext()) {
         StatType var5 = (StatType)var4.next();
         Iterator var6 = var5.getRegistry().iterator();

         while(var6.hasNext()) {
            Object var7 = var6.next();
            String var8 = this.getName(var5, var7);
            var3.add(var8);
         }
      }

      return SharedSuggestionProvider.suggest((Iterable)var3, var2);
   }

   public <T> String getName(StatType<T> var1, Object var2) {
      return Stat.buildName(var1, var2);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
