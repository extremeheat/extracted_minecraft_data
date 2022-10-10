package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLivingBase;
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
import net.minecraft.entity.item.EntityMinecartMobSpawner;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityDrowned;
import net.minecraft.entity.monster.EntityElderGuardian;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityEvoker;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntityHusk;
import net.minecraft.entity.monster.EntityIllusionIllager;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPhantom;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityStray;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCod;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityDolphin;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityMule;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityPufferFish;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySalmon;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySkeletonHorse;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityTropicalFish;
import net.minecraft.entity.passive.EntityTurtle;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.passive.EntityZombieHorse;
import net.minecraft.entity.projectile.EntityDragonFireball;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityEvokerFangs;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntityLlamaSpit;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntitySpectralArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.entity.projectile.EntityTrident;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Items;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class RenderManager {
   private final Map<Class<? extends Entity>, Render<? extends Entity>> field_78729_o = Maps.newHashMap();
   private final Map<String, RenderPlayer> field_178636_l = Maps.newHashMap();
   private final RenderPlayer field_178637_m;
   private FontRenderer field_78736_p;
   private double field_78725_b;
   private double field_78726_c;
   private double field_78723_d;
   public TextureManager field_78724_e;
   public World field_78722_g;
   public Entity field_78734_h;
   public Entity field_147941_i;
   public float field_78735_i;
   public float field_78732_j;
   public GameSettings field_78733_k;
   public double field_78730_l;
   public double field_78731_m;
   public double field_78728_n;
   private boolean field_178639_r;
   private boolean field_178638_s = true;
   private boolean field_85095_o;

   public RenderManager(TextureManager var1, ItemRenderer var2) {
      super();
      this.field_78724_e = var1;
      this.field_78729_o.put(EntityCaveSpider.class, new RenderCaveSpider(this));
      this.field_78729_o.put(EntitySpider.class, new RenderSpider(this));
      this.field_78729_o.put(EntityPig.class, new RenderPig(this));
      this.field_78729_o.put(EntitySheep.class, new RenderSheep(this));
      this.field_78729_o.put(EntityCow.class, new RenderCow(this));
      this.field_78729_o.put(EntityMooshroom.class, new RenderMooshroom(this));
      this.field_78729_o.put(EntityWolf.class, new RenderWolf(this));
      this.field_78729_o.put(EntityChicken.class, new RenderChicken(this));
      this.field_78729_o.put(EntityOcelot.class, new RenderOcelot(this));
      this.field_78729_o.put(EntityRabbit.class, new RenderRabbit(this));
      this.field_78729_o.put(EntityParrot.class, new RenderParrot(this));
      this.field_78729_o.put(EntityTurtle.class, new RenderTurtle(this));
      this.field_78729_o.put(EntitySilverfish.class, new RenderSilverfish(this));
      this.field_78729_o.put(EntityEndermite.class, new RenderEndermite(this));
      this.field_78729_o.put(EntityCreeper.class, new RenderCreeper(this));
      this.field_78729_o.put(EntityEnderman.class, new RenderEnderman(this));
      this.field_78729_o.put(EntitySnowman.class, new RenderSnowMan(this));
      this.field_78729_o.put(EntitySkeleton.class, new RenderSkeleton(this));
      this.field_78729_o.put(EntityWitherSkeleton.class, new RenderWitherSkeleton(this));
      this.field_78729_o.put(EntityStray.class, new RenderStray(this));
      this.field_78729_o.put(EntityWitch.class, new RenderWitch(this));
      this.field_78729_o.put(EntityBlaze.class, new RenderBlaze(this));
      this.field_78729_o.put(EntityPigZombie.class, new RenderPigZombie(this));
      this.field_78729_o.put(EntityZombie.class, new RenderZombie(this));
      this.field_78729_o.put(EntityZombieVillager.class, new RenderZombieVillager(this));
      this.field_78729_o.put(EntityHusk.class, new RenderHusk(this));
      this.field_78729_o.put(EntityDrowned.class, new RenderDrowned(this));
      this.field_78729_o.put(EntitySlime.class, new RenderSlime(this));
      this.field_78729_o.put(EntityMagmaCube.class, new RenderMagmaCube(this));
      this.field_78729_o.put(EntityGiantZombie.class, new RenderGiantZombie(this, 6.0F));
      this.field_78729_o.put(EntityGhast.class, new RenderGhast(this));
      this.field_78729_o.put(EntitySquid.class, new RenderSquid(this));
      this.field_78729_o.put(EntityVillager.class, new RenderVillager(this));
      this.field_78729_o.put(EntityIronGolem.class, new RenderIronGolem(this));
      this.field_78729_o.put(EntityBat.class, new RenderBat(this));
      this.field_78729_o.put(EntityGuardian.class, new RenderGuardian(this));
      this.field_78729_o.put(EntityElderGuardian.class, new RenderElderGuardian(this));
      this.field_78729_o.put(EntityShulker.class, new RenderShulker(this));
      this.field_78729_o.put(EntityPolarBear.class, new RenderPolarBear(this));
      this.field_78729_o.put(EntityEvoker.class, new RenderEvoker(this));
      this.field_78729_o.put(EntityVindicator.class, new RenderVindicator(this));
      this.field_78729_o.put(EntityVex.class, new RenderVex(this));
      this.field_78729_o.put(EntityIllusionIllager.class, new RenderIllusionIllager(this));
      this.field_78729_o.put(EntityPhantom.class, new RenderPhantom(this));
      this.field_78729_o.put(EntityPufferFish.class, new RenderPufferFish(this));
      this.field_78729_o.put(EntitySalmon.class, new RenderSalmon(this));
      this.field_78729_o.put(EntityCod.class, new RenderCod(this));
      this.field_78729_o.put(EntityTropicalFish.class, new RenderTropicalFish(this));
      this.field_78729_o.put(EntityDolphin.class, new RenderDolphin(this));
      this.field_78729_o.put(EntityDragon.class, new RenderDragon(this));
      this.field_78729_o.put(EntityEnderCrystal.class, new RenderEnderCrystal(this));
      this.field_78729_o.put(EntityWither.class, new RenderWither(this));
      this.field_78729_o.put(Entity.class, new RenderEntity(this));
      this.field_78729_o.put(EntityPainting.class, new RenderPainting(this));
      this.field_78729_o.put(EntityItemFrame.class, new RenderItemFrame(this, var2));
      this.field_78729_o.put(EntityLeashKnot.class, new RenderLeashKnot(this));
      this.field_78729_o.put(EntityTippedArrow.class, new RenderTippedArrow(this));
      this.field_78729_o.put(EntitySpectralArrow.class, new RenderSpectralArrow(this));
      this.field_78729_o.put(EntityTrident.class, new RenderTrident(this));
      this.field_78729_o.put(EntitySnowball.class, new RenderSprite(this, Items.field_151126_ay, var2));
      this.field_78729_o.put(EntityEnderPearl.class, new RenderSprite(this, Items.field_151079_bi, var2));
      this.field_78729_o.put(EntityEnderEye.class, new RenderSprite(this, Items.field_151061_bv, var2));
      this.field_78729_o.put(EntityEgg.class, new RenderSprite(this, Items.field_151110_aK, var2));
      this.field_78729_o.put(EntityPotion.class, new RenderPotion(this, var2));
      this.field_78729_o.put(EntityExpBottle.class, new RenderSprite(this, Items.field_151062_by, var2));
      this.field_78729_o.put(EntityFireworkRocket.class, new RenderSprite(this, Items.field_196152_dE, var2));
      this.field_78729_o.put(EntityLargeFireball.class, new RenderFireball(this, 2.0F));
      this.field_78729_o.put(EntitySmallFireball.class, new RenderFireball(this, 0.5F));
      this.field_78729_o.put(EntityDragonFireball.class, new RenderDragonFireball(this));
      this.field_78729_o.put(EntityWitherSkull.class, new RenderWitherSkull(this));
      this.field_78729_o.put(EntityShulkerBullet.class, new RenderShulkerBullet(this));
      this.field_78729_o.put(EntityItem.class, new RenderEntityItem(this, var2));
      this.field_78729_o.put(EntityXPOrb.class, new RenderXPOrb(this));
      this.field_78729_o.put(EntityTNTPrimed.class, new RenderTNTPrimed(this));
      this.field_78729_o.put(EntityFallingBlock.class, new RenderFallingBlock(this));
      this.field_78729_o.put(EntityArmorStand.class, new RenderArmorStand(this));
      this.field_78729_o.put(EntityEvokerFangs.class, new RenderEvokerFangs(this));
      this.field_78729_o.put(EntityMinecartTNT.class, new RenderTntMinecart(this));
      this.field_78729_o.put(EntityMinecartMobSpawner.class, new RenderMinecartMobSpawner(this));
      this.field_78729_o.put(EntityMinecart.class, new RenderMinecart(this));
      this.field_78729_o.put(EntityBoat.class, new RenderBoat(this));
      this.field_78729_o.put(EntityFishHook.class, new RenderFish(this));
      this.field_78729_o.put(EntityAreaEffectCloud.class, new RenderAreaEffectCloud(this));
      this.field_78729_o.put(EntityHorse.class, new RenderHorse(this));
      this.field_78729_o.put(EntitySkeletonHorse.class, new RenderHorseUndead(this));
      this.field_78729_o.put(EntityZombieHorse.class, new RenderHorseUndead(this));
      this.field_78729_o.put(EntityMule.class, new RenderHorseChest(this, 0.92F));
      this.field_78729_o.put(EntityDonkey.class, new RenderHorseChest(this, 0.87F));
      this.field_78729_o.put(EntityLlama.class, new RenderLlama(this));
      this.field_78729_o.put(EntityLlamaSpit.class, new RenderLlamaSpit(this));
      this.field_78729_o.put(EntityLightningBolt.class, new RenderLightningBolt(this));
      this.field_178637_m = new RenderPlayer(this);
      this.field_178636_l.put("default", this.field_178637_m);
      this.field_178636_l.put("slim", new RenderPlayer(this, true));
   }

   public void func_178628_a(double var1, double var3, double var5) {
      this.field_78725_b = var1;
      this.field_78726_c = var3;
      this.field_78723_d = var5;
   }

   public <T extends Entity> Render<T> func_78715_a(Class<? extends Entity> var1) {
      Render var2 = (Render)this.field_78729_o.get(var1);
      if (var2 == null && var1 != Entity.class) {
         var2 = this.func_78715_a(var1.getSuperclass());
         this.field_78729_o.put(var1, var2);
      }

      return var2;
   }

   @Nullable
   public <T extends Entity> Render<T> func_78713_a(Entity var1) {
      if (var1 instanceof AbstractClientPlayer) {
         String var2 = ((AbstractClientPlayer)var1).func_175154_l();
         RenderPlayer var3 = (RenderPlayer)this.field_178636_l.get(var2);
         return var3 != null ? var3 : this.field_178637_m;
      } else {
         return this.func_78715_a(var1.getClass());
      }
   }

   public void func_180597_a(World var1, FontRenderer var2, Entity var3, Entity var4, GameSettings var5, float var6) {
      this.field_78722_g = var1;
      this.field_78733_k = var5;
      this.field_78734_h = var3;
      this.field_147941_i = var4;
      this.field_78736_p = var2;
      if (var3 instanceof EntityLivingBase && ((EntityLivingBase)var3).func_70608_bn()) {
         IBlockState var7 = var1.func_180495_p(new BlockPos(var3));
         Block var8 = var7.func_177230_c();
         if (var8 instanceof BlockBed) {
            int var9 = ((EnumFacing)var7.func_177229_b(BlockBed.field_185512_D)).func_176736_b();
            this.field_78735_i = (float)(var9 * 90 + 180);
            this.field_78732_j = 0.0F;
         }
      } else {
         this.field_78735_i = var3.field_70126_B + (var3.field_70177_z - var3.field_70126_B) * var6;
         this.field_78732_j = var3.field_70127_C + (var3.field_70125_A - var3.field_70127_C) * var6;
      }

      if (var5.field_74320_O == 2) {
         this.field_78735_i += 180.0F;
      }

      this.field_78730_l = var3.field_70142_S + (var3.field_70165_t - var3.field_70142_S) * (double)var6;
      this.field_78731_m = var3.field_70137_T + (var3.field_70163_u - var3.field_70137_T) * (double)var6;
      this.field_78728_n = var3.field_70136_U + (var3.field_70161_v - var3.field_70136_U) * (double)var6;
   }

   public void func_178631_a(float var1) {
      this.field_78735_i = var1;
   }

   public boolean func_178627_a() {
      return this.field_178638_s;
   }

   public void func_178633_a(boolean var1) {
      this.field_178638_s = var1;
   }

   public void func_178629_b(boolean var1) {
      this.field_85095_o = var1;
   }

   public boolean func_178634_b() {
      return this.field_85095_o;
   }

   public boolean func_188390_b(Entity var1) {
      return this.func_78713_a(var1).func_188295_H_();
   }

   public boolean func_178635_a(Entity var1, ICamera var2, double var3, double var5, double var7) {
      Render var9 = this.func_78713_a(var1);
      return var9 != null && var9.func_177071_a(var1, var2, var3, var5, var7);
   }

   public void func_188388_a(Entity var1, float var2, boolean var3) {
      if (var1.field_70173_aa == 0) {
         var1.field_70142_S = var1.field_70165_t;
         var1.field_70137_T = var1.field_70163_u;
         var1.field_70136_U = var1.field_70161_v;
      }

      double var4 = var1.field_70142_S + (var1.field_70165_t - var1.field_70142_S) * (double)var2;
      double var6 = var1.field_70137_T + (var1.field_70163_u - var1.field_70137_T) * (double)var2;
      double var8 = var1.field_70136_U + (var1.field_70161_v - var1.field_70136_U) * (double)var2;
      float var10 = var1.field_70126_B + (var1.field_70177_z - var1.field_70126_B) * var2;
      int var11 = var1.func_70070_b();
      if (var1.func_70027_ad()) {
         var11 = 15728880;
      }

      int var12 = var11 % 65536;
      int var13 = var11 / 65536;
      OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, (float)var12, (float)var13);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      this.func_188391_a(var1, var4 - this.field_78725_b, var6 - this.field_78726_c, var8 - this.field_78723_d, var10, var2, var3);
   }

   public void func_188391_a(Entity var1, double var2, double var4, double var6, float var8, float var9, boolean var10) {
      Render var11 = null;

      try {
         var11 = this.func_78713_a(var1);
         if (var11 != null && this.field_78724_e != null) {
            try {
               var11.func_188297_a(this.field_178639_r);
               var11.func_76986_a(var1, var2, var4, var6, var8, var9);
            } catch (Throwable var17) {
               throw new ReportedException(CrashReport.func_85055_a(var17, "Rendering entity in world"));
            }

            try {
               if (!this.field_178639_r) {
                  var11.func_76979_b(var1, var2, var4, var6, var8, var9);
               }
            } catch (Throwable var18) {
               throw new ReportedException(CrashReport.func_85055_a(var18, "Post-rendering entity in world"));
            }

            if (this.field_85095_o && !var1.func_82150_aj() && !var10 && !Minecraft.func_71410_x().func_189648_am()) {
               try {
                  this.func_85094_b(var1, var2, var4, var6, var8, var9);
               } catch (Throwable var16) {
                  throw new ReportedException(CrashReport.func_85055_a(var16, "Rendering entity hitbox in world"));
               }
            }
         }

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

   public void func_188389_a(Entity var1, float var2) {
      if (var1.field_70173_aa == 0) {
         var1.field_70142_S = var1.field_70165_t;
         var1.field_70137_T = var1.field_70163_u;
         var1.field_70136_U = var1.field_70161_v;
      }

      double var3 = var1.field_70142_S + (var1.field_70165_t - var1.field_70142_S) * (double)var2;
      double var5 = var1.field_70137_T + (var1.field_70163_u - var1.field_70137_T) * (double)var2;
      double var7 = var1.field_70136_U + (var1.field_70161_v - var1.field_70136_U) * (double)var2;
      float var9 = var1.field_70126_B + (var1.field_70177_z - var1.field_70126_B) * var2;
      int var10 = var1.func_70070_b();
      if (var1.func_70027_ad()) {
         var10 = 15728880;
      }

      int var11 = var10 % 65536;
      int var12 = var10 / 65536;
      OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, (float)var11, (float)var12);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      Render var13 = this.func_78713_a(var1);
      if (var13 != null && this.field_78724_e != null) {
         var13.func_188300_b(var1, var3 - this.field_78725_b, var5 - this.field_78726_c, var7 - this.field_78723_d, var9, var2);
      }

   }

   private void func_85094_b(Entity var1, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.func_179132_a(false);
      GlStateManager.func_179090_x();
      GlStateManager.func_179140_f();
      GlStateManager.func_179129_p();
      GlStateManager.func_179084_k();
      float var10 = var1.field_70130_N / 2.0F;
      AxisAlignedBB var11 = var1.func_174813_aQ();
      WorldRenderer.func_189694_a(var11.field_72340_a - var1.field_70165_t + var2, var11.field_72338_b - var1.field_70163_u + var4, var11.field_72339_c - var1.field_70161_v + var6, var11.field_72336_d - var1.field_70165_t + var2, var11.field_72337_e - var1.field_70163_u + var4, var11.field_72334_f - var1.field_70161_v + var6, 1.0F, 1.0F, 1.0F, 1.0F);
      Entity[] var12 = var1.func_70021_al();
      if (var12 != null) {
         Entity[] var13 = var12;
         int var14 = var12.length;

         for(int var15 = 0; var15 < var14; ++var15) {
            Entity var16 = var13[var15];
            double var17 = (var16.field_70165_t - var16.field_70169_q) * (double)var9;
            double var19 = (var16.field_70163_u - var16.field_70167_r) * (double)var9;
            double var21 = (var16.field_70161_v - var16.field_70166_s) * (double)var9;
            AxisAlignedBB var23 = var16.func_174813_aQ();
            WorldRenderer.func_189694_a(var23.field_72340_a - this.field_78725_b + var17, var23.field_72338_b - this.field_78726_c + var19, var23.field_72339_c - this.field_78723_d + var21, var23.field_72336_d - this.field_78725_b + var17, var23.field_72337_e - this.field_78726_c + var19, var23.field_72334_f - this.field_78723_d + var21, 0.25F, 1.0F, 0.0F, 1.0F);
         }
      }

      if (var1 instanceof EntityLivingBase) {
         float var24 = 0.01F;
         WorldRenderer.func_189694_a(var2 - (double)var10, var4 + (double)var1.func_70047_e() - 0.009999999776482582D, var6 - (double)var10, var2 + (double)var10, var4 + (double)var1.func_70047_e() + 0.009999999776482582D, var6 + (double)var10, 1.0F, 0.0F, 0.0F, 1.0F);
      }

      Tessellator var25 = Tessellator.func_178181_a();
      BufferBuilder var26 = var25.func_178180_c();
      Vec3d var27 = var1.func_70676_i(var9);
      var26.func_181668_a(3, DefaultVertexFormats.field_181706_f);
      var26.func_181662_b(var2, var4 + (double)var1.func_70047_e(), var6).func_181669_b(0, 0, 255, 255).func_181675_d();
      var26.func_181662_b(var2 + var27.field_72450_a * 2.0D, var4 + (double)var1.func_70047_e() + var27.field_72448_b * 2.0D, var6 + var27.field_72449_c * 2.0D).func_181669_b(0, 0, 255, 255).func_181675_d();
      var25.func_78381_a();
      GlStateManager.func_179098_w();
      GlStateManager.func_179145_e();
      GlStateManager.func_179089_o();
      GlStateManager.func_179084_k();
      GlStateManager.func_179132_a(true);
   }

   public void func_78717_a(@Nullable World var1) {
      this.field_78722_g = var1;
      if (var1 == null) {
         this.field_78734_h = null;
      }

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

   public void func_178632_c(boolean var1) {
      this.field_178639_r = var1;
   }
}
