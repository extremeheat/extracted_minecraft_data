package net.minecraft.client.resources.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.BreakingQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class SimpleBakedModel implements BakedModel {
   protected final List<BakedQuad> unculledFaces;
   protected final Map<Direction, List<BakedQuad>> culledFaces;
   protected final boolean hasAmbientOcclusion;
   protected final boolean isGui3d;
   protected final TextureAtlasSprite particleIcon;
   protected final ItemTransforms transforms;
   protected final ItemOverrides overrides;

   public SimpleBakedModel(List<BakedQuad> var1, Map<Direction, List<BakedQuad>> var2, boolean var3, boolean var4, TextureAtlasSprite var5, ItemTransforms var6, ItemOverrides var7) {
      super();
      this.unculledFaces = var1;
      this.culledFaces = var2;
      this.hasAmbientOcclusion = var3;
      this.isGui3d = var4;
      this.particleIcon = var5;
      this.transforms = var6;
      this.overrides = var7;
   }

   public List<BakedQuad> getQuads(@Nullable BlockState var1, @Nullable Direction var2, Random var3) {
      return var2 == null ? this.unculledFaces : (List)this.culledFaces.get(var2);
   }

   public boolean useAmbientOcclusion() {
      return this.hasAmbientOcclusion;
   }

   public boolean isGui3d() {
      return this.isGui3d;
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
      private final List<BakedQuad> unculledFaces;
      private final Map<Direction, List<BakedQuad>> culledFaces;
      private final ItemOverrides overrides;
      private final boolean hasAmbientOcclusion;
      private TextureAtlasSprite particleIcon;
      private final boolean isGui3d;
      private final ItemTransforms transforms;

      public Builder(BlockModel var1, ItemOverrides var2) {
         this(var1.hasAmbientOcclusion(), var1.isGui3d(), var1.getTransforms(), var2);
      }

      public Builder(BlockState var1, BakedModel var2, TextureAtlasSprite var3, Random var4, long var5) {
         this(var2.useAmbientOcclusion(), var2.isGui3d(), var2.getTransforms(), var2.getOverrides());
         this.particleIcon = var2.getParticleIcon();
         Direction[] var7 = Direction.values();
         int var8 = var7.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            Direction var10 = var7[var9];
            var4.setSeed(var5);
            Iterator var11 = var2.getQuads(var1, var10, var4).iterator();

            while(var11.hasNext()) {
               BakedQuad var12 = (BakedQuad)var11.next();
               this.addCulledFace(var10, new BreakingQuad(var12, var3));
            }
         }

         var4.setSeed(var5);
         Iterator var13 = var2.getQuads(var1, (Direction)null, var4).iterator();

         while(var13.hasNext()) {
            BakedQuad var14 = (BakedQuad)var13.next();
            this.addUnculledFace(new BreakingQuad(var14, var3));
         }

      }

      private Builder(boolean var1, boolean var2, ItemTransforms var3, ItemOverrides var4) {
         super();
         this.unculledFaces = Lists.newArrayList();
         this.culledFaces = Maps.newEnumMap(Direction.class);
         Direction[] var5 = Direction.values();
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Direction var8 = var5[var7];
            this.culledFaces.put(var8, Lists.newArrayList());
         }

         this.overrides = var4;
         this.hasAmbientOcclusion = var1;
         this.isGui3d = var2;
         this.transforms = var3;
      }

      public SimpleBakedModel.Builder addCulledFace(Direction var1, BakedQuad var2) {
         ((List)this.culledFaces.get(var1)).add(var2);
         return this;
      }

      public SimpleBakedModel.Builder addUnculledFace(BakedQuad var1) {
         this.unculledFaces.add(var1);
         return this;
      }

      public SimpleBakedModel.Builder particle(TextureAtlasSprite var1) {
         this.particleIcon = var1;
         return this;
      }

      public BakedModel build() {
         if (this.particleIcon == null) {
            throw new RuntimeException("Missing particle!");
         } else {
            return new SimpleBakedModel(this.unculledFaces, this.culledFaces, this.hasAmbientOcclusion, this.isGui3d, this.particleIcon, this.transforms, this.overrides);
         }
      }
   }
}
