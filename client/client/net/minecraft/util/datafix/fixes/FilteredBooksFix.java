package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class FilteredBooksFix extends ItemStackTagFix {
   public FilteredBooksFix(Schema var1) {
      super(var1, "Remove filtered text from books", var0 -> var0.equals("minecraft:writable_book") || var0.equals("minecraft:written_book"));
   }

   @Override
   protected <T> Dynamic<T> fixItemStackTag(Dynamic<T> var1) {
      return var1.remove("filtered_title").remove("filtered_pages");
   }
}
