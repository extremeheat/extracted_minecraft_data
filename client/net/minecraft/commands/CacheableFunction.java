package net.minecraft.commands;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.resources.Identifier;
import net.minecraft.server.ServerFunctionManager;

public class CacheableFunction {
   public static final Codec<CacheableFunction> CODEC;
   private final Identifier id;
   private boolean resolved;
   private Optional<CommandFunction<CommandSourceStack>> function = Optional.empty();

   public CacheableFunction(Identifier var1) {
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

   public Identifier getId() {
      return this.id;
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else {
         boolean var10000;
         if (var1 instanceof CacheableFunction) {
            CacheableFunction var2 = (CacheableFunction)var1;
            if (this.getId().equals(var2.getId())) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   static {
      CODEC = Identifier.CODEC.xmap(CacheableFunction::new, CacheableFunction::getId);
   }
}
