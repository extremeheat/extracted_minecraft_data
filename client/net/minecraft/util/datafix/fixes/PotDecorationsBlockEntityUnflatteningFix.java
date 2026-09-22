package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.datafix.ExtraDataFixUtils;

public class PotDecorationsBlockEntityUnflatteningFix extends NamedEntityFix {
   public PotDecorationsBlockEntityUnflatteningFix(final Schema outputSchema) {
      super(outputSchema, true, "PotDecorationsBlockEntityUnflatteningFix", References.BLOCK_ENTITY, "minecraft:decorated_pot");
   }

   protected Typed<?> fix(final Typed<?> entity) {
      Type<?> newType = (Type)this.getOutputSchema().findChoiceType(References.BLOCK_ENTITY).types().get("minecraft:decorated_pot");
      return ExtraDataFixUtils.writeAndReadTypedOrThrow(entity, newType, (contents) -> {
         Dynamic<?> original = contents.get("sherds").orElseEmptyList();
         return contents.set("sherds", PotDecorationsComponentUnflatteningFix.unpackList(original));
      });
   }
}
