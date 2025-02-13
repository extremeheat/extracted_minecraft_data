package net.minecraft.client.renderer.block.model;

import com.mojang.math.Quadrant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.QuadCollection;
import net.minecraft.client.resources.model.SpriteGetter;
import net.minecraft.client.resources.model.UnbakedGeometry;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

public class ItemModelGenerator implements UnbakedModel {
   public static final ResourceLocation GENERATED_ITEM_MODEL_ID = ResourceLocation.withDefaultNamespace("builtin/generated");
   public static final List<String> LAYERS = List.of("layer0", "layer1", "layer2", "layer3", "layer4");
   private static final float MIN_Z = 7.5F;
   private static final float MAX_Z = 8.5F;
   private static final TextureSlots.Data TEXTURE_SLOTS = (new TextureSlots.Data.Builder()).addReference("particle", "layer0").build();
   private static final BlockElementFace.UVs SOUTH_FACE_UVS = new BlockElementFace.UVs(0.0F, 0.0F, 16.0F, 16.0F);
   private static final BlockElementFace.UVs NORTH_FACE_UVS = new BlockElementFace.UVs(16.0F, 0.0F, 0.0F, 16.0F);

   public ItemModelGenerator() {
      super();
   }

   public TextureSlots.Data textureSlots() {
      return TEXTURE_SLOTS;
   }

   public UnbakedGeometry geometry() {
      return ItemModelGenerator::bake;
   }

   @Nullable
   public UnbakedModel.GuiLight guiLight() {
      return UnbakedModel.GuiLight.FRONT;
   }

   private static QuadCollection bake(TextureSlots var0, ModelBaker var1, ModelState var2, ModelDebugName var3) {
      return bake(var0, var1.sprites(), var2, var3);
   }

   private static QuadCollection bake(TextureSlots var0, SpriteGetter var1, ModelState var2, ModelDebugName var3) {
      ArrayList var4 = new ArrayList();

      for(int var5 = 0; var5 < LAYERS.size(); ++var5) {
         String var6 = (String)LAYERS.get(var5);
         Material var7 = var0.getMaterial(var6);
         if (var7 == null) {
            break;
         }

         SpriteContents var8 = var1.get(var7, var3).contents();
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
      float var3 = (float)var0.width();
      float var4 = (float)var0.height();
      ArrayList var5 = new ArrayList();

      for(Span var7 : getSpans(var0)) {
         float var8 = 0.0F;
         float var9 = 0.0F;
         float var10 = 0.0F;
         float var11 = 0.0F;
         float var12 = 0.0F;
         float var13 = 0.0F;
         float var14 = 0.0F;
         float var15 = 0.0F;
         float var16 = 16.0F / var3;
         float var17 = 16.0F / var4;
         float var18 = (float)var7.getMin();
         float var19 = (float)var7.getMax();
         float var20 = (float)var7.getAnchor();
         SpanFacing var21 = var7.getFacing();
         switch (var21.ordinal()) {
            case 0:
               var12 = var18;
               var8 = var18;
               var10 = var13 = var19 + 1.0F;
               var14 = var20;
               var9 = var20;
               var11 = var20;
               var15 = var20 + 1.0F;
               break;
            case 1:
               var14 = var20;
               var15 = var20 + 1.0F;
               var12 = var18;
               var8 = var18;
               var10 = var13 = var19 + 1.0F;
               var9 = var20 + 1.0F;
               var11 = var20 + 1.0F;
               break;
            case 2:
               var12 = var20;
               var8 = var20;
               var10 = var20;
               var13 = var20 + 1.0F;
               var15 = var18;
               var9 = var18;
               var11 = var14 = var19 + 1.0F;
               break;
            case 3:
               var12 = var20;
               var13 = var20 + 1.0F;
               var8 = var20 + 1.0F;
               var10 = var20 + 1.0F;
               var15 = var18;
               var9 = var18;
               var11 = var14 = var19 + 1.0F;
         }

         var8 *= var16;
         var10 *= var16;
         var9 *= var17;
         var11 *= var17;
         var9 = 16.0F - var9;
         var11 = 16.0F - var11;
         var12 *= var16;
         var13 *= var16;
         var14 *= var17;
         var15 *= var17;
         Map var22 = Map.of(var21.getDirection(), new BlockElementFace((Direction)null, var2, var1, new BlockElementFace.UVs(var12, var14, var13, var15), Quadrant.R0));
         switch (var21.ordinal()) {
            case 0:
               var5.add(new BlockElement(new Vector3f(var8, var9, 7.5F), new Vector3f(var10, var9, 8.5F), var22));
               break;
            case 1:
               var5.add(new BlockElement(new Vector3f(var8, var11, 7.5F), new Vector3f(var10, var11, 8.5F), var22));
               break;
            case 2:
               var5.add(new BlockElement(new Vector3f(var8, var9, 7.5F), new Vector3f(var8, var11, 8.5F), var22));
               break;
            case 3:
               var5.add(new BlockElement(new Vector3f(var10, var9, 7.5F), new Vector3f(var10, var11, 8.5F), var22));
         }
      }

      return var5;
   }

   private static List<Span> getSpans(SpriteContents var0) {
      int var1 = var0.width();
      int var2 = var0.height();
      ArrayList var3 = new ArrayList();
      var0.getUniqueFrames().forEach((var4) -> {
         for(int var5 = 0; var5 < var2; ++var5) {
            for(int var6 = 0; var6 < var1; ++var6) {
               boolean var7 = !isTransparent(var0, var4, var6, var5, var1, var2);
               checkTransition(ItemModelGenerator.SpanFacing.UP, var3, var0, var4, var6, var5, var1, var2, var7);
               checkTransition(ItemModelGenerator.SpanFacing.DOWN, var3, var0, var4, var6, var5, var1, var2, var7);
               checkTransition(ItemModelGenerator.SpanFacing.LEFT, var3, var0, var4, var6, var5, var1, var2, var7);
               checkTransition(ItemModelGenerator.SpanFacing.RIGHT, var3, var0, var4, var6, var5, var1, var2, var7);
            }
         }

      });
      return var3;
   }

   private static void checkTransition(SpanFacing var0, List<Span> var1, SpriteContents var2, int var3, int var4, int var5, int var6, int var7, boolean var8) {
      boolean var9 = isTransparent(var2, var3, var4 + var0.getXOffset(), var5 + var0.getYOffset(), var6, var7) && var8;
      if (var9) {
         createOrExpandSpan(var1, var0, var4, var5);
      }

   }

   private static void createOrExpandSpan(List<Span> var0, SpanFacing var1, int var2, int var3) {
      Span var4 = null;

      for(Span var6 : var0) {
         if (var6.getFacing() == var1) {
            int var7 = var1.isHorizontal() ? var3 : var2;
            if (var6.getAnchor() == var7) {
               var4 = var6;
               break;
            }
         }
      }

      int var8 = var1.isHorizontal() ? var3 : var2;
      int var9 = var1.isHorizontal() ? var2 : var3;
      if (var4 == null) {
         var0.add(new Span(var1, var9, var8));
      } else {
         var4.expand(var9);
      }

   }

   private static boolean isTransparent(SpriteContents var0, int var1, int var2, int var3, int var4, int var5) {
      return var2 >= 0 && var3 >= 0 && var2 < var4 && var3 < var5 ? var0.isTransparent(var1, var2, var3) : true;
   }

   static enum SpanFacing {
      UP(Direction.UP, 0, -1),
      DOWN(Direction.DOWN, 0, 1),
      LEFT(Direction.EAST, -1, 0),
      RIGHT(Direction.WEST, 1, 0);

      private final Direction direction;
      private final int xOffset;
      private final int yOffset;

      private SpanFacing(final Direction var3, final int var4, final int var5) {
         this.direction = var3;
         this.xOffset = var4;
         this.yOffset = var5;
      }

      public Direction getDirection() {
         return this.direction;
      }

      public int getXOffset() {
         return this.xOffset;
      }

      public int getYOffset() {
         return this.yOffset;
      }

      boolean isHorizontal() {
         return this == DOWN || this == UP;
      }

      // $FF: synthetic method
      private static SpanFacing[] $values() {
         return new SpanFacing[]{UP, DOWN, LEFT, RIGHT};
      }
   }

   static class Span {
      private final SpanFacing facing;
      private int min;
      private int max;
      private final int anchor;

      public Span(SpanFacing var1, int var2, int var3) {
         super();
         this.facing = var1;
         this.min = var2;
         this.max = var2;
         this.anchor = var3;
      }

      public void expand(int var1) {
         if (var1 < this.min) {
            this.min = var1;
         } else if (var1 > this.max) {
            this.max = var1;
         }

      }

      public SpanFacing getFacing() {
         return this.facing;
      }

      public int getMin() {
         return this.min;
      }

      public int getMax() {
         return this.max;
      }

      public int getAnchor() {
         return this.anchor;
      }
   }
}
