package net.minecraft.world.level;

import com.mojang.serialization.Codec;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;

public record CardinalLighting(float down, float up, float north, float south, float west, float east) {
   public static final CardinalLighting DEFAULT = new CardinalLighting(0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F);
   public static final CardinalLighting NETHER = new CardinalLighting(0.9F, 0.9F, 0.8F, 0.8F, 0.6F, 0.6F);

   public CardinalLighting {
      super();
   }

   public float byFace(final Direction direction) {
      float var10000;
      switch (direction) {
         case DOWN -> var10000 = this.down;
         case UP -> var10000 = this.up;
         case NORTH -> var10000 = this.north;
         case SOUTH -> var10000 = this.south;
         case WEST -> var10000 = this.west;
         case EAST -> var10000 = this.east;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public static enum Type implements StringRepresentable {
      DEFAULT("default", CardinalLighting.DEFAULT),
      NETHER("nether", CardinalLighting.NETHER);

      public static final Codec<Type> CODEC = StringRepresentable.<Type>fromEnum(Type::values);
      private final String name;
      private final CardinalLighting lighting;

      private Type(final String name, final CardinalLighting lighting) {
         this.name = name;
         this.lighting = lighting;
      }

      public CardinalLighting get() {
         return this.lighting;
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{DEFAULT, NETHER};
      }
   }
}
