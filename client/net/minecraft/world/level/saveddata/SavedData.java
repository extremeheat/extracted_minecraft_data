package net.minecraft.world.level.saveddata;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;

public abstract class SavedData {
   private boolean dirty;

   public SavedData() {
      super();
   }

   public abstract CompoundTag save(CompoundTag var1, HolderLookup.Provider var2);

   public void setDirty() {
      this.setDirty(true);
   }

   public void setDirty(boolean var1) {
      this.dirty = var1;
   }

   public boolean isDirty() {
      return this.dirty;
   }

   public CompoundTag save(HolderLookup.Provider var1) {
      CompoundTag var2 = new CompoundTag();
      var2.put("data", this.save(new CompoundTag(), var1));
      NbtUtils.addCurrentDataVersion(var2);
      this.setDirty(false);
      return var2;
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
