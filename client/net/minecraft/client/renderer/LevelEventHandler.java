package net.minecraft.client.renderer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SculkChargeParticleOptions;
import net.minecraft.core.particles.ShriekParticleOption;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BrushableBlock;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.SculkShriekerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawner;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class LevelEventHandler {
   private final Minecraft minecraft;
   private final Level level;
   private final LevelRenderer levelRenderer;
   private final Map<BlockPos, SoundInstance> playingJukeboxSongs = new HashMap();

   public LevelEventHandler(Minecraft var1, Level var2, LevelRenderer var3) {
      super();
      this.minecraft = var1;
      this.level = var2;
      this.levelRenderer = var3;
   }

   public void globalLevelEvent(int var1, BlockPos var2, int var3) {
      switch (var1) {
         case 1023:
         case 1028:
         case 1038:
            Camera var4 = this.minecraft.gameRenderer.getMainCamera();
            if (var4.isInitialized()) {
               Vec3 var5 = Vec3.atCenterOf(var2).subtract(var4.getPosition()).normalize();
               Vec3 var6 = var4.getPosition().add(var5.scale(2.0));
               if (var1 == 1023) {
                  this.level.playLocalSound(var6.x, var6.y, var6.z, SoundEvents.WITHER_SPAWN, SoundSource.HOSTILE, 1.0F, 1.0F, false);
               } else if (var1 == 1038) {
                  this.level.playLocalSound(var6.x, var6.y, var6.z, SoundEvents.END_PORTAL_SPAWN, SoundSource.HOSTILE, 1.0F, 1.0F, false);
               } else {
                  this.level.playLocalSound(var6.x, var6.y, var6.z, SoundEvents.ENDER_DRAGON_DEATH, SoundSource.HOSTILE, 5.0F, 1.0F, false);
               }
            }
         default:
      }
   }

   public void levelEvent(int var1, BlockPos var2, int var3) {
      RandomSource var4 = this.level.random;
      int var7;
      double var8;
      double var10;
      double var12;
      double var17;
      float var39;
      int var45;
      float var46;
      double var49;
      double var55;
      double var57;
      switch (var1) {
         case 1000:
            this.level.playLocalSound(var2, SoundEvents.DISPENSER_DISPENSE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            break;
         case 1001:
            this.level.playLocalSound(var2, SoundEvents.DISPENSER_FAIL, SoundSource.BLOCKS, 1.0F, 1.2F, false);
            break;
         case 1002:
            this.level.playLocalSound(var2, SoundEvents.DISPENSER_LAUNCH, SoundSource.BLOCKS, 1.0F, 1.2F, false);
            break;
         case 1004:
            this.level.playLocalSound(var2, SoundEvents.FIREWORK_ROCKET_SHOOT, SoundSource.NEUTRAL, 1.0F, 1.2F, false);
            break;
         case 1009:
            if (var3 == 0) {
               this.level.playLocalSound(var2, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (var4.nextFloat() - var4.nextFloat()) * 0.8F, false);
            } else if (var3 == 1) {
               this.level.playLocalSound(var2, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 0.7F, 1.6F + (var4.nextFloat() - var4.nextFloat()) * 0.4F, false);
            }
            break;
         case 1010:
            this.level.registryAccess().lookupOrThrow(Registries.JUKEBOX_SONG).get(var3).ifPresent((var2x) -> {
               this.playJukeboxSong(var2x, var2);
            });
            break;
         case 1011:
            this.stopJukeboxSongAndNotifyNearby(var2);
            break;
         case 1015:
            this.level.playLocalSound(var2, SoundEvents.GHAST_WARN, SoundSource.HOSTILE, 10.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1016:
            this.level.playLocalSound(var2, SoundEvents.GHAST_SHOOT, SoundSource.HOSTILE, 10.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1017:
            this.level.playLocalSound(var2, SoundEvents.ENDER_DRAGON_SHOOT, SoundSource.HOSTILE, 10.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1018:
            this.level.playLocalSound(var2, SoundEvents.BLAZE_SHOOT, SoundSource.HOSTILE, 2.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1019:
            this.level.playLocalSound(var2, SoundEvents.ZOMBIE_ATTACK_WOODEN_DOOR, SoundSource.HOSTILE, 2.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1020:
            this.level.playLocalSound(var2, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, SoundSource.HOSTILE, 2.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1021:
            this.level.playLocalSound(var2, SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, SoundSource.HOSTILE, 2.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1022:
            this.level.playLocalSound(var2, SoundEvents.WITHER_BREAK_BLOCK, SoundSource.HOSTILE, 2.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1024:
            this.level.playLocalSound(var2, SoundEvents.WITHER_SHOOT, SoundSource.HOSTILE, 2.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1025:
            this.level.playLocalSound(var2, SoundEvents.BAT_TAKEOFF, SoundSource.NEUTRAL, 0.05F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1026:
            this.level.playLocalSound(var2, SoundEvents.ZOMBIE_INFECT, SoundSource.HOSTILE, 2.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1027:
            this.level.playLocalSound(var2, SoundEvents.ZOMBIE_VILLAGER_CONVERTED, SoundSource.HOSTILE, 2.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1029:
            this.level.playLocalSound(var2, SoundEvents.ANVIL_DESTROY, SoundSource.BLOCKS, 1.0F, var4.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1030:
            this.level.playLocalSound(var2, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1.0F, var4.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1031:
            this.level.playLocalSound(var2, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 0.3F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1032:
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forLocalAmbience(SoundEvents.PORTAL_TRAVEL, var4.nextFloat() * 0.4F + 0.8F, 0.25F));
            break;
         case 1033:
            this.level.playLocalSound(var2, SoundEvents.CHORUS_FLOWER_GROW, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            break;
         case 1034:
            this.level.playLocalSound(var2, SoundEvents.CHORUS_FLOWER_DEATH, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            break;
         case 1035:
            this.level.playLocalSound(var2, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            break;
         case 1039:
            this.level.playLocalSound(var2, SoundEvents.PHANTOM_BITE, SoundSource.HOSTILE, 0.3F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1040:
            this.level.playLocalSound(var2, SoundEvents.ZOMBIE_CONVERTED_TO_DROWNED, SoundSource.HOSTILE, 2.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1041:
            this.level.playLocalSound(var2, SoundEvents.HUSK_CONVERTED_TO_ZOMBIE, SoundSource.HOSTILE, 2.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1042:
            this.level.playLocalSound(var2, SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1043:
            this.level.playLocalSound(var2, SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1044:
            this.level.playLocalSound(var2, SoundEvents.SMITHING_TABLE_USE, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1045:
            this.level.playLocalSound(var2, SoundEvents.POINTED_DRIPSTONE_LAND, SoundSource.BLOCKS, 2.0F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1046:
            this.level.playLocalSound(var2, SoundEvents.POINTED_DRIPSTONE_DRIP_LAVA_INTO_CAULDRON, SoundSource.BLOCKS, 2.0F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1047:
            this.level.playLocalSound(var2, SoundEvents.POINTED_DRIPSTONE_DRIP_WATER_INTO_CAULDRON, SoundSource.BLOCKS, 2.0F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1048:
            this.level.playLocalSound(var2, SoundEvents.SKELETON_CONVERTED_TO_STRAY, SoundSource.HOSTILE, 2.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1049:
            this.level.playLocalSound(var2, SoundEvents.CRAFTER_CRAFT, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            break;
         case 1050:
            this.level.playLocalSound(var2, SoundEvents.CRAFTER_FAIL, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            break;
         case 1051:
            this.level.playLocalSound(var2, SoundEvents.WIND_CHARGE_THROW, SoundSource.BLOCKS, 0.5F, 0.4F / (this.level.getRandom().nextFloat() * 0.4F + 0.8F), false);
            break;
         case 1500:
            ComposterBlock.handleFill(this.level, var2, var3 > 0);
            break;
         case 1501:
            this.level.playLocalSound(var2, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (var4.nextFloat() - var4.nextFloat()) * 0.8F, false);

            for(var45 = 0; var45 < 8; ++var45) {
               this.level.addParticle(ParticleTypes.LARGE_SMOKE, (double)var2.getX() + var4.nextDouble(), (double)var2.getY() + 1.2, (double)var2.getZ() + var4.nextDouble(), 0.0, 0.0, 0.0);
            }

            return;
         case 1502:
            this.level.playLocalSound(var2, SoundEvents.REDSTONE_TORCH_BURNOUT, SoundSource.BLOCKS, 0.5F, 2.6F + (var4.nextFloat() - var4.nextFloat()) * 0.8F, false);

            for(var45 = 0; var45 < 5; ++var45) {
               var49 = (double)var2.getX() + var4.nextDouble() * 0.6 + 0.2;
               var55 = (double)var2.getY() + var4.nextDouble() * 0.6 + 0.2;
               var57 = (double)var2.getZ() + var4.nextDouble() * 0.6 + 0.2;
               this.level.addParticle(ParticleTypes.SMOKE, var49, var55, var57, 0.0, 0.0, 0.0);
            }

            return;
         case 1503:
            this.level.playLocalSound(var2, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, 1.0F, 1.0F, false);

            for(var45 = 0; var45 < 16; ++var45) {
               var49 = (double)var2.getX() + (5.0 + var4.nextDouble() * 6.0) / 16.0;
               var55 = (double)var2.getY() + 0.8125;
               var57 = (double)var2.getZ() + (5.0 + var4.nextDouble() * 6.0) / 16.0;
               this.level.addParticle(ParticleTypes.SMOKE, var49, var55, var57, 0.0, 0.0, 0.0);
            }

            return;
         case 1504:
            PointedDripstoneBlock.spawnDripParticle(this.level, var2, this.level.getBlockState(var2));
            break;
         case 1505:
            BoneMealItem.addGrowthParticles(this.level, var2, var3);
            this.level.playLocalSound(var2, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            break;
         case 2000:
            this.shootParticles(var3, var2, var4, ParticleTypes.SMOKE);
            break;
         case 2001:
            BlockState var24 = Block.stateById(var3);
            if (!var24.isAir()) {
               SoundType var27 = var24.getSoundType();
               this.level.playLocalSound(var2, var27.getBreakSound(), SoundSource.BLOCKS, (var27.getVolume() + 1.0F) / 2.0F, var27.getPitch() * 0.8F, false);
            }

            this.level.addDestroyBlockEffect(var2, var24);
            break;
         case 2002:
         case 2007:
            Vec3 var23 = Vec3.atBottomCenterOf(var2);

            for(int var25 = 0; var25 < 8; ++var25) {
               this.levelRenderer.addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.SPLASH_POTION)), var23.x, var23.y, var23.z, var4.nextGaussian() * 0.15, var4.nextDouble() * 0.2, var4.nextGaussian() * 0.15);
            }

            float var26 = (float)(var3 >> 16 & 255) / 255.0F;
            float var35 = (float)(var3 >> 8 & 255) / 255.0F;
            var39 = (float)(var3 >> 0 & 255) / 255.0F;
            SimpleParticleType var44 = var1 == 2007 ? ParticleTypes.INSTANT_EFFECT : ParticleTypes.EFFECT;

            for(var45 = 0; var45 < 100; ++var45) {
               var49 = var4.nextDouble() * 4.0;
               var55 = var4.nextDouble() * 3.141592653589793 * 2.0;
               var57 = Math.cos(var55) * var49;
               var17 = 0.01 + var4.nextDouble() * 0.5;
               double var59 = Math.sin(var55) * var49;
               Particle var21 = this.levelRenderer.addParticleInternal(var44, var44.getType().getOverrideLimiter(), var23.x + var57 * 0.1, var23.y + 0.3, var23.z + var59 * 0.1, var57, var17, var59);
               if (var21 != null) {
                  float var22 = 0.75F + var4.nextFloat() * 0.25F;
                  var21.setColor(var26 * var22, var35 * var22, var39 * var22);
                  var21.setPower((float)var49);
               }
            }

            this.level.playLocalSound(var2, SoundEvents.SPLASH_POTION_BREAK, SoundSource.NEUTRAL, 1.0F, var4.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 2003:
            double var5 = (double)var2.getX() + 0.5;
            double var33 = (double)var2.getY();
            double var40 = (double)var2.getZ() + 0.5;

            for(int var47 = 0; var47 < 8; ++var47) {
               this.levelRenderer.addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.ENDER_EYE)), var5, var33, var40, var4.nextGaussian() * 0.15, var4.nextDouble() * 0.2, var4.nextGaussian() * 0.15);
            }

            for(var49 = 0.0; var49 < 6.283185307179586; var49 += 0.15707963267948966) {
               this.levelRenderer.addParticle(ParticleTypes.PORTAL, var5 + Math.cos(var49) * 5.0, var33 - 0.4, var40 + Math.sin(var49) * 5.0, Math.cos(var49) * -5.0, 0.0, Math.sin(var49) * -5.0);
               this.levelRenderer.addParticle(ParticleTypes.PORTAL, var5 + Math.cos(var49) * 5.0, var33 - 0.4, var40 + Math.sin(var49) * 5.0, Math.cos(var49) * -7.0, 0.0, Math.sin(var49) * -7.0);
            }

            return;
         case 2004:
            for(var7 = 0; var7 < 20; ++var7) {
               var8 = (double)var2.getX() + 0.5 + (var4.nextDouble() - 0.5) * 2.0;
               var10 = (double)var2.getY() + 0.5 + (var4.nextDouble() - 0.5) * 2.0;
               var12 = (double)var2.getZ() + 0.5 + (var4.nextDouble() - 0.5) * 2.0;
               this.level.addParticle(ParticleTypes.SMOKE, var8, var10, var12, 0.0, 0.0, 0.0);
               this.level.addParticle(ParticleTypes.FLAME, var8, var10, var12, 0.0, 0.0, 0.0);
            }

            return;
         case 2006:
            for(var45 = 0; var45 < 200; ++var45) {
               var46 = var4.nextFloat() * 4.0F;
               float var53 = var4.nextFloat() * 6.2831855F;
               var55 = (double)(Mth.cos(var53) * var46);
               var57 = 0.01 + var4.nextDouble() * 0.5;
               var17 = (double)(Mth.sin(var53) * var46);
               Particle var19 = this.levelRenderer.addParticleInternal(ParticleTypes.DRAGON_BREATH, false, (double)var2.getX() + var55 * 0.1, (double)var2.getY() + 0.3, (double)var2.getZ() + var17 * 0.1, var55, var57, var17);
               if (var19 != null) {
                  var19.setPower(var46);
               }
            }

            if (var3 == 1) {
               this.level.playLocalSound(var2, SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.HOSTILE, 1.0F, var4.nextFloat() * 0.1F + 0.9F, false);
            }
            break;
         case 2008:
            this.level.addParticle(ParticleTypes.EXPLOSION, (double)var2.getX() + 0.5, (double)var2.getY() + 0.5, (double)var2.getZ() + 0.5, 0.0, 0.0, 0.0);
            break;
         case 2009:
            for(var45 = 0; var45 < 8; ++var45) {
               this.level.addParticle(ParticleTypes.CLOUD, (double)var2.getX() + var4.nextDouble(), (double)var2.getY() + 1.2, (double)var2.getZ() + var4.nextDouble(), 0.0, 0.0, 0.0);
            }

            return;
         case 2010:
            this.shootParticles(var3, var2, var4, ParticleTypes.WHITE_SMOKE);
            break;
         case 2011:
            ParticleUtils.spawnParticleInBlock(this.level, var2, var3, ParticleTypes.HAPPY_VILLAGER);
            break;
         case 2012:
            ParticleUtils.spawnParticleInBlock(this.level, var2, var3, ParticleTypes.HAPPY_VILLAGER);
            break;
         case 2013:
            ParticleUtils.spawnSmashAttackParticles(this.level, var2, var3);
            break;
         case 3000:
            this.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, true, true, (double)var2.getX() + 0.5, (double)var2.getY() + 0.5, (double)var2.getZ() + 0.5, 0.0, 0.0, 0.0);
            this.level.playLocalSound(var2, SoundEvents.END_GATEWAY_SPAWN, SoundSource.BLOCKS, 10.0F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F, false);
            break;
         case 3001:
            this.level.playLocalSound(var2, SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 64.0F, 0.8F + this.level.random.nextFloat() * 0.3F, false);
            break;
         case 3002:
            if (var3 >= 0 && var3 < Direction.Axis.VALUES.length) {
               ParticleUtils.spawnParticlesAlongAxis(Direction.Axis.VALUES[var3], this.level, var2, 0.125, ParticleTypes.ELECTRIC_SPARK, UniformInt.of(10, 19));
            } else {
               ParticleUtils.spawnParticlesOnBlockFaces(this.level, var2, ParticleTypes.ELECTRIC_SPARK, UniformInt.of(3, 5));
            }
            break;
         case 3003:
            ParticleUtils.spawnParticlesOnBlockFaces(this.level, var2, ParticleTypes.WAX_ON, UniformInt.of(3, 5));
            this.level.playLocalSound(var2, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            break;
         case 3004:
            ParticleUtils.spawnParticlesOnBlockFaces(this.level, var2, ParticleTypes.WAX_OFF, UniformInt.of(3, 5));
            break;
         case 3005:
            ParticleUtils.spawnParticlesOnBlockFaces(this.level, var2, ParticleTypes.SCRAPE, UniformInt.of(3, 5));
            break;
         case 3006:
            var7 = var3 >> 6;
            float var42;
            float var56;
            if (var7 > 0) {
               if (var4.nextFloat() < 0.3F + (float)var7 * 0.1F) {
                  var39 = 0.15F + 0.02F * (float)var7 * (float)var7 * var4.nextFloat();
                  float var34 = 0.4F + 0.3F * (float)var7 * var4.nextFloat();
                  this.level.playLocalSound(var2, SoundEvents.SCULK_BLOCK_CHARGE, SoundSource.BLOCKS, var39, var34, false);
               }

               byte var41 = (byte)(var3 & 63);
               UniformInt var37 = UniformInt.of(0, var7);
               var42 = 0.005F;
               Supplier var11 = () -> {
                  return new Vec3(Mth.nextDouble(var4, -0.004999999888241291, 0.004999999888241291), Mth.nextDouble(var4, -0.004999999888241291, 0.004999999888241291), Mth.nextDouble(var4, -0.004999999888241291, 0.004999999888241291));
               };
               if (var41 == 0) {
                  Direction[] var48 = Direction.values();
                  int var13 = var48.length;

                  for(int var14 = 0; var14 < var13; ++var14) {
                     Direction var15 = var48[var14];
                     float var16 = var15 == Direction.DOWN ? 3.1415927F : 0.0F;
                     var17 = var15.getAxis() == Direction.Axis.Y ? 0.65 : 0.57;
                     ParticleUtils.spawnParticlesOnBlockFace(this.level, var2, new SculkChargeParticleOptions(var16), var37, var15, var11, var17);
                  }

                  return;
               } else {
                  Iterator var50 = MultifaceBlock.unpack(var41).iterator();

                  while(var50.hasNext()) {
                     Direction var52 = (Direction)var50.next();
                     var56 = var52 == Direction.UP ? 3.1415927F : 0.0F;
                     var57 = 0.35;
                     ParticleUtils.spawnParticlesOnBlockFace(this.level, var2, new SculkChargeParticleOptions(var56), var37, var52, var11, 0.35);
                  }

                  return;
               }
            } else {
               this.level.playLocalSound(var2, SoundEvents.SCULK_BLOCK_CHARGE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
               boolean var43 = this.level.getBlockState(var2).isCollisionShapeFullBlock(this.level, var2);
               int var38 = var43 ? 40 : 20;
               var42 = var43 ? 0.45F : 0.25F;
               var46 = 0.07F;

               for(int var51 = 0; var51 < var38; ++var51) {
                  float var54 = 2.0F * var4.nextFloat() - 1.0F;
                  var56 = 2.0F * var4.nextFloat() - 1.0F;
                  float var58 = 2.0F * var4.nextFloat() - 1.0F;
                  this.level.addParticle(ParticleTypes.SCULK_CHARGE_POP, (double)var2.getX() + 0.5 + (double)(var54 * var42), (double)var2.getY() + 0.5 + (double)(var56 * var42), (double)var2.getZ() + 0.5 + (double)(var58 * var42), (double)(var54 * 0.07F), (double)(var56 * 0.07F), (double)(var58 * 0.07F));
               }

               return;
            }
         case 3007:
            for(int var32 = 0; var32 < 10; ++var32) {
               this.level.addParticle(new ShriekParticleOption(var32 * 5), (double)var2.getX() + 0.5, (double)var2.getY() + SculkShriekerBlock.TOP_Y, (double)var2.getZ() + 0.5, 0.0, 0.0, 0.0);
            }

            BlockState var36 = this.level.getBlockState(var2);
            boolean var9 = var36.hasProperty(BlockStateProperties.WATERLOGGED) && (Boolean)var36.getValue(BlockStateProperties.WATERLOGGED);
            if (!var9) {
               this.level.playLocalSound((double)var2.getX() + 0.5, (double)var2.getY() + SculkShriekerBlock.TOP_Y, (double)var2.getZ() + 0.5, SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.BLOCKS, 2.0F, 0.6F + this.level.random.nextFloat() * 0.4F, false);
            }
            break;
         case 3008:
            BlockState var6 = Block.stateById(var3);
            Block var31 = var6.getBlock();
            if (var31 instanceof BrushableBlock var29) {
               this.level.playLocalSound(var2, var29.getBrushCompletedSound(), SoundSource.PLAYERS, 1.0F, 1.0F, false);
            }

            this.level.addDestroyBlockEffect(var2, var6);
            break;
         case 3009:
            ParticleUtils.spawnParticlesOnBlockFaces(this.level, var2, ParticleTypes.EGG_CRACK, UniformInt.of(3, 6));
            break;
         case 3011:
            TrialSpawner.addSpawnParticles(this.level, var2, var4, TrialSpawner.FlameParticle.decode(var3).particleType);
            break;
         case 3012:
            this.level.playLocalSound(var2, SoundEvents.TRIAL_SPAWNER_SPAWN_MOB, SoundSource.BLOCKS, 1.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, true);
            TrialSpawner.addSpawnParticles(this.level, var2, var4, TrialSpawner.FlameParticle.decode(var3).particleType);
            break;
         case 3013:
            this.level.playLocalSound(var2, SoundEvents.TRIAL_SPAWNER_DETECT_PLAYER, SoundSource.BLOCKS, 1.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, true);
            TrialSpawner.addDetectPlayerParticles(this.level, var2, var4, var3, ParticleTypes.TRIAL_SPAWNER_DETECTED_PLAYER);
            break;
         case 3014:
            this.level.playLocalSound(var2, SoundEvents.TRIAL_SPAWNER_EJECT_ITEM, SoundSource.BLOCKS, 1.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, true);
            TrialSpawner.addEjectItemParticles(this.level, var2, var4);
            break;
         case 3015:
            BlockEntity var30 = this.level.getBlockEntity(var2);
            if (var30 instanceof VaultBlockEntity var28) {
               VaultBlockEntity.Client.emitActivationParticles(this.level, var28.getBlockPos(), var28.getBlockState(), var28.getSharedData(), var3 == 0 ? ParticleTypes.SMALL_FLAME : ParticleTypes.SOUL_FIRE_FLAME);
               this.level.playLocalSound(var2, SoundEvents.VAULT_ACTIVATE, SoundSource.BLOCKS, 1.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, true);
            }
            break;
         case 3016:
            VaultBlockEntity.Client.emitDeactivationParticles(this.level, var2, var3 == 0 ? ParticleTypes.SMALL_FLAME : ParticleTypes.SOUL_FIRE_FLAME);
            this.level.playLocalSound(var2, SoundEvents.VAULT_DEACTIVATE, SoundSource.BLOCKS, 1.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, true);
            break;
         case 3017:
            TrialSpawner.addEjectItemParticles(this.level, var2, var4);
            break;
         case 3018:
            for(var7 = 0; var7 < 10; ++var7) {
               var8 = var4.nextGaussian() * 0.02;
               var10 = var4.nextGaussian() * 0.02;
               var12 = var4.nextGaussian() * 0.02;
               this.level.addParticle(ParticleTypes.POOF, (double)var2.getX() + var4.nextDouble(), (double)var2.getY() + var4.nextDouble(), (double)var2.getZ() + var4.nextDouble(), var8, var10, var12);
            }

            this.level.playLocalSound(var2, SoundEvents.COBWEB_PLACE, SoundSource.BLOCKS, 1.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, true);
            break;
         case 3019:
            this.level.playLocalSound(var2, SoundEvents.TRIAL_SPAWNER_DETECT_PLAYER, SoundSource.BLOCKS, 1.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, true);
            TrialSpawner.addDetectPlayerParticles(this.level, var2, var4, var3, ParticleTypes.TRIAL_SPAWNER_DETECTED_PLAYER_OMINOUS);
            break;
         case 3020:
            this.level.playLocalSound(var2, SoundEvents.TRIAL_SPAWNER_OMINOUS_ACTIVATE, SoundSource.BLOCKS, var3 == 0 ? 0.3F : 1.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, true);
            TrialSpawner.addDetectPlayerParticles(this.level, var2, var4, 0, ParticleTypes.TRIAL_SPAWNER_DETECTED_PLAYER_OMINOUS);
            TrialSpawner.addBecomeOminousParticles(this.level, var2, var4);
            break;
         case 3021:
            this.level.playLocalSound(var2, SoundEvents.TRIAL_SPAWNER_SPAWN_ITEM, SoundSource.BLOCKS, 1.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, true);
            TrialSpawner.addSpawnParticles(this.level, var2, var4, TrialSpawner.FlameParticle.decode(var3).particleType);
      }

   }

   private void shootParticles(int var1, BlockPos var2, RandomSource var3, SimpleParticleType var4) {
      Direction var5 = Direction.from3DDataValue(var1);
      int var6 = var5.getStepX();
      int var7 = var5.getStepY();
      int var8 = var5.getStepZ();

      for(int var9 = 0; var9 < 10; ++var9) {
         double var10 = var3.nextDouble() * 0.2 + 0.01;
         double var12 = (double)var2.getX() + (double)var6 * 0.6 + 0.5 + (double)var6 * 0.01 + (var3.nextDouble() - 0.5) * (double)var8 * 0.5;
         double var14 = (double)var2.getY() + (double)var7 * 0.6 + 0.5 + (double)var7 * 0.01 + (var3.nextDouble() - 0.5) * (double)var7 * 0.5;
         double var16 = (double)var2.getZ() + (double)var8 * 0.6 + 0.5 + (double)var8 * 0.01 + (var3.nextDouble() - 0.5) * (double)var6 * 0.5;
         double var18 = (double)var6 * var10 + var3.nextGaussian() * 0.01;
         double var20 = (double)var7 * var10 + var3.nextGaussian() * 0.01;
         double var22 = (double)var8 * var10 + var3.nextGaussian() * 0.01;
         this.levelRenderer.addParticle(var4, var12, var14, var16, var18, var20, var22);
      }

   }

   private void playJukeboxSong(Holder<JukeboxSong> var1, BlockPos var2) {
      this.stopJukeboxSong(var2);
      JukeboxSong var3 = (JukeboxSong)var1.value();
      SoundEvent var4 = (SoundEvent)var3.soundEvent().value();
      SimpleSoundInstance var5 = SimpleSoundInstance.forJukeboxSong(var4, Vec3.atCenterOf(var2));
      this.playingJukeboxSongs.put(var2, var5);
      this.minecraft.getSoundManager().play(var5);
      this.minecraft.gui.setNowPlaying(var3.description());
      this.notifyNearbyEntities(this.level, var2, true);
   }

   private void stopJukeboxSong(BlockPos var1) {
      SoundInstance var2 = (SoundInstance)this.playingJukeboxSongs.remove(var1);
      if (var2 != null) {
         this.minecraft.getSoundManager().stop(var2);
      }

   }

   private void stopJukeboxSongAndNotifyNearby(BlockPos var1) {
      this.stopJukeboxSong(var1);
      this.notifyNearbyEntities(this.level, var1, false);
   }

   private void notifyNearbyEntities(Level var1, BlockPos var2, boolean var3) {
      List var4 = var1.getEntitiesOfClass(LivingEntity.class, (new AABB(var2)).inflate(3.0));
      Iterator var5 = var4.iterator();

      while(var5.hasNext()) {
         LivingEntity var6 = (LivingEntity)var5.next();
         var6.setRecordPlayingNearby(var2, var3);
      }

   }
}
