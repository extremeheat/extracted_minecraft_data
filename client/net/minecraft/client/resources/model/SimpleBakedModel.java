package net.minecraft.client.resources.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class SimpleBakedModel implements BakedModel {
   public static final String PARTICLE_TEXTURE_REFERENCE = "particle";
   private final List<BakedQuad> unculledFaces;
   private final Map<Direction, List<BakedQuad>> culledFaces;
   private final boolean hasAmbientOcclusion;
   private final boolean isGui3d;
   private final boolean usesBlockLight;
   private final TextureAtlasSprite particleIcon;
   private final ItemTransforms transforms;

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

   public static BakedModel bakeElements(List<BlockElement> var0, TextureSlots var1, SpriteGetter var2, ModelState var3, boolean var4, boolean var5, boolean var6, ItemTransforms var7) {
      TextureAtlasSprite var8 = findSprite(var2, var1, "particle");
      Builder var9 = (new Builder(var4, var5, var6, var7)).particle(var8);
      Iterator var10 = var0.iterator();

      while(var10.hasNext()) {
         BlockElement var11 = (BlockElement)var10.next();
         Iterator var12 = var11.faces.keySet().iterator();

         while(var12.hasNext()) {
            Direction var13 = (Direction)var12.next();
            BlockElementFace var14 = (BlockElementFace)var11.faces.get(var13);
            TextureAtlasSprite var15 = findSprite(var2, var1, var14.texture());
            if (var14.cullForDirection() == null) {
               var9.addUnculledFace(bakeFace(var11, var14, var15, var13, var3));
            } else {
               var9.addCulledFace(Direction.rotate(var3.getRotation().getMatrix(), var14.cullForDirection()), bakeFace(var11, var14, var15, var13, var3));
            }
         }
      }

      return var9.build();
   }

   private static BakedQuad bakeFace(BlockElement var0, BlockElementFace var1, TextureAtlasSprite var2, Direction var3, ModelState var4) {
      return FaceBakery.bakeQuad(var0.from, var0.to, var1, var2, var3, var4, var0.rotation, var0.shade, var0.lightEmission);
   }

   private static TextureAtlasSprite findSprite(SpriteGetter var0, TextureSlots var1, String var2) {
      Material var3 = var1.getMaterial(var2);
      return var3 != null ? var0.get(var3) : var0.reportMissingReference(var2);
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

   public TextureAtlasSprite getParticleIcon() {
      return this.particleIcon;
   }

   public ItemTransforms getTransforms() {
      return this.transforms;
   }

   public static class Builder {
      private final ImmutableList.Builder<BakedQuad> unculledFaces = ImmutableList.builder();
      private final EnumMap<Direction, ImmutableList.Builder<BakedQuad>> culledFaces = Maps.newEnumMap(Direction.class);
      private final boolean hasAmbientOcclusion;
      @Nullable
      private TextureAtlasSprite particleIcon;
      private final boolean usesBlockLight;
      private final boolean isGui3d;
      private final ItemTransforms transforms;

      public Builder(boolean var1, boolean var2, boolean var3, ItemTransforms var4) {
         super();
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
