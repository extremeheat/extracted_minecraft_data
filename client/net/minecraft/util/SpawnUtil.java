package net.minecraft.util;

import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.StainedGlassBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.block.state.BlockState;

public class SpawnUtil {
   public SpawnUtil() {
      super();
   }

   public static <T extends Mob> Optional<T> trySpawnMob(EntityType<T> var0, MobSpawnType var1, ServerLevel var2, BlockPos var3, int var4, int var5, int var6, Strategy var7) {
      BlockPos.MutableBlockPos var8 = var3.mutable();

      for(int var9 = 0; var9 < var4; ++var9) {
         int var10 = Mth.randomBetweenInclusive(var2.random, -var5, var5);
         int var11 = Mth.randomBetweenInclusive(var2.random, -var5, var5);
         var8.setWithOffset(var3, var10, var6, var11);
         if (var2.getWorldBorder().isWithinBounds((BlockPos)var8) && moveToPossibleSpawnPosition(var2, var6, var8, var7)) {
            Mob var12 = (Mob)var0.create(var2, (Consumer)null, var8, var1, false, false);
            if (var12 != null) {
               if (var12.checkSpawnRules(var2, var1) && var12.checkSpawnObstruction(var2)) {
                  var2.addFreshEntityWithPassengers(var12);
                  return Optional.of(var12);
               }

               var12.discard();
            }
         }
      }

      return Optional.empty();
   }

   private static boolean moveToPossibleSpawnPosition(ServerLevel var0, int var1, BlockPos.MutableBlockPos var2, Strategy var3) {
      BlockPos.MutableBlockPos var4 = (new BlockPos.MutableBlockPos()).set(var2);
      BlockState var5 = var0.getBlockState(var4);

      for(int var6 = var1; var6 >= -var1; --var6) {
         var2.move(Direction.DOWN);
         var4.setWithOffset(var2, (Direction)Direction.UP);
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
      /** @deprecated */
      @Deprecated
      Strategy LEGACY_IRON_GOLEM = (var0, var1, var2, var3, var4) -> {
         if (!var2.is(Blocks.COBWEB) && !var2.is(Blocks.CACTUS) && !var2.is(Blocks.GLASS_PANE) && !(var2.getBlock() instanceof StainedGlassPaneBlock) && !(var2.getBlock() instanceof StainedGlassBlock) && !(var2.getBlock() instanceof LeavesBlock) && !var2.is(Blocks.CONDUIT) && !var2.is(Blocks.ICE) && !var2.is(Blocks.TNT) && !var2.is(Blocks.GLOWSTONE) && !var2.is(Blocks.BEACON) && !var2.is(Blocks.SEA_LANTERN) && !var2.is(Blocks.FROSTED_ICE) && !var2.is(Blocks.TINTED_GLASS) && !var2.is(Blocks.GLASS)) {
            return (var4.isAir() || var4.liquid()) && (var2.isSolid() || var2.is(Blocks.POWDER_SNOW));
         } else {
            return false;
         }
      };
      Strategy ON_TOP_OF_COLLIDER = (var0, var1, var2, var3, var4) -> {
         return var4.getCollisionShape(var0, var3).isEmpty() && Block.isFaceFull(var2.getCollisionShape(var0, var1), Direction.UP);
      };

      boolean canSpawnOn(ServerLevel var1, BlockPos var2, BlockState var3, BlockPos var4, BlockState var5);
   }
}
