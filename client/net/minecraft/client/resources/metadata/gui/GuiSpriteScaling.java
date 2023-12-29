package net.minecraft.client.resources.metadata.gui;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.OptionalInt;
import java.util.function.Function;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;

public interface GuiSpriteScaling {
   Codec<GuiSpriteScaling> CODEC = GuiSpriteScaling.Type.CODEC.dispatch(GuiSpriteScaling::type, GuiSpriteScaling.Type::codec);
   GuiSpriteScaling DEFAULT = new GuiSpriteScaling.Stretch();

   GuiSpriteScaling.Type type();

   public static record NineSlice(int d, int e, GuiSpriteScaling.NineSlice.Border f) implements GuiSpriteScaling {
      private final int width;
      private final int height;
      private final GuiSpriteScaling.NineSlice.Border border;
      public static final Codec<GuiSpriteScaling.NineSlice> CODEC = ExtraCodecs.validate(
         RecordCodecBuilder.create(
            var0 -> var0.group(
                     ExtraCodecs.POSITIVE_INT.fieldOf("width").forGetter(GuiSpriteScaling.NineSlice::width),
                     ExtraCodecs.POSITIVE_INT.fieldOf("height").forGetter(GuiSpriteScaling.NineSlice::height),
                     GuiSpriteScaling.NineSlice.Border.CODEC.fieldOf("border").forGetter(GuiSpriteScaling.NineSlice::border)
                  )
                  .apply(var0, GuiSpriteScaling.NineSlice::new)
         ),
         GuiSpriteScaling.NineSlice::validate
      );

      public NineSlice(int var1, int var2, GuiSpriteScaling.NineSlice.Border var3) {
         super();
         this.width = var1;
         this.height = var2;
         this.border = var3;
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

      public static record Border(int a, int b, int c, int d) {
         private final int left;
         private final int top;
         private final int right;
         private final int bottom;
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
            .xmap(
               var0 -> (GuiSpriteScaling.NineSlice.Border)var0.map(Function.identity(), Function.identity()),
               var0 -> var0.unpackValue().isPresent() ? Either.left(var0) : Either.right(var0)
            );

         public Border(int var1, int var2, int var3, int var4) {
            super();
            this.left = var1;
            this.top = var2;
            this.right = var3;
            this.bottom = var4;
         }

         private OptionalInt unpackValue() {
            return this.left() == this.top() && this.top() == this.right() && this.right() == this.bottom()
               ? OptionalInt.of(this.left())
               : OptionalInt.empty();
         }
      }
   }

   public static record Stretch() implements GuiSpriteScaling {
      public static final Codec<GuiSpriteScaling.Stretch> CODEC = Codec.unit(GuiSpriteScaling.Stretch::new);

      public Stretch() {
         super();
      }

      @Override
      public GuiSpriteScaling.Type type() {
         return GuiSpriteScaling.Type.STRETCH;
      }
   }

   public static record Tile(int d, int e) implements GuiSpriteScaling {
      private final int width;
      private final int height;
      public static final Codec<GuiSpriteScaling.Tile> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  ExtraCodecs.POSITIVE_INT.fieldOf("width").forGetter(GuiSpriteScaling.Tile::width),
                  ExtraCodecs.POSITIVE_INT.fieldOf("height").forGetter(GuiSpriteScaling.Tile::height)
               )
               .apply(var0, GuiSpriteScaling.Tile::new)
      );

      public Tile(int var1, int var2) {
         super();
         this.width = var1;
         this.height = var2;
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
      private final Codec<? extends GuiSpriteScaling> codec;

      private Type(String var3, Codec<? extends GuiSpriteScaling> var4) {
         this.key = var3;
         this.codec = var4;
      }

      @Override
      public String getSerializedName() {
         return this.key;
      }

      public Codec<? extends GuiSpriteScaling> codec() {
         return this.codec;
      }
   }
}
