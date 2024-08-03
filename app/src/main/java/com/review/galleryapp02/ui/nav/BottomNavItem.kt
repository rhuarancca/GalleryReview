package com.review.galleryapp02.ui.nav

import androidx.annotation.DrawableRes
import com.ebookfrenzy.galleryapp02.R


sealed class BottomNavItem(@DrawableRes var icon: Int, var route: String) {
    object Gallery : BottomNavItem(R.drawable.home_icon, "gallery")
    object Maps: BottomNavItem(R.drawable.maps_icon,"maps")
    object Room : BottomNavItem(R.drawable.maps_icon, "room")
    object ScanQr: BottomNavItem(R.drawable.scanqr_icon,"scanqr")

    object GalleryMaps: BottomNavItem( R.drawable.gallmaps_icon,"galleryMaps")
    object Painting : BottomNavItem(R.drawable.paint_icon, "painting")

    object returnButton : BottomNavItem(R.drawable.icon_return, "return")
}
