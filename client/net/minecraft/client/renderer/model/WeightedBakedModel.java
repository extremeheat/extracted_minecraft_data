package net.minecraft.client.renderer.model;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.WeightedRandom;

public class WeightedBakedModel implements IBakedModel {
   private final int field_177567_a;
   private final List<WeightedBakedModel.WeightedModel> field_177565_b;
   private final IBakedModel field_177566_c;

   public WeightedBakedModel(List<WeightedBakedModel.WeightedModel> var1) {
      super();
      this.field_177565_b = var1;
      this.field_177567_a = WeightedRandom.func_76272_a(var1);
      this.field_177566_c = ((WeightedBakedModel.WeightedModel)var1.get(0)).field_185281_b;
   }

   public List<BakedQuad> func_200117_a(@Nullable IBlockState var1, @Nullable EnumFacing var2, Random var3) {
      return ((WeightedBakedModel.WeightedModel)WeightedRandom.func_180166_a(this.field_177565_b, Math.abs((int)var3.nextLong()) % this.field_177567_a)).field_185281_b.func_200117_a(var1, var2, var3);
   }

   public boolean func_177555_b() {
      return this.field_177566_c.func_177555_b();
   }

   public boolean func_177556_c() {
      return this.field_177566_c.func_177556_c();
   }

   public boolean func_188618_c() {
      return this.field_177566_c.func_188618_c();
   }

   public TextureAtlasSprite func_177554_e() {
      return this.field_177566_c.func_177554_e();
   }

   public ItemCameraTransforms func_177552_f() {
      return this.field_177566_c.func_177552_f();
   }

   public ItemOverrideList func_188617_f() {
      return this.field_177566_c.func_188617_f();
   }

   static class WeightedModel extends WeightedRandom.Item {
      protected final IBakedModel field_185281_b;

      public WeightedModel(IBakedModel var1, int var2) {
         super(var2);
         this.field_185281_b = var1;
      }
   }

   public static class Builder {
      private final List<WeightedBakedModel.WeightedModel> field_177678_a = Lists.newArrayList();

      public Builder() {
         super();
      }

      public WeightedBakedModel.Builder func_177677_a(@Nullable IBakedModel var1, int var2) {
         if (var1 != null) {
            this.field_177678_a.add(new WeightedBakedModel.WeightedModel(var1, var2));
         }

         return this;
      }

      @Nullable
      public IBakedModel func_209614_a() {
         if (this.field_177678_a.isEmpty()) {
            return null;
         } else {
            return (IBakedModel)(this.field_177678_a.size() == 1 ? ((WeightedBakedModel.WeightedModel)this.field_177678_a.get(0)).field_185281_b : new WeightedBakedModel(this.field_177678_a));
         }
      }
   }
}
