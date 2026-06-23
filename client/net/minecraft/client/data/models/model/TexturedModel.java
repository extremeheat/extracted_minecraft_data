package net.minecraft.client.data.models.model;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

public class TexturedModel {
   public static final Provider CUBE;
   public static final Provider CUBE_INNER_FACES;
   public static final Provider CUBE_MIRRORED;
   public static final Provider COLUMN;
   public static final Provider COLUMN_HORIZONTAL;
   public static final Provider CUBE_BOTTOM_TOP;
   public static final Provider CUBE_BOTTOM_TOP_INDENTED;
   public static final Provider CUBE_TOP;
   public static final Provider ORIENTABLE_ONLY_TOP;
   public static final Provider ORIENTABLE;
   public static final Provider CARPET;
   public static final Provider MOSSY_CARPET_SIDE;
   public static final Provider FLOWERBED_1;
   public static final Provider FLOWERBED_2;
   public static final Provider FLOWERBED_3;
   public static final Provider FLOWERBED_4;
   public static final Provider LEAF_LITTER_1;
   public static final Provider LEAF_LITTER_2;
   public static final Provider LEAF_LITTER_3;
   public static final Provider LEAF_LITTER_4;
   public static final Provider GLAZED_TERRACOTTA;
   public static final Provider CORAL_FAN;
   public static final Provider ANVIL;
   public static final Provider LEAVES;
   public static final Provider LANTERN;
   public static final Provider HANGING_LANTERN;
   public static final Provider CHAIN;
   public static final Provider SEAGRASS;
   public static final Provider COLUMN_ALT;
   public static final Provider COLUMN_HORIZONTAL_ALT;
   public static final Provider TOP_BOTTOM_WITH_WALL;
   public static final Provider COLUMN_WITH_WALL;
   private final TextureMapping mapping;
   private final ModelTemplate template;

   private TexturedModel(final TextureMapping mapping, final ModelTemplate template) {
      super();
      this.mapping = mapping;
      this.template = template;
   }

   public ModelTemplate getTemplate() {
      return this.template;
   }

   public TextureMapping getMapping() {
      return this.mapping;
   }

   public TexturedModel updateTextures(final Consumer<TextureMapping> mutator) {
      mutator.accept(this.mapping);
      return this;
   }

   public Identifier create(final Block block, final BiConsumer<Identifier, ModelInstance> modelOutput) {
      return this.template.create(block, this.mapping, modelOutput);
   }

   public Identifier createWithSuffix(final Block block, final String extraSuffix, final BiConsumer<Identifier, ModelInstance> modelOutput) {
      return this.template.createWithSuffix(block, extraSuffix, this.mapping, modelOutput);
   }

   private static Provider createDefault(final Function<Block, TextureMapping> mapping, final ModelTemplate template) {
      return (block) -> new TexturedModel((TextureMapping)mapping.apply(block), template);
   }

   public static TexturedModel createAllSame(final Material material) {
      return new TexturedModel(TextureMapping.cube(material), ModelTemplates.CUBE_ALL);
   }

   static {
      CUBE = createDefault(TextureMapping::cube, ModelTemplates.CUBE_ALL);
      CUBE_INNER_FACES = createDefault(TextureMapping::cube, ModelTemplates.CUBE_ALL_INNER_FACES);
      CUBE_MIRRORED = createDefault(TextureMapping::cube, ModelTemplates.CUBE_MIRRORED_ALL);
      COLUMN = createDefault(TextureMapping::column, ModelTemplates.CUBE_COLUMN);
      COLUMN_HORIZONTAL = createDefault(TextureMapping::column, ModelTemplates.CUBE_COLUMN_HORIZONTAL);
      CUBE_BOTTOM_TOP = createDefault(TextureMapping::cubeBottomTop, ModelTemplates.CUBE_BOTTOM_TOP);
      CUBE_BOTTOM_TOP_INDENTED = createDefault(TextureMapping::cubeBottomTop, ModelTemplates.CUBE_BOTTOM_TOP_INDENTED);
      CUBE_TOP = createDefault(TextureMapping::cubeTop, ModelTemplates.CUBE_TOP);
      ORIENTABLE_ONLY_TOP = createDefault(TextureMapping::orientableCubeOnlyTop, ModelTemplates.CUBE_ORIENTABLE);
      ORIENTABLE = createDefault(TextureMapping::orientableCube, ModelTemplates.CUBE_ORIENTABLE_TOP_BOTTOM);
      CARPET = createDefault(TextureMapping::wool, ModelTemplates.CARPET);
      MOSSY_CARPET_SIDE = createDefault(TextureMapping::side, ModelTemplates.MOSSY_CARPET_SIDE);
      FLOWERBED_1 = createDefault(TextureMapping::flowerbed, ModelTemplates.FLOWERBED_1);
      FLOWERBED_2 = createDefault(TextureMapping::flowerbed, ModelTemplates.FLOWERBED_2);
      FLOWERBED_3 = createDefault(TextureMapping::flowerbed, ModelTemplates.FLOWERBED_3);
      FLOWERBED_4 = createDefault(TextureMapping::flowerbed, ModelTemplates.FLOWERBED_4);
      LEAF_LITTER_1 = createDefault(TextureMapping::defaultTexture, ModelTemplates.LEAF_LITTER_1);
      LEAF_LITTER_2 = createDefault(TextureMapping::defaultTexture, ModelTemplates.LEAF_LITTER_2);
      LEAF_LITTER_3 = createDefault(TextureMapping::defaultTexture, ModelTemplates.LEAF_LITTER_3);
      LEAF_LITTER_4 = createDefault(TextureMapping::defaultTexture, ModelTemplates.LEAF_LITTER_4);
      GLAZED_TERRACOTTA = createDefault(TextureMapping::pattern, ModelTemplates.GLAZED_TERRACOTTA);
      CORAL_FAN = createDefault(TextureMapping::fan, ModelTemplates.CORAL_FAN);
      ANVIL = createDefault(TextureMapping::top, ModelTemplates.ANVIL);
      LEAVES = createDefault(TextureMapping::cube, ModelTemplates.LEAVES);
      LANTERN = createDefault(TextureMapping::lantern, ModelTemplates.LANTERN);
      HANGING_LANTERN = createDefault(TextureMapping::lantern, ModelTemplates.HANGING_LANTERN);
      CHAIN = createDefault(TextureMapping::defaultTexture, ModelTemplates.CHAIN);
      SEAGRASS = createDefault(TextureMapping::defaultTexture, ModelTemplates.SEAGRASS);
      COLUMN_ALT = createDefault(TextureMapping::logColumn, ModelTemplates.CUBE_COLUMN);
      COLUMN_HORIZONTAL_ALT = createDefault(TextureMapping::logColumn, ModelTemplates.CUBE_COLUMN_HORIZONTAL);
      TOP_BOTTOM_WITH_WALL = createDefault(TextureMapping::cubeBottomTopWithWall, ModelTemplates.CUBE_BOTTOM_TOP);
      COLUMN_WITH_WALL = createDefault(TextureMapping::columnWithWall, ModelTemplates.CUBE_COLUMN);
   }

   @FunctionalInterface
   public interface Provider {
      TexturedModel get(final Block block);

      default Identifier create(final Block block, final BiConsumer<Identifier, ModelInstance> modelOutput) {
         return this.get(block).create(block, modelOutput);
      }

      default Identifier createWithSuffix(final Block block, final String suffix, final BiConsumer<Identifier, ModelInstance> modelOutput) {
         return this.get(block).createWithSuffix(block, suffix, modelOutput);
      }

      default Provider updateTexture(final Consumer<TextureMapping> mutator) {
         return (block) -> this.get(block).updateTextures(mutator);
      }
   }
}
