package net.minecraft.server.commands.data;

import com.google.common.collect.Iterables;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.commands.arguments.NbtTagArgument;
import net.minecraft.commands.arguments.ResourceOrIdArgument;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.PrimitiveTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.ArgProvider;
import net.minecraft.server.commands.LootContextSources;
import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class DataCommands {
   private static final SimpleCommandExceptionType ERROR_MERGE_UNCHANGED = new SimpleCommandExceptionType(Component.translatable("commands.data.merge.failed"));
   private static final DynamicCommandExceptionType ERROR_GET_NOT_NUMBER = new DynamicCommandExceptionType((path) -> Component.translatableEscape("commands.data.get.invalid", path));
   private static final DynamicCommandExceptionType ERROR_GET_NON_EXISTENT = new DynamicCommandExceptionType((path) -> Component.translatableEscape("commands.data.get.unknown", path));
   private static final SimpleCommandExceptionType ERROR_MULTIPLE_TAGS = new SimpleCommandExceptionType(Component.translatable("commands.data.get.multiple"));
   private static final DynamicCommandExceptionType ERROR_EXPECTED_OBJECT = new DynamicCommandExceptionType((node) -> Component.translatableEscape("commands.data.modify.expected_object", node));
   private static final DynamicCommandExceptionType ERROR_EXPECTED_VALUE = new DynamicCommandExceptionType((node) -> Component.translatableEscape("commands.data.modify.expected_value", node));
   private static final Dynamic2CommandExceptionType ERROR_INVALID_SUBSTRING = new Dynamic2CommandExceptionType((start, end) -> Component.translatableEscape("commands.data.modify.invalid_substring", start, end));
   public static final List<ArgProvider.Factory<DataAccessor>> ALL_PROVIDERS;
   public static final List<ArgProvider<DataAccessor>> TARGET_PROVIDERS;
   public static final List<ArgProvider<DataAccessor>> SOURCE_PROVIDERS;

   public DataCommands() {
      super();
   }

   public static void register(final CommandDispatcher<CommandSourceStack> dispatcher, final CommandBuildContext buildContext) {
      LiteralArgumentBuilder<CommandSourceStack> root = (LiteralArgumentBuilder)Commands.literal("data").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS));

      for(ArgProvider<DataAccessor> targetProvider : TARGET_PROVIDERS) {
         ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)root.then(targetProvider.wrap(Commands.literal("merge"), (p) -> p.then(Commands.argument("nbt", CompoundTagArgument.compoundTag()).executes((c) -> mergeData((CommandSourceStack)c.getSource(), targetProvider.access(c), CompoundTagArgument.getCompoundTag(c, "nbt"))))))).then(targetProvider.wrap(Commands.literal("get"), (p) -> p.executes((c) -> getData((CommandSourceStack)c.getSource(), targetProvider.access(c))).then(((RequiredArgumentBuilder)Commands.argument("path", NbtPathArgument.nbtPath()).executes((c) -> getData((CommandSourceStack)c.getSource(), targetProvider.access(c), NbtPathArgument.getPath(c, "path")))).then(Commands.argument("scale", DoubleArgumentType.doubleArg()).executes((c) -> getNumeric((CommandSourceStack)c.getSource(), targetProvider.access(c), NbtPathArgument.getPath(c, "path"), DoubleArgumentType.getDouble(c, "scale")))))))).then(targetProvider.wrap(Commands.literal("remove"), (p) -> p.then(Commands.argument("path", NbtPathArgument.nbtPath()).executes((c) -> removeData((CommandSourceStack)c.getSource(), targetProvider.access(c), NbtPathArgument.getPath(c, "path"))))))).then(decorateModification(buildContext, (parent, rest) -> parent.then(Commands.literal("insert").then(Commands.argument("index", IntegerArgumentType.integer()).then(rest.create((context, target, targetPath, source) -> targetPath.insert(IntegerArgumentType.getInteger(context, "index"), target, source))))).then(Commands.literal("prepend").then(rest.create((context, target, targetPath, source) -> targetPath.insert(0, target, source)))).then(Commands.literal("append").then(rest.create((context, target, targetPath, source) -> targetPath.insert(-1, target, source)))).then(Commands.literal("set").then(rest.create((context, target, targetPath, source) -> targetPath.set(target, (Tag)Iterables.getLast(source))))).then(Commands.literal("merge").then(rest.create((context, target, targetPath, source) -> {
               CompoundTag combinedSources = new CompoundTag();

               for(Tag sourceTag : source) {
                  if (NbtPathArgument.NbtPath.isTooDeep(sourceTag, 0)) {
                     throw NbtPathArgument.ERROR_DATA_TOO_DEEP.create();
                  }

                  if (!(sourceTag instanceof CompoundTag)) {
                     throw ERROR_EXPECTED_OBJECT.create(sourceTag);
                  }

                  CompoundTag tag = (CompoundTag)sourceTag;
                  combinedSources.merge(tag);
               }

               Collection<Tag> targets = targetPath.getOrCreate(target, CompoundTag::new);
               int changedCount = 0;

               for(Tag targetTag : targets) {
                  if (!(targetTag instanceof CompoundTag)) {
                     throw ERROR_EXPECTED_OBJECT.create(targetTag);
                  }

                  CompoundTag targetObject = (CompoundTag)targetTag;
                  CompoundTag originalTarget = targetObject.copy();
                  targetObject.merge(combinedSources);
                  changedCount += originalTarget.equals(targetObject) ? 0 : 1;
               }

               return changedCount;
            })))));
      }

      dispatcher.register(root);
   }

   private static String getAsText(final Tag tag) throws CommandSyntaxException {
      Objects.requireNonNull(tag);
      byte var2 = 0;
      String var10000;
      //$FF: var2->value
      //0->net/minecraft/nbt/StringTag
      //1->net/minecraft/nbt/PrimitiveTag
      switch (tag.typeSwitch<invokedynamic>(tag, var2)) {
         case 0:
            StringTag var3 = (StringTag)tag;
            StringTag var8 = var3;

            try {
               var9 = var8.value();
            } catch (Throwable var6) {
               throw new MatchException(var6.toString(), var6);
            }

            String value = var9;
            var10000 = value;
            break;
         case 1:
            PrimitiveTag primitiveTag = (PrimitiveTag)tag;
            var10000 = primitiveTag.toString();
            break;
         default:
            throw ERROR_EXPECTED_VALUE.create(tag);
      }

      return var10000;
   }

   private static List<Tag> stringifyTagList(final List<Tag> source, final StringProcessor stringProcessor) throws CommandSyntaxException {
      List<Tag> result = new ArrayList(source.size());

      for(Tag tag : source) {
         String text = getAsText(tag);
         result.add(StringTag.valueOf(stringProcessor.process(text)));
      }

      return result;
   }

   private static ArgumentBuilder<CommandSourceStack, ?> decorateModification(final CommandBuildContext buildContext, final BiConsumer<ArgumentBuilder<CommandSourceStack, ?>, DataManipulatorDecorator> nodeSupplier) {
      LiteralArgumentBuilder<CommandSourceStack> modify = Commands.literal("modify");

      for(ArgProvider<DataAccessor> targetProvider : TARGET_PROVIDERS) {
         targetProvider.wrap(modify, (t) -> {
            ArgumentBuilder<CommandSourceStack, ?> targetPathNode = Commands.argument("targetPath", NbtPathArgument.nbtPath());

            for(ArgProvider<DataAccessor> sourceProvider : SOURCE_PROVIDERS) {
               nodeSupplier.accept(targetPathNode, (DataManipulatorDecorator)(manipulator) -> sourceProvider.wrap(Commands.literal("from"), (s) -> s.executes((c) -> manipulateData(c, targetProvider, manipulator, getSingletonSource(c, sourceProvider))).then(Commands.argument("sourcePath", NbtPathArgument.nbtPath()).executes((c) -> manipulateData(c, targetProvider, manipulator, resolveSourcePath(c, sourceProvider))))));
               nodeSupplier.accept(targetPathNode, (DataManipulatorDecorator)(manipulator) -> sourceProvider.wrap(Commands.literal("string"), (s) -> s.executes((c) -> manipulateData(c, targetProvider, manipulator, stringifyTagList(getSingletonSource(c, sourceProvider), (str) -> str))).then(((RequiredArgumentBuilder)Commands.argument("sourcePath", NbtPathArgument.nbtPath()).executes((c) -> manipulateData(c, targetProvider, manipulator, stringifyTagList(resolveSourcePath(c, sourceProvider), (str) -> str)))).then(((RequiredArgumentBuilder)Commands.argument("start", IntegerArgumentType.integer()).executes((c) -> manipulateData(c, targetProvider, manipulator, stringifyTagList(resolveSourcePath(c, sourceProvider), (str) -> substring(str, IntegerArgumentType.getInteger(c, "start")))))).then(Commands.argument("end", IntegerArgumentType.integer()).executes((c) -> manipulateData(c, targetProvider, manipulator, stringifyTagList(resolveSourcePath(c, sourceProvider), (str) -> substring(str, IntegerArgumentType.getInteger(c, "start"), IntegerArgumentType.getInteger(c, "end"))))))))));
            }

            nodeSupplier.accept(targetPathNode, (DataManipulatorDecorator)(manipulator) -> LootContextSources.addContextSources(Commands.literal("compute"), (contextDecorator) -> ((RequiredArgumentBuilder)Commands.argument("provider", ResourceOrIdArgument.numberProvider(buildContext)).executes((c) -> {
                     LootContext lootContext = contextDecorator.createContext(c);
                     Holder<NumberProvider> provider = ResourceOrIdArgument.getNumberProvider(c, "provider");
                     float value = ((NumberProvider)provider.value()).getFloat(lootContext);
                     return manipulateData(c, targetProvider, manipulator, List.of(FloatTag.valueOf(value)));
                  })).then(Commands.literal("integer").executes((c) -> {
                     LootContext lootContext = contextDecorator.createContext(c);
                     Holder<NumberProvider> provider = ResourceOrIdArgument.getNumberProvider(c, "provider");
                     int value = ((NumberProvider)provider.value()).getInt(lootContext);
                     return manipulateData(c, targetProvider, manipulator, List.of(IntTag.valueOf(value)));
                  }))));
            nodeSupplier.accept(targetPathNode, (DataManipulatorDecorator)(manipulator) -> Commands.literal("value").then(Commands.argument("value", NbtTagArgument.nbtTag()).executes((c) -> {
                  List<Tag> source = Collections.singletonList(NbtTagArgument.getNbtTag(c, "value"));
                  return manipulateData(c, targetProvider, manipulator, source);
               })));
            return t.then(targetPathNode);
         });
      }

      return modify;
   }

   private static String validatedSubstring(final String input, final int start, final int end) throws CommandSyntaxException {
      if (start >= 0 && end <= input.length() && start <= end) {
         return input.substring(start, end);
      } else {
         throw ERROR_INVALID_SUBSTRING.create(start, end);
      }
   }

   private static String substring(final String input, final int start, final int end) throws CommandSyntaxException {
      int length = input.length();
      int absoluteStart = getOffset(start, length);
      int absoluteEnd = getOffset(end, length);
      return validatedSubstring(input, absoluteStart, absoluteEnd);
   }

   private static String substring(final String input, final int start) throws CommandSyntaxException {
      int length = input.length();
      return validatedSubstring(input, getOffset(start, length), length);
   }

   private static int getOffset(final int index, final int length) {
      return index >= 0 ? index : length + index;
   }

   private static List<Tag> getSingletonSource(final CommandContext<CommandSourceStack> context, final ArgProvider<DataAccessor> sourceProvider) throws CommandSyntaxException {
      DataAccessor source = sourceProvider.access(context);
      return Collections.singletonList(source.getData());
   }

   private static List<Tag> resolveSourcePath(final CommandContext<CommandSourceStack> context, final ArgProvider<DataAccessor> sourceProvider) throws CommandSyntaxException {
      DataAccessor source = sourceProvider.access(context);
      NbtPathArgument.NbtPath sourcePath = NbtPathArgument.getPath(context, "sourcePath");
      return sourcePath.get(source.getData());
   }

   private static int manipulateData(final CommandContext<CommandSourceStack> context, final ArgProvider<DataAccessor> targetProvider, final DataManipulator manipulator, final List<Tag> source) throws CommandSyntaxException {
      DataAccessor target = targetProvider.access(context);
      NbtPathArgument.NbtPath targetPath = NbtPathArgument.getPath(context, "targetPath");
      CompoundTag targetData = target.getData();
      int result = manipulator.modify(context, targetData, targetPath, source);
      if (result == 0) {
         throw ERROR_MERGE_UNCHANGED.create();
      } else {
         target.setData(targetData);
         ((CommandSourceStack)context.getSource()).sendSuccess(() -> target.getModifiedSuccess(), true);
         return result;
      }
   }

   private static int removeData(final CommandSourceStack source, final DataAccessor accessor, final NbtPathArgument.NbtPath path) throws CommandSyntaxException {
      CompoundTag result = accessor.getData();
      int count = path.remove(result);
      if (count == 0) {
         throw ERROR_MERGE_UNCHANGED.create();
      } else {
         accessor.setData(result);
         source.sendSuccess(() -> accessor.getModifiedSuccess(), true);
         return count;
      }
   }

   public static Tag getSingleTag(final NbtPathArgument.NbtPath path, final DataAccessor accessor) throws CommandSyntaxException {
      Collection<Tag> tags = path.get(accessor.getData());
      Iterator<Tag> iterator = tags.iterator();
      Tag result = (Tag)iterator.next();
      if (iterator.hasNext()) {
         throw ERROR_MULTIPLE_TAGS.create();
      } else {
         return result;
      }
   }

   private static int getData(final CommandSourceStack source, final DataAccessor accessor, final NbtPathArgument.NbtPath path) throws CommandSyntaxException {
      Tag tag = getSingleTag(path, accessor);
      Objects.requireNonNull(tag);
      byte var6 = 0;
      int var16;
      //$FF: var6->value
      //0->net/minecraft/nbt/NumericTag
      //1->net/minecraft/nbt/CollectionTag
      //2->net/minecraft/nbt/CompoundTag
      //3->net/minecraft/nbt/StringTag
      //4->net/minecraft/nbt/EndTag
      switch (tag.typeSwitch<invokedynamic>(tag, var6)) {
         case 0:
            NumericTag numericTag = (NumericTag)tag;
            var16 = Mth.floor(numericTag.doubleValue());
            break;
         case 1:
            CollectionTag collectionTag = (CollectionTag)tag;
            var16 = collectionTag.size();
            break;
         case 2:
            CompoundTag compoundTag = (CompoundTag)tag;
            var16 = compoundTag.size();
            break;
         case 3:
            StringTag var10 = (StringTag)tag;
            StringTag var10000 = var10;

            try {
               var15 = var10000.value();
            } catch (Throwable var13) {
               throw new MatchException(var13.toString(), var13);
            }

            String value = var15;
            var16 = value.length();
            break;
         case 4:
            EndTag ignored = (EndTag)tag;
            throw ERROR_GET_NON_EXISTENT.create(path.toString());
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      int result = var16;
      source.sendSuccess(() -> accessor.getPrintSuccess(tag), false);
      return result;
   }

   private static int getNumeric(final CommandSourceStack source, final DataAccessor accessor, final NbtPathArgument.NbtPath path, final double scale) throws CommandSyntaxException {
      Tag tag = getSingleTag(path, accessor);
      if (tag instanceof NumericTag numericTag) {
         int result = Mth.floor(numericTag.doubleValue() * scale);
         source.sendSuccess(() -> accessor.getPrintSuccess(path, scale, result), false);
         return result;
      } else {
         throw ERROR_GET_NOT_NUMBER.create(path.toString());
      }
   }

   private static int getData(final CommandSourceStack source, final DataAccessor accessor) throws CommandSyntaxException {
      CompoundTag data = accessor.getData();
      source.sendSuccess(() -> accessor.getPrintSuccess(data), false);
      return 1;
   }

   private static int mergeData(final CommandSourceStack source, final DataAccessor accessor, final CompoundTag nbt) throws CommandSyntaxException {
      CompoundTag old = accessor.getData();
      if (NbtPathArgument.NbtPath.isTooDeep(nbt, 0)) {
         throw NbtPathArgument.ERROR_DATA_TOO_DEEP.create();
      } else {
         CompoundTag result = old.copy().merge(nbt);
         if (old.equals(result)) {
            throw ERROR_MERGE_UNCHANGED.create();
         } else {
            accessor.setData(result);
            source.sendSuccess(() -> accessor.getModifiedSuccess(), true);
            return 1;
         }
      }
   }

   static {
      ALL_PROVIDERS = List.of(EntityDataAccessor.PROVIDER, BlockDataAccessor.PROVIDER, StorageDataAccessor.PROVIDER);
      TARGET_PROVIDERS = ArgProvider.buildList("target", ALL_PROVIDERS);
      SOURCE_PROVIDERS = ArgProvider.buildList("source", ALL_PROVIDERS);
   }

   @FunctionalInterface
   private interface DataManipulator {
      int modify(CommandContext<CommandSourceStack> context, CompoundTag targetData, NbtPathArgument.NbtPath targetPath, List<Tag> source) throws CommandSyntaxException;
   }

   @FunctionalInterface
   private interface DataManipulatorDecorator {
      ArgumentBuilder<CommandSourceStack, ?> create(DataManipulator manipulator);
   }

   @FunctionalInterface
   private interface StringProcessor {
      String process(String string) throws CommandSyntaxException;
   }
}
