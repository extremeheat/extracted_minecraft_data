package net.minecraft.world.entity;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class EntityAttachments {
   private final Map<EntityAttachment, List<Vec3>> attachments;

   EntityAttachments(Map<EntityAttachment, List<Vec3>> var1) {
      super();
      this.attachments = var1;
   }

   public static EntityAttachments createDefault(float var0, float var1) {
      return builder().build(var0, var1);
   }

   public static Builder builder() {
      return new Builder();
   }

   public EntityAttachments scale(float var1, float var2, float var3) {
      return new EntityAttachments(Util.makeEnumMap(EntityAttachment.class, (var4) -> {
         ArrayList var5 = new ArrayList();

         for(Vec3 var7 : (List)this.attachments.get(var4)) {
            var5.add(var7.multiply((double)var1, (double)var2, (double)var3));
         }

         return var5;
      }));
   }

   @Nullable
   public Vec3 getNullable(EntityAttachment var1, int var2, float var3) {
      List var4 = (List)this.attachments.get(var1);
      return var2 >= 0 && var2 < var4.size() ? transformPoint((Vec3)var4.get(var2), var3) : null;
   }

   public Vec3 get(EntityAttachment var1, int var2, float var3) {
      Vec3 var4 = this.getNullable(var1, var2, var3);
      if (var4 == null) {
         String var10002 = String.valueOf(var1);
         throw new IllegalStateException("Had no attachment point of type: " + var10002 + " for index: " + var2);
      } else {
         return var4;
      }
   }

   public Vec3 getClamped(EntityAttachment var1, int var2, float var3) {
      List var4 = (List)this.attachments.get(var1);
      if (var4.isEmpty()) {
         throw new IllegalStateException("Had no attachment points of type: " + String.valueOf(var1));
      } else {
         Vec3 var5 = (Vec3)var4.get(Mth.clamp(var2, 0, var4.size() - 1));
         return transformPoint(var5, var3);
      }
   }

   private static Vec3 transformPoint(Vec3 var0, float var1) {
      return var0.yRot(-var1 * 0.017453292F);
   }

   public static class Builder {
      private final Map<EntityAttachment, List<Vec3>> attachments = new EnumMap(EntityAttachment.class);

      Builder() {
         super();
      }

      public Builder attach(EntityAttachment var1, float var2, float var3, float var4) {
         return this.attach(var1, new Vec3((double)var2, (double)var3, (double)var4));
      }

      public Builder attach(EntityAttachment var1, Vec3 var2) {
         ((List)this.attachments.computeIfAbsent(var1, (var0) -> new ArrayList(1))).add(var2);
         return this;
      }

      public EntityAttachments build(float var1, float var2) {
         Map var3 = Util.makeEnumMap(EntityAttachment.class, (var3x) -> {
            List var4 = (List)this.attachments.get(var3x);
            return var4 == null ? var3x.createFallbackPoints(var1, var2) : List.copyOf(var4);
         });
         return new EntityAttachments(var3);
      }
   }
}
