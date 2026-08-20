package com.mojang.renderpearl.backend.opengl;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jspecify.annotations.Nullable;

public class FrameBufferCache {
   private final Object2IntMap<CacheKey> cache = new Object2IntOpenHashMap();

   public FrameBufferCache() {
      super();
   }

   public int getFbo(final DirectStateAccess dsa, final List<@Nullable FrameBufferAttachment> colorTextures, final @Nullable FrameBufferAttachment depthTexture) {
      return this.getFbo(dsa, colorTextures, depthTexture, 0);
   }

   public int getFbo(final DirectStateAccess dsa, final List<@Nullable FrameBufferAttachment> colorTextures, final @Nullable FrameBufferAttachment depthTexture, final int mipOffset) {
      CacheKey cacheKey = new CacheKey(colorTextures, depthTexture, mipOffset);
      return this.cache.computeIfAbsent(cacheKey, (var6) -> this.createFbo(cacheKey, dsa, colorTextures, depthTexture, mipOffset));
   }

   private int createFbo(final CacheKey key, final DirectStateAccess dsa, final List<@Nullable FrameBufferAttachment> colorAttachments, final @Nullable FrameBufferAttachment depthAttachment, final int mipOffset) {
      int fbo = dsa.createFrameBufferObject();
      int colorAttachmentCount = colorAttachments.size();
      int[] colorIds = new int[colorAttachmentCount];
      int[] mipLevels = new int[colorAttachmentCount];

      for(int i = 0; i < colorAttachmentCount; ++i) {
         FrameBufferAttachment attachment = (FrameBufferAttachment)colorAttachments.get(i);
         if (attachment != null) {
            colorIds[i] = attachment.glId();
            mipLevels[i] = attachment.fboMipLevel() + mipOffset;
            attachment.addAssociatedFbo(key);
         } else {
            colorIds[i] = 0;
            mipLevels[i] = 0;
         }
      }

      if (depthAttachment != null) {
         depthAttachment.addAssociatedFbo(key);
      }

      dsa.bindFrameBufferTextures(fbo, colorIds, mipLevels, depthAttachment == null ? 0 : depthAttachment.glId(), depthAttachment == null ? 0 : depthAttachment.fboMipLevel(), 0);
      return fbo;
   }

   public void destroyFbo(final CacheKey key) {
      if (this.cache.containsKey(key)) {
         for(FrameBufferAttachment associatedAttachment : key.associatedAttachments) {
            if (associatedAttachment != null) {
               associatedAttachment.removeAssociatedFbo(key);
            }
         }

         int fboId = this.cache.removeInt(key);
         GlStateManager._glDeleteFramebuffers(fboId);
      }
   }

   public static class CacheKey {
      private final int[] data;
      private final int hash;
      public final List<@Nullable FrameBufferAttachment> associatedAttachments;

      public CacheKey(final List<@Nullable FrameBufferAttachment> colorAttachments, final @Nullable FrameBufferAttachment depthAttachment, final int mipOffset) {
         super();
         int colorAttachmentCount = colorAttachments.size();
         this.data = new int[(colorAttachmentCount + (depthAttachment != null ? 1 : 0)) * 2];

         for(int i = 0; i < colorAttachmentCount; ++i) {
            FrameBufferAttachment attachment = (FrameBufferAttachment)colorAttachments.get(i);
            if (attachment != null) {
               this.data[i * 2] = attachment.glId();
               this.data[i * 2 + 1] = attachment.fboMipLevel() + mipOffset;
            } else {
               this.data[i * 2] = 0;
               this.data[i * 2 + 1] = 0;
            }
         }

         this.associatedAttachments = new ArrayList(colorAttachments);
         if (depthAttachment != null) {
            this.data[colorAttachmentCount * 2] = depthAttachment.glId();
            this.data[colorAttachmentCount * 2 + 1] = depthAttachment.fboMipLevel() + mipOffset;
            this.associatedAttachments.add(depthAttachment);
         }

         this.hash = Arrays.hashCode(this.data);
      }

      public int hashCode() {
         return this.hash;
      }

      public boolean equals(final Object obj) {
         if (this == obj) {
            return true;
         } else if (obj instanceof CacheKey) {
            CacheKey other = (CacheKey)obj;
            return this.hash != other.hash ? false : Arrays.equals(this.data, other.data);
         } else {
            return false;
         }
      }
   }
}
