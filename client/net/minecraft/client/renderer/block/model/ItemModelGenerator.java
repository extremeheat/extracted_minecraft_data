package net.minecraft.client.renderer.block.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.client.resources.model.SpriteGetter;
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

   public ItemModelGenerator() {
      super();
   }

   public TextureSlots.Data getTextureSlots() {
      return TEXTURE_SLOTS;
   }

   public void resolveDependencies(ResolvableModel.Resolver var1) {
   }

   @Nullable
   public UnbakedModel.GuiLight getGuiLight() {
      return UnbakedModel.GuiLight.FRONT;
   }

   public BakedModel bake(TextureSlots var1, ModelBaker var2, ModelState var3, boolean var4, boolean var5, ItemTransforms var6) {
      return this.bake(var1, var2.sprites(), var3, var4, var5, var6);
   }

   private BakedModel bake(TextureSlots var1, SpriteGetter var2, ModelState var3, boolean var4, boolean var5, ItemTransforms var6) {
      TextureSlots.Data.Builder var7 = new TextureSlots.Data.Builder();
      ArrayList var8 = new ArrayList();

      for(int var9 = 0; var9 < LAYERS.size(); ++var9) {
         String var10 = (String)LAYERS.get(var9);
         Material var11 = var1.getMaterial(var10);
         if (var11 == null) {
            break;
         }

         var7.addTexture(var10, var11);
         SpriteContents var12 = var2.get(var11).contents();
         var8.addAll(this.processFrames(var9, var10, var12));
      }

      return SimpleBakedModel.bakeElements(var8, var1, var2, var3, var4, var5, false, var6);
   }

   private List<BlockElement> processFrames(int var1, String var2, SpriteContents var3) {
      Map var4 = Map.of(Direction.SOUTH, new BlockElementFace((Direction)null, var1, var2, new BlockFaceUV(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0)), Direction.NORTH, new BlockElementFace((Direction)null, var1, var2, new BlockFaceUV(new float[]{16.0F, 0.0F, 0.0F, 16.0F}, 0)));
      ArrayList var5 = new ArrayList();
      var5.add(new BlockElement(new Vector3f(0.0F, 0.0F, 7.5F), new Vector3f(16.0F, 16.0F, 8.5F), var4));
      var5.addAll(this.createSideElements(var3, var2, var1));
      return var5;
   }

   private List<BlockElement> createSideElements(SpriteContents var1, String var2, int var3) {
      float var4 = (float)var1.width();
      float var5 = (float)var1.height();
      ArrayList var6 = new ArrayList();

      for(Span var8 : this.getSpans(var1)) {
         float var9 = 0.0F;
         float var10 = 0.0F;
         float var11 = 0.0F;
         float var12 = 0.0F;
         float var13 = 0.0F;
         float var14 = 0.0F;
         float var15 = 0.0F;
         float var16 = 0.0F;
         float var17 = 16.0F / var4;
         float var18 = 16.0F / var5;
         float var19 = (float)var8.getMin();
         float var20 = (float)var8.getMax();
         float var21 = (float)var8.getAnchor();
         SpanFacing var22 = var8.getFacing();
         switch (var22.ordinal()) {
            case 0:
               var13 = var19;
               var9 = var19;
               var11 = var14 = var20 + 1.0F;
               var15 = var21;
               var10 = var21;
               var12 = var21;
               var16 = var21 + 1.0F;
               break;
            case 1:
               var15 = var21;
               var16 = var21 + 1.0F;
               var13 = var19;
               var9 = var19;
               var11 = var14 = var20 + 1.0F;
               var10 = var21 + 1.0F;
               var12 = var21 + 1.0F;
               break;
            case 2:
               var13 = var21;
               var9 = var21;
               var11 = var21;
               var14 = var21 + 1.0F;
               var16 = var19;
               var10 = var19;
               var12 = var15 = var20 + 1.0F;
               break;
            case 3:
               var13 = var21;
               var14 = var21 + 1.0F;
               var9 = var21 + 1.0F;
               var11 = var21 + 1.0F;
               var16 = var19;
               var10 = var19;
               var12 = var15 = var20 + 1.0F;
         }

         var9 *= var17;
         var11 *= var17;
         var10 *= var18;
         var12 *= var18;
         var10 = 16.0F - var10;
         var12 = 16.0F - var12;
         var13 *= var17;
         var14 *= var17;
         var15 *= var18;
         var16 *= var18;
         Map var23 = Map.of(var22.getDirection(), new BlockElementFace((Direction)null, var3, var2, new BlockFaceUV(new float[]{var13, var15, var14, var16}, 0)));
         switch (var22.ordinal()) {
            case 0:
               var6.add(new BlockElement(new Vector3f(var9, var10, 7.5F), new Vector3f(var11, var10, 8.5F), var23));
               break;
            case 1:
               var6.add(new BlockElement(new Vector3f(var9, var12, 7.5F), new Vector3f(var11, var12, 8.5F), var23));
               break;
            case 2:
               var6.add(new BlockElement(new Vector3f(var9, var10, 7.5F), new Vector3f(var9, var12, 8.5F), var23));
               break;
            case 3:
               var6.add(new BlockElement(new Vector3f(var11, var10, 7.5F), new Vector3f(var11, var12, 8.5F), var23));
         }
      }

      return var6;
   }

   private List<Span> getSpans(SpriteContents var1) {
      int var2 = var1.width();
      int var3 = var1.height();
      ArrayList var4 = new ArrayList();
      var1.getUniqueFrames().forEach((var5) -> {
         for(int var6 = 0; var6 < var3; ++var6) {
            for(int var7 = 0; var7 < var2; ++var7) {
               boolean var8 = !this.isTransparent(var1, var5, var7, var6, var2, var3);
               this.checkTransition(ItemModelGenerator.SpanFacing.UP, var4, var1, var5, var7, var6, var2, var3, var8);
               this.checkTransition(ItemModelGenerator.SpanFacing.DOWN, var4, var1, var5, var7, var6, var2, var3, var8);
               this.checkTransition(ItemModelGenerator.SpanFacing.LEFT, var4, var1, var5, var7, var6, var2, var3, var8);
               this.checkTransition(ItemModelGenerator.SpanFacing.RIGHT, var4, var1, var5, var7, var6, var2, var3, var8);
            }
         }

      });
      return var4;
   }

   private void checkTransition(SpanFacing var1, List<Span> var2, SpriteContents var3, int var4, int var5, int var6, int var7, int var8, boolean var9) {
      boolean var10 = this.isTransparent(var3, var4, var5 + var1.getXOffset(), var6 + var1.getYOffset(), var7, var8) && var9;
      if (var10) {
         this.createOrExpandSpan(var2, var1, var5, var6);
      }

   }

   private void createOrExpandSpan(List<Span> var1, SpanFacing var2, int var3, int var4) {
      Span var5 = null;

      for(Span var7 : var1) {
         if (var7.getFacing() == var2) {
            int var8 = var2.isHorizontal() ? var4 : var3;
            if (var7.getAnchor() == var8) {
               var5 = var7;
               break;
            }
         }
      }

      int var9 = var2.isHorizontal() ? var4 : var3;
      int var10 = var2.isHorizontal() ? var3 : var4;
      if (var5 == null) {
         var1.add(new Span(var2, var10, var9));
      } else {
         var5.expand(var10);
      }

   }

   private boolean isTransparent(SpriteContents var1, int var2, int var3, int var4, int var5, int var6) {
      return var3 >= 0 && var4 >= 0 && var3 < var5 && var4 < var6 ? var1.isTransparent(var2, var3, var4) : true;
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
