package net.minecraft.nbt.visitors;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.TagType;

public record FieldTree(int depth, Map<String, TagType<?>> selectedFields, Map<String, FieldTree> fieldsToRecurse) {
   private FieldTree(int var1) {
      this(var1, new HashMap<>(), new HashMap<>());
   }

   public FieldTree(int depth, Map<String, TagType<?>> selectedFields, Map<String, FieldTree> fieldsToRecurse) {
      super();
      this.depth = depth;
      this.selectedFields = selectedFields;
      this.fieldsToRecurse = fieldsToRecurse;
   }

   public static FieldTree createRoot() {
      return new FieldTree(1);
   }

   public void addEntry(FieldSelector var1) {
      if (this.depth <= var1.path().size()) {
         this.fieldsToRecurse.computeIfAbsent(var1.path().get(this.depth - 1), var1x -> new FieldTree(this.depth + 1)).addEntry(var1);
      } else {
         this.selectedFields.put(var1.name(), var1.type());
      }
   }

   public boolean isSelected(TagType<?> var1, String var2) {
      return var1.equals(this.selectedFields().get(var2));
   }
}