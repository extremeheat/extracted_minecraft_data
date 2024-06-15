package net.minecraft.util.monitoring.jmx;

import com.mojang.logging.LogUtils;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.DynamicMBean;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;

public final class MinecraftServerStatistics implements DynamicMBean {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final MinecraftServer server;
   private final MBeanInfo mBeanInfo;
   private final Map<String, MinecraftServerStatistics.AttributeDescription> attributeDescriptionByName = Stream.of(
         new MinecraftServerStatistics.AttributeDescription("tickTimes", this::getTickTimes, "Historical tick times (ms)", long[].class),
         new MinecraftServerStatistics.AttributeDescription("averageTickTime", this::getAverageTickTime, "Current average tick time (ms)", long.class)
      )
      .collect(Collectors.toMap(var0 -> var0.name, Function.identity()));

   private MinecraftServerStatistics(MinecraftServer var1) {
      super();
      this.server = var1;
      MBeanAttributeInfo[] var2 = this.attributeDescriptionByName
         .values()
         .stream()
         .map(MinecraftServerStatistics.AttributeDescription::asMBeanAttributeInfo)
         .toArray(MBeanAttributeInfo[]::new);
      this.mBeanInfo = new MBeanInfo(
         MinecraftServerStatistics.class.getSimpleName(), "metrics for dedicated server", var2, null, null, new MBeanNotificationInfo[0]
      );
   }

   public static void registerJmxMonitoring(MinecraftServer var0) {
      try {
         ManagementFactory.getPlatformMBeanServer().registerMBean(new MinecraftServerStatistics(var0), new ObjectName("net.minecraft.server:type=Server"));
      } catch (InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException | MalformedObjectNameException var2) {
         LOGGER.warn("Failed to initialise server as JMX bean", var2);
      }
   }

   private float getAverageTickTime() {
      return this.server.getCurrentSmoothedTickTime();
   }

   private long[] getTickTimes() {
      return this.server.getTickTimesNanos();
   }

   @Nullable
   @Override
   public Object getAttribute(String var1) {
      MinecraftServerStatistics.AttributeDescription var2 = this.attributeDescriptionByName.get(var1);
      return var2 == null ? null : var2.getter.get();
   }

   @Override
   public void setAttribute(Attribute var1) {
   }

   @Override
   public AttributeList getAttributes(String[] var1) {
      List var2 = Arrays.stream(var1)
         .map(this.attributeDescriptionByName::get)
         .filter(Objects::nonNull)
         .map(var0 -> new Attribute(var0.name, var0.getter.get()))
         .collect(Collectors.toList());
      return new AttributeList(var2);
   }

   @Override
   public AttributeList setAttributes(AttributeList var1) {
      return new AttributeList();
   }

   @Nullable
   @Override
   public Object invoke(String var1, Object[] var2, String[] var3) {
      return null;
   }

   @Override
   public MBeanInfo getMBeanInfo() {
      return this.mBeanInfo;
   }

   static final class AttributeDescription {
      final String name;
      final Supplier<Object> getter;
      private final String description;
      private final Class<?> type;

      AttributeDescription(String var1, Supplier<Object> var2, String var3, Class<?> var4) {
         super();
         this.name = var1;
         this.getter = var2;
         this.description = var3;
         this.type = var4;
      }

      private MBeanAttributeInfo asMBeanAttributeInfo() {
         return new MBeanAttributeInfo(this.name, this.type.getSimpleName(), this.description, true, false, false);
      }
   }
}
