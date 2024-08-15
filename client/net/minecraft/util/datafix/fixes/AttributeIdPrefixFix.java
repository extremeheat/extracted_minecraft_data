package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import java.util.List;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class AttributeIdPrefixFix extends AttributesRenameFix {
   private static final List<String> PREFIXES = List.of("generic.", "horse.", "player.", "zombie.");

   public AttributeIdPrefixFix(Schema var1) {
      super(var1, "AttributeIdPrefixFix", AttributeIdPrefixFix::replaceId);
   }

   private static String replaceId(String var0) {
      String var1 = NamespacedSchema.ensureNamespaced(var0);

      for (String var3 : PREFIXES) {
         String var4 = NamespacedSchema.ensureNamespaced(var3);
         if (var1.startsWith(var4)) {
            return "minecraft:" + var1.substring(var4.length());
         }
      }

      return var0;
   }
}
