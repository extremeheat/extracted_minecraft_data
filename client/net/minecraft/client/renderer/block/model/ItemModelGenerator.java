package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.math.Vector3f;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class ItemModelGenerator {
   public static final List<String> LAYERS = Lists.newArrayList(new String[]{"layer0", "layer1", "layer2", "layer3", "layer4"});

   public ItemModelGenerator() {
      super();
   }

   public BlockModel generateBlockModel(Function<ResourceLocation, TextureAtlasSprite> var1, BlockModel var2) {
      HashMap var3 = Maps.newHashMap();
      ArrayList var4 = Lists.newArrayList();

      for(int var5 = 0; var5 < LAYERS.size(); ++var5) {
         String var6 = (String)LAYERS.get(var5);
         if (!var2.hasTexture(var6)) {
            break;
         }

         String var7 = var2.getTexture(var6);
         var3.put(var6, var7);
         TextureAtlasSprite var8 = (TextureAtlasSprite)var1.apply(new ResourceLocation(var7));
         var4.addAll(this.processFrames(var5, var6, var8));
      }

      var3.put("particle", var2.hasTexture("particle") ? var2.getTexture("particle") : (String)var3.get("layer0"));
      BlockModel var9 = new BlockModel((ResourceLocation)null, var4, var3, false, false, var2.getTransforms(), var2.getOverrides());
      var9.name = var2.name;
      return var9;
   }

   private List<BlockElement> processFrames(int var1, String var2, TextureAtlasSprite var3) {
      HashMap var4 = Maps.newHashMap();
      var4.put(Direction.SOUTH, new BlockElementFace((Direction)null, var1, var2, new BlockFaceUV(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0)));
      var4.put(Direction.NORTH, new BlockElementFace((Direction)null, var1, var2, new BlockFaceUV(new float[]{16.0F, 0.0F, 0.0F, 16.0F}, 0)));
      ArrayList var5 = Lists.newArrayList();
      var5.add(new BlockElement(new Vector3f(0.0F, 0.0F, 7.5F), new Vector3f(16.0F, 16.0F, 8.5F), var4, (BlockElementRotation)null, true));
      var5.addAll(this.createSideElements(var3, var2, var1));
      return var5;
   }

   private List<BlockElement> createSideElements(TextureAtlasSprite var1, String var2, int var3) {
      float var4 = (float)var1.getWidth();
      float var5 = (float)var1.getHeight();
      ArrayList var6 = Lists.newArrayList();
      Iterator var7 = this.getSpans(var1).iterator();

      while(var7.hasNext()) {
         ItemModelGenerator.Span var8 = (ItemModelGenerator.Span)var7.next();
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
         ItemModelGenerator.SpanFacing var22 = var8.getFacing();
         switch(var22) {
         case UP:
            var13 = var19;
            var9 = var19;
            var11 = var14 = var20 + 1.0F;
            var15 = var21;
            var10 = var21;
            var12 = var21;
            var16 = var21 + 1.0F;
            break;
         case DOWN:
            var15 = var21;
            var16 = var21 + 1.0F;
            var13 = var19;
            var9 = var19;
            var11 = var14 = var20 + 1.0F;
            var10 = var21 + 1.0F;
            var12 = var21 + 1.0F;
            break;
         case LEFT:
            var13 = var21;
            var9 = var21;
            var11 = var21;
            var14 = var21 + 1.0F;
            var16 = var19;
            var10 = var19;
            var12 = var15 = var20 + 1.0F;
            break;
         case RIGHT:
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
         HashMap var23 = Maps.newHashMap();
         var23.put(var22.getDirection(), new BlockElementFace((Direction)null, var3, var2, new BlockFaceUV(new float[]{var13, var15, var14, var16}, 0)));
         switch(var22) {
         case UP:
            var6.add(new BlockElement(new Vector3f(var9, var10, 7.5F), new Vector3f(var11, var10, 8.5F), var23, (BlockElementRotation)null, true));
            break;
         case DOWN:
            var6.add(new BlockElement(new Vector3f(var9, var12, 7.5F), new Vector3f(var11, var12, 8.5F), var23, (BlockElementRotation)null, true));
            break;
         case LEFT:
            var6.add(new BlockElement(new Vector3f(var9, var10, 7.5F), new Vector3f(var9, var12, 8.5F), var23, (BlockElementRotation)null, true));
            break;
         case RIGHT:
            var6.add(new BlockElement(new Vector3f(var11, var10, 7.5F), new Vector3f(var11, var12, 8.5F), var23, (BlockElementRotation)null, true));
         }
      }

      return var6;
   }

   private List<ItemModelGenerator.Span> getSpans(TextureAtlasSprite var1) {
      int var2 = var1.getWidth();
      int var3 = var1.getHeight();
      ArrayList var4 = Lists.newArrayList();

      for(int var5 = 0; var5 < var1.getFrameCount(); ++var5) {
         for(int var6 = 0; var6 < var3; ++var6) {
            for(int var7 = 0; var7 < var2; ++var7) {
               boolean var8 = !this.isTransparent(var1, var5, var7, var6, var2, var3);
               this.checkTransition(ItemModelGenerator.SpanFacing.UP, var4, var1, var5, var7, var6, var2, var3, var8);
               this.checkTransition(ItemModelGenerator.SpanFacing.DOWN, var4, var1, var5, var7, var6, var2, var3, var8);
               this.checkTransition(ItemModelGenerator.SpanFacing.LEFT, var4, var1, var5, var7, var6, var2, var3, var8);
               this.checkTransition(ItemModelGenerator.SpanFacing.RIGHT, var4, var1, var5, var7, var6, var2, var3, var8);
            }
         }
      }

      return var4;
   }

   private void checkTransition(ItemModelGenerator.SpanFacing var1, List<ItemModelGenerator.Span> var2, TextureAtlasSprite var3, int var4, int var5, int var6, int var7, int var8, boolean var9) {
      boolean var10 = this.isTransparent(var3, var4, var5 + var1.getXOffset(), var6 + var1.getYOffset(), var7, var8) && var9;
      if (var10) {
         this.createOrExpandSpan(var2, var1, var5, var6);
      }

   }

   private void createOrExpandSpan(List<ItemModelGenerator.Span> var1, ItemModelGenerator.SpanFacing var2, int var3, int var4) {
      ItemModelGenerator.Span var5 = null;
      Iterator var6 = var1.iterator();

      while(var6.hasNext()) {
         ItemModelGenerator.Span var7 = (ItemModelGenerator.Span)var6.next();
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
         var1.add(new ItemModelGenerator.Span(var2, var10, var9));
      } else {
         var5.expand(var10);
      }

   }

   private boolean isTransparent(TextureAtlasSprite var1, int var2, int var3, int var4, int var5, int var6) {
      return var3 >= 0 && var4 >= 0 && var3 < var5 && var4 < var6 ? var1.isTransparent(var2, var3, var4) : true;
   }

   static class Span {
      private final ItemModelGenerator.SpanFacing facing;
      private int min;
      private int max;
      private final int anchor;

      public Span(ItemModelGenerator.SpanFacing var1, int var2, int var3) {
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

      public ItemModelGenerator.SpanFacing getFacing() {
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

   static enum SpanFacing {
      UP(Direction.UP, 0, -1),
      DOWN(Direction.DOWN, 0, 1),
      LEFT(Direction.EAST, -1, 0),
      RIGHT(Direction.WEST, 1, 0);

      private final Direction direction;
      private final int xOffset;
      private final int yOffset;

      private SpanFacing(Direction var3, int var4, int var5) {
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

      private boolean isHorizontal() {
         return this == DOWN || this == UP;
      }
   }
}
