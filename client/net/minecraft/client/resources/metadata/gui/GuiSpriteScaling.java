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
   Codec<GuiSpriteScaling> CODEC = GuiSpriteScaling.Type.CODEC.dispatch(GuiSpriteScaling::type, Type::codec);
   GuiSpriteScaling DEFAULT = new Stretch();

   Type type();

   public static record Stretch() implements GuiSpriteScaling {
      public static final MapCodec<Stretch> CODEC = MapCodec.unit(Stretch::new);

      public Stretch() {
         super();
      }

      public Type type() {
         return GuiSpriteScaling.Type.STRETCH;
      }
   }

   public static record Tile(int width, int height) implements GuiSpriteScaling {
      public static final MapCodec<Tile> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(ExtraCodecs.POSITIVE_INT.fieldOf("width").forGetter(Tile::width), ExtraCodecs.POSITIVE_INT.fieldOf("height").forGetter(Tile::height)).apply(i, Tile::new));

      public Tile {
         super();
      }

      public Type type() {
         return GuiSpriteScaling.Type.TILE;
      }
   }

   public static record NineSlice(int width, int height, Border border, boolean stretchInner) implements GuiSpriteScaling {
      public static final MapCodec<NineSlice> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(ExtraCodecs.POSITIVE_INT.fieldOf("width").forGetter(NineSlice::width), ExtraCodecs.POSITIVE_INT.fieldOf("height").forGetter(NineSlice::height), GuiSpriteScaling.NineSlice.Border.CODEC.fieldOf("border").forGetter(NineSlice::border), Codec.BOOL.optionalFieldOf("stretch_inner", false).forGetter(NineSlice::stretchInner)).apply(i, NineSlice::new)).validate(NineSlice::validate);

      public NineSlice {
         super();
      }

      private static DataResult<NineSlice> validate(final NineSlice nineSlice) {
         Border border = nineSlice.border();
         if (border.left() + border.right() >= nineSlice.width()) {
            return DataResult.error(() -> {
               int var10000 = border.left();
               return "Nine-sliced texture has no horizontal center slice: " + var10000 + " + " + border.right() + " >= " + nineSlice.width();
            });
         } else {
            return border.top() + border.bottom() >= nineSlice.height() ? DataResult.error(() -> {
               int var10000 = border.top();
               return "Nine-sliced texture has no vertical center slice: " + var10000 + " + " + border.bottom() + " >= " + nineSlice.height();
            }) : DataResult.success(nineSlice);
         }
      }

      public Type type() {
         return GuiSpriteScaling.Type.NINE_SLICE;
      }

      public static record Border(int left, int top, int right, int bottom) {
         private static final Codec<Border> VALUE_CODEC;
         private static final Codec<Border> RECORD_CODEC;
         private static final Codec<Border> CODEC;

         public Border {
            super();
         }

         private OptionalInt unpackValue() {
            return this.left() == this.top() && this.top() == this.right() && this.right() == this.bottom() ? OptionalInt.of(this.left()) : OptionalInt.empty();
         }

         static {
            VALUE_CODEC = ExtraCodecs.POSITIVE_INT.flatComapMap((size) -> new Border(size, size, size, size), (border) -> {
               OptionalInt size = border.unpackValue();
               return size.isPresent() ? DataResult.success(size.getAsInt()) : DataResult.error(() -> "Border has different side sizes");
            });
            RECORD_CODEC = RecordCodecBuilder.create((i) -> i.group(ExtraCodecs.NON_NEGATIVE_INT.fieldOf("left").forGetter(Border::left), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("top").forGetter(Border::top), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("right").forGetter(Border::right), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("bottom").forGetter(Border::bottom)).apply(i, Border::new));
            CODEC = Codec.either(VALUE_CODEC, RECORD_CODEC).xmap(Either::unwrap, (border) -> border.unpackValue().isPresent() ? Either.left(border) : Either.right(border));
         }
      }
   }

   public static enum Type implements StringRepresentable {
      STRETCH("stretch", GuiSpriteScaling.Stretch.CODEC),
      TILE("tile", GuiSpriteScaling.Tile.CODEC),
      NINE_SLICE("nine_slice", GuiSpriteScaling.NineSlice.CODEC);

      public static final Codec<Type> CODEC = StringRepresentable.<Type>fromEnum(Type::values);
      private final String key;
      private final MapCodec<? extends GuiSpriteScaling> codec;

      private Type(final String key, final MapCodec<? extends GuiSpriteScaling> codec) {
         this.key = key;
         this.codec = codec;
      }

      public String getSerializedName() {
         return this.key;
      }

      public MapCodec<? extends GuiSpriteScaling> codec() {
         return this.codec;
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{STRETCH, TILE, NINE_SLICE};
      }
   }
}
