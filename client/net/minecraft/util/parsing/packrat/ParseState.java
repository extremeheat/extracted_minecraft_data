package net.minecraft.util.parsing.packrat;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;

public abstract class ParseState<S> {
   private final Map<ParseState.CacheKey<?>, ParseState.CacheEntry<?>> ruleCache = new HashMap<>();
   private final Dictionary<S> dictionary;
   private final ErrorCollector<S> errorCollector;

   protected ParseState(Dictionary<S> var1, ErrorCollector<S> var2) {
      super();
      this.dictionary = var1;
      this.errorCollector = var2;
   }

   public ErrorCollector<S> errorCollector() {
      return this.errorCollector;
   }

   public <T> Optional<T> parseTopRule(Atom<T> var1) {
      Optional var2 = this.parse(var1);
      if (var2.isPresent()) {
         this.errorCollector.finish(this.mark());
      }

      return var2;
   }

   public <T> Optional<T> parse(Atom<T> var1) {
      ParseState.CacheKey var2 = new ParseState.CacheKey(var1, this.mark());
      ParseState.CacheEntry var3 = this.lookupInCache(var2);
      if (var3 != null) {
         this.restore(var3.mark());
         return var3.value;
      } else {
         Rule var4 = this.dictionary.get(var1);
         if (var4 == null) {
            throw new IllegalStateException("No symbol " + var1);
         } else {
            Optional var5 = var4.parse(this);
            this.storeInCache(var2, var5);
            return var5;
         }
      }
   }

   @Nullable
   private <T> ParseState.CacheEntry<T> lookupInCache(ParseState.CacheKey<T> var1) {
      return (ParseState.CacheEntry<T>)this.ruleCache.get(var1);
   }

   private <T> void storeInCache(ParseState.CacheKey<T> var1, Optional<T> var2) {
      this.ruleCache.put(var1, new ParseState.CacheEntry(var2, this.mark()));
   }

   public abstract S input();

   public abstract int mark();

   public abstract void restore(int var1);

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
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
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
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
