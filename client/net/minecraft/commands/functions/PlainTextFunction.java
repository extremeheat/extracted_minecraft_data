package net.minecraft.commands.functions;

import com.mojang.brigadier.CommandDispatcher;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.commands.FunctionInstantiationException;
import net.minecraft.commands.execution.UnboundEntryAction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public record PlainTextFunction<T>(ResourceLocation id, List<UnboundEntryAction<T>> entries) implements CommandFunction<T>, InstantiatedFunction<T> {
   public PlainTextFunction(ResourceLocation var1, List<UnboundEntryAction<T>> var2) {
      super();
      this.id = var1;
      this.entries = var2;
   }

   public InstantiatedFunction<T> instantiate(@Nullable CompoundTag var1, CommandDispatcher<T> var2) throws FunctionInstantiationException {
      return this;
   }

   public ResourceLocation id() {
      return this.id;
   }

   public List<UnboundEntryAction<T>> entries() {
      return this.entries;
   }
}
