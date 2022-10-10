package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.util.datafix.TypeReferences;

public abstract class BlockRename extends DataFix {
   private final String field_206310_a;

   public BlockRename(Schema var1, String var2) {
      super(var1, false);
      this.field_206310_a = var2;
   }

   public TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(TypeReferences.field_211300_p);
      Type var2 = DSL.named(TypeReferences.field_211300_p.typeName(), DSL.namespacedString());
      if (!Objects.equals(var1, var2)) {
         throw new IllegalStateException("block type is not what was expected.");
      } else {
         TypeRewriteRule var3 = this.fixTypeEverywhere(this.field_206310_a + " for block", var2, (var1x) -> {
            return (var1) -> {
               return var1.mapSecond(this::func_206309_a);
            };
         });
         TypeRewriteRule var4 = this.fixTypeEverywhereTyped(this.field_206310_a + " for block_state", this.getInputSchema().getType(TypeReferences.field_211296_l), (var1x) -> {
            return var1x.update(DSL.remainderFinder(), (var1) -> {
               Optional var2 = var1.get("Name").flatMap(Dynamic::getStringValue);
               return var2.isPresent() ? var1.set("Name", var1.createString(this.func_206309_a((String)var2.get()))) : var1;
            });
         });
         return TypeRewriteRule.seq(var3, var4);
      }
   }

   protected abstract String func_206309_a(String var1);

   public static DataFix func_207437_a(Schema var0, String var1, final Function<String, String> var2) {
      return new BlockRename(var0, var1) {
         protected String func_206309_a(String var1) {
            return (String)var2.apply(var1);
         }
      };
   }
}
