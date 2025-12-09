package net.minecraft.client.resources.model;

import net.minecraft.resources.Identifier;

public interface ResolvableModel {
   void resolveDependencies(Resolver var1);

   public interface Resolver {
      void markDependency(Identifier var1);
   }
}
