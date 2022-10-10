package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.util.datafix.TypeReferences;

public class EntityArmorAndHeld extends DataFix {
   public EntityArmorAndHeld(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public TypeRewriteRule makeRule() {
      return this.func_206323_b(this.getInputSchema().getTypeRaw(TypeReferences.field_211295_k));
   }

   private <IS> TypeRewriteRule func_206323_b(Type<IS> var1) {
      Type var2 = DSL.and(DSL.optional(DSL.field("Equipment", DSL.list(var1))), DSL.remainderType());
      Type var3 = DSL.and(DSL.optional(DSL.field("ArmorItems", DSL.list(var1))), DSL.optional(DSL.field("HandItems", DSL.list(var1))), DSL.remainderType());
      OpticFinder var4 = DSL.typeFinder(var2);
      OpticFinder var5 = DSL.fieldFinder("Equipment", DSL.list(var1));
      return this.fixTypeEverywhereTyped("EntityEquipmentToArmorAndHandFix", this.getInputSchema().getType(TypeReferences.field_211299_o), this.getOutputSchema().getType(TypeReferences.field_211299_o), (var4x) -> {
         Either var5x = Either.right(DSL.unit());
         Either var6 = Either.right(DSL.unit());
         Dynamic var7 = (Dynamic)var4x.getOrCreate(DSL.remainderFinder());
         Optional var8 = var4x.getOptional(var5);
         if (var8.isPresent()) {
            List var9 = (List)var8.get();
            Object var10 = ((Optional)var1.read(var7.emptyMap()).getSecond()).orElseThrow(() -> {
               return new IllegalStateException("Could not parse newly created empty itemstack.");
            });
            if (!var9.isEmpty()) {
               var5x = Either.left(Lists.newArrayList(new Object[]{var9.get(0), var10}));
            }

            if (var9.size() > 1) {
               ArrayList var11 = Lists.newArrayList(new Object[]{var10, var10, var10, var10});

               for(int var12 = 1; var12 < Math.min(var9.size(), 5); ++var12) {
                  var11.set(var12 - 1, var9.get(var12));
               }

               var6 = Either.left(var11);
            }
         }

         Optional var14 = var7.get("DropChances").flatMap(Dynamic::getStream);
         if (var14.isPresent()) {
            Iterator var15 = Stream.concat((Stream)var14.get(), Stream.generate(() -> {
               return var7.createInt(0);
            })).iterator();
            float var16 = ((Dynamic)var15.next()).getNumberValue(0).floatValue();
            Dynamic var13;
            if (!var7.get("HandDropChances").isPresent()) {
               var13 = var7.emptyMap().merge(var7.createFloat(var16)).merge(var7.createFloat(0.0F));
               var7 = var7.set("HandDropChances", var13);
            }

            if (!var7.get("ArmorDropChances").isPresent()) {
               var13 = var7.emptyMap().merge(var7.createFloat(((Dynamic)var15.next()).getNumberValue(0).floatValue())).merge(var7.createFloat(((Dynamic)var15.next()).getNumberValue(0).floatValue())).merge(var7.createFloat(((Dynamic)var15.next()).getNumberValue(0).floatValue())).merge(var7.createFloat(((Dynamic)var15.next()).getNumberValue(0).floatValue()));
               var7 = var7.set("ArmorDropChances", var13);
            }

            var7 = var7.remove("DropChances");
         }

         return var4x.set(var4, var3, Pair.of(var5x, Pair.of(var6, var7)));
      });
   }
}
