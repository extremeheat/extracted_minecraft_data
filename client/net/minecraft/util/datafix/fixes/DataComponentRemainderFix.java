package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import javax.annotation.Nullable;

public abstract class DataComponentRemainderFix extends DataFix {
   private final String name;
   private final String componentId;
   private final String newComponentId;

   public DataComponentRemainderFix(Schema var1, String var2, String var3) {
      this(var1, var2, var3, var3);
   }

   public DataComponentRemainderFix(Schema var1, String var2, String var3, String var4) {
      super(var1, false);
      this.name = var2;
      this.componentId = var3;
      this.newComponentId = var4;
   }

   public final TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.DATA_COMPONENTS);
      return this.fixTypeEverywhereTyped(this.name, var1, (var1x) -> var1x.update(DSL.remainderFinder(), (var1) -> {
            Optional var2 = var1.get(this.componentId).result();
            if (var2.isEmpty()) {
               return var1;
            } else {
               Dynamic var3 = this.fixComponent((Dynamic)var2.get());
               return var1.remove(this.componentId).setFieldIfPresent(this.newComponentId, Optional.ofNullable(var3));
            }
         }));
   }

   @Nullable
   protected abstract <T> Dynamic<T> fixComponent(Dynamic<T> var1);
}
