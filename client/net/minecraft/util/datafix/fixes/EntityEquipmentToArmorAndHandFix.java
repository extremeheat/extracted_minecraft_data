package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class EntityEquipmentToArmorAndHandFix extends DataFix {
   public EntityEquipmentToArmorAndHandFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public TypeRewriteRule makeRule() {
      return this.cap(this.getInputSchema().getTypeRaw(References.ITEM_STACK));
   }

   private <IS> TypeRewriteRule cap(Type<IS> var1) {
      Type var2 = DSL.and(DSL.optional(DSL.field("Equipment", DSL.list(var1))), DSL.remainderType());
      Type var3 = DSL.and(DSL.optional(DSL.field("ArmorItems", DSL.list(var1))), DSL.optional(DSL.field("HandItems", DSL.list(var1))), DSL.remainderType());
      OpticFinder var4 = DSL.typeFinder(var2);
      OpticFinder var5 = DSL.fieldFinder("Equipment", DSL.list(var1));
      return this.fixTypeEverywhereTyped("EntityEquipmentToArmorAndHandFix", this.getInputSchema().getType(References.ENTITY), this.getOutputSchema().getType(References.ENTITY), (var4x) -> {
         Either var5x = Either.right(DSL.unit());
         Either var6 = Either.right(DSL.unit());
         Dynamic var7 = (Dynamic)var4x.getOrCreate(DSL.remainderFinder());
         Optional var8 = var4x.getOptional(var5);
         if (var8.isPresent()) {
            List var9 = (List)var8.get();
            Object var10 = ((Pair)var1.read(var7.emptyMap()).result().orElseThrow(() -> {
               return new IllegalStateException("Could not parse newly created empty itemstack.");
            })).getFirst();
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

         Optional var14 = var7.get("DropChances").asStreamOpt().result();
         if (var14.isPresent()) {
            Iterator var15 = Stream.concat((Stream)var14.get(), Stream.generate(() -> {
               return var7.createInt(0);
            })).iterator();
            float var16 = ((Dynamic)var15.next()).asFloat(0.0F);
            Dynamic var13;
            Stream var10001;
            if (!var7.get("HandDropChances").result().isPresent()) {
               var10001 = Stream.of(var16, 0.0F);
               Objects.requireNonNull(var7);
               var13 = var7.createList(var10001.map(var7::createFloat));
               var7 = var7.set("HandDropChances", var13);
            }

            if (!var7.get("ArmorDropChances").result().isPresent()) {
               var10001 = Stream.of(((Dynamic)var15.next()).asFloat(0.0F), ((Dynamic)var15.next()).asFloat(0.0F), ((Dynamic)var15.next()).asFloat(0.0F), ((Dynamic)var15.next()).asFloat(0.0F));
               Objects.requireNonNull(var7);
               var13 = var7.createList(var10001.map(var7::createFloat));
               var7 = var7.set("ArmorDropChances", var13);
            }

            var7 = var7.remove("DropChances");
         }

         return var4x.set(var4, var3, Pair.of(var5x, Pair.of(var6, var7)));
      });
   }
}
