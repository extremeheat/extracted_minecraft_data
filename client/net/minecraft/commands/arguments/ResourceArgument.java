package net.minecraft.commands.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.structure.Structure;

public class ResourceArgument<T> implements ArgumentType<Holder.Reference<T>> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012");
   private static final DynamicCommandExceptionType ERROR_NOT_SUMMONABLE_ENTITY = new DynamicCommandExceptionType((var0) -> Component.translatableEscape("entity.not_summonable", var0));
   public static final Dynamic2CommandExceptionType ERROR_UNKNOWN_RESOURCE = new Dynamic2CommandExceptionType((var0, var1) -> Component.translatableEscape("argument.resource.not_found", var0, var1));
   public static final Dynamic3CommandExceptionType ERROR_INVALID_RESOURCE_TYPE = new Dynamic3CommandExceptionType((var0, var1, var2) -> Component.translatableEscape("argument.resource.invalid_type", var0, var1, var2));
   final ResourceKey<? extends Registry<T>> registryKey;
   private final HolderLookup<T> registryLookup;

   public ResourceArgument(CommandBuildContext var1, ResourceKey<? extends Registry<T>> var2) {
      super();
      this.registryKey = var2;
      this.registryLookup = var1.lookupOrThrow(var2);
   }

   public static <T> ResourceArgument<T> resource(CommandBuildContext var0, ResourceKey<? extends Registry<T>> var1) {
      return new ResourceArgument<T>(var0, var1);
   }

   public static <T> Holder.Reference<T> getResource(CommandContext<CommandSourceStack> var0, String var1, ResourceKey<Registry<T>> var2) throws CommandSyntaxException {
      Holder.Reference var3 = (Holder.Reference)var0.getArgument(var1, Holder.Reference.class);
      ResourceKey var4 = var3.key();
      if (var4.isFor(var2)) {
         return var3;
      } else {
         throw ERROR_INVALID_RESOURCE_TYPE.create(var4.location(), var4.registry(), var2.location());
      }
   }

   public static Holder.Reference<Attribute> getAttribute(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      return getResource(var0, var1, Registries.ATTRIBUTE);
   }

   public static Holder.Reference<ConfiguredFeature<?, ?>> getConfiguredFeature(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      return getResource(var0, var1, Registries.CONFIGURED_FEATURE);
   }

   public static Holder.Reference<Structure> getStructure(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      return getResource(var0, var1, Registries.STRUCTURE);
   }

   public static Holder.Reference<EntityType<?>> getEntityType(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      return getResource(var0, var1, Registries.ENTITY_TYPE);
   }

   public static Holder.Reference<EntityType<?>> getSummonableEntityType(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      Holder.Reference var2 = getResource(var0, var1, Registries.ENTITY_TYPE);
      if (!((EntityType)var2.value()).canSummon()) {
         throw ERROR_NOT_SUMMONABLE_ENTITY.create(var2.key().location().toString());
      } else {
         return var2;
      }
   }

   public static Holder.Reference<MobEffect> getMobEffect(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      return getResource(var0, var1, Registries.MOB_EFFECT);
   }

   public static Holder.Reference<Enchantment> getEnchantment(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      return getResource(var0, var1, Registries.ENCHANTMENT);
   }

   public Holder.Reference<T> parse(StringReader var1) throws CommandSyntaxException {
      ResourceLocation var2 = ResourceLocation.read(var1);
      ResourceKey var3 = ResourceKey.create(this.registryKey, var2);
      return (Holder.Reference)this.registryLookup.get(var3).orElseThrow(() -> ERROR_UNKNOWN_RESOURCE.createWithContext(var1, var2, this.registryKey.location()));
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      return SharedSuggestionProvider.suggestResource(this.registryLookup.listElementIds().map(ResourceKey::location), var2);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(final StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }

   public static class Info<T> implements ArgumentTypeInfo<ResourceArgument<T>, Info<T>.Template> {
      public Info() {
         super();
      }

      public void serializeToNetwork(Info<T>.Template var1, FriendlyByteBuf var2) {
         var2.writeResourceKey(var1.registryKey);
      }

      public Info<T>.Template deserializeFromNetwork(FriendlyByteBuf var1) {
         return new Template(var1.readRegistryKey());
      }

      public void serializeToJson(Info<T>.Template var1, JsonObject var2) {
         var2.addProperty("registry", var1.registryKey.location().toString());
      }

      public Info<T>.Template unpack(ResourceArgument<T> var1) {
         return new Template(var1.registryKey);
      }

      // $FF: synthetic method
      public ArgumentTypeInfo.Template deserializeFromNetwork(final FriendlyByteBuf var1) {
         return this.deserializeFromNetwork(var1);
      }

      public final class Template implements ArgumentTypeInfo.Template<ResourceArgument<T>> {
         final ResourceKey<? extends Registry<T>> registryKey;

         Template(final ResourceKey<? extends Registry<T>> var2) {
            super();
            this.registryKey = var2;
         }

         public ResourceArgument<T> instantiate(CommandBuildContext var1) {
            return new ResourceArgument<T>(var1, this.registryKey);
         }

         public ArgumentTypeInfo<ResourceArgument<T>, ?> type() {
            return Info.this;
         }

         // $FF: synthetic method
         public ArgumentType instantiate(final CommandBuildContext var1) {
            return this.instantiate(var1);
         }
      }
   }
}
