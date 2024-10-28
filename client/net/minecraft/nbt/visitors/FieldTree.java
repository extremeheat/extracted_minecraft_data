package net.minecraft.nbt.visitors;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.TagType;

public record FieldTree(int depth, Map<String, TagType<?>> selectedFields, Map<String, FieldTree> fieldsToRecurse) {
   private FieldTree(int var1) {
      this(var1, new HashMap(), new HashMap());
   }

   public FieldTree(int var1, Map<String, TagType<?>> var2, Map<String, FieldTree> var3) {
      super();
      this.depth = var1;
      this.selectedFields = var2;
      this.fieldsToRecurse = var3;
   }

   public static FieldTree createRoot() {
      return new FieldTree(1);
   }

   public void addEntry(FieldSelector var1) {
      if (this.depth <= var1.path().size()) {
         ((FieldTree)this.fieldsToRecurse.computeIfAbsent((String)var1.path().get(this.depth - 1), (var1x) -> {
            return new FieldTree(this.depth + 1);
         })).addEntry(var1);
      } else {
         this.selectedFields.put(var1.name(), var1.type());
      }

   }

   public boolean isSelected(TagType<?> var1, String var2) {
      return var1.equals(this.selectedFields().get(var2));
   }

   public int depth() {
      return this.depth;
   }

   public Map<String, TagType<?>> selectedFields() {
      return this.selectedFields;
   }

   public Map<String, FieldTree> fieldsToRecurse() {
      return this.fieldsToRecurse;
   }
}
