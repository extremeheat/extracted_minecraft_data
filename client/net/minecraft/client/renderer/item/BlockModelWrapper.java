package net.minecraft.client.renderer.item;

import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Vector3f;

public class BlockModelWrapper implements ItemModel {
   private final List<ItemTintSource> tints;
   private final List<BakedQuad> quads;
   private final Supplier<Vector3f[]> extents;
   private final ModelRenderProperties properties;
   private final boolean animated;

   public BlockModelWrapper(List<ItemTintSource> var1, List<BakedQuad> var2, ModelRenderProperties var3) {
      super();
      this.tints = var1;
      this.quads = var2;
      this.properties = var3;
      this.extents = Suppliers.memoize(() -> computeExtents(this.quads));
      boolean var4 = false;

      for(BakedQuad var6 : var2) {
         if (var6.sprite().isAnimated()) {
            var4 = true;
            break;
         }
      }

      this.animated = var4;
   }

   public static Vector3f[] computeExtents(List<BakedQuad> var0) {
      HashSet var1 = new HashSet();

      for(BakedQuad var3 : var0) {
         int[] var10000 = var3.vertices();
         Objects.requireNonNull(var1);
         FaceBakery.extractPositions(var10000, var1::add);
      }

      return (Vector3f[])var1.toArray((var0x) -> new Vector3f[var0x]);
   }

   public void update(ItemStackRenderState var1, ItemStack var2, ItemModelResolver var3, ItemDisplayContext var4, @Nullable ClientLevel var5, @Nullable LivingEntity var6, int var7) {
      var1.appendModelIdentityElement(this);
      ItemStackRenderState.LayerRenderState var8 = var1.newLayer();
      if (var2.hasFoil()) {
         ItemStackRenderState.FoilType var9 = hasSpecialAnimatedTexture(var2) ? ItemStackRenderState.FoilType.SPECIAL : ItemStackRenderState.FoilType.STANDARD;
         var8.setFoilType(var9);
         var1.setAnimated();
         var1.appendModelIdentityElement(var9);
      }

      int var13 = this.tints.size();
      int[] var10 = var8.prepareTintLayers(var13);

      for(int var11 = 0; var11 < var13; ++var11) {
         int var12 = ((ItemTintSource)this.tints.get(var11)).calculate(var2, var5, var6);
         var10[var11] = var12;
         var1.appendModelIdentityElement(var12);
      }

      var8.setExtents(this.extents);
      var8.setRenderType(ItemBlockRenderTypes.getRenderType(var2));
      this.properties.applyToLayer(var8, var4);
      var8.prepareQuadList().addAll(this.quads);
      if (this.animated) {
         var1.setAnimated();
      }

   }

   private static boolean hasSpecialAnimatedTexture(ItemStack var0) {
      return var0.is(ItemTags.COMPASSES) || var0.is(Items.CLOCK);
   }

   public static record Unbaked(ResourceLocation model, List<ItemTintSource> tints) implements ItemModel.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ResourceLocation.CODEC.fieldOf("model").forGetter(Unbaked::model), ItemTintSources.CODEC.listOf().optionalFieldOf("tints", List.of()).forGetter(Unbaked::tints)).apply(var0, Unbaked::new));

      public Unbaked(ResourceLocation var1, List<ItemTintSource> var2) {
         super();
         this.model = var1;
         this.tints = var2;
      }

      public void resolveDependencies(ResolvableModel.Resolver var1) {
         var1.markDependency(this.model);
      }

      public ItemModel bake(ItemModel.BakingContext var1) {
         ModelBaker var2 = var1.blockModelBaker();
         ResolvedModel var3 = var2.getModel(this.model);
         TextureSlots var4 = var3.getTopTextureSlots();
         List var5 = var3.bakeTopGeometry(var4, var2, BlockModelRotation.X0_Y0).getAll();
         ModelRenderProperties var6 = ModelRenderProperties.fromResolvedModel(var2, var3, var4);
         return new BlockModelWrapper(this.tints, var5, var6);
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }
   }
}
