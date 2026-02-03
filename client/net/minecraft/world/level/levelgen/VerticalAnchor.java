package net.minecraft.world.level.levelgen;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.world.level.dimension.DimensionType;

public interface VerticalAnchor {
   Codec<VerticalAnchor> CODEC = Codec.xor(VerticalAnchor.Absolute.CODEC, Codec.xor(VerticalAnchor.AboveBottom.CODEC, VerticalAnchor.BelowTop.CODEC)).xmap(VerticalAnchor::merge, VerticalAnchor::split);
   VerticalAnchor BOTTOM = aboveBottom(0);
   VerticalAnchor TOP = belowTop(0);

   static VerticalAnchor absolute(final int value) {
      return new Absolute(value);
   }

   static VerticalAnchor aboveBottom(final int offset) {
      return new AboveBottom(offset);
   }

   static VerticalAnchor belowTop(final int offset) {
      return new BelowTop(offset);
   }

   static VerticalAnchor bottom() {
      return BOTTOM;
   }

   static VerticalAnchor top() {
      return TOP;
   }

   private static VerticalAnchor merge(final Either<Absolute, Either<AboveBottom, BelowTop>> either) {
      return (VerticalAnchor)either.map(Function.identity(), Either::unwrap);
   }

   private static Either<Absolute, Either<AboveBottom, BelowTop>> split(final VerticalAnchor anchor) {
      return anchor instanceof Absolute ? Either.left((Absolute)anchor) : Either.right(anchor instanceof AboveBottom ? Either.left((AboveBottom)anchor) : Either.right((BelowTop)anchor));
   }

   int resolveY(final WorldGenerationContext heightAccessor);

   public static record Absolute(int y) implements VerticalAnchor {
      public static final Codec<Absolute> CODEC;

      public Absolute {
         super();
      }

      public int resolveY(final WorldGenerationContext heightAccessor) {
         return this.y;
      }

      public String toString() {
         return this.y + " absolute";
      }

      static {
         CODEC = Codec.intRange(DimensionType.MIN_Y, DimensionType.MAX_Y).fieldOf("absolute").xmap(Absolute::new, Absolute::y).codec();
      }
   }

   public static record AboveBottom(int offset) implements VerticalAnchor {
      public static final Codec<AboveBottom> CODEC;

      public AboveBottom {
         super();
      }

      public int resolveY(final WorldGenerationContext heightAccessor) {
         return heightAccessor.getMinGenY() + this.offset;
      }

      public String toString() {
         return this.offset + " above bottom";
      }

      static {
         CODEC = Codec.intRange(DimensionType.MIN_Y, DimensionType.MAX_Y).fieldOf("above_bottom").xmap(AboveBottom::new, AboveBottom::offset).codec();
      }
   }

   public static record BelowTop(int offset) implements VerticalAnchor {
      public static final Codec<BelowTop> CODEC;

      public BelowTop {
         super();
      }

      public int resolveY(final WorldGenerationContext heightAccessor) {
         return heightAccessor.getGenDepth() - 1 + heightAccessor.getMinGenY() - this.offset;
      }

      public String toString() {
         return this.offset + " below top";
      }

      static {
         CODEC = Codec.intRange(DimensionType.MIN_Y, DimensionType.MAX_Y).fieldOf("below_top").xmap(BelowTop::new, BelowTop::offset).codec();
      }
   }
}
