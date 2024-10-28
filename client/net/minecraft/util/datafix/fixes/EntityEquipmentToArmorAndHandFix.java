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
      Type var3 = DSL.and(DSL.optional(DSL.field("ArmorItems", DSL.list(var1))), DSL.optional(DSL.field("HandItems", DSL.list(var1))), DSL.optional(DSL.field("body_armor_item", var1)), DSL.remainderType());
      OpticFinder var4 = DSL.typeFinder(var2);
      OpticFinder var5 = DSL.fieldFinder("Equipment", DSL.list(var1));
      return this.fixTypeEverywhereTyped("EntityEquipmentToArmorAndHandFix", this.getInputSchema().getType(References.ENTITY), this.getOutputSchema().getType(References.ENTITY), (var4x) -> {
         Either var5x = Either.right(DSL.unit());
         Either var6 = Either.right(DSL.unit());
         Either var7 = Either.right(DSL.unit());
         Dynamic var8 = (Dynamic)var4x.getOrCreate(DSL.remainderFinder());
         Optional var9 = var4x.getOptional(var5);
         if (var9.isPresent()) {
            List var10 = (List)var9.get();
            Object var11 = ((Pair)var1.read(var8.emptyMap()).result().orElseThrow(() -> {
               return new IllegalStateException("Could not parse newly created empty itemstack.");
            })).getFirst();
            if (!var10.isEmpty()) {
               var5x = Either.left(Lists.newArrayList(new Object[]{var10.get(0), var11}));
            }

            if (var10.size() > 1) {
               ArrayList var12 = Lists.newArrayList(new Object[]{var11, var11, var11, var11});

               for(int var13 = 1; var13 < Math.min(var10.size(), 5); ++var13) {
                  var12.set(var13 - 1, var10.get(var13));
               }

               var6 = Either.left(var12);
            }
         }

         Optional var15 = var8.get("DropChances").asStreamOpt().result();
         if (var15.isPresent()) {
            Iterator var16 = Stream.concat((Stream)var15.get(), Stream.generate(() -> {
               return var8.createInt(0);
            })).iterator();
            float var17 = ((Dynamic)var16.next()).asFloat(0.0F);
            Dynamic var14;
            Stream var10001;
            if (var8.get("HandDropChances").result().isEmpty()) {
               var10001 = Stream.of(var17, 0.0F);
               Objects.requireNonNull(var8);
               var14 = var8.createList(var10001.map(var8::createFloat));
               var8 = var8.set("HandDropChances", var14);
            }

            if (var8.get("ArmorDropChances").result().isEmpty()) {
               var10001 = Stream.of(((Dynamic)var16.next()).asFloat(0.0F), ((Dynamic)var16.next()).asFloat(0.0F), ((Dynamic)var16.next()).asFloat(0.0F), ((Dynamic)var16.next()).asFloat(0.0F));
               Objects.requireNonNull(var8);
               var14 = var8.createList(var10001.map(var8::createFloat));
               var8 = var8.set("ArmorDropChances", var14);
            }

            var8 = var8.remove("DropChances");
         }

         return var4x.set(var4, var3, Pair.of(var5x, Pair.of(var6, Pair.of(var7, var8))));
      });
   }
}
