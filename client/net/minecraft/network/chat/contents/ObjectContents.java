package net.minecraft.network.chat.contents;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.data.AtlasIds;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

public record ObjectContents(ObjectInfo contents) implements ComponentContents {
   private static final String PLACEHOLDER = Character.toString('\ufffc');
   public static final MapCodec<ObjectContents> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ObjectContents.ObjectInfo.MAP_CODEC.forGetter(ObjectContents::contents)).apply(var0, ObjectContents::new));
   public static final ComponentContents.Type<ObjectContents> TYPE;

   public ObjectContents(ObjectInfo var1) {
      super();
      this.contents = var1;
   }

   public ComponentContents.Type<?> type() {
      return TYPE;
   }

   public <T> Optional<T> visit(FormattedText.ContentConsumer<T> var1) {
      return var1.accept(this.contents.description());
   }

   public <T> Optional<T> visit(FormattedText.StyledContentConsumer<T> var1, Style var2) {
      return var1.accept(var2.withFont(this.contents.updatedFont()), PLACEHOLDER);
   }

   static {
      TYPE = new ComponentContents.Type<ObjectContents>(CODEC, "object");
   }

   public interface ObjectInfo {
      MapCodec<ObjectInfo> MAP_CODEC = ObjectContents.AtlasSprite.MAP_CODEC.flatXmap(DataResult::success, (var0) -> {
         DataResult var10000;
         if (var0 instanceof AtlasSprite var1) {
            var10000 = DataResult.success(var1);
         } else {
            var10000 = DataResult.error(() -> "Unknown object contents: " + String.valueOf(var0));
         }

         return var10000;
      });

      FontDescription updatedFont();

      String description();
   }

   public static record AtlasSprite(ResourceLocation atlas, ResourceLocation sprite) implements ObjectInfo {
      public static final ResourceLocation DEFAULT_ATLAS;
      public static final MapCodec<AtlasSprite> MAP_CODEC;

      public AtlasSprite(ResourceLocation var1, ResourceLocation var2) {
         super();
         this.atlas = var1;
         this.sprite = var2;
      }

      public FontDescription updatedFont() {
         return new FontDescription.AtlasSprite(this.atlas, this.sprite);
      }

      private static String toShortName(ResourceLocation var0) {
         return var0.getNamespace().equals("minecraft") ? var0.getPath() : var0.toString();
      }

      public String description() {
         String var1 = toShortName(this.sprite);
         return this.atlas.equals(DEFAULT_ATLAS) ? "[" + var1 + "]" : "[" + var1 + "@" + toShortName(this.atlas) + "]";
      }

      static {
         DEFAULT_ATLAS = AtlasIds.BLOCKS;
         MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ResourceLocation.CODEC.optionalFieldOf("atlas", DEFAULT_ATLAS).forGetter(AtlasSprite::atlas), ResourceLocation.CODEC.fieldOf("sprite").forGetter(AtlasSprite::sprite)).apply(var0, AtlasSprite::new));
      }
   }
}
