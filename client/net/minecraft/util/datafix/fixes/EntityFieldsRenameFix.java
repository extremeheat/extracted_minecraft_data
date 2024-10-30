package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Iterator;
import java.util.Map;

public class EntityFieldsRenameFix extends NamedEntityFix {
   private final Map<String, String> renames;

   public EntityFieldsRenameFix(Schema var1, String var2, String var3, Map<String, String> var4) {
      super(var1, false, var2, References.ENTITY, var3);
      this.renames = var4;
   }

   public Dynamic<?> fixTag(Dynamic<?> var1) {
      Map.Entry var3;
      for(Iterator var2 = this.renames.entrySet().iterator(); var2.hasNext(); var1 = var1.renameField((String)var3.getKey(), (String)var3.getValue())) {
         var3 = (Map.Entry)var2.next();
      }

      return var1;
   }

   protected Typed<?> fix(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), this::fixTag);
   }
}
