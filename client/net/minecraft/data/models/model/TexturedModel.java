package net.minecraft.data.models.model;

import com.google.gson.JsonElement;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class TexturedModel {
   public static final Provider CUBE;
   public static final Provider CUBE_INNER_FACES;
   public static final Provider CUBE_MIRRORED;
   public static final Provider COLUMN;
   public static final Provider COLUMN_HORIZONTAL;
   public static final Provider CUBE_TOP_BOTTOM;
   public static final Provider CUBE_TOP;
   public static final Provider ORIENTABLE_ONLY_TOP;
   public static final Provider ORIENTABLE;
   public static final Provider CARPET;
   public static final Provider FLOWERBED_1;
   public static final Provider FLOWERBED_2;
   public static final Provider FLOWERBED_3;
   public static final Provider FLOWERBED_4;
   public static final Provider GLAZED_TERRACOTTA;
   public static final Provider CORAL_FAN;
   public static final Provider PARTICLE_ONLY;
   public static final Provider ANVIL;
   public static final Provider LEAVES;
   public static final Provider LANTERN;
   public static final Provider HANGING_LANTERN;
   public static final Provider SEAGRASS;
   public static final Provider COLUMN_ALT;
   public static final Provider COLUMN_HORIZONTAL_ALT;
   public static final Provider TOP_BOTTOM_WITH_WALL;
   public static final Provider COLUMN_WITH_WALL;
   private final TextureMapping mapping;
   private final ModelTemplate template;

   private TexturedModel(TextureMapping var1, ModelTemplate var2) {
      super();
      this.mapping = var1;
      this.template = var2;
   }

   public ModelTemplate getTemplate() {
      return this.template;
   }

   public TextureMapping getMapping() {
      return this.mapping;
   }

   public TexturedModel updateTextures(Consumer<TextureMapping> var1) {
      var1.accept(this.mapping);
      return this;
   }

   public ResourceLocation create(Block var1, BiConsumer<ResourceLocation, Supplier<JsonElement>> var2) {
      return this.template.create(var1, this.mapping, var2);
   }

   public ResourceLocation createWithSuffix(Block var1, String var2, BiConsumer<ResourceLocation, Supplier<JsonElement>> var3) {
      return this.template.createWithSuffix(var1, var2, this.mapping, var3);
   }

   private static Provider createDefault(Function<Block, TextureMapping> var0, ModelTemplate var1) {
      return (var2) -> {
         return new TexturedModel((TextureMapping)var0.apply(var2), var1);
      };
   }

   public static TexturedModel createAllSame(ResourceLocation var0) {
      return new TexturedModel(TextureMapping.cube(var0), ModelTemplates.CUBE_ALL);
   }

   static {
      CUBE = createDefault(TextureMapping::cube, ModelTemplates.CUBE_ALL);
      CUBE_INNER_FACES = createDefault(TextureMapping::cube, ModelTemplates.CUBE_ALL_INNER_FACES);
      CUBE_MIRRORED = createDefault(TextureMapping::cube, ModelTemplates.CUBE_MIRRORED_ALL);
      COLUMN = createDefault(TextureMapping::column, ModelTemplates.CUBE_COLUMN);
      COLUMN_HORIZONTAL = createDefault(TextureMapping::column, ModelTemplates.CUBE_COLUMN_HORIZONTAL);
      CUBE_TOP_BOTTOM = createDefault(TextureMapping::cubeBottomTop, ModelTemplates.CUBE_BOTTOM_TOP);
      CUBE_TOP = createDefault(TextureMapping::cubeTop, ModelTemplates.CUBE_TOP);
      ORIENTABLE_ONLY_TOP = createDefault(TextureMapping::orientableCubeOnlyTop, ModelTemplates.CUBE_ORIENTABLE);
      ORIENTABLE = createDefault(TextureMapping::orientableCube, ModelTemplates.CUBE_ORIENTABLE_TOP_BOTTOM);
      CARPET = createDefault(TextureMapping::wool, ModelTemplates.CARPET);
      FLOWERBED_1 = createDefault(TextureMapping::flowerbed, ModelTemplates.FLOWERBED_1);
      FLOWERBED_2 = createDefault(TextureMapping::flowerbed, ModelTemplates.FLOWERBED_2);
      FLOWERBED_3 = createDefault(TextureMapping::flowerbed, ModelTemplates.FLOWERBED_3);
      FLOWERBED_4 = createDefault(TextureMapping::flowerbed, ModelTemplates.FLOWERBED_4);
      GLAZED_TERRACOTTA = createDefault(TextureMapping::pattern, ModelTemplates.GLAZED_TERRACOTTA);
      CORAL_FAN = createDefault(TextureMapping::fan, ModelTemplates.CORAL_FAN);
      PARTICLE_ONLY = createDefault(TextureMapping::particle, ModelTemplates.PARTICLE_ONLY);
      ANVIL = createDefault(TextureMapping::top, ModelTemplates.ANVIL);
      LEAVES = createDefault(TextureMapping::cube, ModelTemplates.LEAVES);
      LANTERN = createDefault(TextureMapping::lantern, ModelTemplates.LANTERN);
      HANGING_LANTERN = createDefault(TextureMapping::lantern, ModelTemplates.HANGING_LANTERN);
      SEAGRASS = createDefault(TextureMapping::defaultTexture, ModelTemplates.SEAGRASS);
      COLUMN_ALT = createDefault(TextureMapping::logColumn, ModelTemplates.CUBE_COLUMN);
      COLUMN_HORIZONTAL_ALT = createDefault(TextureMapping::logColumn, ModelTemplates.CUBE_COLUMN_HORIZONTAL);
      TOP_BOTTOM_WITH_WALL = createDefault(TextureMapping::cubeBottomTopWithWall, ModelTemplates.CUBE_BOTTOM_TOP);
      COLUMN_WITH_WALL = createDefault(TextureMapping::columnWithWall, ModelTemplates.CUBE_COLUMN);
   }

   @FunctionalInterface
   public interface Provider {
      TexturedModel get(Block var1);

      default ResourceLocation create(Block var1, BiConsumer<ResourceLocation, Supplier<JsonElement>> var2) {
         return this.get(var1).create(var1, var2);
      }

      default ResourceLocation createWithSuffix(Block var1, String var2, BiConsumer<ResourceLocation, Supplier<JsonElement>> var3) {
         return this.get(var1).createWithSuffix(var1, var2, var3);
      }

      default Provider updateTexture(Consumer<TextureMapping> var1) {
         return (var2) -> {
            return this.get(var2).updateTextures(var1);
         };
      }
   }
}
