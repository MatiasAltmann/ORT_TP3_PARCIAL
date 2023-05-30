package ar.edu.ort.tp3.parcialtp3ort.tools

import android.view.View
import android.widget.ImageView
import ar.edu.ort.tp3.parcialtp3ort.R
import ar.edu.ort.tp3.parcialtp3ort.entities.Make
import com.bumptech.glide.Glide

class ImageFetching {

    companion object {
        fun getImageManaged(view: View, element: ImageView, path: String?, altPath: String?) {
            val defaultImg = R.drawable.avatar_car

            Glide
                .with(view)
                .load(path).fitCenter()
                .placeholder(defaultImg)
                .error(
                    Glide
                        .with(view)
                        .load(Make.Constants.getImage(altPath)).fitCenter()
                        .error(
                            Glide
                                .with(view)
                                .load(defaultImg))
                )
                .into(element)
        }
    }

}