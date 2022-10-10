package net.minecraft.client.renderer.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

public class SimpleBakedModel implements IBakedModel {
   protected final List<BakedQuad> field_177563_a;
   protected final Map<EnumFacing, List<BakedQuad>> field_177561_b;
   protected final boolean field_177562_c;
   protected final boolean field_177559_d;
   protected final TextureAtlasSprite field_177560_e;
   protected final ItemCameraTransforms field_177558_f;
   protected final ItemOverrideList field_188620_g;

   public SimpleBakedModel(List<BakedQuad> var1, Map<EnumFacing, List<BakedQuad>> var2, boolean var3, boolean var4, TextureAtlasSprite var5, ItemCameraTransforms var6, ItemOverrideList var7) {
      super();
      this.field_177563_a = var1;
      this.field_177561_b = var2;
      this.field_177562_c = var3;
      this.field_177559_d = var4;
      this.field_177560_e = var5;
      this.field_177558_f = var6;
      this.field_188620_g = var7;
   }

   public List<BakedQuad> func_200117_a(@Nullable IBlockState var1, @Nullable EnumFacing var2, Random var3) {
      return var2 == null ? this.field_177563_a : (List)this.field_177561_b.get(var2);
   }

   public boolean func_177555_b() {
      return this.field_177562_c;
   }

   public boolean func_177556_c() {
      return this.field_177559_d;
   }

   public boolean func_188618_c() {
      return false;
   }

   public TextureAtlasSprite func_177554_e() {
      return this.field_177560_e;
   }

   public ItemCameraTransforms func_177552_f() {
      return this.field_177558_f;
   }

   public ItemOverrideList func_188617_f() {
      return this.field_188620_g;
   }

   public static class Builder {
      private final List<BakedQuad> field_177656_a;
      private final Map<EnumFacing, List<BakedQuad>> field_177654_b;
      private final ItemOverrideList field_188646_c;
      private final boolean field_177655_c;
      private TextureAtlasSprite field_177652_d;
      private final boolean field_177653_e;
      private final ItemCameraTransforms field_177651_f;

      public Builder(ModelBlock var1, ItemOverrideList var2) {
         this(var1.func_178309_b(), var1.func_178311_c(), var1.func_181682_g(), var2);
      }

      public Builder(IBlockState var1, IBakedModel var2, TextureAtlasSprite var3, Random var4, long var5) {
         this(var2.func_177555_b(), var2.func_177556_c(), var2.func_177552_f(), var2.func_188617_f());
         this.field_177652_d = var2.func_177554_e();
         EnumFacing[] var7 = EnumFacing.values();
         int var8 = var7.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            EnumFacing var10 = var7[var9];
            var4.setSeed(var5);
            Iterator var11 = var2.func_200117_a(var1, var10, var4).iterator();

            while(var11.hasNext()) {
               BakedQuad var12 = (BakedQuad)var11.next();
               this.func_177650_a(var10, new BakedQuadRetextured(var12, var3));
            }
         }

         var4.setSeed(var5);
         Iterator var13 = var2.func_200117_a(var1, (EnumFacing)null, var4).iterator();

         while(var13.hasNext()) {
            BakedQuad var14 = (BakedQuad)var13.next();
            this.func_177648_a(new BakedQuadRetextured(var14, var3));
         }

      }

      private Builder(boolean var1, boolean var2, ItemCameraTransforms var3, ItemOverrideList var4) {
         super();
         this.field_177656_a = Lists.newArrayList();
         this.field_177654_b = Maps.newEnumMap(EnumFacing.class);
         EnumFacing[] var5 = EnumFacing.values();
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            EnumFacing var8 = var5[var7];
            this.field_177654_b.put(var8, Lists.newArrayList());
         }

         this.field_188646_c = var4;
         this.field_177655_c = var1;
         this.field_177653_e = var2;
         this.field_177651_f = var3;
      }

      public SimpleBakedModel.Builder func_177650_a(EnumFacing var1, BakedQuad var2) {
         ((List)this.field_177654_b.get(var1)).add(var2);
         return this;
      }

      public SimpleBakedModel.Builder func_177648_a(BakedQuad var1) {
         this.field_177656_a.add(var1);
         return this;
      }

      public SimpleBakedModel.Builder func_177646_a(TextureAtlasSprite var1) {
         this.field_177652_d = var1;
         return this;
      }

      public IBakedModel func_177645_b() {
         if (this.field_177652_d == null) {
            throw new RuntimeException("Missing particle!");
         } else {
            return new SimpleBakedModel(this.field_177656_a, this.field_177654_b, this.field_177655_c, this.field_177653_e, this.field_177652_d, this.field_177651_f, this.field_188646_c);
         }
      }
   }
}
