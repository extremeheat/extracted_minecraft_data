package net.minecraft.world.level.block.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.LockCode;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

public class BeaconBlockEntity extends BlockEntity implements MenuProvider {
   private static final int MAX_LEVELS = 4;
   public static final MobEffect[][] BEACON_EFFECTS = new MobEffect[][]{
      {MobEffects.MOVEMENT_SPEED, MobEffects.DIG_SPEED}, {MobEffects.DAMAGE_RESISTANCE, MobEffects.JUMP}, {MobEffects.DAMAGE_BOOST}, {MobEffects.REGENERATION}
   };
   private static final Set<MobEffect> VALID_EFFECTS = Arrays.stream(BEACON_EFFECTS).flatMap(Arrays::stream).collect(Collectors.toSet());
   public static final int DATA_LEVELS = 0;
   public static final int DATA_PRIMARY = 1;
   public static final int DATA_SECONDARY = 2;
   public static final int NUM_DATA_VALUES = 3;
   private static final int BLOCKS_CHECK_PER_TICK = 10;
   List<BeaconBlockEntity.BeaconBeamSection> beamSections = Lists.newArrayList();
   private List<BeaconBlockEntity.BeaconBeamSection> checkingBeamSections = Lists.newArrayList();
   int levels;
   private int lastCheckY;
   @Nullable
   MobEffect primaryPower;
   @Nullable
   MobEffect secondaryPower;
   @Nullable
   private Component name;
   private LockCode lockKey = LockCode.NO_LOCK;
   private final ContainerData dataAccess = new ContainerData() {
      @Override
      public int get(int var1) {
         return switch(var1) {
            case 0 -> BeaconBlockEntity.this.levels;
            case 1 -> MobEffect.getIdFromNullable(BeaconBlockEntity.this.primaryPower);
            case 2 -> MobEffect.getIdFromNullable(BeaconBlockEntity.this.secondaryPower);
            default -> 0;
         };
      }

      @Override
      public void set(int var1, int var2) {
         switch(var1) {
            case 0:
               BeaconBlockEntity.this.levels = var2;
               break;
            case 1:
               if (!BeaconBlockEntity.this.level.isClientSide && !BeaconBlockEntity.this.beamSections.isEmpty()) {
                  BeaconBlockEntity.playSound(BeaconBlockEntity.this.level, BeaconBlockEntity.this.worldPosition, SoundEvents.BEACON_POWER_SELECT);
               }

               BeaconBlockEntity.this.primaryPower = BeaconBlockEntity.getValidEffectById(var2);
               break;
            case 2:
               BeaconBlockEntity.this.secondaryPower = BeaconBlockEntity.getValidEffectById(var2);
         }
      }

      @Override
      public int getCount() {
         return 3;
      }
   };

   public BeaconBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.BEACON, var1, var2);
   }

   public static void tick(Level var0, BlockPos var1, BlockState var2, BeaconBlockEntity var3) {
      int var4 = var1.getX();
      int var5 = var1.getY();
      int var6 = var1.getZ();
      BlockPos var7;
      if (var3.lastCheckY < var5) {
         var7 = var1;
         var3.checkingBeamSections = Lists.newArrayList();
         var3.lastCheckY = var1.getY() - 1;
      } else {
         var7 = new BlockPos(var4, var3.lastCheckY + 1, var6);
      }

      BeaconBlockEntity.BeaconBeamSection var8 = var3.checkingBeamSections.isEmpty()
         ? null
         : var3.checkingBeamSections.get(var3.checkingBeamSections.size() - 1);
      int var9 = var0.getHeight(Heightmap.Types.WORLD_SURFACE, var4, var6);

      for(int var10 = 0; var10 < 10 && var7.getY() <= var9; ++var10) {
         BlockState var11 = var0.getBlockState(var7);
         Block var12 = var11.getBlock();
         if (var12 instanceof BeaconBeamBlock) {
            float[] var13 = ((BeaconBeamBlock)var12).getColor().getTextureDiffuseColors();
            if (var3.checkingBeamSections.size() <= 1) {
               var8 = new BeaconBlockEntity.BeaconBeamSection(var13);
               var3.checkingBeamSections.add(var8);
            } else if (var8 != null) {
               if (Arrays.equals(var13, var8.color)) {
                  var8.increaseHeight();
               } else {
                  var8 = new BeaconBlockEntity.BeaconBeamSection(
                     new float[]{(var8.color[0] + var13[0]) / 2.0F, (var8.color[1] + var13[1]) / 2.0F, (var8.color[2] + var13[2]) / 2.0F}
                  );
                  var3.checkingBeamSections.add(var8);
               }
            }
         } else {
            if (var8 == null || var11.getLightBlock(var0, var7) >= 15 && !var11.is(Blocks.BEDROCK)) {
               var3.checkingBeamSections.clear();
               var3.lastCheckY = var9;
               break;
            }

            var8.increaseHeight();
         }

         var7 = var7.above();
         ++var3.lastCheckY;
      }

      int var15 = var3.levels;
      if (var0.getGameTime() % 80L == 0L) {
         if (!var3.beamSections.isEmpty()) {
            var3.levels = updateBase(var0, var4, var5, var6);
         }

         if (var3.levels > 0 && !var3.beamSections.isEmpty()) {
            applyEffects(var0, var1, var3.levels, var3.primaryPower, var3.secondaryPower);
            playSound(var0, var1, SoundEvents.BEACON_AMBIENT);
         }
      }

      if (var3.lastCheckY >= var9) {
         var3.lastCheckY = var0.getMinBuildHeight() - 1;
         boolean var16 = var15 > 0;
         var3.beamSections = var3.checkingBeamSections;
         if (!var0.isClientSide) {
            boolean var17 = var3.levels > 0;
            if (!var16 && var17) {
               playSound(var0, var1, SoundEvents.BEACON_ACTIVATE);

               for(ServerPlayer var14 : var0.getEntitiesOfClass(
                  ServerPlayer.class,
                  new AABB((double)var4, (double)var5, (double)var6, (double)var4, (double)(var5 - 4), (double)var6).inflate(10.0, 5.0, 10.0)
               )) {
                  CriteriaTriggers.CONSTRUCT_BEACON.trigger(var14, var3.levels);
               }
            } else if (var16 && !var17) {
               playSound(var0, var1, SoundEvents.BEACON_DEACTIVATE);
            }
         }
      }
   }

   private static int updateBase(Level var0, int var1, int var2, int var3) {
      int var4 = 0;

      for(int var5 = 1; var5 <= 4; var4 = var5++) {
         int var6 = var2 - var5;
         if (var6 < var0.getMinBuildHeight()) {
            break;
         }

         boolean var7 = true;

         for(int var8 = var1 - var5; var8 <= var1 + var5 && var7; ++var8) {
            for(int var9 = var3 - var5; var9 <= var3 + var5; ++var9) {
               if (!var0.getBlockState(new BlockPos(var8, var6, var9)).is(BlockTags.BEACON_BASE_BLOCKS)) {
                  var7 = false;
                  break;
               }
            }
         }

         if (!var7) {
            break;
         }
      }

      return var4;
   }

   @Override
   public void setRemoved() {
      playSound(this.level, this.worldPosition, SoundEvents.BEACON_DEACTIVATE);
      super.setRemoved();
   }

   private static void applyEffects(Level var0, BlockPos var1, int var2, @Nullable MobEffect var3, @Nullable MobEffect var4) {
      if (!var0.isClientSide && var3 != null) {
         double var5 = (double)(var2 * 10 + 10);
         byte var7 = 0;
         if (var2 >= 4 && var3 == var4) {
            var7 = 1;
         }

         int var8 = (9 + var2 * 2) * 20;
         AABB var9 = new AABB(var1).inflate(var5).expandTowards(0.0, (double)var0.getHeight(), 0.0);
         List var10 = var0.getEntitiesOfClass(Player.class, var9);

         for(Player var12 : var10) {
            var12.addEffect(new MobEffectInstance(var3, var8, var7, true, true));
         }

         if (var2 >= 4 && var3 != var4 && var4 != null) {
            for(Player var14 : var10) {
               var14.addEffect(new MobEffectInstance(var4, var8, 0, true, true));
            }
         }
      }
   }

   public static void playSound(Level var0, BlockPos var1, SoundEvent var2) {
      var0.playSound(null, var1, var2, SoundSource.BLOCKS, 1.0F, 1.0F);
   }

   public List<BeaconBlockEntity.BeaconBeamSection> getBeamSections() {
      return (List<BeaconBlockEntity.BeaconBeamSection>)(this.levels == 0 ? ImmutableList.of() : this.beamSections);
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @Override
   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   @Nullable
   static MobEffect getValidEffectById(int var0) {
      MobEffect var1 = MobEffect.byId(var0);
      return VALID_EFFECTS.contains(var1) ? var1 : null;
   }

   @Override
   public void load(CompoundTag var1) {
      super.load(var1);
      this.primaryPower = getValidEffectById(var1.getInt("Primary"));
      this.secondaryPower = getValidEffectById(var1.getInt("Secondary"));
      if (var1.contains("CustomName", 8)) {
         this.name = Component.Serializer.fromJson(var1.getString("CustomName"));
      }

      this.lockKey = LockCode.fromTag(var1);
   }

   @Override
   protected void saveAdditional(CompoundTag var1) {
      super.saveAdditional(var1);
      var1.putInt("Primary", MobEffect.getIdFromNullable(this.primaryPower));
      var1.putInt("Secondary", MobEffect.getIdFromNullable(this.secondaryPower));
      var1.putInt("Levels", this.levels);
      if (this.name != null) {
         var1.putString("CustomName", Component.Serializer.toJson(this.name));
      }

      this.lockKey.addToTag(var1);
   }

   public void setCustomName(@Nullable Component var1) {
      this.name = var1;
   }

   @Nullable
   @Override
   public AbstractContainerMenu createMenu(int var1, Inventory var2, Player var3) {
      return BaseContainerBlockEntity.canUnlock(var3, this.lockKey, this.getDisplayName())
         ? new BeaconMenu(var1, var2, this.dataAccess, ContainerLevelAccess.create(this.level, this.getBlockPos()))
         : null;
   }

   @Override
   public Component getDisplayName() {
      return (Component)(this.name != null ? this.name : Component.translatable("container.beacon"));
   }

   @Override
   public void setLevel(Level var1) {
      super.setLevel(var1);
      this.lastCheckY = var1.getMinBuildHeight() - 1;
   }

   public static class BeaconBeamSection {
      final float[] color;
      private int height;

      public BeaconBeamSection(float[] var1) {
         super();
         this.color = var1;
         this.height = 1;
      }

      protected void increaseHeight() {
         ++this.height;
      }

      public float[] getColor() {
         return this.color;
      }

      public int getHeight() {
         return this.height;
      }
   }
}
