package net.minecraft.commands.functions;

import java.util.List;
import net.minecraft.commands.FunctionInstantiationException;
import net.minecraft.commands.execution.UnboundEntryAction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public record PlainTextFunction<Runtime>(Identifier id, List<UnboundEntryAction<Runtime>> entries) implements CommandFunction<Runtime>, InstantiatedFunction<Runtime> {
   public PlainTextFunction {
      super();
   }

   public InstantiatedFunction<Runtime> instantiate(final @Nullable CompoundTag arguments) throws FunctionInstantiationException {
      return this;
   }
}
