package io.userfeeds.gallery

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import io.userfeeds.sdk.core.ranking.RankingItem

class GalleryAdapter(private val items: List<RankingItem>) : RecyclerView.Adapter<GalleryAdapter.Holder>() {

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.image_view, parent, false)
        return Holder(itemView)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val imageView = holder.itemView.findViewById(R.id.image) as ImageView
        val target = items[position].target
        val httpTarget = convertToHttp(target)
        Glide.with(imageView.context)
                .load(httpTarget)
                .into(imageView)
    }

    private fun convertToHttp(target: String): String {
        return when {
            target.startsWith("fs:/ipfs/") -> "https://ipfs.io" + target.removePrefix("fs:")
            target.startsWith("fs://ipfs/") -> "https://ipfs.io" + target.removePrefix("fs:/")
            target.startsWith("ipfs://") -> "https://ipfs.io/ipfs" + target.removePrefix("ipfs:/")
            else -> target
        }
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
