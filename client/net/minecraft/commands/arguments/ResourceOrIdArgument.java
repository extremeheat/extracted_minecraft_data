package net.minecraft.commands.arguments;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.Codec;
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
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
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
   public static final DynamicCommandExceptionType ERROR_FAILED_TO_PARSE = new DynamicCommandExceptionType(
      var0 -> Component.translatableEscape("argument.resource_or_id.failed_to_parse", var0)
   );
   private static final SimpleCommandExceptionType ERROR_INVALID = new SimpleCommandExceptionType(Component.translatable("argument.resource_or_id.invalid"));
   private final HolderLookup.Provider registryLookup;
   private final boolean hasRegistry;
   private final Codec<Holder<T>> codec;

   protected ResourceOrIdArgument(CommandBuildContext var1, ResourceKey<Registry<T>> var2, Codec<Holder<T>> var3) {
      super();
      this.registryLookup = var1;
      this.hasRegistry = var1.lookup(var2).isPresent();
      this.codec = var3;
   }

   public static ResourceOrIdArgument.LootTableArgument lootTable(CommandBuildContext var0) {
      return new ResourceOrIdArgument.LootTableArgument(var0);
   }

   public static Holder<LootTable> getLootTable(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      return getResource(var0, var1);
   }

   public static ResourceOrIdArgument.LootModifierArgument lootModifier(CommandBuildContext var0) {
      return new ResourceOrIdArgument.LootModifierArgument(var0);
   }

   public static Holder<LootItemFunction> getLootModifier(CommandContext<CommandSourceStack> var0, String var1) {
      return getResource(var0, var1);
   }

   public static ResourceOrIdArgument.LootPredicateArgument lootPredicate(CommandBuildContext var0) {
      return new ResourceOrIdArgument.LootPredicateArgument(var0);
   }

   public static Holder<LootItemCondition> getLootPredicate(CommandContext<CommandSourceStack> var0, String var1) {
      return getResource(var0, var1);
   }

   private static <T> Holder<T> getResource(CommandContext<CommandSourceStack> var0, String var1) {
      return (Holder<T>)var0.getArgument(var1, Holder.class);
   }

   @Nullable
   public Holder<T> parse(StringReader var1) throws CommandSyntaxException {
      Tag var2 = parseInlineOrId(var1);
      if (!this.hasRegistry) {
         return null;
      } else {
         RegistryOps var3 = this.registryLookup.createSerializationContext(NbtOps.INSTANCE);
         return (Holder<T>)this.codec.parse(var3, var2).getOrThrow(var1x -> ERROR_FAILED_TO_PARSE.createWithContext(var1, var1x));
      }
   }

   @VisibleForTesting
   static Tag parseInlineOrId(StringReader var0) throws CommandSyntaxException {
      int var1 = var0.getCursor();
      Tag var2 = new TagParser(var0).readValue();
      if (hasConsumedWholeArg(var0)) {
         return var2;
      } else {
         var0.setCursor(var1);
         ResourceLocation var3 = ResourceLocation.read(var0);
         if (hasConsumedWholeArg(var0)) {
            return StringTag.valueOf(var3.toString());
         } else {
            var0.setCursor(var1);
            throw ERROR_INVALID.createWithContext(var0);
         }
      }
   }

   private static boolean hasConsumedWholeArg(StringReader var0) {
      return !var0.canRead() || var0.peek() == ' ';
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public static class LootModifierArgument extends ResourceOrIdArgument<LootItemFunction> {
      protected LootModifierArgument(CommandBuildContext var1) {
         super(var1, Registries.ITEM_MODIFIER, LootItemFunctions.CODEC);
      }
   }

   public static class LootPredicateArgument extends ResourceOrIdArgument<LootItemCondition> {
      protected LootPredicateArgument(CommandBuildContext var1) {
         super(var1, Registries.PREDICATE, LootItemCondition.CODEC);
      }
   }

   public static class LootTableArgument extends ResourceOrIdArgument<LootTable> {
      protected LootTableArgument(CommandBuildContext var1) {
         super(var1, Registries.LOOT_TABLE, LootTable.CODEC);
      }
   }
}
