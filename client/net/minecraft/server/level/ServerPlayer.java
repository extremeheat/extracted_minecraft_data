package net.minecraft.server.level;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ClientboundChatPacket;
import net.minecraft.network.protocol.game.ClientboundContainerClosePacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetDataPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundHorseScreenOpenPacket;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundMerchantOffersPacket;
import net.minecraft.network.protocol.game.ClientboundOpenBookPacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerLookAtPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundResourcePackPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundSetCameraPacket;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.protocol.game.ServerboundClientInformationPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.HorseInventoryMenu;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ComplexItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ServerItemCooldowns;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Team;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPlayer extends Player implements ContainerListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private String language = "en_US";
   public ServerGamePacketListenerImpl connection;
   public final MinecraftServer server;
   public final ServerPlayerGameMode gameMode;
   private final List<Integer> entitiesToRemove = Lists.newLinkedList();
   private final PlayerAdvancements advancements;
   private final ServerStatsCounter stats;
   private float lastRecordedHealthAndAbsorption = 1.4E-45F;
   private int lastRecordedFoodLevel = -2147483648;
   private int lastRecordedAirLevel = -2147483648;
   private int lastRecordedArmor = -2147483648;
   private int lastRecordedLevel = -2147483648;
   private int lastRecordedExperience = -2147483648;
   private float lastSentHealth = -1.0E8F;
   private int lastSentFood = -99999999;
   private boolean lastFoodSaturationZero = true;
   private int lastSentExp = -99999999;
   private int spawnInvulnerableTime = 60;
   private ChatVisiblity chatVisibility;
   private boolean canChatColor = true;
   private long lastActionTime = Util.getMillis();
   private Entity camera;
   private boolean isChangingDimension;
   private boolean seenCredits;
   private final ServerRecipeBook recipeBook;
   private Vec3 levitationStartPos;
   private int levitationStartTime;
   private boolean disconnected;
   @Nullable
   private Vec3 enteredNetherPosition;
   private SectionPos lastSectionPos = SectionPos.of(0, 0, 0);
   private int containerCounter;
   public boolean ignoreSlotUpdateHack;
   public int latency;
   public boolean wonGame;

   public ServerPlayer(MinecraftServer var1, ServerLevel var2, GameProfile var3, ServerPlayerGameMode var4) {
      super(var2, var3);
      var4.player = this;
      this.gameMode = var4;
      this.server = var1;
      this.recipeBook = new ServerRecipeBook(var1.getRecipeManager());
      this.stats = var1.getPlayerList().getPlayerStats(this);
      this.advancements = var1.getPlayerList().getPlayerAdvancements(this);
      this.maxUpStep = 1.0F;
      this.fudgeSpawnLocation(var2);
   }

   private void fudgeSpawnLocation(ServerLevel var1) {
      BlockPos var2 = var1.getSharedSpawnPos();
      if (var1.dimension.isHasSkyLight() && var1.getLevelData().getGameType() != GameType.ADVENTURE) {
         int var3 = Math.max(0, this.server.getSpawnRadius(var1));
         int var4 = Mth.floor(var1.getWorldBorder().getDistanceToBorder((double)var2.getX(), (double)var2.getZ()));
         if (var4 < var3) {
            var3 = var4;
         }

         if (var4 <= 1) {
            var3 = 1;
         }

         long var5 = (long)(var3 * 2 + 1);
         long var7 = var5 * var5;
         int var9 = var7 > 2147483647L ? 2147483647 : (int)var7;
         int var10 = this.getCoprime(var9);
         int var11 = (new Random()).nextInt(var9);

         for(int var12 = 0; var12 < var9; ++var12) {
            int var13 = (var11 + var10 * var12) % var9;
            int var14 = var13 % (var3 * 2 + 1);
            int var15 = var13 / (var3 * 2 + 1);
            BlockPos var16 = var1.getDimension().getValidSpawnPosition(var2.getX() + var14 - var3, var2.getZ() + var15 - var3, false);
            if (var16 != null) {
               this.moveTo(var16, 0.0F, 0.0F);
               if (var1.noCollision(this)) {
                  break;
               }
            }
         }
      } else {
         this.moveTo(var2, 0.0F, 0.0F);

         while(!var1.noCollision(this) && this.y < 255.0D) {
            this.setPos(this.x, this.y + 1.0D, this.z);
         }
      }

   }

   private int getCoprime(int var1) {
      return var1 <= 16 ? var1 - 1 : 17;
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      if (var1.contains("playerGameType", 99)) {
         if (this.getServer().getForceGameType()) {
            this.gameMode.setGameModeForPlayer(this.getServer().getDefaultGameType());
         } else {
            this.gameMode.setGameModeForPlayer(GameType.byId(var1.getInt("playerGameType")));
         }
      }

      if (var1.contains("enteredNetherPosition", 10)) {
         CompoundTag var2 = var1.getCompound("enteredNetherPosition");
         this.enteredNetherPosition = new Vec3(var2.getDouble("x"), var2.getDouble("y"), var2.getDouble("z"));
      }

      this.seenCredits = var1.getBoolean("seenCredits");
      if (var1.contains("recipeBook", 10)) {
         this.recipeBook.fromNbt(var1.getCompound("recipeBook"));
      }

      if (this.isSleeping()) {
         this.stopSleeping();
      }

   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("playerGameType", this.gameMode.getGameModeForPlayer().getId());
      var1.putBoolean("seenCredits", this.seenCredits);
      if (this.enteredNetherPosition != null) {
         CompoundTag var2 = new CompoundTag();
         var2.putDouble("x", this.enteredNetherPosition.x);
         var2.putDouble("y", this.enteredNetherPosition.y);
         var2.putDouble("z", this.enteredNetherPosition.z);
         var1.put("enteredNetherPosition", var2);
      }

      Entity var6 = this.getRootVehicle();
      Entity var3 = this.getVehicle();
      if (var3 != null && var6 != this && var6.hasOnePlayerPassenger()) {
         CompoundTag var4 = new CompoundTag();
         CompoundTag var5 = new CompoundTag();
         var6.save(var5);
         var4.putUUID("Attach", var3.getUUID());
         var4.put("Entity", var5);
         var1.put("RootVehicle", var4);
      }

      var1.put("recipeBook", this.recipeBook.toNbt());
   }

   public void setExperiencePoints(int var1) {
      float var2 = (float)this.getXpNeededForNextLevel();
      float var3 = (var2 - 1.0F) / var2;
      this.experienceProgress = Mth.clamp((float)var1 / var2, 0.0F, var3);
      this.lastSentExp = -1;
   }

   public void setExperienceLevels(int var1) {
      this.experienceLevel = var1;
      this.lastSentExp = -1;
   }

   public void giveExperienceLevels(int var1) {
      super.giveExperienceLevels(var1);
      this.lastSentExp = -1;
   }

   public void onEnchantmentPerformed(ItemStack var1, int var2) {
      super.onEnchantmentPerformed(var1, var2);
      this.lastSentExp = -1;
   }

   public void initMenu() {
      this.containerMenu.addSlotListener(this);
   }

   public void onEnterCombat() {
      super.onEnterCombat();
      this.connection.send(new ClientboundPlayerCombatPacket(this.getCombatTracker(), ClientboundPlayerCombatPacket.Event.ENTER_COMBAT));
   }

   public void onLeaveCombat() {
      super.onLeaveCombat();
      this.connection.send(new ClientboundPlayerCombatPacket(this.getCombatTracker(), ClientboundPlayerCombatPacket.Event.END_COMBAT));
   }

   protected void onInsideBlock(BlockState var1) {
      CriteriaTriggers.ENTER_BLOCK.trigger(this, var1);
   }

   protected ItemCooldowns createItemCooldowns() {
      return new ServerItemCooldowns(this);
   }

   public void tick() {
      this.gameMode.tick();
      --this.spawnInvulnerableTime;
      if (this.invulnerableTime > 0) {
         --this.invulnerableTime;
      }

      this.containerMenu.broadcastChanges();
      if (!this.level.isClientSide && !this.containerMenu.stillValid(this)) {
         this.closeContainer();
         this.containerMenu = this.inventoryMenu;
      }

      while(!this.entitiesToRemove.isEmpty()) {
         int var1 = Math.min(this.entitiesToRemove.size(), 2147483647);
         int[] var2 = new int[var1];
         Iterator var3 = this.entitiesToRemove.iterator();
         int var4 = 0;

         while(var3.hasNext() && var4 < var1) {
            var2[var4++] = (Integer)var3.next();
            var3.remove();
         }

         this.connection.send(new ClientboundRemoveEntitiesPacket(var2));
      }

      Entity var5 = this.getCamera();
      if (var5 != this) {
         if (var5.isAlive()) {
            this.absMoveTo(var5.x, var5.y, var5.z, var5.yRot, var5.xRot);
            this.getLevel().getChunkSource().move(this);
            if (this.isSneaking()) {
               this.setCamera(this);
            }
         } else {
            this.setCamera(this);
         }
      }

      CriteriaTriggers.TICK.trigger(this);
      if (this.levitationStartPos != null) {
         CriteriaTriggers.LEVITATION.trigger(this, this.levitationStartPos, this.tickCount - this.levitationStartTime);
      }

      this.advancements.flushDirty(this);
   }

   public void doTick() {
      try {
         if (!this.isSpectator() || this.level.hasChunkAt(new BlockPos(this))) {
            super.tick();
         }

         for(int var1 = 0; var1 < this.inventory.getContainerSize(); ++var1) {
            ItemStack var5 = this.inventory.getItem(var1);
            if (var5.getItem().isComplex()) {
               Packet var6 = ((ComplexItem)var5.getItem()).getUpdatePacket(var5, this.level, this);
               if (var6 != null) {
                  this.connection.send(var6);
               }
            }
         }

         if (this.getHealth() != this.lastSentHealth || this.lastSentFood != this.foodData.getFoodLevel() || this.foodData.getSaturationLevel() == 0.0F != this.lastFoodSaturationZero) {
            this.connection.send(new ClientboundSetHealthPacket(this.getHealth(), this.foodData.getFoodLevel(), this.foodData.getSaturationLevel()));
            this.lastSentHealth = this.getHealth();
            this.lastSentFood = this.foodData.getFoodLevel();
            this.lastFoodSaturationZero = this.foodData.getSaturationLevel() == 0.0F;
         }

         if (this.getHealth() + this.getAbsorptionAmount() != this.lastRecordedHealthAndAbsorption) {
            this.lastRecordedHealthAndAbsorption = this.getHealth() + this.getAbsorptionAmount();
            this.updateScoreForCriteria(ObjectiveCriteria.HEALTH, Mth.ceil(this.lastRecordedHealthAndAbsorption));
         }

         if (this.foodData.getFoodLevel() != this.lastRecordedFoodLevel) {
            this.lastRecordedFoodLevel = this.foodData.getFoodLevel();
            this.updateScoreForCriteria(ObjectiveCriteria.FOOD, Mth.ceil((float)this.lastRecordedFoodLevel));
         }

         if (this.getAirSupply() != this.lastRecordedAirLevel) {
            this.lastRecordedAirLevel = this.getAirSupply();
            this.updateScoreForCriteria(ObjectiveCriteria.AIR, Mth.ceil((float)this.lastRecordedAirLevel));
         }

         if (this.getArmorValue() != this.lastRecordedArmor) {
            this.lastRecordedArmor = this.getArmorValue();
            this.updateScoreForCriteria(ObjectiveCriteria.ARMOR, Mth.ceil((float)this.lastRecordedArmor));
         }

         if (this.totalExperience != this.lastRecordedExperience) {
            this.lastRecordedExperience = this.totalExperience;
            this.updateScoreForCriteria(ObjectiveCriteria.EXPERIENCE, Mth.ceil((float)this.lastRecordedExperience));
         }

         if (this.experienceLevel != this.lastRecordedLevel) {
            this.lastRecordedLevel = this.experienceLevel;
            this.updateScoreForCriteria(ObjectiveCriteria.LEVEL, Mth.ceil((float)this.lastRecordedLevel));
         }

         if (this.totalExperience != this.lastSentExp) {
            this.lastSentExp = this.totalExperience;
            this.connection.send(new ClientboundSetExperiencePacket(this.experienceProgress, this.totalExperience, this.experienceLevel));
         }

         if (this.tickCount % 20 == 0) {
            CriteriaTriggers.LOCATION.trigger(this);
         }

      } catch (Throwable var4) {
         CrashReport var2 = CrashReport.forThrowable(var4, "Ticking player");
         CrashReportCategory var3 = var2.addCategory("Player being ticked");
         this.fillCrashReportCategory(var3);
         throw new ReportedException(var2);
      }
   }

   private void updateScoreForCriteria(ObjectiveCriteria var1, int var2) {
      this.getScoreboard().forAllObjectives(var1, this.getScoreboardName(), (var1x) -> {
         var1x.setScore(var2);
      });
   }

   public void die(DamageSource var1) {
      boolean var2 = this.level.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES);
      if (var2) {
         Component var3 = this.getCombatTracker().getDeathMessage();
         this.connection.send(new ClientboundPlayerCombatPacket(this.getCombatTracker(), ClientboundPlayerCombatPacket.Event.ENTITY_DIED, var3), (var2x) -> {
            if (!var2x.isSuccess()) {
               boolean var3x = true;
               String var4 = var3.getString(256);
               TranslatableComponent var5 = new TranslatableComponent("death.attack.message_too_long", new Object[]{(new TextComponent(var4)).withStyle(ChatFormatting.YELLOW)});
               Component var6 = (new TranslatableComponent("death.attack.even_more_magic", new Object[]{this.getDisplayName()})).withStyle((var1) -> {
                  var1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, var5));
               });
               this.connection.send(new ClientboundPlayerCombatPacket(this.getCombatTracker(), ClientboundPlayerCombatPacket.Event.ENTITY_DIED, var6));
            }

         });
         Team var4 = this.getTeam();
         if (var4 != null && var4.getDeathMessageVisibility() != Team.Visibility.ALWAYS) {
            if (var4.getDeathMessageVisibility() == Team.Visibility.HIDE_FOR_OTHER_TEAMS) {
               this.server.getPlayerList().broadcastToTeam(this, var3);
            } else if (var4.getDeathMessageVisibility() == Team.Visibility.HIDE_FOR_OWN_TEAM) {
               this.server.getPlayerList().broadcastToAllExceptTeam(this, var3);
            }
         } else {
            this.server.getPlayerList().broadcastMessage(var3);
         }
      } else {
         this.connection.send(new ClientboundPlayerCombatPacket(this.getCombatTracker(), ClientboundPlayerCombatPacket.Event.ENTITY_DIED));
      }

      this.removeEntitiesOnShoulder();
      if (!this.isSpectator()) {
         this.dropAllDeathLoot(var1);
      }

      this.getScoreboard().forAllObjectives(ObjectiveCriteria.DEATH_COUNT, this.getScoreboardName(), Score::increment);
      LivingEntity var7 = this.getKillCredit();
      if (var7 != null) {
         this.awardStat(Stats.ENTITY_KILLED_BY.get(var7.getType()));
         var7.awardKillScore(this, this.deathScore, var1);
         if (!this.level.isClientSide && var7 instanceof WitherBoss) {
            boolean var8 = false;
            if (this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
               BlockPos var5 = new BlockPos(this.x, this.y, this.z);
               BlockState var6 = Blocks.WITHER_ROSE.defaultBlockState();
               if (this.level.getBlockState(var5).isAir() && var6.canSurvive(this.level, var5)) {
                  this.level.setBlock(var5, var6, 3);
                  var8 = true;
               }
            }

            if (!var8) {
               ItemEntity var9 = new ItemEntity(this.level, this.x, this.y, this.z, new ItemStack(Items.WITHER_ROSE));
               this.level.addFreshEntity(var9);
            }
         }
      }

      this.awardStat(Stats.DEATHS);
      this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_DEATH));
      this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
      this.clearFire();
      this.setSharedFlag(0, false);
      this.getCombatTracker().recheckStatus();
   }

   public void awardKillScore(Entity var1, int var2, DamageSource var3) {
      if (var1 != this) {
         super.awardKillScore(var1, var2, var3);
         this.increaseScore(var2);
         String var4 = this.getScoreboardName();
         String var5 = var1.getScoreboardName();
         this.getScoreboard().forAllObjectives(ObjectiveCriteria.KILL_COUNT_ALL, var4, Score::increment);
         if (var1 instanceof Player) {
            this.awardStat(Stats.PLAYER_KILLS);
            this.getScoreboard().forAllObjectives(ObjectiveCriteria.KILL_COUNT_PLAYERS, var4, Score::increment);
         } else {
            this.awardStat(Stats.MOB_KILLS);
         }

         this.handleTeamKill(var4, var5, ObjectiveCriteria.TEAM_KILL);
         this.handleTeamKill(var5, var4, ObjectiveCriteria.KILLED_BY_TEAM);
         CriteriaTriggers.PLAYER_KILLED_ENTITY.trigger(this, var1, var3);
      }
   }

   private void handleTeamKill(String var1, String var2, ObjectiveCriteria[] var3) {
      PlayerTeam var4 = this.getScoreboard().getPlayersTeam(var2);
      if (var4 != null) {
         int var5 = var4.getColor().getId();
         if (var5 >= 0 && var5 < var3.length) {
            this.getScoreboard().forAllObjectives(var3[var5], var1, Score::increment);
         }
      }

   }

   public boolean hurt(DamageSource var1, float var2) {
      if (this.isInvulnerableTo(var1)) {
         return false;
      } else {
         boolean var3 = this.server.isDedicatedServer() && this.isPvpAllowed() && "fall".equals(var1.msgId);
         if (!var3 && this.spawnInvulnerableTime > 0 && var1 != DamageSource.OUT_OF_WORLD) {
            return false;
         } else {
            if (var1 instanceof EntityDamageSource) {
               Entity var4 = var1.getEntity();
               if (var4 instanceof Player && !this.canHarmPlayer((Player)var4)) {
                  return false;
               }

               if (var4 instanceof AbstractArrow) {
                  AbstractArrow var5 = (AbstractArrow)var4;
                  Entity var6 = var5.getOwner();
                  if (var6 instanceof Player && !this.canHarmPlayer((Player)var6)) {
                     return false;
                  }
               }
            }

            return super.hurt(var1, var2);
         }
      }
   }

   public boolean canHarmPlayer(Player var1) {
      return !this.isPvpAllowed() ? false : super.canHarmPlayer(var1);
   }

   private boolean isPvpAllowed() {
      return this.server.isPvpAllowed();
   }

   @Nullable
   public Entity changeDimension(DimensionType var1) {
      this.isChangingDimension = true;
      DimensionType var2 = this.dimension;
      if (var2 == DimensionType.THE_END && var1 == DimensionType.OVERWORLD) {
         this.unRide();
         this.getLevel().removePlayerImmediately(this);
         if (!this.wonGame) {
            this.wonGame = true;
            this.connection.send(new ClientboundGameEventPacket(4, this.seenCredits ? 0.0F : 1.0F));
            this.seenCredits = true;
         }

         return this;
      } else {
         ServerLevel var3 = this.server.getLevel(var2);
         this.dimension = var1;
         ServerLevel var4 = this.server.getLevel(var1);
         LevelData var5 = this.level.getLevelData();
         this.connection.send(new ClientboundRespawnPacket(var1, var5.getGeneratorType(), this.gameMode.getGameModeForPlayer()));
         this.connection.send(new ClientboundChangeDifficultyPacket(var5.getDifficulty(), var5.isDifficultyLocked()));
         PlayerList var6 = this.server.getPlayerList();
         var6.sendPlayerPermissionLevel(this);
         var3.removePlayerImmediately(this);
         this.removed = false;
         double var7 = this.x;
         double var9 = this.y;
         double var11 = this.z;
         float var13 = this.xRot;
         float var14 = this.yRot;
         double var15 = 8.0D;
         float var17 = var14;
         var3.getProfiler().push("moving");
         if (var2 == DimensionType.OVERWORLD && var1 == DimensionType.NETHER) {
            this.enteredNetherPosition = new Vec3(this.x, this.y, this.z);
            var7 /= 8.0D;
            var11 /= 8.0D;
         } else if (var2 == DimensionType.NETHER && var1 == DimensionType.OVERWORLD) {
            var7 *= 8.0D;
            var11 *= 8.0D;
         } else if (var2 == DimensionType.OVERWORLD && var1 == DimensionType.THE_END) {
            BlockPos var18 = var4.getDimensionSpecificSpawn();
            var7 = (double)var18.getX();
            var9 = (double)var18.getY();
            var11 = (double)var18.getZ();
            var14 = 90.0F;
            var13 = 0.0F;
         }

         this.moveTo(var7, var9, var11, var14, var13);
         var3.getProfiler().pop();
         var3.getProfiler().push("placing");
         double var38 = Math.min(-2.9999872E7D, var4.getWorldBorder().getMinX() + 16.0D);
         double var20 = Math.min(-2.9999872E7D, var4.getWorldBorder().getMinZ() + 16.0D);
         double var22 = Math.min(2.9999872E7D, var4.getWorldBorder().getMaxX() - 16.0D);
         double var24 = Math.min(2.9999872E7D, var4.getWorldBorder().getMaxZ() - 16.0D);
         var7 = Mth.clamp(var7, var38, var22);
         var11 = Mth.clamp(var11, var20, var24);
         this.moveTo(var7, var9, var11, var14, var13);
         if (var1 == DimensionType.THE_END) {
            int var26 = Mth.floor(this.x);
            int var27 = Mth.floor(this.y) - 1;
            int var28 = Mth.floor(this.z);
            boolean var29 = true;
            boolean var30 = false;

            for(int var31 = -2; var31 <= 2; ++var31) {
               for(int var32 = -2; var32 <= 2; ++var32) {
                  for(int var33 = -1; var33 < 3; ++var33) {
                     int var34 = var26 + var32 * 1 + var31 * 0;
                     int var35 = var27 + var33;
                     int var36 = var28 + var32 * 0 - var31 * 1;
                     boolean var37 = var33 < 0;
                     var4.setBlockAndUpdate(new BlockPos(var34, var35, var36), var37 ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.AIR.defaultBlockState());
                  }
               }
            }

            this.moveTo((double)var26, (double)var27, (double)var28, var14, 0.0F);
            this.setDeltaMovement(Vec3.ZERO);
         } else if (!var4.getPortalForcer().findAndMoveToPortal(this, var17)) {
            var4.getPortalForcer().createPortal(this);
            var4.getPortalForcer().findAndMoveToPortal(this, var17);
         }

         var3.getProfiler().pop();
         this.setLevel(var4);
         var4.addDuringPortalTeleport(this);
         this.triggerDimensionChangeTriggers(var3);
         this.connection.teleport(this.x, this.y, this.z, var14, var13);
         this.gameMode.setLevel(var4);
         this.connection.send(new ClientboundPlayerAbilitiesPacket(this.abilities));
         var6.sendLevelInfo(this, var4);
         var6.sendAllPlayerInfo(this);
         Iterator var39 = this.getActiveEffects().iterator();

         while(var39.hasNext()) {
            MobEffectInstance var40 = (MobEffectInstance)var39.next();
            this.connection.send(new ClientboundUpdateMobEffectPacket(this.getId(), var40));
         }

         this.connection.send(new ClientboundLevelEventPacket(1032, BlockPos.ZERO, 0, false));
         this.lastSentExp = -1;
         this.lastSentHealth = -1.0F;
         this.lastSentFood = -1;
         return this;
      }
   }

   private void triggerDimensionChangeTriggers(ServerLevel var1) {
      DimensionType var2 = var1.dimension.getType();
      DimensionType var3 = this.level.dimension.getType();
      CriteriaTriggers.CHANGED_DIMENSION.trigger(this, var2, var3);
      if (var2 == DimensionType.NETHER && var3 == DimensionType.OVERWORLD && this.enteredNetherPosition != null) {
         CriteriaTriggers.NETHER_TRAVEL.trigger(this, this.enteredNetherPosition);
      }

      if (var3 != DimensionType.NETHER) {
         this.enteredNetherPosition = null;
      }

   }

   public boolean broadcastToPlayer(ServerPlayer var1) {
      if (var1.isSpectator()) {
         return this.getCamera() == this;
      } else {
         return this.isSpectator() ? false : super.broadcastToPlayer(var1);
      }
   }

   private void broadcast(BlockEntity var1) {
      if (var1 != null) {
         ClientboundBlockEntityDataPacket var2 = var1.getUpdatePacket();
         if (var2 != null) {
            this.connection.send(var2);
         }
      }

   }

   public void take(Entity var1, int var2) {
      super.take(var1, var2);
      this.containerMenu.broadcastChanges();
   }

   public Either<Player.BedSleepingProblem, Unit> startSleepInBed(BlockPos var1) {
      return super.startSleepInBed(var1).ifRight((var1x) -> {
         this.awardStat(Stats.SLEEP_IN_BED);
         CriteriaTriggers.SLEPT_IN_BED.trigger(this);
      });
   }

   public void stopSleepInBed(boolean var1, boolean var2, boolean var3) {
      if (this.isSleeping()) {
         this.getLevel().getChunkSource().broadcastAndSend(this, new ClientboundAnimatePacket(this, 2));
      }

      super.stopSleepInBed(var1, var2, var3);
      if (this.connection != null) {
         this.connection.teleport(this.x, this.y, this.z, this.yRot, this.xRot);
      }

   }

   public boolean startRiding(Entity var1, boolean var2) {
      Entity var3 = this.getVehicle();
      if (!super.startRiding(var1, var2)) {
         return false;
      } else {
         Entity var4 = this.getVehicle();
         if (var4 != var3 && this.connection != null) {
            this.connection.teleport(this.x, this.y, this.z, this.yRot, this.xRot);
         }

         return true;
      }
   }

   public void stopRiding() {
      Entity var1 = this.getVehicle();
      super.stopRiding();
      Entity var2 = this.getVehicle();
      if (var2 != var1 && this.connection != null) {
         this.connection.teleport(this.x, this.y, this.z, this.yRot, this.xRot);
      }

   }

   public boolean isInvulnerableTo(DamageSource var1) {
      return super.isInvulnerableTo(var1) || this.isChangingDimension() || this.abilities.invulnerable && var1 == DamageSource.WITHER;
   }

   protected void checkFallDamage(double var1, boolean var3, BlockState var4, BlockPos var5) {
   }

   protected void onChangedBlock(BlockPos var1) {
      if (!this.isSpectator()) {
         super.onChangedBlock(var1);
      }

   }

   public void doCheckFallDamage(double var1, boolean var3) {
      int var4 = Mth.floor(this.x);
      int var5 = Mth.floor(this.y - 0.20000000298023224D);
      int var6 = Mth.floor(this.z);
      BlockPos var7 = new BlockPos(var4, var5, var6);
      if (this.level.hasChunkAt(var7)) {
         BlockState var8 = this.level.getBlockState(var7);
         if (var8.isAir()) {
            BlockPos var9 = var7.below();
            BlockState var10 = this.level.getBlockState(var9);
            Block var11 = var10.getBlock();
            if (var11.is(BlockTags.FENCES) || var11.is(BlockTags.WALLS) || var11 instanceof FenceGateBlock) {
               var7 = var9;
               var8 = var10;
            }
         }

         super.checkFallDamage(var1, var3, var8, var7);
      }
   }

   public void openTextEdit(SignBlockEntity var1) {
      var1.setAllowedPlayerEditor(this);
      this.connection.send(new ClientboundOpenSignEditorPacket(var1.getBlockPos()));
   }

   private void nextContainerCounter() {
      this.containerCounter = this.containerCounter % 100 + 1;
   }

   public OptionalInt openMenu(@Nullable MenuProvider var1) {
      if (var1 == null) {
         return OptionalInt.empty();
      } else {
         if (this.containerMenu != this.inventoryMenu) {
            this.closeContainer();
         }

         this.nextContainerCounter();
         AbstractContainerMenu var2 = var1.createMenu(this.containerCounter, this.inventory, this);
         if (var2 == null) {
            if (this.isSpectator()) {
               this.displayClientMessage((new TranslatableComponent("container.spectatorCantOpen", new Object[0])).withStyle(ChatFormatting.RED), true);
            }

            return OptionalInt.empty();
         } else {
            this.connection.send(new ClientboundOpenScreenPacket(var2.containerId, var2.getType(), var1.getDisplayName()));
            var2.addSlotListener(this);
            this.containerMenu = var2;
            return OptionalInt.of(this.containerCounter);
         }
      }
   }

   public void sendMerchantOffers(int var1, MerchantOffers var2, int var3, int var4, boolean var5, boolean var6) {
      this.connection.send(new ClientboundMerchantOffersPacket(var1, var2, var3, var4, var5, var6));
   }

   public void openHorseInventory(AbstractHorse var1, Container var2) {
      if (this.containerMenu != this.inventoryMenu) {
         this.closeContainer();
      }

      this.nextContainerCounter();
      this.connection.send(new ClientboundHorseScreenOpenPacket(this.containerCounter, var2.getContainerSize(), var1.getId()));
      this.containerMenu = new HorseInventoryMenu(this.containerCounter, this.inventory, var2, var1);
      this.containerMenu.addSlotListener(this);
   }

   public void openItemGui(ItemStack var1, InteractionHand var2) {
      Item var3 = var1.getItem();
      if (var3 == Items.WRITTEN_BOOK) {
         if (WrittenBookItem.resolveBookComponents(var1, this.createCommandSourceStack(), this)) {
            this.containerMenu.broadcastChanges();
         }

         this.connection.send(new ClientboundOpenBookPacket(var2));
      }

   }

   public void openCommandBlock(CommandBlockEntity var1) {
      var1.setSendToClient(true);
      this.broadcast(var1);
   }

   public void slotChanged(AbstractContainerMenu var1, int var2, ItemStack var3) {
      if (!(var1.getSlot(var2) instanceof ResultSlot)) {
         if (var1 == this.inventoryMenu) {
            CriteriaTriggers.INVENTORY_CHANGED.trigger(this, this.inventory);
         }

         if (!this.ignoreSlotUpdateHack) {
            this.connection.send(new ClientboundContainerSetSlotPacket(var1.containerId, var2, var3));
         }
      }
   }

   public void refreshContainer(AbstractContainerMenu var1) {
      this.refreshContainer(var1, var1.getItems());
   }

   public void refreshContainer(AbstractContainerMenu var1, NonNullList<ItemStack> var2) {
      this.connection.send(new ClientboundContainerSetContentPacket(var1.containerId, var2));
      this.connection.send(new ClientboundContainerSetSlotPacket(-1, -1, this.inventory.getCarried()));
   }

   public void setContainerData(AbstractContainerMenu var1, int var2, int var3) {
      this.connection.send(new ClientboundContainerSetDataPacket(var1.containerId, var2, var3));
   }

   public void closeContainer() {
      this.connection.send(new ClientboundContainerClosePacket(this.containerMenu.containerId));
      this.doCloseContainer();
   }

   public void broadcastCarriedItem() {
      if (!this.ignoreSlotUpdateHack) {
         this.connection.send(new ClientboundContainerSetSlotPacket(-1, -1, this.inventory.getCarried()));
      }
   }

   public void doCloseContainer() {
      this.containerMenu.removed(this);
      this.containerMenu = this.inventoryMenu;
   }

   public void setPlayerInput(float var1, float var2, boolean var3, boolean var4) {
      if (this.isPassenger()) {
         if (var1 >= -1.0F && var1 <= 1.0F) {
            this.xxa = var1;
         }

         if (var2 >= -1.0F && var2 <= 1.0F) {
            this.zza = var2;
         }

         this.jumping = var3;
         this.setSneaking(var4);
      }

   }

   public void awardStat(Stat<?> var1, int var2) {
      this.stats.increment(this, var1, var2);
      this.getScoreboard().forAllObjectives(var1, this.getScoreboardName(), (var1x) -> {
         var1x.add(var2);
      });
   }

   public void resetStat(Stat<?> var1) {
      this.stats.setValue(this, var1, 0);
      this.getScoreboard().forAllObjectives(var1, this.getScoreboardName(), Score::reset);
   }

   public int awardRecipes(Collection<Recipe<?>> var1) {
      return this.recipeBook.addRecipes(var1, this);
   }

   public void awardRecipesByKey(ResourceLocation[] var1) {
      ArrayList var2 = Lists.newArrayList();
      ResourceLocation[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ResourceLocation var6 = var3[var5];
         this.server.getRecipeManager().byKey(var6).ifPresent(var2::add);
      }

      this.awardRecipes(var2);
   }

   public int resetRecipes(Collection<Recipe<?>> var1) {
      return this.recipeBook.removeRecipes(var1, this);
   }

   public void giveExperiencePoints(int var1) {
      super.giveExperiencePoints(var1);
      this.lastSentExp = -1;
   }

   public void disconnect() {
      this.disconnected = true;
      this.ejectPassengers();
      if (this.isSleeping()) {
         this.stopSleepInBed(true, false, false);
      }

   }

   public boolean hasDisconnected() {
      return this.disconnected;
   }

   public void resetSentInfo() {
      this.lastSentHealth = -1.0E8F;
   }

   public void displayClientMessage(Component var1, boolean var2) {
      this.connection.send(new ClientboundChatPacket(var1, var2 ? ChatType.GAME_INFO : ChatType.CHAT));
   }

   protected void completeUsingItem() {
      if (!this.useItem.isEmpty() && this.isUsingItem()) {
         this.connection.send(new ClientboundEntityEventPacket(this, (byte)9));
         super.completeUsingItem();
      }

   }

   public void lookAt(EntityAnchorArgument.Anchor var1, Vec3 var2) {
      super.lookAt(var1, var2);
      this.connection.send(new ClientboundPlayerLookAtPacket(var1, var2.x, var2.y, var2.z));
   }

   public void lookAt(EntityAnchorArgument.Anchor var1, Entity var2, EntityAnchorArgument.Anchor var3) {
      Vec3 var4 = var3.apply(var2);
      super.lookAt(var1, var4);
      this.connection.send(new ClientboundPlayerLookAtPacket(var1, var2, var3));
   }

   public void restoreFrom(ServerPlayer var1, boolean var2) {
      if (var2) {
         this.inventory.replaceWith(var1.inventory);
         this.setHealth(var1.getHealth());
         this.foodData = var1.foodData;
         this.experienceLevel = var1.experienceLevel;
         this.totalExperience = var1.totalExperience;
         this.experienceProgress = var1.experienceProgress;
         this.setScore(var1.getScore());
         this.portalEntranceBlock = var1.portalEntranceBlock;
         this.portalEntranceOffset = var1.portalEntranceOffset;
         this.portalEntranceForwards = var1.portalEntranceForwards;
      } else if (this.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) || var1.isSpectator()) {
         this.inventory.replaceWith(var1.inventory);
         this.experienceLevel = var1.experienceLevel;
         this.totalExperience = var1.totalExperience;
         this.experienceProgress = var1.experienceProgress;
         this.setScore(var1.getScore());
      }

      this.enchantmentSeed = var1.enchantmentSeed;
      this.enderChestInventory = var1.enderChestInventory;
      this.getEntityData().set(DATA_PLAYER_MODE_CUSTOMISATION, var1.getEntityData().get(DATA_PLAYER_MODE_CUSTOMISATION));
      this.lastSentExp = -1;
      this.lastSentHealth = -1.0F;
      this.lastSentFood = -1;
      this.recipeBook.copyOverData(var1.recipeBook);
      this.entitiesToRemove.addAll(var1.entitiesToRemove);
      this.seenCredits = var1.seenCredits;
      this.enteredNetherPosition = var1.enteredNetherPosition;
      this.setShoulderEntityLeft(var1.getShoulderEntityLeft());
      this.setShoulderEntityRight(var1.getShoulderEntityRight());
   }

   protected void onEffectAdded(MobEffectInstance var1) {
      super.onEffectAdded(var1);
      this.connection.send(new ClientboundUpdateMobEffectPacket(this.getId(), var1));
      if (var1.getEffect() == MobEffects.LEVITATION) {
         this.levitationStartTime = this.tickCount;
         this.levitationStartPos = new Vec3(this.x, this.y, this.z);
      }

      CriteriaTriggers.EFFECTS_CHANGED.trigger(this);
   }

   protected void onEffectUpdated(MobEffectInstance var1, boolean var2) {
      super.onEffectUpdated(var1, var2);
      this.connection.send(new ClientboundUpdateMobEffectPacket(this.getId(), var1));
      CriteriaTriggers.EFFECTS_CHANGED.trigger(this);
   }

   protected void onEffectRemoved(MobEffectInstance var1) {
      super.onEffectRemoved(var1);
      this.connection.send(new ClientboundRemoveMobEffectPacket(this.getId(), var1.getEffect()));
      if (var1.getEffect() == MobEffects.LEVITATION) {
         this.levitationStartPos = null;
      }

      CriteriaTriggers.EFFECTS_CHANGED.trigger(this);
   }

   public void teleportTo(double var1, double var3, double var5) {
      this.connection.teleport(var1, var3, var5, this.yRot, this.xRot);
   }

   public void crit(Entity var1) {
      this.getLevel().getChunkSource().broadcastAndSend(this, new ClientboundAnimatePacket(var1, 4));
   }

   public void magicCrit(Entity var1) {
      this.getLevel().getChunkSource().broadcastAndSend(this, new ClientboundAnimatePacket(var1, 5));
   }

   public void onUpdateAbilities() {
      if (this.connection != null) {
         this.connection.send(new ClientboundPlayerAbilitiesPacket(this.abilities));
         this.updateInvisibilityStatus();
      }
   }

   public ServerLevel getLevel() {
      return (ServerLevel)this.level;
   }

   public void setGameMode(GameType var1) {
      this.gameMode.setGameModeForPlayer(var1);
      this.connection.send(new ClientboundGameEventPacket(3, (float)var1.getId()));
      if (var1 == GameType.SPECTATOR) {
         this.removeEntitiesOnShoulder();
         this.stopRiding();
      } else {
         this.setCamera(this);
      }

      this.onUpdateAbilities();
      this.updateEffectVisibility();
   }

   public boolean isSpectator() {
      return this.gameMode.getGameModeForPlayer() == GameType.SPECTATOR;
   }

   public boolean isCreative() {
      return this.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
   }

   public void sendMessage(Component var1) {
      this.sendMessage(var1, ChatType.SYSTEM);
   }

   public void sendMessage(Component var1, ChatType var2) {
      this.connection.send(new ClientboundChatPacket(var1, var2), (var3) -> {
         if (!var3.isSuccess() && (var2 == ChatType.GAME_INFO || var2 == ChatType.SYSTEM)) {
            boolean var4 = true;
            String var5 = var1.getString(256);
            Component var6 = (new TextComponent(var5)).withStyle(ChatFormatting.YELLOW);
            this.connection.send(new ClientboundChatPacket((new TranslatableComponent("multiplayer.message_not_delivered", new Object[]{var6})).withStyle(ChatFormatting.RED), ChatType.SYSTEM));
         }

      });
   }

   public String getIpAddress() {
      String var1 = this.connection.connection.getRemoteAddress().toString();
      var1 = var1.substring(var1.indexOf("/") + 1);
      var1 = var1.substring(0, var1.indexOf(":"));
      return var1;
   }

   public void updateOptions(ServerboundClientInformationPacket var1) {
      this.language = var1.getLanguage();
      this.chatVisibility = var1.getChatVisibility();
      this.canChatColor = var1.getChatColors();
      this.getEntityData().set(DATA_PLAYER_MODE_CUSTOMISATION, (byte)var1.getModelCustomisation());
      this.getEntityData().set(DATA_PLAYER_MAIN_HAND, (byte)(var1.getMainHand() == HumanoidArm.LEFT ? 0 : 1));
   }

   public ChatVisiblity getChatVisibility() {
      return this.chatVisibility;
   }

   public void sendTexturePack(String var1, String var2) {
      this.connection.send(new ClientboundResourcePackPacket(var1, var2));
   }

   protected int getPermissionLevel() {
      return this.server.getProfilePermissions(this.getGameProfile());
   }

   public void resetLastActionTime() {
      this.lastActionTime = Util.getMillis();
   }

   public ServerStatsCounter getStats() {
      return this.stats;
   }

   public ServerRecipeBook getRecipeBook() {
      return this.recipeBook;
   }

   public void sendRemoveEntity(Entity var1) {
      if (var1 instanceof Player) {
         this.connection.send(new ClientboundRemoveEntitiesPacket(new int[]{var1.getId()}));
      } else {
         this.entitiesToRemove.add(var1.getId());
      }

   }

   public void cancelRemoveEntity(Entity var1) {
      this.entitiesToRemove.remove(var1.getId());
   }

   protected void updateInvisibilityStatus() {
      if (this.isSpectator()) {
         this.removeEffectParticles();
         this.setInvisible(true);
      } else {
         super.updateInvisibilityStatus();
      }

   }

   public Entity getCamera() {
      return (Entity)(this.camera == null ? this : this.camera);
   }

   public void setCamera(Entity var1) {
      Entity var2 = this.getCamera();
      this.camera = (Entity)(var1 == null ? this : var1);
      if (var2 != this.camera) {
         this.connection.send(new ClientboundSetCameraPacket(this.camera));
         this.teleportTo(this.camera.x, this.camera.y, this.camera.z);
      }

   }

   protected void processDimensionDelay() {
      if (this.changingDimensionDelay > 0 && !this.isChangingDimension) {
         --this.changingDimensionDelay;
      }

   }

   public void attack(Entity var1) {
      if (this.gameMode.getGameModeForPlayer() == GameType.SPECTATOR) {
         this.setCamera(var1);
      } else {
         super.attack(var1);
      }

   }

   public long getLastActionTime() {
      return this.lastActionTime;
   }

   @Nullable
   public Component getTabListDisplayName() {
      return null;
   }

   public void swing(InteractionHand var1) {
      super.swing(var1);
      this.resetAttackStrengthTicker();
   }

   public boolean isChangingDimension() {
      return this.isChangingDimension;
   }

   public void hasChangedDimension() {
      this.isChangingDimension = false;
   }

   public void startFallFlying() {
      this.setSharedFlag(7, true);
   }

   public void stopFallFlying() {
      this.setSharedFlag(7, true);
      this.setSharedFlag(7, false);
   }

   public PlayerAdvancements getAdvancements() {
      return this.advancements;
   }

   public void teleportTo(ServerLevel var1, double var2, double var4, double var6, float var8, float var9) {
      this.setCamera(this);
      this.stopRiding();
      if (var1 == this.level) {
         this.connection.teleport(var2, var4, var6, var8, var9);
      } else {
         ServerLevel var10 = this.getLevel();
         this.dimension = var1.dimension.getType();
         LevelData var11 = var1.getLevelData();
         this.connection.send(new ClientboundRespawnPacket(this.dimension, var11.getGeneratorType(), this.gameMode.getGameModeForPlayer()));
         this.connection.send(new ClientboundChangeDifficultyPacket(var11.getDifficulty(), var11.isDifficultyLocked()));
         this.server.getPlayerList().sendPlayerPermissionLevel(this);
         var10.removePlayerImmediately(this);
         this.removed = false;
         this.moveTo(var2, var4, var6, var8, var9);
         this.setLevel(var1);
         var1.addDuringCommandTeleport(this);
         this.triggerDimensionChangeTriggers(var10);
         this.connection.teleport(var2, var4, var6, var8, var9);
         this.gameMode.setLevel(var1);
         this.server.getPlayerList().sendLevelInfo(this, var1);
         this.server.getPlayerList().sendAllPlayerInfo(this);
      }

   }

   public void trackChunk(ChunkPos var1, Packet<?> var2, Packet<?> var3) {
      this.connection.send(var3);
      this.connection.send(var2);
   }

   public void untrackChunk(ChunkPos var1) {
      this.connection.send(new ClientboundForgetLevelChunkPacket(var1.x, var1.z));
   }

   public SectionPos getLastSectionPos() {
      return this.lastSectionPos;
   }

   public void setLastSectionPos(SectionPos var1) {
      this.lastSectionPos = var1;
   }

   public void playNotifySound(SoundEvent var1, SoundSource var2, float var3, float var4) {
      this.connection.send(new ClientboundSoundPacket(var1, var2, this.x, this.y, this.z, var3, var4));
   }

   public Packet<?> getAddEntityPacket() {
      return new ClientboundAddPlayerPacket(this);
   }

   public ItemEntity drop(ItemStack var1, boolean var2, boolean var3) {
      ItemEntity var4 = super.drop(var1, var2, var3);
      if (var4 == null) {
         return null;
      } else {
         this.level.addFreshEntity(var4);
         ItemStack var5 = var4.getItem();
         if (var3) {
            if (!var5.isEmpty()) {
               this.awardStat(Stats.ITEM_DROPPED.get(var5.getItem()), var1.getCount());
            }

            this.awardStat(Stats.DROP);
         }

         return var4;
      }
   }
}
