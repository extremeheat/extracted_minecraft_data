package net.minecraft.world.entity.ai.village.poi;

import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.VisibleForDebug;

public class PoiRecord {
   private final BlockPos pos;
   private final Holder<PoiType> poiType;
   private int freeTickets;
   private final Runnable setDirty;

   PoiRecord(BlockPos var1, Holder<PoiType> var2, int var3, Runnable var4) {
      super();
      this.pos = var1.immutable();
      this.poiType = var2;
      this.freeTickets = var3;
      this.setDirty = var4;
   }

   public PoiRecord(BlockPos var1, Holder<PoiType> var2, Runnable var3) {
      this(var1, var2, ((PoiType)var2.value()).maxTickets(), var3);
   }

   public PoiRecord.Packed pack() {
      return new PoiRecord.Packed(this.pos, this.poiType, this.freeTickets);
   }

   @Deprecated
   @VisibleForDebug
   public int getFreeTickets() {
      return this.freeTickets;
   }

   protected boolean acquireTicket() {
      if (this.freeTickets <= 0) {
         return false;
      } else {
         this.freeTickets--;
         this.setDirty.run();
         return true;
      }
   }

   protected boolean releaseTicket() {
      if (this.freeTickets >= this.poiType.value().maxTickets()) {
         return false;
      } else {
         this.freeTickets++;
         this.setDirty.run();
         return true;
      }
   }

   public boolean hasSpace() {
      return this.freeTickets > 0;
   }

   public boolean isOccupied() {
      return this.freeTickets != this.poiType.value().maxTickets();
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public Holder<PoiType> getPoiType() {
      return this.poiType;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 != null && this.getClass() == var1.getClass() ? Objects.equals(this.pos, ((PoiRecord)var1).pos) : false;
      }
   }

   @Override
   public int hashCode() {
      return this.pos.hashCode();
   }

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
