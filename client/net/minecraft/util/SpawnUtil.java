package net.minecraft.util;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class SpawnUtil {
   public SpawnUtil() {
      super();
   }

   public static <T extends Mob> Optional<T> trySpawnMob(
      EntityType<T> var0, MobSpawnType var1, ServerLevel var2, BlockPos var3, int var4, int var5, int var6, SpawnUtil.Strategy var7
   ) {
      BlockPos.MutableBlockPos var8 = var3.mutable();

      for(int var9 = 0; var9 < var4; ++var9) {
         int var10 = Mth.randomBetweenInclusive(var2.random, -var5, var5);
         int var11 = Mth.randomBetweenInclusive(var2.random, -var5, var5);
         var8.setWithOffset(var3, var10, var6, var11);
         if (var2.getWorldBorder().isWithinBounds(var8) && moveToPossibleSpawnPosition(var2, var6, var8, var7)) {
            Mob var12 = (Mob)var0.create(var2, null, null, null, var8, var1, false, false);
            if (var12 != null) {
               if (var12.checkSpawnRules(var2, var1) && var12.checkSpawnObstruction(var2)) {
                  var2.addFreshEntityWithPassengers(var12);
                  return Optional.of((T)var12);
               }

               var12.discard();
            }
         }
      }

      return Optional.empty();
   }

   private static boolean moveToPossibleSpawnPosition(ServerLevel var0, int var1, BlockPos.MutableBlockPos var2, SpawnUtil.Strategy var3) {
      BlockPos.MutableBlockPos var4 = new BlockPos.MutableBlockPos().set(var2);
      BlockState var5 = var0.getBlockState(var4);

      for(int var6 = var1; var6 >= -var1; --var6) {
         var2.move(Direction.DOWN);
         var4.setWithOffset(var2, Direction.UP);
         BlockState var7 = var0.getBlockState(var2);
         if (var3.canSpawnOn(var0, var2, var7, var4, var5)) {
            var2.move(Direction.UP);
            return true;
         }

         var5 = var7;
      }

      return false;
   }

   public interface Strategy {
      SpawnUtil.Strategy LEGACY_IRON_GOLEM = (var0, var1, var2, var3, var4) -> (var4.isAir() || var4.getMaterial().isLiquid())
            && var2.getMaterial().isSolidBlocking();
      SpawnUtil.Strategy ON_TOP_OF_COLLIDER = (var0, var1, var2, var3, var4) -> var4.getCollisionShape(var0, var3).isEmpty()
            && Block.isFaceFull(var2.getCollisionShape(var0, var1), Direction.UP);

      boolean canSpawnOn(ServerLevel var1, BlockPos var2, BlockState var3, BlockPos var4, BlockState var5);
   }
}
