package net.minecraft.world.level.block.entity;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.Services;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class SkullBlockEntity extends BlockEntity {
   public static final String TAG_SKULL_OWNER = "SkullOwner";
   @Nullable
   private static GameProfileCache profileCache;
   @Nullable
   private static MinecraftSessionService sessionService;
   @Nullable
   private static Executor mainThreadExecutor;
   @Nullable
   private GameProfile owner;
   private int mouthTickCount;
   private boolean isMovingMouth;

   public SkullBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.SKULL, var1, var2);
   }

   public static void setup(Services var0, Executor var1) {
      profileCache = var0.profileCache();
      sessionService = var0.sessionService();
      mainThreadExecutor = var1;
   }

   public static void clear() {
      profileCache = null;
      sessionService = null;
      mainThreadExecutor = null;
   }

   protected void saveAdditional(CompoundTag var1) {
      super.saveAdditional(var1);
      if (this.owner != null) {
         CompoundTag var2 = new CompoundTag();
         NbtUtils.writeGameProfile(var2, this.owner);
         var1.put("SkullOwner", var2);
      }

   }

   public void load(CompoundTag var1) {
      super.load(var1);
      if (var1.contains("SkullOwner", 10)) {
         this.setOwner(NbtUtils.readGameProfile(var1.getCompound("SkullOwner")));
      } else if (var1.contains("ExtraType", 8)) {
         String var2 = var1.getString("ExtraType");
         if (!StringUtil.isNullOrEmpty(var2)) {
            this.setOwner(new GameProfile((UUID)null, var2));
         }
      }

   }

   public static void dragonHeadAnimation(Level var0, BlockPos var1, BlockState var2, SkullBlockEntity var3) {
      if (var0.hasNeighborSignal(var1)) {
         var3.isMovingMouth = true;
         ++var3.mouthTickCount;
      } else {
         var3.isMovingMouth = false;
      }

   }

   public float getMouthAnimation(float var1) {
      return this.isMovingMouth ? (float)this.mouthTickCount + var1 : (float)this.mouthTickCount;
   }

   @Nullable
   public GameProfile getOwnerProfile() {
      return this.owner;
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   public void setOwner(@Nullable GameProfile var1) {
      synchronized(this) {
         this.owner = var1;
      }

      this.updateOwnerProfile();
   }

   private void updateOwnerProfile() {
      updateGameprofile(this.owner, (var1) -> {
         this.owner = var1;
         this.setChanged();
      });
   }

   public static void updateGameprofile(@Nullable GameProfile var0, Consumer<GameProfile> var1) {
      if (var0 != null && !StringUtil.isNullOrEmpty(var0.getName()) && (!var0.isComplete() || !var0.getProperties().containsKey("textures")) && profileCache != null && sessionService != null) {
         profileCache.getAsync(var0.getName(), (var2) -> {
            Util.backgroundExecutor().execute(() -> {
               Util.ifElse(var2, (var1x) -> {
                  Property var2 = (Property)Iterables.getFirst(var1x.getProperties().get("textures"), (Object)null);
                  if (var2 == null) {
                     var1x = sessionService.fillProfileProperties(var1x, true);
                  }

                  mainThreadExecutor.execute(() -> {
                     profileCache.add(var1x);
                     var1.accept(var1x);
                  });
               }, () -> {
                  mainThreadExecutor.execute(() -> {
                     var1.accept(var0);
                  });
               });
            });
         });
      } else {
         var1.accept(var0);
      }
   }

   // $FF: synthetic method
   public Packet getUpdatePacket() {
      return this.getUpdatePacket();
   }
}
