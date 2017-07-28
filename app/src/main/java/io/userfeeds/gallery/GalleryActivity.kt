package io.userfeeds.gallery

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.userfeeds.sdk.core.UserfeedsService
import io.userfeeds.sdk.core.algorithm.Algorithm
import io.userfeeds.sdk.core.ranking.RankingItem
import kotlinx.android.synthetic.main.gallery_activity.*

class GalleryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gallery_activity)
        val userfeedId = UserfeedIdPrefs(this).load()
        UserfeedsService.get().getRanking(userfeedId, Algorithm("labels", ""), null)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSuccess, this::onError)
    }

    private fun onSuccess(items: List<RankingItem>) {
        images.layoutManager = GridLayoutManager(this, 3)
        images.adapter = GalleryAdapter(items)
    }

    private fun onError(error: Throwable) {
        Log.e("TAG", "error", error)
    }
}
