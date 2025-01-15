package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class EquipmentFormatFix extends DataFix {
   public EquipmentFormatFix(Schema var1) {
      super(var1, true);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getTypeRaw(References.ITEM_STACK);
      Type var2 = this.getOutputSchema().getTypeRaw(References.ITEM_STACK);
      OpticFinder var3 = var1.findField("id");
      return this.fix(var1, var2, var3);
   }

   private <ItemStackOld, ItemStackNew> TypeRewriteRule fix(Type<ItemStackOld> var1, Type<ItemStackNew> var2, OpticFinder<?> var3) {
      Type var4 = DSL.named(References.ENTITY_EQUIPMENT.typeName(), DSL.and(DSL.optional(DSL.field("ArmorItems", DSL.list(var1))), DSL.optional(DSL.field("HandItems", DSL.list(var1))), DSL.optional(DSL.field("body_armor_item", var1)), DSL.optional(DSL.field("saddle", var1))));
      Type var5 = DSL.named(References.ENTITY_EQUIPMENT.typeName(), DSL.optional(DSL.field("equipment", DSL.and(DSL.optional(DSL.field("mainhand", var2)), DSL.optional(DSL.field("offhand", var2)), DSL.optional(DSL.field("feet", var2)), DSL.and(DSL.optional(DSL.field("legs", var2)), DSL.optional(DSL.field("chest", var2)), DSL.optional(DSL.field("head", var2)), DSL.and(DSL.optional(DSL.field("body", var2)), DSL.optional(DSL.field("saddle", var2)), DSL.remainderType()))))));
      if (!var4.equals(this.getInputSchema().getType(References.ENTITY_EQUIPMENT))) {
         throw new IllegalStateException("Input entity_equipment type does not match expected");
      } else if (!var5.equals(this.getOutputSchema().getType(References.ENTITY_EQUIPMENT))) {
         throw new IllegalStateException("Output entity_equipment type does not match expected");
      } else {
         return this.fixTypeEverywhere("EquipmentFormatFix", var4, var5, (var2x) -> {
            Predicate var3x = (var3xx) -> {
               Typed var4 = new Typed(var1, var2x, var3xx);
               return var4.getOptional(var3).isEmpty();
            };
            return (var2) -> {
               String var3 = (String)var2.getFirst();
               Pair var4 = (Pair)var2.getSecond();
               List var5 = (List)((Either)var4.getFirst()).map(Function.identity(), (var0) -> List.of());
               List var6 = (List)((Either)((Pair)var4.getSecond()).getFirst()).map(Function.identity(), (var0) -> List.of());
               Either var7 = (Either)((Pair)((Pair)var4.getSecond()).getSecond()).getFirst();
               Either var8 = (Either)((Pair)((Pair)var4.getSecond()).getSecond()).getSecond();
               Either var9 = getItemFromList(0, var5, var3x);
               Either var10 = getItemFromList(1, var5, var3x);
               Either var11 = getItemFromList(2, var5, var3x);
               Either var12 = getItemFromList(3, var5, var3x);
               Either var13 = getItemFromList(0, var6, var3x);
               Either var14 = getItemFromList(1, var6, var3x);
               return areAllEmpty(var7, var8, var9, var10, var11, var12, var13, var14) ? Pair.of(var3, Either.right(Unit.INSTANCE)) : Pair.of(var3, Either.left(Pair.of(var13, Pair.of(var14, Pair.of(var9, Pair.of(var10, Pair.of(var11, Pair.of(var12, Pair.of(var7, Pair.of(var8, new Dynamic(var2x)))))))))));
            };
         });
      }
   }

   @SafeVarargs
   private static boolean areAllEmpty(Either<?, Unit>... var0) {
      for(Either var4 : var0) {
         if (var4.right().isEmpty()) {
            return false;
         }
      }

      return true;
   }

   private static <ItemStack> Either<ItemStack, Unit> getItemFromList(int var0, List<ItemStack> var1, Predicate<ItemStack> var2) {
      if (var0 >= var1.size()) {
         return Either.right(Unit.INSTANCE);
      } else {
         Object var3 = var1.get(var0);
         return var2.test(var3) ? Either.right(Unit.INSTANCE) : Either.left(var3);
      }
   }
}
