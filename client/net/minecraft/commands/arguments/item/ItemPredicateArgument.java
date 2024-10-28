package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.parsing.packrat.commands.Grammar;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemPredicateArgument implements ArgumentType<Result> {
   private static final Collection<String> EXAMPLES = Arrays.asList("stick", "minecraft:stick", "#stick", "#stick{foo:'bar'}");
   static final DynamicCommandExceptionType ERROR_UNKNOWN_ITEM = new DynamicCommandExceptionType((var0) -> {
      return Component.translatableEscape("argument.item.id.invalid", var0);
   });
   static final DynamicCommandExceptionType ERROR_UNKNOWN_TAG = new DynamicCommandExceptionType((var0) -> {
      return Component.translatableEscape("arguments.item.tag.unknown", var0);
   });
   static final DynamicCommandExceptionType ERROR_UNKNOWN_COMPONENT = new DynamicCommandExceptionType((var0) -> {
      return Component.translatableEscape("arguments.item.component.unknown", var0);
   });
   static final Dynamic2CommandExceptionType ERROR_MALFORMED_COMPONENT = new Dynamic2CommandExceptionType((var0, var1) -> {
      return Component.translatableEscape("arguments.item.component.malformed", var0, var1);
   });
   static final DynamicCommandExceptionType ERROR_UNKNOWN_PREDICATE = new DynamicCommandExceptionType((var0) -> {
      return Component.translatableEscape("arguments.item.predicate.unknown", var0);
   });
   static final Dynamic2CommandExceptionType ERROR_MALFORMED_PREDICATE = new Dynamic2CommandExceptionType((var0, var1) -> {
      return Component.translatableEscape("arguments.item.predicate.malformed", var0, var1);
   });
   private static final ResourceLocation COUNT_ID = ResourceLocation.withDefaultNamespace("count");
   static final Map<ResourceLocation, ComponentWrapper> PSEUDO_COMPONENTS;
   static final Map<ResourceLocation, PredicateWrapper> PSEUDO_PREDICATES;
   private final Grammar<List<Predicate<ItemStack>>> grammarWithContext;

   public ItemPredicateArgument(CommandBuildContext var1) {
      super();
      Context var2 = new Context(var1);
      this.grammarWithContext = ComponentPredicateParser.createGrammar(var2);
   }

   public static ItemPredicateArgument itemPredicate(CommandBuildContext var0) {
      return new ItemPredicateArgument(var0);
   }

   public Result parse(StringReader var1) throws CommandSyntaxException {
      Predicate var10000 = Util.allOf((List)this.grammarWithContext.parseForCommands(var1));
      Objects.requireNonNull(var10000);
      return var10000::test;
   }

   public static Result getItemPredicate(CommandContext<CommandSourceStack> var0, String var1) {
      return (Result)var0.getArgument(var1, Result.class);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      return this.grammarWithContext.parseForSuggestions(var2);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(final StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }

   static {
      PSEUDO_COMPONENTS = (Map)Stream.of(new ComponentWrapper(COUNT_ID, (var0) -> {
         return true;
      }, MinMaxBounds.Ints.CODEC.map((var0) -> {
         return (var1) -> {
            return var0.matches(var1.getCount());
         };
      }))).collect(Collectors.toUnmodifiableMap(ComponentWrapper::id, (var0) -> {
         return var0;
      }));
      PSEUDO_PREDICATES = (Map)Stream.of(new PredicateWrapper(COUNT_ID, MinMaxBounds.Ints.CODEC.map((var0) -> {
         return (var1) -> {
            return var0.matches(var1.getCount());
         };
      }))).collect(Collectors.toUnmodifiableMap(PredicateWrapper::id, (var0) -> {
         return var0;
      }));
   }

   private static class Context implements ComponentPredicateParser.Context<Predicate<ItemStack>, ComponentWrapper, PredicateWrapper> {
      private final HolderLookup.RegistryLookup<Item> items;
      private final HolderLookup.RegistryLookup<DataComponentType<?>> components;
      private final HolderLookup.RegistryLookup<ItemSubPredicate.Type<?>> predicates;
      private final RegistryOps<Tag> registryOps;

      Context(HolderLookup.Provider var1) {
         super();
         this.items = var1.lookupOrThrow(Registries.ITEM);
         this.components = var1.lookupOrThrow(Registries.DATA_COMPONENT_TYPE);
         this.predicates = var1.lookupOrThrow(Registries.ITEM_SUB_PREDICATE_TYPE);
         this.registryOps = var1.createSerializationContext(NbtOps.INSTANCE);
      }

      public Predicate<ItemStack> forElementType(ImmutableStringReader var1, ResourceLocation var2) throws CommandSyntaxException {
         Holder.Reference var3 = (Holder.Reference)this.items.get(ResourceKey.create(Registries.ITEM, var2)).orElseThrow(() -> {
            return ItemPredicateArgument.ERROR_UNKNOWN_ITEM.createWithContext(var1, var2);
         });
         return (var1x) -> {
            return var1x.is((Holder)var3);
         };
      }

      public Predicate<ItemStack> forTagType(ImmutableStringReader var1, ResourceLocation var2) throws CommandSyntaxException {
         HolderSet var3 = (HolderSet)this.items.get(TagKey.create(Registries.ITEM, var2)).orElseThrow(() -> {
            return ItemPredicateArgument.ERROR_UNKNOWN_TAG.createWithContext(var1, var2);
         });
         return (var1x) -> {
            return var1x.is(var3);
         };
      }

      public ComponentWrapper lookupComponentType(ImmutableStringReader var1, ResourceLocation var2) throws CommandSyntaxException {
         ComponentWrapper var3 = (ComponentWrapper)ItemPredicateArgument.PSEUDO_COMPONENTS.get(var2);
         if (var3 != null) {
            return var3;
         } else {
            DataComponentType var4 = (DataComponentType)this.components.get(ResourceKey.create(Registries.DATA_COMPONENT_TYPE, var2)).map(Holder::value).orElseThrow(() -> {
               return ItemPredicateArgument.ERROR_UNKNOWN_COMPONENT.createWithContext(var1, var2);
            });
            return ItemPredicateArgument.ComponentWrapper.create(var1, var2, var4);
         }
      }

      public Predicate<ItemStack> createComponentTest(ImmutableStringReader var1, ComponentWrapper var2, Tag var3) throws CommandSyntaxException {
         return var2.decode(var1, this.registryOps, var3);
      }

      public Predicate<ItemStack> createComponentTest(ImmutableStringReader var1, ComponentWrapper var2) {
         return var2.presenceChecker;
      }

      public PredicateWrapper lookupPredicateType(ImmutableStringReader var1, ResourceLocation var2) throws CommandSyntaxException {
         PredicateWrapper var3 = (PredicateWrapper)ItemPredicateArgument.PSEUDO_PREDICATES.get(var2);
         return var3 != null ? var3 : (PredicateWrapper)this.predicates.get(ResourceKey.create(Registries.ITEM_SUB_PREDICATE_TYPE, var2)).map(PredicateWrapper::new).orElseThrow(() -> {
            return ItemPredicateArgument.ERROR_UNKNOWN_PREDICATE.createWithContext(var1, var2);
         });
      }

      public Predicate<ItemStack> createPredicateTest(ImmutableStringReader var1, PredicateWrapper var2, Tag var3) throws CommandSyntaxException {
         return var2.decode(var1, this.registryOps, var3);
      }

      public Stream<ResourceLocation> listElementTypes() {
         return this.items.listElementIds().map(ResourceKey::location);
      }

      public Stream<ResourceLocation> listTagTypes() {
         return this.items.listTagIds().map(TagKey::location);
      }

      public Stream<ResourceLocation> listComponentTypes() {
         return Stream.concat(ItemPredicateArgument.PSEUDO_COMPONENTS.keySet().stream(), this.components.listElements().filter((var0) -> {
            return !((DataComponentType)var0.value()).isTransient();
         }).map((var0) -> {
            return var0.key().location();
         }));
      }

      public Stream<ResourceLocation> listPredicateTypes() {
         return Stream.concat(ItemPredicateArgument.PSEUDO_PREDICATES.keySet().stream(), this.predicates.listElementIds().map(ResourceKey::location));
      }

      public Predicate<ItemStack> negate(Predicate<ItemStack> var1) {
         return var1.negate();
      }

      public Predicate<ItemStack> anyOf(List<Predicate<ItemStack>> var1) {
         return Util.anyOf(var1);
      }

      // $FF: synthetic method
      public Object anyOf(final List var1) {
         return this.anyOf(var1);
      }

      // $FF: synthetic method
      public Object createPredicateTest(final ImmutableStringReader var1, final Object var2, final Tag var3) throws CommandSyntaxException {
         return this.createPredicateTest(var1, (PredicateWrapper)var2, var3);
      }

      // $FF: synthetic method
      public Object lookupPredicateType(final ImmutableStringReader var1, final ResourceLocation var2) throws CommandSyntaxException {
         return this.lookupPredicateType(var1, var2);
      }

      // $FF: synthetic method
      public Object lookupComponentType(final ImmutableStringReader var1, final ResourceLocation var2) throws CommandSyntaxException {
         return this.lookupComponentType(var1, var2);
      }

      // $FF: synthetic method
      public Object forTagType(final ImmutableStringReader var1, final ResourceLocation var2) throws CommandSyntaxException {
         return this.forTagType(var1, var2);
      }

      // $FF: synthetic method
      public Object forElementType(final ImmutableStringReader var1, final ResourceLocation var2) throws CommandSyntaxException {
         return this.forElementType(var1, var2);
      }
   }

   public interface Result extends Predicate<ItemStack> {
   }

   private static record ComponentWrapper(ResourceLocation id, Predicate<ItemStack> presenceChecker, Decoder<? extends Predicate<ItemStack>> valueChecker) {
      final Predicate<ItemStack> presenceChecker;

      ComponentWrapper(ResourceLocation var1, Predicate<ItemStack> var2, Decoder<? extends Predicate<ItemStack>> var3) {
         super();
         this.id = var1;
         this.presenceChecker = var2;
         this.valueChecker = var3;
      }

      public static <T> ComponentWrapper create(ImmutableStringReader var0, ResourceLocation var1, DataComponentType<T> var2) throws CommandSyntaxException {
         Codec var3 = var2.codec();
         if (var3 == null) {
            throw ItemPredicateArgument.ERROR_UNKNOWN_COMPONENT.createWithContext(var0, var1);
         } else {
            return new ComponentWrapper(var1, (var1x) -> {
               return var1x.has(var2);
            }, var3.map((var1x) -> {
               return (var2x) -> {
                  Object var3 = var2x.get(var2);
                  return Objects.equals(var1x, var3);
               };
            }));
         }
      }

      public Predicate<ItemStack> decode(ImmutableStringReader var1, RegistryOps<Tag> var2, Tag var3) throws CommandSyntaxException {
         DataResult var4 = this.valueChecker.parse(var2, var3);
         return (Predicate)var4.getOrThrow((var2x) -> {
            return ItemPredicateArgument.ERROR_MALFORMED_COMPONENT.createWithContext(var1, this.id.toString(), var2x);
         });
      }

      public ResourceLocation id() {
         return this.id;
      }

      public Predicate<ItemStack> presenceChecker() {
         return this.presenceChecker;
      }

      public Decoder<? extends Predicate<ItemStack>> valueChecker() {
         return this.valueChecker;
      }
   }

   static record PredicateWrapper(ResourceLocation id, Decoder<? extends Predicate<ItemStack>> type) {
      public PredicateWrapper(Holder.Reference<ItemSubPredicate.Type<?>> var1) {
         this(var1.key().location(), ((ItemSubPredicate.Type)var1.value()).codec().map((var0) -> {
            Objects.requireNonNull(var0);
            return var0::matches;
         }));
      }

      PredicateWrapper(ResourceLocation var1, Decoder<? extends Predicate<ItemStack>> var2) {
         super();
         this.id = var1;
         this.type = var2;
      }

      public Predicate<ItemStack> decode(ImmutableStringReader var1, RegistryOps<Tag> var2, Tag var3) throws CommandSyntaxException {
         DataResult var4 = this.type.parse(var2, var3);
         return (Predicate)var4.getOrThrow((var2x) -> {
            return ItemPredicateArgument.ERROR_MALFORMED_PREDICATE.createWithContext(var1, this.id.toString(), var2x);
         });
      }

      public ResourceLocation id() {
         return this.id;
      }

      public Decoder<? extends Predicate<ItemStack>> type() {
         return this.type;
      }
   }
}
