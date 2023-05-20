package net.minecraft.server.commands.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.commands.arguments.NbtTagArgument;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class DataCommands {
   private static final SimpleCommandExceptionType ERROR_MERGE_UNCHANGED = new SimpleCommandExceptionType(Component.translatable("commands.data.merge.failed"));
   private static final DynamicCommandExceptionType ERROR_GET_NOT_NUMBER = new DynamicCommandExceptionType(
      var0 -> Component.translatable("commands.data.get.invalid", var0)
   );
   private static final DynamicCommandExceptionType ERROR_GET_NON_EXISTENT = new DynamicCommandExceptionType(
      var0 -> Component.translatable("commands.data.get.unknown", var0)
   );
   private static final SimpleCommandExceptionType ERROR_MULTIPLE_TAGS = new SimpleCommandExceptionType(Component.translatable("commands.data.get.multiple"));
   private static final DynamicCommandExceptionType ERROR_EXPECTED_OBJECT = new DynamicCommandExceptionType(
      var0 -> Component.translatable("commands.data.modify.expected_object", var0)
   );
   private static final DynamicCommandExceptionType ERROR_EXPECTED_VALUE = new DynamicCommandExceptionType(
      var0 -> Component.translatable("commands.data.modify.expected_value", var0)
   );
   public static final List<Function<String, DataCommands.DataProvider>> ALL_PROVIDERS = ImmutableList.of(
      EntityDataAccessor.PROVIDER, BlockDataAccessor.PROVIDER, StorageDataAccessor.PROVIDER
   );
   public static final List<DataCommands.DataProvider> TARGET_PROVIDERS = ALL_PROVIDERS.stream()
      .map(var0 -> var0.apply("target"))
      .collect(ImmutableList.toImmutableList());
   public static final List<DataCommands.DataProvider> SOURCE_PROVIDERS = ALL_PROVIDERS.stream()
      .map(var0 -> var0.apply("source"))
      .collect(ImmutableList.toImmutableList());

   public DataCommands() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      LiteralArgumentBuilder var1 = (LiteralArgumentBuilder)Commands.literal("data").requires(var0x -> var0x.hasPermission(2));

      for(DataCommands.DataProvider var3 : TARGET_PROVIDERS) {
         ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)var1.then(
                     var3.wrap(
                        Commands.literal("merge"),
                        var1x -> var1x.then(
                              Commands.argument("nbt", CompoundTagArgument.compoundTag())
                                 .executes(
                                    var1xx -> mergeData(
                                          (CommandSourceStack)var1xx.getSource(), var3.access(var1xx), CompoundTagArgument.getCompoundTag(var1xx, "nbt")
                                       )
                                 )
                           )
                     )
                  ))
                  .then(
                     var3.wrap(
                        Commands.literal("get"),
                        var1x -> var1x.executes(var1xx -> getData((CommandSourceStack)var1xx.getSource(), var3.access(var1xx)))
                              .then(
                                 ((RequiredArgumentBuilder)Commands.argument("path", NbtPathArgument.nbtPath())
                                       .executes(
                                          var1xx -> getData(
                                                (CommandSourceStack)var1xx.getSource(), var3.access(var1xx), NbtPathArgument.getPath(var1xx, "path")
                                             )
                                       ))
                                    .then(
                                       Commands.argument("scale", DoubleArgumentType.doubleArg())
                                          .executes(
                                             var1xx -> getNumeric(
                                                   (CommandSourceStack)var1xx.getSource(),
                                                   var3.access(var1xx),
                                                   NbtPathArgument.getPath(var1xx, "path"),
                                                   DoubleArgumentType.getDouble(var1xx, "scale")
                                                )
                                          )
                                    )
                              )
                     )
                  ))
               .then(
                  var3.wrap(
                     Commands.literal("remove"),
                     var1x -> var1x.then(
                           Commands.argument("path", NbtPathArgument.nbtPath())
                              .executes(
                                 var1xx -> removeData((CommandSourceStack)var1xx.getSource(), var3.access(var1xx), NbtPathArgument.getPath(var1xx, "path"))
                              )
                        )
                  )
               ))
            .then(
               decorateModification(
                  (var0x, var1x) -> var0x.then(
                           Commands.literal("insert")
                              .then(
                                 Commands.argument("index", IntegerArgumentType.integer())
                                    .then(
                                       var1x.create(
                                          (var0xx, var1xx, var2, var3x) -> var2.insert(IntegerArgumentType.getInteger(var0xx, "index"), var1xx, var3x)
                                       )
                                    )
                              )
                        )
                        .then(Commands.literal("prepend").then(var1x.create((var0xx, var1xx, var2, var3x) -> var2.insert(0, var1xx, var3x))))
                        .then(Commands.literal("append").then(var1x.create((var0xx, var1xx, var2, var3x) -> var2.insert(-1, var1xx, var3x))))
                        .then(Commands.literal("set").then(var1x.create((var0xx, var1xx, var2, var3x) -> var2.set(var1xx, (Tag)Iterables.getLast(var3x)))))
                        .then(Commands.literal("merge").then(var1x.create((var0xx, var1xx, var2, var3x) -> {
                           CompoundTag var4 = new CompoundTag();
            
                           for(Tag var6 : var3x) {
                              if (NbtPathArgument.NbtPath.isTooDeep(var6, 0)) {
                                 throw NbtPathArgument.ERROR_DATA_TOO_DEEP.create();
                              }
            
                              if (!(var6 instanceof CompoundTag)) {
                                 throw ERROR_EXPECTED_OBJECT.create(var6);
                              }
            
                              CompoundTag var7 = (CompoundTag)var6;
                              var4.merge(var7);
                           }
            
                           List var11 = var2.getOrCreate(var1xx, CompoundTag::new);
                           int var12 = 0;
            
                           for(Tag var8 : var11) {
                              if (!(var8 instanceof CompoundTag)) {
                                 throw ERROR_EXPECTED_OBJECT.create(var8);
                              }
            
                              CompoundTag var9 = (CompoundTag)var8;
                              CompoundTag var10 = var9.copy();
                              var9.merge(var4);
                              var12 += var10.equals(var9) ? 0 : 1;
                           }
            
                           return var12;
                        })))
               )
            );
      }

      var0.register(var1);
   }

   private static String getAsText(Tag var0) throws CommandSyntaxException {
      if (var0.getType().isValue()) {
         return var0.getAsString();
      } else {
         throw ERROR_EXPECTED_VALUE.create(var0);
      }
   }

   private static List<Tag> stringifyTagList(List<Tag> var0, Function<String, String> var1) throws CommandSyntaxException {
      ArrayList var2 = new ArrayList(var0.size());

      for(Tag var4 : var0) {
         String var5 = getAsText(var4);
         var2.add(StringTag.valueOf((String)var1.apply(var5)));
      }

      return var2;
   }

   private static ArgumentBuilder<CommandSourceStack, ?> decorateModification(
      BiConsumer<ArgumentBuilder<CommandSourceStack, ?>, DataCommands.DataManipulatorDecorator> var0
   ) {
      LiteralArgumentBuilder var1 = Commands.literal("modify");

      for(DataCommands.DataProvider var3 : TARGET_PROVIDERS) {
         var3.wrap(
            var1,
            var2 -> {
               RequiredArgumentBuilder var3x = Commands.argument("targetPath", NbtPathArgument.nbtPath());
   
               for(DataCommands.DataProvider var5 : SOURCE_PROVIDERS) {
                  var0.accept(
                     var3x,
                     var2x -> var5.wrap(
                           Commands.literal("from"),
                           var3xx -> var3xx.executes(var3xxx -> manipulateData(var3xxx, var3, var2x, getSingletonSource(var3xxx, var5)))
                                 .then(
                                    Commands.argument("sourcePath", NbtPathArgument.nbtPath())
                                       .executes(var3xxx -> manipulateData(var3xxx, var3, var2x, resolveSourcePath(var3xxx, var5)))
                                 )
                        )
                  );
                  var0.accept(
                     var3x,
                     var2x -> var5.wrap(
                           Commands.literal("string"),
                           var3xx -> var3xx.executes(
                                    var3xxx -> manipulateData(
                                          var3xxx, var3, var2x, stringifyTagList(getSingletonSource(var3xxx, var5), var0xxxxx -> var0xxxxx)
                                       )
                                 )
                                 .then(
                                    ((RequiredArgumentBuilder)Commands.argument("sourcePath", NbtPathArgument.nbtPath())
                                          .executes(
                                             var3xxx -> manipulateData(
                                                   var3xxx, var3, var2x, stringifyTagList(resolveSourcePath(var3xxx, var5), var0xxxxx -> var0xxxxx)
                                                )
                                          ))
                                       .then(
                                          ((RequiredArgumentBuilder)Commands.argument("start", IntegerArgumentType.integer(0))
                                                .executes(
                                                   var3xxx -> manipulateData(
                                                         var3xxx,
                                                         var3,
                                                         var2x,
                                                         stringifyTagList(
                                                            resolveSourcePath(var3xxx, var5),
                                                            var1xxxxx -> var1xxxxx.substring(IntegerArgumentType.getInteger(var3xxx, "start"))
                                                         )
                                                      )
                                                ))
                                             .then(
                                                Commands.argument("end", IntegerArgumentType.integer(0))
                                                   .executes(
                                                      var3xxx -> manipulateData(
                                                            var3xxx,
                                                            var3,
                                                            var2x,
                                                            stringifyTagList(
                                                               resolveSourcePath(var3xxx, var5),
                                                               var1xxxxx -> var1xxxxx.substring(
                                                                     IntegerArgumentType.getInteger(var3xxx, "start"),
                                                                     IntegerArgumentType.getInteger(var3xxx, "end")
                                                                  )
                                                            )
                                                         )
                                                   )
                                             )
                                       )
                                 )
                        )
                  );
               }
   
               var0.accept(var3x, var1xx -> Commands.literal("value").then(Commands.argument("value", NbtTagArgument.nbtTag()).executes(var2x -> {
                     List var3xx = Collections.singletonList(NbtTagArgument.getNbtTag(var2x, "value"));
                     return manipulateData(var2x, var3, var1xx, var3xx);
                  })));
               return var2.then(var3x);
            }
         );
      }

      return var1;
   }

   private static List<Tag> getSingletonSource(CommandContext<CommandSourceStack> var0, DataCommands.DataProvider var1) throws CommandSyntaxException {
      DataAccessor var2 = var1.access(var0);
      return Collections.singletonList(var2.getData());
   }

   private static List<Tag> resolveSourcePath(CommandContext<CommandSourceStack> var0, DataCommands.DataProvider var1) throws CommandSyntaxException {
      DataAccessor var2 = var1.access(var0);
      NbtPathArgument.NbtPath var3 = NbtPathArgument.getPath(var0, "sourcePath");
      return var3.get(var2.getData());
   }

   private static int manipulateData(
      CommandContext<CommandSourceStack> var0, DataCommands.DataProvider var1, DataCommands.DataManipulator var2, List<Tag> var3
   ) throws CommandSyntaxException {
      DataAccessor var4 = var1.access(var0);
      NbtPathArgument.NbtPath var5 = NbtPathArgument.getPath(var0, "targetPath");
      CompoundTag var6 = var4.getData();
      int var7 = var2.modify(var0, var6, var5, var3);
      if (var7 == 0) {
         throw ERROR_MERGE_UNCHANGED.create();
      } else {
         var4.setData(var6);
         ((CommandSourceStack)var0.getSource()).sendSuccess(var4.getModifiedSuccess(), true);
         return var7;
      }
   }

   private static int removeData(CommandSourceStack var0, DataAccessor var1, NbtPathArgument.NbtPath var2) throws CommandSyntaxException {
      CompoundTag var3 = var1.getData();
      int var4 = var2.remove(var3);
      if (var4 == 0) {
         throw ERROR_MERGE_UNCHANGED.create();
      } else {
         var1.setData(var3);
         var0.sendSuccess(var1.getModifiedSuccess(), true);
         return var4;
      }
   }

   private static Tag getSingleTag(NbtPathArgument.NbtPath var0, DataAccessor var1) throws CommandSyntaxException {
      List var2 = var0.get(var1.getData());
      Iterator var3 = var2.iterator();
      Tag var4 = (Tag)var3.next();
      if (var3.hasNext()) {
         throw ERROR_MULTIPLE_TAGS.create();
      } else {
         return var4;
      }
   }

   private static int getData(CommandSourceStack var0, DataAccessor var1, NbtPathArgument.NbtPath var2) throws CommandSyntaxException {
      Tag var3 = getSingleTag(var2, var1);
      int var4;
      if (var3 instanceof NumericTag) {
         var4 = Mth.floor(((NumericTag)var3).getAsDouble());
      } else if (var3 instanceof CollectionTag) {
         var4 = ((CollectionTag)var3).size();
      } else if (var3 instanceof CompoundTag) {
         var4 = ((CompoundTag)var3).size();
      } else {
         if (!(var3 instanceof StringTag)) {
            throw ERROR_GET_NON_EXISTENT.create(var2.toString());
         }

         var4 = var3.getAsString().length();
      }

      var0.sendSuccess(var1.getPrintSuccess(var3), false);
      return var4;
   }

   private static int getNumeric(CommandSourceStack var0, DataAccessor var1, NbtPathArgument.NbtPath var2, double var3) throws CommandSyntaxException {
      Tag var5 = getSingleTag(var2, var1);
      if (!(var5 instanceof NumericTag)) {
         throw ERROR_GET_NOT_NUMBER.create(var2.toString());
      } else {
         int var6 = Mth.floor(((NumericTag)var5).getAsDouble() * var3);
         var0.sendSuccess(var1.getPrintSuccess(var2, var3, var6), false);
         return var6;
      }
   }

   private static int getData(CommandSourceStack var0, DataAccessor var1) throws CommandSyntaxException {
      var0.sendSuccess(var1.getPrintSuccess(var1.getData()), false);
      return 1;
   }

   private static int mergeData(CommandSourceStack var0, DataAccessor var1, CompoundTag var2) throws CommandSyntaxException {
      CompoundTag var3 = var1.getData();
      if (NbtPathArgument.NbtPath.isTooDeep(var2, 0)) {
         throw NbtPathArgument.ERROR_DATA_TOO_DEEP.create();
      } else {
         CompoundTag var4 = var3.copy().merge(var2);
         if (var3.equals(var4)) {
            throw ERROR_MERGE_UNCHANGED.create();
         } else {
            var1.setData(var4);
            var0.sendSuccess(var1.getModifiedSuccess(), true);
            return 1;
         }
      }
   }

   interface DataManipulator {
      int modify(CommandContext<CommandSourceStack> var1, CompoundTag var2, NbtPathArgument.NbtPath var3, List<Tag> var4) throws CommandSyntaxException;
   }

   interface DataManipulatorDecorator {
      ArgumentBuilder<CommandSourceStack, ?> create(DataCommands.DataManipulator var1);
   }

   public interface DataProvider {
      DataAccessor access(CommandContext<CommandSourceStack> var1) throws CommandSyntaxException;

      ArgumentBuilder<CommandSourceStack, ?> wrap(
         ArgumentBuilder<CommandSourceStack, ?> var1, Function<ArgumentBuilder<CommandSourceStack, ?>, ArgumentBuilder<CommandSourceStack, ?>> var2
      );
   }
}
