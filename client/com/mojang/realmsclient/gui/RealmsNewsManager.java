package com.mojang.realmsclient.gui;

import com.mojang.realmsclient.dto.RealmsNews;
import com.mojang.realmsclient.util.RealmsPersistence;

public class RealmsNewsManager {
   private final RealmsPersistence newsLocalStorage;
   private boolean hasUnreadNews;
   private String newsLink;

   public RealmsNewsManager(RealmsPersistence var1) {
      super();
      this.newsLocalStorage = var1;
      RealmsPersistence.RealmsPersistenceData var2 = var1.read();
      this.hasUnreadNews = var2.hasUnreadNews;
      this.newsLink = var2.newsLink;
   }

   public boolean hasUnreadNews() {
      return this.hasUnreadNews;
   }

   public String newsLink() {
      return this.newsLink;
   }

   public void updateUnreadNews(RealmsNews var1) {
      RealmsPersistence.RealmsPersistenceData var2 = this.updateNewsStorage(var1);
      this.hasUnreadNews = var2.hasUnreadNews;
      this.newsLink = var2.newsLink;
   }

   private RealmsPersistence.RealmsPersistenceData updateNewsStorage(RealmsNews var1) {
      RealmsPersistence.RealmsPersistenceData var2 = new RealmsPersistence.RealmsPersistenceData();
      var2.newsLink = var1.newsLink;
      RealmsPersistence.RealmsPersistenceData var3 = this.newsLocalStorage.read();
      boolean var4 = var2.newsLink == null || var2.newsLink.equals(var3.newsLink);
      if (var4) {
         return var3;
      } else {
         var2.hasUnreadNews = true;
         this.newsLocalStorage.save(var2);
         return var2;
      }
   }
}
