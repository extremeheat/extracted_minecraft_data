package net.minecraft.commands;

import com.mojang.serialization.Codec;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.resources.Identifier;
import net.minecraft.server.ServerFunctionManager;

public class CacheableFunction {
   public static final Codec<CacheableFunction> CODEC;
   private final Identifier id;
   private boolean resolved;
   private Optional<CommandFunction<CommandSourceStack>> function = Optional.empty();

   public CacheableFunction(final Identifier id) {
      super();
      this.id = id;
   }

   public Optional<CommandFunction<CommandSourceStack>> get(final ServerFunctionManager manager) {
      if (!this.resolved) {
         this.function = manager.get(this.id);
         this.resolved = true;
      }

      return this.function;
   }

   public Identifier getId() {
      return this.id;
   }

   public boolean equals(final Object obj) {
      if (obj == this) {
         return true;
      } else {
         boolean var10000;
         if (obj instanceof CacheableFunction) {
            CacheableFunction cacheableFunction = (CacheableFunction)obj;
            if (this.getId().equals(cacheableFunction.getId())) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.id});
   }

   static {
      CODEC = Identifier.CODEC.xmap(CacheableFunction::new, CacheableFunction::getId);
   }
}
