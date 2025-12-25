package com.itsol.vn.wallpaper.live.parallax.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

@Immutable
data class LanguageModel(@StringRes val name: Int, @DrawableRes val flag: Int, val key: String) {

}