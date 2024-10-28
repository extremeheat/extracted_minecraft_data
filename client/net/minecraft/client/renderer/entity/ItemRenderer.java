package net.minecraft.client.renderer.entity;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.mojang.math.MatrixUtil;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ItemRenderer implements ResourceManagerReloadListener {
   public static final ResourceLocation ENCHANTED_GLINT_ENTITY = ResourceLocation.withDefaultNamespace("textures/misc/enchanted_glint_entity.png");
   public static final ResourceLocation ENCHANTED_GLINT_ITEM = ResourceLocation.withDefaultNamespace("textures/misc/enchanted_glint_item.png");
   private static final Set<Item> IGNORED;
   public static final int GUI_SLOT_CENTER_X = 8;
   public static final int GUI_SLOT_CENTER_Y = 8;
   public static final int ITEM_COUNT_BLIT_OFFSET = 200;
   public static final float COMPASS_FOIL_UI_SCALE = 0.5F;
   public static final float COMPASS_FOIL_FIRST_PERSON_SCALE = 0.75F;
   public static final float COMPASS_FOIL_TEXTURE_SCALE = 0.0078125F;
   private static final ModelResourceLocation TRIDENT_MODEL;
   public static final ModelResourceLocation TRIDENT_IN_HAND_MODEL;
   private static final ModelResourceLocation SPYGLASS_MODEL;
   public static final ModelResourceLocation SPYGLASS_IN_HAND_MODEL;
   private final Minecraft minecraft;
   private final ItemModelShaper itemModelShaper;
   private final TextureManager textureManager;
   private final ItemColors itemColors;
   private final BlockEntityWithoutLevelRenderer blockEntityRenderer;

   public ItemRenderer(Minecraft var1, TextureManager var2, ModelManager var3, ItemColors var4, BlockEntityWithoutLevelRenderer var5) {
      super();
      this.minecraft = var1;
      this.textureManager = var2;
      this.itemModelShaper = new ItemModelShaper(var3);
      this.blockEntityRenderer = var5;
      Iterator var6 = BuiltInRegistries.ITEM.iterator();

      while(var6.hasNext()) {
         Item var7 = (Item)var6.next();
         if (!IGNORED.contains(var7)) {
            this.itemModelShaper.register(var7, ModelResourceLocation.inventory(BuiltInRegistries.ITEM.getKey(var7)));
         }
      }

      this.itemColors = var4;
   }

   public ItemModelShaper getItemModelShaper() {
      return this.itemModelShaper;
   }

   private void renderModelLists(BakedModel var1, ItemStack var2, int var3, int var4, PoseStack var5, VertexConsumer var6) {
      RandomSource var7 = RandomSource.create();
      long var8 = 42L;
      Direction[] var10 = Direction.values();
      int var11 = var10.length;

      for(int var12 = 0; var12 < var11; ++var12) {
         Direction var13 = var10[var12];
         var7.setSeed(42L);
         this.renderQuadList(var5, var6, var1.getQuads((BlockState)null, var13, var7), var2, var3, var4);
      }

      var7.setSeed(42L);
      this.renderQuadList(var5, var6, var1.getQuads((BlockState)null, (Direction)null, var7), var2, var3, var4);
   }

   public void render(ItemStack var1, ItemDisplayContext var2, boolean var3, PoseStack var4, MultiBufferSource var5, int var6, int var7, BakedModel var8) {
      if (!var1.isEmpty()) {
         var4.pushPose();
         boolean var9 = var2 == ItemDisplayContext.GUI || var2 == ItemDisplayContext.GROUND || var2 == ItemDisplayContext.FIXED;
         if (var9) {
            if (var1.is(Items.TRIDENT)) {
               var8 = this.itemModelShaper.getModelManager().getModel(TRIDENT_MODEL);
            } else if (var1.is(Items.SPYGLASS)) {
               var8 = this.itemModelShaper.getModelManager().getModel(SPYGLASS_MODEL);
            }
         }

         var8.getTransforms().getTransform(var2).apply(var3, var4);
         var4.translate(-0.5F, -0.5F, -0.5F);
         if (var8.isCustomRenderer() || var1.is(Items.TRIDENT) && !var9) {
            this.blockEntityRenderer.renderByItem(var1, var2, var4, var5, var6, var7);
         } else {
            boolean var10;
            label63: {
               if (var2 != ItemDisplayContext.GUI && !var2.firstPerson()) {
                  Item var12 = var1.getItem();
                  if (var12 instanceof BlockItem) {
                     BlockItem var11 = (BlockItem)var12;
                     Block var15 = var11.getBlock();
                     var10 = !(var15 instanceof HalfTransparentBlock) && !(var15 instanceof StainedGlassPaneBlock);
                     break label63;
                  }
               }

               var10 = true;
            }

            RenderType var14 = ItemBlockRenderTypes.getRenderType(var1, var10);
            VertexConsumer var16;
            if (hasAnimatedTexture(var1) && var1.hasFoil()) {
               PoseStack.Pose var13 = var4.last().copy();
               if (var2 == ItemDisplayContext.GUI) {
                  MatrixUtil.mulComponentWise(var13.pose(), 0.5F);
               } else if (var2.firstPerson()) {
                  MatrixUtil.mulComponentWise(var13.pose(), 0.75F);
               }

               var16 = getCompassFoilBuffer(var5, var14, var13);
            } else if (var10) {
               var16 = getFoilBufferDirect(var5, var14, true, var1.hasFoil());
            } else {
               var16 = getFoilBuffer(var5, var14, true, var1.hasFoil());
            }

            this.renderModelLists(var8, var1, var6, var7, var4, var16);
         }

         var4.popPose();
      }
   }

   private static boolean hasAnimatedTexture(ItemStack var0) {
      return var0.is(ItemTags.COMPASSES) || var0.is(Items.CLOCK);
   }

   public static VertexConsumer getArmorFoilBuffer(MultiBufferSource var0, RenderType var1, boolean var2) {
      return var2 ? VertexMultiConsumer.create(var0.getBuffer(RenderType.armorEntityGlint()), var0.getBuffer(var1)) : var0.getBuffer(var1);
   }

   public static VertexConsumer getCompassFoilBuffer(MultiBufferSource var0, RenderType var1, PoseStack.Pose var2) {
      return VertexMultiConsumer.create(new SheetedDecalTextureGenerator(var0.getBuffer(RenderType.glint()), var2, 0.0078125F), var0.getBuffer(var1));
   }

   public static VertexConsumer getFoilBuffer(MultiBufferSource var0, RenderType var1, boolean var2, boolean var3) {
      if (var3) {
         return Minecraft.useShaderTransparency() && var1 == Sheets.translucentItemSheet() ? VertexMultiConsumer.create(var0.getBuffer(RenderType.glintTranslucent()), var0.getBuffer(var1)) : VertexMultiConsumer.create(var0.getBuffer(var2 ? RenderType.glint() : RenderType.entityGlint()), var0.getBuffer(var1));
      } else {
         return var0.getBuffer(var1);
      }
   }

   public static VertexConsumer getFoilBufferDirect(MultiBufferSource var0, RenderType var1, boolean var2, boolean var3) {
      return var3 ? VertexMultiConsumer.create(var0.getBuffer(var2 ? RenderType.glint() : RenderType.entityGlintDirect()), var0.getBuffer(var1)) : var0.getBuffer(var1);
   }

   private void renderQuadList(PoseStack var1, VertexConsumer var2, List<BakedQuad> var3, ItemStack var4, int var5, int var6) {
      boolean var7 = !var4.isEmpty();
      PoseStack.Pose var8 = var1.last();
      Iterator var9 = var3.iterator();

      while(var9.hasNext()) {
         BakedQuad var10 = (BakedQuad)var9.next();
         int var11 = -1;
         if (var7 && var10.isTinted()) {
            var11 = this.itemColors.getColor(var4, var10.getTintIndex());
         }

         float var12 = (float)FastColor.ARGB32.alpha(var11) / 255.0F;
         float var13 = (float)FastColor.ARGB32.red(var11) / 255.0F;
         float var14 = (float)FastColor.ARGB32.green(var11) / 255.0F;
         float var15 = (float)FastColor.ARGB32.blue(var11) / 255.0F;
         var2.putBulkData(var8, var10, var13, var14, var15, var12, var5, var6);
      }

   }

   public BakedModel getModel(ItemStack var1, @Nullable Level var2, @Nullable LivingEntity var3, int var4) {
      BakedModel var5;
      if (var1.is(Items.TRIDENT)) {
         var5 = this.itemModelShaper.getModelManager().getModel(TRIDENT_IN_HAND_MODEL);
      } else if (var1.is(Items.SPYGLASS)) {
         var5 = this.itemModelShaper.getModelManager().getModel(SPYGLASS_IN_HAND_MODEL);
      } else {
         var5 = this.itemModelShaper.getItemModel(var1);
      }

      ClientLevel var6 = var2 instanceof ClientLevel ? (ClientLevel)var2 : null;
      BakedModel var7 = var5.getOverrides().resolve(var5, var1, var6, var3, var4);
      return var7 == null ? this.itemModelShaper.getModelManager().getMissingModel() : var7;
   }

   public void renderStatic(ItemStack var1, ItemDisplayContext var2, int var3, int var4, PoseStack var5, MultiBufferSource var6, @Nullable Level var7, int var8) {
      this.renderStatic((LivingEntity)null, var1, var2, false, var5, var6, var7, var3, var4, var8);
   }

   public void renderStatic(@Nullable LivingEntity var1, ItemStack var2, ItemDisplayContext var3, boolean var4, PoseStack var5, MultiBufferSource var6, @Nullable Level var7, int var8, int var9, int var10) {
      if (!var2.isEmpty()) {
         BakedModel var11 = this.getModel(var2, var7, var1, var10);
         this.render(var2, var3, var4, var5, var6, var8, var9, var11);
      }
   }

   public void onResourceManagerReload(ResourceManager var1) {
      this.itemModelShaper.rebuildCache();
   }

   static {
      IGNORED = Sets.newHashSet(new Item[]{Items.AIR});
      TRIDENT_MODEL = ModelResourceLocation.inventory(ResourceLocation.withDefaultNamespace("trident"));
      TRIDENT_IN_HAND_MODEL = ModelResourceLocation.inventory(ResourceLocation.withDefaultNamespace("trident_in_hand"));
      SPYGLASS_MODEL = ModelResourceLocation.inventory(ResourceLocation.withDefaultNamespace("spyglass"));
      SPYGLASS_IN_HAND_MODEL = ModelResourceLocation.inventory(ResourceLocation.withDefaultNamespace("spyglass_in_hand"));
   }
}
