package net.minecraft.util.datafix.fixes;

import com.google.gson.JsonElement;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.JsonOps;
import java.util.Map;
import java.util.Optional;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Util;

public class LegacyHoverEventFix extends DataFix {
   public LegacyHoverEventFix(Schema var1) {
      super(var1, false);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.TEXT_COMPONENT).findFieldType("hoverEvent");
      return this.createFixer(this.getInputSchema().getTypeRaw(References.TEXT_COMPONENT), var1);
   }

   private <C, H extends Pair<String, ?>> TypeRewriteRule createFixer(Type<C> var1, Type<H> var2) {
      Type var3 = DSL.named(References.TEXT_COMPONENT.typeName(), DSL.or(DSL.or(DSL.string(), DSL.list(var1)), DSL.and(DSL.optional(DSL.field("extra", DSL.list(var1))), DSL.optional(DSL.field("separator", var1)), DSL.optional(DSL.field("hoverEvent", var2)), DSL.remainderType())));
      if (!var3.equals(this.getInputSchema().getType(References.TEXT_COMPONENT))) {
         String var10002 = String.valueOf(var3);
         throw new IllegalStateException("Text component type did not match, expected " + var10002 + " but got " + String.valueOf(this.getInputSchema().getType(References.TEXT_COMPONENT)));
      } else {
         return this.fixTypeEverywhere("LegacyHoverEventFix", var3, (var2x) -> (var2xx) -> var2xx.mapSecond((var2x) -> var2x.mapRight((var2xx) -> var2xx.mapSecond((var2x) -> var2x.mapSecond((var2xx) -> {
                           Dynamic var3 = (Dynamic)var2xx.getSecond();
                           Optional var4 = var3.get("hoverEvent").result();
                           if (var4.isEmpty()) {
                              return var2xx;
                           } else {
                              Optional var5 = ((Dynamic)var4.get()).get("value").result();
                              if (var5.isEmpty()) {
                                 return var2xx;
                              } else {
                                 String var6 = (String)((Either)var2xx.getFirst()).left().map(Pair::getFirst).orElse("");
                                 Pair var7 = (Pair)this.fixHoverEvent(var2, var6, (Dynamic)var4.get());
                                 return var2xx.mapFirst((var1) -> Either.left(var7));
                              }
                           }
                        })))));
      }
   }

   private <H> H fixHoverEvent(Type<H> var1, String var2, Dynamic<?> var3) {
      return (H)("show_text".equals(var2) ? fixShowTextHover(var1, var3) : createPlaceholderHover(var1, var3));
   }

   private static <H> H fixShowTextHover(Type<H> var0, Dynamic<?> var1) {
      Dynamic var2 = var1.renameField("value", "contents");
      return (H)Util.readTypedOrThrow(var0, var2).getValue();
   }

   private static <H> H createPlaceholderHover(Type<H> var0, Dynamic<?> var1) {
      JsonElement var2 = (JsonElement)var1.convert(JsonOps.INSTANCE).getValue();
      Dynamic var3 = new Dynamic(JavaOps.INSTANCE, Map.of("action", "show_text", "contents", Map.of("text", "Legacy hoverEvent: " + GsonHelper.toStableString(var2))));
      return (H)Util.readTypedOrThrow(var0, var3).getValue();
   }
}
