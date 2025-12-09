package net.minecraft.client.renderer.item;

import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

public class BlockModelWrapper implements ItemModel {
   private static final Function<ItemStack, RenderType> ITEM_RENDER_TYPE_GETTER = (var0) -> Sheets.translucentItemSheet();
   private static final Function<ItemStack, RenderType> BLOCK_RENDER_TYPE_GETTER = (var0) -> {
      Item var2 = var0.getItem();
      if (var2 instanceof BlockItem var1) {
         ChunkSectionLayer var3 = ItemBlockRenderTypes.getChunkRenderType(var1.getBlock().defaultBlockState());
         if (var3 != ChunkSectionLayer.TRANSLUCENT) {
            return Sheets.cutoutBlockSheet();
         }
      }

      return Sheets.translucentBlockItemSheet();
   };
   private final List<ItemTintSource> tints;
   private final List<BakedQuad> quads;
   private final Supplier<Vector3fc[]> extents;
   private final ModelRenderProperties properties;
   private final boolean animated;
   private final Function<ItemStack, RenderType> renderType;

   BlockModelWrapper(List<ItemTintSource> var1, List<BakedQuad> var2, ModelRenderProperties var3, Function<ItemStack, RenderType> var4) {
      super();
      this.tints = var1;
      this.quads = var2;
      this.properties = var3;
      this.renderType = var4;
      this.extents = Suppliers.memoize(() -> computeExtents(this.quads));
      boolean var5 = false;

      for(BakedQuad var7 : var2) {
         if (var7.sprite().contents().isAnimated()) {
            var5 = true;
            break;
         }
      }

      this.animated = var5;
   }

   public static Vector3fc[] computeExtents(List<BakedQuad> var0) {
      HashSet var1 = new HashSet();

      for(BakedQuad var3 : var0) {
         for(int var4 = 0; var4 < 4; ++var4) {
            var1.add(var3.position(var4));
         }
      }

      return (Vector3fc[])var1.toArray((var0x) -> new Vector3fc[var0x]);
   }

   public void update(ItemStackRenderState var1, ItemStack var2, ItemModelResolver var3, ItemDisplayContext var4, @Nullable ClientLevel var5, @Nullable ItemOwner var6, int var7) {
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
         int var12 = ((ItemTintSource)this.tints.get(var11)).calculate(var2, var5, var6 == null ? null : var6.asLivingEntity());
         var10[var11] = var12;
         var1.appendModelIdentityElement(var12);
      }

      var8.setExtents(this.extents);
      var8.setRenderType((RenderType)this.renderType.apply(var2));
      this.properties.applyToLayer(var8, var4);
      var8.prepareQuadList().addAll(this.quads);
      if (this.animated) {
         var1.setAnimated();
      }

   }

   static Function<ItemStack, RenderType> detectRenderType(List<BakedQuad> var0) {
      Iterator var1 = var0.iterator();
      if (!var1.hasNext()) {
         return ITEM_RENDER_TYPE_GETTER;
      } else {
         Identifier var2 = ((BakedQuad)var1.next()).sprite().atlasLocation();

         while(var1.hasNext()) {
            BakedQuad var3 = (BakedQuad)var1.next();
            Identifier var4 = var3.sprite().atlasLocation();
            if (!var4.equals(var2)) {
               String var10002 = String.valueOf(var2);
               throw new IllegalStateException("Multiple atlases used in model, expected " + var10002 + ", but also got " + String.valueOf(var4));
            }
         }

         if (var2.equals(TextureAtlas.LOCATION_ITEMS)) {
            return ITEM_RENDER_TYPE_GETTER;
         } else if (var2.equals(TextureAtlas.LOCATION_BLOCKS)) {
            return BLOCK_RENDER_TYPE_GETTER;
         } else {
            throw new IllegalArgumentException("Atlas " + String.valueOf(var2) + " can't be usef for item models");
         }
      }
   }

   private static boolean hasSpecialAnimatedTexture(ItemStack var0) {
      return var0.is(ItemTags.COMPASSES) || var0.is(Items.CLOCK);
   }

   public static record Unbaked(Identifier model, List<ItemTintSource> tints) implements ItemModel.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Identifier.CODEC.fieldOf("model").forGetter(Unbaked::model), ItemTintSources.CODEC.listOf().optionalFieldOf("tints", List.of()).forGetter(Unbaked::tints)).apply(var0, Unbaked::new));

      public Unbaked(Identifier var1, List<ItemTintSource> var2) {
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
         List var5 = var3.bakeTopGeometry(var4, var2, BlockModelRotation.IDENTITY).getAll();
         ModelRenderProperties var6 = ModelRenderProperties.fromResolvedModel(var2, var3, var4);
         Function var7 = BlockModelWrapper.detectRenderType(var5);
         return new BlockModelWrapper(this.tints, var5, var6, var7);
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }
   }
}
