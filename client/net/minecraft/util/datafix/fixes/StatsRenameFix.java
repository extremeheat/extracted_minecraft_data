package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import java.util.Map;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class StatsRenameFix extends DataFix {
   private final String name;
   private final Map<String, String> renames;

   public StatsRenameFix(Schema var1, String var2, Map<String, String> var3) {
      super(var1, false);
      this.name = var2;
      this.renames = var3;
   }

   protected TypeRewriteRule makeRule() {
      return TypeRewriteRule.seq(this.createStatRule(), this.createCriteriaRule());
   }

   private TypeRewriteRule createCriteriaRule() {
      Type var1 = this.getOutputSchema().getType(References.OBJECTIVE);
      Type var2 = this.getInputSchema().getType(References.OBJECTIVE);
      OpticFinder var3 = var2.findField("CriteriaType");
      TaggedChoiceType var4 = (TaggedChoiceType)var3.type().findChoiceType("type", -1).orElseThrow(() -> {
         return new IllegalStateException("Can't find choice type for criteria");
      });
      Type var5 = (Type)var4.types().get("minecraft:custom");
      if (var5 == null) {
         throw new IllegalStateException("Failed to find custom criterion type variant");
      } else {
         OpticFinder var6 = DSL.namedChoice("minecraft:custom", var5);
         OpticFinder var7 = DSL.fieldFinder("id", NamespacedSchema.namespacedString());
         return this.fixTypeEverywhereTyped(this.name, var2, var1, (var4x) -> {
            return var4x.updateTyped(var3, (var3x) -> {
               return var3x.updateTyped(var6, (var2) -> {
                  return var2.update(var7, (var1) -> {
                     return (String)this.renames.getOrDefault(var1, var1);
                  });
               });
            });
         });
      }
   }

   private TypeRewriteRule createStatRule() {
      Type var1 = this.getOutputSchema().getType(References.STATS);
      Type var2 = this.getInputSchema().getType(References.STATS);
      OpticFinder var3 = var2.findField("stats");
      OpticFinder var4 = var3.type().findField("minecraft:custom");
      OpticFinder var5 = NamespacedSchema.namespacedString().finder();
      return this.fixTypeEverywhereTyped(this.name, var2, var1, (var4x) -> {
         return var4x.updateTyped(var3, (var3x) -> {
            return var3x.updateTyped(var4, (var2) -> {
               return var2.update(var5, (var1) -> {
                  return (String)this.renames.getOrDefault(var1, var1);
               });
            });
         });
      });
   }
}
