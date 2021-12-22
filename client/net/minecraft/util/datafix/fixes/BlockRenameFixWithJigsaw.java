package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public abstract class BlockRenameFixWithJigsaw extends BlockRenameFix {
   private final String name;

   public BlockRenameFixWithJigsaw(Schema var1, String var2) {
      super(var1, var2);
      this.name = var2;
   }

   public TypeRewriteRule makeRule() {
      TypeReference var1 = References.BLOCK_ENTITY;
      String var2 = "minecraft:jigsaw";
      OpticFinder var3 = DSL.namedChoice("minecraft:jigsaw", this.getInputSchema().getChoiceType(var1, "minecraft:jigsaw"));
      TypeRewriteRule var4 = this.fixTypeEverywhereTyped(this.name + " for jigsaw state", this.getInputSchema().getType(var1), this.getOutputSchema().getType(var1), (var3x) -> {
         return var3x.updateTyped(var3, this.getOutputSchema().getChoiceType(var1, "minecraft:jigsaw"), (var1x) -> {
            return var1x.update(DSL.remainderFinder(), (var1) -> {
               return var1.update("final_state", (var2) -> {
                  Optional var10000 = var2.asString().result().map((var1x) -> {
                     int var2 = var1x.indexOf(91);
                     int var3 = var1x.indexOf(123);
                     int var4 = var1x.length();
                     if (var2 > 0) {
                        var4 = Math.min(var4, var2);
                     }

                     if (var3 > 0) {
                        var4 = Math.min(var4, var3);
                     }

                     String var5 = var1x.substring(0, var4);
                     String var6 = this.fixBlock(var5);
                     return var6 + var1x.substring(var4);
                  });
                  Objects.requireNonNull(var1);
                  return (Dynamic)DataFixUtils.orElse(var10000.map(var1::createString), var2);
               });
            });
         });
      });
      return TypeRewriteRule.seq(super.makeRule(), var4);
   }

   public static DataFix create(Schema var0, String var1, final Function<String, String> var2) {
      return new BlockRenameFixWithJigsaw(var0, var1) {
         protected String fixBlock(String var1) {
            return (String)var2.apply(var1);
         }
      };
   }
}
