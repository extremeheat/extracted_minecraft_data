package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Culler;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.Direction;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Salmon;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.Donkey;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.animal.horse.Mule;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.animal.horse.TraderLlama;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.fishing.FishingHook;
import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Giant;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Illusioner;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.PigZombie;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.MinecartSpawner;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class EntityRenderDispatcher {
   private final Map<Class<? extends Entity>, EntityRenderer<? extends Entity>> renderers = Maps.newHashMap();
   private final Map<String, PlayerRenderer> playerRenderers = Maps.newHashMap();
   private final PlayerRenderer defaultPlayerRenderer;
   private Font font;
   private double xOff;
   private double yOff;
   private double zOff;
   public final TextureManager textureManager;
   public Level level;
   public Camera camera;
   public Entity crosshairPickEntity;
   public float playerRotY;
   public float playerRotX;
   public Options options;
   private boolean solidRender;
   private boolean shouldRenderShadow = true;
   private boolean renderHitBoxes;

   private <T extends Entity> void register(Class<T> var1, EntityRenderer<? super T> var2) {
      this.renderers.put(var1, var2);
   }

   public EntityRenderDispatcher(TextureManager var1, ItemRenderer var2, ReloadableResourceManager var3) {
      super();
      this.textureManager = var1;
      this.register(CaveSpider.class, new CaveSpiderRenderer(this));
      this.register(Spider.class, new SpiderRenderer(this));
      this.register(Pig.class, new PigRenderer(this));
      this.register(Sheep.class, new SheepRenderer(this));
      this.register(Cow.class, new CowRenderer(this));
      this.register(MushroomCow.class, new MushroomCowRenderer(this));
      this.register(Wolf.class, new WolfRenderer(this));
      this.register(Chicken.class, new ChickenRenderer(this));
      this.register(Ocelot.class, new OcelotRenderer(this));
      this.register(Rabbit.class, new RabbitRenderer(this));
      this.register(Parrot.class, new ParrotRenderer(this));
      this.register(Turtle.class, new TurtleRenderer(this));
      this.register(Silverfish.class, new SilverfishRenderer(this));
      this.register(Endermite.class, new EndermiteRenderer(this));
      this.register(Creeper.class, new CreeperRenderer(this));
      this.register(EnderMan.class, new EndermanRenderer(this));
      this.register(SnowGolem.class, new SnowGolemRenderer(this));
      this.register(Skeleton.class, new SkeletonRenderer(this));
      this.register(WitherSkeleton.class, new WitherSkeletonRenderer(this));
      this.register(Stray.class, new StrayRenderer(this));
      this.register(Witch.class, new WitchRenderer(this));
      this.register(Blaze.class, new BlazeRenderer(this));
      this.register(PigZombie.class, new PigZombieRenderer(this));
      this.register(Zombie.class, new ZombieRenderer(this));
      this.register(ZombieVillager.class, new ZombieVillagerRenderer(this, var3));
      this.register(Husk.class, new HuskRenderer(this));
      this.register(Drowned.class, new DrownedRenderer(this));
      this.register(Slime.class, new SlimeRenderer(this));
      this.register(MagmaCube.class, new LavaSlimeRenderer(this));
      this.register(Giant.class, new GiantMobRenderer(this, 6.0F));
      this.register(Ghast.class, new GhastRenderer(this));
      this.register(Squid.class, new SquidRenderer(this));
      this.register(Villager.class, new VillagerRenderer(this, var3));
      this.register(WanderingTrader.class, new WanderingTraderRenderer(this));
      this.register(IronGolem.class, new IronGolemRenderer(this));
      this.register(Bat.class, new BatRenderer(this));
      this.register(Guardian.class, new GuardianRenderer(this));
      this.register(ElderGuardian.class, new ElderGuardianRenderer(this));
      this.register(Shulker.class, new ShulkerRenderer(this));
      this.register(PolarBear.class, new PolarBearRenderer(this));
      this.register(Evoker.class, new EvokerRenderer(this));
      this.register(Vindicator.class, new VindicatorRenderer(this));
      this.register(Pillager.class, new PillagerRenderer(this));
      this.register(Ravager.class, new RavagerRenderer(this));
      this.register(Vex.class, new VexRenderer(this));
      this.register(Illusioner.class, new IllusionerRenderer(this));
      this.register(Phantom.class, new PhantomRenderer(this));
      this.register(Pufferfish.class, new PufferfishRenderer(this));
      this.register(Salmon.class, new SalmonRenderer(this));
      this.register(Cod.class, new CodRenderer(this));
      this.register(TropicalFish.class, new TropicalFishRenderer(this));
      this.register(Dolphin.class, new DolphinRenderer(this));
      this.register(Panda.class, new PandaRenderer(this));
      this.register(Cat.class, new CatRenderer(this));
      this.register(Fox.class, new FoxRenderer(this));
      this.register(EnderDragon.class, new EnderDragonRenderer(this));
      this.register(EndCrystal.class, new EndCrystalRenderer(this));
      this.register(WitherBoss.class, new WitherBossRenderer(this));
      this.register(Entity.class, new DefaultRenderer(this));
      this.register(Painting.class, new PaintingRenderer(this));
      this.register(ItemFrame.class, new ItemFrameRenderer(this, var2));
      this.register(LeashFenceKnotEntity.class, new LeashKnotRenderer(this));
      this.register(Arrow.class, new TippableArrowRenderer(this));
      this.register(SpectralArrow.class, new SpectralArrowRenderer(this));
      this.register(ThrownTrident.class, new ThrownTridentRenderer(this));
      this.register(Snowball.class, new ThrownItemRenderer(this, var2));
      this.register(ThrownEnderpearl.class, new ThrownItemRenderer(this, var2));
      this.register(EyeOfEnder.class, new ThrownItemRenderer(this, var2));
      this.register(ThrownEgg.class, new ThrownItemRenderer(this, var2));
      this.register(ThrownPotion.class, new ThrownItemRenderer(this, var2));
      this.register(ThrownExperienceBottle.class, new ThrownItemRenderer(this, var2));
      this.register(FireworkRocketEntity.class, new FireworkEntityRenderer(this, var2));
      this.register(LargeFireball.class, new ThrownItemRenderer(this, var2, 3.0F));
      this.register(SmallFireball.class, new ThrownItemRenderer(this, var2, 0.75F));
      this.register(DragonFireball.class, new DragonFireballRenderer(this));
      this.register(WitherSkull.class, new WitherSkullRenderer(this));
      this.register(ShulkerBullet.class, new ShulkerBulletRenderer(this));
      this.register(ItemEntity.class, new ItemEntityRenderer(this, var2));
      this.register(ExperienceOrb.class, new ExperienceOrbRenderer(this));
      this.register(PrimedTnt.class, new TntRenderer(this));
      this.register(FallingBlockEntity.class, new FallingBlockRenderer(this));
      this.register(ArmorStand.class, new ArmorStandRenderer(this));
      this.register(EvokerFangs.class, new EvokerFangsRenderer(this));
      this.register(MinecartTNT.class, new TntMinecartRenderer(this));
      this.register(MinecartSpawner.class, new MinecartRenderer(this));
      this.register(AbstractMinecart.class, new MinecartRenderer(this));
      this.register(Boat.class, new BoatRenderer(this));
      this.register(FishingHook.class, new FishingHookRenderer(this));
      this.register(AreaEffectCloud.class, new AreaEffectCloudRenderer(this));
      this.register(Horse.class, new HorseRenderer(this));
      this.register(SkeletonHorse.class, new UndeadHorseRenderer(this));
      this.register(ZombieHorse.class, new UndeadHorseRenderer(this));
      this.register(Mule.class, new ChestedHorseRenderer(this, 0.92F));
      this.register(Donkey.class, new ChestedHorseRenderer(this, 0.87F));
      this.register(Llama.class, new LlamaRenderer(this));
      this.register(TraderLlama.class, new LlamaRenderer(this));
      this.register(LlamaSpit.class, new LlamaSpitRenderer(this));
      this.register(LightningBolt.class, new LightningBoltRenderer(this));
      this.defaultPlayerRenderer = new PlayerRenderer(this);
      this.playerRenderers.put("default", this.defaultPlayerRenderer);
      this.playerRenderers.put("slim", new PlayerRenderer(this, true));
   }

   public void setPosition(double var1, double var3, double var5) {
      this.xOff = var1;
      this.yOff = var3;
      this.zOff = var5;
   }

   public <T extends Entity, U extends EntityRenderer<T>> U getRenderer(Class<? extends Entity> var1) {
      EntityRenderer var2 = (EntityRenderer)this.renderers.get(var1);
      if (var2 == null && var1 != Entity.class) {
         var2 = this.getRenderer(var1.getSuperclass());
         this.renderers.put(var1, var2);
      }

      return var2;
   }

   @Nullable
   public <T extends Entity, U extends EntityRenderer<T>> U getRenderer(T var1) {
      if (var1 instanceof AbstractClientPlayer) {
         String var2 = ((AbstractClientPlayer)var1).getModelName();
         PlayerRenderer var3 = (PlayerRenderer)this.playerRenderers.get(var2);
         return var3 != null ? var3 : this.defaultPlayerRenderer;
      } else {
         return this.getRenderer(var1.getClass());
      }
   }

   public void prepare(Level var1, Font var2, Camera var3, Entity var4, Options var5) {
      this.level = var1;
      this.options = var5;
      this.camera = var3;
      this.crosshairPickEntity = var4;
      this.font = var2;
      if (var3.getEntity() instanceof LivingEntity && ((LivingEntity)var3.getEntity()).isSleeping()) {
         Direction var6 = ((LivingEntity)var3.getEntity()).getBedOrientation();
         if (var6 != null) {
            this.playerRotY = var6.getOpposite().toYRot();
            this.playerRotX = 0.0F;
         }
      } else {
         this.playerRotY = var3.getYRot();
         this.playerRotX = var3.getXRot();
      }

   }

   public void setPlayerRotY(float var1) {
      this.playerRotY = var1;
   }

   public boolean shouldRenderShadow() {
      return this.shouldRenderShadow;
   }

   public void setRenderShadow(boolean var1) {
      this.shouldRenderShadow = var1;
   }

   public void setRenderHitBoxes(boolean var1) {
      this.renderHitBoxes = var1;
   }

   public boolean shouldRenderHitBoxes() {
      return this.renderHitBoxes;
   }

   public boolean hasSecondPass(Entity var1) {
      return this.getRenderer(var1).hasSecondPass();
   }

   public boolean shouldRender(Entity var1, Culler var2, double var3, double var5, double var7) {
      EntityRenderer var9 = this.getRenderer(var1);
      return var9 != null && var9.shouldRender(var1, var2, var3, var5, var7);
   }

   public void render(Entity var1, float var2, boolean var3) {
      if (var1.tickCount == 0) {
         var1.xOld = var1.x;
         var1.yOld = var1.y;
         var1.zOld = var1.z;
      }

      double var4 = Mth.lerp((double)var2, var1.xOld, var1.x);
      double var6 = Mth.lerp((double)var2, var1.yOld, var1.y);
      double var8 = Mth.lerp((double)var2, var1.zOld, var1.z);
      float var10 = Mth.lerp(var2, var1.yRotO, var1.yRot);
      int var11 = var1.getLightColor();
      if (var1.isOnFire()) {
         var11 = 15728880;
      }

      int var12 = var11 % 65536;
      int var13 = var11 / 65536;
      GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)var12, (float)var13);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.render(var1, var4 - this.xOff, var6 - this.yOff, var8 - this.zOff, var10, var2, var3);
   }

   public void render(Entity var1, double var2, double var4, double var6, float var8, float var9, boolean var10) {
      EntityRenderer var11 = null;

      try {
         var11 = this.getRenderer(var1);
         if (var11 != null && this.textureManager != null) {
            try {
               var11.setSolidRender(this.solidRender);
               var11.render(var1, var2, var4, var6, var8, var9);
            } catch (Throwable var17) {
               throw new ReportedException(CrashReport.forThrowable(var17, "Rendering entity in world"));
            }

            try {
               if (!this.solidRender) {
                  var11.postRender(var1, var2, var4, var6, var8, var9);
               }
            } catch (Throwable var18) {
               throw new ReportedException(CrashReport.forThrowable(var18, "Post-rendering entity in world"));
            }

            if (this.renderHitBoxes && !var1.isInvisible() && !var10 && !Minecraft.getInstance().showOnlyReducedInfo()) {
               try {
                  this.renderHitbox(var1, var2, var4, var6, var8, var9);
               } catch (Throwable var16) {
                  throw new ReportedException(CrashReport.forThrowable(var16, "Rendering entity hitbox in world"));
               }
            }
         }

      } catch (Throwable var19) {
         CrashReport var13 = CrashReport.forThrowable(var19, "Rendering entity in world");
         CrashReportCategory var14 = var13.addCategory("Entity being rendered");
         var1.fillCrashReportCategory(var14);
         CrashReportCategory var15 = var13.addCategory("Renderer details");
         var15.setDetail("Assigned renderer", (Object)var11);
         var15.setDetail("Location", (Object)CrashReportCategory.formatLocation(var2, var4, var6));
         var15.setDetail("Rotation", (Object)var8);
         var15.setDetail("Delta", (Object)var9);
         throw new ReportedException(var13);
      }
   }

   public void renderSecondPass(Entity var1, float var2) {
      if (var1.tickCount == 0) {
         var1.xOld = var1.x;
         var1.yOld = var1.y;
         var1.zOld = var1.z;
      }

      double var3 = Mth.lerp((double)var2, var1.xOld, var1.x);
      double var5 = Mth.lerp((double)var2, var1.yOld, var1.y);
      double var7 = Mth.lerp((double)var2, var1.zOld, var1.z);
      float var9 = Mth.lerp(var2, var1.yRotO, var1.yRot);
      int var10 = var1.getLightColor();
      if (var1.isOnFire()) {
         var10 = 15728880;
      }

      int var11 = var10 % 65536;
      int var12 = var10 / 65536;
      GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)var11, (float)var12);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      EntityRenderer var13 = this.getRenderer(var1);
      if (var13 != null && this.textureManager != null) {
         var13.renderSecondPass(var1, var3 - this.xOff, var5 - this.yOff, var7 - this.zOff, var9, var2);
      }

   }

   private void renderHitbox(Entity var1, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.depthMask(false);
      GlStateManager.disableTexture();
      GlStateManager.disableLighting();
      GlStateManager.disableCull();
      GlStateManager.disableBlend();
      float var10 = var1.getBbWidth() / 2.0F;
      AABB var11 = var1.getBoundingBox();
      LevelRenderer.renderLineBox(var11.minX - var1.x + var2, var11.minY - var1.y + var4, var11.minZ - var1.z + var6, var11.maxX - var1.x + var2, var11.maxY - var1.y + var4, var11.maxZ - var1.z + var6, 1.0F, 1.0F, 1.0F, 1.0F);
      if (var1 instanceof EnderDragon) {
         EnderDragonPart[] var12 = ((EnderDragon)var1).getSubEntities();
         int var13 = var12.length;

         for(int var14 = 0; var14 < var13; ++var14) {
            EnderDragonPart var15 = var12[var14];
            double var16 = (var15.x - var15.xo) * (double)var9;
            double var18 = (var15.y - var15.yo) * (double)var9;
            double var20 = (var15.z - var15.zo) * (double)var9;
            AABB var22 = var15.getBoundingBox();
            LevelRenderer.renderLineBox(var22.minX - this.xOff + var16, var22.minY - this.yOff + var18, var22.minZ - this.zOff + var20, var22.maxX - this.xOff + var16, var22.maxY - this.yOff + var18, var22.maxZ - this.zOff + var20, 0.25F, 1.0F, 0.0F, 1.0F);
         }
      }

      if (var1 instanceof LivingEntity) {
         float var23 = 0.01F;
         LevelRenderer.renderLineBox(var2 - (double)var10, var4 + (double)var1.getEyeHeight() - 0.009999999776482582D, var6 - (double)var10, var2 + (double)var10, var4 + (double)var1.getEyeHeight() + 0.009999999776482582D, var6 + (double)var10, 1.0F, 0.0F, 0.0F, 1.0F);
      }

      Tesselator var24 = Tesselator.getInstance();
      BufferBuilder var25 = var24.getBuilder();
      Vec3 var26 = var1.getViewVector(var9);
      var25.begin(3, DefaultVertexFormat.POSITION_COLOR);
      var25.vertex(var2, var4 + (double)var1.getEyeHeight(), var6).color(0, 0, 255, 255).endVertex();
      var25.vertex(var2 + var26.x * 2.0D, var4 + (double)var1.getEyeHeight() + var26.y * 2.0D, var6 + var26.z * 2.0D).color(0, 0, 255, 255).endVertex();
      var24.end();
      GlStateManager.enableTexture();
      GlStateManager.enableLighting();
      GlStateManager.enableCull();
      GlStateManager.disableBlend();
      GlStateManager.depthMask(true);
   }

   public void setLevel(@Nullable Level var1) {
      this.level = var1;
      if (var1 == null) {
         this.camera = null;
      }

   }

   public double distanceToSqr(double var1, double var3, double var5) {
      return this.camera.getPosition().distanceToSqr(var1, var3, var5);
   }

   public Font getFont() {
      return this.font;
   }

   public void setSolidRendering(boolean var1) {
      this.solidRender = var1;
   }
}
