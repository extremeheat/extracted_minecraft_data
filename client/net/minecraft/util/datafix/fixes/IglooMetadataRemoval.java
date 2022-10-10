package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;

public class IglooMetadataRemoval extends DataFix {
   public IglooMetadataRemoval(Schema var1, boolean var2) {
      super(var1, var2);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(TypeReferences.field_211303_s);
      Type var2 = this.getOutputSchema().getType(TypeReferences.field_211303_s);
      return this.writeFixAndRead("IglooMetadataRemovalFix", var1, var2, IglooMetadataRemoval::func_211926_a);
   }

   private static <T> Dynamic<T> func_211926_a(Dynamic<T> var0) {
      boolean var1 = (Boolean)var0.get("Children").flatMap(Dynamic::getStream).map((var0x) -> {
         return var0x.allMatch(IglooMetadataRemoval::func_211930_c);
      }).orElse(false);
      return var1 ? var0.set("id", var0.createString("Igloo")).remove("Children") : var0.update("Children", IglooMetadataRemoval::func_211929_b);
   }

   private static <T> Dynamic<T> func_211929_b(Dynamic<T> var0) {
      Optional var10000 = var0.getStream().map((var0x) -> {
         return var0x.filter((var0) -> {
            return !func_211930_c(var0);
         });
      });
      var0.getClass();
      return (Dynamic)var10000.map(var0::createList).orElse(var0);
   }

   private static boolean func_211930_c(Dynamic<?> var0) {
      return var0.getString("id").equals("Iglu");
   }
}
