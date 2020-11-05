package net.minecraft.commands.arguments;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EquipmentSlot;

public class SlotArgument implements ArgumentType<Integer> {
   private static final Collection<String> EXAMPLES = Arrays.asList("container.5", "12", "weapon");
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_SLOT = new DynamicCommandExceptionType((var0) -> {
      return new TranslatableComponent("slot.unknown", new Object[]{var0});
   });
   private static final Map<String, Integer> SLOTS = (Map)Util.make(Maps.newHashMap(), (var0) -> {
      int var1;
      for(var1 = 0; var1 < 54; ++var1) {
         var0.put("container." + var1, var1);
      }

      for(var1 = 0; var1 < 9; ++var1) {
         var0.put("hotbar." + var1, var1);
      }

      for(var1 = 0; var1 < 27; ++var1) {
         var0.put("inventory." + var1, 9 + var1);
      }

      for(var1 = 0; var1 < 27; ++var1) {
         var0.put("enderchest." + var1, 200 + var1);
      }

      for(var1 = 0; var1 < 8; ++var1) {
         var0.put("villager." + var1, 300 + var1);
      }

      for(var1 = 0; var1 < 15; ++var1) {
         var0.put("horse." + var1, 500 + var1);
      }

      var0.put("weapon", 98);
      var0.put("weapon.mainhand", 98);
      var0.put("weapon.offhand", 99);
      var0.put("armor.head", 100 + EquipmentSlot.HEAD.getIndex());
      var0.put("armor.chest", 100 + EquipmentSlot.CHEST.getIndex());
      var0.put("armor.legs", 100 + EquipmentSlot.LEGS.getIndex());
      var0.put("armor.feet", 100 + EquipmentSlot.FEET.getIndex());
      var0.put("horse.saddle", 400);
      var0.put("horse.armor", 401);
      var0.put("horse.chest", 499);
   });

   public SlotArgument() {
      super();
   }

   public static SlotArgument slot() {
      return new SlotArgument();
   }

   public static int getSlot(CommandContext<CommandSourceStack> var0, String var1) {
      return (Integer)var0.getArgument(var1, Integer.class);
   }

   public Integer parse(StringReader var1) throws CommandSyntaxException {
      String var2 = var1.readUnquotedString();
      if (!SLOTS.containsKey(var2)) {
         throw ERROR_UNKNOWN_SLOT.create(var2);
      } else {
         return (Integer)SLOTS.get(var2);
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      return SharedSuggestionProvider.suggest((Iterable)SLOTS.keySet(), var2);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
