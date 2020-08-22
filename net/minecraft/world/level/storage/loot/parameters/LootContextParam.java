package net.minecraft.world.level.storage.loot.parameters;

import net.minecraft.resources.ResourceLocation;

public class LootContextParam {
   private final ResourceLocation name;

   public LootContextParam(ResourceLocation var1) {
      this.name = var1;
   }

   public ResourceLocation getName() {
      return this.name;
   }

   public String toString() {
      return "<parameter " + this.name + ">";
   }
}
