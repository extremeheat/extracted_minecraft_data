package net.minecraft.world;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketUpdateBossInfo;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

public class BossInfoServer extends BossInfo {
   private final Set<EntityPlayerMP> field_186762_h = Sets.newHashSet();
   private final Set<EntityPlayerMP> field_186763_i;
   private boolean field_186764_j;

   public BossInfoServer(ITextComponent var1, BossInfo.Color var2, BossInfo.Overlay var3) {
      super(MathHelper.func_188210_a(), var1, var2, var3);
      this.field_186763_i = Collections.unmodifiableSet(this.field_186762_h);
      this.field_186764_j = true;
   }

   public void func_186735_a(float var1) {
      if (var1 != this.field_186750_b) {
         super.func_186735_a(var1);
         this.func_186759_a(SPacketUpdateBossInfo.Operation.UPDATE_PCT);
      }

   }

   public void func_186745_a(BossInfo.Color var1) {
      if (var1 != this.field_186751_c) {
         super.func_186745_a(var1);
         this.func_186759_a(SPacketUpdateBossInfo.Operation.UPDATE_STYLE);
      }

   }

   public void func_186746_a(BossInfo.Overlay var1) {
      if (var1 != this.field_186752_d) {
         super.func_186746_a(var1);
         this.func_186759_a(SPacketUpdateBossInfo.Operation.UPDATE_STYLE);
      }

   }

   public BossInfo func_186741_a(boolean var1) {
      if (var1 != this.field_186753_e) {
         super.func_186741_a(var1);
         this.func_186759_a(SPacketUpdateBossInfo.Operation.UPDATE_PROPERTIES);
      }

      return this;
   }

   public BossInfo func_186742_b(boolean var1) {
      if (var1 != this.field_186754_f) {
         super.func_186742_b(var1);
         this.func_186759_a(SPacketUpdateBossInfo.Operation.UPDATE_PROPERTIES);
      }

      return this;
   }

   public BossInfo func_186743_c(boolean var1) {
      if (var1 != this.field_186755_g) {
         super.func_186743_c(var1);
         this.func_186759_a(SPacketUpdateBossInfo.Operation.UPDATE_PROPERTIES);
      }

      return this;
   }

   public void func_186739_a(ITextComponent var1) {
      if (!Objects.equal(var1, this.field_186749_a)) {
         super.func_186739_a(var1);
         this.func_186759_a(SPacketUpdateBossInfo.Operation.UPDATE_NAME);
      }

   }

   private void func_186759_a(SPacketUpdateBossInfo.Operation var1) {
      if (this.field_186764_j) {
         SPacketUpdateBossInfo var2 = new SPacketUpdateBossInfo(var1, this);
         Iterator var3 = this.field_186762_h.iterator();

         while(var3.hasNext()) {
            EntityPlayerMP var4 = (EntityPlayerMP)var3.next();
            var4.field_71135_a.func_147359_a(var2);
         }
      }

   }

   public void func_186760_a(EntityPlayerMP var1) {
      if (this.field_186762_h.add(var1) && this.field_186764_j) {
         var1.field_71135_a.func_147359_a(new SPacketUpdateBossInfo(SPacketUpdateBossInfo.Operation.ADD, this));
      }

   }

   public void func_186761_b(EntityPlayerMP var1) {
      if (this.field_186762_h.remove(var1) && this.field_186764_j) {
         var1.field_71135_a.func_147359_a(new SPacketUpdateBossInfo(SPacketUpdateBossInfo.Operation.REMOVE, this));
      }

   }

   public void func_201360_b() {
      if (!this.field_186762_h.isEmpty()) {
         Iterator var1 = this.field_186762_h.iterator();

         while(var1.hasNext()) {
            EntityPlayerMP var2 = (EntityPlayerMP)var1.next();
            this.func_186761_b(var2);
         }
      }

   }

   public boolean func_201359_g() {
      return this.field_186764_j;
   }

   public void func_186758_d(boolean var1) {
      if (var1 != this.field_186764_j) {
         this.field_186764_j = var1;
         Iterator var2 = this.field_186762_h.iterator();

         while(var2.hasNext()) {
            EntityPlayerMP var3 = (EntityPlayerMP)var2.next();
            var3.field_71135_a.func_147359_a(new SPacketUpdateBossInfo(var1 ? SPacketUpdateBossInfo.Operation.ADD : SPacketUpdateBossInfo.Operation.REMOVE, this));
         }
      }

   }

   public Collection<EntityPlayerMP> func_186757_c() {
      return this.field_186763_i;
   }
}
