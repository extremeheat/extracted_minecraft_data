package net.minecraft.nbt.visitors;

import java.util.List;
import net.minecraft.nbt.TagType;

public record FieldSelector(List<String> path, TagType<?> type, String name) {
   public FieldSelector(TagType<?> var1, String var2) {
      this(List.of(), var1, var2);
   }

   public FieldSelector(String var1, TagType<?> var2, String var3) {
      this(List.of(var1), var2, var3);
   }

   public FieldSelector(String var1, String var2, TagType<?> var3, String var4) {
      this(List.of(var1, var2), var3, var4);
   }

   public FieldSelector(List<String> var1, TagType<?> var2, String var3) {
      super();
      this.path = var1;
      this.type = var2;
      this.name = var3;
   }

   public List<String> path() {
      return this.path;
   }

   public TagType<?> type() {
      return this.type;
   }

   public String name() {
      return this.name;
   }
}
