package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.lang.management.ManagementFactory;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.jspecify.annotations.Nullable;
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
      this.setDetail("Minecraft Version", SharedConstants.getCurrentVersion().name());
      this.setDetail("Minecraft Version ID", SharedConstants.getCurrentVersion().id());
      this.setDetail("Operating System", OPERATING_SYSTEM);
      this.setDetail("Java Version", JAVA_VERSION);
      this.setDetail("Java VM Version", JAVA_VM_VERSION);
      this.setDetail("Memory", (Supplier)(() -> {
         Runtime runtime = Runtime.getRuntime();
         long max = runtime.maxMemory();
         long total = runtime.totalMemory();
         long free = runtime.freeMemory();
         long maxMb = max / 1048576L;
         long totalMb = total / 1048576L;
         long freeMb = free / 1048576L;
         return free + " bytes (" + freeMb + " MiB) / " + total + " bytes (" + totalMb + " MiB) up to " + max + " bytes (" + maxMb + " MiB)";
      }));
      this.setDetail("CPUs", (Supplier)(() -> String.valueOf(Runtime.getRuntime().availableProcessors())));
      this.ignoreErrors("hardware", () -> this.putHardware(new SystemInfo()));
      this.setDetail("JVM Flags", (Supplier)(() -> printJvmFlags((arg) -> arg.startsWith("-X"))));
      this.setDetail("Debug Flags", (Supplier)(() -> printJvmFlags((arg) -> arg.startsWith("-DMC_DEBUG_"))));
   }

   private static String printJvmFlags(final Predicate<String> selector) {
      List<String> allArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
      List<String> selectedArguments = allArguments.stream().filter(selector).toList();
      return String.format(Locale.ROOT, "%d total; %s", selectedArguments.size(), String.join(" ", selectedArguments));
   }

   public void setDetail(final String key, final String value) {
      this.entries.put(key, value);
   }

   public void setDetail(final String key, final Supplier<String> valueSupplier) {
      try {
         this.setDetail(key, (String)valueSupplier.get());
      } catch (Exception e) {
         LOGGER.warn("Failed to get system info for {}", key, e);
         this.setDetail(key, "ERR");
      }

   }

   private void putHardware(final SystemInfo systemInfo) {
      HardwareAbstractionLayer hardware = systemInfo.getHardware();
      this.ignoreErrors("processor", () -> this.putProcessor(hardware.getProcessor()));
      this.ignoreErrors("graphics", () -> this.putGraphics(hardware.getGraphicsCards()));
      this.ignoreErrors("memory", () -> this.putMemory(hardware.getMemory()));
      this.ignoreErrors("storage", this::putStorage);
   }

   private void ignoreErrors(final String group, final Runnable action) {
      try {
         action.run();
      } catch (Throwable t) {
         LOGGER.warn("Failed retrieving info for group {}", group, t);
      }

   }

   public static float sizeInMiB(final long bytes) {
      return (float)bytes / 1048576.0F;
   }

   private void putPhysicalMemory(final List<PhysicalMemory> memoryPackages) {
      int memorySlot = 0;

      for(PhysicalMemory physicalMemory : memoryPackages) {
         String prefix = String.format(Locale.ROOT, "Memory slot #%d ", memorySlot++);
         this.setDetail(prefix + "capacity (MiB)", (Supplier)(() -> String.format(Locale.ROOT, "%.2f", sizeInMiB(physicalMemory.getCapacity()))));
         this.setDetail(prefix + "clockSpeed (GHz)", (Supplier)(() -> String.format(Locale.ROOT, "%.2f", (float)physicalMemory.getClockSpeed() / 1.0E9F)));
         String var10001 = prefix + "type";
         Objects.requireNonNull(physicalMemory);
         this.setDetail(var10001, physicalMemory::getMemoryType);
      }

   }

   private void putVirtualMemory(final VirtualMemory virtualMemory) {
      this.setDetail("Virtual memory max (MiB)", (Supplier)(() -> String.format(Locale.ROOT, "%.2f", sizeInMiB(virtualMemory.getVirtualMax()))));
      this.setDetail("Virtual memory used (MiB)", (Supplier)(() -> String.format(Locale.ROOT, "%.2f", sizeInMiB(virtualMemory.getVirtualInUse()))));
      this.setDetail("Swap memory total (MiB)", (Supplier)(() -> String.format(Locale.ROOT, "%.2f", sizeInMiB(virtualMemory.getSwapTotal()))));
      this.setDetail("Swap memory used (MiB)", (Supplier)(() -> String.format(Locale.ROOT, "%.2f", sizeInMiB(virtualMemory.getSwapUsed()))));
   }

   private void putMemory(final GlobalMemory memory) {
      this.ignoreErrors("physical memory", () -> this.putPhysicalMemory(memory.getPhysicalMemory()));
      this.ignoreErrors("virtual memory", () -> this.putVirtualMemory(memory.getVirtualMemory()));
   }

   private void putGraphics(final List<GraphicsCard> graphicsCards) {
      int gpuIndex = 0;

      for(GraphicsCard graphicsCard : graphicsCards) {
         String prefix = String.format(Locale.ROOT, "Graphics card #%d ", gpuIndex++);
         String var10001 = prefix + "name";
         Objects.requireNonNull(graphicsCard);
         this.setDetail(var10001, graphicsCard::getName);
         var10001 = prefix + "vendor";
         Objects.requireNonNull(graphicsCard);
         this.setDetail(var10001, graphicsCard::getVendor);
         this.setDetail(prefix + "VRAM (MiB)", (Supplier)(() -> String.format(Locale.ROOT, "%.2f", sizeInMiB(graphicsCard.getVRam()))));
         var10001 = prefix + "deviceId";
         Objects.requireNonNull(graphicsCard);
         this.setDetail(var10001, graphicsCard::getDeviceId);
         var10001 = prefix + "versionInfo";
         Objects.requireNonNull(graphicsCard);
         this.setDetail(var10001, graphicsCard::getVersionInfo);
      }

   }

   private void putProcessor(final CentralProcessor processor) {
      CentralProcessor.ProcessorIdentifier processorIdentifier = processor.getProcessorIdentifier();
      Objects.requireNonNull(processorIdentifier);
      this.setDetail("Processor Vendor", processorIdentifier::getVendor);
      Objects.requireNonNull(processorIdentifier);
      this.setDetail("Processor Name", processorIdentifier::getName);
      Objects.requireNonNull(processorIdentifier);
      this.setDetail("Identifier", processorIdentifier::getIdentifier);
      Objects.requireNonNull(processorIdentifier);
      this.setDetail("Microarchitecture", processorIdentifier::getMicroarchitecture);
      this.setDetail("Frequency (GHz)", (Supplier)(() -> String.format(Locale.ROOT, "%.2f", (float)processorIdentifier.getVendorFreq() / 1.0E9F)));
      this.setDetail("Number of physical packages", (Supplier)(() -> String.valueOf(processor.getPhysicalPackageCount())));
      this.setDetail("Number of physical CPUs", (Supplier)(() -> String.valueOf(processor.getPhysicalProcessorCount())));
      this.setDetail("Number of logical CPUs", (Supplier)(() -> String.valueOf(processor.getLogicalProcessorCount())));
   }

   private void putStorage() {
      this.putSpaceForProperty("jna.tmpdir");
      this.putSpaceForProperty("org.lwjgl.system.SharedLibraryExtractPath");
      this.putSpaceForProperty("io.netty.native.workdir");
      this.putSpaceForProperty("java.io.tmpdir");
      this.putSpaceForPath("workdir", () -> "");
   }

   private void putSpaceForProperty(final String env) {
      this.putSpaceForPath(env, () -> System.getProperty(env));
   }

   private void putSpaceForPath(final String id, final Supplier<@Nullable String> pathSupplier) {
      String key = "Space in storage for " + id + " (MiB)";

      try {
         String path = (String)pathSupplier.get();
         if (path == null) {
            this.setDetail(key, "<path not set>");
            return;
         }

         FileStore store = Files.getFileStore(Path.of(path));
         this.setDetail(key, String.format(Locale.ROOT, "available: %.2f, total: %.2f", sizeInMiB(store.getUsableSpace()), sizeInMiB(store.getTotalSpace())));
      } catch (InvalidPathException e) {
         LOGGER.warn("{} is not a path", id, e);
         this.setDetail(key, "<invalid path>");
      } catch (Exception e) {
         LOGGER.warn("Failed retrieving storage space for {}", id, e);
         this.setDetail(key, "ERR");
      }

   }

   public void appendToCrashReportString(final StringBuilder sb) {
      sb.append("-- ").append("System Details").append(" --\n");
      sb.append("Details:");
      this.entries.forEach((key, value) -> {
         sb.append("\n\t");
         sb.append(key);
         sb.append(": ");
         sb.append(value);
      });
   }

   public String toLineSeparatedString() {
      return (String)this.entries.entrySet().stream().map((e) -> {
         String var10000 = (String)e.getKey();
         return var10000 + ": " + (String)e.getValue();
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
