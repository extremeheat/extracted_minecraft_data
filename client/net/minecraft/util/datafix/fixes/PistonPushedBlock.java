package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;

public class PistonPushedBlock extends NamedEntityFix {
   public PistonPushedBlock(Schema var1, boolean var2) {
      super(var1, var2, "BlockEntityBlockStateFix", TypeReferences.field_211294_j, "minecraft:piston");
   }

   protected Typed<?> func_207419_a(Typed<?> var1) {
      Type var2 = this.getOutputSchema().getChoiceType(TypeReferences.field_211294_j, "minecraft:piston");
      Type var3 = var2.findFieldType("blockState");
      OpticFinder var4 = DSL.fieldFinder("blockState", var3);
      Dynamic var5 = (Dynamic)var1.get(DSL.remainderFinder());
      int var6 = var5.getInt("blockId");
      var5 = var5.remove("blockId");
      int var7 = var5.getInt("blockData") & 15;
      var5 = var5.remove("blockData");
      Dynamic var8 = BlockStateFlatteningMap.func_210049_b(var6 << 4 | var7);
      Typed var9 = (Typed)var2.pointTyped(var1.getOps()).orElseThrow(() -> {
         return new IllegalStateException("Could not create new piston block entity.");
      });
      return var9.set(DSL.remainderFinder(), var5).set(var4, (Typed)((Optional)var3.readTyped(var8).getSecond()).orElseThrow(() -> {
         return new IllegalStateException("Could not parse newly created block state tag.");
      }));
   }
}
