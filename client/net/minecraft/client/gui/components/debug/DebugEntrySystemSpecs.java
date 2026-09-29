package net.minecraft.client.gui.components.debug;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.device.DeviceInfo;
import com.mojang.renderpearl.api.device.DeviceType;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

public class DebugEntrySystemSpecs implements DebugScreenEntry {
   private static @Nullable String cpuInfo;

   public DebugEntrySystemSpecs() {
      super();
   }

   public static String getCpuInfo() {
      if (cpuInfo == null) {
         cpuInfo = "<unknown>";

         try {
            CentralProcessor processor = (new SystemInfo()).getHardware().getProcessor();
            cpuInfo = String.format(Locale.ROOT, "%dx %s", processor.getLogicalProcessorCount(), processor.getProcessorIdentifier().getName()).replaceAll("\\s+", " ");
         } catch (Throwable var1) {
         }
      }

      return cpuInfo;
   }

   public void display(final DebugScreenDisplayer displayer, final @Nullable Level serverOrClientLevel, final @Nullable LevelChunk clientChunk, final @Nullable LevelChunk serverChunk) {
      DeviceInfo deviceInfo = RenderSystem.getDevice().getDeviceInfo();
      Window window = Minecraft.getInstance().getWindow();
      displayer.addFactToGroup(DebugGroups.SYSTEM_SPECS, "Java", (fact) -> fact.value(System.getProperty("java.version")));
      displayer.addFactToGroup(DebugGroups.SYSTEM_SPECS, "CPU", (fact) -> fact.value(getCpuInfo()));
      displayer.addFactToGroup(DebugGroups.SYSTEM_SPECS, "Display", (fact) -> fact.value(window.getWidth()).text("x").value(window.getHeight()).text(" (").value(deviceInfo.vendorName()).text(")"));
      displayer.addFactToGroup(DebugGroups.SYSTEM_SPECS, "Window", (fact) -> fact.value(window.getScreenWidth()).text("x").value(window.getScreenHeight()).text(" (").formattedValue("%.2f", window.getPixelDensity()).text("x pixel density)"));
      displayer.addToGroup(DebugGroups.SYSTEM_SPECS, List.of(String.format(Locale.ROOT, "%s%s", deviceInfo.name(), this.typeName(deviceInfo.type())), String.format(Locale.ROOT, "%s %s", deviceInfo.backendName(), this.firstLine(deviceInfo.driverInfo()))));
   }

   private String firstLine(final String value) {
      return (String)value.lines().findFirst().orElse(value);
   }

   private String typeName(final DeviceType type) {
      String var10000;
      switch (type) {
         case OTHER -> var10000 = "";
         case INTEGRATED -> var10000 = " (iGPU)";
         case DISCRETE -> var10000 = " (dGPU)";
         case VIRTUAL -> var10000 = " (vGPU)";
         case CPU -> var10000 = " (software)";
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public boolean isAllowed(final boolean reducedDebugInfo) {
      return true;
   }
}
