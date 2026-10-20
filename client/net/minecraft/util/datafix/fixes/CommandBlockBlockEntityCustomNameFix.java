package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class CommandBlockBlockEntityCustomNameFix extends NamedEntityWriteReadFix {
   public CommandBlockBlockEntityCustomNameFix(final Schema outputSchema) {
      super(outputSchema, true, "Command block block entity custom name fix", References.BLOCK_ENTITY, "minecraft:command_block");
   }

   protected <T> Dynamic<T> fix(final Dynamic<T> input) {
      return BlockEntityCustomNameToComponentFix.<T>fixTagCustomName(input);
   }
}
