package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.PhysicalMemory;
import oshi.hardware.VirtualMemory;

public class SystemReport {
   public static final long BYTES_PER_MEBIBYTE = 1048576L;
   private static final long ONE_GIGA = 1000000000L;
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String OPERATING_SYSTEM;
   private static final String JAVA_VERSION;
   private static final String JAVA_VM_VERSION;
   private final Map<String, String> entries = Maps.newLinkedHashMap();

   public SystemReport() {
      super();
      this.setDetail("Minecraft Version", SharedConstants.getCurrentVersion().getName());
      this.setDetail("Minecraft Version ID", SharedConstants.getCurrentVersion().getId());
      this.setDetail("Operating System", OPERATING_SYSTEM);
      this.setDetail("Java Version", JAVA_VERSION);
      this.setDetail("Java VM Version", JAVA_VM_VERSION);
      this.setDetail("Memory", () -> {
         Runtime var0 = Runtime.getRuntime();
         long var1 = var0.maxMemory();
         long var3 = var0.totalMemory();
         long var5 = var0.freeMemory();
         long var7 = var1 / 1048576L;
         long var9 = var3 / 1048576L;
         long var11 = var5 / 1048576L;
         return "" + var5 + " bytes (" + var11 + " MiB) / " + var3 + " bytes (" + var9 + " MiB) up to " + var1 + " bytes (" + var7 + " MiB)";
      });
      this.setDetail("CPUs", () -> {
         return String.valueOf(Runtime.getRuntime().availableProcessors());
      });
      this.ignoreErrors("hardware", () -> {
         this.putHardware(new SystemInfo());
      });
      this.setDetail("JVM Flags", () -> {
         List var0 = (List)Util.getVmArguments().collect(Collectors.toList());
         return String.format(Locale.ROOT, "%d total; %s", var0.size(), String.join(" ", var0));
      });
   }

   public void setDetail(String var1, String var2) {
      this.entries.put(var1, var2);
   }

   public void setDetail(String var1, Supplier<String> var2) {
      try {
         this.setDetail(var1, (String)var2.get());
      } catch (Exception var4) {
         LOGGER.warn("Failed to get system info for {}", var1, var4);
         this.setDetail(var1, "ERR");
      }

   }

   private void putHardware(SystemInfo var1) {
      HardwareAbstractionLayer var2 = var1.getHardware();
      this.ignoreErrors("processor", () -> {
         this.putProcessor(var2.getProcessor());
      });
      this.ignoreErrors("graphics", () -> {
         this.putGraphics(var2.getGraphicsCards());
      });
      this.ignoreErrors("memory", () -> {
         this.putMemory(var2.getMemory());
      });
   }

   private void ignoreErrors(String var1, Runnable var2) {
      try {
         var2.run();
      } catch (Throwable var4) {
         LOGGER.warn("Failed retrieving info for group {}", var1, var4);
      }

   }

   private void putPhysicalMemory(List<PhysicalMemory> var1) {
      int var2 = 0;
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         PhysicalMemory var4 = (PhysicalMemory)var3.next();
         String var5 = String.format(Locale.ROOT, "Memory slot #%d ", var2++);
         this.setDetail(var5 + "capacity (MB)", () -> {
            return String.format(Locale.ROOT, "%.2f", (float)var4.getCapacity() / 1048576.0F);
         });
         this.setDetail(var5 + "clockSpeed (GHz)", () -> {
            return String.format(Locale.ROOT, "%.2f", (float)var4.getClockSpeed() / 1.0E9F);
         });
         String var10001 = var5 + "type";
         Objects.requireNonNull(var4);
         this.setDetail(var10001, var4::getMemoryType);
      }

   }

   private void putVirtualMemory(VirtualMemory var1) {
      this.setDetail("Virtual memory max (MB)", () -> {
         return String.format(Locale.ROOT, "%.2f", (float)var1.getVirtualMax() / 1048576.0F);
      });
      this.setDetail("Virtual memory used (MB)", () -> {
         return String.format(Locale.ROOT, "%.2f", (float)var1.getVirtualInUse() / 1048576.0F);
      });
      this.setDetail("Swap memory total (MB)", () -> {
         return String.format(Locale.ROOT, "%.2f", (float)var1.getSwapTotal() / 1048576.0F);
      });
      this.setDetail("Swap memory used (MB)", () -> {
         return String.format(Locale.ROOT, "%.2f", (float)var1.getSwapUsed() / 1048576.0F);
      });
   }

   private void putMemory(GlobalMemory var1) {
      this.ignoreErrors("physical memory", () -> {
         this.putPhysicalMemory(var1.getPhysicalMemory());
      });
      this.ignoreErrors("virtual memory", () -> {
         this.putVirtualMemory(var1.getVirtualMemory());
      });
   }

   private void putGraphics(List<GraphicsCard> var1) {
      int var2 = 0;
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         GraphicsCard var4 = (GraphicsCard)var3.next();
         String var5 = String.format(Locale.ROOT, "Graphics card #%d ", var2++);
         String var10001 = var5 + "name";
         Objects.requireNonNull(var4);
         this.setDetail(var10001, var4::getName);
         var10001 = var5 + "vendor";
         Objects.requireNonNull(var4);
         this.setDetail(var10001, var4::getVendor);
         this.setDetail(var5 + "VRAM (MB)", () -> {
            return String.format(Locale.ROOT, "%.2f", (float)var4.getVRam() / 1048576.0F);
         });
         var10001 = var5 + "deviceId";
         Objects.requireNonNull(var4);
         this.setDetail(var10001, var4::getDeviceId);
         var10001 = var5 + "versionInfo";
         Objects.requireNonNull(var4);
         this.setDetail(var10001, var4::getVersionInfo);
      }

   }

   private void putProcessor(CentralProcessor var1) {
      CentralProcessor.ProcessorIdentifier var2 = var1.getProcessorIdentifier();
      Objects.requireNonNull(var2);
      this.setDetail("Processor Vendor", var2::getVendor);
      Objects.requireNonNull(var2);
      this.setDetail("Processor Name", var2::getName);
      Objects.requireNonNull(var2);
      this.setDetail("Identifier", var2::getIdentifier);
      Objects.requireNonNull(var2);
      this.setDetail("Microarchitecture", var2::getMicroarchitecture);
      this.setDetail("Frequency (GHz)", () -> {
         return String.format(Locale.ROOT, "%.2f", (float)var2.getVendorFreq() / 1.0E9F);
      });
      this.setDetail("Number of physical packages", () -> {
         return String.valueOf(var1.getPhysicalPackageCount());
      });
      this.setDetail("Number of physical CPUs", () -> {
         return String.valueOf(var1.getPhysicalProcessorCount());
      });
      this.setDetail("Number of logical CPUs", () -> {
         return String.valueOf(var1.getLogicalProcessorCount());
      });
   }

   public void appendToCrashReportString(StringBuilder var1) {
      var1.append("-- ").append("System Details").append(" --\n");
      var1.append("Details:");
      this.entries.forEach((var1x, var2) -> {
         var1.append("\n\t");
         var1.append(var1x);
         var1.append(": ");
         var1.append(var2);
      });
   }

   public String toLineSeparatedString() {
      return (String)this.entries.entrySet().stream().map((var0) -> {
         String var10000 = (String)var0.getKey();
         return var10000 + ": " + (String)var0.getValue();
      }).collect(Collectors.joining(System.lineSeparator()));
   }

   static {
      String var10000 = System.getProperty("os.name");
      OPERATING_SYSTEM = var10000 + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version");
      var10000 = System.getProperty("java.version");
      JAVA_VERSION = var10000 + ", " + System.getProperty("java.vendor");
      var10000 = System.getProperty("java.vm.name");
      JAVA_VM_VERSION = var10000 + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor");
   }
}
