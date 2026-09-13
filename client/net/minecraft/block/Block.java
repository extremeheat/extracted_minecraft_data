package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.RegistryNamespaced;
import net.minecraft.util.RegistryNamespacedDefaultedByKey;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class Block {
   public static final RegistryNamespaced field_149771_c = new RegistryNamespacedDefaultedByKey("air");
   private CreativeTabs field_149772_a;
   protected String field_149768_d;
   public static final Block$SoundType field_149769_e = new Block$SoundType("stone", 1.0F, 1.0F);
   public static final Block$SoundType field_149766_f = new Block$SoundType("wood", 1.0F, 1.0F);
   public static final Block$SoundType field_149767_g = new Block$SoundType("gravel", 1.0F, 1.0F);
   public static final Block$SoundType field_149779_h = new Block$SoundType("grass", 1.0F, 1.0F);
   public static final Block$SoundType field_149780_i = new Block$SoundType("stone", 1.0F, 1.0F);
   public static final Block$SoundType field_149777_j = new Block$SoundType("stone", 1.0F, 1.5F);
   public static final Block$SoundType field_149778_k = new Block$1("stone", 1.0F, 1.0F);
   public static final Block$SoundType field_149775_l = new Block$SoundType("cloth", 1.0F, 1.0F);
   public static final Block$SoundType field_149776_m = new Block$SoundType("sand", 1.0F, 1.0F);
   public static final Block$SoundType field_149773_n = new Block$SoundType("snow", 1.0F, 1.0F);
   public static final Block$SoundType field_149774_o = new Block$2("ladder", 1.0F, 1.0F);
   public static final Block$SoundType field_149788_p = new Block$3("anvil", 0.3F, 1.0F);
   protected boolean field_149787_q;
   protected int field_149786_r;
   protected boolean field_149785_s;
   protected int field_149784_t;
   protected boolean field_149783_u;
   protected float field_149782_v;
   protected float field_149781_w;
   protected boolean field_149791_x = true;
   protected boolean field_149790_y = true;
   protected boolean field_149789_z;
   protected boolean field_149758_A;
   protected double field_149759_B;
   protected double field_149760_C;
   protected double field_149754_D;
   protected double field_149755_E;
   protected double field_149756_F;
   protected double field_149757_G;
   public Block$SoundType field_149762_H = field_149769_e;
   public float field_149763_I = 1.0F;
   protected final Material field_149764_J;
   public float field_149765_K = 0.6F;
   private String field_149770_b;
   protected IIcon field_149761_L;

   public static int func_149682_b(Block var0) {
      return field_149771_c.func_148757_b(var0);
   }

   public static Block func_149729_e(int var0) {
      return (Block)field_149771_c.func_148754_a(var0);
   }

   public static Block func_149634_a(Item var0) {
      return func_149729_e(Item.func_150891_b(var0));
   }

   public static Block func_149684_b(String var0) {
      if (field_149771_c.func_148741_d(var0)) {
         return (Block)field_149771_c.func_82594_a(var0);
      } else {
         try {
            return (Block)field_149771_c.func_148754_a(Integer.parseInt(var0));
         } catch (NumberFormatException var2) {
            return null;
         }
      }
   }

   public boolean func_149730_j() {
      return this.field_149787_q;
   }

   public int func_149717_k() {
      return this.field_149786_r;
   }

   public boolean func_149751_l() {
      return this.field_149785_s;
   }

   public int func_149750_m() {
      return this.field_149784_t;
   }

   public boolean func_149710_n() {
      return this.field_149783_u;
   }

   public Material func_149688_o() {
      return this.field_149764_J;
   }

   public MapColor func_149728_f(int var1) {
      return this.func_149688_o().func_151565_r();
   }

   public static void func_149671_p() {
      field_149771_c.func_148756_a(0, "air", new BlockAir().func_149663_c("air"));
      field_149771_c.func_148756_a(
         1, "stone", new BlockStone().func_149711_c(1.5F).func_149752_b(10.0F).func_149672_a(field_149780_i).func_149663_c("stone").func_149658_d("stone")
      );
      field_149771_c.func_148756_a(
         2, "grass", new BlockGrass().func_149711_c(0.6F).func_149672_a(field_149779_h).func_149663_c("grass").func_149658_d("grass")
      );
      field_149771_c.func_148756_a(3, "dirt", new BlockDirt().func_149711_c(0.5F).func_149672_a(field_149767_g).func_149663_c("dirt").func_149658_d("dirt"));
      Block var0 = new Block(Material.field_151576_e)
         .func_149711_c(2.0F)
         .func_149752_b(10.0F)
         .func_149672_a(field_149780_i)
         .func_149663_c("stonebrick")
         .func_149647_a(CreativeTabs.field_78030_b)
         .func_149658_d("cobblestone");
      field_149771_c.func_148756_a(4, "cobblestone", var0);
      Block var1 = new BlockWood().func_149711_c(2.0F).func_149752_b(5.0F).func_149672_a(field_149766_f).func_149663_c("wood").func_149658_d("planks");
      field_149771_c.func_148756_a(5, "planks", var1);
      field_149771_c.func_148756_a(
         6, "sapling", new BlockSapling().func_149711_c(0.0F).func_149672_a(field_149779_h).func_149663_c("sapling").func_149658_d("sapling")
      );
      field_149771_c.func_148756_a(
         7,
         "bedrock",
         new Block(Material.field_151576_e)
            .func_149722_s()
            .func_149752_b(6000000.0F)
            .func_149672_a(field_149780_i)
            .func_149663_c("bedrock")
            .func_149649_H()
            .func_149647_a(CreativeTabs.field_78030_b)
            .func_149658_d("bedrock")
      );
      field_149771_c.func_148756_a(
         8,
         "flowing_water",
         new BlockDynamicLiquid(Material.field_151586_h)
            .func_149711_c(100.0F)
            .func_149713_g(3)
            .func_149663_c("water")
            .func_149649_H()
            .func_149658_d("water_flow")
      );
      field_149771_c.func_148756_a(
         9,
         "water",
         new BlockStaticLiquid(Material.field_151586_h)
            .func_149711_c(100.0F)
            .func_149713_g(3)
            .func_149663_c("water")
            .func_149649_H()
            .func_149658_d("water_still")
      );
      field_149771_c.func_148756_a(
         10,
         "flowing_lava",
         new BlockDynamicLiquid(Material.field_151587_i)
            .func_149711_c(100.0F)
            .func_149715_a(1.0F)
            .func_149663_c("lava")
            .func_149649_H()
            .func_149658_d("lava_flow")
      );
      field_149771_c.func_148756_a(
         11,
         "lava",
         new BlockStaticLiquid(Material.field_151587_i)
            .func_149711_c(100.0F)
            .func_149715_a(1.0F)
            .func_149663_c("lava")
            .func_149649_H()
            .func_149658_d("lava_still")
      );
      field_149771_c.func_148756_a(12, "sand", new BlockSand().func_149711_c(0.5F).func_149672_a(field_149776_m).func_149663_c("sand").func_149658_d("sand"));
      field_149771_c.func_148756_a(
         13, "gravel", new BlockGravel().func_149711_c(0.6F).func_149672_a(field_149767_g).func_149663_c("gravel").func_149658_d("gravel")
      );
      field_149771_c.func_148756_a(
         14,
         "gold_ore",
         new BlockOre().func_149711_c(3.0F).func_149752_b(5.0F).func_149672_a(field_149780_i).func_149663_c("oreGold").func_149658_d("gold_ore")
      );
      field_149771_c.func_148756_a(
         15,
         "iron_ore",
         new BlockOre().func_149711_c(3.0F).func_149752_b(5.0F).func_149672_a(field_149780_i).func_149663_c("oreIron").func_149658_d("iron_ore")
      );
      field_149771_c.func_148756_a(
         16,
         "coal_ore",
         new BlockOre().func_149711_c(3.0F).func_149752_b(5.0F).func_149672_a(field_149780_i).func_149663_c("oreCoal").func_149658_d("coal_ore")
      );
      field_149771_c.func_148756_a(17, "log", new BlockOldLog().func_149663_c("log").func_149658_d("log"));
      field_149771_c.func_148756_a(18, "leaves", new BlockOldLeaf().func_149663_c("leaves").func_149658_d("leaves"));
      field_149771_c.func_148756_a(
         19, "sponge", new BlockSponge().func_149711_c(0.6F).func_149672_a(field_149779_h).func_149663_c("sponge").func_149658_d("sponge")
      );
      field_149771_c.func_148756_a(
         20,
         "glass",
         new BlockGlass(Material.field_151592_s, false).func_149711_c(0.3F).func_149672_a(field_149778_k).func_149663_c("glass").func_149658_d("glass")
      );
      field_149771_c.func_148756_a(
         21,
         "lapis_ore",
         new BlockOre().func_149711_c(3.0F).func_149752_b(5.0F).func_149672_a(field_149780_i).func_149663_c("oreLapis").func_149658_d("lapis_ore")
      );
      field_149771_c.func_148756_a(
         22,
         "lapis_block",
         new BlockCompressed(MapColor.field_151652_H)
            .func_149711_c(3.0F)
            .func_149752_b(5.0F)
            .func_149672_a(field_149780_i)
            .func_149663_c("blockLapis")
            .func_149647_a(CreativeTabs.field_78030_b)
            .func_149658_d("lapis_block")
      );
      field_149771_c.func_148756_a(
         23, "dispenser", new BlockDispenser().func_149711_c(3.5F).func_149672_a(field_149780_i).func_149663_c("dispenser").func_149658_d("dispenser")
      );
      Block var2 = new BlockSandStone().func_149672_a(field_149780_i).func_149711_c(0.8F).func_149663_c("sandStone").func_149658_d("sandstone");
      field_149771_c.func_148756_a(24, "sandstone", var2);
      field_149771_c.func_148756_a(25, "noteblock", new BlockNote().func_149711_c(0.8F).func_149663_c("musicBlock").func_149658_d("noteblock"));
      field_149771_c.func_148756_a(26, "bed", new BlockBed().func_149711_c(0.2F).func_149663_c("bed").func_149649_H().func_149658_d("bed"));
      field_149771_c.func_148756_a(
         27, "golden_rail", new BlockRailPowered().func_149711_c(0.7F).func_149672_a(field_149777_j).func_149663_c("goldenRail").func_149658_d("rail_golden")
      );
      field_149771_c.func_148756_a(
         28,
         "detector_rail",
         new BlockRailDetector().func_149711_c(0.7F).func_149672_a(field_149777_j).func_149663_c("detectorRail").func_149658_d("rail_detector")
      );
      field_149771_c.func_148756_a(29, "sticky_piston", new BlockPistonBase(true).func_149663_c("pistonStickyBase"));
      field_149771_c.func_148756_a(30, "web", new BlockWeb().func_149713_g(1).func_149711_c(4.0F).func_149663_c("web").func_149658_d("web"));
      field_149771_c.func_148756_a(31, "tallgrass", new BlockTallGrass().func_149711_c(0.0F).func_149672_a(field_149779_h).func_149663_c("tallgrass"));
      field_149771_c.func_148756_a(
         32, "deadbush", new BlockDeadBush().func_149711_c(0.0F).func_149672_a(field_149779_h).func_149663_c("deadbush").func_149658_d("deadbush")
      );
      field_149771_c.func_148756_a(33, "piston", new BlockPistonBase(false).func_149663_c("pistonBase"));
      field_149771_c.func_148756_a(34, "piston_head", new BlockPistonExtension());
      field_149771_c.func_148756_a(
         35,
         "wool",
         new BlockColored(Material.field_151580_n).func_149711_c(0.8F).func_149672_a(field_149775_l).func_149663_c("cloth").func_149658_d("wool_colored")
      );
      field_149771_c.func_148756_a(36, "piston_extension", new BlockPistonMoving());
      field_149771_c.func_148756_a(
         37, "yellow_flower", new BlockFlower(0).func_149711_c(0.0F).func_149672_a(field_149779_h).func_149663_c("flower1").func_149658_d("flower_dandelion")
      );
      field_149771_c.func_148756_a(
         38, "red_flower", new BlockFlower(1).func_149711_c(0.0F).func_149672_a(field_149779_h).func_149663_c("flower2").func_149658_d("flower_rose")
      );
      field_149771_c.func_148756_a(
         39,
         "brown_mushroom",
         new BlockMushroom().func_149711_c(0.0F).func_149672_a(field_149779_h).func_149715_a(0.125F).func_149663_c("mushroom").func_149658_d("mushroom_brown")
      );
      field_149771_c.func_148756_a(
         40, "red_mushroom", new BlockMushroom().func_149711_c(0.0F).func_149672_a(field_149779_h).func_149663_c("mushroom").func_149658_d("mushroom_red")
      );
      field_149771_c.func_148756_a(
         41,
         "gold_block",
         new BlockCompressed(MapColor.field_151647_F)
            .func_149711_c(3.0F)
            .func_149752_b(10.0F)
            .func_149672_a(field_149777_j)
            .func_149663_c("blockGold")
            .func_149658_d("gold_block")
      );
      field_149771_c.func_148756_a(
         42,
         "iron_block",
         new BlockCompressed(MapColor.field_151668_h)
            .func_149711_c(5.0F)
            .func_149752_b(10.0F)
            .func_149672_a(field_149777_j)
            .func_149663_c("blockIron")
            .func_149658_d("iron_block")
      );
      field_149771_c.func_148756_a(
         43, "double_stone_slab", new BlockStoneSlab(true).func_149711_c(2.0F).func_149752_b(10.0F).func_149672_a(field_149780_i).func_149663_c("stoneSlab")
      );
      field_149771_c.func_148756_a(
         44, "stone_slab", new BlockStoneSlab(false).func_149711_c(2.0F).func_149752_b(10.0F).func_149672_a(field_149780_i).func_149663_c("stoneSlab")
      );
      Block var3 = new Block(Material.field_151576_e)
         .func_149711_c(2.0F)
         .func_149752_b(10.0F)
         .func_149672_a(field_149780_i)
         .func_149663_c("brick")
         .func_149647_a(CreativeTabs.field_78030_b)
         .func_149658_d("brick");
      field_149771_c.func_148756_a(45, "brick_block", var3);
      field_149771_c.func_148756_a(46, "tnt", new BlockTNT().func_149711_c(0.0F).func_149672_a(field_149779_h).func_149663_c("tnt").func_149658_d("tnt"));
      field_149771_c.func_148756_a(
         47, "bookshelf", new BlockBookshelf().func_149711_c(1.5F).func_149672_a(field_149766_f).func_149663_c("bookshelf").func_149658_d("bookshelf")
      );
      field_149771_c.func_148756_a(
         48,
         "mossy_cobblestone",
         new Block(Material.field_151576_e)
            .func_149711_c(2.0F)
            .func_149752_b(10.0F)
            .func_149672_a(field_149780_i)
            .func_149663_c("stoneMoss")
            .func_149647_a(CreativeTabs.field_78030_b)
            .func_149658_d("cobblestone_mossy")
      );
      field_149771_c.func_148756_a(
         49,
         "obsidian",
         new BlockObsidian().func_149711_c(50.0F).func_149752_b(2000.0F).func_149672_a(field_149780_i).func_149663_c("obsidian").func_149658_d("obsidian")
      );
      field_149771_c.func_148756_a(
         50,
         "torch",
         new BlockTorch().func_149711_c(0.0F).func_149715_a(0.9375F).func_149672_a(field_149766_f).func_149663_c("torch").func_149658_d("torch_on")
      );
      field_149771_c.func_148756_a(
         51,
         "fire",
         new BlockFire().func_149711_c(0.0F).func_149715_a(1.0F).func_149672_a(field_149766_f).func_149663_c("fire").func_149649_H().func_149658_d("fire")
      );
      field_149771_c.func_148756_a(
         52,
         "mob_spawner",
         new BlockMobSpawner().func_149711_c(5.0F).func_149672_a(field_149777_j).func_149663_c("mobSpawner").func_149649_H().func_149658_d("mob_spawner")
      );
      field_149771_c.func_148756_a(53, "oak_stairs", new BlockStairs(var1, 0).func_149663_c("stairsWood"));
      field_149771_c.func_148756_a(54, "chest", new BlockChest(0).func_149711_c(2.5F).func_149672_a(field_149766_f).func_149663_c("chest"));
      field_149771_c.func_148756_a(
         55,
         "redstone_wire",
         new BlockRedstoneWire()
            .func_149711_c(0.0F)
            .func_149672_a(field_149769_e)
            .func_149663_c("redstoneDust")
            .func_149649_H()
            .func_149658_d("redstone_dust")
      );
      field_149771_c.func_148756_a(
         56,
         "diamond_ore",
         new BlockOre().func_149711_c(3.0F).func_149752_b(5.0F).func_149672_a(field_149780_i).func_149663_c("oreDiamond").func_149658_d("diamond_ore")
      );
      field_149771_c.func_148756_a(
         57,
         "diamond_block",
         new BlockCompressed(MapColor.field_151648_G)
            .func_149711_c(5.0F)
            .func_149752_b(10.0F)
            .func_149672_a(field_149777_j)
            .func_149663_c("blockDiamond")
            .func_149658_d("diamond_block")
      );
      field_149771_c.func_148756_a(
         58,
         "crafting_table",
         new BlockWorkbench().func_149711_c(2.5F).func_149672_a(field_149766_f).func_149663_c("workbench").func_149658_d("crafting_table")
      );
      field_149771_c.func_148756_a(59, "wheat", new BlockCrops().func_149663_c("crops").func_149658_d("wheat"));
      Block var4 = new BlockFarmland().func_149711_c(0.6F).func_149672_a(field_149767_g).func_149663_c("farmland").func_149658_d("farmland");
      field_149771_c.func_148756_a(60, "farmland", var4);
      field_149771_c.func_148756_a(
         61,
         "furnace",
         new BlockFurnace(false).func_149711_c(3.5F).func_149672_a(field_149780_i).func_149663_c("furnace").func_149647_a(CreativeTabs.field_78031_c)
      );
      field_149771_c.func_148756_a(
         62, "lit_furnace", new BlockFurnace(true).func_149711_c(3.5F).func_149672_a(field_149780_i).func_149715_a(0.875F).func_149663_c("furnace")
      );
      field_149771_c.func_148756_a(
         63,
         "standing_sign",
         new BlockSign(TileEntitySign.class, true).func_149711_c(1.0F).func_149672_a(field_149766_f).func_149663_c("sign").func_149649_H()
      );
      field_149771_c.func_148756_a(
         64,
         "wooden_door",
         new BlockDoor(Material.field_151575_d)
            .func_149711_c(3.0F)
            .func_149672_a(field_149766_f)
            .func_149663_c("doorWood")
            .func_149649_H()
            .func_149658_d("door_wood")
      );
      field_149771_c.func_148756_a(
         65, "ladder", new BlockLadder().func_149711_c(0.4F).func_149672_a(field_149774_o).func_149663_c("ladder").func_149658_d("ladder")
      );
      field_149771_c.func_148756_a(
         66, "rail", new BlockRail().func_149711_c(0.7F).func_149672_a(field_149777_j).func_149663_c("rail").func_149658_d("rail_normal")
      );
      field_149771_c.func_148756_a(67, "stone_stairs", new BlockStairs(var0, 0).func_149663_c("stairsStone"));
      field_149771_c.func_148756_a(
         68, "wall_sign", new BlockSign(TileEntitySign.class, false).func_149711_c(1.0F).func_149672_a(field_149766_f).func_149663_c("sign").func_149649_H()
      );
      field_149771_c.func_148756_a(
         69, "lever", new BlockLever().func_149711_c(0.5F).func_149672_a(field_149766_f).func_149663_c("lever").func_149658_d("lever")
      );
      field_149771_c.func_148756_a(
         70,
         "stone_pressure_plate",
         new BlockPressurePlate("stone", Material.field_151576_e, BlockPressurePlate$Sensitivity.mobs)
            .func_149711_c(0.5F)
            .func_149672_a(field_149780_i)
            .func_149663_c("pressurePlate")
      );
      field_149771_c.func_148756_a(
         71,
         "iron_door",
         new BlockDoor(Material.field_151573_f)
            .func_149711_c(5.0F)
            .func_149672_a(field_149777_j)
            .func_149663_c("doorIron")
            .func_149649_H()
            .func_149658_d("door_iron")
      );
      field_149771_c.func_148756_a(
         72,
         "wooden_pressure_plate",
         new BlockPressurePlate("planks_oak", Material.field_151575_d, BlockPressurePlate$Sensitivity.everything)
            .func_149711_c(0.5F)
            .func_149672_a(field_149766_f)
            .func_149663_c("pressurePlate")
      );
      field_149771_c.func_148756_a(
         73,
         "redstone_ore",
         new BlockRedstoneOre(false)
            .func_149711_c(3.0F)
            .func_149752_b(5.0F)
            .func_149672_a(field_149780_i)
            .func_149663_c("oreRedstone")
            .func_149647_a(CreativeTabs.field_78030_b)
            .func_149658_d("redstone_ore")
      );
      field_149771_c.func_148756_a(
         74,
         "lit_redstone_ore",
         new BlockRedstoneOre(true)
            .func_149715_a(0.625F)
            .func_149711_c(3.0F)
            .func_149752_b(5.0F)
            .func_149672_a(field_149780_i)
            .func_149663_c("oreRedstone")
            .func_149658_d("redstone_ore")
      );
      field_149771_c.func_148756_a(
         75,
         "unlit_redstone_torch",
         new BlockRedstoneTorch(false).func_149711_c(0.0F).func_149672_a(field_149766_f).func_149663_c("notGate").func_149658_d("redstone_torch_off")
      );
      field_149771_c.func_148756_a(
         76,
         "redstone_torch",
         new BlockRedstoneTorch(true)
            .func_149711_c(0.0F)
            .func_149715_a(0.5F)
            .func_149672_a(field_149766_f)
            .func_149663_c("notGate")
            .func_149647_a(CreativeTabs.field_78028_d)
            .func_149658_d("redstone_torch_on")
      );
      field_149771_c.func_148756_a(77, "stone_button", new BlockButtonStone().func_149711_c(0.5F).func_149672_a(field_149780_i).func_149663_c("button"));
      field_149771_c.func_148756_a(
         78, "snow_layer", new BlockSnow().func_149711_c(0.1F).func_149672_a(field_149773_n).func_149663_c("snow").func_149713_g(0).func_149658_d("snow")
      );
      field_149771_c.func_148756_a(
         79, "ice", new BlockIce().func_149711_c(0.5F).func_149713_g(3).func_149672_a(field_149778_k).func_149663_c("ice").func_149658_d("ice")
      );
      field_149771_c.func_148756_a(
         80, "snow", new BlockSnowBlock().func_149711_c(0.2F).func_149672_a(field_149773_n).func_149663_c("snow").func_149658_d("snow")
      );
      field_149771_c.func_148756_a(
         81, "cactus", new BlockCactus().func_149711_c(0.4F).func_149672_a(field_149775_l).func_149663_c("cactus").func_149658_d("cactus")
      );
      field_149771_c.func_148756_a(82, "clay", new BlockClay().func_149711_c(0.6F).func_149672_a(field_149767_g).func_149663_c("clay").func_149658_d("clay"));
      field_149771_c.func_148756_a(
         83, "reeds", new BlockReed().func_149711_c(0.0F).func_149672_a(field_149779_h).func_149663_c("reeds").func_149649_H().func_149658_d("reeds")
      );
      field_149771_c.func_148756_a(
         84,
         "jukebox",
         new BlockJukebox().func_149711_c(2.0F).func_149752_b(10.0F).func_149672_a(field_149780_i).func_149663_c("jukebox").func_149658_d("jukebox")
      );
      field_149771_c.func_148756_a(
         85,
         "fence",
         new BlockFence("planks_oak", Material.field_151575_d).func_149711_c(2.0F).func_149752_b(5.0F).func_149672_a(field_149766_f).func_149663_c("fence")
      );
      Block var5 = new BlockPumpkin(false).func_149711_c(1.0F).func_149672_a(field_149766_f).func_149663_c("pumpkin").func_149658_d("pumpkin");
      field_149771_c.func_148756_a(86, "pumpkin", var5);
      field_149771_c.func_148756_a(
         87, "netherrack", new BlockNetherrack().func_149711_c(0.4F).func_149672_a(field_149780_i).func_149663_c("hellrock").func_149658_d("netherrack")
      );
      field_149771_c.func_148756_a(
         88, "soul_sand", new BlockSoulSand().func_149711_c(0.5F).func_149672_a(field_149776_m).func_149663_c("hellsand").func_149658_d("soul_sand")
      );
      field_149771_c.func_148756_a(
         89,
         "glowstone",
         new BlockGlowstone(Material.field_151592_s)
            .func_149711_c(0.3F)
            .func_149672_a(field_149778_k)
            .func_149715_a(1.0F)
            .func_149663_c("lightgem")
            .func_149658_d("glowstone")
      );
      field_149771_c.func_148756_a(
         90,
         "portal",
         new BlockPortal().func_149711_c(-1.0F).func_149672_a(field_149778_k).func_149715_a(0.75F).func_149663_c("portal").func_149658_d("portal")
      );
      field_149771_c.func_148756_a(
         91,
         "lit_pumpkin",
         new BlockPumpkin(true).func_149711_c(1.0F).func_149672_a(field_149766_f).func_149715_a(1.0F).func_149663_c("litpumpkin").func_149658_d("pumpkin")
      );
      field_149771_c.func_148756_a(
         92, "cake", new BlockCake().func_149711_c(0.5F).func_149672_a(field_149775_l).func_149663_c("cake").func_149649_H().func_149658_d("cake")
      );
      field_149771_c.func_148756_a(
         93,
         "unpowered_repeater",
         new BlockRedstoneRepeater(false)
            .func_149711_c(0.0F)
            .func_149672_a(field_149766_f)
            .func_149663_c("diode")
            .func_149649_H()
            .func_149658_d("repeater_off")
      );
      field_149771_c.func_148756_a(
         94,
         "powered_repeater",
         new BlockRedstoneRepeater(true)
            .func_149711_c(0.0F)
            .func_149715_a(0.625F)
            .func_149672_a(field_149766_f)
            .func_149663_c("diode")
            .func_149649_H()
            .func_149658_d("repeater_on")
      );
      field_149771_c.func_148756_a(
         95,
         "stained_glass",
         new BlockStainedGlass(Material.field_151592_s).func_149711_c(0.3F).func_149672_a(field_149778_k).func_149663_c("stainedGlass").func_149658_d("glass")
      );
      field_149771_c.func_148756_a(
         96,
         "trapdoor",
         new BlockTrapDoor(Material.field_151575_d)
            .func_149711_c(3.0F)
            .func_149672_a(field_149766_f)
            .func_149663_c("trapdoor")
            .func_149649_H()
            .func_149658_d("trapdoor")
      );
      field_149771_c.func_148756_a(97, "monster_egg", new BlockSilverfish().func_149711_c(0.75F).func_149663_c("monsterStoneEgg"));
      Block var6 = new BlockStoneBrick()
         .func_149711_c(1.5F)
         .func_149752_b(10.0F)
         .func_149672_a(field_149780_i)
         .func_149663_c("stonebricksmooth")
         .func_149658_d("stonebrick");
      field_149771_c.func_148756_a(98, "stonebrick", var6);
      field_149771_c.func_148756_a(
         99,
         "brown_mushroom_block",
         new BlockHugeMushroom(Material.field_151575_d, 0)
            .func_149711_c(0.2F)
            .func_149672_a(field_149766_f)
            .func_149663_c("mushroom")
            .func_149658_d("mushroom_block")
      );
      field_149771_c.func_148756_a(
         100,
         "red_mushroom_block",
         new BlockHugeMushroom(Material.field_151575_d, 1)
            .func_149711_c(0.2F)
            .func_149672_a(field_149766_f)
            .func_149663_c("mushroom")
            .func_149658_d("mushroom_block")
      );
      field_149771_c.func_148756_a(
         101,
         "iron_bars",
         new BlockPane("iron_bars", "iron_bars", Material.field_151573_f, true)
            .func_149711_c(5.0F)
            .func_149752_b(10.0F)
            .func_149672_a(field_149777_j)
            .func_149663_c("fenceIron")
      );
      field_149771_c.func_148756_a(
         102,
         "glass_pane",
         new BlockPane("glass", "glass_pane_top", Material.field_151592_s, false).func_149711_c(0.3F).func_149672_a(field_149778_k).func_149663_c("thinGlass")
      );
      Block var7 = new BlockMelon().func_149711_c(1.0F).func_149672_a(field_149766_f).func_149663_c("melon").func_149658_d("melon");
      field_149771_c.func_148756_a(103, "melon_block", var7);
      field_149771_c.func_148756_a(
         104, "pumpkin_stem", new BlockStem(var5).func_149711_c(0.0F).func_149672_a(field_149766_f).func_149663_c("pumpkinStem").func_149658_d("pumpkin_stem")
      );
      field_149771_c.func_148756_a(
         105, "melon_stem", new BlockStem(var7).func_149711_c(0.0F).func_149672_a(field_149766_f).func_149663_c("pumpkinStem").func_149658_d("melon_stem")
      );
      field_149771_c.func_148756_a(106, "vine", new BlockVine().func_149711_c(0.2F).func_149672_a(field_149779_h).func_149663_c("vine").func_149658_d("vine"));
      field_149771_c.func_148756_a(
         107, "fence_gate", new BlockFenceGate().func_149711_c(2.0F).func_149752_b(5.0F).func_149672_a(field_149766_f).func_149663_c("fenceGate")
      );
      field_149771_c.func_148756_a(108, "brick_stairs", new BlockStairs(var3, 0).func_149663_c("stairsBrick"));
      field_149771_c.func_148756_a(109, "stone_brick_stairs", new BlockStairs(var6, 0).func_149663_c("stairsStoneBrickSmooth"));
      field_149771_c.func_148756_a(
         110, "mycelium", new BlockMycelium().func_149711_c(0.6F).func_149672_a(field_149779_h).func_149663_c("mycel").func_149658_d("mycelium")
      );
      field_149771_c.func_148756_a(
         111, "waterlily", new BlockLilyPad().func_149711_c(0.0F).func_149672_a(field_149779_h).func_149663_c("waterlily").func_149658_d("waterlily")
      );
      Block var8 = new Block(Material.field_151576_e)
         .func_149711_c(2.0F)
         .func_149752_b(10.0F)
         .func_149672_a(field_149780_i)
         .func_149663_c("netherBrick")
         .func_149647_a(CreativeTabs.field_78030_b)
         .func_149658_d("nether_brick");
      field_149771_c.func_148756_a(112, "nether_brick", var8);
      field_149771_c.func_148756_a(
         113,
         "nether_brick_fence",
         new BlockFence("nether_brick", Material.field_151576_e)
            .func_149711_c(2.0F)
            .func_149752_b(10.0F)
            .func_149672_a(field_149780_i)
            .func_149663_c("netherFence")
      );
      field_149771_c.func_148756_a(114, "nether_brick_stairs", new BlockStairs(var8, 0).func_149663_c("stairsNetherBrick"));
      field_149771_c.func_148756_a(115, "nether_wart", new BlockNetherWart().func_149663_c("netherStalk").func_149658_d("nether_wart"));
      field_149771_c.func_148756_a(
         116,
         "enchanting_table",
         new BlockEnchantmentTable().func_149711_c(5.0F).func_149752_b(2000.0F).func_149663_c("enchantmentTable").func_149658_d("enchanting_table")
      );
      field_149771_c.func_148756_a(
         117, "brewing_stand", new BlockBrewingStand().func_149711_c(0.5F).func_149715_a(0.125F).func_149663_c("brewingStand").func_149658_d("brewing_stand")
      );
      field_149771_c.func_148756_a(118, "cauldron", new BlockCauldron().func_149711_c(2.0F).func_149663_c("cauldron").func_149658_d("cauldron"));
      field_149771_c.func_148756_a(119, "end_portal", new BlockEndPortal(Material.field_151567_E).func_149711_c(-1.0F).func_149752_b(6000000.0F));
      field_149771_c.func_148756_a(
         120,
         "end_portal_frame",
         new BlockEndPortalFrame()
            .func_149672_a(field_149778_k)
            .func_149715_a(0.125F)
            .func_149711_c(-1.0F)
            .func_149663_c("endPortalFrame")
            .func_149752_b(6000000.0F)
            .func_149647_a(CreativeTabs.field_78031_c)
            .func_149658_d("endframe")
      );
      field_149771_c.func_148756_a(
         121,
         "end_stone",
         new Block(Material.field_151576_e)
            .func_149711_c(3.0F)
            .func_149752_b(15.0F)
            .func_149672_a(field_149780_i)
            .func_149663_c("whiteStone")
            .func_149647_a(CreativeTabs.field_78030_b)
            .func_149658_d("end_stone")
      );
      field_149771_c.func_148756_a(
         122,
         "dragon_egg",
         new BlockDragonEgg()
            .func_149711_c(3.0F)
            .func_149752_b(15.0F)
            .func_149672_a(field_149780_i)
            .func_149715_a(0.125F)
            .func_149663_c("dragonEgg")
            .func_149658_d("dragon_egg")
      );
      field_149771_c.func_148756_a(
         123,
         "redstone_lamp",
         new BlockRedstoneLight(false)
            .func_149711_c(0.3F)
            .func_149672_a(field_149778_k)
            .func_149663_c("redstoneLight")
            .func_149647_a(CreativeTabs.field_78028_d)
            .func_149658_d("redstone_lamp_off")
      );
      field_149771_c.func_148756_a(
         124,
         "lit_redstone_lamp",
         new BlockRedstoneLight(true).func_149711_c(0.3F).func_149672_a(field_149778_k).func_149663_c("redstoneLight").func_149658_d("redstone_lamp_on")
      );
      field_149771_c.func_148756_a(
         125, "double_wooden_slab", new BlockWoodSlab(true).func_149711_c(2.0F).func_149752_b(5.0F).func_149672_a(field_149766_f).func_149663_c("woodSlab")
      );
      field_149771_c.func_148756_a(
         126, "wooden_slab", new BlockWoodSlab(false).func_149711_c(2.0F).func_149752_b(5.0F).func_149672_a(field_149766_f).func_149663_c("woodSlab")
      );
      field_149771_c.func_148756_a(
         127, "cocoa", new BlockCocoa().func_149711_c(0.2F).func_149752_b(5.0F).func_149672_a(field_149766_f).func_149663_c("cocoa").func_149658_d("cocoa")
      );
      field_149771_c.func_148756_a(128, "sandstone_stairs", new BlockStairs(var2, 0).func_149663_c("stairsSandStone"));
      field_149771_c.func_148756_a(
         129,
         "emerald_ore",
         new BlockOre().func_149711_c(3.0F).func_149752_b(5.0F).func_149672_a(field_149780_i).func_149663_c("oreEmerald").func_149658_d("emerald_ore")
      );
      field_149771_c.func_148756_a(
         130,
         "ender_chest",
         new BlockEnderChest().func_149711_c(22.5F).func_149752_b(1000.0F).func_149672_a(field_149780_i).func_149663_c("enderChest").func_149715_a(0.5F)
      );
      field_149771_c.func_148756_a(131, "tripwire_hook", new BlockTripWireHook().func_149663_c("tripWireSource").func_149658_d("trip_wire_source"));
      field_149771_c.func_148756_a(132, "tripwire", new BlockTripWire().func_149663_c("tripWire").func_149658_d("trip_wire"));
      field_149771_c.func_148756_a(
         133,
         "emerald_block",
         new BlockCompressed(MapColor.field_151653_I)
            .func_149711_c(5.0F)
            .func_149752_b(10.0F)
            .func_149672_a(field_149777_j)
            .func_149663_c("blockEmerald")
            .func_149658_d("emerald_block")
      );
      field_149771_c.func_148756_a(134, "spruce_stairs", new BlockStairs(var1, 1).func_149663_c("stairsWoodSpruce"));
      field_149771_c.func_148756_a(135, "birch_stairs", new BlockStairs(var1, 2).func_149663_c("stairsWoodBirch"));
      field_149771_c.func_148756_a(136, "jungle_stairs", new BlockStairs(var1, 3).func_149663_c("stairsWoodJungle"));
      field_149771_c.func_148756_a(
         137, "command_block", new BlockCommandBlock().func_149722_s().func_149752_b(6000000.0F).func_149663_c("commandBlock").func_149658_d("command_block")
      );
      field_149771_c.func_148756_a(138, "beacon", new BlockBeacon().func_149663_c("beacon").func_149715_a(1.0F).func_149658_d("beacon"));
      field_149771_c.func_148756_a(139, "cobblestone_wall", new BlockWall(var0).func_149663_c("cobbleWall"));
      field_149771_c.func_148756_a(
         140, "flower_pot", new BlockFlowerPot().func_149711_c(0.0F).func_149672_a(field_149769_e).func_149663_c("flowerPot").func_149658_d("flower_pot")
      );
      field_149771_c.func_148756_a(141, "carrots", new BlockCarrot().func_149663_c("carrots").func_149658_d("carrots"));
      field_149771_c.func_148756_a(142, "potatoes", new BlockPotato().func_149663_c("potatoes").func_149658_d("potatoes"));
      field_149771_c.func_148756_a(143, "wooden_button", new BlockButtonWood().func_149711_c(0.5F).func_149672_a(field_149766_f).func_149663_c("button"));
      field_149771_c.func_148756_a(
         144, "skull", new BlockSkull().func_149711_c(1.0F).func_149672_a(field_149780_i).func_149663_c("skull").func_149658_d("skull")
      );
      field_149771_c.func_148756_a(
         145, "anvil", new BlockAnvil().func_149711_c(5.0F).func_149672_a(field_149788_p).func_149752_b(2000.0F).func_149663_c("anvil")
      );
      field_149771_c.func_148756_a(146, "trapped_chest", new BlockChest(1).func_149711_c(2.5F).func_149672_a(field_149766_f).func_149663_c("chestTrap"));
      field_149771_c.func_148756_a(
         147,
         "light_weighted_pressure_plate",
         new BlockPressurePlateWeighted("gold_block", Material.field_151573_f, 15)
            .func_149711_c(0.5F)
            .func_149672_a(field_149766_f)
            .func_149663_c("weightedPlate_light")
      );
      field_149771_c.func_148756_a(
         148,
         "heavy_weighted_pressure_plate",
         new BlockPressurePlateWeighted("iron_block", Material.field_151573_f, 150)
            .func_149711_c(0.5F)
            .func_149672_a(field_149766_f)
            .func_149663_c("weightedPlate_heavy")
      );
      field_149771_c.func_148756_a(
         149,
         "unpowered_comparator",
         new BlockRedstoneComparator(false)
            .func_149711_c(0.0F)
            .func_149672_a(field_149766_f)
            .func_149663_c("comparator")
            .func_149649_H()
            .func_149658_d("comparator_off")
      );
      field_149771_c.func_148756_a(
         150,
         "powered_comparator",
         new BlockRedstoneComparator(true)
            .func_149711_c(0.0F)
            .func_149715_a(0.625F)
            .func_149672_a(field_149766_f)
            .func_149663_c("comparator")
            .func_149649_H()
            .func_149658_d("comparator_on")
      );
      field_149771_c.func_148756_a(
         151,
         "daylight_detector",
         new BlockDaylightDetector().func_149711_c(0.2F).func_149672_a(field_149766_f).func_149663_c("daylightDetector").func_149658_d("daylight_detector")
      );
      field_149771_c.func_148756_a(
         152,
         "redstone_block",
         new BlockCompressedPowered(MapColor.field_151656_f)
            .func_149711_c(5.0F)
            .func_149752_b(10.0F)
            .func_149672_a(field_149777_j)
            .func_149663_c("blockRedstone")
            .func_149658_d("redstone_block")
      );
      field_149771_c.func_148756_a(
         153,
         "quartz_ore",
         new BlockOre().func_149711_c(3.0F).func_149752_b(5.0F).func_149672_a(field_149780_i).func_149663_c("netherquartz").func_149658_d("quartz_ore")
      );
      field_149771_c.func_148756_a(
         154,
         "hopper",
         new BlockHopper().func_149711_c(3.0F).func_149752_b(8.0F).func_149672_a(field_149766_f).func_149663_c("hopper").func_149658_d("hopper")
      );
      Block var9 = new BlockQuartz().func_149672_a(field_149780_i).func_149711_c(0.8F).func_149663_c("quartzBlock").func_149658_d("quartz_block");
      field_149771_c.func_148756_a(155, "quartz_block", var9);
      field_149771_c.func_148756_a(156, "quartz_stairs", new BlockStairs(var9, 0).func_149663_c("stairsQuartz"));
      field_149771_c.func_148756_a(
         157,
         "activator_rail",
         new BlockRailPowered().func_149711_c(0.7F).func_149672_a(field_149777_j).func_149663_c("activatorRail").func_149658_d("rail_activator")
      );
      field_149771_c.func_148756_a(
         158, "dropper", new BlockDropper().func_149711_c(3.5F).func_149672_a(field_149780_i).func_149663_c("dropper").func_149658_d("dropper")
      );
      field_149771_c.func_148756_a(
         159,
         "stained_hardened_clay",
         new BlockColored(Material.field_151576_e)
            .func_149711_c(1.25F)
            .func_149752_b(7.0F)
            .func_149672_a(field_149780_i)
            .func_149663_c("clayHardenedStained")
            .func_149658_d("hardened_clay_stained")
      );
      field_149771_c.func_148756_a(
         160,
         "stained_glass_pane",
         new BlockStainedGlassPane().func_149711_c(0.3F).func_149672_a(field_149778_k).func_149663_c("thinStainedGlass").func_149658_d("glass")
      );
      field_149771_c.func_148756_a(161, "leaves2", new BlockNewLeaf().func_149663_c("leaves").func_149658_d("leaves"));
      field_149771_c.func_148756_a(162, "log2", new BlockNewLog().func_149663_c("log").func_149658_d("log"));
      field_149771_c.func_148756_a(163, "acacia_stairs", new BlockStairs(var1, 4).func_149663_c("stairsWoodAcacia"));
      field_149771_c.func_148756_a(164, "dark_oak_stairs", new BlockStairs(var1, 5).func_149663_c("stairsWoodDarkOak"));
      field_149771_c.func_148756_a(
         170,
         "hay_block",
         new BlockHay()
            .func_149711_c(0.5F)
            .func_149672_a(field_149779_h)
            .func_149663_c("hayBlock")
            .func_149647_a(CreativeTabs.field_78030_b)
            .func_149658_d("hay_block")
      );
      field_149771_c.func_148756_a(
         171, "carpet", new BlockCarpet().func_149711_c(0.1F).func_149672_a(field_149775_l).func_149663_c("woolCarpet").func_149713_g(0)
      );
      field_149771_c.func_148756_a(
         172,
         "hardened_clay",
         new BlockHardenedClay()
            .func_149711_c(1.25F)
            .func_149752_b(7.0F)
            .func_149672_a(field_149780_i)
            .func_149663_c("clayHardened")
            .func_149658_d("hardened_clay")
      );
      field_149771_c.func_148756_a(
         173,
         "coal_block",
         new Block(Material.field_151576_e)
            .func_149711_c(5.0F)
            .func_149752_b(10.0F)
            .func_149672_a(field_149780_i)
            .func_149663_c("blockCoal")
            .func_149647_a(CreativeTabs.field_78030_b)
            .func_149658_d("coal_block")
      );
      field_149771_c.func_148756_a(
         174, "packed_ice", new BlockPackedIce().func_149711_c(0.5F).func_149672_a(field_149778_k).func_149663_c("icePacked").func_149658_d("ice_packed")
      );
      field_149771_c.func_148756_a(175, "double_plant", new BlockDoublePlant());

      for(Block var11 : field_149771_c) {
         if (var11.field_149764_J == Material.field_151579_a) {
            var11.field_149783_u = false;
         } else {
            boolean var12 = false;
            boolean var13 = var11.func_149645_b() == 10;
            boolean var14 = var11 instanceof BlockSlab;
            boolean var15 = var11 == var4;
            boolean var16 = var11.field_149785_s;
            boolean var17 = var11.field_149786_r == 0;
            if (var13 || var14 || var15 || var16 || var17) {
               var12 = true;
            }

            var11.field_149783_u = var12;
         }
      }
   }

   protected Block(Material var1) {
      super();
      this.field_149764_J = var1;
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      this.field_149787_q = this.func_149662_c();
      this.field_149786_r = this.func_149662_c() ? 255 : 0;
      this.field_149785_s = !var1.func_76228_b();
   }

   protected Block func_149672_a(Block$SoundType var1) {
      this.field_149762_H = var1;
      return this;
   }

   protected Block func_149713_g(int var1) {
      this.field_149786_r = var1;
      return this;
   }

   protected Block func_149715_a(float var1) {
      this.field_149784_t = (int)(15.0F * var1);
      return this;
   }

   protected Block func_149752_b(float var1) {
      this.field_149781_w = var1 * 3.0F;
      return this;
   }

   public boolean func_149637_q() {
      return this.field_149764_J.func_76230_c() && this.func_149686_d();
   }

   public boolean func_149721_r() {
      return this.field_149764_J.func_76218_k() && this.func_149686_d() && !this.func_149744_f();
   }

   public boolean func_149686_d() {
      return true;
   }

   public boolean func_149655_b(IBlockAccess var1, int var2, int var3, int var4) {
      return !this.field_149764_J.func_76230_c();
   }

   public int func_149645_b() {
      return 0;
   }

   protected Block func_149711_c(float var1) {
      this.field_149782_v = var1;
      if (this.field_149781_w < var1 * 5.0F) {
         this.field_149781_w = var1 * 5.0F;
      }

      return this;
   }

   protected Block func_149722_s() {
      this.func_149711_c(-1.0F);
      return this;
   }

   public float func_149712_f(World var1, int var2, int var3, int var4) {
      return this.field_149782_v;
   }

   protected Block func_149675_a(boolean var1) {
      this.field_149789_z = var1;
      return this;
   }

   public boolean func_149653_t() {
      return this.field_149789_z;
   }

   public boolean func_149716_u() {
      return this.field_149758_A;
   }

   protected final void func_149676_a(float var1, float var2, float var3, float var4, float var5, float var6) {
      this.field_149759_B = (double)var1;
      this.field_149760_C = (double)var2;
      this.field_149754_D = (double)var3;
      this.field_149755_E = (double)var4;
      this.field_149756_F = (double)var5;
      this.field_149757_G = (double)var6;
   }

   public int func_149677_c(IBlockAccess var1, int var2, int var3, int var4) {
      Block var5 = var1.func_147439_a(var2, var3, var4);
      int var6 = var1.func_72802_i(var2, var3, var4, var5.func_149750_m());
      if (var6 == 0 && var5 instanceof BlockSlab) {
         var5 = var1.func_147439_a(var2, --var3, var4);
         return var1.func_72802_i(var2, var3, var4, var5.func_149750_m());
      } else {
         return var6;
      }
   }

   public boolean func_149646_a(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      if (var5 == 0 && this.field_149760_C > 0.0) {
         return true;
      } else if (var5 == 1 && this.field_149756_F < 1.0) {
         return true;
      } else if (var5 == 2 && this.field_149754_D > 0.0) {
         return true;
      } else if (var5 == 3 && this.field_149757_G < 1.0) {
         return true;
      } else if (var5 == 4 && this.field_149759_B > 0.0) {
         return true;
      } else if (var5 == 5 && this.field_149755_E < 1.0) {
         return true;
      } else {
         return !var1.func_147439_a(var2, var3, var4).func_149662_c();
      }
   }

   public boolean func_149747_d(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      return var1.func_147439_a(var2, var3, var4).func_149688_o().func_76220_a();
   }

   public IIcon func_149673_e(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      return this.func_149691_a(var5, var1.func_72805_g(var2, var3, var4));
   }

   public IIcon func_149691_a(int var1, int var2) {
      return this.field_149761_L;
   }

   public final IIcon func_149733_h(int var1) {
      return this.func_149691_a(var1, 0);
   }

   public AxisAlignedBB func_149633_g(World var1, int var2, int var3, int var4) {
      return AxisAlignedBB.func_72330_a(
         (double)var2 + this.field_149759_B,
         (double)var3 + this.field_149760_C,
         (double)var4 + this.field_149754_D,
         (double)var2 + this.field_149755_E,
         (double)var3 + this.field_149756_F,
         (double)var4 + this.field_149757_G
      );
   }

   public void func_149743_a(World var1, int var2, int var3, int var4, AxisAlignedBB var5, List var6, Entity var7) {
      AxisAlignedBB var8 = this.func_149668_a(var1, var2, var3, var4);
      if (var8 != null && var5.func_72326_a(var8)) {
         var6.add(var8);
      }
   }

   public AxisAlignedBB func_149668_a(World var1, int var2, int var3, int var4) {
      return AxisAlignedBB.func_72330_a(
         (double)var2 + this.field_149759_B,
         (double)var3 + this.field_149760_C,
         (double)var4 + this.field_149754_D,
         (double)var2 + this.field_149755_E,
         (double)var3 + this.field_149756_F,
         (double)var4 + this.field_149757_G
      );
   }

   public boolean func_149662_c() {
      return true;
   }

   public boolean func_149678_a(int var1, boolean var2) {
      return this.func_149703_v();
   }

   public boolean func_149703_v() {
      return true;
   }

   public void func_149674_a(World var1, int var2, int var3, int var4, Random var5) {
   }

   public void func_149734_b(World var1, int var2, int var3, int var4, Random var5) {
   }

   public void func_149664_b(World var1, int var2, int var3, int var4, int var5) {
   }

   public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
   }

   public int func_149738_a(World var1) {
      return 10;
   }

   public void func_149726_b(World var1, int var2, int var3, int var4) {
   }

   public void func_149749_a(World var1, int var2, int var3, int var4, Block var5, int var6) {
   }

   public int func_149745_a(Random var1) {
      return 1;
   }

   public Item func_149650_a(int var1, Random var2, int var3) {
      return Item.func_150898_a(this);
   }

   public float func_149737_a(EntityPlayer var1, World var2, int var3, int var4, int var5) {
      float var6 = this.func_149712_f(var2, var3, var4, var5);
      if (var6 < 0.0F) {
         return 0.0F;
      } else {
         return !var1.func_146099_a(this) ? var1.func_146096_a(this, false) / var6 / 100.0F : var1.func_146096_a(this, true) / var6 / 30.0F;
      }
   }

   public final void func_149697_b(World var1, int var2, int var3, int var4, int var5, int var6) {
      this.func_149690_a(var1, var2, var3, var4, var5, 1.0F, var6);
   }

   public void func_149690_a(World var1, int var2, int var3, int var4, int var5, float var6, int var7) {
      if (!var1.field_72995_K) {
         int var8 = this.func_149679_a(var7, var1.field_73012_v);

         for(int var9 = 0; var9 < var8; ++var9) {
            if (!(var1.field_73012_v.nextFloat() > var6)) {
               Item var10 = this.func_149650_a(var5, var1.field_73012_v, var7);
               if (var10 != null) {
                  this.func_149642_a(var1, var2, var3, var4, new ItemStack(var10, 1, this.func_149692_a(var5)));
               }
            }
         }
      }
   }

   protected void func_149642_a(World var1, int var2, int var3, int var4, ItemStack var5) {
      if (!var1.field_72995_K && var1.func_82736_K().func_82766_b("doTileDrops")) {
         float var6 = 0.7F;
         double var7 = (double)(var1.field_73012_v.nextFloat() * var6) + (double)(1.0F - var6) * 0.5;
         double var9 = (double)(var1.field_73012_v.nextFloat() * var6) + (double)(1.0F - var6) * 0.5;
         double var11 = (double)(var1.field_73012_v.nextFloat() * var6) + (double)(1.0F - var6) * 0.5;
         EntityItem var13 = new EntityItem(var1, (double)var2 + var7, (double)var3 + var9, (double)var4 + var11, var5);
         var13.field_145804_b = 10;
         var1.func_72838_d(var13);
      }
   }

   protected void func_149657_c(World var1, int var2, int var3, int var4, int var5) {
      if (!var1.field_72995_K) {
         while(var5 > 0) {
            int var6 = EntityXPOrb.func_70527_a(var5);
            var5 -= var6;
            var1.func_72838_d(new EntityXPOrb(var1, (double)var2 + 0.5, (double)var3 + 0.5, (double)var4 + 0.5, var6));
         }
      }
   }

   public int func_149692_a(int var1) {
      return 0;
   }

   public float func_149638_a(Entity var1) {
      return this.field_149781_w / 5.0F;
   }

   public MovingObjectPosition func_149731_a(World var1, int var2, int var3, int var4, Vec3 var5, Vec3 var6) {
      this.func_149719_a(var1, var2, var3, var4);
      var5 = var5.func_72441_c((double)(-var2), (double)(-var3), (double)(-var4));
      var6 = var6.func_72441_c((double)(-var2), (double)(-var3), (double)(-var4));
      Vec3 var7 = var5.func_72429_b(var6, this.field_149759_B);
      Vec3 var8 = var5.func_72429_b(var6, this.field_149755_E);
      Vec3 var9 = var5.func_72435_c(var6, this.field_149760_C);
      Vec3 var10 = var5.func_72435_c(var6, this.field_149756_F);
      Vec3 var11 = var5.func_72434_d(var6, this.field_149754_D);
      Vec3 var12 = var5.func_72434_d(var6, this.field_149757_G);
      if (!this.func_149654_a(var7)) {
         var7 = null;
      }

      if (!this.func_149654_a(var8)) {
         var8 = null;
      }

      if (!this.func_149687_b(var9)) {
         var9 = null;
      }

      if (!this.func_149687_b(var10)) {
         var10 = null;
      }

      if (!this.func_149661_c(var11)) {
         var11 = null;
      }

      if (!this.func_149661_c(var12)) {
         var12 = null;
      }

      Vec3 var13 = null;
      if (var7 != null && (var13 == null || var5.func_72436_e(var7) < var5.func_72436_e(var13))) {
         var13 = var7;
      }

      if (var8 != null && (var13 == null || var5.func_72436_e(var8) < var5.func_72436_e(var13))) {
         var13 = var8;
      }

      if (var9 != null && (var13 == null || var5.func_72436_e(var9) < var5.func_72436_e(var13))) {
         var13 = var9;
      }

      if (var10 != null && (var13 == null || var5.func_72436_e(var10) < var5.func_72436_e(var13))) {
         var13 = var10;
      }

      if (var11 != null && (var13 == null || var5.func_72436_e(var11) < var5.func_72436_e(var13))) {
         var13 = var11;
      }

      if (var12 != null && (var13 == null || var5.func_72436_e(var12) < var5.func_72436_e(var13))) {
         var13 = var12;
      }

      if (var13 == null) {
         return null;
      } else {
         byte var14 = -1;
         if (var13 == var7) {
            var14 = 4;
         }

         if (var13 == var8) {
            var14 = 5;
         }

         if (var13 == var9) {
            var14 = 0;
         }

         if (var13 == var10) {
            var14 = 1;
         }

         if (var13 == var11) {
            var14 = 2;
         }

         if (var13 == var12) {
            var14 = 3;
         }

         return new MovingObjectPosition(var2, var3, var4, var14, var13.func_72441_c((double)var2, (double)var3, (double)var4));
      }
   }

   private boolean func_149654_a(Vec3 var1) {
      if (var1 == null) {
         return false;
      } else {
         return var1.field_72448_b >= this.field_149760_C
            && var1.field_72448_b <= this.field_149756_F
            && var1.field_72449_c >= this.field_149754_D
            && var1.field_72449_c <= this.field_149757_G;
      }
   }

   private boolean func_149687_b(Vec3 var1) {
      if (var1 == null) {
         return false;
      } else {
         return var1.field_72450_a >= this.field_149759_B
            && var1.field_72450_a <= this.field_149755_E
            && var1.field_72449_c >= this.field_149754_D
            && var1.field_72449_c <= this.field_149757_G;
      }
   }

   private boolean func_149661_c(Vec3 var1) {
      if (var1 == null) {
         return false;
      } else {
         return var1.field_72450_a >= this.field_149759_B
            && var1.field_72450_a <= this.field_149755_E
            && var1.field_72448_b >= this.field_149760_C
            && var1.field_72448_b <= this.field_149756_F;
      }
   }

   public void func_149723_a(World var1, int var2, int var3, int var4, Explosion var5) {
   }

   public int func_149701_w() {
      return 0;
   }

   public boolean func_149705_a(World var1, int var2, int var3, int var4, int var5, ItemStack var6) {
      return this.func_149707_d(var1, var2, var3, var4, var5);
   }

   public boolean func_149707_d(World var1, int var2, int var3, int var4, int var5) {
      return this.func_149742_c(var1, var2, var3, var4);
   }

   public boolean func_149742_c(World var1, int var2, int var3, int var4) {
      return var1.func_147439_a(var2, var3, var4).field_149764_J.func_76222_j();
   }

   public boolean func_149727_a(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
      return false;
   }

   public void func_149724_b(World var1, int var2, int var3, int var4, Entity var5) {
   }

   public int func_149660_a(World var1, int var2, int var3, int var4, int var5, float var6, float var7, float var8, int var9) {
      return var9;
   }

   public void func_149699_a(World var1, int var2, int var3, int var4, EntityPlayer var5) {
   }

   public void func_149640_a(World var1, int var2, int var3, int var4, Entity var5, Vec3 var6) {
   }

   public void func_149719_a(IBlockAccess var1, int var2, int var3, int var4) {
   }

   public final double func_149704_x() {
      return this.field_149759_B;
   }

   public final double func_149753_y() {
      return this.field_149755_E;
   }

   public final double func_149665_z() {
      return this.field_149760_C;
   }

   public final double func_149669_A() {
      return this.field_149756_F;
   }

   public final double func_149706_B() {
      return this.field_149754_D;
   }

   public final double func_149693_C() {
      return this.field_149757_G;
   }

   public int func_149635_D() {
      return 16777215;
   }

   public int func_149741_i(int var1) {
      return 16777215;
   }

   public int func_149720_d(IBlockAccess var1, int var2, int var3, int var4) {
      return 16777215;
   }

   public int func_149709_b(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      return 0;
   }

   public boolean func_149744_f() {
      return false;
   }

   public void func_149670_a(World var1, int var2, int var3, int var4, Entity var5) {
   }

   public int func_149748_c(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      return 0;
   }

   public void func_149683_g() {
   }

   public void func_149636_a(World var1, EntityPlayer var2, int var3, int var4, int var5, int var6) {
      var2.func_71064_a(StatList.field_75934_C[func_149682_b(this)], 1);
      var2.func_71020_j(0.025F);
      if (this.func_149700_E() && EnchantmentHelper.func_77502_d(var2)) {
         ItemStack var8 = this.func_149644_j(var6);
         if (var8 != null) {
            this.func_149642_a(var1, var3, var4, var5, var8);
         }
      } else {
         int var7 = EnchantmentHelper.func_77517_e(var2);
         this.func_149697_b(var1, var3, var4, var5, var6, var7);
      }
   }

   protected boolean func_149700_E() {
      return this.func_149686_d() && !this.field_149758_A;
   }

   protected ItemStack func_149644_j(int var1) {
      int var2 = 0;
      Item var3 = Item.func_150898_a(this);
      if (var3 != null && var3.func_77614_k()) {
         var2 = var1;
      }

      return new ItemStack(var3, 1, var2);
   }

   public int func_149679_a(int var1, Random var2) {
      return this.func_149745_a(var2);
   }

   public boolean func_149718_j(World var1, int var2, int var3, int var4) {
      return true;
   }

   public void func_149689_a(World var1, int var2, int var3, int var4, EntityLivingBase var5, ItemStack var6) {
   }

   public void func_149714_e(World var1, int var2, int var3, int var4, int var5) {
   }

   public Block func_149663_c(String var1) {
      this.field_149770_b = var1;
      return this;
   }

   public String func_149732_F() {
      return StatCollector.func_74838_a(this.func_149739_a() + ".name");
   }

   public String func_149739_a() {
      return "tile." + this.field_149770_b;
   }

   public boolean func_149696_a(World var1, int var2, int var3, int var4, int var5, int var6) {
      return false;
   }

   public boolean func_149652_G() {
      return this.field_149790_y;
   }

   protected Block func_149649_H() {
      this.field_149790_y = false;
      return this;
   }

   public int func_149656_h() {
      return this.field_149764_J.func_76227_m();
   }

   public float func_149685_I() {
      return this.func_149637_q() ? 0.2F : 1.0F;
   }

   public void func_149746_a(World var1, int var2, int var3, int var4, Entity var5, float var6) {
   }

   public Item func_149694_d(World var1, int var2, int var3, int var4) {
      return Item.func_150898_a(this);
   }

   public int func_149643_k(World var1, int var2, int var3, int var4) {
      return this.func_149692_a(var1.func_72805_g(var2, var3, var4));
   }

   public void func_149666_a(Item var1, CreativeTabs var2, List var3) {
      var3.add(new ItemStack(var1, 1, 0));
   }

   public CreativeTabs func_149708_J() {
      return this.field_149772_a;
   }

   public Block func_149647_a(CreativeTabs var1) {
      this.field_149772_a = var1;
      return this;
   }

   public void func_149681_a(World var1, int var2, int var3, int var4, int var5, EntityPlayer var6) {
   }

   public void func_149725_f(World var1, int var2, int var3, int var4, int var5) {
   }

   public void func_149639_l(World var1, int var2, int var3, int var4) {
   }

   public boolean func_149648_K() {
      return false;
   }

   public boolean func_149698_L() {
      return true;
   }

   public boolean func_149659_a(Explosion var1) {
      return true;
   }

   public boolean func_149667_c(Block var1) {
      return this == var1;
   }

   public static boolean func_149680_a(Block var0, Block var1) {
      if (var0 == null || var1 == null) {
         return false;
      } else {
         return var0 == var1 ? true : var0.func_149667_c(var1);
      }
   }

   public boolean func_149740_M() {
      return false;
   }

   public int func_149736_g(World var1, int var2, int var3, int var4, int var5) {
      return 0;
   }

   protected Block func_149658_d(String var1) {
      this.field_149768_d = var1;
      return this;
   }

   protected String func_149641_N() {
      return this.field_149768_d == null ? "MISSING_ICON_BLOCK_" + func_149682_b(this) + "_" + this.field_149770_b : this.field_149768_d;
   }

   public IIcon func_149735_b(int var1, int var2) {
      return this.func_149691_a(var1, var2);
   }

   public void func_149651_a(IIconRegister var1) {
      this.field_149761_L = var1.func_94245_a(this.func_149641_N());
   }

   public String func_149702_O() {
      return null;
   }
}
