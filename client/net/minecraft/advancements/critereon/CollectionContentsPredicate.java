package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Predicate;

public interface CollectionContentsPredicate<T, P extends Predicate<T>> extends Predicate<Iterable<T>> {
   List<P> unpack();

   static <T, P extends Predicate<T>> Codec<CollectionContentsPredicate<T, P>> codec(Codec<P> var0) {
      return var0.listOf().xmap(CollectionContentsPredicate::of, CollectionContentsPredicate::unpack);
   }

   @SafeVarargs
   static <T, P extends Predicate<T>> CollectionContentsPredicate<T, P> of(P... var0) {
      return of(List.of((P[])var0));
   }

   static <T, P extends Predicate<T>> CollectionContentsPredicate<T, P> of(List<P> var0) {
      return (CollectionContentsPredicate<T, P>)(switch (var0.size()) {
         case 0 -> new CollectionContentsPredicate.Zero();
         case 1 -> new CollectionContentsPredicate.Single((P)var0.getFirst());
         default -> new CollectionContentsPredicate.Multiple(var0);
      });
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

   public static class Zero<T, P extends Predicate<T>> implements CollectionContentsPredicate<T, P> {
      public Zero() {
         super();
      }

      public boolean test(Iterable<T> var1) {
         return true;
      }

      @Override
      public List<P> unpack() {
         return List.of();
      }
   }
}
