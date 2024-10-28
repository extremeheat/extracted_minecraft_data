package net.minecraft.nbt.visitors;

import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.nbt.TagType;

public class SkipFields extends CollectToTag {
   private final Deque<FieldTree> stack = new ArrayDeque();

   public SkipFields(FieldSelector... var1) {
      super();
      FieldTree var2 = FieldTree.createRoot();
      FieldSelector[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         FieldSelector var6 = var3[var5];
         var2.addEntry(var6);
      }

      this.stack.push(var2);
   }

   public StreamTagVisitor.EntryResult visitEntry(TagType<?> var1, String var2) {
      FieldTree var3 = (FieldTree)this.stack.element();
      if (var3.isSelected(var1, var2)) {
         return StreamTagVisitor.EntryResult.SKIP;
      } else {
         if (var1 == CompoundTag.TYPE) {
            FieldTree var4 = (FieldTree)var3.fieldsToRecurse().get(var2);
            if (var4 != null) {
               this.stack.push(var4);
            }
         }

         return super.visitEntry(var1, var2);
      }
   }

   public StreamTagVisitor.ValueResult visitContainerEnd() {
      if (this.depth() == ((FieldTree)this.stack.element()).depth()) {
         this.stack.pop();
      }

      return super.visitContainerEnd();
   }
}
