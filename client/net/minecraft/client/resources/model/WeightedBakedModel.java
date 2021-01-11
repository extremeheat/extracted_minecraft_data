package net.minecraft.client.resources.model;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.WeightedRandom;

public class WeightedBakedModel implements IBakedModel {
   private final int field_177567_a;
   private final List<WeightedBakedModel.MyWeighedRandomItem> field_177565_b;
   private final IBakedModel field_177566_c;

   public WeightedBakedModel(List<WeightedBakedModel.MyWeighedRandomItem> var1) {
      super();
      this.field_177565_b = var1;
      this.field_177567_a = WeightedRandom.func_76272_a(var1);
      this.field_177566_c = ((WeightedBakedModel.MyWeighedRandomItem)var1.get(0)).field_177636_b;
   }

   public List<BakedQuad> func_177551_a(EnumFacing var1) {
      return this.field_177566_c.func_177551_a(var1);
   }

   public List<BakedQuad> func_177550_a() {
      return this.field_177566_c.func_177550_a();
   }

   public boolean func_177555_b() {
      return this.field_177566_c.func_177555_b();
   }

   public boolean func_177556_c() {
      return this.field_177566_c.func_177556_c();
   }

   public boolean func_177553_d() {
      return this.field_177566_c.func_177553_d();
   }

   public TextureAtlasSprite func_177554_e() {
      return this.field_177566_c.func_177554_e();
   }

   public ItemCameraTransforms func_177552_f() {
      return this.field_177566_c.func_177552_f();
   }

   public IBakedModel func_177564_a(long var1) {
      return ((WeightedBakedModel.MyWeighedRandomItem)WeightedRandom.func_180166_a(this.field_177565_b, Math.abs((int)var1 >> 16) % this.field_177567_a)).field_177636_b;
   }

   static class MyWeighedRandomItem extends WeightedRandom.Item implements Comparable<WeightedBakedModel.MyWeighedRandomItem> {
      protected final IBakedModel field_177636_b;

      public MyWeighedRandomItem(IBakedModel var1, int var2) {
         super(var2);
         this.field_177636_b = var1;
      }

      public int compareTo(WeightedBakedModel.MyWeighedRandomItem var1) {
         return ComparisonChain.start().compare(var1.field_76292_a, this.field_76292_a).compare(this.func_177635_a(), var1.func_177635_a()).result();
      }

      protected int func_177635_a() {
         int var1 = this.field_177636_b.func_177550_a().size();
         EnumFacing[] var2 = EnumFacing.values();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            EnumFacing var5 = var2[var4];
            var1 += this.field_177636_b.func_177551_a(var5).size();
         }

         return var1;
      }

      public String toString() {
         return "MyWeighedRandomItem{weight=" + this.field_76292_a + ", model=" + this.field_177636_b + '}';
      }

      // $FF: synthetic method
      public int compareTo(Object var1) {
         return this.compareTo((WeightedBakedModel.MyWeighedRandomItem)var1);
      }
   }

   public static class Builder {
      private List<WeightedBakedModel.MyWeighedRandomItem> field_177678_a = Lists.newArrayList();

      public Builder() {
         super();
      }

      public WeightedBakedModel.Builder func_177677_a(IBakedModel var1, int var2) {
         this.field_177678_a.add(new WeightedBakedModel.MyWeighedRandomItem(var1, var2));
         return this;
      }

      public WeightedBakedModel func_177676_a() {
         Collections.sort(this.field_177678_a);
         return new WeightedBakedModel(this.field_177678_a);
      }

      public IBakedModel func_177675_b() {
         return ((WeightedBakedModel.MyWeighedRandomItem)this.field_177678_a.get(0)).field_177636_b;
      }
   }
}
