package net.minecraft.client.renderer.entity;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelChicken;
import net.minecraft.client.model.ModelCow;
import net.minecraft.client.model.ModelHorse;
import net.minecraft.client.model.ModelOcelot;
import net.minecraft.client.model.ModelPig;
import net.minecraft.client.model.ModelSheep1;
import net.minecraft.client.model.ModelSheep2;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.model.ModelSquid;
import net.minecraft.client.model.ModelWolf;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.RenderEnderCrystal;
import net.minecraft.client.renderer.tileentity.RenderItemFrame;
import net.minecraft.client.renderer.tileentity.RenderWitherSkull;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityMinecartMobSpawner;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.effect.EntityLightningBolt;
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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class RenderManager {
   private Map field_78729_o = new HashMap();
   public static RenderManager field_78727_a = new RenderManager();
   private FontRenderer field_78736_p;
   public static double field_78725_b;
   public static double field_78726_c;
   public static double field_78723_d;
   public TextureManager field_78724_e;
   public ItemRenderer field_78721_f;
   public World field_78722_g;
   public EntityLivingBase field_78734_h;
   public Entity field_147941_i;
   public float field_78735_i;
   public float field_78732_j;
   public GameSettings field_78733_k;
   public double field_78730_l;
   public double field_78731_m;
   public double field_78728_n;
   public static boolean field_85095_o;

   private RenderManager() {
      super();
      this.field_78729_o.put(EntityCaveSpider.class, new RenderCaveSpider());
      this.field_78729_o.put(EntitySpider.class, new RenderSpider());
      this.field_78729_o.put(EntityPig.class, new RenderPig(new ModelPig(), new ModelPig(0.5F), 0.7F));
      this.field_78729_o.put(EntitySheep.class, new RenderSheep(new ModelSheep2(), new ModelSheep1(), 0.7F));
      this.field_78729_o.put(EntityCow.class, new RenderCow(new ModelCow(), 0.7F));
      this.field_78729_o.put(EntityMooshroom.class, new RenderMooshroom(new ModelCow(), 0.7F));
      this.field_78729_o.put(EntityWolf.class, new RenderWolf(new ModelWolf(), new ModelWolf(), 0.5F));
      this.field_78729_o.put(EntityChicken.class, new RenderChicken(new ModelChicken(), 0.3F));
      this.field_78729_o.put(EntityOcelot.class, new RenderOcelot(new ModelOcelot(), 0.4F));
      this.field_78729_o.put(EntitySilverfish.class, new RenderSilverfish());
      this.field_78729_o.put(EntityCreeper.class, new RenderCreeper());
      this.field_78729_o.put(EntityEnderman.class, new RenderEnderman());
      this.field_78729_o.put(EntitySnowman.class, new RenderSnowMan());
      this.field_78729_o.put(EntitySkeleton.class, new RenderSkeleton());
      this.field_78729_o.put(EntityWitch.class, new RenderWitch());
      this.field_78729_o.put(EntityBlaze.class, new RenderBlaze());
      this.field_78729_o.put(EntityZombie.class, new RenderZombie());
      this.field_78729_o.put(EntitySlime.class, new RenderSlime(new ModelSlime(16), new ModelSlime(0), 0.25F));
      this.field_78729_o.put(EntityMagmaCube.class, new RenderMagmaCube());
      this.field_78729_o.put(EntityPlayer.class, new RenderPlayer());
      this.field_78729_o.put(EntityGiantZombie.class, new RenderGiantZombie(new ModelZombie(), 0.5F, 6.0F));
      this.field_78729_o.put(EntityGhast.class, new RenderGhast());
      this.field_78729_o.put(EntitySquid.class, new RenderSquid(new ModelSquid(), 0.7F));
      this.field_78729_o.put(EntityVillager.class, new RenderVillager());
      this.field_78729_o.put(EntityIronGolem.class, new RenderIronGolem());
      this.field_78729_o.put(EntityBat.class, new RenderBat());
      this.field_78729_o.put(EntityDragon.class, new RenderDragon());
      this.field_78729_o.put(EntityEnderCrystal.class, new RenderEnderCrystal());
      this.field_78729_o.put(EntityWither.class, new RenderWither());
      this.field_78729_o.put(Entity.class, new RenderEntity());
      this.field_78729_o.put(EntityPainting.class, new RenderPainting());
      this.field_78729_o.put(EntityItemFrame.class, new RenderItemFrame());
      this.field_78729_o.put(EntityLeashKnot.class, new RenderLeashKnot());
      this.field_78729_o.put(EntityArrow.class, new RenderArrow());
      this.field_78729_o.put(EntitySnowball.class, new RenderSnowball(Items.field_151126_ay));
      this.field_78729_o.put(EntityEnderPearl.class, new RenderSnowball(Items.field_151079_bi));
      this.field_78729_o.put(EntityEnderEye.class, new RenderSnowball(Items.field_151061_bv));
      this.field_78729_o.put(EntityEgg.class, new RenderSnowball(Items.field_151110_aK));
      this.field_78729_o.put(EntityPotion.class, new RenderSnowball(Items.field_151068_bn, 16384));
      this.field_78729_o.put(EntityExpBottle.class, new RenderSnowball(Items.field_151062_by));
      this.field_78729_o.put(EntityFireworkRocket.class, new RenderSnowball(Items.field_151152_bP));
      this.field_78729_o.put(EntityLargeFireball.class, new RenderFireball(2.0F));
      this.field_78729_o.put(EntitySmallFireball.class, new RenderFireball(0.5F));
      this.field_78729_o.put(EntityWitherSkull.class, new RenderWitherSkull());
      this.field_78729_o.put(EntityItem.class, new RenderItem());
      this.field_78729_o.put(EntityXPOrb.class, new RenderXPOrb());
      this.field_78729_o.put(EntityTNTPrimed.class, new RenderTNTPrimed());
      this.field_78729_o.put(EntityFallingBlock.class, new RenderFallingBlock());
      this.field_78729_o.put(EntityMinecartTNT.class, new RenderTntMinecart());
      this.field_78729_o.put(EntityMinecartMobSpawner.class, new RenderMinecartMobSpawner());
      this.field_78729_o.put(EntityMinecart.class, new RenderMinecart());
      this.field_78729_o.put(EntityBoat.class, new RenderBoat());
      this.field_78729_o.put(EntityFishHook.class, new RenderFish());
      this.field_78729_o.put(EntityHorse.class, new RenderHorse(new ModelHorse(), 0.75F));
      this.field_78729_o.put(EntityLightningBolt.class, new RenderLightningBolt());

      for(Render var2 : this.field_78729_o.values()) {
         var2.func_76976_a(this);
      }
   }

   public Render func_78715_a(Class var1) {
      Render var2 = (Render)this.field_78729_o.get(var1);
      if (var2 == null && var1 != Entity.class) {
         var2 = this.func_78715_a(var1.getSuperclass());
         this.field_78729_o.put(var1, var2);
      }

      return var2;
   }

   public Render func_78713_a(Entity var1) {
      return this.func_78715_a(var1.getClass());
   }

   public void func_147938_a(World var1, TextureManager var2, FontRenderer var3, EntityLivingBase var4, Entity var5, GameSettings var6, float var7) {
      this.field_78722_g = var1;
      this.field_78724_e = var2;
      this.field_78733_k = var6;
      this.field_78734_h = var4;
      this.field_147941_i = var5;
      this.field_78736_p = var3;
      if (var4.func_70608_bn()) {
         Block var8 = var1.func_147439_a(
            MathHelper.func_76128_c(var4.field_70165_t), MathHelper.func_76128_c(var4.field_70163_u), MathHelper.func_76128_c(var4.field_70161_v)
         );
         if (var8 == Blocks.field_150324_C) {
            int var9 = var1.func_72805_g(
               MathHelper.func_76128_c(var4.field_70165_t), MathHelper.func_76128_c(var4.field_70163_u), MathHelper.func_76128_c(var4.field_70161_v)
            );
            int var10 = var9 & 3;
            this.field_78735_i = (float)(var10 * 90 + 180);
            this.field_78732_j = 0.0F;
         }
      } else {
         this.field_78735_i = var4.field_70126_B + (var4.field_70177_z - var4.field_70126_B) * var7;
         this.field_78732_j = var4.field_70127_C + (var4.field_70125_A - var4.field_70127_C) * var7;
      }

      if (var6.field_74320_O == 2) {
         this.field_78735_i += 180.0F;
      }

      this.field_78730_l = var4.field_70142_S + (var4.field_70165_t - var4.field_70142_S) * (double)var7;
      this.field_78731_m = var4.field_70137_T + (var4.field_70163_u - var4.field_70137_T) * (double)var7;
      this.field_78728_n = var4.field_70136_U + (var4.field_70161_v - var4.field_70136_U) * (double)var7;
   }

   public boolean func_147937_a(Entity var1, float var2) {
      return this.func_147936_a(var1, var2, false);
   }

   public boolean func_147936_a(Entity var1, float var2, boolean var3) {
      if (var1.field_70173_aa == 0) {
         var1.field_70142_S = var1.field_70165_t;
         var1.field_70137_T = var1.field_70163_u;
         var1.field_70136_U = var1.field_70161_v;
      }

      double var4 = var1.field_70142_S + (var1.field_70165_t - var1.field_70142_S) * (double)var2;
      double var6 = var1.field_70137_T + (var1.field_70163_u - var1.field_70137_T) * (double)var2;
      double var8 = var1.field_70136_U + (var1.field_70161_v - var1.field_70136_U) * (double)var2;
      float var10 = var1.field_70126_B + (var1.field_70177_z - var1.field_70126_B) * var2;
      int var11 = var1.func_70070_b(var2);
      if (var1.func_70027_ad()) {
         var11 = 15728880;
      }

      int var12 = var11 % 65536;
      int var13 = var11 / 65536;
      OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, (float)var12 / 1.0F, (float)var13 / 1.0F);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      return this.func_147939_a(var1, var4 - field_78725_b, var6 - field_78726_c, var8 - field_78723_d, var10, var2, var3);
   }

   public boolean func_147940_a(Entity var1, double var2, double var4, double var6, float var8, float var9) {
      return this.func_147939_a(var1, var2, var4, var6, var8, var9, false);
   }

   public boolean func_147939_a(Entity var1, double var2, double var4, double var6, float var8, float var9, boolean var10) {
      Render var11 = null;

      try {
         var11 = this.func_78713_a(var1);
         if (var11 != null && this.field_78724_e != null) {
            if (!var11.func_147905_a() || var10) {
               try {
                  var11.func_76986_a(var1, var2, var4, var6, var8, var9);
               } catch (Throwable var18) {
                  throw new ReportedException(CrashReport.func_85055_a(var18, "Rendering entity in world"));
               }

               try {
                  var11.func_76979_b(var1, var2, var4, var6, var8, var9);
               } catch (Throwable var17) {
                  throw new ReportedException(CrashReport.func_85055_a(var17, "Post-rendering entity in world"));
               }

               if (field_85095_o && !var1.func_82150_aj() && !var10) {
                  try {
                     this.func_85094_b(var1, var2, var4, var6, var8, var9);
                  } catch (Throwable var16) {
                     throw new ReportedException(CrashReport.func_85055_a(var16, "Rendering entity hitbox in world"));
                  }
               }
            }
         } else if (this.field_78724_e != null) {
            return false;
         }

         return true;
      } catch (Throwable var19) {
         CrashReport var13 = CrashReport.func_85055_a(var19, "Rendering entity in world");
         CrashReportCategory var14 = var13.func_85058_a("Entity being rendered");
         var1.func_85029_a(var14);
         CrashReportCategory var15 = var13.func_85058_a("Renderer details");
         var15.func_71507_a("Assigned renderer", var11);
         var15.func_71507_a("Location", CrashReportCategory.func_85074_a(var2, var4, var6));
         var15.func_71507_a("Rotation", var8);
         var15.func_71507_a("Delta", var9);
         throw new ReportedException(var13);
      }
   }

   private void func_85094_b(Entity var1, double var2, double var4, double var6, float var8, float var9) {
      GL11.glDepthMask(false);
      GL11.glDisable(3553);
      GL11.glDisable(2896);
      GL11.glDisable(2884);
      GL11.glDisable(3042);
      float var10 = var1.field_70130_N / 2.0F;
      AxisAlignedBB var11 = AxisAlignedBB.func_72330_a(
         var2 - (double)var10, var4, var6 - (double)var10, var2 + (double)var10, var4 + (double)var1.field_70131_O, var6 + (double)var10
      );
      RenderGlobal.func_147590_a(var11, 16777215);
      GL11.glEnable(3553);
      GL11.glEnable(2896);
      GL11.glEnable(2884);
      GL11.glDisable(3042);
      GL11.glDepthMask(true);
   }

   public void func_78717_a(World var1) {
      this.field_78722_g = var1;
   }

   public double func_78714_a(double var1, double var3, double var5) {
      double var7 = var1 - this.field_78730_l;
      double var9 = var3 - this.field_78731_m;
      double var11 = var5 - this.field_78728_n;
      return var7 * var7 + var9 * var9 + var11 * var11;
   }

   public FontRenderer func_78716_a() {
      return this.field_78736_p;
   }

   public void func_94178_a(IIconRegister var1) {
      for(Render var3 : this.field_78729_o.values()) {
         var3.func_94143_a(var1);
      }
   }
}
