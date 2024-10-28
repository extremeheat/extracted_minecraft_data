package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Streams;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.util.datafix.ComponentDataFixUtils;

public class DropInvalidSignDataFix extends NamedEntityFix {
   private static final String[] FIELDS_TO_DROP = new String[]{"Text1", "Text2", "Text3", "Text4", "FilteredText1", "FilteredText2", "FilteredText3", "FilteredText4", "Color", "GlowingText"};

   public DropInvalidSignDataFix(Schema var1, String var2, String var3) {
      super(var1, false, var2, References.BLOCK_ENTITY, var3);
   }

   private static <T> Dynamic<T> fix(Dynamic<T> var0) {
      var0 = var0.update("front_text", DropInvalidSignDataFix::fixText);
      var0 = var0.update("back_text", DropInvalidSignDataFix::fixText);
      String[] var1 = FIELDS_TO_DROP;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         String var4 = var1[var3];
         var0 = var0.remove(var4);
      }

      return var0;
   }

   private static <T> Dynamic<T> fixText(Dynamic<T> var0) {
      boolean var1 = var0.get("_filtered_correct").asBoolean(false);
      if (var1) {
         return var0.remove("_filtered_correct");
      } else {
         Optional var2 = var0.get("filtered_messages").asStreamOpt().result();
         if (var2.isEmpty()) {
            return var0;
         } else {
            Dynamic var3 = ComponentDataFixUtils.createEmptyComponent(var0.getOps());
            List var4 = ((Stream)var0.get("messages").asStreamOpt().result().orElse(Stream.of())).toList();
            List var5 = Streams.mapWithIndex((Stream)var2.get(), (var2x, var3x) -> {
               Dynamic var5 = var3x < (long)var4.size() ? (Dynamic)var4.get((int)var3x) : var3;
               return var2x.equals(var3) ? var5 : var2x;
            }).toList();
            return var5.stream().allMatch((var1x) -> {
               return var1x.equals(var3);
            }) ? var0.remove("filtered_messages") : var0.set("filtered_messages", var0.createList(var5.stream()));
         }
      }
   }

   protected Typed<?> fix(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), DropInvalidSignDataFix::fix);
   }
}
