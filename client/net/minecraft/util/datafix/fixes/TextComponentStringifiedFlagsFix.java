package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Optional;

public class TextComponentStringifiedFlagsFix extends DataFix {
   public TextComponentStringifiedFlagsFix(Schema var1) {
      super(var1, false);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.TEXT_COMPONENT);
      return this.fixTypeEverywhere("TextComponentStringyFlagsFix", var1, (var0) -> (var0x) -> var0x.mapSecond((var0) -> var0.mapRight((var0x) -> var0x.mapSecond((var0) -> var0.mapSecond((var0x) -> var0x.mapSecond((var0) -> var0.update("bold", TextComponentStringifiedFlagsFix::stringToBool).update("italic", TextComponentStringifiedFlagsFix::stringToBool).update("underlined", TextComponentStringifiedFlagsFix::stringToBool).update("strikethrough", TextComponentStringifiedFlagsFix::stringToBool).update("obfuscated", TextComponentStringifiedFlagsFix::stringToBool)))))));
   }

   private static <T> Dynamic<T> stringToBool(Dynamic<T> var0) {
      Optional var1 = var0.asString().result();
      return var1.isPresent() ? var0.createBoolean(Boolean.parseBoolean((String)var1.get())) : var0;
   }
}
