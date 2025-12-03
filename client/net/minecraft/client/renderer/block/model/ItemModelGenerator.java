package net.minecraft.client.renderer.block.model;

import com.mojang.math.Quadrant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.QuadCollection;
import net.minecraft.client.resources.model.UnbakedGeometry;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

public class ItemModelGenerator implements UnbakedModel {
   public static final Identifier GENERATED_ITEM_MODEL_ID = Identifier.withDefaultNamespace("builtin/generated");
   public static final List<String> LAYERS = List.of("layer0", "layer1", "layer2", "layer3", "layer4");
   private static final float MIN_Z = 7.5F;
   private static final float MAX_Z = 8.5F;
   private static final TextureSlots.Data TEXTURE_SLOTS = (new TextureSlots.Data.Builder()).addReference("particle", "layer0").build();
   private static final BlockElementFace.UVs SOUTH_FACE_UVS = new BlockElementFace.UVs(0.0F, 0.0F, 16.0F, 16.0F);
   private static final BlockElementFace.UVs NORTH_FACE_UVS = new BlockElementFace.UVs(16.0F, 0.0F, 0.0F, 16.0F);
   private static final float UV_SHRINK = 0.1F;

   public ItemModelGenerator() {
      super();
   }

   public TextureSlots.Data textureSlots() {
      return TEXTURE_SLOTS;
   }

   public UnbakedGeometry geometry() {
      return ItemModelGenerator::bake;
   }

   public UnbakedModel.@Nullable GuiLight guiLight() {
      return UnbakedModel.GuiLight.FRONT;
   }

   private static QuadCollection bake(TextureSlots var0, ModelBaker var1, ModelState var2, ModelDebugName var3) {
      ArrayList var4 = new ArrayList();

      for(int var5 = 0; var5 < LAYERS.size(); ++var5) {
         String var6 = (String)LAYERS.get(var5);
         Material var7 = var0.getMaterial(var6);
         if (var7 == null) {
            break;
         }

         SpriteContents var8 = var1.sprites().get(var7, var3).contents();
         var4.addAll(processFrames(var5, var6, var8));
      }

      return SimpleUnbakedGeometry.bake(var4, var0, var1, var2, var3);
   }

   private static List<BlockElement> processFrames(int var0, String var1, SpriteContents var2) {
      Map var3 = Map.of(Direction.SOUTH, new BlockElementFace((Direction)null, var0, var1, SOUTH_FACE_UVS, Quadrant.R0), Direction.NORTH, new BlockElementFace((Direction)null, var0, var1, NORTH_FACE_UVS, Quadrant.R0));
      ArrayList var4 = new ArrayList();
      var4.add(new BlockElement(new Vector3f(0.0F, 0.0F, 7.5F), new Vector3f(16.0F, 16.0F, 8.5F), var3));
      var4.addAll(createSideElements(var2, var1, var0));
      return var4;
   }

   private static List<BlockElement> createSideElements(SpriteContents var0, String var1, int var2) {
      float var3 = 16.0F / (float)var0.width();
      float var4 = 16.0F / (float)var0.height();
      ArrayList var5 = new ArrayList();

      for(SideFace var7 : getSideFaces(var0)) {
         float var8 = (float)var7.x();
         float var9 = (float)var7.y();
         SideDirection var10 = var7.facing();
         float var11 = var8 + 0.1F;
         float var12 = var8 + 1.0F - 0.1F;
         float var13;
         float var14;
         if (var10.isHorizontal()) {
            var13 = var9 + 0.1F;
            var14 = var9 + 1.0F - 0.1F;
         } else {
            var13 = var9 + 1.0F - 0.1F;
            var14 = var9 + 0.1F;
         }

         float var15 = var8;
         float var16 = var9;
         float var17 = var8;
         float var18 = var9;
         switch (var10.ordinal()) {
            case 0:
               var17 = var8 + 1.0F;
               break;
            case 1:
               var17 = var8 + 1.0F;
               var16 = var9 + 1.0F;
               var18 = var9 + 1.0F;
               break;
            case 2:
               var18 = var9 + 1.0F;
               break;
            case 3:
               var15 = var8 + 1.0F;
               var17 = var8 + 1.0F;
               var18 = var9 + 1.0F;
         }

         var15 *= var3;
         var17 *= var3;
         var16 *= var4;
         var18 *= var4;
         var16 = 16.0F - var16;
         var18 = 16.0F - var18;
         Map var19 = Map.of(var10.getDirection(), new BlockElementFace((Direction)null, var2, var1, new BlockElementFace.UVs(var11 * var3, var13 * var3, var12 * var4, var14 * var4), Quadrant.R0));
         switch (var10.ordinal()) {
            case 0:
               var5.add(new BlockElement(new Vector3f(var15, var16, 7.5F), new Vector3f(var17, var16, 8.5F), var19));
               break;
            case 1:
               var5.add(new BlockElement(new Vector3f(var15, var18, 7.5F), new Vector3f(var17, var18, 8.5F), var19));
               break;
            case 2:
               var5.add(new BlockElement(new Vector3f(var15, var16, 7.5F), new Vector3f(var15, var18, 8.5F), var19));
               break;
            case 3:
               var5.add(new BlockElement(new Vector3f(var17, var16, 7.5F), new Vector3f(var17, var18, 8.5F), var19));
         }
      }

      return var5;
   }

   private static Collection<SideFace> getSideFaces(SpriteContents var0) {
      int var1 = var0.width();
      int var2 = var0.height();
      HashSet var3 = new HashSet();
      var0.getUniqueFrames().forEach((var4) -> {
         for(int var5 = 0; var5 < var2; ++var5) {
            for(int var6 = 0; var6 < var1; ++var6) {
               boolean var7 = !isTransparent(var0, var4, var6, var5, var1, var2);
               if (var7) {
                  checkTransition(ItemModelGenerator.SideDirection.UP, var3, var0, var4, var6, var5, var1, var2);
                  checkTransition(ItemModelGenerator.SideDirection.DOWN, var3, var0, var4, var6, var5, var1, var2);
                  checkTransition(ItemModelGenerator.SideDirection.LEFT, var3, var0, var4, var6, var5, var1, var2);
                  checkTransition(ItemModelGenerator.SideDirection.RIGHT, var3, var0, var4, var6, var5, var1, var2);
               }
            }
         }

      });
      return var3;
   }

   private static void checkTransition(SideDirection var0, Set<SideFace> var1, SpriteContents var2, int var3, int var4, int var5, int var6, int var7) {
      if (isTransparent(var2, var3, var4 - var0.direction.getStepX(), var5 - var0.direction.getStepY(), var6, var7)) {
         var1.add(new SideFace(var0, var4, var5));
      }

   }

   private static boolean isTransparent(SpriteContents var0, int var1, int var2, int var3, int var4, int var5) {
      return var2 >= 0 && var3 >= 0 && var2 < var4 && var3 < var5 ? var0.isTransparent(var1, var2, var3) : true;
   }

   static enum SideDirection {
      UP(Direction.UP),
      DOWN(Direction.DOWN),
      LEFT(Direction.EAST),
      RIGHT(Direction.WEST);

      final Direction direction;

      private SideDirection(final Direction var3) {
         this.direction = var3;
      }

      public Direction getDirection() {
         return this.direction;
      }

      boolean isHorizontal() {
         return this == DOWN || this == UP;
      }

      // $FF: synthetic method
      private static SideDirection[] $values() {
         return new SideDirection[]{UP, DOWN, LEFT, RIGHT};
      }
   }

   static record SideFace(SideDirection facing, int x, int y) {
      SideFace(SideDirection var1, int var2, int var3) {
         super();
         this.facing = var1;
         this.x = var2;
         this.y = var3;
      }
   }
}
