package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;

public abstract class ItemStackComponentRemainderFix extends DataFix {
   private final String name;
   private final String componentId;
   private final String newComponentId;

   public ItemStackComponentRemainderFix(Schema var1, String var2, String var3) {
      this(var1, var2, var3, var3);
   }

   public ItemStackComponentRemainderFix(Schema var1, String var2, String var3, String var4) {
      super(var1, false);
      this.name = var2;
      this.componentId = var3;
      this.newComponentId = var4;
   }

   public final TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.ITEM_STACK);
      OpticFinder var2 = var1.findField("components");
      return this.fixTypeEverywhereTyped(this.name, var1, (var2x) -> {
         return var2x.updateTyped(var2, (var1) -> {
            return var1.update(DSL.remainderFinder(), (var1x) -> {
               return var1x.renameAndFixField(this.componentId, this.newComponentId, this::fixComponent);
            });
         });
      });
   }

   protected abstract <T> Dynamic<T> fixComponent(Dynamic<T> var1);
}
