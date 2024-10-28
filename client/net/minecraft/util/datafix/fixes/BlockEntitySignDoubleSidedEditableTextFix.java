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

public class BlockEntitySignDoubleSidedEditableTextFix extends NamedEntityFix {
   public static final String FILTERED_CORRECT = "_filtered_correct";
   private static final String DEFAULT_COLOR = "black";

   public BlockEntitySignDoubleSidedEditableTextFix(Schema var1, String var2, String var3) {
      super(var1, false, var2, References.BLOCK_ENTITY, var3);
   }

   private static <T> Dynamic<T> fixTag(Dynamic<T> var0) {
      return var0.set("front_text", fixFrontTextTag(var0)).set("back_text", createDefaultText(var0)).set("is_waxed", var0.createBoolean(false));
   }

   private static <T> Dynamic<T> fixFrontTextTag(Dynamic<T> var0) {
      Dynamic var1 = ComponentDataFixUtils.createEmptyComponent(var0.getOps());
      List var2 = getLines(var0, "Text").map((var1x) -> {
         return (Dynamic)var1x.orElse(var1);
      }).toList();
      Dynamic var3 = var0.emptyMap().set("messages", var0.createList(var2.stream())).set("color", (Dynamic)var0.get("Color").result().orElse(var0.createString("black"))).set("has_glowing_text", (Dynamic)var0.get("GlowingText").result().orElse(var0.createBoolean(false))).set("_filtered_correct", var0.createBoolean(true));
      List var4 = getLines(var0, "FilteredText").toList();
      if (var4.stream().anyMatch(Optional::isPresent)) {
         var3 = var3.set("filtered_messages", var0.createList(Streams.mapWithIndex(var4.stream(), (var1x, var2x) -> {
            Dynamic var4 = (Dynamic)var2.get((int)var2x);
            return (Dynamic)var1x.orElse(var4);
         })));
      }

      return var3;
   }

   private static <T> Stream<Optional<Dynamic<T>>> getLines(Dynamic<T> var0, String var1) {
      return Stream.of(var0.get(var1 + "1").result(), var0.get(var1 + "2").result(), var0.get(var1 + "3").result(), var0.get(var1 + "4").result());
   }

   private static <T> Dynamic<T> createDefaultText(Dynamic<T> var0) {
      return var0.emptyMap().set("messages", createEmptyLines(var0)).set("color", var0.createString("black")).set("has_glowing_text", var0.createBoolean(false));
   }

   private static <T> Dynamic<T> createEmptyLines(Dynamic<T> var0) {
      Dynamic var1 = ComponentDataFixUtils.createEmptyComponent(var0.getOps());
      return var0.createList(Stream.of(var1, var1, var1, var1));
   }

   protected Typed<?> fix(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), BlockEntitySignDoubleSidedEditableTextFix::fixTag);
   }
}
