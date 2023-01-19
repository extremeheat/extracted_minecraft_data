package net.minecraft.world.level.block.entity;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Services;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class SkullBlockEntity extends BlockEntity {
   public static final String TAG_SKULL_OWNER = "SkullOwner";
   public static final String TAG_NOTE_BLOCK_SOUND = "note_block_sound";
   @Nullable
   private static GameProfileCache profileCache;
   @Nullable
   private static MinecraftSessionService sessionService;
   @Nullable
   private static Executor mainThreadExecutor;
   @Nullable
   private GameProfile owner;
   @Nullable
   private ResourceLocation noteBlockSound;
   private int animationTickCount;
   private boolean isAnimating;

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

   @Override
   protected void saveAdditional(CompoundTag var1) {
      super.saveAdditional(var1);
      if (this.owner != null) {
         CompoundTag var2 = new CompoundTag();
         NbtUtils.writeGameProfile(var2, this.owner);
         var1.put("SkullOwner", var2);
      }

      if (this.noteBlockSound != null) {
         var1.putString("note_block_sound", this.noteBlockSound.toString());
      }
   }

   @Override
   public void load(CompoundTag var1) {
      super.load(var1);
      if (var1.contains("SkullOwner", 10)) {
         this.setOwner(NbtUtils.readGameProfile(var1.getCompound("SkullOwner")));
      } else if (var1.contains("ExtraType", 8)) {
         String var2 = var1.getString("ExtraType");
         if (!StringUtil.isNullOrEmpty(var2)) {
            this.setOwner(new GameProfile(null, var2));
         }
      }

      if (var1.contains("note_block_sound", 8)) {
         this.noteBlockSound = ResourceLocation.tryParse(var1.getString("note_block_sound"));
      }
   }

   public static void animation(Level var0, BlockPos var1, BlockState var2, SkullBlockEntity var3) {
      if (var0.hasNeighborSignal(var1)) {
         var3.isAnimating = true;
         ++var3.animationTickCount;
      } else {
         var3.isAnimating = false;
      }
   }

   public float getAnimation(float var1) {
      return this.isAnimating ? (float)this.animationTickCount + var1 : (float)this.animationTickCount;
   }

   @Nullable
   public GameProfile getOwnerProfile() {
      return this.owner;
   }

   @Nullable
   public ResourceLocation getNoteBlockSound() {
      return this.noteBlockSound;
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @Override
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
      updateGameprofile(this.owner, var1 -> {
         this.owner = var1;
         this.setChanged();
      });
   }

   public static void updateGameprofile(@Nullable GameProfile var0, Consumer<GameProfile> var1) {
      if (var0 != null
         && !StringUtil.isNullOrEmpty(var0.getName())
         && (!var0.isComplete() || !var0.getProperties().containsKey("textures"))
         && profileCache != null
         && sessionService != null) {
         profileCache.getAsync(var0.getName(), var2 -> Util.backgroundExecutor().execute(() -> Util.ifElse(var2, var1xxx -> {
                  Property var2xx = (Property)Iterables.getFirst(var1xxx.getProperties().get("textures"), null);
                  if (var2xx == null) {
                     var1xxx = sessionService.fillProfileProperties(var1xxx, true);
                  }

                  GameProfile var3 = var1xxx;
                  mainThreadExecutor.execute(() -> {
                     profileCache.add(var3);
                     var1.accept(var3);
                  });
               }, () -> mainThreadExecutor.execute(() -> var1.accept(var0)))));
      } else {
         var1.accept(var0);
      }
   }
}
