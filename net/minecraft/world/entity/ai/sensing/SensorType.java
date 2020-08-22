package net.minecraft.world.entity.ai.sensing;

import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class SensorType {
   public static final SensorType DUMMY = register("dummy", DummySensor::new);
   public static final SensorType NEAREST_LIVING_ENTITIES = register("nearest_living_entities", NearestLivingEntitySensor::new);
   public static final SensorType NEAREST_PLAYERS = register("nearest_players", PlayerSensor::new);
   public static final SensorType INTERACTABLE_DOORS = register("interactable_doors", InteractableDoorsSensor::new);
   public static final SensorType NEAREST_BED = register("nearest_bed", NearestBedSensor::new);
   public static final SensorType HURT_BY = register("hurt_by", HurtBySensor::new);
   public static final SensorType VILLAGER_HOSTILES = register("villager_hostiles", VillagerHostilesSensor::new);
   public static final SensorType VILLAGER_BABIES = register("villager_babies", VillagerBabiesSensor::new);
   public static final SensorType SECONDARY_POIS = register("secondary_pois", SecondaryPoiSensor::new);
   public static final SensorType GOLEM_LAST_SEEN = register("golem_last_seen", GolemSensor::new);
   private final Supplier factory;

   private SensorType(Supplier var1) {
      this.factory = var1;
   }

   public Sensor create() {
      return (Sensor)this.factory.get();
   }

   private static SensorType register(String var0, Supplier var1) {
      return (SensorType)Registry.register(Registry.SENSOR_TYPE, (ResourceLocation)(new ResourceLocation(var0)), new SensorType(var1));
   }
}
