package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Streams;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.LegacyComponentDataFixUtils;

public class DropInvalidSignDataFix extends DataFix {
   private final String entityName;

   public DropInvalidSignDataFix(Schema var1, String var2) {
      super(var1, false);
      this.entityName = var2;
   }

   private <T> Dynamic<T> fix(Dynamic<T> var1) {
      var1 = var1.update("front_text", DropInvalidSignDataFix::fixText);
      var1 = var1.update("back_text", DropInvalidSignDataFix::fixText);

      for(String var3 : BlockEntitySignDoubleSidedEditableTextFix.FIELDS_TO_DROP) {
         var1 = var1.remove(var3);
      }

      return var1;
   }

   private static <T> Dynamic<T> fixText(Dynamic<T> var0) {
      Optional var1 = var0.get("filtered_messages").asStreamOpt().result();
      if (var1.isEmpty()) {
         return var0;
      } else {
         Dynamic var2 = LegacyComponentDataFixUtils.createEmptyComponent(var0.getOps());
         List var3 = ((Stream)var0.get("messages").asStreamOpt().result().orElse(Stream.of())).toList();
         List var4 = Streams.mapWithIndex((Stream)var1.get(), (var2x, var3x) -> {
            Dynamic var5 = var3x < (long)var3.size() ? (Dynamic)var3.get((int)var3x) : var2;
            return var2x.equals(var2) ? var5 : var2x;
         }).toList();
         return var4.equals(var3) ? var0.remove("filtered_messages") : var0.set("filtered_messages", var0.createList(var4.stream()));
      }
   }

   public TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.BLOCK_ENTITY);
      Type var2 = this.getInputSchema().getChoiceType(References.BLOCK_ENTITY, this.entityName);
      OpticFinder var3 = DSL.namedChoice(this.entityName, var2);
      return this.fixTypeEverywhereTyped("DropInvalidSignDataFix for " + this.entityName, var1, (var3x) -> var3x.updateTyped(var3, var2, (var2x) -> {
            boolean var3 = ((Dynamic)var2x.get(DSL.remainderFinder())).get("_filtered_correct").asBoolean(false);
            return var3 ? var2x.update(DSL.remainderFinder(), (var0) -> var0.remove("_filtered_correct")) : Util.writeAndReadTypedOrThrow(var2x, var2, this::fix);
         }));
   }
}
