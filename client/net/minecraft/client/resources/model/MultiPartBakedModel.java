package net.minecraft.client.resources.model;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;

public class MultiPartBakedModel implements BakedModel {
   private final List<Pair<Predicate<BlockState>, BakedModel>> selectors;
   protected final boolean hasAmbientOcclusion;
   protected final boolean isGui3d;
   protected final boolean usesBlockLight;
   protected final TextureAtlasSprite particleIcon;
   protected final ItemTransforms transforms;
   protected final ItemOverrides overrides;
   private final Map<BlockState, BitSet> selectorCache = new Reference2ObjectOpenHashMap();

   public MultiPartBakedModel(List<Pair<Predicate<BlockState>, BakedModel>> var1) {
      super();
      this.selectors = var1;
      BakedModel var2 = (BakedModel)((Pair)var1.iterator().next()).getRight();
      this.hasAmbientOcclusion = var2.useAmbientOcclusion();
      this.isGui3d = var2.isGui3d();
      this.usesBlockLight = var2.usesBlockLight();
      this.particleIcon = var2.getParticleIcon();
      this.transforms = var2.getTransforms();
      this.overrides = var2.getOverrides();
   }

   public List<BakedQuad> getQuads(@Nullable BlockState var1, @Nullable Direction var2, RandomSource var3) {
      if (var1 == null) {
         return Collections.emptyList();
      } else {
         BitSet var4 = (BitSet)this.selectorCache.get(var1);
         if (var4 == null) {
            var4 = new BitSet();

            for(int var5 = 0; var5 < this.selectors.size(); ++var5) {
               Pair var6 = (Pair)this.selectors.get(var5);
               if (((Predicate)var6.getLeft()).test(var1)) {
                  var4.set(var5);
               }
            }

            this.selectorCache.put(var1, var4);
         }

         ArrayList var9 = Lists.newArrayList();
         long var10 = var3.nextLong();

         for(int var8 = 0; var8 < var4.length(); ++var8) {
            if (var4.get(var8)) {
               var9.addAll(((BakedModel)((Pair)this.selectors.get(var8)).getRight()).getQuads(var1, var2, RandomSource.create(var10)));
            }
         }

         return var9;
      }
   }

   public boolean useAmbientOcclusion() {
      return this.hasAmbientOcclusion;
   }

   public boolean isGui3d() {
      return this.isGui3d;
   }

   public boolean usesBlockLight() {
      return this.usesBlockLight;
   }

   public boolean isCustomRenderer() {
      return false;
   }

   public TextureAtlasSprite getParticleIcon() {
      return this.particleIcon;
   }

   public ItemTransforms getTransforms() {
      return this.transforms;
   }

   public ItemOverrides getOverrides() {
      return this.overrides;
   }

   public static class Builder {
      private final List<Pair<Predicate<BlockState>, BakedModel>> selectors = Lists.newArrayList();

      public Builder() {
         super();
      }

      public void add(Predicate<BlockState> var1, BakedModel var2) {
         this.selectors.add(Pair.of(var1, var2));
      }

      public BakedModel build() {
         return new MultiPartBakedModel(this.selectors);
      }
   }
}
