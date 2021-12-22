package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Objects;

public class EntityMinecartIdentifiersFix extends DataFix {
   private static final List<String> MINECART_BY_ID = Lists.newArrayList(new String[]{"MinecartRideable", "MinecartChest", "MinecartFurnace"});

   public EntityMinecartIdentifiersFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public TypeRewriteRule makeRule() {
      TaggedChoiceType var1 = this.getInputSchema().findChoiceType(References.ENTITY);
      TaggedChoiceType var2 = this.getOutputSchema().findChoiceType(References.ENTITY);
      return this.fixTypeEverywhere("EntityMinecartIdentifiersFix", var1, var2, (var2x) -> {
         return (var3) -> {
            if (!Objects.equals(var3.getFirst(), "Minecart")) {
               return var3;
            } else {
               Typed var4 = (Typed)var1.point(var2x, "Minecart", var3.getSecond()).orElseThrow(IllegalStateException::new);
               Dynamic var5 = (Dynamic)var4.getOrCreate(DSL.remainderFinder());
               int var7 = var5.get("Type").asInt(0);
               String var6;
               if (var7 > 0 && var7 < MINECART_BY_ID.size()) {
                  var6 = (String)MINECART_BY_ID.get(var7);
               } else {
                  var6 = "MinecartRideable";
               }

               return Pair.of(var6, (DataResult)var4.write().map((var2xx) -> {
                  return ((Type)var2.types().get(var6)).read(var2xx);
               }).result().orElseThrow(() -> {
                  return new IllegalStateException("Could not read the new minecart.");
               }));
            }
         };
      });
   }
}
