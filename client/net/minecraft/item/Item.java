package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockWall;
import net.minecraft.block.BlockWood;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.RegistryNamespaced;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class Item {
   public static final RegistryNamespaced field_150901_e = new RegistryNamespaced();
   protected static final UUID field_111210_e = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
   private CreativeTabs field_77701_a;
   protected static Random field_77697_d = new Random();
   protected int field_77777_bU = 64;
   private int field_77699_b;
   protected boolean field_77789_bW;
   protected boolean field_77787_bX;
   private Item field_77700_c;
   private String field_77785_bY;
   private String field_77774_bZ;
   protected IIcon field_77791_bV;
   protected String field_111218_cA;

   public Item() {
      super();
   }

   public static int func_150891_b(Item var0) {
      return var0 == null ? 0 : field_150901_e.func_148757_b(var0);
   }

   public static Item func_150899_d(int var0) {
      return (Item)field_150901_e.func_148754_a(var0);
   }

   public static Item func_150898_a(Block var0) {
      return func_150899_d(Block.func_149682_b(var0));
   }

   public static void func_150900_l() {
      field_150901_e.func_148756_a(256, "iron_shovel", new ItemSpade(Item$ToolMaterial.IRON).func_77655_b("shovelIron").func_111206_d("iron_shovel"));
      field_150901_e.func_148756_a(257, "iron_pickaxe", new ItemPickaxe(Item$ToolMaterial.IRON).func_77655_b("pickaxeIron").func_111206_d("iron_pickaxe"));
      field_150901_e.func_148756_a(258, "iron_axe", new ItemAxe(Item$ToolMaterial.IRON).func_77655_b("hatchetIron").func_111206_d("iron_axe"));
      field_150901_e.func_148756_a(259, "flint_and_steel", new ItemFlintAndSteel().func_77655_b("flintAndSteel").func_111206_d("flint_and_steel"));
      field_150901_e.func_148756_a(260, "apple", new ItemFood(4, 0.3F, false).func_77655_b("apple").func_111206_d("apple"));
      field_150901_e.func_148756_a(261, "bow", new ItemBow().func_77655_b("bow").func_111206_d("bow"));
      field_150901_e.func_148756_a(262, "arrow", new Item().func_77655_b("arrow").func_77637_a(CreativeTabs.field_78037_j).func_111206_d("arrow"));
      field_150901_e.func_148756_a(263, "coal", new ItemCoal().func_77655_b("coal").func_111206_d("coal"));
      field_150901_e.func_148756_a(264, "diamond", new Item().func_77655_b("diamond").func_77637_a(CreativeTabs.field_78035_l).func_111206_d("diamond"));
      field_150901_e.func_148756_a(
         265, "iron_ingot", new Item().func_77655_b("ingotIron").func_77637_a(CreativeTabs.field_78035_l).func_111206_d("iron_ingot")
      );
      field_150901_e.func_148756_a(
         266, "gold_ingot", new Item().func_77655_b("ingotGold").func_77637_a(CreativeTabs.field_78035_l).func_111206_d("gold_ingot")
      );
      field_150901_e.func_148756_a(267, "iron_sword", new ItemSword(Item$ToolMaterial.IRON).func_77655_b("swordIron").func_111206_d("iron_sword"));
      field_150901_e.func_148756_a(268, "wooden_sword", new ItemSword(Item$ToolMaterial.WOOD).func_77655_b("swordWood").func_111206_d("wood_sword"));
      field_150901_e.func_148756_a(269, "wooden_shovel", new ItemSpade(Item$ToolMaterial.WOOD).func_77655_b("shovelWood").func_111206_d("wood_shovel"));
      field_150901_e.func_148756_a(270, "wooden_pickaxe", new ItemPickaxe(Item$ToolMaterial.WOOD).func_77655_b("pickaxeWood").func_111206_d("wood_pickaxe"));
      field_150901_e.func_148756_a(271, "wooden_axe", new ItemAxe(Item$ToolMaterial.WOOD).func_77655_b("hatchetWood").func_111206_d("wood_axe"));
      field_150901_e.func_148756_a(272, "stone_sword", new ItemSword(Item$ToolMaterial.STONE).func_77655_b("swordStone").func_111206_d("stone_sword"));
      field_150901_e.func_148756_a(273, "stone_shovel", new ItemSpade(Item$ToolMaterial.STONE).func_77655_b("shovelStone").func_111206_d("stone_shovel"));
      field_150901_e.func_148756_a(274, "stone_pickaxe", new ItemPickaxe(Item$ToolMaterial.STONE).func_77655_b("pickaxeStone").func_111206_d("stone_pickaxe"));
      field_150901_e.func_148756_a(275, "stone_axe", new ItemAxe(Item$ToolMaterial.STONE).func_77655_b("hatchetStone").func_111206_d("stone_axe"));
      field_150901_e.func_148756_a(276, "diamond_sword", new ItemSword(Item$ToolMaterial.EMERALD).func_77655_b("swordDiamond").func_111206_d("diamond_sword"));
      field_150901_e.func_148756_a(
         277, "diamond_shovel", new ItemSpade(Item$ToolMaterial.EMERALD).func_77655_b("shovelDiamond").func_111206_d("diamond_shovel")
      );
      field_150901_e.func_148756_a(
         278, "diamond_pickaxe", new ItemPickaxe(Item$ToolMaterial.EMERALD).func_77655_b("pickaxeDiamond").func_111206_d("diamond_pickaxe")
      );
      field_150901_e.func_148756_a(279, "diamond_axe", new ItemAxe(Item$ToolMaterial.EMERALD).func_77655_b("hatchetDiamond").func_111206_d("diamond_axe"));
      field_150901_e.func_148756_a(
         280, "stick", new Item().func_77664_n().func_77655_b("stick").func_77637_a(CreativeTabs.field_78035_l).func_111206_d("stick")
      );
      field_150901_e.func_148756_a(281, "bowl", new Item().func_77655_b("bowl").func_77637_a(CreativeTabs.field_78035_l).func_111206_d("bowl"));
      field_150901_e.func_148756_a(282, "mushroom_stew", new ItemSoup(6).func_77655_b("mushroomStew").func_111206_d("mushroom_stew"));
      field_150901_e.func_148756_a(283, "golden_sword", new ItemSword(Item$ToolMaterial.GOLD).func_77655_b("swordGold").func_111206_d("gold_sword"));
      field_150901_e.func_148756_a(284, "golden_shovel", new ItemSpade(Item$ToolMaterial.GOLD).func_77655_b("shovelGold").func_111206_d("gold_shovel"));
      field_150901_e.func_148756_a(285, "golden_pickaxe", new ItemPickaxe(Item$ToolMaterial.GOLD).func_77655_b("pickaxeGold").func_111206_d("gold_pickaxe"));
      field_150901_e.func_148756_a(286, "golden_axe", new ItemAxe(Item$ToolMaterial.GOLD).func_77655_b("hatchetGold").func_111206_d("gold_axe"));
      field_150901_e.func_148756_a(
         287, "string", new ItemReed(Blocks.field_150473_bD).func_77655_b("string").func_77637_a(CreativeTabs.field_78035_l).func_111206_d("string")
      );
      field_150901_e.func_148756_a(288, "feather", new Item().func_77655_b("feather").func_77637_a(CreativeTabs.field_78035_l).func_111206_d("feather"));
      field_150901_e.func_148756_a(
         289,
         "gunpowder",
         new Item().func_77655_b("sulphur").func_77631_c(PotionHelper.field_77930_k).func_77637_a(CreativeTabs.field_78035_l).func_111206_d("gunpowder")
      );
      field_150901_e.func_148756_a(290, "wooden_hoe", new ItemHoe(Item$ToolMaterial.WOOD).func_77655_b("hoeWood").func_111206_d("wood_hoe"));
      field_150901_e.func_148756_a(291, "stone_hoe", new ItemHoe(Item$ToolMaterial.STONE).func_77655_b("hoeStone").func_111206_d("stone_hoe"));
      field_150901_e.func_148756_a(292, "iron_hoe", new ItemHoe(Item$ToolMaterial.IRON).func_77655_b("hoeIron").func_111206_d("iron_hoe"));
      field_150901_e.func_148756_a(293, "diamond_hoe", new ItemHoe(Item$ToolMaterial.EMERALD).func_77655_b("hoeDiamond").func_111206_d("diamond_hoe"));
      field_150901_e.func_148756_a(294, "golden_hoe", new ItemHoe(Item$ToolMaterial.GOLD).func_77655_b("hoeGold").func_111206_d("gold_hoe"));
      field_150901_e.func_148756_a(
         295, "wheat_seeds", new ItemSeeds(Blocks.field_150464_aj, Blocks.field_150458_ak).func_77655_b("seeds").func_111206_d("seeds_wheat")
      );
      field_150901_e.func_148756_a(296, "wheat", new Item().func_77655_b("wheat").func_77637_a(CreativeTabs.field_78035_l).func_111206_d("wheat"));
      field_150901_e.func_148756_a(297, "bread", new ItemFood(5, 0.6F, false).func_77655_b("bread").func_111206_d("bread"));
      field_150901_e.func_148756_a(
         298, "leather_helmet", new ItemArmor(ItemArmor$ArmorMaterial.CLOTH, 0, 0).func_77655_b("helmetCloth").func_111206_d("leather_helmet")
      );
      field_150901_e.func_148756_a(
         299, "leather_chestplate", new ItemArmor(ItemArmor$ArmorMaterial.CLOTH, 0, 1).func_77655_b("chestplateCloth").func_111206_d("leather_chestplate")
      );
      field_150901_e.func_148756_a(
         300, "leather_leggings", new ItemArmor(ItemArmor$ArmorMaterial.CLOTH, 0, 2).func_77655_b("leggingsCloth").func_111206_d("leather_leggings")
      );
      field_150901_e.func_148756_a(
         301, "leather_boots", new ItemArmor(ItemArmor$ArmorMaterial.CLOTH, 0, 3).func_77655_b("bootsCloth").func_111206_d("leather_boots")
      );
      field_150901_e.func_148756_a(
         302, "chainmail_helmet", new ItemArmor(ItemArmor$ArmorMaterial.CHAIN, 1, 0).func_77655_b("helmetChain").func_111206_d("chainmail_helmet")
      );
      field_150901_e.func_148756_a(
         303, "chainmail_chestplate", new ItemArmor(ItemArmor$ArmorMaterial.CHAIN, 1, 1).func_77655_b("chestplateChain").func_111206_d("chainmail_chestplate")
      );
      field_150901_e.func_148756_a(
         304, "chainmail_leggings", new ItemArmor(ItemArmor$ArmorMaterial.CHAIN, 1, 2).func_77655_b("leggingsChain").func_111206_d("chainmail_leggings")
      );
      field_150901_e.func_148756_a(
         305, "chainmail_boots", new ItemArmor(ItemArmor$ArmorMaterial.CHAIN, 1, 3).func_77655_b("bootsChain").func_111206_d("chainmail_boots")
      );
      field_150901_e.func_148756_a(
         306, "iron_helmet", new ItemArmor(ItemArmor$ArmorMaterial.IRON, 2, 0).func_77655_b("helmetIron").func_111206_d("iron_helmet")
      );
      field_150901_e.func_148756_a(
         307, "iron_chestplate", new ItemArmor(ItemArmor$ArmorMaterial.IRON, 2, 1).func_77655_b("chestplateIron").func_111206_d("iron_chestplate")
      );
      field_150901_e.func_148756_a(
         308, "iron_leggings", new ItemArmor(ItemArmor$ArmorMaterial.IRON, 2, 2).func_77655_b("leggingsIron").func_111206_d("iron_leggings")
      );
      field_150901_e.func_148756_a(309, "iron_boots", new ItemArmor(ItemArmor$ArmorMaterial.IRON, 2, 3).func_77655_b("bootsIron").func_111206_d("iron_boots"));
      field_150901_e.func_148756_a(
         310, "diamond_helmet", new ItemArmor(ItemArmor$ArmorMaterial.DIAMOND, 3, 0).func_77655_b("helmetDiamond").func_111206_d("diamond_helmet")
      );
      field_150901_e.func_148756_a(
         311, "diamond_chestplate", new ItemArmor(ItemArmor$ArmorMaterial.DIAMOND, 3, 1).func_77655_b("chestplateDiamond").func_111206_d("diamond_chestplate")
      );
      field_150901_e.func_148756_a(
         312, "diamond_leggings", new ItemArmor(ItemArmor$ArmorMaterial.DIAMOND, 3, 2).func_77655_b("leggingsDiamond").func_111206_d("diamond_leggings")
      );
      field_150901_e.func_148756_a(
         313, "diamond_boots", new ItemArmor(ItemArmor$ArmorMaterial.DIAMOND, 3, 3).func_77655_b("bootsDiamond").func_111206_d("diamond_boots")
      );
      field_150901_e.func_148756_a(
         314, "golden_helmet", new ItemArmor(ItemArmor$ArmorMaterial.GOLD, 4, 0).func_77655_b("helmetGold").func_111206_d("gold_helmet")
      );
      field_150901_e.func_148756_a(
         315, "golden_chestplate", new ItemArmor(ItemArmor$ArmorMaterial.GOLD, 4, 1).func_77655_b("chestplateGold").func_111206_d("gold_chestplate")
      );
      field_150901_e.func_148756_a(
         316, "golden_leggings", new ItemArmor(ItemArmor$ArmorMaterial.GOLD, 4, 2).func_77655_b("leggingsGold").func_111206_d("gold_leggings")
      );
      field_150901_e.func_148756_a(
         317, "golden_boots", new ItemArmor(ItemArmor$ArmorMaterial.GOLD, 4, 3).func_77655_b("bootsGold").func_111206_d("gold_boots")
      );
      field_150901_e.func_148756_a(318, "flint", new Item().func_77655_b("flint").func_77637_a(CreativeTabs.field_78035_l).func_111206_d("flint"));
      field_150901_e.func_148756_a(319, "porkchop", new ItemFood(3, 0.3F, true).func_77655_b("porkchopRaw").func_111206_d("porkchop_raw"));
      field_150901_e.func_148756_a(320, "cooked_porkchop", new ItemFood(8, 0.8F, true).func_77655_b("porkchopCooked").func_111206_d("porkchop_cooked"));
      field_150901_e.func_148756_a(321, "painting", new ItemHangingEntity(EntityPainting.class).func_77655_b("painting").func_111206_d("painting"));
      field_150901_e.func_148756_a(
         322,
         "golden_apple",
         new ItemAppleGold(4, 1.2F, false)
            .func_77848_i()
            .func_77844_a(Potion.field_76428_l.field_76415_H, 5, 1, 1.0F)
            .func_77655_b("appleGold")
            .func_111206_d("apple_golden")
      );
      field_150901_e.func_148756_a(323, "sign", new ItemSign().func_77655_b("sign").func_111206_d("sign"));
      field_150901_e.func_148756_a(324, "wooden_door", new ItemDoor(Material.field_151575_d).func_77655_b("doorWood").func_111206_d("door_wood"));
      Item var0 = new ItemBucket(Blocks.field_150350_a).func_77655_b("bucket").func_77625_d(16).func_111206_d("bucket_empty");
      field_150901_e.func_148756_a(325, "bucket", var0);
      field_150901_e.func_148756_a(
         326, "water_bucket", new ItemBucket(Blocks.field_150358_i).func_77655_b("bucketWater").func_77642_a(var0).func_111206_d("bucket_water")
      );
      field_150901_e.func_148756_a(
         327, "lava_bucket", new ItemBucket(Blocks.field_150356_k).func_77655_b("bucketLava").func_77642_a(var0).func_111206_d("bucket_lava")
      );
      field_150901_e.func_148756_a(328, "minecart", new ItemMinecart(0).func_77655_b("minecart").func_111206_d("minecart_normal"));
      field_150901_e.func_148756_a(329, "saddle", new ItemSaddle().func_77655_b("saddle").func_111206_d("saddle"));
      field_150901_e.func_148756_a(330, "iron_door", new ItemDoor(Material.field_151573_f).func_77655_b("doorIron").func_111206_d("door_iron"));
      field_150901_e.func_148756_a(
         331, "redstone", new ItemRedstone().func_77655_b("redstone").func_77631_c(PotionHelper.field_77932_i).func_111206_d("redstone_dust")
      );
      field_150901_e.func_148756_a(332, "snowball", new ItemSnowball().func_77655_b("snowball").func_111206_d("snowball"));
      field_150901_e.func_148756_a(333, "boat", new ItemBoat().func_77655_b("boat").func_111206_d("boat"));
      field_150901_e.func_148756_a(334, "leather", new Item().func_77655_b("leather").func_77637_a(CreativeTabs.field_78035_l).func_111206_d("leather"));
      field_150901_e.func_148756_a(335, "milk_bucket", new ItemBucketMilk().func_77655_b("milk").func_77642_a(var0).func_111206_d("bucket_milk"));
      field_150901_e.func_148756_a(336, "brick", new Item().func_77655_b("brick").func_77637_a(CreativeTabs.field_78035_l).func_111206_d("brick"));
      field_150901_e.func_148756_a(337, "clay_ball", new Item().func_77655_b("clay").func_77637_a(CreativeTabs.field_78035_l).func_111206_d("clay_ball"));
      field_150901_e.func_148756_a(
         338, "reeds", new ItemReed(Blocks.field_150436_aH).func_77655_b("reeds").func_77637_a(CreativeTabs.field_78035_l).func_111206_d("reeds")
      );
      field_150901_e.func_148756_a(339, "paper", new Item().func_77655_b("paper").func_77637_a(CreativeTabs.field_78026_f).func_111206_d("paper"));
      field_150901_e.func_148756_a(340, "book", new ItemBook().func_77655_b("book").func_77637_a(CreativeTabs.field_78026_f).func_111206_d("book_normal"));
      field_150901_e.func_148756_a(341, "slime_ball", new Item().func_77655_b("slimeball").func_77637_a(CreativeTabs.field_78026_f).func_111206_d("slimeball"));
      field_150901_e.func_148756_a(342, "chest_minecart", new ItemMinecart(1).func_77655_b("minecartChest").func_111206_d("minecart_chest"));
      field_150901_e.func_148756_a(343, "furnace_minecart", new ItemMinecart(2).func_77655_b("minecartFurnace").func_111206_d("minecart_furnace"));
      field_150901_e.func_148756_a(344, "egg", new ItemEgg().func_77655_b("egg").func_111206_d("egg"));
      field_150901_e.func_148756_a(345, "compass", new Item().func_77655_b("compass").func_77637_a(CreativeTabs.field_78040_i).func_111206_d("compass"));
      field_150901_e.func_148756_a(346, "fishing_rod", new ItemFishingRod().func_77655_b("fishingRod").func_111206_d("fishing_rod"));
      field_150901_e.func_148756_a(347, "clock", new Item().func_77655_b("clock").func_77637_a(CreativeTabs.field_78040_i).func_111206_d("clock"));
      field_150901_e.func_148756_a(
         348,
         "glowstone_dust",
         new Item()
            .func_77655_b("yellowDust")
            .func_77631_c(PotionHelper.field_77929_j)
            .func_77637_a(CreativeTabs.field_78035_l)
            .func_111206_d("glowstone_dust")
      );
      field_150901_e.func_148756_a(349, "fish", new ItemFishFood(false).func_77655_b("fish").func_111206_d("fish_raw").func_77627_a(true));
      field_150901_e.func_148756_a(350, "cooked_fished", new ItemFishFood(true).func_77655_b("fish").func_111206_d("fish_cooked").func_77627_a(true));
      field_150901_e.func_148756_a(351, "dye", new ItemDye().func_77655_b("dyePowder").func_111206_d("dye_powder"));
      field_150901_e.func_148756_a(352, "bone", new Item().func_77655_b("bone").func_77664_n().func_77637_a(CreativeTabs.field_78026_f).func_111206_d("bone"));
      field_150901_e.func_148756_a(
         353,
         "sugar",
         new Item().func_77655_b("sugar").func_77631_c(PotionHelper.field_77922_b).func_77637_a(CreativeTabs.field_78035_l).func_111206_d("sugar")
      );
      field_150901_e.func_148756_a(
         354, "cake", new ItemReed(Blocks.field_150414_aQ).func_77625_d(1).func_77655_b("cake").func_77637_a(CreativeTabs.field_78039_h).func_111206_d("cake")
      );
      field_150901_e.func_148756_a(355, "bed", new ItemBed().func_77625_d(1).func_77655_b("bed").func_111206_d("bed"));
      field_150901_e.func_148756_a(
         356, "repeater", new ItemReed(Blocks.field_150413_aR).func_77655_b("diode").func_77637_a(CreativeTabs.field_78028_d).func_111206_d("repeater")
      );
      field_150901_e.func_148756_a(357, "cookie", new ItemFood(2, 0.1F, false).func_77655_b("cookie").func_111206_d("cookie"));
      field_150901_e.func_148756_a(358, "filled_map", new ItemMap().func_77655_b("map").func_111206_d("map_filled"));
      field_150901_e.func_148756_a(359, "shears", new ItemShears().func_77655_b("shears").func_111206_d("shears"));
      field_150901_e.func_148756_a(360, "melon", new ItemFood(2, 0.3F, false).func_77655_b("melon").func_111206_d("melon"));
      field_150901_e.func_148756_a(
         361, "pumpkin_seeds", new ItemSeeds(Blocks.field_150393_bb, Blocks.field_150458_ak).func_77655_b("seeds_pumpkin").func_111206_d("seeds_pumpkin")
      );
      field_150901_e.func_148756_a(
         362, "melon_seeds", new ItemSeeds(Blocks.field_150394_bc, Blocks.field_150458_ak).func_77655_b("seeds_melon").func_111206_d("seeds_melon")
      );
      field_150901_e.func_148756_a(363, "beef", new ItemFood(3, 0.3F, true).func_77655_b("beefRaw").func_111206_d("beef_raw"));
      field_150901_e.func_148756_a(364, "cooked_beef", new ItemFood(8, 0.8F, true).func_77655_b("beefCooked").func_111206_d("beef_cooked"));
      field_150901_e.func_148756_a(
         365,
         "chicken",
         new ItemFood(2, 0.3F, true).func_77844_a(Potion.field_76438_s.field_76415_H, 30, 0, 0.3F).func_77655_b("chickenRaw").func_111206_d("chicken_raw")
      );
      field_150901_e.func_148756_a(366, "cooked_chicken", new ItemFood(6, 0.6F, true).func_77655_b("chickenCooked").func_111206_d("chicken_cooked"));
      field_150901_e.func_148756_a(
         367,
         "rotten_flesh",
         new ItemFood(4, 0.1F, true).func_77844_a(Potion.field_76438_s.field_76415_H, 30, 0, 0.8F).func_77655_b("rottenFlesh").func_111206_d("rotten_flesh")
      );
      field_150901_e.func_148756_a(368, "ender_pearl", new ItemEnderPearl().func_77655_b("enderPearl").func_111206_d("ender_pearl"));
      field_150901_e.func_148756_a(369, "blaze_rod", new Item().func_77655_b("blazeRod").func_77637_a(CreativeTabs.field_78035_l).func_111206_d("blaze_rod"));
      field_150901_e.func_148756_a(
         370,
         "ghast_tear",
         new Item().func_77655_b("ghastTear").func_77631_c(PotionHelper.field_77923_c).func_77637_a(CreativeTabs.field_78038_k).func_111206_d("ghast_tear")
      );
      field_150901_e.func_148756_a(
         371, "gold_nugget", new Item().func_77655_b("goldNugget").func_77637_a(CreativeTabs.field_78035_l).func_111206_d("gold_nugget")
      );
      field_150901_e.func_148756_a(
         372,
         "nether_wart",
         new ItemSeeds(Blocks.field_150388_bm, Blocks.field_150425_aM).func_77655_b("netherStalkSeeds").func_77631_c("+4").func_111206_d("nether_wart")
      );
      field_150901_e.func_148756_a(373, "potion", new ItemPotion().func_77655_b("potion").func_111206_d("potion"));
      field_150901_e.func_148756_a(374, "glass_bottle", new ItemGlassBottle().func_77655_b("glassBottle").func_111206_d("potion_bottle_empty"));
      field_150901_e.func_148756_a(
         375,
         "spider_eye",
         new ItemFood(2, 0.8F, false)
            .func_77844_a(Potion.field_76436_u.field_76415_H, 5, 0, 1.0F)
            .func_77655_b("spiderEye")
            .func_77631_c(PotionHelper.field_77920_d)
            .func_111206_d("spider_eye")
      );
      field_150901_e.func_148756_a(
         376,
         "fermented_spider_eye",
         new Item()
            .func_77655_b("fermentedSpiderEye")
            .func_77631_c(PotionHelper.field_77921_e)
            .func_77637_a(CreativeTabs.field_78038_k)
            .func_111206_d("spider_eye_fermented")
      );
      field_150901_e.func_148756_a(
         377,
         "blaze_powder",
         new Item()
            .func_77655_b("blazePowder")
            .func_77631_c(PotionHelper.field_77919_g)
            .func_77637_a(CreativeTabs.field_78038_k)
            .func_111206_d("blaze_powder")
      );
      field_150901_e.func_148756_a(
         378,
         "magma_cream",
         new Item().func_77655_b("magmaCream").func_77631_c(PotionHelper.field_77931_h).func_77637_a(CreativeTabs.field_78038_k).func_111206_d("magma_cream")
      );
      field_150901_e.func_148756_a(
         379,
         "brewing_stand",
         new ItemReed(Blocks.field_150382_bo).func_77655_b("brewingStand").func_77637_a(CreativeTabs.field_78038_k).func_111206_d("brewing_stand")
      );
      field_150901_e.func_148756_a(
         380, "cauldron", new ItemReed(Blocks.field_150383_bp).func_77655_b("cauldron").func_77637_a(CreativeTabs.field_78038_k).func_111206_d("cauldron")
      );
      field_150901_e.func_148756_a(381, "ender_eye", new ItemEnderEye().func_77655_b("eyeOfEnder").func_111206_d("ender_eye"));
      field_150901_e.func_148756_a(
         382,
         "speckled_melon",
         new Item()
            .func_77655_b("speckledMelon")
            .func_77631_c(PotionHelper.field_77918_f)
            .func_77637_a(CreativeTabs.field_78038_k)
            .func_111206_d("melon_speckled")
      );
      field_150901_e.func_148756_a(383, "spawn_egg", new ItemMonsterPlacer().func_77655_b("monsterPlacer").func_111206_d("spawn_egg"));
      field_150901_e.func_148756_a(384, "experience_bottle", new ItemExpBottle().func_77655_b("expBottle").func_111206_d("experience_bottle"));
      field_150901_e.func_148756_a(385, "fire_charge", new ItemFireball().func_77655_b("fireball").func_111206_d("fireball"));
      field_150901_e.func_148756_a(
         386, "writable_book", new ItemWritableBook().func_77655_b("writingBook").func_77637_a(CreativeTabs.field_78026_f).func_111206_d("book_writable")
      );
      field_150901_e.func_148756_a(387, "written_book", new ItemEditableBook().func_77655_b("writtenBook").func_111206_d("book_written").func_77625_d(16));
      field_150901_e.func_148756_a(388, "emerald", new Item().func_77655_b("emerald").func_77637_a(CreativeTabs.field_78035_l).func_111206_d("emerald"));
      field_150901_e.func_148756_a(389, "item_frame", new ItemHangingEntity(EntityItemFrame.class).func_77655_b("frame").func_111206_d("item_frame"));
      field_150901_e.func_148756_a(
         390,
         "flower_pot",
         new ItemReed(Blocks.field_150457_bL).func_77655_b("flowerPot").func_77637_a(CreativeTabs.field_78031_c).func_111206_d("flower_pot")
      );
      field_150901_e.func_148756_a(
         391, "carrot", new ItemSeedFood(4, 0.6F, Blocks.field_150459_bM, Blocks.field_150458_ak).func_77655_b("carrots").func_111206_d("carrot")
      );
      field_150901_e.func_148756_a(
         392, "potato", new ItemSeedFood(1, 0.3F, Blocks.field_150469_bN, Blocks.field_150458_ak).func_77655_b("potato").func_111206_d("potato")
      );
      field_150901_e.func_148756_a(393, "baked_potato", new ItemFood(6, 0.6F, false).func_77655_b("potatoBaked").func_111206_d("potato_baked"));
      field_150901_e.func_148756_a(
         394,
         "poisonous_potato",
         new ItemFood(2, 0.3F, false)
            .func_77844_a(Potion.field_76436_u.field_76415_H, 5, 0, 0.6F)
            .func_77655_b("potatoPoisonous")
            .func_111206_d("potato_poisonous")
      );
      field_150901_e.func_148756_a(395, "map", new ItemEmptyMap().func_77655_b("emptyMap").func_111206_d("map_empty"));
      field_150901_e.func_148756_a(
         396,
         "golden_carrot",
         new ItemFood(6, 1.2F, false).func_77655_b("carrotGolden").func_77631_c(PotionHelper.field_82818_l).func_111206_d("carrot_golden")
      );
      field_150901_e.func_148756_a(397, "skull", new ItemSkull().func_77655_b("skull").func_111206_d("skull"));
      field_150901_e.func_148756_a(398, "carrot_on_a_stick", new ItemCarrotOnAStick().func_77655_b("carrotOnAStick").func_111206_d("carrot_on_a_stick"));
      field_150901_e.func_148756_a(
         399, "nether_star", new ItemSimpleFoiled().func_77655_b("netherStar").func_77637_a(CreativeTabs.field_78035_l).func_111206_d("nether_star")
      );
      field_150901_e.func_148756_a(
         400, "pumpkin_pie", new ItemFood(8, 0.3F, false).func_77655_b("pumpkinPie").func_77637_a(CreativeTabs.field_78039_h).func_111206_d("pumpkin_pie")
      );
      field_150901_e.func_148756_a(401, "fireworks", new ItemFirework().func_77655_b("fireworks").func_111206_d("fireworks"));
      field_150901_e.func_148756_a(
         402,
         "firework_charge",
         new ItemFireworkCharge().func_77655_b("fireworksCharge").func_77637_a(CreativeTabs.field_78026_f).func_111206_d("fireworks_charge")
      );
      field_150901_e.func_148756_a(
         403, "enchanted_book", new ItemEnchantedBook().func_77625_d(1).func_77655_b("enchantedBook").func_111206_d("book_enchanted")
      );
      field_150901_e.func_148756_a(
         404,
         "comparator",
         new ItemReed(Blocks.field_150441_bU).func_77655_b("comparator").func_77637_a(CreativeTabs.field_78028_d).func_111206_d("comparator")
      );
      field_150901_e.func_148756_a(
         405, "netherbrick", new Item().func_77655_b("netherbrick").func_77637_a(CreativeTabs.field_78035_l).func_111206_d("netherbrick")
      );
      field_150901_e.func_148756_a(406, "quartz", new Item().func_77655_b("netherquartz").func_77637_a(CreativeTabs.field_78035_l).func_111206_d("quartz"));
      field_150901_e.func_148756_a(407, "tnt_minecart", new ItemMinecart(3).func_77655_b("minecartTnt").func_111206_d("minecart_tnt"));
      field_150901_e.func_148756_a(408, "hopper_minecart", new ItemMinecart(5).func_77655_b("minecartHopper").func_111206_d("minecart_hopper"));
      field_150901_e.func_148756_a(
         417,
         "iron_horse_armor",
         new Item().func_77655_b("horsearmormetal").func_77625_d(1).func_77637_a(CreativeTabs.field_78026_f).func_111206_d("iron_horse_armor")
      );
      field_150901_e.func_148756_a(
         418,
         "golden_horse_armor",
         new Item().func_77655_b("horsearmorgold").func_77625_d(1).func_77637_a(CreativeTabs.field_78026_f).func_111206_d("gold_horse_armor")
      );
      field_150901_e.func_148756_a(
         419,
         "diamond_horse_armor",
         new Item().func_77655_b("horsearmordiamond").func_77625_d(1).func_77637_a(CreativeTabs.field_78026_f).func_111206_d("diamond_horse_armor")
      );
      field_150901_e.func_148756_a(420, "lead", new ItemLead().func_77655_b("leash").func_111206_d("lead"));
      field_150901_e.func_148756_a(421, "name_tag", new ItemNameTag().func_77655_b("nameTag").func_111206_d("name_tag"));
      field_150901_e.func_148756_a(
         422, "command_block_minecart", new ItemMinecart(6).func_77655_b("minecartCommandBlock").func_111206_d("minecart_command_block").func_77637_a(null)
      );
      field_150901_e.func_148756_a(2256, "record_13", new ItemRecord("13").func_77655_b("record").func_111206_d("record_13"));
      field_150901_e.func_148756_a(2257, "record_cat", new ItemRecord("cat").func_77655_b("record").func_111206_d("record_cat"));
      field_150901_e.func_148756_a(2258, "record_blocks", new ItemRecord("blocks").func_77655_b("record").func_111206_d("record_blocks"));
      field_150901_e.func_148756_a(2259, "record_chirp", new ItemRecord("chirp").func_77655_b("record").func_111206_d("record_chirp"));
      field_150901_e.func_148756_a(2260, "record_far", new ItemRecord("far").func_77655_b("record").func_111206_d("record_far"));
      field_150901_e.func_148756_a(2261, "record_mall", new ItemRecord("mall").func_77655_b("record").func_111206_d("record_mall"));
      field_150901_e.func_148756_a(2262, "record_mellohi", new ItemRecord("mellohi").func_77655_b("record").func_111206_d("record_mellohi"));
      field_150901_e.func_148756_a(2263, "record_stal", new ItemRecord("stal").func_77655_b("record").func_111206_d("record_stal"));
      field_150901_e.func_148756_a(2264, "record_strad", new ItemRecord("strad").func_77655_b("record").func_111206_d("record_strad"));
      field_150901_e.func_148756_a(2265, "record_ward", new ItemRecord("ward").func_77655_b("record").func_111206_d("record_ward"));
      field_150901_e.func_148756_a(2266, "record_11", new ItemRecord("11").func_77655_b("record").func_111206_d("record_11"));
      field_150901_e.func_148756_a(2267, "record_wait", new ItemRecord("wait").func_77655_b("record").func_111206_d("record_wait"));
      HashSet var1 = Sets.newHashSet(
         new Block[]{
            Blocks.field_150350_a,
            Blocks.field_150382_bo,
            Blocks.field_150324_C,
            Blocks.field_150388_bm,
            Blocks.field_150383_bp,
            Blocks.field_150457_bL,
            Blocks.field_150464_aj,
            Blocks.field_150436_aH,
            Blocks.field_150414_aQ,
            Blocks.field_150465_bP,
            Blocks.field_150332_K,
            Blocks.field_150326_M,
            Blocks.field_150439_ay,
            Blocks.field_150416_aS,
            Blocks.field_150393_bb,
            Blocks.field_150472_an,
            Blocks.field_150455_bV,
            Blocks.field_150473_bD,
            Blocks.field_150374_bv,
            Blocks.field_150394_bc,
            Blocks.field_150437_az,
            Blocks.field_150441_bU,
            Blocks.field_150488_af,
            Blocks.field_150444_as,
            Blocks.field_150413_aR,
            Blocks.field_150454_av,
            Blocks.field_150466_ao
         }
      );

      for(String var3 : Block.field_149771_c.func_148742_b()) {
         Block var4 = (Block)Block.field_149771_c.func_82594_a(var3);
         Object var5;
         if (var4 == Blocks.field_150325_L) {
            var5 = new ItemCloth(Blocks.field_150325_L).func_77655_b("cloth");
         } else if (var4 == Blocks.field_150406_ce) {
            var5 = new ItemCloth(Blocks.field_150406_ce).func_77655_b("clayHardenedStained");
         } else if (var4 == Blocks.field_150399_cn) {
            var5 = new ItemCloth(Blocks.field_150399_cn).func_77655_b("stainedGlass");
         } else if (var4 == Blocks.field_150397_co) {
            var5 = new ItemCloth(Blocks.field_150397_co).func_77655_b("stainedGlassPane");
         } else if (var4 == Blocks.field_150404_cg) {
            var5 = new ItemCloth(Blocks.field_150404_cg).func_77655_b("woolCarpet");
         } else if (var4 == Blocks.field_150346_d) {
            var5 = new ItemMultiTexture(Blocks.field_150346_d, Blocks.field_150346_d, BlockDirt.field_150009_a).func_77655_b("dirt");
         } else if (var4 == Blocks.field_150354_m) {
            var5 = new ItemMultiTexture(Blocks.field_150354_m, Blocks.field_150354_m, BlockSand.field_149838_a).func_77655_b("sand");
         } else if (var4 == Blocks.field_150364_r) {
            var5 = new ItemMultiTexture(Blocks.field_150364_r, Blocks.field_150364_r, BlockOldLog.field_150168_M).func_77655_b("log");
         } else if (var4 == Blocks.field_150363_s) {
            var5 = new ItemMultiTexture(Blocks.field_150363_s, Blocks.field_150363_s, BlockNewLog.field_150169_M).func_77655_b("log");
         } else if (var4 == Blocks.field_150344_f) {
            var5 = new ItemMultiTexture(Blocks.field_150344_f, Blocks.field_150344_f, BlockWood.field_150096_a).func_77655_b("wood");
         } else if (var4 == Blocks.field_150418_aU) {
            var5 = new ItemMultiTexture(Blocks.field_150418_aU, Blocks.field_150418_aU, BlockSilverfish.field_150198_a).func_77655_b("monsterStoneEgg");
         } else if (var4 == Blocks.field_150417_aV) {
            var5 = new ItemMultiTexture(Blocks.field_150417_aV, Blocks.field_150417_aV, BlockStoneBrick.field_150142_a).func_77655_b("stonebricksmooth");
         } else if (var4 == Blocks.field_150322_A) {
            var5 = new ItemMultiTexture(Blocks.field_150322_A, Blocks.field_150322_A, BlockSandStone.field_150157_a).func_77655_b("sandStone");
         } else if (var4 == Blocks.field_150371_ca) {
            var5 = new ItemMultiTexture(Blocks.field_150371_ca, Blocks.field_150371_ca, BlockQuartz.field_150191_a).func_77655_b("quartzBlock");
         } else if (var4 == Blocks.field_150333_U) {
            var5 = new ItemSlab(Blocks.field_150333_U, Blocks.field_150333_U, Blocks.field_150334_T, false).func_77655_b("stoneSlab");
         } else if (var4 == Blocks.field_150334_T) {
            var5 = new ItemSlab(Blocks.field_150334_T, Blocks.field_150333_U, Blocks.field_150334_T, true).func_77655_b("stoneSlab");
         } else if (var4 == Blocks.field_150376_bx) {
            var5 = new ItemSlab(Blocks.field_150376_bx, Blocks.field_150376_bx, Blocks.field_150373_bw, false).func_77655_b("woodSlab");
         } else if (var4 == Blocks.field_150373_bw) {
            var5 = new ItemSlab(Blocks.field_150373_bw, Blocks.field_150376_bx, Blocks.field_150373_bw, true).func_77655_b("woodSlab");
         } else if (var4 == Blocks.field_150345_g) {
            var5 = new ItemMultiTexture(Blocks.field_150345_g, Blocks.field_150345_g, BlockSapling.field_149882_a).func_77655_b("sapling");
         } else if (var4 == Blocks.field_150362_t) {
            var5 = new ItemLeaves(Blocks.field_150362_t).func_77655_b("leaves");
         } else if (var4 == Blocks.field_150361_u) {
            var5 = new ItemLeaves(Blocks.field_150361_u).func_77655_b("leaves");
         } else if (var4 == Blocks.field_150395_bd) {
            var5 = new ItemColored(Blocks.field_150395_bd, false);
         } else if (var4 == Blocks.field_150329_H) {
            var5 = new ItemColored(Blocks.field_150329_H, true).func_150943_a(new String[]{"shrub", "grass", "fern"});
         } else if (var4 == Blocks.field_150327_N) {
            var5 = new ItemMultiTexture(Blocks.field_150327_N, Blocks.field_150327_N, BlockFlower.field_149858_b).func_77655_b("flower");
         } else if (var4 == Blocks.field_150328_O) {
            var5 = new ItemMultiTexture(Blocks.field_150328_O, Blocks.field_150328_O, BlockFlower.field_149859_a).func_77655_b("rose");
         } else if (var4 == Blocks.field_150431_aC) {
            var5 = new ItemSnow(Blocks.field_150431_aC, Blocks.field_150431_aC);
         } else if (var4 == Blocks.field_150392_bi) {
            var5 = new ItemLilyPad(Blocks.field_150392_bi);
         } else if (var4 == Blocks.field_150331_J) {
            var5 = new ItemPiston(Blocks.field_150331_J);
         } else if (var4 == Blocks.field_150320_F) {
            var5 = new ItemPiston(Blocks.field_150320_F);
         } else if (var4 == Blocks.field_150463_bK) {
            var5 = new ItemMultiTexture(Blocks.field_150463_bK, Blocks.field_150463_bK, BlockWall.field_150092_a).func_77655_b("cobbleWall");
         } else if (var4 == Blocks.field_150467_bQ) {
            var5 = new ItemAnvilBlock(Blocks.field_150467_bQ).func_77655_b("anvil");
         } else if (var4 == Blocks.field_150398_cm) {
            var5 = new ItemDoublePlant(Blocks.field_150398_cm, Blocks.field_150398_cm, BlockDoublePlant.field_149892_a).func_77655_b("doublePlant");
         } else {
            if (var1.contains(var4)) {
               continue;
            }

            var5 = new ItemBlock(var4);
         }

         field_150901_e.func_148756_a(Block.func_149682_b(var4), var3, var5);
      }
   }

   public Item func_77625_d(int var1) {
      this.field_77777_bU = var1;
      return this;
   }

   public int func_94901_k() {
      return 1;
   }

   public IIcon func_77617_a(int var1) {
      return this.field_77791_bV;
   }

   public final IIcon func_77650_f(ItemStack var1) {
      return this.func_77617_a(var1.func_77960_j());
   }

   public boolean func_77648_a(ItemStack var1, EntityPlayer var2, World var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10) {
      return false;
   }

   public float func_150893_a(ItemStack var1, Block var2) {
      return 1.0F;
   }

   public ItemStack func_77659_a(ItemStack var1, World var2, EntityPlayer var3) {
      return var1;
   }

   public ItemStack func_77654_b(ItemStack var1, World var2, EntityPlayer var3) {
      return var1;
   }

   public int func_77639_j() {
      return this.field_77777_bU;
   }

   public int func_77647_b(int var1) {
      return 0;
   }

   public boolean func_77614_k() {
      return this.field_77787_bX;
   }

   protected Item func_77627_a(boolean var1) {
      this.field_77787_bX = var1;
      return this;
   }

   public int func_77612_l() {
      return this.field_77699_b;
   }

   protected Item func_77656_e(int var1) {
      this.field_77699_b = var1;
      return this;
   }

   public boolean func_77645_m() {
      return this.field_77699_b > 0 && !this.field_77787_bX;
   }

   public boolean func_77644_a(ItemStack var1, EntityLivingBase var2, EntityLivingBase var3) {
      return false;
   }

   public boolean func_150894_a(ItemStack var1, World var2, Block var3, int var4, int var5, int var6, EntityLivingBase var7) {
      return false;
   }

   public boolean func_150897_b(Block var1) {
      return false;
   }

   public boolean func_111207_a(ItemStack var1, EntityPlayer var2, EntityLivingBase var3) {
      return false;
   }

   public Item func_77664_n() {
      this.field_77789_bW = true;
      return this;
   }

   public boolean func_77662_d() {
      return this.field_77789_bW;
   }

   public boolean func_77629_n_() {
      return false;
   }

   public Item func_77655_b(String var1) {
      this.field_77774_bZ = var1;
      return this;
   }

   public String func_77657_g(ItemStack var1) {
      String var2 = this.func_77667_c(var1);
      return var2 == null ? "" : StatCollector.func_74838_a(var2);
   }

   public String func_77658_a() {
      return "item." + this.field_77774_bZ;
   }

   public String func_77667_c(ItemStack var1) {
      return "item." + this.field_77774_bZ;
   }

   public Item func_77642_a(Item var1) {
      this.field_77700_c = var1;
      return this;
   }

   public boolean func_77630_h(ItemStack var1) {
      return true;
   }

   public boolean func_77651_p() {
      return true;
   }

   public Item func_77668_q() {
      return this.field_77700_c;
   }

   public boolean func_77634_r() {
      return this.field_77700_c != null;
   }

   public int func_82790_a(ItemStack var1, int var2) {
      return 16777215;
   }

   public void func_77663_a(ItemStack var1, World var2, Entity var3, int var4, boolean var5) {
   }

   public void func_77622_d(ItemStack var1, World var2, EntityPlayer var3) {
   }

   public boolean func_77643_m_() {
      return false;
   }

   public EnumAction func_77661_b(ItemStack var1) {
      return EnumAction.none;
   }

   public int func_77626_a(ItemStack var1) {
      return 0;
   }

   public void func_77615_a(ItemStack var1, World var2, EntityPlayer var3, int var4) {
   }

   protected Item func_77631_c(String var1) {
      this.field_77785_bY = var1;
      return this;
   }

   public String func_150896_i(ItemStack var1) {
      return this.field_77785_bY;
   }

   public boolean func_150892_m(ItemStack var1) {
      return this.func_150896_i(var1) != null;
   }

   public void func_77624_a(ItemStack var1, EntityPlayer var2, List var3, boolean var4) {
   }

   public String func_77653_i(ItemStack var1) {
      return ("" + StatCollector.func_74838_a(this.func_77657_g(var1) + ".name")).trim();
   }

   public boolean func_77636_d(ItemStack var1) {
      return var1.func_77948_v();
   }

   public EnumRarity func_77613_e(ItemStack var1) {
      return var1.func_77948_v() ? EnumRarity.rare : EnumRarity.common;
   }

   public boolean func_77616_k(ItemStack var1) {
      return this.func_77639_j() == 1 && this.func_77645_m();
   }

   protected MovingObjectPosition func_77621_a(World var1, EntityPlayer var2, boolean var3) {
      float var4 = 1.0F;
      float var5 = var2.field_70127_C + (var2.field_70125_A - var2.field_70127_C) * var4;
      float var6 = var2.field_70126_B + (var2.field_70177_z - var2.field_70126_B) * var4;
      double var7 = var2.field_70169_q + (var2.field_70165_t - var2.field_70169_q) * (double)var4;
      double var9 = var2.field_70167_r + (var2.field_70163_u - var2.field_70167_r) * (double)var4 + 1.62 - (double)var2.field_70129_M;
      double var11 = var2.field_70166_s + (var2.field_70161_v - var2.field_70166_s) * (double)var4;
      Vec3 var13 = Vec3.func_72443_a(var7, var9, var11);
      float var14 = MathHelper.func_76134_b(-var6 * 0.017453292F - 3.1415927F);
      float var15 = MathHelper.func_76126_a(-var6 * 0.017453292F - 3.1415927F);
      float var16 = -MathHelper.func_76134_b(-var5 * 0.017453292F);
      float var17 = MathHelper.func_76126_a(-var5 * 0.017453292F);
      float var18 = var15 * var16;
      float var20 = var14 * var16;
      double var21 = 5.0;
      Vec3 var23 = var13.func_72441_c((double)var18 * var21, (double)var17 * var21, (double)var20 * var21);
      return var1.func_147447_a(var13, var23, var3, !var3, false);
   }

   public int func_77619_b() {
      return 0;
   }

   public boolean func_77623_v() {
      return false;
   }

   public IIcon func_77618_c(int var1, int var2) {
      return this.func_77617_a(var1);
   }

   public void func_150895_a(Item var1, CreativeTabs var2, List var3) {
      var3.add(new ItemStack(var1, 1, 0));
   }

   public CreativeTabs func_77640_w() {
      return this.field_77701_a;
   }

   public Item func_77637_a(CreativeTabs var1) {
      this.field_77701_a = var1;
      return this;
   }

   public boolean func_82788_x() {
      return true;
   }

   public boolean func_82789_a(ItemStack var1, ItemStack var2) {
      return false;
   }

   public void func_94581_a(IIconRegister var1) {
      this.field_77791_bV = var1.func_94245_a(this.func_111208_A());
   }

   public Multimap func_111205_h() {
      return HashMultimap.create();
   }

   protected Item func_111206_d(String var1) {
      this.field_111218_cA = var1;
      return this;
   }

   protected String func_111208_A() {
      return this.field_111218_cA == null ? "MISSING_ICON_ITEM_" + field_150901_e.func_148757_b(this) + "_" + this.field_77774_bZ : this.field_111218_cA;
   }
}
