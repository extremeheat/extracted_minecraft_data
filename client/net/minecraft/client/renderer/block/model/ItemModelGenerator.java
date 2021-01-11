package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.vector.Vector3f;

public class ItemModelGenerator {
   public static final List<String> field_178398_a = Lists.newArrayList(new String[]{"layer0", "layer1", "layer2", "layer3", "layer4"});

   public ItemModelGenerator() {
      super();
   }

   public ModelBlock func_178392_a(TextureMap var1, ModelBlock var2) {
      HashMap var3 = Maps.newHashMap();
      ArrayList var4 = Lists.newArrayList();

      for(int var5 = 0; var5 < field_178398_a.size(); ++var5) {
         String var6 = (String)field_178398_a.get(var5);
         if (!var2.func_178300_b(var6)) {
            break;
         }

         String var7 = var2.func_178308_c(var6);
         var3.put(var6, var7);
         TextureAtlasSprite var8 = var1.func_110572_b((new ResourceLocation(var7)).toString());
         var4.addAll(this.func_178394_a(var5, var6, var8));
      }

      if (var4.isEmpty()) {
         return null;
      } else {
         var3.put("particle", var2.func_178300_b("particle") ? var2.func_178308_c("particle") : (String)var3.get("layer0"));
         return new ModelBlock(var4, var3, false, false, var2.func_181682_g());
      }
   }

   private List<BlockPart> func_178394_a(int var1, String var2, TextureAtlasSprite var3) {
      HashMap var4 = Maps.newHashMap();
      var4.put(EnumFacing.SOUTH, new BlockPartFace((EnumFacing)null, var1, var2, new BlockFaceUV(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0)));
      var4.put(EnumFacing.NORTH, new BlockPartFace((EnumFacing)null, var1, var2, new BlockFaceUV(new float[]{16.0F, 0.0F, 0.0F, 16.0F}, 0)));
      ArrayList var5 = Lists.newArrayList();
      var5.add(new BlockPart(new Vector3f(0.0F, 0.0F, 7.5F), new Vector3f(16.0F, 16.0F, 8.5F), var4, (BlockPartRotation)null, true));
      var5.addAll(this.func_178397_a(var3, var2, var1));
      return var5;
   }

   private List<BlockPart> func_178397_a(TextureAtlasSprite var1, String var2, int var3) {
      float var4 = (float)var1.func_94211_a();
      float var5 = (float)var1.func_94216_b();
      ArrayList var6 = Lists.newArrayList();
      Iterator var7 = this.func_178393_a(var1).iterator();

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
         float var17 = 0.0F;
         float var18 = 0.0F;
         float var19 = (float)var8.func_178385_b();
         float var20 = (float)var8.func_178384_c();
         float var21 = (float)var8.func_178381_d();
         ItemModelGenerator.SpanFacing var22 = var8.func_178383_a();
         switch(var22) {
         case UP:
            var13 = var19;
            var9 = var19;
            var11 = var14 = var20 + 1.0F;
            var15 = var21;
            var10 = var21;
            var16 = var21;
            var12 = var21;
            var17 = 16.0F / var4;
            var18 = 16.0F / (var5 - 1.0F);
            break;
         case DOWN:
            var16 = var21;
            var15 = var21;
            var13 = var19;
            var9 = var19;
            var11 = var14 = var20 + 1.0F;
            var10 = var21 + 1.0F;
            var12 = var21 + 1.0F;
            var17 = 16.0F / var4;
            var18 = 16.0F / (var5 - 1.0F);
            break;
         case LEFT:
            var13 = var21;
            var9 = var21;
            var14 = var21;
            var11 = var21;
            var16 = var19;
            var10 = var19;
            var12 = var15 = var20 + 1.0F;
            var17 = 16.0F / (var4 - 1.0F);
            var18 = 16.0F / var5;
            break;
         case RIGHT:
            var14 = var21;
            var13 = var21;
            var9 = var21 + 1.0F;
            var11 = var21 + 1.0F;
            var16 = var19;
            var10 = var19;
            var12 = var15 = var20 + 1.0F;
            var17 = 16.0F / (var4 - 1.0F);
            var18 = 16.0F / var5;
         }

         float var23 = 16.0F / var4;
         float var24 = 16.0F / var5;
         var9 *= var23;
         var11 *= var23;
         var10 *= var24;
         var12 *= var24;
         var10 = 16.0F - var10;
         var12 = 16.0F - var12;
         var13 *= var17;
         var14 *= var17;
         var15 *= var18;
         var16 *= var18;
         HashMap var25 = Maps.newHashMap();
         var25.put(var22.func_178367_a(), new BlockPartFace((EnumFacing)null, var3, var2, new BlockFaceUV(new float[]{var13, var15, var14, var16}, 0)));
         switch(var22) {
         case UP:
            var6.add(new BlockPart(new Vector3f(var9, var10, 7.5F), new Vector3f(var11, var10, 8.5F), var25, (BlockPartRotation)null, true));
            break;
         case DOWN:
            var6.add(new BlockPart(new Vector3f(var9, var12, 7.5F), new Vector3f(var11, var12, 8.5F), var25, (BlockPartRotation)null, true));
            break;
         case LEFT:
            var6.add(new BlockPart(new Vector3f(var9, var10, 7.5F), new Vector3f(var9, var12, 8.5F), var25, (BlockPartRotation)null, true));
            break;
         case RIGHT:
            var6.add(new BlockPart(new Vector3f(var11, var10, 7.5F), new Vector3f(var11, var12, 8.5F), var25, (BlockPartRotation)null, true));
         }
      }

      return var6;
   }

   private List<ItemModelGenerator.Span> func_178393_a(TextureAtlasSprite var1) {
      int var2 = var1.func_94211_a();
      int var3 = var1.func_94216_b();
      ArrayList var4 = Lists.newArrayList();

      for(int var5 = 0; var5 < var1.func_110970_k(); ++var5) {
         int[] var6 = var1.func_147965_a(var5)[0];

         for(int var7 = 0; var7 < var3; ++var7) {
            for(int var8 = 0; var8 < var2; ++var8) {
               boolean var9 = !this.func_178391_a(var6, var8, var7, var2, var3);
               this.func_178396_a(ItemModelGenerator.SpanFacing.UP, var4, var6, var8, var7, var2, var3, var9);
               this.func_178396_a(ItemModelGenerator.SpanFacing.DOWN, var4, var6, var8, var7, var2, var3, var9);
               this.func_178396_a(ItemModelGenerator.SpanFacing.LEFT, var4, var6, var8, var7, var2, var3, var9);
               this.func_178396_a(ItemModelGenerator.SpanFacing.RIGHT, var4, var6, var8, var7, var2, var3, var9);
            }
         }
      }

      return var4;
   }

   private void func_178396_a(ItemModelGenerator.SpanFacing var1, List<ItemModelGenerator.Span> var2, int[] var3, int var4, int var5, int var6, int var7, boolean var8) {
      boolean var9 = this.func_178391_a(var3, var4 + var1.func_178372_b(), var5 + var1.func_178371_c(), var6, var7) && var8;
      if (var9) {
         this.func_178395_a(var2, var1, var4, var5);
      }

   }

   private void func_178395_a(List<ItemModelGenerator.Span> var1, ItemModelGenerator.SpanFacing var2, int var3, int var4) {
      ItemModelGenerator.Span var5 = null;
      Iterator var6 = var1.iterator();

      while(var6.hasNext()) {
         ItemModelGenerator.Span var7 = (ItemModelGenerator.Span)var6.next();
         if (var7.func_178383_a() == var2) {
            int var8 = var2.func_178369_d() ? var4 : var3;
            if (var7.func_178381_d() == var8) {
               var5 = var7;
               break;
            }
         }
      }

      int var9 = var2.func_178369_d() ? var4 : var3;
      int var10 = var2.func_178369_d() ? var3 : var4;
      if (var5 == null) {
         var1.add(new ItemModelGenerator.Span(var2, var10, var9));
      } else {
         var5.func_178382_a(var10);
      }

   }

   private boolean func_178391_a(int[] var1, int var2, int var3, int var4, int var5) {
      if (var2 >= 0 && var3 >= 0 && var2 < var4 && var3 < var5) {
         return (var1[var3 * var4 + var2] >> 24 & 255) == 0;
      } else {
         return true;
      }
   }

   static class Span {
      private final ItemModelGenerator.SpanFacing field_178389_a;
      private int field_178387_b;
      private int field_178388_c;
      private final int field_178386_d;

      public Span(ItemModelGenerator.SpanFacing var1, int var2, int var3) {
         super();
         this.field_178389_a = var1;
         this.field_178387_b = var2;
         this.field_178388_c = var2;
         this.field_178386_d = var3;
      }

      public void func_178382_a(int var1) {
         if (var1 < this.field_178387_b) {
            this.field_178387_b = var1;
         } else if (var1 > this.field_178388_c) {
            this.field_178388_c = var1;
         }

      }

      public ItemModelGenerator.SpanFacing func_178383_a() {
         return this.field_178389_a;
      }

      public int func_178385_b() {
         return this.field_178387_b;
      }

      public int func_178384_c() {
         return this.field_178388_c;
      }

      public int func_178381_d() {
         return this.field_178386_d;
      }
   }

   static enum SpanFacing {
      UP(EnumFacing.UP, 0, -1),
      DOWN(EnumFacing.DOWN, 0, 1),
      LEFT(EnumFacing.EAST, -1, 0),
      RIGHT(EnumFacing.WEST, 1, 0);

      private final EnumFacing field_178376_e;
      private final int field_178373_f;
      private final int field_178374_g;

      private SpanFacing(EnumFacing var3, int var4, int var5) {
         this.field_178376_e = var3;
         this.field_178373_f = var4;
         this.field_178374_g = var5;
      }

      public EnumFacing func_178367_a() {
         return this.field_178376_e;
      }

      public int func_178372_b() {
         return this.field_178373_f;
      }

      public int func_178371_c() {
         return this.field_178374_g;
      }

      private boolean func_178369_d() {
         return this == DOWN || this == UP;
      }
   }
}
