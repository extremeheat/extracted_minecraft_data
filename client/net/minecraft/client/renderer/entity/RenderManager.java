package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelChicken;
import net.minecraft.client.model.ModelCow;
import net.minecraft.client.model.ModelHorse;
import net.minecraft.client.model.ModelOcelot;
import net.minecraft.client.model.ModelPig;
import net.minecraft.client.model.ModelRabbit;
import net.minecraft.client.model.ModelSheep2;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.model.ModelSquid;
import net.minecraft.client.model.ModelWolf;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.RenderEnderCrystal;
import net.minecraft.client.renderer.tileentity.RenderItemFrame;
import net.minecraft.client.renderer.tileentity.RenderWitherSkull;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
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
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class RenderManager {
   private Map<Class<? extends Entity>, Render<? extends Entity>> field_78729_o = Maps.newHashMap();
   private Map<String, RenderPlayer> field_178636_l = Maps.newHashMap();
   private RenderPlayer field_178637_m;
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
   private boolean field_178639_r = false;
   private boolean field_178638_s = true;
   private boolean field_85095_o = false;

   public RenderManager(TextureManager var1, RenderItem var2) {
      super();
      this.field_78724_e = var1;
      this.field_78729_o.put(EntityCaveSpider.class, new RenderCaveSpider(this));
      this.field_78729_o.put(EntitySpider.class, new RenderSpider(this));
      this.field_78729_o.put(EntityPig.class, new RenderPig(this, new ModelPig(), 0.7F));
      this.field_78729_o.put(EntitySheep.class, new RenderSheep(this, new ModelSheep2(), 0.7F));
      this.field_78729_o.put(EntityCow.class, new RenderCow(this, new ModelCow(), 0.7F));
      this.field_78729_o.put(EntityMooshroom.class, new RenderMooshroom(this, new ModelCow(), 0.7F));
      this.field_78729_o.put(EntityWolf.class, new RenderWolf(this, new ModelWolf(), 0.5F));
      this.field_78729_o.put(EntityChicken.class, new RenderChicken(this, new ModelChicken(), 0.3F));
      this.field_78729_o.put(EntityOcelot.class, new RenderOcelot(this, new ModelOcelot(), 0.4F));
      this.field_78729_o.put(EntityRabbit.class, new RenderRabbit(this, new ModelRabbit(), 0.3F));
      this.field_78729_o.put(EntitySilverfish.class, new RenderSilverfish(this));
      this.field_78729_o.put(EntityEndermite.class, new RenderEndermite(this));
      this.field_78729_o.put(EntityCreeper.class, new RenderCreeper(this));
      this.field_78729_o.put(EntityEnderman.class, new RenderEnderman(this));
      this.field_78729_o.put(EntitySnowman.class, new RenderSnowMan(this));
      this.field_78729_o.put(EntitySkeleton.class, new RenderSkeleton(this));
      this.field_78729_o.put(EntityWitch.class, new RenderWitch(this));
      this.field_78729_o.put(EntityBlaze.class, new RenderBlaze(this));
      this.field_78729_o.put(EntityPigZombie.class, new RenderPigZombie(this));
      this.field_78729_o.put(EntityZombie.class, new RenderZombie(this));
      this.field_78729_o.put(EntitySlime.class, new RenderSlime(this, new ModelSlime(16), 0.25F));
      this.field_78729_o.put(EntityMagmaCube.class, new RenderMagmaCube(this));
      this.field_78729_o.put(EntityGiantZombie.class, new RenderGiantZombie(this, new ModelZombie(), 0.5F, 6.0F));
      this.field_78729_o.put(EntityGhast.class, new RenderGhast(this));
      this.field_78729_o.put(EntitySquid.class, new RenderSquid(this, new ModelSquid(), 0.7F));
      this.field_78729_o.put(EntityVillager.class, new RenderVillager(this));
      this.field_78729_o.put(EntityIronGolem.class, new RenderIronGolem(this));
      this.field_78729_o.put(EntityBat.class, new RenderBat(this));
      this.field_78729_o.put(EntityGuardian.class, new RenderGuardian(this));
      this.field_78729_o.put(EntityDragon.class, new RenderDragon(this));
      this.field_78729_o.put(EntityEnderCrystal.class, new RenderEnderCrystal(this));
      this.field_78729_o.put(EntityWither.class, new RenderWither(this));
      this.field_78729_o.put(Entity.class, new RenderEntity(this));
      this.field_78729_o.put(EntityPainting.class, new RenderPainting(this));
      this.field_78729_o.put(EntityItemFrame.class, new RenderItemFrame(this, var2));
      this.field_78729_o.put(EntityLeashKnot.class, new RenderLeashKnot(this));
      this.field_78729_o.put(EntityArrow.class, new RenderArrow(this));
      this.field_78729_o.put(EntitySnowball.class, new RenderSnowball(this, Items.field_151126_ay, var2));
      this.field_78729_o.put(EntityEnderPearl.class, new RenderSnowball(this, Items.field_151079_bi, var2));
      this.field_78729_o.put(EntityEnderEye.class, new RenderSnowball(this, Items.field_151061_bv, var2));
      this.field_78729_o.put(EntityEgg.class, new RenderSnowball(this, Items.field_151110_aK, var2));
      this.field_78729_o.put(EntityPotion.class, new RenderPotion(this, var2));
      this.field_78729_o.put(EntityExpBottle.class, new RenderSnowball(this, Items.field_151062_by, var2));
      this.field_78729_o.put(EntityFireworkRocket.class, new RenderSnowball(this, Items.field_151152_bP, var2));
      this.field_78729_o.put(EntityLargeFireball.class, new RenderFireball(this, 2.0F));
      this.field_78729_o.put(EntitySmallFireball.class, new RenderFireball(this, 0.5F));
      this.field_78729_o.put(EntityWitherSkull.class, new RenderWitherSkull(this));
      this.field_78729_o.put(EntityItem.class, new RenderEntityItem(this, var2));
      this.field_78729_o.put(EntityXPOrb.class, new RenderXPOrb(this));
      this.field_78729_o.put(EntityTNTPrimed.class, new RenderTNTPrimed(this));
      this.field_78729_o.put(EntityFallingBlock.class, new RenderFallingBlock(this));
      this.field_78729_o.put(EntityArmorStand.class, new ArmorStandRenderer(this));
      this.field_78729_o.put(EntityMinecartTNT.class, new RenderTntMinecart(this));
      this.field_78729_o.put(EntityMinecartMobSpawner.class, new RenderMinecartMobSpawner(this));
      this.field_78729_o.put(EntityMinecart.class, new RenderMinecart(this));
      this.field_78729_o.put(EntityBoat.class, new RenderBoat(this));
      this.field_78729_o.put(EntityFishHook.class, new RenderFish(this));
      this.field_78729_o.put(EntityHorse.class, new RenderHorse(this, new ModelHorse(), 0.75F));
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
         if (var8 == Blocks.field_150324_C) {
            int var9 = ((EnumFacing)var7.func_177229_b(BlockBed.field_176387_N)).func_176736_b();
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

   public boolean func_147937_a(Entity var1, float var2) {
      return this.func_147936_a(var1, var2, false);
   }

   public boolean func_178635_a(Entity var1, ICamera var2, double var3, double var5, double var7) {
      Render var9 = this.func_78713_a(var1);
      return var9 != null && var9.func_177071_a(var1, var2, var3, var5, var7);
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
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      return this.func_147939_a(var1, var4 - this.field_78725_b, var6 - this.field_78726_c, var8 - this.field_78723_d, var10, var2, var3);
   }

   public void func_178630_b(Entity var1, float var2) {
      double var3 = var1.field_70142_S + (var1.field_70165_t - var1.field_70142_S) * (double)var2;
      double var5 = var1.field_70137_T + (var1.field_70163_u - var1.field_70137_T) * (double)var2;
      double var7 = var1.field_70136_U + (var1.field_70161_v - var1.field_70136_U) * (double)var2;
      Render var9 = this.func_78713_a(var1);
      if (var9 != null && this.field_78724_e != null) {
         int var10 = var1.func_70070_b(var2);
         int var11 = var10 % 65536;
         int var12 = var10 / 65536;
         OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, (float)var11 / 1.0F, (float)var12 / 1.0F);
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         var9.func_177067_a(var1, var3 - this.field_78725_b, var5 - this.field_78726_c, var7 - this.field_78723_d);
      }

   }

   public boolean func_147940_a(Entity var1, double var2, double var4, double var6, float var8, float var9) {
      return this.func_147939_a(var1, var2, var4, var6, var8, var9, false);
   }

   public boolean func_147939_a(Entity var1, double var2, double var4, double var6, float var8, float var9, boolean var10) {
      Render var11 = null;

      try {
         var11 = this.func_78713_a(var1);
         if (var11 != null && this.field_78724_e != null) {
            try {
               if (var11 instanceof RendererLivingEntity) {
                  ((RendererLivingEntity)var11).func_177086_a(this.field_178639_r);
               }

               var11.func_76986_a(var1, var2, var4, var6, var8, var9);
            } catch (Throwable var18) {
               throw new ReportedException(CrashReport.func_85055_a(var18, "Rendering entity in world"));
            }

            try {
               if (!this.field_178639_r) {
                  var11.func_76979_b(var1, var2, var4, var6, var8, var9);
               }
            } catch (Throwable var17) {
               throw new ReportedException(CrashReport.func_85055_a(var17, "Post-rendering entity in world"));
            }

            if (this.field_85095_o && !var1.func_82150_aj() && !var10) {
               try {
                  this.func_85094_b(var1, var2, var4, var6, var8, var9);
               } catch (Throwable var16) {
                  throw new ReportedException(CrashReport.func_85055_a(var16, "Rendering entity hitbox in world"));
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
      GlStateManager.func_179132_a(false);
      GlStateManager.func_179090_x();
      GlStateManager.func_179140_f();
      GlStateManager.func_179129_p();
      GlStateManager.func_179084_k();
      float var10 = var1.field_70130_N / 2.0F;
      AxisAlignedBB var11 = var1.func_174813_aQ();
      AxisAlignedBB var12 = new AxisAlignedBB(var11.field_72340_a - var1.field_70165_t + var2, var11.field_72338_b - var1.field_70163_u + var4, var11.field_72339_c - var1.field_70161_v + var6, var11.field_72336_d - var1.field_70165_t + var2, var11.field_72337_e - var1.field_70163_u + var4, var11.field_72334_f - var1.field_70161_v + var6);
      RenderGlobal.func_181563_a(var12, 255, 255, 255, 255);
      if (var1 instanceof EntityLivingBase) {
         float var13 = 0.01F;
         RenderGlobal.func_181563_a(new AxisAlignedBB(var2 - (double)var10, var4 + (double)var1.func_70047_e() - 0.009999999776482582D, var6 - (double)var10, var2 + (double)var10, var4 + (double)var1.func_70047_e() + 0.009999999776482582D, var6 + (double)var10), 255, 0, 0, 255);
      }

      Tessellator var16 = Tessellator.func_178181_a();
      WorldRenderer var14 = var16.func_178180_c();
      Vec3 var15 = var1.func_70676_i(var9);
      var14.func_181668_a(3, DefaultVertexFormats.field_181706_f);
      var14.func_181662_b(var2, var4 + (double)var1.func_70047_e(), var6).func_181669_b(0, 0, 255, 255).func_181675_d();
      var14.func_181662_b(var2 + var15.field_72450_a * 2.0D, var4 + (double)var1.func_70047_e() + var15.field_72448_b * 2.0D, var6 + var15.field_72449_c * 2.0D).func_181669_b(0, 0, 255, 255).func_181675_d();
      var16.func_78381_a();
      GlStateManager.func_179098_w();
      GlStateManager.func_179145_e();
      GlStateManager.func_179089_o();
      GlStateManager.func_179084_k();
      GlStateManager.func_179132_a(true);
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

   public void func_178632_c(boolean var1) {
      this.field_178639_r = var1;
   }
}
