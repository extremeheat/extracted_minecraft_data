package net.minecraft.world.level.levelgen.structure.pieces;

import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public record StructurePieceSerializationContext(ResourceManager resourceManager, RegistryAccess registryAccess, StructureTemplateManager structureTemplateManager) {
   public StructurePieceSerializationContext(ResourceManager var1, RegistryAccess var2, StructureTemplateManager var3) {
      super();
      this.resourceManager = var1;
      this.registryAccess = var2;
      this.structureTemplateManager = var3;
   }

   public static StructurePieceSerializationContext fromLevel(ServerLevel var0) {
      MinecraftServer var1 = var0.getServer();
      return new StructurePieceSerializationContext(var1.getResourceManager(), var1.registryAccess(), var1.getStructureManager());
   }

   public ResourceManager resourceManager() {
      return this.resourceManager;
   }

   public RegistryAccess registryAccess() {
      return this.registryAccess;
   }

   public StructureTemplateManager structureTemplateManager() {
      return this.structureTemplateManager;
   }
}
