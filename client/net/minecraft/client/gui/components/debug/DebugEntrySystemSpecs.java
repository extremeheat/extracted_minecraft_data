package net.minecraft.client.gui.components.debug;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

public class DebugEntrySystemSpecs implements DebugScreenEntry {
   private static final ResourceLocation GROUP = ResourceLocation.withDefaultNamespace("system");

   public DebugEntrySystemSpecs() {
      super();
   }

   public void display(DebugScreenDisplayer var1, @Nullable Level var2, @Nullable LevelChunk var3, @Nullable LevelChunk var4) {
      GpuDevice var5 = RenderSystem.getDevice();
      var1.addToGroup(GROUP, List.of(String.format(Locale.ROOT, "Java: %s", System.getProperty("java.version")), String.format(Locale.ROOT, "CPU: %s", GLX._getCpuInfo()), String.format(Locale.ROOT, "Display: %dx%d (%s)", Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight(), var5.getVendor()), var5.getRenderer(), String.format(Locale.ROOT, "%s %s", var5.getBackendName(), var5.getVersion())));
   }

   public boolean isAllowed(boolean var1) {
      return true;
   }
}
