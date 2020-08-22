package net.minecraft.world.entity.schedule;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import java.util.List;

public class Timeline {
   private final List keyframes = Lists.newArrayList();
   private int previousIndex;

   public Timeline addKeyframe(int var1, float var2) {
      this.keyframes.add(new Keyframe(var1, var2));
      this.sortAndDeduplicateKeyframes();
      return this;
   }

   private void sortAndDeduplicateKeyframes() {
      Int2ObjectAVLTreeMap var1 = new Int2ObjectAVLTreeMap();
      this.keyframes.forEach((var1x) -> {
         Keyframe var10000 = (Keyframe)var1.put(var1x.getTimeStamp(), var1x);
      });
      this.keyframes.clear();
      this.keyframes.addAll(var1.values());
      this.previousIndex = 0;
   }

   public float getValueAt(int var1) {
      if (this.keyframes.size() <= 0) {
         return 0.0F;
      } else {
         Keyframe var2 = (Keyframe)this.keyframes.get(this.previousIndex);
         Keyframe var3 = (Keyframe)this.keyframes.get(this.keyframes.size() - 1);
         boolean var4 = var1 < var2.getTimeStamp();
         int var5 = var4 ? 0 : this.previousIndex;
         float var6 = var4 ? var3.getValue() : var2.getValue();

         for(int var7 = var5; var7 < this.keyframes.size(); ++var7) {
            Keyframe var8 = (Keyframe)this.keyframes.get(var7);
            if (var8.getTimeStamp() > var1) {
               break;
            }

            this.previousIndex = var7;
            var6 = var8.getValue();
         }

         return var6;
      }
   }
}
