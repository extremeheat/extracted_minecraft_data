package net.minecraft.world.level.levelgen;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.world.level.dimension.DimensionType;

public interface VerticalAnchor {
   Codec<VerticalAnchor> CODEC = Codec.xor(VerticalAnchor.Absolute.CODEC, Codec.xor(VerticalAnchor.AboveBottom.CODEC, VerticalAnchor.BelowTop.CODEC))
      .xmap(VerticalAnchor::merge, VerticalAnchor::split);
   VerticalAnchor BOTTOM = aboveBottom(0);
   VerticalAnchor TOP = belowTop(0);

   static VerticalAnchor absolute(int var0) {
      return new VerticalAnchor.Absolute(var0);
   }

   static VerticalAnchor aboveBottom(int var0) {
      return new VerticalAnchor.AboveBottom(var0);
   }

   static VerticalAnchor belowTop(int var0) {
      return new VerticalAnchor.BelowTop(var0);
   }

   static VerticalAnchor bottom() {
      return BOTTOM;
   }

   static VerticalAnchor top() {
      return TOP;
   }

   private static VerticalAnchor merge(Either<VerticalAnchor.Absolute, Either<VerticalAnchor.AboveBottom, VerticalAnchor.BelowTop>> var0) {
      return (VerticalAnchor)var0.map(Function.identity(), Either::unwrap);
   }

   private static Either<VerticalAnchor.Absolute, Either<VerticalAnchor.AboveBottom, VerticalAnchor.BelowTop>> split(VerticalAnchor var0) {
      return var0 instanceof VerticalAnchor.Absolute
         ? Either.left((VerticalAnchor.Absolute)var0)
         : Either.right(
            var0 instanceof VerticalAnchor.AboveBottom ? Either.left((VerticalAnchor.AboveBottom)var0) : Either.right((VerticalAnchor.BelowTop)var0)
         );
   }

   int resolveY(WorldGenerationContext var1);

   public static record AboveBottom(int e) implements VerticalAnchor {
      private final int offset;
      public static final Codec<VerticalAnchor.AboveBottom> CODEC = Codec.intRange(DimensionType.MIN_Y, DimensionType.MAX_Y)
         .fieldOf("above_bottom")
         .xmap(VerticalAnchor.AboveBottom::new, VerticalAnchor.AboveBottom::offset)
         .codec();

      public AboveBottom(int var1) {
         super();
         this.offset = var1;
      }

      @Override
      public int resolveY(WorldGenerationContext var1) {
         return var1.getMinGenY() + this.offset;
      }

      @Override
      public String toString() {
         return this.offset + " above bottom";
      }
   }

   public static record Absolute(int e) implements VerticalAnchor {
      private final int y;
      public static final Codec<VerticalAnchor.Absolute> CODEC = Codec.intRange(DimensionType.MIN_Y, DimensionType.MAX_Y)
         .fieldOf("absolute")
         .xmap(VerticalAnchor.Absolute::new, VerticalAnchor.Absolute::y)
         .codec();

      public Absolute(int var1) {
         super();
         this.y = var1;
      }

      @Override
      public int resolveY(WorldGenerationContext var1) {
         return this.y;
      }

      @Override
      public String toString() {
         return this.y + " absolute";
      }
   }

   public static record BelowTop(int e) implements VerticalAnchor {
      private final int offset;
      public static final Codec<VerticalAnchor.BelowTop> CODEC = Codec.intRange(DimensionType.MIN_Y, DimensionType.MAX_Y)
         .fieldOf("below_top")
         .xmap(VerticalAnchor.BelowTop::new, VerticalAnchor.BelowTop::offset)
         .codec();

      public BelowTop(int var1) {
         super();
         this.offset = var1;
      }

      @Override
      public int resolveY(WorldGenerationContext var1) {
         return var1.getGenDepth() - 1 + var1.getMinGenY() - this.offset;
      }

      @Override
      public String toString() {
         return this.offset + " below top";
      }
   }
}
