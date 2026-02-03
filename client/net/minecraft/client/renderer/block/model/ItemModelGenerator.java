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

   private static QuadCollection bake(final TextureSlots textureSlots, final ModelBaker modelBaker, final ModelState modelState, final ModelDebugName name) {
      List<BlockElement> elements = new ArrayList();

      for(int layerIndex = 0; layerIndex < LAYERS.size(); ++layerIndex) {
         String textureReference = (String)LAYERS.get(layerIndex);
         Material material = textureSlots.getMaterial(textureReference);
         if (material == null) {
            break;
         }

         SpriteContents sprite = modelBaker.sprites().get(material, name).contents();
         elements.addAll(processFrames(layerIndex, textureReference, sprite));
      }

      return SimpleUnbakedGeometry.bake(elements, textureSlots, modelBaker, modelState, name);
   }

   private static List<BlockElement> processFrames(final int tintIndex, final String textureName, final SpriteContents sprite) {
      Map<Direction, BlockElementFace> frontAndBackFaces = Map.of(Direction.SOUTH, new BlockElementFace((Direction)null, tintIndex, textureName, SOUTH_FACE_UVS, Quadrant.R0), Direction.NORTH, new BlockElementFace((Direction)null, tintIndex, textureName, NORTH_FACE_UVS, Quadrant.R0));
      List<BlockElement> elements = new ArrayList();
      elements.add(new BlockElement(new Vector3f(0.0F, 0.0F, 7.5F), new Vector3f(16.0F, 16.0F, 8.5F), frontAndBackFaces));
      elements.addAll(createSideElements(sprite, textureName, tintIndex));
      return elements;
   }

   private static List<BlockElement> createSideElements(final SpriteContents sprite, final String textureName, final int tintIndex) {
      float xScale = 16.0F / (float)sprite.width();
      float yScale = 16.0F / (float)sprite.height();
      List<BlockElement> result = new ArrayList();

      for(SideFace sideFace : getSideFaces(sprite)) {
         float x = (float)sideFace.x();
         float y = (float)sideFace.y();
         SideDirection sideDirection = sideFace.facing();
         float u0 = x + 0.1F;
         float u1 = x + 1.0F - 0.1F;
         float v0;
         float v1;
         if (sideDirection.isHorizontal()) {
            v0 = y + 0.1F;
            v1 = y + 1.0F - 0.1F;
         } else {
            v0 = y + 1.0F - 0.1F;
            v1 = y + 0.1F;
         }

         float startX = x;
         float startY = y;
         float endX = x;
         float endY = y;
         switch (sideDirection.ordinal()) {
            case 0:
               endX = x + 1.0F;
               break;
            case 1:
               endX = x + 1.0F;
               startY = y + 1.0F;
               endY = y + 1.0F;
               break;
            case 2:
               endY = y + 1.0F;
               break;
            case 3:
               startX = x + 1.0F;
               endX = x + 1.0F;
               endY = y + 1.0F;
         }

         startX *= xScale;
         endX *= xScale;
         startY *= yScale;
         endY *= yScale;
         startY = 16.0F - startY;
         endY = 16.0F - endY;
         Map<Direction, BlockElementFace> faces = Map.of(sideDirection.getDirection(), new BlockElementFace((Direction)null, tintIndex, textureName, new BlockElementFace.UVs(u0 * xScale, v0 * xScale, u1 * yScale, v1 * yScale), Quadrant.R0));
         switch (sideDirection.ordinal()) {
            case 0:
               result.add(new BlockElement(new Vector3f(startX, startY, 7.5F), new Vector3f(endX, startY, 8.5F), faces));
               break;
            case 1:
               result.add(new BlockElement(new Vector3f(startX, endY, 7.5F), new Vector3f(endX, endY, 8.5F), faces));
               break;
            case 2:
               result.add(new BlockElement(new Vector3f(startX, startY, 7.5F), new Vector3f(startX, endY, 8.5F), faces));
               break;
            case 3:
               result.add(new BlockElement(new Vector3f(endX, startY, 7.5F), new Vector3f(endX, endY, 8.5F), faces));
         }
      }

      return result;
   }

   private static Collection<SideFace> getSideFaces(final SpriteContents sprite) {
      int width = sprite.width();
      int height = sprite.height();
      Set<SideFace> sideFaces = new HashSet();
      sprite.getUniqueFrames().forEach((frame) -> {
         for(int y = 0; y < height; ++y) {
            for(int x = 0; x < width; ++x) {
               boolean thisOpaque = !isTransparent(sprite, frame, x, y, width, height);
               if (thisOpaque) {
                  checkTransition(ItemModelGenerator.SideDirection.UP, sideFaces, sprite, frame, x, y, width, height);
                  checkTransition(ItemModelGenerator.SideDirection.DOWN, sideFaces, sprite, frame, x, y, width, height);
                  checkTransition(ItemModelGenerator.SideDirection.LEFT, sideFaces, sprite, frame, x, y, width, height);
                  checkTransition(ItemModelGenerator.SideDirection.RIGHT, sideFaces, sprite, frame, x, y, width, height);
               }
            }
         }

      });
      return sideFaces;
   }

   private static void checkTransition(final SideDirection facing, final Set<SideFace> sideFaces, final SpriteContents sprite, final int frame, final int x, final int y, final int width, final int height) {
      if (isTransparent(sprite, frame, x - facing.direction.getStepX(), y - facing.direction.getStepY(), width, height)) {
         sideFaces.add(new SideFace(facing, x, y));
      }

   }

   private static boolean isTransparent(final SpriteContents sprite, final int frame, final int x, final int y, final int width, final int height) {
      return x >= 0 && y >= 0 && x < width && y < height ? sprite.isTransparent(frame, x, y) : true;
   }

   private static enum SideDirection {
      UP(Direction.UP),
      DOWN(Direction.DOWN),
      LEFT(Direction.EAST),
      RIGHT(Direction.WEST);

      private final Direction direction;

      private SideDirection(final Direction direction) {
         this.direction = direction;
      }

      public Direction getDirection() {
         return this.direction;
      }

      private boolean isHorizontal() {
         return this == DOWN || this == UP;
      }

      // $FF: synthetic method
      private static SideDirection[] $values() {
         return new SideDirection[]{UP, DOWN, LEFT, RIGHT};
      }
   }

   private static record SideFace(SideDirection facing, int x, int y) {
      private SideFace {
         super();
      }
   }
}
