package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Objects;

public class BlockEntityBannerColorFix extends NamedEntityFix {
   public BlockEntityBannerColorFix(final Schema outputSchema, final boolean changesType) {
      super(outputSchema, changesType, "BlockEntityBannerColorFix", References.BLOCK_ENTITY, "minecraft:banner");
   }

   public Dynamic<?> fixTag(Dynamic<?> input) {
      input = input.update("Base", (base) -> base.createInt(15 - base.asInt(0)));
      input = input.update("Patterns", (list) -> {
         DataResult var10000 = list.asStreamOpt().map((stream) -> stream.map((pattern) -> pattern.update("Color", (color) -> color.createInt(15 - color.asInt(0)))));
         Objects.requireNonNull(list);
         return (Dynamic)DataFixUtils.orElse(var10000.map(list::createList).result(), list);
      });
      return input;
   }

   protected Typed<?> fix(final Typed<?> entity) {
      return entity.update(DSL.remainderFinder(), this::fixTag);
   }
}
