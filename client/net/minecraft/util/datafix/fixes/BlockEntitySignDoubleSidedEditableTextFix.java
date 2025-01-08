package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Streams;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.util.datafix.LegacyComponentDataFixUtils;

public class BlockEntitySignDoubleSidedEditableTextFix extends NamedEntityWriteReadFix {
   public static final List<String> FIELDS_TO_DROP = List.of("Text1", "Text2", "Text3", "Text4", "FilteredText1", "FilteredText2", "FilteredText3", "FilteredText4", "Color", "GlowingText");
   public static final String FILTERED_CORRECT = "_filtered_correct";
   private static final String DEFAULT_COLOR = "black";

   public BlockEntitySignDoubleSidedEditableTextFix(Schema var1, String var2, String var3) {
      super(var1, true, var2, References.BLOCK_ENTITY, var3);
   }

   protected <T> Dynamic<T> fix(Dynamic<T> var1) {
      var1 = var1.set("front_text", fixFrontTextTag(var1)).set("back_text", createDefaultText(var1)).set("is_waxed", var1.createBoolean(false)).set("_filtered_correct", var1.createBoolean(true));

      for(String var3 : FIELDS_TO_DROP) {
         var1 = var1.remove(var3);
      }

      return var1;
   }

   private static <T> Dynamic<T> fixFrontTextTag(Dynamic<T> var0) {
      Dynamic var1 = LegacyComponentDataFixUtils.createEmptyComponent(var0.getOps());
      List var2 = getLines(var0, "Text").map((var1x) -> (Dynamic)var1x.orElse(var1)).toList();
      Dynamic var3 = var0.emptyMap().set("messages", var0.createList(var2.stream())).set("color", (Dynamic)var0.get("Color").result().orElse(var0.createString("black"))).set("has_glowing_text", (Dynamic)var0.get("GlowingText").result().orElse(var0.createBoolean(false)));
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
      Dynamic var1 = LegacyComponentDataFixUtils.createEmptyComponent(var0.getOps());
      return var0.createList(Stream.of(var1, var1, var1, var1));
   }
}
