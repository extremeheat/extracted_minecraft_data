package net.minecraft.client.resources.model;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BreakingFour;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

public class SimpleBakedModel implements IBakedModel {
   protected final List<BakedQuad> field_177563_a;
   protected final List<List<BakedQuad>> field_177561_b;
   protected final boolean field_177562_c;
   protected final boolean field_177559_d;
   protected final TextureAtlasSprite field_177560_e;
   protected final ItemCameraTransforms field_177558_f;

   public SimpleBakedModel(List<BakedQuad> var1, List<List<BakedQuad>> var2, boolean var3, boolean var4, TextureAtlasSprite var5, ItemCameraTransforms var6) {
      super();
      this.field_177563_a = var1;
      this.field_177561_b = var2;
      this.field_177562_c = var3;
      this.field_177559_d = var4;
      this.field_177560_e = var5;
      this.field_177558_f = var6;
   }

   public List<BakedQuad> func_177551_a(EnumFacing var1) {
      return (List)this.field_177561_b.get(var1.ordinal());
   }

   public List<BakedQuad> func_177550_a() {
      return this.field_177563_a;
   }

   public boolean func_177555_b() {
      return this.field_177562_c;
   }

   public boolean func_177556_c() {
      return this.field_177559_d;
   }

   public boolean func_177553_d() {
      return false;
   }

   public TextureAtlasSprite func_177554_e() {
      return this.field_177560_e;
   }

   public ItemCameraTransforms func_177552_f() {
      return this.field_177558_f;
   }

   public static class Builder {
      private final List<BakedQuad> field_177656_a;
      private final List<List<BakedQuad>> field_177654_b;
      private final boolean field_177655_c;
      private TextureAtlasSprite field_177652_d;
      private boolean field_177653_e;
      private ItemCameraTransforms field_177651_f;

      public Builder(ModelBlock var1) {
         this(var1.func_178309_b(), var1.func_178311_c(), var1.func_181682_g());
      }

      public Builder(IBakedModel var1, TextureAtlasSprite var2) {
         this(var1.func_177555_b(), var1.func_177556_c(), var1.func_177552_f());
         this.field_177652_d = var1.func_177554_e();
         EnumFacing[] var3 = EnumFacing.values();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            EnumFacing var6 = var3[var5];
            this.func_177649_a(var1, var2, var6);
         }

         this.func_177647_a(var1, var2);
      }

      private void func_177649_a(IBakedModel var1, TextureAtlasSprite var2, EnumFacing var3) {
         Iterator var4 = var1.func_177551_a(var3).iterator();

         while(var4.hasNext()) {
            BakedQuad var5 = (BakedQuad)var4.next();
            this.func_177650_a(var3, new BreakingFour(var5, var2));
         }

      }

      private void func_177647_a(IBakedModel var1, TextureAtlasSprite var2) {
         Iterator var3 = var1.func_177550_a().iterator();

         while(var3.hasNext()) {
            BakedQuad var4 = (BakedQuad)var3.next();
            this.func_177648_a(new BreakingFour(var4, var2));
         }

      }

      private Builder(boolean var1, boolean var2, ItemCameraTransforms var3) {
         super();
         this.field_177656_a = Lists.newArrayList();
         this.field_177654_b = Lists.newArrayListWithCapacity(6);
         EnumFacing[] var4 = EnumFacing.values();
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            EnumFacing var10000 = var4[var6];
            this.field_177654_b.add(Lists.newArrayList());
         }

         this.field_177655_c = var1;
         this.field_177653_e = var2;
         this.field_177651_f = var3;
      }

      public SimpleBakedModel.Builder func_177650_a(EnumFacing var1, BakedQuad var2) {
         ((List)this.field_177654_b.get(var1.ordinal())).add(var2);
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
            return new SimpleBakedModel(this.field_177656_a, this.field_177654_b, this.field_177655_c, this.field_177653_e, this.field_177652_d, this.field_177651_f);
         }
      }
   }
}
