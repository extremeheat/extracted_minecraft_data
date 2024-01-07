package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.function.Function;

public abstract class BlockRenameFixWithJigsaw extends BlockRenameFix {
   private final String name;

   public BlockRenameFixWithJigsaw(Schema var1, String var2) {
      super(var1, var2);
      this.name = var2;
   }

   @Override
   public TypeRewriteRule makeRule() {
      TypeReference var1 = References.BLOCK_ENTITY;
      String var2 = "minecraft:jigsaw";
      OpticFinder var3 = DSL.namedChoice("minecraft:jigsaw", this.getInputSchema().getChoiceType(var1, "minecraft:jigsaw"));
      TypeRewriteRule var4 = this.fixTypeEverywhereTyped(
         this.name + " for jigsaw state",
         this.getInputSchema().getType(var1),
         this.getOutputSchema().getType(var1),
         var3x -> var3x.updateTyped(
               var3,
               this.getOutputSchema().getChoiceType(var1, "minecraft:jigsaw"),
               var1xx -> var1xx.update(
                     DSL.remainderFinder(),
                     var1xxx -> var1xxx.update("final_state", var2xx -> (Dynamic)DataFixUtils.orElse(var2xx.asString().result().map(var1xxxxx -> {
                              int var2xxxx = var1xxxxx.indexOf(91);
                              int var3xxx = var1xxxxx.indexOf(123);
                              int var4xx = var1xxxxx.length();
                              if (var2xxxx > 0) {
                                 var4xx = Math.min(var4xx, var2xxxx);
                              }
         
                              if (var3xxx > 0) {
                                 var4xx = Math.min(var4xx, var3xxx);
                              }
         
                              String var5 = var1xxxxx.substring(0, var4xx);
                              String var6 = this.fixBlock(var5);
                              return var6 + var1xxxxx.substring(var4xx);
                           }).map(var1xxx::createString), var2xx))
                  )
            )
      );
      return TypeRewriteRule.seq(super.makeRule(), var4);
   }

   public static DataFix create(Schema var0, String var1, final Function<String, String> var2) {
      return new BlockRenameFixWithJigsaw(var0, var1) {
         @Override
         protected String fixBlock(String var1) {
            return (String)var2.apply(var1);
         }
      };
   }
}
