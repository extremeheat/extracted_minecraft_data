package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.Util;

public class FilteredBooksFix extends ItemStackTagFix {
   public FilteredBooksFix(Schema var1) {
      super(var1, "Remove filtered text from books", (var0) -> var0.equals("minecraft:writable_book") || var0.equals("minecraft:written_book"));
   }

   protected Typed<?> fixItemStackTag(Typed<?> var1) {
      return Util.writeAndReadTypedOrThrow(var1, var1.getType(), (var0) -> var0.remove("filtered_title").remove("filtered_pages"));
   }
}
