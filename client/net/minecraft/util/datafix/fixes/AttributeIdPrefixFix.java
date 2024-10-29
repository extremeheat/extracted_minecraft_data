package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class AttributeIdPrefixFix extends AttributesRenameFix {
   private static final List<String> PREFIXES = List.of("generic.", "horse.", "player.", "zombie.");

   public AttributeIdPrefixFix(Schema var1) {
      super(var1, "AttributeIdPrefixFix", AttributeIdPrefixFix::replaceId);
   }

   private static String replaceId(String var0) {
      String var1 = NamespacedSchema.ensureNamespaced(var0);
      Iterator var2 = PREFIXES.iterator();

      String var4;
      do {
         if (!var2.hasNext()) {
            return var0;
         }

         String var3 = (String)var2.next();
         var4 = NamespacedSchema.ensureNamespaced(var3);
      } while(!var1.startsWith(var4));

      String var10000 = var1.substring(var4.length());
      return "minecraft:" + var10000;
   }
}
