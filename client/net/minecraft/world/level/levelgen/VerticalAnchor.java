package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Util;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;

public record VerticalAnchor(Type type, int offset) {
   private static final Codec<Integer> OFFSET_CODEC;
   public static final Codec<VerticalAnchor> CODEC;
   private static final VerticalAnchor BOTTOM;
   private static final VerticalAnchor TOP;

   public VerticalAnchor {
      super();
   }

   public static VerticalAnchor absolute(final int value) {
      return new VerticalAnchor(VerticalAnchor.Type.ABSOLUTE, value);
   }

   public static VerticalAnchor aboveBottom(final int offset) {
      return new VerticalAnchor(VerticalAnchor.Type.ABOVE_BOTTOM, offset);
   }

   public static VerticalAnchor belowTop(final int offset) {
      return new VerticalAnchor(VerticalAnchor.Type.BELOW_TOP, offset);
   }

   public static VerticalAnchor bottom() {
      return BOTTOM;
   }

   public static VerticalAnchor top() {
      return TOP;
   }

   public static VerticalAnchor relativeToSeaLevel(final int offset) {
      return new VerticalAnchor(VerticalAnchor.Type.RELATIVE_TO_SEA_LEVEL, offset);
   }

   public static VerticalAnchor seaLevel() {
      return relativeToSeaLevel(0);
   }

   public int resolveY(final Context context) {
      int var10000;
      switch (this.type.ordinal()) {
         case 0 -> var10000 = this.offset;
         case 1 -> var10000 = context.minY() + this.offset;
         case 2 -> var10000 = context.maxY() - this.offset;
         case 3 -> var10000 = context.seaLevel() + this.offset;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public String toString() {
      int var10000 = this.offset;
      return var10000 + " " + this.type.getSerializedName();
   }

   static {
      OFFSET_CODEC = Codec.intRange(DimensionType.MIN_Y, DimensionType.MAX_Y);
      CODEC = ExtraCodecs.singleKeyDispatch(VerticalAnchor.Type.CODEC, VerticalAnchor::type, Util.memoize((Function)((type) -> OFFSET_CODEC.xmap((offset) -> new VerticalAnchor(type, offset), VerticalAnchor::offset))));
      BOTTOM = aboveBottom(0);
      TOP = belowTop(0);
   }

   public static enum Type implements StringRepresentable {
      ABSOLUTE("absolute"),
      ABOVE_BOTTOM("above_bottom"),
      BELOW_TOP("below_top"),
      RELATIVE_TO_SEA_LEVEL("relative_to_sea_level");

      public static final Codec<Type> CODEC = StringRepresentable.<Type>fromEnum(Type::values);
      private final String name;

      private Type(final String name) {
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{ABSOLUTE, ABOVE_BOTTOM, BELOW_TOP, RELATIVE_TO_SEA_LEVEL};
      }
   }

   public static record Context(int minY, int height, int seaLevel) {
      public Context {
         super();
      }

      public static Context from(final LevelAccessor level) {
         if (level instanceof WorldGenLevel worldGenLevel) {
            return from(worldGenLevel.getLevel().getChunkSource().getGenerator(), level);
         } else {
            return new Context(level.getMinY(), level.getHeight(), level.getSeaLevel());
         }
      }

      public static Context from(final ChunkGenerator generator, final LevelHeightAccessor heightAccessor) {
         return new Context(Math.max(heightAccessor.getMinY(), generator.getMinY()), Math.min(heightAccessor.getHeight(), generator.getGenDepth()), generator.getSeaLevel());
      }

      public int maxY() {
         return this.minY + this.height - 1;
      }
   }
}
