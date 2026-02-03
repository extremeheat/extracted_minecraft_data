package net.minecraft.client.renderer.item;

import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
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
   private static final Function<ItemStack, RenderType> ITEM_RENDER_TYPE_GETTER = (stack) -> Sheets.translucentItemSheet();
   private static final Function<ItemStack, RenderType> BLOCK_RENDER_TYPE_GETTER = (stack) -> {
      Item patt0$temp = stack.getItem();
      if (patt0$temp instanceof BlockItem blockItem) {
         ChunkSectionLayer blockLayer = ItemBlockRenderTypes.getChunkRenderType(blockItem.getBlock().defaultBlockState());
         if (blockLayer != ChunkSectionLayer.TRANSLUCENT) {
            return Sheets.cutoutBlockItemSheet();
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

   private BlockModelWrapper(final List<ItemTintSource> tints, final List<BakedQuad> quads, final ModelRenderProperties properties, final Function<ItemStack, RenderType> renderType) {
      super();
      this.tints = tints;
      this.quads = quads;
      this.properties = properties;
      this.renderType = renderType;
      this.extents = Suppliers.memoize(() -> computeExtents(this.quads));
      boolean animated = false;

      for(BakedQuad quad : quads) {
         if (quad.sprite().contents().isAnimated()) {
            animated = true;
            break;
         }
      }

      this.animated = animated;
   }

   public static Vector3fc[] computeExtents(final List<BakedQuad> quads) {
      Set<Vector3fc> result = new HashSet();

      for(BakedQuad quad : quads) {
         for(int vertex = 0; vertex < 4; ++vertex) {
            result.add(quad.position(vertex));
         }
      }

      return (Vector3fc[])result.toArray((x$0) -> new Vector3fc[x$0]);
   }

   public void update(final ItemStackRenderState output, final ItemStack item, final ItemModelResolver resolver, final ItemDisplayContext displayContext, final @Nullable ClientLevel level, final @Nullable ItemOwner owner, final int seed) {
      output.appendModelIdentityElement(this);
      ItemStackRenderState.LayerRenderState layer = output.newLayer();
      if (item.hasFoil()) {
         ItemStackRenderState.FoilType foilType = hasSpecialAnimatedTexture(item) ? ItemStackRenderState.FoilType.SPECIAL : ItemStackRenderState.FoilType.STANDARD;
         layer.setFoilType(foilType);
         output.setAnimated();
         output.appendModelIdentityElement(foilType);
      }

      int activeTints = this.tints.size();
      int[] tintLayers = layer.prepareTintLayers(activeTints);

      for(int i = 0; i < activeTints; ++i) {
         int tint = ((ItemTintSource)this.tints.get(i)).calculate(item, level, owner == null ? null : owner.asLivingEntity());
         tintLayers[i] = tint;
         output.appendModelIdentityElement(tint);
      }

      layer.setExtents(this.extents);
      layer.setRenderType((RenderType)this.renderType.apply(item));
      this.properties.applyToLayer(layer, displayContext);
      layer.prepareQuadList().addAll(this.quads);
      if (this.animated) {
         output.setAnimated();
      }

   }

   private static Function<ItemStack, RenderType> detectRenderType(final List<BakedQuad> quads) {
      Iterator<BakedQuad> quadIterator = quads.iterator();
      if (!quadIterator.hasNext()) {
         return ITEM_RENDER_TYPE_GETTER;
      } else {
         Identifier expectedAtlas = ((BakedQuad)quadIterator.next()).sprite().atlasLocation();

         while(quadIterator.hasNext()) {
            BakedQuad quad = (BakedQuad)quadIterator.next();
            Identifier quadAtlas = quad.sprite().atlasLocation();
            if (!quadAtlas.equals(expectedAtlas)) {
               String var10002 = String.valueOf(expectedAtlas);
               throw new IllegalStateException("Multiple atlases used in model, expected " + var10002 + ", but also got " + String.valueOf(quadAtlas));
            }
         }

         if (expectedAtlas.equals(TextureAtlas.LOCATION_ITEMS)) {
            return ITEM_RENDER_TYPE_GETTER;
         } else if (expectedAtlas.equals(TextureAtlas.LOCATION_BLOCKS)) {
            return BLOCK_RENDER_TYPE_GETTER;
         } else {
            throw new IllegalArgumentException("Atlas " + String.valueOf(expectedAtlas) + " can't be usef for item models");
         }
      }
   }

   private static boolean hasSpecialAnimatedTexture(final ItemStack itemStack) {
      return itemStack.is(ItemTags.COMPASSES) || itemStack.is(Items.CLOCK);
   }

   public static record Unbaked(Identifier model, List<ItemTintSource> tints) implements ItemModel.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Identifier.CODEC.fieldOf("model").forGetter(Unbaked::model), ItemTintSources.CODEC.listOf().optionalFieldOf("tints", List.of()).forGetter(Unbaked::tints)).apply(i, Unbaked::new));

      public Unbaked {
         super();
      }

      public void resolveDependencies(final ResolvableModel.Resolver resolver) {
         resolver.markDependency(this.model);
      }

      public ItemModel bake(final ItemModel.BakingContext context) {
         ModelBaker baker = context.blockModelBaker();
         ResolvedModel resolvedModel = baker.getModel(this.model);
         TextureSlots textureSlots = resolvedModel.getTopTextureSlots();
         List<BakedQuad> quads = resolvedModel.bakeTopGeometry(textureSlots, baker, BlockModelRotation.IDENTITY).getAll();
         ModelRenderProperties properties = ModelRenderProperties.fromResolvedModel(baker, resolvedModel, textureSlots);
         Function<ItemStack, RenderType> renderTypeGetter = BlockModelWrapper.detectRenderType(quads);
         return new BlockModelWrapper(this.tints, quads, properties, renderTypeGetter);
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }
   }
}
