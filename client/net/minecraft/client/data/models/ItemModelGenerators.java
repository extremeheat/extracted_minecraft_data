package net.minecraft.client.data.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.client.color.item.Dye;
import net.minecraft.client.color.item.Firework;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.color.item.MapColor;
import net.minecraft.client.color.item.Potion;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.item.BundleSelectedItemSpecialRenderer;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.RangeSelectItemModel;
import net.minecraft.client.renderer.item.properties.conditional.Broken;
import net.minecraft.client.renderer.item.properties.conditional.BundleHasSelectedItem;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.client.renderer.item.properties.conditional.FishingRodCast;
import net.minecraft.client.renderer.item.properties.numeric.CompassAngle;
import net.minecraft.client.renderer.item.properties.numeric.CompassAngleState;
import net.minecraft.client.renderer.item.properties.numeric.CrossbowPull;
import net.minecraft.client.renderer.item.properties.numeric.Time;
import net.minecraft.client.renderer.item.properties.numeric.UseCycle;
import net.minecraft.client.renderer.item.properties.numeric.UseDuration;
import net.minecraft.client.renderer.item.properties.select.Charge;
import net.minecraft.client.renderer.item.properties.select.DisplayContext;
import net.minecraft.client.renderer.item.properties.select.TrimMaterialProperty;
import net.minecraft.client.renderer.special.ShieldSpecialRenderer;
import net.minecraft.client.renderer.special.TridentSpecialRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimMaterials;

public class ItemModelGenerators {
   private static final ItemTintSource BLANK_LAYER = ItemModelUtils.constantTint(-1);
   private static final String SLOT_HELMET = "helmet";
   private static final String SLOT_CHESTPLATE = "chestplate";
   private static final String SLOT_LEGGINS = "leggings";
   private static final String SLOT_BOOTS = "boots";
   private static final List<TrimMaterialData> TRIM_MATERIAL_MODELS;
   private final ItemModelOutput itemModelOutput;
   private final BiConsumer<ResourceLocation, ModelInstance> modelOutput;

   public ItemModelGenerators(ItemModelOutput var1, BiConsumer<ResourceLocation, ModelInstance> var2) {
      super();
      this.itemModelOutput = var1;
      this.modelOutput = var2;
   }

   private void declareCustomModelItem(Item var1) {
      this.itemModelOutput.accept(var1, ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(var1)));
   }

   private ResourceLocation createFlatItemModel(Item var1, ModelTemplate var2) {
      return var2.create(ModelLocationUtils.getModelLocation(var1), TextureMapping.layer0(var1), this.modelOutput);
   }

   private void generateFlatItem(Item var1, ModelTemplate var2) {
      this.itemModelOutput.accept(var1, ItemModelUtils.plainModel(this.createFlatItemModel(var1, var2)));
   }

   private ResourceLocation createFlatItemModel(Item var1, String var2, ModelTemplate var3) {
      return var3.create(ModelLocationUtils.getModelLocation(var1, var2), TextureMapping.layer0(TextureMapping.getItemTexture(var1, var2)), this.modelOutput);
   }

   private ResourceLocation createFlatItemModel(Item var1, Item var2, ModelTemplate var3) {
      return var3.create(ModelLocationUtils.getModelLocation(var1), TextureMapping.layer0(var2), this.modelOutput);
   }

   private void generateFlatItem(Item var1, Item var2, ModelTemplate var3) {
      this.itemModelOutput.accept(var1, ItemModelUtils.plainModel(this.createFlatItemModel(var1, var2, var3)));
   }

   private void generateItemWithTintedOverlay(Item var1, ItemTintSource var2) {
      this.generateItemWithTintedOverlay(var1, "_overlay", var2);
   }

   private void generateItemWithTintedOverlay(Item var1, String var2, ItemTintSource var3) {
      ResourceLocation var4 = this.generateLayeredItem(var1, TextureMapping.getItemTexture(var1), TextureMapping.getItemTexture(var1, var2));
      this.itemModelOutput.accept(var1, ItemModelUtils.tintedModel(var4, BLANK_LAYER, var3));
   }

   private List<RangeSelectItemModel.Entry> createCompassModels(Item var1) {
      ArrayList var2 = new ArrayList();
      ItemModel.Unbaked var3 = ItemModelUtils.plainModel(this.createFlatItemModel(var1, "_16", ModelTemplates.FLAT_ITEM));
      var2.add(ItemModelUtils.override(var3, 0.0F));

      for(int var4 = 1; var4 < 32; ++var4) {
         int var5 = Mth.positiveModulo(var4 - 16, 32);
         ItemModel.Unbaked var6 = ItemModelUtils.plainModel(this.createFlatItemModel(var1, String.format(Locale.ROOT, "_%02d", var5), ModelTemplates.FLAT_ITEM));
         var2.add(ItemModelUtils.override(var6, (float)var4 - 0.5F));
      }

      var2.add(ItemModelUtils.override(var3, 31.5F));
      return var2;
   }

   private void generateStandardCompassItem(Item var1) {
      List var2 = this.createCompassModels(var1);
      this.itemModelOutput.accept(var1, ItemModelUtils.conditional(ItemModelUtils.hasComponent(DataComponents.LODESTONE_TRACKER), ItemModelUtils.rangeSelect(new CompassAngle(true, CompassAngleState.CompassTarget.LODESTONE), 32.0F, var2), ItemModelUtils.inOverworld(ItemModelUtils.rangeSelect(new CompassAngle(true, CompassAngleState.CompassTarget.SPAWN), 32.0F, var2), ItemModelUtils.rangeSelect(new CompassAngle(true, CompassAngleState.CompassTarget.NONE), 32.0F, var2))));
   }

   private void generateRecoveryCompassItem(Item var1) {
      this.itemModelOutput.accept(var1, ItemModelUtils.rangeSelect(new CompassAngle(true, CompassAngleState.CompassTarget.RECOVERY), 32.0F, this.createCompassModels(var1)));
   }

   private void generateClockItem(Item var1) {
      ArrayList var2 = new ArrayList();
      ItemModel.Unbaked var3 = ItemModelUtils.plainModel(this.createFlatItemModel(var1, "_00", ModelTemplates.FLAT_ITEM));
      var2.add(ItemModelUtils.override(var3, 0.0F));

      for(int var4 = 1; var4 < 64; ++var4) {
         ItemModel.Unbaked var5 = ItemModelUtils.plainModel(this.createFlatItemModel(var1, String.format(Locale.ROOT, "_%02d", var4), ModelTemplates.FLAT_ITEM));
         var2.add(ItemModelUtils.override(var5, (float)var4 - 0.5F));
      }

      var2.add(ItemModelUtils.override(var3, 63.5F));
      this.itemModelOutput.accept(var1, ItemModelUtils.inOverworld(ItemModelUtils.rangeSelect(new Time(true, Time.TimeSource.DAYTIME), 64.0F, var2), ItemModelUtils.rangeSelect(new Time(true, Time.TimeSource.RANDOM), 64.0F, var2)));
   }

   private ResourceLocation generateLayeredItem(Item var1, ResourceLocation var2, ResourceLocation var3) {
      return ModelTemplates.TWO_LAYERED_ITEM.create(var1, TextureMapping.layered(var2, var3), this.modelOutput);
   }

   private ResourceLocation generateLayeredItem(ResourceLocation var1, ResourceLocation var2, ResourceLocation var3) {
      return ModelTemplates.TWO_LAYERED_ITEM.create(var1, TextureMapping.layered(var2, var3), this.modelOutput);
   }

   private void generateLayeredItem(ResourceLocation var1, ResourceLocation var2, ResourceLocation var3, ResourceLocation var4) {
      ModelTemplates.THREE_LAYERED_ITEM.create(var1, TextureMapping.layered(var2, var3, var4), this.modelOutput);
   }

   private void generateTrimmableItem(Item var1, ResourceKey<EquipmentAsset> var2, String var3, boolean var4) {
      ResourceLocation var5 = ModelLocationUtils.getModelLocation(var1);
      ResourceLocation var6 = TextureMapping.getItemTexture(var1);
      ResourceLocation var7 = TextureMapping.getItemTexture(var1, "_overlay");
      ArrayList var8 = new ArrayList(TRIM_MATERIAL_MODELS.size());

      for(TrimMaterialData var10 : TRIM_MATERIAL_MODELS) {
         ResourceLocation var11 = var5.withSuffix("_" + var10.name() + "_trim");
         ResourceLocation var12 = ResourceLocation.withDefaultNamespace("trims/items/" + var3 + "_trim_" + var10.textureName(var2));
         ItemModel.Unbaked var13;
         if (var4) {
            this.generateLayeredItem(var11, var6, var7, var12);
            var13 = ItemModelUtils.tintedModel(var11, new Dye(-6265536));
         } else {
            this.generateLayeredItem(var11, var6, var12);
            var13 = ItemModelUtils.plainModel(var11);
         }

         var8.add(ItemModelUtils.when(var10.materialKey, var13));
      }

      ItemModel.Unbaked var14;
      if (var4) {
         ModelTemplates.TWO_LAYERED_ITEM.create(var5, TextureMapping.layered(var6, var7), this.modelOutput);
         var14 = ItemModelUtils.tintedModel(var5, new Dye(-6265536));
      } else {
         ModelTemplates.FLAT_ITEM.create(var5, TextureMapping.layer0(var6), this.modelOutput);
         var14 = ItemModelUtils.plainModel(var5);
      }

      this.itemModelOutput.accept(var1, ItemModelUtils.select(new TrimMaterialProperty(), var14, var8));
   }

   private void generateBundleModels(Item var1) {
      ItemModel.Unbaked var2 = ItemModelUtils.plainModel(this.createFlatItemModel(var1, ModelTemplates.FLAT_ITEM));
      ResourceLocation var3 = this.generateBundleCoverModel(var1, ModelTemplates.BUNDLE_OPEN_BACK_INVENTORY, "_open_back");
      ResourceLocation var4 = this.generateBundleCoverModel(var1, ModelTemplates.BUNDLE_OPEN_FRONT_INVENTORY, "_open_front");
      ItemModel.Unbaked var5 = ItemModelUtils.composite(ItemModelUtils.plainModel(var3), new BundleSelectedItemSpecialRenderer.Unbaked(), ItemModelUtils.plainModel(var4));
      ItemModel.Unbaked var6 = ItemModelUtils.conditional(new BundleHasSelectedItem(), var5, var2);
      this.itemModelOutput.accept(var1, ItemModelUtils.select(new DisplayContext(), var2, ItemModelUtils.when(ItemDisplayContext.GUI, var6)));
   }

   private ResourceLocation generateBundleCoverModel(Item var1, ModelTemplate var2, String var3) {
      ResourceLocation var4 = TextureMapping.getItemTexture(var1, var3);
      return var2.create(var1, TextureMapping.layer0(var4), this.modelOutput);
   }

   private void generateBow(Item var1) {
      ItemModel.Unbaked var2 = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(var1));
      ItemModel.Unbaked var3 = ItemModelUtils.plainModel(this.createFlatItemModel(var1, "_pulling_0", ModelTemplates.BOW));
      ItemModel.Unbaked var4 = ItemModelUtils.plainModel(this.createFlatItemModel(var1, "_pulling_1", ModelTemplates.BOW));
      ItemModel.Unbaked var5 = ItemModelUtils.plainModel(this.createFlatItemModel(var1, "_pulling_2", ModelTemplates.BOW));
      this.itemModelOutput.accept(var1, ItemModelUtils.conditional(ItemModelUtils.isUsingItem(), ItemModelUtils.rangeSelect(new UseDuration(false), 0.05F, var3, ItemModelUtils.override(var4, 0.65F), ItemModelUtils.override(var5, 0.9F)), var2));
   }

   private void generateCrossbow(Item var1) {
      ItemModel.Unbaked var2 = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(var1));
      ItemModel.Unbaked var3 = ItemModelUtils.plainModel(this.createFlatItemModel(var1, "_pulling_0", ModelTemplates.CROSSBOW));
      ItemModel.Unbaked var4 = ItemModelUtils.plainModel(this.createFlatItemModel(var1, "_pulling_1", ModelTemplates.CROSSBOW));
      ItemModel.Unbaked var5 = ItemModelUtils.plainModel(this.createFlatItemModel(var1, "_pulling_2", ModelTemplates.CROSSBOW));
      ItemModel.Unbaked var6 = ItemModelUtils.plainModel(this.createFlatItemModel(var1, "_arrow", ModelTemplates.CROSSBOW));
      ItemModel.Unbaked var7 = ItemModelUtils.plainModel(this.createFlatItemModel(var1, "_firework", ModelTemplates.CROSSBOW));
      this.itemModelOutput.accept(var1, ItemModelUtils.conditional(ItemModelUtils.isUsingItem(), ItemModelUtils.rangeSelect(new CrossbowPull(), var3, ItemModelUtils.override(var4, 0.58F), ItemModelUtils.override(var5, 1.0F)), ItemModelUtils.select(new Charge(), var2, ItemModelUtils.when(CrossbowItem.ChargeType.ARROW, var6), ItemModelUtils.when(CrossbowItem.ChargeType.ROCKET, var7))));
   }

   private void generateBooleanDispatch(Item var1, ConditionalItemModelProperty var2, ItemModel.Unbaked var3, ItemModel.Unbaked var4) {
      this.itemModelOutput.accept(var1, ItemModelUtils.conditional(var2, var3, var4));
   }

   private void generateElytra(Item var1) {
      ItemModel.Unbaked var2 = ItemModelUtils.plainModel(this.createFlatItemModel(var1, ModelTemplates.FLAT_ITEM));
      ItemModel.Unbaked var3 = ItemModelUtils.plainModel(this.createFlatItemModel(var1, "_broken", ModelTemplates.FLAT_ITEM));
      this.generateBooleanDispatch(var1, new Broken(), var3, var2);
   }

   private void generateBrush(Item var1) {
      ItemModel.Unbaked var2 = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(var1));
      ItemModel.Unbaked var3 = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(var1, "_brushing_0"));
      ItemModel.Unbaked var4 = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(var1, "_brushing_1"));
      ItemModel.Unbaked var5 = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(var1, "_brushing_2"));
      this.itemModelOutput.accept(var1, ItemModelUtils.rangeSelect(new UseCycle(10.0F), 0.1F, var2, ItemModelUtils.override(var3, 0.25F), ItemModelUtils.override(var4, 0.5F), ItemModelUtils.override(var5, 0.75F)));
   }

   private void generateFishingRod(Item var1) {
      ItemModel.Unbaked var2 = ItemModelUtils.plainModel(this.createFlatItemModel(var1, ModelTemplates.FLAT_HANDHELD_ROD_ITEM));
      ItemModel.Unbaked var3 = ItemModelUtils.plainModel(this.createFlatItemModel(var1, "_cast", ModelTemplates.FLAT_HANDHELD_ROD_ITEM));
      this.generateBooleanDispatch(var1, new FishingRodCast(), var3, var2);
   }

   private void generateGoatHorn(Item var1) {
      ItemModel.Unbaked var2 = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(var1));
      ItemModel.Unbaked var3 = ItemModelUtils.plainModel(ModelLocationUtils.decorateItemModelLocation("tooting_goat_horn"));
      this.generateBooleanDispatch(var1, ItemModelUtils.isUsingItem(), var3, var2);
   }

   private void generateShield(Item var1) {
      ItemModel.Unbaked var2 = ItemModelUtils.specialModel(ModelLocationUtils.getModelLocation(var1), new ShieldSpecialRenderer.Unbaked());
      ItemModel.Unbaked var3 = ItemModelUtils.specialModel(ModelLocationUtils.getModelLocation(var1, "_blocking"), new ShieldSpecialRenderer.Unbaked());
      this.generateBooleanDispatch(var1, ItemModelUtils.isUsingItem(), var3, var2);
   }

   private static ItemModel.Unbaked createFlatModelDispatch(ItemModel.Unbaked var0, ItemModel.Unbaked var1) {
      return ItemModelUtils.select(new DisplayContext(), var1, ItemModelUtils.when(List.of(ItemDisplayContext.GUI, ItemDisplayContext.GROUND, ItemDisplayContext.FIXED), var0));
   }

   private void generateSpyglass(Item var1) {
      ItemModel.Unbaked var2 = ItemModelUtils.plainModel(this.createFlatItemModel(var1, ModelTemplates.FLAT_ITEM));
      ItemModel.Unbaked var3 = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(var1, "_in_hand"));
      this.itemModelOutput.accept(var1, createFlatModelDispatch(var2, var3));
   }

   private void generateTrident(Item var1) {
      ItemModel.Unbaked var2 = ItemModelUtils.plainModel(this.createFlatItemModel(var1, ModelTemplates.FLAT_ITEM));
      ItemModel.Unbaked var3 = ItemModelUtils.specialModel(ModelLocationUtils.getModelLocation(var1, "_in_hand"), new TridentSpecialRenderer.Unbaked());
      ItemModel.Unbaked var4 = ItemModelUtils.specialModel(ModelLocationUtils.getModelLocation(var1, "_throwing"), new TridentSpecialRenderer.Unbaked());
      ItemModel.Unbaked var5 = ItemModelUtils.conditional(ItemModelUtils.isUsingItem(), var4, var3);
      this.itemModelOutput.accept(var1, createFlatModelDispatch(var2, var5));
   }

   private void addPotionTint(Item var1, ResourceLocation var2) {
      this.itemModelOutput.accept(var1, ItemModelUtils.tintedModel(var2, new Potion()));
   }

   private void generatePotion(Item var1) {
      ResourceLocation var2 = this.generateLayeredItem(var1, ModelLocationUtils.decorateItemModelLocation("potion_overlay"), ModelLocationUtils.getModelLocation(var1));
      this.addPotionTint(var1, var2);
   }

   private void generateTippedArrow(Item var1) {
      ResourceLocation var2 = this.generateLayeredItem(var1, ModelLocationUtils.getModelLocation(var1, "_head"), ModelLocationUtils.getModelLocation(var1, "_base"));
      this.addPotionTint(var1, var2);
   }

   private void generateDyedItem(Item var1, int var2) {
      ResourceLocation var3 = this.createFlatItemModel(var1, ModelTemplates.FLAT_ITEM);
      this.itemModelOutput.accept(var1, ItemModelUtils.tintedModel(var3, new Dye(var2)));
   }

   private void generateSpawnEgg(Item var1, int var2, int var3) {
      ResourceLocation var4 = ModelLocationUtils.decorateItemModelLocation("template_spawn_egg");
      this.itemModelOutput.accept(var1, ItemModelUtils.tintedModel(var4, ItemModelUtils.constantTint(var2), ItemModelUtils.constantTint(var3)));
   }

   private void generateWolfArmor(Item var1) {
      ResourceLocation var2 = TextureMapping.getItemTexture(var1);
      ResourceLocation var3 = TextureMapping.getItemTexture(var1, "_overlay");
      ResourceLocation var4 = ModelTemplates.FLAT_ITEM.create(var1, TextureMapping.layer0(var2), this.modelOutput);
      ResourceLocation var5 = ModelLocationUtils.getModelLocation(var1, "_dyed");
      ModelTemplates.TWO_LAYERED_ITEM.create(var5, TextureMapping.layered(var2, var3), this.modelOutput);
      this.itemModelOutput.accept(var1, ItemModelUtils.conditional(ItemModelUtils.hasComponent(DataComponents.DYED_COLOR), ItemModelUtils.tintedModel(var5, BLANK_LAYER, new Dye(0)), ItemModelUtils.plainModel(var4)));
   }

   public void run() {
      this.generateFlatItem(Items.ACACIA_BOAT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.CHERRY_BOAT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.ACACIA_CHEST_BOAT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.CHERRY_CHEST_BOAT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.AMETHYST_SHARD, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.APPLE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.ARMADILLO_SCUTE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.ARMOR_STAND, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.ARROW, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.BAKED_POTATO, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.BAMBOO, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.BEEF, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.BEETROOT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.BEETROOT_SOUP, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.BIRCH_BOAT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.BIRCH_CHEST_BOAT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.BLACK_DYE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.BLAZE_POWDER, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.BLAZE_ROD, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.BLUE_DYE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.BONE_MEAL, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.BORDURE_INDENTED_BANNER_PATTERN, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.BOOK, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.BOWL, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.BREAD, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.BRICK, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.BREEZE_ROD, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.BROWN_DYE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.BUCKET, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.CARROT_ON_A_STICK, ModelTemplates.FLAT_HANDHELD_ROD_ITEM);
      this.generateFlatItem(Items.WARPED_FUNGUS_ON_A_STICK, ModelTemplates.FLAT_HANDHELD_ROD_ITEM);
      this.generateFlatItem(Items.CHARCOAL, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.CHEST_MINECART, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.CHICKEN, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.CHORUS_FRUIT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.CLAY_BALL, ModelTemplates.FLAT_ITEM);
      this.generateClockItem(Items.CLOCK);
      this.generateFlatItem(Items.COAL, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.COD_BUCKET, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.COMMAND_BLOCK_MINECART, ModelTemplates.FLAT_ITEM);
      this.generateStandardCompassItem(Items.COMPASS);
      this.generateRecoveryCompassItem(Items.RECOVERY_COMPASS);
      this.generateFlatItem(Items.COOKED_BEEF, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.COOKED_CHICKEN, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.COOKED_COD, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.COOKED_MUTTON, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.COOKED_PORKCHOP, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.COOKED_RABBIT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.COOKED_SALMON, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.COOKIE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.RAW_COPPER, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.COPPER_INGOT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.CREEPER_BANNER_PATTERN, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.CYAN_DYE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.DARK_OAK_BOAT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.DARK_OAK_CHEST_BOAT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.DIAMOND, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.DIAMOND_AXE, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.DIAMOND_HOE, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.DIAMOND_HORSE_ARMOR, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.DIAMOND_PICKAXE, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.DIAMOND_SHOVEL, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.DIAMOND_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.DRAGON_BREATH, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.DRIED_KELP, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.EGG, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.EMERALD, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.ENCHANTED_BOOK, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.ENDER_EYE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.ENDER_PEARL, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.END_CRYSTAL, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.EXPERIENCE_BOTTLE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.FERMENTED_SPIDER_EYE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.FIELD_MASONED_BANNER_PATTERN, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.FIREWORK_ROCKET, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.FIRE_CHARGE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.FLINT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.FLINT_AND_STEEL, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.FLOW_BANNER_PATTERN, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.FLOWER_BANNER_PATTERN, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.FURNACE_MINECART, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.GHAST_TEAR, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.GLASS_BOTTLE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.GLISTERING_MELON_SLICE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.GLOBE_BANNER_PATTERN, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.GLOW_BERRIES, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.GLOWSTONE_DUST, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.GLOW_INK_SAC, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.GLOW_ITEM_FRAME, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.RAW_GOLD, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.GOLDEN_APPLE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.GOLDEN_AXE, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.GOLDEN_CARROT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.GOLDEN_HOE, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.GOLDEN_HORSE_ARMOR, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.GOLDEN_PICKAXE, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.GOLDEN_SHOVEL, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.GOLDEN_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.GOLD_INGOT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.GOLD_NUGGET, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.GRAY_DYE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.GREEN_DYE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.GUNPOWDER, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.GUSTER_BANNER_PATTERN, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.HEART_OF_THE_SEA, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.HONEYCOMB, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.HONEY_BOTTLE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.HOPPER_MINECART, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.INK_SAC, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.RAW_IRON, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.IRON_AXE, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.IRON_HOE, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.IRON_HORSE_ARMOR, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.IRON_INGOT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.IRON_NUGGET, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.IRON_PICKAXE, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.IRON_SHOVEL, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.IRON_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.ITEM_FRAME, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.JUNGLE_BOAT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.JUNGLE_CHEST_BOAT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.KNOWLEDGE_BOOK, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.LAPIS_LAZULI, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.LAVA_BUCKET, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.LEATHER, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.LIGHT_BLUE_DYE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.LIGHT_GRAY_DYE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.LIME_DYE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.MAGENTA_DYE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.MAGMA_CREAM, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.MANGROVE_BOAT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.MANGROVE_CHEST_BOAT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.BAMBOO_RAFT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.BAMBOO_CHEST_RAFT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.MAP, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.MELON_SLICE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.MILK_BUCKET, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.MINECART, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.MOJANG_BANNER_PATTERN, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.MUSHROOM_STEW, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.DISC_FRAGMENT_5, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.MUSIC_DISC_11, ModelTemplates.MUSIC_DISC);
      this.generateFlatItem(Items.MUSIC_DISC_13, ModelTemplates.MUSIC_DISC);
      this.generateFlatItem(Items.MUSIC_DISC_BLOCKS, ModelTemplates.MUSIC_DISC);
      this.generateFlatItem(Items.MUSIC_DISC_CAT, ModelTemplates.MUSIC_DISC);
      this.generateFlatItem(Items.MUSIC_DISC_CHIRP, ModelTemplates.MUSIC_DISC);
      this.generateFlatItem(Items.MUSIC_DISC_CREATOR, ModelTemplates.MUSIC_DISC);
      this.generateFlatItem(Items.MUSIC_DISC_CREATOR_MUSIC_BOX, ModelTemplates.MUSIC_DISC);
      this.generateFlatItem(Items.MUSIC_DISC_FAR, ModelTemplates.MUSIC_DISC);
      this.generateFlatItem(Items.MUSIC_DISC_MALL, ModelTemplates.MUSIC_DISC);
      this.generateFlatItem(Items.MUSIC_DISC_MELLOHI, ModelTemplates.MUSIC_DISC);
      this.generateFlatItem(Items.MUSIC_DISC_PIGSTEP, ModelTemplates.MUSIC_DISC);
      this.generateFlatItem(Items.MUSIC_DISC_PRECIPICE, ModelTemplates.MUSIC_DISC);
      this.generateFlatItem(Items.MUSIC_DISC_STAL, ModelTemplates.MUSIC_DISC);
      this.generateFlatItem(Items.MUSIC_DISC_STRAD, ModelTemplates.MUSIC_DISC);
      this.generateFlatItem(Items.MUSIC_DISC_WAIT, ModelTemplates.MUSIC_DISC);
      this.generateFlatItem(Items.MUSIC_DISC_WARD, ModelTemplates.MUSIC_DISC);
      this.generateFlatItem(Items.MUSIC_DISC_OTHERSIDE, ModelTemplates.MUSIC_DISC);
      this.generateFlatItem(Items.MUSIC_DISC_RELIC, ModelTemplates.MUSIC_DISC);
      this.generateFlatItem(Items.MUSIC_DISC_5, ModelTemplates.MUSIC_DISC);
      this.generateFlatItem(Items.MUTTON, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.NAME_TAG, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.NAUTILUS_SHELL, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.NETHERITE_AXE, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.NETHERITE_HOE, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.NETHERITE_INGOT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.NETHERITE_PICKAXE, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.NETHERITE_SCRAP, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.NETHERITE_SHOVEL, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.NETHERITE_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.NETHER_BRICK, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.RESIN_BRICK, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.NETHER_STAR, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.OAK_BOAT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.OAK_CHEST_BOAT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.ORANGE_DYE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.PAINTING, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.PALE_OAK_BOAT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.PALE_OAK_CHEST_BOAT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.PAPER, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.PHANTOM_MEMBRANE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.PIGLIN_BANNER_PATTERN, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.PINK_DYE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.POISONOUS_POTATO, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.POPPED_CHORUS_FRUIT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.PORKCHOP, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.POWDER_SNOW_BUCKET, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.PRISMARINE_CRYSTALS, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.PRISMARINE_SHARD, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.PUFFERFISH, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.PUFFERFISH_BUCKET, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.PUMPKIN_PIE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.PURPLE_DYE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.QUARTZ, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.RABBIT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.RABBIT_FOOT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.RABBIT_HIDE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.RABBIT_STEW, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.RED_DYE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.ROTTEN_FLESH, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.SADDLE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.SALMON, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.SALMON_BUCKET, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.TURTLE_SCUTE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.SHEARS, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.SHULKER_SHELL, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.SKULL_BANNER_PATTERN, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.SLIME_BALL, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.SNOWBALL, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.ECHO_SHARD, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.SPECTRAL_ARROW, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.SPIDER_EYE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.SPRUCE_BOAT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.SPRUCE_CHEST_BOAT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.STICK, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.STONE_AXE, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.STONE_HOE, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.STONE_PICKAXE, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.STONE_SHOVEL, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.STONE_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.SUGAR, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.SUSPICIOUS_STEW, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.TNT_MINECART, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.TOTEM_OF_UNDYING, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.TROPICAL_FISH, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.TROPICAL_FISH_BUCKET, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.AXOLOTL_BUCKET, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.TADPOLE_BUCKET, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.WATER_BUCKET, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.WHEAT, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.WHITE_DYE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.WIND_CHARGE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.MACE, ModelTemplates.FLAT_HANDHELD_MACE_ITEM);
      this.generateFlatItem(Items.WOODEN_AXE, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.WOODEN_HOE, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.WOODEN_PICKAXE, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.WOODEN_SHOVEL, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.WOODEN_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.WRITABLE_BOOK, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.WRITTEN_BOOK, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.YELLOW_DYE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.DEBUG_STICK, Items.STICK, ModelTemplates.FLAT_HANDHELD_ITEM);
      this.generateFlatItem(Items.ENCHANTED_GOLDEN_APPLE, Items.GOLDEN_APPLE, ModelTemplates.FLAT_ITEM);
      this.generateTrimmableItem(Items.TURTLE_HELMET, EquipmentAssets.TURTLE_SCUTE, "helmet", false);
      this.generateTrimmableItem(Items.LEATHER_HELMET, EquipmentAssets.LEATHER, "helmet", true);
      this.generateTrimmableItem(Items.LEATHER_CHESTPLATE, EquipmentAssets.LEATHER, "chestplate", true);
      this.generateTrimmableItem(Items.LEATHER_LEGGINGS, EquipmentAssets.LEATHER, "leggings", true);
      this.generateTrimmableItem(Items.LEATHER_BOOTS, EquipmentAssets.LEATHER, "boots", true);
      this.generateTrimmableItem(Items.CHAINMAIL_HELMET, EquipmentAssets.CHAINMAIL, "helmet", false);
      this.generateTrimmableItem(Items.CHAINMAIL_CHESTPLATE, EquipmentAssets.CHAINMAIL, "chestplate", false);
      this.generateTrimmableItem(Items.CHAINMAIL_LEGGINGS, EquipmentAssets.CHAINMAIL, "leggings", false);
      this.generateTrimmableItem(Items.CHAINMAIL_BOOTS, EquipmentAssets.CHAINMAIL, "boots", false);
      this.generateTrimmableItem(Items.IRON_HELMET, EquipmentAssets.IRON, "helmet", false);
      this.generateTrimmableItem(Items.IRON_CHESTPLATE, EquipmentAssets.IRON, "chestplate", false);
      this.generateTrimmableItem(Items.IRON_LEGGINGS, EquipmentAssets.IRON, "leggings", false);
      this.generateTrimmableItem(Items.IRON_BOOTS, EquipmentAssets.IRON, "boots", false);
      this.generateTrimmableItem(Items.DIAMOND_HELMET, EquipmentAssets.DIAMOND, "helmet", false);
      this.generateTrimmableItem(Items.DIAMOND_CHESTPLATE, EquipmentAssets.DIAMOND, "chestplate", false);
      this.generateTrimmableItem(Items.DIAMOND_LEGGINGS, EquipmentAssets.DIAMOND, "leggings", false);
      this.generateTrimmableItem(Items.DIAMOND_BOOTS, EquipmentAssets.DIAMOND, "boots", false);
      this.generateTrimmableItem(Items.GOLDEN_HELMET, EquipmentAssets.GOLD, "helmet", false);
      this.generateTrimmableItem(Items.GOLDEN_CHESTPLATE, EquipmentAssets.GOLD, "chestplate", false);
      this.generateTrimmableItem(Items.GOLDEN_LEGGINGS, EquipmentAssets.GOLD, "leggings", false);
      this.generateTrimmableItem(Items.GOLDEN_BOOTS, EquipmentAssets.GOLD, "boots", false);
      this.generateTrimmableItem(Items.NETHERITE_HELMET, EquipmentAssets.NETHERITE, "helmet", false);
      this.generateTrimmableItem(Items.NETHERITE_CHESTPLATE, EquipmentAssets.NETHERITE, "chestplate", false);
      this.generateTrimmableItem(Items.NETHERITE_LEGGINGS, EquipmentAssets.NETHERITE, "leggings", false);
      this.generateTrimmableItem(Items.NETHERITE_BOOTS, EquipmentAssets.NETHERITE, "boots", false);
      this.generateDyedItem(Items.LEATHER_HORSE_ARMOR, -6265536);
      this.generateFlatItem(Items.ANGLER_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.ARCHER_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.ARMS_UP_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.BLADE_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.BREWER_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.BURN_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.DANGER_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.EXPLORER_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.FLOW_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.FRIEND_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.GUSTER_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.HEART_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.HEARTBREAK_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.HOWL_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.MINER_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.MOURNER_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.PLENTY_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.PRIZE_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.SCRAPE_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.SHEAF_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.SHELTER_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.SKULL_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.SNORT_POTTERY_SHERD, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.TRIAL_KEY, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.OMINOUS_TRIAL_KEY, ModelTemplates.FLAT_ITEM);
      this.generateFlatItem(Items.OMINOUS_BOTTLE, ModelTemplates.FLAT_ITEM);
      this.generateItemWithTintedOverlay(Items.FIREWORK_STAR, new Firework());
      this.generateItemWithTintedOverlay(Items.FILLED_MAP, "_markings", new MapColor());
      this.generateBundleModels(Items.BUNDLE);
      this.generateBundleModels(Items.BLACK_BUNDLE);
      this.generateBundleModels(Items.WHITE_BUNDLE);
      this.generateBundleModels(Items.GRAY_BUNDLE);
      this.generateBundleModels(Items.LIGHT_GRAY_BUNDLE);
      this.generateBundleModels(Items.LIGHT_BLUE_BUNDLE);
      this.generateBundleModels(Items.BLUE_BUNDLE);
      this.generateBundleModels(Items.CYAN_BUNDLE);
      this.generateBundleModels(Items.YELLOW_BUNDLE);
      this.generateBundleModels(Items.RED_BUNDLE);
      this.generateBundleModels(Items.PURPLE_BUNDLE);
      this.generateBundleModels(Items.MAGENTA_BUNDLE);
      this.generateBundleModels(Items.PINK_BUNDLE);
      this.generateBundleModels(Items.GREEN_BUNDLE);
      this.generateBundleModels(Items.LIME_BUNDLE);
      this.generateBundleModels(Items.BROWN_BUNDLE);
      this.generateBundleModels(Items.ORANGE_BUNDLE);
      this.generateSpyglass(Items.SPYGLASS);
      this.generateTrident(Items.TRIDENT);
      this.generateWolfArmor(Items.WOLF_ARMOR);
      this.generateBow(Items.BOW);
      this.generateCrossbow(Items.CROSSBOW);
      this.generateElytra(Items.ELYTRA);
      this.generateBrush(Items.BRUSH);
      this.generateFishingRod(Items.FISHING_ROD);
      this.generateGoatHorn(Items.GOAT_HORN);
      this.generateShield(Items.SHIELD);
      this.generateTippedArrow(Items.TIPPED_ARROW);
      this.generatePotion(Items.POTION);
      this.generatePotion(Items.SPLASH_POTION);
      this.generatePotion(Items.LINGERING_POTION);
      this.generateSpawnEgg(Items.ARMADILLO_SPAWN_EGG, 11366765, 8538184);
      this.generateSpawnEgg(Items.ALLAY_SPAWN_EGG, 56063, 44543);
      this.generateSpawnEgg(Items.AXOLOTL_SPAWN_EGG, 16499171, 10890612);
      this.generateSpawnEgg(Items.BAT_SPAWN_EGG, 4996656, 986895);
      this.generateSpawnEgg(Items.BEE_SPAWN_EGG, 15582019, 4400155);
      this.generateSpawnEgg(Items.BLAZE_SPAWN_EGG, 16167425, 16775294);
      this.generateSpawnEgg(Items.BOGGED_SPAWN_EGG, 9084018, 3231003);
      this.generateSpawnEgg(Items.BREEZE_SPAWN_EGG, 11506911, 9529055);
      this.generateSpawnEgg(Items.CAT_SPAWN_EGG, 15714446, 9794134);
      this.generateSpawnEgg(Items.CAMEL_SPAWN_EGG, 16565097, 13341495);
      this.generateSpawnEgg(Items.CAVE_SPIDER_SPAWN_EGG, 803406, 11013646);
      this.generateSpawnEgg(Items.CHICKEN_SPAWN_EGG, 10592673, 16711680);
      this.generateSpawnEgg(Items.COD_SPAWN_EGG, 12691306, 15058059);
      this.generateSpawnEgg(Items.COW_SPAWN_EGG, 4470310, 10592673);
      this.generateSpawnEgg(Items.CREEPER_SPAWN_EGG, 894731, 0);
      this.generateSpawnEgg(Items.DOLPHIN_SPAWN_EGG, 2243405, 16382457);
      this.generateSpawnEgg(Items.DONKEY_SPAWN_EGG, 5457209, 8811878);
      this.generateSpawnEgg(Items.DROWNED_SPAWN_EGG, 9433559, 7969893);
      this.generateSpawnEgg(Items.ELDER_GUARDIAN_SPAWN_EGG, 13552826, 7632531);
      this.generateSpawnEgg(Items.ENDER_DRAGON_SPAWN_EGG, 1842204, 14711290);
      this.generateSpawnEgg(Items.ENDERMAN_SPAWN_EGG, 1447446, 0);
      this.generateSpawnEgg(Items.ENDERMITE_SPAWN_EGG, 1447446, 7237230);
      this.generateSpawnEgg(Items.EVOKER_SPAWN_EGG, 9804699, 1973274);
      this.generateSpawnEgg(Items.FOX_SPAWN_EGG, 14005919, 13396256);
      this.generateSpawnEgg(Items.FROG_SPAWN_EGG, 13661252, 16762748);
      this.generateSpawnEgg(Items.GHAST_SPAWN_EGG, 16382457, 12369084);
      this.generateSpawnEgg(Items.GLOW_SQUID_SPAWN_EGG, 611926, 8778172);
      this.generateSpawnEgg(Items.GOAT_SPAWN_EGG, 10851452, 5589310);
      this.generateSpawnEgg(Items.GUARDIAN_SPAWN_EGG, 5931634, 15826224);
      this.generateSpawnEgg(Items.HOGLIN_SPAWN_EGG, 13004373, 6251620);
      this.generateSpawnEgg(Items.HORSE_SPAWN_EGG, 12623485, 15656192);
      this.generateSpawnEgg(Items.HUSK_SPAWN_EGG, 7958625, 15125652);
      this.generateSpawnEgg(Items.IRON_GOLEM_SPAWN_EGG, 14405058, 7643954);
      this.generateSpawnEgg(Items.LLAMA_SPAWN_EGG, 12623485, 10051392);
      this.generateSpawnEgg(Items.MAGMA_CUBE_SPAWN_EGG, 3407872, 16579584);
      this.generateSpawnEgg(Items.MOOSHROOM_SPAWN_EGG, 10489616, 12040119);
      this.generateSpawnEgg(Items.MULE_SPAWN_EGG, 1769984, 5321501);
      this.generateSpawnEgg(Items.OCELOT_SPAWN_EGG, 15720061, 5653556);
      this.generateSpawnEgg(Items.PANDA_SPAWN_EGG, 15198183, 1776418);
      this.generateSpawnEgg(Items.PARROT_SPAWN_EGG, 894731, 16711680);
      this.generateSpawnEgg(Items.PHANTOM_SPAWN_EGG, 4411786, 8978176);
      this.generateSpawnEgg(Items.PIG_SPAWN_EGG, 15771042, 14377823);
      this.generateSpawnEgg(Items.PIGLIN_SPAWN_EGG, 10051392, 16380836);
      this.generateSpawnEgg(Items.PIGLIN_BRUTE_SPAWN_EGG, 5843472, 16380836);
      this.generateSpawnEgg(Items.PILLAGER_SPAWN_EGG, 5451574, 9804699);
      this.generateSpawnEgg(Items.POLAR_BEAR_SPAWN_EGG, 15658718, 14014157);
      this.generateSpawnEgg(Items.PUFFERFISH_SPAWN_EGG, 16167425, 3654642);
      this.generateSpawnEgg(Items.RABBIT_SPAWN_EGG, 10051392, 7555121);
      this.generateSpawnEgg(Items.RAVAGER_SPAWN_EGG, 7697520, 5984329);
      this.generateSpawnEgg(Items.SALMON_SPAWN_EGG, 10489616, 951412);
      this.generateSpawnEgg(Items.SHEEP_SPAWN_EGG, 15198183, 16758197);
      this.generateSpawnEgg(Items.SHULKER_SPAWN_EGG, 9725844, 5060690);
      this.generateSpawnEgg(Items.SILVERFISH_SPAWN_EGG, 7237230, 3158064);
      this.generateSpawnEgg(Items.SKELETON_SPAWN_EGG, 12698049, 4802889);
      this.generateSpawnEgg(Items.SKELETON_HORSE_SPAWN_EGG, 6842447, 15066584);
      this.generateSpawnEgg(Items.SLIME_SPAWN_EGG, 5349438, 8306542);
      this.generateSpawnEgg(Items.SNIFFER_SPAWN_EGG, 8855049, 2468720);
      this.generateSpawnEgg(Items.SNOW_GOLEM_SPAWN_EGG, 14283506, 8496292);
      this.generateSpawnEgg(Items.SPIDER_SPAWN_EGG, 3419431, 11013646);
      this.generateSpawnEgg(Items.SQUID_SPAWN_EGG, 2243405, 7375001);
      this.generateSpawnEgg(Items.STRAY_SPAWN_EGG, 6387319, 14543594);
      this.generateSpawnEgg(Items.STRIDER_SPAWN_EGG, 10236982, 5065037);
      this.generateSpawnEgg(Items.TADPOLE_SPAWN_EGG, 7164733, 1444352);
      this.generateSpawnEgg(Items.TRADER_LLAMA_SPAWN_EGG, 15377456, 4547222);
      this.generateSpawnEgg(Items.TROPICAL_FISH_SPAWN_EGG, 15690005, 16775663);
      this.generateSpawnEgg(Items.TURTLE_SPAWN_EGG, 15198183, 44975);
      this.generateSpawnEgg(Items.VEX_SPAWN_EGG, 8032420, 15265265);
      this.generateSpawnEgg(Items.VILLAGER_SPAWN_EGG, 5651507, 12422002);
      this.generateSpawnEgg(Items.VINDICATOR_SPAWN_EGG, 9804699, 2580065);
      this.generateSpawnEgg(Items.WANDERING_TRADER_SPAWN_EGG, 4547222, 15377456);
      this.generateSpawnEgg(Items.WARDEN_SPAWN_EGG, 1001033, 3790560);
      this.generateSpawnEgg(Items.WITCH_SPAWN_EGG, 3407872, 5349438);
      this.generateSpawnEgg(Items.WITHER_SPAWN_EGG, 1315860, 5075616);
      this.generateSpawnEgg(Items.WITHER_SKELETON_SPAWN_EGG, 1315860, 4672845);
      this.generateSpawnEgg(Items.WOLF_SPAWN_EGG, 14144467, 13545366);
      this.generateSpawnEgg(Items.ZOGLIN_SPAWN_EGG, 13004373, 15132390);
      this.generateSpawnEgg(Items.CREAKING_SPAWN_EGG, 6250335, 16545810);
      this.generateSpawnEgg(Items.ZOMBIE_SPAWN_EGG, 44975, 7969893);
      this.generateSpawnEgg(Items.ZOMBIE_HORSE_SPAWN_EGG, 3232308, 9945732);
      this.generateSpawnEgg(Items.ZOMBIE_VILLAGER_SPAWN_EGG, 5651507, 7969893);
      this.generateSpawnEgg(Items.ZOMBIFIED_PIGLIN_SPAWN_EGG, 15373203, 5009705);
      this.declareCustomModelItem(Items.AIR);
      this.declareCustomModelItem(Items.AMETHYST_CLUSTER);
      this.declareCustomModelItem(Items.SMALL_AMETHYST_BUD);
      this.declareCustomModelItem(Items.MEDIUM_AMETHYST_BUD);
      this.declareCustomModelItem(Items.LARGE_AMETHYST_BUD);
      this.declareCustomModelItem(Items.SMALL_DRIPLEAF);
      this.declareCustomModelItem(Items.BIG_DRIPLEAF);
      this.declareCustomModelItem(Items.HANGING_ROOTS);
      this.declareCustomModelItem(Items.POINTED_DRIPSTONE);
      this.declareCustomModelItem(Items.BONE);
      this.declareCustomModelItem(Items.COD);
      this.declareCustomModelItem(Items.FEATHER);
      this.declareCustomModelItem(Items.LEAD);
   }

   static {
      TRIM_MATERIAL_MODELS = List.of(new TrimMaterialData("quartz", TrimMaterials.QUARTZ, Map.of()), new TrimMaterialData("iron", TrimMaterials.IRON, Map.of(EquipmentAssets.IRON, "iron_darker")), new TrimMaterialData("netherite", TrimMaterials.NETHERITE, Map.of(EquipmentAssets.NETHERITE, "netherite_darker")), new TrimMaterialData("redstone", TrimMaterials.REDSTONE, Map.of()), new TrimMaterialData("copper", TrimMaterials.COPPER, Map.of()), new TrimMaterialData("gold", TrimMaterials.GOLD, Map.of(EquipmentAssets.GOLD, "gold_darker")), new TrimMaterialData("emerald", TrimMaterials.EMERALD, Map.of()), new TrimMaterialData("diamond", TrimMaterials.DIAMOND, Map.of(EquipmentAssets.DIAMOND, "diamond_darker")), new TrimMaterialData("lapis", TrimMaterials.LAPIS, Map.of()), new TrimMaterialData("amethyst", TrimMaterials.AMETHYST, Map.of()), new TrimMaterialData("resin", TrimMaterials.RESIN, Map.of()));
   }

   static record TrimMaterialData(String name, ResourceKey<TrimMaterial> materialKey, Map<ResourceKey<EquipmentAsset>, String> overrideArmorMaterials) {
      final ResourceKey<TrimMaterial> materialKey;

      TrimMaterialData(String var1, ResourceKey<TrimMaterial> var2, Map<ResourceKey<EquipmentAsset>, String> var3) {
         super();
         this.name = var1;
         this.materialKey = var2;
         this.overrideArmorMaterials = var3;
      }

      public String textureName(ResourceKey<EquipmentAsset> var1) {
         return (String)this.overrideArmorMaterials.getOrDefault(var1, this.name);
      }
   }
}
