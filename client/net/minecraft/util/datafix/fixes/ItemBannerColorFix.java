package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class ItemBannerColorFix extends DataFix {
   public ItemBannerColorFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.ITEM_STACK);
      OpticFinder var2 = DSL.fieldFinder("id", DSL.named(References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
      OpticFinder var3 = var1.findField("tag");
      OpticFinder var4 = var3.type().findField("BlockEntityTag");
      return this.fixTypeEverywhereTyped("ItemBannerColorFix", var1, (var3x) -> {
         Optional var4x = var3x.getOptional(var2);
         if (var4x.isPresent() && Objects.equals(((Pair)var4x.get()).getSecond(), "minecraft:banner")) {
            Dynamic var5 = (Dynamic)var3x.get(DSL.remainderFinder());
            Optional var6 = var3x.getOptionalTyped(var3);
            if (var6.isPresent()) {
               Typed var7 = (Typed)var6.get();
               Optional var8 = var7.getOptionalTyped(var4);
               if (var8.isPresent()) {
                  Typed var9 = (Typed)var8.get();
                  Dynamic var10 = (Dynamic)var7.get(DSL.remainderFinder());
                  Dynamic var11 = (Dynamic)var9.getOrCreate(DSL.remainderFinder());
                  if (var11.get("Base").asNumber().result().isPresent()) {
                     var5 = var5.set("Damage", var5.createShort((short)(var11.get("Base").asInt(0) & 15)));
                     Optional var12 = var10.get("display").result();
                     if (var12.isPresent()) {
                        Dynamic var13 = (Dynamic)var12.get();
                        Dynamic var14 = var13.createMap(ImmutableMap.of(var13.createString("Lore"), var13.createList(Stream.of(var13.createString("(+NBT")))));
                        if (Objects.equals(var13, var14)) {
                           return var3x.set(DSL.remainderFinder(), var5);
                        }
                     }

                     var11.remove("Base");
                     return var3x.set(DSL.remainderFinder(), var5).set(var3, var7.set(var4, var9.set(DSL.remainderFinder(), var11)));
                  }
               }
            }

            return var3x.set(DSL.remainderFinder(), var5);
         } else {
            return var3x;
         }
      });
   }
}
