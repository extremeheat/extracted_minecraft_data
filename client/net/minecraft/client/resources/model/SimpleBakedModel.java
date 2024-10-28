package net.minecraft.client.resources.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class SimpleBakedModel implements BakedModel {
   protected final List<BakedQuad> unculledFaces;
   protected final Map<Direction, List<BakedQuad>> culledFaces;
   protected final boolean hasAmbientOcclusion;
   protected final boolean isGui3d;
   protected final boolean usesBlockLight;
   protected final TextureAtlasSprite particleIcon;
   protected final ItemTransforms transforms;

   public SimpleBakedModel(List<BakedQuad> var1, Map<Direction, List<BakedQuad>> var2, boolean var3, boolean var4, boolean var5, TextureAtlasSprite var6, ItemTransforms var7) {
      super();
      this.unculledFaces = var1;
      this.culledFaces = var2;
      this.hasAmbientOcclusion = var3;
      this.isGui3d = var5;
      this.usesBlockLight = var4;
      this.particleIcon = var6;
      this.transforms = var7;
   }

   public List<BakedQuad> getQuads(@Nullable BlockState var1, @Nullable Direction var2, RandomSource var3) {
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

   public static class Builder {
      private final ImmutableList.Builder<BakedQuad> unculledFaces;
      private final EnumMap<Direction, ImmutableList.Builder<BakedQuad>> culledFaces;
      private final boolean hasAmbientOcclusion;
      @Nullable
      private TextureAtlasSprite particleIcon;
      private final boolean usesBlockLight;
      private final boolean isGui3d;
      private final ItemTransforms transforms;

      public Builder(BlockModel var1, boolean var2) {
         this(var1.hasAmbientOcclusion(), var1.getGuiLight().lightLikeBlock(), var2, var1.getTransforms());
      }

      private Builder(boolean var1, boolean var2, boolean var3, ItemTransforms var4) {
         super();
         this.unculledFaces = ImmutableList.builder();
         this.culledFaces = Maps.newEnumMap(Direction.class);
         this.hasAmbientOcclusion = var1;
         this.usesBlockLight = var2;
         this.isGui3d = var3;
         this.transforms = var4;
         Direction[] var5 = Direction.values();
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Direction var8 = var5[var7];
            this.culledFaces.put(var8, ImmutableList.builder());
         }

      }

      public Builder addCulledFace(Direction var1, BakedQuad var2) {
         ((ImmutableList.Builder)this.culledFaces.get(var1)).add(var2);
         return this;
      }

      public Builder addUnculledFace(BakedQuad var1) {
         this.unculledFaces.add(var1);
         return this;
      }

      public Builder particle(TextureAtlasSprite var1) {
         this.particleIcon = var1;
         return this;
      }

      public Builder item() {
         return this;
      }

      public BakedModel build() {
         if (this.particleIcon == null) {
            throw new RuntimeException("Missing particle!");
         } else {
            Map var1 = Maps.transformValues(this.culledFaces, ImmutableList.Builder::build);
            return new SimpleBakedModel(this.unculledFaces.build(), new EnumMap(var1), this.hasAmbientOcclusion, this.usesBlockLight, this.isGui3d, this.particleIcon, this.transforms);
         }
      }
   }
}
