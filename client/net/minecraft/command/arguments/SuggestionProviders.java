package net.minecraft.command.arguments;

import com.google.common.collect.Maps;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.TextComponentTranslation;

public class SuggestionProviders {
   private static final Map<ResourceLocation, SuggestionProvider<ISuggestionProvider>> field_197506_e = Maps.newHashMap();
   private static final ResourceLocation field_197507_f = new ResourceLocation("minecraft:ask_server");
   public static final SuggestionProvider<ISuggestionProvider> field_197502_a;
   public static final SuggestionProvider<CommandSource> field_197503_b;
   public static final SuggestionProvider<CommandSource> field_197504_c;
   public static final SuggestionProvider<CommandSource> field_197505_d;

   public static <S extends ISuggestionProvider> SuggestionProvider<S> func_197494_a(ResourceLocation var0, SuggestionProvider<ISuggestionProvider> var1) {
      if (field_197506_e.containsKey(var0)) {
         throw new IllegalArgumentException("A command suggestion provider is already registered with the name " + var0);
      } else {
         field_197506_e.put(var0, var1);
         return new SuggestionProviders.Wrapper(var0, var1);
      }
   }

   public static SuggestionProvider<ISuggestionProvider> func_197498_a(ResourceLocation var0) {
      return (SuggestionProvider)field_197506_e.getOrDefault(var0, field_197502_a);
   }

   public static ResourceLocation func_197497_a(SuggestionProvider<ISuggestionProvider> var0) {
      return var0 instanceof SuggestionProviders.Wrapper ? ((SuggestionProviders.Wrapper)var0).field_197493_b : field_197507_f;
   }

   public static SuggestionProvider<ISuggestionProvider> func_197496_b(SuggestionProvider<ISuggestionProvider> var0) {
      return var0 instanceof SuggestionProviders.Wrapper ? var0 : field_197502_a;
   }

   static {
      field_197502_a = func_197494_a(field_197507_f, (var0, var1) -> {
         return ((ISuggestionProvider)var0.getSource()).func_197009_a(var0, var1);
      });
      field_197503_b = func_197494_a(new ResourceLocation("minecraft:all_recipes"), (var0, var1) -> {
         return ISuggestionProvider.func_197014_a(((ISuggestionProvider)var0.getSource()).func_199612_m(), var1);
      });
      field_197504_c = func_197494_a(new ResourceLocation("minecraft:available_sounds"), (var0, var1) -> {
         return ISuggestionProvider.func_197014_a(((ISuggestionProvider)var0.getSource()).func_197010_l(), var1);
      });
      field_197505_d = func_197494_a(new ResourceLocation("minecraft:summonable_entities"), (var0, var1) -> {
         return ISuggestionProvider.func_201725_a(IRegistry.field_212629_r.func_201756_e().filter(EntityType::func_200720_b), var1, EntityType::func_200718_a, (var0x) -> {
            return new TextComponentTranslation(Util.func_200697_a("entity", EntityType.func_200718_a(var0x)), new Object[0]);
         });
      });
   }

   public static class Wrapper implements SuggestionProvider<ISuggestionProvider> {
      private final SuggestionProvider<ISuggestionProvider> field_197492_a;
      private final ResourceLocation field_197493_b;

      public Wrapper(ResourceLocation var1, SuggestionProvider<ISuggestionProvider> var2) {
         super();
         this.field_197492_a = var2;
         this.field_197493_b = var1;
      }

      public CompletableFuture<Suggestions> getSuggestions(CommandContext<ISuggestionProvider> var1, SuggestionsBuilder var2) throws CommandSyntaxException {
         return this.field_197492_a.getSuggestions(var1, var2);
      }
   }
}
