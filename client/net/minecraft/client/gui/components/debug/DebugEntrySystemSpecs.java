package net.minecraft.client.gui.components.debug;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.systems.DeviceInfo;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

public class DebugEntrySystemSpecs implements DebugScreenEntry {
   private static final Identifier GROUP = Identifier.withDefaultNamespace("system");

   public DebugEntrySystemSpecs() {
      super();
   }

   public void display(final DebugScreenDisplayer displayer, final @Nullable Level serverOrClientLevel, final @Nullable LevelChunk clientChunk, final @Nullable LevelChunk serverChunk) {
      DeviceInfo deviceInfo = RenderSystem.getDevice().getDeviceInfo();
      displayer.addToGroup(GROUP, List.of(String.format(Locale.ROOT, "Java: %s", System.getProperty("java.version")), String.format(Locale.ROOT, "CPU: %s", GLX._getCpuInfo()), String.format(Locale.ROOT, "Display: %dx%d (%s)", Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight(), deviceInfo.vendorName()), deviceInfo.name(), String.format(Locale.ROOT, "%s %s", deviceInfo.backendName(), deviceInfo.driverInfo())));
   }

   public boolean isAllowed(final boolean reducedDebugInfo) {
      return true;
   }
}
