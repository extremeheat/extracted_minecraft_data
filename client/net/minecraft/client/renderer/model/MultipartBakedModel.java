package net.minecraft.client.renderer.model;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Util;
import org.apache.commons.lang3.tuple.Pair;

public class MultipartBakedModel implements IBakedModel {
   private final List<Pair<Predicate<IBlockState>, IBakedModel>> field_188626_f;
   protected final boolean field_188621_a;
   protected final boolean field_188622_b;
   protected final TextureAtlasSprite field_188623_c;
   protected final ItemCameraTransforms field_188624_d;
   protected final ItemOverrideList field_188625_e;
   private final Map<IBlockState, BitSet> field_210277_g = new Object2ObjectOpenCustomHashMap(Util.func_212443_g());

   public MultipartBakedModel(List<Pair<Predicate<IBlockState>, IBakedModel>> var1) {
      super();
      this.field_188626_f = var1;
      IBakedModel var2 = (IBakedModel)((Pair)var1.iterator().next()).getRight();
      this.field_188621_a = var2.func_177555_b();
      this.field_188622_b = var2.func_177556_c();
      this.field_188623_c = var2.func_177554_e();
      this.field_188624_d = var2.func_177552_f();
      this.field_188625_e = var2.func_188617_f();
   }

   public List<BakedQuad> func_200117_a(@Nullable IBlockState var1, @Nullable EnumFacing var2, Random var3) {
      if (var1 == null) {
         return Collections.emptyList();
      } else {
         BitSet var4 = (BitSet)this.field_210277_g.get(var1);
         if (var4 == null) {
            var4 = new BitSet();

            for(int var5 = 0; var5 < this.field_188626_f.size(); ++var5) {
               Pair var6 = (Pair)this.field_188626_f.get(var5);
               if (((Predicate)var6.getLeft()).test(var1)) {
                  var4.set(var5);
               }
            }

            this.field_210277_g.put(var1, var4);
         }

         ArrayList var9 = Lists.newArrayList();
         long var10 = var3.nextLong();

         for(int var8 = 0; var8 < var4.length(); ++var8) {
            if (var4.get(var8)) {
               var9.addAll(((IBakedModel)((Pair)this.field_188626_f.get(var8)).getRight()).func_200117_a(var1, var2, new Random(var10)));
            }
         }

         return var9;
      }
   }

   public boolean func_177555_b() {
      return this.field_188621_a;
   }

   public boolean func_177556_c() {
      return this.field_188622_b;
   }

   public boolean func_188618_c() {
      return false;
   }

   public TextureAtlasSprite func_177554_e() {
      return this.field_188623_c;
   }

   public ItemCameraTransforms func_177552_f() {
      return this.field_188624_d;
   }

   public ItemOverrideList func_188617_f() {
      return this.field_188625_e;
   }

   public static class Builder {
      private final List<Pair<Predicate<IBlockState>, IBakedModel>> field_188649_a = Lists.newArrayList();

      public Builder() {
         super();
      }

      public void func_188648_a(Predicate<IBlockState> var1, IBakedModel var2) {
         this.field_188649_a.add(Pair.of(var1, var2));
      }

      public IBakedModel func_188647_a() {
         return new MultipartBakedModel(this.field_188649_a);
      }
   }
}
