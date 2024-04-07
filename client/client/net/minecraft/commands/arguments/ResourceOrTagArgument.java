package net.minecraft.commands.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Either;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public class ResourceOrTagArgument<T> implements ArgumentType<ResourceOrTagArgument.Result<T>> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012", "#skeletons", "#minecraft:skeletons");
   private static final Dynamic2CommandExceptionType ERROR_UNKNOWN_TAG = new Dynamic2CommandExceptionType(
      (var0, var1) -> Component.translatableEscape("argument.resource_tag.not_found", var0, var1)
   );
   private static final Dynamic3CommandExceptionType ERROR_INVALID_TAG_TYPE = new Dynamic3CommandExceptionType(
      (var0, var1, var2) -> Component.translatableEscape("argument.resource_tag.invalid_type", var0, var1, var2)
   );
   private final HolderLookup<T> registryLookup;
   final ResourceKey<? extends Registry<T>> registryKey;

   public ResourceOrTagArgument(CommandBuildContext var1, ResourceKey<? extends Registry<T>> var2) {
      super();
      this.registryKey = var2;
      this.registryLookup = var1.lookupOrThrow(var2);
   }

   public static <T> ResourceOrTagArgument<T> resourceOrTag(CommandBuildContext var0, ResourceKey<? extends Registry<T>> var1) {
      return new ResourceOrTagArgument<>(var0, var1);
   }

   public static <T> ResourceOrTagArgument.Result<T> getResourceOrTag(CommandContext<CommandSourceStack> var0, String var1, ResourceKey<Registry<T>> var2) throws CommandSyntaxException {
      ResourceOrTagArgument.Result var3 = (ResourceOrTagArgument.Result)var0.getArgument(var1, ResourceOrTagArgument.Result.class);
      Optional var4 = var3.cast(var2);
      return (ResourceOrTagArgument.Result<T>)var4.orElseThrow(() -> (CommandSyntaxException)var3.unwrap().map(var1xx -> {
            ResourceKey var2x = var1xx.key();
            return ResourceArgument.ERROR_INVALID_RESOURCE_TYPE.create(var2x.location(), var2x.registry(), var2.location());
         }, var1xx -> {
            TagKey var2x = var1xx.key();
            return ERROR_INVALID_TAG_TYPE.create(var2x.location(), var2x.registry(), var2.location());
         }));
   }

   public ResourceOrTagArgument.Result<T> parse(StringReader var1) throws CommandSyntaxException {
      if (var1.canRead() && var1.peek() == '#') {
         int var7 = var1.getCursor();

         try {
            var1.skip();
            ResourceLocation var8 = ResourceLocation.read(var1);
            TagKey var9 = TagKey.create(this.registryKey, var8);
            HolderSet.Named var5 = this.registryLookup
               .get(var9)
               .orElseThrow(() -> ERROR_UNKNOWN_TAG.createWithContext(var1, var8, this.registryKey.location()));
            return new ResourceOrTagArgument.TagResult<>(var5);
         } catch (CommandSyntaxException var6) {
            var1.setCursor(var7);
            throw var6;
         }
      } else {
         ResourceLocation var2 = ResourceLocation.read(var1);
         ResourceKey var3 = ResourceKey.create(this.registryKey, var2);
         Holder.Reference var4 = this.registryLookup
            .get(var3)
            .orElseThrow(() -> ResourceArgument.ERROR_UNKNOWN_RESOURCE.createWithContext(var1, var2, this.registryKey.location()));
         return new ResourceOrTagArgument.ResourceResult<>(var4);
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      SharedSuggestionProvider.suggestResource(this.registryLookup.listTagIds().map(TagKey::location), var2, "#");
      return SharedSuggestionProvider.suggestResource(this.registryLookup.listElementIds().map(ResourceKey::location), var2);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public static class Info<T> implements ArgumentTypeInfo<ResourceOrTagArgument<T>, ResourceOrTagArgument.Info<T>.Template> {
      public Info() {
         super();
      }

      public void serializeToNetwork(ResourceOrTagArgument.Info<T>.Template var1, FriendlyByteBuf var2) {
         var2.writeResourceKey(var1.registryKey);
      }

      public ResourceOrTagArgument.Info<T>.Template deserializeFromNetwork(FriendlyByteBuf var1) {
         return new ResourceOrTagArgument.Info.Template(var1.readRegistryKey());
      }

      public void serializeToJson(ResourceOrTagArgument.Info<T>.Template var1, JsonObject var2) {
         var2.addProperty("registry", var1.registryKey.location().toString());
      }

      public ResourceOrTagArgument.Info<T>.Template unpack(ResourceOrTagArgument<T> var1) {
         return new ResourceOrTagArgument.Info.Template(var1.registryKey);
      }

      public final class Template implements ArgumentTypeInfo.Template<ResourceOrTagArgument<T>> {
         final ResourceKey<? extends Registry<T>> registryKey;

         Template(ResourceKey<? extends Registry<T>> var2) {
            super();
            this.registryKey = var2;
         }

         public ResourceOrTagArgument<T> instantiate(CommandBuildContext var1) {
            return new ResourceOrTagArgument<>(var1, this.registryKey);
         }

         @Override
         public ArgumentTypeInfo<ResourceOrTagArgument<T>, ?> type() {
            return Info.this;
         }
      }
   }

   static record ResourceResult<T>(Holder.Reference<T> value) implements ResourceOrTagArgument.Result<T> {
      ResourceResult(Holder.Reference<T> value) {
         super();
         this.value = value;
      }

      @Override
      public Either<Holder.Reference<T>, HolderSet.Named<T>> unwrap() {
         return Either.left(this.value);
      }

      @Override
      public <E> Optional<ResourceOrTagArgument.Result<E>> cast(ResourceKey<? extends Registry<E>> var1) {
         return this.value.key().isFor(var1) ? Optional.of((ResourceOrTagArgument.Result<E>)this) : Optional.empty();
      }

      public boolean test(Holder<T> var1) {
         return var1.equals(this.value);
      }

      @Override
      public String asPrintable() {
         return this.value.key().location().toString();
      }
   }

   public interface Result<T> extends Predicate<Holder<T>> {
      Either<Holder.Reference<T>, HolderSet.Named<T>> unwrap();

      <E> Optional<ResourceOrTagArgument.Result<E>> cast(ResourceKey<? extends Registry<E>> var1);

      String asPrintable();
   }

   static record TagResult<T>(HolderSet.Named<T> tag) implements ResourceOrTagArgument.Result<T> {
      TagResult(HolderSet.Named<T> tag) {
         super();
         this.tag = tag;
      }

      @Override
      public Either<Holder.Reference<T>, HolderSet.Named<T>> unwrap() {
         return Either.right(this.tag);
      }

      @Override
      public <E> Optional<ResourceOrTagArgument.Result<E>> cast(ResourceKey<? extends Registry<E>> var1) {
         return this.tag.key().isFor(var1) ? Optional.of((ResourceOrTagArgument.Result<E>)this) : Optional.empty();
      }

      public boolean test(Holder<T> var1) {
         return this.tag.contains(var1);
      }

      @Override
      public String asPrintable() {
         return "#" + this.tag.key().location();
      }
   }
}
