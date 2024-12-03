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
      public static final MapCodec<Tile> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ExtraCodecs.POSITIVE_INT.fieldOf("width").forGetter(Tile::width), ExtraCodecs.POSITIVE_INT.fieldOf("height").forGetter(Tile::height)).apply(var0, Tile::new));

      public Tile(int var1, int var2) {
         super();
         this.width = var1;
         this.height = var2;
      }

      public Type type() {
         return GuiSpriteScaling.Type.TILE;
      }
   }

   public static record NineSlice(int width, int height, Border border, boolean stretchInner) implements GuiSpriteScaling {
      public static final MapCodec<NineSlice> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ExtraCodecs.POSITIVE_INT.fieldOf("width").forGetter(NineSlice::width), ExtraCodecs.POSITIVE_INT.fieldOf("height").forGetter(NineSlice::height), GuiSpriteScaling.NineSlice.Border.CODEC.fieldOf("border").forGetter(NineSlice::border), Codec.BOOL.optionalFieldOf("stretch_inner", false).forGetter(NineSlice::stretchInner)).apply(var0, NineSlice::new)).validate(NineSlice::validate);

      public NineSlice(int var1, int var2, Border var3, boolean var4) {
         super();
         this.width = var1;
         this.height = var2;
         this.border = var3;
         this.stretchInner = var4;
      }

      private static DataResult<NineSlice> validate(NineSlice var0) {
         Border var1 = var0.border();
         if (var1.left() + var1.right() >= var0.width()) {
            return DataResult.error(() -> {
               int var10000 = var1.left();
               return "Nine-sliced texture has no horizontal center slice: " + var10000 + " + " + var1.right() + " >= " + var0.width();
            });
         } else {
            return var1.top() + var1.bottom() >= var0.height() ? DataResult.error(() -> {
               int var10000 = var1.top();
               return "Nine-sliced texture has no vertical center slice: " + var10000 + " + " + var1.bottom() + " >= " + var0.height();
            }) : DataResult.success(var0);
         }
      }

      public Type type() {
         return GuiSpriteScaling.Type.NINE_SLICE;
      }

      public static record Border(int left, int top, int right, int bottom) {
         private static final Codec<Border> VALUE_CODEC;
         private static final Codec<Border> RECORD_CODEC;
         static final Codec<Border> CODEC;

         public Border(int var1, int var2, int var3, int var4) {
            super();
            this.left = var1;
            this.top = var2;
            this.right = var3;
            this.bottom = var4;
         }

         private OptionalInt unpackValue() {
            return this.left() == this.top() && this.top() == this.right() && this.right() == this.bottom() ? OptionalInt.of(this.left()) : OptionalInt.empty();
         }

         static {
            VALUE_CODEC = ExtraCodecs.POSITIVE_INT.flatComapMap((var0) -> new Border(var0, var0, var0, var0), (var0) -> {
               OptionalInt var1 = var0.unpackValue();
               return var1.isPresent() ? DataResult.success(var1.getAsInt()) : DataResult.error(() -> "Border has different side sizes");
            });
            RECORD_CODEC = RecordCodecBuilder.create((var0) -> var0.group(ExtraCodecs.NON_NEGATIVE_INT.fieldOf("left").forGetter(Border::left), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("top").forGetter(Border::top), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("right").forGetter(Border::right), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("bottom").forGetter(Border::bottom)).apply(var0, Border::new));
            CODEC = Codec.either(VALUE_CODEC, RECORD_CODEC).xmap(Either::unwrap, (var0) -> var0.unpackValue().isPresent() ? Either.left(var0) : Either.right(var0));
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

      private Type(final String var3, final MapCodec<? extends GuiSpriteScaling> var4) {
         this.key = var3;
         this.codec = var4;
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
