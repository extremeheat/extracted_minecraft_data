package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public abstract class BlockRenameFix extends DataFix {
   private final String name;

   public BlockRenameFix(Schema var1, String var2) {
      super(var1, false);
      this.name = var2;
   }

   public TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.BLOCK_NAME);
      Type var2 = DSL.named(References.BLOCK_NAME.typeName(), NamespacedSchema.namespacedString());
      if (!Objects.equals(var1, var2)) {
         throw new IllegalStateException("block type is not what was expected.");
      } else {
         TypeRewriteRule var3 = this.fixTypeEverywhere(this.name + " for block", var2, var1x -> var1xx -> var1xx.mapSecond(this::renameBlock));
         TypeRewriteRule var4 = this.fixTypeEverywhereTyped(
            this.name + " for block_state",
            this.getInputSchema().getType(References.BLOCK_STATE),
            var1x -> var1x.update(DSL.remainderFinder(), this::fixBlockState)
         );
         TypeRewriteRule var5 = this.fixTypeEverywhereTyped(
            this.name + " for flat_block_state",
            this.getInputSchema().getType(References.FLAT_BLOCK_STATE),
            var1x -> var1x.update(
                  DSL.remainderFinder(),
                  var1xx -> (Dynamic)DataFixUtils.orElse(var1xx.asString().result().map(this::fixFlatBlockState).map(var1xx::createString), var1xx)
               )
         );
         return TypeRewriteRule.seq(var3, new TypeRewriteRule[]{var4, var5});
      }
   }

   private Dynamic<?> fixBlockState(Dynamic<?> var1) {
      Optional var2 = var1.get("Name").asString().result();
      return var2.isPresent() ? var1.set("Name", var1.createString(this.renameBlock((String)var2.get()))) : var1;
   }

   private String fixFlatBlockState(String var1) {
      int var2 = var1.indexOf(91);
      int var3 = var1.indexOf(123);
      int var4 = var1.length();
      if (var2 > 0) {
         var4 = var2;
      }

      if (var3 > 0) {
         var4 = Math.min(var4, var3);
      }

      String var5 = var1.substring(0, var4);
      String var6 = this.renameBlock(var5);
      return var6 + var1.substring(var4);
   }

   protected abstract String renameBlock(String var1);

   public static DataFix create(Schema var0, String var1, final Function<String, String> var2) {
      return new BlockRenameFix(var0, var1) {
         @Override
         protected String renameBlock(String var1) {
            return (String)var2.apply(var1);
         }
      };
   }
}
