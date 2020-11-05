package net.minecraft.world.level.block.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
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
   public static final MobEffect[][] BEACON_EFFECTS;
   private static final Set<MobEffect> VALID_EFFECTS;
   private List<BeaconBlockEntity.BeaconBeamSection> beamSections = Lists.newArrayList();
   private List<BeaconBlockEntity.BeaconBeamSection> checkingBeamSections = Lists.newArrayList();
   private int levels;
   private int lastCheckY;
   @Nullable
   private MobEffect primaryPower;
   @Nullable
   private MobEffect secondaryPower;
   @Nullable
   private Component name;
   private LockCode lockKey;
   private final ContainerData dataAccess;

   public BeaconBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.BEACON, var1, var2);
      this.lockKey = LockCode.NO_LOCK;
      this.dataAccess = new ContainerData() {
         public int get(int var1) {
            switch(var1) {
            case 0:
               return BeaconBlockEntity.this.levels;
            case 1:
               return MobEffect.getId(BeaconBlockEntity.this.primaryPower);
            case 2:
               return MobEffect.getId(BeaconBlockEntity.this.secondaryPower);
            default:
               return 0;
            }
         }

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

         public int getCount() {
            return 3;
         }
      };
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

      BeaconBlockEntity.BeaconBeamSection var8 = var3.checkingBeamSections.isEmpty() ? null : (BeaconBlockEntity.BeaconBeamSection)var3.checkingBeamSections.get(var3.checkingBeamSections.size() - 1);
      int var9 = var0.getHeight(Heightmap.Types.WORLD_SURFACE, var4, var6);

      int var10;
      for(var10 = 0; var10 < 10 && var7.getY() <= var9; ++var10) {
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
                  var8 = new BeaconBlockEntity.BeaconBeamSection(new float[]{(var8.color[0] + var13[0]) / 2.0F, (var8.color[1] + var13[1]) / 2.0F, (var8.color[2] + var13[2]) / 2.0F});
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

      var10 = var3.levels;
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
         boolean var15 = var10 > 0;
         var3.beamSections = var3.checkingBeamSections;
         if (!var0.isClientSide) {
            boolean var16 = var3.levels > 0;
            if (!var15 && var16) {
               playSound(var0, var1, SoundEvents.BEACON_ACTIVATE);
               Iterator var17 = var0.getEntitiesOfClass(ServerPlayer.class, (new AABB((double)var4, (double)var5, (double)var6, (double)var4, (double)(var5 - 4), (double)var6)).inflate(10.0D, 5.0D, 10.0D)).iterator();

               while(var17.hasNext()) {
                  ServerPlayer var14 = (ServerPlayer)var17.next();
                  CriteriaTriggers.CONSTRUCT_BEACON.trigger(var14, var3.levels);
               }
            } else if (var15 && !var16) {
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
         AABB var9 = (new AABB(var1)).inflate(var5).expandTowards(0.0D, (double)var0.getMaxBuildHeight(), 0.0D);
         List var10 = var0.getEntitiesOfClass(Player.class, var9);
         Iterator var11 = var10.iterator();

         Player var12;
         while(var11.hasNext()) {
            var12 = (Player)var11.next();
            var12.addEffect(new MobEffectInstance(var3, var8, var7, true, true));
         }

         if (var2 >= 4 && var3 != var4 && var4 != null) {
            var11 = var10.iterator();

            while(var11.hasNext()) {
               var12 = (Player)var11.next();
               var12.addEffect(new MobEffectInstance(var4, var8, 0, true, true));
            }
         }

      }
   }

   public static void playSound(Level var0, BlockPos var1, SoundEvent var2) {
      var0.playSound((Player)null, (BlockPos)var1, var2, SoundSource.BLOCKS, 1.0F, 1.0F);
   }

   public List<BeaconBlockEntity.BeaconBeamSection> getBeamSections() {
      return (List)(this.levels == 0 ? ImmutableList.of() : this.beamSections);
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return new ClientboundBlockEntityDataPacket(this.worldPosition, 3, this.getUpdateTag());
   }

   public CompoundTag getUpdateTag() {
      return this.save(new CompoundTag());
   }

   public double getViewDistance() {
      return 256.0D;
   }

   @Nullable
   private static MobEffect getValidEffectById(int var0) {
      MobEffect var1 = MobEffect.byId(var0);
      return VALID_EFFECTS.contains(var1) ? var1 : null;
   }

   public void load(CompoundTag var1) {
      super.load(var1);
      this.primaryPower = getValidEffectById(var1.getInt("Primary"));
      this.secondaryPower = getValidEffectById(var1.getInt("Secondary"));
      if (var1.contains("CustomName", 8)) {
         this.name = Component.Serializer.fromJson(var1.getString("CustomName"));
      }

      this.lockKey = LockCode.fromTag(var1);
   }

   public CompoundTag save(CompoundTag var1) {
      super.save(var1);
      var1.putInt("Primary", MobEffect.getId(this.primaryPower));
      var1.putInt("Secondary", MobEffect.getId(this.secondaryPower));
      var1.putInt("Levels", this.levels);
      if (this.name != null) {
         var1.putString("CustomName", Component.Serializer.toJson(this.name));
      }

      this.lockKey.addToTag(var1);
      return var1;
   }

   public void setCustomName(@Nullable Component var1) {
      this.name = var1;
   }

   @Nullable
   public AbstractContainerMenu createMenu(int var1, Inventory var2, Player var3) {
      return BaseContainerBlockEntity.canUnlock(var3, this.lockKey, this.getDisplayName()) ? new BeaconMenu(var1, var2, this.dataAccess, ContainerLevelAccess.create(this.level, this.getBlockPos())) : null;
   }

   public Component getDisplayName() {
      return (Component)(this.name != null ? this.name : new TranslatableComponent("container.beacon"));
   }

   public void setLevel(Level var1) {
      super.setLevel(var1);
      this.lastCheckY = var1.getMinBuildHeight() - 1;
   }

   static {
      BEACON_EFFECTS = new MobEffect[][]{{MobEffects.MOVEMENT_SPEED, MobEffects.DIG_SPEED}, {MobEffects.DAMAGE_RESISTANCE, MobEffects.JUMP}, {MobEffects.DAMAGE_BOOST}, {MobEffects.REGENERATION}};
      VALID_EFFECTS = (Set)Arrays.stream(BEACON_EFFECTS).flatMap(Arrays::stream).collect(Collectors.toSet());
   }

   public static class BeaconBeamSection {
      private final float[] color;
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
