package net.minecraft.nbt.visitors;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.nbt.TagType;

public class CollectFields extends CollectToTag {
   private int fieldsToGetCount;
   private final Set<TagType<?>> wantedTypes;
   private final Deque<FieldTree> stack = new ArrayDeque<>();

   public CollectFields(FieldSelector... var1) {
      super();
      this.fieldsToGetCount = var1.length;
      Builder var2 = ImmutableSet.builder();
      FieldTree var3 = FieldTree.createRoot();

      for(FieldSelector var7 : var1) {
         var3.addEntry(var7);
         var2.add(var7.type());
      }

      this.stack.push(var3);
      var2.add(CompoundTag.TYPE);
      this.wantedTypes = var2.build();
   }

   @Override
   public StreamTagVisitor.ValueResult visitRootEntry(TagType<?> var1) {
      return var1 != CompoundTag.TYPE ? StreamTagVisitor.ValueResult.HALT : super.visitRootEntry(var1);
   }

   @Override
   public StreamTagVisitor.EntryResult visitEntry(TagType<?> var1) {
      FieldTree var2 = this.stack.element();
      if (this.depth() > var2.depth()) {
         return super.visitEntry(var1);
      } else if (this.fieldsToGetCount <= 0) {
         return StreamTagVisitor.EntryResult.HALT;
      } else {
         return !this.wantedTypes.contains(var1) ? StreamTagVisitor.EntryResult.SKIP : super.visitEntry(var1);
      }
   }

   @Override
   public StreamTagVisitor.EntryResult visitEntry(TagType<?> var1, String var2) {
      FieldTree var3 = this.stack.element();
      if (this.depth() > var3.depth()) {
         return super.visitEntry(var1, var2);
      } else if (var3.selectedFields().remove(var2, var1)) {
         --this.fieldsToGetCount;
         return super.visitEntry(var1, var2);
      } else {
         if (var1 == CompoundTag.TYPE) {
            FieldTree var4 = var3.fieldsToRecurse().get(var2);
            if (var4 != null) {
               this.stack.push(var4);
               return super.visitEntry(var1, var2);
            }
         }

         return StreamTagVisitor.EntryResult.SKIP;
      }
   }

   @Override
   public StreamTagVisitor.ValueResult visitContainerEnd() {
      if (this.depth() == this.stack.element().depth()) {
         this.stack.pop();
      }

      return super.visitContainerEnd();
   }

   public int getMissingFieldCount() {
      return this.fieldsToGetCount;
   }
}
