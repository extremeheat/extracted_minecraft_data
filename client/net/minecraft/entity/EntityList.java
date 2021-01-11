package net.minecraft.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.entity.ai.EntityMinecartMobSpawner;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.item.EntityMinecartFurnace;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityList {
   private static final Logger field_151516_b = LogManager.getLogger();
   private static final Map<String, Class<? extends Entity>> field_75625_b = Maps.newHashMap();
   private static final Map<Class<? extends Entity>, String> field_75626_c = Maps.newHashMap();
   private static final Map<Integer, Class<? extends Entity>> field_75623_d = Maps.newHashMap();
   private static final Map<Class<? extends Entity>, Integer> field_75624_e = Maps.newHashMap();
   private static final Map<String, Integer> field_180126_g = Maps.newHashMap();
   public static final Map<Integer, EntityList.EntityEggInfo> field_75627_a = Maps.newLinkedHashMap();

   private static void func_75618_a(Class<? extends Entity> var0, String var1, int var2) {
      if (field_75625_b.containsKey(var1)) {
         throw new IllegalArgumentException("ID is already registered: " + var1);
      } else if (field_75623_d.containsKey(var2)) {
         throw new IllegalArgumentException("ID is already registered: " + var2);
      } else if (var2 == 0) {
         throw new IllegalArgumentException("Cannot register to reserved id: " + var2);
      } else if (var0 == null) {
         throw new IllegalArgumentException("Cannot register null clazz for id: " + var2);
      } else {
         field_75625_b.put(var1, var0);
         field_75626_c.put(var0, var1);
         field_75623_d.put(var2, var0);
         field_75624_e.put(var0, var2);
         field_180126_g.put(var1, var2);
      }
   }

   private static void func_75614_a(Class<? extends Entity> var0, String var1, int var2, int var3, int var4) {
      func_75618_a(var0, var1, var2);
      field_75627_a.put(var2, new EntityList.EntityEggInfo(var2, var3, var4));
   }

   public static Entity func_75620_a(String var0, World var1) {
      Entity var2 = null;

      try {
         Class var3 = (Class)field_75625_b.get(var0);
         if (var3 != null) {
            var2 = (Entity)var3.getConstructor(World.class).newInstance(var1);
         }
      } catch (Exception var4) {
         var4.printStackTrace();
      }

      return var2;
   }

   public static Entity func_75615_a(NBTTagCompound var0, World var1) {
      Entity var2 = null;
      if ("Minecart".equals(var0.func_74779_i("id"))) {
         var0.func_74778_a("id", EntityMinecart.EnumMinecartType.func_180038_a(var0.func_74762_e("Type")).func_180040_b());
         var0.func_82580_o("Type");
      }

      try {
         Class var3 = (Class)field_75625_b.get(var0.func_74779_i("id"));
         if (var3 != null) {
            var2 = (Entity)var3.getConstructor(World.class).newInstance(var1);
         }
      } catch (Exception var4) {
         var4.printStackTrace();
      }

      if (var2 != null) {
         var2.func_70020_e(var0);
      } else {
         field_151516_b.warn("Skipping Entity with id " + var0.func_74779_i("id"));
      }

      return var2;
   }

   public static Entity func_75616_a(int var0, World var1) {
      Entity var2 = null;

      try {
         Class var3 = func_90035_a(var0);
         if (var3 != null) {
            var2 = (Entity)var3.getConstructor(World.class).newInstance(var1);
         }
      } catch (Exception var4) {
         var4.printStackTrace();
      }

      if (var2 == null) {
         field_151516_b.warn("Skipping Entity with id " + var0);
      }

      return var2;
   }

   public static int func_75619_a(Entity var0) {
      Integer var1 = (Integer)field_75624_e.get(var0.getClass());
      return var1 == null ? 0 : var1;
   }

   public static Class<? extends Entity> func_90035_a(int var0) {
      return (Class)field_75623_d.get(var0);
   }

   public static String func_75621_b(Entity var0) {
      return (String)field_75626_c.get(var0.getClass());
   }

   public static int func_180122_a(String var0) {
      Integer var1 = (Integer)field_180126_g.get(var0);
      return var1 == null ? 90 : var1;
   }

   public static String func_75617_a(int var0) {
      return (String)field_75626_c.get(func_90035_a(var0));
   }

   public static void func_151514_a() {
   }

   public static List<String> func_180124_b() {
      Set var0 = field_75625_b.keySet();
      ArrayList var1 = Lists.newArrayList();
      Iterator var2 = var0.iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         Class var4 = (Class)field_75625_b.get(var3);
         if ((var4.getModifiers() & 1024) != 1024) {
            var1.add(var3);
         }
      }

      var1.add("LightningBolt");
      return var1;
   }

   public static boolean func_180123_a(Entity var0, String var1) {
      String var2 = func_75621_b(var0);
      if (var2 == null && var0 instanceof EntityPlayer) {
         var2 = "Player";
      } else if (var2 == null && var0 instanceof EntityLightningBolt) {
         var2 = "LightningBolt";
      }

      return var1.equals(var2);
   }

   public static boolean func_180125_b(String var0) {
      return "Player".equals(var0) || func_180124_b().contains(var0);
   }

   static {
      func_75618_a(EntityItem.class, "Item", 1);
      func_75618_a(EntityXPOrb.class, "XPOrb", 2);
      func_75618_a(EntityEgg.class, "ThrownEgg", 7);
      func_75618_a(EntityLeashKnot.class, "LeashKnot", 8);
      func_75618_a(EntityPainting.class, "Painting", 9);
      func_75618_a(EntityArrow.class, "Arrow", 10);
      func_75618_a(EntitySnowball.class, "Snowball", 11);
      func_75618_a(EntityLargeFireball.class, "Fireball", 12);
      func_75618_a(EntitySmallFireball.class, "SmallFireball", 13);
      func_75618_a(EntityEnderPearl.class, "ThrownEnderpearl", 14);
      func_75618_a(EntityEnderEye.class, "EyeOfEnderSignal", 15);
      func_75618_a(EntityPotion.class, "ThrownPotion", 16);
      func_75618_a(EntityExpBottle.class, "ThrownExpBottle", 17);
      func_75618_a(EntityItemFrame.class, "ItemFrame", 18);
      func_75618_a(EntityWitherSkull.class, "WitherSkull", 19);
      func_75618_a(EntityTNTPrimed.class, "PrimedTnt", 20);
      func_75618_a(EntityFallingBlock.class, "FallingSand", 21);
      func_75618_a(EntityFireworkRocket.class, "FireworksRocketEntity", 22);
      func_75618_a(EntityArmorStand.class, "ArmorStand", 30);
      func_75618_a(EntityBoat.class, "Boat", 41);
      func_75618_a(EntityMinecartEmpty.class, EntityMinecart.EnumMinecartType.RIDEABLE.func_180040_b(), 42);
      func_75618_a(EntityMinecartChest.class, EntityMinecart.EnumMinecartType.CHEST.func_180040_b(), 43);
      func_75618_a(EntityMinecartFurnace.class, EntityMinecart.EnumMinecartType.FURNACE.func_180040_b(), 44);
      func_75618_a(EntityMinecartTNT.class, EntityMinecart.EnumMinecartType.TNT.func_180040_b(), 45);
      func_75618_a(EntityMinecartHopper.class, EntityMinecart.EnumMinecartType.HOPPER.func_180040_b(), 46);
      func_75618_a(EntityMinecartMobSpawner.class, EntityMinecart.EnumMinecartType.SPAWNER.func_180040_b(), 47);
      func_75618_a(EntityMinecartCommandBlock.class, EntityMinecart.EnumMinecartType.COMMAND_BLOCK.func_180040_b(), 40);
      func_75618_a(EntityLiving.class, "Mob", 48);
      func_75618_a(EntityMob.class, "Monster", 49);
      func_75614_a(EntityCreeper.class, "Creeper", 50, 894731, 0);
      func_75614_a(EntitySkeleton.class, "Skeleton", 51, 12698049, 4802889);
      func_75614_a(EntitySpider.class, "Spider", 52, 3419431, 11013646);
      func_75618_a(EntityGiantZombie.class, "Giant", 53);
      func_75614_a(EntityZombie.class, "Zombie", 54, 44975, 7969893);
      func_75614_a(EntitySlime.class, "Slime", 55, 5349438, 8306542);
      func_75614_a(EntityGhast.class, "Ghast", 56, 16382457, 12369084);
      func_75614_a(EntityPigZombie.class, "PigZombie", 57, 15373203, 5009705);
      func_75614_a(EntityEnderman.class, "Enderman", 58, 1447446, 0);
      func_75614_a(EntityCaveSpider.class, "CaveSpider", 59, 803406, 11013646);
      func_75614_a(EntitySilverfish.class, "Silverfish", 60, 7237230, 3158064);
      func_75614_a(EntityBlaze.class, "Blaze", 61, 16167425, 16775294);
      func_75614_a(EntityMagmaCube.class, "LavaSlime", 62, 3407872, 16579584);
      func_75618_a(EntityDragon.class, "EnderDragon", 63);
      func_75618_a(EntityWither.class, "WitherBoss", 64);
      func_75614_a(EntityBat.class, "Bat", 65, 4996656, 986895);
      func_75614_a(EntityWitch.class, "Witch", 66, 3407872, 5349438);
      func_75614_a(EntityEndermite.class, "Endermite", 67, 1447446, 7237230);
      func_75614_a(EntityGuardian.class, "Guardian", 68, 5931634, 15826224);
      func_75614_a(EntityPig.class, "Pig", 90, 15771042, 14377823);
      func_75614_a(EntitySheep.class, "Sheep", 91, 15198183, 16758197);
      func_75614_a(EntityCow.class, "Cow", 92, 4470310, 10592673);
      func_75614_a(EntityChicken.class, "Chicken", 93, 10592673, 16711680);
      func_75614_a(EntitySquid.class, "Squid", 94, 2243405, 7375001);
      func_75614_a(EntityWolf.class, "Wolf", 95, 14144467, 13545366);
      func_75614_a(EntityMooshroom.class, "MushroomCow", 96, 10489616, 12040119);
      func_75618_a(EntitySnowman.class, "SnowMan", 97);
      func_75614_a(EntityOcelot.class, "Ozelot", 98, 15720061, 5653556);
      func_75618_a(EntityIronGolem.class, "VillagerGolem", 99);
      func_75614_a(EntityHorse.class, "EntityHorse", 100, 12623485, 15656192);
      func_75614_a(EntityRabbit.class, "Rabbit", 101, 10051392, 7555121);
      func_75614_a(EntityVillager.class, "Villager", 120, 5651507, 12422002);
      func_75618_a(EntityEnderCrystal.class, "EnderCrystal", 200);
   }

   public static class EntityEggInfo {
      public final int field_75613_a;
      public final int field_75611_b;
      public final int field_75612_c;
      public final StatBase field_151512_d;
      public final StatBase field_151513_e;

      public EntityEggInfo(int var1, int var2, int var3) {
         super();
         this.field_75613_a = var1;
         this.field_75611_b = var2;
         this.field_75612_c = var3;
         this.field_151512_d = StatList.func_151182_a(this);
         this.field_151513_e = StatList.func_151176_b(this);
      }
   }
}
