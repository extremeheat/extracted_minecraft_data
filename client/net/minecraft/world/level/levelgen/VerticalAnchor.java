package net.minecraft.world.level.levelgen;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.dimension.DimensionType;

public abstract class VerticalAnchor {
   public static final Codec<VerticalAnchor> CODEC;
   private static final VerticalAnchor BOTTOM;
   private static final VerticalAnchor TOP;
   private final int value;

   protected VerticalAnchor(int var1) {
      super();
      this.value = var1;
   }

   public static VerticalAnchor absolute(int var0) {
      return new VerticalAnchor.Absolute(var0);
   }

   public static VerticalAnchor aboveBottom(int var0) {
      return new VerticalAnchor.AboveBottom(var0);
   }

   public static VerticalAnchor belowTop(int var0) {
      return new VerticalAnchor.BelowTop(var0);
   }

   public static VerticalAnchor bottom() {
      return BOTTOM;
   }

   public static VerticalAnchor top() {
      return TOP;
   }

   private static VerticalAnchor merge(Either<VerticalAnchor.Absolute, Either<VerticalAnchor.AboveBottom, VerticalAnchor.BelowTop>> var0) {
      return (VerticalAnchor)var0.map(Function.identity(), (var0x) -> {
         return (VerticalAnchor)var0x.map(Function.identity(), Function.identity());
      });
   }

   private static Either<VerticalAnchor.Absolute, Either<VerticalAnchor.AboveBottom, VerticalAnchor.BelowTop>> split(VerticalAnchor var0) {
      return var0 instanceof VerticalAnchor.Absolute ? Either.left((VerticalAnchor.Absolute)var0) : Either.right(var0 instanceof VerticalAnchor.AboveBottom ? Either.left((VerticalAnchor.AboveBottom)var0) : Either.right((VerticalAnchor.BelowTop)var0));
   }

   protected int value() {
      return this.value;
   }

   public abstract int resolveY(WorldGenerationContext var1);

   static {
      CODEC = ExtraCodecs.xor(VerticalAnchor.Absolute.CODEC, ExtraCodecs.xor(VerticalAnchor.AboveBottom.CODEC, VerticalAnchor.BelowTop.CODEC)).xmap(VerticalAnchor::merge, VerticalAnchor::split);
      BOTTOM = aboveBottom(0);
      TOP = belowTop(0);
   }

   static final class Absolute extends VerticalAnchor {
      public static final Codec<VerticalAnchor.Absolute> CODEC;

      protected Absolute(int var1) {
         super(var1);
      }

      public int resolveY(WorldGenerationContext var1) {
         return this.value();
      }

      public String toString() {
         return this.value() + " absolute";
      }

      static {
         CODEC = Codec.intRange(DimensionType.MIN_Y, DimensionType.MAX_Y).fieldOf("absolute").xmap(VerticalAnchor.Absolute::new, VerticalAnchor::value).codec();
      }
   }

   static final class AboveBottom extends VerticalAnchor {
      public static final Codec<VerticalAnchor.AboveBottom> CODEC;

      protected AboveBottom(int var1) {
         super(var1);
      }

      public int resolveY(WorldGenerationContext var1) {
         return var1.getMinGenY() + this.value();
      }

      public String toString() {
         return this.value() + " above bottom";
      }

      static {
         CODEC = Codec.intRange(DimensionType.MIN_Y, DimensionType.MAX_Y).fieldOf("above_bottom").xmap(VerticalAnchor.AboveBottom::new, VerticalAnchor::value).codec();
      }
   }

   static final class BelowTop extends VerticalAnchor {
      public static final Codec<VerticalAnchor.BelowTop> CODEC;

      protected BelowTop(int var1) {
         super(var1);
      }

      public int resolveY(WorldGenerationContext var1) {
         return var1.getGenDepth() - 1 + var1.getMinGenY() - this.value();
      }

      public String toString() {
         return this.value() + " below top";
      }

      static {
         CODEC = Codec.intRange(DimensionType.MIN_Y, DimensionType.MAX_Y).fieldOf("below_top").xmap(VerticalAnchor.BelowTop::new, VerticalAnchor::value).codec();
      }
   }
}
