package net.minecraft.client.gui.screens.worldselection;

import java.nio.file.Path;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.server.RegistryLayer;
import net.minecraft.world.level.storage.PrimaryLevelData;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface CreateWorldCallback {
   boolean create(CreateWorldScreen createWorldScreen, LayeredRegistryAccess<RegistryLayer> finalLayers, PrimaryLevelData worldData, @Nullable Path tempDataPackDir);
}
