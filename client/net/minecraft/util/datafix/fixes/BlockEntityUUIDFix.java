package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class BlockEntityUUIDFix extends AbstractUUIDFix {
   public BlockEntityUUIDFix(Schema var1) {
      super(var1, References.BLOCK_ENTITY);
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("BlockEntityUUIDFix", this.getInputSchema().getType(this.typeReference), var1 -> {
         var1 = this.updateNamedChoice(var1, "minecraft:conduit", this::updateConduit);
         return this.updateNamedChoice(var1, "minecraft:skull", this::updateSkull);
      });
   }

   private Dynamic<?> updateSkull(Dynamic<?> var1) {
      return var1.get("Owner")
         .get()
         .map(var0 -> replaceUUIDString(var0, "Id", "Id").orElse(var0))
         .map(var1x -> var1.remove("Owner").set("SkullOwner", var1x))
         .result()
         .orElse(var1);
   }

   private Dynamic<?> updateConduit(Dynamic<?> var1) {
      return replaceUUIDMLTag(var1, "target_uuid", "Target").orElse(var1);
   }
}
