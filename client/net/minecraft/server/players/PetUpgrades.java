package net.minecraft.server.players;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class PetUpgrades {
   private static final Map<EntityType<?>, List<Consumer<TamableAnimal>>> PETS = new HashMap();

   public PetUpgrades() {
      super();
   }

   public static void addPet(EntityType<?> var0) {
      PETS.put(var0, List.of());
   }

   public static void addUpgrade(EntityType<?> var0, Consumer<TamableAnimal> var1) {
      ArrayList var2 = new ArrayList((Collection)PETS.get(var0));
      var2.add(var1);
      PETS.put(var0, var2);
   }

   public static void getPet(EntityType<?> var0, ServerPlayer var1) {
      ServerLevel var2 = var1.serverLevel();
      BlockPos var3 = var1.blockPosition();
      BlockPos.MutableBlockPos var4 = var3.mutable().move(Direction.WEST);

      while(!var2.getBlockState(var4).getCollisionShape(var2, var4).isEmpty()) {
         var4.move(Direction.UP);
      }

      TamableAnimal var5 = (TamableAnimal)var0.spawn(var1.serverLevel(), var4.immutable(), EntitySpawnReason.EVENT);
      if (var5 != null) {
         var5.setTame(true, false);
         var5.setInvulnerable(true);
         var5.setOwner(var1);
         List var6 = (List)PETS.get(var0);
         var6.forEach((var1x) -> var1x.accept(var5));
      }

   }

   public static void upgradePet(EntityType<?> var0, Class<? extends TamableAnimal> var1, ServerPlayer var2) {
      List var3 = (List)PETS.get(var0);
      ServerLevel var4 = var2.serverLevel();
      BlockPos var5 = var2.blockPosition();
      BlockPos.MutableBlockPos var6 = var5.mutable();

      while(!var4.getBlockState(var6).getCollisionShape(var4, var6).isEmpty()) {
         var6.move(Direction.UP);
      }

      var3.forEach((var2x) -> {
         TamableAnimal var3 = (TamableAnimal)var2.serverLevel().getNearestEntity(var1, TargetingConditions.forNonCombat(), var2, var2.position().x, var2.position().y, var2.position().z, var2.getBoundingBox().inflate(10.0, 10.0, 10.0));
         if (var3 != null) {
            var2x.accept(var3);
         }

      });
   }
}
