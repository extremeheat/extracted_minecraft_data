package net.minecraft.util.random;

public interface WeightedEntry {
   Weight getWeight();

   static <T> WeightedEntry.Wrapper<T> wrap(T var0, int var1) {
      return new WeightedEntry.Wrapper<>((T)var0, Weight.of(var1));
   }

   public static class IntrusiveBase implements WeightedEntry {
      private final Weight weight;

      public IntrusiveBase(int var1) {
         super();
         this.weight = Weight.of(var1);
      }

      public IntrusiveBase(Weight var1) {
         super();
         this.weight = var1;
      }

      @Override
      public Weight getWeight() {
         return this.weight;
      }
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
}
