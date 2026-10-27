package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;

public class DefaultComponentSoundRemainderFix extends DataFix {
   public DefaultComponentSoundRemainderFix(final Schema outputSchema) {
      super(outputSchema, false);
   }

   public final TypeRewriteRule makeRule() {
      Type<?> dataComponentsType = this.getInputSchema().getType(References.DATA_COMPONENTS);
      OpticFinder<?> equippableField = dataComponentsType.findField("minecraft:equippable");
      return this.fixTypeEverywhereTyped("Default sounds in consumable and equippable components fix", dataComponentsType, (components) -> components.updateTyped(equippableField, (equippable) -> equippable.update(DSL.remainderFinder(), (remainder) -> {
               remainder = setFieldIfNotPresent(remainder, "equip_sound", "minecraft:item.armor.equip_generic");
               remainder = setFieldIfNotPresent(remainder, "shearing_sound", "minecraft:item.shears.snip");
               return remainder;
            })).update(DSL.remainderFinder(), (componentsRemainder) -> componentsRemainder.update("minecraft:consumable", (consumable) -> setFieldIfNotPresent(consumable, "sound", "minecraft:entity.generic.eat"))));
   }

   private static <T> Dynamic<T> setFieldIfNotPresent(final Dynamic<T> input, final String field, final String value) {
      return input.get(field).result().isEmpty() ? input.set(field, input.createString(value)) : input;
   }
}
