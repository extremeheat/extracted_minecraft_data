package net.minecraft.world.level.mines;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public enum MineSpawnStrategy implements StringRepresentable {
   SURFACE("surface", (var0) -> {
      BlockPos var1 = var0.getSharedSpawnPos();
      var0.getBlockState(var1);
      int var2 = var0.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, var1);
      return var1.atY(var2).getBottomCenter();
   }),
   CAVE("cave", (var0) -> {
      BlockPos var1 = var0.getSharedSpawnPos();
      int var2 = var0.getMinY();
      int var3 = var0.getMaxY();
      BlockPos var4 = var1.atY((var2 + var3) / 2);
      BlockPos.MutableBlockPos var5 = var4.mutable();

      while(var5.getY() > var2 && !isValidSpawn(var0, var5)) {
         var5.move(Direction.DOWN);
      }

      if (var5.getY() > var2) {
         return var5.getBottomCenter();
      } else {
         var5.set(var4);

         while(var5.getY() < var3 && !isValidSpawn(var0, var5)) {
            var5.move(Direction.UP);
         }

         return var5.getY() < var3 ? var5.getBottomCenter() : var4.getBottomCenter();
      }
   });

   public static final Codec<MineSpawnStrategy> CODEC = StringRepresentable.<MineSpawnStrategy>fromEnum(MineSpawnStrategy::values);
   private final String id;
   private final Function<ServerLevel, Vec3> spawn;

   private MineSpawnStrategy(final String var3, final Function<ServerLevel, Vec3> var4) {
      this.id = var3;
      this.spawn = var4;
   }

   public Vec3 getSpawnPosition(ServerLevel var1) {
      return (Vec3)this.spawn.apply(var1);
   }

   private static boolean isValidSpawn(ServerLevel var0, BlockPos var1) {
      BlockPos var2 = var1.below();
      return var0.getBlockState(var1).getCollisionShape(var0, var1).isEmpty() && var0.getBlockState(var2).isFaceSturdy(var0, var2, Direction.UP);
   }

   public String getSerializedName() {
      return this.id;
   }

   // $FF: synthetic method
   private static MineSpawnStrategy[] $values() {
      return new MineSpawnStrategy[]{SURFACE, CAVE};
   }
}
