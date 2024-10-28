package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.types.templates.Hook.HookFunction;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V102 extends Schema {
   public V102(int var1, Schema var2) {
      super(var1, var2);
   }

   public void registerTypes(Schema var1, Map<String, Supplier<TypeTemplate>> var2, Map<String, Supplier<TypeTemplate>> var3) {
      super.registerTypes(var1, var2, var3);
      var1.registerType(true, References.ITEM_STACK, () -> {
         return DSL.hook(DSL.optionalFields("id", References.ITEM_NAME.in(var1), "tag", DSL.optionalFields(new Pair[]{Pair.of("EntityTag", References.ENTITY_TREE.in(var1)), Pair.of("BlockEntityTag", References.BLOCK_ENTITY.in(var1)), Pair.of("CanDestroy", DSL.list(References.BLOCK_NAME.in(var1))), Pair.of("CanPlaceOn", DSL.list(References.BLOCK_NAME.in(var1))), Pair.of("Items", DSL.list(References.ITEM_STACK.in(var1))), Pair.of("ChargedProjectiles", DSL.list(References.ITEM_STACK.in(var1)))})), V99.ADD_NAMES, HookFunction.IDENTITY);
      });
   }
}
