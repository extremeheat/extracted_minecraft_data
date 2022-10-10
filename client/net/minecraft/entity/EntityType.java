package net.minecraft.entity;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
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
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.entity.item.EntityMinecartCommandBlock;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.item.EntityMinecartFurnace;
import net.minecraft.entity.item.EntityMinecartHopper;
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
import net.minecraft.entity.player.EntityPlayer;
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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityType<T extends Entity> {
   private static final Logger field_200731_aJ = LogManager.getLogger();
   public static final EntityType<EntityAreaEffectCloud> field_200788_b = func_200712_a("area_effect_cloud", EntityType.Builder.func_201757_a(EntityAreaEffectCloud.class, EntityAreaEffectCloud::new));
   public static final EntityType<EntityArmorStand> field_200789_c = func_200712_a("armor_stand", EntityType.Builder.func_201757_a(EntityArmorStand.class, EntityArmorStand::new));
   public static final EntityType<EntityTippedArrow> field_200790_d = func_200712_a("arrow", EntityType.Builder.func_201757_a(EntityTippedArrow.class, EntityTippedArrow::new));
   public static final EntityType<EntityBat> field_200791_e = func_200712_a("bat", EntityType.Builder.func_201757_a(EntityBat.class, EntityBat::new));
   public static final EntityType<EntityBlaze> field_200792_f = func_200712_a("blaze", EntityType.Builder.func_201757_a(EntityBlaze.class, EntityBlaze::new));
   public static final EntityType<EntityBoat> field_200793_g = func_200712_a("boat", EntityType.Builder.func_201757_a(EntityBoat.class, EntityBoat::new));
   public static final EntityType<EntityCaveSpider> field_200794_h = func_200712_a("cave_spider", EntityType.Builder.func_201757_a(EntityCaveSpider.class, EntityCaveSpider::new));
   public static final EntityType<EntityChicken> field_200795_i = func_200712_a("chicken", EntityType.Builder.func_201757_a(EntityChicken.class, EntityChicken::new));
   public static final EntityType<EntityCod> field_203780_j = func_200712_a("cod", EntityType.Builder.func_201757_a(EntityCod.class, EntityCod::new));
   public static final EntityType<EntityCow> field_200796_j = func_200712_a("cow", EntityType.Builder.func_201757_a(EntityCow.class, EntityCow::new));
   public static final EntityType<EntityCreeper> field_200797_k = func_200712_a("creeper", EntityType.Builder.func_201757_a(EntityCreeper.class, EntityCreeper::new));
   public static final EntityType<EntityDonkey> field_200798_l = func_200712_a("donkey", EntityType.Builder.func_201757_a(EntityDonkey.class, EntityDonkey::new));
   public static final EntityType<EntityDolphin> field_205137_n = func_200712_a("dolphin", EntityType.Builder.func_201757_a(EntityDolphin.class, EntityDolphin::new));
   public static final EntityType<EntityDragonFireball> field_200799_m = func_200712_a("dragon_fireball", EntityType.Builder.func_201757_a(EntityDragonFireball.class, EntityDragonFireball::new));
   public static final EntityType<EntityDrowned> field_204724_o = func_200712_a("drowned", EntityType.Builder.func_201757_a(EntityDrowned.class, EntityDrowned::new));
   public static final EntityType<EntityElderGuardian> field_200800_n = func_200712_a("elder_guardian", EntityType.Builder.func_201757_a(EntityElderGuardian.class, EntityElderGuardian::new));
   public static final EntityType<EntityEnderCrystal> field_200801_o = func_200712_a("end_crystal", EntityType.Builder.func_201757_a(EntityEnderCrystal.class, EntityEnderCrystal::new));
   public static final EntityType<EntityDragon> field_200802_p = func_200712_a("ender_dragon", EntityType.Builder.func_201757_a(EntityDragon.class, EntityDragon::new));
   public static final EntityType<EntityEnderman> field_200803_q = func_200712_a("enderman", EntityType.Builder.func_201757_a(EntityEnderman.class, EntityEnderman::new));
   public static final EntityType<EntityEndermite> field_200804_r = func_200712_a("endermite", EntityType.Builder.func_201757_a(EntityEndermite.class, EntityEndermite::new));
   public static final EntityType<EntityEvokerFangs> field_200805_s = func_200712_a("evoker_fangs", EntityType.Builder.func_201757_a(EntityEvokerFangs.class, EntityEvokerFangs::new));
   public static final EntityType<EntityEvoker> field_200806_t = func_200712_a("evoker", EntityType.Builder.func_201757_a(EntityEvoker.class, EntityEvoker::new));
   public static final EntityType<EntityXPOrb> field_200807_u = func_200712_a("experience_orb", EntityType.Builder.func_201757_a(EntityXPOrb.class, EntityXPOrb::new));
   public static final EntityType<EntityEnderEye> field_200808_v = func_200712_a("eye_of_ender", EntityType.Builder.func_201757_a(EntityEnderEye.class, EntityEnderEye::new));
   public static final EntityType<EntityFallingBlock> field_200809_w = func_200712_a("falling_block", EntityType.Builder.func_201757_a(EntityFallingBlock.class, EntityFallingBlock::new));
   public static final EntityType<EntityFireworkRocket> field_200810_x = func_200712_a("firework_rocket", EntityType.Builder.func_201757_a(EntityFireworkRocket.class, EntityFireworkRocket::new));
   public static final EntityType<EntityGhast> field_200811_y = func_200712_a("ghast", EntityType.Builder.func_201757_a(EntityGhast.class, EntityGhast::new));
   public static final EntityType<EntityGiantZombie> field_200812_z = func_200712_a("giant", EntityType.Builder.func_201757_a(EntityGiantZombie.class, EntityGiantZombie::new));
   public static final EntityType<EntityGuardian> field_200761_A = func_200712_a("guardian", EntityType.Builder.func_201757_a(EntityGuardian.class, EntityGuardian::new));
   public static final EntityType<EntityHorse> field_200762_B = func_200712_a("horse", EntityType.Builder.func_201757_a(EntityHorse.class, EntityHorse::new));
   public static final EntityType<EntityHusk> field_200763_C = func_200712_a("husk", EntityType.Builder.func_201757_a(EntityHusk.class, EntityHusk::new));
   public static final EntityType<EntityIllusionIllager> field_200764_D = func_200712_a("illusioner", EntityType.Builder.func_201757_a(EntityIllusionIllager.class, EntityIllusionIllager::new));
   public static final EntityType<EntityItem> field_200765_E = func_200712_a("item", EntityType.Builder.func_201757_a(EntityItem.class, EntityItem::new));
   public static final EntityType<EntityItemFrame> field_200766_F = func_200712_a("item_frame", EntityType.Builder.func_201757_a(EntityItemFrame.class, EntityItemFrame::new));
   public static final EntityType<EntityLargeFireball> field_200767_G = func_200712_a("fireball", EntityType.Builder.func_201757_a(EntityLargeFireball.class, EntityLargeFireball::new));
   public static final EntityType<EntityLeashKnot> field_200768_H = func_200712_a("leash_knot", EntityType.Builder.func_201757_a(EntityLeashKnot.class, EntityLeashKnot::new).func_200706_c());
   public static final EntityType<EntityLlama> field_200769_I = func_200712_a("llama", EntityType.Builder.func_201757_a(EntityLlama.class, EntityLlama::new));
   public static final EntityType<EntityLlamaSpit> field_200770_J = func_200712_a("llama_spit", EntityType.Builder.func_201757_a(EntityLlamaSpit.class, EntityLlamaSpit::new));
   public static final EntityType<EntityMagmaCube> field_200771_K = func_200712_a("magma_cube", EntityType.Builder.func_201757_a(EntityMagmaCube.class, EntityMagmaCube::new));
   public static final EntityType<EntityMinecartEmpty> field_200772_L = func_200712_a("minecart", EntityType.Builder.func_201757_a(EntityMinecartEmpty.class, EntityMinecartEmpty::new));
   public static final EntityType<EntityMinecartChest> field_200773_M = func_200712_a("chest_minecart", EntityType.Builder.func_201757_a(EntityMinecartChest.class, EntityMinecartChest::new));
   public static final EntityType<EntityMinecartCommandBlock> field_200774_N = func_200712_a("command_block_minecart", EntityType.Builder.func_201757_a(EntityMinecartCommandBlock.class, EntityMinecartCommandBlock::new));
   public static final EntityType<EntityMinecartFurnace> field_200775_O = func_200712_a("furnace_minecart", EntityType.Builder.func_201757_a(EntityMinecartFurnace.class, EntityMinecartFurnace::new));
   public static final EntityType<EntityMinecartHopper> field_200776_P = func_200712_a("hopper_minecart", EntityType.Builder.func_201757_a(EntityMinecartHopper.class, EntityMinecartHopper::new));
   public static final EntityType<EntityMinecartMobSpawner> field_200777_Q = func_200712_a("spawner_minecart", EntityType.Builder.func_201757_a(EntityMinecartMobSpawner.class, EntityMinecartMobSpawner::new));
   public static final EntityType<EntityMinecartTNT> field_200778_R = func_200712_a("tnt_minecart", EntityType.Builder.func_201757_a(EntityMinecartTNT.class, EntityMinecartTNT::new));
   public static final EntityType<EntityMule> field_200779_S = func_200712_a("mule", EntityType.Builder.func_201757_a(EntityMule.class, EntityMule::new));
   public static final EntityType<EntityMooshroom> field_200780_T = func_200712_a("mooshroom", EntityType.Builder.func_201757_a(EntityMooshroom.class, EntityMooshroom::new));
   public static final EntityType<EntityOcelot> field_200781_U = func_200712_a("ocelot", EntityType.Builder.func_201757_a(EntityOcelot.class, EntityOcelot::new));
   public static final EntityType<EntityPainting> field_200782_V = func_200712_a("painting", EntityType.Builder.func_201757_a(EntityPainting.class, EntityPainting::new));
   public static final EntityType<EntityParrot> field_200783_W = func_200712_a("parrot", EntityType.Builder.func_201757_a(EntityParrot.class, EntityParrot::new));
   public static final EntityType<EntityPig> field_200784_X = func_200712_a("pig", EntityType.Builder.func_201757_a(EntityPig.class, EntityPig::new));
   public static final EntityType<EntityPufferFish> field_203779_Z = func_200712_a("pufferfish", EntityType.Builder.func_201757_a(EntityPufferFish.class, EntityPufferFish::new));
   public static final EntityType<EntityPigZombie> field_200785_Y = func_200712_a("zombie_pigman", EntityType.Builder.func_201757_a(EntityPigZombie.class, EntityPigZombie::new));
   public static final EntityType<EntityPolarBear> field_200786_Z = func_200712_a("polar_bear", EntityType.Builder.func_201757_a(EntityPolarBear.class, EntityPolarBear::new));
   public static final EntityType<EntityTNTPrimed> field_200735_aa = func_200712_a("tnt", EntityType.Builder.func_201757_a(EntityTNTPrimed.class, EntityTNTPrimed::new));
   public static final EntityType<EntityRabbit> field_200736_ab = func_200712_a("rabbit", EntityType.Builder.func_201757_a(EntityRabbit.class, EntityRabbit::new));
   public static final EntityType<EntitySalmon> field_203778_ae = func_200712_a("salmon", EntityType.Builder.func_201757_a(EntitySalmon.class, EntitySalmon::new));
   public static final EntityType<EntitySheep> field_200737_ac = func_200712_a("sheep", EntityType.Builder.func_201757_a(EntitySheep.class, EntitySheep::new));
   public static final EntityType<EntityShulker> field_200738_ad = func_200712_a("shulker", EntityType.Builder.func_201757_a(EntityShulker.class, EntityShulker::new));
   public static final EntityType<EntityShulkerBullet> field_200739_ae = func_200712_a("shulker_bullet", EntityType.Builder.func_201757_a(EntityShulkerBullet.class, EntityShulkerBullet::new));
   public static final EntityType<EntitySilverfish> field_200740_af = func_200712_a("silverfish", EntityType.Builder.func_201757_a(EntitySilverfish.class, EntitySilverfish::new));
   public static final EntityType<EntitySkeleton> field_200741_ag = func_200712_a("skeleton", EntityType.Builder.func_201757_a(EntitySkeleton.class, EntitySkeleton::new));
   public static final EntityType<EntitySkeletonHorse> field_200742_ah = func_200712_a("skeleton_horse", EntityType.Builder.func_201757_a(EntitySkeletonHorse.class, EntitySkeletonHorse::new));
   public static final EntityType<EntitySlime> field_200743_ai = func_200712_a("slime", EntityType.Builder.func_201757_a(EntitySlime.class, EntitySlime::new));
   public static final EntityType<EntitySmallFireball> field_200744_aj = func_200712_a("small_fireball", EntityType.Builder.func_201757_a(EntitySmallFireball.class, EntitySmallFireball::new));
   public static final EntityType<EntitySnowman> field_200745_ak = func_200712_a("snow_golem", EntityType.Builder.func_201757_a(EntitySnowman.class, EntitySnowman::new));
   public static final EntityType<EntitySnowball> field_200746_al = func_200712_a("snowball", EntityType.Builder.func_201757_a(EntitySnowball.class, EntitySnowball::new));
   public static final EntityType<EntitySpectralArrow> field_200747_am = func_200712_a("spectral_arrow", EntityType.Builder.func_201757_a(EntitySpectralArrow.class, EntitySpectralArrow::new));
   public static final EntityType<EntitySpider> field_200748_an = func_200712_a("spider", EntityType.Builder.func_201757_a(EntitySpider.class, EntitySpider::new));
   public static final EntityType<EntitySquid> field_200749_ao = func_200712_a("squid", EntityType.Builder.func_201757_a(EntitySquid.class, EntitySquid::new));
   public static final EntityType<EntityStray> field_200750_ap = func_200712_a("stray", EntityType.Builder.func_201757_a(EntityStray.class, EntityStray::new));
   public static final EntityType<EntityTropicalFish> field_204262_at = func_200712_a("tropical_fish", EntityType.Builder.func_201757_a(EntityTropicalFish.class, EntityTropicalFish::new));
   public static final EntityType<EntityTurtle> field_203099_aq = func_200712_a("turtle", EntityType.Builder.func_201757_a(EntityTurtle.class, EntityTurtle::new));
   public static final EntityType<EntityEgg> field_200751_aq = func_200712_a("egg", EntityType.Builder.func_201757_a(EntityEgg.class, EntityEgg::new));
   public static final EntityType<EntityEnderPearl> field_200752_ar = func_200712_a("ender_pearl", EntityType.Builder.func_201757_a(EntityEnderPearl.class, EntityEnderPearl::new));
   public static final EntityType<EntityExpBottle> field_200753_as = func_200712_a("experience_bottle", EntityType.Builder.func_201757_a(EntityExpBottle.class, EntityExpBottle::new));
   public static final EntityType<EntityPotion> field_200754_at = func_200712_a("potion", EntityType.Builder.func_201757_a(EntityPotion.class, EntityPotion::new));
   public static final EntityType<EntityVex> field_200755_au = func_200712_a("vex", EntityType.Builder.func_201757_a(EntityVex.class, EntityVex::new));
   public static final EntityType<EntityVillager> field_200756_av = func_200712_a("villager", EntityType.Builder.func_201757_a(EntityVillager.class, EntityVillager::new));
   public static final EntityType<EntityIronGolem> field_200757_aw = func_200712_a("iron_golem", EntityType.Builder.func_201757_a(EntityIronGolem.class, EntityIronGolem::new));
   public static final EntityType<EntityVindicator> field_200758_ax = func_200712_a("vindicator", EntityType.Builder.func_201757_a(EntityVindicator.class, EntityVindicator::new));
   public static final EntityType<EntityWitch> field_200759_ay = func_200712_a("witch", EntityType.Builder.func_201757_a(EntityWitch.class, EntityWitch::new));
   public static final EntityType<EntityWither> field_200760_az = func_200712_a("wither", EntityType.Builder.func_201757_a(EntityWither.class, EntityWither::new));
   public static final EntityType<EntityWitherSkeleton> field_200722_aA = func_200712_a("wither_skeleton", EntityType.Builder.func_201757_a(EntityWitherSkeleton.class, EntityWitherSkeleton::new));
   public static final EntityType<EntityWitherSkull> field_200723_aB = func_200712_a("wither_skull", EntityType.Builder.func_201757_a(EntityWitherSkull.class, EntityWitherSkull::new));
   public static final EntityType<EntityWolf> field_200724_aC = func_200712_a("wolf", EntityType.Builder.func_201757_a(EntityWolf.class, EntityWolf::new));
   public static final EntityType<EntityZombie> field_200725_aD = func_200712_a("zombie", EntityType.Builder.func_201757_a(EntityZombie.class, EntityZombie::new));
   public static final EntityType<EntityZombieHorse> field_200726_aE = func_200712_a("zombie_horse", EntityType.Builder.func_201757_a(EntityZombieHorse.class, EntityZombieHorse::new));
   public static final EntityType<EntityZombieVillager> field_200727_aF = func_200712_a("zombie_villager", EntityType.Builder.func_201757_a(EntityZombieVillager.class, EntityZombieVillager::new));
   public static final EntityType<EntityPhantom> field_203097_aH = func_200712_a("phantom", EntityType.Builder.func_201757_a(EntityPhantom.class, EntityPhantom::new));
   public static final EntityType<EntityLightningBolt> field_200728_aG = func_200712_a("lightning_bolt", EntityType.Builder.func_201758_a(EntityLightningBolt.class).func_200706_c());
   public static final EntityType<EntityPlayer> field_200729_aH = func_200712_a("player", EntityType.Builder.func_201758_a(EntityPlayer.class).func_200706_c().func_200705_b());
   public static final EntityType<EntityFishHook> field_200730_aI = func_200712_a("fishing_bobber", EntityType.Builder.func_201758_a(EntityFishHook.class).func_200706_c().func_200705_b());
   public static final EntityType<EntityTrident> field_203098_aL = func_200712_a("trident", EntityType.Builder.func_201757_a(EntityTrident.class, EntityTrident::new));
   private final Class<? extends T> field_201761_aK;
   private final Function<? super World, ? extends T> field_200732_aK;
   private final boolean field_200733_aL;
   private final boolean field_200734_aM;
   @Nullable
   private String field_210762_aX;
   @Nullable
   private ITextComponent field_212547_aX;
   @Nullable
   private final Type<?> field_206832_aX;

   public static <T extends Entity> EntityType<T> func_200712_a(String var0, EntityType.Builder<T> var1) {
      EntityType var2 = var1.func_206830_a(var0);
      IRegistry.field_212629_r.func_82595_a(new ResourceLocation(var0), var2);
      return var2;
   }

   @Nullable
   public static ResourceLocation func_200718_a(EntityType<?> var0) {
      return IRegistry.field_212629_r.func_177774_c(var0);
   }

   @Nullable
   public static EntityType<?> func_200713_a(String var0) {
      return (EntityType)IRegistry.field_212629_r.func_212608_b(ResourceLocation.func_208304_a(var0));
   }

   public EntityType(Class<? extends T> var1, Function<? super World, ? extends T> var2, boolean var3, boolean var4, @Nullable Type<?> var5) {
      super();
      this.field_201761_aK = var1;
      this.field_200732_aK = var2;
      this.field_200733_aL = var3;
      this.field_200734_aM = var4;
      this.field_206832_aX = var5;
   }

   @Nullable
   public Entity func_208049_a(World var1, @Nullable ItemStack var2, @Nullable EntityPlayer var3, BlockPos var4, boolean var5, boolean var6) {
      return this.func_208050_a(var1, var2 == null ? null : var2.func_77978_p(), var2 != null && var2.func_82837_s() ? var2.func_200301_q() : null, var3, var4, var5, var6);
   }

   @Nullable
   public T func_208050_a(World var1, @Nullable NBTTagCompound var2, @Nullable ITextComponent var3, @Nullable EntityPlayer var4, BlockPos var5, boolean var6, boolean var7) {
      Entity var8 = this.func_210761_b(var1, var2, var3, var4, var5, var6, var7);
      var1.func_72838_d(var8);
      return var8;
   }

   @Nullable
   public T func_210761_b(World var1, @Nullable NBTTagCompound var2, @Nullable ITextComponent var3, @Nullable EntityPlayer var4, BlockPos var5, boolean var6, boolean var7) {
      Entity var8 = this.func_200721_a(var1);
      if (var8 == null) {
         return null;
      } else {
         double var9;
         if (var6) {
            var8.func_70107_b((double)var5.func_177958_n() + 0.5D, (double)(var5.func_177956_o() + 1), (double)var5.func_177952_p() + 0.5D);
            var9 = func_208051_a(var1, var5, var7, var8.func_174813_aQ());
         } else {
            var9 = 0.0D;
         }

         var8.func_70012_b((double)var5.func_177958_n() + 0.5D, (double)var5.func_177956_o() + var9, (double)var5.func_177952_p() + 0.5D, MathHelper.func_76142_g(var1.field_73012_v.nextFloat() * 360.0F), 0.0F);
         if (var8 instanceof EntityLiving) {
            EntityLiving var11 = (EntityLiving)var8;
            var11.field_70759_as = var11.field_70177_z;
            var11.field_70761_aq = var11.field_70177_z;
            var11.func_204210_a(var1.func_175649_E(new BlockPos(var11)), (IEntityLivingData)null, var2);
            var11.func_70642_aH();
         }

         if (var3 != null && var8 instanceof EntityLivingBase) {
            var8.func_200203_b(var3);
         }

         func_208048_a(var1, var4, var8, var2);
         return var8;
      }
   }

   protected static double func_208051_a(IWorldReaderBase var0, BlockPos var1, boolean var2, AxisAlignedBB var3) {
      AxisAlignedBB var4 = new AxisAlignedBB(var1);
      if (var2) {
         var4 = var4.func_72321_a(0.0D, -1.0D, 0.0D);
      }

      Stream var5 = var0.func_212388_b((Entity)null, var4);
      return 1.0D + VoxelShapes.func_212437_a(EnumFacing.Axis.Y, var3, var5, var2 ? -2.0D : -1.0D);
   }

   public static void func_208048_a(World var0, @Nullable EntityPlayer var1, @Nullable Entity var2, @Nullable NBTTagCompound var3) {
      if (var3 != null && var3.func_150297_b("EntityTag", 10)) {
         MinecraftServer var4 = var0.func_73046_m();
         if (var4 != null && var2 != null) {
            if (var0.field_72995_K || !var2.func_184213_bq() || var1 != null && var4.func_184103_al().func_152596_g(var1.func_146103_bH())) {
               NBTTagCompound var5 = var2.func_189511_e(new NBTTagCompound());
               UUID var6 = var2.func_110124_au();
               var5.func_197643_a(var3.func_74775_l("EntityTag"));
               var2.func_184221_a(var6);
               var2.func_70020_e(var5);
            }
         }
      }
   }

   public boolean func_200715_a() {
      return this.field_200733_aL;
   }

   public boolean func_200720_b() {
      return this.field_200734_aM;
   }

   public Class<? extends T> func_201760_c() {
      return this.field_201761_aK;
   }

   public String func_210760_d() {
      if (this.field_210762_aX == null) {
         this.field_210762_aX = Util.func_200697_a("entity", IRegistry.field_212629_r.func_177774_c(this));
      }

      return this.field_210762_aX;
   }

   public ITextComponent func_212546_e() {
      if (this.field_212547_aX == null) {
         this.field_212547_aX = new TextComponentTranslation(this.func_210760_d(), new Object[0]);
      }

      return this.field_212547_aX;
   }

   @Nullable
   public T func_200721_a(World var1) {
      return (Entity)this.field_200732_aK.apply(var1);
   }

   @Nullable
   public static Entity func_200714_a(World var0, ResourceLocation var1) {
      return func_200719_a(var0, (EntityType)IRegistry.field_212629_r.func_212608_b(var1));
   }

   @Nullable
   public static Entity func_200717_a(int var0, World var1) {
      return func_200719_a(var1, (EntityType)IRegistry.field_212629_r.func_148754_a(var0));
   }

   @Nullable
   public static Entity func_200716_a(NBTTagCompound var0, World var1) {
      ResourceLocation var2 = new ResourceLocation(var0.func_74779_i("id"));
      Entity var3 = func_200714_a(var1, var2);
      if (var3 == null) {
         field_200731_aJ.warn("Skipping Entity with id {}", var2);
      } else {
         var3.func_70020_e(var0);
      }

      return var3;
   }

   @Nullable
   private static Entity func_200719_a(World var0, @Nullable EntityType<?> var1) {
      return var1 == null ? null : var1.func_200721_a(var0);
   }

   public static class Builder<T extends Entity> {
      private final Class<? extends T> field_201759_a;
      private final Function<? super World, ? extends T> field_200709_a;
      private boolean field_200710_b = true;
      private boolean field_200711_c = true;

      private Builder(Class<? extends T> var1, Function<? super World, ? extends T> var2) {
         super();
         this.field_201759_a = var1;
         this.field_200709_a = var2;
      }

      public static <T extends Entity> EntityType.Builder<T> func_201757_a(Class<? extends T> var0, Function<? super World, ? extends T> var1) {
         return new EntityType.Builder(var0, var1);
      }

      public static <T extends Entity> EntityType.Builder<T> func_201758_a(Class<? extends T> var0) {
         return new EntityType.Builder(var0, (var0x) -> {
            return null;
         });
      }

      public EntityType.Builder<T> func_200705_b() {
         this.field_200711_c = false;
         return this;
      }

      public EntityType.Builder<T> func_200706_c() {
         this.field_200710_b = false;
         return this;
      }

      public EntityType<T> func_206830_a(String var1) {
         Type var2 = null;
         if (this.field_200710_b) {
            try {
               var2 = DataFixesManager.func_210901_a().getSchema(DataFixUtils.makeKey(1631)).getChoiceType(TypeReferences.field_211298_n, var1);
            } catch (IllegalStateException var4) {
               if (SharedConstants.field_206244_b) {
                  throw var4;
               }

               EntityType.field_200731_aJ.warn("No data fixer registered for entity {}", var1);
            }
         }

         return new EntityType(this.field_201759_a, this.field_200709_a, this.field_200710_b, this.field_200711_c, var2);
      }
   }
}
