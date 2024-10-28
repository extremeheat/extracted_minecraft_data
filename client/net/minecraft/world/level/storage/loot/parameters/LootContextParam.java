package net.minecraft.world.level.storage.loot.parameters;

import net.minecraft.resources.ResourceLocation;

public class LootContextParam<T> {
   private final ResourceLocation name;

   public LootContextParam(ResourceLocation var1) {
      super();
      this.name = var1;
   }

   public ResourceLocation getName() {
      return this.name;
   }

   public String toString() {
      return "<parameter " + String.valueOf(this.name) + ">";
   }
}
