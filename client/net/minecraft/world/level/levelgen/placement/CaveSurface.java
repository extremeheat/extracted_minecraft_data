package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;

public enum CaveSurface implements StringRepresentable {
   CEILING(Direction.UP, 1, "ceiling"),
   FLOOR(Direction.DOWN, -1, "floor");

   public static final Codec<CaveSurface> CODEC = StringRepresentable.fromEnum(CaveSurface::values);
   private final Direction direction;
   private final int y;
   private final String id;

   private CaveSurface(final Direction nullxx, final int nullxxx, final String nullxxxx) {
      this.direction = nullxx;
      this.y = nullxxx;
      this.id = nullxxxx;
   }

   public Direction getDirection() {
      return this.direction;
   }

   public int getY() {
      return this.y;
   }

   @Override
   public String getSerializedName() {
      return this.id;
   }
}
