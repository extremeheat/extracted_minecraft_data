package net.minecraft.entity;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.entity.ai.EntityMinecartMobSpawner;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
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
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGiantZombie;
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
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityList {
   private static final Logger field_151516_b = LogManager.getLogger();
   private static Map field_75625_b = new HashMap();
   private static Map field_75626_c = new HashMap();
   private static Map field_75623_d = new HashMap();
   private static Map field_75624_e = new HashMap();
   private static Map field_75622_f = new HashMap();
   public static HashMap field_75627_a = new LinkedHashMap();

   private static void func_75618_a(Class var0, String var1, int var2) {
      if (field_75625_b.containsKey(var1)) {
         throw new IllegalArgumentException("ID is already registered: " + var1);
      } else if (field_75623_d.containsKey(var2)) {
         throw new IllegalArgumentException("ID is already registered: " + var2);
      } else {
         field_75625_b.put(var1, var0);
         field_75626_c.put(var0, var1);
         field_75623_d.put(var2, var0);
         field_75624_e.put(var0, var2);
         field_75622_f.put(var1, var2);
      }
   }

   private static void func_75614_a(Class var0, String var1, int var2, int var3, int var4) {
      func_75618_a(var0, var1, var2);
      field_75627_a.put(var2, new EntityList$EntityEggInfo(var2, var3, var4));
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
         switch(var0.func_74762_e("Type")) {
            case 0:
               var0.func_74778_a("id", "MinecartRideable");
               break;
            case 1:
               var0.func_74778_a("id", "MinecartChest");
               break;
            case 2:
               var0.func_74778_a("id", "MinecartFurnace");
         }

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
      Class var1 = var0.getClass();
      return field_75624_e.containsKey(var1) ? field_75624_e.get(var1) : 0;
   }

   public static Class func_90035_a(int var0) {
      return (Class)field_75623_d.get(var0);
   }

   public static String func_75621_b(Entity var0) {
      return (String)field_75626_c.get(var0.getClass());
   }

   public static String func_75617_a(int var0) {
      Class var1 = func_90035_a(var0);
      return var1 != null ? (String)field_75626_c.get(var1) : null;
   }

   public static void func_151514_a() {
   }

   public static Set func_151515_b() {
      return Collections.unmodifiableSet(field_75622_f.keySet());
   }

   static {
      func_75618_a(EntityItem.class, "Item", 1);
      func_75618_a(EntityXPOrb.class, "XPOrb", 2);
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
      func_75618_a(EntityBoat.class, "Boat", 41);
      func_75618_a(EntityMinecartEmpty.class, "MinecartRideable", 42);
      func_75618_a(EntityMinecartChest.class, "MinecartChest", 43);
      func_75618_a(EntityMinecartFurnace.class, "MinecartFurnace", 44);
      func_75618_a(EntityMinecartTNT.class, "MinecartTNT", 45);
      func_75618_a(EntityMinecartHopper.class, "MinecartHopper", 46);
      func_75618_a(EntityMinecartMobSpawner.class, "MinecartSpawner", 47);
      func_75618_a(EntityMinecartCommandBlock.class, "MinecartCommandBlock", 40);
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
      func_75614_a(EntityVillager.class, "Villager", 120, 5651507, 12422002);
      func_75618_a(EntityEnderCrystal.class, "EnderCrystal", 200);
   }
}
