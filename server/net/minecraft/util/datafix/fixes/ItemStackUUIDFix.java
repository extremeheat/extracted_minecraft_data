package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class ItemStackUUIDFix extends AbstractUUIDFix {
   public ItemStackUUIDFix(Schema var1) {
      super(var1, References.ITEM_STACK);
   }

   public TypeRewriteRule makeRule() {
      OpticFinder var1 = DSL.fieldFinder("id", DSL.named(References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
      return this.fixTypeEverywhereTyped("ItemStackUUIDFix", this.getInputSchema().getType(this.typeReference), (var2) -> {
         OpticFinder var3 = var2.getType().findField("tag");
         return var2.updateTyped(var3, (var3x) -> {
            return var3x.update(DSL.remainderFinder(), (var3) -> {
               var3 = this.updateAttributeModifiers(var3);
               if ((Boolean)var2.getOptional(var1).map((var0) -> {
                  return "minecraft:player_head".equals(var0.getSecond());
               }).orElse(false)) {
                  var3 = this.updateSkullOwner(var3);
               }

               return var3;
            });
         });
      });
   }

   private Dynamic<?> updateAttributeModifiers(Dynamic<?> var1) {
      return var1.update("AttributeModifiers", (var1x) -> {
         return var1.createList(var1x.asStream().map((var0) -> {
            return (Dynamic)replaceUUIDLeastMost(var0, "UUID", "UUID").orElse(var0);
         }));
      });
   }

   private Dynamic<?> updateSkullOwner(Dynamic<?> var1) {
      return var1.update("SkullOwner", (var0) -> {
         return (Dynamic)replaceUUIDString(var0, "Id", "Id").orElse(var0);
      });
   }
}
