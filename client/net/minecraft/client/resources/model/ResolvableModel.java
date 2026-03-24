package net.minecraft.client.resources.model;

import net.minecraft.resources.Identifier;

public interface ResolvableModel {
   void resolveDependencies(Resolver resolver);

   public interface Resolver {
      void markDependency(Identifier id);
   }
}
