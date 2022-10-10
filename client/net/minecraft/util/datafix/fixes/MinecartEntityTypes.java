package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;

public class MinecartEntityTypes extends DataFix {
   private static final List<String> field_188222_a = Lists.newArrayList(new String[]{"MinecartRideable", "MinecartChest", "MinecartFurnace"});

   public MinecartEntityTypes(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public TypeRewriteRule makeRule() {
      TaggedChoiceType var1 = this.getInputSchema().findChoiceType(TypeReferences.field_211299_o);
      TaggedChoiceType var2 = this.getOutputSchema().findChoiceType(TypeReferences.field_211299_o);
      return this.fixTypeEverywhere("EntityMinecartIdentifiersFix", var1, var2, (var2x) -> {
         return (var3) -> {
            if (!Objects.equals(var3.getFirst(), "Minecart")) {
               return var3;
            } else {
               Typed var4 = (Typed)var1.point(var2x, "Minecart", var3.getSecond()).orElseThrow(IllegalStateException::new);
               Dynamic var5 = (Dynamic)var4.getOrCreate(DSL.remainderFinder());
               int var7 = var5.getInt("Type");
               String var6;
               if (var7 > 0 && var7 < field_188222_a.size()) {
                  var6 = (String)field_188222_a.get(var7);
               } else {
                  var6 = "MinecartRideable";
               }

               return Pair.of(var6, ((Optional)((Type)var2.types().get(var6)).read(var4.write()).getSecond()).orElseThrow(() -> {
                  return new IllegalStateException("Could not read the new minecart.");
               }));
            }
         };
      });
   }
}
