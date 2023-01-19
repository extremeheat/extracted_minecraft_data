package net.minecraft.world.entity.ai.memory;

import com.google.common.collect.Iterables;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.sensing.Sensor;

public class NearestVisibleLivingEntities {
   private static final NearestVisibleLivingEntities EMPTY = new NearestVisibleLivingEntities();
   private final List<LivingEntity> nearbyEntities;
   private final Predicate<LivingEntity> lineOfSightTest;

   private NearestVisibleLivingEntities() {
      super();
      this.nearbyEntities = List.of();
      this.lineOfSightTest = var0 -> false;
   }

   public NearestVisibleLivingEntities(LivingEntity var1, List<LivingEntity> var2) {
      super();
      this.nearbyEntities = var2;
      Object2BooleanOpenHashMap var3 = new Object2BooleanOpenHashMap(var2.size());
      Predicate var4 = var1x -> Sensor.isEntityTargetable(var1, var1x);
      this.lineOfSightTest = var2x -> var3.computeIfAbsent(var2x, var4);
   }

   public static NearestVisibleLivingEntities empty() {
      return EMPTY;
   }

   public Optional<LivingEntity> findClosest(Predicate<LivingEntity> var1) {
      for(LivingEntity var3 : this.nearbyEntities) {
         if (var1.test(var3) && this.lineOfSightTest.test(var3)) {
            return Optional.of(var3);
         }
      }

      return Optional.empty();
   }

   public Iterable<LivingEntity> findAll(Predicate<LivingEntity> var1) {
      return Iterables.filter(this.nearbyEntities, var2 -> var1.test(var2) && this.lineOfSightTest.test(var2));
   }

   public Stream<LivingEntity> find(Predicate<LivingEntity> var1) {
      return this.nearbyEntities.stream().filter(var2 -> var1.test(var2) && this.lineOfSightTest.test(var2));
   }

   public boolean contains(LivingEntity var1) {
      return this.nearbyEntities.contains(var1) && this.lineOfSightTest.test(var1);
   }

   public boolean contains(Predicate<LivingEntity> var1) {
      for(LivingEntity var3 : this.nearbyEntities) {
         if (var1.test(var3) && this.lineOfSightTest.test(var3)) {
            return true;
         }
      }

      return false;
   }
}
