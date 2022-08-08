package net.minecraft.world.entity.ai.memory;

import com.google.common.collect.Iterables;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.Iterator;
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
      this.lineOfSightTest = (var0) -> {
         return false;
      };
   }

   public NearestVisibleLivingEntities(LivingEntity var1, List<LivingEntity> var2) {
      super();
      this.nearbyEntities = var2;
      Object2BooleanOpenHashMap var3 = new Object2BooleanOpenHashMap(var2.size());
      Predicate var4 = (var1x) -> {
         return Sensor.isEntityTargetable(var1, var1x);
      };
      this.lineOfSightTest = (var2x) -> {
         return var3.computeIfAbsent(var2x, var4);
      };
   }

   public static NearestVisibleLivingEntities empty() {
      return EMPTY;
   }

   public Optional<LivingEntity> findClosest(Predicate<LivingEntity> var1) {
      Iterator var2 = this.nearbyEntities.iterator();

      LivingEntity var3;
      do {
         if (!var2.hasNext()) {
            return Optional.empty();
         }

         var3 = (LivingEntity)var2.next();
      } while(!var1.test(var3) || !this.lineOfSightTest.test(var3));

      return Optional.of(var3);
   }

   public Iterable<LivingEntity> findAll(Predicate<LivingEntity> var1) {
      return Iterables.filter(this.nearbyEntities, (var2) -> {
         return var1.test(var2) && this.lineOfSightTest.test(var2);
      });
   }

   public Stream<LivingEntity> find(Predicate<LivingEntity> var1) {
      return this.nearbyEntities.stream().filter((var2) -> {
         return var1.test(var2) && this.lineOfSightTest.test(var2);
      });
   }

   public boolean contains(LivingEntity var1) {
      return this.nearbyEntities.contains(var1) && this.lineOfSightTest.test(var1);
   }

   public boolean contains(Predicate<LivingEntity> var1) {
      Iterator var2 = this.nearbyEntities.iterator();

      LivingEntity var3;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         var3 = (LivingEntity)var2.next();
      } while(!var1.test(var3) || !this.lineOfSightTest.test(var3));

      return true;
   }
}
