package net.minecraft.world.level.pathfinder;

public class BinaryHeap {
   private Node[] heap = new Node[128];
   private int size;

   public BinaryHeap() {
      super();
   }

   public Node insert(Node var1) {
      if (var1.heapIdx >= 0) {
         throw new IllegalStateException("OW KNOWS!");
      } else {
         if (this.size == this.heap.length) {
            Node[] var2 = new Node[this.size << 1];
            System.arraycopy(this.heap, 0, var2, 0, this.size);
            this.heap = var2;
         }

         this.heap[this.size] = var1;
         var1.heapIdx = this.size;
         this.upHeap(this.size++);
         return var1;
      }
   }

   public void clear() {
      this.size = 0;
   }

   public Node pop() {
      Node var1 = this.heap[0];
      this.heap[0] = this.heap[--this.size];
      this.heap[this.size] = null;
      if (this.size > 0) {
         this.downHeap(0);
      }

      var1.heapIdx = -1;
      return var1;
   }

   public void changeCost(Node var1, float var2) {
      float var3 = var1.f;
      var1.f = var2;
      if (var2 < var3) {
         this.upHeap(var1.heapIdx);
      } else {
         this.downHeap(var1.heapIdx);
      }

   }

   private void upHeap(int var1) {
      Node var2 = this.heap[var1];

      int var4;
      for(float var3 = var2.f; var1 > 0; var1 = var4) {
         var4 = var1 - 1 >> 1;
         Node var5 = this.heap[var4];
         if (var3 >= var5.f) {
            break;
         }

         this.heap[var1] = var5;
         var5.heapIdx = var1;
      }

      this.heap[var1] = var2;
      var2.heapIdx = var1;
   }

   private void downHeap(int var1) {
      Node var2 = this.heap[var1];
      float var3 = var2.f;

      while(true) {
         int var4 = 1 + (var1 << 1);
         int var5 = var4 + 1;
         if (var4 >= this.size) {
            break;
         }

         Node var6 = this.heap[var4];
         float var7 = var6.f;
         Node var8;
         float var9;
         if (var5 >= this.size) {
            var8 = null;
            var9 = 1.0F / 0.0;
         } else {
            var8 = this.heap[var5];
            var9 = var8.f;
         }

         if (var7 < var9) {
            if (var7 >= var3) {
               break;
            }

            this.heap[var1] = var6;
            var6.heapIdx = var1;
            var1 = var4;
         } else {
            if (var9 >= var3) {
               break;
            }

            this.heap[var1] = var8;
            var8.heapIdx = var1;
            var1 = var5;
         }
      }

      this.heap[var1] = var2;
      var2.heapIdx = var1;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }
}
