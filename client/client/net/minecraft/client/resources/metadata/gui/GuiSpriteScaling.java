package net.minecraft.client.resources.metadata.gui;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.OptionalInt;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;

public interface GuiSpriteScaling {
   Codec<GuiSpriteScaling> CODEC = GuiSpriteScaling.Type.CODEC.dispatch(GuiSpriteScaling::type, GuiSpriteScaling.Type::codec);
   GuiSpriteScaling DEFAULT = new GuiSpriteScaling.Stretch();

   GuiSpriteScaling.Type type();

   public static record NineSlice(int width, int height, GuiSpriteScaling.NineSlice.Border border) implements GuiSpriteScaling {
      public static final MapCodec<GuiSpriteScaling.NineSlice> CODEC = RecordCodecBuilder.mapCodec(
            var0 -> var0.group(
                     ExtraCodecs.POSITIVE_INT.fieldOf("width").forGetter(GuiSpriteScaling.NineSlice::width),
                     ExtraCodecs.POSITIVE_INT.fieldOf("height").forGetter(GuiSpriteScaling.NineSlice::height),
                     GuiSpriteScaling.NineSlice.Border.CODEC.fieldOf("border").forGetter(GuiSpriteScaling.NineSlice::border)
                  )
                  .apply(var0, GuiSpriteScaling.NineSlice::new)
         )
         .validate(GuiSpriteScaling.NineSlice::validate);

      public NineSlice(int width, int height, GuiSpriteScaling.NineSlice.Border border) {
         super();
         this.width = width;
         this.height = height;
         this.border = border;
      }

      private static DataResult<GuiSpriteScaling.NineSlice> validate(GuiSpriteScaling.NineSlice var0) {
         GuiSpriteScaling.NineSlice.Border var1 = var0.border();
         if (var1.left() + var1.right() >= var0.width()) {
            return DataResult.error(() -> "Nine-sliced texture has no horizontal center slice: " + var1.left() + " + " + var1.right() + " >= " + var0.width());
         } else {
            return var1.top() + var1.bottom() >= var0.height()
               ? DataResult.error(() -> "Nine-sliced texture has no vertical center slice: " + var1.top() + " + " + var1.bottom() + " >= " + var0.height())
               : DataResult.success(var0);
         }
      }

      @Override
      public GuiSpriteScaling.Type type() {
         return GuiSpriteScaling.Type.NINE_SLICE;
      }

      public static record Border(int left, int top, int right, int bottom) {
         private static final Codec<GuiSpriteScaling.NineSlice.Border> VALUE_CODEC = ExtraCodecs.POSITIVE_INT
            .flatComapMap(var0 -> new GuiSpriteScaling.NineSlice.Border(var0, var0, var0, var0), var0 -> {
               OptionalInt var1 = var0.unpackValue();
               return var1.isPresent() ? DataResult.success(var1.getAsInt()) : DataResult.error(() -> "Border has different side sizes");
            });
         private static final Codec<GuiSpriteScaling.NineSlice.Border> RECORD_CODEC = RecordCodecBuilder.create(
            var0 -> var0.group(
                     ExtraCodecs.NON_NEGATIVE_INT.fieldOf("left").forGetter(GuiSpriteScaling.NineSlice.Border::left),
                     ExtraCodecs.NON_NEGATIVE_INT.fieldOf("top").forGetter(GuiSpriteScaling.NineSlice.Border::top),
                     ExtraCodecs.NON_NEGATIVE_INT.fieldOf("right").forGetter(GuiSpriteScaling.NineSlice.Border::right),
                     ExtraCodecs.NON_NEGATIVE_INT.fieldOf("bottom").forGetter(GuiSpriteScaling.NineSlice.Border::bottom)
                  )
                  .apply(var0, GuiSpriteScaling.NineSlice.Border::new)
         );
         static final Codec<GuiSpriteScaling.NineSlice.Border> CODEC = Codec.either(VALUE_CODEC, RECORD_CODEC)
            .xmap(Either::unwrap, var0 -> var0.unpackValue().isPresent() ? Either.left(var0) : Either.right(var0));

         public Border(int left, int top, int right, int bottom) {
            super();
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
         }

         private OptionalInt unpackValue() {
            return this.left() == this.top() && this.top() == this.right() && this.right() == this.bottom() ? OptionalInt.of(this.left()) : OptionalInt.empty();
         }
      }
   }

   public static record Stretch() implements GuiSpriteScaling {
      public static final MapCodec<GuiSpriteScaling.Stretch> CODEC = MapCodec.unit(GuiSpriteScaling.Stretch::new);

      public Stretch() {
         super();
      }

      @Override
      public GuiSpriteScaling.Type type() {
         return GuiSpriteScaling.Type.STRETCH;
      }
   }

   public static record Tile(int width, int height) implements GuiSpriteScaling {
      public static final MapCodec<GuiSpriteScaling.Tile> CODEC = RecordCodecBuilder.mapCodec(
         var0 -> var0.group(
                  ExtraCodecs.POSITIVE_INT.fieldOf("width").forGetter(GuiSpriteScaling.Tile::width),
                  ExtraCodecs.POSITIVE_INT.fieldOf("height").forGetter(GuiSpriteScaling.Tile::height)
               )
               .apply(var0, GuiSpriteScaling.Tile::new)
      );

      public Tile(int width, int height) {
         super();
         this.width = width;
         this.height = height;
      }

      @Override
      public GuiSpriteScaling.Type type() {
         return GuiSpriteScaling.Type.TILE;
      }
   }

   public static enum Type implements StringRepresentable {
      STRETCH("stretch", GuiSpriteScaling.Stretch.CODEC),
      TILE("tile", GuiSpriteScaling.Tile.CODEC),
      NINE_SLICE("nine_slice", GuiSpriteScaling.NineSlice.CODEC);

      public static final Codec<GuiSpriteScaling.Type> CODEC = StringRepresentable.fromEnum(GuiSpriteScaling.Type::values);
      private final String key;
      private final MapCodec<? extends GuiSpriteScaling> codec;

      private Type(final String param3, final MapCodec<? extends GuiSpriteScaling> param4) {
         this.key = nullxx;
         this.codec = nullxxx;
      }

      @Override
      public String getSerializedName() {
         return this.key;
      }

      public MapCodec<? extends GuiSpriteScaling> codec() {
         return this.codec;
      }
   }
}
