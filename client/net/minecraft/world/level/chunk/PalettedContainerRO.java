package net.minecraft.world.level.chunk;

import com.mojang.serialization.DataResult;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.core.IdMap;
import net.minecraft.network.FriendlyByteBuf;

public interface PalettedContainerRO<T> {
   T get(int var1, int var2, int var3);

   void getAll(Consumer<T> var1);

   void write(FriendlyByteBuf var1);

   int getSerializedSize();

   boolean maybeHas(Predicate<T> var1);

   void count(PalettedContainer.CountConsumer<T> var1);

   PalettedContainer<T> copy();

   PalettedContainer<T> recreate();

   PalettedContainerRO.PackedData<T> pack(IdMap<T> var1, PalettedContainer.Strategy var2);

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

   public interface Unpacker<T, C extends PalettedContainerRO<T>> {
      DataResult<C> read(IdMap<T> var1, PalettedContainer.Strategy var2, PalettedContainerRO.PackedData<T> var3);
   }
}
