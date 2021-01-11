package net.minecraft.client.resources.model;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockPart;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.block.model.ModelBlockDefinition;
import net.minecraft.client.renderer.texture.IIconCreator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IRegistry;
import net.minecraft.util.RegistrySimple;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModelBakery {
   private static final Set<ResourceLocation> field_177602_b = Sets.newHashSet(new ResourceLocation[]{new ResourceLocation("blocks/water_flow"), new ResourceLocation("blocks/water_still"), new ResourceLocation("blocks/lava_flow"), new ResourceLocation("blocks/lava_still"), new ResourceLocation("blocks/destroy_stage_0"), new ResourceLocation("blocks/destroy_stage_1"), new ResourceLocation("blocks/destroy_stage_2"), new ResourceLocation("blocks/destroy_stage_3"), new ResourceLocation("blocks/destroy_stage_4"), new ResourceLocation("blocks/destroy_stage_5"), new ResourceLocation("blocks/destroy_stage_6"), new ResourceLocation("blocks/destroy_stage_7"), new ResourceLocation("blocks/destroy_stage_8"), new ResourceLocation("blocks/destroy_stage_9"), new ResourceLocation("items/empty_armor_slot_helmet"), new ResourceLocation("items/empty_armor_slot_chestplate"), new ResourceLocation("items/empty_armor_slot_leggings"), new ResourceLocation("items/empty_armor_slot_boots")});
   private static final Logger field_177603_c = LogManager.getLogger();
   protected static final ModelResourceLocation field_177604_a = new ModelResourceLocation("builtin/missing", "missing");
   private static final Map<String, String> field_177600_d = Maps.newHashMap();
   private static final Joiner field_177601_e;
   private final IResourceManager field_177598_f;
   private final Map<ResourceLocation, TextureAtlasSprite> field_177599_g = Maps.newHashMap();
   private final Map<ResourceLocation, ModelBlock> field_177611_h = Maps.newLinkedHashMap();
   private final Map<ModelResourceLocation, ModelBlockDefinition.Variants> field_177612_i = Maps.newLinkedHashMap();
   private final TextureMap field_177609_j;
   private final BlockModelShapes field_177610_k;
   private final FaceBakery field_177607_l = new FaceBakery();
   private final ItemModelGenerator field_177608_m = new ItemModelGenerator();
   private RegistrySimple<ModelResourceLocation, IBakedModel> field_177605_n = new RegistrySimple();
   private static final ModelBlock field_177606_o;
   private static final ModelBlock field_177618_p;
   private static final ModelBlock field_177617_q;
   private static final ModelBlock field_177616_r;
   private Map<String, ResourceLocation> field_177615_s = Maps.newLinkedHashMap();
   private final Map<ResourceLocation, ModelBlockDefinition> field_177614_t = Maps.newHashMap();
   private Map<Item, List<String>> field_177613_u = Maps.newIdentityHashMap();

   public ModelBakery(IResourceManager var1, TextureMap var2, BlockModelShapes var3) {
      super();
      this.field_177598_f = var1;
      this.field_177609_j = var2;
      this.field_177610_k = var3;
   }

   public IRegistry<ModelResourceLocation, IBakedModel> func_177570_a() {
      this.func_177577_b();
      this.func_177597_h();
      this.func_177572_j();
      this.func_177593_l();
      this.func_177588_f();
      return this.field_177605_n;
   }

   private void func_177577_b() {
      this.func_177591_a(this.field_177610_k.func_178120_a().func_178446_a().values());
      this.field_177612_i.put(field_177604_a, new ModelBlockDefinition.Variants(field_177604_a.func_177518_c(), Lists.newArrayList(new ModelBlockDefinition.Variant[]{new ModelBlockDefinition.Variant(new ResourceLocation(field_177604_a.func_110623_a()), ModelRotation.X0_Y0, false, 1)})));
      ResourceLocation var1 = new ResourceLocation("item_frame");
      ModelBlockDefinition var2 = this.func_177586_a(var1);
      this.func_177569_a(var2, new ModelResourceLocation(var1, "normal"));
      this.func_177569_a(var2, new ModelResourceLocation(var1, "map"));
      this.func_177595_c();
      this.func_177590_d();
   }

   private void func_177591_a(Collection<ModelResourceLocation> var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         ModelResourceLocation var3 = (ModelResourceLocation)var2.next();

         try {
            ModelBlockDefinition var4 = this.func_177586_a(var3);

            try {
               this.func_177569_a(var4, var3);
            } catch (Exception var6) {
               field_177603_c.warn("Unable to load variant: " + var3.func_177518_c() + " from " + var3);
            }
         } catch (Exception var7) {
            field_177603_c.warn("Unable to load definition " + var3, var7);
         }
      }

   }

   private void func_177569_a(ModelBlockDefinition var1, ModelResourceLocation var2) {
      this.field_177612_i.put(var2, var1.func_178330_b(var2.func_177518_c()));
   }

   private ModelBlockDefinition func_177586_a(ResourceLocation var1) {
      ResourceLocation var2 = this.func_177584_b(var1);
      ModelBlockDefinition var3 = (ModelBlockDefinition)this.field_177614_t.get(var2);
      if (var3 == null) {
         ArrayList var4 = Lists.newArrayList();

         try {
            Iterator var5 = this.field_177598_f.func_135056_b(var2).iterator();

            while(var5.hasNext()) {
               IResource var6 = (IResource)var5.next();
               InputStream var7 = null;

               try {
                  var7 = var6.func_110527_b();
                  ModelBlockDefinition var8 = ModelBlockDefinition.func_178331_a(new InputStreamReader(var7, Charsets.UTF_8));
                  var4.add(var8);
               } catch (Exception var13) {
                  throw new RuntimeException("Encountered an exception when loading model definition of '" + var1 + "' from: '" + var6.func_177241_a() + "' in resourcepack: '" + var6.func_177240_d() + "'", var13);
               } finally {
                  IOUtils.closeQuietly(var7);
               }
            }
         } catch (IOException var15) {
            throw new RuntimeException("Encountered an exception when loading model definition of model " + var2.toString(), var15);
         }

         var3 = new ModelBlockDefinition(var4);
         this.field_177614_t.put(var2, var3);
      }

      return var3;
   }

   private ResourceLocation func_177584_b(ResourceLocation var1) {
      return new ResourceLocation(var1.func_110624_b(), "blockstates/" + var1.func_110623_a() + ".json");
   }

   private void func_177595_c() {
      Iterator var1 = this.field_177612_i.keySet().iterator();

      while(var1.hasNext()) {
         ModelResourceLocation var2 = (ModelResourceLocation)var1.next();
         Iterator var3 = ((ModelBlockDefinition.Variants)this.field_177612_i.get(var2)).func_178420_b().iterator();

         while(var3.hasNext()) {
            ModelBlockDefinition.Variant var4 = (ModelBlockDefinition.Variant)var3.next();
            ResourceLocation var5 = var4.func_178431_a();
            if (this.field_177611_h.get(var5) == null) {
               try {
                  ModelBlock var6 = this.func_177594_c(var5);
                  this.field_177611_h.put(var5, var6);
               } catch (Exception var7) {
                  field_177603_c.warn("Unable to load block model: '" + var5 + "' for variant: '" + var2 + "'", var7);
               }
            }
         }
      }

   }

   private ModelBlock func_177594_c(ResourceLocation var1) throws IOException {
      String var3 = var1.func_110623_a();
      if ("builtin/generated".equals(var3)) {
         return field_177606_o;
      } else if ("builtin/compass".equals(var3)) {
         return field_177618_p;
      } else if ("builtin/clock".equals(var3)) {
         return field_177617_q;
      } else if ("builtin/entity".equals(var3)) {
         return field_177616_r;
      } else {
         Object var2;
         if (var3.startsWith("builtin/")) {
            String var4 = var3.substring("builtin/".length());
            String var5 = (String)field_177600_d.get(var4);
            if (var5 == null) {
               throw new FileNotFoundException(var1.toString());
            }

            var2 = new StringReader(var5);
         } else {
            IResource var9 = this.field_177598_f.func_110536_a(this.func_177580_d(var1));
            var2 = new InputStreamReader(var9.func_110527_b(), Charsets.UTF_8);
         }

         ModelBlock var11;
         try {
            ModelBlock var10 = ModelBlock.func_178307_a((Reader)var2);
            var10.field_178317_b = var1.toString();
            var11 = var10;
         } finally {
            ((Reader)var2).close();
         }

         return var11;
      }
   }

   private ResourceLocation func_177580_d(ResourceLocation var1) {
      return new ResourceLocation(var1.func_110624_b(), "models/" + var1.func_110623_a() + ".json");
   }

   private void func_177590_d() {
      this.func_177592_e();
      Iterator var1 = Item.field_150901_e.iterator();

      while(var1.hasNext()) {
         Item var2 = (Item)var1.next();
         List var3 = this.func_177596_a(var2);
         Iterator var4 = var3.iterator();

         while(var4.hasNext()) {
            String var5 = (String)var4.next();
            ResourceLocation var6 = this.func_177583_a(var5);
            this.field_177615_s.put(var5, var6);
            if (this.field_177611_h.get(var6) == null) {
               try {
                  ModelBlock var7 = this.func_177594_c(var6);
                  this.field_177611_h.put(var6, var7);
               } catch (Exception var8) {
                  field_177603_c.warn("Unable to load item model: '" + var6 + "' for item: '" + Item.field_150901_e.func_177774_c(var2) + "'", var8);
               }
            }
         }
      }

   }

   private void func_177592_e() {
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150348_b), Lists.newArrayList(new String[]{"stone", "granite", "granite_smooth", "diorite", "diorite_smooth", "andesite", "andesite_smooth"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150346_d), Lists.newArrayList(new String[]{"dirt", "coarse_dirt", "podzol"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150344_f), Lists.newArrayList(new String[]{"oak_planks", "spruce_planks", "birch_planks", "jungle_planks", "acacia_planks", "dark_oak_planks"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150345_g), Lists.newArrayList(new String[]{"oak_sapling", "spruce_sapling", "birch_sapling", "jungle_sapling", "acacia_sapling", "dark_oak_sapling"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150354_m), Lists.newArrayList(new String[]{"sand", "red_sand"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150364_r), Lists.newArrayList(new String[]{"oak_log", "spruce_log", "birch_log", "jungle_log"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150362_t), Lists.newArrayList(new String[]{"oak_leaves", "spruce_leaves", "birch_leaves", "jungle_leaves"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150360_v), Lists.newArrayList(new String[]{"sponge", "sponge_wet"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150322_A), Lists.newArrayList(new String[]{"sandstone", "chiseled_sandstone", "smooth_sandstone"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_180395_cM), Lists.newArrayList(new String[]{"red_sandstone", "chiseled_red_sandstone", "smooth_red_sandstone"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150329_H), Lists.newArrayList(new String[]{"dead_bush", "tall_grass", "fern"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150330_I), Lists.newArrayList(new String[]{"dead_bush"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150325_L), Lists.newArrayList(new String[]{"black_wool", "red_wool", "green_wool", "brown_wool", "blue_wool", "purple_wool", "cyan_wool", "silver_wool", "gray_wool", "pink_wool", "lime_wool", "yellow_wool", "light_blue_wool", "magenta_wool", "orange_wool", "white_wool"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150327_N), Lists.newArrayList(new String[]{"dandelion"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150328_O), Lists.newArrayList(new String[]{"poppy", "blue_orchid", "allium", "houstonia", "red_tulip", "orange_tulip", "white_tulip", "pink_tulip", "oxeye_daisy"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150333_U), Lists.newArrayList(new String[]{"stone_slab", "sandstone_slab", "cobblestone_slab", "brick_slab", "stone_brick_slab", "nether_brick_slab", "quartz_slab"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_180389_cP), Lists.newArrayList(new String[]{"red_sandstone_slab"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150399_cn), Lists.newArrayList(new String[]{"black_stained_glass", "red_stained_glass", "green_stained_glass", "brown_stained_glass", "blue_stained_glass", "purple_stained_glass", "cyan_stained_glass", "silver_stained_glass", "gray_stained_glass", "pink_stained_glass", "lime_stained_glass", "yellow_stained_glass", "light_blue_stained_glass", "magenta_stained_glass", "orange_stained_glass", "white_stained_glass"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150418_aU), Lists.newArrayList(new String[]{"stone_monster_egg", "cobblestone_monster_egg", "stone_brick_monster_egg", "mossy_brick_monster_egg", "cracked_brick_monster_egg", "chiseled_brick_monster_egg"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150417_aV), Lists.newArrayList(new String[]{"stonebrick", "mossy_stonebrick", "cracked_stonebrick", "chiseled_stonebrick"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150376_bx), Lists.newArrayList(new String[]{"oak_slab", "spruce_slab", "birch_slab", "jungle_slab", "acacia_slab", "dark_oak_slab"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150463_bK), Lists.newArrayList(new String[]{"cobblestone_wall", "mossy_cobblestone_wall"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150467_bQ), Lists.newArrayList(new String[]{"anvil_intact", "anvil_slightly_damaged", "anvil_very_damaged"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150371_ca), Lists.newArrayList(new String[]{"quartz_block", "chiseled_quartz_block", "quartz_column"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150406_ce), Lists.newArrayList(new String[]{"black_stained_hardened_clay", "red_stained_hardened_clay", "green_stained_hardened_clay", "brown_stained_hardened_clay", "blue_stained_hardened_clay", "purple_stained_hardened_clay", "cyan_stained_hardened_clay", "silver_stained_hardened_clay", "gray_stained_hardened_clay", "pink_stained_hardened_clay", "lime_stained_hardened_clay", "yellow_stained_hardened_clay", "light_blue_stained_hardened_clay", "magenta_stained_hardened_clay", "orange_stained_hardened_clay", "white_stained_hardened_clay"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150397_co), Lists.newArrayList(new String[]{"black_stained_glass_pane", "red_stained_glass_pane", "green_stained_glass_pane", "brown_stained_glass_pane", "blue_stained_glass_pane", "purple_stained_glass_pane", "cyan_stained_glass_pane", "silver_stained_glass_pane", "gray_stained_glass_pane", "pink_stained_glass_pane", "lime_stained_glass_pane", "yellow_stained_glass_pane", "light_blue_stained_glass_pane", "magenta_stained_glass_pane", "orange_stained_glass_pane", "white_stained_glass_pane"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150361_u), Lists.newArrayList(new String[]{"acacia_leaves", "dark_oak_leaves"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150363_s), Lists.newArrayList(new String[]{"acacia_log", "dark_oak_log"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_180397_cI), Lists.newArrayList(new String[]{"prismarine", "prismarine_bricks", "dark_prismarine"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150404_cg), Lists.newArrayList(new String[]{"black_carpet", "red_carpet", "green_carpet", "brown_carpet", "blue_carpet", "purple_carpet", "cyan_carpet", "silver_carpet", "gray_carpet", "pink_carpet", "lime_carpet", "yellow_carpet", "light_blue_carpet", "magenta_carpet", "orange_carpet", "white_carpet"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150398_cm), Lists.newArrayList(new String[]{"sunflower", "syringa", "double_grass", "double_fern", "double_rose", "paeonia"}));
      this.field_177613_u.put(Items.field_151031_f, Lists.newArrayList(new String[]{"bow", "bow_pulling_0", "bow_pulling_1", "bow_pulling_2"}));
      this.field_177613_u.put(Items.field_151044_h, Lists.newArrayList(new String[]{"coal", "charcoal"}));
      this.field_177613_u.put(Items.field_151112_aM, Lists.newArrayList(new String[]{"fishing_rod", "fishing_rod_cast"}));
      this.field_177613_u.put(Items.field_151115_aP, Lists.newArrayList(new String[]{"cod", "salmon", "clownfish", "pufferfish"}));
      this.field_177613_u.put(Items.field_179566_aV, Lists.newArrayList(new String[]{"cooked_cod", "cooked_salmon"}));
      this.field_177613_u.put(Items.field_151100_aR, Lists.newArrayList(new String[]{"dye_black", "dye_red", "dye_green", "dye_brown", "dye_blue", "dye_purple", "dye_cyan", "dye_silver", "dye_gray", "dye_pink", "dye_lime", "dye_yellow", "dye_light_blue", "dye_magenta", "dye_orange", "dye_white"}));
      this.field_177613_u.put(Items.field_151068_bn, Lists.newArrayList(new String[]{"bottle_drinkable", "bottle_splash"}));
      this.field_177613_u.put(Items.field_151144_bL, Lists.newArrayList(new String[]{"skull_skeleton", "skull_wither", "skull_zombie", "skull_char", "skull_creeper"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_180390_bo), Lists.newArrayList(new String[]{"oak_fence_gate"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_180407_aO), Lists.newArrayList(new String[]{"oak_fence"}));
      this.field_177613_u.put(Items.field_179570_aq, Lists.newArrayList(new String[]{"oak_door"}));
   }

   private List<String> func_177596_a(Item var1) {
      List var2 = (List)this.field_177613_u.get(var1);
      if (var2 == null) {
         var2 = Collections.singletonList(((ResourceLocation)Item.field_150901_e.func_177774_c(var1)).toString());
      }

      return var2;
   }

   private ResourceLocation func_177583_a(String var1) {
      ResourceLocation var2 = new ResourceLocation(var1);
      return new ResourceLocation(var2.func_110624_b(), "item/" + var2.func_110623_a());
   }

   private void func_177588_f() {
      Iterator var1 = this.field_177612_i.keySet().iterator();

      while(var1.hasNext()) {
         ModelResourceLocation var2 = (ModelResourceLocation)var1.next();
         WeightedBakedModel.Builder var3 = new WeightedBakedModel.Builder();
         int var4 = 0;
         Iterator var5 = ((ModelBlockDefinition.Variants)this.field_177612_i.get(var2)).func_178420_b().iterator();

         while(true) {
            while(var5.hasNext()) {
               ModelBlockDefinition.Variant var6 = (ModelBlockDefinition.Variant)var5.next();
               ModelBlock var7 = (ModelBlock)this.field_177611_h.get(var6.func_178431_a());
               if (var7 != null && var7.func_178303_d()) {
                  ++var4;
                  var3.func_177677_a(this.func_177578_a(var7, var6.func_178432_b(), var6.func_178433_c()), var6.func_178430_d());
               } else {
                  field_177603_c.warn("Missing model for: " + var2);
               }
            }

            if (var4 == 0) {
               field_177603_c.warn("No weighted models for: " + var2);
            } else if (var4 == 1) {
               this.field_177605_n.func_82595_a(var2, var3.func_177675_b());
            } else {
               this.field_177605_n.func_82595_a(var2, var3.func_177676_a());
            }
            break;
         }
      }

      var1 = this.field_177615_s.entrySet().iterator();

      while(true) {
         while(var1.hasNext()) {
            Entry var8 = (Entry)var1.next();
            ResourceLocation var9 = (ResourceLocation)var8.getValue();
            ModelResourceLocation var10 = new ModelResourceLocation((String)var8.getKey(), "inventory");
            ModelBlock var11 = (ModelBlock)this.field_177611_h.get(var9);
            if (var11 != null && var11.func_178303_d()) {
               if (this.func_177587_c(var11)) {
                  this.field_177605_n.func_82595_a(var10, new BuiltInModel(var11.func_181682_g()));
               } else {
                  this.field_177605_n.func_82595_a(var10, this.func_177578_a(var11, ModelRotation.X0_Y0, false));
               }
            } else {
               field_177603_c.warn("Missing model for: " + var9);
            }
         }

         return;
      }
   }

   private Set<ResourceLocation> func_177575_g() {
      HashSet var1 = Sets.newHashSet();
      ArrayList var2 = Lists.newArrayList(this.field_177612_i.keySet());
      Collections.sort(var2, new Comparator<ModelResourceLocation>() {
         public int compare(ModelResourceLocation var1, ModelResourceLocation var2) {
            return var1.toString().compareTo(var2.toString());
         }

         // $FF: synthetic method
         public int compare(Object var1, Object var2) {
            return this.compare((ModelResourceLocation)var1, (ModelResourceLocation)var2);
         }
      });
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         ModelResourceLocation var4 = (ModelResourceLocation)var3.next();
         ModelBlockDefinition.Variants var5 = (ModelBlockDefinition.Variants)this.field_177612_i.get(var4);
         Iterator var6 = var5.func_178420_b().iterator();

         while(var6.hasNext()) {
            ModelBlockDefinition.Variant var7 = (ModelBlockDefinition.Variant)var6.next();
            ModelBlock var8 = (ModelBlock)this.field_177611_h.get(var7.func_178431_a());
            if (var8 == null) {
               field_177603_c.warn("Missing model for: " + var4);
            } else {
               var1.addAll(this.func_177585_a(var8));
            }
         }
      }

      var1.addAll(field_177602_b);
      return var1;
   }

   private IBakedModel func_177578_a(ModelBlock var1, ModelRotation var2, boolean var3) {
      TextureAtlasSprite var4 = (TextureAtlasSprite)this.field_177599_g.get(new ResourceLocation(var1.func_178308_c("particle")));
      SimpleBakedModel.Builder var5 = (new SimpleBakedModel.Builder(var1)).func_177646_a(var4);
      Iterator var6 = var1.func_178298_a().iterator();

      while(var6.hasNext()) {
         BlockPart var7 = (BlockPart)var6.next();
         Iterator var8 = var7.field_178240_c.keySet().iterator();

         while(var8.hasNext()) {
            EnumFacing var9 = (EnumFacing)var8.next();
            BlockPartFace var10 = (BlockPartFace)var7.field_178240_c.get(var9);
            TextureAtlasSprite var11 = (TextureAtlasSprite)this.field_177599_g.get(new ResourceLocation(var1.func_178308_c(var10.field_178242_d)));
            if (var10.field_178244_b == null) {
               var5.func_177648_a(this.func_177589_a(var7, var10, var11, var9, var2, var3));
            } else {
               var5.func_177650_a(var2.func_177523_a(var10.field_178244_b), this.func_177589_a(var7, var10, var11, var9, var2, var3));
            }
         }
      }

      return var5.func_177645_b();
   }

   private BakedQuad func_177589_a(BlockPart var1, BlockPartFace var2, TextureAtlasSprite var3, EnumFacing var4, ModelRotation var5, boolean var6) {
      return this.field_177607_l.func_178414_a(var1.field_178241_a, var1.field_178239_b, var2, var3, var4, var5, var1.field_178237_d, var6, var1.field_178238_e);
   }

   private void func_177597_h() {
      this.func_177574_i();
      Iterator var1 = this.field_177611_h.values().iterator();

      while(var1.hasNext()) {
         ModelBlock var2 = (ModelBlock)var1.next();
         var2.func_178299_a(this.field_177611_h);
      }

      ModelBlock.func_178312_b(this.field_177611_h);
   }

   private void func_177574_i() {
      ArrayDeque var1 = Queues.newArrayDeque();
      HashSet var2 = Sets.newHashSet();
      Iterator var3 = this.field_177611_h.keySet().iterator();

      ResourceLocation var5;
      while(var3.hasNext()) {
         ResourceLocation var4 = (ResourceLocation)var3.next();
         var2.add(var4);
         var5 = ((ModelBlock)this.field_177611_h.get(var4)).func_178305_e();
         if (var5 != null) {
            var1.add(var5);
         }
      }

      while(!var1.isEmpty()) {
         ResourceLocation var7 = (ResourceLocation)var1.pop();

         try {
            if (this.field_177611_h.get(var7) != null) {
               continue;
            }

            ModelBlock var8 = this.func_177594_c(var7);
            this.field_177611_h.put(var7, var8);
            var5 = var8.func_178305_e();
            if (var5 != null && !var2.contains(var5)) {
               var1.add(var5);
            }
         } catch (Exception var6) {
            field_177603_c.warn("In parent chain: " + field_177601_e.join(this.func_177573_e(var7)) + "; unable to load model: '" + var7 + "'", var6);
         }

         var2.add(var7);
      }

   }

   private List<ResourceLocation> func_177573_e(ResourceLocation var1) {
      ArrayList var2 = Lists.newArrayList(new ResourceLocation[]{var1});
      ResourceLocation var3 = var1;

      while((var3 = this.func_177576_f(var3)) != null) {
         var2.add(0, var3);
      }

      return var2;
   }

   private ResourceLocation func_177576_f(ResourceLocation var1) {
      Iterator var2 = this.field_177611_h.entrySet().iterator();

      Entry var3;
      ModelBlock var4;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         var3 = (Entry)var2.next();
         var4 = (ModelBlock)var3.getValue();
      } while(var4 == null || !var1.equals(var4.func_178305_e()));

      return (ResourceLocation)var3.getKey();
   }

   private Set<ResourceLocation> func_177585_a(ModelBlock var1) {
      HashSet var2 = Sets.newHashSet();
      Iterator var3 = var1.func_178298_a().iterator();

      while(var3.hasNext()) {
         BlockPart var4 = (BlockPart)var3.next();
         Iterator var5 = var4.field_178240_c.values().iterator();

         while(var5.hasNext()) {
            BlockPartFace var6 = (BlockPartFace)var5.next();
            ResourceLocation var7 = new ResourceLocation(var1.func_178308_c(var6.field_178242_d));
            var2.add(var7);
         }
      }

      var2.add(new ResourceLocation(var1.func_178308_c("particle")));
      return var2;
   }

   private void func_177572_j() {
      final Set var1 = this.func_177575_g();
      var1.addAll(this.func_177571_k());
      var1.remove(TextureMap.field_174945_f);
      IIconCreator var2 = new IIconCreator() {
         public void func_177059_a(TextureMap var1x) {
            Iterator var2 = var1.iterator();

            while(var2.hasNext()) {
               ResourceLocation var3 = (ResourceLocation)var2.next();
               TextureAtlasSprite var4 = var1x.func_174942_a(var3);
               ModelBakery.this.field_177599_g.put(var3, var4);
            }

         }
      };
      this.field_177609_j.func_174943_a(this.field_177598_f, var2);
      this.field_177599_g.put(new ResourceLocation("missingno"), this.field_177609_j.func_174944_f());
   }

   private Set<ResourceLocation> func_177571_k() {
      HashSet var1 = Sets.newHashSet();
      Iterator var2 = this.field_177615_s.values().iterator();

      while(true) {
         while(true) {
            ModelBlock var4;
            do {
               if (!var2.hasNext()) {
                  return var1;
               }

               ResourceLocation var3 = (ResourceLocation)var2.next();
               var4 = (ModelBlock)this.field_177611_h.get(var3);
            } while(var4 == null);

            var1.add(new ResourceLocation(var4.func_178308_c("particle")));
            Iterator var5;
            ResourceLocation var11;
            if (this.func_177581_b(var4)) {
               for(var5 = ItemModelGenerator.field_178398_a.iterator(); var5.hasNext(); var1.add(var11)) {
                  String var10 = (String)var5.next();
                  var11 = new ResourceLocation(var4.func_178308_c(var10));
                  if (var4.func_178310_f() == field_177618_p && !TextureMap.field_174945_f.equals(var11)) {
                     TextureAtlasSprite.func_176603_b(var11.toString());
                  } else if (var4.func_178310_f() == field_177617_q && !TextureMap.field_174945_f.equals(var11)) {
                     TextureAtlasSprite.func_176602_a(var11.toString());
                  }
               }
            } else if (!this.func_177587_c(var4)) {
               var5 = var4.func_178298_a().iterator();

               while(var5.hasNext()) {
                  BlockPart var6 = (BlockPart)var5.next();
                  Iterator var7 = var6.field_178240_c.values().iterator();

                  while(var7.hasNext()) {
                     BlockPartFace var8 = (BlockPartFace)var7.next();
                     ResourceLocation var9 = new ResourceLocation(var4.func_178308_c(var8.field_178242_d));
                     var1.add(var9);
                  }
               }
            }
         }
      }
   }

   private boolean func_177581_b(ModelBlock var1) {
      if (var1 == null) {
         return false;
      } else {
         ModelBlock var2 = var1.func_178310_f();
         return var2 == field_177606_o || var2 == field_177618_p || var2 == field_177617_q;
      }
   }

   private boolean func_177587_c(ModelBlock var1) {
      if (var1 == null) {
         return false;
      } else {
         ModelBlock var2 = var1.func_178310_f();
         return var2 == field_177616_r;
      }
   }

   private void func_177593_l() {
      Iterator var1 = this.field_177615_s.values().iterator();

      while(var1.hasNext()) {
         ResourceLocation var2 = (ResourceLocation)var1.next();
         ModelBlock var3 = (ModelBlock)this.field_177611_h.get(var2);
         if (this.func_177581_b(var3)) {
            ModelBlock var4 = this.func_177582_d(var3);
            if (var4 != null) {
               var4.field_178317_b = var2.toString();
            }

            this.field_177611_h.put(var2, var4);
         } else if (this.func_177587_c(var3)) {
            this.field_177611_h.put(var2, var3);
         }
      }

      var1 = this.field_177599_g.values().iterator();

      while(var1.hasNext()) {
         TextureAtlasSprite var5 = (TextureAtlasSprite)var1.next();
         if (!var5.func_130098_m()) {
            var5.func_130103_l();
         }
      }

   }

   private ModelBlock func_177582_d(ModelBlock var1) {
      return this.field_177608_m.func_178392_a(this.field_177609_j, var1);
   }

   static {
      field_177600_d.put("missing", "{ \"textures\": {   \"particle\": \"missingno\",   \"missingno\": \"missingno\"}, \"elements\": [ {     \"from\": [ 0, 0, 0 ],     \"to\": [ 16, 16, 16 ],     \"faces\": {         \"down\":  { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"down\", \"texture\": \"#missingno\" },         \"up\":    { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"up\", \"texture\": \"#missingno\" },         \"north\": { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"north\", \"texture\": \"#missingno\" },         \"south\": { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"south\", \"texture\": \"#missingno\" },         \"west\":  { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"west\", \"texture\": \"#missingno\" },         \"east\":  { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"east\", \"texture\": \"#missingno\" }    }}]}");
      field_177601_e = Joiner.on(" -> ");
      field_177606_o = ModelBlock.func_178294_a("{\"elements\":[{  \"from\": [0, 0, 0],   \"to\": [16, 16, 16],   \"faces\": {       \"down\": {\"uv\": [0, 0, 16, 16], \"texture\":\"\"}   }}]}");
      field_177618_p = ModelBlock.func_178294_a("{\"elements\":[{  \"from\": [0, 0, 0],   \"to\": [16, 16, 16],   \"faces\": {       \"down\": {\"uv\": [0, 0, 16, 16], \"texture\":\"\"}   }}]}");
      field_177617_q = ModelBlock.func_178294_a("{\"elements\":[{  \"from\": [0, 0, 0],   \"to\": [16, 16, 16],   \"faces\": {       \"down\": {\"uv\": [0, 0, 16, 16], \"texture\":\"\"}   }}]}");
      field_177616_r = ModelBlock.func_178294_a("{\"elements\":[{  \"from\": [0, 0, 0],   \"to\": [16, 16, 16],   \"faces\": {       \"down\": {\"uv\": [0, 0, 16, 16], \"texture\":\"\"}   }}]}");
      field_177606_o.field_178317_b = "generation marker";
      field_177618_p.field_178317_b = "compass generation marker";
      field_177617_q.field_178317_b = "class generation marker";
      field_177616_r.field_178317_b = "block entity marker";
   }
}
