package net.minecraft.commands;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerFunctionManager;

public class CacheableFunction {
   public static final Codec<CacheableFunction> CODEC = ResourceLocation.CODEC.xmap(CacheableFunction::new, CacheableFunction::getId);
   private final ResourceLocation id;
   private boolean resolved;
   private Optional<CommandFunction<CommandSourceStack>> function = Optional.empty();

   public CacheableFunction(ResourceLocation var1) {
      super();
      this.id = var1;
   }

   public Optional<CommandFunction<CommandSourceStack>> get(ServerFunctionManager var1) {
      if (!this.resolved) {
         this.function = var1.get(this.id);
         this.resolved = true;
      }

      return this.function;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else {
         if (var1 instanceof CacheableFunction var2 && this.getId().equals(var2.getId())) {
            return true;
         }

         return false;
      }
   }
}
