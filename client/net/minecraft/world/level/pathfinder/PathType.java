package net.minecraft.world.level.pathfinder;

import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;

public enum PathType {
   BLOCKED(0, -1.0F),
   OPEN(1, 0.0F),
   WALKABLE(2, 0.0F),
   WALKABLE_DOOR(3, 0.0F),
   TRAPDOOR(4, 0.0F),
   POWDER_SNOW(5, -1.0F),
   ON_TOP_OF_POWDER_SNOW(6, 0.0F),
   FENCE(7, -1.0F),
   LAVA(8, -1.0F),
   WATER(9, 8.0F),
   WATER_BORDER(10, 8.0F),
   RAIL(11, 0.0F),
   UNPASSABLE_RAIL(12, -1.0F),
   FIRE_IN_NEIGHBOR(13, 8.0F),
   FIRE(14, 16.0F),
   DAMAGING_IN_NEIGHBOR(15, 8.0F),
   DAMAGING(16, -1.0F),
   DOOR_OPEN(17, 0.0F),
   DOOR_WOOD_CLOSED(18, -1.0F),
   DOOR_IRON_CLOSED(19, -1.0F),
   BREACH(20, 4.0F),
   LEAVES(21, -1.0F),
   STICKY_HONEY(22, 8.0F),
   COCOA(23, 0.0F),
   DAMAGE_CAUTIOUS(24, 0.0F),
   ON_TOP_OF_TRAPDOOR(25, 0.0F),
   BIG_MOBS_CLOSE_TO_DANGER(26, 4.0F);

   private static final IntFunction<PathType> BY_ID = ByIdMap.<PathType>continuous((t) -> t.id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
   public static final StreamCodec<ByteBuf, PathType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, (t) -> t.id);
   private final int id;
   private final float malus;

   private PathType(final int id, final float defaultCost) {
      this.id = id;
      this.malus = defaultCost;
   }

   public float getMalus() {
      return this.malus;
   }

   // $FF: synthetic method
   private static PathType[] $values() {
      return new PathType[]{BLOCKED, OPEN, WALKABLE, WALKABLE_DOOR, TRAPDOOR, POWDER_SNOW, ON_TOP_OF_POWDER_SNOW, FENCE, LAVA, WATER, WATER_BORDER, RAIL, UNPASSABLE_RAIL, FIRE_IN_NEIGHBOR, FIRE, DAMAGING_IN_NEIGHBOR, DAMAGING, DOOR_OPEN, DOOR_WOOD_CLOSED, DOOR_IRON_CLOSED, BREACH, LEAVES, STICKY_HONEY, COCOA, DAMAGE_CAUTIOUS, ON_TOP_OF_TRAPDOOR, BIG_MOBS_CLOSE_TO_DANGER};
   }
}
