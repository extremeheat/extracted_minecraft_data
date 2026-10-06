package net.minecraft.world.level.pathfinder;

import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public enum PathType {
   BLOCKED(0, -1.0F),
   OPEN(1, 0.0F, BlockTags.PATHFINDING_OPEN),
   WALKABLE(2, 0.0F),
   WALKABLE_DOOR(3, 0.0F),
   DROP_DOWN(4, 0.0F, BlockTags.PATHFINDING_DROP_DOWN),
   POWDER_SNOW(5, -1.0F, BlockTags.PATHFINDING_POWDER_SNOW),
   ON_TOP_OF_POWDER_SNOW(6, 0.0F),
   FENCE(7, -1.0F),
   LAVA(8, -1.0F),
   WATER(9, 8.0F),
   WATER_BORDER(10, 8.0F),
   RAIL(11, 0.0F, BlockTags.PATHFINDING_RAILS),
   UNPASSABLE_RAIL(12, -1.0F),
   FIRE_IN_NEIGHBOR(13, 8.0F),
   FIRE(14, 16.0F),
   DAMAGING_IN_NEIGHBOR(15, 8.0F),
   DAMAGING(16, -1.0F, BlockTags.PATHFINDING_DAMAGING),
   DOOR_OPEN(17, 0.0F),
   DOOR_WOOD_CLOSED(18, -1.0F),
   DOOR_IRON_CLOSED(19, -1.0F),
   BREACH(20, 4.0F),
   LEAVES(21, -1.0F, BlockTags.PATHFINDING_LEAVES),
   STICKY(22, 8.0F, BlockTags.PATHFINDING_STICKY),
   AVOID_IN_AIR(23, 0.0F, BlockTags.PATHFINDING_AVOID_IN_AIR),
   DAMAGE_CAUTIOUS(24, 0.0F, BlockTags.PATHFINDING_DAMAGE_CAUTIOUS),
   ON_TOP_OF_DROP_DOWN(25, 0.0F),
   BIG_MOBS_CLOSE_TO_DANGER(26, 4.0F);

   public static final StreamCodec<ByteBuf, PathType> STREAM_CODEC = ByteBufCodecs.enumCodec(PathType.class, (t) -> t.id);
   private final int id;
   private final float malus;
   private final Optional<TagKey<Block>> blockTag;

   private PathType(final int id, final float defaultCost) {
      this(id, defaultCost, Optional.empty());
   }

   private PathType(final int id, final float defaultCost, final TagKey<Block> blockTag) {
      this(id, defaultCost, Optional.of(blockTag));
   }

   private PathType(final int id, final float defaultCost, final Optional<TagKey<Block>> blockTag) {
      this.id = id;
      this.malus = defaultCost;
      this.blockTag = blockTag;
   }

   public float getMalus() {
      return this.malus;
   }

   public boolean isForState(final BlockState blockState) {
      return this.blockTag.isPresent() && blockState.is((TagKey)this.blockTag.get());
   }

   // $FF: synthetic method
   private static PathType[] $values() {
      return new PathType[]{BLOCKED, OPEN, WALKABLE, WALKABLE_DOOR, DROP_DOWN, POWDER_SNOW, ON_TOP_OF_POWDER_SNOW, FENCE, LAVA, WATER, WATER_BORDER, RAIL, UNPASSABLE_RAIL, FIRE_IN_NEIGHBOR, FIRE, DAMAGING_IN_NEIGHBOR, DAMAGING, DOOR_OPEN, DOOR_WOOD_CLOSED, DOOR_IRON_CLOSED, BREACH, LEAVES, STICKY, AVOID_IN_AIR, DAMAGE_CAUTIOUS, ON_TOP_OF_DROP_DOWN, BIG_MOBS_CLOSE_TO_DANGER};
   }
}
