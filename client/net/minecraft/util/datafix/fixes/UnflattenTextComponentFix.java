package net.minecraft.util.datafix.fixes;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.Util;
import org.slf4j.Logger;

public class UnflattenTextComponentFix extends DataFix {
   private static final Logger LOGGER = LogUtils.getLogger();

   public UnflattenTextComponentFix(Schema var1) {
      super(var1, true);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.TEXT_COMPONENT);
      Type var2 = this.getOutputSchema().getType(References.TEXT_COMPONENT);
      return this.createFixer(var1, var2);
   }

   private <T> TypeRewriteRule createFixer(Type<Pair<String, String>> var1, Type<T> var2) {
      return this.fixTypeEverywhere("UnflattenTextComponentFix", var1, var2, (var1x) -> (var2x) -> Util.readTypedOrThrow(var2, unflattenJson(var1x, (String)var2x.getSecond()), true).getValue());
   }

   private static <T> Dynamic<T> unflattenJson(DynamicOps<T> var0, String var1) {
      try {
         JsonElement var2 = JsonParser.parseString(var1);
         if (!var2.isJsonNull()) {
            return new Dynamic(var0, JsonOps.INSTANCE.convertTo(var0, var2));
         }
      } catch (Exception var3) {
         LOGGER.error("Failed to unflatten text component json: {}", var1, var3);
      }

      return new Dynamic(var0, var0.createString(var1));
   }
}
