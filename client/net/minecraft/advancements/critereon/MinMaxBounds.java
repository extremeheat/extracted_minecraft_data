package net.minecraft.advancements.critereon;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;

public interface MinMaxBounds<T extends Number> {
   SimpleCommandExceptionType ERROR_EMPTY = new SimpleCommandExceptionType(Component.translatable("argument.range.empty"));
   SimpleCommandExceptionType ERROR_SWAPPED = new SimpleCommandExceptionType(Component.translatable("argument.range.swapped"));

   Optional<T> min();

   Optional<T> max();

   default boolean isAny() {
      return this.min().isEmpty() && this.max().isEmpty();
   }

   default Optional<T> unwrapPoint() {
      Optional var1 = this.min();
      Optional var2 = this.max();
      return var1.equals(var2) ? var1 : Optional.empty();
   }

   static <T extends Number, R extends MinMaxBounds<T>> Codec<R> createCodec(Codec<T> var0, MinMaxBounds.BoundsFactory<T, R> var1) {
      Codec var2 = RecordCodecBuilder.create(
         var2x -> var2x.group(var0.optionalFieldOf("min").forGetter(MinMaxBounds::min), var0.optionalFieldOf("max").forGetter(MinMaxBounds::max))
               .apply(var2x, var1::create)
      );
      return Codec.either(var2, var0)
         .xmap(var1x -> (MinMaxBounds)var1x.map(var0xx -> var0xx, var1xx -> var1.create(Optional.of(var1xx), Optional.of(var1xx))), var0x -> {
            Optional var1x = var0x.unwrapPoint();
            return var1x.isPresent() ? Either.right((Number)var1x.get()) : Either.left(var0x);
         });
   }

   static <T extends Number, R extends MinMaxBounds<T>> R fromReader(
      StringReader var0,
      MinMaxBounds.BoundsFromReaderFactory<T, R> var1,
      Function<String, T> var2,
      Supplier<DynamicCommandExceptionType> var3,
      Function<T, T> var4
   ) throws CommandSyntaxException {
      if (!var0.canRead()) {
         throw ERROR_EMPTY.createWithContext(var0);
      } else {
         int var5 = var0.getCursor();

         try {
            Optional var6 = readNumber(var0, var2, var3).map(var4);
            Optional var7;
            if (var0.canRead(2) && var0.peek() == '.' && var0.peek(1) == '.') {
               var0.skip();
               var0.skip();
               var7 = readNumber(var0, var2, var3).map(var4);
               if (var6.isEmpty() && var7.isEmpty()) {
                  throw ERROR_EMPTY.createWithContext(var0);
               }
            } else {
               var7 = var6;
            }

            if (var6.isEmpty() && var7.isEmpty()) {
               throw ERROR_EMPTY.createWithContext(var0);
            } else {
               return (R)var1.create(var0, var6, var7);
            }
         } catch (CommandSyntaxException var8) {
            var0.setCursor(var5);
            throw new CommandSyntaxException(var8.getType(), var8.getRawMessage(), var8.getInput(), var5);
         }
      }
   }

   private static <T extends Number> Optional<T> readNumber(StringReader var0, Function<String, T> var1, Supplier<DynamicCommandExceptionType> var2) throws CommandSyntaxException {
      int var3 = var0.getCursor();

      while (var0.canRead() && isAllowedInputChat(var0)) {
         var0.skip();
      }

      String var4 = var0.getString().substring(var3, var0.getCursor());
      if (var4.isEmpty()) {
         return Optional.empty();
      } else {
         try {
            return Optional.of((T)var1.apply(var4));
         } catch (NumberFormatException var6) {
            throw ((DynamicCommandExceptionType)var2.get()).createWithContext(var0, var4);
         }
      }
   }

   private static boolean isAllowedInputChat(StringReader var0) {
      char var1 = var0.peek();
      if ((var1 < '0' || var1 > '9') && var1 != '-') {
         return var1 != '.' ? false : !var0.canRead(2) || var0.peek(1) != '.';
      } else {
         return true;
      }
   }

   @FunctionalInterface
   public interface BoundsFactory<T extends Number, R extends MinMaxBounds<T>> {
      R create(Optional<T> var1, Optional<T> var2);
   }

   @FunctionalInterface
   public interface BoundsFromReaderFactory<T extends Number, R extends MinMaxBounds<T>> {
      R create(StringReader var1, Optional<T> var2, Optional<T> var3) throws CommandSyntaxException;
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
