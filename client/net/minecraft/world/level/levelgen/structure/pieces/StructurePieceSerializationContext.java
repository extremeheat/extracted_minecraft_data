package net.minecraft.world.level.levelgen.structure.pieces;

import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public record StructurePieceSerializationContext(
   ResourceManager resourceManager, RegistryAccess registryAccess, StructureTemplateManager structureTemplateManager
) {
   public StructurePieceSerializationContext(ResourceManager resourceManager, RegistryAccess registryAccess, StructureTemplateManager structureTemplateManager) {
      super();
      this.resourceManager = resourceManager;
      this.registryAccess = registryAccess;
      this.structureTemplateManager = structureTemplateManager;
   }

   public static StructurePieceSerializationContext fromLevel(ServerLevel var0) {
      MinecraftServer var1 = var0.getServer();
      return new StructurePieceSerializationContext(var1.getResourceManager(), var1.registryAccess(), var1.getStructureManager());
   }
}
