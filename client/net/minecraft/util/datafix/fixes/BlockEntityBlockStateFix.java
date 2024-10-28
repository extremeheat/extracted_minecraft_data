package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;

public class BlockEntityBlockStateFix extends NamedEntityFix {
   public BlockEntityBlockStateFix(Schema var1, boolean var2) {
      super(var1, var2, "BlockEntityBlockStateFix", References.BLOCK_ENTITY, "minecraft:piston");
   }

   protected Typed<?> fix(Typed<?> var1) {
      Type var2 = this.getOutputSchema().getChoiceType(References.BLOCK_ENTITY, "minecraft:piston");
      Type var3 = var2.findFieldType("blockState");
      OpticFinder var4 = DSL.fieldFinder("blockState", var3);
      Dynamic var5 = (Dynamic)var1.get(DSL.remainderFinder());
      int var6 = var5.get("blockId").asInt(0);
      var5 = var5.remove("blockId");
      int var7 = var5.get("blockData").asInt(0) & 15;
      var5 = var5.remove("blockData");
      Dynamic var8 = BlockStateData.getTag(var6 << 4 | var7);
      Typed var9 = (Typed)var2.pointTyped(var1.getOps()).orElseThrow(() -> {
         return new IllegalStateException("Could not create new piston block entity.");
      });
      return var9.set(DSL.remainderFinder(), var5).set(var4, (Typed)((Pair)var3.readTyped(var8).result().orElseThrow(() -> {
         return new IllegalStateException("Could not parse newly created block state tag.");
      })).getFirst());
   }
}
