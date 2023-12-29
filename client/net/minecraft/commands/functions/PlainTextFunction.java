package net.minecraft.commands.functions;

import com.mojang.brigadier.CommandDispatcher;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.commands.FunctionInstantiationException;
import net.minecraft.commands.execution.UnboundEntryAction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public record PlainTextFunction<T>(ResourceLocation a, List<UnboundEntryAction<T>> b) implements CommandFunction<T>, InstantiatedFunction<T> {
   private final ResourceLocation id;
   private final List<UnboundEntryAction<T>> entries;

   public PlainTextFunction(ResourceLocation var1, List<UnboundEntryAction<T>> var2) {
      super();
      this.id = var1;
      this.entries = var2;
   }

   @Override
   public InstantiatedFunction<T> instantiate(@Nullable CompoundTag var1, CommandDispatcher<T> var2, T var3) throws FunctionInstantiationException {
      return this;
   }
}
