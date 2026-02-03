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
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.DynamicMBean;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import net.minecraft.server.MinecraftServer;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public final class MinecraftServerStatistics implements DynamicMBean {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final MinecraftServer server;
   private final MBeanInfo mBeanInfo;
   private final Map<String, AttributeDescription> attributeDescriptionByName;

   private MinecraftServerStatistics(final MinecraftServer server) {
      super();
      this.attributeDescriptionByName = (Map)Stream.of(new AttributeDescription("tickTimes", this::getTickTimes, "Historical tick times (ms)", long[].class), new AttributeDescription("averageTickTime", this::getAverageTickTime, "Current average tick time (ms)", Long.TYPE)).collect(Collectors.toMap((attributeDescription) -> attributeDescription.name, Function.identity()));
      this.server = server;
      MBeanAttributeInfo[] mBeanAttributeInfos = (MBeanAttributeInfo[])this.attributeDescriptionByName.values().stream().map(AttributeDescription::asMBeanAttributeInfo).toArray((x$0) -> new MBeanAttributeInfo[x$0]);
      this.mBeanInfo = new MBeanInfo(MinecraftServerStatistics.class.getSimpleName(), "metrics for dedicated server", mBeanAttributeInfos, (MBeanConstructorInfo[])null, (MBeanOperationInfo[])null, new MBeanNotificationInfo[0]);
   }

   public static void registerJmxMonitoring(final MinecraftServer server) {
      try {
         ManagementFactory.getPlatformMBeanServer().registerMBean(new MinecraftServerStatistics(server), new ObjectName("net.minecraft.server:type=Server"));
      } catch (InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException | MalformedObjectNameException e) {
         LOGGER.warn("Failed to initialise server as JMX bean", e);
      }

   }

   private float getAverageTickTime() {
      return this.server.getCurrentSmoothedTickTime();
   }

   private long[] getTickTimes() {
      return this.server.getTickTimesNanos();
   }

   public @Nullable Object getAttribute(final String attribute) {
      AttributeDescription attributeDescription = (AttributeDescription)this.attributeDescriptionByName.get(attribute);
      return attributeDescription == null ? null : attributeDescription.getter.get();
   }

   public void setAttribute(final Attribute attribute) {
   }

   public AttributeList getAttributes(final String[] attributes) {
      Stream var10000 = Arrays.stream(attributes);
      Map var10001 = this.attributeDescriptionByName;
      Objects.requireNonNull(var10001);
      List<Attribute> attributeList = (List)var10000.map(var10001::get).filter(Objects::nonNull).map((attributeDescription) -> new Attribute(attributeDescription.name, attributeDescription.getter.get())).collect(Collectors.toList());
      return new AttributeList(attributeList);
   }

   public AttributeList setAttributes(final AttributeList attributes) {
      return new AttributeList();
   }

   public @Nullable Object invoke(final String actionName, final Object[] params, final String[] signature) {
      return null;
   }

   public MBeanInfo getMBeanInfo() {
      return this.mBeanInfo;
   }

   private static final class AttributeDescription {
      private final String name;
      private final Supplier<Object> getter;
      private final String description;
      private final Class<?> type;

      private AttributeDescription(final String name, final Supplier<Object> getter, final String description, final Class<?> type) {
         super();
         this.name = name;
         this.getter = getter;
         this.description = description;
         this.type = type;
      }

      private MBeanAttributeInfo asMBeanAttributeInfo() {
         return new MBeanAttributeInfo(this.name, this.type.getSimpleName(), this.description, true, false, false);
      }
   }
}
