package org.thoughtcrime.securesms.stories.settings.my

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.thoughtcrime.securesms.database.SignalDatabase
import org.thoughtcrime.securesms.database.model.DistributionListId
import org.thoughtcrime.securesms.database.model.DistributionListPrivacyData
import org.thoughtcrime.securesms.database.model.DistributionListPrivacyMode
import org.thoughtcrime.securesms.stories.Stories

class MyStorySettingsRepository {

  fun getPrivacyState(): Single<MyStoryPrivacyState> {
    return Single.fromCallable {
      val privacyData: DistributionListPrivacyData = SignalDatabase.distributionLists.getPrivacyData(DistributionListId.MY_STORY)

      MyStoryPrivacyState(
        privacyMode = privacyData.privacyMode,
        connectionCount = if (privacyData.privacyMode == DistributionListPrivacyMode.ALL_EXCEPT) privacyData.rawMemberCount else privacyData.memberCount
      )
    }.subscribeOn(Schedulers.io())
  }

  fun setPrivacyMode(privacyMode: DistributionListPrivacyMode): Completable {
    return Completable.fromAction {
      SignalDatabase.distributionLists.setPrivacyMode(DistributionListId.MY_STORY, privacyMode)
      Stories.onStorySettingsChanged(DistributionListId.MY_STORY)
    }.subscribeOn(Schedulers.io())
  }

  fun getRepliesAndReactionsEnabled(): Single<Boolean> {
    return Single.fromCallable {
      SignalDatabase.distributionLists.getStoryType(DistributionListId.MY_STORY).isStoryWithReplies
    }.subscribeOn(Schedulers.io())
  }

  fun setRepliesAndReactionsEnabled(repliesAndReactionsEnabled: Boolean): Completable {
    return Completable.fromAction {
      SignalDatabase.distributionLists.setAllowsReplies(DistributionListId.MY_STORY, repliesAndReactionsEnabled)
      Stories.onStorySettingsChanged(DistributionListId.MY_STORY)
    }.subscribeOn(Schedulers.io())
  }
}
