package net.minecraft.client.resources.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
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
   protected final boolean usesBlockLight;
   protected final TextureAtlasSprite particleIcon;
   protected final ItemTransforms transforms;
   protected final ItemOverrides overrides;

   public SimpleBakedModel(List<BakedQuad> var1, Map<Direction, List<BakedQuad>> var2, boolean var3, boolean var4, boolean var5, TextureAtlasSprite var6, ItemTransforms var7, ItemOverrides var8) {
      super();
      this.unculledFaces = var1;
      this.culledFaces = var2;
      this.hasAmbientOcclusion = var3;
      this.isGui3d = var5;
      this.usesBlockLight = var4;
      this.particleIcon = var6;
      this.transforms = var7;
      this.overrides = var8;
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
      private final List<BakedQuad> unculledFaces;
      private final Map<Direction, List<BakedQuad>> culledFaces;
      private final ItemOverrides overrides;
      private final boolean hasAmbientOcclusion;
      private TextureAtlasSprite particleIcon;
      private final boolean usesBlockLight;
      private final boolean isGui3d;
      private final ItemTransforms transforms;

      public Builder(BlockModel var1, ItemOverrides var2, boolean var3) {
         this(var1.hasAmbientOcclusion(), var1.getGuiLight().lightLikeBlock(), var3, var1.getTransforms(), var2);
      }

      private Builder(boolean var1, boolean var2, boolean var3, ItemTransforms var4, ItemOverrides var5) {
         super();
         this.unculledFaces = Lists.newArrayList();
         this.culledFaces = Maps.newEnumMap(Direction.class);
         Direction[] var6 = Direction.values();
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            Direction var9 = var6[var8];
            this.culledFaces.put(var9, Lists.newArrayList());
         }

         this.overrides = var5;
         this.hasAmbientOcclusion = var1;
         this.usesBlockLight = var2;
         this.isGui3d = var3;
         this.transforms = var4;
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

      public SimpleBakedModel.Builder item() {
         return this;
      }

      public BakedModel build() {
         if (this.particleIcon == null) {
            throw new RuntimeException("Missing particle!");
         } else {
            return new SimpleBakedModel(this.unculledFaces, this.culledFaces, this.hasAmbientOcclusion, this.usesBlockLight, this.isGui3d, this.particleIcon, this.transforms, this.overrides);
         }
      }
   }
}
