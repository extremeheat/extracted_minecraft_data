package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class BlockEntitySignDoubleSidedEditableTextFix extends NamedEntityFix {
   public BlockEntitySignDoubleSidedEditableTextFix(Schema var1, String var2, String var3) {
      super(var1, false, var2, References.BLOCK_ENTITY, var3);
   }

   private static Dynamic<?> fixTag(Dynamic<?> var0) {
      String var1 = "black";
      Dynamic var2 = var0.emptyMap();
      var2 = var2.set("messages", getTextList(var0, "Text"));
      var2 = var2.set("filtered_messages", getTextList(var0, "FilteredText"));
      Optional var3 = var0.get("Color").result();
      var2 = var2.set("color", var3.isPresent() ? (Dynamic)var3.get() : var2.createString("black"));
      Optional var4 = var0.get("GlowingText").result();
      var2 = var2.set("has_glowing_text", var4.isPresent() ? (Dynamic)var4.get() : var2.createBoolean(false));
      Dynamic var5 = var0.emptyMap();
      Dynamic var6 = getEmptyTextList(var0);
      var5 = var5.set("messages", var6);
      var5 = var5.set("filtered_messages", var6);
      var5 = var5.set("color", var5.createString("black"));
      var5 = var5.set("has_glowing_text", var5.createBoolean(false));
      var0 = var0.set("front_text", var2);
      return var0.set("back_text", var5);
   }

   private static <T> Dynamic<T> getTextList(Dynamic<T> var0, String var1) {
      Dynamic var2 = var0.createString(getEmptyComponent());
      return var0.createList(
         Stream.of(
            (T[])((Dynamic)var0.get(var1 + "1").result().orElse((T)var2),
            (Dynamic)var0.get(var1 + "2").result().orElse((T)var2),
            (Dynamic)var0.get(var1 + "3").result().orElse((T)var2),
            (Dynamic)var0.get(var1 + "4").result().orElse((T)var2))
         )
      );
   }

   private static <T> Dynamic<T> getEmptyTextList(Dynamic<T> var0) {
      Dynamic var1 = var0.createString(getEmptyComponent());
      return var0.createList(Stream.of((T[])(var1, var1, var1, var1)));
   }

   private static String getEmptyComponent() {
      return Component.Serializer.toJson(CommonComponents.EMPTY);
   }

   @Override
   protected Typed<?> fix(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), BlockEntitySignDoubleSidedEditableTextFix::fixTag);
   }
}
