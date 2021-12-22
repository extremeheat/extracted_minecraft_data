package net.minecraft.data;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.StringUtils;

public class BlockFamily {
   private final Block baseBlock;
   final Map<BlockFamily.Variant, Block> variants = Maps.newHashMap();
   boolean generateModel = true;
   boolean generateRecipe = true;
   @Nullable
   String recipeGroupPrefix;
   @Nullable
   String recipeUnlockedBy;

   BlockFamily(Block var1) {
      super();
      this.baseBlock = var1;
   }

   public Block getBaseBlock() {
      return this.baseBlock;
   }

   public Map<BlockFamily.Variant, Block> getVariants() {
      return this.variants;
   }

   public Block get(BlockFamily.Variant var1) {
      return (Block)this.variants.get(var1);
   }

   public boolean shouldGenerateModel() {
      return this.generateModel;
   }

   public boolean shouldGenerateRecipe() {
      return this.generateRecipe;
   }

   public Optional<String> getRecipeGroupPrefix() {
      return StringUtils.isBlank(this.recipeGroupPrefix) ? Optional.empty() : Optional.of(this.recipeGroupPrefix);
   }

   public Optional<String> getRecipeUnlockedBy() {
      return StringUtils.isBlank(this.recipeUnlockedBy) ? Optional.empty() : Optional.of(this.recipeUnlockedBy);
   }

   public static class Builder {
      private final BlockFamily family;

      public Builder(Block var1) {
         super();
         this.family = new BlockFamily(var1);
      }

      public BlockFamily getFamily() {
         return this.family;
      }

      public BlockFamily.Builder button(Block var1) {
         this.family.variants.put(BlockFamily.Variant.BUTTON, var1);
         return this;
      }

      public BlockFamily.Builder chiseled(Block var1) {
         this.family.variants.put(BlockFamily.Variant.CHISELED, var1);
         return this;
      }

      public BlockFamily.Builder cracked(Block var1) {
         this.family.variants.put(BlockFamily.Variant.CRACKED, var1);
         return this;
      }

      public BlockFamily.Builder cut(Block var1) {
         this.family.variants.put(BlockFamily.Variant.CUT, var1);
         return this;
      }

      public BlockFamily.Builder door(Block var1) {
         this.family.variants.put(BlockFamily.Variant.DOOR, var1);
         return this;
      }

      public BlockFamily.Builder fence(Block var1) {
         this.family.variants.put(BlockFamily.Variant.FENCE, var1);
         return this;
      }

      public BlockFamily.Builder fenceGate(Block var1) {
         this.family.variants.put(BlockFamily.Variant.FENCE_GATE, var1);
         return this;
      }

      public BlockFamily.Builder sign(Block var1, Block var2) {
         this.family.variants.put(BlockFamily.Variant.SIGN, var1);
         this.family.variants.put(BlockFamily.Variant.WALL_SIGN, var2);
         return this;
      }

      public BlockFamily.Builder slab(Block var1) {
         this.family.variants.put(BlockFamily.Variant.SLAB, var1);
         return this;
      }

      public BlockFamily.Builder stairs(Block var1) {
         this.family.variants.put(BlockFamily.Variant.STAIRS, var1);
         return this;
      }

      public BlockFamily.Builder pressurePlate(Block var1) {
         this.family.variants.put(BlockFamily.Variant.PRESSURE_PLATE, var1);
         return this;
      }

      public BlockFamily.Builder polished(Block var1) {
         this.family.variants.put(BlockFamily.Variant.POLISHED, var1);
         return this;
      }

      public BlockFamily.Builder trapdoor(Block var1) {
         this.family.variants.put(BlockFamily.Variant.TRAPDOOR, var1);
         return this;
      }

      public BlockFamily.Builder wall(Block var1) {
         this.family.variants.put(BlockFamily.Variant.WALL, var1);
         return this;
      }

      public BlockFamily.Builder dontGenerateModel() {
         this.family.generateModel = false;
         return this;
      }

      public BlockFamily.Builder dontGenerateRecipe() {
         this.family.generateRecipe = false;
         return this;
      }

      public BlockFamily.Builder recipeGroupPrefix(String var1) {
         this.family.recipeGroupPrefix = var1;
         return this;
      }

      public BlockFamily.Builder recipeUnlockedBy(String var1) {
         this.family.recipeUnlockedBy = var1;
         return this;
      }
   }

   public static enum Variant {
      BUTTON("button"),
      CHISELED("chiseled"),
      CRACKED("cracked"),
      CUT("cut"),
      DOOR("door"),
      FENCE("fence"),
      FENCE_GATE("fence_gate"),
      SIGN("sign"),
      SLAB("slab"),
      STAIRS("stairs"),
      PRESSURE_PLATE("pressure_plate"),
      POLISHED("polished"),
      TRAPDOOR("trapdoor"),
      WALL("wall"),
      WALL_SIGN("wall_sign");

      private final String name;

      private Variant(String var3) {
         this.name = var3;
      }

      public String getName() {
         return this.name;
      }

      // $FF: synthetic method
      private static BlockFamily.Variant[] $values() {
         return new BlockFamily.Variant[]{BUTTON, CHISELED, CRACKED, CUT, DOOR, FENCE, FENCE_GATE, SIGN, SLAB, STAIRS, PRESSURE_PLATE, POLISHED, TRAPDOOR, WALL, WALL_SIGN};
      }
   }
}
