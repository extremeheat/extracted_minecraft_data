package net.minecraft.commands.arguments;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ResourceOrIdArgument<T> implements ArgumentType<Holder<T>> {
   private static final Collection<String> EXAMPLES = List.of("foo", "foo:bar", "012", "{}", "true");
   public static final DynamicCommandExceptionType ERROR_FAILED_TO_PARSE = new DynamicCommandExceptionType((var0) -> Component.translatableEscape("argument.resource_or_id.failed_to_parse", var0));
   private static final SimpleCommandExceptionType ERROR_INVALID = new SimpleCommandExceptionType(Component.translatable("argument.resource_or_id.invalid"));
   private static final TagParser<?> VALUE_PARSER;
   private final HolderLookup.Provider registryLookup;
   private final boolean hasRegistry;
   private final Codec<Holder<T>> codec;

   protected ResourceOrIdArgument(CommandBuildContext var1, ResourceKey<Registry<T>> var2, Codec<Holder<T>> var3) {
      super();
      this.registryLookup = var1;
      this.hasRegistry = var1.lookup(var2).isPresent();
      this.codec = var3;
   }

   public static LootTableArgument lootTable(CommandBuildContext var0) {
      return new LootTableArgument(var0);
   }

   public static Holder<LootTable> getLootTable(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      return getResource(var0, var1);
   }

   public static LootModifierArgument lootModifier(CommandBuildContext var0) {
      return new LootModifierArgument(var0);
   }

   public static Holder<LootItemFunction> getLootModifier(CommandContext<CommandSourceStack> var0, String var1) {
      return getResource(var0, var1);
   }

   public static LootPredicateArgument lootPredicate(CommandBuildContext var0) {
      return new LootPredicateArgument(var0);
   }

   public static Holder<LootItemCondition> getLootPredicate(CommandContext<CommandSourceStack> var0, String var1) {
      return getResource(var0, var1);
   }

   private static <T> Holder<T> getResource(CommandContext<CommandSourceStack> var0, String var1) {
      return (Holder)var0.getArgument(var1, Holder.class);
   }

   @Nullable
   public Holder<T> parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1, VALUE_PARSER);
   }

   @Nullable
   private <O> Holder<T> parse(StringReader var1, TagParser<O> var2) throws CommandSyntaxException {
      RegistryOps var3 = this.registryLookup.createSerializationContext(var2.getOps());
      Dynamic var4 = parseInlineOrId(var3, var2, var1);
      return !this.hasRegistry ? null : (Holder)this.codec.parse(var4).getOrThrow((var1x) -> ERROR_FAILED_TO_PARSE.createWithContext(var1, var1x));
   }

   @VisibleForTesting
   static <T> Dynamic<T> parseInlineOrId(DynamicOps<T> var0, TagParser<T> var1, StringReader var2) throws CommandSyntaxException {
      int var3 = var2.getCursor();
      Object var4 = var1.parseAsArgument(var2);
      if (hasConsumedWholeArg(var2)) {
         return new Dynamic(var0, var4);
      } else {
         var2.setCursor(var3);
         ResourceLocation var5 = ResourceLocation.read(var2);
         if (hasConsumedWholeArg(var2)) {
            return new Dynamic(var0, var0.createString(var5.toString()));
         } else {
            var2.setCursor(var3);
            throw ERROR_INVALID.createWithContext(var2);
         }
      }
   }

   private static boolean hasConsumedWholeArg(StringReader var0) {
      return !var0.canRead() || var0.peek() == ' ';
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   @Nullable
   public Object parse(final StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }

   static {
      VALUE_PARSER = TagParser.create(NbtOps.INSTANCE);
   }

   public static class LootTableArgument extends ResourceOrIdArgument<LootTable> {
      protected LootTableArgument(CommandBuildContext var1) {
         super(var1, Registries.LOOT_TABLE, LootTable.CODEC);
      }

      // $FF: synthetic method
      @Nullable
      public Object parse(final StringReader var1) throws CommandSyntaxException {
         return super.parse(var1);
      }
   }

   public static class LootModifierArgument extends ResourceOrIdArgument<LootItemFunction> {
      protected LootModifierArgument(CommandBuildContext var1) {
         super(var1, Registries.ITEM_MODIFIER, LootItemFunctions.CODEC);
      }

      // $FF: synthetic method
      @Nullable
      public Object parse(final StringReader var1) throws CommandSyntaxException {
         return super.parse(var1);
      }
   }

   public static class LootPredicateArgument extends ResourceOrIdArgument<LootItemCondition> {
      protected LootPredicateArgument(CommandBuildContext var1) {
         super(var1, Registries.PREDICATE, LootItemCondition.CODEC);
      }

      // $FF: synthetic method
      @Nullable
      public Object parse(final StringReader var1) throws CommandSyntaxException {
         return super.parse(var1);
      }
   }
}
