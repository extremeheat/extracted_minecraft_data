package net.minecraft.client.renderer.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class BlockModelWrapper implements ItemModel {
   private final BakedModel model;
   private final List<ItemTintSource> tints;

   BlockModelWrapper(BakedModel var1, List<ItemTintSource> var2) {
      super();
      this.model = var1;
      this.tints = var2;
   }

   public void update(ItemStackRenderState var1, ItemStack var2, ItemModelResolver var3, ItemDisplayContext var4, @Nullable ClientLevel var5, @Nullable LivingEntity var6, int var7) {
      ItemStackRenderState.LayerRenderState var8 = var1.newLayer();
      if (var2.hasFoil()) {
         var8.setFoilType(hasSpecialAnimatedTexture(var2) ? ItemStackRenderState.FoilType.SPECIAL : ItemStackRenderState.FoilType.STANDARD);
      }

      int var9 = this.tints.size();
      int[] var10 = var8.prepareTintLayers(var9);

      for(int var11 = 0; var11 < var9; ++var11) {
         var10[var11] = ((ItemTintSource)this.tints.get(var11)).calculate(var2);
      }

      RenderType var12 = ItemBlockRenderTypes.getRenderType(var2);
      var8.setupBlockModel(this.model, var12);
   }

   private static boolean hasSpecialAnimatedTexture(ItemStack var0) {
      return var0.is(ItemTags.COMPASSES) || var0.is(Items.CLOCK);
   }

   public static record Unbaked(ResourceLocation model, List<ItemTintSource> tints) implements ItemModel.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(ResourceLocation.CODEC.fieldOf("model").forGetter(Unbaked::model), ItemTintSources.CODEC.listOf().optionalFieldOf("tints", List.of()).forGetter(Unbaked::tints)).apply(var0, Unbaked::new);
      });

      public Unbaked(ResourceLocation var1, List<ItemTintSource> var2) {
         super();
         this.model = var1;
         this.tints = var2;
      }

      public void resolveDependencies(ResolvableModel.Resolver var1) {
         var1.resolve(this.model);
      }

      public ItemModel bake(ItemModel.BakingContext var1) {
         BakedModel var2 = var1.bake(this.model);
         return new BlockModelWrapper(var2, this.tints);
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }

      public ResourceLocation model() {
         return this.model;
      }

      public List<ItemTintSource> tints() {
         return this.tints;
      }
   }
}
