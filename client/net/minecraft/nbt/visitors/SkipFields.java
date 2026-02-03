package net.minecraft.nbt.visitors;

import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.nbt.TagType;

public class SkipFields extends CollectToTag {
   private final Deque<FieldTree> stack = new ArrayDeque();

   public SkipFields(final FieldSelector... wantedFields) {
      super();
      FieldTree rootFrame = FieldTree.createRoot();

      for(FieldSelector wantedField : wantedFields) {
         rootFrame.addEntry(wantedField);
      }

      this.stack.push(rootFrame);
   }

   public StreamTagVisitor.EntryResult visitEntry(final TagType<?> type, final String id) {
      FieldTree currentFrame = (FieldTree)this.stack.element();
      if (currentFrame.isSelected(type, id)) {
         return StreamTagVisitor.EntryResult.SKIP;
      } else {
         if (type == CompoundTag.TYPE) {
            FieldTree newFrame = (FieldTree)currentFrame.fieldsToRecurse().get(id);
            if (newFrame != null) {
               this.stack.push(newFrame);
            }
         }

         return super.visitEntry(type, id);
      }
   }

   public StreamTagVisitor.ValueResult visitContainerEnd() {
      if (this.depth() == ((FieldTree)this.stack.element()).depth()) {
         this.stack.pop();
      }

      return super.visitContainerEnd();
   }
}
