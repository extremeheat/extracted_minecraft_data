package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class CustomNameStringToComponentItem extends DataFix {
   public CustomNameStringToComponentItem(Schema var1, boolean var2) {
      super(var1, var2);
   }

   private Dynamic<?> func_209621_a(Dynamic<?> var1) {
      Optional var2 = var1.get("display");
      if (var2.isPresent()) {
         Dynamic var3 = (Dynamic)var2.get();
         Optional var4 = var3.get("Name").flatMap(Dynamic::getStringValue);
         if (var4.isPresent()) {
            var3 = var3.set("Name", var3.createString(ITextComponent.Serializer.func_150696_a(new TextComponentString((String)var4.get()))));
         } else {
            Optional var5 = var3.get("LocName").flatMap(Dynamic::getStringValue);
            if (var5.isPresent()) {
               var3 = var3.set("Name", var3.createString(ITextComponent.Serializer.func_150696_a(new TextComponentTranslation((String)var5.get(), new Object[0]))));
               var3 = var3.remove("LocName");
            }
         }

         return var1.set("display", var3);
      } else {
         return var1;
      }
   }

   public TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(TypeReferences.field_211295_k);
      OpticFinder var2 = var1.findField("tag");
      return this.fixTypeEverywhereTyped("ItemCustomNameToComponentFix", var1, (var2x) -> {
         return var2x.updateTyped(var2, (var1) -> {
            return var1.update(DSL.remainderFinder(), this::func_209621_a);
         });
      });
   }
}
