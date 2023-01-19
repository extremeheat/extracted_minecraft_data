package net.minecraft.commands.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class ResourceKeyArgument<T> implements ArgumentType<ResourceKey<T>> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012");
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_ATTRIBUTE = new DynamicCommandExceptionType(
      var0 -> Component.translatable("attribute.unknown", var0)
   );
   private static final DynamicCommandExceptionType ERROR_INVALID_FEATURE = new DynamicCommandExceptionType(
      var0 -> Component.translatable("commands.place.feature.invalid", var0)
   );
   private static final DynamicCommandExceptionType ERROR_INVALID_STRUCTURE = new DynamicCommandExceptionType(
      var0 -> Component.translatable("commands.place.structure.invalid", var0)
   );
   private static final DynamicCommandExceptionType ERROR_INVALID_TEMPLATE_POOL = new DynamicCommandExceptionType(
      var0 -> Component.translatable("commands.place.jigsaw.invalid", var0)
   );
   final ResourceKey<? extends Registry<T>> registryKey;

   public ResourceKeyArgument(ResourceKey<? extends Registry<T>> var1) {
      super();
      this.registryKey = var1;
   }

   public static <T> ResourceKeyArgument<T> key(ResourceKey<? extends Registry<T>> var0) {
      return new ResourceKeyArgument<>(var0);
   }

   private static <T> ResourceKey<T> getRegistryType(
      CommandContext<CommandSourceStack> var0, String var1, ResourceKey<Registry<T>> var2, DynamicCommandExceptionType var3
   ) throws CommandSyntaxException {
      ResourceKey var4 = (ResourceKey)var0.getArgument(var1, ResourceKey.class);
      Optional var5 = var4.cast(var2);
      return (ResourceKey<T>)var5.orElseThrow(() -> var3.create(var4));
   }

   private static <T> Registry<T> getRegistry(CommandContext<CommandSourceStack> var0, ResourceKey<? extends Registry<T>> var1) {
      return ((CommandSourceStack)var0.getSource()).getServer().registryAccess().registryOrThrow(var1);
   }

   private static <T> Holder<T> getRegistryKeyType(
      CommandContext<CommandSourceStack> var0, String var1, ResourceKey<Registry<T>> var2, DynamicCommandExceptionType var3
   ) throws CommandSyntaxException {
      ResourceKey var4 = getRegistryType(var0, var1, var2, var3);
      return getRegistry(var0, var2).getHolder(var4).orElseThrow(() -> var3.create(var4.location()));
   }

   public static Attribute getAttribute(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      ResourceKey var2 = getRegistryType(var0, var1, Registry.ATTRIBUTE_REGISTRY, ERROR_UNKNOWN_ATTRIBUTE);
      return getRegistry(var0, Registry.ATTRIBUTE_REGISTRY).getOptional(var2).orElseThrow(() -> ERROR_UNKNOWN_ATTRIBUTE.create(var2.location()));
   }

   public static Holder<ConfiguredFeature<?, ?>> getConfiguredFeature(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      return getRegistryKeyType(var0, var1, Registry.CONFIGURED_FEATURE_REGISTRY, ERROR_INVALID_FEATURE);
   }

   public static Holder<Structure> getStructure(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      return getRegistryKeyType(var0, var1, Registry.STRUCTURE_REGISTRY, ERROR_INVALID_STRUCTURE);
   }

   public static Holder<StructureTemplatePool> getStructureTemplatePool(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      return getRegistryKeyType(var0, var1, Registry.TEMPLATE_POOL_REGISTRY, ERROR_INVALID_TEMPLATE_POOL);
   }

   public ResourceKey<T> parse(StringReader var1) throws CommandSyntaxException {
      ResourceLocation var2 = ResourceLocation.read(var1);
      return ResourceKey.create(this.registryKey, var2);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      Object var4 = var1.getSource();
      return var4 instanceof SharedSuggestionProvider var3
         ? var3.suggestRegistryElements(this.registryKey, SharedSuggestionProvider.ElementSuggestionType.ELEMENTS, var2, var1)
         : var2.buildFuture();
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public static class Info<T> implements ArgumentTypeInfo<ResourceKeyArgument<T>, ResourceKeyArgument.Info<T>.Template> {
      public Info() {
         super();
      }

      public void serializeToNetwork(ResourceKeyArgument.Info.Template var1, FriendlyByteBuf var2) {
         var2.writeResourceLocation(var1.registryKey.location());
      }

      public ResourceKeyArgument.Info<T>.Template deserializeFromNetwork(FriendlyByteBuf var1) {
         ResourceLocation var2 = var1.readResourceLocation();
         return new ResourceKeyArgument.Info.Template(ResourceKey.createRegistryKey(var2));
      }

      public void serializeToJson(ResourceKeyArgument.Info.Template var1, JsonObject var2) {
         var2.addProperty("registry", var1.registryKey.location().toString());
      }

      public ResourceKeyArgument.Info<T>.Template unpack(ResourceKeyArgument<T> var1) {
         return new ResourceKeyArgument.Info.Template(var1.registryKey);
      }

      public final class Template implements ArgumentTypeInfo.Template<ResourceKeyArgument<T>> {
         final ResourceKey<? extends Registry<T>> registryKey;

         Template(ResourceKey<? extends Registry<T>> var2) {
            super();
            this.registryKey = var2;
         }

         public ResourceKeyArgument<T> instantiate(CommandBuildContext var1) {
            return new ResourceKeyArgument<>(this.registryKey);
         }

         @Override
         public ArgumentTypeInfo<ResourceKeyArgument<T>, ?> type() {
            return Info.this;
         }
      }
   }
}
