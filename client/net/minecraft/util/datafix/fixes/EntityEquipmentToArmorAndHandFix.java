package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
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
import java.util.function.Function;
import java.util.stream.Stream;

public class EntityEquipmentToArmorAndHandFix extends DataFix {
   public EntityEquipmentToArmorAndHandFix(Schema var1) {
      super(var1, true);
   }

   public TypeRewriteRule makeRule() {
      return this.cap(this.getInputSchema().getTypeRaw(References.ITEM_STACK), this.getOutputSchema().getTypeRaw(References.ITEM_STACK));
   }

   private <ItemStackOld, ItemStackNew> TypeRewriteRule cap(Type<ItemStackOld> var1, Type<ItemStackNew> var2) {
      Type var3 = DSL.named(References.ENTITY_EQUIPMENT.typeName(), DSL.optional(DSL.field("Equipment", DSL.list(var1))));
      Type var4 = DSL.named(References.ENTITY_EQUIPMENT.typeName(), DSL.and(DSL.optional(DSL.field("ArmorItems", DSL.list(var2))), DSL.optional(DSL.field("HandItems", DSL.list(var2))), DSL.optional(DSL.field("body_armor_item", var2)), DSL.optional(DSL.field("saddle", var2))));
      if (!var3.equals(this.getInputSchema().getType(References.ENTITY_EQUIPMENT))) {
         throw new IllegalStateException("Input entity_equipment type does not match expected");
      } else if (!var4.equals(this.getOutputSchema().getType(References.ENTITY_EQUIPMENT))) {
         throw new IllegalStateException("Output entity_equipment type does not match expected");
      } else {
         return TypeRewriteRule.seq(this.fixTypeEverywhereTyped("EntityEquipmentToArmorAndHandFix - drop chances", this.getInputSchema().getType(References.ENTITY), (var0) -> var0.update(DSL.remainderFinder(), EntityEquipmentToArmorAndHandFix::fixDropChances)), this.fixTypeEverywhere("EntityEquipmentToArmorAndHandFix - equipment", var3, var4, (var1x) -> {
            Object var2x = ((Pair)var2.read((new Dynamic(var1x)).emptyMap()).result().orElseThrow(() -> new IllegalStateException("Could not parse newly created empty itemstack."))).getFirst();
            Either var3 = Either.right(DSL.unit());
            return (var2xx) -> var2xx.mapSecond((var2) -> {
                  List var3x = (List)var2.map(Function.identity(), (var0) -> List.of());
                  Either var4 = Either.right(DSL.unit());
                  Either var5 = Either.right(DSL.unit());
                  if (!var3x.isEmpty()) {
                     var4 = Either.left(Lists.newArrayList(new Object[]{var3x.getFirst(), var2x}));
                  }

                  if (var3x.size() > 1) {
                     ArrayList var6 = Lists.newArrayList(new Object[]{var2x, var2x, var2x, var2x});

                     for(int var7 = 1; var7 < Math.min(var3x.size(), 5); ++var7) {
                        var6.set(var7 - 1, var3x.get(var7));
                     }

                     var5 = Either.left(var6);
                  }

                  return Pair.of(var5, Pair.of(var4, Pair.of(var3, var3)));
               });
         }));
      }
   }

   private static Dynamic<?> fixDropChances(Dynamic<?> var0) {
      Optional var1 = var0.get("DropChances").asStreamOpt().result();
      var0 = var0.remove("DropChances");
      if (var1.isPresent()) {
         Iterator var2 = Stream.concat(((Stream)var1.get()).map((var0x) -> var0x.asFloat(0.0F)), Stream.generate(() -> 0.0F)).iterator();
         float var3 = (Float)var2.next();
         if (var0.get("HandDropChances").result().isEmpty()) {
            Stream var10003 = Stream.of(var3, 0.0F);
            Objects.requireNonNull(var0);
            var0 = var0.set("HandDropChances", var0.createList(var10003.map(var0::createFloat)));
         }

         if (var0.get("ArmorDropChances").result().isEmpty()) {
            Stream var5 = Stream.of((Float)var2.next(), (Float)var2.next(), (Float)var2.next(), (Float)var2.next());
            Objects.requireNonNull(var0);
            var0 = var0.set("ArmorDropChances", var0.createList(var5.map(var0::createFloat)));
         }
      }

      return var0;
   }
}
